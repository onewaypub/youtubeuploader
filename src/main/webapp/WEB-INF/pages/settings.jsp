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
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.js"></script>
<!-- Binde alle kompilierten Plugins zusammen ein (wie hier unten) oder such dir einzelne Dateien nach Bedarf aus -->
<script src="js/bootstrap.js"></script>
<script type="text/javascript" src="js/dropdown.js"></script>

<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
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
		<form action="/connectToFacebook.do?${_csrf.parameterName}=${_csrf.token}" method="POST"><button type="submit">Connect to Facebook</button></form>
		<form action="/connectToTwitter.do?${_csrf.parameterName}=${_csrf.token}" method="POST"><button type="submit">Connect to Twitter</button></form>
		<form action="/connectToGoogle.do?${_csrf.parameterName}=${_csrf.token}" method="POST"><button type="submit">Connect to Google</button></form>
		<form action="/connectToYoutube.do?${_csrf.parameterName}=${_csrf.token}" method="POST"><button type="submit">Connect to Youtube</button></form>
	</div>
	<!-- jQuery (wird für Bootstrap JavaScript-Plugins benötigt) -->
	<script src="js/sockjs-0.3.4.js"></script>
	<script src="js/stomp.js"></script>
</body>
</html>