package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.Product;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;


public class ProductController extends BasicI18nController {

	private String shortDesc;
	
	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
		loadCurrentLanguage();
	}
	
	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Product p = (Product)this.model.getRowData();
		p.setActive(active);
		getManagerBean().update(p);
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

	public String getI18nCategory() throws ManagerBeanException {
		String category = "";
		Product p = (Product)this.model.getRowData();
		IManagerBean bean = BeanManager.getManagerBean(ProductCategoryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_DETAIL_PRODUCT_CATEGORY_ID), p.getProductCategory().getId());
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		if (list.size() > 0) {
			ProductCategoryDetail pcd = (ProductCategoryDetail)list.get(0);
			category = pcd.getLabel();
		}
		return category;
	}

	public String getI18nBrand() throws ManagerBeanException {
		String brand = "";
		Product p = (Product)this.model.getRowData();
		if (p.getBrand() != null) {
			IManagerBean bean = BeanManager.getManagerBean(BrandDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BRAND_DETAIL_BRAND_ID), p.getBrand().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BRAND_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() > 0) {
				BrandDetail bd = (BrandDetail)list.get(0);
				brand = bd.getLabel();
			}
		}
		return brand;
	}

	public void onDelImage(ActionEvent event) {
		Product current = (Product)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Product current = (Product)getTo();
		current.setImage(image);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setShortDesc(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProductDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.PRODUCT_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((ProductDetail) iterator.next()).getProduct().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((ProductDetail) iterator.next()).getProduct().getId().toString(),alias));
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
		if (getShortDesc() != null && getShortDesc().length()>0) {
			completeDetailCriteria(ICMSAlias.PRODUCT_DETAIL_SHORT_LABEL, getShortDesc());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	
}