package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.REAL;
import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.PMS_BLOCKED_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_CANCELLED_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_DIRECT_CUSTOMER;
import static com.code.aon.ui.common.ICommonMessages.PMS_FREE;
import static com.code.aon.ui.common.ICommonMessages.PMS_HOTEL;
import static com.code.aon.ui.common.ICommonMessages.PMS_OCCUPATION_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_TOTAL;

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
import org.apache.poi.ss.usermodel.CellStyle;

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
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

/*String PMS_CHECKIN_ABBRV = "report_checkin_abbrv";
String PMS_CHECKOUT_ABBRV = "report_checkout_abbrv";
String PMS_ALLOTMENT = "pms_allotment";
String PMS_AVAILABILITY_ABBRV = "pms_availability_abbrv";
*/
import java.io.IOException;
import java.io.Serializable;

public class AgencyBookingController extends DataScrollerState implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel[] hotels;
	private Customer[] agencies;
	private InvoicingGroup[] agencyGroups;
	private Date fromDate;
	private Date toDate;
	private Date reservationFromDate;
	private Date reservationToDate;
	private boolean groupAgencies;
	private String[] agencyList;

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

	public Date getReservationFromDate() {
		return reservationFromDate;
	}
	public void setReservationFromDate(Date reservationFromDate) {
		this.reservationFromDate = reservationFromDate;
	}

	public Date getReservationToDate() {
		return reservationToDate;
	}
	public void setReservationToDate(Date reservationToDate) {
		this.reservationToDate = reservationToDate;
	}

	public boolean isGroupAgencies() {
		return groupAgencies;
	}
	public void setGroupAgencies(boolean groupAgencies) {
		this.groupAgencies = groupAgencies;
	}

	public String[] getAgencyList() {
		return agencyList;
	}
	public void setAgencyList(String[] agencyList) {
		this.agencyList = agencyList;
	}

	public List<DayBooking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<DayBooking> bookingList) {
		this.bookingList = bookingList;
	}

	public void onInit(ActionEvent event) {
		setHotels(null);
		setAgencies(null);
		setAgencyGroups(null);
		setFromDate(new Date());
		setToDate(DateUtils.addWeeks(new Date(), 2));
		setReservationFromDate(null);
		setReservationToDate(null);
		setGroupAgencies(false);
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
		PreparedStatement agencyBookingStmt = null;
		ResultSet agencyBookingRs = null;
		PreparedStatement agencyCancelledStmt = null;
		ResultSet agencyCancelledRs = null;
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

			agencyBookingStmt = connection.prepareStatement(getAgencyBookingSQL());
			SQLUtils.setDate(agencyBookingStmt, 1, getFromDate());
			SQLUtils.setDate(agencyBookingStmt, 2, getToDate());
			if (getReservationFromDate() != null || getReservationToDate() != null) {
				SQLUtils.setDate(agencyBookingStmt, 3, getReservationFromDate() != null ? getReservationFromDate() : DateUtils.addYears(getReservationToDate(), -1));
				SQLUtils.setDate(agencyBookingStmt, 4, getReservationToDate() != null ? getReservationToDate() : getToDate());
			}
			agencyBookingRs = agencyBookingStmt.executeQuery();
			while (agencyBookingRs.next()) {
				String hotel = agencyBookingRs.getString(HOTEL);
				Date stayDate = agencyBookingRs.getDate(STAY_DATE);
				String agency = agencyBookingRs.getString(AGENCY);
				if (agency == null) {
					agency = AonUtil.getMessage(PMS_DIRECT_CUSTOMER);
				}
				String agencyGroup = agencyBookingRs.getString(AGENCY_GROUP);
				if (isGroupAgencies() && StringUtils.isNotBlank(agencyGroup)) {
					agency = agencyGroup;
				}
				int rooms = agencyBookingRs.getInt(ROOMS);

				DayBooking dayBooking = new DayBooking();
				dayBooking.setHotel(hotel);
				dayBooking.setDate(stayDate);
				int index = getBookingList().indexOf(dayBooking);
				if (index >= 0) {
					dayBooking = getBookingList().get(index);

					DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
					if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
						dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
					}
					dayAgencyBooking.setRoomBusy(dayAgencyBooking.getRoomBusy() + rooms);
					dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
				}

				if (!ArrayUtils.contains(agencyList, agency)) {
					agencyList = (String[])ArrayUtils.add(agencyList, agency);
				}
			}

			agencyCancelledStmt = connection.prepareStatement(getRoomCancelledSQL());
			SQLUtils.setDate(agencyCancelledStmt, 1, getFromDate());
			SQLUtils.setDate(agencyCancelledStmt, 2, getToDate());
			if (getReservationFromDate() != null || getReservationToDate() != null) {
				SQLUtils.setDate(agencyCancelledStmt, 3, getReservationFromDate() != null ? getReservationFromDate() : DateUtils.addYears(getReservationToDate(), -1));
				SQLUtils.setDate(agencyCancelledStmt, 4, getReservationToDate() != null ? getReservationToDate() : getToDate());
			}
			agencyCancelledRs = agencyCancelledStmt.executeQuery();
			while (agencyCancelledRs.next()) {
				String hotel = agencyCancelledRs.getString(HOTEL);
				Date startDate = agencyCancelledRs.getDate(START_DATE);
				startDate = startDate.before(getFromDate()) ? DateUtils.truncate(getFromDate(), Calendar.DATE) : startDate;
				Date endDate = agencyCancelledRs.getDate(END_DATE);
				endDate = endDate.after(getToDate()) ? DateUtils.truncate(getToDate(), Calendar.DATE) : DateUtils.addDays(endDate, -1);
				String agency = agencyCancelledRs.getString(AGENCY);
				if (agency == null) {
					agency = AonUtil.getMessage(PMS_DIRECT_CUSTOMER);
				}
				String agencyGroup = agencyCancelledRs.getString(AGENCY_GROUP);
				if (isGroupAgencies() && StringUtils.isNotBlank(agencyGroup)) {
					agency = agencyGroup;
				}
				int rooms = agencyCancelledRs.getInt(ROOMS);
				for (Date date=DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date=DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);

						DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
						if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
							dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
						}
						dayAgencyBooking.setRoomCancelled(dayAgencyBooking.getRoomCancelled() + rooms);
						dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
					}
				}

				if (!ArrayUtils.contains(agencyList, agency)) {
					agencyList = (String[])ArrayUtils.add(agencyList, agency);
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
			SQLUtils.closeQuietly(agencyCancelledRs);
			SQLUtils.closeQuietly(agencyCancelledStmt);
			SQLUtils.closeQuietly(agencyBookingRs);
			SQLUtils.closeQuietly(agencyBookingStmt);
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private void initializeBookingList(Connection connection) throws AonSQLException {
		setBookingList(new LinkedList<DayBooking>());
		setAgencyList(ArrayUtils.EMPTY_STRING_ARRAY);

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

	private String getAgencyBookingSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", B.stay_date AS " + STAY_DATE);
		stmt.append(", IF(R.alias IS NOT NULL AND R.alias != '', R.alias, R.name) AS " + AGENCY + ", IG.description AS " + AGENCY_GROUP);
		stmt.append(", COUNT(DISTINCT B.id) AS " + ROOMS);
		stmt.append(" FROM booking AS B");
		stmt.append(" LEFT JOIN hotel AS H ON B.hotel = H.id AND H.active = 1");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN project_reservation_room AS PRR ON B.project_reservation_room = PRR.id");
		stmt.append(" LEFT JOIN project_reservation AS PR ON PRR.project_reservation = PR.project");
		stmt.append(" LEFT JOIN registry AS R ON B.agency = R.id");
		stmt.append(" LEFT JOIN customer AS C ON R.id = C.registry");
		stmt.append(" LEFT JOIN invoicing_group AS IG ON C.invoicing_group = IG.id"); 
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		stmt.append(" AND B.stay_type IN (0,2)");
		if (ArrayUtils.isNotEmpty(getAgencies()) || ArrayUtils.isEmpty(getAgencyGroups())) {
			String prefix = ArrayUtils.isEmpty(getAgencies()) ? "(B.agency IS NULL OR " : ArrayUtils.isNotEmpty(getAgencyGroups()) ? "(" : "";
			stmt.append(" AND " + prefix + "B.agency IN (" + getAgencyIds() + ")");
		}
		if (ArrayUtils.isEmpty(getAgencies()) || ArrayUtils.isNotEmpty(getAgencyGroups())) {
			if (ArrayUtils.isEmpty(getAgencies()) && ArrayUtils.isNotEmpty(getAgencyGroups())) {
				stmt.append(" AND C.invoicing_group IN (" + getAgencyGroupIds() + ")");
			} else {
				stmt.append(" OR C.invoicing_group IN (" + getAgencyGroupIds() + "))");
			}
		}
		if (getReservationFromDate() != null || getReservationToDate() != null) {
			stmt.append(" AND PR.creation_date BETWEEN ? AND ?");
		}
		stmt.append(" GROUP BY W.description, B.stay_date, B.agency, IG.description");
		stmt.append(" ORDER BY " + HOTEL + ", " + STAY_DATE + ", " + AGENCY + ", " + AGENCY_GROUP);

		return stmt.toString();
	}

	private String getRoomCancelledSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE);
		stmt.append(", IF(R.alias IS NOT NULL AND R.alias != '', R.alias, R.name) AS " + AGENCY + ", IG.description AS " + AGENCY_GROUP);
		stmt.append(", COUNT(DISTINCT PRR.id) AS " + ROOMS);
		stmt.append(" FROM project_reservation AS PR");
		stmt.append(" LEFT JOIN project_reservation_room AS PRR ON PR.project = PRR.project_reservation");
		stmt.append(" LEFT JOIN hotel AS H ON PR.hotel = H.id AND H.active = 1");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN registry AS R ON PR.agency = R.id");
		stmt.append(" LEFT JOIN customer AS C ON R.id = C.registry");
		stmt.append(" LEFT JOIN invoicing_group AS IG ON C.invoicing_group = IG.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append(" AND PR.status = " + ReservationStatus.CANCELLED.ordinal());
		stmt.append(" AND PR.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND PR.end_date > ?");
		stmt.append(" AND PR.start_date <= ?");
		if (ArrayUtils.isNotEmpty(getAgencies()) || ArrayUtils.isEmpty(getAgencyGroups())) {
			String prefix = ArrayUtils.isEmpty(getAgencies()) ? "(PR.agency IS NULL OR " : ArrayUtils.isNotEmpty(getAgencyGroups()) ? "(" : "";
			stmt.append(" AND " + prefix + "PR.agency IN (" + getAgencyIds() + ")");
		}
		if (ArrayUtils.isEmpty(getAgencies()) || ArrayUtils.isNotEmpty(getAgencyGroups())) {
			if (ArrayUtils.isEmpty(getAgencies()) && ArrayUtils.isNotEmpty(getAgencyGroups())) {
				stmt.append(" AND C.invoicing_group IN (" + getAgencyGroupIds() + ")");
			} else {
				stmt.append(" OR C.invoicing_group IN (" + getAgencyGroupIds() + "))");
			}
		}
		if (getReservationFromDate() != null || getReservationToDate() != null) {
			stmt.append(" AND PR.creation_date BETWEEN ? AND ?");
		}
		stmt.append(" GROUP BY W.description, PR.start_date, PR.end_date, PR.agency, IG.description");
		stmt.append(" ORDER BY " + HOTEL + "," + START_DATE + "," + END_DATE + "," + AGENCY + "," + AGENCY_GROUP);

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

	private String getAgencyIds() throws ManagerBeanException {
		String agencyIds = "";
		if (ArrayUtils.isNotEmpty(getAgencies())) {
			for (Customer agency : getAgencies()) {
				agencyIds += agency.getId() + ",";
			}
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			agencyIds = StringUtils.join(collectionsController.getAgencyIds(), ",");
		}
		return StringUtils.removeEnd(agencyIds, ",");
	}

	private String getAgencyGroupIds() throws ManagerBeanException {
		String agencyGroupIds = "";
		if (ArrayUtils.isNotEmpty(getAgencyGroups())) {
			for (InvoicingGroup agencyGroup : getAgencyGroups()) {
				agencyGroupIds += agencyGroup.getId() + ",";
			}
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			agencyGroupIds = StringUtils.join(collectionsController.getAgencyGroupIds(), ",");
		}
		return StringUtils.removeEnd(agencyGroupIds, ",");
	}

	public String onExcelReport() {
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
		String fileName = "Booking por Agencia";
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
				HSSFCell busyCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(3), dayBooking.getRoomBusyPercent().toString() + "%");
				alignCell(report, busyCell, CellStyle.ALIGN_RIGHT);
				report.exportColumn(metadata.getColumns().get(4), dayBooking.getRoomFree());
				report.exportColumn(metadata.getColumns().get(5), dayBooking.getRoomBlocked());
				report.exportColumn(metadata.getColumns().get(6), dayBooking.getRoomTotal());
				for (String agency : agencyList) {
					DayAgencyBooking agencyBooking = dayBooking.getAgencyBookingMap().get(agency);
					report.exportColumn(metadata.getColumns().get(7), (agencyBooking != null) ? agencyBooking.getRoomBusy() : null);

					HSSFCell cancCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(8), (agencyBooking != null) ? agencyBooking.getRoomCancelled() : null);
					if (agencyBooking != null && agencyBooking.getRoomCancelled() > 0) {
						paintCell(report, cancCell, HSSFColor.RED.index);
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
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addMergedRegion(0, 0, 0, 1);
		report.addMergedRegion(0, 0, 2, 6);

		HSSFCellStyle cellStyleBlue = newExcelHeaderStyle(report);
		cellFont = report.createFont();
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setColor(HSSFColor.BLUE.index);
	    cellStyleBlue.setFont(cellFont);
		for (int i=0, from=7, to=7; i<agencyList.length; i++, from=to) {
			report.addHeaderCell(agencyList[i], 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			to = to + 2;
			report.addMergedRegion(0, 0, from, to-1);
		}

		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("hotel", Types.VARCHAR, AonUtil.getMessage(PMS_HOTEL).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("date", Types.DATE, AonUtil.getMessage(DATE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusyPercent", Types.VARCHAR, "%" + AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFree", Types.INTEGER, AonUtil.getMessage(PMS_FREE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBlocked", Types.INTEGER, AonUtil.getMessage(PMS_BLOCKED_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomTotal", Types.INTEGER, AonUtil.getMessage(PMS_TOTAL).toUpperCase(), 30));
		for (int i=0; i<agencyList.length; i++) {
			metadata.getColumns().add(new ReportColumnMetadata("agencyBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("agencyCancelled", Types.INTEGER, AonUtil.getMessage(PMS_CANCELLED_ABBRV).toUpperCase(), 30));
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

	private void alignCell(ExcelReportExporter report, HSSFCell cell, short align) {
		HSSFCellStyle cellStyle = report.createCellStyle();
		if (cellStyle == null) {
			cellStyle = report.createCellStyle();
		}

	    cellStyle.setAlignment(align);
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
		private Map<String, DayAgencyBooking> agencyBookingMap;

		public DayBooking() {
			roomBusy = 0;
			roomBlocked = 0;
			roomTotal = 0;
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

		public Map<String, DayAgencyBooking> getAgencyBookingMap() {
			return agencyBookingMap;
		}
		public void setAgencyBookingMap(Map<String, DayAgencyBooking> agencyBookingMap) {
			this.agencyBookingMap = agencyBookingMap;
		}

		public Integer getRoomBusyPercent() {
			return (roomTotal > 0) ? roomBusy * 100 / roomTotal : 0;
		}

		public Integer getRoomFree() {
			return roomTotal - roomBusy - roomBlocked;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final DayBooking o = (DayBooking)obj;
			return o.getHotel().equals(getHotel()) && o.getDate().equals(getDate());
		}

	}

	/***************** DAY AGENCY BOOKING *********************************/

	public static class DayAgencyBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Integer roomBusy;
		private Integer roomCancelled;

		public DayAgencyBooking() {
			roomBusy = 0;
			roomCancelled = 0;
		}

		public Integer getRoomBusy() {
			return roomBusy;
		}
		public void setRoomBusy(Integer roomBusy) {
			this.roomBusy = roomBusy;
		}

		public Integer getRoomCancelled() {
			return roomCancelled;
		}
		public void setRoomCancelled(Integer roomCancelled) {
			this.roomCancelled = roomCancelled;
		}

	}

}
