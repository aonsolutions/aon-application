package com.esferalia.aon.gwt.office.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class IssueWriteWidget extends Composite {

	private static IssueWriteWidgetUiBinder uiBinder = GWT
			.create(IssueWriteWidgetUiBinder.class);

	interface IssueWriteWidgetUiBinder extends
			UiBinder<Widget, IssueWriteWidget> {
	}

	@UiField
	TabLayoutPanel tabPanel;

	public IssueWriteWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		

	}

}
