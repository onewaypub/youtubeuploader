angular.module("videoApp.services").service("VideoService",
		function($q, $timeout, $resource) {

			var service = {}, listener = $q.defer(), socket = {
				client : null,
				stomp : null
			}, messageIds = [];

			service.RECONNECT_TIMEOUT = 30000;
			service.SOCKET_URL = "/YouTubeUploader/chat";
			service.CHAT_TOPIC = "/topic/message";
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
			
	        service.getAll = function () {
	            var videoResource = $resource('videos', {}, {
	                query: {method: 'GET', params: {}, isArray: true}
	            });
	            return videoResource.query();
	        }

			var reconnect = function() {
				$timeout(function() {
					initialize();
				}, this.RECONNECT_TIMEOUT);
			};

			var getMessage = function(data) {
				var message = JSON.parse(data);
				return message;
			};

			var startListener = function() {
				socket.stomp.subscribe(service.CHAT_TOPIC, function(data) {
					listener.notify(getMessage(data.body));
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

/*angular
.module('myApp', ['ngResource'])
angular.module("videoApp.services").service('VideoListService', function ($log, $resource) {
    return {
        getAll: function () {
            var userResource = $resource('users', {}, {
                query: {method: 'GET', params: {}, isArray: true}
            });
            return userResource.query();
        }
    }
})*/
