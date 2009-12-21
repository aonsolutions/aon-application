package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

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
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;

public class ImageController extends GridController {

	private boolean cancelOnSelect = false;
	
	private int currentType = MenuType.SIDEBAR.ordinal();

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "menu_form");
		}
		cancelOnSelect = false;
	}

	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
	}

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
		clearCheckList();
	}

	public void processTabChange(TabChangeEvent event) throws AbortProcessingException, ManagerBeanException, ExpressionException {
		switch (event.getNewTabIndex()) {
		case 0:
			currentType = MenuType.SIDEBAR.ordinal();
			break;
		case 1:
			currentType = MenuType.TOP.ordinal();
			break;
		case 2:
			currentType = MenuType.FOOT.ordinal();
			break;
		default:
			break;
		}
		changeMenuList();
	}

	public void defaultMenuChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		Menu menu = (Menu) model.getRowData();
		if (selected) {
			updateDefaultMenu(menu);
			menu.setDefaultMenu(true);
			//changeMenuList();
		}
		cancelOnSelect = true;
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

	public int getCurrentTab() {
		if (currentType == MenuType.TOP.ordinal()) return 1;
		else if (currentType == MenuType.FOOT.ordinal()) return 2;
		return 0;
	}

	public void onAccept(ActionEvent event) {
		try {
			Menu menu  = (Menu)getTo();
			IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), "" + menu.getType().ordinal());
			criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_DEFAULT_MENU), true);
			List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(criteria);
			if (list.size() == 0) {
				menu.setDefaultMenu(true);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
		super.accept(event);
		super.onReset(event);
		try {
			changeMenuList();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		MenuOptionController moc = (MenuOptionController)AonUtil.getController("menu_option");
		IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
		Menu menu = (Menu) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), "" + menu.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.MENU_OPTION_POSITION));
		moc.setCurrentMenu(menu);
		moc.setCriteria(criteria);
		moc.onSearch(event);
	}

}
