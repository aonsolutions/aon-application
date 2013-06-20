<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<!-- H E A D -->
<tiles:get name="begin"/>
<!-- / H E A D -->

<body>

<!-- T I T R E -->
<tiles:get name="title"/>

<!-- / T I T R E -->

<table cellspacing="1" cellpadding="0" border="0" width="80%" align="center" bgcolor="#336699">

<!-- N A V I G A T I O N    N E W S -->
<tr><td bgcolor="#FFFFFF" valign="top"><tiles:get name="top" /></td></tr>
<tr><td bgcolor="#FFFFFF" valign="top">
<table cellspacing="1" cellpadding="1" border="0" width="100%" align="center">



<tr valign="top">
<td align="center"><br/><tiles:get name="content"/></td>
</tr>

</table></td></tr></table>

</body>
</html>