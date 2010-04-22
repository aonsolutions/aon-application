package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.LinkDetail;

public class LinkHandler {

	private String label;
	
	private String link;
	
	public LinkHandler (LinkDetail ld) {
		label = ld.getLabel();
		link = ld.getLink().getUrl();
	}

	public String getLabel() {
		return label;
	}

	public String getLink() {
		return link;
	}

}
