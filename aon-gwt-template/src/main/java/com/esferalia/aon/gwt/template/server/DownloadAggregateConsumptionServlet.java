package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.apache.poi.ss.util.Region;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBConsumption;
import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.Warehouse;

@WebServlet(name = "DownloadTemplatesAggregateConsumption", urlPatterns = { "/aon_gwt_template/gwt_download_aggregate_consumption/*" })
public class DownloadAggregateConsumptionServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{

        String fileId = p_request.getParameter("id");
        String domain_id = p_request.getParameter("domain_id");
        String sizeReq = p_request.getParameter("size");
        Integer size = Integer.parseInt(sizeReq);	
        Integer domainId = Integer.parseInt(domain_id);
        Boolean detail = p_request.getParameter("detail").equalsIgnoreCase("True");
        String only_negative = p_request.getParameter("only_negative");

        Vector<Warehouse> warehouses = new Vector<Warehouse>();
        
        for (int i = 0; i < size; i++) {
        	 String wName = p_request.getParameter("warehouse"+i);
        	 String wId = p_request.getParameter("warehouse_id"+i);
        	 Warehouse warehouse = new Warehouse();
        	 warehouse.setDomainId(domainId);
             Integer wIdnum = Integer.parseInt(wId);
             warehouse.setId(wIdnum);
        	 warehouse.setName(wName);
        	 warehouses.add(warehouse);
		}
        String domain = AonUtil.getDomainName();
        boolean onlyNegative = "1".equals(only_negative);
       
        Integer initialId = null, finalId = null;
        
        Date initialDate = null, finalDate = null;
      
        
        byte[] b = null ;
        

        if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
        	b = DBConsults.getTemplate(domain,domainId, id);
        }
        else return;
        
        File f = new File("/tmp/"+"consumo"+".xml"); 
        try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(f,b);
		} catch (Exception e) {
			e.printStackTrace();
		}
        TemplateInfo aux = null;
		try {
			aux = com.esferalia.aon.gwt.template.server.Utils.readxml(f);
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

		
        File archivoXLS = new File("consumo" + ".xls" );
        if(archivoXLS.exists()) archivoXLS.delete();
        archivoXLS.createNewFile();        
        HSSFWorkbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        
        for (int index = 0; index < size; size++) {
    
        	HSSFSheet hoja = libro.createSheet("Plantilla "+ index);
        
        	Integer columns = aux.getColumns().size();
        	hoja.addMergedRegion(new Region(0,(short)0,0,columns.shortValue()));
        	Row rowInfo = hoja.createRow(0);
        	Row fila = hoja.createRow(1);
        
        
        
        	String info = "Control de Consumo ## "+DBConsumption.getWarehouseName(domain, domainId, warehouses.get(index).getId())+" ## "
        			+ DBConsumption.getInventoryName(domain, domainId, initialId) +" ## "
        			+ DBConsumption.getInventoryName(domain, domainId, finalId);
        
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
 
        	Map<Integer, ConsumptionItem> map = DBConsumption.getConsumption(domain, domainId, initialId, finalId, warehouses.get(index).getId(), new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()));
        	Vector<ConsumptionItem> v =  new Vector<ConsumptionItem>(map.values());
        	Integer num = 0;
        	for(Integer j = 0; j< v.size();j++){
        		ConsumptionItem ci = v.get(j);
        		ci.setConsumption(ci.getInitialQuantity()+ci.getPurchases()+ci.getTransfersPlus()-ci.getSales()-ci.getTransfersMinus()-ci.getFinalQuantity());

        		if(!(ci.getInitialQuantity() == 0 && 
        			ci.getPurchases() == 0 &&
        			ci.getSales() == 0 &&
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
        				case "Inicial": celda.setCellValue(ci.getInitialQuantity());celda.setCellStyle(style2);break;
        				case "Valor Inicial": celda.setCellValue(ci.getInitialValue() * ci.getInitialQuantity());celda.setCellStyle(style2);break;
        				case "Compras": celda.setCellValue(ci.getPurchases());celda.setCellStyle(style2);break;
        				case "Valor Compras": celda.setCellValue(ci.getPurchases() * ci.getPurchasesValue());celda.setCellStyle(style2);break;
        				case "Ventas": celda.setCellValue(ci.getSales());celda.setCellStyle(style2);break;
        				case "Valor Ventas": celda.setCellValue(ci.getSales() * ci.getSalesValue());celda.setCellStyle(style2);break;
        				case "Final": celda.setCellValue(ci.getFinalQuantity());celda.setCellStyle(style2);break;
        				case "Valor Final": celda.setCellValue(ci.getFinalQuantity() * ci.getFinalValue());celda.setCellStyle(style2);break;
        				case "Traspaso": celda.setCellValue(ci.getTransfersPlus()-ci.getTransfersMinus());celda.setCellStyle(style2);break;
        				case "Valor Traspaso": celda.setCellValue((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()));celda.setCellStyle(style2);break;
        				case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Consumo": celda.setCellValue(ci.getConsumption());celda.setCellStyle(style2);break;
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
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	}