package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.GridController;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Controller for warehouse
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class WarehouseController extends GridController {

	/**
	 * Default constructor
	 */
	public WarehouseController() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.GridController#onAccept(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.resetTo();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.GridController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}
}
