package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.LinesController;

public class PayrollLinesController extends LinesController {

	@Override
	public void onAccept(ActionEvent event) {
		boolean bol = isNew();
		super.onAccept(event);
		if ( bol )
			super.onReset( event );
	}

}
