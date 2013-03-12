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
<title>Validate XSD Response</title>
<html:base />
</head>
<body bgcolor="white">
<html:form action="/ValidateXsd" method="post"
	enctype="multipart/form-data">
	<table>
		<tr>
			<td align="center" colspan="2"><font size="4">Validate XML</font></td>
		</tr>

		<tr>
			<td align="left" colspan="2">Validate XSD Result:</td>
		</tr>

		<tr>
			<td align="left" colspan="2"><html:textarea property="resultString" cols="100" rows="20" /></td>
		</tr>
	</table>


</html:form>
</body>
</html:html>