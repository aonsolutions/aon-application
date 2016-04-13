package com.esferalia.aon.gwt.template.server;

import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.jooq.Condition;

import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBStock;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

@WebServlet(name = "DownloadTemplatesStock", urlPatterns = { "/aon_gwt_template/gwt_download_stock/*" })
public class DownloadStockServlet extends HttpServlet {

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
        String statuses = p_request.getParameter("statuses");
        String types = p_request.getParameter("types");
        String quantity = p_request.getParameter("quantity");
        String close_inventory = p_request.getParameter("close");
        String only_non_cero = p_request.getParameter("only_non_cero");
        String login = p_request.getParameter("username");
        String packagedInfo = p_request.getParameter("packaged_info");
        Boolean packaged = packagedInfo.equals("1");
        
        Boolean closeInventory = close_inventory.equals("true");
        Integer domainId = Integer.parseInt(domain_id);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Domain domain = AON.getDomain(domainName, domainId, login);
        Integer idFile  = Integer.parseInt(fileId);
        Integer userId = AonServletUtils.getRequestUserId(p_request);
        User user = new User().setId(userId).setLogin(login);
        Warehouse w = new Warehouse();
        if(!warehouse.equals("-"))
        	w = DBStock.getWarehouse(domain, user, warehouse);
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
        if(packaged) hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()+2));
        else hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()));
        
        Row rowInfo = hoja.createRow(0);
        Row fila = hoja.createRow(1);
                
        String info = "Stock ## " + warehouse + " ## "
        		+ AonDateUtils.getDay(new Date())
        		+ "-" + (AonDateUtils.getMonth(new Date()) +1)
        		+ "-" +  AonDateUtils.getYear(new Date());
        
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

        Integer columnsAux = columns;
        if(packaged){
        	 Cell cell1 = fila.createCell(columns);
        	 cell1.setCellValue("Stock");
        	 cell1.setCellStyle(style);
        	 Cell cell2 = fila.createCell(columns+1);
        	 cell2.setCellValue("Etiqueta");
        	 cell2.setCellStyle(style);
        	 columnsAux = columns + 2;
        }
        Cell celdaf = fila.createCell(columnsAux);
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
        if(!quantity.equals("") && !quantity.equals("undefined")){
        	
        	if(quantity.contains("=")){
        		c = c.and(STOCK.QUANTITY.eq(Double.parseDouble(quantity.substring(1))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(Double.parseDouble(quantity.substring(1))));
        	}
        	else if(quantity.contains(">")){
        		c = c.and(STOCK.QUANTITY.greaterThan(Double.parseDouble(quantity.substring(1))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.greaterThan(Double.parseDouble(quantity.substring(1))));
        	}
        	else if(quantity.contains(">=")){
        		c = c.and(STOCK.QUANTITY.greaterOrEqual(Double.parseDouble(quantity.substring(2))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(Double.parseDouble(quantity.substring(2))));
        	}
        	else if(quantity.contains("<")){
        		c = c.and(STOCK.QUANTITY.lessThan(Double.parseDouble(quantity.substring(1))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.lessThan(Double.parseDouble(quantity.substring(1))));
        	}
        	else if(quantity.contains("<=")){
        		c = c.and(STOCK.QUANTITY.lessOrEqual(Double.parseDouble(quantity.substring(2))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.lessOrEqual(Double.parseDouble(quantity.substring(2))));
        	}
        	else if(quantity.contains("!")){
        		c = c.and(STOCK.QUANTITY.ne(Double.parseDouble(quantity.substring(1))));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.ne(Double.parseDouble(quantity.substring(1))));
        	}
        	else if(quantity.contains(":")){
        		Integer pos = quantity.indexOf(":");
        		Double qA = Double.parseDouble(quantity.substring(0,pos));
        		Double qB = Double.parseDouble(quantity.substring(pos+1));
        		c = c.and(STOCK.QUANTITY.between(qA, qB));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.between(qA, qB));
        	}
        	else if(quantity.contains("|")){
        		Integer pos = quantity.indexOf("|");
        		Double qA = Double.parseDouble(quantity.substring(0,pos));
        		Double qB = Double.parseDouble(quantity.substring(pos+1));
        		c = c.and(STOCK.QUANTITY.eq(qA).or(STOCK.QUANTITY.eq(qB)));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(qA).or(INVENTORY_DETAIL.REAL_QUANTITY.eq(qB)));
        	}
        	else if(quantity.contains("&")){
        		Integer pos = quantity.indexOf("&");
        		Double qA = Double.parseDouble(quantity.substring(0,pos));
        		Double qB = Double.parseDouble(quantity.substring(pos+1));
        		c = c.and(STOCK.QUANTITY.eq(qA).and(STOCK.QUANTITY.eq(qB)));
            	c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(qA).and(INVENTORY_DETAIL.REAL_QUANTITY.eq(qB)));
        	}
        	else if(quantity.equalsIgnoreCase("null")){
            	c = c.and(STOCK.QUANTITY.isNull());
                c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.isNull());
        	}
        	else if(quantity.equalsIgnoreCase("not null")){
            	c = c.and(STOCK.QUANTITY.isNotNull());
                c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.isNotNull());
        	}
        	else{
        		c = c.and(STOCK.QUANTITY.eq(Double.parseDouble(quantity)));
        		c2 = c2.and(INVENTORY_DETAIL.REAL_QUANTITY.eq(Double.parseDouble(quantity)));
        	}
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
        	v= DBStock.getInventoryClosed(domain, inventoryId, c2, user.getLogin());
        }
        else
        	v= DBStock.getStocks(domain,w.getId(),c,"1".equals(only_non_cero), user.getLogin());

        for(Integer j = 0; j< v.size();j++){
        	Row row = hoja.createRow(j+2);
        	StockInfo si = v.get(j);
        	for(Integer k = 0; k< columns; k++){
				Cell celda = row.createCell(k);
				String type = aux.getColumns().get(k);
        		switch (type) {
        		case "Producto": celda.setCellValue(si.getProduct());celda.setCellStyle(style3);break;
        		case "Cantidad": celda.setCellValue(si.getQuantity());celda.setCellStyle(style2);break;
        		case "Detalle 1":  celda.setCellValue(si.getItem().getDetail());celda.setCellStyle(style2);break;
        		case "Detalle 2":  celda.setCellValue(si.getItem().getDetail2());celda.setCellStyle(style2);break;
        		case "Detalle 3":  celda.setCellValue(si.getItem().getDetail3());celda.setCellStyle(style2);break;
        		case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style2);break;
        		case "Nombre": celda.setCellValue(si.getProductName());celda.setCellStyle(style3);break;
        		case "N\u00FAmero Serie": celda.setCellValue(si.getItem().getSerialNumber());celda.setCellStyle(style3);break;
        		case "Formato": celda.setCellValue(si.getItem().getPackFormatTag().getName());celda.setCellStyle(style3);break; 
        		case "Unidades": celda.setCellValue(si.getItem().getPackUnits());celda.setCellStyle(style3);break; 
        		case "Formato Unidades": celda.setCellValue(si.getItem().getPackUnitsTag().getName());celda.setCellStyle(style3);break; 
        		case "Medida": celda.setCellValue(si.getItem().getPackMeasurement());celda.setCellStyle(style3);break; 
        		case "Formato Medida": celda.setCellValue(si.getItem().getPackMeasurementTag().getName());celda.setCellStyle(style3);break; 
        		default:
        			break;
        		}
        	}
        	columnsAux = columns;
        	if(packaged){
        		Cell valueCell = row.createCell(columns);
        		Double value = si.getQuantity() * si.getItem().getPackMeasurement() * si.getItem().getPackUnits();
        		valueCell.setCellValue(value);
        		valueCell.setCellStyle(style2);
        		Cell unityCell = row.createCell(columns+1);
        		unityCell.setCellValue(si.getItem().getPackMeasurementTag().getName());
        		unityCell.setCellStyle(style2);
        		columnsAux = columns+2;
        	}
        	
        	Cell lastCell = row.createCell(columnsAux);
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
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + name +".xls" +"\"");
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
