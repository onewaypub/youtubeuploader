<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Connect to Google</h3>

<form action="<c:url value="/connect/google" />.do?${_csrf.parameterName}=${_csrf.token}" method="POST">
	<div class="formInfo">
		<p>
			You haven't created any connections with Google yet. Click the button to connect Spring Social Showcase with your Twitter account. 
			(You'll be redirected to Twitter where you'll be asked to authorize the connection.)
		</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/google/connect-with-google.png" />"/></button></p>
	<label for="postTweet"><input id="postTweet" type="checkbox" name="postTweet" /> Post a tweet about connecting with Spring Social Showcase</label>
</form>
