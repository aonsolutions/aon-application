package com.esferalia.aon.gwt.template.server;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.esferalia.aon.gwt.template.jooq.DBConsumption;
import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class ConsumptionUtil {
	
	private static final String PDF = "pdf";
	
    public static File generateConsumption(Domain domain, Vector<Warehouse> warehouses, String fileType, Boolean onlyNegative,
    		Boolean detail, Integer size, String login, Boolean packaged, Boolean withoutInv) throws ServletException, IOException{
    
    	Collections.sort(warehouses, (Warehouse s1, Warehouse s2) -> s1.getName().compareTo(s2.getName()));        
       
        TemplateInfo aux = getTemplateInfo(detail);
		
        File archivoXLS = new File("consumo" + ".xls");
        File archivoPDF = new File("consumo" + ".pdf");
        if(archivoXLS.exists()) archivoXLS.delete();
        if(archivoPDF.exists()) archivoPDF.delete();
        archivoXLS.createNewFile();     
        archivoPDF.createNewFile();
        
        Map<Integer, ConsumptionItem> cisMap = new HashMap<Integer, ConsumptionItem>();
        HSSFWorkbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        Integer columns = aux.getColumns().size();
    	
        Map<String, Vector<ConsumptionItem>> allMap = new HashMap<String, Vector<ConsumptionItem>>();
        for(Warehouse w : warehouses){
        	ConsumptionItem consumptionItem = DBConsumption.getTwoLastInventory(domain, w.getId(), login);
        	Integer initialId = consumptionItem.getInitialId(), finalId = consumptionItem.getFinalId();
            Date initialDate = consumptionItem.getInitialDate() != null ? AonDateUtils.addDays(consumptionItem.getInitialDate(), 1) : null, finalDate = consumptionItem.getFinalDate();
            String initialInventoryName = DBConsumption.getInventoryName(domain, initialId, login);
            String finalInventoryName = DBConsumption.getInventoryName(domain, finalId, login);
            consumptionItem.setWarehouseId(w.getId());
            consumptionItem.setWarehouseName(w.getName());
            consumptionItem.setInitialInventoryName(initialInventoryName);
            consumptionItem.setFinalInventoryName(finalInventoryName);
        	cisMap.put(w.getId(), consumptionItem);
        	Map<Integer, ConsumptionItem> map = withoutInv 
        			? DBConsumption.getConsumptionWithoutInventory(domain, login, initialId, finalId, w.getId(), consumptionItem.getWarehouseName())
        			: DBConsumption.getConsumption(domain, login, initialId, finalId, w.getId(), new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()), consumptionItem.getWarehouseName());

        	Vector<ConsumptionItem> v =  new Vector<ConsumptionItem>(map.values());
        	
        	allMap.put(w.getName(), v);
        }
        libro2(domain.getName(), domain.getId(),login, warehouses, libro, onlyNegative, allMap, getTemplateInfoC(),0);
        //libro(domain.getName(), domain.getId(), login, warehouses, libro, onlyNegative, allMap, getTemplateInfoA(), 1, packaged);
        libro(domain.getName(), domain.getId(), login, warehouses, libro, onlyNegative, allMap, getTemplateInfoB(), 2, packaged);
        
        for (int index = 0; index < size; index++) {
       
        	ConsumptionItem consumptionItem = cisMap.get(warehouses.get(index).getId());
            String initialInventoryName = consumptionItem.getInitialInventoryName();
            String finalInventoryName = consumptionItem.getFinalInventoryName();
        	HSSFSheet hoja = libro.createSheet("Plantilla "+ (index+3));
        	if(packaged) hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()+1));
            else hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()-1));
        	Row rowInfo = hoja.createRow(0);
        	Row fila = hoja.createRow(1);
        
        
        
        	String info = "Control de Consumo ## " + warehouses.get(index).getName()+" ## "
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
       
        	if(packaged){
        		Cell cell1 = fila.createCell(columns);
        		cell1.setCellValue("Stock");
        		cell1.setCellStyle(style);
        		Cell cell2 = fila.createCell(columns+1);
        		cell2.setCellValue("Etiqueta");
        		cell2.setCellStyle(style);
        	}
         	Vector<ConsumptionItem> v =  allMap.get(warehouses.get(index).getName());
        	
        	Integer num = 0;
        	for(Integer j = 0; j< v.size();j++){
        		ConsumptionItem ci = v.get(j);
        		ci.setConsumption(ci.getInitialQuantity()+ci.getPurchasesAlb()+ci.getPurchasesFac()+ci.getTransfersPlus()-ci.getSalesAlb()-ci.getSalesFac()-ci.getTransfersMinus()-ci.getFinalQuantity());

        		Double consumValue = ci.getConsumValue();
            	
        		
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
        			Item item = AON.getItem(domain.getName(), domain.getId(), login, ci.getItemId());
        			for(Integer k = 0; k< columns; k++){
        	    		Cell celda = row.createCell(k);
        	    		String type = aux.getColumns().get(k); 
     
        				switch (type) {
        				case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style3);break;
        				case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style2);break;
        				case "Nombre": 
        					String str  = ci.getProductName();
                			/*if(item.getPackFormatTag().getName() != null)
                				str = str + " "+item.getPackFormatTag().getName()+" "
                				+ item.getPackUnits() + " " + item.getPackUnitsTag().getName() + " "
                				+ item.getPackMeasurement() + " " + item.getPackMeasurementTag().getName();
                				*/
        					celda.setCellValue(str);celda.setCellStyle(style3);break;
        				case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        				case "Inicial \u20AC": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        				case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style2);break;
        				case "Compras \u20AC": celda.setCellValue(round((ci.getValuePAlb())+(ci.getValuePFac()),2));celda.setCellStyle(style2);break;
        				case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style2);break;
        				case "Ventas \u20AC": celda.setCellValue(round((ci.getValueSAlb())+(ci.getValueSFac()),2));celda.setCellStyle(style2);break;
        				case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style2);break;
        				case "Final \u20AC": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style2);break;
        				case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style2);break;
        				case "Traspaso \u20AC": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style2);break;
        				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
        							else celda.setCellValue(0);	
        							celda.setCellStyle(style2);break; 
        				case "Consumo \u20AC": 
        					if(ci.getConsumption() == 0) celda.setCellValue(0);
        					else celda.setCellValue(round(consumValue,2));
        					celda.setCellStyle(style2);break;
        				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style2);break;

        				case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				default:
        					break;
        			}
        			}
      
        			if(packaged){
                		Cell valueCell = row.createCell(columns);
                		Double value = ci.getConsumption() * item.getPackMeasurement() * item.getPackUnits();
                		valueCell.setCellValue(value);
                		valueCell.setCellStyle(style2);
                		Cell unityCell = row.createCell(columns+1);
                		unityCell.setCellValue(item.getPackMeasurementTag().getName());
                		unityCell.setCellStyle(style2);
            		}
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
			 
        	 for (Integer index = 0; index < size+2; index++){ 
        		 HSSFSheet my_worksheet = libro.getSheetAt(index);
        		 Iterator<Row> rowIterator = my_worksheet.iterator();             
        		 Integer columnNum = getColumnNum(index, columns, packaged);
        		 PdfPTable my_table = new PdfPTable(columnNum);
        		 PdfPCell table_cell;
        		 Integer i = 0;
        		 
        		 while(rowIterator.hasNext()) {
                    Row row = rowIterator.next(); 
                    Iterator<Cell> cellIterator = row.cellIterator();
                    		
                            while(cellIterator.hasNext()) {
                            		if(i == 0){
                            			cellIterator.next();
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
        			 	iText_xls_2_pdf.add(new Paragraph("Almac\u00e9n: " + warehouses.get(index-2).getName(), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Inicial: " + new Phrase(cisMap.get(warehouses.get(index-2).getId()).getInitialInventoryName(), fontPhrase2), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Final: " + new Phrase(cisMap.get(warehouses.get(index-2).getId()).getFinalInventoryName(), fontPhrase2), fontPhrase));
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
        
        //long length;
        //FileInputStream fis;
        if(fileType.equals(PDF)){
        	return archivoPDF;
        }
        else{// if(fileType.equals(EXCEL)){
        	return archivoXLS;
        }
    }
    
    
    public static File generateConsumption(Domain domain, Vector<Warehouse> warehouses, String fileType, Boolean onlyNegative,
    		Boolean detail, Integer size, String login, Date startDate, Date endDate, Boolean packaged) throws ServletException, IOException{
    	Collections.sort(warehouses, (Warehouse s1, Warehouse s2) -> s1.getName().compareTo(s2.getName()));        
        
        TemplateInfo aux = getTemplateInfo(detail);
		
        File archivoXLS = new File("consumo" + ".xls");
        File archivoPDF = new File("consumo" + ".pdf");
        if(archivoXLS.exists()) archivoXLS.delete();
        if(archivoPDF.exists()) archivoPDF.delete();
        archivoXLS.createNewFile();     
        archivoPDF.createNewFile();
        
        Map<Integer, ConsumptionItem> cisMap = new HashMap<Integer, ConsumptionItem>();
        HSSFWorkbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        Integer columns = aux.getColumns().size();
    	
        Map<String, Vector<ConsumptionItem>> allMap = new HashMap<String, Vector<ConsumptionItem>>();
        for(Warehouse w : warehouses){
        
        	Inventory initialInventory = DBConsumption.getInitialInventory(domain, login, w.getId(), AonDateUtils.toSql(startDate), AonDateUtils.toSql(endDate));
        	Inventory finalInventory = DBConsumption.getFinalInventory(domain, login, w.getId(), AonDateUtils.toSql(startDate), AonDateUtils.toSql(endDate));
        	ConsumptionItem consumptionItem = new ConsumptionItem()
        			.setInitialId(initialInventory.getId())
        			.setInitialDate(initialInventory.getInventoryDate())
        			.setInitialInventoryName(initialInventory.getDescription())
        			.setFinalId(finalInventory.getId())
        			.setFinalDate(finalInventory.getInventoryDate())
        			.setFinalInventoryName(finalInventory.getDescription());
        
        	if(initialInventory.getId() != null && finalInventory.getId() != null
        			&& initialInventory.getId() != finalInventory.getId()){
        		Integer initialId = consumptionItem.getInitialId(), finalId = consumptionItem.getFinalId();
        		Date initialDate = AonDateUtils.addDays(consumptionItem.getInitialDate(), 1), finalDate = consumptionItem.getFinalDate();
            	String initialInventoryName = DBConsumption.getInventoryName(domain, initialId, login);
            	String finalInventoryName = DBConsumption.getInventoryName(domain, finalId, login);
            	consumptionItem.setWarehouseId(w.getId());
            	consumptionItem.setWarehouseName(w.getName());
            	consumptionItem.setInitialInventoryName(initialInventoryName);
            	consumptionItem.setFinalInventoryName(finalInventoryName);
        		cisMap.put(w.getId(), consumptionItem);
        		Map<Integer, ConsumptionItem> map = DBConsumption.getConsumption(domain, login, initialId, finalId, w.getId(), new java.sql.Date(initialDate.getTime()), new java.sql.Date(finalDate.getTime()), consumptionItem.getWarehouseName());

        		Vector<ConsumptionItem> v =  new Vector<ConsumptionItem>(map.values());
        	
        		allMap.put(w.getName(), v);
        	} else{
        		cisMap.put(w.getId(), new ConsumptionItem());
        		allMap.put(w.getName(), new Vector<ConsumptionItem>());
        	}
        }
        libro2(domain.getName(), domain.getId(), login, warehouses, libro, onlyNegative, allMap, getTemplateInfoC(), 0);
        //libro(domain.getName(), domain.getId(), login, warehouses, libro, onlyNegative, allMap, getTemplateInfoA(), 1, packaged);
        libro(domain.getName(), domain.getId(), login, warehouses, libro, onlyNegative, allMap, getTemplateInfoB(), 2, packaged);
        
        for (int index = 0; index < size; index++) {
       
        	ConsumptionItem consumptionItem = cisMap.get(warehouses.get(index).getId());
            String initialInventoryName = consumptionItem.getInitialInventoryName() != null 
            		? consumptionItem.getInitialInventoryName() : " ";
            String finalInventoryName = consumptionItem.getFinalInventoryName() != null 
            		? consumptionItem.getFinalInventoryName() : " ";
        	HSSFSheet hoja = libro.createSheet("Plantilla "+ (index+3));
        
        	if(packaged) hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()+1));
            else hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()-1));
        	
        	Row rowInfo = hoja.createRow(0);
        	Row fila = hoja.createRow(1);
        
        
        
        	String info = "Control de Consumo ## " + warehouses.get(index).getName()+" ## "
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

        	if(packaged){
        		Cell cell1 = fila.createCell(columns);
        		cell1.setCellValue("Stock");
        		cell1.setCellStyle(style);
        		Cell cell2 = fila.createCell(columns+1);
        		cell2.setCellValue("Etiqueta");
        		cell2.setCellStyle(style);
        	}
        	
         	Vector<ConsumptionItem> v =  allMap.get(warehouses.get(index).getName());
        	
        	Integer num = 0;
        	for(Integer j = 0; j< v.size();j++){
        		ConsumptionItem ci = v.get(j);
        		ci.setConsumption(ci.getInitialQuantity()+ci.getPurchasesAlb()+ci.getPurchasesFac()+ci.getTransfersPlus()-ci.getSalesAlb()-ci.getSalesFac()-ci.getTransfersMinus()-ci.getFinalQuantity());

        		Double consumValue = ci.getConsumValue();
        		
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
        			Item item = AON.getItem(domain.getName(), domain.getId(), login, ci.getItemId());

        			for(Integer k = 0; k< columns; k++){
        	    		Cell celda = row.createCell(k);
        	    		String type = aux.getColumns().get(k); 
     
        				switch (type) {
        				case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style3);break;
        				case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style2);break;
        				case "Nombre": 
        					String str  = ci.getProductName();
                			/*if(item.getPackFormatTag().getName() != null)
                				str = str + " "+item.getPackFormatTag().getName()+" "
                				+ item.getPackUnits() + " " + item.getPackUnitsTag().getName() + " "
                				+ item.getPackMeasurement() + " " + item.getPackMeasurementTag().getName();
        					*/
        					celda.setCellValue(str);celda.setCellStyle(style3);break;
        				case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        				case "Inicial \u20AC": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style2);break;
        				case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style2);break;
        				case "Compras \u20AC": celda.setCellValue(round((ci.getValuePAlb())+(ci.getValuePFac()),2));celda.setCellStyle(style2);break;
        				case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style2);break;
        				case "Ventas \u20AC": celda.setCellValue(round((ci.getValueSAlb())+(ci.getValueSFac()),2));celda.setCellStyle(style2);break;
        				case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style2);break;
        				case "Final \u20AC": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style2);break;
        				case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style2);break;
        				case "Traspaso \u20AC": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style2);break;
        				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
        							else celda.setCellValue(0);	
        							celda.setCellStyle(style2);break; 
        				case "Consumo \u20AC": 
        					if(ci.getConsumption() == 0) celda.setCellValue(0);
        					else celda.setCellValue(round(consumValue,2));
        					celda.setCellStyle(style2);break;
        				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style2);break;

        				case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				default:
        					break;
        			}
        			}
        			
        			if(packaged){
                		Cell valueCell = row.createCell(columns);
                		Double value = ci.getConsumption() * item.getPackMeasurement() * item.getPackUnits();
                		valueCell.setCellValue(value);
                		valueCell.setCellStyle(style2);
                		Cell unityCell = row.createCell(columns+1);
                		unityCell.setCellValue(item.getPackMeasurementTag().getName());
                		unityCell.setCellStyle(style2);
            		}

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
			 
			 Paragraph title = new Paragraph("Control de Consumo",fontTitle);
			 try {
				iText_xls_2_pdf.add(title);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
			 
        	 for (int index = 0; index < size+2; index++){ 
        		 HSSFSheet my_worksheet = libro.getSheetAt(index);
        		 Iterator<Row> rowIterator = my_worksheet.iterator();             
        		 Integer columnNum = getColumnNum(index, columns, packaged);
        		 PdfPTable my_table = new PdfPTable(columnNum);
        		 PdfPCell table_cell;
        		 Integer i = 0;
        		 
        		 while(rowIterator.hasNext()) {
                    Row row = rowIterator.next(); 
                    Iterator<Cell> cellIterator = row.cellIterator();
                    		
                            while(cellIterator.hasNext()) {
                            		if(i == 0){
                            			cellIterator.next();
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
        			 	iText_xls_2_pdf.add(new Paragraph("Almac\u00e9n: " + warehouses.get(index-2).getName(), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Inicial: " + new Phrase(cisMap.get(warehouses.get(index-2).getId()).getInitialInventoryName(), fontPhrase2), fontPhrase));
        			 	iText_xls_2_pdf.add(new Paragraph("Inventario Final: " + new Phrase(cisMap.get(warehouses.get(index-2).getId()).getFinalInventoryName(), fontPhrase2), fontPhrase));
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
        
        //long length;
        //FileInputStream fis;
        if(fileType.equals(PDF)){
        	return archivoPDF;
        }
        else{// if(fileType.equals(EXCEL)){
        	return archivoXLS;
        }
    }
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	private static TemplateInfo getTemplateInfoA() {
		Vector<String> v = new Vector<String>();
		v.add("Hotel");
		v.add("Almac\u00e9n");
		v.add("Producto");
		v.add("Inicial \u20AC"); 
		v.add("Compras \u20AC"); 
		v.add("Ventas \u20AC");
		v.add("Final \u20AC");
		v.add("Traspaso \u20AC"); 
		v.add("Consumo \u20AC");
		return new TemplateInfo().setColumns(v);
	}

	private static  TemplateInfo getTemplateInfoB() {
		Vector<String> v = new Vector<String>();
		v.add("Hotel");
		v.add("Desde");
		v.add("Hasta");
		v.add("Almac\u00e9n");
		v.add("Producto");
		v.add("Nombre");
		v.add("Inicial");
		v.add("Inicial \u20AC");
		v.add("Compras");
		v.add("Compras \u20AC");
		v.add("Ventas");
		v.add("Ventas \u20AC");
		v.add("Traspaso");
		v.add("Traspaso \u20AC");
		v.add("Final");
		v.add("Final \u20AC");
		v.add("Consumo");
		v.add("Consumo \u20AC");
		return new TemplateInfo().setColumns(v);
	}
	
	private static TemplateInfo getTemplateInfoC() {
		Vector<String> v = new Vector<String>();
		v.add("Hotel");
		v.add("Desde");
		v.add("Hasta");
		v.add("Almac\u00e9n");
		v.add("Inicial \u20AC"); 
		v.add("Compras \u20AC"); 
		v.add("Ventas \u20AC");
		v.add("Final \u20AC");
		v.add("Traspaso \u20AC"); 
		v.add("Consumo \u20AC");
		return new TemplateInfo().setColumns(v);
	}
	
	private static TemplateInfo getTemplateInfo(Boolean detail) {
		Vector<String> v = new Vector<String>();
		v.add("Producto");
		v.add("Nombre");
		v.add("Inicial");
		if(detail) v.add("Inicial \u20AC");
		v.add("Compras");
		if(detail) v.add("Compras \u20AC");
		v.add("Ventas");
		if(detail) v.add("Ventas \u20AC");
		v.add("Traspaso");
		if(detail) v.add("Traspaso \u20AC");
		v.add("Final");
		if(detail) v.add("Final \u20AC");
		v.add("Consumo");
		v.add("Consumo \u20AC");
		return new TemplateInfo().setColumns(v);
	}
	
	private static  void libro(String domain, Integer domainId, String login, Vector<Warehouse>  warehouses, HSSFWorkbook libro,  boolean onlyNegative, Map<String, Vector<ConsumptionItem>> map, TemplateInfo special1, Integer hoja, Boolean packaged){
		
		Integer columns = special1.getColumns().size(); 
	
    	HSSFSheet hoja0 = libro.createSheet("Plantilla "+ hoja);
        
    	if(packaged)hoja0.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()+1));
    	else hoja0.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()-1));
    	
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
    		
    	if(packaged){
    		Cell cell1 = fila0.createCell(columns);
            cell1.setCellValue("Stock");
    		cell1.setCellStyle(style0);
    		Cell cell2 = fila0.createCell(columns+1);
            cell2.setCellValue("Etiqueta");
    		cell2.setCellStyle(style0);
    	}
    	
    	Integer num0 = 0;
    	Integer l = 0;

    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    	for (Warehouse w : warehouses) {
        	Vector<ConsumptionItem> v2 = map.get(w.getName());
            Collections.sort(v2, (ConsumptionItem c1, ConsumptionItem c2) -> c1.getConsumValue().compareTo(c2.getConsumValue()));        
        	for(ConsumptionItem ci : v2){
        		ci.setConsumption(ci.getInitialQuantity()+ci.getPurchasesAlb()+ci.getPurchasesFac()+ci.getTransfersPlus()-ci.getSalesAlb()-ci.getSalesFac()-ci.getTransfersMinus()-ci.getFinalQuantity());

        		Double consumValue = ci.getConsumValue();
            	
        		
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
        			Item item = AON.getItem(domain, domainId, login, ci.getItemId());
        			for(Integer k = 0; k< columns; k++){
        	    		Cell celda = row.createCell(k);
        	    		String type = special1.getColumns().get(k); 
     
        				switch (type) {
        				case "Hotel": celda.setCellValue(ci.getHotel());celda.setCellStyle(style30);break;
        				case "Desde": celda.setCellValue(ci.getInitialDate() != null ? format.format(sumarRestarDiasFecha(ci.getInitialDate(), -1)) : "-");celda.setCellStyle(style30);break;
           				case "Hasta": celda.setCellValue(format.format(ci.getFinalDate()));celda.setCellStyle(style30);break;
        				case "Almac\u00e9n": celda.setCellValue(ci.getWarehouseName());celda.setCellStyle(style30);break;
        				case "Producto": celda.setCellValue(ci.getProductCode());celda.setCellStyle(style30);break;
        				case "Texto Libre": celda.setCellValue("");celda.setCellStyle(style20);break;
        				case "Nombre": 
        					String str  = ci.getProductName();
                			/*if(item.getPackFormatTag().getName() != null)
                				str = str + " "+item.getPackFormatTag().getName()+" "
                				+ item.getPackUnits() + " " + item.getPackUnitsTag().getName() + " "
                				+ item.getPackMeasurement() + " " + item.getPackMeasurementTag().getName();
                				*/
        					celda.setCellValue(str);celda.setCellStyle(style30);break;
        				case "Inicial": celda.setCellValue(round(ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
        				case "Inicial \u20AC": celda.setCellValue(round(ci.getInitialValue() * ci.getInitialQuantity(),2));celda.setCellStyle(style20);break;
        				case "Compras": celda.setCellValue(round(ci.getPurchasesAlb()+ci.getPurchasesFac(),2));celda.setCellStyle(style20);break;
        				case "Compras \u20AC": celda.setCellValue(round((ci.getValuePAlb())+(ci.getValuePFac()),2));celda.setCellStyle(style20);break;
        				case "Ventas": celda.setCellValue(round(ci.getSalesAlb()+ ci.getSalesFac(),2));celda.setCellStyle(style20);break;
        				case "Ventas \u20AC": celda.setCellValue(round((ci.getValueSAlb())+(ci.getValueSFac()),2));celda.setCellStyle(style20);break;
        				case "Final": celda.setCellValue(round(ci.getFinalQuantity(),2));celda.setCellStyle(style20);break;
        				case "Final \u20AC": celda.setCellValue(round(ci.getFinalQuantity() * ci.getFinalValue(),2));celda.setCellStyle(style20);break;
        				case "Traspaso": celda.setCellValue(round(ci.getTransfersPlus()-ci.getTransfersMinus(),2));celda.setCellStyle(style20);break;
        				case "Traspaso \u20AC": celda.setCellValue(round((ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice()),2));celda.setCellStyle(style20);break;
        				//case "Precio": celda.setCellValue(round(ci.getPrice(),2));celda.setCellStyle(style2);break; 
        				//case "Valor Consumo": celda.setCellValue(round(ci.getPrice()*ci.getConsumption(),2));celda.setCellStyle(style2);break;
        				case "Precio": if(ci.getConsumption() != 0) celda.setCellValue(round(consumValue / ci.getConsumption(), 2));
        							else celda.setCellValue(0);	
        							celda.setCellStyle(style20);break; 
        				case "Consumo \u20AC":
        					if(ci.getConsumption() == 0) celda.setCellValue(0);
        					else celda.setCellValue(round(consumValue,2));
        					celda.setCellStyle(style20);break;
        				case "Importe": celda.setCellValue(round(consumValue,2));celda.setCellStyle(style20);break;

        				case "Consumo": celda.setCellValue(round(ci.getConsumption(),2));celda.setCellStyle(style20);break;
        				default:
        					break;
        			}
        			}
        			
        			if(packaged){
                		Cell valueCell = row.createCell(columns);
                		Double value = ci.getConsumption() * item.getPackMeasurement() * item.getPackUnits();
                		valueCell.setCellValue(value);
                		valueCell.setCellStyle(style20);
                		Cell unityCell = row.createCell(columns+1);
                		unityCell.setCellValue(item.getPackMeasurementTag().getName());
                		unityCell.setCellStyle(style20);
            		}
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
	
	private static  void libro2(String domain, Integer domainId, String login, Vector<Warehouse>  warehouses, HSSFWorkbook libro,  boolean onlyNegative, Map<String, Vector<ConsumptionItem>> map, TemplateInfo special1, Integer hoja){
		
		Integer columns = special1.getColumns().size(); 
	
    	HSSFSheet hoja0 = libro.createSheet("Plantilla "+ hoja);
        
    	hoja0.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.shortValue()-1));

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

    	Integer l = 0;
    	for (Warehouse w : warehouses) {
        	Vector<ConsumptionItem> v2 = map.get(w.getName());
        	Double consumo = 0.0;
        	Double traspaso = 0.0;
        	Double fin = 0.0;
        	Double ventas = 0.0;
        	Double compras = 0.0;
        	Double inicial = 0.0;
        	String hotel = DBConsumption.getHotelName(domain, domainId, login, w.getId());
        	Date startDate = new Date();
        	Date endDate = new Date();
        	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        	for(ConsumptionItem ci : v2){
        		startDate = ci.getInitialDate() != null ? sumarRestarDiasFecha(ci.getInitialDate(), -1) : null;
        		endDate = ci.getFinalDate();
        		ci.setConsumption(ci.getInitialQuantity()+ci.getPurchasesAlb()+ci.getPurchasesFac()+ci.getTransfersPlus()-ci.getSalesAlb()-ci.getSalesFac()-ci.getTransfersMinus()-ci.getFinalQuantity());
        		Double consumValue = ci.getConsumption() == 0 ? 0 : ci.getConsumValue();
            	
        		if((onlyNegative && consumValue < 0) || !onlyNegative){
        		
        			if(consumValue != null) consumo = consumo + consumValue;
        		
        			Double transferValue = (ci.getTransfersPlus() * ci.getPrice()) - (ci.getTransfersMinus() * ci.getPrice());
        			if(transferValue != null) traspaso = traspaso + transferValue;
        		
        			Double finalValue = ci.getFinalQuantity() * ci.getFinalValue();
        			if(finalValue != null) fin = fin + finalValue;
        		
        			Double salesValue = (ci.getValueSAlb())+(ci.getValueSFac());
        			if(salesValue != null) ventas = ventas +salesValue;
        			
        			Double purchasesValue = (ci.getValuePAlb())+(ci.getValuePFac());
        			if(purchasesValue != null) compras = compras +purchasesValue;
        		
        			Double initialValue = ci.getInitialValue() * ci.getInitialQuantity();
        			if(initialValue != null) inicial = inicial + initialValue;
        		
        		}
        	}
        	Row row = hoja0.createRow(l+2);
        	for(Integer k = 0; k< columns; k++){
        	   	Cell celda = row.createCell(k);
        	   	String type = special1.getColumns().get(k); 
           		switch (type) {
        				case "Hotel": celda.setCellValue(hotel);celda.setCellStyle(style30);break;
           				case "Desde": celda.setCellValue(startDate != null ? format.format(startDate) : "-");celda.setCellStyle(style30);break;
           				case "Hasta": celda.setCellValue(format.format(endDate));celda.setCellStyle(style30);break;
        				case "Almac\u00e9n": celda.setCellValue(w.getName());celda.setCellStyle(style30);break;
        				case "Inicial \u20AC": celda.setCellValue(round(inicial,2));celda.setCellStyle(style20);break;
        				case "Compras \u20AC": celda.setCellValue(round(compras,2));celda.setCellStyle(style20);break;
        				case "Ventas \u20AC": celda.setCellValue(round(ventas,2));celda.setCellStyle(style20);break;
        				case "Final \u20AC": celda.setCellValue(round(fin,2));celda.setCellStyle(style20);break;
        				case "Traspaso \u20AC": celda.setCellValue(round(traspaso,2));celda.setCellStyle(style20);break;
        				case "Consumo \u20AC": celda.setCellValue(round(consumo,2));celda.setCellStyle(style20);break;
        				default:
        					break;
        		}
        	}
        	row.setHeightInPoints(20);
        	l++;
        }
        		
    	for(Integer h = 0; h< columns;h++){
    		hoja0.autoSizeColumn(h);
    	}      	
	}
	
	 public static Date sumarRestarDiasFecha(Date fecha, int dias){
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(fecha); // Configuramos la fecha que se recibe
		 calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
		 return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
	 }
	
	private static Integer getColumnNum(Integer index, Integer columns, Boolean packaged) {
		 if(index == 0)
			 return 8;
		 else if(index == 1)
			 return packaged ? 12 : 9;
		 else if(index == 2)
			 return packaged ? 19 : 16;
		 else
			return packaged ? columns + 3 : columns;
	}
}
