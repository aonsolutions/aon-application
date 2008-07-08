package com.code.aon.ui.cms.event;

import com.code.aon.cms.Company;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Company.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.COMPANY_NAME));
			event.getController().setCriteria(criteria);
		}catch (Exception e) {
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((CompanyController)event.getController()).onLoadActivities(null);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((CompanyController)event.getController()).onLoadActivities(null);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		((CompanyController)event.getController()).onLoadActivities(null);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((CompanyController)event.getController()).onLoadActivities(null);
	}
}
