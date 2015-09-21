package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
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
	FlowPanel flowIssuesPanel;

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
		addIssue();
		addCommentIssues();
	}

	private void setHeaderTitle() {
		titleLabel.setText(issue.getTitle());
		numberIssueLabel.setText("#" + issue.getNumber());
		userCreateLabel.setText(issue.getUser().getLogin());
		
		int days = DateUtils.getDaysBetween(issue.getCreateAt(), (new Date()));
		
		whenCreateLabel.setText("Abierto hace " + days + " d\u00EDas - " + issue.getComments()
				+ " comentarios");
	}

	private void addIssue() {
		
		IssueReadWidget issueRead = new IssueReadWidget();
		issueRead.addIssue(issue);
		
		flowIssuesPanel.add(issueRead);

	}

	private void addCommentIssues() {
		
		for (int x = 0; x < issue.getIssueComments().length(); x++) {
			
			IssueReadWidget issueRead = new IssueReadWidget();
			issueRead.addComment(issue.getIssueComments().get(x));
			flowIssuesPanel.add(issueRead);
		}

		

	}
}
