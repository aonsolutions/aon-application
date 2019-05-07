package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeAFIDialog extends CustomDialog {
	
	interface Binder extends UiBinder<Widget, EmployeeAFIDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	CheckBox startContractCkBox;
	
	@UiField
	CheckBox endContractCkBox;
	
	@UiField
	CheckBox changeContractCkBox;
	
	@UiField
	CheckBox quoteContractCkBox;
	
	@UiField
	CheckBox ocupationContractCkBox;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;

	public EmployeeAFIDialog() {
		setCaption("Generador fichero AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
	}

	protected abstract void onAccept();
	
	public boolean isStartContract() {
		return startContractCkBox.getValue();
	}
	
	public boolean isEndContract() {
		return endContractCkBox.getValue();
	}
	
	public boolean isChangeContract() {
		return changeContractCkBox.getValue();
	}
	
	public boolean isQuoteContract() {
		return quoteContractCkBox.getValue();
	}
	
	public boolean isOcupationContract() {
		return ocupationContractCkBox.getValue();
	}

}
