package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class InvoiceRecordingSearchListener extends InvoiceSearchListener {

	public boolean isSales(){
		return getContext().getInvoiceType().equals(InvoiceType.SALES);
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		getContext().setInvoiceType(InvoiceType.SALES);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
	}	

}