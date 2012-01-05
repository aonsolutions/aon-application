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
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.dao.IPmsAlias;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class PmsCollectionsController {

	private List<SelectItem> reservationStatuses;
	private List<SelectItem> bookingHolders;

	public Hotel getHotel() {
		return null;
	}
	public void setHotel(Hotel hotel) {
	}

	public List<SelectItem> getCurrentUserHotels() throws ManagerBeanException {
		List<SelectItem> currentUserHotels = new LinkedList<SelectItem>();
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IPmsAlias.HOTEL_SCOPE_ID));
		for (ITransferObject ito : hotelBean.getList(criteria)) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			currentUserHotels.add(item);
		}
		return currentUserHotels;
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

	public List<SelectItem> getRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_CATEGORY_ID), new Integer(1));
		for (ITransferObject ito : itemBean.getList(criteria)) {
			Item item = (Item)ito;
			SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			roomItems.add(roomItem);
		}
		return roomItems;
	}

}