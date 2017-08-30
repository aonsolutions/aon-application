package com.code.aon.ui.product.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.product.controller.ProductExportGwtController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ProductStatus[] statuses;
	private ProductStatus[] itemStatuses;
	private ProductType[] types;
	private ProductKind[] kinds;
	private ProductCategory category;
	private Tax vat;
	private Tax retention;
	private Account purchaseAccount;
	private Account salesAccount;
	private Tag[] tags;
	private Supplier supplier;
	private String supplierCode;

	public ProductStatus[] getStatuses() {
		return statuses;
	}
	public void setStatuses(ProductStatus[] statuses) {
		ProductExportGwtController.setStatuses(statuses);
		this.statuses = statuses;
	}

	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}
	public void setItemStatuses(ProductStatus[] itemStatuses) {
		ProductExportGwtController.setItemStatuses(itemStatuses);
		this.itemStatuses = itemStatuses;
	}

	public ProductType[] getTypes() {
		return types;
	}
	public void setTypes(ProductType[] types) {
		ProductExportGwtController.setTypes(types);
		this.types = types;
	}
	
	public ProductKind[] getKinds() {
		return kinds;
	}
	public void setKinds(ProductKind[] kinds) {
		ProductExportGwtController.setKinds(kinds);
		this.kinds = kinds;
	}
	
	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		ProductExportGwtController.setCategory(category);
		this.category = category;
	}	

	public Tax getVat() {
		return vat;
	}
	public void setVat(Tax vat) {
		ProductExportGwtController.setVat(vat);
		this.vat = vat;
	}
	
	public Tax getRetention() {
		return retention;
	}
	public void setRetention(Tax retention) {
		ProductExportGwtController.setRetention(retention);
		this.retention = retention;
	}

	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public void setPurchaseAccount(Account purchaseAccount) {
		ProductExportGwtController.setPurchaseAccount(purchaseAccount);
		this.purchaseAccount = purchaseAccount;
	}

	public Account getSalesAccount() {
		return salesAccount;
	}
	public void setSalesAccount(Account salesAccount) {
		ProductExportGwtController.setSalesAccount(salesAccount);
		this.salesAccount = salesAccount;
	}
	
	public Tag[] getTags() {
		if (ArrayUtils.isEmpty(tags)) {
			tags = new Tag[]{getEmptyTag()};
		}
		ProductExportGwtController.setTags(tags);
		return tags;
	}
	public void setTags(Tag[] tags) {
		ProductExportGwtController.setTags(tags);
		this.tags = tags;
	}
	public int getTagsSize() {
		return ArrayUtils.getLength(tags);
	}
	public List<Integer> getTagsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Tag tag : getTags() ) {
			if ((tag != null) && (tag.getId() != null)) {
				ids.add(tag.getId());
			}
		}
		return ids;
	}			
	
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		ProductExportGwtController.setSupplier(supplier);
		this.supplier = supplier;
	}
	
	public String getSupplierCode() {
		return supplierCode;
	}
	public void setSupplierCode(String supplierCode) {
		ProductExportGwtController.setSupplierCode(supplierCode);
		this.supplierCode = supplierCode;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setStatuses(new ProductStatus[]{ProductStatus.ACTIVE});
		setItemStatuses(new ProductStatus[0]);
		setTypes(new ProductType[0]);
		setKinds(new ProductKind[0]);
		setCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).createNewTo());
		setVat((Tax)BeanManager.getManagerBean(Tax.class).createNewTo());
		setRetention((Tax)BeanManager.getManagerBean(Tax.class).createNewTo());
		setPurchaseAccount((Account)BeanManager.getManagerBean(Account.class).createNewTo());
		setSalesAccount((Account)BeanManager.getManagerBean(Account.class).createNewTo());
		setTags(new Tag[]{getEmptyTag()});
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		setSupplierCode(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getStatuses())) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_STATUS);
			addEnumToCriteria(criteria, alias, getStatuses());
		}
		if (!ArrayUtils.isEmpty(getItemStatuses())) {
			addEnumToCriteria(criteria, "Product.items.status", getItemStatuses());
		}
		if (!ArrayUtils.isEmpty(getTypes())) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_TYPE);
			addEnumToCriteria(criteria, alias, getTypes());
		}
		if (getController() instanceof BasicController && !((BasicController)getController()).isLookup()) {
			if (!ArrayUtils.isEmpty(getKinds())) {
				String alias = getController().resolveAlias(IEntityAlias.PRODUCT_KIND);
				addEnumToCriteria(criteria, alias, getKinds());
			} else {
				String alias = getController().resolveAlias(IEntityAlias.PRODUCT_KIND);
				Expression kindExpr = ExpressionUtilities.getEqualExpression(alias, ProductKind.SALE_PURCHASE);
				if (AonUtil.getRoleManager().isPurchaseOperator()) {
					kindExpr = ExpressionUtilities.getOrExpression(kindExpr,  ExpressionUtilities.getEqualExpression(alias, ProductKind.PURCHASE));
				}
				if (AonUtil.getRoleManager().isSaleOperator()) {
					kindExpr = ExpressionUtilities.getOrExpression(kindExpr,  ExpressionUtilities.getEqualExpression(alias, ProductKind.SALE));
				}
				criteria.addExpression(kindExpr);
			}
		}
		if (getCategory() != null && getCategory().getId() != null) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_PRODUCT_CATEGORY_ID);
			criteria.addEqualExpression(alias, getCategory().getId());
		}
		if ((getVat() != null) && (getVat().getId() != null)) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_VAT_ID);
			criteria.addEqualExpression(alias, getVat().getId());			
		}	
		if ((getRetention() != null) && (getRetention().getId() != null)) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_RETENTION_ID);
			criteria.addEqualExpression(alias, getRetention().getId());			
		}	
		if ((getPurchaseAccount() != null) && (getPurchaseAccount().getId() != null)) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_PURCHASE_ACCOUNT_ID);
			criteria.addEqualExpression(alias, getPurchaseAccount().getId());			
		}	
		if ((getSalesAccount() != null) && (getSalesAccount().getId() != null)) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_SALES_ACCOUNT_ID);
			criteria.addEqualExpression(alias, getSalesAccount().getId());			
		}	
		if (getTagsSize() > 0) {
			addEnumToCriteria(criteria, "Product.tags.tag.id", getTagsIds().toArray());	
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Product_items_suppliers_registry_id"), getSupplier().getId());
		}
		if (StringUtils.isNotBlank(StringUtils.trim(getSupplierCode()))) {
			criteria.addEqualExpression(getController().resolveAlias("Product_items_suppliers_code"), StringUtils.trim(getSupplierCode()));
		}
	}

	public void onAddTag(ActionEvent event) {
		this.tags = (Tag[]) ArrayUtils.add(this.tags, getEmptyTag());
	}

	public void onRemoveTag(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.tags = (Tag[]) ArrayUtils.remove(this.tags, index);
		if ( ArrayUtils.isEmpty(this.tags) ) {
			setTags(new Tag[]{getEmptyTag()});
		}
	}		

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public List<SelectItem> getSelectableTags() throws ManagerBeanException {
    	IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT);
    	criteria.addOrder(tagBean.getFieldName(IEntityAlias.TAG_NAME));
    	return ConfigCollectionsController.getTagList((List)tagBean.getList(criteria));
    }    

    public int getSelectableTagsCount() throws ManagerBeanException {
    	IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT);
    	return tagBean.getCount(criteria);
    }    

	private Tag getEmptyTag() {
		return getCollectionsController().getEmptyTag();
	}	

	private ConfigCollectionsController getCollectionsController() {
		return (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
	}

}