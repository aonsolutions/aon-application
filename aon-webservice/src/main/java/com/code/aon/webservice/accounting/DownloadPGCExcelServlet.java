package com.code.aon.webservice.accounting;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
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

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;

@WebServlet(name = "DownloadPGCExcel", urlPatterns = {"/aon_gwt_aio/download_pgc_excel/*"})
public class DownloadPGCExcelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Filter pgcFilter(HttpServletRequest req, AccountProperties f) {
		String code = req.getParameter("code");
		String description = req.getParameter("description");
		String alias = req.getParameter("alias");
		String active = req.getParameter("active");
		String costCenter = req.getParameter("costCenter");
		String entryEnabled = req.getParameter("entryEnabled");
		
		Filter filter = null;
		
		if(code != null && !"null".equals(code) && !"".equals(code)) {
			filter = (filter == null)
					? f.getCodeProperty().like(code) 
					: filter.and(f.getCodeProperty().like(code));
		}
		if(description != null && !"null".equals(description) && !"".equals(description)) {
			filter = (filter == null)
					? f.getDescriptionProperty().like(description)
					: filter.and(f.getDescriptionProperty().like(description));
		}
		
		if(alias != null && !"null".equals(alias) && !"".equals(alias)) {
			filter = (filter == null)
					? f.getAliasProperty().like(alias)
					: filter.and(f.getAliasProperty().like(alias));
		}
		
		if(active != null && !"null".equals(active) && !"".equals(active)) {
			// TODO
		}

		if(costCenter != null && !"null".equals(costCenter) && !"".equals(costCenter)) {
			filter = (filter == null)
					? f.getCostCenterProperty().like(costCenter)
					: filter.and(f.getCostCenterProperty().like(costCenter));
		}
		
		if(entryEnabled != null && !"null".equals(entryEnabled) && !"".equals(entryEnabled)) {
			// TODO
		}
		
		return filter;
	}

	private Integer cont;
	private Row row;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		String domainName = req.getParameter("domain");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String userName = req.getParameter("username");
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));		
		
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Plan General Contable");
     
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
		style2.setAlignment(HorizontalAlignment.RIGHT);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		
		Cell c0 = fila.createCell(0);
    	c0.setCellValue("CÓDIGO");
    	c0.setCellStyle(style);

    	Cell c1 = fila.createCell(1);
    	c1.setCellValue("DESCRIPCIÓN");
    	c1.setCellStyle(style);
    	
    	Cell c2 = fila.createCell(2);
    	c2.setCellValue("ALIAS");
    	c2.setCellStyle(style);
    	
    	Cell c3 = fila.createCell(3);
    	c3.setCellValue("¿PERMITE APUNTES?");
    	c3.setCellStyle(style);
    	
    
    	
    	cont = 1;
    	ACCOUNTING.getAccounts(domain.getName(), domain.getId(), userName, f -> pgcFilter(req, f))
    	.sorted((p1, p2)-> p1.getCode().compareTo(p2.getCode()))
    	.forEach(result -> {
    		row = hoja.createRow(cont++);
    		
			Cell ct0 = row.createCell(0);
			ct0.setCellValue(result.getCode()); 
	    	
			Cell ct1 = row.createCell(1);
			ct1.setCellValue(result.getDescription());
	    	
			Cell ct2 = row.createCell(2);
			ct2.setCellValue(result.getAlias());
	    	
			Cell ct3 = row.createCell(3);
			ct3.setCellValue(result.isEntryEnabled() ? "SÍ" : "-");
    	});
    	
    	for(Integer i = 0; i < 9; i++){
    		hoja.autoSizeColumn(i);
    	}
     
        libro.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Utils.giveBackData(resp, data , "pgc.xls");
    }
}
