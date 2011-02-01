package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

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
import com.code.aon.ui.form.FormUtil;

public class ProductCategoryController extends BasicI18nController implements ICMSConstants {

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
		SubCategoryController lc = (SubCategoryController)FormUtil.getController(SUB_CATEGORY);
		IManagerBean moBean = BeanManager.getManagerBean(ProductCategory.class);
		ProductCategory productCategory = (ProductCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID), "" + productCategory.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ALIAS));
		lc.setParent(productCategory);
		lc.setCriteria(criteria);
		lc.onSearch(event);
	}

	public String getBack(){
		if (FormUtil.getController(PRODUCT).getTo()==null)
			return PRODUCT_LIST;
		return PRODUCT_FORM;
	}

}