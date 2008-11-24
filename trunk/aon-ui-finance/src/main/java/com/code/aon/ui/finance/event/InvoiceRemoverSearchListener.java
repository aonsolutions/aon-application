package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoiceRemovingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class InvoiceRemoverSearchListener extends ControllerSearchListener {

	private InvoiceRemovingParameters removingParams;
		
	public InvoiceRemovingParameters getRemovingParams() {
		return removingParams;
	}

	public void setRemovingParams(InvoiceRemovingParameters removingParams) {
		this.removingParams = removingParams;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setRemovingParams( new InvoiceRemovingParameters() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
		if(getRemovingParams().getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),getRemovingParams().getFromDate());
		}
		if(getRemovingParams().getToDate() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),getRemovingParams().getToDate());
		}
		Registry registry = getRemovingParams().getRegistry();
		if ( (registry != null) && (registry.getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID),registry.getId());
		}
		if(getRemovingParams().getFromNumber() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER),getRemovingParams().getFromNumber());
		}
		if(getRemovingParams().getToNumber() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER),getRemovingParams().getToNumber());
		}
	}	

}