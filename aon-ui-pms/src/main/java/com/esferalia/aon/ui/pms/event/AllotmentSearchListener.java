package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class AllotmentSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Date fromDate;
	private Date toDate;
	private Customer agency;
	private InvoicingGroup agencyGroup;
	private String remarks;
	private Boolean active;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public InvoicingGroup getAgencyGroup() {
		return agencyGroup;
	}
	public void setAgencyGroup(InvoicingGroup agencyGroup) {
		this.agencyGroup = agencyGroup;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setFromDate(DateUtils.truncate(new Date(), Calendar.DATE));
		setToDate(DateUtils.truncate(DateUtils.addWeeks(new Date(), 2), Calendar.DATE));
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setAgencyGroup((InvoicingGroup)BeanManager.getManagerBean(InvoicingGroup.class).createNewTo());
		setRemarks(null);
		setActive(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_HOTEL_ID), getHotel().getId());			
		}
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_END_DATE), getFromDate());			
		}
		if (getToDate() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_START_DATE), getToDate());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_AGENCY_ID), getAgency().getId());			
		}
		if (getAgencyGroup() != null && getAgencyGroup().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_ID), getAgencyGroup().getId());			
		}
		if (StringUtils.isNotBlank(getRemarks())) {
			criteria.addExpression(ExpressionUtilities.getLikeExpression(getFieldName(IEntityAlias.ALLOTMENT_REMARKS), "%"+getRemarks()+"%"));
		}
		if (getActive() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_ACTIVE), getActive());
		}
	}

}