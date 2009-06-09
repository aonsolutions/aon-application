<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<% 
	java.util.ResourceBundle bundle = 
		java.util.ResourceBundle.getBundle("com.code.aon.ui.webmail.i18n.messages");
	com.code.aon.ui.webmail.controller.MessageController messageBean = 
		(com.code.aon.ui.webmail.controller.MessageController) session.getAttribute("message");
	com.code.aon.ui.common.controller.ConfigurationController configBean = 
		(com.code.aon.ui.common.controller.ConfigurationController) session.getAttribute("aonConfiguration");
%>
<%
	java.security.Principal principal = request.getUserPrincipal();
	com.code.aon.jaas.auth.AuthPrincipal user; 
	if ( principal instanceof com.code.aon.jaas.auth.AuthPrincipal ) {
		user = (com.code.aon.jaas.auth.AuthPrincipal) principal;
	} else {
		user = new com.code.aon.jaas.auth.AuthPrincipal( principal.getName() );
	}
	com.code.aon.common.IManagerBean beanUser = com.code.aon.common.BeanManager.getManagerBean(com.code.aon.config.User.class);
	com.code.aon.ql.Criteria criteriaUser = new com.code.aon.ql.Criteria();
	criteriaUser.addExpression(beanUser.getFieldName(com.code.aon.config.dao.IConfigAlias.USER_LOGIN), user.getShortName());
	java.util.Iterator iterUser = beanUser.getList(criteriaUser).iterator();
	String username = "";
	if (iterUser.hasNext()){
		username = ((com.code.aon.config.User)iterUser.next()).getName();
	}
	java.util.Date date = new java.util.Date();
	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("EEE, dd/MM/yy-HH:mm");
%>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/iceCss/aon-iceCss.css"/>
	<table cellpading="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td>
				<div class="aon-text-bold"><%=username%></div>
			</td>
			<td style="text-align: right;">
				<div><%=df.format(date)%></div>
			</td>
		</tr>
	</table>
<hr>
<div style="font-size:10px; font-family:arial,verdana,sans;margin:10 0 30 0px;">
	<table cellpading="0" cellspacing="0" border="0">
		<tr>
			<td>
				<div class="aon-text-bold"><%=bundle.getString("aon_webmail_from")%>:</div>
			</td>
			<td>
				<div><%=messageBean.getMessage().getSender()%></div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="aon-text-bold"><%=bundle.getString("aon_webmail_to")%>:</div>
			</td>
			<td>
				<div><%=messageBean.getMessage().getRecipientsTo()%></div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="aon-text-bold"><%=bundle.getString("aon_webmail_cc")%>:</div>
			</td>
			<td>
				<div><%=messageBean.getMessage().getRecipientsCc()%></div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="aon-text-bold"><%=bundle.getString("aon_webmail_date")%>:</div>
			</td>
			<td>
				<div><%=messageBean.getMessage().getSentDateString()%></div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="aon-text-bold"><%=bundle.getString("aon_webmail_subject")%>:</div>
			</td>
			<td>
				<div><%=messageBean.getMessage().getSubject()%></div>
			</td>
		</tr>
	</table>
</div>
<%=messageBean.getMessage().getContent()%>
<script>
	window.print();
</script>
