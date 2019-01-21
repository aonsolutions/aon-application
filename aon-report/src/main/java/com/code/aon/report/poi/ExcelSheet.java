package com.code.aon.report.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelSheet {

    private HSSFSheet sheet;
    private HSSFRow headerRow;
    private HSSFRow row;
    private int columnCount;
    private int rowCount;
    private int cellCount;

    public ExcelSheet( HSSFWorkbook workbook, String name ) {
	    sheet = workbook.createSheet(name);
        sheet.setDefaultColumnWidth(50);
	    columnCount = 0;
	    rowCount = 0;
	    cellCount = 0;        
	}
    
	public void addHeaderRow() {
		headerRow = sheet.createRow(rowCount++);
	    columnCount = 0;
	}

	public void addHeaderCell(String label, int width, HSSFCellStyle cellStyle) {
		if (headerRow == null) {
			addHeaderRow();
		}
		if (width > 0) {
			sheet.setColumnWidth(columnCount, width);	
		}
		HSSFCellUtil.createCell(headerRow, columnCount++, label, cellStyle);
	}

	public HSSFCell addCell(HSSFCellStyle cellStyle) {
		HSSFCell cell = row.createCell(cellCount++);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
		return cell;
	}

	public void startLine() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;		
	}

	public void addMergedRegion(int fromRow, int toRow, int fromCell, int toCell) {
		sheet.addMergedRegion(new CellRangeAddress(fromRow, toRow, fromCell, toCell));
	}
	
	public void autoSizeColumns() {
		for(int i=0; i<columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
}
