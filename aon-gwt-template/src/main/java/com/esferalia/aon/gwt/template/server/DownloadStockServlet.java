package com.esferalia.aon.gwt.template.server;

import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;

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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.DateUtil;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBStock;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.api.services.drive.Drive;

@WebServlet(name = "DownloadTemplatesStock", urlPatterns = { "/aon_gwt_template/gwt_download_stock/*" })
public class DownloadStockServlet extends HttpServlet {



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
        String warehouse = p_request.getParameter("warehouse");
        
        String category = p_request.getParameter("category");
        String brand = p_request.getParameter("brand");
        String code = p_request.getParameter("code");
        String description = p_request.getParameter("description");
        String stock = p_request.getParameter("stock");
        
        String barcode = p_request.getParameter("barcode");
        String provider = p_request.getParameter("provider");
        String tags = p_request.getParameter("tags");
        String statuses = p_request.getParameter("statuses");
        String types = p_request.getParameter("types");
        String quantity = p_request.getParameter("quantity");
        String close_inventory = p_request.getParameter("close");
        String only_non_cero = p_request.getParameter("only_non_cero");
        
        Boolean closeInventory = close_inventory.equals("true");
        Integer domainId = Integer.parseInt(domain_id);
        String domain = AonUtil.getDomainName();
        Integer idFile  = Integer.parseInt(fileId);
        Integer userId = AonUtil.getAuthPrincipal().getUserId();
        Warehouse w = new Warehouse();
        if(!warehouse.equals("-"))
        	w = DBStock.getWarehouse(warehouse, domainId, domain, userId);

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
        
        String info = "Stock ## " + warehouse + " ## "
        		+ DateUtil.getDay(d)
        		+ "-" + (DateUtil.getMonth(d)+1)
        		+ "-" + DateUtil.getYear(d);
        
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
		style2.setAlignment(CellStyle.ALIGN_RIGHT);
		style2.setBorderBottom(CellStyle.BORDER_THIN);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		style3.setBorderRight(CellStyle.BORDER_THIN);
		style3.setBorderLeft(CellStyle.BORDER_THIN);
		
        for(Integer i = 0; i< columns; i++){
        	Cell celda = fila.createCell(i);
        	celda.setCellValue(aux.getColumns().get(i));
        	celda.setCellStyle(style);  	
        }
        Cell celdaf = fila.createCell(columns);
        celdaf.setCellStyle(style);
        /*for(Integer i = 0; i<= columns; i++){
        	if(aux.getColumns().size()!=i && ( aux.getColumns().get(i).equals("Producto") || aux.getColumns().get(i).equals("Nombre")))
            	hoja.setDefaultColumnStyle(i, style3);
        	else hoja.setDefaultColumnStyle(i, style2);
        }*/
        
        Condition c = PRODUCT.DOMAIN.eq(domainId);
        Condition c2 = c;
        if(!category.equals("null") && !category.equals("") && !category.equals("undefined"))
        	{
        	c = c.and(PRODUCT.CATEGORY.eq(Integer.parseInt(category)));
        	c2 = c;
        	}
        if(!brand.equals("null") && !brand.equals("") && !brand.equals("undefined"))
        	{
        	c = c.and(PRODUCT.BRAND.eq(Integer.parseInt(brand)));
        	c2 = c;
        	}
        if(!code.equals("null") && !code.equals("") && !code.equals("undefined"))
        	{
        	c = c.and(PRODUCT.CODE.like("%"+code+"%"));
        	c2 = c;
        	}
        if(!description.equals("null") && !description.equals("") && !description.equals("undefined"))
        	{
        	c = c.and(PRODUCT.NAME.like("%"+description+"%"));
        	c2 = c;
        	}
        if(!stock.equals("false") && !stock.equals("null") && !stock.equals("") && !stock.equals("undefined"))
        	{
        	c = c.and(STOCK.QUANTITY.greaterThan(0.0));
        	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.greaterThan(0.0));
        	}
        if(!quantity.equals("null") && !quantity.equals("") && !quantity.equals("undefined")){
        	c = c.and(STOCK.QUANTITY.eq(Double.parseDouble(quantity)));
        	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(Double.parseDouble(quantity)));
        }
        if(!types.equals("null") && !types.equals("") && !types.equals("undefined")){
        	String s= types.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			c2 = c2.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			c2 = c2.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
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
        			c2 = c2.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			c2 = c2.and(PRODUCT.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
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
        if(!barcode.equals("null") && !barcode.equals("") && !barcode.equals("undefined"))
        	{
        		c = c.and(ITEM.BARCODE.like("%"+barcode+"%"));
        		c2 = c2.and(ITEM.BARCODE.like("%"+barcode+"%"));
        	}
        //if(!provider.equals("null") && !provider.equals("") && !provider.equals("undefined"))
        
        Vector<StockInfo> v;
        if(closeInventory){
        	String inventory_id = p_request.getParameter("inventory");
        	Integer inventoryId = Integer.parseInt(inventory_id);
        	v= DBStock.getInventoryClosed(domain, domainId, inventoryId, c2);
        }
        else
        	v= DBStock.getStocks(domain,domainId,w.getId(),c,"1".equals(only_non_cero));

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+2);
        	for(Integer k = 0; k< columns; k++){
				Cell celda = row.createCell(k);
				String type = aux.getColumns().get(k);
				StockInfo si = v.get(j);
        		switch (type) {
        		case "Producto": celda.setCellValue(si.getProduct());celda.setCellStyle(style3);break;
        		//case "Series": celda.setCellValue(si.getSeries().getCode());break;
        		//case "Almac\u00e9n Destino": celda.setCellValue(si.getTargetWarehouse().getName());break;
        		case "Cantidad": celda.setCellValue(si.getQuantity());celda.setCellStyle(style2);break;
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
