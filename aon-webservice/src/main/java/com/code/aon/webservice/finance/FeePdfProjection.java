package com.code.aon.webservice.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.AonMonth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "DownloadFeeProjectionPdf", urlPatterns = {"/aon_gwt_aio/download_fee_projection_pdf/*",
																"/download_fee_projection_pdf/*"})
public class FeePdfProjection extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_OFFICE_PORT = 2002;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String userName = parameters.get("login");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		try {
			date = format.parse(parameters.get("date"));
		} catch (ParseException e) {
			date = new Date();
		}
		
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		
		HSSFWorkbook workbook = proba(domain, userName, date);

		ByteArrayOutputStream archivo = new ByteArrayOutputStream();
		workbook.write(archivo);  
		archivo.close();
		workbook.close();

		File inputFile = null;
		File outputFile = null;
		OfficeManager officeManager = null;
		try {

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
		AonIOUtils.copy(new FileInputStream(outputFile), resp.getOutputStream());
		resp.flushBuffer();
	} catch (Throwable e) {
		throw new ServletException(e);
	} finally {
		// TODO REMOVE AND CLOSE EVERYTHING
		if (inputFile != null && inputFile.canWrite()) inputFile.delete();
		if (outputFile != null && outputFile.canWrite()) outputFile.delete();
		if (officeManager != null) officeManager.stop();
	}
	}

	private HSSFWorkbook proba(Domain domain, String login, Date date) {
		Integer month = AonDateUtils.getMonth(date);
		Integer year = AonDateUtils.getYear(date);
		Date from = AonDateUtils.getDate(year, month, 1);
		Date to = AonDateUtils.addYears(from, 1);
		
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Proyección de Cuotas");
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
        Integer rowIndex = printTitle(workbook, sheet, from);
        
		LinkedList<Fee> list = AON.getFeeList(domain.getName(), domain.getId(), login, 
				f -> f.getBillingDateProperty().ge(AonDateUtils.toSql(from))
				.and(f.getBillingDateProperty().lt(AonDateUtils.toSql(to)))
				.and(f.getDomainProperty().eq(domain.getId())));
		
		for (Fee fee : list)
			rowIndex = printValues(workbook, sheet, domain, fee, from, rowIndex);

		printTotal(workbook, sheet, rowIndex);
		
    	for(Integer i = 0; i < 14; i++)
    		sheet.autoSizeColumn(i);
    	
		return workbook;
	}
	
	private Integer printTitle(HSSFWorkbook libro, HSSFSheet hoja, Date from){
		Row row = hoja.createRow(0);
		  
        row.setHeightInPoints(16);
        CellStyle titleStyle = getTitleStyle(libro);        

        createCell(row, titleStyle, "CLIENTE", 0);
        createCell(row, titleStyle, "CUOTA", 1);
        
    	for(Integer i = 0; i < 12; i++){
    		Date date = AonDateUtils.addMonths(from, i);
    		Integer month = AonDateUtils.getMonth(date);
    		Integer year = AonDateUtils.getYear(date);
    		createCell(row, titleStyle, AonMonth.values()[month].getName().substring(0,3) + " " + year, i+2);
    	}
    	
    	createCell(row, titleStyle, "TOTAL", 14);    	
    	return 1;
	}
	
	private Integer printValues(HSSFWorkbook workbook, HSSFSheet sheet, Domain domain, Fee fee, Date from, Integer rowIndex){
		Row row = sheet.createRow(rowIndex);
		CellStyle style = getValueStyle(workbook);   
		CellStyle doubleStyle = getDoubleStyle(workbook);      
		Registry registry = AON.getRegistry(domain.getName(), domain.getId(), "", fee.getCustomer());
		
		createCell(row, style, registry.getName(), 0);
		createCell(row, style, fee.getDescription(), 1);
		
		Integer period = BillingPeriod.values()[fee.getPeriod()].getValue();
		Date billingDate = fee.getBillingDate();
		Integer dif = AonDateUtils.getMonth(billingDate) - AonDateUtils.getMonth(from);
		if(dif < 0) dif = dif + 14;
		else dif = dif + 2;
		Double total = 0.0;
		Double discount = 1.0;
		if(fee.getDiscount() != null) discount = ((100 - fee.getDiscount())/100);
		Double price = fee.getPrice() * fee.getQuantity() * discount;
		while (dif < 14){
			createCell(row, doubleStyle, price, dif);
			total = total + price;
			dif = dif + period;
			if(period == 0) dif = 14;
		}
		createCell(row, doubleStyle, total, 14);
		return rowIndex+1;
	}
	
	private Integer printTotal(HSSFWorkbook workbook, HSSFSheet sheet, Integer rowIndex){
		Row row = sheet.createRow(rowIndex);		
		CellStyle doubleStyle = getDoubleStyle(workbook);      
		CellStyle titleStyle = getTitleStyle(workbook);
		createCell(row, titleStyle, "TOTAL", 0);
		createCell(row, titleStyle, "", 1);
		
		createFormulaCell(row, doubleStyle, "SUM(C1:C"+(rowIndex)+")", 2);
		createFormulaCell(row, doubleStyle, "SUM(D1:D"+(rowIndex)+")", 3);
		createFormulaCell(row, doubleStyle, "SUM(E1:E"+(rowIndex)+")", 4);
		createFormulaCell(row, doubleStyle, "SUM(F1:F"+(rowIndex)+")", 5);
		createFormulaCell(row, doubleStyle, "SUM(G1:G"+(rowIndex)+")", 6);
		createFormulaCell(row, doubleStyle, "SUM(H1:H"+(rowIndex)+")", 7);
		createFormulaCell(row, doubleStyle, "SUM(I1:I"+(rowIndex)+")", 8);
		createFormulaCell(row, doubleStyle, "SUM(J1:J"+(rowIndex)+")", 9);
		createFormulaCell(row, doubleStyle, "SUM(K1:K"+(rowIndex)+")", 10);
		createFormulaCell(row, doubleStyle, "SUM(L1:L"+(rowIndex)+")", 11);
		createFormulaCell(row, doubleStyle, "SUM(M1:M"+(rowIndex)+")", 12);
		createFormulaCell(row, doubleStyle, "SUM(N1:N"+(rowIndex)+")", 13);
		createFormulaCell(row, doubleStyle, "SUM(O1:O"+(rowIndex)+")", 14);
	
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
	
	private CellStyle getTitleStyle(HSSFWorkbook libro){
		 CellStyle style = libro.createCellStyle();
		 Font font = libro.createFont();
		 font.setFontHeightInPoints((short)7);
		 font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 style.setFont(font);
		 style.setAlignment(CellStyle.ALIGN_CENTER);
		 style.setBorderBottom(CellStyle.BORDER_MEDIUM); 
		 return style;
	}
	
	private CellStyle getValueStyle(HSSFWorkbook libro){
		CellStyle style3 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font2.setFontHeightInPoints((short) 7);
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		return style3;
	}
	
	private CellStyle getDoubleStyle(HSSFWorkbook libro){
		CellStyle style3 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font2.setFontHeightInPoints((short)7);
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_RIGHT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		return style3;
	}
}
