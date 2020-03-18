package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

@WebServlet(name = "DownloadTemplates", urlPatterns = { "/aon_gwt_template/gwt_download_invoice_template/*"
														,"/aon_gwt_aio/gwt_download_invoice_template/*"})
public class DownloadInvoiceTemplateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{        
        LinkedList<String> columnList = new LinkedList<String>();
        columnList.add("TIPO OPERACIÓN");
        columnList.add("TIPO FACTURA");
        columnList.add("FECHA");
        columnList.add("SERIE");
        columnList.add("NÚMERO");
        columnList.add("NIF");
        columnList.add("NOMBRE");
        columnList.add("DIRECCIÓN");
        columnList.add("CIUDAD");
        columnList.add("PROVINCIA");
        columnList.add("CÓDIGO POSTAL");
        columnList.add("PAÍS");
        columnList.add("CUENTA BASE");
        columnList.add("BASE");
        columnList.add("%Impuesto");
        columnList.add("%RE");
        columnList.add("CUOTA RE");
        columnList.add("%RETENCIÓN");
        columnList.add("CUOTA RETENCIÓN");
        columnList.add("TOTAL FACTURA");
        columnList.add("CLAVE RETENCIÓN");
        columnList.add("SUBCLAVE RETENCIÓN");

        
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

        for(Integer i = 0; i< columnList.size(); i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(columnList.get(i));
        	celda.setCellStyle(style);
        }
        Cell celdaf = fila.createCell(columns);
        celdaf.setCellStyle(style);
       /* for(Integer i = 0; i<= aux.getColumns().size(); i++){
        	if(aux.getColumns().size()!=i && ( aux.getColumns().get(i).equals("Producto") || aux.getColumns().get(i).equals("Nombre")))
        		hoja.setDefaultColumnStyle(i, style3);
        	else hoja.setDefaultColumnStyle(i, style2);
        }*/

        for(Integer h = 0; h< columns;h++){
        	hoja.autoSizeColumn(h);
        }
        libro.write(archivo);       
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Integer length = data.length;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"invoiceTemplate.xls" +"\"");
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
