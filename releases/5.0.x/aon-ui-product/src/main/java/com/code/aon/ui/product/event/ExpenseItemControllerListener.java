package com.code.aon.ui.product.event;

import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ExpenseItemControllerListener extends ItemControllerListener {

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	super.afterBeanCreated(event);

    	Item item = (Item)event.getController().getTo();
        item.getProduct().setType(ProductType.EXPENSE);
    }

}