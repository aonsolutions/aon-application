package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.enumeration.BookingHolder;

public class ReservationRequestController extends BasicController implements IPmsConstants {

	private String selectedTab;
	private int nights;
	private ReservationRequestGuest requestGuest;
	private boolean skipResetAvailabilityMap;

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public int getNights() {
		if (nights == 0) {
			ReservationRequest request = (ReservationRequest)getTo();
			nights = request.getNights();
		}
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public void resetNights() {
		setNights(0);
	}

	public ReservationRequestGuest getRequestGuest() {
		return requestGuest;
	}
	public void setRequestGuest(ReservationRequestGuest requestGuest) {
		this.requestGuest = requestGuest;
	}

	public void setSkipResetAvailabilityMap(boolean value) {
		this.skipResetAvailabilityMap = value;
	}

	@Override
	public void accept(ActionEvent event) {
		super.accept(event);

		if (!skipResetAvailabilityMap) {
			ReservationRequestRoomController requestRoomController = (ReservationRequestRoomController)AonUtil.getRegisteredBean(RESERVATION_REQUEST_ROOM_CONTROLLER_NAME);
			requestRoomController.setAvailableRoomStayMap(null);
		}
	}

	public void onStartDateChanged(ActionEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (request.getStartDate() != null) {
			request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
		} else {
			resetNights();
		}
	}

	public void onNightsChanged(ValueChangeEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setNights((Integer)event.getNewValue());
		} else {
			resetNights();
		}
		request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
	}

	public void onEndDateChanged(ActionEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (request.getEndDate() != null) {
			request.setStartDate(DateUtils.addDays(request.getEndDate(), 0-getNights()));
		} else {
			resetNights();
		}
	}

	public void onHolderChanged(ValueChangeEvent event) throws ManagerBeanException {
		ReservationRequest request = (ReservationRequest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			request.setBookingHolder((BookingHolder)event.getNewValue());
			if (!request.isAgencyHolder()) {
				request.setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			} 
			if (!request.isCompanyHolder()) {
				request.setCompany((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			}
		}
	}

	public List<SelectItem> getAgencies() throws ManagerBeanException {
		return getCustomerList(true);
	}

	public List<SelectItem> getCompanies() throws ManagerBeanException {
		return getCustomerList(false);
	}

	private List<SelectItem> getCustomerList(boolean agency) throws ManagerBeanException {
		List<SelectItem> customers = new LinkedList<SelectItem>();
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		if (agency) {
			criteria.addNotEqualExpression("Customer.registry.segments.segment.name", "EMPRESA");
		} else {
			criteria.addEqualExpression("Customer.registry.segments.segment.name", "EMPRESA");
		}
		criteria.addEqualExpression("Customer.registry.addInfos.attribute", "SOLRES");
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, customerBean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID));
		criteria.addOrder(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_NAME));
		for (ITransferObject ito : customerBean.getList(criteria)) {
			Customer customer = (Customer)ito;
			SelectItem customerItem = new SelectItem(customer, customer.getRegistry().getFullName());
			customers.add(customerItem);
		}
		return customers;
	}

}