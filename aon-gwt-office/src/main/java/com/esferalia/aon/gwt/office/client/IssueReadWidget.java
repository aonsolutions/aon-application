package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class IssueReadWidget extends Composite {
	
	interface MyStyle extends CssResource {
		@ClassName("issue-header")
		String issueHeader();
		
		@ClassName("issue-body")
		String issueBody();
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
		
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setText(issue.getBody());
		textArea.setWidth("100%");
		textArea.setStyleName(style.issueBody());
 
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(10);

		Label footerUser = new Label(issue.getUser().getLogin() + " added ");
		hPanel.add(footerUser);
		addLabels(hPanel, issue.getLabels());

		flexTable.setWidget(0, 0, issueHeader);
		flexTable.setWidget(1, 0, textArea);
		flexTable.setWidget(2, 0, hPanel);
	}
	
	public void addComment(JsIssueComment comment) {
		
		Label issueHeader = new Label(comment.getUser().getLogin());
		issueHeader.setStyleName(AON.AON_BOLD);
		issueHeader.addStyleName(style.issueHeader());
		
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setText(comment.getBody());
		textArea.setWidth("100%");
		textArea.setStyleName(style.issueBody());

		flexTable.setWidget(0, 0, issueHeader);
		flexTable.setWidget(1, 0, textArea);

	}
	
	private void addLabels(HorizontalPanel hPanel, JsArray<JsLabel> labels) {

		for (int x = 0; x < labels.length(); x++)
			hPanel.add(addLabel(labels.get(x)));
	}

	private Widget addLabel(JsLabel jsLabel) {
		Label label = new Label(jsLabel.getName());
		label.getElement().getStyle().setColor("#" + jsLabel.getColor());
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);

		return label;

	}


}
