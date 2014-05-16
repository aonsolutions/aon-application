package com.esferalia.aon.ui.pms.controller;

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

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
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

	private Hotel hotel;
	private Customer agency;
	private InvoicingGroup agencyGroup;
	private Date fromDate;
	private Date toDate;
	private Integer breakdownType;
	private String[] agencies;
	private Map<String, AgencyBreakdown> agencyBreakdownMap;

	private List<DayBooking> bookingList;

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

	public String[] getAgencies() {
		return agencies;
	}
	public void setAgencies(String[] agencies) {
		this.agencies = agencies;
	}

	public Map<String, AgencyBreakdown> getAgencyBreakdownMap() {
		return agencyBreakdownMap;
	}
	public void setAgencyBreakdownMap(Map<String, AgencyBreakdown> agencyBreakdownMap) {
		this.agencyBreakdownMap = agencyBreakdownMap;
	}

	public List<DayBooking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<DayBooking> bookingList) {
		this.bookingList = bookingList;
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
		setBreakdownType(null);
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
		PreparedStatement agencyBookingStmt = null;
		ResultSet agencyBookingRs = null;
		PreparedStatement agencyBreakdownStmt = null;
		ResultSet agencyBreakdownRs = null;
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

			allotmentStmt = connection.prepareStatement(getAllotmentSQL());
			SQLUtils.setDate(allotmentStmt, 1, getFromDate());
			SQLUtils.setDate(allotmentStmt, 2, getToDate());
			allotmentRs = allotmentStmt.executeQuery();
			while (allotmentRs.next()) {
				String agency = allotmentRs.getString(AGENCY);
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

						if (isRequestedAgency(agency)) {
							DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
							if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
								dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
							}
							dayAgencyBooking.setRoomAllotment(dayAgencyBooking.getRoomAllotment() + allotment);
							dayAgencyBooking.setRoomAvailable(dayAgencyBooking.getRoomAllotment());
							dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
						}
					}
				}

				if (!ArrayUtils.contains(agencies, agency) && isRequestedAgency(agency)) {
					agencies = (String[])ArrayUtils.add(agencies, agency);
					agencyBreakdownMap.put(agency, new AgencyBreakdown());
				}
			}

			agencyBookingStmt = connection.prepareStatement(getAgencyBookingSQL(false, false));
			SQLUtils.setDate(agencyBookingStmt, 1, getFromDate());
			SQLUtils.setDate(agencyBookingStmt, 2, getToDate());
			SQLUtils.setDate(agencyBookingStmt, 3, getFromDate());
			SQLUtils.setDate(agencyBookingStmt, 4, getToDate());
			agencyBookingRs = agencyBookingStmt.executeQuery();
			while (agencyBookingRs.next()) {
				String agency = agencyBookingRs.getString(AGENCY);
				String hotel = agencyBookingRs.getString(HOTEL);
				int allotment = agencyBookingRs.getInt(ALLOTMENT);
				Date stayDate = agencyBookingRs.getDate(STAY_DATE);
				int rooms = agencyBookingRs.getInt(ROOMS);

				DayBooking dayBooking = new DayBooking();
				dayBooking.setHotel(hotel);
				dayBooking.setDate(stayDate);
				int index = getBookingList().indexOf(dayBooking);
				if (index >= 0) {
					dayBooking = getBookingList().get(index);
					dayBooking.setRoomAllotmentBusy(dayBooking.getRoomAllotmentBusy() + ((allotment > rooms) ? rooms : allotment));

					if (isRequestedAgency(agency)) {
						DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
						if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
							dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
						}
						dayAgencyBooking.setRoomBusy(dayAgencyBooking.getRoomBusy() + rooms);
						dayAgencyBooking.setRoomAvailable(dayAgencyBooking.getRoomAvailable() - ((allotment > rooms) ? rooms : allotment));
						dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
					}
				}
			}

			if (isRoomTypeBreakdown() || isTariffBreakdown()) {
				agencyBreakdownStmt = connection.prepareStatement(getAgencyBookingSQL(isRoomTypeBreakdown(), isTariffBreakdown()));
				SQLUtils.setDate(agencyBreakdownStmt, 1, getFromDate());
				SQLUtils.setDate(agencyBreakdownStmt, 2, getToDate());
				SQLUtils.setDate(agencyBreakdownStmt, 3, getFromDate());
				SQLUtils.setDate(agencyBreakdownStmt, 4, getToDate());
				agencyBreakdownRs = agencyBreakdownStmt.executeQuery();
				while (agencyBreakdownRs.next()) {
					String agency = agencyBreakdownRs.getString(AGENCY);
					String hotel = agencyBreakdownRs.getString(HOTEL);
					Date stayDate = agencyBreakdownRs.getDate(STAY_DATE);
					int rooms = agencyBreakdownRs.getInt(ROOMS);
					String breakdown = agencyBreakdownRs.getString(BREAKDOWN);
	
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(stayDate);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						if (isRequestedAgency(agency)) {
							DayAgencyBooking dayAgencyBooking = new DayAgencyBooking();
							if (dayBooking.getAgencyBookingMap().containsKey(agency)) {
								dayAgencyBooking = dayBooking.getAgencyBookingMap().get(agency);
							}
							Integer occupationBreakdown = 0;
							if (dayAgencyBooking.getOccupationBreakdownMap().containsKey(breakdown)) {
								occupationBreakdown = dayAgencyBooking.getOccupationBreakdownMap().get(breakdown);
							}
							dayAgencyBooking.getOccupationBreakdownMap().put(breakdown, occupationBreakdown + rooms);
							dayBooking.getAgencyBookingMap().put(agency, dayAgencyBooking);
						}
					}
	
					if (getAgencyBreakdownMap().containsKey(agency) && !getAgencyBreakdownMap().get(agency).getBreakdowns().contains(breakdown)) {
						getAgencyBreakdownMap().get(agency).getBreakdowns().add(breakdown);
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
			SQLUtils.closeQuietly(agencyBreakdownRs);
			SQLUtils.closeQuietly(agencyBreakdownStmt);
			SQLUtils.closeQuietly(agencyBookingRs);
			SQLUtils.closeQuietly(agencyBookingStmt);
			SQLUtils.closeQuietly(allotmentRs);
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeAgencyList() {
		agencies = ArrayUtils.EMPTY_STRING_ARRAY;
		agencyBreakdownMap = new HashMap<String, AgencyBreakdown>();
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
		stmt.append(" ORDER BY " + HOTEL + ", " + STAY_DATE + ", " + STAY_TYPE);

		return stmt.toString();
	}

	private String getAllotmentSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(IFNULL(R.alias, R.name), IG.description) AS " + AGENCY + ", W.description AS " + HOTEL);
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
		stmt.append(" AND A.active = 1");
		stmt.append(" ORDER BY " + AGENCY + ", " + HOTEL);

		return stmt.toString();
	}

	private String getAgencyBookingSQL(boolean roomTypeBreakdown, boolean tariffBreakdown) throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(IFNULL(R.alias, R.name), IG.description) AS " + AGENCY + ", W.description AS " + HOTEL);
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
		stmt.append(" ORDER BY " + AGENCY + ", " + HOTEL + ", " + STAY_DATE);
		stmt.append(roomTypeBreakdown || tariffBreakdown ? ", " + BREAKDOWN : "");

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

	private boolean isRequestedAgency(String agency) {
		if ((getAgency() != null && getAgency().getId() != null) || (getAgencyGroup() != null && getAgencyGroup().getId() != null)) {
			if (getAgency() != null && getAgency().getId() != null) {
				String alias = StringUtils.isEmpty(getAgency().getRegistry().getAlias()) ? getAgency().getRegistry().getName() : getAgency().getRegistry().getAlias();
				return agency.equals(alias);
			}
			if (getAgencyGroup() != null && getAgencyGroup().getId() != null) {
				return agency.equals(getAgencyGroup().getDescription());
			}
		}
		return true;
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
				for (String agency : agencies) {
					DayAgencyBooking agencyBooking = dayBooking.getAgencyBookingMap().get(agency);
					report.exportColumn(metadata.getColumns().get(10), (agencyBooking != null) ? agencyBooking.getRoomAllotment() : null);
					HSSFCell busyCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(11), (agencyBooking != null) ? agencyBooking.getRoomBusy() : null);
					if (agencyBooking != null && agencyBooking.getRoomBusy() >= agencyBooking.getRoomAllotment()) {
						paintCell(report, busyCell, HSSFColor.GREEN.index);
					}

					if (agencyBooking != null) {
						for (String breakdown : agencyBreakdownMap.get(agency).getBreakdowns()) {
							Integer rooms = agencyBooking.getOccupationBreakdownMap().get(breakdown);
							HSSFCell breakdownCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(11), (rooms != null) ? rooms : null);
							paintCell(report, breakdownCell, HSSFColor.CORNFLOWER_BLUE.index);
						}
					}

					HSSFCell availCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(12), (agencyBooking != null) ? agencyBooking.getRoomAvailable() : null);
					if (agencyBooking != null && dayBooking.getRoomFreePotential() < 0 && agencyBooking.getRoomAvailable() > 0) {
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
		report.addHeaderCell("REAL", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("POTENCIAL", 0, cellStyleBlack);
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
		for (int i=0, from=10, to=10; i<agencies.length; i++, from=to) {
			report.addHeaderCell(agencies[i], 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			to = to + 3;
			if (agencyBreakdownMap.containsKey(agencies[i])) {
				for (int j=0; j<agencyBreakdownMap.get(agencies[i]).getBreakdowns().size(); j++) {
					report.addHeaderCell("", 0, cellStyleBlue);
					to++;
				}
			}
			report.addMergedRegion(0, 0, from, to-1);
		}

		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("hotel", Types.VARCHAR, "HOTEL", 30));
		metadata.getColumns().add(new ReportColumnMetadata("date", Types.DATE, "FECHA", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusy", Types.INTEGER, "OCUP.", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFree", Types.INTEGER, "LIBRE", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBlocked", Types.INTEGER, "BLOQ.", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomTotal", Types.INTEGER, "TOTAL", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomAllotment", Types.INTEGER, "CUPO", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomAvailable", Types.INTEGER, "DISP.", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusyPotential", Types.INTEGER, "OCUP.", 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFreePotential", Types.INTEGER, "LIBRE", 30));
		for (int i=0; i<agencies.length; i++) {
			metadata.getColumns().add(new ReportColumnMetadata("agencyAllotment", Types.INTEGER, "CUPO", 30));
			metadata.getColumns().add(new ReportColumnMetadata("agencyBusy", Types.INTEGER, "OCUP.", 30));
			if (agencyBreakdownMap.containsKey(agencies[i])) {
				for (int j=0; j<agencyBreakdownMap.get(agencies[i]).getBreakdowns().size(); j++) {
					String breakdown = agencyBreakdownMap.get(agencies[i]).getBreakdowns().get(j);
					metadata.getColumns().add(new ReportColumnMetadata("agencyBusy_" + breakdown, Types.INTEGER, breakdown, 30));
				}
			}
			metadata.getColumns().add(new ReportColumnMetadata("agencyAvailable", Types.INTEGER, "DISP.", 30));
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

		public Map<String, DayAgencyBooking> getAgencyBookingMap() {
			return agencyBookingMap;
		}
		public void setAgencyBookingMap(Map<String, DayAgencyBooking> agencyBookingMap) {
			this.agencyBookingMap = agencyBookingMap;
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

	/***************** DAY AGENCY BOOKING *********************************/

	public static class DayAgencyBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Integer roomAllotment;
		private Integer roomBusy;
		private Integer roomAvailable;
		private Map<String, Integer> occupationBreakdownMap;

		public DayAgencyBooking() {
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

	/***************** AGENCY BREAKDOWN *********************************/

	public static class AgencyBreakdown implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private List<String> breakdowns;
		private int agencySize = 3;

		public AgencyBreakdown() {
			breakdowns = new LinkedList<String>();
		}

		public List<String> getBreakdowns() {
			return breakdowns;
		}
		public void setBreakdowns(List<String> breakdowns) {
			this.breakdowns = breakdowns;
		}

		public int getBreakdownsSize() {
			return agencySize + getBreakdowns().size();
		}

	}

}
