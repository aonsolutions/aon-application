package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.enumeration.Templates;

public class LinkCategoryHandler {

	private String label;

	private String url;

	private ArrayList<LinkHandler> list;
	
	public LinkCategoryHandler (LinkCategoryDetail lcd, ArrayList<LinkHandler> list) {
		this.label = lcd.getLabel();
		String link = Templates.LINK.getHtmlName();
		link = link.replaceAll("%NAME%", lcd.getLinkCategory().getAlias());
		this.url = link;
		this.list = list;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public ArrayList<LinkHandler> getList() {
		return list;
	}

}
