package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendPercentDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendPercentDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	Label typePercent;
	
	@UiField
	DoubleBox percentBox;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;

	public EmployeeCalendPercentDialog(String caption) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));		
		
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
	
	public double getPercentValue() {
		return percentBox.getValue();
	}
	
	public void setLabelText(String label){
		typePercent.setText(label);
	}

}
