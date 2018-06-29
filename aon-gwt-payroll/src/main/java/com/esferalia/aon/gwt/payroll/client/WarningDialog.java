package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WarningDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, WarningDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	Button cancelButton;
	
	@UiField
	Label label;

	public WarningDialog() {
		setCaption("Aviso");
		
		setWidget(binder.createAndBindUi(this));		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
	}
	
	public WarningDialog(String caption, String message) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		label.setText(message);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});	
	}

}
