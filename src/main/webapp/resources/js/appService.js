angular.module("videoApp.services").service("VideoService",
		function($q, $timeout, $resource) {

			var service = {}, listener = $q.defer(), socket = {
				client : null,
				stomp : null
			}, messageIds = [];

			service.RECONNECT_TIMEOUT = 30000;
			service.SOCKET_URL = "/YouTubeUploader/chat";
			service.EVENT_TOPIC = "/topic/event";
			service.CHAT_BROKER = "/app/chat";

			service.receive = function() {
				return listener.promise;
			};

			service.send = function(message) {
				var id = Math.floor(Math.random() * 1000000);
				socket.stomp.send(service.CHAT_BROKER, {
					priority : 9
				}, JSON.stringify({
					message : message,
					id : id
				}));
				messageIds.push(id);
			};
			
	        service.getAllVideos = function () {
	            var videoResource = $resource('videos', {}, {
	                query: {method: 'GET', params: {}, isArray: true}
	            });
	            return videoResource.query();
	        }

	        service.getPlaylist = function () {
	            var playlistResource = $resource('playlist', {}, {
	                query: {method: 'GET', params: {}, isArray: true}
	            });
	            return playlistResource.query();
	        }

	        service.getCategorylist = function () {
	            var categoryResource = $resource('categorylist', {}, {
	                query: {method: 'GET', params: {}, isArray: true}
	            });
	            return categoryResource.query();
	        }

	        service.deleteVideo = function (index) {
	            var deleteResource = $resource('delete/' + index, {}, {
	                query: {method: 'GET', params: {}, isArray: true}
	            });
	            return deleteResource.query();
	        }

	        service.saveVideo = function (video) {
	            var saveResource = $resource('update/video', {}, {
	                query: {method: 'POST', params: {}, isArray: true}
	            });
	            saveResource.save({}, video);
	            return saveResource.query();
	        }

			var reconnect = function() {
				$timeout(function() {
					initialize();
				}, this.RECONNECT_TIMEOUT);
			};

			var getJSON = function(data) {
				var event = JSON.parse(data);
				return event;
			};

			var startListener = function() {
				socket.stomp.subscribe(service.EVENT_TOPIC, function(data) {
					listener.notify(getJSON(data.body));
				});
			};
			
			var initialize = function() {
				socket.client = new SockJS(service.SOCKET_URL);
				socket.stomp = Stomp.over(socket.client);
				socket.stomp.connect({}, startListener);
				socket.stomp.onclose = reconnect;
			};

			initialize();
			return service;
		});

