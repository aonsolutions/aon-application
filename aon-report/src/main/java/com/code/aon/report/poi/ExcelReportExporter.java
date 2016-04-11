package com.code.aon.report.poi;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

import com.code.aon.report.ReportException;

public class ExcelReportExporter implements IReportExporter {
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	public static final String DECIMAL_PATTERN = "#,##0.00";
	public static final String NUMBER_PATTERN = "#,###";
	public  static short DEFAULT_BACKGROUND = new HSSFColor.AUTOMATIC().getIndex();

	private HSSFWorkbook workbook;
	private ExcelSheet sheet;
    private HSSFCellStyle headerCellStyle;
    private DataFormat dataFormat;
    private CellStyle numberCellStyle;
    private CellStyle decimalCellStyle;
    private CellStyle dateCellStyle;
    private Map<String, ExcelSheet> sheetMap;

	public void startExport(String name) throws ReportException {
	    workbook = new HSSFWorkbook();
	    setSheet(createSheet(name));
        dataFormat = workbook.getCreationHelper().createDataFormat();
	    headerCellStyle = createHeaderStyle();
	}
	
	public ExcelSheet createSheet( String name ) throws ReportException {
		if(workbook==null){
			throw new ReportException(" The workbook is not created. Start it first!");
		}
		if(sheetMap==null){
			sheetMap = new HashMap<String, ExcelSheet>();
		}
		sheetMap.put(name, new ExcelSheet(workbook, name));
		return sheetMap.get(name);
	}
	
	public ExcelSheet getSheet() {
		return sheet;
	}

	public void setSheet(ExcelSheet sheet) {
		this.sheet = sheet;
	}
	
	public void restoreSheet(String name) throws ReportException {
		if(sheetMap.containsKey(name)){
			setSheet(sheetMap.get(name));
		} else {
			throw new ReportException(" The specified sheet does not exist. Create it first!");
		}
	}

	public HSSFCellStyle createHeaderStyle() {
		HSSFCellStyle headerCellStyle = createCellStyle();
	    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
	    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	    headerCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		return headerCellStyle;
	}

	public void exportHeader(ReportMetadata metadata) throws ReportException {
		exportHeader(metadata, headerCellStyle);
	}
	public void exportHeader(ReportMetadata metadata,HSSFCellStyle cellStyle) throws ReportException {
		addHeaderRow();
        for (int i=1; i<(metadata.getCount()+1); i++) {
			ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
			int width = getWidth( columnMetadata.getDisplaySize() );
			addHeaderCell(columnMetadata.getLabel(), width,cellStyle);
		}
	}

	public int getWidth(int characters) {
		int width = ((characters + 2) * 256);
		width = width < 512 ? 512 : width;
		return width > (255 * 255) ? (255 * 255) : width;
	}

	public void addHeaderRow() {
		this.sheet.addHeaderRow();
	}
	
	public void addHeaderCell(String label) {
		addHeaderCell(label, 0);
	}
	
	public void addHeaderCell(String label, int width) {
		addHeaderCell(label, width, headerCellStyle);
	}

	public void addHeaderCell(String label, int width, HSSFCellStyle cellStyle) {
		this.sheet.addHeaderCell(label, width, cellStyle);
	}

	public HSSFCell addCell() {
		return addCell(null);
	}

	public HSSFCell addCell(HSSFCellStyle cellStyle) {
		return this.sheet.addCell(cellStyle);
	}
	
	public HSSFCell addStringCell(String value,HSSFCellStyle cellStyle) {
		HSSFCell cell = addStringCell(value);
		cell.setCellStyle(cellStyle);
		return cell;
	}

	public HSSFCell addStringCell(String value) {
		HSSFCell cell = addCell();
		cell.setCellValue(value);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell;
	}

	public HSSFCell addNumberCell(double value,HSSFCellStyle cellStyle) {
		HSSFCell cell = addNumberCell(value);
		cell.setCellStyle(cellStyle);
		return cell;
	}
	
