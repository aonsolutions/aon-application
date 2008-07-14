<% 
	com.code.aon.ui.webmail.controller.MessageController messageBean = 
		(com.code.aon.ui.webmail.controller.MessageController) session.getAttribute("message");
%>
<%=messageBean.getMessage().getContent()%>
