<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>



<html:html locale="true">
<head>
<title>Viewer Response</title>
<html:base />
</head>
<body bgcolor="white">
<html:form action="/Viewer" method="post" 
	enctype="multipart/form-data">
	<table>
		<tr>
			<td align="center" colspan="2"><font size="4">Viewer XBRL</font></td>
		</tr>
		<tr>
			<td align="left" colspan="2">General errors:</td>
		</tr>
		<tr>
			<td align="left" colspan="2"><html:textarea property="errors" cols="100" rows="5" /></td>
		</tr>
		
		
		<tr>
			<td align="left" colspan="2">Viewer Result &nbsp;&nbsp;
			<html:submit>preview</html:submit>
		</tr>
		<tr>
			<td align="left" colspan="2"><html:textarea property="htmlString" cols="100" rows="20" /></td>
		</tr>
	</table>


</html:form>
</body>
</html:html>