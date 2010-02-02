package com.code.aon.ui.cms.controller;

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
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;


public class ProductController extends BasicI18nController implements ICMSConstants, Constants {
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		ProductDetail fd = (ProductDetail) getModelRowdataI18n();
		if (fd != null) label = fd.getLabel();
		return label;
	}	

	public String getI18nShortLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		ProductDetail fd = (ProductDetail) getModelRowdataI18n();
		if (fd != null) label = fd.getShortLabel();
		return label;
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
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Product current = (Product)getTo();
		current.setImage(image);
	}
	
}