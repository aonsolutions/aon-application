package com.code.aon.ui.marketplace.event;

import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the ItemController.
 */
public class ItemVetoListener extends ControllerAdapter {

	/**
	 * Sets a default productType to the current Item
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
			Item item = (Item)event.getController().getTo();
            item.getProduct().setType(ProductType.COMMERCIAL_PRODUCT);
		}
}