package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class IssueReadPanel extends Composite {

	interface Listener {

		void onUpdateIssueState(String state);

		void onIssueComment(String body);
	}

	interface GridStyle extends CssResource {
		@ClassName("options-text-area")
		String optionsTextArea();

		@ClassName("row-background")
		String rowBackground();
	}

	private static IssueReadPanelUiBinder uiBinder = GWT
			.create(IssueReadPanelUiBinder.class);

	interface IssueReadPanelUiBinder extends UiBinder<Widget, IssueReadPanel> {
	}

	@UiField
	GridStyle style;
	@UiField
	Label companyLabel;
	@UiField
	Label userLogged;

	@UiField
	Label titleLabel;
	@UiField
	HorizontalPanel labelsHPanel;
	@UiField
	Label priorityLabel;
	@UiField
	FlowPanel historialVPanel;
	
	@UiField
	Button closedButton;
	@UiField
	Button commentButton;
	
	@UiField
	TextArea commentTextArea;

	private List<Listener> listeners;
	private IssueSelected issue;

	private DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");	
	
	public IssueReadPanel(IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));

		this.listeners = new LinkedList<Listener>();
		this.issue = issue;
		
		setCompany(issue.getCompany());
		setPriority(issue.getPriority());
		setAsunto(issue.getTitle());
		setLabels(issue.getTags());
		printComment(issue.getState(), issue.getCreateAt(), issue.getBody());
		
		for (DefaultAonIssueComments comment : issue.getComments()) {
			printComment("COMENTADO", comment.getCreatedAt(), comment.getBody());
		}

		userLogged.setText(issue.getUser().getName());

		if (issue instanceof IssueGrid.IssueOpenLoadSelected) {
			createClosedButton();
			createCommentButton();			
		} else if (issue instanceof IssueGrid.IssueClosedLoadSelected) {
			commentButton.setVisible(false);
			commentTextArea.setVisible(false);
			createReopenButton();
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void setCompany(String company) {
		companyLabel.setText(company.toUpperCase());
	}

	private void setAsunto(String asunto) {
		this.titleLabel.setText(asunto.toUpperCase());
	}

	private void setPriority(String priority) {
		this.priorityLabel.setText(priority);
	}
	
	private void setLabels(List<DefaultAonTagIssueSelected> tags) {
		
		if ( tags.size() == 0 )
			labelsHPanel.add(new Label("Etiquetas no asignadas"));
		
		for ( DefaultAonTagIssueSelected tag : tags)
			labelsHPanel.add(new Label(tag.getName()));
	}
	
	private void printComment(String type, Date createAt, String body) {		
		
		int days = CalendarUtil.getDaysBetween(createAt, new Date());
		Grid grid = new Grid(2,1);
		Label label = new Label();
		label.setText(type + " por " + issue.getUser().getName()
				+ " el " + fmt.format(createAt) + " (hace " + days + ((days == 1) ? " d\u00EDas)" : " d\u00EDas)"));
		label.setStyleName(AON.AON_BOLD);
		label.getElement().getStyle().setFontStyle(FontStyle.ITALIC);

		TextArea textArea = getTextArea(new String(body.replaceAll("--", "\n")));
		grid.setWidget(0, 0, label);
		grid.getRowFormatter().addStyleName(0, style.rowBackground());
		grid.setWidget(1, 0, textArea);

		historialVPanel.add(grid);

		
	}

	private void createCommentButton() {
		commentButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onCommentButtonClick();
			}
		});
	}

	private void createClosedButton() {
		closedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onClosedButtonClick();
			}
		});
	}

	private void createReopenButton() {
		closedButton.setText("Abrir");
		closedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onReopenButtonClick();
			}
		});
	}

	private void onCommentButtonClick() {
		for (Listener listener : listeners)
			listener.onIssueComment(commentTextArea.getValue());
	}

	private void onClosedButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.CLOSED.getValue());
	}

	private void onReopenButtonClick() {
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.REOPEN.getValue());
	}
	
	private TextArea getTextArea(String text) {
		TextArea textArea = new TextArea();
		textArea.setReadOnly(true);
		textArea.setVisibleLines(5);
		textArea.setCharacterWidth(10);
		textArea.setWidth("600px");
		textArea.setStylePrimaryName(style.optionsTextArea());
		textArea.setValue(text);
		return textArea;
	}
}
