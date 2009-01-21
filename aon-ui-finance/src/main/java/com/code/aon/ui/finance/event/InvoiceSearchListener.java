package com.code.aon.ui.finance.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class InvoiceSearchListener extends ControllerSearchListener {
	
	private String defaultType;

	private InvoiceType type;
	
	private Registry registry;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Date issueDateFrom;
	
	private Date issueDateTo;

    private Item item;

	private Bank bank;
    
	private Date dueDateFrom;
	
	private Date dueDateTo;
    
	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}

	public InvoiceType getType() {
		return type;
	}

	public void setType(InvoiceType type) {
		this.type = type;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	
	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}
	
	public Date getIssueDateFrom() {
		return issueDateFrom;
	}

	public void setIssueDateFrom(Date issueDateFrom) {
		this.issueDateFrom = issueDateFrom;
	}

	public Date getIssueDateTo() {
		return issueDateTo;
	}

	public void setIssueDateTo(Date issueDateTo) {
		this.issueDateTo = issueDateTo;
	}
	
	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
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
		setType(null);
		if ( getDefaultType() != null ) {
			setType(InvoiceType.valueOf(getDefaultType()));
		}
		setFromNumber(null);
		setToNumber(null);				
		setIssueDateFrom(null);
		setIssueDateTo(null);		
		setRegistry( new Registry() );
		setItem( new Item() );
		getItem().setProduct(new Product());
		setDueDateFrom(null);
		setDueDateTo(null);
		setBank( new Bank() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getType() != null) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), getType());	
		}
		
		if ( (getRegistry() != null) && (getRegistry().getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}		
		if(getFromNumber() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getFromNumber());
		}
		if(getToNumber() !=  null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getToNumber());
		}				
		if(getIssueDateFrom() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getIssueDateFrom());
		}
		if(getIssueDateTo() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getIssueDateTo());
		}		
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			criteria.addEqualExpression("Invoice.lines.item.id", getItem().getId());
		}		
		if ( getDueDateFrom() != null ) {
			criteria.addGreaterThanOrEqualExpression("Invoice.finances.dueDate", getDueDateFrom());
		}
		if ( getDueDateTo() != null ) {
			criteria.addGreaterThanOrEqualExpression("Invoice.finances.dueDate", getDueDateTo());			
		}
		if ( (getBank() != null) && (!StringUtils.isEmpty(getBank().getCode())) ) {
			criteria.addEqualExpression("Invoice.finances.bank.code", getBank().getCode());			
		}		
	}	

}