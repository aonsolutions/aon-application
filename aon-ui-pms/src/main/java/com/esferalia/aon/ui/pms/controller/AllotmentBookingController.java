package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.PMS_ALLOTMENT;
import static com.code.aon.ui.common.ICommonMessages.PMS_AVAILABILITY_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_BLOCKED_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_FREE;
import static com.code.aon.ui.common.ICommonMessages.PMS_HOTEL;
import static com.code.aon.ui.common.ICommonMessages.PMS_OCCUPATION_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_TOTAL;
import static com.code.aon.ui.common.ICommonMessages.POTENTIAL;
import static com.code.aon.ui.common.ICommonMessages.REAL;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;

import com.code.aon.AonVersion;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class AllotmentBookingController extends DataScrollerState implements ISQLConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer reportType;
	private Hotel[] hotels;
	private Customer[] agencies;
	private InvoicingGroup[] agencyGroups;
	private String[] rateCodes;
	private Date fromDate;
	private Date toDate;
	private Integer breakdownType;
	private String[] holderList;
	private Map<String, HolderBreakdown> holderBreakdownMap;

	private List<DayBooking> bookingList;

	public Integer getReportType() {
		return reportType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

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

	public InvoicingGroup[] getAgencyGroups() {
		return agencyGroups;
	}
	public void setAgencyGroups(InvoicingGroup[] agencyGroups) {
		this.agencyGroups = agencyGroups;
	}
	public String getAgencyGroupNames() {
		String agencyGroupNames = "";
		for (InvoicingGroup agencyGroup : getAgencyGroups()) {
			agencyGroupNames += agencyGroup.getDescription() + "; ";
		}
		return StringUtils.removeEnd(agencyGroupNames, "; ");
	}

	public String[] getRateCodes() {
		return rateCodes;
	}
	public void setRateCodes(String[] rateCodes) {
		this.rateCodes = rateCodes;
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

	public Integer getBreakdownType() {
		return breakdownType;
	}
	public void setBreakdownType(Integer breakdownType) {
		this.breakdownType = breakdownType;
	}

	public String[] getHolderList() {
		return holderList;
	}
	public void setHolderList(String[] holderList) {
		this.holderList = holderList;
	}

	public Map<String, HolderBreakdown> getHolderBreakdownMap() {
		return holderBreakdownMap;
	}
	public void setHolderBreakdownMap(Map<String, HolderBreakdown> holderBreakdownMap) {
		this.holderBreakdownMap = holderBreakdownMap;
	}

	public List<DayBooking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<DayBooking> bookingList) {
		this.bookingList = bookingList;
	}

	public void onInit(ActionEvent event) {
		setReportType(0);
		setHotels(null);
		setAgencies(null);
		setAgencyGroups(null);
		setRateCodes(null);
		setFromDate(new Date());
		setToDate(DateUtils.addWeeks(new Date(), 2));
		setBreakdownType(null);
	}
	
	public boolean isAgencyReportType() {
		return getReportType() != null && getReportType().intValue() == 0;
	}

	public boolean isRateCodeReportType() {
		return getReportType() != null && getReportType().intValue() == 1;
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
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		PreparedStatement holderBookingStmt = null;
		ResultSet holderBookingRs = null;
		PreparedStatement holderBreakdownStmt = null;
		ResultSet holderBreakdownRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			initializeBookingList(connection);
			initializeHolderList();

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

			allotmentStmt = connection.prepareStatement(getHolderAllotmentSQL());
			SQLUtils.setDate(allotmentStmt, 1, getFromDate());
			SQLUtils.setDate(allotmentStmt, 2, getToDate());
			allotmentRs = allotmentStmt.executeQuery();
			while (allotmentRs.next()) {
				String holder = allotmentRs.getString(HOLDER);
				String hotel = allotmentRs.getString(HOTEL);
				int allotment = allotmentRs.getInt(ALLOTMENT);
				Date startDate = !allotmentRs.getDate(START_DATE).before(getFromDate()) ? allotmentRs.getDate(START_DATE) : getFromDate();
				Date endDate = !allotmentRs.getDate(END_DATE).after(getToDate()) ? allotmentRs.getDate(END_DATE) : getToDate();
				for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date = DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						dayBooking.setRoomAllotment(dayBooking.getRoomAllotment() + allotment);

						if (isRequestedHolder(holder)) {
							DayHolderBooking dayHolderBooking = new DayHolderBooking();
							if (dayBooking.getHolderBookingMap().containsKey(holder)) {
								dayHolderBooking = dayBooking.getHolderBookingMap().get(holder);
							}
							dayHolderBooking.setRoomAllotment(dayHolderBooking.getRoomAllotment() + allotment);
							dayHolderBooking.setRoomAvailable(dayHolderBooking.getRoomAllotment());
							dayBooking.getHolderBookingMap().put(holder, dayHolderBooking);
						}
					}
				}

				if (!ArrayUtils.contains(holderList, holder) && isRequestedHolder(holder)) {
					holderList = (String[])ArrayUtils.add(holderList, holder);
					holderBreakdownMap.put(holder, new HolderBreakdown());
				}
			}

			holderBookingStmt = connection.prepareStatement(getHolderBookingSQL(false, false));
			SQLUtils.setDate(holderBookingStmt, 1, getFromDate());
			SQLUtils.setDate(holderBookingStmt, 2, getToDate());
			SQLUtils.setDate(holderBookingStmt, 3, getFromDate());
			SQLUtils.setDate(holderBookingStmt, 4, getToDate());
			holderBookingRs = holderBookingStmt.executeQuery();
			while (holderBookingRs.next()) {
				String holder = holderBookingRs.getString(HOLDER);
				String hotel = holderBookingRs.getString(HOTEL);
				int allotment = holderBookingRs.getInt(ALLOTMENT);
				Date stayDate = holderBookingRs.getDate(STAY_DATE);
				int rooms = holderBookingRs.getInt(ROOMS);

				DayBooking dayBooking = new DayBooking();
				dayBooking.setHotel(hotel);
				dayBooking.setDate(stayDate);
				int index = getBookingList().indexOf(dayBooking);
				if (index >= 0) {
					dayBooking = getBookingList().get(index);
					dayBooking.setRoomAllotmentBusy(dayBooking.getRoomAllotmentBusy() + ((allotment > rooms) ? rooms : allotment));

					if (isRequestedHolder(holder)) {
						DayHolderBooking dayHolderBooking = new DayHolderBooking();
						if (dayBooking.getHolderBookingMap().containsKey(holder)) {
							dayHolderBooking = dayBooking.getHolderBookingMap().get(holder);
						}
						dayHolderBooking.setRoomBusy(dayHolderBooking.getRoomBusy() + rooms);
						dayHolderBooking.setRoomAvailable(dayHolderBooking.getRoomAvailable() - ((allotment > rooms) ? rooms : allotment));
						dayBooking.getHolderBookingMap().put(holder, dayHolderBooking);
					}
				}
			}

			if (isRoomTypeBreakdown() || isTariffBreakdown()) {
				holderBreakdownStmt = connection.prepareStatement(getHolderBookingSQL(isRoomTypeBreakdown(), isTariffBreakdown()));
				SQLUtils.setDate(holderBreakdownStmt, 1, getFromDate());
				SQLUtils.setDate(holderBreakdownStmt, 2, getToDate());
				SQLUtils.setDate(holderBreakdownStmt, 3, getFromDate());
				SQLUtils.setDate(holderBreakdownStmt, 4, getToDate());
				holderBreakdownRs = holderBreakdownStmt.executeQuery();
				while (holderBreakdownRs.next()) {
					String holder = holderBreakdownRs.getString(HOLDER);
					String hotel = holderBreakdownRs.getString(HOTEL);
					Date stayDate = holderBreakdownRs.getDate(STAY_DATE);
					int rooms = holderBreakdownRs.getInt(ROOMS);
					String breakdown = holderBreakdownRs.getString(BREAKDOWN);
	
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(stayDate);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						if (isRequestedHolder(holder)) {
							DayHolderBooking dayHolderBooking = new DayHolderBooking();
							if (dayBooking.getHolderBookingMap().containsKey(holder)) {
								dayHolderBooking = dayBooking.getHolderBookingMap().get(holder);
							}
							Integer occupationBreakdown = 0;
							if (dayHolderBooking.getOccupationBreakdownMap().containsKey(breakdown)) {
								occupationBreakdown = dayHolderBooking.getOccupationBreakdownMap().get(breakdown);
							}
							dayHolderBooking.getOccupationBreakdownMap().put(breakdown, occupationBreakdown + rooms);
							dayBooking.getHolderBookingMap().put(holder, dayHolderBooking);
						}
					}
	
					if (getHolderBreakdownMap().containsKey(holder) && !getHolderBreakdownMap().get(holder).getBreakdowns().contains(breakdown)) {
						getHolderBreakdownMap().get(holder).getBreakdowns().add(breakdown);
					}
				}
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
			SQLUtils.closeQuietly(holderBreakdownRs);
			SQLUtils.closeQuietly(holderBreakdownStmt);
			SQLUtils.closeQuietly(holderBookingRs);
			SQLUtils.closeQuietly(holderBookingStmt);
			SQLUtils.closeQuietly(allotmentRs);
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeHolderList() {
		holderList = ArrayUtils.EMPTY_STRING_ARRAY;
		holderBreakdownMap = new HashMap<String, HolderBreakdown>();
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
		stmt.append(" ORDER BY " + HOTEL + ", " + STAY_DATE + ", " + STAY_TYPE);

		return stmt.toString();
	}

	private String getHolderAllotmentSQL() throws ManagerBeanException {
		return isAgencyReportType() ? getAgencyAllotmentSQL() : getRateCodeAllotmentSQL();
	}

	private String getAgencyAllotmentSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(IFNULL(R.alias, R.name), IG.description) AS " + HOLDER + ", W.description AS " + HOTEL);
		stmt.append(", A.quantity AS " + ALLOTMENT + ", A.start_date AS " + START_DATE + ", A.end_date AS " + END_DATE);
		stmt.append(" FROM allotment AS A");
		stmt.append(" LEFT JOIN hotel AS H ON A.hotel = H.id AND H.active = 1");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN registry AS R ON A.agency = R.id");
		stmt.append(" LEFT JOIN invoicing_group AS IG ON A.agency_group = IG.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("A.domain"));
		stmt.append(" AND A.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND A.end_date >= ?");
		stmt.append(" AND A.start_date <= ?");
		stmt.append(" AND (A.agency IS NOT NULL OR A.agency_group IS NOT NULL)");
		stmt.append(" AND A.active = 1");
		stmt.append(" ORDER BY " + HOLDER + ", " + HOTEL);

		return stmt.toString();
	}

	private String getRateCodeAllotmentSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT A.rate_code AS " + HOLDER + ", W.description AS " + HOTEL);
		stmt.append(", A.quantity AS " + ALLOTMENT + ", A.start_date AS " + START_DATE + ", A.end_date AS " + END_DATE);
		stmt.append(" FROM allotment AS A");
		stmt.append(" LEFT JOIN hotel AS H ON A.hotel = H.id AND H.active = 1");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN registry AS R ON A.agency = R.id");
		stmt.append(" LEFT JOIN invoicing_group AS IG ON A.agency_group = IG.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("A.domain"));
		stmt.append(" AND A.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND A.end_date >= ?");
		stmt.append(" AND A.start_date <= ?");
		stmt.append(" AND A.rate_code IS NOT NULL");
		stmt.append(" AND A.active = 1");
		stmt.append(" ORDER BY " + HOLDER + ", " + HOTEL);

		return stmt.toString();
	}

	private String getHolderBookingSQL(boolean roomTypeBreakdown, boolean tariffBreakdown) throws ManagerBeanException {
		return isAgencyReportType() ? getAgencyBookingSQL(roomTypeBreakdown, tariffBreakdown) : getRateCodeBookingSQL(roomTypeBreakdown, tariffBreakdown);
	}

	private String getAgencyBookingSQL(boolean roomTypeBreakdown, boolean tariffBreakdown) throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(IFNULL(R.alias, R.name), IG.description) AS " + HOLDER + ", W.description AS " + HOTEL);
		stmt.append(", A.quantity AS " + ALLOTMENT + ", B.stay_date AS " + STAY_DATE);
		stmt.append(", COUNT(DISTINCT B.id) AS " + ROOMS);
		stmt.append(roomTypeBreakdown ? ", P.code AS " + BREAKDOWN : "");
		stmt.append(tariffBreakdown ? ", T.code AS " + BREAKDOWN : "");
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
		stmt.append(roomTypeBreakdown ? " LEFT JOIN item AS I ON B.item = I.id" : "");
		stmt.append(roomTypeBreakdown ? " LEFT JOIN product AS P ON I.product = P.id" : "");
		stmt.append(tariffBreakdown ? " LEFT JOIN tariff AS T ON B.tariff = T.id" : "");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("A.domain"));
		stmt.append(" AND A.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND A.end_date >= ?");
		stmt.append(" AND A.start_date <= ?");
		stmt.append(" AND A.active = 1");
		stmt.append(" AND B.stay_date IS NOT NULL");
		stmt.append(" GROUP BY A.id, B.stay_date");
		stmt.append(roomTypeBreakdown ? ", P.code" : "");
		stmt.append(tariffBreakdown ? ", T.code" : "");
		stmt.append(" ORDER BY " + HOLDER + ", " + HOTEL + ", " + STAY_DATE);
		stmt.append(roomTypeBreakdown || tariffBreakdown ? ", " + BREAKDOWN : "");

		return stmt.toString();
	}

	private String getRateCodeBookingSQL(boolean roomTypeBreakdown, boolean tariffBreakdown) throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT A.rate_code AS " + HOLDER + ", W.description AS " + HOTEL);
		stmt.append(", A.quantity AS " + ALLOTMENT + ", B.stay_date AS " + STAY_DATE);
		stmt.append(", COUNT(DISTINCT B.id) AS " + ROOMS);
		stmt.append(roomTypeBreakdown ? ", P.code AS " + BREAKDOWN : "");
		stmt.append(tariffBreakdown ? ", T.code AS " + BREAKDOWN : "");
		stmt.append(" FROM allotment AS A, hotel AS H, workplace AS W, booking AS B, project_reservation_room AS PRR");
		stmt.append(roomTypeBreakdown ? ", item AS I, product AS P" : "");
		stmt.append(tariffBreakdown ? ", tariff AS T" : "");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("A.domain"));
		stmt.append(" AND A.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND A.end_date >= ?");
		stmt.append(" AND A.start_date <= ?");
		stmt.append(" AND A.rate_code IS NOT NULL");
		stmt.append(" AND A.active = 1");
		stmt.append(" AND A.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND A.hotel = B.hotel");
		stmt.append(" AND B.stay_date BETWEEN A.start_date AND A.end_date");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		stmt.append(" AND B.stay_type IN (0,2)");
		stmt.append(" AND (0 = (SELECT COUNT(*) FROM allotment_item WHERE allotment = A.id)");
		stmt.append("     OR B.item IN (SELECT item FROM allotment_item WHERE allotment = A.id))");
		stmt.append(" AND (0 = (SELECT COUNT(*) FROM allotment_tariff WHERE allotment = A.id)");
		stmt.append("     OR B.tariff IN (SELECT tariff FROM allotment_tariff WHERE allotment = A.id))");
		stmt.append(" AND B.project_reservation_room = PRR.id");
		stmt.append(" AND A.rate_code = PRR.allotment_rate_code");
		stmt.append(roomTypeBreakdown ? " AND B.item = I.id" : "");
		stmt.append(roomTypeBreakdown ? " AND I.product = P.id" : "");
		stmt.append(tariffBreakdown ? " AND B.tariff = T.id" : "");
		stmt.append(" GROUP BY A.id, B.stay_date");
		stmt.append(roomTypeBreakdown ? ", P.code" : "");
		stmt.append(tariffBreakdown ? ", T.code" : "");
		stmt.append(" ORDER BY " + HOLDER + ", " + HOTEL + ", " + STAY_DATE);
		stmt.append(roomTypeBreakdown || tariffBreakdown ? ", " + BREAKDOWN : "");
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

	private boolean isRequestedHolder(String holderName) {
		if (isAgencyReportType()) {
			if (ArrayUtils.isNotEmpty(getAgencies()) || ArrayUtils.isNotEmpty(getAgencyGroups())) {
				for (Customer agency : getAgencies()) {
					String alias = StringUtils.isEmpty(agency.getRegistry().getAlias()) ? agency.getRegistry().getName() : agency.getRegistry().getAlias();
					if (holderName.equals(alias)) {
						return true;
					}
				}
				for (InvoicingGroup agencyGroup : getAgencyGroups()) {
					if (holderName.equals(agencyGroup.getDescription())) {
						return true;
					}
				}
			} else {
				return true;
			}
		} else {
			if (ArrayUtils.isNotEmpty(getRateCodes())) {
				for (String rateCode : getRateCodes()) {
					if (holderName.equals(rateCode)) {
						return true;
					}
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean isRoomTypeBreakdown() {
		return getBreakdownType() != null && getBreakdownType().intValue() == 0;
	}

	private boolean isTariffBreakdown() {
		return getBreakdownType() != null && getBreakdownType().intValue() == 1;
	}

	public String onExcelReport() {
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
		String fileName = "Control de Cupos";
		response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
		response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");

		ServletOutputStream output;
		try {
			output = response.getOutputStream();
			ExcelReportExporter report = new ExcelReportExporter();
			report.startExport(IReportExporter.DEFAULT_NAME);
			ReportMetadata metadata = createExcelHeader(report);
			report.exportHeader(metadata);
			for (DayBooking dayBooking : getBookingList()) {
				report.startLine();
				report.exportColumn(metadata.getColumns().get(0), dayBooking.getHotel());
				report.exportColumn(metadata.getColumns().get(1), dayBooking.getDate());
				report.exportColumn(metadata.getColumns().get(2), dayBooking.getRoomBusy());
				report.exportColumn(metadata.getColumns().get(3), dayBooking.getRoomFree());
				report.exportColumn(metadata.getColumns().get(4), dayBooking.getRoomBlocked());
				report.exportColumn(metadata.getColumns().get(5), dayBooking.getRoomTotal());
				report.exportColumn(metadata.getColumns().get(6), dayBooking.getRoomAllotment());
				report.exportColumn(metadata.getColumns().get(7), dayBooking.getRoomAvailable());
				report.exportColumn(metadata.getColumns().get(8), dayBooking.getRoomBusyPotential());
				HSSFCell freePotentialCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(9), dayBooking.getRoomFreePotential());
				if (dayBooking.getRoomFreePotential() < 0) {
					paintCell(report, freePotentialCell, HSSFColor.RED.index);
				}
				for (String holder : holderList) {
					DayHolderBooking holderBooking = dayBooking.getHolderBookingMap().get(holder);
					report.exportColumn(metadata.getColumns().get(10), (holderBooking != null) ? holderBooking.getRoomAllotment() : null);
					HSSFCell busyCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(11), (holderBooking != null) ? holderBooking.getRoomBusy() : null);
					if (holderBooking != null && holderBooking.getRoomBusy() >= holderBooking.getRoomAllotment()) {
						paintCell(report, busyCell, HSSFColor.GREEN.index);
					}

					if (holderBooking != null) {
						for (String breakdown : holderBreakdownMap.get(holder).getBreakdowns()) {
							Integer rooms = holderBooking.getOccupationBreakdownMap().get(breakdown);
							HSSFCell breakdownCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(11), (rooms != null) ? rooms : null);
							paintCell(report, breakdownCell, HSSFColor.CORNFLOWER_BLUE.index);
						}
					}

					HSSFCell availCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(12), (holderBooking != null) ? holderBooking.getRoomAvailable() : null);
					if (holderBooking != null && dayBooking.getRoomFreePotential() < 0 && holderBooking.getRoomAvailable() > 0) {
						paintCell(report, availCell, HSSFColor.RED.index);
					}
				}
				report.endLine();
			}
			report.autoSizeColumns();
			report.endExport(output);
			response.flushBuffer();
			faces.responseComplete();
		} catch (ReportException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private ReportMetadata createExcelHeader(ExcelReportExporter report) {
		HSSFCellStyle cellStyleBlack = newExcelHeaderStyle(report);
		HSSFFont cellFont = report.createFont();
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    cellStyleBlack.setFont(cellFont);

	    report.addHeaderRow();
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell(AonUtil.getMessage(REAL).toUpperCase(), 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell(AonUtil.getMessage(POTENTIAL).toUpperCase(), 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addMergedRegion(0, 0, 0, 1);
		report.addMergedRegion(0, 0, 2, 5);
		report.addMergedRegion(0, 0, 6, 9);

		HSSFCellStyle cellStyleBlue = newExcelHeaderStyle(report);
		cellFont = report.createFont();
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setColor(HSSFColor.BLUE.index);
	    cellStyleBlue.setFont(cellFont);
		for (int i=0, from=10, to=10; i<holderList.length; i++, from=to) {
			report.addHeaderCell(holderList[i], 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			to = to + 3;
			if (holderBreakdownMap.containsKey(holderList[i])) {
				for (int j=0; j<holderBreakdownMap.get(holderList[i]).getBreakdowns().size(); j++) {
					report.addHeaderCell("", 0, cellStyleBlue);
					to++;
				}
			}
			report.addMergedRegion(0, 0, from, to-1);
		}

		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("hotel", Types.VARCHAR, AonUtil.getMessage(PMS_HOTEL).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("date", Types.DATE, AonUtil.getMessage(DATE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFree", Types.INTEGER, AonUtil.getMessage(PMS_FREE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBlocked", Types.INTEGER, AonUtil.getMessage(PMS_BLOCKED_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomTotal", Types.INTEGER, AonUtil.getMessage(PMS_TOTAL).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomAllotment", Types.INTEGER, AonUtil.getMessage(PMS_ALLOTMENT).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomAvailable", Types.INTEGER, AonUtil.getMessage(PMS_AVAILABILITY_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusyPotential", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFreePotential", Types.INTEGER, AonUtil.getMessage(PMS_FREE).toUpperCase(), 30));
		for (int i=0; i<holderList.length; i++) {
			metadata.getColumns().add(new ReportColumnMetadata("holderAllotment", Types.INTEGER, AonUtil.getMessage(PMS_ALLOTMENT).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("holderBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
			if (holderBreakdownMap.containsKey(holderList[i])) {
				for (int j=0; j<holderBreakdownMap.get(holderList[i]).getBreakdowns().size(); j++) {
					String breakdown = holderBreakdownMap.get(holderList[i]).getBreakdowns().get(j);
					metadata.getColumns().add(new ReportColumnMetadata("holderBusy_" + breakdown, Types.INTEGER, breakdown, 30));
				}
			}
			metadata.getColumns().add(new ReportColumnMetadata("holderAvailable", Types.INTEGER, AonUtil.getMessage(PMS_AVAILABILITY_ABBRV).toUpperCase(), 30));
		}
		return metadata;
	}

	private HSSFCellStyle newExcelHeaderStyle(ExcelReportExporter report) {
		HSSFCellStyle cellStyle = report.createCellStyle();
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THICK);
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	    cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    return cellStyle;
	}

	private void paintCell(ExcelReportExporter report, HSSFCell cell, short color) {
		HSSFCellStyle cellStyle = report.createCellStyle();
		if (cellStyle == null) {
			cellStyle = report.createCellStyle();
		}

		HSSFFont cellFont = report.createFont();
	    cellFont.setColor(color);
	    cellStyle.setFont(cellFont);
	    cell.setCellStyle(cellStyle);
	}

	/***************** DAY BOOKING *********************************/

	public static class DayBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String hotel;
		private Date date;
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomTotal;
		private Integer roomAllotment;
		private Integer roomAllotmentBusy;
		private Map<String, DayHolderBooking> holderBookingMap;

		public DayBooking() {
			roomBusy = 0;
			roomBlocked = 0;
			roomTotal = 0;
			roomAllotment = 0;
			roomAllotmentBusy = 0;
			holderBookingMap = new HashMap<String, DayHolderBooking>();
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

		public Map<String, DayHolderBooking> getHolderBookingMap() {
			return holderBookingMap;
		}
		public void setHolderBookingMap(Map<String, DayHolderBooking> holderBookingMap) {
			this.holderBookingMap = holderBookingMap;
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

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final DayBooking o = (DayBooking)obj;
			return o.getHotel().equals(getHotel()) && o.getDate().equals(getDate());
		}

	}

	/***************** DAY HOLDER BOOKING *********************************/

	public static class DayHolderBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Integer roomAllotment;
		private Integer roomBusy;
		private Integer roomAvailable;
		private Map<String, Integer> occupationBreakdownMap;

		public DayHolderBooking() {
			roomAllotment = 0;
			roomBusy = 0;
			roomAvailable = 0;
			occupationBreakdownMap = new HashMap<String, Integer>();
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

		public Map<String, Integer> getOccupationBreakdownMap() {
			return occupationBreakdownMap;
		}
		public void setOccupationBreakdownMap(Map<String, Integer> occupationBreakdownMap) {
			this.occupationBreakdownMap = occupationBreakdownMap;
		}

	}

	/***************** HOLDER BREAKDOWN *********************************/

	public static class HolderBreakdown implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private List<String> breakdowns;
		private int holderSize = 3;

		public HolderBreakdown() {
			breakdowns = new LinkedList<String>();
		}

		public List<String> getBreakdowns() {
			return breakdowns;
		}
		public void setBreakdowns(List<String> breakdowns) {
			this.breakdowns = breakdowns;
		}

		public int getBreakdownsSize() {
			return holderSize + getBreakdowns().size();
		}

	}

}
