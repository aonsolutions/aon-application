package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.cms.util.ControllerUtil;

public class ProductCategoryHandler {

	private String alias;
	
	private String label;

	private String url;

	public ProductCategoryHandler(ProductCategory category){
		this(ProductCategoryHandler.recoverProductCategoryDetail(category));
	}
	
	public ProductCategoryHandler(ProductCategoryDetail detail){
		this.alias = detail.getProductCategory().getAlias();
		this.label = detail==null?null:detail.getLabel();
		this.url = Templates.PRODUCT_CATEGORY.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", this.alias);
	}

	private static ProductCategoryDetail recoverProductCategoryDetail(ProductCategory object){
		Session s = HibernateUtil.getSession();
		String stmt = "SELECT pcd FROM ProductCategoryDetail pcd " +
				"WHERE pcd.productCategory.id = ? " +
				"AND pcd.language.id = ?";
		Query query = s.createQuery(stmt);
		query.setInteger(0, object.getId());
		query.setInteger(1, ControllerUtil.getCurrentLanguage().getId());
		List<ProductCategoryDetail> list = query.list();
		if (!list.isEmpty())
			return list.get(0);
		return null;
	}

	public String getAlias() {
		return alias;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

}
