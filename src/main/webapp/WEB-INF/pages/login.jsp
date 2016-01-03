<html>
<link rel="stylesheet" href="css/shadow.css">
<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.css">
<link rel="stylesheet"
	href="webjars/bootstrap-material-design/0.3.0/dist/css/material.css">
<link href="resources/dropzone/basic.css" rel="stylesheet" />
<link href="resources/dropzone/dropzone.css" rel="stylesheet" />
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<head>
<title>Login</title>
</head>
<body onload='document.f.username.focus();'>
	<h3>Anmeldung</h3>
	<form name='f' action='/YouTubeUploader/login' method='POST'>
		<div class="row">
			<label for="relasedate">Benutzer</label> <input type='text'
				name='username' value=''>
		</div>
		<div class="row">
			<label for="relasedate">Benutzer</label><input type='password'
				name='password' />
		</div>
		<div class="row">
			<input name="submit" type="submit" value="Login" />
		</div>
	</form>
</body>
<script type="text/javascript"
	src="webjars/bootstrap/3.3.6/js/bootstrap.js"></script>
<script type="text/javascript"
	src="webjars/bootstrap/3.3.6/js/collapse.js"></script>
<script type="text/javascript"
	src="webjars/bootstrap-material-design/0.3.0/dist/js/material.js"></script>
<script type="text/javascript" src="webjars/lodash/3.10.1/lodash.js"></script>

</html>