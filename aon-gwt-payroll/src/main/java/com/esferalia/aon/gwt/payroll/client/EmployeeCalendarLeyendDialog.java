package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeCalendarLeyendDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarLeyendDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------

	public EmployeeCalendarLeyendDialog() {
		setCaption("Leyenda");
		
		setWidget(binder.createAndBindUi(this));
	}

}
