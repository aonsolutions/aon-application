package com.code.aon.report.poi;

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


public class ExcelReportExporter implements IReportExporter {
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String NUMBER_PATTERN = "#,##0.00";
	public  static short DEFAULT_BACKGROUND = new HSSFColor.AUTOMATIC().getIndex();
	
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
    
    public HSSFCellStyle  createCellStyle() {
    	return wb.createCellStyle();	
    }
    
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

	public HSSFCell addStringCell( String value ) {
		HSSFCell cell = addCell();
		cell.setCellValue( value );
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell;
	}
	
	public HSSFCell addNumberCell( double number ) {
		HSSFCell cell = addCell();
		cell.setCellValue( number );
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	private CellStyle getDecimalCellStyle() {
		if ( decimalCellStyle == null ) {
			decimalCellStyle = wb.createCellStyle();
			decimalCellStyle.setDataFormat( dataFormat.getFormat(NUMBER_PATTERN) );
		}
		return decimalCellStyle;
	}
	
	public HSSFCell addDecimalCell( double number) {
		HSSFCell cell = addCell();
		cell.setCellValue( number );
		cell.setCellStyle( getDecimalCellStyle() );
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}
	public HSSFCell addEmptyDecimalCell() {
		HSSFCell cell = addCell();
		cell.setCellStyle( getDecimalCellStyle() );
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}
	
	public HSSFCell addBooleanCell( boolean value ) {
		HSSFCell cell = addCell();
		cell.setCellValue( value );
		cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
		return cell;
	}

	private CellStyle getDateCellStyle() {
		if ( dateCellStyle == null ) {
			dateCellStyle = wb.createCellStyle();
			dateCellStyle.setDataFormat( dataFormat.getFormat(DATE_PATTERN) );
		}
		return dateCellStyle;
	}	
	
	public HSSFCell addDateCell( Date value ) {
		HSSFCell cell = addCell();
		if (value==null) {
			cell.setCellValue("");	
		} else {
			cell.setCellValue(value);
		}
		cell.setCellStyle(getDateCellStyle());
		return cell;
	}
	

	@Override
	public Object exportColumn(ReportColumnMetadata column, Object data) throws ReportException {
		HSSFCell cell = null;
		if (column.getType() == Types.VARCHAR 
				|| column.getType() == Types.CHAR 
				|| column.getType() == Types.LONGVARCHAR) {
			cell = addStringCell((String) data); 
		} else if (column.getType() == Types.INTEGER || column.getType() == Types.TINYINT || column.getType() == Types.SMALLINT ) {
			double d = data==null?0.0: ((Integer) data).doubleValue();
			cell = addNumberCell( d );
		} else if ( column.getType() == Types.BIT) {
			cell = addCell();
			if (data != null) {
				cell.setCellValue((Boolean) data);
			}
			
		} else if (column.getType() == Types.DATE ) {
			cell = addDateCell( (Date)data );
		} else if (column.getType() == Types.TIMESTAMP) {
			cell = addCell();
			if (data != null) {
				Calendar c = Calendar.getInstance();
				c.setTime( (Date) data );
				cell.setCellValue(c);
			}
		} else if (column.getType() == Types.DOUBLE) {
			if (data != null) {
				cell = addDecimalCell( (Double) data );	
			} else {
				cell = addEmptyDecimalCell( );
			}
			
		} else if (column.getType() == Types.LONGVARBINARY) {
			cell = addCell();
			cell.setCellValue("BLOB");
		} else {
			throw new ReportException(" Tipo no soportado para " + column.getName() + " (" +  column.getType() + ")");	
		}
		return cell;
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
