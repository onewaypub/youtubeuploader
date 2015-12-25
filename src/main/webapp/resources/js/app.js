angular.module('youtubeuploader', [ 'ngResource' ]).service('VideoService',
		function($log, $resource) {
			return {
				getAll : function() {
					var videoResource = $resource('/videos', {}, {
						query : {
							method : 'GET',
							params : {},
							isArray : true
						}
					});
					return videoResource.query();
				}
			}
		}).controller('VideoController', function($scope, $log, VideoService) {
	$scope.videos = VideoService.getAll();
});