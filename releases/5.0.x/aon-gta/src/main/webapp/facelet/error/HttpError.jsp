<%@page pageEncoding="UTF-8" isErrorPage="true"%>
<%@page import="com.code.aon.common.AonException"%>
<%@page import="java.util.Enumeration"%>
<%
	request.setAttribute("aon_http_error_code",request.getAttribute("errorCode"));
%>
<jsp:forward page="/facelet/error/error.faces" />
