angular.module("videoApp.controllers").controller("VideoCtrl",
		function($scope, VideoService) {
			$scope.videos = [];
			$scope.message = "";
			$scope.max = 140;

			$scope.addMessage = function() {
				VideoService.send($scope.message);
				$scope.message = "";
			};
			
			

			VideoService.receive().then(null, null, function(video) {
				$scope.videos.push(video);
			});
		});

