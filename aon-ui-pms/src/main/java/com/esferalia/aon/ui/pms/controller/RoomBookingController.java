package com.esferalia.aon.ui.pms.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.util.PmsReportManager;

public class RoomBookingController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomBookingController.class.getName());
	
	private Hotel hotel;
	private Item item;
	private Customer agency;
	private Date fromDate;
	private Date toDate;
	
	private List<DayBooking> bookingList;
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
	public List<DayBooking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<DayBooking> bookingList) {
		this.bookingList = bookingList;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	@SuppressWarnings("rawtypes")
	private void buildBookingList() throws ManagerBeanException {
		PmsReportManager reportManager = PmsReportManager.getInstance();
		
		buildEmptyList(getHotel());
		
		String checkinSelect = reportManager.getRoomBookingCheckInSQL(getHotel(), getAgency(), getItem());
		String checkoutSelect = reportManager.getRoomBookingCheckOutSQL(getHotel(), getAgency(), getItem());
		String occupationSelect = reportManager.getRoomBookingFirstDayOccupationSQL(getHotel(), getAgency(), getItem());
		String roomsSelect = reportManager.getHotelRoomsSQL(getHotel(), getItem());
		String blockedRoomsSelect = reportManager.getBlockedRoomsSQL(getHotel(), getItem());

		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());

		Query checkinQuery = session.createSQLQuery(checkinSelect);
		checkinQuery.setDate("start", new java.sql.Date(getFromDate().getTime()));
		checkinQuery.setDate("end", new java.sql.Date(getToDate().getTime()));

		Query checkoutQuery = session.createSQLQuery(checkoutSelect);
		checkoutQuery.setDate("start", new java.sql.Date(getFromDate().getTime()));
		checkoutQuery.setDate("end", new java.sql.Date(getToDate().getTime()));
		
		Query occupationQuery = session.createSQLQuery(occupationSelect);
		occupationQuery.setDate("start", new java.sql.Date(getFromDate().getTime()));

		Query roomsQuery = session.createSQLQuery(roomsSelect);
		
		Query blockedRoomsQuery = session.createSQLQuery(blockedRoomsSelect);
		blockedRoomsQuery.setDate("start", new java.sql.Date(getFromDate().getTime()));
		blockedRoomsQuery.setDate("end", new java.sql.Date(getToDate().getTime()));

		Iterator checkinIterator = checkinQuery.list().iterator();
		Iterator checkoutIterator = checkoutQuery.list().iterator();
		Iterator occupationIterator = occupationQuery.list().iterator();
		Iterator roomsIterator = roomsQuery.list().iterator();
		Iterator blockedRoomsIterator = blockedRoomsQuery.list().iterator();
		
		Object checkin = checkinIterator.hasNext()?checkinIterator.next():null;
		Object checkout = checkoutIterator.hasNext()?checkoutIterator.next():null;
		Object occupation = occupationIterator.hasNext()?occupationIterator.next():null;
		Object rooms = roomsIterator.hasNext()?roomsIterator.next():null;
		Object blockedRooms = blockedRoomsIterator.hasNext()?blockedRoomsIterator.next():null;
		
		int roomBusy = 0;
		int guestTotal = 0;
		
		for(DayBooking booking: getBookingList() ){
			String checkinHotel = checkin!=null?(String) (((Object[])checkin)[0]):null;
			String checkoutHotel = checkout!=null?(String) (((Object[])checkout)[0]):null;
			Date checkinDate = checkin!=null?(Date) (((Object[])checkin)[1]):null;
			Date checkoutDate = checkout!=null?(Date) (((Object[])checkout)[1]):null;
			// Se cargan las entradas de habitaciones y de huespedes
			while ( booking.getDate().equals(checkinDate) && booking.getHotel().equals(checkinHotel) ){
				if(checkin!=null){
					booking.setRoomCheckin(booking.getRoomCheckin() + ((BigInteger) (((Object[])checkin)[2])).intValue() );
					booking.setGuestCheckin(booking.getGuestCheckin() + ((BigDecimal) (((Object[])checkin)[3])).intValue() );
					checkin = checkinIterator.hasNext()?checkinIterator.next():null;
					checkinDate = checkin!=null?(Date) (((Object[])checkin)[1]):null;
					checkinHotel = checkin!=null?(String) (((Object[])checkin)[0]):null;
				}
			}
			// Se cargan las salidas de habitaciones y de huespedes
			while ( booking.getDate().equals(checkoutDate) && booking.getHotel().equals(checkoutHotel) ){
				if(checkout!=null){
					booking.setRoomCheckout(booking.getRoomCheckout() + ((BigInteger) (((Object[])checkout)[2])).intValue() );
					booking.setGuestCheckout(booking.getGuestCheckout() + ((BigDecimal) (((Object[])checkout)[3])).intValue() );
					checkout = checkoutIterator.hasNext()?checkoutIterator.next():null;
					checkoutDate = checkout!=null?(Date) (((Object[])checkout)[1]):null;
					checkoutHotel = checkout!=null?(String) (((Object[])checkout)[0]):null;
				}
			}
			// Se cargan la ocupacion de habitaciones y el total de huespedes
			String occupationHotel = occupation!=null?((String) ((Object[])occupation)[0]):null;
			if( booking.getDate().equals(getFromDate()) ){
				if( !booking.getHotel().equals(occupationHotel) ){
					booking.setRoomBusy( 0 );
					booking.setGuestTotal( 0 );
					roomBusy = 0;
					guestTotal = 0;
				} else {
					booking.setRoomBusy( ((BigInteger) (((Object[])occupation)[1])).intValue() );
					roomBusy = ((BigInteger) (((Object[])occupation)[1])).intValue();
					booking.setGuestTotal( ((BigDecimal) (((Object[])occupation)[2])).intValue() );
					guestTotal = ((BigDecimal) (((Object[])occupation)[2])).intValue();
					occupation = occupationIterator.hasNext()?occupationIterator.next():null;
				}
			} else {
				roomBusy += (booking.getRoomCheckin() - booking.getRoomCheckout());
				booking.setRoomBusy( roomBusy );
				guestTotal += (booking.getGuestCheckin() - booking.getGuestCheckout());
				booking.setGuestTotal( guestTotal );
			}
			// Se cargan las habitaciones bloqueadas
			String blockedRoomHotel = blockedRooms!=null?((String) ((Object[])blockedRooms)[0]):null;
			Date blockedRoomDate = blockedRooms!=null?((Date) ((Object[])blockedRooms)[1]):null;
			if( booking.getDate().equals(blockedRoomDate) && booking.getHotel().equals(blockedRoomHotel) ){
				if(blockedRooms!=null){
					booking.setRoomBlocked( ((BigInteger) (((Object[])blockedRooms)[2])).intValue() );
				}
				blockedRooms = blockedRoomsIterator.hasNext()?blockedRoomsIterator.next():null;
			}
			// Se carga el total de habitaciones
			String roomHotel = rooms!=null?((String) ((Object[])rooms)[0]):null;
			if( !booking.getHotel().equals(roomHotel) ){
				rooms = roomsIterator.hasNext()?roomsIterator.next():null;
			}
			if(rooms!=null){
				booking.setRoomTotal( ((BigInteger) (((Object[])rooms)[1])).intValue() );
			}
		}
	}
	
	private void buildEmptyList(Hotel hotel2) throws ManagerBeanException{
		setBookingList(new LinkedList<RoomBookingController.DayBooking>());
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		if( getHotel() != null && getHotel().getId()!=null ){
			fromCal.setTime(getFromDate());
			toCal.setTime(getToDate());
			while(fromCal.before(toCal) || fromCal.equals(toCal)){
				DayBooking b = new DayBooking();
				b.setHotel(getHotel().getWorkPlace().getDescription());
				b.setDate(fromCal.getTime());
				getBookingList().add(b);
				fromCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else {
			PmsCollectionsController collections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			for(ITransferObject to: collections.getCurrentUserHotelList() ){
				Hotel hotel = (Hotel) to;
				fromCal.setTime(getFromDate());
				toCal.setTime(getToDate());
				while(fromCal.before(toCal) || fromCal.equals(toCal)){
					DayBooking b = new DayBooking();
					b.setHotel(hotel.getWorkPlace().getDescription());
					b.setDate(fromCal.getTime());
					getBookingList().add(b);
					fromCal.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
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
	
	public class DayBooking {
		private String hotel;
		private Date date;
		private Integer roomCheckin;
		private Integer roomCheckout;
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomTotal;
		private Integer guestCheckin;
		private Integer guestCheckout;
		private Integer guestTotal;
		
		public DayBooking (){
			roomCheckin=0;
			roomCheckout=0;
			roomBusy=0;
			roomTotal=0;
			roomBlocked=0;
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
		public Integer getRoomFree() {
			
			return roomTotal - roomBusy - roomBlocked;
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
