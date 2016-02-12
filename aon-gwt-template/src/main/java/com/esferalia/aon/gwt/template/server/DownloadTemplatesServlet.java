package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

@WebServlet(name = "DownloadTemplates", urlPatterns = { "/aon_gwt_template/gwt_download/*" })
public class DownloadTemplatesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String driveId = p_request.getParameter("drive_id");
        String fileId = p_request.getParameter("id");
        String name = p_request.getParameter("name");
        String login = p_request.getParameter("username");
        String domain_id = p_request.getParameter("domain_id");
        User user = new User().setLogin(login);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Integer domainId = Integer.parseInt(domain_id);
        Domain domain = AON.getDomain(domainName, domainId, login);
        Integer idFile = Integer.parseInt(fileId);
        byte[] b = null ;
        
        if (driveId != ""){
        	b = DriveUtils.getByteFile(domain, user, driveId, idFile);	
        }
        else if(fileId!=""){
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
  
		HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Plantilla 1");
        Integer columns = aux.getColumns().size();

        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()));
        
        Row rowInfo = hoja.createRow(0);
        Row fila = hoja.createRow(1);
        
        
        String info = aux.getType()+" ## ";
        
        rowInfo.setHeightInPoints(16);
        fila.setHeightInPoints(16);
        CellStyle style = libro.createCellStyle();CellStyle styleInfo = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);styleInfo.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);styleInfo.setAlignment(CellStyle.ALIGN_CENTER);
        style.setBorderBottom(CellStyle.BORDER_MEDIUM); styleInfo.setBorderBottom(CellStyle.BORDER_MEDIUM);
       	styleInfo.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);

       	Cell cellInfo = rowInfo.createCell(0);
       	cellInfo.setCellValue(info);
       	cellInfo.setCellStyle(styleInfo);
      
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
      	style2.setFont(font2);
      	style2.setAlignment(CellStyle.ALIGN_CENTER);
		style2.setBorderBottom(CellStyle.BORDER_THIN);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setBorderLeft(CellStyle.BORDER_THIN);
      	
        CellStyle style3 = libro.createCellStyle();
      	style3.setFont(font2);
      	style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		style3.setBorderRight(CellStyle.BORDER_THIN);
		style3.setBorderLeft(CellStyle.BORDER_THIN);

        for(Integer i = 0; i< aux.getColumns().size(); i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(aux.getColumns().get(i));
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
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + name + ".xls" +"\"");
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
