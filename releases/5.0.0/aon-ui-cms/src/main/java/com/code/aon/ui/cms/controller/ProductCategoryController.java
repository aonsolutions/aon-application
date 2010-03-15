package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class ProductCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "product_category_form");
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
		ProductCategory pc = (ProductCategory)this.model.getRowData();
		pc.setActive(active);
		getManagerBean().update(pc);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String title = "";
		ProductCategory pc = (ProductCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_CATEGORY_DETAIL_PRODUCT_CATEGORY_ID), pc.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.PRODUCT_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			ProductCategoryDetail pcd = (ProductCategoryDetail)list.get(0);
			title = pcd.getLabel();
		}
		return title;
	}
	
	public void onSelectSubCategories(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		SubCategoryController lc = (SubCategoryController)AonUtil.getController("subCategory");
		IManagerBean moBean = BeanManager.getManagerBean(ProductCategory.class);
		ProductCategory productCategory = (ProductCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID), "" + productCategory.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ALIAS));
		lc.setParent(productCategory);
		lc.setCriteria(criteria);
		lc.onSearch(event);
	}

	public List<SelectItem> getParentCategories() throws ManagerBeanException, ExpressionException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
		criteria.addNullExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID));
		List<ITransferObject> list = (List<ITransferObject>)categoryBean.getList(criteria);
		SelectItem item = new SelectItem(null,"------");
		categories.add(item);
		for (int i = 0; i < list.size(); i++) {
			ProductCategory pcd = (ProductCategory)list.get(i);
			item = new SelectItem(pcd.getId(),pcd.getAlias());
			if (getTo()!=null && !pcd.getId().equals(((ProductCategory)getTo()).getId()))
				categories.add(item);
		}
		return categories;
	}
	
	public List<SelectItem> getCategories() throws ManagerBeanException, ExpressionException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
		criteria.addNullExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID));
		List<ITransferObject> list = (List<ITransferObject>)categoryBean.getList(criteria);
		SelectItem item;
		for (int i = 0; i < list.size(); i++) {
			ProductCategory pcd = (ProductCategory)list.get(i);
			item = new SelectItem(pcd.getId(),pcd.getAlias());
			categories.add(item);
			IManagerBean categorySubCatBean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteriaSubCat = new Criteria();
			criteriaSubCat.addEqualExpression(categorySubCatBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
			criteriaSubCat.addExpression(categorySubCatBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID), ""+pcd.getId());
			List<ITransferObject> listSubCat = (List<ITransferObject>)categorySubCatBean.getList(criteriaSubCat);
			for (int j = 0; j < listSubCat.size(); j++) {
				ProductCategory pcdSubCat = (ProductCategory)listSubCat.get(j);
				item = new SelectItem(pcdSubCat.getId(),"&nbsp;&nbsp;&nbsp;&nbsp;"+pcd.getAlias());
				categories.add(item);
			}
		}
		return categories;
	}

}