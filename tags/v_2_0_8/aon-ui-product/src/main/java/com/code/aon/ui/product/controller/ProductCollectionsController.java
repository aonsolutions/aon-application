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
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.Tariff;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.PluProductType;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.enumeration.TaxType;
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
				SelectItem item = new SelectItem(brand.getId(),brand.getName());
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
				SelectItem item = new SelectItem(pCategory.getId(), pCategory.getName());
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
				SelectItemGroup itemGroup = new SelectItemGroup(catGroup.getName(),catGroup.getName(),true,obtainGroupCateogries(catGroup.getId()));
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
			SelectItem item = new SelectItem(category.getId(),category.getName());
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
				SelectItem item = new SelectItem(group.getId(), group.getName());
				pCategoryGroups.add(item);
			}
		}
		return pCategoryGroups;
	}

	/** The taxes list. */
	private List<SelectItem> taxes;

	/**
	 * Gets the taxes.
	 * 
	 * @return the taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getTaxes() throws ManagerBeanException {
		if (taxes == null) {
			taxes = new LinkedList<SelectItem>();
			List<ITransferObject> c = BeanManager.getManagerBean(Tax.class).getList(null);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax.getId(), tax.getName());
				taxes.add(item);
			}
		}
		return taxes;
	}

	/** The vat taxes list. */
	private List<SelectItem> vatTaxes;

	/**
	 * Gets the vat taxes.
	 * 
	 * @return the vat taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getVatTaxes() throws ManagerBeanException {
		if (vatTaxes == null) {
			vatTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IProductAlias.TAX_TYPE),TaxType.VAT);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax.getId(), tax.getName());
				vatTaxes.add(item);
			}
		}
		return vatTaxes;
	}
	
	/** The retention taxes list. */
	private List<SelectItem> retentionTaxes;
	
	/**
	 * Gets the retention taxes.
	 * 
	 * @return the retention taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getRetentionTaxes() throws ManagerBeanException {
		if (retentionTaxes == null) {
			retentionTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IProductAlias.TAX_TYPE),TaxType.RETENTION);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax.getId(), tax.getName());
				retentionTaxes.add(item);
			}
		}
		return retentionTaxes;
	}

	/**
	 * Gets the tax types
	 * 
	 * @return the tax types
	 */
	public List<SelectItem> getTaxTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		TaxType[] taxTypes = TaxType.values();
		for (int i = 0; i < taxTypes.length; i++) {
			TaxType type = taxTypes[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
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
				SelectItem item = new SelectItem(tariff.getId(),tariff.getName());
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