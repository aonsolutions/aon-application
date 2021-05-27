package com.code.aon.ui.product.event;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemSearchListener extends RegistrySearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ProductStatus[] itemStatuses;
	private Supplier supplier;
	private String supplierCode;
	private Supplier supplierParam;
	private Product product;
	private ProductKind[] kinds;
	private ProductCategory category;
	
	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}

	public void setItemStatuses(ProductStatus[] itemStatuses) {
		this.itemStatuses = itemStatuses;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public Supplier getSupplierParam() {
		return supplierParam;
	}

	public void setSupplierParam(Supplier supplierParam) {
		this.supplierParam = supplierParam;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductKind[] getKinds() {
		return kinds;
	}
	public void setKinds(ProductKind[] kinds) {
		this.kinds = kinds;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	@Override
	protected void init() throws ManagerBeanException {
		ProductStatus[] defaultItemStatus = {ProductStatus.ACTIVE};
		setItemStatuses(defaultItemStatus);
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		if (getSupplierParam() != null && getSupplierParam().getId() != null) {
			setSupplier(getSupplierParam());
		} else {
			setSupplier((Supplier)supplierBean.createNewTo());
		}
		setSupplierCode(null);
		setSupplierParam((Supplier)supplierBean.createNewTo());
		setProduct((Product) BeanManager.getManagerBean(Product.class).createNewTo());
		setKinds(new ProductKind[0]);
		setCategory((ProductCategory) BeanManager.getManagerBean(ProductCategory.class).createNewTo());
		super.init();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getItemStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.ITEM_STATUS);
			addEnumToCriteria(criteria, status, getItemStatuses());
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Item_suppliers_registry_id"), getSupplier().getId());
		}
		if (StringUtils.isNotBlank(StringUtils.trim(getSupplierCode()))) {
			criteria.addEqualExpression(getController().resolveAlias("Items_suppliers_code"), StringUtils.trim(getSupplierCode()));
		}
		if (getProduct() != null && getProduct().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getProduct().getId());
		}
		if (getController() instanceof BasicController && !((BasicController)getController()).isLookup()) {
			if (!ArrayUtils.isEmpty(getKinds())) {
				String alias = getController().resolveAlias(IEntityAlias.ITEM_PRODUCT_KIND);
				addEnumToCriteria(criteria, alias, getKinds());
			} else {
				String alias = getController().resolveAlias(IEntityAlias.ITEM_PRODUCT_KIND);
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
			criteria.addEqualExpression(getController().resolveAlias("Item_product_category<id"), getCategory().getId());
		}
		super.completeCriteria(criteria);
	}
	
}