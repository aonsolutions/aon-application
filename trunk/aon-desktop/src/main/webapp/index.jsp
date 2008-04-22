<%@page import="com.code.aon.jaas.auth.AuthPrincipal"%>
<%@page import="java.security.Principal"%>
<%
	AuthPrincipal userPrincipal = null;
	Principal principal = request.getUserPrincipal();
	if ( principal instanceof AuthPrincipal ) {
		userPrincipal = (AuthPrincipal) principal;
	} else {
		userPrincipal = new AuthPrincipal( principal.getName() );
	}
	String username = userPrincipal.getShortName();
	String url = request.getScheme() + "://" + request.getHeader("host") + "" + request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Conectando a Aon-Solutions</title>
<style>
	html,body{height:100%;}
	body{font-size:11px;font-family:Arial, Helvetica, sans-serif;font-weight:bold;}
</style>
<script> 
	if(navigator.javaEnabled()==false) {
		alert("Su navegador no tiene Java activado. Por favor activelo antes de nada.");
	} 
</script> 
</head>

<body style="margin:auto;height:100%;">
<div style="height:100%;width:100%;text-align:center;vertical-align:middle;" />
	<table border="0" width="100%" height="90%">
		<tr>
			<td style="text-align:center;vertical-align:middle;">	
			<div style="padding:50px 0">
				<div style="font-size:20px;font-family:tahoma,arial,sans;margin-bottom:15px;">
					<img src="images/conectando/aon-solutions.gif" /> 
					<span style="color:#427ab3;"> / Desktop </span>	
				</div>	
				<div style="font-size:14px;font-weight:bold;">Conectando...</div>	
				<div><img src="images/conectando/puntos.gif" /></div>
					<div style="margin:20px 0;padding:5px;background-color:#fff;border:#f2f2f2 1px solid;font-weight:normal;font-size:11px;">



						Atención a usuarios: <br><br><b>902.121.009</b><br>
						<a href="mailto:soporte@esferalia.com" style="color:#427ab3;text-decoration:none;">soporte@esferalia.com</a>

					</div>
					<div style="color:#666;font-size:10px;font-weight:normal;">
					 aon Solutions es una marca registrada de <span ><span style="font-style:italic;font-size:11px;font-weight:bold;">esferalia</span> NETWORKS S.A.</span>
					</div>
				
				</div>
			</td>
		</tr>
	</table>
	<OBJECT codebase="http://java.sun.com/update/1.6.0/jinstall-6-windows-i586.cab" 
		classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"
		width="100%" height="10%">
			<param name="archive" value="AonSolutionsApplet.jar">
			<param name="code" value="AonSolutionsApplet.class">
			<param name="username" value="<%=username%>" >
			<param name="url" value="<%=url%>" >
		<COMMENT>
			<EMBED width="100%" height="25" code="AonSolutionsApplet.class"
				archive="AonSolutionsApplet.jar" type="application/x-java-applet"
				mayscript="true" username="<%=username%>" url="<%=url%>" 
				pluginspage="http://java.sun.com/products/plugin/index.html#download">
				<NOEMBED>
				</NOEMBED>
			</EMBED>
		</COMMENT>
	</OBJECT>
<!--
	<applet code="AonSolutionsApplet.class" archive="AonSolutionsApplet.jar" width="10" height="10"> 
		<param name="username" value="<%=username%>" >
		<param name="url" value="<%=url%>" >
	</applet>
	
-->
	
</div>
</body>
</html>
