package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IssueReadPanel extends Composite {

	private static IssueReadPanelUiBinder uiBinder = GWT
			.create(IssueReadPanelUiBinder.class);

	interface IssueReadPanelUiBinder extends UiBinder<Widget, IssueReadPanel> {
	}
	
	@UiField
	Label idLabel;
	@UiField
	Label dateLabel;
	@UiField
	Label titleLabel;	
	@UiField
	TextArea commentTextArea;

	public IssueReadPanel(IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setNumber(issue.getId());
		setDate(issue.getCreateAt());
		setAsunto(issue.getTitle());
		setBody(issue.getBody());
	}
	
	private void setNumber(Integer id) {
		this.idLabel.setText(String.valueOf(id));
	}
	
	private void setDate(Date date) {
		this.dateLabel.setText(String.valueOf(date));
	}
	
	private void setAsunto(String asunto) {
		this.titleLabel.setText(asunto);
	}
	
	private void setBody(String body) {
		this.commentTextArea.setValue(body);
	}
}
