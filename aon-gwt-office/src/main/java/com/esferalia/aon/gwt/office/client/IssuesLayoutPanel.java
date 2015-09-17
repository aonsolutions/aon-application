package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class IssuesLayoutPanel extends Composite {
	
	interface MyStyle extends CssResource {
		@ClassName("issue-header")
		String issueHeader();
		
	}

	private static IssuesLayoutPanelUiBinder uiBinder = GWT
			.create(IssuesLayoutPanelUiBinder.class);

	interface IssuesLayoutPanelUiBinder extends
			UiBinder<Widget, IssuesLayoutPanel> {
	}
	
	@UiField
	MyStyle style;
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
	}
	
	private void setHeaderTitle() {
		titleLabel.setText(issue.getTitle());
		numberIssueLabel.setText("#" + issue.getNumber());
		userCreateLabel.setText(issue.getUser().getLogin());
		whenCreateLabel.setText("Abierto hace .... - " + issue.getComments() + " comentarios");
	}
	
	private void addIssue() {
		
		FlexTable table = new FlexTable();
				
		table.setWidth("70%");
		table.setCellSpacing(5);
		table.setCellPadding(3);
		
		Label issueHeader = new Label(issue.getUser().getLogin());		
		issueHeader.setStyleName(AON.AON_BOLD);
		issueHeader.addStyleName(style.issueHeader());
		
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setText(issue.getBody());
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(10);
		
		Label footerUser = new Label(issue.getUser().getLogin() + " added ");
		hPanel.add(footerUser);
		addLabels(hPanel, issue.getLabels());
		
		table.setWidget(0, 0, issueHeader);
		table.setWidget(1, 0, textArea);
		table.setWidget(2, 0, hPanel);
		
		flowIssuesPanel.add(table);
		
	}
	
	private void addLabels(HorizontalPanel hPanel, JsArray<JsLabel> labels) {
		
		for (int x = 0; x < labels.length() ; x++)
			hPanel.add(addLabel(labels.get(x)));
	}
	
	
	private Widget addLabel(JsLabel jsLabel) {
		Label label = new Label(jsLabel.getName());
		label.getElement().getStyle().setColor("#" + jsLabel.getColor());
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		return label;

	}

}