	public HSSFCell addNumberCell(double number) {	
		HSSFCell cell = addCell();
		cell.setCellValue(number);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public HSSFCell addEmptyNumberCell() {
		HSSFCell cell = addCell();
		cell.setCellStyle(getNumberCellStyle());
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public HSSFCell addDecimalCell(double number) {
		return addDecimalCell(number,getDecimalCellStyle());
	}
	
	public HSSFCell addDecimalCell(double number,CellStyle cellStyle) {
		HSSFCell cell = addCell();
		cell.setCellValue(number);
		cell.setCellStyle(cellStyle);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public HSSFCell addEmptyDecimalCell() {
		HSSFCell cell = addCell();
		cell.setCellStyle(getDecimalCellStyle());
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		return cell;
	}

	public HSSFCell addDateCell(Date value) {
		return addDateCell(value,getDateCellStyle());
	}

	public HSSFCell addDateCell(Date value,CellStyle style) {
		HSSFCell cell = addCell();
		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
		cell.setCellStyle(style);
		return cell;
	}

	public HSSFCell addBooleanCell(boolean value) {
		HSSFCell cell = addCell();
		cell.setCellValue(value);
		cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
		return cell;
	}

	private CellStyle getNumberCellStyle() {
		if (numberCellStyle == null) {
			numberCellStyle = createCellStyle();
			numberCellStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
		}
		return numberCellStyle;
	}

	private CellStyle getDecimalCellStyle() {
		if (decimalCellStyle == null) {
			decimalCellStyle = createCellStyle();
			decimalCellStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
		}
		return decimalCellStyle;
	}

	private CellStyle getDateCellStyle() {
		if (dateCellStyle == null) {
			dateCellStyle = createCellStyle();
			dateCellStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
		}
		return dateCellStyle;
	}	

    public HSSFCellStyle createCellStyle() {
    	return workbook.createCellStyle();	
    }

    public DataFormat getDataFormat() {
    	return dataFormat;	
    }

    public HSSFFont createFont() {
    	return workbook.createFont();	
    }

	public void startLine() {
		this.sheet.startLine();
	}

	public Object exportColumn(ReportColumnMetadata column, Object data) throws ReportException {
		HSSFCell cell = null;
		if (column.getType() == Types.VARCHAR || column.getType() == Types.CHAR || column.getType() == Types.LONGVARCHAR) {
			cell = addStringCell((String)data); 
		} else if (column.getType() == Types.INTEGER || column.getType() == Types.TINYINT || column.getType() == Types.SMALLINT) {
			if (data != null) {
				cell = addNumberCell(((Integer)data).doubleValue());
			} else {
				cell = addEmptyNumberCell();
			}
		} else if (column.getType() == Types.DOUBLE) {
			if (data != null) {
				cell = addDecimalCell((Double)data);	
			} else {
				cell = addEmptyDecimalCell();
			}
		} else if (column.getType() == Types.DATE) {
			cell = addDateCell((Date)data);
		} else if (column.getType() == Types.TIMESTAMP) {
			cell = addCell();
			if (data != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime((Date)data);
				cell.setCellValue(calendar);
			}
		} else if (column.getType() == Types.BIT) {
			cell = addCell();
			if (data != null) {
				cell.setCellValue((Boolean)data);
			}
		} else if (column.getType() == Types.LONGVARBINARY) {
			cell = addCell();
			cell.setCellValue("BLOB");
		} else {
			throw new ReportException(" Tipo no soportado para " + column.getName() + " (" +  column.getType() + ")");	
		}
		return cell;
	}

	public void addMergedRegion(int fromRow, int toRow, int fromCell, int toCell) {
		this.sheet.addMergedRegion(fromRow, toRow, fromCell, toCell);
	}

	public void endLine() {
	}

	public void autoSizeColumns() {
		this.sheet.autoSizeColumns();
	}

	public void endExport(OutputStream out) throws ReportException {
		try {
			workbook.write(out);
		} catch (IOException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

}
