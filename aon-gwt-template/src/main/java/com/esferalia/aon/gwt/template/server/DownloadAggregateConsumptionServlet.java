package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.esferalia.aon.gwt.template.jooq.DBFee;
import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@WebServlet(name = "DownloadTemplatesAggregateConsumption", urlPatterns = { "/aon_gwt_template/gwt_download_aggregate_consumption/*" })
public class DownloadAggregateConsumptionServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PDF = "pdf";
	private static final String EXCEL = "excel";
	
	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{

        String fileId = p_request.getParameter("id");
        String domain_id = p_request.getParameter("domain_id");
        String sizeReq = p_request.getParameter("size");
        Integer size = Integer.parseInt(sizeReq);	
        Integer domainId = Integer.parseInt(domain_id);
        Boolean detail = p_request.getParameter("detail").equalsIgnoreCase("True");
        String only_negative = p_request.getParameter("only_negative");
        String fileType = p_request.getParameter("file_type");

        Vector<Warehouse> warehouses = new Vector<Warehouse>();
        String domain = AonUtil.getDomainName();
        for (int i = 0; i < size; i++) {
        	 String wId = p_request.getParameter("warehouse_id"+i);
        	 Warehouse warehouse = new Warehouse();
        	 warehouse.setDomainId(domainId);
             Integer wIdnum = Integer.parseInt(wId);
             warehouse.setId(wIdnum);
             String name = DBConsumption.getWarehouseName(domain, domainId, warehouse.getId());
             warehouse.setName(name);
      		 warehouses.add(warehouse);
		}
        
        boolean onlyNegative = "1".equals(only_negative);
        
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
        File archivoPDF = new File("consumo" + ".pdf" );
        if(archivoXLS.exists()) archivoXLS.delete();
        if(archivoPDF.exists()) archivoPDF.delete();
        archivoXLS.createNewFile();     
        archivoPDF.createNewFile();
        
        Vector<ConsumptionItem> cis = new Vector<ConsumptionItem>();
        
        HSSFWorkbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        Integer columns = aux.getColumns().size();
    	
        Map<String, Vector<ConsumptionItem>> allMap = new HashMap<String, Vector<ConsumptionItem>>();
        Integer z= 0;
        for(Warehouse w : warehouses){
        	ConsumptionItem consumptionItem = DBConsumption.getTwoLastInventory(domain, domainId, warehouses.get(z).getId());
        	Integer initialId = consumptionItem.getInitialId(), finalId = consumptionItem.getFinalId();
            Date initialDate = consumptionItem.getInitialDate(), finalDate = consumptionItem.getFinalDate();
            String warehouseName = DBConsumption.getWarehouseName(domain, domainId, warehouses.get(z).getId());
            String initialInventoryName = DBConsumption.getInventoryName(domain, domainId, initialId);
            String finalInventoryName = DBConsumption.getInventoryName(domain, domainId, finalId);
            consumptionItem.setWarehouseId(warehouses.get(z).getId());
            consumptionItem.setWarehouseName(warehouseName);
            consumptionItem.setInitialInventoryName(initialInventoryName);
            consumptionItem.setFinalInventoryName(finalInventoryName);
        	cis.add(consumptionItem);

        	Map<Integer, ConsumptionItem> map = DBConsumption.getConsumption(domain, domainId, initialId, finalId, warehouses.get(z).getId(), new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()), warehouseName);

        	Vector<ConsumptionItem> v =  new Vector<ConsumptionItem>(map.values());
        	
        	allMap.put(w.getName(), v);
        	z++;
        }
        libro(domain, domainId, warehouses, libro, cis, onlyNegative, allMap, getTemplateInfoA(),0);
        libro(domain, domainId, warehouses, libro, cis, onlyNegative, allMap, getTemplateInfoB(),1);
        
        for (int index = 0; index < size; index++) {
        	
        	ConsumptionItem consumptionItem = DBConsumption.getTwoLastInventory(domain, domainId, warehouses.get(index).getId());
        	Integer initialId = consumptionItem.getInitialId(), finalId = consumptionItem.getFinalId();
            Date initialDate = consumptionItem.getInitialDate(), finalDate = consumptionItem.getFinalDate();
            String warehouseName = DBConsumption.getWarehouseName(domain, domainId, warehouses.get(index).getId());
            String initialInventoryName = DBConsumption.getInventoryName(domain, domainId, initialId);
            String finalInventoryName = DBConsumption.getInventoryName(domain, domainId, finalId);
            consumptionItem.setWarehouseId(warehouses.get(index).getId());
            consumptionItem.setWarehouseName(warehouseName);
            consumptionItem.setInitialInventoryName(initialInventoryName);
            consumptionItem.setFinalInventoryName(finalInventoryName);
        	cis.add(consumptionItem);
        	HSSFSheet hoja = libro.createSheet("Plantilla "+ (index+2));
        
        	//Integer columns = aux.getColumns().size();
        	hoja.addMergedRegion(new Region(0,(short)0,0,columns.shortValue()));
        	Row rowInfo = hoja.createRow(0);
        	Row fila = hoja.createRow(1);
        
        
        
        	String info = "Control de Consumo ## " + warehouseName+" ## "
        			+ initialInventoryName +" ## " + finalInventoryName;
        
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
 
        	Map<Integer, ConsumptionItem> map = DBConsumption.getConsumption(domain, domainId, initialId, finalId, warehouses.get(index).getId(), new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()), warehouseName);
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
        				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
        							else celda.setCellValue(0);	
        							celda.setCellStyle(style2);break; 
        				case "Valor Consumo": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style2);break;
        				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style2);break;

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
        }
        
        libro.write(archivo);    
        
        if(fileType.equals(PDF)){
        	
        	 Document iText_xls_2_pdf = new Document(PageSize.A4.rotate());
    		 try {
    			 PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(archivoPDF));
    		 } catch (DocumentException e) {
    			 e.printStackTrace();
    		 }
    		 iText_xls_2_pdf.open();
    		 
			 com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font();
			 fontTitle.setSize(16);
			 fontTitle.setStyle(com.itextpdf.text.Font.BOLD);
			 
			 Paragraph title = new Paragraph("Informe Agregado de Control de Consumo",fontTitle);
			 try {
				iText_xls_2_pdf.add(title);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
			 
        	 for (int index = 0; index < size+2; index++){ 
        		 HSSFSheet my_worksheet = libro.getSheetAt(index);
        		 Iterator<Row> rowIterator = my_worksheet.iterator();             
        		 PdfPTable my_table;
        		 Integer columnNum;
        		 if(index == 0){
        			 my_table = new PdfPTable(8);
        			 columnNum = 8;
        		 }
        		 else if(index == 1){
        			 my_table = new PdfPTable(16);
        			 columnNum = 16;
        		 }
        		 else{
        			 my_table = new PdfPTable(columns);
        			 columnNum = columns;
        		 }
        		 PdfPCell table_cell;
        		 Integer i = 0;
        		 
        		 while(rowIterator.hasNext()) {
                    Row row = rowIterator.next(); 
                    Iterator<Cell> cellIterator = row.cellIterator();
                    		
                            while(cellIterator.hasNext()) {
                            		if(i == 0){
                            			Cell cell = cellIterator.next();
                            		}
                            		else{
                            		com.itextpdf.text.Font font1 = new com.itextpdf.text.Font();
                            		font1.setSize(8);
                            		font1.setStyle(com.itextpdf.text.Font.BOLD);
                            		
                            		com.itextpdf.text.Font font2 = new com.itextpdf.text.Font();
                            		font2.setSize(8);
                            		
                                    Cell cell = cellIterator.next(); //Fetch CELL
                                    switch(cell.getCellType()) { //Identify CELL type
                                            //you need to add more code here based on
                                            //your requirement / transformations
                                    case Cell.CELL_TYPE_STRING:
                                    	if(row.getRowNum() == 1)
                                    		table_cell=new PdfPCell(new Phrase(cell.getStringCellValue(), font1));
                                    	else table_cell=new PdfPCell(new Phrase(cell.getStringCellValue(), font2));
                                    	if(cell.getColumnIndex() != columnNum) my_table.addCell(table_cell);
                                    	break;
                                    case Cell.CELL_TYPE_BLANK:
                                    	//Push the data from Excel to PDF Cell
                                        table_cell=new PdfPCell();
                                        //feel free to move the code below to suit to your needs
                                        
                                        if(cell.getColumnIndex() != columnNum) my_table.addCell(table_cell);
                                       break;
                                    
                            		case Cell.CELL_TYPE_BOOLEAN:
                            			//Push the data from Excel to PDF Cell
                            			String text ="";
                            			if(cell.getBooleanCellValue())text = "true";
                            			else text = "false";
                                		table_cell=new PdfPCell(new Phrase(text, font2));
                                		//feel free to move the code below to suit to your needs
                                		 if(cell.getColumnIndex() != columnNum) my_table.addCell(table_cell);
                                		break;
                            		case Cell.CELL_TYPE_FORMULA:
                            			//Push the data from Excel to PDF Cell
                            			table_cell=new PdfPCell(new Phrase(cell.getCellFormula(), font2));
                                		//feel free to move the code below to suit to your needs
                            			 if(cell.getColumnIndex() != columnNum) my_table.addCell(table_cell);
                                		break;
                            		case Cell.CELL_TYPE_NUMERIC:
                            			//Push the data from Excel to PDF Cell
                            			Double d = cell.getNumericCellValue();
                            			table_cell=new PdfPCell(new Phrase(d.toString(), font2));
                                		//feel free to move the code below to suit to your needs
                            			 if(cell.getColumnIndex() != columnNum) my_table.addCell(table_cell);
                                		break;
                            		}	
                            		}
                            		i++;
                                    //next line
                            }
            
        		 }
        		 try {
        			 

        			 com.itextpdf.text.Font fontPhrase = new com.itextpdf.text.Font();
        			 fontPhrase.setSize(12);
        			 fontPhrase.setStyle(com.itextpdf.text.Font.BOLD);
            	
        			 com.itextpdf.text.Font fontPhrase2 = new com.itextpdf.text.Font();
        			 fontPhrase.setSize(12);
        			 
        			 if(index<2){
        				 iText_xls_2_pdf.add(new Paragraph(" "));
        				 iText_xls_2_pdf.add(new Paragraph("Resumen " + index, fontPhrase));
        				 iText_xls_2_pdf.add(new Paragraph(" "));
        			 }
        			 else{
        				 iText_xls_2_pdf.add(new Paragraph(" "));
        			 	iText_xls_2_pdf.add(new Paragraph("Almacén: " + cis.get(index).getWarehouseName(), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Inicial: " + new Phrase(cis.get(index).getInitialInventoryName(), fontPhrase2), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Final: " + new Phrase(cis.get(index).getFinalInventoryName(), fontPhrase2), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph(" "));
        			 }
        			 iText_xls_2_pdf.add(my_table);
        			 
        			 iText_xls_2_pdf.add(new Paragraph(" "));
        		 } catch (DocumentException e) {
        			 e.printStackTrace();
        		 }                       
        	 }
        	 iText_xls_2_pdf.close();
        }
        archivo.close();
        
        long length;
        FileInputStream fis;
        if(fileType.equals(PDF)){
        	length = archivoPDF.length();
        	fis = new FileInputStream(archivoPDF);
        	p_response.addHeader("Content-Disposition","attachment; filename=\"" + archivoPDF.getName() +"\"");
        	p_response.setContentType("application/pdf");
        }
        else{// if(fileType.equals(EXCEL)){
        	length = archivoXLS.length();
        	fis = new FileInputStream(archivoXLS);
 
        	p_response.addHeader("Content-Disposition","attachment; filename=\"" + archivoXLS.getName() +"\"");
        	p_response.setContentType("application/msexcel");
        }
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
	
	private TemplateInfo getTemplateInfoA() {
		Vector<String> v1 = new Vector<String>();
		v1.add("Hotel");
		v1.add("Almacén");
		v1.add("Valor Inicial"); 
		v1.add("Valor Compras"); 
		v1.add("Valor Ventas");
		v1.add("Valor Final");
		v1.add("Valor Traspaso"); 
		v1.add("Valor Consumo");
		TemplateInfo special1 = new TemplateInfo();
		special1.setColumns(v1);
		return special1;
	}

	private TemplateInfo getTemplateInfoB() {
		Vector<String> v2 = new Vector<String>();
		v2.add("Hotel");
		v2.add("Almacén");
		v2.add("Producto");
		v2.add("Nombre");
		v2.add("Inicial");
		v2.add("Valor Inicial");
		v2.add("Compras");
		v2.add("Valor Compras");
		v2.add("Ventas");
		v2.add("Valor Ventas");
		v2.add("Traspaso");
		v2.add("Valor Traspaso");
		v2.add("Final");
		v2.add("Valor Final");
		v2.add("Consumo");
		v2.add("Valor Consumo");
		TemplateInfo special2 = new TemplateInfo();
		special2.setColumns(v2);
		return special2;
	}
	
	private void libro(String domain, Integer domainId, Vector<Warehouse>  warehouses, HSSFWorkbook libro, Vector<ConsumptionItem> cis,  boolean onlyNegative, Map<String, Vector<ConsumptionItem>> map, TemplateInfo special1, Integer hoja){
		
		Integer columns = special1.getColumns().size(); 
	
    	HSSFSheet hoja0 = libro.createSheet("Plantilla "+ hoja);
        
    	hoja0.addMergedRegion(new Region(0,(short)0,0,columns.shortValue()));
    	Row rowInfo0 = hoja0.createRow(0);
    	Row fila0 = hoja0.createRow(1);
    	

    	String info0 = "Control de Consumo ## ";
        
    	rowInfo0.setHeightInPoints(16);
    	fila0.setHeightInPoints(16);
    	CellStyle style0 = libro.createCellStyle();CellStyle styleInfo0 = libro.createCellStyle();
    	Font font0 = libro.createFont();
    	font0.setFontHeightInPoints((short)12);
    	font0.setBoldweight(Font.BOLDWEIGHT_BOLD);
    	style0.setFont(font0);styleInfo0.setFont(font0);
    	style0.setAlignment(CellStyle.ALIGN_CENTER);styleInfo0.setAlignment(CellStyle.ALIGN_CENTER);
    	style0.setBorderBottom(CellStyle.BORDER_MEDIUM); styleInfo0.setBorderBottom(CellStyle.BORDER_MEDIUM);
    	styleInfo0.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);

    	Cell cellInfo0 = rowInfo0.createCell(0);
    	cellInfo0.setCellValue(info0);
    	cellInfo0.setCellStyle(styleInfo0);
    	
    	CellStyle style20 = libro.createCellStyle();
   		Font font20 = libro.createFont();
    	font0.setFontHeightInPoints((short)12);
		style20.setFont(font20);
		style20.setAlignment(CellStyle.ALIGN_RIGHT);
		style20.setBorderBottom(CellStyle.BORDER_THIN);
		style20.setBorderRight(CellStyle.BORDER_THIN);
		style20.setBorderLeft(CellStyle.BORDER_THIN);
	
		CellStyle style30 = libro.createCellStyle();
		style30.setFont(font20);
		style30.setAlignment(CellStyle.ALIGN_LEFT);
		style30.setBorderBottom(CellStyle.BORDER_THIN);
		style30.setBorderRight(CellStyle.BORDER_THIN);
		style30.setBorderLeft(CellStyle.BORDER_THIN);
	
		for(Integer i = 0; i< columns; i++){
    	Cell celda0 = fila0.createCell(i);
        celda0.setCellValue(special1.getColumns().get(i));
        celda0.setCellStyle(style0);  	
    	}
    	Cell celdaf0 = fila0.createCell(columns);
    	celdaf0.setCellStyle(style0);

    	Vector<Vector<ConsumptionItem>> v0 =  new Vector<Vector<ConsumptionItem>>(map.values());
		Collections.sort(v0, (Vector<ConsumptionItem> s1, Vector<ConsumptionItem> s2) -> s1.get(0).getWarehouseName().compareTo(s2.get(0).getWarehouseName()));
    	
    	Integer num0 = 0;
    	Integer l = 0;
    	for(Integer j = 0; j< v0.size();j++){
        	Vector<ConsumptionItem> v2 = v0.get(j);
        	for(ConsumptionItem ci : v2){
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
        			Row row = hoja0.createRow((l-num0)+2);
        			for(Integer k = 0; k< columns; k++){
        	    		Cell celda = row.createCell(k);
        	    		String type = special1.getColumns().get(k); 
     
        				switch (type) {
        				case "Hotel": celda.setCellValue(ci.getHotel());celda.setCellStyle(style30);break;
        				case "Almacén": celda.setCellValue(ci.getWarehouseName());celda.setCellStyle(style30);break;
        				case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style30);break;
        				case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style20);break;
        				case "Nombre": celda.setCellValue(ci.getProductName());celda.setCellStyle(style30);break;
        				case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
        				case "Valor Inicial": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
        				case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style20);break;
        				case "Valor Compras": celda.setCellValue(round((ci.getPurchasesAlb() * ci.getPurchasesValueAlb())+(ci.getPurchasesFac() * ci.getPurchasesValueFac()),2));celda.setCellStyle(style20);break;
        				case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style20);break;
        				case "Valor Ventas": celda.setCellValue(round((ci.getSalesAlb() * ci.getSalesValueAlb())+(ci.getSalesFac() * ci.getSalesValueFac()),2));celda.setCellStyle(style20);break;
        				case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style20);break;
        				case "Valor Final": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style20);break;
        				case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style20);break;
        				case "Valor Traspaso": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style20);break;
        				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
        							else celda.setCellValue(0);	
        							celda.setCellStyle(style20);break; 
        				case "Valor Consumo": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style20);break;
        				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style20);break;

        				case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style20);break;
        				default:
        					break;
        			}
        			}
        			Cell lastCell = row.createCell(columns);
        			lastCell.setCellStyle(style20);
        			row.setHeightInPoints(20);
        		}
        		else{
        			num0++;
        		}
        		l++;
        	}    		
    	}
    	for(Integer h = 0; h< columns;h++){
    		hoja0.autoSizeColumn(h);
    	}
	}
	
	private void libro2(String domain, Integer domainId, Vector<Warehouse>  warehouses, HSSFWorkbook libro, Vector<ConsumptionItem> cis, boolean onlyNegative, Map<String, Vector<ConsumptionItem>> map, String hotel) {
    	
    	Vector<String> v2 = new Vector<String>();
		v2.add("Hotel");
		v2.add("Almacen");
		v2.add("Producto");
		v2.add("Nombre");
		v2.add("Inicial");
		v2.add("Valor Inicial");
		v2.add("Compras");
		v2.add("Valor Compras");
		v2.add("Ventas");
		v2.add("Valor Ventas");
		v2.add("Traspaso");
		v2.add("Valor Traspaso");
		v2.add("Final");
		v2.add("Valor Final");
		v2.add("Consumo");
		v2.add("Valor Consumo");
		TemplateInfo special2 = new TemplateInfo();
		special2.setColumns(v2);
		
		Integer columns = v2.size();
		
		ConsumptionItem consumptionItem1 = DBConsumption.getTwoLastInventory(domain, domainId, warehouses.get(1).getId());
    	Integer initialId1 = consumptionItem1.getInitialId(), finalId1 = consumptionItem1.getFinalId();
        Date initialDate1 = consumptionItem1.getInitialDate(), finalDate1 = consumptionItem1.getFinalDate();
        String warehouseName1 = DBConsumption.getWarehouseName(domain, domainId, warehouses.get(1).getId());
        String initialInventoryName1 = DBConsumption.getInventoryName(domain, domainId, initialId1);
        String finalInventoryName1 = DBConsumption.getInventoryName(domain, domainId, finalId1);
        consumptionItem1.setWarehouseId(warehouses.get(1).getId());
        consumptionItem1.setWarehouseName(warehouseName1);
        consumptionItem1.setInitialInventoryName(initialInventoryName1);
        consumptionItem1.setFinalInventoryName(finalInventoryName1);
    	cis.add(consumptionItem1);
    	HSSFSheet hoja1 = libro.createSheet("Plantilla "+ 1);
        
    	hoja1.addMergedRegion(new Region(0,(short)0,0,columns.shortValue()));
    	Row rowInfo1 = hoja1.createRow(0);
    	Row fila1 = hoja1.createRow(1);
   

    	String info0 = "Control de Consumo ## " + warehouseName1+" ## "
    			+ initialInventoryName1 +" ## " + finalInventoryName1;
        
    	rowInfo1.setHeightInPoints(16);
    	fila1.setHeightInPoints(16);
    	CellStyle style1 = libro.createCellStyle();CellStyle styleInfo1 = libro.createCellStyle();
    	Font font1 = libro.createFont();
    	font1.setFontHeightInPoints((short)12);
    	font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
    	style1.setFont(font1);styleInfo1.setFont(font1);
    	style1.setAlignment(CellStyle.ALIGN_CENTER);styleInfo1.setAlignment(CellStyle.ALIGN_CENTER);
    	style1.setBorderBottom(CellStyle.BORDER_MEDIUM); styleInfo1.setBorderBottom(CellStyle.BORDER_MEDIUM);
    	styleInfo1.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);

    	Cell cellInfo0 = rowInfo1.createCell(0);
    	cellInfo0.setCellValue(info0);
    	cellInfo0.setCellStyle(styleInfo1);
    	
    	CellStyle style20 = libro.createCellStyle();
   		Font font20 = libro.createFont();
    	font1.setFontHeightInPoints((short)12);
		style20.setFont(font20);
		style20.setAlignment(CellStyle.ALIGN_RIGHT);
		style20.setBorderBottom(CellStyle.BORDER_THIN);
		style20.setBorderRight(CellStyle.BORDER_THIN);
		style20.setBorderLeft(CellStyle.BORDER_THIN);
	
		CellStyle style30 = libro.createCellStyle();
		style30.setFont(font20);
		style30.setAlignment(CellStyle.ALIGN_LEFT);
		style30.setBorderBottom(CellStyle.BORDER_THIN);
		style30.setBorderRight(CellStyle.BORDER_THIN);
		style30.setBorderLeft(CellStyle.BORDER_THIN);
	
		for(Integer i = 0; i< columns; i++){
    	Cell celda0 = fila1.createCell(i);
        celda0.setCellValue(special2.getColumns().get(i));
        celda0.setCellStyle(style1);  	
    	}
    	Cell celdaf0 = fila1.createCell(columns);
    	celdaf0.setCellStyle(style1);

    	String warehouseName ="";
    	Map<Integer, ConsumptionItem> map1 = DBConsumption.getConsumption(domain, domainId, initialId1, finalId1, warehouses.get(1).getId(), new java.sql.Date(initialDate1.getTime()), new java.sql.Date(finalDate1.getTime()), warehouseName);
    	Vector<ConsumptionItem> v1 =  new Vector<ConsumptionItem>(map1.values());
    	Integer num1 = 0;
    	
    	for(Integer j = 0; j< v1.size();j++){
    		ConsumptionItem ci = v1.get(j);
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
    			Row row = hoja1.createRow((j-num1)+2);
    			for(Integer k = 0; k< columns; k++){
    	    		Cell celda = row.createCell(k);
    	    		String type = special2.getColumns().get(k); 
 
    				switch (type) {
    				case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style30);break;
    				case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style20);break;
    				case "Nombre": celda.setCellValue(ci.getProductName());celda.setCellStyle(style30);break;
    				case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
    				case "Valor Inicial": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
    				case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style20);break;
    				case "Valor Compras": celda.setCellValue(round((ci.getPurchasesAlb() * ci.getPurchasesValueAlb())+(ci.getPurchasesFac() * ci.getPurchasesValueFac()),2));celda.setCellStyle(style20);break;
    				case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style20);break;
    				case "Valor Ventas": celda.setCellValue(round((ci.getSalesAlb() * ci.getSalesValueAlb())+(ci.getSalesFac() * ci.getSalesValueFac()),2));celda.setCellStyle(style20);break;
    				case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style20);break;
    				case "Valor Final": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style20);break;
    				case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style20);break;
    				case "Valor Traspaso": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style20);break;
    				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
    				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
    				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
    							else celda.setCellValue(0);	
    							celda.setCellStyle(style20);break; 
    				case "Valor Consumo": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style20);break;
    				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style20);break;

    				case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style20);break;
    				default:
    					break;
    			}
    			}
    			Cell lastCell = row.createCell(columns);
    			lastCell.setCellStyle(style20);
    			row.setHeightInPoints(20);
    		}
    		else{
    			num1++;
    		}
    	}
    	for(Integer h = 0; h< columns;h++){
    		hoja1.autoSizeColumn(h);
    	}
	}
	
}