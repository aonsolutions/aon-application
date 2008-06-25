<%@page pageEncoding="UTF-8" isErrorPage="true"%>
<%
	request.setAttribute("aon_http_error_code",request.getAttribute("errorCode"));
%>
<jsp:forward page="/facelet/error/error.faces" />
