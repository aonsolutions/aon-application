package com.code.aon.ui.product.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.product.Brand;
import com.code.aon.product.Catalogue;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.PluProductType;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.product</code>
 * 
 * @author Consulting & Development. ecastellano - 14-dic-2005
 */
public class ProductCollectionsController {

	/** The mime types list. */
	private List<SelectItem> mimeTypes;
	private List<SelectItem> productStatuses;
	private List<SelectItem> productTypes;
	private List<SelectItem> pluProductTypes;
	
	public Brand getBrand() {
		return null;
	}
	
	public void setBrand(Brand brand ) {
	}
	
	public List<SelectItem> getBrands() throws ManagerBeanException {
		List<SelectItem> brands = new LinkedList<SelectItem>();
		IManagerBean brandBean = BeanManager.getManagerBean(Brand.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(brandBean.getFieldName(IProductAlias.BRAND_NAME));
		Iterator<ITransferObject> iter = brandBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Brand brand = (Brand) iter.next();
			SelectItem item = new SelectItem(brand, brand.getName());
			brands.add(item);
		}
		return brands;
	}

	public ProductCategory getCategory() {
		return null;
	}
	
	public void setCategory(ProductCategory productCategory) {
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCategories() throws ManagerBeanException {
		boolean groups = false;
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
		Iterator iter = categoryGroupBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			groups = true;
			ProductCategoryGroup categoryGroup = (ProductCategoryGroup)iter.next();
			SelectItem[] groupCategories = obtainGroupCategories(categoryGroup.getId());
			SelectItemGroup item = new SelectItemGroup(categoryGroup.getName(), categoryGroup.getName(), false, groupCategories);
			categories.add(item);
		}

		SelectItem[] noGroupCategories = obtainNoGroupCategories();
		if (groups) {
			SelectItemGroup item = new SelectItemGroup("OTRAS", "OTRAS", false, noGroupCategories);
			categories.add(item);
		} else {
			for (SelectItem category : noGroupCategories) {
				categories.add(category);
			}
		}
		return categories;
	}

	@SuppressWarnings("unchecked")
	private SelectItem[] obtainGroupCategories(Integer groupId) throws ManagerBeanException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), groupId);
		criteria.addOrder(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
		Iterator iter = categoryBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			ProductCategory category = (ProductCategory)iter.next();
			SelectItem item = new SelectItem(category, category.getName());
			categories.add(item);
		}
		return categories.toArray(new SelectItem[categories.size()]);
	}
	
	@SuppressWarnings("unchecked")
	private SelectItem[] obtainNoGroupCategories() throws ManagerBeanException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP));
		criteria.addOrder(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
		Iterator iter = categoryBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			ProductCategory category = (ProductCategory)iter.next();
			SelectItem item = new SelectItem(category, category.getName());
			categories.add(item);
		}
		return categories.toArray(new SelectItem[categories.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCategoryGroups() throws ManagerBeanException {
		List<SelectItem> categoryGroups = new LinkedList<SelectItem>();
		IManagerBean categoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
		Iterator iter = categoryGroupBean.getList(criteria).iterator();
		while(iter.hasNext()){
			ProductCategoryGroup categoryGroup = (ProductCategoryGroup)iter.next();
			SelectItem item = new SelectItem(categoryGroup, categoryGroup.getName());
			categoryGroups.add(item);
		}
		return categoryGroups;
	}

	/**
	 * Gets the product statuses
	 * 
	 * @return the product statuses
	 */
	public List<SelectItem> getProductStatuses() {
		if (productStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productStatuses = new LinkedList<SelectItem>();
			for (ProductStatus status:ProductStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				productStatuses.add(item);
			}
		}
		return productStatuses;
	}
	
	/**
	 * Gets the product types
	 * 
	 * @return the product types
	 */
	public List<SelectItem> getProductTypes() {
		if (productTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productTypes = new LinkedList<SelectItem>();
			for (ProductType type:ProductType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				productTypes .add(item);
			}
		}
		return productTypes;
	}
	
	/**
	 * Gets the product types
	 * 
	 * @return the product types
	 */
	public List<SelectItem> getNoExpenseProductTypes() {
		if (productTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productTypes = new LinkedList<SelectItem>();
			for (ProductType type:ProductType.values()) {
				if (type != ProductType.EXPENSE) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					productTypes .add(item);
				}
			}
		}
		return productTypes;
	}
	
	/**
	 * Gets the MIME types
	 * 
	 * @return the mime types
	 */
	public List<SelectItem> getMimeTypes() {
		if (mimeTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mimeTypes = new LinkedList<SelectItem>();
			for (MimeType mimeType : MimeType.values()) {
				String name = mimeType.getName(locale);
				SelectItem item = new SelectItem(mimeType, name);
				mimeTypes.add(item);
			}
		}
		return mimeTypes;
	}

	/**
	 * Gets the catalogues.
	 * 
	 * @return the catalogues
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getCatalogues() throws ManagerBeanException {
		List<SelectItem> catalogues = new LinkedList<SelectItem>();
		IManagerBean catalogueBean = BeanManager.getManagerBean(Catalogue.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(catalogueBean.getFieldName(IProductAlias.CATALOGUE_NAME));
		Iterator<ITransferObject> iter = catalogueBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Catalogue catalogue = (Catalogue) iter.next();
			SelectItem item = new SelectItem(catalogue,catalogue.getName());
			catalogues.add(item);
		}
		return catalogues;
	}
	
	/**
	 * Gets the plu product types
	 * 
	 * @return the plu product types
	 */
	public List<SelectItem> getPluProductTypes() {
		if (pluProductTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			pluProductTypes = new LinkedList<SelectItem>();
			for (PluProductType status:PluProductType.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				pluProductTypes.add(item);
			}
		}
		return pluProductTypes;
	}
	
}