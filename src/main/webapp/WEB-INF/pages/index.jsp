<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
				<h1>Neues Video</h1>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-7"></div>
			<div class="col-md-1">
				<form action="settings.do?${_csrf.parameterName}=${_csrf.token}"
					method="post" enctype="multipart/form-data" align="center">
					<button type="submit" class="btn btn-default">Einstellungen</button>
				</form>
			</div>
			<div class="col-md-1" align="center">
				<a href="logout.do" class="btn btn-default">Abmelden</a>
			</div>
			<div class="col-md-1" align="center">
				<a href="list.do?${_csrf.parameterName}=${_csrf.token}"
					class="btn btn-default">Reload</a>
			</div>
			<div class="col-md-1"></div>
		</div>
		<form action="add.do?${_csrf.parameterName}=${_csrf.token}"
			method="post" enctype="multipart/form-data"
			modelattribute="videolist">
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-10">
					<div class="form-group">
						<label for="title">Titel</label> <input type="text" name="title"
							class="form-control" id="title" placeholder="Titel" required="required">
					</div>
					<div class="form-group">
						<label for="description">Beschreibung</label>
						<textarea class="form-control" rows="3" placeholder="Beschreibung"
							name="description" id="description" required="required"></textarea>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-3">
					<div class="form-group">
						<label for="tags">Tags (z.B. tag1,tag2,tag3)</label> <input type="text" name="tags"
							class="form-control" id="tags" placeholder="Tags" required="required">
					</div>
				</div>
				<div class="col-md-4">
					<div class="form-group">
						<label for="relasedate">Ver�ffentlichungsdatum (z.B. 12.12.2015 10:00)</label> <input pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d [012]{0,1}[0-9]:[0-6][0-9]$"
							type="datetime" class="form-control" id="timestamp"
							name="timestamp" placeholder="Ver�ffentlichungsdatum" required="required">
					</div>
				</div>
				<div class="col-md-3">
					<div class="form-group">
						<label for="relasedate">Youtube Kategorie</label><select
							class="form-control" id="categoryId" name="categoryId" required="required">
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
							placeholder="shorttitle" required="required">
					</div>
				</div>
				<div class="col-md-4">
					<div class="form-group">
						<label for="relasedate">Genre</label> <input type="text"
							name="gerne" class="form-control" id="gerne" placeholder="Genre">
					</div>
				</div>
				<div class="col-md-3">
					<div class="form-group">
						<label for="relasedate">Entwickler</label> <input type="text"
							name="developer" class="form-control" id="developer"
							placeholder="Entwickler" required="required">
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
							placeholder="publisher" required="required">
					</div>
				</div>
				<div class="col-md-4">
					<div class="form-group">
						<label for="relasedate">Spiele-Ver�ffentlichung (z.B. 12.12.2015)</label> <input pattern="^([1-9]|([012][0-9])|(3[01])).([0]{0,1}[1-9]|1[012]).\d\d\d\d$"
							type="text" name="published" class="form-control" id="published"
							placeholder="Ver�ffentlichung">
					</div>
				</div>
				<div class="col-md-3">
					<c:if test="${not empty playlist}">
						<div class="form-group">
							<label for="sel1">Playlist</label> <select class="form-control"
								id="playlist" name="playlist" required="required">
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
						<label for="relasedate">Alterbeschr�nkung</label> <input type="checkbox" name="ageRestricted"
								class="form-control" id="ageRestricted"></input>
					</div>
				</div>
				<div class="col-md-4">
				</div>
				<div class="col-md-3">
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="thumbnail">Thumbnail</label><br> <input
							id="thumbfile" type="file" style="display: none" accept="image/*"
							name="thumbnail" placeholder="Thumbnaildatei" required="required">
						<div class="input-append">
							<input id="thumb" class="input-large" type="text"> <a
								class="btn btn-primary"
								onclick="$('input[id=thumbfile]').click();">Browse</a>
						</div>
						<script type="text/javascript">
							$('input[id=thumbfile]').change(function() {
								$('#thumb').val($(this).val());
							});
						</script>
					</div>
				</div>
				<div class="col-md-8">
					<div class="form-group">
						<label for="video">Video</label> <input id="vidfile" type="file"
							name="video" style="display: none" accept="video/*"
							placeholder="Thumbnaildatei" required="required">
						<div class="input-append">
							<input id="vid" class="input-large" type="text"> <a
								class="btn btn-primary"
								onclick="$('input[id=vidfile]').click();">Browse</a>
						</div>
						<script type="text/javascript">
							$('input[id=vidfile]').change(function() {
								$('#vid').val($(this).val());
							});
						</script>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-10">
					<button type="submit" class="btn btn-default">Abschicken</button>
				</div>
				<div class="col-md-1"></div>
			</div>
		</form>
		<c:if test="${not empty videolist}">
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-10">
					<h1>Videosliste</h1>
					<table class="table table-striped">
						<thead>
							<tr>
								<th>#</th>
								<th>Thumbnail</th>
								<th>Ver�ffentlichungsdatum</th>
								<th>Titel</th>
								<th>Beschreibung</th>
								<th>Privacy Einstellung</th>
								<th>Status</th>
								<th>Aktionen</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="video" items="${videolist}">
								<c:if test="${not empty video.errors}">
									<tr data-toggle="collapse" data-target="#details${video.id}"
										class="warning accordion-toggle">
								</c:if>
								<c:if test="${empty video.errors}">
									<tr data-toggle="collapse" data-target="#details${video.id}"
										class="accordion-toggle">
								</c:if>
								<th scope="row">${video.id}</th>
								<td><img
									src="getThumbnailImage/<c:out value="${video.id}"/>.do" /></td>
								<td><fmt:formatDate value="${video.releaseDate}"
										type="both" pattern="dd.MM.yyyy HH:mm" /></td>
								<td>${video.title}</td>
								<td>${video.description}</td>
								<td>${video.privacySetting.displayName}</td>
								<td>${video.state.displayName}</td>
								<td><form
										action="delete.do?${_csrf.parameterName}=${_csrf.token}"
										method="post">
										<input type="hidden" name="id" value="${video.id}">
										<button type="submit"
											class="btn btn-primary btn-xs btn-danger">
											<span class="glyphicon glyphicon-remove-sign"></span>
										</button>
									</form>
									<button class="btn btn-default btn-xs">
										<span class="glyphicon glyphicon-eye-open"></span>
									</button></td>
								</tr>
								<tr>
									<td colspan="15" class="hiddenRow">
										<div class=" accordian-body collapse" id="details${video.id}">
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">Fehler</label>
												</div>
												<div class="col-md-10">
													<c:forEach var="errorItem" items="${video.errors}">${errorItem}<br />
													</c:forEach>
												</div>
											</div>
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">ThumbnailUpload Status</label>
												</div>
												<div class="col-md-10">${video.thumbnailUploadState.displayName}</div>
											</div>
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">Video Upload Status</label>
												</div>
												<div class="col-md-8">${video.videoUploadState.displayName}</div>
											</div>
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">Video URL</label>
												</div>
												<div class="col-md-8"><a href="${video.videoUrl}">${video.videoUrl}</a></div>
											</div>
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">Video ID</label>
												</div>
												<div class="col-md-8">${video.youtubeId}</div>
											</div>
											<div class="row">
												<div class="col-md-2">
													<label for="thumbnail">Tags</label>
												</div>
												<div class="col-md-8">
													<c:forEach var="tagsItem" items="${video.tags}">${tagsItem}<br />
													</c:forEach>
												</div>
											</div>
											<c:if
												test="${(video.state ne 'WaitForProcessing') || (video.state ne 'Done') || (video.state ne 'Error')}">
												<div align="center"
													class="embed-responsive embed-responsive-16by9">
													<video controls loop class="embed-responsive-item"
														preload="none">
														<source src="getVideo/<c:out value="${video.id}"/>.do?t=<c:out value="${random}"/>"
															type="video/mp4">
													</video>
												</div>
											</c:if>
										</div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="col-md-1"></div>
			</div>
		</c:if>
	</div>
	<!-- jQuery (wird f�r Bootstrap JavaScript-Plugins ben�tigt) -->
	<script src="js/sockjs-0.3.4.js"></script>
	<script src="js/stomp.js"></script>
</body>
</html>