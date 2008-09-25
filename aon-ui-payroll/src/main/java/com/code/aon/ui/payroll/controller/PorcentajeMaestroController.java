package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;

public class PorcentajeMaestroController extends BasicController {

	public void onEnter(ActionEvent event) {
		if ( super.model != null ) {
			super.onEditSearch( event );
			super.onSearch( event );
		}
		super.onSelectFirst( event );
	}

	public void onExit(ActionEvent event) {
		// TODO Auto-generated method stub
	}

}
