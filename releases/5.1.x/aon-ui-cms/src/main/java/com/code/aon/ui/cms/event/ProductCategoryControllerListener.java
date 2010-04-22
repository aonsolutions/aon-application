package com.code.aon.ui.cms.event;

import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProductCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			event.getController().getCriteria().addNullExpression(event.getController().getManagerBean().getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		assignSection(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignSection(event);
	}

	private void assignSection(ControllerEvent event){
		LinkCategory to = (LinkCategory)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}
}
