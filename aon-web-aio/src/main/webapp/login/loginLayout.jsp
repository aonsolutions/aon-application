<%@ page session="false" language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@page import="java.io.InputStream" %>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<jsp:useBean id="dbUptodate" class="com.code.aon.ui.dbutils.controller.DatabaseUptodate" scope="request"/>
<jsp:useBean id="login" class="com.code.aon.ui.resources.bean.LoginBean" scope="request"/>
<jsp:useBean id="failedLogin" class="com.code.aon.ui.common.controller.FailedLogin" scope="request"/>
<jsp:useBean id="customize" class="com.code.aon.ui.resources.bean.CustomizeBean" scope="request"/>
<jsp:useBean id="companyDisplay" class="com.code.aon.ui.company.controller.CompanyDisplay" scope="request"/>
<%
login.init(request.getServerName(), request.getContextPath());
dbUptodate.init(login.getDbProperties(), request.getServerName(), request.getLocale());
failedLogin.setShowError("true".equals(request.getParameter("showError")));
customize.initMessages(request.getLocale());
customize.initResources(login.getResolver());
customize.initApplicationVersion(application.getResourceAsStream("META-INF/MANIFEST.MF"));
customize.init(login.getDbProperties(), request.getServerName());
companyDisplay.init(login.getDbProperties(), request.getServerName());
ResourceBundle securityBundle = ResourceBundle.getBundle("com.code.aon.bridge.i18n.messages", request.getLocale());
ResourceBundle companyBundle = ResourceBundle.getBundle("com.code.aon.ui.company.i18n.messages", request.getLocale());
ResourceBundle dbutilsBundle = ResourceBundle.getBundle("com.code.aon.ui.dbutils.i18n.messages", request.getLocale());
%>	
<head>
	<title><%=customize.getApplicationTitle()%></title>
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8"/>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-store" />
	<link rel="stylesheet" href="aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css" />
	<link rel="shortcut icon" type="image/x-icon" href="<%=customize.getFavicon()%>" />
</head>

