package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarNonWorkingDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarNonWorkingDialog> {}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	CheckBox mondayCB;
	
	@UiField
	CheckBox tuesdayCB;
	
	@UiField
	CheckBox wednesdayCB;
	
	@UiField
	CheckBox thursdayCB;
	
	@UiField
	CheckBox fridayCB;
	
	@UiField
	CheckBox saturdayCB;
	
	@UiField
	CheckBox sundayCB;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private Byte nonWorkingDays[] = new Byte[7];
	
	public EmployeeCalendarNonWorkingDialog(Byte[] nonWorkingDays) {
		setCaption("DIAS NO LABORABLES");
		
		setWidget(binder.createAndBindUi(this));
		
		this.nonWorkingDays = nonWorkingDays;
		
		initCheckBox();
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
				hide();
			}
		});	
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------- ABSTRACT METHODS --------------------------------
	// -------------------------------------------------------------------------------

	protected abstract void onAccept();

	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------

	private void initCheckBox() {
		mondayCB.setValue(nonWorkingDays[1] == 1);
		tuesdayCB.setValue(nonWorkingDays[2] == 1);
		wednesdayCB.setValue(nonWorkingDays[3] == 1);
		thursdayCB.setValue(nonWorkingDays[4] == 1);
		fridayCB.setValue(nonWorkingDays[5] == 1);
		saturdayCB.setValue(nonWorkingDays[6] == 1);
		sundayCB.setValue(nonWorkingDays[0] == 1);
	}
	
	protected Byte[] getNonWorkingDays() {
		for(int day=0; day<7; day++) {
			switch (day) {
				case 1:
					nonWorkingDays[day] = mondayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 2:
					nonWorkingDays[day] = tuesdayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 3:
					nonWorkingDays[day] = wednesdayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 4:
					nonWorkingDays[day] = thursdayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 5:
					nonWorkingDays[day] = fridayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 6:
					nonWorkingDays[day] = saturdayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				case 0:
					nonWorkingDays[day] = sundayCB.getValue() == true ? (byte)1 : (byte)0;
					break;
				default:
					break;
			}
		}
		return this.nonWorkingDays;
	}
}
