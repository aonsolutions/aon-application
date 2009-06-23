package com.code.aon.ui.finance.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class InvoicePrintSearchListener extends ControllerSearchListener {
	
	private InvoiceType type;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Date fromDate;
	
	private Date toDate;

	private Registry registry;

	public InvoiceType getType() {
		return type;
	}

	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	public boolean isSales(){
		return getType().equals(InvoiceType.SALES);
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
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
		
	@Override
	protected void init() throws ManagerBeanException {
		setType(InvoiceType.SALES);
		setFromNumber(null);
		setToNumber(null);		
		setFromDate(null);
		setToDate(null);		
		setRegistry(new Registry());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), getType());
		if ( (getRegistry() != null) && (getRegistry().getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}
		if(getFromNumber() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getFromNumber());
		}
		if(getToNumber() !=  null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getToNumber());
		}		
		if(getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getFromDate());
		}
		if(getToDate() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getToDate());
		}		
	}	

}