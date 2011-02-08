package com.code.aon.ui.webinfo.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.webinfo.controller.CompanyWebInfoPageController;
import com.code.aon.webinfo.WebInfoPage;
import com.code.aon.webinfo.dao.IWebInfoAlias;

public class CompanyWebInfoPageControllerListener extends ControllerAdapter {


	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			IManagerBean wipBean = BeanManager.getManagerBean(WebInfoPage.class);
			CompanyWebInfoPageController wipc = (CompanyWebInfoPageController)event.getController();
			Criteria criteria = new Criteria();
			criteria.addOrder(wipBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_POSITION));
			wipc.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		super.beforeModelInitialized(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyWebInfoPageController wipc = (CompanyWebInfoPageController)event.getController();
		WebInfoPage wip = (WebInfoPage)wipc.getTo();
		wip.setPosition(wipc.getLastPosition());
	}

}
