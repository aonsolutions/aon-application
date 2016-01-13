package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester.SetRootRule;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
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
