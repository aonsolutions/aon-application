package com.code.aon.webservice.commercial;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ProjectCommercialProperties;
import com.esferalia.aon.occam.api.model.project.ProjectSource;
import com.esferalia.aon.occam.api.model.project.ProjectStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "DownloadProjectCommercialExcel", urlPatterns = {"/aon_gwt_aio/download_projectCommercial_excel/*"})
public class DownloadProjectCommercialExcelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Filter projectCommercialFilter(HttpServletRequest req, ProjectCommercialProperties f) {
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String name = req.getParameter("name");
		String target = req.getParameter("target");
		String seller = req.getParameter("seller");
		String source = req.getParameter("source");
		String comments = req.getParameter("comments");
		String status = req.getParameter("status");
		String probability = req.getParameter("probability");
		String fromDate = req.getParameter("from_date");
		String toDate = req.getParameter("to_date");
		
		Filter filter = f.getDomainProperty().eq(domainId);
		
		if(name != null && !"null".equals(name) && !"".equals(name)) {
			filter = filter.and(f.getNameProperty().like(name));
		}
		
		if(source != null && !"null".equals(source) && !"".equals(source)) {
			System.out.println("SOURCE - " + source);
			filter = filter.and(f.getStatusProperty().eq(ProjectSource.valueOf(source).value()));
		}
		
		if(comments != null && !"null".equals(comments) && !"".equals(comments)) {
			filter = filter.and(f.getCommentsProperty().like(comments));
		}
		
		if(status != null && !"null".equals(status) && !"".equals(status)) {
			System.out.println("STATUS - " + status);
			filter = filter.and(f.getStatusProperty().eq(ProjectStatus.valueOf(status).value()));
		}
		
		if(probability != null && !"null".equals(probability) && !"".equals(probability)) {
			filter = filter.and(f.getProbabilityProperty().eq(AonNumberUtils.toInteger(probability)));
		}
		
		if(target != null && !"null".equals(target) && !"".equals(target)) {
			filter = filter.and(f.getTargetProperty().eq(Integer.parseInt(target)));
		}
		if(seller != null && !"null".equals(seller) && !"".equals(seller)) {
			filter = filter.and(f.getSellerProperty().eq(Integer.parseInt(seller)));
		}
		
		if(fromDate != null && !"null".equals(fromDate) && !"".equals(fromDate)) {
			Date date = AonDateUtils.simpleParse(fromDate);
			filter = filter.and(f.getDateProperty().ge(AonDateUtils.toSql(date)));
		}
		
		if(toDate != null && !"null".equals(toDate) && !"".equals(toDate)) {
			Date date = AonDateUtils.simpleParse(toDate);
			filter = filter.and(f.getDateProperty().le(AonDateUtils.toSql(date)));
		}
		
		return filter;
	}

	private Integer cont;
	private Row row;
	private HashMap<Integer, String> registries = new HashMap<>();
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		String domainName = req.getParameter("domain");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String userName = req.getParameter("username");
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));		
		
		
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Operaciones Comerciales");
     
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
    	c0.setCellValue("Fecha");
    	c0.setCellStyle(style);

    	Cell c1 = fila.createCell(1);
    	c1.setCellValue("Comercial");
    	c1.setCellStyle(style);
    	
    	Cell c2 = fila.createCell(2);
    	c2.setCellValue("Cliente Potencial");
    	c2.setCellStyle(style);
    	
    	Cell c3 = fila.createCell(3);
    	c3.setCellValue("Nombre");
    	c3.setCellStyle(style);
    	
    	Cell c4 = fila.createCell(4);
    	c4.setCellValue("Tipo");
    	c4.setCellStyle(style);
    	
    	Cell c5 = fila.createCell(5);
    	c5.setCellValue("Origen");
    	c5.setCellStyle(style);

    	Cell c6 = fila.createCell(6);
    	c6.setCellValue("Estado");
    	c6.setCellStyle(style);
    	
    	Cell c7 = fila.createCell(7);
    	c7.setCellValue("Fecha Estado");
    	c7.setCellStyle(style);
    	
    	Cell c8 = fila.createCell(8);
    	c8.setCellValue("Probabilidad");
    	c8.setCellStyle(style);
    	
    	cont = 1;
    	AON.getProjectCommercialStream(domain.getName(), domain.getId(), userName, f -> projectCommercialFilter(req, f))
    	.sorted((p1, p2) -> {
    		if(p2.getDate() == null && p1.getDate() == null)
    			return 0;            
    		else if(p2.getDate() == null)
    			return -1;
    		else if (p1.getDate() == null)
    			return 1; 	
    		return p2.getDate().compareTo(p1.getDate());	
    	})
    	.forEach(result -> {
    		row = hoja.createRow(cont++);
    		
			Cell ct0 = row.createCell(0);
			ct0.setCellValue(AonDateUtils.simpleFormat(result.getDate())); 
	    	
			Cell ct1 = row.createCell(1);
			ct1.setCellValue(getRegistryName(domain, userName, result.getSeller()));
	    	
			Cell ct2 = row.createCell(2);
			ct2.setCellValue(getRegistryName(domain, userName, result.getTarget()));
	    	
			Cell ct3 = row.createCell(3);
			ct3.setCellValue(result.getName());

			Cell ct4 = row.createCell(4);
			ct4.setCellValue(result.getProjectTypeName());	
			
			Cell ct5 = row.createCell(5);
			ct5.setCellValue(ProjectSource.safeValueOf(result.getSource()).getDescription());
	    	
			Cell ct6 = row.createCell(6);
			ct6.setCellValue(ProjectStatus.safeValueOf(result.getStatus()).getDescription());	
	    	
			Cell ct7= row.createCell(7);
			ct7.setCellValue(AonDateUtils.simpleFormat(result.getStatusDate()));	
			
			Cell ct8 = row.createCell(8);
			ct8.setCellValue(result.getProbability() != null ? result.getProbability().toString() : ""); 	
    	});
    	
    	for(Integer i = 0; i < 9; i++){
    		hoja.autoSizeColumn(i);
    	}
     
        libro.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Utils.giveBackData(resp, data , "projectCommercial.xls");
    }

	
	private String getRegistryName(Domain domain, String login, Integer id) {
		if(id == null) return "";
		else if(registries.containsKey(id))
			return registries.get(id);
		else {
			String name = AON.getRegistry(domain.getName(), domain.getId(), login, id).getName();
			registries.put(id, name);
			return name;
		}
	}
}
