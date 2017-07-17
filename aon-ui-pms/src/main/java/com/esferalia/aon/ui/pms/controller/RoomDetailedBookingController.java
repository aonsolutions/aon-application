package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.PMS_BLOCKED_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_CHECKIN_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_CHECKOUT_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_FREE;
import static com.code.aon.ui.common.ICommonMessages.PMS_HOTEL;
import static com.code.aon.ui.common.ICommonMessages.PMS_OCCUPATION_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.PMS_TOTAL;

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
import org.apache.poi.ss.usermodel.CellStyle;

import com.code.aon.AonVersion;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.customer.Customer;
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

public class RoomDetailedBookingController extends DataScrollerState implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel[] hotels;
	private Customer[] agencies;
	private Date fromDate;
	private Date toDate;
	private String[] roomTypeList;
	private int hotelRoomTypesCount;

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

	public String[] getRoomTypeList() {
		return roomTypeList;
	}
	public void setRoomTypeList(String[] roomTypeList) {
		this.roomTypeList = roomTypeList;
	}

	public int getHotelRoomTypesCount() {
		return hotelRoomTypesCount;
	}
	public void setHotelRoomTypesCount(int hotelRoomTypesCount) {
		this.hotelRoomTypesCount = hotelRoomTypesCount;
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
		setFromDate(new Date());
		setToDate(DateUtils.addWeeks(new Date(), 2));
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
				String roomCode = bookingRs.getString(ROOM_CODE);
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
						if (type == BookingStayType.CHECKIN.ordinal()) {
							dayBooking.setRoomCheckin(dayBooking.getRoomCheckin() + rooms);
							dayBooking.setRoomBusy(dayBooking.getRoomBusy() + rooms);
						} else if (type == BookingStayType.CHECKOUT.ordinal()) {
							dayBooking.setRoomCheckout(dayBooking.getRoomCheckout() + rooms);
						} else if (type == BookingStayType.STAY.ordinal()) {
							dayBooking.setRoomBusy(dayBooking.getRoomBusy() + rooms);
						} else {
							dayBooking.setRoomBlocked(dayBooking.getRoomBlocked() + rooms);
						}

						DayRoomTypeBooking dayRoomTypeBooking = new DayRoomTypeBooking();
						if (dayBooking.getRoomTypeBookingMap().containsKey(roomCode)) {
							dayRoomTypeBooking = dayBooking.getRoomTypeBookingMap().get(roomCode);
						}
						if (type == BookingStayType.CHECKIN.ordinal()) {
							dayRoomTypeBooking.setRoomCheckin(rooms);
							dayRoomTypeBooking.setRoomBusy(dayRoomTypeBooking.getRoomBusy() + rooms);
						} else if (type == BookingStayType.CHECKOUT.ordinal()) {
							dayRoomTypeBooking.setRoomCheckout(rooms);
						} else if (type == BookingStayType.STAY.ordinal()) {
							dayRoomTypeBooking.setRoomBusy(dayRoomTypeBooking.getRoomBusy() + rooms);
						} else {
							dayRoomTypeBooking.setRoomBlocked(rooms);
						}
						dayBooking.getRoomTypeBookingMap().put(roomCode, dayRoomTypeBooking);

						if (!ArrayUtils.contains(roomTypeList, roomCode)) {
							roomTypeList = (String[])ArrayUtils.add(roomTypeList, roomCode);
						}
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
			SQLUtils.closeQuietly(agencyBookingRs);
			SQLUtils.closeQuietly(agencyBookingStmt);
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private void initializeBookingList(Connection connection) throws AonSQLException {
		setBookingList(new LinkedList<DayBooking>());
		setRoomTypeList(ArrayUtils.EMPTY_STRING_ARRAY);

		PreparedStatement totalStmt = null;
		ResultSet totalRs = null;
		try {
			totalStmt = connection.prepareStatement(getRoomTotalSQL());
			totalRs = totalStmt.executeQuery();
			while (totalRs.next()) {
				String hotel = totalRs.getString(HOTEL);
				String roomCode = totalRs.getString(ROOM_CODE);
				int rooms = totalRs.getInt(ROOMS);
				for (Date date=DateUtils.truncate(getFromDate(), Calendar.DATE); !date.after(getToDate()); date=DateUtils.addDays(date, 1)) {
					DayBooking dayBooking = new DayBooking();
					dayBooking.setHotel(hotel);
					dayBooking.setDate(date);
					dayBooking.setRoomTotal(rooms);
					int index = getBookingList().indexOf(dayBooking);
					if (index >= 0) {
						dayBooking = getBookingList().get(index);
						dayBooking.setRoomTotal(dayBooking.getRoomTotal() + rooms);
					} else {
						getBookingList().add(dayBooking);
					}
					DayRoomTypeBooking dayRoomTypeBooking = new DayRoomTypeBooking();
					dayRoomTypeBooking.setRoomTotal(rooms);
					dayRoomTypeBooking.setHotelRoomType(true);
					dayBooking.getRoomTypeBookingMap().put(roomCode, dayRoomTypeBooking);

					if (!ArrayUtils.contains(roomTypeList, roomCode)) {
						roomTypeList = (String[])ArrayUtils.add(roomTypeList, roomCode);
					}
				}
			}
			setHotelRoomTypesCount(roomTypeList.length);
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(totalRs);
			SQLUtils.closeQuietly(totalStmt);
		}
	}

	private String getRoomTotalSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", P.code AS "+ ROOM_CODE + ", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM room AS R, hotel as H, workplace AS W, item AS I, product AS P");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND R.item = I.id");
		stmt.append(" AND I.product = P.id");
		stmt.append(" GROUP BY W.description, P.code");
		stmt.append(" ORDER BY " + HOTEL + "," + ROOM_CODE);

		return stmt.toString();
	}

	private String getRoomBookingSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT W.description AS " + HOTEL + ", P.code AS " + ROOM_CODE + ", B.stay_date AS " + STAY_DATE + ", B.stay_type AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM booking AS B, hotel AS H, workplace AS W, item AS I, product AS P");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND B.item = I.id");
		stmt.append(" AND I.product = P.id");
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		if (ArrayUtils.isNotEmpty(getAgencies())) {
			stmt.append(" AND B.agency IN (" + getAgencyIds() + ")");
		}
		stmt.append(" GROUP BY W.description, P.code, B.stay_date, B.stay_type");
		stmt.append(" UNION ");
		stmt.append("SELECT W.description AS " + HOTEL + ", P.code AS " + ROOM_CODE + ", AA.date AS " + STAY_DATE + ", 10 AS " + STAY_TYPE);
		stmt.append(", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM asset_activity AS AA, room AS R, hotel as H, workplace AS W, item AS I, product AS P");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.hotel = H.id");
		stmt.append(" AND H.active = 1");
		stmt.append(" AND H.workplace = W.id");
		stmt.append(" AND R.active = 1");
		stmt.append(" AND R.hotel IN (" + getHotelIds() + ")");
		stmt.append(" AND R.item = I.id");
		stmt.append(" AND I.product = P.id");
		stmt.append(" AND AA.asset = R.asset");
		stmt.append(" AND AA.status <> " + ActivityStatus.BUSY.getValue());
		stmt.append(" AND AA.date BETWEEN ? AND ?");
		stmt.append(" GROUP BY W.description, P.code, AA.date");
		stmt.append(" ORDER BY " + HOTEL + "," + ROOM_CODE + "," + STAY_DATE + "," + STAY_TYPE);

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

	public String onExcelReport() {
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
		String fileName = "Booking por Tipo de Habitacion";
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
				report.exportColumn(metadata.getColumns().get(2), dayBooking.getRoomCheckin());
				report.exportColumn(metadata.getColumns().get(3), dayBooking.getRoomCheckout());
				report.exportColumn(metadata.getColumns().get(4), dayBooking.getRoomBusy());
				HSSFCell busyCell = (HSSFCell)report.exportColumn(metadata.getColumns().get(5), dayBooking.getRoomBusyPercent().toString() + "%");
				alignCell(report, busyCell, CellStyle.ALIGN_RIGHT);
				report.exportColumn(metadata.getColumns().get(6), dayBooking.getRoomFree());
				report.exportColumn(metadata.getColumns().get(7), dayBooking.getRoomBlocked());
				report.exportColumn(metadata.getColumns().get(8), dayBooking.getRoomTotal());
				for (String roomType : roomTypeList) {
					DayRoomTypeBooking roomTypeBooking = dayBooking.getRoomTypeBookingMap().get(roomType);
					report.exportColumn(metadata.getColumns().get(9), (roomTypeBooking != null) ? roomTypeBooking.getRoomCheckin() : null);
					report.exportColumn(metadata.getColumns().get(10), (roomTypeBooking != null) ? roomTypeBooking.getRoomCheckout() : null);
					report.exportColumn(metadata.getColumns().get(11), (roomTypeBooking != null) ? roomTypeBooking.getRoomBusy() : null);
					HSSFCell cell = (HSSFCell)report.exportColumn(metadata.getColumns().get(12), (roomTypeBooking != null) ? roomTypeBooking.getRoomFree() : null);
					if (roomTypeBooking != null && roomTypeBooking.getRoomFree() < 0) {
						paintCell(report, cell, HSSFColor.RED.index);
					}
					report.exportColumn(metadata.getColumns().get(13), (roomTypeBooking != null) ? roomTypeBooking.getRoomBlocked() : null);
					report.exportColumn(metadata.getColumns().get(14), (roomTypeBooking != null) ? roomTypeBooking.getRoomTotal() : null);
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
		report.addHeaderCell(AonUtil.getMessage(PMS_HOTEL).toUpperCase(), 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addHeaderCell("", 0, cellStyleBlack);
		report.addMergedRegion(0, 0, 0, 1);
		report.addMergedRegion(0, 0, 2, 8);

		HSSFCellStyle cellStyleBlue = newExcelHeaderStyle(report);
		cellFont = report.createFont();
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setColor(HSSFColor.BLUE.index);
	    cellStyleBlue.setFont(cellFont);
		HSSFCellStyle cellStyleRed = newExcelHeaderStyle(report);
		cellFont = report.createFont();
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setColor(HSSFColor.RED.index);
	    cellStyleRed.setFont(cellFont);
		for (int i=0, from=9, to=9; i<roomTypeList.length; i++, from=to) {
			report.addHeaderCell(roomTypeList[i], 0, (i<hotelRoomTypesCount) ? cellStyleBlue : cellStyleRed);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			report.addHeaderCell("", 0, cellStyleBlue);
			to = to + 6;
			report.addMergedRegion(0, 0, from, to-1);
		}

		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("hotel", Types.VARCHAR, AonUtil.getMessage(PMS_HOTEL).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("date", Types.DATE, AonUtil.getMessage(DATE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomCheckin", Types.INTEGER, AonUtil.getMessage(PMS_CHECKIN_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomCheckout", Types.INTEGER, AonUtil.getMessage(PMS_CHECKOUT_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBusyPercent", Types.VARCHAR, "%" + AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomFree", Types.INTEGER, AonUtil.getMessage(PMS_FREE).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomBlocked", Types.INTEGER, AonUtil.getMessage(PMS_BLOCKED_ABBRV).toUpperCase(), 30));
		metadata.getColumns().add(new ReportColumnMetadata("roomTotal", Types.INTEGER, AonUtil.getMessage(PMS_TOTAL).toUpperCase(), 30));
		for (int i=0; i<roomTypeList.length; i++) {
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeCheckin", Types.INTEGER, AonUtil.getMessage(PMS_CHECKIN_ABBRV).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeCheckout", Types.INTEGER, AonUtil.getMessage(PMS_CHECKOUT_ABBRV).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeBusy", Types.INTEGER, AonUtil.getMessage(PMS_OCCUPATION_ABBRV).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeFree", Types.INTEGER, AonUtil.getMessage(PMS_FREE).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeBlocked", Types.INTEGER, AonUtil.getMessage(PMS_BLOCKED_ABBRV).toUpperCase(), 30));
			metadata.getColumns().add(new ReportColumnMetadata("roomTypeTotal", Types.INTEGER, AonUtil.getMessage(PMS_TOTAL).toUpperCase(), 30));
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
		private Integer roomCheckin;
		private Integer roomCheckout;
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomTotal;
		private Map<String, DayRoomTypeBooking> roomTypeBookingMap;

		public DayBooking() {
			roomCheckin = 0;
			roomCheckout = 0;
			roomBusy = 0;
			roomBlocked = 0;
			roomTotal = 0;
			roomTypeBookingMap = new HashMap<String, DayRoomTypeBooking>();
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

		public Map<String, DayRoomTypeBooking> getRoomTypeBookingMap() {
			return roomTypeBookingMap;
		}
		public void setRoomTypeBookingMap(Map<String, DayRoomTypeBooking> roomTypeBookingMap) {
			this.roomTypeBookingMap = roomTypeBookingMap;
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

	/***************** DAY ROOM TYPE BOOKING *********************************/

	public static class DayRoomTypeBooking implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Integer roomCheckin;
		private Integer roomCheckout;
		private Integer roomBusy;
		private Integer roomBlocked;
		private Integer roomTotal;
		private boolean hotelRoomType;

		public DayRoomTypeBooking() {
			roomCheckin = 0;
			roomCheckout = 0;
			roomBusy = 0;
			roomBlocked = 0;
			roomTotal = 0;
			hotelRoomType = false;
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

		public boolean isHotelRoomType() {
			return hotelRoomType;
		}
		public void setHotelRoomType(boolean hotelRoomType) {
			this.hotelRoomType = hotelRoomType;
		}

		public Integer getRoomFree() {
			return roomTotal - roomBusy - roomBlocked;
		}

	}

}
