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
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Tariff;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class PmsCollectionsController {

	private List<SelectItem> reservationCheckStatuses;
	private List<SelectItem> reservationStatuses;
	private List<SelectItem> reservationDivertStatuses;
	private List<SelectItem> bookingHolders;

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
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		return hotelBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserHotelList() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
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

	public List<SelectItem> getCurrentUserServiceHotels() throws ManagerBeanException {
		List<SelectItem> currentUserServiceHotels = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCurrentUserServiceHotelList()) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			currentUserServiceHotels.add(item);
		}
		return currentUserServiceHotels;
	}
	
	public int getCurrentUserServiceHotelsCount() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		criteria.addNotNullExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_SERVICE_CATALOGUE_ID));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		return hotelBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserServiceHotelList() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		criteria.addNotNullExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_SERVICE_CATALOGUE_ID));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		criteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		return hotelBean.getList(criteria);
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
		if (PmsUtils.isAgencyUser()) {
			criteria.addInExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_ID), PmsUtils.getUserAgencies());
		}
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, customerBean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID));
		criteria.addOrder(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_NAME));
		for (ITransferObject ito : customerBean.getList(criteria)) {
			Customer customer = (Customer)ito;
			SelectItem customerItem = new SelectItem(customer, customer.getRegistry().getFullName());
			customers.add(customerItem);
		}
		return customers;
	}

	public List<SelectItem> getSellers() throws ManagerBeanException {
		List<SelectItem> sellers = new LinkedList<SelectItem>();
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_STATUS), SellerStatus.ACTIVE);
		criteria.addOrder(sellerBean.getFieldName(IEntityAlias.SELLER_REGISTRY_NAME));
		for (ITransferObject ito : sellerBean.getList(criteria)) {
			Seller seller = (Seller)ito;
			SelectItem sellerItem = new SelectItem(seller, seller.getRegistry().getFullName());
			sellers.add(sellerItem);
		}
		return sellers;
	}

	public List<SelectItem> getRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), IReservationConstants.ROOM_CATEGORY);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), new Integer(((ApplicationParameter)ito).getValue()));
			criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
			for (ITransferObject itr : itemBean.getList(criteria)) {
				Item item = (Item)itr;
				SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
				roomItems.add(roomItem);
			}
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

	public List<SelectItem> getDirectPayMethods() throws ManagerBeanException {
		List<PayMethodType> directPayMethods = new LinkedList<PayMethodType>();
		directPayMethods.add(PayMethodType.CASH_BASIS);
		directPayMethods.add(PayMethodType.DEBIT_CARD);
		directPayMethods.add(PayMethodType.CREDIT_CARD);
		directPayMethods.add(PayMethodType.CHEQUE);
		directPayMethods.add(PayMethodType.BANK_TRANSFER);
		return getPayMethods(directPayMethods);
	}

	public List<SelectItem> getNoCashDirectPayMethods() throws ManagerBeanException {
		List<PayMethodType> noCashDirectPayMethods = new LinkedList<PayMethodType>();
		noCashDirectPayMethods.add(PayMethodType.DEBIT_CARD);
		noCashDirectPayMethods.add(PayMethodType.CREDIT_CARD);
		noCashDirectPayMethods.add(PayMethodType.CHEQUE);
		noCashDirectPayMethods.add(PayMethodType.BANK_TRANSFER);
		return getPayMethods(noCashDirectPayMethods);
	}

	private List<SelectItem> getPayMethods(List<PayMethodType> payMethodTypes) throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		if (payMethodTypes != null && payMethodTypes.size() > 0) {
			criteria.addExpression(ExpressionUtilities.getInExpression(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), payMethodTypes));
		}
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod payMethod = (PayMethod)ito;
			SelectItem item = new SelectItem(payMethod, payMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}

}
