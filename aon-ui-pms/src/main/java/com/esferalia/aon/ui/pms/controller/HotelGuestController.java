package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsReportManager;


public class HotelGuestController extends DataScrollerState implements ICollectionProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel hotel;
	private Date date;
	private List<HotelGuest> hotelGuestList;	
	
	public List<HotelGuest> getHotelGuestList() {
		return hotelGuestList;
	}
	public void setHotelGuestList(List<HotelGuest> hotelGuestList) {
		this.hotelGuestList = hotelGuestList;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setHotel(null);
		setDate(new Date());
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		String select = PmsReportManager.getInstance().getHotelGuestsSQL(getHotel());
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		query.setDate("date", new java.sql.Date(getDate().getTime()));
		List<HotelGuest> list = new LinkedList<HotelGuest>();
		for(Object o: query.list()){
			HotelGuest guest = new HotelGuest();
			guest.setHotelName((String) (((Object[])o)[0]));
			guest.setRoomNumber((String) (((Object[])o)[1]));
			guest.setGuestName((String) (((Object[])o)[2]));
			guest.setGuestCount((Integer) (((Object[])o)[3]));
			guest.setDocument((String) (((Object[])o)[4]));
			guest.setTelephone((String) (((Object[])o)[5])); 
			guest.setEmail((String) (((Object[])o)[6]));
			guest.setReservationId((String) (((Object[])o)[7]).toString()); 
			guest.setStartDate((Date) (((Object[])o)[8]));
			guest.setEndDate((Date) (((Object[])o)[9]));
			list.add(guest);
		}
		setHotelGuestList(list);
		setModel(new SerializableListDataModel(getHotelGuestList()));
	}
	
	
	/**************************************************/
	/**************************************************/
	
	public static class HotelGuest implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String hotelName;
		private String roomNumber; 
		private String guestName;  
		private Integer guestCount;
		private String document; 
		private String telephone; 
		private String email;
		private String reservationId; 
		private Date startDate; 
		private Date endDate;
		public String getHotelName() {
			return hotelName;
		}
		public void setHotelName(String hotelName) {
			this.hotelName = hotelName;
		}
		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}
		public String getGuestName() {
			return guestName;
		}
		public void setGuestName(String guestName) {
			this.guestName = guestName;
		}
		public Integer getGuestCount() {
			return guestCount;
		}
		public void setGuestCount(Integer guestCount) {
			this.guestCount = guestCount;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getReservationId() {
			return reservationId;
		}
		public void setReservationId(String reservationId) {
			this.reservationId = reservationId;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getHotelGuestList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
}
