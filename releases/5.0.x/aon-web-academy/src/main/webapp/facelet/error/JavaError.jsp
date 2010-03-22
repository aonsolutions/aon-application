<%@page pageEncoding="UTF-8" isErrorPage="true"%>
<%
	request.setAttribute("aon_http_error_code", new Integer(500));
	request.setAttribute("aon_exception", exception);
%>
<jsp:forward page="/facelet/error/error.faces" />
