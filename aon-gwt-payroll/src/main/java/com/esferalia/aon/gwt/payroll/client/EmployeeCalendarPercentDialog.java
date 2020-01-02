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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarPercentDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarPercentDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	ListBox typeDrop;
	
	@UiField
	Label typePercent;
	
	@UiField
	TextBox percentBox;
	
	@UiField
	Label messageLabel;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	Double journeyHours;

	public EmployeeCalendarPercentDialog(String caption, String hoursContract) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		typeDrop.clear();
		typeDrop.addItem("Huelga");
		typeDrop.addItem("ERE");
		typeDrop.addItem("Ausencia Injustificada");
		
		messageLabel.setText("Horas Jornada = " + hoursContract);
		
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
		
		this.journeyHours = Double.parseDouble(hoursContract);
		
	}

	protected abstract void onAccept();
	
	public double getPercentValue() {
		Double hoursStrike = Double.parseDouble(percentBox.getValue());
		if(hoursStrike >= this.journeyHours)
			return 1.00;
		
		return hoursStrike/this.journeyHours;
	}
	
	public void setLabelText(String label){
		typePercent.setText(label);
	}
	
	public Integer getTypeDrop(){
		return typeDrop.getSelectedIndex();
	}

}
