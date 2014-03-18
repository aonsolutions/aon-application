package com.code.aon.ui.config.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.ui.form.BasicController;

public class TagController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private TagType type;

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	@Override
	public void onReset(ActionEvent arg0) {
		super.onReset(arg0);
		Tag tag = (Tag) getTo();
		if ( getType() != null ) {
			tag.setType( getType() );
		}	
	}
	
}
