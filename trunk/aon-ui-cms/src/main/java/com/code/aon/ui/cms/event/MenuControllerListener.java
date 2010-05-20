package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Footer;
import com.code.aon.cms.Header;
import com.code.aon.cms.Menu;
import com.code.aon.cms.Section;
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
			MenuController controller = (MenuController)event.getController();
			Criteria criteria = controller.getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Menu.class);
			criteria.addExpression(bean.getFieldName(ICMSAlias.MENU_TYPE), "" + controller.getCurrentType());
			criteria.addOrder(bean.getFieldName(ICMSAlias.MENU_DEFAULT_MENU),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.MENU_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
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
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		MenuController controller = (MenuController)event.getController();
		Menu menu  = (Menu)controller.getTo();
		try {
			boolean dependences = false;
			String dependences_msg = "DEPENDENCES TO REMOVE. ";
			
			IManagerBean bean = BeanManager.getManagerBean(Section.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SECTION_MENU_ID),menu.getId());
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < list.size(); i++) {
				dependences = true;
				Section section = (Section)list.get(i);
				int id = section.getId();
				String name = section.getAlias();
				dependences_msg += "Section "+id+"-"+name+"; ";
			}
			
			bean = BeanManager.getManagerBean(Header.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.HEADER_MENU_ID),menu.getId());
			list = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < list.size(); i++) {
				dependences = true;
				Header section = (Header)list.get(i);
				int id = section.getId();
				String name = section.getAlias();
				dependences_msg += "Header "+id+"-"+name+"; ";
			}
			
			bean = BeanManager.getManagerBean(Footer.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_MENU_ID),menu.getId());
			list = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < list.size(); i++) {
				dependences = true;
				Footer section = (Footer)list.get(i);
				int id = section.getId();
				String name = section.getAlias();
				dependences_msg += "Footer "+id+"-"+name+"; ";
			}
			if (dependences){
				throw new ControllerListenerException(dependences_msg);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
