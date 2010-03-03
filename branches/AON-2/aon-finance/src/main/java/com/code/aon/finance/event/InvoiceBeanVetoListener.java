package com.code.aon.finance.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Invoice invoice = (Invoice)evt.getTo();
		if(invoice.getType() == InvoiceType.SALES){
	    	String referenceCode = StringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0");
			if (!StringUtils.isEmpty(invoice.getSeries())) {
				referenceCode = invoice.getSeries() + "/" + referenceCode;
			}
			invoice.setReferenceCode(referenceCode);
		} else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(invoice.getIssueDate());
			invoice.setSeries(Integer.toString(calendar.get(Calendar.YEAR)));
	        if(invoice.getNumber() == 0) {
	        	Criteria criteria = new Criteria();
	        	criteria.addExpression(ExpressionUtilities.getNotEqualExpression("invoice.type", InvoiceType.SALES.ordinal()));
	        	invoice.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Invoice", criteria));
			}
		}
	}

}