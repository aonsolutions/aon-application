package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBProduct;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.google.api.services.drive.Drive;

public class DownloadProductServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String driveId = p_request.getParameter("drive_id");
        String fileId = p_request.getParameter("id");
        String name = p_request.getParameter("name");
        String domain_id = p_request.getParameter("domain_id");
        Integer domainId = Integer.parseInt(domain_id);
        String domain = AonUtil.getDomainName();
        Integer idFile = Integer.parseInt(fileId);
        
        byte[] b = null ;
        
        if (driveId != ""){
        	Drive d = null;
        	
        	DomainGserviceaccount g = null;
			try {
				g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain,domainId);
				d = DriveUtils.serviceInitialize(g);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
        	
			com.google.api.services.drive.model.File f = null;
			try {
				f = DriveUtils.getFile(d, driveId, idFile);
				if(f.getDescription().equals("OLDRIVE"))
					d = DriveUtils.serviceInitializeOld(g);
			} catch (SQLException | GeneralSecurityException e) {
				e.printStackTrace();
			}

			InputStream in = DriveUtils.downloadFile(d, f);
			b = Utils.InputStreamToByte(in);
        	
        }
        else if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
        	b = DBConsults.getTemplate(domain,domainId, id);
        }
        else return;
        
        File f = new File("/tmp/"+name+".xml"); 
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

        File archivoXLS = new File(name + ".xls" );
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
        Vector<ProductInfo> v =  DBProduct.getProducts(domain,domainId);

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+1);
        	for(Integer k = 0; k< columns; k++){
        		Cell celda = row.createCell(k);
        		String type = aux.getColumns().get(k);
        		ProductInfo pi = v.get(j);
        		String tags="";
        		for(ProductTag pt :pi.getTags()){
        			tags = tags + ", "+pt.getTag().getName();
        		}
        		switch (type) {
        		case "Nombre": celda.setCellValue(pi.getDownloadItem().getName());break;
        		case "C\u00f3digo": celda.setCellValue(pi.getDownloadItem().getCode());break;
        		case "Precio Coste": celda.setCellValue(pi.getDownloadItem().getPurchasePrice());break;
        		case "Precio Venta Base": celda.setCellValue(pi.getDownloadItem().getPrice());break;
        		case "Categor\u00eda": celda.setCellValue(pi.getDownloadItem().getCategory());break;
        		case "Marca": celda.setCellValue(pi.getDownloadItem().getBrand());break;
        		case "Etiqueta":  celda.setCellValue(tags);break;
        		case "Tipo": celda.setCellValue(com.code.aon.product.enumeration.ProductType.values()[pi.getDownloadItem().getType().ordinal()].getName(new Locale("es_ES")));break;
        		case "IVA": celda.setCellValue(pi.getDownloadItem().getVat().getName());break;
        		case "IRPF": celda.setCellValue(pi.getDownloadItem().getRetention().getName());break;
        		case "Inventoriable": celda.setCellValue(pi.getDownloadItem().isInventoriable());break;
        		case "Producto Compuesto": celda.setCellValue(pi.getDownloadItem().isComposition());break;
        		case "Precio Composici\u00f3n": celda.setCellValue(pi.getDownloadItem().isCompositionPrice());break;
        		case "Estado": celda.setCellValue(ProductStatus.values()[pi.getDownloadItem().getStatus()].getName(new Locale("es_ES")));break;
        		case "C\u00f3digo de Barras":  celda.setCellValue(pi.getDownloadItem().getBarcode());break;
        		case "Descripci\u00f3n":  celda.setCellValue(pi.getDownloadItem().getDescription());break;
        		case "Detalle 1":  celda.setCellValue(pi.getDownloadItem().getDetail());break;
        		case "Detalle 2":  celda.setCellValue(pi.getDownloadItem().getDetail2());break;
        		case "Detalle 3":  celda.setCellValue(pi.getDownloadItem().getDetail3());break;
        		default:
        			break;        		}
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
        
       //TODO probar --->  libro.close();

    }
	
	}
