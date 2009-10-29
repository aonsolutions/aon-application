package com.code.aon.finance.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
			invoice.setReferenceCode((invoice.getSeries()!=null&&!invoice.getSeries().equals(""))?invoice.getSeries()+"/":"" + invoice.getNumber());
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