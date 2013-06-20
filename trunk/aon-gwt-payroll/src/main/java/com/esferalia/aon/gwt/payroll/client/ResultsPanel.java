package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class ResultsPanel extends ResizeComposite {

	interface Binder extends UiBinder<Widget, ResultsPanel> { }
	private static final Binder binder = GWT.create(Binder.class);

	
	@UiField HTML htmlPanel;
	
	public ResultsPanel() {
		initWidget(binder.createAndBindUi(this));
	}
	
	
	public void setHTML(String html) {
		htmlPanel.setHTML(html);
	}
	
}
