package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Trash extends Composite {

	private static TrashUiBinder uiBinder = GWT.create(TrashUiBinder.class);

	interface TrashUiBinder extends UiBinder<Widget, Trash> {
	}

	public Trash() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
