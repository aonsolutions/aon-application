package com.code.aon.ui.purchase.controller;

import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the purchase maintenance.
 */
public class PurchaseController extends GridController {
	
	/**
	 * The empty constructor.
	 */
	public PurchaseController(){
		super();
	}
	
	/**
	 * On reset. Method launched by the menu.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		super.onReset(null);
	}
}