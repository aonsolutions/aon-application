package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the tax maintenance.
 */
public class TaxController extends GridController {

	/**
	 * The empty constructor.
	 */
	public TaxController() {
		super();
	}
	
	/**
	 * On reset. Method launched by the menu
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) {
		super.onReset(null);
	}

    /**
     * On accept. Sets the current tax to null after adding it.
     * 
     * @param event the event
     */
    @Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.resetTo();
	}

	/**
	 * On remove. Sets the current productCategory to null after removing it.
	 * 
	 * @param event the event
	 */
	@Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}
}