angular.module("videoApp.controllers").controller("VideoCtrl",
		function($scope, VideoService) {
			$scope.videos = [];
			$scope.playlist = [];
			$scope.categories = [];
			/*$scope.message = "";
			$scope.max = 140;

			$scope.addMessage = function() {
				VideoService.send($scope.message);
				$scope.message = "";
			};*/

			VideoService.receive().then(null, null, function(event) {
				if(event.typ === 'VideoAddEvent'){
					$scope.videos.push(event.o);
				} else if(event.typ === 'VideoDeleteEvent'){
					$scope.videos.splice( $scope.videos.indexOf(event.o), 1 );
				} else if(event.typ === 'StatusUpdateEvent'){
					
				}
			});
			
			$scope.videos = VideoService.getAllVideos();
			$scope.categories = VideoService.getCategorylist();
			$scope.playlist = VideoService.getPlaylist();
		});

