package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContrataContractOptionsDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, ContrataContractOptionsDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	Button dischargeButton;
	
	@UiField
	Button movementButton;
	
	public ContrataContractOptionsDialog(String caption) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		dischargeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDischarge();
				hide();
			}
		});
		
		movementButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onMovements();
				hide();
			}
		});
		
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------ ABSTRACT METHODS -------------------------------
	// -------------------------------------------------------------------------------

	protected abstract void onDischarge();
	protected abstract void onMovements();
	
}
