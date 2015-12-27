<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html ng-app="videoApp">
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
<body>
	<br />
	<div class="row">
		<div class="container">
			<div>
				<div class="col-xs-6 col-xs-offset-3">
					<div class="well">
						<form action="upload" class="dropzone" dropzone="" id="dropzone">
							<div class="dz-default dz-message">
								<h3>Videos hier hereinziehen</h3>
							</div>
						</form>
					</div>
				</div>
				<script type="text/javascript">
					dropzone.autoDiscover = true;
				</script>
			</div>
		</div>
	</div>
	<br />
	<div ng-controller="VideoCtrl" ng-cloak class="row">
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
					<div ng-repeat="video in videos | filter:query"
						class="list-group-item" style="margin-top: 16px">
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-10">
								<div class="form-group">
									<label for="title">Titel</label><input type="text"
										name="title" class="form-control" id="title"
										placeholder="Titel" value="{{video.title}}">
								</div>
								<div class="form-group">
									<label for="description">Beschreibung</label>
									<textarea class="form-control" rows="3"
										placeholder="Beschreibung" name="description" id="description">{{video.description}}</textarea>
								</div>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="tags">Tags (z.B. tag1,tag2,tag3)</label> <input
										type="text" name="tags" class="form-control" id="tags"
										placeholder="Tags" value="{{video.tags}}">
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="relasedate">Ver�ffentlichungsdatum (z.B.
										12.12.2015 10:00)</label> <input
										pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d [012]{0,1}[0-9]:[0-6][0-9]$"
										type="datetime" class="form-control" id="timestamp"
										name="timestamp" placeholder="Ver�ffentlichungsdatum"
										 value="{{video.releaseDate}}">
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="relasedate">Youtube Kategorie</label><select
										class="form-control" id="categoryId" name="categoryId"
										 value="{{video.categoryId}}">
										<option id="-1" value="-1">-</option>
										<c:forEach var="categoryItem" items="${categories}">
											<option id="${categoryItem.key}" value="${categoryItem.key}">${categoryItem.value}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="relasedate">Spieletitel</label> <input type="text"
										name="shorttitle" class="form-control" id="shorttitle"
										placeholder="shorttitle" value="{{video.shorttitle}}">
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="relasedate">Genre</label> <input type="text"
										name="gerne" class="form-control" id="gerne"
										placeholder="Genre" value="{{video.genre}}">
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="relasedate">Entwickler</label> <input type="text"
										name="developer" class="form-control" id="developer"
										placeholder="Entwickler" value="{{video.developer}}">
								</div>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="relasedate">Publisher</label> <input type="text"
										name="publisher" class="form-control" id="publisher"
										placeholder="publisher" value="{{video.publisher}}">
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="relasedate">Spiele-Ver�ffentlichung (z.B.
										12.12.2015)</label> <input
										pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d$"
										type="text" name="published" class="form-control"
										id="published" placeholder="Ver�ffentlichung" value="{{video.published}}">
								</div>
							</div>
							<div class="col-md-3">
								<c:if test="${not empty playlist}">
									<div class="form-group">
										<label for="sel1">Playlist</label> <select
											class="form-control" id="playlist" name="playlist"
											 value="{{video.playlist}}">
											<c:forEach var="playlistItem" items="${playlist}">
												<option id="${playlistItem.key}" value="${playlistItem.key}">${playlistItem.value}</option>
											</c:forEach>
										</select>
									</div>
								</c:if>
							</div>
							<div class="col-md-1"></div>
						</div>
						<div class="row">
							<div class="col-md-1"></div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="relasedate">Alterbeschr�nkung</label> <input
										type="checkbox" name="ageRestricted" class="form-control"
										id="ageRestricted"  value="{{video.ageRestricted}}"></input>
								</div>
							</div>
							<div class="col-md-4"></div>
							<div class="col-md-3"></div>
							<div class="col-md-1"></div>
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
