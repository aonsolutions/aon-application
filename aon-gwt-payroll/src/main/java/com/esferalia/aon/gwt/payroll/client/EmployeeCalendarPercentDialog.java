package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarPercentDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarPercentDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	ListBox typeDrop;
	
	@UiField
	TextBox percentBox;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------

	public EmployeeCalendarPercentDialog(String caption) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		typeDrop.clear();
		typeDrop.addItem("Huelga");
		typeDrop.addItem("ERE");
		typeDrop.addItem("Ausencia Injustificada");
		typeDrop.addItem("ERE Fuerza mayor");
		typeDrop.addItem("ERE Fuerza mayor (Exoneraci" + String.valueOf("\u00F3") + "n de cuotas)");
		
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
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------
	
	public Integer getTypeDrop(){
		return typeDrop.getSelectedIndex();
	}
	
	public double getPercentValue() {
		if(StringUtils.isEmpty(percentBox.getValue())) {
			return 1.00;
		}
		
		Double percent;
		
		try {
			percent = Double.parseDouble(percentBox.getValue());
			
			if(percent == 0.00)
				percent = 1.00;
			else {
				percent = percent / 100;
			}
		
		} catch (NumberFormatException e) {
			percent = 1.00;
		}
		
		return percent;
	}	

}
