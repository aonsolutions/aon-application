package com.code.aon.finance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.CustomerFeeDB;

@Entity
@Table(name="customer_fee")
public class CustomerFee extends CustomerFeeDB implements ICalculable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String invoicingDescription;

    @Transient
	public Customer getInvoicingCustomer() {
		return (getInvoicingGroup() != null && getInvoicingGroup().getId() != null) ? getInvoicingGroup().getCustomer() : getCustomer();
    }

    @Transient
	public String getInvoicingDescription() {
		return (StringUtils.isNotEmpty(invoicingDescription)) ? invoicingDescription : getDescription();
    }
    @Transient
	public void setInvoicingDescription(String invoicingDescription) {
    	this.invoicingDescription = invoicingDescription;
    }

	public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
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
		super.setBillingDate(calendar.getTime());
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
		super.setBillingDate(calendar.getTime());
	}

	@Transient
	public double getTaxes() {
		return 0;
	}

	@Transient
	public boolean isPrepaymentFee() throws ManagerBeanException {
		if (getItem() != null && getItem().getId() != null && getItem().getProduct().getType() == ProductType.PREPAYMENT) {
			IManagerBean prepaymentBean = BeanManager.getManagerBean(Prepayment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT), PrepaymentCollect.FEE);
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT_ID), getId());
			return prepaymentBean.getCount(criteria) > 0;
		}
		return false;
	}

	@Transient
	public Integer getPrepaymentId() throws ManagerBeanException {
		if (getItem() != null && getItem().getId() != null && getItem().getProduct().getType() == ProductType.PREPAYMENT) {
			IManagerBean prepaymentBean = BeanManager.getManagerBean(Prepayment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT), PrepaymentCollect.FEE);
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT_ID), getId());
			for (ITransferObject ito : prepaymentBean.getList(criteria)) {
				return ((Prepayment)ito).getId();
			}
		}
		return null;
	}

}