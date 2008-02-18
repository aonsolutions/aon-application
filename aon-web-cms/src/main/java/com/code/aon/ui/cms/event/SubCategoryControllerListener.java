package com.code.aon.ui.cms.event;

import com.code.aon.cms.ProductCategory;
import com.code.aon.ui.cms.controller.SubCategoryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SubCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		SubCategoryController controller = (SubCategoryController)event.getController();
		ProductCategory to = (ProductCategory)controller.getTo();
		to.setParent(controller.getParent());
		to.setActive(true);
	}
	
}
