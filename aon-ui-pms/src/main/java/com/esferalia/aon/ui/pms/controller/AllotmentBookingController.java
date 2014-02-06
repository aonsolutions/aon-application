package com.esferalia.aon.ui.pms.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class AllotmentBookingController implements ICollectionProvider, ISQLConstants {

	private Hotel hotel;
	private Customer agency;
	private InvoicingGroup agencyGroup;
	private Date fromDate;
	private Date toDate;
	private String[] agencies;

	private List<DayBooking> bookingList;
	private DataModel model;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public InvoicingGroup getAgencyGroup() {
		return agencyGroup;
	}
	public void setAgencyGroup(InvoicingGroup agencyGroup) {
		this.agencyGroup = agencyGroup;
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

	public String[] getAgencies() {
		return agencies;
	}
	public void setAgencies(String[] agencies) {
		this.agencies = agencies;
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
		setHotel(null);
		setAgency(null);
		setAgencyGroup(null);
		if (getFromDate() == null) {
			setFromDate(new Date());
		}
		if (getToDate() == null) {
			setToDate(DateUtils.addWeeks(new Date(), 2));
		}
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
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		try {
			initializeAgencyList();
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
				Date stayDate = bookingRs.getDate(STAY_DATE);
				int type = bookingRs.getInt(STAY_TYPE);
				int rooms = bookingRs.getInt(ROOMS);
				if (stayDate != null) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(stayDate);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						if (type == BookingStayType.STAY.ordinal()) {
							dayBooking.setRoomBusy(dayBooking.getRoomBusy() + rooms);
						} else {
							dayBooking.setRoomBlocked(rooms);
						}
					}
				}
			}

			allotmentStmt = connection.prepareStatement(getRoomAllotmentSQL());
			SQLUtils.setDate(allotmentStmt, 1, getFromDate());
			SQLUtils.setDate(allotmentStmt, 2, getToDate());
			SQLUtils.setDate(allotmentStmt, 3, getFromDate());
			SQLUtils.setDate(allotmentStmt, 4, getToDate());
			allotmentRs = allotmentStmt.executeQuery();
			while (allotmentRs.next()) {
				String agency = allotmentRs.getString(AGENCY);
				String hotel = allotmentRs.getString(HOTEL);
				int allotment = allotmentRs.getInt(ALLOTMENT);
				Date startDate = !allotmentRs.getDate(START_DATE).before(getFromDate()) ? allotmentRs.getDate(START_DATE) : getFromDate();
				Date endDate = !allotmentRs.getDate(END_DATE).after(getToDate()) ? allotmentRs.getDate(END_DATE) : getToDate();
				Date stayDate = allotmentRs.getDate(STAY_DATE);
				int rooms = allotmentRs.getInt(ROOMS);

				if (!ArrayUtils.contains(agencies, agency)) {
					agencies = (String[])ArrayUtils.add(agencies, agency);
				}

				if (stayDate != null) {
					startDate = DateUtils.truncate(stayDate, Calendar.DATE);
					endDate = DateUtils.truncate(stayDate, Calendar.DATE);
				}
				for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date = DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						dayBooking.setRoomAllotment(dayBooking.getRoomAllotment() + allotment);
						dayBooking.setRoomAllotmentBusy(dayBooking.getRoomAllotmentBusy() + ((allotment > rooms) ? rooms : allotment));

						DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
						if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
							dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
						}
						dayAgencyBooking.setRoomAllotment(dayAgencyBooking.getRoomAllotment() + allotment);
						dayAgencyBooking.setRoomBusy(dayAgencyBooking.getRoomBusy() + rooms);
						dayAgencyBooking.setRoomAvailable(dayAgencyBooking.getRoomAvailable() + ((allotment > rooms) ? allotment - rooms : 0));
						dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
					}
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
			SQLUtils.closeQuietly(allotmentRs);
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeAgencyList() throws AonSQLException {
		agencies = ArrayUtils.EMPTY_STRING_ARRAY;
	}

	private void initializeBookingList() throws AonSQLException {
		setBookingList(new LinkedList<DayBooking>());

		Connection connection = null;
		PreparedStatement totalStmt = null;
		ResultSet totalRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
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
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(totalRs);
			SQLUtils.closeQuietly(totalStmt);
			SQLUtils.closeQuietly(connection);
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
		stmt.append(" GROUP BY W.description");
		stmt.append(" ORDER BY " + HOTEL);

		return stmt.toString();
	}

	private String getRoomBookingSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", B.stay_date AS " + STAY_DATE + ", 2 AS " + STAY_TYPE + ", COUNT(DISTINCT B.id) AS " + ROOMS);
		stmt.append(" FROM booking AS B, hotel AS H, workplace AS W");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		stmt.append(" AND B.stay_type IN (0,2)");
		stmt.append(" GROUP BY W.description, B.stay_date");
		stmt.append(" UNION ");
		stmt.append("SELECT W.description AS " + HOTEL + ", AA.date AS " + STAY_DATE + ", 10 AS " + STAY_TYPE + ", COUNT(*) AS " + ROOMS);
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
		stmt.append(" GROUP BY W.description, AA.date");
		stmt.append(" ORDER BY " + HOTEL + "," + STAY_DATE + "," + STAY_TYPE);

		return stmt.toString();
	}

	private String getRoomAllotmentSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(IFNULL(R.alias, R.name), IG.description) AS " + AGENCY + ", W.description AS " + HOTEL + ", A.quantity AS " + ALLOTMENT);
		stmt.append(", A.start_date AS " + START_DATE + ", A.end_date AS " + END_DATE + ", B.stay_date AS " + STAY_DATE + ", COUNT(DISTINCT B.id) AS " + ROOMS);
		stmt.append(" FROM allotment AS A");
		stmt.append(" LEFT JOIN hotel AS H ON A.hotel = H.id AND H.active = 1");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN registry AS R ON A.agency = R.id");
		stmt.append(" LEFT JOIN invoicing_group AS IG ON A.agency_group = IG.id");
		stmt.append(" LEFT JOIN booking AS B ON A.hotel = B.hotel");
		stmt.append("    AND B.stay_date BETWEEN A.start_date AND A.end_date");
		stmt.append("    AND B.stay_date BETWEEN ? AND ?");
		stmt.append("    AND B.stay_type IN (0,2)");
		stmt.append("    AND ((A.agency IS NOT NULL AND B.agency = A.agency)");
		stmt.append("        OR (A.agency IS NULL AND B.agency IN (SELECT registry FROM customer WHERE invoicing_group = A.agency_group)))");
		stmt.append("    AND (0 = (SELECT COUNT(*) FROM allotment_item WHERE allotment = A.id)");
		stmt.append("        OR B.item IN (SELECT item FROM allotment_item WHERE allotment = A.id))");
		stmt.append("    AND (0 = (SELECT COUNT(*) FROM allotment_tariff WHERE allotment = A.id)");
		stmt.append("        OR B.tariff IN (SELECT tariff FROM allotment_tariff WHERE allotment = A.id))");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("A.domain"));
		stmt.append(" AND A.hotel IN (" + getHotelIds() + ")");
		if (getAgency() != null && getAgency().getId() != null) {
			stmt.append(" AND A.agency = " + getAgency().getId());
		}
		if (getAgencyGroup() != null && getAgencyGroup().getId() != null) {
			stmt.append(" AND A.agency_group = " + getAgencyGroup().getId());
		}
		stmt.append(" AND A.end_date >= ?");
		stmt.append(" AND A.start_date <= ?");
		stmt.append(" AND A.active = 1");
		stmt.append(" GROUP BY A.id, B.stay_date");
		stmt.append(" ORDER BY " + AGENCY + "," + HOTEL + "," + STAY_DATE);

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
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomTotal;
		private Integer roomAllotment;
		private Integer roomAllotmentBusy;
		private Map<String, DayAgencyBooking> agencyBookingMap;

		public DayBooking() {
			roomBusy = 0;
			roomBlocked = 0;
			roomTotal = 0;
			roomAllotment = 0;
			roomAllotmentBusy = 0;
			agencyBookingMap = new HashMap<String, DayAgencyBooking>();
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

		public Integer getRoomAllotment() {
			return roomAllotment;
		}
		public void setRoomAllotment(Integer roomAllotment) {
			this.roomAllotment = roomAllotment;
		}

		public Integer getRoomAllotmentBusy() {
			return roomAllotmentBusy;
		}
		public void setRoomAllotmentBusy(Integer roomAllotmentBusy) {
			this.roomAllotmentBusy = roomAllotmentBusy;
		}

		public Integer getRoomFree() {
			return roomTotal - roomBusy - roomBlocked;
		}

		public Integer getRoomAvailable() {
			return (roomAllotment - roomAllotmentBusy) > 0 ? roomAllotment - roomAllotmentBusy : 0;
		}

		public Integer getRoomBusyPotential() {
			return roomBusy + getRoomAvailable();
		}

		public Integer getRoomFreePotential() {
			return getRoomFree() - getRoomAvailable();
		}

		public Map<String, DayAgencyBooking> getAgencyBookingMap() {
			return agencyBookingMap;
		}

		public void setAgencyBookingMap(Map<String, DayAgencyBooking> agencyBookingMap) {
			this.agencyBookingMap = agencyBookingMap;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final DayBooking o = (DayBooking)obj;
			return o.getHotel().equals(getHotel()) && o.getDate().equals(getDate());
		}

	}

	/***************** DAY AGENCY BOOKING *********************************/

	public class DayAgencyBooking {
		private Integer roomAllotment;
		private Integer roomBusy;
		private Integer roomAvailable;

		public DayAgencyBooking() {
			roomAllotment = 0;
			roomBusy = 0;
			roomAvailable = 0;
		}

		public Integer getRoomAllotment() {
			return roomAllotment;
		}
		public void setRoomAllotment(Integer roomAllotment) {
			this.roomAllotment = roomAllotment;
		}

		public Integer getRoomBusy() {
			return roomBusy;
		}
		public void setRoomBusy(Integer roomBusy) {
			this.roomBusy = roomBusy;
		}

		public Integer getRoomAvailable() {
			return roomAvailable;
		}

		public void setRoomAvailable(Integer roomAvailable) {
			this.roomAvailable = roomAvailable;
		}

	}

}
