package net.aonsolutions.aon.api.excel;

import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelExport {
	
	SXSSFWorkbook workbook;
	SXSSFSheet sheet;
	String sheetName;
	List<AonExcelColumn> columns = new LinkedList<>();
	int rowIndex;
	
	protected static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,36,105));
	protected static final XSSFColor AON_LIGHT_BLUE = new XSSFColor(new java.awt.Color(219,228,244));
	protected static final XSSFColor AON_LIGHT_GRAY = new XSSFColor(new java.awt.Color(240,240,240));

	
	protected void build() {
		workbook = new SXSSFWorkbook(1);
		sheet = (SXSSFSheet) workbook.createSheet(sheetName);
	}

	protected void buildHeader() {
        Row row = sheet.createRow(0);
        XSSFCellStyle style = getHeaderStyle();
        for(Integer i = 0; i< columns.size(); i++){
            sheet.setColumnWidth(i, columns.get(i).getWidth() * 256);
        	Cell celda = row.createCell(i);
        	celda.setCellValue(columns.get(i).getValue());
        	celda.setCellStyle(style);
        }
	}
	
	protected Font getHeaderFont() {
		Font font= workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 8);
		font.setColor(IndexedColors.WHITE.index);
		return font;
	}
	
	protected XSSFCellStyle getHeaderStyle() {
		Font font = getHeaderFont();
		XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
		style.setAlignment( HorizontalAlignment.CENTER );
		style.setVerticalAlignment( VerticalAlignment.CENTER);
	    style.setBorderBottom(BorderStyle.MEDIUM);
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setFillForegroundColor(AON_BLUE);
	    style.setFont(font);
	    return style;
	}
	
	protected XSSFCellStyle getParStyle() {
		Font font= workbook.createFont();
        font.setFontHeightInPoints((short) 8);
        font.setColor( IndexedColors.BLACK.index );
		
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setAlignment( HorizontalAlignment.CENTER );
        style.setVerticalAlignment( VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(AON_LIGHT_GRAY);
        style.setFont(font);
        return style;
	}
	
	protected XSSFCellStyle getImparStyle() {
		Font font= workbook.createFont();
        font.setFontHeightInPoints((short) 8);
        font.setColor( IndexedColors.BLACK.index );
		
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setAlignment( HorizontalAlignment.CENTER );
        style.setVerticalAlignment( VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(AON_LIGHT_BLUE);
        style.setFont(font);
        return style;
	}
	
	protected boolean isPar(Integer n) {
		return n % 2 == 0;
	}

}
