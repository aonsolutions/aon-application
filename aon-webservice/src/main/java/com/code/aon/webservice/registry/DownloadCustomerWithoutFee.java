package com.code.aon.webservice.registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DownloadCustomerWithoutFee", urlPatterns = {"/aon_gwt_aio/ms/download_customer_without_fee/*",
																"/download_customer_without_fee/*",
																"/aon_gwt_fiscal/download_customer_without_fee/*"})
public class DownloadCustomerWithoutFee extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER  = Logger.getLogger(DownloadCustomerWithoutFee.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Fee projection download - GET METHOD");
		
		String domainName = req.getParameter("domain");
		String userName = req.getParameter("login");
		String type = req.getParameter("type");
		Integer domainId = AonStringUtils.isBlank(req.getParameter("domainId")) ? 1 : Integer.parseInt(req.getParameter("domainId"));
		
		JSONObject filterJSON = new JSONObject(decode(req.getParameter("filter")));
		CustomerParams customerParams = createCustomerParams(domainId, filterJSON);
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));
		
		if(type.equalsIgnoreCase(MSG.EXCEL)){
			excel(req, resp, domain, userName, customerParams);
		}
	}

	private void excel(HttpServletRequest req, HttpServletResponse resp, Domain domain, String userName, CustomerParams customerParams) throws ServletException, IOException {
		HSSFWorkbook workbook = proba(domain, userName, false, customerParams);

		ByteArrayOutputStream archivo = new ByteArrayOutputStream();
		workbook.write(archivo);  
		byte[] data = archivo.toByteArray();
		archivo.close();
		workbook.close();

		Utils.giveBackData(resp, data, "customerWithoutFee.xls");
	}
	
	Integer rowIndex;
	private HSSFWorkbook proba(Domain domain, String login, Boolean isPdf, CustomerParams customerParams) {		
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Clientes Sin Cuotas");
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
	
		rowIndex = printTitle(workbook, sheet, isPdf);

		List<Customer> customers = AON.getCustomerWithoutFee(domain, login, customerParams);
        CellStyle valueStyle = getValueStyle(workbook, isPdf);		
		customers.stream().forEach(c -> {
			Row row = sheet.createRow(rowIndex);
		
	        createCell(row, valueStyle, c.getId().toString(), 0);
	        createCell(row, valueStyle, c.getDocument() != null ? c.getDocument() : "", 1);
	        createCell(row, valueStyle, c.getName() != null ? c.getName() : "", 2);
	        createCell(row, valueStyle, c.getAlias() != null ? c.getAlias() : "", 3);
	        createCell(row, valueStyle, c.getStatus().getDescription(), 4);
	        rowIndex++;	
		});

    	for(Integer i = 0; i < 4; i++)
    		sheet.autoSizeColumn(i);
    	
		return workbook;
	}
	
	private Integer printTitle(HSSFWorkbook libro, HSSFSheet hoja, Boolean isPdf){
		Row row = hoja.createRow(0);
		  
        CellStyle titleStyle = getTitleStyle(libro, isPdf);        

        createCell(row, titleStyle, "Código", 0);
        createCell(row, titleStyle, "Documento", 1);
        createCell(row, titleStyle, "Razón Social", 2);
        createCell(row, titleStyle, "Alias", 3);
        createCell(row, titleStyle, "Estado", 4);
        
    	return 1;
	}
	
	
	private void createCell(Row row, CellStyle style, String value, Integer columnIndex){
		Cell c = row.createCell(columnIndex);
    	c.setCellValue(value);
    	c.setCellStyle(style);
	}
		
	
	private CellStyle getTitleStyle(HSSFWorkbook libro, Boolean isPdf){
		 CellStyle style = libro.createCellStyle();
		 Font font = libro.createFont();
		 if(isPdf){
			 font.setFontHeightInPoints((short)7);
		 } else font.setFontHeightInPoints((short)9);
		 font.setBold(true);
		 style.setFont(font);
		 style.setAlignment(HorizontalAlignment.CENTER);
		 style.setBorderBottom(BorderStyle.MEDIUM); 
		 return style;
	}
	
	private CellStyle getValueStyle(HSSFWorkbook libro, Boolean isPdf){
		CellStyle style3 = libro.createCellStyle();
        Font font2 = libro.createFont();
        if(isPdf){
        	font2.setFontHeightInPoints((short)7);
        } else font2.setFontHeightInPoints((short)9);
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		return style3;
	}
	

	
	private CustomerParams createCustomerParams(Integer domainId, JSONObject filterJSON) {
		CustomerParams customerParams = new CustomerParams();
		
		customerParams.setDomain(domainId);
		
		if(filterJSON.opt("customer") != null) {
			String customer = filterJSON.optString("customer");
			customerParams.setCustomer(customer);
		}
		
		if(filterJSON.opt("status") != null) {
			Integer status = filterJSON.optInt("status");
			customerParams.setCustomerStatus(status.byteValue());
		}
		
		if(filterJSON.opt("customerIds") != null) {
			 JSONObject customerIds = filterJSON.optJSONObject("customerIds");
			 List<Integer> ids = new ArrayList<>();
			 for(int i=0; i<customerIds.length(); i++) {
				 ids.add(customerIds.optInt("customerId"+i));
			 }
			
			 customerParams.setCustomerIds(ids);
		}
		
		customerParams.setOffset(null);
		customerParams.setLimit(null);
		
		return customerParams;
	}
	
	public String decode(String value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value.getBytes()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
	
}
