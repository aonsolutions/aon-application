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
<title>Transformation to XBRL</title>
<html:base />
</head>
<body bgcolor="white">
<html:form action="/TransformToXml" method="post"
	enctype="multipart/form-data">
	<table>
		<tr>
			<td align="center" colspan="2"><font size="4">Please
			enter the XBRL file for transform to XML</font>
		</tr>

		<tr>
			<td align="left" colspan="2"><font color="red"><html:errors /></font>
		</tr>



		<tr>
			<td align="right">File Name</td>
			<td align="left"><html:file property="theFile" /></td>
		</tr>
		<tr>
			<td align="right">Validate XBRL?</td>
			<td align="left"><html:checkbox property="validateXbrl"></html:checkbox> </td>
		</tr>

		<tr>
			<td align="center" colspan="2"><html:submit>Transform</html:submit>
			</td>
		</tr>
	</table>


</html:form>
</body>
</html:html>
