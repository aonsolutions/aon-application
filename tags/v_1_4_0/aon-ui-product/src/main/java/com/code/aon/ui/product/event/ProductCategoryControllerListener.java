package com.code.aon.ui.product.event;

import com.code.aon.product.ProductCategory;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ProductCategoryController;

public class ProductCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
	((ProductCategory)((ProductCategoryController)this.getController()).getTo()).setId(((ProductCategoryController)this.getController()).getCategory().getId());
	((ProductCategory)((ProductCategoryController)this.getController()).getTo()).setName(((ProductCategoryController)this.getController()).getCategory().getName());

	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		
		((ProductCategory)((ProductCategoryController)this.getController()).getTo()).setId(((ProductCategoryController)this.getController()).getCategory().getId());
		((ProductCategory)((ProductCategoryController)this.getController()).getTo()).setName(((ProductCategoryController)this.getController()).getCategory().getName());

	}

}