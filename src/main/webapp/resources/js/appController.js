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

			VideoService.receive().then(null, null, function(event) {
				if (event.typ === 'VideoAddEvent') {
					$scope.videos.push(event.o);
				} else if (event.typ === 'VideoDeleteEvent') {
					for (var i = 0; i < $scope.videos.length; i++) {
						if (event.o.id === $scope.videos[i].id) {
							$scope.videos.splice(i, 1);
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
