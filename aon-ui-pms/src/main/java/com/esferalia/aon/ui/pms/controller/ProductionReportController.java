package com.esferalia.aon.ui.pms.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProductionReportController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductionReportController.class.getName());
	
	private Hotel hotel;
	private Date date;

	private Map<String, ReportObject> productionMap = new HashMap<>(),
			pendingProductionMap = new HashMap<>(), paxMap = new HashMap<>(),
			roomMap = new HashMap<>(), availableRoomMap = new HashMap<>(),
			roomOcupationMap = new HashMap<>(),
			advancePaymethodMap = new HashMap<>(),
			paymethodMap = new HashMap<>();

	private DataModel productionModel, pendingProductionModel, paxModel,
			roomModel, availableRoomModel, roomOcupationModel, ratioModel, advancePaymethodModel,
			paymethodModel, summaryModel;

	private ReportObject productionTotal, ratioTotal, advancePaymethodTotal,
			paymethodTotal;

	private boolean testingProductionSql, testingPendingSql, testingPaxSql,
			testingPaymethodSql, testingRoomsSql, testingOpendateRoomsSql;
	
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

	public Integer getPreviousYear() {
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
		return cal.get(Calendar.YEAR)-1;
	}
	
	public boolean isNevv() {
		return productionModel == null && paxModel == null && roomModel == null
				&& availableRoomModel == null && roomOcupationModel == null
				&& advancePaymethodModel == null && paymethodModel == null
				&& pendingProductionModel == null;
	}
	
	public boolean isTestingProductionQuery() {
		return testingProductionSql;
	}
	
	public boolean isTestingPendingQuery() {
		return testingPendingSql;
	}
	
	public boolean isTestingPaxQuery() {
		return testingPaxSql;
	}
	
	public boolean isTestingPaymethodQuery() {
		return testingPaymethodSql;
	}
	
	public boolean isTestingRoomsQuery() {
		return testingRoomsSql;
	}
	
	public boolean isTestingOpendateRoomsQuery() {
		return testingOpendateRoomsSql;
	}

	public DataModel getProductionModel() {
		return productionModel;
	}
	
	public DataModel getPendingProductionModel() {
		return pendingProductionModel;
	}

	public DataModel getPaxModel() {
		return paxModel;
	}
	
	public DataModel getRoomModel() {
		return roomModel;
	}
	
	public DataModel getAvailableRoomModel() {
		return availableRoomModel;
	}

	public DataModel getRoomOcupationModel() {
		return roomOcupationModel;
	}

	public DataModel getRatioModel() {
		return ratioModel;
	}

	public DataModel getAdvancePaymethodModel() {
		return advancePaymethodModel;
	}
	
	public DataModel getSummaryModel() {
		return summaryModel;
	}

	public DataModel getPaymethodModel() {
		return paymethodModel;
	}
	
	public ReportObject getProductionTotal() {
		return productionTotal;
	}
	public ReportObject getRatioTotal() {
		return ratioTotal;
	}
	public ReportObject getAdvancePaymethodTotal() {
		return advancePaymethodTotal;
	}
	public ReportObject getPaymethodTotal() {
		return paymethodTotal;
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException {
		hotel = null;
		date = DateUtils.addDays(new Date(), -1);
		init();
	}
		
	private void init(){
		testingProductionSql = false;
		testingPendingSql = false;
		testingPaxSql = false;
		testingPaymethodSql = false;
		testingRoomsSql = false;
		testingOpendateRoomsSql = false;
		
		productionMap.clear();
		pendingProductionMap.clear();
		paxMap.clear();
		roomMap.clear();
		availableRoomMap.clear();
		roomOcupationMap.clear();
		advancePaymethodMap.clear();
		paymethodMap.clear();
		
		productionModel = null;
		pendingProductionModel = null;
		paxModel = null;
		roomModel = null;
		availableRoomModel = null;
		roomOcupationModel = null;
		ratioModel = null;
		advancePaymethodModel = null;
		paymethodModel = null;
		summaryModel = null;
		
		productionTotal = null;
		ratioTotal = null;
		advancePaymethodTotal = null;
		paymethodTotal = null;
	}
	
	public void onSearch(ActionEvent event) {
		
		init();
		
		if (getDate().after(new Date())
				|| DateUtils.isSameDay(getDate(), new Date())) {
			date = DateUtils.addDays(new Date(), -1);
			AonUtil.addErrorMessage("La fecha debe ser anterior al dia actual.");
			throw new AbortProcessingException(
					"La fecha debe ser anterior al dia actual.");
		}
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		int defaultInactiveInterval = session.getMaxInactiveInterval();
		
		try {
			session.setMaxInactiveInterval(6*60);
			buildReport();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			session.setMaxInactiveInterval(defaultInactiveInterval);
		}
		
	}
	
	private SerializableListDataModel buildModel(Map<String, ReportObject> map){
		return new SerializableListDataModel( map.entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry::getKey))
				.map(Map.Entry::getValue)
				.collect(Collectors.toList())
				);
	}
	
	private ReportObject buildTotalizeTo(Map<String, ReportObject> map) {
		ReportObject to = new ReportObject();
		to.setDescription("TOTAL");
		to.setDayAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getDayAmount).sum());
		to.setPreviousDayAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getPreviousDayAmount).sum());
		to.setMonthAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getMonthAmount).sum());
		to.setPreviousMonthAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getPreviousMonthAmount).sum());
		to.setYearAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getYearAmount).sum());
		to.setPreviousYearAmount(map.values().stream()
				.map(o -> (ReportObject) o)
				.mapToDouble(ReportObject::getPreviousYearAmount).sum());
		return to;
	}
	
	
	public String onExcelReport() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "informe_produccion";
			dateFormatter.applyPattern("yyyy/MM/dd");
			fileName += "_" + hotel.getWorkPlace().getDescription() + "_" + dateFormatter.format(getDate());
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();

			if(!excelReport(output)){
				AonUtil.addErrorMessage("No existen datos para generar el informe.");
			}
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void buildReport() throws AonSQLException {
		
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
		
		java.sql.Date date = SQLUtils.date2sql(getDate());
		java.sql.Date previousDate = SQLUtils.date2sql((DateUtils.addYears(getDate(), -1)));
		Integer year = cal.get(Calendar.YEAR);
		Integer previousYear = year-1;
		Integer month = cal.get(Calendar.MONTH)+1;
		Integer hotel = getHotel().getId();
		Integer wp = getHotel().getWorkPlace().getId();
		Integer rooms = getRoomsCountHotel(hotel);
		java.sql.Date openDate = getOpenDate(hotel, year);
		
		ApplicationParameter ap = AppParamUtil.getParameter("PMS_PRODUCTION_REPORT_PCATEGORY");
		ApplicationParameter ap_hora_apertura = AppParamUtil.getParameter("PMS_HORA_APERTURA");
		ApplicationParameter ap_hora_cierre = AppParamUtil.getParameter("PMS_HORA_CIERRE");
		
		int productCategory = 0;
		try {
			productCategory = (ap==null || ap.getValue()==null)?11:Integer.parseInt(ap.getValue());
		} catch (Exception e) {
			productCategory = 11;
		}
		String horaApertura = null;
		try {
			horaApertura = (ap_hora_apertura==null || ap_hora_apertura.getValue()==null)?"4":ap_hora_apertura.getValue();
		} catch (Exception e) {
			horaApertura = "4";
		}
		String horaCierre = null;
		try {
			horaCierre = (ap_hora_cierre==null || ap_hora_cierre.getValue()==null)?"03:59:59":ap_hora_cierre.getValue();
		} catch (Exception e) {
			horaApertura = "03:59:59";
		}
		
		Date logDate = new Date();
		LOGGER.info("****** INFORME DE PRODUCCION **************");
		
		
		LOGGER.info("****** SEARCHING FOR TESTING QUERY IN -> " + ConsoleOutput.SQL_FILE);
		
		LOGGER.info("****** Inicio de la busqueda de produccion       -> " + timeFormatter.format(new Date()));
		Date tmpDate = new Date();
		buildProductionReport(date, previousDate, year, previousYear, month, hotel, wp, productCategory, horaApertura, horaCierre);
		long diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de produccion          -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de prod. pendiente  -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPendingProductionReport(date, previousDate, year, previousYear, month, hotel, wp);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de prod. pendiente     -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de opendate room    -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildOpendateroomsReport(date, previousDate, year, previousYear, month, hotel, wp, rooms, openDate);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de opendate room       -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de habitaciones     -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildRoomsReport(date, previousDate, year, previousYear, month, hotel, wp, rooms, openDate);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de habitaciones        -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de pax              -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPaxReport(date, previousDate, year, previousYear, month, hotel, openDate);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de pax                 -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de facturas         -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPaymethodReport(date, previousDate, year, previousYear, month, hotel, wp, horaApertura, horaCierre);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de facturas            -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		diff = (new Date()).getTime() - logDate.getTime();
        LOGGER.info("****** Tiempo TOTAL                              -> " + diff + " seg. (" 
        		+ (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg.)" );
		
	}
	
	private void buildProductionReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer productCategory,
			String horaApertura, String horaCierre) throws AonSQLException {
		Connection connection = null;
		PreparedStatement productionStmt = null;
		ResultSet productionRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				productionStmt = connection
						.prepareStatement(getHotelProductionSQL(date,
								previousDate, year, previousYear, month, hotel,
								wp, productCategory, horaApertura, horaCierre));
				productionRs = productionStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(productionStmt);
				SQLUtils.closeQuietly(productionRs);
				testingProductionSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PRODUCCION, continua con la query por defecto");
				productionStmt = connection
						.prepareStatement(hotelProductionSQL(date,
								previousDate, year, previousYear, month, hotel,
								wp, productCategory, horaApertura, horaCierre));
				productionRs = productionStmt.executeQuery();
			}
			while (productionRs.next()) {
				String description = productionRs.getString(1);
				String period = productionRs.getString(2);
				Double amount = CommonUtil.round(productionRs.getDouble(3));

				if (!productionMap.containsKey(description)) {
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					productionMap.put(description, ro);
				}
				if (period.equals("ANIO")) {
					productionMap.get(description).setYearAmount(amount);
				} else if (period.equals("ANIO_ANTERIOR")) {
					productionMap.get(description)
							.setPreviousYearAmount(amount);
				} else if (period.equals("MES")) {
					productionMap.get(description).setMonthAmount(amount);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					productionMap.get(description).setPreviousMonthAmount(
							amount);
				} else if (period.equals("DIA")) {
					productionMap.get(description).setDayAmount(amount);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					productionMap.get(description).setPreviousDayAmount(amount);
				}
			}

			productionModel = buildModel(productionMap);
			productionTotal = buildTotalizeTo(productionMap);

		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(productionRs);
			SQLUtils.closeQuietly(productionStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void buildPendingProductionReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp) throws AonSQLException {
		Connection connection = null;
		PreparedStatement pendingProductionStmt = null;
		ResultSet pendingProductionRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				pendingProductionStmt = connection
						.prepareStatement(getPendingHotelProductionSQL(date,
								previousDate, hotel, year, previousYear, month,
								wp));
				pendingProductionRs = pendingProductionStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(pendingProductionStmt);
				SQLUtils.closeQuietly(pendingProductionRs);
				testingPendingSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PRODUCCION PENDIENTE, continua con la query por defecto");
				pendingProductionStmt = connection
						.prepareStatement(pendingHotelProductionSQL(date,
								previousDate, hotel, year, previousYear, month,
								wp));
				pendingProductionRs = pendingProductionStmt.executeQuery();
			}
			while (pendingProductionRs.next()) {
				String description = pendingProductionRs.getString(1);
				String period = pendingProductionRs.getString(2);
				Double amount = CommonUtil.round(pendingProductionRs.getDouble(3));

				if (!pendingProductionMap.containsKey(description)) {
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					pendingProductionMap.put(description, ro);
				}

				if (period.equals("ANIO")) {
					double _amount = pendingProductionMap.get(description)
							.getYearAmount();
					pendingProductionMap.get(description).setYearAmount(
							_amount + amount);
				} else if (period.equals("ANIO_ANTERIOR")) {
					double _amount = pendingProductionMap.get(description)
							.getPreviousYearAmount();
					pendingProductionMap.get(description)
							.setPreviousYearAmount(_amount + amount);
				} else if (period.equals("MES")) {
					double _amount = pendingProductionMap.get(description)
							.getMonthAmount();
					pendingProductionMap.get(description).setMonthAmount(
							_amount + amount);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					double _amount = pendingProductionMap.get(description)
							.getPreviousMonthAmount();
					pendingProductionMap.get(description)
							.setPreviousMonthAmount(_amount + amount);
				} else if (period.equals("DIA")) {
					double _amount = pendingProductionMap.get(description)
							.getDayAmount();
					pendingProductionMap.get(description).setDayAmount(
							_amount + amount);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					double _amount = pendingProductionMap.get(description)
							.getPreviousDayAmount();
					pendingProductionMap.get(description).setPreviousDayAmount(
							_amount + amount);
				}
			}

			pendingProductionModel = buildModel(pendingProductionMap);
			summaryModel = buildModel(getSummaryMap());

		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(pendingProductionRs);
			SQLUtils.closeQuietly(pendingProductionStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void buildPaxReport(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel, java.sql.Date opendate)
			throws AonSQLException {
		Connection connection = null;
		PreparedStatement paxStmt = null;
		ResultSet paxRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				paxStmt = connection.prepareStatement(getPaxSQL(date, previousDate,
						year, previousYear, month, hotel, opendate));
				paxRs = paxStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(paxStmt);
				SQLUtils.closeQuietly(paxRs);
				testingPaxSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PAX, continua con la query por defecto");
				paxStmt = connection.prepareStatement(paxSQL(date, previousDate,
						year, previousYear, month, hotel, opendate));
				paxRs = paxStmt.executeQuery();
			}
			
			if (!paxMap.containsKey("PAX")) {
				ReportObject ro = new ReportObject();
				ro.setDescription("PAX");
				paxMap.put("PAX", ro);
			}
			
			while (paxRs.next()) {
				String period = paxRs.getString(1);
				Double amount = CommonUtil.round(paxRs.getDouble(2));
				
				if (period.equals("ANIO")) {
					paxMap.get("PAX").setYearAmount(amount);
				} else if (period.equals("ANIO_ANTERIOR")) {
					paxMap.get("PAX").setPreviousYearAmount(amount);
				} else if (period.equals("MES")) {
					paxMap.get("PAX").setMonthAmount(amount);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					paxMap.get("PAX").setPreviousMonthAmount(amount);
				} else if (period.equals("DIA")) {
					paxMap.get("PAX").setDayAmount(amount);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					paxMap.get("PAX").setPreviousDayAmount(amount);
				}
			}

			paxModel = buildModel(paxMap);
			ratioModel = buildModel(getProductionRatioMap());
			ratioTotal = buildTotalizeTo(getProductionRatioMap());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(paxRs);
			SQLUtils.closeQuietly(paxStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void buildPaymethodReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, String horaApertura,
			String horaCierre) throws AonSQLException {
		Connection connection = null;
		PreparedStatement paymethodStmt = null;
		ResultSet paymethodRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				paymethodStmt = connection
						.prepareStatement(getInvoicePayMethodsSQL(date,
								previousDate, year, previousYear, month, wp, horaApertura, horaCierre));
				paymethodRs = paymethodStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(paymethodStmt);
				SQLUtils.closeQuietly(paymethodRs);
				testingPaymethodSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PAYMETHOD, continua con la query por defecto");
				paymethodStmt = connection
						.prepareStatement(invoicePayMethodsSQL(date,
								previousDate, year, previousYear, month, wp, horaApertura, horaCierre));
				paymethodRs = paymethodStmt.executeQuery();
			}
			while (paymethodRs.next()) {
				String description = paymethodRs.getString(1);
				String type = paymethodRs.getString(2);
				String period = paymethodRs.getString(3);
				Double amount = CommonUtil.round(paymethodRs.getDouble(4));
//				System.out.println(period + " - " + description +" - "+ type + " - " + amount);
				
				Map<String, ReportObject> map = null;
				if (type.equalsIgnoreCase("NORMAL")) {
					map = paymethodMap;
				} else if (type.equalsIgnoreCase("ANTICIPO")) {
					map = advancePaymethodMap;
				}

				if (!map.containsKey(description)) {
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					map.put(description, ro);
				}
				
				if (period.equals("ANIO")) {
					map.get(description).setYearAmount(amount);
				} else if (period.equals("ANIO_ANTERIOR")) {
					map.get(description).setPreviousYearAmount(amount);
				} else if (period.equals("MES")) {
					map.get(description).setMonthAmount(amount);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					map.get(description).setPreviousMonthAmount(amount);
				} else if (period.equals("DIA")) {
					map.get(description).setDayAmount(amount);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					map.get(description).setPreviousDayAmount(amount);
				}
			}

			advancePaymethodModel = buildModel(advancePaymethodMap);
			advancePaymethodTotal = buildTotalizeTo(advancePaymethodMap);

			paymethodModel = buildModel(paymethodMap);
			paymethodTotal = buildTotalizeTo(paymethodMap);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(paymethodRs);
			SQLUtils.closeQuietly(paymethodStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	
	private java.sql.Date getOpenDate(Integer hotel, Integer year) throws AonSQLException {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			stmt = connection.prepareStatement(opendateSQL(year, hotel));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				return rs.getDate(1);
			}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
		return null;
	}
	
	private Integer getRoomsCountHotel(Integer hotel) throws AonSQLException {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			stmt = connection.prepareStatement(roomCountSQL(hotel));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
		return null;
	}
	
	private void buildRoomsReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer rooms, java.sql.Date opendate) throws AonSQLException {
		Connection connection = null;
		PreparedStatement roomsStmt = null;
		ResultSet roomsRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				roomsStmt = connection.prepareStatement(getRoomsSQL(date,
						previousDate, year, previousYear, month, wp, hotel,
						rooms, opendate));
				roomsRs = roomsStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(roomsStmt);
				SQLUtils.closeQuietly(roomsRs);
				testingRoomsSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE ROOMS, continua con la query por defecto");
				roomsStmt = connection.prepareStatement(roomsSQL(date,
						previousDate, hotel, year, previousYear, month, rooms,
						opendate));
				roomsRs = roomsStmt.executeQuery();
			}
			
			if (!availableRoomMap.containsKey("Hab.Ocupadas")) {
				ReportObject ro = new ReportObject();
				ro.setDescription("Hab.Ocupadas");
				availableRoomMap.put("Hab.Ocupadas", ro);
			}
			if (!availableRoomMap.containsKey("Hab.Disponibles")) {
				ReportObject ro = new ReportObject();
				ro.setDescription("Hab.Disponibles");
				availableRoomMap.put("Hab.Disponibles", ro);
			}
			if (!roomOcupationMap.containsKey("% Ocupación")) {
				ReportObject ro = new ReportObject();
				ro.setDescription("% Ocupación");
				roomOcupationMap.put("% Ocupación", ro);
			}
			
			while (roomsRs.next()) {
				String period = roomsRs.getString(1);
				Double roomCount = CommonUtil.round(roomsRs.getDouble(2));
//				Double availablesRoomCount = CommonUtil.round(roomsRs.getDouble(3));
//				Double ocupation = CommonUtil.round(roomsRs.getDouble(4));
//				System.out.println(period + " - " + roomCount +" - "+ availablesRoomCount + " - " + ocupation);
				
				double roomYear = roomMap.get("Habitaciones").getYearAmount();
				double roomPreviousYear = roomMap.get("Habitaciones").getPreviousYearAmount();
				double roomMonth = roomMap.get("Habitaciones").getMonthAmount();
				double roomPreviousMonth = roomMap.get("Habitaciones").getPreviousMonthAmount();
				double roomDay = roomMap.get("Habitaciones").getDayAmount();
				double roomPreviousDay = roomMap.get("Habitaciones").getPreviousDayAmount();
				
				if (period.equals("ANIO")) {
					availableRoomMap.get("Hab.Ocupadas").setYearAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setYearAmount(roomMap.get("Habitaciones").getYearAmount()-roomCount);
					double availableRoomYear = availableRoomMap.get("Hab.Ocupadas").getYearAmount();
					roomOcupationMap.get("% Ocupación").setYearAmount(roomYear > 0.0f ? (availableRoomYear * 100 / roomYear) : 0.0);
				} else if (period.equals("ANIO_ANTERIOR")) {
					availableRoomMap.get("Hab.Ocupadas").setPreviousYearAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setPreviousYearAmount(roomMap.get("Habitaciones").getPreviousYearAmount()-roomCount);
					double availableRoomPreviousYear = availableRoomMap.get("Hab.Ocupadas").getPreviousYearAmount();
					roomOcupationMap.get("% Ocupación").setPreviousYearAmount(roomPreviousYear > 0.0f ? (availableRoomPreviousYear * 100 / roomPreviousYear) : 0.0);
				} else if (period.equals("MES")) {
					availableRoomMap.get("Hab.Ocupadas").setMonthAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setMonthAmount(roomMap.get("Habitaciones").getMonthAmount()-roomCount);
					double availableRoomMonth = availableRoomMap.get("Hab.Ocupadas").getMonthAmount();
					roomOcupationMap.get("% Ocupación").setMonthAmount(roomMonth > 0.0f ? (availableRoomMonth * 100 / roomMonth) : 0.0);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					availableRoomMap.get("Hab.Ocupadas").setPreviousMonthAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setPreviousMonthAmount(roomMap.get("Habitaciones").getPreviousMonthAmount()-roomCount);
					double availableRoomPreviousMonth = availableRoomMap.get("Hab.Ocupadas").getPreviousMonthAmount();
					roomOcupationMap.get("% Ocupación").setPreviousMonthAmount(roomPreviousMonth > 0.0f ? (availableRoomPreviousMonth * 100 / roomPreviousMonth) : 0.0);
				} else if (period.equals("DIA")) {
					availableRoomMap.get("Hab.Ocupadas").setDayAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setDayAmount(roomMap.get("Habitaciones").getDayAmount()-roomCount);
					double availableRoomDay = availableRoomMap.get("Hab.Ocupadas").getDayAmount();
					roomOcupationMap.get("% Ocupación").setDayAmount(roomDay > 0.0f ? (availableRoomDay * 100 / roomDay) : 0.0);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					availableRoomMap.get("Hab.Ocupadas").setPreviousDayAmount(roomCount);
					availableRoomMap.get("Hab.Disponibles").setPreviousDayAmount(roomMap.get("Habitaciones").getPreviousDayAmount()-roomCount);
					double availableRoomPreviousDay = availableRoomMap.get("Hab.Ocupadas").getPreviousDayAmount();
					roomOcupationMap.get("% Ocupación").setPreviousDayAmount(roomPreviousDay > 0.0f ? (availableRoomPreviousDay * 100 / roomPreviousDay) : 0.0);
				}
			}
			
			availableRoomModel = buildModel(availableRoomMap);
			roomOcupationModel = buildModel(roomOcupationMap);
			
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(roomsRs);
			SQLUtils.closeQuietly(roomsStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void buildOpendateroomsReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer rooms, java.sql.Date opendate) throws AonSQLException {
		Connection connection = null;
		PreparedStatement opendateRoomsStmt = null;
		ResultSet opendateRoomsRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				opendateRoomsStmt = connection
						.prepareStatement(getOpendateRoomsSQL(date,
								previousDate, year, previousYear, month, wp,
								hotel, rooms, opendate));
				opendateRoomsRs = opendateRoomsStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(opendateRoomsStmt);
				SQLUtils.closeQuietly(opendateRoomsRs);
				testingOpendateRoomsSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE ROOMS, continua con la query por defecto");
				opendateRoomsStmt = connection
						.prepareStatement(opendateroomsSQL(date, previousDate,
								hotel, year, previousYear, month, rooms,
								opendate));
				opendateRoomsRs = opendateRoomsStmt.executeQuery();
			}
			
			if (!roomMap.containsKey("Habitaciones")) {
				ReportObject ro = new ReportObject();
				ro.setDescription("Habitaciones");
				roomMap.put("Habitaciones", ro);
			}
			
			while (opendateRoomsRs.next()) {
				String period = opendateRoomsRs.getString(1);
				Double ocupation = CommonUtil.round(opendateRoomsRs.getDouble(2));
//				Double days = CommonUtil.round(opendateRoomsRs.getDouble(3));
//				System.out.println(period + " - " + ocupation + " - " + days);
				
				if (period.equals("ANIO")) {
					roomMap.get("Habitaciones").setYearAmount(ocupation);
				} else if (period.equals("ANIO_ANTERIOR")) {
					roomMap.get("Habitaciones").setPreviousYearAmount(ocupation);
				} else if (period.equals("MES")) {
					roomMap.get("Habitaciones").setMonthAmount(ocupation);
				} else if (period.equals("MES_ANIO_ANTERIOR")) {
					roomMap.get("Habitaciones").setPreviousMonthAmount(ocupation);
				} else if (period.equals("DIA")) {
					roomMap.get("Habitaciones").setDayAmount(ocupation);
				} else if (period.equals("DIA_ANIO_ANTERIOR")) {
					roomMap.get("Habitaciones").setPreviousDayAmount(ocupation);
				}
			}
			
			roomModel = buildModel(roomMap);
			
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(opendateRoomsRs);
			SQLUtils.closeQuietly(opendateRoomsStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private String getLocalSQL(String fileName, boolean printOutput) {
		if (Files.exists(Paths.get(ConsoleOutput.SQL_FILE + fileName))) {
			LOGGER.info("****** TESTING QUERY DETECTED -> " + ConsoleOutput.SQL_FILE + fileName);
			try {
				if(printOutput){
					ConsoleOutput.sqlToJava(fileName);
				}
				String query = new String( Files.readAllBytes(Paths.get(ConsoleOutput.SQL_FILE + fileName)) );
				query = query.replaceAll("SET @.*;", "");
				return query;
			} catch (FileNotFoundException e) {
				// nada
			} catch (IOException e) {
				// nada
			}
		}
		return null;
	}
	
	private String getHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer productCategory,
			String horaApertura, String horaCierre) {
		String query = getLocalSQL(ConsoleOutput.production, false);
		if (query != null) {
			testingProductionSql = true;
			query = query
					.replaceAll("@date", "'" + date.toString() + "'")
					.replaceAll("@previousDate",
							"'" + previousDate.toString() + "'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("@wp", wp.toString())
					.replaceAll("@productCategory", productCategory.toString())
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = hotelProductionSQL(date, previousDate, year, previousYear,
					month, hotel, wp, productCategory, horaApertura, horaCierre);
		}
		return query;
	}

	private String getPendingHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month, Integer wp) {
		String query = getLocalSQL(ConsoleOutput.pending, false);
		if (query != null) {
			testingPendingSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = pendingHotelProductionSQL(date, previousDate, hotel,
					year, previousYear, month, wp);
		}
		return query;
	}

	private String getPaxSQL(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel, java.sql.Date opendate) {
		String query = getLocalSQL(ConsoleOutput.pax, false);
		if (query != null) {
			testingPaxSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("@opendate", "'"+opendate.toString()+"'")
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = paxSQL(date, previousDate, year, previousYear, month,
					hotel, opendate);
		}
		return query;
	}

	private String getInvoicePayMethodsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp, String horaApertura, String horaCierre) {
		String query = getLocalSQL(ConsoleOutput.paymethod, false);
		if (query != null) {
			testingPaymethodSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@wp", wp.toString())
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = invoicePayMethodsSQL(date, previousDate, year,
					previousYear, month, wp, horaApertura, horaCierre);
		}
		return query;
	}
	
	private String getRoomsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp, Integer hotel, Integer rooms, java.sql.Date opendate) {
		String query = getLocalSQL(ConsoleOutput.rooms, false);
		if (query != null) {
			testingRoomsSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("@wp", wp.toString())
					.replaceAll("@rooms", rooms.toString())
					.replaceAll("@opendate", "'"+opendate.toString()+"'")
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = roomsSQL(date, previousDate, hotel, year, previousYear, month, rooms, opendate);
		}
		return query;
	}
	
	private String getOpendateRoomsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp, Integer hotel, Integer rooms, java.sql.Date opendate) {
		String query = getLocalSQL(ConsoleOutput.opendaterooms, false);
		if (query != null) {
			testingOpendateRoomsSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@wp", wp.toString())
					.replaceFirst("[\n|\t|\r]*", "");
		} else {
			query = opendateroomsSQL(date, previousDate, hotel, year,
					previousYear, month, rooms, opendate);
		}
		return query;
	}
	
	
	private Map<String, ReportObject> getProductionRatioMap() {
		Map<String, ReportObject> productionRatioMap = new HashMap<>();
		productionMap.keySet().stream().sorted().forEach(key -> {
			fillPaxRatioMapObject(key, productionMap, productionRatioMap);
		});
		
		fillRoomPaxRatioMapObject("PAX", " PAX por Habitación", paxMap, productionRatioMap);
		
		fillRoomPaxRatioMapObject("1.VENTAS", " VENTAS/Habitación", pendingProductionMap, productionRatioMap);
		
		fillPaxRatioMapObject("1.VENTAS", pendingProductionMap, productionRatioMap);
		
		return productionRatioMap;
	}
	
	private void fillPaxRatioMapObject(String key, Map<String, ReportObject> productionMap, Map<String, ReportObject> productionRatioMap){
		ReportObject ro = new ReportObject();
		if( productionMap.containsKey(key) ){
			String description = productionMap.get(key).getDescription().replace("1.", " ");
			ro.setDescription(description+"/Pax");
			double paxDayAmount = paxMap.values().stream().mapToDouble(ReportObject::getDayAmount).sum();
			ro.setDayAmount(paxDayAmount > 0.0f ? (productionMap.get(key).getDayAmount() / paxDayAmount) : 0.0);
			double paxPreviousDayAmount = paxMap.values().stream().mapToDouble(ReportObject::getPreviousDayAmount).sum();
			ro.setPreviousDayAmount(paxPreviousDayAmount > 0.0f ? (productionMap.get(key).getPreviousDayAmount() / paxPreviousDayAmount) : 0.0);
			double paxMonthAmount = paxMap.values().stream().mapToDouble(ReportObject::getMonthAmount).sum();
			ro.setMonthAmount(paxMonthAmount > 0.0f ? (productionMap.get(key).getMonthAmount() / paxMonthAmount) : 0.0);			 
			double paxPreviousMonthAmount = paxMap.values().stream().mapToDouble(ReportObject::getPreviousMonthAmount).sum();
			ro.setPreviousMonthAmount(paxPreviousMonthAmount > 0.0f ? (productionMap.get(key).getPreviousMonthAmount() / paxPreviousMonthAmount) : 0.0);
			double paxYearAmount = paxMap.values().stream().mapToDouble(ReportObject::getYearAmount).sum();
			ro.setYearAmount(paxYearAmount > 0.0f ? (productionMap.get(key).getYearAmount() / paxYearAmount) : 0.0);
			double paxPreviousYearAmount = paxMap.values().stream().mapToDouble(ReportObject::getPreviousYearAmount).sum();
			ro.setPreviousYearAmount(paxPreviousYearAmount > 0.0f ? (productionMap.get(key).getPreviousYearAmount() / paxPreviousYearAmount) : 0.0);
		}
		productionRatioMap.put(key, ro);
	}
	
	
	private void fillRoomPaxRatioMapObject(String key, String description, Map<String, ReportObject> map, Map<String, ReportObject> productionRatioMap){
		double roomYear = roomMap.get("Habitaciones").getYearAmount() 
				- availableRoomMap.get("Hab.Disponibles").getYearAmount();
		double roomPreviousYear = roomMap.get("Habitaciones").getPreviousYearAmount() 
				-  availableRoomMap.get("Hab.Disponibles").getPreviousYearAmount();
		
		double roomMonth = roomMap.get("Habitaciones").getMonthAmount()
				- availableRoomMap.get("Hab.Disponibles").getMonthAmount();
		double roomPreviousMonth = roomMap.get("Habitaciones").getPreviousMonthAmount()
				- availableRoomMap.get("Hab.Disponibles").getPreviousMonthAmount();
		
		double roomDay = roomMap.get("Habitaciones").getDayAmount()
				- availableRoomMap.get("Hab.Disponibles").getDayAmount();
		double roomPreviousDay = roomMap.get("Habitaciones").getPreviousDayAmount()
				- availableRoomMap.get("Hab.Disponibles").getPreviousDayAmount();
		
		double yearAmount = map.get(key).getYearAmount();
		double previousYearAmount = map.get(key).getPreviousYearAmount();
		double monthAmount = map.get(key).getMonthAmount();
		double previousMonthAmount = map.get(key).getPreviousMonthAmount();
		double dayAmount = map.get(key).getDayAmount();
		double previousDayAmount = map.get(key).getPreviousDayAmount();
		
		ReportObject ro = new ReportObject();
		ro.setDescription(description);		
		ro.setYearAmount(roomYear != 0.0f ? ( yearAmount / roomYear) : 0.0);
		ro.setPreviousYearAmount(roomPreviousYear != 0.0f ? ( previousYearAmount / roomPreviousYear) : 0.0);
		ro.setMonthAmount(roomMonth != 0.0f ? ( monthAmount / roomMonth) : 0.0);
		ro.setPreviousMonthAmount(roomPreviousMonth != 0.0f ? ( previousMonthAmount / roomPreviousMonth) : 0.0);
		ro.setDayAmount(roomDay != 0.0f ? ( dayAmount / roomDay) : 0.0);
		ro.setPreviousDayAmount(roomPreviousDay != 0.0f ? ( previousDayAmount / roomPreviousDay) : 0.0);
		productionRatioMap.put(description, ro);
	}
	
	private Map<String, ReportObject> getSummaryMap() {
		Map<String, ReportObject> summaryMap = new HashMap<>();

		String ALOJAMIENTO = "ALOJAMIENTO";
		String OTROS_INGRESOS = "OTROS INGRESOS";
		String VENTAS = "1.VENTAS";
		String SALDO_CTA_CLIENTE = "3.SALDO CTA. CLIENTE";

		double ALOJAMIENTO_YearAmount = productionMap
				.containsKey(ALOJAMIENTO) ? productionMap.get(ALOJAMIENTO)
				.getYearAmount() : 0.0;
		double OTROS_INGRESOS_YearAmount = productionMap
				.containsKey(OTROS_INGRESOS) ? productionMap
				.get(OTROS_INGRESOS).getYearAmount() : 0.0;

		double VENTAS_YearAmount = pendingProductionMap
				.containsKey(VENTAS) ? pendingProductionMap.get(VENTAS)
				.getYearAmount() : 0.0;
		double SALDO_CTA_CLIENTE_DayAmount = pendingProductionMap
				.containsKey(SALDO_CTA_CLIENTE) ? pendingProductionMap.get(
				SALDO_CTA_CLIENTE).getDayAmount() : 0.0;
		
		ReportObject ro = new ReportObject();
		ro.setDescription("1.FACTURACION RESERVAS");
		ro.setDayAmount( ALOJAMIENTO_YearAmount + OTROS_INGRESOS_YearAmount);
		summaryMap.put("1.FACTURACION RESERVAS", ro);
		
		ro = new ReportObject();
		ro.setDescription("2.PRODUCCION");
		ro.setDayAmount( (VENTAS_YearAmount + SALDO_CTA_CLIENTE_DayAmount ) * 1.1 );
		summaryMap.put("2.PRODUCCION", ro);
		
		ro = new ReportObject();
		ro.setDescription("3.DIFERENCIA");
		ro.setDayAmount(
				(ALOJAMIENTO_YearAmount + OTROS_INGRESOS_YearAmount)
				- ((VENTAS_YearAmount + SALDO_CTA_CLIENTE_DayAmount ) * 1.1));
		summaryMap.put("3.DIFERENCIA", ro);
		
		return summaryMap;
	}
	
	
	// *********************************************
	// EXCEL REPORT
	// *********************************************
	private boolean excelReport(OutputStream output) throws IOException, ReportException, AonConnectionException {
		
		ExcelReportExporter exporter = new ExcelReportExporter();

		exporter.startExport("Informe");
		ReportMetadata columnMetadata = getContractColumnMetadata();
		exporter.exportHeader(columnMetadata);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "FACTURACION POR AREA", productionMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportInnerHeader(exporter, columnMetadata, "PRODUCCION");
		exportData(exporter, columnMetadata, null, pendingProductionMap, false);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "PAX-ROOMS", paxMap, false);
		exportData(exporter, columnMetadata, null, roomMap, false);
		exportData(exporter, columnMetadata, null, availableRoomMap, false);
		exportData(exporter, columnMetadata, null, roomOcupationMap, false);

		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "RATIOS", getProductionRatioMap(), false);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "FRAS. ANTICIPO (Forma de pago)", advancePaymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "FACTURAS (Forma de pago)", paymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportTotalizeRow(exporter, columnMetadata, advancePaymethodMap, paymethodMap);
		
