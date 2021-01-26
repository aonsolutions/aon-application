package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedList;

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
import org.json.JSONObject;

import com.esferalia.aon.gwt.template.server.imports.DownloadImportTemplateServlet;
import com.esferalia.aon.gwt.template.server.imports.IConstants;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

@WebServlet(name = "DownloadTemplatesFee", urlPatterns = { "/aon_gwt_template/ms/gwt_download_fee/*"
															 ,"/aon_gwt_aio/ms/gwt_download_fee/*"})
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
	
		LinkedList<String> columnList = DownloadImportTemplateServlet.getFeeColumnList();
		
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
   
        LinkedList<Fee> fees = AON.getFeeList(domain.getName(), domain.getId(), login, f -> feeFilter(domain, filterJSON, f));
        for(Integer i = 0; i < fees.size(); i++) {
        	Row row = hoja.createRow(i+1);
        	for(Integer j = 0; j < columnList.size(); j++) {
        		Cell cell = row.createCell(j);
        		String title = columnList.get(j);
        		Fee fee = fees.get(i);
        		if(IConstants.CLIENTE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getCustomer().getDocument());
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
        		} else if((IConstants.FECHA_FACTURACION.equalsIgnoreCase(title) || IConstants.FECHA_FACTURACIÓN.equalsIgnoreCase(title))&& fee.getBillingDate() != null) {
        			cell.setCellValue(fee.getBillingDate());
        			cell.setCellStyle(dateStyle);
        		} else if(IConstants.PERIODO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getPeriod().getValue());
        		} else if(IConstants.COMERCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getSeller().getRegistryDocument());
        		} else if(IConstants.CENTRO_DE_TRABAJO.equalsIgnoreCase(title) || IConstants.CENTRO_TRABAJO.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getWorkplace().getDescription());
        		} else if(IConstants.GRUPO_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_FACTURACIÓN.equalsIgnoreCase(title) || IConstants.GRUPO.equalsIgnoreCase(title)
        				|| IConstants.GRUPO_DE_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_DE_FACTURACIÓN.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getInvoicingGroup().getDescription());
        		} else if(IConstants.CONFIDENCIAL.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.isConfidential());
        		} else if(IConstants.DESCRIPCION.equalsIgnoreCase(title) || IConstants.DESCRIPCIÓN.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getDescription());
        		} else if(IConstants.EXPEDIENTE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getProject().getName());
        		} else if(IConstants.DETALLE_1.equalsIgnoreCase(title) || IConstants.DETALLE1.equalsIgnoreCase(title) || IConstants.DETALLE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail());
        		} else if(IConstants.DETALLE_2.equalsIgnoreCase(title) || IConstants.DETALLE2.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail2());
        		} else if(IConstants.DETALLE_3.equalsIgnoreCase(title) || IConstants.DETALLE3.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getDetail3());
        		} else if(IConstants.CODIGO_DE_BARRAS.equalsIgnoreCase(title) || IConstants.CÓDIGO_DE_BARRAS.equalsIgnoreCase(title) || IConstants.BARCODE.equalsIgnoreCase(title)
        				|| IConstants.CODIGO_BARRAS.equalsIgnoreCase(title) || IConstants.CÓDIGO_BARRAS.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getBarcode());
        		} else if(IConstants.NÚMERO_DE_SERIE.equalsIgnoreCase(title) || IConstants.NUMERO_DE_SERIE.equalsIgnoreCase(title) || IConstants.SERIAL_NUMBER.equalsIgnoreCase(title)
        				|| IConstants.NÚMERO_SERIE.equalsIgnoreCase(title) || IConstants.NUMERO_SERIE.equalsIgnoreCase(title)) {
        			cell.setCellValue(fee.getItem().getSerialNumber());
        		} else if(IConstants.LINEA.equalsIgnoreCase(title) || IConstants.LÍNEA.equalsIgnoreCase(title)) {
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
	
	
	private Filter feeFilter(Domain domain, JSONObject filterJSON, FeeProperties f) {
		Filter filter =  f.getDomainProperty().eq(domain.getId());

		if(filterJSON.opt("from") != null) { // FECHA FACTURACIÓN
			java.sql.Date from = new java.sql.Date(filterJSON.optLong("from"));
			filter = filter.and(f.getBillingDateProperty().ge(from));
		}
		
		if(filterJSON.opt("to") != null) { // FECHA FACTURACIÓN
			java.sql.Date to = new java.sql.Date(filterJSON.optLong("to"));
			filter = filter.and(f.getBillingDateProperty().le(to));			
		}
		
		if(filterJSON.opt("item") != null) {
			Integer item = filterJSON.optInt("item");
			filter = filter.and(f.getItemProperty().eq(item));
		}
		
		if(filterJSON.opt("status") != null) {
			Integer status = filterJSON.optInt("status");
			filter = filter.and(f.getStatusProperty().eq(status.byteValue()));
		}
		
		if(filterJSON.opt("scope") != null) {
			Integer scope = filterJSON.optInt("scope");
			filter = filter.and(f.getScopeProperty().eq(scope));
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
