package com.code.aon.ui.product.event;

import com.code.aon.product.ProductCategory;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener adderd to the ProductCategoryController when a category group is no required.
 */
public class PCategoryControllerListener extends ControllerAdapter {

	/**
	 * Before bean added.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProductCategory category = (ProductCategory)event.getController().getTo();
		category.setGroup(null);
	}
	
	/**
	 * Before bean updated.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProductCategory category = (ProductCategory)event.getController().getTo();
		category.setGroup(null);
	}
}