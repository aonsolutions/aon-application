package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBStock;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;

@WebServlet(name = "DownloadTemplatesProposal", urlPatterns = { "/aon_gwt_template/gwt_download_proposal/*" })
public class DownloadProposalServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{

        String fileId = p_request.getParameter("id");
        String domain_id = p_request.getParameter("domain_id");
        String proposal_id = p_request.getParameter("proposal");
        
        Integer proposalId = Integer.parseInt(proposal_id);
        Integer domainId = Integer.parseInt(domain_id);
        String domain = AonUtil.getDomainName();

        byte[] b = null ;
        

        if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
        	b = DBConsults.getTemplate(domain,domainId, id);
        }
        else return;
        
        File f = new File("/tmp/"+"compra"+".xml"); 
        try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(f,b);
		} catch (Exception e) {
			e.printStackTrace();
		}
        TemplateInfo aux = null;
		try {
			aux = com.esferalia.aon.gwt.template.server.Utils.readxml(f);
		} catch (Exception e) {
			e.printStackTrace();
		}

        File archivoXLS = new File("compra" + ".xls" );
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
       
        Integer columns = aux.getColumns().size();
        for(Integer i = 0; i< columns; i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(aux.getColumns().get(i));
        	celda.setCellStyle(style);
        }
        Vector<StockInfo> v = DBStock.getProposal(domain, domainId, proposalId);

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+1);
        	for(Integer k = 0; k< columns; k++){
        		Cell celda = row.createCell(k);
        		String type = aux.getColumns().get(k);
        		StockInfo si = v.get(j);
        		switch (type) {
        		case "Producto": celda.setCellValue(si.getProduct());break;
        		//case "Series": celda.setCellValue(si.getSeries().getCode());break;
        		//case "Almac\u00e9n Destino": celda.setCellValue(si.getTargetWarehouse().getName());break;
        		case "Cantidad": celda.setCellValue(si.getQuantity());break;
        		case "Detalle 1":  celda.setCellValue(si.getDetail());break;
        		case "Detalle 2":  celda.setCellValue(si.getDetail2());break;
        		case "Detalle 3":  celda.setCellValue(si.getDetail3());break;
        		case "Texto Libre": celda.setCellValue("");break;
        		case "Nombre": celda.setCellValue(si.getProductName());break;
        		case "Centro de Trabajo": celda.setCellValue(si.getWorkplaceStr());break;
        		case "Departamento": celda.setCellValue(si.getDepartmentStr());break;
           		//case "Comentarios": celda.setCellValue(si.getComments());break;
        		default:
        			break;
        		}
        	}
        }
        for(Integer h = 0; h< columns;h++){
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
        

    }
	
	}