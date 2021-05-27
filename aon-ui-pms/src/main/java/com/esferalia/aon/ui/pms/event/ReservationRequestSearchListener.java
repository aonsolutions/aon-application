package com.esferalia.aon.ui.pms.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ReservationRequestSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private String crsCode;
	private Customer agency;
	private Customer company;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public String getCrsCode() {
		return crsCode;
	}

	public void setCrsCode(String crsCode) {
		this.crsCode = crsCode;
	}

	public Customer getAgency() {
		return agency;
	}

	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public Customer getCompany() {
		return company;
	}

	public void setCompany(Customer company) {
		this.company = company;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setCrsCode(null);
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setCompany((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_HOTEL_ID), getHotel().getId());			
		}
		if (StringUtils.isNotEmpty(getCrsCode())) {
			if (getCrsCode().startsWith("=")) {
				criteria.addEqualExpression(getController().resolveAlias("ReservationRequest.rooms.crsCode"), getCrsCode().substring(1));
			} else {
				criteria.addExpression(ExpressionUtilities.getLikeExpression(getController().resolveAlias("ReservationRequest.rooms.crsCode"), "%"+getCrsCode()+"%"));
			}
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_AGENCY_ID), getAgency().getId());			
		} else if (PmsUtils.isAgencyUser()) {
			criteria.addInExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_AGENCY_ID), PmsUtils.getUserAgencies());
		}
		if (getCompany() != null && getCompany().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_COMPANY_ID), getCompany().getId());			
		}
		if (!AonUtil.getRoleManager().isCommercialOperator()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_BOOKING_HOLDER), BookingHolder.GUEST);			
		}
	}

}