package com.esferalia.aon.gwt.template.server;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.jooq.Condition;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.product.enumeration.ProductStatus;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBProduct;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.watson.server.AonDateUtils;

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

        String barcode = p_request.getParameter("barcode");
        String serialNumber = p_request.getParameter("serialNumber");
        String itemSerialDate1 = p_request.getParameter("itemSerialDate1");
        String itemSerialDate2 = p_request.getParameter("itemSerialDate2");
        String detail = p_request.getParameter("detail");
        String detail2 = p_request.getParameter("detail2");
        String detail3 = p_request.getParameter("detail3");
        String itemDescription = p_request.getParameter("itemDescription");
        String purchasePrice = p_request.getParameter("purchasePrice");
        String profitPercent = p_request.getParameter("profitPercent");
        String price = p_request.getParameter("price");
        String itemStatuses = p_request.getParameter("itemStatuses");
        
    	String creationUser = p_request.getParameter("creationUser");
    	String creationDate1 = p_request.getParameter("creationDate1");
    	String creationDate2 = p_request.getParameter("creationDate2");
    	String modificationUser = p_request.getParameter("modificationUser");
    	String modificationDate1 = p_request.getParameter("modificationDate1");
    	String modificationDate2 = p_request.getParameter("modificationDate2");

        String login = p_request.getParameter("username");
        
        Integer domainId = Integer.parseInt(domain_id);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Domain domain = AON.getDomain(domainName, domainId, login);
        Integer idFile = Integer.parseInt(fileId);
        
        User user = AON.getUser(domainName, domainId, login);
        
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
        
        String info = "Productos ## "
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
        Cell celdaf = fila.createCell(columns);
        celdaf.setCellStyle(style);
       /* for(Integer i = 0; i<= columns; i++){
        	if(aux.getColumns().size()!=i && ( aux.getColumns().get(i).equals("Producto") || aux.getColumns().get(i).equals("Nombre")))
            	hoja.setDefaultColumnStyle(i, style3);
        	else hoja.setDefaultColumnStyle(i, style2);
        }*/
        
        Condition c = PRODUCT.DOMAIN.eq(domainId);
        
        //-------------------- PRODUCT FILTER
        
        if(!category.equals("null") && !category.equals("") && !category.equals("undefined"))
        	c = c.and(PRODUCT.CATEGORY.eq(Integer.parseInt(category)));
        if(!brand.equals("null") && !brand.equals("") && !brand.equals("undefined"))
        	c = c.and(PRODUCT.BRAND.eq(Integer.parseInt(brand)));
        if(!code.equals("null") && !code.equals("") && !code.equals("undefined"))
        	c = c.and(PRODUCT.CODE.like("%"+code+"%"));
        if(!description.equals("null") && !description.equals("") && !description.equals("undefined"))
        	c = c.and(PRODUCT.NAME.like("%"+description+"%"));
        
        Condition roleCondition = PRODUCT.KIND.eq((byte) 0);
        for (AonRole role : user.getUserRoles()) {
			if(role.equals(AonRole.PURCHASE))
				roleCondition = roleCondition.or(PRODUCT.KIND.eq((byte) 1));
			if(role.equals(AonRole.SALE))
				roleCondition = roleCondition.or(PRODUCT.KIND.eq((byte) 2));
		}
        c = c.and(roleCondition);
        
        if(!types.equals("null") && !types.equals("") && !types.equals("undefined")){
        	String s= types.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(PRODUCT.TYPE.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(PRODUCT.TYPE.eq((byte)Integer.parseInt(s.substring(0, index))));
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
        
        //-------------------- ITEM FILTER
        
        if(!barcode.equals("null") && !barcode.equals("") && !barcode.equals("undefined")){
        	c = c.and(ITEM.BARCODE.like("%"+barcode+"%"));
        }
        if(!serialNumber.equals("null") && !serialNumber.equals("") && !serialNumber.equals("undefined")){
        	c = c.and(ITEM.SERIAL_NUMBER.like("%"+serialNumber+"%"));
        }
        if(!itemSerialDate1.equals("null") && !itemSerialDate1.equals("") && !itemSerialDate1.equals("undefined")){	
        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(itemSerialDate1);
	        	java.sql.Date date = new java.sql.Date(d1.getTime());
	        	c = c.and(ITEM.SERIAL_DATE.greaterOrEqual(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        if(!itemSerialDate2.equals("null") && !itemSerialDate2.equals("") && !itemSerialDate2.equals("undefined")){

        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(itemSerialDate2);
	        	java.sql.Date date = new java.sql.Date(d1.getTime());
	        	c = c.and(ITEM.SERIAL_DATE.lessOrEqual(date));
        	} catch (ParseException e) {
        		e.printStackTrace();
        	}
        }
        if(!detail.equals("null") && !detail.equals("") && !detail.equals("undefined")){
        	c = c.and(ITEM.DETAIL.like("%"+detail+"%"));
        }
        if(!detail2.equals("null") && !detail2.equals("") && !detail2.equals("undefined")){
        	c = c.and(ITEM.DETAIL2.like("%"+detail2+"%"));
        }
        if(!detail3.equals("null") && !detail3.equals("") && !detail3.equals("undefined")){
        	c = c.and(ITEM.DETAIL3.like("%"+detail3+"%"));
        }
        if(!itemDescription.equals("null") && !itemDescription.equals("") && !itemDescription.equals("undefined")){
        	c = c.and(ITEM.DESCRIPTION.like("%"+itemDescription+"%"));
        }
        if(!purchasePrice.equals("null") && !purchasePrice.equals("") && !purchasePrice.equals("undefined")){
        	c = c.and(ITEM.PURCHASE_PRICE.eq(Double.parseDouble(purchasePrice)));
        }
        if(!profitPercent.equals("null") && !profitPercent.equals("") && !profitPercent.equals("undefined")){
        	c = c.and(ITEM.PROFIT_PERCENT.eq(Double.parseDouble(profitPercent)));
        }
        if(!price.equals("null") && !price.equals("") && !price.equals("undefined")){
        	c = c.and(ITEM.PRICE.eq(Double.parseDouble(price)));
        }
        if(!itemStatuses.equals("null") && !itemStatuses.equals("") && !itemStatuses.equals("undefined")){
        	String s= itemStatuses.substring(1) ;
        	while(s !=""){
        		Integer index = s.indexOf("$");
        		if(index == -1){
        			c = c.and(ITEM.STATUS.eq((byte)Integer.parseInt(s)));
        			s="";
        		}
        		else{ 
        			c = c.and(ITEM.STATUS.eq((byte)Integer.parseInt(s.substring(0, index))));
        			s = s.substring(index+1);
        		}
        	}
        }
       /*if(!supplierCode.equals("null") && !supplierCode.equals("") && !supplierCode.equals("undefined")){
        	c = c.and(ITEM.SERIAL_NUMBER.like("%"+serialNumber+"%"));
        }*/
        
        //-------------------- AUDITORY FILTER
        
        if(!creationUser.equals("null") && !creationUser.equals("") && !creationUser.equals("undefined"))
        	c = c.and(PRODUCT.CREATION_USER.like("%"+creationUser+"%"));
        
        if(!creationDate1.equals("null") && !creationDate1.equals("") && !creationDate1.equals("undefined")){	
        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(creationDate1);
				Timestamp t = new Timestamp(d1.getTime());
	        	c = c.and(PRODUCT.CREATION_DATE.greaterOrEqual(t));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        if(!creationDate2.equals("null") && !creationDate2.equals("") && !creationDate2.equals("undefined")){	
        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(creationDate2);
				Timestamp t = new Timestamp(d1.getTime());
	        	c = c.and(PRODUCT.CREATION_DATE.lessOrEqual(t));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        if(!modificationUser.equals("null") && !modificationUser.equals("") && !modificationUser.equals("undefined"))
        	c = c.and(PRODUCT.MODIFICATION_USER.like("%"+modificationUser+"%"));
        
        if(!modificationDate1.equals("null") && !modificationDate1.equals("") && !modificationDate1.equals("undefined")){	
        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(modificationDate1);
				Timestamp t = new Timestamp(d1.getTime());
	        	c = c.and(PRODUCT.MODIFICATION_DATE.greaterOrEqual(t));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        if(!modificationDate2.equals("null") && !modificationDate2.equals("") && !modificationDate2.equals("undefined")){	
        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        	try {
				Date d1 = formatter.parse(modificationDate2);
	        	Timestamp t = new Timestamp(d1.getTime());
	        	c = c.and(PRODUCT.MODIFICATION_DATE.lessOrEqual(t));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        Vector<ProductInfo> v =  DBProduct.getProducts(domainName,domainId,c, login);

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
        		case "Precio Coste": celda.setCellValue(round(pi.getDownloadItem().getPurchasePrice(),2));celda.setCellStyle(style2);break;
        		case "Precio Venta Base": celda.setCellValue(round(pi.getDownloadItem().getPrice(),2));celda.setCellStyle(style2);break;
        		case "Categor\u00eda": celda.setCellValue(pi.getDownloadItem().getCategory());celda.setCellStyle(style2);break;
        		case "Marca": celda.setCellValue(pi.getDownloadItem().getBrand());celda.setCellStyle(style2);break;
        		case "Etiqueta":  celda.setCellValue(tags);celda.setCellStyle(style2);break;
        		case "Tipo": celda.setCellValue(com.code.aon.product.enumeration.ProductType.values()[pi.getDownloadItem().getType().ordinal()].getName(new Locale("es_ES")));celda.setCellStyle(style2);break;
        		case "IVA": celda.setCellValue(pi.getDownloadItem().getVat().getName());celda.setCellStyle(style2);break;
        		case "IRPF": celda.setCellValue(pi.getDownloadItem().getRetention().getName());celda.setCellStyle(style2);break;
        		case "Inventoriable":
        		case "Inventariable":celda.setCellValue(pi.getDownloadItem().isInventoriable());celda.setCellStyle(style2);break;
        		case "Producto Compuesto": celda.setCellValue(pi.getDownloadItem().isComposition());celda.setCellStyle(style2);break;
        		case "Precio Composici\u00f3n": celda.setCellValue(pi.getDownloadItem().isCompositionPrice());celda.setCellStyle(style2);break;
        		case "Estado": celda.setCellValue(ProductStatus.values()[pi.getDownloadItem().getStatus()].getName(new Locale("es_ES")));celda.setCellStyle(style2);break;
        		case "C\u00f3digo de Barras":  celda.setCellValue(pi.getDownloadItem().getBarcode());celda.setCellStyle(style2);break;
        		case "Descripci\u00f3n":  celda.setCellValue(pi.getDownloadItem().getDescription());celda.setCellStyle(style2);break;
        		case "Detalle 1":  celda.setCellValue(pi.getDownloadItem().getDetail());celda.setCellStyle(style2);break;
        		case "Detalle 2":  celda.setCellValue(pi.getDownloadItem().getDetail2());celda.setCellStyle(style2);break;
        		case "Detalle 3":  celda.setCellValue(pi.getDownloadItem().getDetail3());celda.setCellStyle(style2);break;
        		case "Numero Serie": celda.setCellValue(pi.getDownloadItem().getSerialNumber());celda.setCellStyle(style2);break; 
        		case "Loteable": celda.setCellValue(pi.getProduct().getLotable());celda.setCellStyle(style2);break;
        		case "Serializable": celda.setCellValue(pi.getProduct().getSerializable());celda.setCellStyle(style2);break;
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
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	}
