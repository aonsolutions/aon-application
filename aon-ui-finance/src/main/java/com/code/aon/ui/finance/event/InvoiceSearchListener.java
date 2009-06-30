package com.code.aon.ui.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class InvoiceSearchListener extends RegistrySearchListener {
	
	private String defaultType;
	
	private String defaultStatus;

	private Registry registry;
	
    private Item item;

	private Bank bank;
    
	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
	public String getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(String defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setRegistry(new Registry());
		setItem(new Item());
		getItem().setProduct(new Product());
		setBank(new Bank());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType()); 
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), type);	
		}
		if (getDefaultStatus() != null) {
			InvoiceStatus status = InvoiceStatus.valueOf(getDefaultStatus()); 
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_STATUS), status);	
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Invoice.lines.item.id", getItem().getId());
		}		
		if ((getBank() != null) && (!StringUtils.isEmpty(getBank().getCode()))) {
			criteria.addEqualExpression("Invoice.finances.bank.code", getBank().getCode());			
		}		
	}	

}