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
import com.code.aon.product.Tariff;
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

	/** The brands list. */
	private List<SelectItem> brands;

	/**
	 * Gets the brands.
	 * 
	 * @return the brands
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getBrands() throws ManagerBeanException {
		if (brands == null) {
			brands = new LinkedList<SelectItem>();
			IManagerBean brandBean = BeanManager.getManagerBean(Brand.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(brandBean.getFieldName(IProductAlias.BRAND_NAME));
			Iterator<ITransferObject> iter = brandBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Brand brand = (Brand) iter.next();
				SelectItem item = new SelectItem(brand, brand.getName());
				brands.add(item);
			}
		}
		return brands;
	}

	/** The productCategories list. */
	private List<SelectItem> pCategories;

	/**
	 * Gets the productCategories.
	 * 
	 * @return the productCategories
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getCategories() throws ManagerBeanException {
		if (pCategories == null) {
			pCategories = new LinkedList<SelectItem>();
			IManagerBean productCategoryBean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(productCategoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
			Iterator<ITransferObject> iter = productCategoryBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				ProductCategory pCategory = (ProductCategory) iter.next();
				SelectItem item = new SelectItem(pCategory, pCategory.getName());
				pCategories.add(item);
			}
		}
		return pCategories;
	}
	
	private List<SelectItem> pCategoriesByGroup;
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCategoriesByGroup() throws ManagerBeanException {
		if(pCategoriesByGroup == null){
			pCategoriesByGroup = new LinkedList<SelectItem>();
			IManagerBean pCategoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(pCategoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
			Iterator iter = pCategoryGroupBean.getList(criteria).iterator();
			while(iter.hasNext()){
				ProductCategoryGroup catGroup = (ProductCategoryGroup)iter.next();
				SelectItemGroup itemGroup = new SelectItemGroup(catGroup.getName(), catGroup.getName(), true, obtainGroupCateogries(catGroup.getId()));
				pCategoriesByGroup.add(itemGroup);
			}
		}
		return pCategoriesByGroup;
	}

	@SuppressWarnings("unchecked")
	private SelectItem[] obtainGroupCateogries(Integer id) throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean pcategoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(pcategoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), id);
		criteria.addOrder(pcategoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_NAME));
		Iterator iter = pcategoryBean.getList(criteria).iterator();
		while(iter.hasNext()){
			ProductCategory category = (ProductCategory)iter.next();
			SelectItem item = new SelectItem(category, category.getName());
			items.add(item);
		}
		return items.toArray(new SelectItem[items.size()]);
	}
	
	private List<SelectItem> pCategoryGroups;
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPCategoryGroups() throws ManagerBeanException {
		if(pCategoryGroups == null){
			pCategoryGroups = new LinkedList<SelectItem>();
			IManagerBean pCategoryGroupBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(pCategoryGroupBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME));
			Iterator iter = pCategoryGroupBean.getList(criteria).iterator();
			while(iter.hasNext()){
				ProductCategoryGroup group = (ProductCategoryGroup)iter.next();
				SelectItem item = new SelectItem(group, group.getName());
				pCategoryGroups.add(item);
			}
		}
		return pCategoryGroups;
	}

	/**
	 * Gets the product statuses
	 * 
	 * @return the product statuses
	 */
	public List<SelectItem> getProductStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		ProductStatus[] pStatuses = ProductStatus.values();
		for (int i = 0; i < pStatuses.length; i++) {
			ProductStatus status = pStatuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			types.add(item);
		}
		return types;
	}
	
	/**
	 * Gets the product types
	 * 
	 * @return the product types
	 */
	public List<SelectItem> getProductTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		ProductType[] pTypes = ProductType.values();
		for (int i = 0; i < pTypes.length; i++) {
			ProductType status = pTypes[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			types.add(item);
		}
		return types;
	}
	

	/** The mime types list. */
	private List<SelectItem> mimeTypes;

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

	/** The catalogues list. */
	private List<SelectItem> catalogues;

	/**
	 * Gets the catalogues.
	 * 
	 * @return the catalogues
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getCatalogues() throws ManagerBeanException {
		if (catalogues == null) {
			catalogues = new LinkedList<SelectItem>();
			IManagerBean catalogueBean = BeanManager.getManagerBean(Catalogue.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(catalogueBean.getFieldName(IProductAlias.CATALOGUE_NAME));
			Iterator<ITransferObject> iter = catalogueBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Catalogue catalogue = (Catalogue) iter.next();
				SelectItem item = new SelectItem(catalogue,catalogue.getName());
				catalogues.add(item);
			}
		}
		return catalogues;
	}
	
	/** The tariffs list. */
	private List<SelectItem> tariffs;

	/**
	 * Gets the tariffs.
	 * 
	 * @return the tariffs
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getTariffs() throws ManagerBeanException {
		if (tariffs == null) {
			tariffs = new LinkedList<SelectItem>();
			IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(tariffBean.getFieldName(IProductAlias.TARIFF_NAME));
			Iterator<ITransferObject> iter = tariffBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Tariff tariff = (Tariff) iter.next();
				SelectItem item = new SelectItem(tariff, tariff.getName());
				tariffs.add(item);
			}
		}
		return tariffs;
	}
	
	/**
	 * Gets the plu product types
	 * 
	 * @return the plu product types
	 */
	public List<SelectItem> getPluProductTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		PluProductType[] pStatuses = PluProductType.values();
		for (int i = 0; i < pStatuses.length; i++) {
			PluProductType status = pStatuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			types.add(item);
		}
		return types;
	}
	
}