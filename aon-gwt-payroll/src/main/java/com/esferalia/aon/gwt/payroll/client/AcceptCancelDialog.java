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

public abstract class AcceptCancelDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, AcceptCancelDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Label label1;

	@UiField
	Label label2;
	
	@UiField
	Label label3;

	public AcceptCancelDialog() {
		setCaption("Aviso");
		
		setWidget(binder.createAndBindUi(this));		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
	}
	
	public AcceptCancelDialog(String caption, String message1, String message2, String message3) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		label1.setText(message1);
		label2.setText(message2);
		label3.setText(message3);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});	
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});	
	}
	
	protected abstract void onAccept();

}
