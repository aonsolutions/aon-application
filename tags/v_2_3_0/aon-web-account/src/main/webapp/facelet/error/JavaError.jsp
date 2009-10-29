<%@page pageEncoding="UTF-8" isErrorPage="true"%>
<%
		try {
		request.setAttribute("aon_http_error_code", new Integer(500));
		request.setAttribute("aon_exception", exception);
%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.PrintStream"%>
<jsp:forward page="/facelet/error/error.faces" />
<%
		} catch (Throwable t) {
		FileOutputStream fos = new FileOutputStream("/tmp/errorPuto.txt");
		PrintStream ps = new PrintStream(fos);
		t.printStackTrace( ps );
	}
%>
