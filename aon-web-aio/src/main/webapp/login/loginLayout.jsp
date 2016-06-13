<%@ page session="false" language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@page import="com.code.aon.ui.util.AonUtil" %>
<%@page import="java.io.InputStream" %>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.apache.commons.lang.StringUtils" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<jsp:useBean id="customize" class="com.code.aon.ui.resources.bean.CustomizeBean" scope="request"/>
<jsp:useBean id="companyDisplay" class="com.code.aon.ui.company.controller.CompanyDisplay" scope="request"/>
<%
try {
	boolean showError = "true".equals(request.getParameter("showError"));
	ResourceBundle commonBundle = customize.initMessages(request.getLocale());
	customize.initResources();
	customize.initApplicationVersion(application.getResourceAsStream("/META-INF/MANIFEST.MF"));
	String domainName = AonUtil.getServerName(request);
	customize.init(domainName);
	companyDisplay.init(domainName);
	
%>

<head>
	<title><%=StringUtils.isEmpty(customize.getApplicationTitle()) ? companyDisplay.getCompanyLabel() : customize.getApplicationTitle()  %></title>

	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8"/>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-store" />
	<link rel="stylesheet" href="aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css" />
	<link rel="shortcut icon" type="image/x-icon" href="<%=customize.getFavicon()%>" />
	<script type="text/javascript">
		function setMaxWidth(img, width) {
			if (img.width > width) {
	    		img.width = width;
			}
		}
	</script>	
</head>
<%
	com.code.aon.ui.dbutils.controller.DatabaseUptodate du = new com.code.aon.ui.dbutils.controller.DatabaseUptodate();
	if (du.isUpdatable()) {
		throw new com.code.aon.pool.AonConnectionException("La base de datos necesita ser actualizada. <br/> Versión actual de la BD: " + du.getCurrentVersion());
	}
	HttpSession session = request.getSession(false);
	if ( session != null ) {
		boolean loginServletFail = "true".equals(session.getAttribute("loginServletFail"));
		if ( loginServletFail ) {
			throw new com.code.aon.pool.AonConnectionException(commonBundle.getString("aon_login_err"));
		}		
	}
%>	
<body id="aon-body" onload="document.getElementById('j_username').focus();">
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
											<%=commonBundle.getString("aon_connected_to")%>
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<table class="aon-width-all">
											<tbody>
												<tr>
													<td class="aon-logo-left">
														<c:if test="${companyDisplay.withLogo}">
															<img
																title="<%=companyDisplay.getCompanyLabel()%>"
																style="max-width: 201px;"
																src="aonDocuments/company.logo"
																onload="setMaxWidth(this, 200);"/>
														</c:if>
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
						<form id="login" method="post" action="j_security_check"
							onsubmit="document.getElementById('login_btn').disabled = 'disabled';">
							<div class="aon-login-title">
								<img class="aon-graphicImage"
									src="<%=customize.getLoginLogo()%>" />
								<c:if test="${not empty customize.applicationTitle}">
									<c:if test="${not empty customize.loginSeparator}">
										<span style="<%=customize.getFontStyle()%>" class="aon-outputText"> <%=customize.getLoginSeparator()%> <%=customize.getApplicationTitle()%></span>
									</c:if>
									<c:if test="${empty customize.loginSeparator}">
										<span style="<%=customize.getFontStyle()%>" class="aon-outputText"> / <%=customize.getApplicationTitle()%></span>
									</c:if>
								</c:if>
							</div>

							<div class="aon-login-box">
								<span class="aon-login-help"><%=commonBundle.getString("aon_login_label")%></span>
<%
	if (showError) {
%>								
								<jsp:useBean id="failedLogin" class="com.code.aon.ui.common.controller.FailedLogin" scope="request"/>
								<div id="errorDiv" class="aon-errors" >
									<div class="aon-error-message">
										<%=failedLogin.getMessage(request)%>
									</div>
								</div>
<%
	}
%>								
								<table class="aon-width-all">
									<tr>
										<td class="aon-login-box-left">
											<label for="username" class="aon-login-label">
												<%=commonBundle.getString("aon_login_user")%>
											</label>
										</td>
										<td class="aon-login-box-right">
											<input type="text" id="j_username" name="j_username" class="aon-login-input" size="20" maxlength="64" />
										</td>
									</tr>
									<tr>
										<td class="aon-login-box-left">
											<label for="j_password" class="aon-login-label">
												<%=commonBundle.getString("aon_login_passwd")%>
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
													value="<%=commonBundle.getString("aon_login_validate")%>" 
													class="aon-login-button"
													onclick="document.getElementById('errorDiv').style.display = 'none';" />
										</td>
									</tr>
									<tr>
										<td class="aon-login-box-left">
											<label for="google-signin" class="aon-login-label">
												<%=commonBundle.getString("aon_login_with")%>
											</label>
										</td>
										<td class="aon-login-box-right">
											<c:url value="/oauth2" var="google_oauth2_url">
											</c:url>
											<a id="google-oauth2" href="${google_oauth2_url}" target="_blank" onClick="self.name='<%=domainName%>';window.open(this.href, this.target, 'width=600,height=800,scrollbars=yes'); return false;" >
												<img src="aonResource/com/code/aon/ui/resources/facelet/login/css/images/icons/google.png"/>
											</a>
												
											<!-- amazon -->
											<c:url value="/amazonoauth2" var="amazon_oauth2_url">
											</c:url>
											
										
											<a id="LoginWithAmazon" href="${amazon_oauth2_url}" target="_blank" onClick="self.name='<%=domainName%>';window.open(this.href, this.target, 'width=700,height=800,scrollbars=yes'); return false;" >
												<img src="aonResource/com/code/aon/ui/resources/facelet/login/css/images/icons/amazon.png"/>
											</a>
											
											
											<!-- github -->
											<c:url value="/githuboauth2" var="github_oauth2_url">
											</c:url>
											
										
											<a id="LoginWithGithub" href="${github_oauth2_url}" target="_blank" onClick="self.name='<%=domainName%>';window.open(this.href, this.target, 'width=700,height=800,scrollbars=yes'); return false;" >
												<img src="aonResource/com/code/aon/ui/resources/facelet/login/css/images/icons/github.png"/>
											</a>
											
														
										</td>
									</tr>
								</table>

								<div class="aon-login-info">
									<div class="aon-login-info-title">
										<%=commonBundle.getString("aon_support_title")%>
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
								<c:if test="${!(customize.hideTrademark and customize.hideVersion)}">
									<div class="aon-login-info2">	
										<c:if test="${!customize.hideTrademark}">
										    <c:if test="${customize.customized}">
												<span class="aon-outputText">
													<%=commonBundle.getString("aon_powered_by")%>
												</span>
										    </c:if>
											<a target="_blank"
												href="<%=commonBundle.getString("aon_solutions_url")%>">
												<span class="aon-outputText">
													<%=commonBundle.getString("aon_solutions")%>
												</span>
											</a>    
										    <c:if test="${!customize.customized}">
												<span class="aon-outputText">
													<%=commonBundle.getString("aon_trademark")%>
												</span>
												<span class="aon-footer-company-label">
													<%=commonBundle.getString("aon_solutions_ltd")%>
												</span>
											</c:if>
										</c:if>
										<c:if test="${!customize.hideVersion}">
											<c:if test="${customize.applicationVersion != null}">
												<div>
													<%
													String value = commonBundle.getString("aon_about_version");
										    		MessageFormat mf = new MessageFormat( value );
										    		out.print( mf.format(new Object[]{customize.getApplicationVersion()}) ); 
										    		%>
										    		<c:if test="${customize.buildDate != null}">
										    			 (<%= customize.getBuildDate() %>)
										    		</c:if>
									    		</div>
											</c:if>
											<div>
												<%=commonBundle.getString("aon_about_db_version")%> <%= du.getCurrentVersion() %>
								    		</div>
								    	</c:if>
									</div>
								</c:if>
							</div>
						</form> 
									
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
<%
	} catch (com.code.aon.pool.AonConnectionException e){
%>
<head>
<style type="text/css">
	body {color:#222; font-size:12px;font-family: sans-serif; background:#fff url('images/errorLoginBack.png') left top repeat-x;}
	h1 {font-size:150%;font-family:'Trebuchet MS', Verdana, sans-serif; color:#000}
	#page {font-size:122%;width:720px; margin:144px auto 0 auto;text-align:left;line-height:1.2;}
	#message {padding-right:400px;min-height:360px;background:transparent url('images/errorLogin.png') right top no-repeat;}
	.boton{font-size:12px;font-family:Verdana,Helvetica;font-weight:bold;color:white;background:#638cb5;border:0px;width:80px;height:26px;}
</style>
<meta http-equiv="Content-Type" content="application/xhtml; charset=utf-8" />
<link rel="shortcut icon" href="http://www.aonsolutions.es/favicon.ico" type="image/x-icon" />
<title>Error en acceso</title>
</head>
<body>
<div id="page">
	<div id="message">
		<h1>Error</h1>
		<p><%=e.getMessage()%></p>
		<p>&#160;</p>
		<form name="form1" action="javascript:history.back(1)" method="post">
		    <input type="submit" value="Volver" class="boton">
		</form>
	</div>
</div>
</body>
<%		
	}
%>

</html>