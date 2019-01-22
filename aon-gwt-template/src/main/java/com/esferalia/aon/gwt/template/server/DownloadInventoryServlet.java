package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBInventory;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

@WebServlet(name = "DownloadTemplatesInventory", urlPatterns = { "/aon_gwt_template/gwt_download_inventory/*"
																 ,"/aon_gwt_aio/gwt_download_inventory/*"})
public class DownloadInventoryServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{

        String fileId = p_request.getParameter("id");
        String domain_id = p_request.getParameter("domain_id");
        String closed = p_request.getParameter("closed");
        String inventory_id = p_request.getParameter("inventory");
        String login = p_request.getParameter("username");
        User user = new User().setLogin(login);
        Integer inventoryId = Integer.parseInt(inventory_id);
        Integer domainId = Integer.parseInt(domain_id);
        Boolean close = closed.equals("true");
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Domain domain = new Domain().setId(domainId).setName(domainName);
        byte[] b = null ;
        
        if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
        	b = DBConsults.getTemplate(domain, user, id);
        }
        else return;
       
        TemplateInfo aux = null;
		try {
			aux = com.esferalia.aon.gwt.template.server.Utils.readxml(new ByteArrayInputStream(b));
		} catch (Exception e) {
			e.printStackTrace();
		}

        String info;
        if(close){
        	//archivoXLS = new File("inventory_closed" + ".xls" );
        	info = "Listado de Recuento ## ";
        }
        else{
        	//archivoXLS = new File("inventory_valued" + ".xls" );
        	info = "Inventario Valorado ## ";
        }
              
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Plantilla 1");
        
        Integer columns = aux.getColumns().size();
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()));
        Row rowInfo = hoja.createRow(0);
        Row fila = hoja.createRow(1);
        
        
        info = info + DBInventory.getInventoryName(domain, inventoryId, login);
        
         
        
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
 
        //DBConsumption.getConsumption(domain, domainId, 2, 2, 2, new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()));
        Vector<InventoryInfo> v =  DBInventory.getInventory(domain, inventoryId, close, login);
        
        for(Integer j = 0; j< v.size();j++){
        	InventoryInfo ii = v.get(j);
        	Row row = hoja.createRow(j+2);
        	for(Integer k = 0; k< columns; k++){
        		Cell celda = row.createCell(k);
        		String type = aux.getColumns().get(k);  
        		if(close){
            		switch (type) {
            		case "Producto": celda.setCellValue(ii.getProductCode());celda.setCellStyle(style3);break;
            		case "Detalle 1":  celda.setCellValue(ii.getDetail());celda.setCellStyle(style2);break;
            		case "Detalle 2":  celda.setCellValue(ii.getDetail2());celda.setCellStyle(style2);break;
            		case "Detalle 3":  celda.setCellValue(ii.getDetail3());celda.setCellStyle(style2);break;
            		case "Nombre": celda.setCellValue(ii.getProductName());celda.setCellStyle(style3);break;
            		case "Categor\u00eda": celda.setCellValue(ii.getProductCategory());celda.setCellStyle(style2);break; 
            		case "Recuento": celda.setCellValue("");celda.setCellStyle(style2);break;
            		case "Inventario": celda.setCellValue(ii.getInventory());celda.setCellStyle(style2);break;
            		case "Numero Serie": celda.setCellValue(ii.getSerialNumber());celda.setCellStyle(style2);break;
            		default:
            			break;
            		}
        		}
        		else{
            		switch (type) {
            		case "Producto": celda.setCellValue(ii.getProductCode());celda.setCellStyle(style3);break;
            		case "Detalle 1":  celda.setCellValue(ii.getDetail());celda.setCellStyle(style2);break;
            		case "Detalle 2":  celda.setCellValue(ii.getDetail2());celda.setCellStyle(style2);break;
            		case "Detalle 3":  celda.setCellValue(ii.getDetail3());celda.setCellStyle(style2);break;
            		case "Nombre": celda.setCellValue(ii.getProductName());celda.setCellStyle(style3);break;
            		case "Categor\u00eda": celda.setCellValue(ii.getProductCategory());celda.setCellStyle(style2);break; 
            		case "Coste": celda.setCellValue(round(ii.getCost(),2));celda.setCellStyle(style2);break; 
            		case "Inventario": celda.setCellValue(ii.getInventory());celda.setCellStyle(style2);break;
            		case "Total": celda.setCellValue(ii.getCost()*ii.getInventory());celda.setCellStyle(style2);break;
            		case "Numero Serie": celda.setCellValue(ii.getSerialNumber());celda.setCellStyle(style2);break;
            		default:
            			break;
            		}
        		}

        	}
        	Cell lastCell = row.createCell(columns);
        	lastCell.setCellStyle(style2);
        	row.setHeightInPoints(20);

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
        
        if(close)
            p_response.addHeader("Content-Disposition","attachment; filename=\"" + "inventory_closed.xls" +"\"");        
        else
            p_response.addHeader("Content-Disposition","attachment; filename=\"" + "inventory_valued.xls" +"\"");        
            
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
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	}