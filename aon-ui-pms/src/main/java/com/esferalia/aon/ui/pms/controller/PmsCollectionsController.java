package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.enumeration.Shift;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class PmsCollectionsController {

	private List<SelectItem> reservationCheckStatuses;
	private List<SelectItem> reservationStatuses;
	private List<SelectItem> reservationDivertStatuses;
	private List<SelectItem> bookingHolders;
	private List<SelectItem> shifts;

	public Hotel getHotel() {
		return null;
	}
	public void setHotel(Hotel hotel) {
	}

	public List<SelectItem> getCurrentUserHotels() throws ManagerBeanException {
		List<SelectItem> currentUserHotels = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCurrentUserHotelList()) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			currentUserHotels.add(item);
		}
		return currentUserHotels;
	}
	
	public int getCurrentUserHotelsCount() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), new Boolean(true));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		return hotelBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserHotelList() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), new Boolean(true));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		criteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		return hotelBean.getList(criteria);
	}
	
	public List<Integer> getCurrentUserHotelIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for (ITransferObject ito: getCurrentUserHotelList()) {
			Hotel hotel = (Hotel)ito;
			list.add(hotel.getId());
		}
		return list;
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
			criteria.addNotEqualExpression("Customer.registry.segments.segment.name", IReservationConstants.COMPANY);
		} else {
			criteria.addEqualExpression("Customer.registry.segments.segment.name", IReservationConstants.COMPANY);
		}
		criteria.addEqualExpression("Customer.registry.addInfos.attribute", IReservationConstants.SOLRES.toUpperCase());
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, customerBean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID));
		criteria.addOrder(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_NAME));
		for (ITransferObject ito : customerBean.getList(criteria)) {
			Customer customer = (Customer)ito;
			SelectItem customerItem = new SelectItem(customer, customer.getRegistry().getFullName());
			customers.add(customerItem);
		}
		return customers;
	}

	public List<SelectItem> getRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), new Integer(1));
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
		for (ITransferObject ito : itemBean.getList(criteria)) {
			Item item = (Item)ito;
			SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			roomItems.add(roomItem);
		}
		return roomItems;
	}

	public List<SelectItem> getTariffs() throws ManagerBeanException {
		List<SelectItem> tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(tariffBean.getFieldName(IEntityAlias.TARIFF_NAME));
		for (ITransferObject ito : tariffBean.getList(criteria)) {
			Tariff tariff = (Tariff)ito;
			SelectItem item = new SelectItem(tariff, tariff.getCode() + " - " + tariff.getName());
			tariffs.add(item);
		}
		return tariffs;
	}
	
	public List<SelectItem> getReservationCheckStatuses() {
		if (reservationCheckStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationCheckStatuses = new LinkedList<SelectItem>();
			for (ReservationCheckStatus status : ReservationCheckStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				reservationCheckStatuses.add(item);
			}
		}
		return reservationCheckStatuses;
	}
	
	public List<SelectItem> getAbbreviatedReservationCheckStatuses() {
		if (reservationCheckStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationCheckStatuses = new LinkedList<SelectItem>();
			for (ReservationCheckStatus status : ReservationCheckStatus.values()) {
				String name = status.getName(locale);
				if (status == ReservationCheckStatus.NO_CHECK) {
					name = "No";
				} else if (status == ReservationCheckStatus.CHECK_IN) {
					name = "In";
				} else if (status == ReservationCheckStatus.CHECK_OUT) {
					name = "Out";
				} 
				SelectItem item = new SelectItem(status, name);
				reservationCheckStatuses.add(item);
			}
		}
		return reservationCheckStatuses;
	}
	
	public List<SelectItem> getReservationStatuses() {
		if (reservationStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationStatuses = new LinkedList<SelectItem>();
			for (ReservationStatus status : ReservationStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				reservationStatuses.add(item);
			}
		}
		return reservationStatuses;
	}
	
	public List<SelectItem> getReservationDivertStatuses() {
		if (reservationDivertStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationDivertStatuses = new LinkedList<SelectItem>();
			for (ReservationDivertStatus status : ReservationDivertStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				reservationDivertStatuses.add(item);
			}
		}
		return reservationDivertStatuses;
	}

	public List<SelectItem> getBookingHolders() {
		if (bookingHolders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			bookingHolders = new LinkedList<SelectItem>();
			for (BookingHolder holder : BookingHolder.values()) {
				String name = holder.getName(locale);
				SelectItem item = new SelectItem(holder, name);
				bookingHolders.add(item);
			}
		}
		return bookingHolders;
	}

	public List<SelectItem> getShifts() {
		if (shifts == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			shifts = new LinkedList<SelectItem>();
			for (Shift shift : Shift.values()) {
				String name = shift.getName(locale);
				SelectItem item = new SelectItem(shift, name);
				shifts.add(item);
			}
		}
		return shifts;
	}
	
}
