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

import com.code.aon.cms.Section;
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

	public void onAccept(ActionEvent event) {
		try {
			Sidebar sidebar = (Sidebar)getTo();
			IManagerBean bean = BeanManager.getManagerBean(Sidebar.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				sidebar.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		super.onAccept(event);
	}

	public void defaultChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		Sidebar sidebar = (Sidebar) model.getRowData();
		if (selected) {
			sidebar.setDefault_(true);
			updateDefault(sidebar);
		}
		cancelOnSelect = true;
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault(Sidebar defaultSidebar) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Sidebar.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Sidebar sidebar = (Sidebar)list.get(i);
			if (defaultSidebar != sidebar)
				sidebar.setDefault_(false);
			bean.update(sidebar);
		}
	}

}
