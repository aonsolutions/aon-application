package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class IssuesLayoutPanel extends Composite {

	private static IssuesLayoutPanelUiBinder uiBinder = GWT
			.create(IssuesLayoutPanelUiBinder.class);

	interface IssuesLayoutPanelUiBinder extends
			UiBinder<Widget, IssuesLayoutPanel> {
	}
	
	@UiField
	InlineLabel titleLabel;
	@UiField
	InlineLabel numberIssueLabel;
	@UiField
	InlineLabel userCreateLabel;
	@UiField
	InlineLabel whenCreateLabel;
	@UiField
	Label titleIssue;
	@UiField
	TextArea bodyIssue;
	
	@UiField
	HorizontalPanel labelsPanel;
	
	private IssueSelected issue;

	public IssuesLayoutPanel(final IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.issue = issue;
		init();
	}
	
	
	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	private void init() {
		setHeaderTitle();
	}
	
	private void setHeaderTitle() {
		titleLabel.setText(issue.getTitle());
		numberIssueLabel.setText("#" + issue.getNumber());
		userCreateLabel.setText(issue.getUser().getLogin());
		whenCreateLabel.setText("Abierto hace ");
	}
	
	public void setUserCreated(String text) {
		userCreateLabel.setText(text);
		titleIssue.setText(text);
	}
	
	public void setTitleIssue(String text) {
		titleIssue.setText(userCreateLabel.getText());
	}
	
	public void setBodyIssueText(String text) {
		bodyIssue.setText(text);
	}
	
	public void addLabels(JsArray<JsLabel> labels) {
		
		for (int x = 0; x < labels.length() ; x++)
			addLabel(labels.get(x));
	}
	
	
	private void addLabel(JsLabel jsLabel) {
		Label label = new Label(jsLabel.getName());
		label.getElement().getStyle().setColor("#" + jsLabel.getColor());
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		labelsPanel.add(label);

	}

}
