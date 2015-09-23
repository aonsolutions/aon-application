package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IssueReadWidget extends Composite {

	interface MyStyle extends CssResource {
		@ClassName("issue-header")
		String issueHeader();

		@ClassName("issue-body")
		String issueBody();
		
		@ClassName("align")
		String align();
	}

	private static IssueReadWidgetUiBinder uiBinder = GWT
			.create(IssueReadWidgetUiBinder.class);

	interface IssueReadWidgetUiBinder extends UiBinder<Widget, IssueReadWidget> {
	}

	@UiField
	MyStyle style;
	@UiField
	FlexTable flexTable;

	public IssueReadWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		flexTable.setWidth("70%");
		flexTable.setCellPadding(3);
		flexTable.setCellSpacing(5);
	}

	public void addIssue(IssueSelected issue) {

		Label issueHeader = new Label(issue.getUser().getLogin());
		issueHeader.setStyleName(AON.AON_BOLD);
		issueHeader.addStyleName(style.issueHeader());

		Label body = new Label(issue.getBody());
		body.setWidth("100%");
		body.setStyleName(style.issueBody());

		flexTable.setWidget(0, 0, issueHeader);
		flexTable.setWidget(1, 0, body);
	}

	public void addComment(JsIssueComment comment, Integer x) {

		Label issueHeader = new Label(comment.getUser().getLogin());
		issueHeader.setStyleName(AON.AON_BOLD);
		issueHeader.addStyleName(style.issueHeader());

		Label body = new Label(comment.getBody());
		body.setWidth("100%");
		body.setStyleName(style.issueBody());

		flexTable.setWidget(0, 0, issueHeader);
		flexTable.setWidget(1, 0, body);
	}
}
