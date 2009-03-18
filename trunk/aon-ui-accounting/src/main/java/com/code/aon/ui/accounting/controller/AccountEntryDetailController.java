package com.code.aon.ui.accounting.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.LinesController;

public class AccountEntryDetailController extends LinesController {
	
	public void onBalanceAmount(ActionEvent event) {
		
	}
	
	public void onBalance(ActionEvent event) {
		
	}
	
	public void onAccept(ActionEvent event) {
		boolean adding = isNew();
		super.onAccept(event);
		if (adding) {
			super.onReset(event);	
		}
	}
		
}
