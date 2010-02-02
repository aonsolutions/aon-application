package com.code.aon.ui.finance.util.print;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class FeePrinter extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(FeePrinter.class.getName());

	private Integer itemId;
	
	private Month billingDateMonth;
	
	private int billingDateYear;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Month getBillingDateMonth() {
		return billingDateMonth;
	}

	public void setBillingDateMonth(Month billingDateMonth) {
		this.billingDateMonth = billingDateMonth;
	}

	public int getBillingDateYear() {
		return billingDateYear;
	}

	public void setBillingDateYear(int billingDateYear) {
		this.billingDateYear = billingDateYear;
	}
	
	private void initializeParams(){
		setItemId(null);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		setBillingDateMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setBillingDateYear(calendar.get(Calendar.YEAR));
	}
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		initializeParams();
		super.onEditSearch(event);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		createCriteria();
		super.onSearch(event);
	}

	private void createCriteria() {
		Criteria criteria = new Criteria();
		try {
			if(getItemId() !=  null){
				criteria.addEqualExpression(getFieldName(ISalesAlias.CUSTOMER_FEE_ITEM_ID), getItemId());
			}
			if(getBillingDateMonth() != null){
				criteria.addExpression(obtainFromToExpression(getBillingDateMonth(), getBillingDateYear())); 
			}
			this.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating search criteria", e);
		}
	}

	private Expression obtainFromToExpression(Month month, int year) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month.getValue(), 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date from = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date to = calendar.getTime();
		return ExpressionUtilities.getBetweenExpression(getFieldName(ISalesAlias.CUSTOMER_FEE_BILLING_DATE), from, to);
	}
}