<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html ng-app="videoApp">
<head lang="de">
<meta charset="UTF-8">
<link rel="stylesheet" href="css/shadow.css">
<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.css">
<link rel="stylesheet"
	href="webjars/bootstrap-material-design/0.3.0/dist/css/material.css">
<link href="resources/dropzone/basic.css" rel="stylesheet" />
<link href="resources/dropzone/dropzone.css" rel="stylesheet" />
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<title></title>
<%-- <sec:csrfMetaTags />
<c:if test="${!empty _csrf.token}">
	<script type="text/javascript">
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
	</script>
</c:if> --%>
</head>
<body>
	<br />
	<div class="row">
		<div class="container">
			<div>
				<div class="col-xs-6 col-xs-offset-3">
					<div class="well">
						<form action="upload/video" class="dropzone" dropzone=""
							id="dropzone" enctype="multipart/form-data">
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
				<div class="list-group-item box-shadow--3dp">
					<div class="accordion accordion-caret" id="accordion">
						<div class="accordion-group"
							ng-repeat="video in videos | filter:query">
							<br />
							<div class="accordion-heading">
								<div class="row">
									<div class="col-md-1">
										<i ng-click="saveVideo(video)" class="material-icons"
											data-toggle="tooltip" title="Video-Metadaten speichern"
											style="cursor: default;">save</i><a data-toggle="collapse"
											data-toggle="tooltip" title="Details anzeigen"
											data-parent="#accordion" href="#collapse{{video.id}}"><i
											class="material-icons" style="cursor: default; color: black;">details</i></a>
										<i class="material-icons" data-toggle="modal"
											data-target="#deleteModal" data-toggle="tooltip"
											title="Video löschen" ng-really-message="Are you sure?"
											ng-really-click="deleteVideo(video)" style="cursor: default;">delete</i>
									</div>
									<div class="col-md-4">
										<label for="title">Titel</label><input type="text"
											name="title" class="form-control" id="title"
											placeholder="Titel" ng-model="video.title">
									</div>
									<div class="col-md-2">
										<div class="form-group">
											<label for="releaseDate">Veröffentlichungsdatum</label> <input
												ng-pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d [012]{0,1}[0-9]:[0-6][0-9]$"
												type="datetime" class="form-control" id="releaseDate"
												name="releaseDate" placeholder="z.B. 12.12.2015 10:00"
												ng-model="video.releaseDate">
										</div>
									</div>
									<div class="col-md-1">
										<label for="title">Status</label><br />{{video.state}}
									</div>
									<div class="col-md-2">
										<br />
										<div class="progress progress-striped active">
											<div style="width: {{video.process" class="progress-bar"></div>
										</div>
										<div style="display: none;" id="source-button"
											class="btn btn-primary btn-xs">&lt; &gt;</div>
									</div>
									<div class="col-md-1">
										<!-- Button trigger modal -->
										<button type="button" class="btn btn-raised btn-xs"
											data-toggle="modal" data-target="#myModal">
											<i class="material-icons">video_library</i>
										</button>

										<!-- Modal -->
										<div class="modal fade" id="myModal" tabindex="-1"
											role="dialog" aria-labelledby="myModalLabel">
											<div class="modal-dialog" role="document"
												style="width: 1225px;">
												<div class="modal-content">
													<div class="modal-header">
														<button type="button" class="close" data-dismiss="modal"
															aria-label="Close">
															<span aria-hidden="true">&times;</span>
														</button>
														<h4 class="modal-title" id="myModalLabel">
															<i class="material-icons">video_library</i>
														</h4>
													</div>
													<div class="modal-body">
														<!-- <video controls height="648" width="1176"
															class="embed-responsive-item" preload="none"
															ng-src=""></video> -->
														<a href="getVideo/{{video.id}}.mp4">Video Link</a>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-default"
															data-dismiss="modal">Close</button>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-1"></div>
								</div>
								<div id="collapse{{video.id}}" class="accordion-body collapse">
									<div class="accordion-inner">
										<div class="row">
											<div class="col-md-1"></div>
											<div class="col-md-10">
												<label for="description">Beschreibung</label>
												<textarea class="form-control" rows="3"
													placeholder="Beschreibung" name="description" id=ription
													" ng-model="video.description"></textarea>
											</div>
											<div class="col-md-1"></div>
										</div>
										<br />
										<div class="row">
											<div class="col-md-1"></div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="tags">Tags</label> <input type="text"
														name="tags" class="form-control" id="tags"
														placeholder="z.B. tag1,tag2,tag3" ng-model="video.tags">
												</div>
											</div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="categoryId">Youtube Kategorie</label><select
														class="form-control" id="categoryId" name="categoryId"
														ng-model="video.categoryId">
														<option ng-repeat="category in cats" id="{{category.id}}">{{category.value}}</option>
													</select>
												</div>
											</div>
											<div class="col-md-1"></div>
										</div>
										<div class="row">
											<div class="col-md-1"></div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="relasedate">Spieletitel</label> <input
														type="text" name="shorttitle" class="form-control"
														id="shorttitle" placeholder="shorttitle"
														ng-model="video.shorttitle">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label for="relasedate">Genre</label> <input type="text"
														name="gerne" class="form-control" id="gerne"
														placeholder="Genre" ng-model="video.genre">
												</div>
											</div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="relasedate">Entwickler</label> <input
														type="text" name="developer" class="form-control"
														id="developer" placeholder="Entwickler"
														ng-model="video.developer">
												</div>
											</div>
											<div class="col-md-1"></div>
										</div>
										<div class="row">
											<div class="col-md-1"></div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="relasedate">Publisher</label> <input
														type="text" name="publisher" class="form-control"
														id="publisher" placeholder="publisher"
														ng-model="video.publisher">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label for="relasedate">Spiele-Veröffentlichung</label> <input
														ng-pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d$"
														type="text" name="published" class="form-control"
														id="published" placeholder="z.B. 12.12.2015"
														ng-model="video.published">
												</div>
											</div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="sel1">Playlist</label> <select
														class="form-control" id="playlist" name="playlist"
														ng-model="video.playlist">
														<option ng-repeat="playlistitem in playlist"
															id="{{playlistitem.id}}">{{playlistitem.value}}</option>
													</select>
												</div>
											</div>
											<div class="col-md-1"></div>
										</div>
										<div class="row">
											<div class="col-md-1"></div>
											<div class="col-md-3">
												<div class="form-group">
													<label for="relasedate">Alterbeschränkung</label> <input
														type="checkbox" name="ageRestricted" class="form-control"
														id="ageRestricted" ng-model="video.ageRestricted"></input>
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
				</div>
			</div>
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
		<script type="text/javascript" src="resources/dropzone/dropzone.js"></script>
		<script type="text/javascript">
			Dropzone.options.dropzone = {
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
		<script>
			$(document).ready(function() {
				$('[data-toggle="tooltip"]').tooltip();
			});
		</script>
</body>
</html>
