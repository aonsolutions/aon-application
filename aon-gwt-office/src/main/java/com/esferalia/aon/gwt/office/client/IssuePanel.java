package com.esferalia.aon.gwt.office.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IssuePanel extends CustomDialog {
	
	interface Listener {
		
		void onPriorityChangeValue(String name);
	}

	interface IssuePanelUiBinder extends UiBinder<Widget, IssuePanel> {
	}

	private static IssuePanelUiBinder uiBinder = GWT
			.create(IssuePanelUiBinder.class);

	@UiField
	HorizontalPanel priorityHPanel;
	@UiField
	HorizontalPanel typeHPanel;
	@UiField
	TextBox titleTextBox;
	@UiField
	TextBox senderTextBox;
	@UiField
	TextArea commentTextArea;
	
	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	
	private List<Listener> listeners;

	public IssuePanel(String title) {
		setCaption(title);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		setAnimationEnabled(true);
		setGlassEnabled(true);	
		
		this.listeners = new LinkedList<Listener>();
		
		for ( int x = 0; x < Priority.values().length; x++) {
			
			RadioButton rb = new RadioButton("Priority", Priority.values()[x].getValue());			
			rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					
				}
			});
			priorityHPanel.add(rb);
		}
		
		for ( int x = 0 ; x < NoticeType.values().length; x++) {
			RadioButton rb = new RadioButton("Type", NoticeType.values()[x].getValue());
			rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					
				}
			});
			typeHPanel.add(rb);
		}
	}
	
	public void addListener(Listener listener) {		
		listeners.add(listener);
	}
	
	public void removeListener (Listener listener) {
		listeners.remove(listener);
	}

	public void showPopupPanel() {
		center();
	}
	
	// ----------------------------------------------------
	
	public void setTitle(String title) {
		titleTextBox.setValue(title);
	}
	
	public void setSender (String login) {
		senderTextBox.setValue(login);
	}
	
	public void setBody(String body) {
		commentTextArea.setValue(body);
	}
	
	public void setPriority(String priority) {
		
		Iterator<Widget> iter = priorityHPanel.iterator();
		
		while ( iter.hasNext() ) {
			RadioButton rb = (RadioButton) iter.next();
			if ( rb.getText().compareTo(priority) == 0)
				rb.setValue(true);
		}
	}
	
	public void setType(String type) {
		Iterator<Widget> iter = typeHPanel.iterator();
		
		while ( iter.hasNext() ) {
			RadioButton rb = (RadioButton) iter.next();
			if ( rb.getText().compareTo(type) == 0)
				rb.setValue(true);
		}

	}
	
	// ----------------------------------------------------
	// ------------------------------------------- Handlers

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
}
