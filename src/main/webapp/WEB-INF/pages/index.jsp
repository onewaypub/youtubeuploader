<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html ng-app="chatApp">
<head lang="de">
<meta charset="UTF-8">
<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.css">
<link rel="stylesheet"
	href="webjars/bootstrap-material-design/0.3.0/dist/css/material.css">
<link href="resources/dropzone/basic.css" rel="stylesheet" />
<link href="resources/dropzone/dropzone.css" rel="stylesheet" />
<title></title>
<sec:csrfMetaTags />
<c:if test="${!empty _csrf.token}">
	<script type="text/javascript">
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
	</script>
</c:if>
</head>
<body ng-controller="ChatCtrl" ng-cloak>
	<div class="row">
		<div class="container">
			<div>
				<div class="col-xs-6 col-xs-offset-3">
					<div class="well">
						<form action="upload" class="dropzone" dropzone="" id="dropzone">
							<div class="dz-default dz-message">Videos hier hereinziehen</div>
						</form>
					</div>
				</div>
				<script type="text/javascript">
					dropzone.autoDiscover = true;
				</script>
			</div>
		</div>
	</div>
	<div class="row">
			<p ng-repeat="message in messages | orderBy:'time':true"
			class="message">
			<time>{{message.time | date:'HH:mm'}}</time>
			<span ng-class="{self: message.self}">{{message.message}}</span>
		</p>
	
		<div id="videoList" class="col-sm-offset-1 col-sm-10">
			<div class="input-group">
				<input class="form-control" id="search" name="search"
					placeholder="Search for" ng-model="query" required="required" /> <span
					class="input-group-btn">
					<button type="submit" class="btn btn-default">
						<i class="glyphicon glyphicon-search"></i>
					</button>
				</span>
			</div>
			<div class="list-group">
				<div class="list-group-item">
					<div ng-repeat="message in messages | filter:query"
						class="list-group-item" style="margin-top: 16px">
						<div class="row-picture">
							<img class="circle" src="/getThumbnailImage/{{video.id}}"
								alt="icon">
						</div>
						<div class="row-content">
							<h4 class="list-group-item-heading">{{video.id}}
								{{video.title}}</h4>

							<p class="list-group-item-text">
								<i class="glyphicon glyphicon-envelope"></i>
								{{video.description}}
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="webjars/jquery/1.11.1/jquery.js"></script>
	<script type="text/javascript"
		src="webjars/angularjs/1.4.8/angular.min.js"></script>
	<script type="text/javascript"
		src="webjars/angularjs/1.4.8/angular-resource.min.js"></script>
	<script type="text/javascript"
		src="webjars/bootstrap-material-design/0.3.0/dist/js/material.js"></script>
	<script type="text/javascript" src="webjars/lodash/3.10.1/lodash.js"></script>
	<script type="text/javascript" src="resources/js/stomp.js"></script>
	<script type="text/javascript" src="resources/js/socks.js"></script>
	<script type="text/javascript" src="resources/js/app.js"></script>
	<script type="text/javascript" src="resources/js/appController.js"></script>
	<script type="text/javascript" src="resources/js/appService.js"></script>
	<script type="text/javascript" src="resources/dropzone/dropzone.js"></script>
	<script type="text/javascript">
		Dropzone.options.dropzone = {
			paramName : "file", // The name that will be used to transfer the file
			maxFilesize : 10000, // MB
			headers : {
				"X-CSRF-TOKEN" : "${_csrf.token}"
			},
			acceptFile : "video/*",
			accept : function(file, done) {
				if (file.name == "justinbieber.jpg") {
					done("Naha, you don't.");
				} else {
					done();
				}
			}
		};
	</script>
</body>
</html>
 