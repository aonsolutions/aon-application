package com.code.aon.ui.product.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductSearchListener extends ControllerSearchListenerEx {

	private ProductStatus[] statuses;
	
	private ProductType[] types;
	
	private Tax vat;
	
	private Tax retention;
	
	private Account purchaseAccount;
	
	private Account salesAccount;
	
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
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getStatuses())) {
			String alias = getController().resolveAlias(IEntityAlias.PRODUCT_STATUS);
			addEnumToCriteria(criteria, alias, getStatuses());
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
	}
	
}