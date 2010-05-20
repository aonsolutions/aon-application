package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ModularPageDetail;

public class ModularPageHandler {

	private String title;
	
	public ModularPageHandler (ModularPageDetail mpd) {
		title = mpd.getLabel();
	}

	public String getTitle() {
		return title;
	}

}
