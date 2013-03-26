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
<title>Viewer XBRL</title>
<html:base />
</head>
<body bgcolor="white">
<html:form action="/Viewer" method="post"
	enctype="multipart/form-data">
	<table>
		<tr>
			<td align="center" colspan="2"><font size="4">Please
			enter the XML or XBRL to view</font>
		</tr>

		<tr>
			<td align="left" colspan="2"><font color="red"><html:errors /></font>
		</tr>

		<tr>
			<td align="right">File Name</td>
			<td align="left"><html:file property="theFile" /></td>
		</tr>
		<tr>
			<td align="right">Module</td>
			<td align="left">
				<html:select property="module">
					<html:option value="bal">Balance</html:option>
					<html:option value="pyg">Pérdidas y Ganancias</html:option>
					<html:option value="patnetA">Patrimonio neto A</html:option>
					<html:option value="patnetB">Patrimonio neto B</html:option>				
					<html:option value="flujefec">Flujos efectivo</html:option>
					<html:option value="apartado0">Identificación</html:option>
					<html:option value="apartado14">Medio ambiente (Abreviado y Pymes)</html:option>
					<html:option value="apartado15">Medio ambiente (Normal)</html:option>
				</html:select>
			</td>
		</tr>
		
		<tr>
			<td align="center" colspan="2"><html:submit>View</html:submit>
			</td>
		</tr>
	</table>


</html:form>
</body>
</html:html>
