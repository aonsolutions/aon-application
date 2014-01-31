package com.esferalia.aon.ui.pms.controller;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class RoomBookingController implements ICollectionProvider, ISQLConstants {

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
		if (toDate.before(fromDate)) {
			toDate = fromDate;
		}
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

	public void onInit(ActionEvent event) {
		setItem(null);
		setAgency(null);
		setFromDate(new Date());
		setToDate(new Date());
	}
	
	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(getHotel());
	}

	public void onSearch(ActionEvent event) {
		try {
			buildBookingList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new ListDataModel(getBookingList()));
	}
	
	private void buildBookingList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement bookingStmt = null;
		ResultSet bookingRs = null;
		int hotelRooms = 0;
		try {
			initializeBookingList();

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			bookingStmt = connection.prepareStatement(getRoomBookingSQL());
			SQLUtils.setDate(bookingStmt, 1, getFromDate());
			SQLUtils.setDate(bookingStmt, 2, getToDate());
			SQLUtils.setDate(bookingStmt, 3, getFromDate());
			SQLUtils.setDate(bookingStmt, 4, getToDate());
			bookingRs = bookingStmt.executeQuery();
			while (bookingRs.next()) {
				String hotel = bookingRs.getString(HOTEL);
				Date date = bookingRs.getDate(STAY_DATE);
				int type = bookingRs.getInt(STAY_TYPE);
				int rooms = bookingRs.getInt(ROOMS);
				int guests = bookingRs.getObject(GUESTS) != null ? bookingRs.getInt(GUESTS) : 0;
				if (date != null) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						if (type == BookingStayType.CHECKIN.ordinal()) {
							dayBooking.setRoomCheckin(rooms);
							dayBooking.setGuestCheckin(guests);
							dayBooking.setRoomBusy(dayBooking.getRoomBusy() + rooms);
							dayBooking.setGuestTotal(dayBooking.getGuestTotal() + guests);
						} else if (type == BookingStayType.CHECKOUT.ordinal()) {
							dayBooking.setRoomCheckout(rooms);
							dayBooking.setGuestCheckout(guests);
						} else if (type == BookingStayType.STAY.ordinal()) {
							dayBooking.setRoomBusy(dayBooking.getRoomBusy() + rooms);
							dayBooking.setGuestTotal(dayBooking.getGuestTotal() + guests);
						} else {
							dayBooking.setRoomBlocked(rooms);
						}
						dayBooking.setRoomTotal(hotelRooms);
					}
				} else {
					hotelRooms = rooms;
				}
			}
		} catch (ManagerBeanException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeBookingList() throws ManagerBeanException {
		setBookingList(new LinkedList<RoomBookingController.DayBooking>());
		if (getHotel() != null && getHotel().getId()!=null) {
			for (Date date=DateUtils.truncate(getFromDate(), Calendar.DATE); !date.after(getToDate()); date=DateUtils.addDays(date, 1)) {
				DayBooking dayBooking = new DayBooking();
				dayBooking.setHotel(getHotel().getWorkPlace().getDescription());
				dayBooking.setDate(date);
				getBookingList().add(dayBooking);
			}
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			for (ITransferObject ito: collectionsController.getCurrentUserHotelList()) {
				for (Date date=DateUtils.truncate(getFromDate(), Calendar.DATE); !date.after(getToDate()); date=DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(((Hotel)ito).getWorkPlace().getDescription());
					dayBooking.setDate(date);
					getBookingList().add(dayBooking);
				}
			}
		}
	}

	private String getRoomBookingSQL() throws ManagerBeanException {
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT W.description AS " + HOTEL + ", B.stay_date AS " + STAY_DATE + ", B.stay_type AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS + ", SUM(B.guests) AS " + GUESTS);
		stmt.append(" FROM booking AS B, hotel AS H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = H.id");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		if (getAgency() != null && getAgency().getId() != null) {
			stmt.append(" AND B.agency = " + getAgency().getId());
		}
		if (getItem() != null && getItem().getId() != null) {
			stmt.append(" AND B.item = " + getItem().getId());
		}
		stmt.append(" GROUP BY W.description, B.stay_date, B.stay_type");
		stmt.append(" UNION ");
		stmt.append("SELECT W.description AS " + HOTEL + ", AA.date AS " + STAY_DATE + ", 10 AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS + ", 0 AS " + GUESTS);
		stmt.append(" FROM asset_activity AS AA, room AS R, hotel as H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND AA.asset = R.asset");
		stmt.append(" AND AA.status <> " + ActivityStatus.BUSY.getValue());
		stmt.append(" AND AA.date BETWEEN ? AND ?");
		if (getItem() != null && getItem().getId() != null) {
			stmt.append(" AND R.item = " + getItem().getId());
		}
		stmt.append(" GROUP BY W.description, AA.date");
		stmt.append(" UNION ");
		stmt.append("SELECT W.description AS " + HOTEL + ", NULL AS " + STAY_DATE + ", 20 AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS + ", 0 AS " + GUESTS);
		stmt.append(" FROM room AS R, hotel as H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		if (getItem() != null && getItem().getId() != null) {
			stmt.append(" AND R.item = " + getItem().getId());
		}
		stmt.append(" GROUP BY W.description");
		stmt.append(" ORDER BY " + HOTEL + "," + STAY_DATE + "," + STAY_TYPE);

		return stmt.toString();
	}

	private String getHotelIds() throws ManagerBeanException {
		String hotelIds = "";
		if (getHotel() != null) {
			hotelIds = getHotel().getId().toString();
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			hotelIds = StringUtils.join(collectionsController.getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getBookingList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	/***************** DAY BOOKING *********************************/

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

		public DayBooking() {
			roomCheckin=0;
			roomCheckout=0;
			roomBusy=0;
			roomBlocked=0;
			roomTotal=0;
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

		public Integer getRoomBlocked() {
			return roomBlocked;
		}
		public void setRoomBlocked(Integer roomBlocked) {
			this.roomBlocked = roomBlocked;
		}

		public Integer getRoomTotal() {
			return roomTotal;
		}
		public void setRoomTotal(Integer roomTotal) {
			this.roomTotal = roomTotal;
		}

		public Integer getRoomFree() {
			return roomTotal - roomBusy - roomBlocked;
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

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final DayBooking o = (DayBooking)obj;
			return o.getHotel().equals(getHotel()) && o.getDate().equals(getDate());
		}

	}

}
