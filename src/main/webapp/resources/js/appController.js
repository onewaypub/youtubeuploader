angular.module("videoApp.controllers").controller("VideoCtrl",
		function($scope, VideoService) {
			$scope.videos = [];
			$scope.playlist = [];
			$scope.cats = [];

			$scope.saveVideo = function(video) {
				VideoService.saveVideo(video);
			}

			$scope.deleteVideo = function(video) {
				VideoService.deleteVideo(video.id);
			}
			
			$scope.saveThumbnail = function ($file, $id){
				VideoService.saveThumbnial($file, $id);
				
				for (var i = 0; i < $scope.videos.length; i++) {
					if ($id === $scope.videos[i].id) {
						if($scope.videos[i].tmpThumbnailUrl != null){
							$scope.videos[i].localThumbnailUrl = $scope.videos[i].tmpThumbnailUrl;
						} else {
							$scope.videos[i].tmpThumbnailUrl = $scope.videos[i].localThumbnailUrl;
						}
						$scope.videos[i].localThumbnailUrl = $scope.videos[i].localThumbnailUrl + '?t=' + new Date().getTime(); 
						$scope.$apply();
						break;
					}
				}
			}
            
			VideoService.receive().then(null, null, function(event) {
				if (event.typ === 'VideoAddEvent') {
					$scope.videos.push(event.o);
					toastr.success('Neues Video wurde der Liste hinzugefügt - ' + event.o.title, 'Video');
				} else if (event.typ === 'VideoUpdateEvent') {
					toastr.success('Das Video wurde erfolgreich aktualisiert - ' + event.o.title , 'Video');
				} else if (event.typ === 'VideoUnlockEvent') {
					angular.element(document.getElementById('deleteButton') + event.videoId)[0].disabled = false;
				} else if (event.typ === 'VideoLockEvent') {
					angular.element(document.getElementById('deleteButton') + event.videoId)[0].disabled = true;
				} else if (event.typ === 'ErrorEvent') {
					toastr.error(event.o, 'Fehler');
				} else if (event.typ === 'WarningEvent') {
					toastr.warning(event.o, 'Warnung');
				} else if (event.typ === 'VideoDeleteEvent') {
					for (var i = 0; i < $scope.videos.length; i++) {
						if (event.o.id === $scope.videos[i].id) {
							$scope.videos.splice(i, 1);
							toastr.warning('Video erfolgreich gelöscht - ' + event.o.title , 'Video');
							break;
						}
					}
				} else if (event.typ === 'StatusUpdateEvent') {
					for (var i = 0; i < $scope.videos.length; i++) {
						if (event.o.id === $scope.videos[i].id) {
							$scope.videos[i].process = event.percent;
							$scope.videos[i].state = event.status;
							$scope.$apply();
							break;
						}
					}
				}
			});

			$scope.videos = VideoService.getAllVideos();
			$scope.cats = VideoService.getCategorylist();
			$scope.playlist = VideoService.getPlaylist();

		});
