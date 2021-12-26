package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarNonWorkingDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, EmployeeCalendarNonWorkingDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields

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
	
	// ------------------------------------ Variables
	
	private Byte[] nonWorkingDays = new Byte[7];
	
	// ------------------------------------ Constructor
	
	protected EmployeeCalendarNonWorkingDialog(Byte[] nonWorkingDays) {
		setCaption("DIAS NO LABORABLES");
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		this.nonWorkingDays = nonWorkingDays;
		
		initCheckBox();
		
		cancelButton.addClickHandler(e -> hide());
		acceptButton.addClickHandler(e -> {
			onAccept();
			hide();
		});
		
		showDialog();
	}
	
	// ------------------------------------ Abstract methods

	protected abstract void onAccept();

	// ------------------------------------ Auxiliar methods

	private void initCheckBox() {
		mondayCB.setValue(null != nonWorkingDays[1] && nonWorkingDays[1] == 1);
		tuesdayCB.setValue(null != nonWorkingDays[2] && nonWorkingDays[2] == 1);
		wednesdayCB.setValue(null != nonWorkingDays[3] && nonWorkingDays[3] == 1);
		thursdayCB.setValue(null != nonWorkingDays[4] && nonWorkingDays[4] == 1);
		fridayCB.setValue(null != nonWorkingDays[5] && nonWorkingDays[5] == 1);
		saturdayCB.setValue(null != nonWorkingDays[6] && nonWorkingDays[6] == 1);
		sundayCB.setValue(null != nonWorkingDays[0] && nonWorkingDays[0] == 1);
	}
	
	protected Byte[] getNonWorkingDays() {
		for(int day=0; day<7; day++) {
			switch (day) {
				case 1:
					nonWorkingDays[day] = parseChechBox(mondayCB.getValue());
					break;
				case 2:
					nonWorkingDays[day] = parseChechBox(tuesdayCB.getValue());
					break;
				case 3:
					nonWorkingDays[day] = parseChechBox(wednesdayCB.getValue());
					break;
				case 4:
					nonWorkingDays[day] = parseChechBox(thursdayCB.getValue());
					break;
				case 5:
					nonWorkingDays[day] = parseChechBox(fridayCB.getValue());
					break;
				case 6:
					nonWorkingDays[day] = parseChechBox(saturdayCB.getValue());
					break;
				case 0:
					nonWorkingDays[day] = parseChechBox(sundayCB.getValue());
					break;
				default:
					break;
			}
		}
		return this.nonWorkingDays;
	}
	
	private byte parseChechBox(boolean value) {
		return Boolean.TRUE.equals(value) ? (byte)1 : (byte)0;
	}
	
	// ------------------------------------ Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
}
