package com.code.aon.ui.cms.event;

import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.ProductCategoryController;
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
		checkAlias(event);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((ProductCategoryController) event.getController()).onSelectSubCategories(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((ProductCategoryController) event.getController()).onSelectSubCategories(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	private void checkAlias(ControllerEvent event) throws ControllerListenerException{
		try {
			ProductCategory to = (ProductCategory)event.getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ALIAS),to.getAlias());
			if (!bean.getList(criteria).isEmpty()){
				throw new ControllerListenerException("ALIAS DUPLICATED");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}		
	}
	
}