<body id="aon-body" onload="document.getElementById('j_username').focus();">


	<c:if test="${dbUptodate.uptodate and dbUptodate.connectionAvailable}">
		<table style="padding: 5px;margin-left: auto;margin-right: auto;margin-top: 200px;border:#ccc 1px solid;background-color:#e2f1f5;">
			<tbody>
				<tr>
					<td>
						<span style="font-weight:bold;font-size:1.2em;">
							<%=dbutilsBundle.getString("dbutils_database_uptodate")%>
						</span>
					</td>
				</tr>
				<tr>
					<td>
						<div class="aon-scroll-area" style="width: 90%;">
							<div style="width: 100%; border: solid 1px black;margin-top: 20px;">
								<div style="margin: 5px;">
									<span class="aon-outputText">
										<%=dbutilsBundle.getString("dbutils_database_version")%>: <%=dbUptodate.getCurrentVersion()%>
									</span>
								</div>
							</div>
							<div style="width: 100%; border: solid 2px red;text-align: center;;margin-top: 20px;">
								<div style="margin: 5px; color:red; font-size: 1.2em;">
									<span class="aon-outputText">
										<%=dbutilsBundle.getString("dbutils_update")%>
									</span>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</c:if>	
	<c:if test="${!dbUptodate.connectionAvailable}">
		<table style="padding: 5px;margin-left: auto;margin-right: auto;margin-top: 200px;border:#ccc 1px solid;background-color:#e2f1f5;">
			<tbody>
				<tr>
					<td>
						<span style="font-weight:bold;font-size:1.2em;">ERROR</span>
					</td>
				</tr>
				<tr>
					<td>
						<div class="aon-scroll-area" style="width: 90%;">
							<div style="width: 100%; border: solid 1px black;margin-top: 20px;">
								<div style="margin: 5px;">
									<span class="aon-outputText">
										<%=dbutilsBundle.getString("dbutils_database_error")%>
									</span>
								</div>
							</div>
							<div style="width: 100%; border: solid 2px red;text-align: center;;margin-top: 20px;">
								<div style="margin: 5px; color:red; font-size: 1.2em;">
									<span class="aon-outputText">
										<%=dbUptodate.getConnectionErrorMessage()%>
									</span>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</c:if>
	<c:if test="${!dbUptodate.uptodate and dbUptodate.connectionAvailable}">	
		<div class="aon-login">
		
			<table class="aon-width-all aon-height-all">
				<tr>
				
					<c:if test="${companyDisplay.show}">
					
						<td class="aon-width-300">
							<table class="aon-width-all aon-text-center">
								<tbody>
									<tr>
										<td>
											<span class="aon-outputText">
												<%=companyBundle.getString("company_connected_to")%>
											</span>
										</td>
									</tr>
									<tr>
										<td>
											<table class="aon-width-all">
												<tbody>
													<tr>
														<td class="aon-logo-left">
															<img
																title="<%=companyDisplay.getCompanyLabel()%>"
																style="<%=companyDisplay.getLogoStyle()%>"
																src="aonDocuments/company.logo" />
														</td>
													</tr>
												</tbody>
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<div class="aon-company-label">
												<span class="aon-outputText">
													<%=companyDisplay.getCompanyLabel()%>
												</span>
											</div>
										</td>
									</tr>
								</tbody>
							</table>
						</td>				
	
					</c:if>
	
					<td class=" aon-width-auto">			
						<div class="aon-login-box-internal">
							<form id="login" method="post" action="j_security_check">
								<div class="aon-login-title">
									<img class="aon-graphicImage"
										src="<%=customize.getLoginLogo()%>" />
									<span style="<%=customize.getFontStyle()%>" class="aon-outputText">
										/ <%=customize.getApplicationTitle()%>
									</span>
								</div>
	
								<div class="aon-login-box">
									<span class="aon-login-help"><%=securityBundle.getString("aon_login_label")%></span>
	
									<c:if test="${failedLogin.showError}">
										<div id="errorDiv" class="aon-errors" >
											<div class="aon-error-message">
												<%=failedLogin.getMessage()%>
											</div>
										</div>
									</c:if>
	
									<table class="aon-width-all">
										<tr>
											<td class="aon-login-box-left">
												<label for="username" class="aon-login-label">
													<%=securityBundle.getString("aon_login_user")%>
												</label>
											</td>
											<td class="aon-login-box-right">
												<input type="text" id="j_username" name="j_username" class="aon-login-input" size="20" maxlength="64" />
											</td>
										</tr>
										<tr>
											<td class="aon-login-box-left">
												<label for="j_password" class="aon-login-label">
													<%=securityBundle.getString("aon_login_passwd")%>
												</label>
											</td>
											<td class="aon-login-box-right">
												<input type="password" id="j_password" name="j_password" class="aon-login-input" size="20" maxlength="16" />
											</td>
										</tr>
										<tr>
											<td class="aon-login-box-left">
												&#160;
											</td>
											<td class="aon-login-box-right">
												<input id="login_btn" name="login_btn" type="submit" 
														value="<%=securityBundle.getString("aon_login_validate")%>" 
														class="aon-login-button"
														onclick="document.getElementById('errorDiv').style.display = 'none';" />
											</td>
										</tr>
									</table>
									<div class="aon-login-info">
										<div class="aon-login-info-title">
											<%=customize.getBundle().getString("aon_support_title")%>
										</div>
			
										<div class="aon-bold">
											<%=customize.getSupportTelephone()%>
										</div>
										
										<div>
											<a target="_blank"
												href="mailto:<%=customize.getSupportEmail()%>">
												<span style="<%=customize.getFontStyle()%>" class="aon-outputText">
													<%=customize.getSupportEmail()%>
												</span>
											</a>
										</div>									
									</div>							
									<div class="aon-login-info2">	
										<c:if test="${!customize.hideTrademark}">
										    <c:if test="${customize.customized}">
												<span class="aon-outputText">
													<%=customize.getBundle().getString("aon_powered_by")%>
												</span>
										    </c:if>
											<a target="_blank"
												href="<%=customize.getBundle().getString("aon_solutions_url")%>">
												<span class="aon-outputText">
													<%=customize.getBundle().getString("aon_solutions")%>
												</span>
											</a>    
										    <c:if test="${!customize.customized}">
												<span class="aon-outputText">
													<%=customize.getBundle().getString("aon_trademark")%>
												</span>
												<span class="aon-footer-company-label">
													<%=customize.getBundle().getString("aon_esferalia")%> <%=customize.getBundle().getString("aon_networks")%>
												</span>
											</c:if>
										</c:if>
										<c:if test="${customize.applicationVersion != null}">
											<div>
												<%
												String value = customize.getBundle().getString("aon_about_version");
									    		MessageFormat mf = new MessageFormat( value );
									    		out.print( mf.format(new Object[]{customize.getApplicationVersion()}) ); 
									    		%>
								    		</div>
										</c:if>	
									</div>
								</div>
							</form>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</c:if>			
</body>
</html>