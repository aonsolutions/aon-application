package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;

public class ReservationRequestSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Customer agency;
	private Customer company;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
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
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setCompany((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_HOTEL_ID), getHotel().getId());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_AGENCY_ID), getAgency().getId());			
		}
		if (getCompany() != null && getCompany().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_COMPANY_ID), getCompany().getId());			
		}
		if (!AonUtil.getRoleManager().isSaleOperator()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.RESERVATION_REQUEST_BOOKING_HOLDER), BookingHolder.GUEST);			
		}
	}

}