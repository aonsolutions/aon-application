package com.esferalia.aon.gwt.office.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class IssueWriteWidget extends Composite {

	private static IssueWriteWidgetUiBinder uiBinder = GWT
			.create(IssueWriteWidgetUiBinder.class);

	interface IssueWriteWidgetUiBinder extends
			UiBinder<Widget, IssueWriteWidget> {
	}
	
	@UiField
	TabPanel tabPanel;
	@UiField
	TextArea commentTextArea;
	
	@UiField
	HorizontalPanel hPanel;
	
	@UiField
	Button commentButton;
	@UiField
	Button closeButton;

	public IssueWriteWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		tabPanel.selectTab(0);
		
		commentTextArea.setWidth("98%");
		commentTextArea.setHeight("99%");
		hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
	}
	
	
}
