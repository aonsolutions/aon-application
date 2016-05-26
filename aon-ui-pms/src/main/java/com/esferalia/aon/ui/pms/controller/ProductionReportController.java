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

	
	private Map<String, ReportObject> productionMap = new HashMap<>();
	private Map<String, ReportObject> pendingProductionMap = new HashMap<>();
	private Map<String, ReportObject> paxMap = new HashMap<>();
	private Map<String, ReportObject> advancePaymethodMap = new HashMap<>();
	private Map<String, ReportObject> paymethodMap = new HashMap<>();
	
	private DataModel productionModel;
	private DataModel pendingProductionModel;
	private DataModel paxModel;
	private DataModel ratioModel;
	private DataModel advancePaymethodModel;
	private DataModel paymethodModel;
	private DataModel summaryModel;
	
	private ReportObject productionTotal;
	private ReportObject ratioTotal;
	private ReportObject advancePaymethodTotal;
	private ReportObject paymethodTotal;
	
	private boolean testingProductionSql, testingPendingSql, testingPaxSql, testingPaymethodSql;
	
	
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
		return productionModel == null && paxModel == null
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

	public DataModel getProductionModel() {
		return productionModel;
	}
	
	public DataModel getPendingProductionModel() {
		return pendingProductionModel;
	}

	public DataModel getPaxModel() {
		return paxModel;
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
		
		productionMap.clear();
		pendingProductionMap.clear();
		paxMap.clear();
		advancePaymethodMap.clear();
		paymethodMap.clear();
		
		productionModel = null;
		pendingProductionModel = null;
		paxModel = null;
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
		
		ApplicationParameter ap = AppParamUtil.getParameter("PMS_PRODUCTION_REPORT_PCATEGORY");
		int productCategory = 0;
		try {
			productCategory = (ap==null || ap.getValue()==null)?11:Integer.parseInt(ap.getValue());
		} catch (Exception e) {
			productCategory = 11;
		}
		
		Date logDate = new Date();
		LOGGER.info("****** INFORME DE PRODUCCION **************");
		
		
		LOGGER.info("****** Inicio de la busqueda de produccion       -> " + timeFormatter.format(new Date()));
		Date tmpDate = new Date();
		buildProductionReport(date, previousDate, year, previousYear, month, hotel, wp, productCategory);
		long diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de produccion          -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de prod. pendiente  -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPendingProductionReport(date, previousDate, year, previousYear, month, hotel, wp);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de prod. pendiente     -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de pax              -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPaxReport(date, previousDate, year, previousYear, month, hotel);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de pax                 -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		LOGGER.info("****** Inicio de la busqueda de facturas         -> " + timeFormatter.format(new Date()));
		tmpDate = new Date();
		buildPaymethodReport(date, previousDate, year, previousYear, month, hotel, wp);
		diff = (new Date()).getTime() - tmpDate.getTime();
		LOGGER.info("****** Fin de la busqueda de facturas            -> " + timeFormatter.format(new Date()) 
				+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
		
		
		diff = (new Date()).getTime() - logDate.getTime();
        LOGGER.info("****** Tiempo TOTAL                              -> " + diff + " seg. (" 
        		+ (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg.)" );
		
	}
		
	private void buildProductionReport(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp, Integer productCategory) throws AonSQLException {
		Connection connection = null;
		PreparedStatement productionStmt = null;
		ResultSet productionRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				productionStmt = connection.prepareStatement(getHotelProductionSQL(
						date, previousDate, year, previousYear, month, hotel, wp, productCategory));
				productionRs = productionStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(productionStmt);
				SQLUtils.closeQuietly(productionRs);
				testingProductionSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PRODUCCION, continua con la query por defecto");
				productionStmt = connection.prepareStatement(hotelProductionSQL(
						date, previousDate, year, previousYear, month, hotel, wp, productCategory));
				productionRs = productionStmt.executeQuery();
			}
			while (productionRs.next()) {
				String description = productionRs.getString(1);
				String period = productionRs.getString(2);
				Double amount = productionRs.getDouble(3);

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
								previousDate, hotel, year, previousYear, month));
				pendingProductionRs = pendingProductionStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(pendingProductionStmt);
				SQLUtils.closeQuietly(pendingProductionRs);
				testingPendingSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PRODUCCION PENDIENTE, continua con la query por defecto");
				pendingProductionStmt = connection
						.prepareStatement(pendingHotelProductionSQL(date,
								previousDate, hotel, year, previousYear, month));
				pendingProductionRs = pendingProductionStmt.executeQuery();
			}
			while (pendingProductionRs.next()) {
				String description = pendingProductionRs.getString(1);
				String period = pendingProductionRs.getString(2);
				Double amount = pendingProductionRs.getDouble(3);

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
			Integer year, Integer previousYear, Integer month, Integer hotel)
			throws AonSQLException {
		Connection connection = null;
		PreparedStatement paxStmt = null;
		ResultSet paxRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				paxStmt = connection.prepareStatement(getPaxSQL(date, previousDate,
						year, previousYear, month, hotel));
				paxRs = paxStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(paxStmt);
				SQLUtils.closeQuietly(paxRs);
				testingPaxSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PAX, continua con la query por defecto");
				paxStmt = connection.prepareStatement(paxSQL(date, previousDate,
						year, previousYear, month, hotel));
				paxRs = paxStmt.executeQuery();
			}
			while (paxRs.next()) {
				String period = paxRs.getString(1);
				Double amount = paxRs.getDouble(2);
				if (!paxMap.containsKey("PAX")) {
					ReportObject ro = new ReportObject();
					ro.setDescription("PAX");
					paxMap.put("PAX", ro);
				}
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
			Integer month, Integer hotel, Integer wp) throws AonSQLException {
		Connection connection = null;
		PreparedStatement paymethodStmt = null;
		ResultSet paymethodRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			try {
				paymethodStmt = connection
						.prepareStatement(getInvoicePayMethodsSQL(date,
								previousDate, year, previousYear, month, wp));
				paymethodRs = paymethodStmt.executeQuery();
			} catch (Exception e) {
				SQLUtils.closeQuietly(paymethodStmt);
				SQLUtils.closeQuietly(paymethodRs);
				testingPaymethodSql = false;
				LOGGER.error("@#$%&@#$%&!!!! " + e.getMessage());
				LOGGER.error("ERROR EN LA QUERY DE TEST DE PAYMETHOD, continua con la query por defecto");
				paymethodStmt = connection
						.prepareStatement(invoicePayMethodsSQL(date,
								previousDate, year, previousYear, month, wp));
				paymethodRs = paymethodStmt.executeQuery();
			}
			while (paymethodRs.next()) {
				String description = paymethodRs.getString(1);
				String type = paymethodRs.getString(2);
				String period = paymethodRs.getString(3);
				Double amount = paymethodRs.getDouble(4);

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
	
	private static final String SQL_FILE = String.format("%1$s/PMS_SQL/", System.getProperty("user.home"));
	
	private String getLocalSQL(String fileName) {
		if (Files.exists(Paths.get(SQL_FILE + fileName))) {
			LOGGER.info("****** TESTING QUERY DETECTED -> " + SQL_FILE + fileName);
			try {
				String query = new String(Files.readAllBytes(Paths.get(SQL_FILE + fileName)));
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
			Integer month, Integer hotel, Integer wp, Integer productCategory) {
		String query = getLocalSQL("production.sql");
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
					.replaceAll("[\n|\t]", "");
//			sqlToJava(query);
		} else {
			query = hotelProductionSQL(date, previousDate, year, previousYear,
					month, hotel, wp, productCategory);
		}
		return query;
	}

	private String getPendingHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month) {
		String query = getLocalSQL("pending.sql");
		if (query != null) {
			testingPendingSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("[\n|\t]", "");
//			sqlToJava(query);
		} else {
			query = pendingHotelProductionSQL(date, previousDate, hotel,
					year, previousYear, month);
		}
		return query;
	}

	private String getPaxSQL(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel) {
		String query = getLocalSQL("pax.sql");
		if (query != null) {
			testingPaxSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@hotel", hotel.toString())
					.replaceAll("[\n|\t]", "");
//			sqlToJava(query);
		} else {
			query = paxSQL(date, previousDate, year, previousYear, month,
					hotel);
		}
		return query;
	}

	private String getInvoicePayMethodsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp) {
		String query = getLocalSQL("paymethod.sql");
		if (query != null) {
			testingPaymethodSql = true;
			query = query.replaceAll("@date", "'"+date.toString()+"'")
					.replaceAll("@previousDate", "'"+previousDate.toString()+"'")
					.replaceAll("@year", year.toString())
					.replaceAll("@previousYear", previousYear.toString())
					.replaceAll("@month", month.toString())
					.replaceAll("@wp", wp.toString())
					.replaceAll("[\n|\t]", "");
//			sqlToJava(query);
		} else {
			query = invoicePayMethodsSQL(date, previousDate, year,
					previousYear, month, wp);
		}
		return query;
	}
	
	
	private Map<String, ReportObject> getProductionRatioMap() {
		Map<String, ReportObject> productionRatioMap = new HashMap<>();
		productionMap.keySet().stream().sorted().forEach(key -> {
			fillRatioMapObject(key, productionMap, productionRatioMap);
		});
		fillRatioMapObject("1.VENTAS", pendingProductionMap, productionRatioMap);
		fillRatioMapObject("3.SALDO CTA. CLIENTE", pendingProductionMap, productionRatioMap);
		return productionRatioMap;
	}
	
	private void fillRatioMapObject(String key, Map<String, ReportObject> productionMap, Map<String, ReportObject> productionRatioMap){
		ReportObject ro = new ReportObject();
		ro.setDescription(productionMap.get(key).getDescription()+"/Pax");
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
		productionRatioMap.put(key, ro);
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
		Map<String, ReportObject> productionRatioMap = getProductionRatioMap();
		Map<String, ReportObject> summaryMap = getSummaryMap();
		
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
		exportData(exporter, columnMetadata, "PAX", paxMap, false);

		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "RATIOS", productionRatioMap, false);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "FRAS. ANTICIPO (Forma de pago)", advancePaymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "FACTURAS (Forma de pago)", paymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportTotalizeRow(exporter, columnMetadata, advancePaymethodMap, paymethodMap);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "RESUMEN", summaryMap, false, true);
		
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
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getDayAmount).sum(), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getPreviousDayAmount).sum(), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getMonthAmount).sum(), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getPreviousMonthAmount).sum(), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getYearAmount).sum(), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), map.values().stream().mapToDouble(ReportObject::getPreviousYearAmount).sum(), cellStyle);
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
			Integer month, Integer hotel, Integer wp, Integer productCategory) {
		
		StringBuffer stmt = new StringBuffer();
        stmt.append("  SELECT Concepto,Periodo,SUM(Importe),IVA FROM (");
        stmt.append("  (SELECT IF(I.project is not NULL,");
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
        stmt.append("      IF(YEAR(PS.start_time)="+year+", 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
        stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
        stmt.append("  FROM invoice I");
        stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
        stmt.append("  INNER JOIN pos P ON P.id=PS.pos AND P.workplace="+wp+"");
        stmt.append("  WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL 8 HOUR)         AND date_add('"+date+"',         INTERVAL '1 07:59:59' DAY_SECOND))");
        stmt.append("        OR (PS.start_time between date_add('"+previousDate+"', INTERVAL 8 HOUR) AND date_add('"+previousDate+"', INTERVAL '1 07:59:59' DAY_SECOND)) )");
        stmt.append("  AND I.type=1");
        stmt.append("  AND I.id in (SELECT INVD.invoice ");
        stmt.append("                 FROM invoice_detail INVD");
        stmt.append("                 INNER JOIN item IT             ON INVD.item=IT.id");
        stmt.append("                 INNER JOIN product P           ON IT.product=P.id");
        stmt.append("                WHERE INVD.invoice=I.id ");
        stmt.append("                  AND INVD.workplace="+wp+"");
        stmt.append("                  AND P.category <> "+productCategory+")");
        stmt.append("  GROUP BY 1,2)");
        stmt.append("  UNION       ");
        stmt.append("  (SELECT IF(I.project is not NULL,");
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
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
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
        stmt.append("      IF(YEAR(PS.start_time)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
        stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
        stmt.append("  FROM invoice I");
        stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
        stmt.append("  INNER JOIN pos P ON P.id=PS.pos AND P.workplace="+wp+"");
        stmt.append("  WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)             AND year(PS.start_time)="+year+")");
        stmt.append("        OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )");
        stmt.append("    AND month(PS.start_time)="+month+"");
        stmt.append("    AND I.type=1");
        stmt.append("    AND I.id in (SELECT INVD.invoice ");
        stmt.append("                   FROM invoice_detail INVD");
        stmt.append("                   INNER JOIN item IT             ON INVD.item=IT.id");
        stmt.append("                   INNER JOIN product P           ON IT.product=P.id");
        stmt.append("                  WHERE INVD.invoice=I.id ");
        stmt.append("                    AND INVD.workplace="+wp+"");
        stmt.append("                    AND P.category <> "+productCategory+")");
        stmt.append("  GROUP BY 1,2)");
        stmt.append("  UNION");
        stmt.append("         (SELECT IF(I.project is not NULL,");
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
        stmt.append("      IF(YEAR(I.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
        stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
        stmt.append("  FROM invoice I");
        stmt.append("  WHERE (   (I.issue_date <= '"+date+"'          AND year(I.issue_date)="+year+")");
        stmt.append("         OR (I.issue_date <=  '"+previousDate+"' AND year(I.issue_date)="+previousYear+") )");
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
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
        stmt.append("      IF(YEAR(PS.start_time)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
        stmt.append("      SUM(I.total) as Importe,   '10' as IVA");
        stmt.append("  FROM invoice I");
        stmt.append("  INNER JOIN pos_shift PS ON PS.id=I.pos_shift");
        stmt.append("  INNER JOIN pos P ON P.id=PS.pos AND P.workplace="+wp+"");
        stmt.append("  WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)             AND year(PS.start_time)="+year+")");
        stmt.append("        OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )");
        stmt.append("    AND I.type=1");
        stmt.append("    AND I.id in (SELECT INVD.invoice ");
        stmt.append("                    FROM invoice_detail INVD");
        stmt.append("                    INNER JOIN item IT             ON INVD.item=IT.id");
        stmt.append("                    INNER JOIN product P           ON IT.product=P.id");
        stmt.append("                 WHERE INVD.invoice=I.id ");
        stmt.append("                   AND INVD.workplace="+wp+"");
        stmt.append("                   AND P.category <> "+productCategory+")");
        stmt.append("  GROUP BY 1,2)");
        stmt.append("  UNION");
        stmt.append("  (SELECT IF(I.project is not NULL,");
        stmt.append("      IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
        stmt.append("      'OTROS INGRESOS') as Concepto,");
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
        stmt.append("  INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
        stmt.append("  WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL 8 HOUR)          and date_add('"+date+"',       INTERVAL '1 07:59:59' DAY_SECOND))");
        stmt.append("        OR (PS.start_time between date_add( '"+previousDate+"', INTERVAL 8 HOUR) and date_add( '"+previousDate+"', INTERVAL '1 07:59:59' DAY_SECOND)) )");  
        stmt.append("     AND INV.type=1");
        stmt.append("    AND P.category = "+productCategory+"");
        stmt.append("    AND INVD.workplace="+wp+"");
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
        stmt.append("  WHERE ( (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)            AND year(PS.start_time)="+year+")");
        stmt.append("       OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )        ");
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
        stmt.append("  INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
        stmt.append("  WHERE ( (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)             AND year(PS.start_time)="+year+")");
        stmt.append("       OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )");
        stmt.append("    AND INV.type=1");
        stmt.append("    AND P.category = "+productCategory+"");
        stmt.append("    AND INVD.workplace="+wp+"");
        stmt.append("  GROUP BY IT.id,INVT.percentage,2) ) AS Q ");
        stmt.append("  GROUP BY Concepto,Periodo ");
        stmt.append("  ORDER BY Concepto,Periodo ;");
        
        return stmt.toString();		
	}
	
	private String invoicePayMethodsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp) {
		
        StringBuffer stmt = new StringBuffer();
        stmt.append(" SELECT FormaPago,Tipo,Periodo,SUM(Importe) FROM (");     
        stmt.append("(SELECT PM.name as FormaPago,");
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
        stmt.append("    (SELECT PM.name as FormaPago,");
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
        stmt.append("    (SELECT PM.name as FormaPago,");
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

        stmt.append("(SELECT PM.name as FormaPago,");
        stmt.append("        IF(INV.advance=1,'Anticipo','Normal') as Tipo,");
        stmt.append("        IF(YEAR(PS.start_time)="+year+",'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
        stmt.append("        SUM(F.amount) as Importe");
        stmt.append("   FROM finance F");
        stmt.append("    INNER JOIN pay_method PM       ON PM.id=F.pay_method");
        stmt.append("    INNER JOIN invoice INV         ON INV.id=F.invoice");
        stmt.append("    INNER JOIN pos_shift PS        ON PS.id=INV.pos_shift");
        stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
        stmt.append("    WHERE (  (PS.start_time between date_add('"+date+"', INTERVAL 8 HOUR)         and date_add('"+date+"',       INTERVAL '1 07:59:59' DAY_SECOND))");
        stmt.append("          OR (PS.start_time between date_add('"+previousDate+"', INTERVAL 8 HOUR) and date_add('"+previousDate+"', INTERVAL '1 07:59:59' DAY_SECOND)) )");  
        stmt.append("      AND INV.type=1");
        stmt.append("      AND INV.id IN (SELECT distinct INVD.invoice ");
        stmt.append("                       FROM invoice_detail INVD");
        stmt.append("                      WHERE INVD.workplace="+wp+" ");
        stmt.append("                        AND INVD.invoice=INV.id)");
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
        stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
        stmt.append("    WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)             AND year(PS.start_time)="+year+")");
        stmt.append("          OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )        ");
        stmt.append("      AND MONTH(PS.start_time)="+month+"");
        stmt.append("      AND INV.type=1");        
        stmt.append("      AND INV.id IN (SELECT distinct INVD.invoice ");
        stmt.append("                       FROM invoice_detail INVD");
        stmt.append("                      WHERE INVD.workplace="+wp+" ");
        stmt.append("                        AND INVD.invoice=INV.id)");
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
        stmt.append("    INNER JOIN pos PO              ON PO.id=PS.pos AND PO.workplace="+wp+"");
        stmt.append("    WHERE (  (PS.start_time <= date_add('"+date+"', INTERVAL 8 HOUR)             AND year(PS.start_time)="+year+")");
        stmt.append("          OR (PS.start_time <= date_add( '"+previousDate+"', INTERVAL 8 HOUR)    AND year(PS.start_time)="+previousYear+") )        ");
        stmt.append("      AND INV.type=1");
        stmt.append("      AND INV.id IN (SELECT distinct INVD.invoice ");
        stmt.append("                       FROM invoice_detail INVD");
        stmt.append("                      WHERE INVD.workplace="+wp+" ");
        stmt.append("                        AND INVD.invoice=INV.id)");
        stmt.append("      GROUP BY 1,2,3) ) AS W ");
        stmt.append("  GROUP BY Periodo,Tipo,FormaPago ");
        stmt.append("  ORDER BY Periodo,Tipo,FormaPago ;");
        
        return stmt.toString();
	}
	
	private String paxSQL(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel) {
		
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT IF(B.stay_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1 ");
		stmt.append(" AND (B.stay_date='"+date+"' OR B.stay_date='"+previousDate+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1 ");
		stmt.append(" AND (   (B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("      OR (B.stay_date<='"+previousDate+"' AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append("  AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND B.stay_type<>1 ");
		stmt.append("  AND (  (B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("	   OR (B.stay_date<='"+previousDate+"' AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append(" GROUP BY 1);");
		
		return stmt.toString();
	}

	private String pendingHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer hotel, Integer year,
			Integer previousYear, Integer month) {
		
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT IF(PRS.extra=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto,");
		stmt.append("        IF(PRSD.effective_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id");
		stmt.append(" INNER JOIN room R                                  ON R.asset=AA.asset AND R.hotel="+hotel);
		stmt.append(" WHERE (PRSD.effective_date='"+date+"' OR PRSD.effective_date='"+previousDate+"')");
		stmt.append(" 	AND (AA.date='"+date+"' OR AA.date='"+previousDate+"')");
		stmt.append(" 	AND PR.status<>2");
		stmt.append(" 	AND PR.creation_date<curdate()");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");
		
		stmt.append(" (SELECT IF(PRS.extra=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto, ");
		stmt.append("        IF(YEAR(PRSD.effective_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("        SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id");
		stmt.append(" INNER JOIN room R                                  ON R.asset=AA.asset AND R.hotel="+hotel);
		stmt.append(" WHERE ((PRSD.effective_date<='"+date+"'        AND YEAR(PRSD.effective_date)="+year+")");
		stmt.append("        OR");
		stmt.append(" 		(PRSD.effective_date<='"+previousDate+"' AND YEAR(PRSD.effective_date)="+previousYear+"))");
		stmt.append(" 	AND MONTH(PRSD.effective_date)="+month+"");
		stmt.append(" 	AND ((AA.date<='"+date+"'                     AND YEAR(AA.date)="+year+")");
		stmt.append("        OR");
		stmt.append("       (AA.date<='"+previousDate+"'             AND YEAR(AA.date)="+previousYear+"))");
		stmt.append(" 	AND PR.status<>2");
		stmt.append(" 	AND PR.creation_date<curdate()");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");

		stmt.append(" (SELECT IF(PRS.extra=0,'1.VENTAS','2.OTROS INGRESOS') as Concepto, ");
		stmt.append("        IF(YEAR(PRSD.effective_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("        SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" INNER JOIN asset_activity AA                         ON PRRD.asset_activity=AA.id");
		stmt.append(" INNER JOIN room R                                  ON R.asset=AA.asset AND R.hotel="+hotel);
		stmt.append(" WHERE ((PRSD.effective_date<='"+date+"' AND YEAR(PRSD.effective_date)="+year+")");
		stmt.append("        OR");
		stmt.append(" 		(PRSD.effective_date<='"+previousDate+"' AND YEAR(PRSD.effective_date)="+previousYear+"))");
		stmt.append(" 	AND ((AA.date<='"+date+"'                     AND YEAR(AA.date)="+year+")");
		stmt.append(" 		OR");
		stmt.append(" 		(AA.date<='"+previousDate+"'             AND YEAR(AA.date)="+previousYear+"))");
		stmt.append(" 	AND PR.status<>2 ");
		stmt.append(" 	AND PR.creation_date<curdate() ");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");

		stmt.append(" (SELECT IF(PRS.extra=0,IF(PR.check_status<3,'3.SALDO CTA. CLIENTE','6.NOSHOW - CANCELACIONES FACTURABLES'),'5.OTROS INGRESOS PEND. PRODUCIR') as Concepto,");
		stmt.append(" 	'DIA' as Periodo,");
		stmt.append(" 	SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" 	INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" 	INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" 	INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id");                
		stmt.append(" 	INNER JOIN room R                                  ON R.asset=AA.asset AND R.hotel="+hotel);                       
		stmt.append(" WHERE PRSD.effective_date>'"+date+"'  AND PR.start_date<='"+date+"'");
		stmt.append(" 	AND PR.status<>2 ");
		stmt.append(" 	AND PR.creation_date<curdate() ");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");

		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append(" 	'DIA' as Periodo,");
		stmt.append(" 	SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+date+"'  AND PR.start_date>'"+date+"'");
		stmt.append(" 	AND PR.hotel_reservation="+hotel);
		stmt.append(" 	AND PR.status<>2");
		stmt.append(" 	AND I.advance=1");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" UNION ");

		stmt.append(" (SELECT IF(PRS.extra=0,IF(PR.check_status<3,'3.SALDO CTA. CLIENTE','6.NOSHOW - CANCELACIONES FACTURABLES'),'5.OTROS INGRESOS PEND. PRODUCIR') as Concepto,");
		stmt.append(" 	'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append(" 	SUM(PRSD.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM project_reservation_service PRS");
		stmt.append(" 	INNER JOIN project_reservation_service_detail PRSD ON PRSD.project_reservation_service=PRS.id");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=PRS.project_reservation");
		stmt.append(" 	INNER JOIN project_reservation_room_detail PRRD    ON PRRD.id=PRSD.project_reservation_room_detail");
		stmt.append(" 	INNER JOIN asset_activity AA                       ON PRRD.asset_activity=AA.id");                
		stmt.append(" 	INNER JOIN room R                                  ON R.asset=AA.asset AND R.hotel="+hotel);                          
		stmt.append(" WHERE PRSD.effective_date>'"+previousDate+"'  AND PR.start_date<='"+previousDate+"'");
		stmt.append(" 	AND PR.status<>2 ");
		stmt.append(" GROUP BY 1,2 ) ");
		stmt.append(" UNION ");

		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append(" 	'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append(" 	SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+previousDate+"'  AND PR.start_date>'"+previousDate+"'");
		stmt.append(" 	AND PR.hotel_reservation="+hotel);
		stmt.append(" 	AND I.advance=1 ");
		stmt.append(" GROUP BY 1,2) ");
		stmt.append(" ORDER BY 1,2;");
		
		return stmt.toString();
	}
	
	@SuppressWarnings("unused")
	private void sqlToJava(String query) {
		query = query.replaceAll("@date", "'date'")
				.replaceAll("@previousDate", "'previousDate'")
				.replaceAll("@year", "year")
				.replaceAll("@previousYear", "previousYear")
				.replaceAll("@month", "month")
				.replaceAll("@hotel", "hotel")
				.replaceAll("@wp", "wp")
				.replaceAll("@productCategory", "productCategory");
		System.out.println(query);
	}
	
}
