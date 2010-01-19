package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class MenuController extends BasicController {

	private int currentType = MenuType.SIDEBAR.ordinal();

	public DataModel getModel() throws ManagerBeanException {
		if (model == null) {
			try {
				changeMenuList();
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		}
		return model;
	}

	private void changeMenuList() throws ManagerBeanException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_TYPE), "" + currentType);
		setCriteria(criteria);
		initializeModel();
	}

	@SuppressWarnings("unused")
	public void onTab0(ActionEvent event) {
		currentType = MenuType.SIDEBAR.ordinal();
		try {
			changeMenuList();
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}
	
	@SuppressWarnings("unused")
	public void onTab1(ActionEvent event) {
		currentType = MenuType.TOP.ordinal();
		try {
			changeMenuList();
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}
	
	@SuppressWarnings("unused")
	public void onTab2(ActionEvent event) {
		currentType = MenuType.FOOT.ordinal();
		try {
			changeMenuList();
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}
	
	@SuppressWarnings("unused")
	public void onTab3(ActionEvent event) {
		currentType = MenuType.INNER.ordinal();
		try {
			changeMenuList();
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}
	
	public void defaultMenuChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Menu menu = (Menu) model.getRowData();
		updateDefaultMenu(menu);
		menu.setDefaultMenu(true);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefaultMenu(Menu defaultMenu) throws ManagerBeanException, ExpressionException {
		// Quitamos el defaultMenu de todos los menus de la base de datos
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu)list.get(i);
			menu.setDefaultMenu(false);
		}
		IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), "" + currentType);
		list = (List<ITransferObject>)menuBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu)list.get(i);
			menu.setDefaultMenu(false);
			menuBean.update(menu);
		}

		// Ponemos default al seleccionado
		criteria = new Criteria();
		criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_ID), defaultMenu.getId());
		list = menuBean.getList(criteria);
		if (list.size() > 0) {
			Menu menu = (Menu) list.get(0);
			menu.setDefaultMenu(true);
			menuBean.update(menu);
		}
	}

	public MenuType getCurrentType() {
		if (currentType == MenuType.SIDEBAR.ordinal()) return MenuType.SIDEBAR;
		else if (currentType == MenuType.TOP.ordinal()) return MenuType.TOP;
		else if (currentType == MenuType.FOOT.ordinal()) return MenuType.FOOT;
		else if (currentType == MenuType.INNER.ordinal()) return MenuType.INNER;
		return MenuType.SIDEBAR;
	}
	
	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		MenuOptionController moc = (MenuOptionController)FormUtil.getController("menu_option");
		IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
		Menu menu = (Menu) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), "" + menu.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.MENU_OPTION_POSITION));
		moc.setCurrentMenu(menu);
		moc.setCriteria(criteria);
		moc.onSearch(event);
	}

}
