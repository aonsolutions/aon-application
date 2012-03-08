package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class BookingListController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookingListController.class.getName());
	
	private Hotel hotel;
	private Date fromDate;
	private Date toDate;
	
	private List<Booking> bookingList;
	private DataModel model;
	
	
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
	public List<Booking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<Booking> bookingList) {
		this.bookingList = bookingList;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	
	private void buildBookingList() throws ManagerBeanException {
		
		setBookingList(new LinkedList<BookingListController.Booking>());

		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.setTime(getFromDate());
		toCal.setTime(getToDate());
		
		Booking booking = null;
		while(fromCal.before(toCal)){
			
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoom.class);
			Criteria criteria = new Criteria();
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_START_DATE), fromCal.getTime());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_END_DATE), fromCal.getTime());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
			
			booking = new Booking();
			booking.setDate(fromCal.getTime());
			List<ITransferObject> roomList = bean.getList(criteria);
			
			for(ITransferObject to: roomList){
				ProjectReservationRoom room = (ProjectReservationRoom) to;
				if(room.getProjectReservation().getStartDate().equals(fromCal.getTime())){
					booking.setRoomCheckin(booking.getRoomCheckin()+1);
					booking.setRoomBusy(booking.getRoomBusy()+1);
					booking.setGuestTotal(booking.getGuestTotal()+room.getAdults()+room.getChildren());
					booking.setGuestCheckin(booking.getGuestCheckin()+room.getAdults()+room.getChildren());
				} else if(room.getProjectReservation().getEndDate().equals(fromCal.getTime())){
					booking.setRoomCheckout(booking.getRoomCheckout()+1);
					booking.setGuestCheckout(booking.getGuestCheckout()+room.getAdults()+room.getChildren());
				} else {
					booking.setRoomBusy(booking.getRoomBusy()+1);
					booking.setGuestTotal(booking.getGuestTotal()+room.getAdults()+room.getChildren());
				}
				
			}

			bean = BeanManager.getManagerBean(Room.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			booking.setRoomTotal(bean.getCount(criteria));
			
			getBookingList().add(booking);
			fromCal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	public void onInit(ActionEvent event) {
		setFromDate(new Date());
		setToDate(new Date());
	}
	
	public void onSearch(ActionEvent event) {
		try {
			buildBookingList();
		} catch (ManagerBeanException e) {
			String msg = "Error al construir el listado de booking";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
		setModel(new ListDataModel(getBookingList()));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getBookingList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	/**************************************************/
	/**************************************************/
	
	public class Booking {
		private Date date;
		private Integer roomCheckin;
		private Integer roomCheckout;
		private Integer roomBusy;
		private Integer roomTotal;
		private Integer roomBlocked;
		private Integer roomFree;
		private Integer guestCheckin;
		private Integer guestCheckout;
		private Integer guestTotal;
		
		public Booking (){
			roomCheckin=0;
			roomCheckout=0;
			roomBusy=0;
			roomTotal=0;
			roomBlocked=0;
			roomFree=0;
			guestCheckin=0;
			guestCheckout=0;
			guestTotal=0;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Integer getRoomCheckin() {
			return roomCheckin;
		}
		public void setRoomCheckin(Integer roomCheckin) {
			this.roomCheckin = roomCheckin;
		}
		public Integer getRoomCheckout() {
			return roomCheckout;
		}
		public void setRoomCheckout(Integer roomCheckout) {
			this.roomCheckout = roomCheckout;
		}
		public Integer getRoomBusy() {
			return roomBusy;
		}
		public void setRoomBusy(Integer roomBusy) {
			this.roomBusy = roomBusy;
		}
		public Integer getRoomTotal() {
			return roomTotal;
		}
		public void setRoomTotal(Integer roomTotal) {
			this.roomTotal = roomTotal;
		}
		public Integer getRoomBlocked() {
			return roomBlocked;
		}
		public void setRoomBlocked(Integer roomBlocked) {
			this.roomBlocked = roomBlocked;
		}
		public Integer getRoomFree() {
			return roomFree;
		}
		public void setRoomFree(Integer roomFree) {
			this.roomFree = roomFree;
		}
		public Integer getGuestCheckin() {
			return guestCheckin;
		}
		public void setGuestCheckin(Integer guestCheckin) {
			this.guestCheckin = guestCheckin;
		}
		public Integer getGuestCheckout() {
			return guestCheckout;
		}
		public void setGuestCheckout(Integer guestCheckout) {
			this.guestCheckout = guestCheckout;
		}
		public Integer getGuestTotal() {
			return guestTotal;
		}
		public void setGuestTotal(Integer guestTotal) {
			this.guestTotal = guestTotal;
		}
	}
	
}
