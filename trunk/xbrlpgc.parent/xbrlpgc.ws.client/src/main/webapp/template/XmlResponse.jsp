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
<title>XBRL Transform Response</title>
<html:base />
</head>
<body bgcolor="white">
<html:form action="/TransformToXml" method="post"
	enctype="multipart/form-data">
	<table>
		<tr>
			<td align="center" colspan="2"><font size="4">Transformation to XML</font></td>
		</tr>
		
		<tr>
			<td align="left" colspan="2">General errors:</td>
		</tr>
		<tr>
			<td align="left" colspan="2"><html:textarea property="generalErrors" cols="100" rows="5" /></td>
		</tr>		
		<tr>
			<td align="left" colspan="2">Errors:</td>
		</tr>
		<tr>
			<td align="left" colspan="2"><html:textarea property="validateErrors" cols="100" rows="5" /></td>
		</tr>
		
		<tr>
			<td align="left" colspan="2">Transformation to XML Result:</td>
		</tr>

		<tr>
			<td align="left" colspan="2"><html:textarea property="xmlString" cols="100" rows="20" /></td>
		</tr>
	</table>


</html:form>
</body>
</html:html>