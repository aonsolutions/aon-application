package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.values.IssueValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IssuesLayoutPanel extends Composite {

	interface MyStyle extends CssResource {
		@ClassName("state")
		String state();

		@ClassName("state-open")
		String stateOpen();

		@ClassName("state-close")
		String stateClose();
	}

	private static IssuesLayoutPanelUiBinder uiBinder = GWT
			.create(IssuesLayoutPanelUiBinder.class);

	interface IssuesLayoutPanelUiBinder extends
			UiBinder<Widget, IssuesLayoutPanel> {
	}

	private static final String OPEN = "Open";
	private static final String CLOSED = "Closed";

	@UiField
	MyStyle style;
	@UiField
	InlineLabel titleLabel;
	@UiField
	InlineLabel stateLabel;
	@UiField
	InlineLabel numberIssueLabel;
	@UiField
	InlineLabel userCreateLabel;
	@UiField
	InlineLabel whenCreateLabel;
	@UiField
	HorizontalPanel hPanel;

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

		if (issue.getState().equals(IssueValue.Prop.OPEN.value)) {
			stateLabel.setText(IssuesLayoutPanel.OPEN);
			stateLabel.addStyleName(style.stateOpen());
			stateLabel.addStyleName(AON.AON_ICON_ISSUE_OPENED);
		} else {
			stateLabel.setText(IssuesLayoutPanel.CLOSED);
			stateLabel.addStyleName(style.stateClose());
			stateLabel.addStyleName(AON.AON_ICON_ISSUE_CLOSED);
		}

		userCreateLabel.setText(issue.getUser().getLogin());

		int days = DateUtils.getDaysBetween(issue.getCreateAt(), (new Date()));

		whenCreateLabel.setText("Abierto hace " + days + " d\u00EDas - "
				+ issue.getComments() + " comentarios");

		if (issue.getLabels().length() > 0)
			addLabels();
	}

	private void addIssue() {
		IssueReadWidget issueRead = new IssueReadWidget();
		issueRead.addIssue(issue);
		flowIssuesPanel.add(issueRead);
	}

	private void addCommentIssues() {

		for (int x = 0; x < issue.getIssueComments().length(); x++) {
			IssueReadWidget issueRead = new IssueReadWidget();
			issueRead.addComment(issue.getIssueComments().get(x), x);
			flowIssuesPanel.add(issueRead);
		}
	}

	private void addWriteIssue() {

		IssueWriteWidget issueWrite = new IssueWriteWidget();
		flowIssuesPanel.add(issueWrite);
	}

	private void addLabels() {

		InlineLabel whoCreate = new InlineLabel(issue.getUser().getLogin()
				+ " added ");
		whoCreate.setStyleName(AON.AON_ICON_EMPLOYEE + " "
				+ AON.AON_ICON_CMD_BUTTON);

		hPanel.add(whoCreate);
		addLabels(hPanel, issue.getLabels());

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
