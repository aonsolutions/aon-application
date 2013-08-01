package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class MetaData extends ResizeComposite  {

	interface Listener {
	}

	interface Binder extends UiBinder<Widget, MetaData> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Button viewButton;
	@UiField
	Button collapseAllButton;


	public MetaData() {
		initWidget(binder.createAndBindUi(this));
	}

	
}
