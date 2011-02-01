package com.code.aon.ui.cms.event;

import com.code.aon.cms.Brand;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BrandControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(ICMSAlias.BRAND_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		checkAlias(event);
	}

	private void checkAlias(ControllerEvent event) throws ControllerListenerException{
		try {
			Brand to = (Brand)event.getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(Brand.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BRAND_ALIAS),to.getAlias());
			if (!bean.getList(criteria).isEmpty()){
				throw new ControllerListenerException("ALIAS DUPLICATED");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}		
	}
	
}
