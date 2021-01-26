package com.code.aon.ui.finance.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.controller.FeeExportGwtController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class FeePrinterSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Item item;
	private Month billingDateMonth;
	private Integer billingDateYear;
	private Boolean anual;
	
	public Boolean getAnual() {
		return anual;
	}

	public void setAnual(Boolean anual) {
		this.anual = anual;
	}

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
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		setBillingDateMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setBillingDateYear(calendar.get(Calendar.YEAR));
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID);
			criteria.addEqualExpression(field, getItem().getId());
		}
		if (getBillingDateMonth() != null) {
			criteria.addBetweenExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_BILLING_DATE), obtainFromDate(), obtainToDate());
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_INITIAL_DATE), obtainToDate());
			Expression finalExp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE), obtainFromDate());
			Expression finalExp2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(finalExp1, finalExp2));
		}		
		FeeExportGwtController.setFrom(obtainFromDate());
		FeeExportGwtController.setTo(obtainToDate());
		FeeExportGwtController.setItem(getItem().getId());
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
		if(!anual){
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(obtainFromDate());
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DATE, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
		}else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(obtainFromDate());
			calendar.add(Calendar.YEAR, 1);
			calendar.add(Calendar.DATE, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
			
		}
	}

}