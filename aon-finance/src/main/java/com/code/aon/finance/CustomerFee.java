package com.code.aon.finance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.strategy.ICalculable;
import com.esferalia.aon.entity.master.CustomerFeeDB;

@Entity
@Table(name="customer_fee")
public class CustomerFee extends CustomerFeeDB implements ICalculable {

	private static final long serialVersionUID = 113912434021805866L;

    public void setPrice(double price) {
        setPrice( CommonUtil.round(price, 4) );
    }
    @Transient
	public Month getBillingDateMonth() {
    	if(getBillingDate() != null){
    		Calendar calendar = new GregorianCalendar();
        	calendar.setTime(getBillingDate());
        	return Month.getMonthByValue(calendar.get(Calendar.MONTH));
    	}
    	return Month.JANUARY;
	}
	@Transient
	public void setBillingDateMonth(Month month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(getBillingDateYear(), month.getValue(), 1);
		setBillingDate(calendar.getTime());
	}

	@Transient
	public int getBillingDateYear() {
		Calendar calendar = new GregorianCalendar();
		if (getBillingDate() != null) {
	    	calendar.setTime(getBillingDate());
		} else {
			calendar.setTime(new Date());
		}
		return calendar.get(Calendar.YEAR);
	}
	@Transient
	public void setBillingDateYear(int billingDateYear) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(billingDateYear, getBillingDateMonth().getValue(), 1);
		setBillingDate(calendar.getTime());
	}

	@Override
	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
}