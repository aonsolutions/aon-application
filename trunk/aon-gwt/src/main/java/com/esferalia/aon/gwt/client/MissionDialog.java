package com.esferalia.aon.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class MissionDialog extends CustomDialog {
	
	interface Binder extends UiBinder<Widget, MissionDialog> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	public MissionDialog() {
		setTitle("Misión");
		setWidget(binder.createAndBindUi(this));
	}

}
