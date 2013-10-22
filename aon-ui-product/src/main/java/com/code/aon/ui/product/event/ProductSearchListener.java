package com.code.aon.ui.product.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductSearchListener extends ControllerSearchListenerEx {

	private static final Tag EMPTY_TAG = new Tag();
	
	private ProductStatus[] statuses;
	
	private ProductStatus[] itemStatuses;
	
	private ProductType[] types;
	
	private Tax vat;
	
	private Tax retention;
	
	private Account purchaseAccount;
	
	private Account salesAccount;
	
	private Tag[] tags;
	
	private Supplier supplier;
	
	private ProductCategory category;
	
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public ProductStatus[] getStatuses() {
		return statuses;
	}

	public void setStatuses(ProductStatus[] statuses) {
		this.statuses = statuses;
	}

	public ProductType[] getTypes() {
		return types;
	}

	public void setTypes(ProductType[] types) {
		this.types = types;
	}
	
	public Tax getVat() {
		return vat;
	}

	public void setVat(Tax vat) {
		this.vat = vat;
	}
	
	public Tax getRetention() {
		return retention;
	}

	public void setRetention(Tax retention) {
		this.retention = retention;
	}

	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

	public Account getSalesAccount() {
		return salesAccount;
	}

	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}
	
	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}

	public void setItemStatuses(ProductStatus[] itemStatuses) {
		this.itemStatuses = itemStatuses;
	}
	
	public Tag[] getTags() {
		if (ArrayUtils.isEmpty(tags)) {
			tags = new Tag[]{EMPTY_TAG};
		}
		return tags;
	}

	public void setTags(Tag[] tags) {
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
	
	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}	

	@Override
	protected void init() throws ManagerBeanException {
		setStatuses( new ProductStatus[]{ProductStatus.ACTIVE} );
		setTypes( new ProductType[0] );
		IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
		setVat( (Tax) taxBean.createNewTo() );
		setRetention( (Tax) taxBean.createNewTo() );
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		setPurchaseAccount( (Account) accountBean.createNewTo() );
		setSalesAccount( (Account) accountBean.createNewTo() );
		setItemStatuses( new ProductStatus[0] );
		setTags( new Tag[]{EMPTY_TAG} );
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		setSupplier((Supplier)supplierBean.createNewTo());
		setCategory( (ProductCategory) BeanManager.getManagerBean(ProductCategory.class).createNewTo() );
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
		if ( (getVat() != null) && (getVat().getId() != null) ) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_VAT_ID);
			criteria.addEqualExpression(alias, getVat().getId());			
		}	
		if ( (getRetention() != null) && (getRetention().getId() != null) ) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_RETENTION_ID);
			criteria.addEqualExpression(alias, getRetention().getId());			
		}	
		if ( (getPurchaseAccount() != null) && (getPurchaseAccount().getId() != null) ) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_PURCHASE_ACCOUNT_ID);
			criteria.addEqualExpression(alias, getPurchaseAccount().getId());			
		}	
		if ( (getSalesAccount() != null) && (getSalesAccount().getId() != null) ) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_SALES_ACCOUNT_ID);
			criteria.addEqualExpression(alias, getSalesAccount().getId());			
		}	
		if ( getTagsSize() > 0 ) {
			addEnumToCriteria(criteria, "Product.tags.tag.id", getTagsIds().toArray());	
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Product_items_suppliers_supplier_id"), getSupplier().getId());
		}
		if (getCategory() != null && getCategory().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Product_category<id"), getCategory().getId());
		}
	}
	
	public void onAddTag(ActionEvent event) {
		this.tags = (Tag[]) ArrayUtils.add(this.tags, EMPTY_TAG);
	}
	
	public void onRemoveTag(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.tags = (Tag[]) ArrayUtils.remove(this.tags, index);
		if ( ArrayUtils.isEmpty(this.tags) ) {
			setTags(new Tag[]{EMPTY_TAG});
		}
	}		
	 
    public List<SelectItem> getSelectableTags() throws ManagerBeanException {
    	List<SelectItem> tags = new LinkedList<SelectItem>();
    	IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT );
    	criteria.addOrder(tagBean.getFieldName(IEntityAlias.TAG_NAME));
    	for( ITransferObject to : tagBean.getList(criteria) ) {
    		Tag tag = (Tag) to;
    		SelectItem item = new SelectItem(tag, tag.getName());
    		tags.add(item);
    	}
    	return tags;
    }    
	
}