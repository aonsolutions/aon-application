package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.template.jooq.DBCatalogue;

@WebServlet(name = "DownloadTemplatesCatalogue", urlPatterns = { "/aon_gwt_template/gwt_download_catalogue/*" })
public class DownloadCatalogueServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String domain_id = p_request.getParameter("domain_id");
        String workplace = p_request.getParameter("workplace");
        String department = p_request.getParameter("department");
        Integer domainId = Integer.parseInt(domain_id);
        String domain = AonUtil.getDomainName();

        WorkPlace wp = null;
        if(!workplace.equals("-"))
        		wp = DBCatalogue.getWorkplace(workplace, domainId, domain);

        
        Department dt = null;
        if(!department.equals("-"))
        	dt = DBCatalogue.getDepartment(wp, department, domainId, domain);

        File archivoXLS = new File("itemCatalogue" + ".xls" );
        if(archivoXLS.exists()) archivoXLS.delete();
        archivoXLS.createNewFile();        
        Workbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        Sheet hoja = libro.createSheet("Plantilla 1");
        Row fila = hoja.createRow(0);
        
        
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setBorderBottom(CellStyle.BORDER_MEDIUM);
       

        Cell c1 = fila.createCell(0);c1.setCellValue("Lugar de Trabajo");c1.setCellStyle(style);
        Cell c2 = fila.createCell(1);c2.setCellValue("Departamento");c2.setCellStyle(style);
        Cell c3 = fila.createCell(2);c3.setCellValue("Producto");c3.setCellStyle(style);
        Cell c4 = fila.createCell(3);c4.setCellValue("Nombre");c4.setCellStyle(style);
        Cell c5 = fila.createCell(4);c5.setCellValue("Cantidad");c5.setCellStyle(style);

        Vector<CatalogueInfo> v = DBCatalogue.getCatalogues(domain, domainId, wp, dt);

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+1);

        	Cell ca1 = row.createCell(0);ca1.setCellValue(v.get(j).getWorkplace());
        	Cell ca2 = row.createCell(1);ca2.setCellValue(v.get(j).getDepartment());
        	Cell ca3 = row.createCell(2);ca3.setCellValue(v.get(j).getProductCode());
        	Cell ca4 = row.createCell(3);ca4.setCellValue(v.get(j).getProductName());
        }
        for(Integer h = 0; h< 4;h++){
        	hoja.autoSizeColumn(h);
        }
        libro.write(archivo);        
        archivo.close();


        long length = archivoXLS.length();
        FileInputStream fis = new FileInputStream(archivoXLS);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + archivoXLS.getName() +"\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
        
       //TODO probar --->  libro.close();

    }
	
	}
