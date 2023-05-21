package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.template.server.imports.DownloadImportTemplateServlet;
import com.esferalia.aon.gwt.template.server.imports.IConstants;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "DownloadTemplatesFee", urlPatterns = { "/aon_gwt_template/ms/gwt_download_fee/*"
															 ,"/aon_gwt_aio/ms/gwt_download_fee/*"
															 ,"/aon_gwt_fiscal/ms/gwt_download_fee/*"})
public class DownloadFeeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domain_id = req.getParameter("domain_id");
		String domain_name = req.getParameter("domain_name");
		String login = req.getParameter("username");

		Integer domainId = Integer.parseInt(domain_id);
		Domain domain = AON.getDomain(domain_name, domainId, login);
		JSONObject filterJSON = new JSONObject(decode(req.getParameter("filter")));
	
		LinkedList<String> columnList = DownloadImportTemplateServlet.getExportFeeColumnList();
		
		HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Plantilla 1");
        Integer columns = columnList.size();
        
        Row fila = hoja.createRow(0);
        
        fila.setHeightInPoints(16);
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM); 
      
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
      	style2.setFont(font2);
      	style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
      	
        CellStyle style3 = libro.createCellStyle();
      	style3.setFont(font2);
      	style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		
        CellStyle dateStyle = libro.createCellStyle();
        dateStyle.setDataFormat(libro.getCreationHelper().createDataFormat().getFormat("dd/mm/YYYY"));  

        for(Integer i = 0; i< columnList.size(); i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(columnList.get(i));
        	celda.setCellStyle(style);
        }
        Cell celdaf = fila.createCell(columns);
        celdaf.setCellStyle(style);
   

        
        LinkedList<Fee> fees;
        if(filterJSON.opt("segment") != null) {
			JSONArray segment = filterJSON.optJSONArray("segment");
			if(segment.length() > 0) {
				Integer[] segments = new Integer[segment.length()];
				for (Integer i = 0; i < segment.length(); i++) {
					segments[i] = segment.getInt(i);
				}
				Integer[] cIDs = AON.getRSegmentStream(domain.getName(), domain.getId(), login, f -> f.getSegmentProperty().in(segments));
				fees = AON.getFeeList(domain.getName(), domain.getId(), login, f -> feeFilter(domain, filterJSON, f, cIDs));
			}else  fees = AON.getFeeList(domain.getName(), domain.getId(), login, f -> feeFilter(domain, filterJSON, f, null));
		} else  fees = AON.getFeeList(domain.getName(), domain.getId(), login, f -> feeFilter(domain, filterJSON, f, null));
        for(Integer i = 0; i < fees.size(); i++) {
        	Row row = hoja.createRow(i+1);
        	for(Integer j = 0; j < columnList.size(); j++) {
        		Cell cell = row.createCell(j);
        		String title = columnList.get(j);
        		Fee fee = fees.get(i);
        		if(IConstants.CLIENTE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getCustomer().getDocument());
        		} else if(IConstants.RAZON_SOCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getCustomer().getName());
        		} else if(IConstants.PRODUCTO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getProduct().getCode());
        		} else if(IConstants.CANTIDAD.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getQuantity());
        		} else if(IConstants.PRECIO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getPrice());
        		} else if(IConstants.DESCUENTO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getDiscount());
        		} else if(IConstants.FECHA_INICIO.equalsIgnoreCase(title)&& fee.getStartDate() != null) {
        			cell.setCellValue(fee.getStartDate());
        			cell.setCellStyle(dateStyle);
        		} else if(IConstants.FECHA_FIN.equalsIgnoreCase(title) && fee.getEndDate() != null) {
        			cell.setCellValue(fee.getEndDate());
        			cell.setCellStyle(dateStyle);
        		} else if((IConstants.FECHA_FACTURACION.equalsIgnoreCase(title) || IConstants.FECHA_FACTURACION2.equalsIgnoreCase(title))&& fee.getBillingDate() != null) {
        			cell.setCellValue(fee.getBillingDate());
        			cell.setCellStyle(dateStyle);
        		} else if(IConstants.PERIODO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getPeriod().getValue());
        		} else if(IConstants.COMERCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getSeller().getDocument());
        		} else if(IConstants.NOMBRE_COMERCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getSeller().getName());
        		} else if(IConstants.CENTRO_DE_TRABAJO.equalsIgnoreCase(title) || IConstants.CENTRO_TRABAJO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getWorkplace().getDescription());
        		} else if(IConstants.GRUPO_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_FACTURACION2.equalsIgnoreCase(title) || IConstants.GRUPO.equalsIgnoreCase(title)
        				|| IConstants.GRUPO_DE_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_DE_FACTURACION2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getInvoicingGroup().getDescription());
        		} else if(IConstants.CONFIDENCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.isConfidential());
        		} else if(IConstants.DESCRIPCION.equalsIgnoreCase(title) || IConstants.DESCRIPCION2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getDescription());
        		} else if(IConstants.EXPEDIENTE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getProject().getName());
        		} else if(IConstants.DETALLE_1.equalsIgnoreCase(title) || IConstants.DETALLE1.equalsIgnoreCase(title) || IConstants.DETALLE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail());
        		} else if(IConstants.DETALLE_2.equalsIgnoreCase(title) || IConstants.DETALLE2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail2());
        		} else if(IConstants.DETALLE_3.equalsIgnoreCase(title) || IConstants.DETALLE3.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail3());
        		} else if(IConstants.CODIGO_DE_BARRAS.equalsIgnoreCase(title) || IConstants.CODIGO_DE_BARRAS2.equalsIgnoreCase(title) || IConstants.BARCODE.equalsIgnoreCase(title)
        				|| IConstants.CODIGO_BARRAS.equalsIgnoreCase(title) || IConstants.CODIGO_BARRAS2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getBarcode());
        		} else if(IConstants.NUMERO_DE_SERIE2.equalsIgnoreCase(title) || IConstants.NUMERO_DE_SERIE.equalsIgnoreCase(title) || IConstants.SERIAL_NUMBER.equalsIgnoreCase(title)
        				|| IConstants.NUMERO_SERIE2.equalsIgnoreCase(title) || IConstants.NUMERO_SERIE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getSerialNumber());
        		} else if(IConstants.LINEA.equalsIgnoreCase(title) || IConstants.LINEA2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getLine());
        		}
        	}
        }
        
        for(Integer h = 0; h< columns;h++){
        	hoja.autoSizeColumn(h);
        }
        
        libro.write(archivo);       
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Integer length = data.length;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        resp.addHeader("Content-Disposition","attachment; filename=\"Cuotas.xls\"");
        resp.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
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
		
	}
	
	
	private Filter feeFilter(Domain domain, JSONObject filterJSON, FeeProperties f, Integer[] a) {
		Filter filter =  f.getDomainProperty().eq(domain.getId());

		if(filterJSON.opt("from") != null) { // FECHA FACTURACIÓN
			java.sql.Date from = new java.sql.Date(filterJSON.optLong("from"));
			filter = filter.and(f.getBillingDateProperty().ge(from))
					.and(f.getFinalDateProperty().ge(from).or(f.getFinalDateProperty().isNull()));	
		}
		
		if(filterJSON.opt("to") != null) { // FECHA FACTURACIÓN
			java.sql.Date to = new java.sql.Date(filterJSON.optLong("to"));
			filter = filter.and(f.getBillingDateProperty().le(to))
					.and(f.getInitialDateProperty().le(to));
		}
		
		if(a != null) {
			filter = filter.and(f.getCustomerProperty().in(a));
		}
		
		if(filterJSON.opt("scope") != null) {
			Integer scope = filterJSON.optInt("scope");
			filter = filter.and(f.getScopeProperty().eq(scope));
		}
		
		if(filterJSON.opt("category") != null) {
			Integer category = filterJSON.optInt("category");
			filter = filter.and(f.getCategoryProperty().eq(category));
		}
		
		if(filterJSON.opt("period") != null) {
			Integer period = filterJSON.optInt("period");
			filter = filter.and(f.getPeriodProperty().eq(period.shortValue()));
		}
		
		// CUSTOMER FEE
		
		if(null != filterJSON.opt("month") && null == filterJSON.opt("year")) {
			Integer month = filterJSON.optInt("month") + 1;
			filter = filter.and(f.getMonthBillingDateProperty().eq(month));
		} else if(null == filterJSON.opt("month") && null != filterJSON.opt("year")) {
			Date startBillingDate = new Date(filterJSON.optInt("year"), 0, 1);
			Date endBillingDate = new Date(filterJSON.optInt("year"), 11, 31);
			
			filter = filter.and(f.getBillingDateProperty().between(startBillingDate, endBillingDate));
		} else if(null != filterJSON.opt("month") && null != filterJSON.opt("year")) {
			Date billingDate = new Date(filterJSON.optInt("year"), filterJSON.optInt("month"), 1);
			filter = filter.and(f.getBillingDateProperty().eq(billingDate));
		}
		
		if(filterJSON.opt("periodicity") != null) {
			Integer periodicity = filterJSON.optInt("periodicity");
			filter = filter.and(f.getPeriodProperty().eq(periodicity.shortValue()));
		}
		
		if(filterJSON.opt("customer") != null) {
			Integer customer = filterJSON.optInt("customer");
			filter = filter.and(f.getCustomerProperty().eq(customer));
		}
		
		if(filterJSON.opt("status") != null) {
			Integer status = filterJSON.optInt("status");
			filter = filter.and(f.getStatusProperty().eq(status.byteValue()));
		}
		
		if(filterJSON.opt("segment") != null) {
			Integer segment = filterJSON.optInt("segment");
			filter = filter.and(f.getSegmentProperty().eq(segment));
		}
		
		if(filterJSON.opt("startDate") != null) {
			java.sql.Date startDate = new java.sql.Date(filterJSON.optLong("startDate"));
			
			switch (filterJSON.optInt("startDateCompare")) {
				case (byte) 1:
					filter = filter.and(f.getInitialDateProperty().le(startDate));
					break;
				case (byte) 2:
					filter = filter.and(f.getInitialDateProperty().ge(startDate));
					break;
				default:
					filter = filter.and(f.getInitialDateProperty().eq(startDate));
					break;
			}
		}
		
		if(filterJSON.opt("endDate") != null) {
			java.sql.Date endDate = new java.sql.Date(filterJSON.optLong("endDate"));
			
			switch (filterJSON.optInt("endDateCompare")) {
				case (byte) 1:
					filter = filter.and(f.getFinalDateProperty().le(endDate));
					break;
				case (byte) 2:
					filter = filter.and(f.getFinalDateProperty().ge(endDate));
					break;
				default:
					filter = filter.and(f.getFinalDateProperty().eq(endDate));
					break;
			}
		}
		
		if(filterJSON.opt("product") != null) {
			Integer product = filterJSON.optInt("product");
			filter = filter.and(f.getItemProperty().eq(product));
		}
		
		if(filterJSON.opt("item") != null) {
			Integer item = filterJSON.optInt("item");
			filter = filter.and(f.getItemProperty().eq(item));
		}
		
		if(filterJSON.opt("productCategory") != null) {
			Integer productCategory = filterJSON.optInt("productCategory");
			filter = filter.and(f.getProductCategoryProperty().eq(productCategory));
		}
		
		if(filterJSON.opt("productTag") != null) {
			Integer productTag = filterJSON.optInt("productTag");
			filter = filter.and(f.getProductTagProperty().eq(productTag));
		}
		
		if(filterJSON.opt("quantity") != null) {
			String quantityStr = filterJSON.optString("quantity");
			if(AonStringUtils.containsIgnoreCase(quantityStr, ":")) {
				Double quantityStart = Double.parseDouble(quantityStr.split(":")[0].trim());
				Double quantityEnd = Double.parseDouble(quantityStr.split(":")[1].trim());
				filter = filter.and(f.getQuantityProperty().ge(quantityStart));
				filter = filter.and(f.getQuantityProperty().le(quantityEnd));
			} else if(AonStringUtils.containsIgnoreCase(quantityStr, ">")) {
				String quantityStrSplit = quantityStr.split(">")[1].trim();
				if(AonStringUtils.containsIgnoreCase(quantityStr, "=")) {
					quantityStrSplit = quantityStrSplit.split("=")[1].trim();
					Double quantity = Double.parseDouble(quantityStrSplit);
					filter = filter.and(f.getQuantityProperty().ge(quantity));
				} else {
					Double quantity = Double.parseDouble(quantityStrSplit);
					filter = filter.and(f.getQuantityProperty().gt(quantity));
				}
			} else if(AonStringUtils.containsIgnoreCase(quantityStr, "<")) {
				String quantityStrSplit = quantityStr.split("<")[1].trim();
				if(AonStringUtils.containsIgnoreCase(quantityStrSplit, "=")) {
					quantityStrSplit = quantityStr.split("=")[1].trim();
					Double quantity = Double.parseDouble(quantityStrSplit);
					filter = filter.and(f.getQuantityProperty().le(quantity));
				} else {
					Double quantity = Double.parseDouble(quantityStr);
					filter = filter.and(f.getQuantityProperty().lt(quantity));
				}
			} else {
				Double quantity = Double.parseDouble(quantityStr);
				filter = filter.and(f.getQuantityProperty().eq(quantity));
			}
		}
		
		if(filterJSON.opt("price") != null) {
			String priceStr = filterJSON.optString("price");
			if(AonStringUtils.containsIgnoreCase(priceStr, ":")) {
				Double priceStart = Double.parseDouble(priceStr.split(":")[0].trim());
				Double priceEnd = Double.parseDouble(priceStr.split(":")[1].trim());
				filter = filter.and(f.getPriceProperty().ge(priceStart));
				filter = filter.and(f.getPriceProperty().le(priceEnd));
			} else if(AonStringUtils.containsIgnoreCase(priceStr, ">")) {
				String priceStrSplit = priceStr.split(">")[1].trim();
				if(AonStringUtils.containsIgnoreCase(priceStr, "=")) {
					priceStrSplit = priceStrSplit.split("=")[1].trim();
					Double price = Double.parseDouble(priceStrSplit);
					filter = filter.and(f.getPriceProperty().ge(price));
				} else {
					Double price = Double.parseDouble(priceStrSplit);
					filter = filter.and(f.getPriceProperty().gt(price));
				}
			} else if(AonStringUtils.containsIgnoreCase(priceStr, "<")) {
				String priceStrSplit = priceStr.split("<")[1].trim();
				if(AonStringUtils.containsIgnoreCase(priceStrSplit, "=")) {
					priceStrSplit = priceStrSplit.split("=")[1].trim();
					Double price = Double.parseDouble(priceStrSplit);
					filter = filter.and(f.getPriceProperty().le(price));
				} else {
					Double price = Double.parseDouble(priceStrSplit);
					filter = filter.and(f.getPriceProperty().lt(price));
				}
			} else {
				Double price = Double.parseDouble(priceStr);
				filter = filter.and(f.getPriceProperty().eq(price));
			}
		}
		
		if(filterJSON.opt("discount") != null) {
			String discount = filterJSON.optString("discount");
			filter = filter.and(f.getDiscountExprProperty().eq(discount));
		}
		
		if(filterJSON.opt("seller") != null) {
			Integer seller = filterJSON.optInt("seller");
			filter = filter.and(f.getSellerProperty().eq(seller));
		}
		
		if(filterJSON.opt("workplace") != null) {
			Integer workplace = filterJSON.optInt("workplace");
			filter = filter.and(f.getWorkplaceProperty().eq(workplace));
		}
		
		if(filterJSON.opt("invoicingGroup") != null) {
			Integer invoicingGroup = filterJSON.optInt("invoicingGroup");
			filter = filter.and(f.getInvoicingGroupProperty().eq(invoicingGroup));
		}
		
		if(filterJSON.opt("project") != null) {
			Integer project = filterJSON.optInt("project");
			filter = filter.and(f.getProjectProperty().eq(project));
		}
		
		if(filterJSON.opt("feeIds") != null) {
			 JSONObject feeIds = filterJSON.optJSONObject("feeIds");
			 List<Integer> ids = new ArrayList<>();
			 for(int i=0; i<feeIds.length(); i++) {
				 ids.add(feeIds.optInt("feeId"+i));
			 }
			
			filter = filter.and(f.getIdProperty().in(ids.toArray(Integer[]::new)));
		}
		
		return filter;
	
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
