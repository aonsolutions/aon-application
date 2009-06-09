<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<% 
	com.code.aon.ui.webmail.controller.MessageController messageBean = 
		(com.code.aon.ui.webmail.controller.MessageController) session.getAttribute("message");
%>
<%=messageBean.getMessage().getContent()%>
