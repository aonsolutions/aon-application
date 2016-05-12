package com.esferalia.aon.ui.pms.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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

	
	private Map<String, ReportObject> productionMap = new HashMap<>();
	private Map<String, ReportObject> paxMap = new HashMap<>();
	private Map<String, ReportObject> advancePaymethodMap = new HashMap<>();
	private Map<String, ReportObject> paymethodMap = new HashMap<>();
	private Map<String, ReportObject> pendingProductionMap = new HashMap<>();
	
	private DataModel productionModel;
	private DataModel paxModel;
	private DataModel ratioModel;
	private DataModel advancePaymethodModel;
	private DataModel paymethodModel;
	private DataModel pendingProductionModel;
	
	private ReportObject productionTotal;
	private ReportObject ratioTotal;
	private ReportObject advancePaymethodTotal;
	private ReportObject paymethodTotal;
	
	
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
		date = new Date();
		init();
	}
	
	private void init(){
		productionMap.clear();
		paxMap.clear();
		advancePaymethodMap.clear();
		paymethodMap.clear();
		pendingProductionMap.clear();
		
		productionModel = null;
		paxModel = null;
		ratioModel = null;
		advancePaymethodModel = null;
		paymethodModel = null;
		pendingProductionModel = null;
		
		productionTotal = null;
		ratioTotal = null;
		advancePaymethodTotal = null;
		paymethodTotal = null;
	}
	
	public void onSearch(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		int defaultInactiveInterval = session.getMaxInactiveInterval();
		
		init();
		
		try {
			session.setMaxInactiveInterval(6*60);
			buildProductionReport();
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
	
	private void buildProductionReport() throws AonSQLException {
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
		
		Connection connection = null;
		PreparedStatement productionStmt = null;
		PreparedStatement paxStmt = null;
		PreparedStatement paymethodStmt = null;
		PreparedStatement pendingProductionStmt = null;
		ResultSet productionRs = null;
		ResultSet paxRs = null;
		ResultSet paymethodRs = null;
		ResultSet pendingProductionRs = null;
		
		try {
			Date logDate = new Date();
			LOGGER.info("****** INFORME DE PRODUCCION **************");
			
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			productionStmt = connection.prepareStatement(getHotelProductionSQL(date, previousDate, year, previousYear, month, hotel, wp));
			paxStmt = connection.prepareStatement(getPaxSQL(date, previousDate, year, previousYear, month, hotel));
			paymethodStmt = connection.prepareStatement(getInvoicePayMethodsSQL(date, previousDate, year, previousYear, month, wp));
			pendingProductionStmt = connection.prepareStatement(getPendingHotelProductionSQL(date, previousDate, hotel, year, previousYear, month));
			
			LOGGER.info("****** Inicio de la busqueda de produccion       -> " + timeFormatter.format(new Date()));
			Date tmpDate = new Date();
			productionRs = productionStmt.executeQuery();
			long diff = (new Date()).getTime() - tmpDate.getTime();
			LOGGER.info("****** Fin de la busqueda de produccion          -> " + timeFormatter.format(new Date()) 
					+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
			
			while (productionRs.next()) {
				String description = productionRs.getString(1);
				String period = productionRs.getString(2);
				Double amount = productionRs.getDouble(3);
				
				if(!productionMap.containsKey(description)){
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					productionMap.put(description, ro);
				}
				if(period.equals("ANIO")){
					productionMap.get(description).setYearAmount(amount);
				} else if(period.equals("ANIO_ANTERIOR")){
					productionMap.get(description).setPreviousYearAmount(amount);
				} else if(period.equals("MES")){
					productionMap.get(description).setMonthAmount(amount);
				} else if(period.equals("MES_ANIO_ANTERIOR")){
					productionMap.get(description).setPreviousMonthAmount(amount);
				} else if(period.equals("DIA")){
					productionMap.get(description).setDayAmount(amount);
				} else if(period.equals("DIA_ANIO_ANTERIOR")){
					productionMap.get(description).setPreviousDayAmount(amount);
				}
			}
			
			productionModel = buildModel(productionMap);
			productionTotal = buildTotalizeTo(productionMap);

			LOGGER.info("****** Inicio de la busqueda de pax              -> " + timeFormatter.format(new Date()));
			tmpDate = new Date();
			paxRs = paxStmt.executeQuery();
			diff = (new Date()).getTime() - tmpDate.getTime();
			LOGGER.info("****** Fin de la busqueda de pax                 -> " + timeFormatter.format(new Date()) 
					+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
			while (paxRs.next()) {
				String period = paxRs.getString(1);
				Double amount = paxRs.getDouble(2);
				if(!paxMap.containsKey("PAX")){
					ReportObject ro = new ReportObject();
					ro.setDescription("PAX");
					paxMap.put("PAX", ro);
				}
				if(period.equals("ANIO")){
					paxMap.get("PAX").setYearAmount(amount);
				} else if(period.equals("ANIO_ANTERIOR")){
					paxMap.get("PAX").setPreviousYearAmount(amount);
				} else if(period.equals("MES")){
					paxMap.get("PAX").setMonthAmount(amount);
				} else if(period.equals("MES_ANIO_ANTERIOR")){
					paxMap.get("PAX").setPreviousMonthAmount(amount);
				} else if(period.equals("DIA")){
					paxMap.get("PAX").setDayAmount(amount);
				} else if(period.equals("DIA_ANIO_ANTERIOR")){
					paxMap.get("PAX").setPreviousDayAmount(amount);
				}
			}
			
			paxModel = buildModel(paxMap);
			ratioModel = buildModel(getProductionRatioMap());
			ratioTotal = buildTotalizeTo(getProductionRatioMap());
			
			LOGGER.info("****** Inicio de la busqueda de facturas         -> " + timeFormatter.format(new Date()));
			tmpDate = new Date();
			paymethodRs = paymethodStmt.executeQuery();
			diff = (new Date()).getTime() - tmpDate.getTime();
			LOGGER.info("****** Fin de la busqueda de facturas            -> " + timeFormatter.format(new Date()) 
					+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
			while (paymethodRs.next()) {
				String description = paymethodRs.getString(1);
				String type = paymethodRs.getString(2);
				String period = paymethodRs.getString(3);
				Double amount = paymethodRs.getDouble(4);
				
				Map<String, ReportObject> map = null;
				if(type.equalsIgnoreCase("NORMAL")){
					map = paymethodMap;
				} else if(type.equalsIgnoreCase("ANTICIPO")){
					map = advancePaymethodMap;
				}
				
				if(!map.containsKey(description)){
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					map.put(description, ro);
				}
				if(period.equals("ANIO")){
					map.get(description).setYearAmount(amount);
				} else if(period.equals("ANIO_ANTERIOR")){
					map.get(description).setPreviousYearAmount(amount);
				} else if(period.equals("MES")){
					map.get(description).setMonthAmount(amount);
				} else if(period.equals("MES_ANIO_ANTERIOR")){
					map.get(description).setPreviousMonthAmount(amount);
				} else if(period.equals("DIA")){
					map.get(description).setDayAmount(amount);
				} else if(period.equals("DIA_ANIO_ANTERIOR")){
					map.get(description).setPreviousDayAmount(amount);
				}
			}
			
			advancePaymethodModel = buildModel(advancePaymethodMap);
			advancePaymethodTotal = buildTotalizeTo(advancePaymethodMap);

			paymethodModel = buildModel(paymethodMap);
			paymethodTotal = buildTotalizeTo(paymethodMap);

			LOGGER.info("****** Inicio de la busqueda de prod. pendiente  -> " + timeFormatter.format(new Date()));
			tmpDate = new Date();
			pendingProductionRs = pendingProductionStmt.executeQuery();
			diff = (new Date()).getTime() - tmpDate.getTime();
			LOGGER.info("****** Fin de la busqueda de prod. pendiente     -> " + timeFormatter.format(new Date()) 
					+ " || TIEMPO EMPLEADO -> " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg." );
			while (pendingProductionRs.next()) {
				String description = pendingProductionRs.getString(1);
				String period = pendingProductionRs.getString(2);
				Double amount = pendingProductionRs.getDouble(3);
				
				if(!pendingProductionMap.containsKey(description)){
					ReportObject ro = new ReportObject();
					ro.setDescription(description);
					pendingProductionMap.put(description, ro);
				}
				
				if(period.equals("ANIO")){
					double _amount = pendingProductionMap.get(description).getYearAmount();
					pendingProductionMap.get(description).setYearAmount(_amount+amount);
				} else if(period.equals("ANIO_ANTERIOR")){
					double _amount = pendingProductionMap.get(description).getPreviousYearAmount();
					pendingProductionMap.get(description).setPreviousYearAmount(_amount+amount);
				} else if(period.equals("MES")){
					double _amount = pendingProductionMap.get(description).getMonthAmount();
					pendingProductionMap.get(description).setMonthAmount(_amount+amount);
				} else if(period.equals("MES_ANIO_ANTERIOR")){
					double _amount = pendingProductionMap.get(description).getPreviousMonthAmount();
					pendingProductionMap.get(description).setPreviousMonthAmount(_amount+amount);
				} else if(period.equals("DIA")){
					double _amount = pendingProductionMap.get(description).getDayAmount();
					pendingProductionMap.get(description).setDayAmount(_amount+amount);
				} else if(period.equals("DIA_ANIO_ANTERIOR")){
					double _amount = pendingProductionMap.get(description).getPreviousDayAmount();
					pendingProductionMap.get(description).setPreviousDayAmount(_amount+amount);
				}
			}
			
			pendingProductionModel = buildModel(pendingProductionMap);
			
			diff = (new Date()).getTime() - logDate.getTime();
	        LOGGER.info("****** Tiempo TOTAL                              -> " + diff + " seg. (" 
	        		+ (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg.)" );
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(productionRs);
			SQLUtils.closeQuietly(paxRs);
			SQLUtils.closeQuietly(paymethodRs);
			SQLUtils.closeQuietly(productionStmt);
			SQLUtils.closeQuietly(paxStmt);
			SQLUtils.closeQuietly(paymethodStmt);
			SQLUtils.closeQuietly(connection);
		}
		
	}
	
	private Map<String, ReportObject> getProductionRatioMap() {
		Map<String, ReportObject> productionRatioMap = new HashMap<>();
		productionMap.keySet().stream().sorted().forEach(key -> {
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
		});
		return productionRatioMap;
	}
	
	// *********************************************
	// EXCEL REPORT
	// *********************************************
	private boolean excelReport(OutputStream output) throws IOException, ReportException, AonConnectionException {
		Map<String, ReportObject> productionRatioMap = getProductionRatioMap();
		
		ExcelReportExporter exporter = new ExcelReportExporter();

		exporter.startExport("Informe");
		ReportMetadata columnMetadata = getContractColumnMetadata();
		exporter.exportHeader(columnMetadata);
		
		exportData(exporter, columnMetadata, "FACTURACION POR AREA", productionMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "PAX", paxMap, false);

		exporter.startLine();
		exporter.endLine();
		exportData(exporter, columnMetadata, "RATIOS", productionRatioMap, false);
		
		exporter.startLine();
		exporter.endLine();
		exportInnerHeader(exporter, columnMetadata, "FRAS. ANTICIPO");
		exportData(exporter, columnMetadata, "Forma de pago", advancePaymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportInnerHeader(exporter, columnMetadata, "FACTURAS");
		exportData(exporter, columnMetadata, "Forma de pago", paymethodMap, true);
		
		exporter.startLine();
		exporter.endLine();
		exportTotalizeRow(exporter, columnMetadata, advancePaymethodMap, paymethodMap);
		
		exporter.startLine();
		exporter.endLine();
		exportInnerHeader(exporter, columnMetadata, "PRODUCCION");
		exportData(exporter, columnMetadata, null, pendingProductionMap, false);
		
		exporter.endExport(output);
		output.flush();
		return true;
	}
	
	private ReportMetadata getContractColumnMetadata() throws ReportException {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
		Integer previousYear = cal.get(Calendar.YEAR)-1;
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("",Types.VARCHAR,"",30));
		metadata.getColumns().add(new ReportColumnMetadata("DAY",Types.VARCHAR,"Dia",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_DAY",Types.VARCHAR,"Dia (" +previousYear+ ")" ,15));
		metadata.getColumns().add(new ReportColumnMetadata("MONTH",Types.VARCHAR,"Mes",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_MONTH",Types.VARCHAR,"Mes (" +previousYear+ ")",15));
		metadata.getColumns().add(new ReportColumnMetadata("YEAR",Types.VARCHAR,"Año",15));
		metadata.getColumns().add(new ReportColumnMetadata("PREVIOUS_YEAR",Types.VARCHAR,"Año (" +previousYear+ ")",15));
		return metadata;
	}

	private void exportInnerHeader(ExcelReportExporter exporter,
			ReportMetadata metadata, String description) throws ReportException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer previousYear = cal.get(Calendar.YEAR)-1;
		int column = 0;
		exporter.startLine();
		try {
			HSSFCellStyle cellStyle = exporter.createHeaderStyle();
			exporter.exportColumn(metadata.getColumns().get(column++), description, cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Dia", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Dia (" +previousYear+ ")", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Mes", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Mes (" +previousYear+ ")", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Año", cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), "Año (" +previousYear+ ")", cellStyle);
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
		
		if(StringUtils.isNotBlank(description)){
			HSSFCellStyle cellStyle = exporter.createHeaderStyle();
			exporter.startLine();
			for(int i = 1; i<7; i++){
				if(i==1){
					exporter.exportColumn(metadata.getColumns().get(i), description, cellStyle);
				}
				exporter.exportColumn(metadata.getColumns().get(i), "", cellStyle);
			}
			exporter.endLine();
		}
		
		map.keySet().stream().sorted().forEach(key -> {
			int column = 0;
			exporter.startLine();
			try {
				exporter.exportColumn(metadata.getColumns().get(column++), map.get(key).getDescription());
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getDayAmount()));
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getPreviousDayAmount()));
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getMonthAmount()));
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getPreviousMonthAmount()));
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getYearAmount()));
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.get(key).getPreviousYearAmount()));
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
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getDayAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getPreviousDayAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getMonthAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getPreviousMonthAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getYearAmount).sum()), cellStyle);
				exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(map.values().stream().mapToDouble(ReportObject::getPreviousYearAmount).sum()), cellStyle);
			} catch (ReportException e) {
				LOGGER.error("No se ha podido completar la fila del informe de produccion.");
			} finally {
				exporter.endLine();
			}
		}
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
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(dayAmount), cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(previousDayAmount), cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(monthAmount), cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(previousMonthAmount), cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(yearAmount), cellStyle);
			exporter.exportColumn(metadata.getColumns().get(column++), getFormattedAmount(previousYearAmount), cellStyle);
		} catch (ReportException e) {
			LOGGER.error("No se ha podido completar la fila del informe de produccion.");
		} finally {
			exporter.endLine();
		}
	}
	
	private String getFormattedAmount(Double amount){
		return String.valueOf(CommonUtil.round(amount)).replaceAll("\\.", ",");
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
	
	private String getHotelProductionSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer hotel, Integer wp) {
		
		ApplicationParameter ap = AppParamUtil.getParameter("PMS_PRODUCTION_REPORT_PCATEGORY");
		int productCategory = 0;
		try {
			productCategory = (ap==null || ap.getValue()==null)?11:Integer.parseInt(ap.getValue());
		} catch (NumberFormatException e) {
			productCategory = 11;
		}
		
		StringBuffer stmt = new StringBuffer();
		stmt.append(" (SELECT IF(I.project is not NULL,");
		stmt.append(" 	IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
		stmt.append(" 	'OTROS INGRESOS') as Concepto,");
		stmt.append(" 	IF(I.issue_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(I.total) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" WHERE (I.issue_date = '"+date+"' OR I.issue_date = '"+previousDate+"')");
		stmt.append(" AND I.type=1");
		stmt.append(" 	AND I.id in (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append(" 					INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 					INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" 				WHERE INVD.invoice=I.id AND INVD.workplace="+wp+"");
		stmt.append(" 					AND P.category <> "+productCategory+")");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");
		
		stmt.append(" (SELECT IF(I.project is not NULL,");
		stmt.append(" 	IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
		stmt.append(" 	'OTROS INGRESOS') as Concepto,");
		stmt.append(" 	IF(YEAR(I.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(I.total) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" WHERE ((I.issue_date <= '"+date+"'          AND year(I.issue_date)="+year+")");
		stmt.append(" 		OR");
		stmt.append(" 		(I.issue_date <= '"+previousDate+"' AND year(I.issue_date)="+previousYear+"))");
		stmt.append(" 	AND month(I.issue_date)="+month+"");
		stmt.append(" 	AND I.type=1");
		stmt.append(" 	AND I.id in (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append(" 					INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 					INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" 				WHERE INVD.invoice=I.id AND INVD.workplace="+wp+"");
		stmt.append(" 					AND P.category <> "+productCategory+")");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");
		
		stmt.append(" (SELECT IF(I.project is not NULL,");
		stmt.append(" 	IF(I.service=0,'ALOJAMIENTO','OTROS INGRESOS'),");
		stmt.append(" 	'OTROS INGRESOS') as Concepto,");
		stmt.append(" 	IF(YEAR(I.issue_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(I.total) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" WHERE ((I.issue_date <= '"+date+"'          AND year(I.issue_date)="+year+")");
		stmt.append(" 		OR");
		stmt.append(" 		(I.issue_date <= '"+previousDate+"' AND year(I.issue_date)="+previousYear+"))");
		stmt.append(" 	AND I.type=1");
		stmt.append(" 	AND I.id in (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append(" 					INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 					INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" 				WHERE INVD.invoice=I.id AND INVD.workplace="+wp+"");
		stmt.append(" 					AND P.category <> "+productCategory+")");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");
		
		stmt.append(" (SELECT P.name as Concepto,");
		stmt.append(" 	IF(INV.issue_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append(" FROM invoice INV");
		stmt.append(" 	INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append(" 	INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append(" 	INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 	INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" WHERE (INV.issue_date='"+date+"' OR INV.issue_date='"+previousDate+"')");
		stmt.append(" 	AND INV.type=1");
		stmt.append(" 	AND P.category = " + productCategory);
		stmt.append(" 	AND INVD.workplace="+wp+"");
		stmt.append(" GROUP BY IT.id,INVT.percentage,2)");
		stmt.append(" UNION");
		
		stmt.append(" (SELECT P.name as Concepto,");
		stmt.append(" 	IF(YEAR(INV.issue_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append(" FROM invoice INV");
		stmt.append(" 	INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append(" 	INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append(" 	INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 	INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" WHERE ((INV.issue_date<='"+date+"' AND YEAR(INV.issue_date)="+year+")");
		stmt.append(" 		OR");
		stmt.append(" 		(INV.issue_date<='"+previousDate+"' AND YEAR(INV.issue_date)="+previousYear+"))");
		stmt.append(" 	AND MONTH(INV.issue_date)="+month+"");
		stmt.append(" 	AND INV.type=1");
		stmt.append(" 	AND P.category = " + productCategory);
		stmt.append(" 	AND INVD.workplace="+wp+"");
		stmt.append(" GROUP BY IT.id,INVT.percentage,2)");
		stmt.append(" UNION");
		
		stmt.append(" (SELECT P.name as Concepto,");
		stmt.append("         IF(YEAR(INV.issue_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append(" 	SUM(INVD.taxable_base+(INVD.taxable_base*INVT.percentage/100)), INVT.percentage as IVA");
		stmt.append(" FROM invoice INV");
		stmt.append(" 	INNER JOIN invoice_detail INVD ON INVD.invoice=INV.id");
		stmt.append(" 	INNER JOIN invoice_tax INVT    ON INVT.invoice_detail=INVD.id");
		stmt.append(" 	INNER JOIN item IT             ON INVD.item=IT.id");
		stmt.append(" 	INNER JOIN product P           ON IT.product=P.id");
		stmt.append(" WHERE ((INV.issue_date<='"+date+"' AND YEAR(INV.issue_date)="+year+")");
		stmt.append(" 		OR");
		stmt.append(" 		(INV.issue_date<='"+previousDate+"' AND YEAR(INV.issue_date)="+previousYear+"))");
		stmt.append(" 	AND INV.type=1");
		stmt.append(" 	AND P.category = " + productCategory);
		stmt.append(" 	AND INVD.workplace="+wp+"");
		stmt.append(" GROUP BY IT.id,INVT.percentage,2)");
		stmt.append(" ORDER BY 2,1;"); 
	 	return stmt.toString();
	}
	
	private String getInvoicePayMethodsSQL(java.sql.Date date,
			java.sql.Date previousDate, Integer year, Integer previousYear,
			Integer month, Integer wp) {
				
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT PM.name as FormaPago,"); 
		stmt.append("	    IF((SELECT INV.advance FROM invoice INV"); 
		stmt.append("			WHERE INV.id=F.invoice"); 
		stmt.append("	          AND INV.type=1 ");
		stmt.append("	          AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("							  WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id))=1,'Anticipo','Normal') as Tipo,"); 
		stmt.append("		   IF(INV.issue_date='"+date+"','DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       SUM(F.amount)"); 
		stmt.append("	FROM finance F");
		stmt.append("	INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("	INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("	WHERE INV.issue_date in ('"+date+"', date_sub('"+date+"', interval 1 year))");
		stmt.append("	 AND INV.type=1"); 
		stmt.append("	 AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("	   			     WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id)"); 
		stmt.append("	  GROUP BY 1,2,3)");
		stmt.append("	UNION");
		stmt.append("	(SELECT PM.name as FormaPago,");
		stmt.append("	    IF((SELECT INV.advance FROM invoice INV"); 
		stmt.append("			WHERE INV.id=F.invoice"); 
		stmt.append("	          AND INV.type=1"); 
		stmt.append("	          AND MONTH(INV.issue_date)="+month+"");
		stmt.append("	          AND (INV.issue_date<='"+date+"' OR  INV.issue_date<='"+previousDate+"')"); 
		stmt.append("	          AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("							  WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id))=1,'Anticipo','Normal') as Tipo,"); 
		stmt.append("		   IF(YEAR(INV.issue_date)="+year+",'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       SUM(F.amount)"); 
		stmt.append("	FROM finance F");
		stmt.append("	INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("	INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("	WHERE ((INV.issue_date <='"+date+"'           AND YEAR(INV.issue_date)="+year+")"); 
		stmt.append("	      OR"); 
		stmt.append("	       (INV.issue_date <='"+previousDate+"'  AND YEAR(INV.issue_date)="+previousYear+") )");
		stmt.append("	  AND MONTH(INV.issue_date)="+month+"");
		stmt.append("	  AND F.invoice in"); 
		stmt.append("	      (SELECT INV.id FROM invoice INV"); 
		stmt.append("			WHERE INV.id=F.invoice"); 
		stmt.append("	          AND INV.type=1"); 
		stmt.append("	          AND (INV.issue_date<='"+date+"' OR  INV.issue_date<='"+previousDate+"')"); 
		stmt.append("	          AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("							  WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id))");
		stmt.append("	  GROUP BY 1,2,3)");
		stmt.append("	UNION");
		stmt.append("	(SELECT PM.name as FormaPago,");
		stmt.append("	    IF((SELECT INV.advance FROM invoice INV"); 
		stmt.append("			WHERE INV.id=F.invoice"); 
		stmt.append("	          AND INV.type=1"); 
		stmt.append("	          AND MONTH(INV.issue_date)="+month+"");
		stmt.append("	          AND (INV.issue_date<='"+date+"' OR  INV.issue_date<='"+previousDate+"')"); 
		stmt.append("	          AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("							  WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id))=1,'Anticipo','Normal') as Tipo,"); 
		stmt.append("		   IF(YEAR(INV.issue_date)="+year+",'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       SUM(F.amount)"); 
		stmt.append("	FROM finance F");
		stmt.append("	INNER JOIN pay_method PM       ON PM.id=F.pay_method");
		stmt.append("	INNER JOIN invoice INV         ON INV.id=F.invoice");
		stmt.append("	WHERE ((INV.issue_date <='"+date+"'           AND YEAR(INV.issue_date)="+year+")"); 
		stmt.append("	      OR"); 
		stmt.append("	       (INV.issue_date <='"+previousDate+"'  AND YEAR(INV.issue_date)="+previousYear+") )");
		stmt.append("	  AND F.invoice in"); 
		stmt.append("	      (SELECT INV.id FROM invoice INV"); 
		stmt.append("			WHERE INV.id=F.invoice"); 
		stmt.append("	          AND INV.type=1"); 
		stmt.append("	          AND (INV.issue_date<='"+date+"' OR  INV.issue_date<='"+previousDate+"')"); 
		stmt.append("	          AND INV.id IN (SELECT INVD.invoice FROM invoice_detail INVD");
		stmt.append("							  WHERE INVD.workplace="+wp+" AND INVD.invoice=INV.id))");
		stmt.append("	  GROUP BY 1,2,3)");
		stmt.append("	ORDER BY 3,2,1;");
		return stmt.toString();
	}
	
	private String getPaxSQL(java.sql.Date date, java.sql.Date previousDate,
			Integer year, Integer previousYear, Integer month, Integer hotel) {
		
		StringBuffer stmt = new StringBuffer();
		stmt.append("(SELECT IF(B.stay_date='"+date+"', 'DIA','DIA_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND (B.stay_date='"+date+"' OR B.stay_date='"+previousDate+"')");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'MES','MES_ANIO_ANTERIOR') as Periodo,");
		stmt.append("	       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append(" AND ((B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("   OR"); 
		stmt.append("   (B.stay_date<='"+previousDate+"' AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append("  AND MONTH(B.stay_date)="+month+"");
		stmt.append(" GROUP BY 1)");
		stmt.append(" UNION");
		stmt.append(" (SELECT IF(YEAR(B.stay_date)="+year+", 'ANIO','ANIO_ANTERIOR') as Periodo,");
		stmt.append("       IFNULL(SUM(B.guests),0) as Pax");
		stmt.append(" FROM booking B"); 
		stmt.append(" WHERE B.hotel="+hotel+"");
		stmt.append("  AND ((B.stay_date<='"+date+"'          AND YEAR(B.stay_date)="+year+")");
		stmt.append("	   OR"); 
		stmt.append("	   (B.stay_date<='"+previousDate+"' AND YEAR(B.stay_date)="+previousYear+"))");
		stmt.append(" GROUP BY 1);");
		return stmt.toString();
	}

	private String getPendingHotelProductionSQL(java.sql.Date date,
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
		stmt.append(" 	AND (AA.date='"+date+"' OR AA.date='"+previousDate+"'");
		stmt.append(" 	AND PR.status<>2");
		stmt.append(" 	AND PR.creation_date<curdate()");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");
		
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
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");

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
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");

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
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");

		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append(" 	'DIA' as Periodo,");
		stmt.append(" 	SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+date+"'  AND PR.start_date>'"+date+"'");
		stmt.append(" 	AND PR.hotel_reservation="+hotel);
		stmt.append(" 	AND PR.status<>2");
		stmt.append(" 	AND I.advance=1");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" UNION");

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
		stmt.append(" GROUP BY 1,2 )");
		stmt.append(" UNION");

		stmt.append(" (SELECT '4.SALDO CTA. CLIENTE FRA. ANTICIPO' as Concepto,");
		stmt.append(" 	'DIA_ANIO_ANTERIOR' as Periodo,");
		stmt.append(" 	SUM(I.taxable_base) as Importe,   '10' as IVA");
		stmt.append(" FROM invoice I");
		stmt.append(" 	INNER JOIN project_reservation PR                  ON PR.project=I.project");
		stmt.append(" WHERE I.issue_date<'"+previousDate+"'  AND PR.start_date>'"+previousDate+"'");
		stmt.append(" 	AND PR.hotel_reservation="+hotel);
		stmt.append(" 	AND I.advance=1");
		stmt.append(" GROUP BY 1,2)");
		stmt.append(" ORDER BY 1,2;");
		return stmt.toString();
	}
	
	
	
}
