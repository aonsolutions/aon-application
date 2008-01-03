package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.ui.cms.velocity.GenericGenerator;

public class SidebarOptionHandler {

	private String template;
	
	private Object content;

	public SidebarOptionHandler(SidebarOptionDetail sidebarOptionDetail) {
		template = sidebarOptionDetail.getSidebar_option().getType().getTemplateName();
		if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE)) {
			content = null;
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.BANNER)) {
			content = null;
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.GENERIC)) {
			content = GenericGenerator.getGenericHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.LINK)) {
			content = null;
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.MENU)) {
			content = null;
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}
	
}