//		exporter.startLine();
//		exporter.endLine();
//		exportData(exporter, columnMetadata, "RESUMEN", getSummaryMap(), false, true);
		
		exporter.endExport(output);
		output.flush();
		return true;
	}
	
	private ReportMetadata getContractColumnMetadata() throws ReportException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("yyyy/MM/dd");
		String title = dateFormatter.format(getDate()) + " - " + hotel.getWorkPlace().getDescription();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer previousYear = cal.get(Calendar.YEAR)-1;
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("TITLE",Types.VARCHAR,title,30));
		metadata.getColumns().add(new ReportColumnMetadata("DAY",Types.DOUBLE,"Dia",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_DAY",Types.DOUBLE,"Dia (" +previousYear+ ")",15));
		metadata.getColumns().add(new ReportColumnMetadata("MONTH",Types.DOUBLE,"Mes",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_MONTH",Types.DOUBLE,"Mes (" +previousYear+ ")",15));
		metadata.getColumns().add(new ReportColumnMetadata("YEAR",Types.DOUBLE,"Año",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_YEAR",Types.DOUBLE,"Año (" +previousYear+ ")",15));
		return metadata;
	}

	private void exportInnerHeader(ExcelReportExporter exporter,
			ReportMetadata metadata, String description) throws ReportException {
		int column = 0;
		exporter.startLine();
		try {
			HSSFCellStyle cellStyle = exporter.createHeaderStyle();
			exporter.exportColumn(metadata.getColumns().get(column++), description, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
			addEmptyDecimalCell(exporter, cellStyle);
		} catch (ReportException e) {
			LOGGER.error("No se ha podido completar la fila del informe de produccion.");
		} finally {
			exporter.endLine();
		}
	}
	
	private void exportData(ExcelReportExporter exporter,
			ReportMetadata metadata, String description,
			Map<String, ReportObject> map, boolean totalize)
			throws ReportException {
		exportData(exporter, metadata, description, map, totalize, false);
	}
	
	private void exportData(ExcelReportExporter exporter,
			ReportMetadata metadata, String description,
			Map<String, ReportObject> map, boolean totalize, boolean defaultEmpty)
			throws ReportException {
		
		if(StringUtils.isNotBlank(description)){
			HSSFCellStyle cellStyle = exporter.createHeaderStyle();
			exporter.startLine();
			exporter.exportColumn(metadata.getColumns().get(0), description, cellStyle);
			for(int i = 1; i<7; i++){
				addEmptyDecimalCell(exporter, cellStyle);
			}
			exporter.endLine();
		}
		
		map.keySet().stream().sorted().forEach(key -> {
			int column = 0;
			exporter.startLine();
			try {
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getDescription());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getDayAmount());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getPreviousDayAmount());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getMonthAmount());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getPreviousMonthAmount());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getYearAmount());
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getPreviousYearAmount());
			} catch (ReportException e) {
				LOGGER.error("No se ha podido completar la fila del informe de produccion.");
			} finally {
				exporter.endLine();
			}
		});
		
		if(totalize){
			int column = 0;
			HSSFCellStyle cellStyle = exporter.createFooterStyle();
			exporter.startLine();
			try {
				exporter.exportColumn(metadata.getColumns().get(column++), "", cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getDayAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getPreviousDayAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getMonthAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getPreviousMonthAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getYearAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), CommonUtil.round(map.values().stream().mapToDouble(ReportObject::getPreviousYearAmount).sum()), cellStyle);
			} catch (ReportException e) {
				LOGGER.error("No se ha podido completar la fila del informe de produccion.");
			} finally {
				exporter.endLine();
			}
		}
	}
	
	public HSSFCell addEmptyDecimalCell(ExcelReportExporter exporter, HSSFCellStyle cellStyle) {
		HSSFCell cell = exporter.addCell();
		cell.setCellStyle(cellStyle);
		cell.setCellType(Cell.CELL_TYPE_BLANK);
		return cell;
	}
	
	private void exportTotalizeRow(ExcelReportExporter exporter, ReportMetadata metadata, Map<?, ?>... maps ) throws ReportException {
		int column = 0;
		exporter.startLine();
		try {
			Double dayAmount=0.0;
			Double previousDayAmount=0.0;
			Double monthAmount=0.0;
			Double previousMonthAmount=0.0;
			Double yearAmount=0.0;
			Double previousYearAmount=0.0;
			for(Map<?, ?> map: maps){
				dayAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getDayAmount).sum();
				previousDayAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getPreviousDayAmount).sum();
				monthAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getMonthAmount).sum();
				previousMonthAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getPreviousMonthAmount).sum();
				yearAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getYearAmount).sum();
				previousYearAmount+=map.values().stream().map(o -> (ReportObject) o).mapToDouble(ReportObject::getPreviousYearAmount).sum();
			}
			HSSFCellStyle cellStyle = exporter.createFooterStyle();
			exporter.exportColumn(metadata.getColumns().get(column++), "TOTAL", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), dayAmount, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), previousDayAmount, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), monthAmount, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), previousMonthAmount, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), yearAmount, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), previousYearAmount, cellStyle);
		} catch (ReportException e) {
			LOGGER.error("No se ha podido completar la fila del informe de produccion.");
		} finally {
			exporter.endLine();
		}
	}

	
	public class ReportObject {
		private String description;
		private double dayAmount;
		private double previousDayAmount;
		private double monthAmount;
		private double previousMonthAmount;
		private double yearAmount;
		private double previousYearAmount;
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getDayAmount() {
			return dayAmount;
		}
		public void setDayAmount(double dayAmount) {
			this.dayAmount = dayAmount;
		}
		public double getPreviousDayAmount() {
			return previousDayAmount;
		}
		public void setPreviousDayAmount(double previousDayAmount) {
			this.previousDayAmount = previousDayAmount;
		}
		public double getMonthAmount() {
			return monthAmount;
		}
		public void setMonthAmount(double monthAmount) {
			this.monthAmount = monthAmount;
		}
		public double getPreviousMonthAmount() {
			return previousMonthAmount;
		}
		public void setPreviousMonthAmount(double previousMonthAmount) {
			this.previousMonthAmount = previousMonthAmount;
		}
		public double getYearAmount() {
			return yearAmount;
		}
		public void setYearAmount(double yearAmount) {
			this.yearAmount = yearAmount;
		}
		public double getPreviousYearAmount() {
			return previousYearAmount;
		}
		public void setPreviousYearAmount(double previousYearAmount) {
			this.previousYearAmount = previousYearAmount;
		}
		
	}
	
	// *****************************************
	// SQL
	// *****************************************
	
	private String hotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer productCategory, String horaApertura, String horaCierre) {
		
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT Concepto,Periodo,SUM(Importe),IVA FROM (");
		stmt.append("  (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),  'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(YEAR(PS.start_time)="+year+", 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(F.amount) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
		stmt.append("  INNER JOIN pos P        ON  P.id=PS.pos");
		stmt.append("  INNER JOIN finance F    ON F.invoice=I.id");
		stmt.append("  WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL "+horaApertura+" HOUR)         AND date_add('"+date+"',         INTERVAL '1 "+horaCierre+"' DAY_SECOND))");
		stmt.append("        OR (PS.start_time between date_add('"+previousDate+"', INTERVAL "+horaApertura+" HOUR) AND date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)) )");
		stmt.append("  AND P.workplace="+wp+"");
		stmt.append("  AND I.type=1");
		stmt.append("  AND I.id in (SELECT INVD.invoice ");
		stmt.append("                 FROM invoice_detail INVD");
		stmt.append("                 INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("                 INNER JOIN product P           ON IT.product=P.id");
		stmt.append("                WHERE INVD.invoice=I.id ");
		stmt.append("                  AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION       ");
		stmt.append("  (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),      'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(I.issue_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  WHERE (I.issue_date = '"+date+"' OR I.issue_date =  '"+previousDate+"')");
		stmt.append("  AND I.type=1");
		stmt.append("  AND I.pos_shift is NULL");
		stmt.append("  AND I.id in (SELECT INVD.invoice ");
		stmt.append("                 FROM invoice_detail INVD");
		stmt.append("                 INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("                 INNER JOIN product P           ON IT.product=P.id");
		stmt.append("                WHERE INVD.invoice=I.id");
		stmt.append("                  AND INVD.workplace="+wp+"");
		stmt.append("                  AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),      'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(YEAR(PS.start_time)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(F.amount) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
		stmt.append("  INNER JOIN pos P ON P.id=PS.pos ");
		stmt.append("  INNER JOIN finance F    ON F.invoice=I.id");
		stmt.append("  WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)            AND year(PS.start_time)="+year+")");
		stmt.append("        OR (PS.start_time <= date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)    AND year(PS.start_time)="+previousYear+") )");
		stmt.append("    AND month(PS.start_time)="+month+"");
		stmt.append("    AND P.workplace="+wp+"");
		stmt.append("    AND I.type=1");
		stmt.append("    AND I.id in (SELECT INVD.invoice ");
		stmt.append("                   FROM invoice_detail INVD");
		stmt.append("                   INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("                   INNER JOIN product P           ON IT.product=P.id");
		stmt.append("                  WHERE INVD.invoice=I.id ");
		stmt.append("                    AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION");
		stmt.append("         (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),      'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(YEAR(I.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  WHERE (   (I.issue_date <= '"+date+"'         AND year(I.issue_date)="+year+")");
		stmt.append("         OR (I.issue_date <= '"+previousDate+"' AND year(I.issue_date)="+previousYear+") )");
		stmt.append("  AND month(I.issue_date)="+month+"");
		stmt.append("  AND I.type=1");
		stmt.append("  AND I.pos_shift is NULL");
		stmt.append("  AND I.id in (SELECT INVD.invoice ");
		stmt.append("                 FROM invoice_detail INVD");
		stmt.append("                 INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("                 INNER JOIN product P           ON IT.product=P.id");
		stmt.append("                WHERE INVD.invoice=I.id ");
		stmt.append("                  AND INVD.workplace="+wp+"");
		stmt.append("                  AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),      'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(YEAR(PS.start_time)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(F.amount) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
		stmt.append("  INNER JOIN pos P ON P.id=PS.pos ");
		stmt.append("  INNER JOIN finance F    ON F.invoice=I.id");
		stmt.append("  WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)          AND year(PS.start_time)="+year+")");
		stmt.append("        OR (PS.start_time <= date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)  AND year(PS.start_time)="+previousYear+") )");
		stmt.append("    AND P.workplace="+wp+"");
		stmt.append("    AND I.type=1");
		stmt.append("    AND I.id in (SELECT INVD.invoice ");
		stmt.append("                    FROM invoice_detail INVD");
		stmt.append("                    INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("                    INNER JOIN product P           ON IT.product=P.id");
		stmt.append("                 WHERE INVD.invoice=I.id ");
		stmt.append("                   AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT IF(I.project is not NULL,");
		stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),      'OTROS INGRESOS') as Concepto,");
		stmt.append("      IF(YEAR(I.issue_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo, ");
		stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
		stmt.append("  FROM invoice I");
		stmt.append("  WHERE (  (I.issue_date <= '"+date+"'            AND year(I.issue_date)="+year+")");
		stmt.append("        OR (I.issue_date <=  '"+previousDate+"'   AND year(I.issue_date)="+previousYear+") )");
		stmt.append("    AND I.type=1");
		stmt.append("    AND I.pos_shift is NULL");
		stmt.append("    AND I.id in (SELECT INVD.invoice ");
		stmt.append("                   FROM invoice_detail INVD");
		stmt.append("                   INNER JOIN item IT      ON INVD.item=IT.id");
		stmt.append("                   INNER JOIN product P    ON IT.product=P.id");
		stmt.append("                 WHERE INVD.invoice=I.id ");
		stmt.append("                   AND INVD.workplace="+wp+"");
		stmt.append("                   AND P.category <> "+productCategory+")");
		stmt.append("  GROUP BY 1,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT P.name as Concepto,");
		stmt.append("      IF(YEAR(PS.start_time)="+year+", 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append("  FROM invoice INV");
		stmt.append("  INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append("  INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append("  INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("  INNER JOIN product P           ON IT.product=P.id");
		stmt.append("  INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("  INNER JOIN pos PO              ON PO.id=PS.pos");
		stmt.append("  WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL "+horaApertura+" HOUR)         and date_add('"+date+"',         INTERVAL '1 "+horaCierre+"' DAY_SECOND))");
		stmt.append("        OR (PS.start_time between date_add('"+previousDate+"', INTERVAL "+horaApertura+" HOUR) and date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)) )  ");
		stmt.append("    AND PO.workplace="+wp+"");
		stmt.append("    AND INV.type=1");
		stmt.append("    AND P.category = "+productCategory+"");
		stmt.append("  GROUP BY IT.id,INVT.percentage,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT P.name as Concepto,");
		stmt.append("      IF(YEAR(PS.start_time)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append("  FROM invoice INV");
		stmt.append("  INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append("  INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append("  INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("  INNER JOIN product P           ON IT.product=P.id");
		stmt.append("  INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("  INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
		stmt.append("  WHERE ( (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)             AND year(PS.start_time)="+year+")");
		stmt.append("       OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)    AND year(PS.start_time)="+previousYear+") )        ");
		stmt.append("    AND MONTH(PS.start_time)="+month+"");
		stmt.append("    AND INV.type=1");
		stmt.append("    AND P.category = "+productCategory+"");
		stmt.append("    AND INVD.workplace="+wp+"");
		stmt.append("  GROUP BY IT.id,INVT.percentage,2)");
		stmt.append("  UNION");
		stmt.append("  (SELECT P.name as Concepto,");
		stmt.append(" IF(YEAR(PS.start_time)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("      SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append(" FROM invoice INV");
		stmt.append("  INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append("  INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append("  INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append("  INNER JOIN product P           ON IT.product=P.id");
		stmt.append("  INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("  INNER JOIN pos PO              ON PO.id=PS.pos");
		stmt.append("  WHERE ( (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)            AND year(PS.start_time)="+year+")");
		stmt.append("       OR (PS.start_time <= date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)    AND year(PS.start_time)="+previousYear+") )");
		stmt.append("    AND PO.workplace="+wp+"");
		stmt.append("    AND INV.type=1");
		stmt.append("    AND P.category = "+productCategory+"");
		stmt.append("  GROUP BY IT.id,INVT.percentage,2) ) AS Q ");
		stmt.append("  GROUP BY Concepto,Periodo ");
		stmt.append("  ORDER BY Concepto,Periodo ;");
		stmt.append("");
        return stmt.toString();		
	}
	
	private String invoicePayMethodsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp, String horaApertura, String horaCierre) {
		
        StringBuffer stmt = new StringBuffer();
        		stmt.append(" SELECT FormaPago,Tipo,Periodo,SUM(Importe) FROM (     ");
		stmt.append("(SELECT CONCAT('SSCC ',PM.name) as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("           IF(INV.issue_date='"+date+"','DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("           SUM(F.amount) as Importe");
		stmt.append("    FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("    WHERE (INV.issue_date='"+date+"' OR INV.issue_date='"+previousDate+"')");
		stmt.append("      AND INV.type=1");
		stmt.append("      AND INV.pos_shift is NULL");
		stmt.append("      AND INV.id IN (SELECT distinct INVD.invoice");
		stmt.append("                       FROM invoice_detail INVD");
		stmt.append("                      WHERE INVD.workplace="+wp+" ");
		stmt.append("                        AND INVD.invoice=INV.id)");
		stmt.append("      GROUP BY 1,2,3)");
		stmt.append("    UNION");
		stmt.append("    (SELECT CONCAT('SSCC ',PM.name) as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("           IF(YEAR(INV.issue_date)="+year+",'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("           SUM(F.amount) as Importe");
		stmt.append("    FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("     WHERE (  (INV.issue_date <='"+date+"'          AND YEAR(INV.issue_date)="+year+")");
		stmt.append("          OR (INV.issue_date <='"+previousDate+"'   AND YEAR(INV.issue_date)="+previousYear+") )");
		stmt.append("     AND MONTH(INV.issue_date)="+month+"");
		stmt.append("     AND INV.type=1");
		stmt.append("     AND INV.pos_shift is NULL");
		stmt.append("     AND INV.id IN (SELECT distinct INVD.invoice ");
		stmt.append("                      FROM invoice_detail INVD");
		stmt.append("                     WHERE INVD.workplace="+wp+" ");
		stmt.append("                       AND INVD.invoice=INV.id)");
		stmt.append("      GROUP BY 1,2,3)");
		stmt.append("    UNION");
		stmt.append("    (SELECT CONCAT('SSCC ',PM.name) as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("           IF(YEAR(INV.issue_date)="+year+",'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("           SUM(F.amount) as Importe");
		stmt.append("    FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("    WHERE (  (INV.issue_date <='"+date+"'          AND YEAR(INV.issue_date)="+year+")");
		stmt.append("          OR (INV.issue_date <='"+previousDate+"'  AND YEAR(INV.issue_date)="+previousYear+") )");
		stmt.append("    AND INV.type=1");
		stmt.append("    AND INV.pos_shift is NULL");
		stmt.append("    AND INV.id IN (SELECT distinct INVD.invoice ");
		stmt.append("                     FROM invoice_detail INVD");
		stmt.append("                    WHERE INVD.workplace="+wp+" ");
		stmt.append("                      AND INVD.invoice=INV.id)");
		stmt.append("    GROUP BY 1,2,3)");
		stmt.append("    UNION");
		stmt.append("");
		stmt.append("(SELECT PM.name as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("        IF(YEAR(PS.start_time)="+year+",'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        SUM(F.amount) as Importe");
		stmt.append("   FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("    INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos ");
		stmt.append("    WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL "+horaApertura+" HOUR)         and date_add('"+date+"',         INTERVAL '1 "+horaCierre+"' DAY_SECOND))");
		stmt.append("          OR (PS.start_time between date_add('"+previousDate+"', INTERVAL "+horaApertura+" HOUR) and date_add('"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)) )  ");
		stmt.append("      AND PO.workplace="+wp+"");
		stmt.append("      AND INV.type=1");
		stmt.append("      GROUP BY 1,2,3)");
		stmt.append("    UNION");
		stmt.append("    (SELECT PM.name as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("           IF(YEAR(PS.start_time)="+year+",'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("           SUM(F.amount) as Importe");
		stmt.append("    FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("    INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos");
		stmt.append("    WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)             AND year(PS.start_time)="+year+")");
		stmt.append("          OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)    AND year(PS.start_time)="+previousYear+") )        ");
		stmt.append("      AND MONTH(PS.start_time)="+month+"");
		stmt.append("      AND PO.workplace="+wp+"");
		stmt.append("      AND INV.type=1        ");
		stmt.append("      GROUP BY 1,2,3)");
		stmt.append("    UNION");
		stmt.append("    (SELECT PM.name as FormaPago,");
		stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
		stmt.append("           IF(YEAR(PS.start_time)="+year+",'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("           SUM(F.amount) as Importe");
		stmt.append("    FROM finance F");
		stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("    INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
		stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos ");
		stmt.append("    WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)             AND year(PS.start_time)="+year+")");
		stmt.append("          OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL '1 "+horaCierre+"' DAY_SECOND)    AND year(PS.start_time)="+previousYear+") )        ");
		stmt.append("      AND PO.workplace="+wp+"");
		stmt.append("      AND INV.type=1");
		stmt.append("      GROUP BY 1,2,3) ) AS W ");
		stmt.append("  GROUP BY Periodo,Tipo,FormaPago ");
		stmt.append("  ORDER BY Periodo,Tipo,FormaPago ;");
		stmt.append("");

        return stmt.toString();
	}
		
	private String paxSQL(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel,
			java.sql.Date opendate) {
		
		StringBuffer stmt = new StringBuffer();
		
		stmt.append("(SELECT IF(B.stay_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B ");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" 	AND B.stay_type<>1 ");
		stmt.append(" 	AND (B.stay_date='"+date+"' OR B.stay_date='"+previousDate+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B ");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1 ");
		stmt.append(" AND (   (B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("      OR (B.stay_date<='"+previousDate+"' AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append("   AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B ");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" 	AND B.stay_type<>1 ");
		stmt.append(" 	AND (  ((B.stay_date between '"+opendate+"' AND '"+date+"') AND YEAR(B.stay_date)="+year+")");
		stmt.append("       OR ((B.stay_date between date_sub('"+opendate+"', INTERVAL 1 YEAR) AND '"+previousDate+"') AND YEAR(B.stay_date)="+previousYear+"))	   ");
		stmt.append(" GROUP BY 1);");
		stmt.append("		");
		
		return stmt.toString();
	}

	private String pendingHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month, Integer wp) {
		
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("         IF(YEAR(PRSD.effective_date)="+year+", 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("         IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("  FROM invoice_detail ID");
		stmt.append(" INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append(" INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append(" WHERE I.type=1");
		stmt.append("   AND ID.source = 9");
		stmt.append("   AND ID.item = 5145");
		stmt.append("   AND  year(PRSD.effective_date) between "+previousYear+" and "+year+"");
		stmt.append("   AND   day(PRSD.effective_date) = day('"+date+"')");
		stmt.append("   AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("UNION");
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("        IF(YEAR(I.issue_date)="+year+", 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("    AND I.type=1");
		stmt.append("    AND ID.source <> 9");
		stmt.append("    AND ID.item = 5145 ");
		stmt.append("    AND  year(I.issue_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND   day(I.issue_date) = day('"+date+"')");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("UNION");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(I.issue_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("    AND I.type=1");
		stmt.append("    AND (I.issue_date = '"+date+"'       OR I.issue_date = '"+previousDate+"')");
		stmt.append("    AND ID.source<>9");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2) ");
		stmt.append("UNION");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(PRSD.effective_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID");
		stmt.append("  INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append("  INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append("  WHERE I.type=1");
		stmt.append("    AND (PRSD.effective_date = '"+date+"' OR  PRSD.effective_date='"+previousDate+"')");
		stmt.append("    AND ID.source = 9 ");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("   GROUP BY 1,2)  ");
		stmt.append("");
		stmt.append(" UNION");
		stmt.append("");
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("         IF(YEAR(PRSD.effective_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("         IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID");
		stmt.append("  INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append("  INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append("  WHERE I.type=1");
		stmt.append("    AND ID.item = 5145 ");
		stmt.append("    AND ID.source = 9 ");
		stmt.append("    AND  year(PRSD.effective_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND month(PRSD.effective_date) = "+month+"");
		stmt.append("    AND   day(PRSD.effective_date) <= day('"+date+"')");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("UNION");
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("        IF(YEAR(I.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("    AND I.type=1");
		stmt.append("    AND ID.source <> 9");
		stmt.append("    AND ID.item = 5145 ");
		stmt.append("    AND  year(I.issue_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND month(I.issue_date) = "+month+"");
		stmt.append("    AND   day(I.issue_date) <= day('"+date+"')");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("");
		stmt.append("UNION");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(YEAR(I.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("    AND I.type=1");
		stmt.append("    AND   year(I.issue_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND  month(I.issue_date) = "+month+"");
		stmt.append("    AND    day(I.issue_date) <= day('"+date+"')");
		stmt.append("    AND ID.source <> 9");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2) ");
		stmt.append("UNION");
		stmt.append("");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(YEAR(PRSD.effective_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID");
		stmt.append("  INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append("  INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append("  WHERE I.type=1");
		stmt.append("    AND  year(PRSD.effective_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND month(PRSD.effective_date) = "+month+"");
		stmt.append("    AND   day(PRSD.effective_date) <= day('"+date+"')");
		stmt.append("    AND ID.source = 9 ");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("");
		stmt.append("UNION");
		stmt.append("");
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("         IF(YEAR(PRSD.effective_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("         IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("  FROM invoice_detail ID");
		stmt.append(" INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append(" INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append(" WHERE I.type=1");
		stmt.append("   AND ID.item = 5145 ");
		stmt.append("   AND ID.source = 9 ");
		stmt.append("   AND  year(PRSD.effective_date) between "+previousYear+" and "+year+"");
		stmt.append("   AND   day(PRSD.effective_date) <= day('"+date+"')");
		stmt.append("   AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("UNION");
		stmt.append("(SELECT '6.TASAS' as Concepto,");
		stmt.append("        IF(YEAR(I.issue_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("  FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("   AND I.type=1");
		stmt.append("   AND  year(I.issue_date) between "+previousYear+" and "+year+"");
		stmt.append("   AND   day(I.issue_date) <= day('"+date+"')");
		stmt.append("   AND ID.source <> 9");
		stmt.append("   AND ID.item = 5145 ");
		stmt.append("   AND ID.workplace = "+wp+" ");
		stmt.append("   GROUP BY 1,2)  ");
		stmt.append("");
		stmt.append("");
		stmt.append("");
		stmt.append("UNION");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(YEAR(I.issue_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID, invoice I");
		stmt.append("  WHERE I.id=ID.invoice");
		stmt.append("    AND I.type=1");
		stmt.append("    AND   year(I.issue_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND    day(I.issue_date) <= day('"+date+"')");
		stmt.append("    AND ID.source <> 9");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2) ");
		stmt.append("UNION");
		stmt.append("");
		stmt.append("(SELECT IF(I.service=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(YEAR(PRSD.effective_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("        IFNULL(SUM(ID.taxable_base),0) as Importe, '10' as IVA");
		stmt.append("   FROM invoice_detail ID");
		stmt.append("  INNER JOIN invoice I                                 ON I.id = ID.invoice");
		stmt.append("  INNER JOIN project_reservation_service_detail  PRSD  ON PRSD.id = ID.source_id");
		stmt.append("  WHERE I.type=1");
		stmt.append("    AND  year(PRSD.effective_date) between "+previousYear+" and "+year+"");
		stmt.append("    AND   day(PRSD.effective_date) <= day('"+date+"')");
		stmt.append("    AND ID.source = 9 ");
		stmt.append("    AND ID.item <> 423 ");
		stmt.append("    AND ID.item <> 5145 ");
		stmt.append("    AND ID.workplace = "+wp+" ");
		stmt.append("  GROUP BY 1,2)  ");
		stmt.append("");
		stmt.append(" UNION");
		stmt.append("");
		stmt.append(" (SELECT IF(PRS.extra=0,IF(PR.check_status<3,'3.SALDO CTA. CLIENTE','6.NOSHOW - CANCELACIONES FACTURABLES'),'5.OTROS INGRESOS PEND. PRODUCIR') as Concepto,");
		stmt.append(" 	 'DIA' as Periodo,");
		stmt.append("  	 SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id");
		stmt.append(" INNER JOIN room R                                  ON R.asset=AA.asset");
		stmt.append(" WHERE PRSD.effective_date>'"+date+"'");
		stmt.append("   AND PR.start_date<='"+date+"'");
		stmt.append("   AND PR.status=3");
		stmt.append("   AND R.hotel="+hotel+"");
		stmt.append("   AND PR.creation_date<curdate()");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");
		stmt.append("");
		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append(" 	 'DIA' as Periodo,");
		stmt.append(" 	 SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+date+"'  ");
		stmt.append("   AND PR.start_date>'"+date+"'");
		stmt.append("   AND PR.hotel_reservation="+hotel+"");
		stmt.append("   AND PR.status<>2");
		stmt.append("   AND I.advance=1");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");
		stmt.append("");
		stmt.append(" (SELECT IF(PRS.extra=0,IF(PR.check_status<3,'3.SALDO CTA. CLIENTE','6.NOSHOW - CANCELACIONES FACTURABLES'),'5.OTROS INGRESOS PEND. PRODUCIR') as Concepto,");
		stmt.append(" 	 'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append(" 	 SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id                ");
		stmt.append(" INNER JOIN room R                                  ON R.asset=AA.asset                     ");
		stmt.append(" WHERE PRSD.effective_date>'"+previousDate+"'");
		stmt.append("   AND PR.start_date<='"+previousDate+"'");
		stmt.append("   AND PR.status=3");
		stmt.append("   AND R.hotel="+hotel+"");
		stmt.append(" GROUP BY 1,2 ) ");
		stmt.append(" UNION ");
		stmt.append("");
		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append("  	 'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append(" 	 SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+previousDate+"'");
		stmt.append("   AND PR.start_date>'"+previousDate+"'");
		stmt.append("   AND PR.hotel_reservation="+hotel+"");
		stmt.append("   AND PR.status<>2");
		stmt.append("   AND I.advance=1");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" ORDER BY 1,2;");
		stmt.append("	");
		
		return stmt.toString();
	}
	
	private String roomCountSQL(Integer hotel) {
		StringBuffer stmt = new StringBuffer();
		stmt.append(" SELECT count(item)");
		stmt.append(" from room");
		stmt.append(" where active=1");
		stmt.append(" and hotel=" + hotel);
		return stmt.toString();
	}

	private String opendateSQL(Integer year, Integer hotel) {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT min(stay_date)");
		stmt.append(" from booking");
		stmt.append(" where hotel=" + hotel);
		stmt.append(" and year(stay_date)=" + year);
		return stmt.toString();
	}
	
	private String roomsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month, Integer rooms, java.sql.Date opendate) {
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT IF(B.stay_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("       IFNULL(Count(B.item),0) as Room,");
		stmt.append("       "+rooms+"-IFNULL(Count(B.item),0) as Disponibles,");
		stmt.append("       IFNULL(Count(B.item),0)*100/"+rooms+" as Ocupacion");
		stmt.append("  FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (B.stay_date='"+date+"' OR B.stay_date='"+previousDate+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("      IFNULL(Count(B.item),0) as Room,");
		stmt.append("      ("+rooms+"*(datediff('"+date+"',concat("+year+",'-',"+month+",'-', IF(month('"+opendate+"')="+month+",day('"+opendate+"'),'01')))+1))-IFNULL(Count(B.item),0) as Disponibles,");
		stmt.append("      (IFNULL(Count(B.item),0)*100)/("+rooms+"*(datediff('"+date+"',concat("+year+",'-',"+month+",'-',");
		stmt.append("       IF(month('"+opendate+"')="+month+",");
		stmt.append("          day('"+opendate+"'),'01')))+1)) as Ocupacion");
		stmt.append("  FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (   (B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("       OR (B.stay_date<='"+previousDate+"'  AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append("   AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("      IFNULL(Count(B.item),0) as Room,");
		stmt.append("      ("+rooms+"*(datediff('"+date+"','"+opendate+"')+1))-IFNULL(Count(B.item),0) as Disponibles,");
		stmt.append("      (IFNULL(Count(B.item),0)*100)/("+rooms+"*(datediff('"+date+"','"+opendate+"')+1)) as Ocupacion");
		stmt.append("  FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (  ((B.stay_date between '"+opendate+"' AND '"+date+"') AND YEAR(B.stay_date)="+year+")");
		stmt.append("       OR ((B.stay_date between date_sub('"+opendate+"', INTERVAL 1 YEAR) AND '"+previousDate+"') AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append(" GROUP BY 1);");
		return stmt.toString();
	}
	
	private String opendateroomsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month, Integer rooms, java.sql.Date opendate) {
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT 'DIA' as Periodo,");
		stmt.append("       "+rooms+" as Ocupacion,");
		stmt.append("       1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1");
		stmt.append(" AND (B.stay_date='"+date+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT 'MES' as Periodo,");
		stmt.append("          "+rooms+"*(datediff('"+date+"',concat("+year+",'-',"+month+",'-',");
		stmt.append("          IF(month('"+opendate+"')="+month+",day('"+opendate+"'),'01')))+1) as Ocupacion,");
		stmt.append("          datediff('"+date+"',concat("+year+",'-',"+month+",'-',");
		stmt.append("          IF(month('"+opendate+"')="+month+",day('"+opendate+"'),'01')))+1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (B.stay_date<='"+date+"'        ");
		stmt.append("   AND YEAR(B.stay_date)="+year+")");
		stmt.append("   AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT 'ANIO' as Periodo,");
		stmt.append("         "+rooms+"*(datediff('"+date+"','"+opendate+"')+1) as Ocupacion,");
		stmt.append("         datediff('"+date+"','"+opendate+"')+1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (B.stay_date<='"+date+"'        ");
		stmt.append("   AND YEAR(B.stay_date)="+year+")");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT 'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append("       "+rooms+" as Ocupacion,");
		stmt.append("       1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1");
		stmt.append(" AND (B.stay_date='"+date+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT 'MES_ANIO_ANTERIOR' as Periodo,");
		stmt.append("          "+rooms+"*(datediff('"+date+"',concat("+year+",'-',"+month+",'-',");
		stmt.append("          IF(month('"+opendate+"')="+month+",day('"+opendate+"'),'01')))+1) as Ocupacion,");
		stmt.append("          datediff('"+date+"',concat("+year+",'-',"+month+",'-',");
		stmt.append("          IF(month('"+opendate+"')="+month+",day('"+opendate+"'),'01')))+1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (B.stay_date<='"+date+"'        ");
		stmt.append("   AND YEAR(B.stay_date)="+year+")");
		stmt.append("   AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT 'ANIO_ANTERIOR' as Periodo,");
		stmt.append("         "+rooms+"*(datediff('"+date+"','"+opendate+"')+1) as Ocupacion,");
		stmt.append("         datediff('"+date+"','"+opendate+"')+1 as Dias");
		stmt.append(" FROM booking B");
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("   AND B.stay_type<>1");
		stmt.append("   AND (B.stay_date<='"+date+"'        ");
		stmt.append("   AND YEAR(B.stay_date)="+year+")");
		stmt.append(" GROUP BY 1);");
		return stmt.toString();
	}
	
	
	
	
	
	
	public static class ConsoleOutput {
		
		private static final String SQL_FILE = String.format("%1$s/PMS_SQL/", System.getProperty("user.home"));
		
		public static String opendaterooms = "opendateRooms.sql";
		public static String rooms = "rooms.sql";
		public static String paymethod = "paymethod.sql";
		public static String production = "production.sql";
		public static String pending = "pending.sql";
		public static String pax = "pax.sql";
		
		
		private static void sqlToJava(String fileName) throws IOException {
			List<String> list = Files.readAllLines(Paths.get(SQL_FILE + fileName));
			if (list != null && !list.isEmpty()) {
				System.out.println("#################################");
				System.out.println("### Query code for "
						+ fileName.replaceAll(".sql", "").toUpperCase());
				System.out.println("#################################");
				list.forEach(line -> {
					if (!line.matches("SET .*;")) {
						line = line
								.replaceAll("@date", "'\"+date+\"'")
								.replaceAll("@previousDate", "'\"+previousDate+\"'")
								.replaceAll("@year", "\"+year+\"")
								.replaceAll("@previousYear", "\"+previousYear+\"")
								.replaceAll("@month", "\"+month+\"")
								.replaceAll("@hotel", "\"+hotel+\"")
								.replaceAll("@wp", "\"+wp+\"")
								.replaceAll("@productCategory",
										"\"+productCategory+\"")
								.replaceAll("@rooms", "\"+rooms+\"")
								.replaceAll("@opendate", "'\"+opendate+\"'")
								;
						System.out.println("stmt.append(\"" + line + "\");");
					}
				});
				System.out.println("");
			}
		}
		
		public static void main(String[] args) {
			
			String[] outputQuerys = {
//				opendaterooms, 
//				rooms, 
				paymethod, 
				production, 
				pending, 
//				pax, 
			};
			
			for(String fileName: outputQuerys){
				if(Files.exists(Paths.get(SQL_FILE + fileName))) {
					try {
						sqlToJava(fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
}
