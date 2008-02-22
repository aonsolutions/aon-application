package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.SidebarController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SidebarControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Sidebar.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SIDEBAR_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Sidebar sidebar = (Sidebar)((SidebarController)event.getController()).getTo();
			IManagerBean bean = BeanManager.getManagerBean(Sidebar.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				sidebar.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			((SidebarController)event.getController()).onSelectOptions(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			((SidebarController)event.getController()).onSelectOptions(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
}
