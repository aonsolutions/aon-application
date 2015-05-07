package com.esferalia.aon.gwt.template.server;

import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.Region;
import org.jooq.Condition;

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

@WebServlet(name = "DownloadTemplatesProduct", urlPatterns = { "/aon_gwt_template/gwt_download_product/*" })
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
        
        String description = p_request.getParameter("description");
        String code = p_request.getParameter("code");
        String category = p_request.getParameter("category");
        String tags1 = p_request.getParameter("tags");
        String vat = p_request.getParameter("vat");
        String retention = p_request.getParameter("retention");
        String purchaseAccount = p_request.getParameter("purchaseAccount");
        String salesAccount= p_request.getParameter("salesAccount");
        String serializable= p_request.getParameter("serializable");
        String inventoriable=p_request.getParameter("inventoriable");
        String manufactured = p_request.getParameter("manufactured");
        String composition = p_request.getParameter("composition");
        String statuses = p_request.getParameter("statuses");
        String types = p_request.getParameter("types");
        String brand = p_request.getParameter("brand");

        
        
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
        HSSFWorkbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        HSSFSheet hoja = libro.createSheet("Plantilla 1");
        
        Integer columns = aux.getColumns().size();

        hoja.addMergedRegion(new Region(0,(short)0,0,columns.shortValue()));
        
        Row rowInfo = hoja.createRow(0);
        Row fila = hoja.createRow(1);

        Date d = new Date();
        
        String info = "Productos ## "+com.esferalia.aon.gwt.template.server.Utils.getDay(d.getDate())
        		+"-"+com.esferalia.aon.gwt.template.server.Utils.getMonth(d.getMonth())
        		+"-"+com.esferalia.aon.gwt.template.server.Utils.getYear(d);
        
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
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		
        for(Integer i = 0; i< columns; i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(aux.getColumns().get(i));
        	celda.setCellStyle(style);  	
        }
        Cell celdaf = fila.createCell(columns);
        celdaf.setCellStyle(style);
       /* for(Integer i = 0; i<= columns; i++){
        	if(aux.getColumns().size()!=i && ( aux.getColumns().get(i).equals("Producto") || aux.getColumns().get(i).equals("Nombre")))
            	hoja.setDefaultColumnStyle(i, style3);
        	else hoja.setDefaultColumnStyle(i, style2);
        }*/
        
        Condition c = PRODUCT.DOMAIN.eq(domainId);
        if(!category.equals("null") && !category.equals("") && !category.equals("undefined"))
        	c = c.and(PRODUCT.CATEGORY.eq(Integer.parseInt(category)));
        if(!brand.equals("null") && !brand.equals("") && !brand.equals("undefined"))
        	c = c.and(PRODUCT.BRAND.eq(Integer.parseInt(brand)));
        if(!code.equals("null") && !code.equals("") && !code.equals("undefined"))
        	c = c.and(PRODUCT.CODE.like(code));
        if(!description.equals("null") && !description.equals("") && !description.equals("undefined"))
        	c.and(PRODUCT.NAME.like(description));

        if(!types.equals("null") && !types.equals("") && !types.equals("undefined")){
        	String s= types.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			s = s.substring(index+1);
        		}
        	}
        }
        if(!statuses.equals("null") && !statuses.equals("") && !statuses.equals("undefined")){
        	String s= statuses.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			s = s.substring(index+1);
        		}
        	}
        }
       /* if(!tags.equals("null") && !tags.equals("") && !tags.equals("undefined")){
        	String s= tags.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			s = s.substring(index+1);
        		}
        	}
        }*/
        if(!vat.equals("null") && !vat.equals("") && !vat.equals("undefined")){
        	c = c.and(PRODUCT.VAT.eq(Integer.parseInt(vat)));
        }
        if(!retention.equals("null") && !retention.equals("") && !retention.equals("undefined")){
        	c = c.and(PRODUCT.RETENTION.eq(Integer.parseInt(retention)));
        }
        if(!purchaseAccount.equals("null") && !purchaseAccount.equals("") && !purchaseAccount.equals("undefined")){
        	c = c.and(PRODUCT.PURCHASE_ACCOUNT.eq(Integer.parseInt(purchaseAccount)));
        }
        if(!salesAccount.equals("null") && !salesAccount.equals("") && !salesAccount.equals("undefined")){
        	c = c.and(PRODUCT.SALES_ACCOUNT.eq(Integer.parseInt(salesAccount)));
        }
        if(!serializable.equals("null") && !serializable.equals("") && !serializable.equals("undefined")){
        	if(serializable.equals("true"))
        		c = c.and(PRODUCT.SERIALIZABLE.eq((byte)1));
        	else if(serializable.equals("false"))
        		c = c.and(PRODUCT.SERIALIZABLE.eq((byte)0));
        }
        if(!inventoriable.equals("null") && !inventoriable.equals("") && !inventoriable.equals("undefined")){
        	if(inventoriable.equals("true"))
        		c = c.and(PRODUCT.INVENTORIABLE.eq((byte)0));
        	else if(inventoriable.equals("false"))
        		c = c.and(PRODUCT.INVENTORIABLE.eq((byte)1));
        }
        if(!manufactured.equals("null") && !manufactured.equals("") && !manufactured.equals("undefined")){
        	if(manufactured.equals("true"))
        		c = c.and(PRODUCT.MANUFACTURED.eq((byte)0));
        	else if(manufactured.equals("false"))
        		c = c.and(PRODUCT.MANUFACTURED.eq((byte)1));
        }
        if(!composition.equals("null") && !composition.equals("") && !composition.equals("undefined")){
        	if(composition.equals("true"))
        		c = c.and(PRODUCT.COMPOSITION.eq((byte)0));
        	else if(composition.equals("false"))
        		c = c.and(PRODUCT.COMPOSITION.eq((byte)1));
        }
        
        
        
        Vector<ProductInfo> v =  DBProduct.getProducts(domain,domainId,c);

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+2);
        	for(Integer k = 0; k< columns; k++){
        		Cell celda = row.createCell(k);
        		String type = aux.getColumns().get(k);
        		ProductInfo pi = v.get(j);
        		String tags="";
        		for(ProductTag pt :pi.getTags()){
        			tags = tags + ", "+pt.getTag().getName();
        		}
        		switch (type) {
        		case "Nombre": celda.setCellValue(pi.getDownloadItem().getName());celda.setCellStyle(style3);break;
        		case "C\u00f3digo": celda.setCellValue(pi.getDownloadItem().getCode());celda.setCellStyle(style3);break;
        		case "Precio Coste": celda.setCellValue(pi.getDownloadItem().getPurchasePrice());celda.setCellStyle(style2);break;
        		case "Precio Venta Base": celda.setCellValue(pi.getDownloadItem().getPrice());celda.setCellStyle(style2);break;
        		case "Categor\u00eda": celda.setCellValue(pi.getDownloadItem().getCategory());celda.setCellStyle(style2);break;
        		case "Marca": celda.setCellValue(pi.getDownloadItem().getBrand());celda.setCellStyle(style2);break;
        		case "Etiqueta":  celda.setCellValue(tags);celda.setCellStyle(style2);break;
        		case "Tipo": celda.setCellValue(com.code.aon.product.enumeration.ProductType.values()[pi.getDownloadItem().getType().ordinal()].getName(new Locale("es_ES")));celda.setCellStyle(style2);break;
        		case "IVA": celda.setCellValue(pi.getDownloadItem().getVat().getName());celda.setCellStyle(style2);break;
        		case "IRPF": celda.setCellValue(pi.getDownloadItem().getRetention().getName());celda.setCellStyle(style2);break;
        		case "Inventoriable": celda.setCellValue(pi.getDownloadItem().isInventoriable());celda.setCellStyle(style2);break;
        		case "Producto Compuesto": celda.setCellValue(pi.getDownloadItem().isComposition());celda.setCellStyle(style2);break;
        		case "Precio Composici\u00f3n": celda.setCellValue(pi.getDownloadItem().isCompositionPrice());celda.setCellStyle(style2);break;
        		case "Estado": celda.setCellValue(ProductStatus.values()[pi.getDownloadItem().getStatus()].getName(new Locale("es_ES")));celda.setCellStyle(style2);break;
        		case "C\u00f3digo de Barras":  celda.setCellValue(pi.getDownloadItem().getBarcode());celda.setCellStyle(style2);break;
        		case "Descripci\u00f3n":  celda.setCellValue(pi.getDownloadItem().getDescription());celda.setCellStyle(style2);break;
        		case "Detalle 1":  celda.setCellValue(pi.getDownloadItem().getDetail());celda.setCellStyle(style2);break;
        		case "Detalle 2":  celda.setCellValue(pi.getDownloadItem().getDetail2());celda.setCellStyle(style2);break;
        		case "Detalle 3":  celda.setCellValue(pi.getDownloadItem().getDetail3());celda.setCellStyle(style2);break;
        		default:
        			break;        		}
        	}
        	Cell lastCell = row.createCell(columns);
        	lastCell.setCellStyle(style2);
        	row.setHeightInPoints(20);

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
