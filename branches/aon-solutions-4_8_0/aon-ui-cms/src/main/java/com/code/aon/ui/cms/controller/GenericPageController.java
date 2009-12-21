package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Menu;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class GenericPageController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private boolean richTextEnabled = true;

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "generic_page_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}
	
	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		GenericPage gp = (GenericPage)this.model.getRowData();
		gp.setActive(active);
		getManagerBean().update(gp);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nTitle() throws ManagerBeanException {
		String title = "";
		GenericPage gp = (GenericPage)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), gp.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			GenericPageDetail gpd = (GenericPageDetail)list.get(0);
			title = gpd.getTitle();
		}
		return title;
	}
	
	public List<SelectItem> getMenus() throws ManagerBeanException, ExpressionException {
		List<SelectItem> menus = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem(0, MenuType.getDefaultName(locale) );
		menus.add(item);
		IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), "" + MenuType.SIDEBAR.ordinal());
		List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu)list.get(i);
			int id = menu.getId();
			String name = menu.getAlias();
			item = new SelectItem(id, name);
			menus.add(item);
		}
		return menus;
	}


}