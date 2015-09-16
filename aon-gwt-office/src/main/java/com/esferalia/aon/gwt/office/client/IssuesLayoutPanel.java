package com.esferalia.aon.gwt.office.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IssuesLayoutPanel extends Composite {

	private static IssuesLayoutPanelUiBinder uiBinder = GWT
			.create(IssuesLayoutPanelUiBinder.class);

	interface IssuesLayoutPanelUiBinder extends
			UiBinder<Widget, IssuesLayoutPanel> {
	}
	
	@UiField
	Label titleLabel;
	@UiField
	Label bodyLabel;

	public IssuesLayoutPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setTitle(String text) {
		titleLabel.setText(text);
	}
	
	public void setBody(String text) {
		bodyLabel.setText(text);
	}

}
