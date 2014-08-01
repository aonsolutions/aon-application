package com.esferalia.aon.ui.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class AllotmentSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Customer agency;
	private InvoicingGroup agencyGroup;

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

	public InvoicingGroup getAgencyGroup() {
		return agencyGroup;
	}

	public void setAgencyGroup(InvoicingGroup agencyGroup) {
		this.agencyGroup = agencyGroup;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setAgencyGroup((InvoicingGroup)BeanManager.getManagerBean(InvoicingGroup.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_HOTEL_ID), getHotel().getId());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_AGENCY_ID), getAgency().getId());			
		}
		if (getAgencyGroup() != null && getAgencyGroup().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_ID), getAgencyGroup().getId());			
		}
	}

}