package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

	private DateTimeFormat timeFormat;

	public IssueReadWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		flexTable.setWidth("70%");
		flexTable.setCellPadding(3);
		flexTable.setCellSpacing(5);

		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	public void addIssue(IssueSelected issue) {

		Widget header = buildHeader(issue.getUser().getLogin(), issue.getCreateAt());
		Label body = (Label) buildBody(issue.getBody());

		flexTable.setWidget(0, 0, header);
		flexTable.setWidget(1, 0, body);
	}

	public void addComment(JsIssueComment comment, Integer x) {

		Widget header = buildHeader(comment
				.getUser().getLogin(), comment.getCreatedAtString());
		Widget body = buildBody(comment.getBody());

		flexTable.setWidget(0, 0, header);
		flexTable.setWidget(1, 0, body);

		if ((x % 2) == 0) {
			flexTable.setStyleName(style.rowEven());
		}
	}

	private Widget buildHeader(String name, Object created) {

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		hPanel.setWidth(AON.AON_WIDTH_ALL);
		hPanel.setStyleName(style.issueHeader());

		Label header = new Label(name);
		header.setStyleName(AON.AON_BOLD);
		// header.addStyleName(style.issueHeader());

		Widget daysLabel = buildDaysLabel(created);

		hPanel.add(header);
		hPanel.add(daysLabel);

		return hPanel;
	}

	private Widget buildBody(String text) {
		Label body = new Label(text);
		body.setWidth(AON.AON_WIDTH_ALL);
		body.setStyleName(style.issueBody());

		return body;
	}
	
	private Widget buildDaysLabel (Object text) {
		
		Integer days;
		
		Label dateLabel = new Label();
		dateLabel.setStyleName(style.issueHeaderSubtitle());
		
		if (text instanceof Date)
			days = DateUtils.getDaysBetween((Date) text, new Date());
		
		else
			days = DateUtils.getDaysBetween(getParseDate((String) text), new Date());
		

		dateLabel.setText(" comentado hace " + days + " d\u00EDas");
		return dateLabel;
	 
	}

	private Date getParseDate(String date) {
		return timeFormat.parse(date);
	}
}
