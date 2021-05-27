package com.code.aon.ui.product.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAddInfo;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> mimeTypes;
	private List<SelectItem> productKinds;
	private List<SelectItem> productTypes;
	private List<SelectItem> productStatuses;
	private List<SelectItem> itemTariffTypes;
	
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

	public List<SelectItem> getProductKinds() {
		if (productKinds == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productKinds = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(ProductKind.SALE_PURCHASE, ProductKind.SALE_PURCHASE.getName(locale));
			productKinds.add(item);
			if (AonUtil.getRoleManager().isPurchaseOperator()) {
				item = new SelectItem(ProductKind.PURCHASE, ProductKind.PURCHASE.getName(locale));
				productKinds.add(item);
			}
			if (AonUtil.getRoleManager().isSaleOperator()) {
				item = new SelectItem(ProductKind.SALE, ProductKind.SALE.getName(locale));
				productKinds.add(item);
			}
		}
		return productKinds;
	}
	
	public List<SelectItem> getProductTypes() {
		if (productTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productTypes = new LinkedList<SelectItem>();
			for (ProductType type : ProductType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				productTypes.add(item);
			}
		}
		return productTypes;
	}
	
	public List<SelectItem> getCommonProductTypes() {
		if (productTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productTypes = new LinkedList<SelectItem>();
			for (ProductType type : ProductType.values()) {
				if (type != ProductType.EXPENSE && type != ProductType.INCREASE) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					productTypes.add(item);
				}
			}
		}
		return productTypes;
	}
	
	public List<SelectItem> getProductStatuses() {
		if (productStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			productStatuses = new LinkedList<SelectItem>();
			for (ProductStatus status : ProductStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				productStatuses.add(item);
			}
		}
		return productStatuses;
	}
	
	public List<SelectItem> getItemTariffTypes() {
		if (itemTariffTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			itemTariffTypes = new LinkedList<SelectItem>();
			for (ItemTariffType type : ItemTariffType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				itemTariffTypes.add(item);
			}
		}
		return itemTariffTypes;
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
		criteria.addOrder(brandBean.getFieldName(IEntityAlias.BRAND_NAME));
		for (ITransferObject ito : brandBean.getList(criteria)) {
			Brand brand = (Brand)ito;
			SelectItem item = new SelectItem(brand, brand.getName());
			brands.add(item);
		}
		return brands;
	}

	public int getBrandsCount() throws ManagerBeanException {
		return BeanManager.getManagerBean(Brand.class).getCount(null);
	}

	public ProductCategory getCategory() {
		return null;
	}
	
	public void setCategory(ProductCategory productCategory) {
	}
	
	public List<SelectItem> getCategories() throws ManagerBeanException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryBean.getFieldName(IEntityAlias.PRODUCT_CATEGORY_NAME));
		for (ITransferObject ito : categoryBean.getList(criteria)) {
			ProductCategory category = (ProductCategory)ito;
			SelectItem item = new SelectItem(category, category.getName());
			categories.add(item);
		}
		return categories;
	}

	public List<SelectItem> getExpenseItems() throws ManagerBeanException {
		return getItemsByType(ProductType.EXPENSE);
	}

	public List<SelectItem> getPrepaymentItems() throws ManagerBeanException {
		return getItemsByType(ProductType.PREPAYMENT);
	}

	public List<SelectItem> getIncreaseItems() throws ManagerBeanException {
		return getItemsByType(ProductType.INCREASE);
	}

	private List<SelectItem> getItemsByType(ProductType type) throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_TYPE), type);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
		for (ITransferObject ito : itemBean.getList(criteria)) {
			Item item = (Item)ito;
			SelectItem selectItem = new SelectItem(item, item.getProduct().getName());
			items.add(selectItem);
		}
		return items;
	}

	public List<String> getAddInfoAttributes() throws ManagerBeanException{
    	List<String> addInfos = new LinkedList<String>();
    	IManagerBean addInfoBean = BeanManager.getManagerBean(ItemAddInfo.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(addInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(addInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_ATTRIBUTE));
		for (Object ito : addInfoBean.getList(new ProjectionList(projection), criteria)) {
    		String addInfo = (String)ito;
    		if(!addInfo.contains("system_"))
    			addInfos.add(addInfo);
    	}
    	return addInfos;
    }

	public int getSerializableProductCount() throws ManagerBeanException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_SERIALIZABLE), Boolean.TRUE);
		return productBean.getCount(criteria);
	}

}