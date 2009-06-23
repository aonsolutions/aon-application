package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.invoicing.InvoiceSearchContext;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class InvoiceSearchListener extends ControllerSearchListener {

	private InvoiceSearchContext context;
		
	public InvoiceSearchContext getContext() {
		return context;
	}

	public void setContext(InvoiceSearchContext context) {
		this.context = context;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setContext( new InvoiceSearchContext() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), getContext().getInvoiceType());
		if(getContext().getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),getContext().getFromDate());
		}
		if(getContext().getToDate() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),getContext().getToDate());
		}
		Registry registry = getContext().getRegistry();
		if ( (registry != null) && (registry.getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID),registry.getId());
		}
		if(getContext().getFromNumber() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER),getContext().getFromNumber());
		}
		if(getContext().getToNumber() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER),getContext().getToNumber());
		}
	}	

}