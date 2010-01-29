package com.code.aon.ui.cms.event;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.ModularPageController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ModularPageControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ModularPageController controller = (ModularPageController)event.getController(); 
		try {
			controller.completeCriteria();
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.MODULAR_PAGE_HOMEPAGE),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.MODULAR_PAGE_ACTIVE),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.MODULAR_PAGE_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}

	private void assignSection(ControllerEvent event){
		ModularPage page = (ModularPage)event.getController().getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			((ModularPageController)event.getController()).onSelectOptions(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ModularPageController controller = (ModularPageController)event.getController();
		controller.loadCurrentLanguage();
		try {
			controller.onSelectOptions(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
}