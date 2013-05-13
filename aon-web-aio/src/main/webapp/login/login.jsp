<%@ page session="false" language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">

<jsp:useBean id="failedLogin" class="com.code.aon.ui.common.controller.FailedLogin" scope="page"/>
<%
failedLogin.setShowError(false);
%>
<%@include file="loginLayout.jsp" %>
