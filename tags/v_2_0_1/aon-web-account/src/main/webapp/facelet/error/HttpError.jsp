<%@page pageEncoding="UTF-8" isErrorPage="true"%>
<%@page import="com.code.aon.common.AonException"%>
<%
	String error = request.getParameter("errorCode");
	request.setAttribute("aon_http_error_code",new Integer(error));
	AonException e = new AonException("HTTP Error: " + error);
%>
<jsp:forward page="/facelet/error/error.faces" />
