package com.code.aon.ui.tas.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.GridController;

/**
 * Tas make controller
 * 
 * @author Consulting & Development. Iñigo GAyarre - 15/12/2006
 * @since 1.0
 *
 */
public class TasMakeController extends GridController {
	
	/**
	 * Contructor
	 */
	public TasMakeController() {
		super();
	}

    /**
     * Redefines the onRemove command
     * removes the tranfer object and resets the controller
     * 
     * @see com.code.aon.ui.form.GridController#onRemove(javax.faces.event.ActionEvent)
     */
    @Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}

}
