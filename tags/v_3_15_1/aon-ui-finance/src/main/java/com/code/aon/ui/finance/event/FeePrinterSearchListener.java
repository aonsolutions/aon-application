package com.code.aon.ui.finance.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FeePrinterSearchListener extends ControllerSearchListener {

	private Item item;
	
	private Month billingDateMonth;
	
	private Integer billingDateYear;	
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Month getBillingDateMonth() {
		return billingDateMonth;
	}

	public void setBillingDateMonth(Month billingDateMonth) {
		this.billingDateMonth = billingDateMonth;
	}

	public Integer getBillingDateYear() {
		return billingDateYear;
	}

	public void setBillingDateYear(Integer billingDateYear) {
		this.billingDateYear = billingDateYear;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setItem( new Item() );
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		setBillingDateMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setBillingDateYear(calendar.get(Calendar.YEAR));
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			String field = getController().getFieldName(IFinanceAlias.CUSTOMER_FEE_ITEM_ID);
			criteria.addEqualExpression(field, getItem().getId());
		}
		if (getBillingDateMonth() != null) {
			criteria.addBetweenExpression(getFieldName(IFinanceAlias.CUSTOMER_FEE_BILLING_DATE), obtainFromDate(), obtainToDate());
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.CUSTOMER_FEE_INITIAL_DATE), obtainToDate());
			Expression finalExp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.CUSTOMER_FEE_FINAL_DATE), obtainFromDate());
			Expression finalExp2 = ExpressionUtilities.getNullExpression(getFieldName(IFinanceAlias.CUSTOMER_FEE_FINAL_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(finalExp1, finalExp2));
		}		
	}	

	private Date obtainFromDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(getBillingDateYear(), getBillingDateMonth().getValue(), 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	private Date obtainToDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obtainFromDate());
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

}