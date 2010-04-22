package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Product;
import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class ProductController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "product_form");
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
		Product p = (Product)this.model.getRowData();
		p.setActive(active);
		getManagerBean().update(p);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String title = "";
		Product p = (Product)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_DETAIL_PRODUCT_ID), p.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			ProductDetail pd = (ProductDetail)list.get(0);
			title = pd.getLabel();
		}
		return title;
	}
	
	public String getI18nShortLabel() throws ManagerBeanException {
		String title = "";
		Product p = (Product)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_DETAIL_PRODUCT_ID), p.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			ProductDetail pd = (ProductDetail)list.get(0);
			title = pd.getShortLabel();
		}
		return title;
	}
}