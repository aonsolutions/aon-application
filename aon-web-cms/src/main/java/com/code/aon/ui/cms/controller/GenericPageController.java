package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.cms.Constants;

public class GenericPageController extends BasicI18nController implements Constants {

	private String title;

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	private boolean richTextEnabled = false;

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}
	
	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		GenericPage gp = (GenericPage)this.model.getRowData();
		gp.setActive(active);
		getManagerBean().update(gp);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = NO_VALUE_LABEL;
		GenericPageDetail gpd = (GenericPageDetail)getModelRowdataI18n();
		if (gpd != null) title = gpd.getTitle();
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

	@Override
	public void onEditSearch(ActionEvent event) {
		setTitle(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(GenericPageDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.GENERIC_PAGE_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((GenericPageDetail) iterator.next()).getGeneric_page().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((GenericPageDetail) iterator.next()).getGeneric_page().getId().toString(),alias));
			}
			if (expr != null)
				getCriteria().addExpression(expr);
			else
				getCriteria().addExpression(alias, "-1");
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}

	public void completeCriteria() throws ManagerBeanException {
		if (getTitle() != null && getTitle().length()>0) {
			completeDetailCriteria(ICMSAlias.GENERIC_PAGE_DETAIL_TITLE, getTitle());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}