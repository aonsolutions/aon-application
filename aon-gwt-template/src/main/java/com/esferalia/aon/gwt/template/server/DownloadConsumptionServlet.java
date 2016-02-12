package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
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

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBConsumption;
import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

@WebServlet(name = "DownloadTemplatesConsumption", urlPatterns = { "/aon_gwt_template/gwt_download_consumption/*" })
public class DownloadConsumptionServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{

        String fileId = p_request.getParameter("id");
        String domain_id = p_request.getParameter("domain_id");
        String warehouse = p_request.getParameter("warehouse");
        String initial_date = p_request.getParameter("initial_date");
        String final_date = p_request.getParameter("final_date");
        String initial_id = p_request.getParameter("initial_id");
        String final_id = p_request.getParameter("final_id");
        String only_negative = p_request.getParameter("only_negative");
        String login = p_request.getParameter("username");
        Boolean detail = p_request.getParameter("detail").equalsIgnoreCase("True");
        User user = new User().setLogin(login);
        Integer domainId = Integer.parseInt(domain_id);
        Integer warehouseId = Integer.parseInt(warehouse);
        Integer initialId = Integer.parseInt(initial_id);
        Integer finalId = Integer.parseInt(final_id);
        String domainName = AonServletUtils.getRequestDomainName(p_request);
        Domain domain = new Domain().setId(domainId).setName(domainName);
        Long finalDate2 = Long.parseLong(final_date);
        Long initialDate2 = Long.parseLong(initial_date);
        
        boolean onlyNegative = "1".equals(only_negative);
      
        Date initialDate = AonDateUtils.addDays(new Date(initialDate2), 1);
        Date finalDate = new Date(finalDate2);
        
        byte[] b = null ;
        
        if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
        	b = DBConsults.getTemplate(domain, user, id);
        }
        else return;
        
        TemplateInfo aux = null;
		try {
			aux = com.esferalia.aon.gwt.template.server.Utils.readxml(new ByteArrayInputStream(b));
			Vector<String> v = new Vector<String>();
			Integer i = 0;
			if(detail){
				for (String s : aux.getColumns()) {
					if(!s.contains("Detalle"))
						v.add(s);
					i++;
					switch (s) {
					case "Inicial": v.add("Valor Inicial"); i++; break;
        			case "Compras": v.add("Valor Compras"); i++; break;
        			case "Ventas": v.add("Valor Ventas"); i++; break;
        			case "Final": v.add("Valor Final"); i++; break;
        			case "Traspaso": v.add("Valor Traspaso"); i++; break;
        			default:
        				break;
        			}
				}
			}
			else{
				for (String s : aux.getColumns()) {
					if(!s.contains("Detalle"))
						v.add(s);
					i++;
				}
			}
			aux.setColumns(v);
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
        
        
        
        String info = "Control de Consumo ## "+DBConsumption.getWarehouseName(domain, warehouseId, login)+" ## "
        			+ DBConsumption.getInventoryName(domain, initialId, login) +" ## "
        			+ DBConsumption.getInventoryName(domain, finalId, login);
        
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
        String warehouseName="";
        Map<Integer, ConsumptionItem> map = DBConsumption.getConsumption(domain,login, initialId, finalId, warehouseId, new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()), warehouseName);
        Vector<ConsumptionItem> v =  new Vector<ConsumptionItem>(map.values());
        Integer num = 0;
        for(Integer j = 0; j< v.size();j++){
        	ConsumptionItem ci = v.get(j);
        	ci.setConsumption(ci.getInitialQuantity()+ci.getPurchasesAlb()+ci.getPurchasesFac()+ci.getTransfersPlus()-ci.getSalesAlb()-ci.getSalesFac()-ci.getTransfersMinus()-ci.getFinalQuantity());
        	
        	Double consumValue = (ci.getInitialValue() * ci.getInitialQuantity()) 
					+  	(ci.getPurchasesAlb() * ci.getPurchasesValueAlb())
					+  	(ci.getPurchasesFac() * ci.getPurchasesValueFac())
					+	((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()))
					-	(ci.getSalesAlb() * ci.getSalesValueAlb())
					-	(ci.getSalesFac() * ci.getSalesValueFac())
					-	(ci.getFinalQuantity() * ci.getFinalValue());
        	
        	if(!(ci.getInitialQuantity() == 0 && 
        			ci.getPurchasesAlb() == 0 &&
        			ci.getPurchasesFac() == 0 &&
        			ci.getSalesAlb() == 0 &&
        			ci.getSalesFac() == 0 &&
        			ci.getFinalQuantity() == 0 &&
        			(ci.getTransfersPlus()-ci.getTransfersMinus()) == 0 &&
        			ci.getConsumption() == 0) && 
        			(!onlyNegative || ci.getConsumption() < 0 )){
        		Row row = hoja.createRow((j-num)+2);
        	    for(Integer k = 0; k< columns; k++){
        			Cell celda = row.createCell(k);
        			String type = aux.getColumns().get(k); 
        			
        			switch (type) {
        			case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style3);break;
        			case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style2);break;
        			case "Nombre": celda.setCellValue(ci.getProductName());celda.setCellStyle(style3);break;
        			case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        			case "Valor Inicial": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        			case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style2);break;
        			case "Valor Compras": celda.setCellValue(round((ci.getPurchasesAlb() * ci.getPurchasesValueAlb())+(ci.getPurchasesFac() * ci.getPurchasesValueFac()),2));celda.setCellStyle(style2);break;
        			case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style2);break;
        			case "Valor Ventas": celda.setCellValue(round((ci.getSalesAlb() * ci.getSalesValueAlb())+(ci.getSalesFac() * ci.getSalesValueFac()),2));celda.setCellStyle(style2);break;
        			case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style2);break;
        			case "Valor Final": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style2);break;
        			case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style2);break;
        			case "Valor Traspaso": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style2);break;
        			/*case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(),2));
        						else celda.setCellValue(0);
        						celda.setCellStyle(style2);
        						break;*/ 
        			case "Valor Consumo": celda.setCellValue(round(consumValue,2));
        								celda.setCellStyle(style2);
        								break;
        			case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style2);break;
        			default:
        				break;
        			}
        		}
        		Cell lastCell = row.createCell(columns);
        		lastCell.setCellStyle(style2);
        		row.setHeightInPoints(20);
        	}
        	else{
        		num++;
        	}

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
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + "consumo.xls"+ "\"");
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