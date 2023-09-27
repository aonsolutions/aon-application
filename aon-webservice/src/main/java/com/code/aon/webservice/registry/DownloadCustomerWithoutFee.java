package com.code.aon.webservice.registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
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
		String extend = req.getParameter("extend");
		Integer domainId = AonStringUtils.isBlank(req.getParameter("domainId")) ? 1 : Integer.parseInt(req.getParameter("domainId"));
		
		JSONObject filterJSON = new JSONObject(decode(req.getParameter("filter")));
		CustomerParams customerParams = createCustomerParams(domainId, filterJSON);
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));
		
		if(type.equalsIgnoreCase(MSG.EXCEL)){
			if(AonStringUtils.isBlank(extend))
				excel(req, resp, domain, userName, customerParams);
			else
				excelExtend(req, resp, domain, userName, customerParams);
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
	
	private void excelExtend(HttpServletRequest req, HttpServletResponse resp, Domain domain, String userName, CustomerParams customerParams) throws ServletException, IOException {
		HSSFWorkbook workbook = probaExtend(domain, userName, false, customerParams);

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
	
	private HSSFWorkbook probaExtend(Domain domain, String login, Boolean isPdf, CustomerParams customerParams) {		
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Clientes Sin Cuotas");
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
	
		rowIndex = printTitleExtend(workbook, sheet, isPdf);

		List<Customer> customers = AON.getCustomerWithoutFee(domain, login, customerParams);
        CellStyle valueStyle = getValueStyle(workbook, isPdf);		
		customers.stream().forEach(c -> {
			Row row = sheet.createRow(rowIndex);
			
			CustomerFull cFull = AON.getCustomerFull(domain.getName(), domain.getId(), login, c.getId());
			
			createCell(row, valueStyle, cFull.getRegistry().getId().toString(), 0);
	        createCell(row, valueStyle, cFull.getRegistry().getDocument() != null ? c.getDocument() : "", 1);
	        createCell(row, valueStyle, cFull.getRegistry().getName() != null ? c.getName() : "", 2);
	        createCell(row, valueStyle, cFull.getRegistry().getAlias() != null ? c.getAlias() : "", 3);
	        createCell(row, valueStyle, cFull.getRegistry().getStatus().getDescription(), 4);
	        createCell(row, valueStyle, cFull.getRegistry().isLegalPerson() ? "Persona Juridica" : "Persona Fisica", 5);
	        createCell(row, valueStyle, cFull.getRegistry().getScope() != null ? cFull.getRegistry().getScope().getDescription() : "", 6);
	        createCell(row, valueStyle, cFull.getAccount() != null ? cFull.getAccount().getCode() : "", 7);
	        createCell(row, valueStyle, cFull.getRegistry().getNationality() != null ? cFull.getRegistry().getNationality().getName() : "", 8);
	       
	        Optional<RegistryAddress> address = cFull.getAddresses() == null ? Optional.empty() : cFull.getAddresses().stream().findFirst();
	        createCell(row, valueStyle, address.isPresent() ? address.get().getAddress() : "", 9);
	        createCell(row, valueStyle, address.isPresent() ? address.get().getCity() : "", 10);
	        createCell(row, valueStyle, address.isPresent() ? address.get().getZip() : "", 11);
	        createCell(row, valueStyle, address.isPresent() ? address.get().getGeozoneName() : "", 12);
	        
	        Optional<RegistryMedia> phone = cFull.getPhoneMedias() == null ? Optional.empty() : cFull.getPhoneMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE)).findFirst();
	        createCell(row, valueStyle, phone.isPresent() ? phone.get().getValue() : "", 13);
	        
	        Optional<RegistryMedia> mobile = cFull.getPhoneMedias() == null ? Optional.empty() : cFull.getPhoneMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR)).findFirst();
	        createCell(row, valueStyle, mobile.isPresent() ? mobile.get().getValue() : "", 14);
	        
	        Optional<RegistryMedia> fax = cFull.getPhoneMedias() == null ? Optional.empty() : cFull.getPhoneMedias().stream().filter(m -> m.getMedia().equals(MediaType.FAX)).findFirst();
	        createCell(row, valueStyle, fax.isPresent() ? fax.get().getValue() : "", 15);
	        
	        Optional<RegistryMedia> email = cFull.getEmailMedias() == null ? Optional.empty() : cFull.getEmailMedias().stream().findFirst();
	        createCell(row, valueStyle, email.isPresent() ? email.get().getValue() : "", 16);
	        
	        rowIndex++;	
		});

    	for(Integer i = 0; i < 4; i++)
    		sheet.autoSizeColumn(i);
    	
		return workbook;
	}
	
	private Integer printTitleExtend(HSSFWorkbook libro, HSSFSheet hoja, Boolean isPdf){
		Row row = hoja.createRow(0);
		  
        CellStyle titleStyle = getTitleStyle(libro, isPdf);        

        createCell(row, titleStyle, "Código", 0);
        createCell(row, titleStyle, "Documento", 1);
        createCell(row, titleStyle, "Razón Social", 2);
        createCell(row, titleStyle, "Alias", 3);
        createCell(row, titleStyle, "Estado", 4);
        createCell(row, titleStyle, "Entidad", 5);
        createCell(row, titleStyle, "Ambito", 6);
        createCell(row, titleStyle, "Cuenta Contable", 7);
        createCell(row, titleStyle, "Nacionalidad", 8);
        createCell(row, titleStyle, "Direccion", 9);
        createCell(row, titleStyle, "Localidad", 10);
        createCell(row, titleStyle, "C.P.", 11);
        createCell(row, titleStyle, "Provincia", 12);
        createCell(row, titleStyle, "Telefono", 13);
        createCell(row, titleStyle, "Movil", 14);
        createCell(row, titleStyle, "Fax", 15);
        createCell(row, titleStyle, "Email", 16);
        
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
