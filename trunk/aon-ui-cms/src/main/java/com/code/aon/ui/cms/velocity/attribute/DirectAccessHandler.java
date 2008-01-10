package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.ui.cms.util.MenuOptionUtil;

public class DirectAccessHandler {

	private String label;
	
	private String description;
	
	private String image;
	
	private String url;

	public DirectAccessHandler (DirectAccessDetail detail) {
		label = detail.getLabel();
		description = detail.getDescription();
		image = detail.getDirectAccess().getImage();
		url = getDirectAccessLink(detail);
	}
	
	private String getDirectAccessLink(DirectAccessDetail detail) {
		Integer ident = detail.getDirectAccess().getIdent();
		PageType pageType = detail.getDirectAccess().getType();
		ContentLevel level = detail.getDirectAccess().getLevel();
		String url = detail.getUrl();
		return MenuOptionUtil.getMenuOptionLink(ident, pageType, level, url);
	}
	
	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

}
