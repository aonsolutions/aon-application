package com.code.aon.ui.product.controller;

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
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;

public class ProductCollectionsController {

	private List<SelectItem> mimeTypes;
	private List<SelectItem> productTypes;
	private List<SelectItem> productStatuses;
	
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
		for (ITransferObject ito : brandBean.getList(criteria)) {
			Brand brand = (Brand)ito;
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
	
	public List<SelectItem> getCategories() throws ManagerBeanException {
		boolean groups = false;
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
		for (ITransferObject ito : categoryGroupBean.getList(criteria)) {
			groups = true;
			ProductCategoryGroup categoryGroup = (ProductCategoryGroup)ito;
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

	private SelectItem[] obtainGroupCategories(Integer groupId) throws ManagerBeanException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), groupId);
		criteria.addOrder(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
		for (ITransferObject ito : categoryBean.getList(criteria)) {
			ProductCategory category = (ProductCategory)ito;
			SelectItem item = new SelectItem(category, category.getName());
			categories.add(item);
		}
		return categories.toArray(new SelectItem[categories.size()]);
	}
	
	private SelectItem[] obtainNoGroupCategories() throws ManagerBeanException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP));
		criteria.addOrder(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
		for (ITransferObject ito : categoryBean.getList(criteria)) {
			ProductCategory category = (ProductCategory)ito;
			SelectItem item = new SelectItem(category, category.getName());
			categories.add(item);
		}
		return categories.toArray(new SelectItem[categories.size()]);
	}
	
	public List<SelectItem> getCategoryGroups() throws ManagerBeanException {
		List<SelectItem> categoryGroups = new LinkedList<SelectItem>();
		IManagerBean categoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
		for (ITransferObject ito : categoryGroupBean.getList(criteria)) {
			ProductCategoryGroup categoryGroup = (ProductCategoryGroup)ito;
			SelectItem item = new SelectItem(categoryGroup, categoryGroup.getName());
			categoryGroups.add(item);
		}
		return categoryGroups;
	}

	public List<SelectItem> getCatalogues() throws ManagerBeanException {
		List<SelectItem> catalogues = new LinkedList<SelectItem>();
		IManagerBean catalogueBean = BeanManager.getManagerBean(Catalogue.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(catalogueBean.getFieldName(IProductAlias.CATALOGUE_NAME));
		for (ITransferObject ito : catalogueBean.getList(criteria)) {
			Catalogue catalogue = (Catalogue)ito;
			SelectItem item = new SelectItem(catalogue,catalogue.getName());
			catalogues.add(item);
		}
		return catalogues;
	}

	public List<SelectItem> getExpenseItems() throws ManagerBeanException {
		List<SelectItem> expenseItems = new LinkedList<SelectItem>();
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_TYPE), ProductType.EXPENSE);
		criteria.addOrder(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_NAME));
		for (ITransferObject ito : itemBean.getList(criteria)) {
			Item item = (Item)ito;
			SelectItem selectItem = new SelectItem(item, item.getProduct().getName());
			expenseItems.add(selectItem);
		}
		return expenseItems;
	}

}