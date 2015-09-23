package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IssueReadWidget extends Composite {

	interface MyStyle extends CssResource {
		@ClassName("issue-header")
		String issueHeader();

		@ClassName("issue-body")
		String issueBody();

		@ClassName("row-even")
		String rowEven();
		
		@ClassName("issue-header-subtitle")
		String issueHeaderSubtitle();
	}

	private static IssueReadWidgetUiBinder uiBinder = GWT
			.create(IssueReadWidgetUiBinder.class);

	interface IssueReadWidgetUiBinder extends UiBinder<Widget, IssueReadWidget> {
	}

	@UiField
	MyStyle style;
	@UiField
	FlexTable flexTable;
	
	private Integer days;

	public IssueReadWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		flexTable.setWidth("70%");
		flexTable.setCellPadding(3);
		flexTable.setCellSpacing(5);

	}

	public void addIssue(IssueSelected issue) {
		
		Label header = (Label) buildHeader(issue.getUser().getLogin());
		Label body = (Label) buildBody(issue.getBody());

		flexTable.setWidget(0, 0, header);
		flexTable.setWidget(1, 0, body);
	}

	public void addComment(JsIssueComment comment, Integer x) {

		
		Label header = (Label) buildHeader(comment.getUser().getLogin());
		Label body = (Label) buildBody(comment.getBody());

		flexTable.setWidget(0, 0, header);
		flexTable.setWidget(1, 0, body);

		if ((x % 2) == 0) {
			flexTable.setStyleName(style.rowEven());
		}
	}
	
	private Widget buildHeader(String name) {
		
		Label header = new Label(name);
		header.setStyleName(AON.AON_BOLD);
		header.addStyleName(style.issueHeader());
		
		return header;
	}
	
	private Widget buildBody (String text) {
		Label body = new Label(text);
		body.setWidth(AON.AON_WIDTH_ALL);
		body.setStyleName(style.issueBody());
		
		return body;
	}
}
