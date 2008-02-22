package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Menu;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.MenuController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class MenuControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Menu.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.MENU_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			MenuController controller = (MenuController)event.getController();
			Menu menu  = (Menu)controller.getTo();
			menu.setType(controller.getCurrentType());
			IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), "" + menu.getType().ordinal());
			criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_DEFAULT_MENU), true);
			List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(criteria);
			if (list.size() == 0) {
				menu.setDefaultMenu(true);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			((MenuController)event.getController()).onSelectOptions(null);
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
			((MenuController)event.getController()).onSelectOptions(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
}
