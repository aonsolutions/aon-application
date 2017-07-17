package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
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
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.customer.Customer;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class RoomBookingController extends DataScrollerState implements ICollectionProvider, ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel[] hotels;
	private Item[] items;
	private Customer[] agencies;
	private Date fromDate;
	private Date toDate;
	private boolean showCancelled;

	private List<DayBooking> bookingList;

	public Hotel[] getHotels() {
		return hotels;
	}
	public void setHotels(Hotel[] hotels) {
		this.hotels = hotels;
	}
	public String getHotelNames() {
		String hotelNames = "";
		for (Hotel hotel : getHotels()) {
			hotelNames += hotel.getWorkPlace().getDescription() + "; ";
		}
		return StringUtils.removeEnd(hotelNames, "; ");
	}

	public Item[] getItems() {
		return items;
	}
	public void setItems(Item[] items) {
		this.items = items;
	}
	public String getItemNames() {
		String itemNames = "";
		for (Item item : getItems()) {
			itemNames += item.getFullName() + "; ";
		}
		return StringUtils.removeEnd(itemNames, "; ");
	}

	public Customer[] getAgencies() {
		return agencies;
	}
	public void setAgencies(Customer[] agencies) {
		this.agencies = agencies;
	}
	public String getAgencyNames() {
		String agencyNames = "";
		for (Customer agency : getAgencies()) {
			agencyNames += agency.getRegistry().getFullName() + "; ";
		}
		return StringUtils.removeEnd(agencyNames, "; ");
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

	public boolean isShowCancelled() {
		return showCancelled;
	}
	public void setShowCancelled(boolean showCancelled) {
		this.showCancelled = showCancelled;
	}

	public List<DayBooking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<DayBooking> bookingList) {
		this.bookingList = bookingList;
	}

	public void onInit(ActionEvent event) {
		setHotels(null);
		setItems(null);
		setAgencies(null);
		setFromDate(new Date());
		setToDate(DateUtils.addWeeks(new Date(), 2));
		setShowCancelled(false);
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getCurrentUserHotelRoomItems();
	}

	public void onSearch(ActionEvent event) {
		try {
			buildBookingList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getBookingList()));
	}
	
	private void buildBookingList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement bookingStmt = null;
		ResultSet bookingRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			initializeBookingList(connection);

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
							dayBooking.setRoomBlocked(dayBooking.getRoomBlocked() + rooms);
						}
					}
				}
			}

			if (showCancelled) {
				includeCancelledRooms(connection);
			}
		} catch (ManagerBeanException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeBookingList(Connection connection) throws AonSQLException {
		setBookingList(new LinkedList<DayBooking>());

		PreparedStatement totalStmt = null;
		ResultSet totalRs = null;
		try {
			totalStmt = connection.prepareStatement(getRoomTotalSQL());
			totalRs = totalStmt.executeQuery();
			while (totalRs.next()) {
				String hotel = totalRs.getString(HOTEL);
				int rooms = totalRs.getInt(ROOMS);
				for (Date date=DateUtils.truncate(getFromDate(), Calendar.DATE); !date.after(getToDate()); date=DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					dayBooking.setRoomTotal(rooms);
					getBookingList().add(dayBooking);
				}
			}
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(totalRs);
			SQLUtils.closeQuietly(totalStmt);
		}
	}

	private void includeCancelledRooms(Connection connection) throws AonSQLException {
		PreparedStatement cancelledStmt = null;
		ResultSet cancelledRs = null;
		try {
			cancelledStmt = connection.prepareStatement(getRoomCancelledSQL());
			SQLUtils.setDate(cancelledStmt, 1, getFromDate());
			SQLUtils.setDate(cancelledStmt, 2, getToDate());
			cancelledRs = cancelledStmt.executeQuery();
			while (cancelledRs.next()) {
				String hotel = cancelledRs.getString(HOTEL);
				Date startDate = cancelledRs.getDate(START_DATE);
				startDate = startDate.before(getFromDate()) ? DateUtils.truncate(getFromDate(), Calendar.DATE) : startDate;
				Date endDate = cancelledRs.getDate(END_DATE);
				endDate = endDate.after(getToDate()) ? DateUtils.truncate(getToDate(), Calendar.DATE) : DateUtils.addDays(endDate, -1);
				int rooms = cancelledRs.getInt(ROOMS);
				for (Date date=DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date=DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						dayBooking.setRoomCancelled(dayBooking.getRoomCancelled() + rooms);
					}
				}
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(cancelledRs);
			SQLUtils.closeQuietly(cancelledStmt);
		}
	}

	private String getRoomTotalSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM room AS R, hotel as H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		if (ArrayUtils.isNotEmpty(getItems())) {
			stmt.append(" AND R.item IN (" + getItemIds() + ")");
		}
		stmt.append(" GROUP BY W.description");
		stmt.append(" ORDER BY " + HOTEL);

		return stmt.toString();
	}

	private String getRoomBookingSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", B.stay_date AS " + STAY_DATE + ", B.stay_type AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS + ", SUM(B.guests) AS " + GUESTS);
		stmt.append(" FROM booking AS B, hotel AS H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		if (ArrayUtils.isNotEmpty(getItems())) {
			stmt.append(" AND B.item IN (" + getItemIds() + ")");
		}
		if (ArrayUtils.isNotEmpty(getAgencies())) {
			stmt.append(" AND B.agency IN (" + getAgencyIds() + ")");
		}
		stmt.append(" GROUP BY W.description, B.stay_date, B.stay_type");
		stmt.append(" UNION ");
		stmt.append("SELECT W.description AS " + HOTEL + ", AA.date AS " + STAY_DATE + ", 10 AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS + ", 0 AS " + GUESTS);
		stmt.append(" FROM asset_activity AS AA, room AS R, hotel as H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND AA.asset = R.asset");
		stmt.append(" AND AA.status <> " + ActivityStatus.BUSY.getValue());
		stmt.append(" AND AA.date BETWEEN ? AND ?");
		if (ArrayUtils.isNotEmpty(getItems())) {
			stmt.append(" AND R.item IN (" + getItemIds() + ")");
		}
		stmt.append(" GROUP BY W.description, AA.date");
		stmt.append(" ORDER BY " + HOTEL + "," + STAY_DATE + "," + STAY_TYPE);

		return stmt.toString();
	}

	private String getRoomCancelledSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE);
		stmt.append(", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM project_reservation AS PR, project_reservation_room AS PRR, hotel AS H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append(" AND PR.status = " + ReservationStatus.CANCELLED.ordinal());
		stmt.append(" AND PR.project = PRR.project_reservation");
		stmt.append(" AND PR.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND PR.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND PR.end_date > ?");
		stmt.append(" AND PR.start_date <= ?");
		if (ArrayUtils.isNotEmpty(getItems())) {
			stmt.append(" AND PRR.item IN (" + getItemIds() + ")");
		}
		if (ArrayUtils.isNotEmpty(getAgencies())) {
			stmt.append(" AND PR.agency IN (" + getAgencyIds() + ")");
		}
		stmt.append(" GROUP BY W.description, PR.start_date, PR.end_date");
		stmt.append(" ORDER BY " + HOTEL + "," + START_DATE + "," + END_DATE);

		return stmt.toString();
	}

	private String getHotelIds() throws ManagerBeanException {
		String hotelIds = "";
		if (ArrayUtils.isNotEmpty(getHotels())) {
			for (Hotel hotel : getHotels()) {
				hotelIds += hotel.getId() + ",";
			}
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			hotelIds = StringUtils.join(collectionsController.getCurrentUserHotelIds(), ",");
		}
		return StringUtils.removeEnd(hotelIds, ",");
	}

	private String getItemIds() throws ManagerBeanException {
		String itemIds = "";
		if (ArrayUtils.isNotEmpty(getItems())) {
			for (Item item : getItems()) {
				itemIds += item.getId() + ",";
			}
		}
		return StringUtils.removeEnd(itemIds, ",");
	}

	private String getAgencyIds() throws ManagerBeanException {
		String agencyIds = "";
		if (ArrayUtils.isNotEmpty(getAgencies())) {
			for (Customer agency : getAgencies()) {
				agencyIds += agency.getId() + ",";
			}
		}
		return StringUtils.removeEnd(agencyIds, ",");
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getBookingList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}

	/***************** DAY BOOKING *********************************/

	public static class DayBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String hotel;
		private Date date;
		private Integer roomCheckin;
		private Integer roomCheckout;
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomCancelled;
		private Integer roomTotal;
		private Integer guestCheckin;
		private Integer guestCheckout;
		private Integer guestTotal;

		public DayBooking() {
			roomCheckin = 0;
			roomCheckout = 0;
			roomBusy = 0;
			roomBlocked = 0;
			roomCancelled = 0;
			roomTotal = 0;
			guestCheckin = 0;
			guestCheckout = 0;
			guestTotal = 0;
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

		public Integer getRoomCancelled() {
			return roomCancelled;
		}
		public void setRoomCancelled(Integer roomCancelled) {
			this.roomCancelled = roomCancelled;
		}

		public Integer getRoomTotal() {
			return roomTotal;
		}
		public void setRoomTotal(Integer roomTotal) {
			this.roomTotal = roomTotal;
		}

		public Integer getRoomBusyPercent() {
			return (roomTotal > 0) ? roomBusy * 100 / roomTotal : 0;
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
