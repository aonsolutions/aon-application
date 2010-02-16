package com.code.aon.ui.cms.event;

import com.code.aon.cms.GenericPage;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GenericPageController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class GenericPageControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		GenericPageController controller = (GenericPageController)event.getController();
		try {
			controller.completeCriteria();
			Criteria criteria = controller.getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(GenericPage.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ACTIVE),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ALIAS));
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
		GenericPage page = (GenericPage)event.getController().getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}
}