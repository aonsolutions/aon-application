package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ModularPageController extends BasicI18nController {

	private boolean cancelOnSelect = false;
	
	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "modular_page_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
	}

	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		ModularPageOptionController mpc = (ModularPageOptionController)AonUtil.getController("modular_page_option");
		IManagerBean mpBean = BeanManager.getManagerBean(ModularPageOption.class);
		ModularPage modularPage = (ModularPage) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID), "" + modularPage.getId());
		criteria.addOrder(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
		mpc.setCurrentModularPage(modularPage);
		mpc.setCriteria(criteria);
		mpc.onSearch(event);
	}

	public void defaultHomepageChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		ModularPage modularPage = (ModularPage) model.getRowData();
		if (selected) {
			disableHomepage();
			modularPage.setHomepage(true);
			modularPage.setActive(true);
			IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
			bean.update(modularPage);
		}
		cancelOnSelect = true;
	}
	
	@SuppressWarnings("unchecked")
	private void disableHomepage() throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			ModularPage modularPage = (ModularPage)list.get(i);
			modularPage.setHomepage(false);
			bean.update(modularPage);
		}
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
		ModularPage mp = (ModularPage)this.model.getRowData();
		mp.setActive(active);
		getManagerBean().update(mp);
	}
}
