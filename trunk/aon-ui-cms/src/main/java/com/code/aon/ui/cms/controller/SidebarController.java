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

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
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

public class SidebarController extends GridController {

	private boolean cancelOnSelect = false;
	
	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "sidebar_form");
		}
		cancelOnSelect = false;
	}

	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
	}

	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		SidebarOptionController soc = (SidebarOptionController)AonUtil.getController("sidebar_option");
		IManagerBean soBean = BeanManager.getManagerBean(SidebarOption.class);
		Sidebar sidebar = (Sidebar) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(soBean.getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID), "" + sidebar.getId());
		criteria.addOrder(soBean.getFieldName(ICMSAlias.SIDEBAR_OPTION_POSITION));
		soc.setCurrentSidebar(sidebar);
		soc.setCriteria(criteria);
		soc.onSearch(event);
	}

}
