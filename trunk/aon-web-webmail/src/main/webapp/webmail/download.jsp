<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<% 
	com.code.aon.ui.webmail.controller.AttachController attachBean = 
		(com.code.aon.ui.webmail.controller.AttachController) session.getAttribute("attach");
	attachBean.getAttachment(""+request.getParameter("pos"),response);
%>
