package com.code.aon.ui.report.export;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;

import com.code.aon.report.ReportException;


public class ExcelReportExporter implements IReportExporter {
	
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	DateFormat timestampFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	DecimalFormat numberFormat = new DecimalFormat("#,##0.000");
    HSSFWorkbook wb;
    HSSFSheet sheet;
    HSSFCellStyle headerCellStyle;
    HSSFRow row;
    int rowCount;
    int cellCount;
    
	@Override
	public void setCallBack(IExporterCallBack callBack) {
		
	}

	@Override
	public void exportHeader(OutputStream out,ReportMetadata metadata) throws ReportException {
        sheet.setDefaultColumnWidth(50);
        
        HSSFRow row = sheet.createRow(rowCount++);
        cellCount = 0;
        for (int i = 1; i < (metadata.getCount() + 1); i++) {
			ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
			int width = ((columnMetadata.getDisplaySize() + 2)*256);
			width = width < 512 ? 512 : width;
			width = width > (255 * 255) ? (255 * 255) : width;
			sheet.setColumnWidth(cellCount, width);
			HSSFCellUtil.createCell(row, cellCount++, columnMetadata.getLabel(), headerCellStyle);
		}
	}

	@Override
	public void exportColumn(OutputStream out, ReportColumnMetadata column, Object data) throws ReportException {
		if (column.getType() == Types.VARCHAR 
				|| column.getType() == Types.CHAR 
				|| column.getType() == Types.LONGVARCHAR) {
			row.createCell(cellCount++).setCellValue((String) data);
		} else if (column.getType() == Types.INTEGER || column.getType() == Types.TINYINT || column.getType() == Types.SMALLINT ) {
			double d = data==null?0.0: ((Integer) data).doubleValue();
			row.createCell(cellCount++).setCellValue(d);
		} else if ( column.getType() == Types.BIT) {
			HSSFCell cell = row.createCell(cellCount++);
			if (data != null) {
				cell.setCellValue((Boolean) data);
			}
		} else if (column.getType() == Types.DATE ) {
			row.createCell(cellCount++).setCellValue((Date)data);
		} else if (column.getType() == Types.TIMESTAMP) {
			HSSFCell cell = row.createCell(cellCount++);
			if (data != null) {
				Calendar c = Calendar.getInstance();
				c.setTime( (Date) data );
				cell.setCellValue(c);
			}
		} else if (column.getType() == Types.DOUBLE) {
			row.createCell(cellCount++).setCellValue((Double) data);
		} else if (column.getType() == Types.LONGVARBINARY) {
			row.createCell(cellCount++).setCellValue("BLOB");
		} else {
			throw new ReportException(" Tipo no soportado para " + column.getName() + " (" +  column.getType() + ")");	
		}
	}

	@Override
	public void startExport(OutputStream out) throws ReportException {
	    wb = new HSSFWorkbook();
	    sheet = wb.createSheet("Listado");
	    headerCellStyle = wb.createCellStyle();
	    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
	    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	    headerCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
	}

	@Override
	public void startLine(OutputStream out) throws ReportException {
		row = sheet.createRow(rowCount++);
		cellCount = 0;		
	}

	@Override
	public void endLine(OutputStream out) throws ReportException {
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
