package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ui.form.GridController;

/**
 * Pay Method's controller
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class PayMethodController extends GridController {

	/**
	 * Default constructor 
	 */
	public PayMethodController() {
		super();
	}
	
	/**
	 * Accepts the controller and resets the transfer object
	 * 
	 * @see com.code.aon.ui.form.GridController#onAccept(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.resetTo();
	}
	
	/**
	 * Removes the controller and resets the transfer object
	 * 
	 * @see com.code.aon.ui.form.GridController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}
	
}
