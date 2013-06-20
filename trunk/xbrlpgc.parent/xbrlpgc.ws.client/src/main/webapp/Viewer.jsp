<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insert template="template/template.jsp">
<tiles:put name="begin" content="begin.jsp" />
<tiles:put name="title" content="title.jsp" />
<tiles:put name="top" content="top.jsp" />
<tiles:put name="left" content="left.jsp" />
<tiles:put name="content" content="Viewer.jsp" />
</tiles:insert>