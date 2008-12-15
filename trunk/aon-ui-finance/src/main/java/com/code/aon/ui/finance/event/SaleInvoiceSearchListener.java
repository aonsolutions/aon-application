package com.code.aon.ui.finance.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class SaleInvoiceSearchListener extends ControllerSearchListener {
	
	private Registry registry;
	
	private Date issueDateFrom;
	
	private Date issueDateTo;

    private Item item;
	
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setIssueDateFrom(null);
		setIssueDateTo(null);		
		setRegistry( new Registry() );
		setItem( new Item() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getRegistry() != null) && (getRegistry().getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
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
	}	

}