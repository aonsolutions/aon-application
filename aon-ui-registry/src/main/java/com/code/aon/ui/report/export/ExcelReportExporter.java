package com.code.aon.ui.report.export;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;


public class ExcelReportExporter implements IReportExporter {
	
	private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFCellStyle headerCellStyle;
    private HSSFRow headerRow;
    private HSSFRow row;
    private int columnCount;
    private int rowCount;
    private int cellCount;
    private DataFormat dataFormat;
    private CellStyle dateCellStyle;
    private CellStyle decimalCellStyle;
    
	@Override
	public void exportHeader(ReportMetadata metadata) throws ReportException {
        for (int i = 1; i < (metadata.getCount() + 1); i++) {
			ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
			int width = ((columnMetadata.getDisplaySize() + 2)*256);
			width = width < 512 ? 512 : width;
			width = width > (255 * 255) ? (255 * 255) : width;
			addRow( columnMetadata.getLabel(), width);
		}
	}

	public void addRow( String label ) {
		addRow(label, 0);
	}
	
	public void addRow( String label, int width ) {
		if ( this.headerRow == null ) {
			this.headerRow = sheet.createRow(rowCount++);	
		}
		if ( width > 0 ) {
			sheet.setColumnWidth(columnCount, width);	
		}
		HSSFCellUtil.createCell(headerRow, columnCount++, label, headerCellStyle);
	}
	
	public HSSFCell addCell() {
		return row.createCell(cellCount++);
	}

	public void addStringCell( String value ) {
		HSSFCell cell = addCell();
		cell.setCellValue( value );
		cell.setCellType(Cell.CELL_TYPE_STRING);
	}
	
	public void addNumberCell( double number) {
		HSSFCell cell = addCell();
		cell.setCellValue( number );
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	}

	private CellStyle getDecimalCellStyle() {
		if ( decimalCellStyle == null ) {
			decimalCellStyle = wb.createCellStyle();
			String pattern = AonUtil.getMessage(ICommonMessages.DECIMAL_2_PATTERN);
			decimalCellStyle.setDataFormat( dataFormat.getFormat(pattern) );
		}
		return decimalCellStyle;
	}
	
	public void addDecimalCell( double number) {
		HSSFCell cell = addCell();
		cell.setCellValue( number );
		cell.setCellStyle( getDecimalCellStyle() );
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	}
	
	public void addBooleanCell( boolean value ) {
		HSSFCell cell = addCell();
		cell.setCellValue( value );
		cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
	}

	private CellStyle getDateCellStyle() {
		if ( dateCellStyle == null ) {
			dateCellStyle = wb.createCellStyle();
			String pattern = AonUtil.getMessage(ICommonMessages.DATE_PATTERN);
			dateCellStyle.setDataFormat( dataFormat.getFormat(pattern) );
		}
		return dateCellStyle;
	}	
	
	public void addDateCell( Date value ) {
		HSSFCell cell = addCell();
		cell.setCellValue(value);
		cell.setCellStyle(getDateCellStyle());		
	}
	
	@Override
	public void exportColumn(ReportColumnMetadata column, Object data) throws ReportException {
		if (column.getType() == Types.VARCHAR 
				|| column.getType() == Types.CHAR 
				|| column.getType() == Types.LONGVARCHAR) {
			addStringCell((String) data); 
		} else if (column.getType() == Types.INTEGER || column.getType() == Types.TINYINT || column.getType() == Types.SMALLINT ) {
			double d = data==null?0.0: ((Integer) data).doubleValue();
			addNumberCell( d );
		} else if ( column.getType() == Types.BIT) {
			HSSFCell cell = addCell();
			if (data != null) {
				cell.setCellValue((Boolean) data);
			}
		} else if (column.getType() == Types.DATE ) {
			addDateCell( (Date)data );
		} else if (column.getType() == Types.TIMESTAMP) {
			HSSFCell cell = addCell();
			if (data != null) {
				Calendar c = Calendar.getInstance();
				c.setTime( (Date) data );
				cell.setCellValue(c);
			}
		} else if (column.getType() == Types.DOUBLE) {
			addNumberCell( (Double) data );
		} else if (column.getType() == Types.LONGVARBINARY) {
			addCell().setCellValue("BLOB");
		} else {
			throw new ReportException(" Tipo no soportado para " + column.getName() + " (" +  column.getType() + ")");	
		}
	}

	@Override
	public void startExport( String name ) throws ReportException {
	    wb = new HSSFWorkbook();
	    sheet = wb.createSheet(name);
        sheet.setDefaultColumnWidth(50);
        dataFormat = wb.getCreationHelper().createDataFormat();
	    headerCellStyle = wb.createCellStyle();
	    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
	    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	    headerCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    cellCount = 0;
	    columnCount = 0;
	}

	@Override
	public void startLine() throws ReportException {
		row = sheet.createRow(rowCount++);
		cellCount = 0;		
	}

	@Override
	public void endLine() throws ReportException {
	}
	
	public void autoSizeColumns() {
		for( int i = 0; i < columnCount; i++ ) {
			sheet.autoSizeColumn(i);
		}		
	}

	@Override
	public void endExport(OutputStream out) throws ReportException {
		try {
			wb.write(out);
		} catch (IOException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

}
