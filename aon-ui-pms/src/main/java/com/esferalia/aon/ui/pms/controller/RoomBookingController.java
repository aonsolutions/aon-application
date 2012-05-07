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
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class RoomBookingController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomBookingController.class.getName());
	
	private Hotel hotel;
	private Item item;
	private Customer agency;
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
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
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
	
	private List<ITransferObject> getSelectedHotels() throws ManagerBeanException{
		if(getHotel()!=null && getHotel().getId()!=null){
			List<ITransferObject> list = new LinkedList<ITransferObject>();
			list.add(getHotel());
			return list;
		} else {
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria hotelCriteria = new Criteria();
			hotelCriteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), new Boolean(true));
			UserUtils.getInstance().addScopeFilterToCriteria(hotelCriteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
			hotelCriteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
			return hotelBean.getList(hotelCriteria);
		}
	}
	
	private void buildBookingList() throws ManagerBeanException {
		
		setBookingList(new LinkedList<RoomBookingController.Booking>());
			
		for(ITransferObject hto: getSelectedHotels()){
			Hotel hotel = (Hotel) hto;
			addBooking(hotel);
		}
	}
	
	private void addBooking(Hotel hotel) throws ManagerBeanException {
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.setTime(getFromDate());
		toCal.setTime(getToDate());
		
		Booking booking = null;
		while(fromCal.before(toCal) || fromCal.equals(toCal)){
			
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoom.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID), hotel.getId());
			if(getAgency()!=null && getAgency().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());
			}
			if(getItem()!=null && getItem().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ITEM_ID), getItem().getId());
			}
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_START_DATE), fromCal.getTime());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_END_DATE), fromCal.getTime());
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID));
			
			booking = new Booking();
			booking.setDate(fromCal.getTime());
			List<ITransferObject> roomList = bean.getList(criteria);
			booking.setHotel(hotel.getWorkPlace().getDescription());
			
			for(ITransferObject to: roomList){
				ProjectReservationRoom room = (ProjectReservationRoom) to;
				// para el listado de booking no se tienen en cuenta los desvios a hoteles externos  
				// ( desvio externo = reserva facturada sin habitacion asignada )
				if( !(room.getProjectReservation().getStatus()==ReservationStatus.INVOICED && room.getRoomNumber()==null)){
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
			}
			
			bean = BeanManager.getManagerBean(Room.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
			booking.setRoomTotal(bean.getCount(criteria));
			
			getBookingList().add(booking);
			fromCal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	public void onInit(ActionEvent event) {
		try {
			setItem(null);
			setAgency((Customer) BeanManager.getManagerBean(Customer.class).createNewTo());
			setFromDate(new Date());
			setToDate(new Date());
		} catch (ManagerBeanException e) {
			String msg = "Error de inicio";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
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
	
	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		if (getHotel() != null && getHotel().getId() != null) {
			return getHotelRoomItems(getHotel());
		}
		PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		return collectionsController.getRoomItems(); 
	}

	private List<SelectItem> getHotelRoomItems(Hotel hotel) throws ManagerBeanException {
		List<Integer> items = new LinkedList<Integer>();
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
		for (ITransferObject ito : roomBean.getList(criteria)) {
			Room room = (Room)ito;
			if (!items.contains(room.getItem().getId())) {
				items.add(room.getItem().getId());
			}
		}

		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		if (items.size() > 0) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getInExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), items));
			criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
			for (ITransferObject ito : itemBean.getList(criteria)) {
				Item item = (Item)ito;
				SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
				roomItems.add(roomItem);
			}
		}
		return roomItems;
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
		private String hotel;
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
		public String getHotel() {
			return hotel;
		}
		public void setHotel(String hotel) {
			this.hotel = hotel;
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
