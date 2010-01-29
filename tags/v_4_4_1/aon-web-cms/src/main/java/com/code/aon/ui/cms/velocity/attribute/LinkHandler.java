package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.LinkDetail;

public class LinkHandler {

	private String label;
	
	private String link;
	
	private String description;

	public LinkHandler (LinkDetail ld) {
		label = ld.getLabel();
		link = ld.getLink().getUrl();
		description = ld.getDescription();
	}

	public String getLabel() {
		return label;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

}
