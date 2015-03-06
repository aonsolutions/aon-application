package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class AbsExcelAction  {
	
	protected static final String DATE_PATTERN = "dd/MM/yyyy";
	protected static final String DECIMAL_PATTERN = "#,##0.00";
	protected static final String NUMBER_PATTERN = "#,###";
	protected static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,114,207));
	
	protected SXSSFWorkbook workbook;
	protected SXSSFSheet sheet;
	protected Row row;
	protected int rowCount;
	protected int cellCount;
	protected DataFormat dataFormat;
	protected CellStyle dateStyle;
	protected CellStyle decimalStyle;
	protected CellStyle numberStyle;
	protected CellStyle centerCellStyle;
	protected XSSFCellStyle headerCellStyle;
    
	public void initialize(String name) {
		workbook = new SXSSFWorkbook(1);
		
	    sheet = (SXSSFSheet) workbook.createSheet(name);
	    dataFormat = workbook.getCreationHelper().createDataFormat();
	    rowCount = 0;
	    cellCount = 0;
	    dateStyle = workbook.createCellStyle();
	    dateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
	    dateStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
	    
	    numberStyle = workbook.createCellStyle();
	    numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
	    numberStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
	     
	    decimalStyle = workbook.createCellStyle();
	    decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	    numberStyle.setAlignment( HSSFCellStyle.ALIGN_RIGHT );
	    
		centerCellStyle = workbook.createCellStyle();
		centerCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );

		Font headerFont= workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor( IndexedColors.WHITE.index );

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
		headerCellStyle.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER);
	    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
	    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    headerCellStyle.setFillForegroundColor(AON_BLUE);
	    headerCellStyle.setFont(headerFont);
	    
	    headerRow();
	}
	
	protected Cell alignCenter(Cell cell) {
		cell.setCellStyle( centerCellStyle );
		return cell;
	}

	protected Cell addCell(String value) {
		Cell cell = row.createCell(cellCount++);
		cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell;
	}

	protected Cell addCell(Enum<?> value) {
		Cell cell = row.createCell(cellCount++);
		if ( value != null ) {
			cell.setCellValue(value.toString());
		}
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell;
	}

	protected Cell addCell(Short value) {
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(numberStyle);
		if ( value != null ) {
			cell.setCellValue(value);
		}
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}
	
	protected Cell addCell(Date value) {
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(dateStyle);
		if ( value != null ) {
			cell.setCellValue(value);
		}
		return cell;
	}

	protected Cell addCell(Double number) {
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(decimalStyle);
		cell.setCellValue(number);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public void finalize(OutputStream out) throws IOException {
		workbook.write(out);
		
		// Note that SXSSF allocates temporary files that you 
		// must always clean up explicitly, by calling the dispose method.
		//
		// http://poi.apache.org/spreadsheet/how-to.html#sxssf
		//
		workbook.dispose();
	}

	protected abstract void headerRow();
	 
}
