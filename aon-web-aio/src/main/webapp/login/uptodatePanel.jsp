<%@ page session="false" language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@page import="java.util.ResourceBundle"%>

<%
ResourceBundle dbutilsBundle = ResourceBundle.getBundle("com.code.aon.ui.dbutils.i18n.messages", request.getLocale());
%>	

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
