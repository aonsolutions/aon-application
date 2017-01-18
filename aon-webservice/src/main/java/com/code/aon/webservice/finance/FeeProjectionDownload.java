package com.code.aon.webservice.finance;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.StatDAO;
import com.esferalia.aon.watson.AonMonth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "DownloadFeeProjection", urlPatterns = {"/aon_gwt_aio/download_fee_projection/*",
																"/download_fee_projection/*"})
public class FeeProjectionDownload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PDF = "pdf";
	private static final String EXCEL = "excel";
	private static final int DEFAULT_OFFICE_PORT = 2002;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		HashMap<String, String[]> filterMap = SecurityUtils.getInstance().getParametersMap(req.getPathInfo().substring(1));

		String domainName = parameters.get("domain");
		String userName = parameters.get("login");
		String type = parameters.get("type");
		
		Date date = new Date();
		if(parameters.get("date") != null && !parameters.get("date").equals(""))
			date = new Date(Long.parseLong(parameters.get("date")));
		
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		
		if(type.equalsIgnoreCase(PDF)){
			pdf(req, resp, domain, userName, date, filterMap);
		} else if(type.equalsIgnoreCase(EXCEL)){
			excel(req, resp, domain, userName, date, filterMap);
		}
	}
	
	private void excel(HttpServletRequest req, HttpServletResponse resp,
			Domain domain, String userName, Date date, HashMap<String, String[]>filterMap) throws ServletException, IOException {
		HSSFWorkbook workbook = proba(domain, userName, date, filterMap, false);

		ByteArrayOutputStream archivo = new ByteArrayOutputStream();
		workbook.write(archivo);  
		byte[] data = archivo.toByteArray();
		archivo.close();
		workbook.close();

		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
	        
		resp.addHeader("Content-Disposition","attachment; filename=\"fee_projection.xls" +"\"");
		resp.setContentType("application/msexcel");
		
		if (length > 0 && length <= Integer.MAX_VALUE)
        	resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
        	out.write(buffer, 0, bytes);
    
        bis.close();
        bais.close();
        out.flush();
        out.close();
        out.close();
	}
	
	private void pdf(HttpServletRequest req, HttpServletResponse resp,
			Domain domain, String userName, Date date, HashMap<String, String[]>filterMap) throws ServletException, IOException {
		HSSFWorkbook workbook = proba(domain, userName, date, filterMap, true);

		ByteArrayOutputStream archivo = new ByteArrayOutputStream();
		workbook.write(archivo);  
		archivo.close();
		workbook.close();

		File inputFile = null;
		File outputFile = null;
		OfficeManager officeManager = null;
		
		String fileName = "fee_projection";
		
		inputFile = File.createTempFile("tmp", fileName + "." + MimeType.MS_EXCEL.getExtension());
		FileOutputStream inputFileOs = new FileOutputStream(inputFile);
		AonIOUtils.write(archivo.toByteArray(), inputFileOs);
		inputFileOs.flush();
		inputFileOs.close();
			
		outputFile = File.createTempFile("tmp", fileName + "." + MimeType.PDF.getExtension());
		DocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
		
		officeManager = new DefaultOfficeManagerConfiguration()
			.setPortNumber(DEFAULT_OFFICE_PORT)
			.buildOfficeManager();
		officeManager.start();
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager, formatRegistry);
		converter.convert(inputFile, outputFile);
		resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + fileName + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(outputFile);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		inputFileOs.flush();
		inputFileOs.close();
		resp.flushBuffer();

		// TODO REMOVE AND CLOSE EVERYTHING
		if (inputFile != null && inputFile.canWrite()) inputFile.delete();
		if (outputFile != null && outputFile.canWrite()) outputFile.delete();
		if (officeManager != null) officeManager.stop();
	}
	
	Integer rowIndex;
	private HSSFWorkbook proba(Domain domain, String login, Date date, HashMap<String, String[]> filterMap, Boolean isPdf) {
		Integer month = AonDateUtils.getMonth(date);
		Integer year = AonDateUtils.getYear(date);
		Date from = AonDateUtils.getDate(year, month, 1);
		Date to = AonDateUtils.addYears(from, 1);
		
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Proyección de Cuotas");
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
        
        rowIndex = printTitle(workbook, sheet, from, isPdf);
		AON.getFeeStream(domain.getName(), domain.getId(), login, 
				f -> StatDAO.getFeeFilter(from, to, domain.getId(), filterMap, f))
		.sorted((c1,c2) -> c1.getCustomerName().compareTo(c2.getCustomerName()))
		.forEach(fee -> {
			rowIndex = printValues(workbook, sheet, domain, fee, from, rowIndex, isPdf);
		});			
		
		printTotal(workbook, sheet, rowIndex, isPdf);
    	for(Integer i = 0; i < 14; i++)
    		sheet.autoSizeColumn(i);
    	
		return workbook;
	}
	
	private Integer printTitle(HSSFWorkbook libro, HSSFSheet hoja, Date from, Boolean isPdf){
		Row row = hoja.createRow(0);
		  
        row.setHeightInPoints(16);
        CellStyle titleStyle = getTitleStyle(libro, isPdf);        

        createCell(row, titleStyle, "CLIENTE", 0);
        createCell(row, titleStyle, "CUOTA", 1);
        
    	for(Integer i = 0; i < 12; i++){
    		Date date = AonDateUtils.addMonths(from, i);
    		Integer month = AonDateUtils.getMonth(date);
    		Integer year = AonDateUtils.getYear(date);
    		String monthName = AonMonth.values()[month].getName();
    		if(isPdf) monthName = monthName.substring(0,3);
    		createCell(row, titleStyle, monthName + " " + year, i+2);
    	}
    	
    	createCell(row, titleStyle, "TOTAL", 14);    	
    	return 1;
	}
	
	private Boolean month(int bMonth, int bYear, int month, int year, int period){
		if(period == 0) return month == bMonth && year == bYear;
		return (month % period) == (bMonth % period);		
	}
	
	private Integer printValues(HSSFWorkbook workbook, HSSFSheet sheet, Domain domain, Fee fee, Date from, Integer rowIndex, Boolean isPdf){
		Row row = sheet.createRow(rowIndex);
		CellStyle style = getValueStyle(workbook, isPdf);      
		CellStyle doubleStyle = getDoubleStyle(workbook, isPdf);
		createCell(row, style, fee.getCustomerName(), 0);
		createCell(row, style, fee.getDescription(), 1);
		
		int bMonth = AonDateUtils.getMonth(fee.getBillingDate()); 
		int bYear = AonDateUtils.getYear(fee.getBillingDate()); 
		int period = BillingPeriod.values()[fee.getPeriod()].getValue();
    
		for(Integer i = 0 ; i < 12; i++){
			Date date  = AonDateUtils.addMonths(from, i);
			Boolean endDate = fee.getEndDate() == null || (fee.getEndDate() != null && date.compareTo(fee.getEndDate()) <= 0);
			int month = AonDateUtils.getMonth(date);
			int year = AonDateUtils.getYear(date);
			if(endDate && fee.getBillingDate().compareTo(date) <= 0 
					&& month(bMonth, bYear, month, year, period)){
				Double percent = getPercent(fee.getStartDate(), fee.getEndDate(), date, period);
				Double price = fee.getPrice() * fee.getQuantity() * percent
							* (fee.getDiscount() != null ? (100 - fee.getDiscount())/100 : 1.0);
				createCell(row, doubleStyle, price, i+2);
			}
		}
		createFormulaCell(row, doubleStyle, "SUM(C" + (rowIndex+1) + ":N" + (rowIndex+1) + ")", 14);
		return rowIndex+1;
	}
	
	public Double getPercent(Date startDate, Date endDate, Date date, int period){
		if(period == 0 || (startDate.compareTo(date) < 0 && (endDate == null 
				|| AonDateUtils.addMonths(date, period).compareTo(endDate) <= 0 )))
			return 1.0;
		
		int sMonth = AonDateUtils.getMonth(startDate);
		int sYear = AonDateUtils.getYear(startDate);
		int eMonth = endDate != null ? AonDateUtils.getMonth(endDate) : -1;
		int eYear = endDate != null ? AonDateUtils.getYear(endDate) : -1;
		
		Integer d1 = 0;
		Integer d2 = 0;
		for(Integer i = 0; i < period; i++){
			Date d = AonDateUtils.addMonths(date, i);
			int month = AonDateUtils.getMonth(d);
			int year = AonDateUtils.getYear(d);
			Integer lastDay = AonDateUtils.getDay(AonDateUtils.getMonthLastDay(d));
			Integer start = lastDay;
			Integer end = 0;
			if(sMonth == month && sYear == year)
				start = lastDay - (AonDateUtils.getDay(startDate) - 1);
			if(eMonth == month && eYear == year)
				end = lastDay - AonDateUtils.getDay(endDate);
			
			d1 = d1 + start - end;
			d2 = d2 + lastDay;
		}
		return d1.doubleValue()/d2.doubleValue();
	}
	
	private Integer printTotal(HSSFWorkbook workbook, HSSFSheet sheet, Integer rowIndex, Boolean isPdf){
		Row row = sheet.createRow(rowIndex);		
		CellStyle doubleStyle = getDoubleStyle(workbook, isPdf);      
		CellStyle titleStyle = getTitleStyle(workbook, isPdf);
		createCell(row, titleStyle, "TOTAL", 0);
		createCell(row, titleStyle, "", 1);
		
		createFormulaCell(row, doubleStyle, "SUM(C2:C"+(rowIndex)+")", 2);
		createFormulaCell(row, doubleStyle, "SUM(D2:D"+(rowIndex)+")", 3);
		createFormulaCell(row, doubleStyle, "SUM(E2:E"+(rowIndex)+")", 4);
		createFormulaCell(row, doubleStyle, "SUM(F2:F"+(rowIndex)+")", 5);
		createFormulaCell(row, doubleStyle, "SUM(G2:G"+(rowIndex)+")", 6);
		createFormulaCell(row, doubleStyle, "SUM(H2:H"+(rowIndex)+")", 7);
		createFormulaCell(row, doubleStyle, "SUM(I2:I"+(rowIndex)+")", 8);
		createFormulaCell(row, doubleStyle, "SUM(J2:J"+(rowIndex)+")", 9);
		createFormulaCell(row, doubleStyle, "SUM(K2:K"+(rowIndex)+")", 10);
		createFormulaCell(row, doubleStyle, "SUM(L2:L"+(rowIndex)+")", 11);
		createFormulaCell(row, doubleStyle, "SUM(M2:M"+(rowIndex)+")", 12);
		createFormulaCell(row, doubleStyle, "SUM(N2:N"+(rowIndex)+")", 13);
		createFormulaCell(row, doubleStyle, "SUM(O2:O"+(rowIndex)+")", 14);
	
		return rowIndex+1;
	}
	
	private void createCell(Row row, CellStyle style, String value, Integer columnIndex){
		if(value.length()>24) value = value.substring(0,21) + "...";
		Cell c = row.createCell(columnIndex);
    	c.setCellValue(value);
    	c.setCellStyle(style);
	}
	
	private void createCell(Row row, CellStyle style, Double value, Integer columnIndex){
		Cell c = row.createCell(columnIndex);
    	c.setCellValue(round(value, 2));
    	c.setCellStyle(style);
	}
	
	private void createFormulaCell(Row row, CellStyle style, String value, Integer columnIndex){
		Cell c = row.createCell(columnIndex);
		c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		c.setCellFormula(value);
		c.setCellStyle(style);
	}
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	private CellStyle getTitleStyle(HSSFWorkbook libro, Boolean isPdf){
		 CellStyle style = libro.createCellStyle();
		 Font font = libro.createFont();
		 if(isPdf) font.setFontHeightInPoints((short)7);
	     else font.setFontHeightInPoints((short)9);
		 font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 style.setFont(font);
		 style.setAlignment(CellStyle.ALIGN_CENTER);
		 style.setBorderBottom(CellStyle.BORDER_MEDIUM); 
		 return style;
	}
	
	private CellStyle getValueStyle(HSSFWorkbook libro, Boolean isPdf){
		CellStyle style3 = libro.createCellStyle();
        Font font2 = libro.createFont();
        if(isPdf) font2.setFontHeightInPoints((short)7);
        else font2.setFontHeightInPoints((short)9);
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		return style3;
	}
	
	private CellStyle getDoubleStyle(HSSFWorkbook libro, Boolean isPdf){
		CellStyle style3 = libro.createCellStyle();
        Font font2 = libro.createFont();
        if(isPdf) font2.setFontHeightInPoints((short)7);
        else font2.setFontHeightInPoints((short)9);
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_RIGHT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		return style3;
	}
}
