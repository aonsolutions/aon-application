package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class IssueReadPanel extends Composite {
	
	interface Listener {
		
		void onUpdateIssueState(String state);
	}

	private static IssueReadPanelUiBinder uiBinder = GWT
			.create(IssueReadPanelUiBinder.class);

	interface IssueReadPanelUiBinder extends UiBinder<Widget, IssueReadPanel> {
	}
	
	@UiField
	Label idLabel;
	@UiField
	Label dateLabel;
	@UiField
	Label statusLabel;
	@UiField
	Label titleLabel;
	@UiField
	Label typeLabel;
	@UiField
	Label userLabel;
	@UiField
	HorizontalPanel labelsHPanel;
	@UiField
	Label priorityLabel;
	@UiField
	TextArea commentTextArea;
	
	@UiField
	Button closeButton;
	
	private List<Listener> listeners;

	public IssueReadPanel(IssueSelected issue) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new LinkedList<Listener>();
		
		setNumber(issue.getId());
		setDate(issue.getCreateAt());
		setStatusLabel(issue.getState());
		setAsunto(issue.getTitle());
		setType(issue.getType());
		setPriority(issue.getPriority());
		setUser(issue.getUser().getLogin());
		setLabel(issue.getLabels());
		setBody(issue.getBody());
	}
	
	@UiHandler("closeButton")
	void onCloseButtonClick(ClickEvent event) {
		
		for (Listener listener : listeners)
			listener.onUpdateIssueState(NoticeStatus.CLOSED.getValue());
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void setNumber(Integer id) {
		this.idLabel.setText(String.valueOf(id));
	}
	
	private void setDate(Date date) {
		this.dateLabel.setText(String.valueOf(date));
	}
	
	private void setStatusLabel(String status) {
		this.statusLabel.setText(status);
	}
	
	private void setAsunto(String asunto) {
		this.titleLabel.setText(asunto);
	}
	
	private void setType(String type) {
		this.typeLabel.setText(type);
	}
	
	private void setPriority(String priority) {
		this.priorityLabel.setText(priority);
	}
	
	private void setUser(String login) {
		this.userLabel.setText(login);
	}
	
	private void setLabel(JsArray<JsLabel> labels) {
	
		if ( labels.length() == 0 )
			labelsHPanel.add(new Label("Etiquetas no asignadas"));
		
		for ( int x = 0 ; x < labels.length(); x++)
			labelsHPanel.add(new Label(labels.get(x).getName()));
	}
	
	private void setBody(String body) {
		this.commentTextArea.setValue(body);
	}
}
