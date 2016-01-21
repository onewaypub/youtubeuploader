<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="de" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Youtube Uploader</title>
<link href="css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="css/shadow.css">
<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.css">
<link rel="stylesheet"
	href="webjars/bootstrap-material-design/0.3.0/dist/css/material.css">
<link href="resources/dropzone/basic.css" rel="stylesheet" />
<link href="resources/dropzone/dropzone.css" rel="stylesheet" />
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.1.2/toastr.min.css"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<title>Youtube Uploader</title>
</head>
<body>
	<div class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="javascript:void(0)">Youtube
					Uploader</a>
			</div>
			<div class="navbar-collapse collapse navbar-responsive-collapse">
				<ul class="nav navbar-nav">
					<li><a href="list">Videos</a></li>
					<li class="active"><a href="javascript:void(0)">Einstellungen</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="logout">Logout</div>
					</a></li>
				</ul>
			</div>
		</div>
	</div>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-10">
				<h1>Einstellungen</h1>
			</div>
			<div class="col-md-1"></div>
		</div>
		<form action="save.do?${_csrf.parameterName}=${_csrf.token}"
			method="post" modelattribute="usersettings">
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Mailadresse</label> <input type="email"
							name="mailTo" class="form-control" id="mailTo"
							required="required" value="${usersettings.mailTo}" />
					</div>
				</div>
				<div class="col-md-9"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Nachricht nach Upload</label>
						<c:if test="${usersettings.notifyUploadState}">
							<input type="checkbox" name="notifyUploadState"
								class="form-control" id="notifyUploadState" checked="checked"></input>
						</c:if>
						<c:if test="${!usersettings.notifyUploadState}">
							<input type="checkbox" name="notifyUploadState"
								class="form-control" id="notifyUploadState"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Nachricht nach Verarbeitung</label>
						<c:if test="${usersettings.notifyProcessedState}">
							<input type="checkbox" name="notifyProcessedState"
								class="form-control" id="notifyProcessedState" checked="checked"></input>
						</c:if>
						<c:if test="${not usersettings.notifyProcessedState}">
							<input type="checkbox" name="notifyProcessedState"
								class="form-control" id="notifyProcessedState"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Nachricht nach Freigabe</label>
						<c:if test="${usersettings.notifyReleaseState}">
							<input type="checkbox" name="notifyReleaseState"
								class="form-control" id="notifyReleaseState" checked="checked"></input>
						</c:if>
						<c:if test="${not usersettings.notifyReleaseState}">
							<input type="checkbox" name="notifyReleaseState"
								class="form-control" id="notifyReleaseState"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Nachricht nach Fehler</label>
						<c:if test="${usersettings.notifyErrorState}">
							<input type="checkbox" name="notifyErrorState"
								class="form-control" id="notifyErrorState" checked="checked"></input>
						</c:if>
						<c:if test="${not usersettings.notifyErrorState}">
							<input type="checkbox" name="notifyErrorState"
								class="form-control" id="notifyErrorState"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-8">
					<div class="form-group">
						<label for="title">Video Footer</label>
						<textarea class="form-control" rows="3" placeholder="Video Footer"
							name="videoFooter" id="videoFooter"></textarea>
					</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-8">
					<div class="form-group">
						<label for="title">Standard Tags</label> <input type="text"
							name="defaultTags" class="form-control" id="defaultTags"
							required="required" />
					</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Auf Twitter posten</label>
						<c:if test="${usersettings.notifyErrorState}">
							<input type="checkbox" name="postOnTwitter" class="form-control"
								id="postOnTwitter" checked="checked"></input>
						</c:if>
						<c:if test="${not usersettings.notifyErrorState}">
							<input type="checkbox" name="postOnTwitter" class="form-control"
								id="postOnTwitter"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="title">Twitter Post</label> <input type="text"
							name="twitterPost" class="form-control" id="twitterPost" />
					</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="title">Auf Facebook posten</label>
						<c:if test="${usersettings.notifyErrorState}">
							<input type="checkbox" name="postOnFacebook" class="form-control"
								id="postOnFacebook" checked="checked"></input>
						</c:if>
						<c:if test="${not usersettings.notifyErrorState}">
							<input type="checkbox" name="postOnFacebook" class="form-control"
								id="postOnFacebook"></input>
						</c:if>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="title">Facebook Post</label> <input type="text"
							name="facebookPost" class="form-control" id="facebookPost" />
					</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-1">
					<button type="submit" class="btn btn-default">Speichern &
						Zurück</button>
				</div>
				<div class="col-md-1">
					<a href="list.do?${_csrf.parameterName}=${_csrf.token}"
						class="btn btn-default">Zurück</a>
				</div>
				<div class="col-md-9"></div>
			</div>
		</form>
		<form
			action="connectToFacebook.do?${_csrf.parameterName}=${_csrf.token}"
			method="POST">
			<button type="submit">Connect to Facebook</button>
		</form>
		<form
			action="connectToTwitter.do?${_csrf.parameterName}=${_csrf.token}"
			method="POST">
			<button type="submit">Connect to Twitter</button>
		</form>
		<form
			action="connectToGoogle.do?${_csrf.parameterName}=${_csrf.token}"
			method="POST">
			<button type="submit">Connect to Google</button>
		</form>
		<form
			action="connect/youtube"
			method="POST">
			<button type="submit">Connect to Youtube</button>
		</form>
	</div>
	<script type="text/javascript" src="webjars/jquery/1.11.1/jquery.js"></script>
	<script type="text/javascript"
		src="webjars/angularjs/1.4.8/angular.min.js"></script>
	<script type="text/javascript"
		src="webjars/angularjs/1.4.8/angular-resource.min.js"></script>
	<script type="text/javascript"
		src="webjars/bootstrap/3.3.6/js/bootstrap.js"></script>
	<script type="text/javascript"
		src="webjars/bootstrap/3.3.6/js/collapse.js"></script>
	<script type="text/javascript"
		src="webjars/bootstrap-material-design/0.3.0/dist/js/material.js"></script>
	<script type="text/javascript" src="webjars/lodash/3.10.1/lodash.js"></script>
	<script type="text/javascript" src="resources/js/stomp.js"></script>
	<script type="text/javascript" src="resources/js/socks.js"></script>
	<script type="text/javascript" src="resources/js/app.js"></script>
	<script type="text/javascript" src="resources/js/appController.js"></script>
	<script type="text/javascript" src="resources/js/appService.js"></script>
	<script type="text/javascript" src="resources/js/appDirectives.js"></script>
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.1.2/toastr.min.js"></script>
	<script>
		$(document).ready(function() {
			$('[data-toggle="tooltip"]').tooltip();
		});
	</script>
</body>
</html>