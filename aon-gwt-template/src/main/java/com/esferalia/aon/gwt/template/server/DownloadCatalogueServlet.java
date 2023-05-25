package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.esferalia.aon.gwt.template.jooq.DBCatalogue;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.watson.server.AonDateUtils;

@WebServlet(name = "DownloadTemplatesCatalogue", urlPatterns = { "/aon_gwt_template/gwt_download_catalogue/*"
																 ,"/aon_gwt_aio/gwt_download_catalogue/*"})
public class DownloadCatalogueServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String domainName = p_request.getParameter("domain_name");
		String domain_id = p_request.getParameter("domain_id");
        String workplace = p_request.getParameter("workplace");
        if(workplace.contains("*")){
        	Integer i = workplace.indexOf('*');
        	workplace = workplace.substring(0,i) + "&" + workplace.substring(i+1);
        }
        String department = p_request.getParameter("department");
        String template_id = p_request.getParameter("template_id");
        String login = p_request.getParameter("username");
        Integer domainId = Integer.parseInt(domain_id);
       
        Domain domain = new Domain().setName(domainName).setId(domainId);
        Workplace wp = null;
        if(!workplace.equals("-"))
        		wp = DBCatalogue.getWorkplace(domain, new User().setLogin(login), workplace);
        
        Department dt = null;
        if(!department.equals("-"))
        	dt = DBCatalogue.getDepartment(domain, wp, department, login);

        byte[] b = null ;
        
        if(template_id!=""){
        	Integer id = Integer.parseInt(template_id);
        	b = DBConsults.getTemplate(domain, login, id);
        }
        else return;

        TemplateInfo aux = null;
		try {
			aux = com.esferalia.aon.gwt.template.server.Utils.readxml(new ByteArrayInputStream(b));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Plantilla 1");
        Integer columns = aux.getColumns().size();
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()));
     
        Row rowInfo = hoja.createRow(0);
        Row fila = hoja.createRow(1);
        
        String info = "Catalogo ## " + workplace + " ## " + department + " ## "
        		+ AonDateUtils.getDay(new Date())
        		+ "-" + (AonDateUtils.getMonth(new Date()) +1)
        		+ "-" +  AonDateUtils.getYear(new Date());
        
        
        rowInfo.setHeightInPoints(16);
        fila.setHeightInPoints(16);
        CellStyle style = libro.createCellStyle();CellStyle styleInfo = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);styleInfo.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);styleInfo.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM); styleInfo.setBorderBottom(BorderStyle.MEDIUM);
       	styleInfo.setFillBackgroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
       
       	Cell cellInfo = rowInfo.createCell(0);
       	cellInfo.setCellValue(info);
       	cellInfo.setCellStyle(styleInfo);
        
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.RIGHT);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		
		for(Integer i = 0; i< columns; i++){
	        	Cell celda = fila.createCell(i);
	        	celda.setCellValue(aux.getColumns().get(i));
	        	celda.setCellStyle(style);  	
	    }
	    Cell celdaf = fila.createCell(columns);
	    celdaf.setCellStyle(style);
/*
        for(Integer i = 0; i<= columns; i++){
        	if(aux.getColumns().get(i).equals("Producto") || aux.getColumns().get(i).equals("Nombre"))
        		hoja.setDefaultColumnStyle(i, style3);
        	else hoja.setDefaultColumnStyle(i, style2); 
        }

        /*Cell c1 = fila.createCell(0);c1.setCellValue("Centro de Trabajo");c1.setCellStyle(style);
        Cell c2 = fila.createCell(1);c2.setCellValue("Departamento");c2.setCellStyle(style);
        Cell c3 = fila.createCell(2);c3.setCellValue("Producto");c3.setCellStyle(style);
        Cell c4 = fila.createCell(3);c4.setCellValue("Nombre");c4.setCellStyle(style);
        Cell c5 = fila.createCell(4);c5.setCellValue("Cantidad");c5.setCellStyle(style);
        Cell c6 = fila.createCell(5);c6.setCellValue("Detalle 1");c6.setCellStyle(style);
        Cell c7 = fila.createCell(6);c7.setCellValue("Detalle 2");c7.setCellStyle(style);
        Cell c8 = fila.createCell(7);c8.setCellValue("Detalle 3");c8.setCellStyle(style);
        */

        
        LinkedList<CatalogueInfo> v = DBCatalogue.getCatalogues(domain, wp, dt, login);

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+2);
        	for(Integer k = 0; k< columns; k++){
				Cell celda = row.createCell(k);
				String type = aux.getColumns().get(k);
				CatalogueInfo si = v.get(j);
        		switch (type) {
        		case "Producto": celda.setCellValue(si.getProductCode());celda.setCellStyle(style3);break;
        		//case "Series": celda.setCellValue(si.getSeries().getCode());break;
        		//case "Almac\u00e9n Destino": celda.setCellValue(si.getTargetWarehouse().getName());break;
        		case "Cantidad": celda.setCellValue("");celda.setCellStyle(style2);break;
        		case "Detalle 1":  celda.setCellValue(si.getDetail());celda.setCellStyle(style2);break;
        		case "Detalle 2":  celda.setCellValue(si.getDetail2());celda.setCellStyle(style2);break;
        		case "Detalle 3":  celda.setCellValue(si.getDetail3());celda.setCellStyle(style2);break;
        		case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style2);break;
        		case "Nombre": celda.setCellValue(si.getProductName());celda.setCellStyle(style3);break;
        		//case "Comentarios": celda.setCellValue(si.getComments());break;
        		default:
        			break;
        		}
        	}
        	/*Cell ca1 = row.createCell(0);ca1.setCellValue(v.get(j).getWorkplace());
        	Cell ca2 = row.createCell(1);ca2.setCellValue(v.get(j).getDepartment());
        	Cell ca3 = row.createCell(2);ca3.setCellValue(v.get(j).getProductCode());
        	Cell ca4 = row.createCell(3);ca4.setCellValue(v.get(j).getProductName());
        	Cell ca6 = row.createCell(5);ca6.setCellValue(v.get(j).getDetail());
        	Cell ca7 = row.createCell(6);ca7.setCellValue(v.get(j).getDetail2());
        	Cell ca8 = row.createCell(7);ca8.setCellValue(v.get(j).getDetail3());
        	*/
    	    Cell lastCell = row.createCell(columns);
    	    lastCell.setCellStyle(style2);
        	row.setHeightInPoints(20);
        		
        }
        for(Integer h = 0; h<8;h++){
        	hoja.autoSizeColumn(h);
        }
        libro.write(archivo);   
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Integer length = data.length;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + "itemCatalogue.xls" +"\"");
        p_response.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
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
}
