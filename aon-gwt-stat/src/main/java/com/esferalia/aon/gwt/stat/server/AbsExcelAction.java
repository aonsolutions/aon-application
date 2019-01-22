package com.esferalia.aon.gwt.stat.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class AbsExcelAction  {
	
	protected static final String FONT_FAMILY = "DejaVu Sans Mono";
	protected static final String DATE_PATTERN = "dd/MM/yyyy";
	protected static final String DECIMAL_PATTERN = "#,##0.00";
	protected static final String PERCENT_PATTERN = "##0.0000%";
	protected static final String NUMBER_PATTERN = "#,###";
	protected static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,114,207));
	
	protected XSSFWorkbook workbook;
	protected XSSFSheet sheet;
	//protected Row row;
	protected int rowCount;
	protected int cellCount;
	protected DataFormat dataFormat;
	protected CellStyle dateStyle;
	protected CellStyle decimalStyle;
	protected CellStyle percentStyle;
	protected CellStyle numberStyle;
	protected CellStyle centerCellStyle;
	protected XSSFCellStyle headerCellStyle;
	protected XSSFCellStyle totalCellStyle;
	protected Font boldFont;
	protected Font defaulFont;	
	protected Font smallFont;
	
	public void initialize(String name) {
		initialize(name,true);
	}
    
	public void initialize(String name, boolean printHeaders) {
		workbook = new XSSFWorkbook();
		
		
	    sheet = (XSSFSheet) workbook.createSheet(name);
	    dataFormat = workbook.getCreationHelper().createDataFormat();
	    rowCount = 0;
	    cellCount = 0;
	    dateStyle = workbook.createCellStyle();
	    dateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
	    dateStyle.setAlignment( HorizontalAlignment.CENTER );
	    
	    numberStyle = workbook.createCellStyle();
	    numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
	    numberStyle.setAlignment( HorizontalAlignment.CENTER );
	     
	    decimalStyle = workbook.createCellStyle();
	    decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	    decimalStyle.setAlignment( HorizontalAlignment.RIGHT );

	    percentStyle = workbook.createCellStyle();
	    percentStyle.setDataFormat(dataFormat.getFormat(PERCENT_PATTERN));
	    percentStyle.setAlignment( HorizontalAlignment.RIGHT );

		centerCellStyle = workbook.createCellStyle();
		centerCellStyle.setAlignment( HorizontalAlignment.CENTER );

		defaulFont= workbook.createFont();
		defaulFont.setFontHeightInPoints((short) 9);
		
		smallFont = workbook.createFont();
		smallFont.setFontHeightInPoints((short) 8);

		boldFont= workbook.createFont();
		boldFont.setFontHeightInPoints((short) 9);
		boldFont.setBold(true);

		Font headerFont= workbook.createFont();
		headerFont.setBold(true);
		headerFont.setColor( IndexedColors.WHITE.index );

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
	    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerCellStyle.setFillForegroundColor(AON_BLUE);
	    headerCellStyle.setFont(headerFont);
	    
		totalCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		totalCellStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	    totalCellStyle.setAlignment( HorizontalAlignment.RIGHT );
		totalCellStyle.setFont(boldFont);
	    
	    if (printHeaders) {
	    	headerRow();
	    }
	}
	
	protected Cell alignCenter(Cell cell) {
		cell.setCellStyle( centerCellStyle );
		return cell;
	}

	
//	protected Cell addCell(String value) {
//		Cell cell = row.createCell(cellCount++);
//		cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
//		cell.setCellType(CellType.STRING);
//		return cell;
//	}
//
//	protected Cell addCell(Enum<?> value) {
//		Cell cell = row.createCell(cellCount++);
//		if ( value != null ) {
//			cell.setCellValue(value.toString());
//		}
//		cell.setCellType(CellType.STRING);
//		return cell;
//	}
//
//	protected Cell addCell(Short value) {
//		Cell cell = row.createCell(cellCount++);
//		cell.setCellStyle(numberStyle);
//		if ( value != null ) {
//			cell.setCellValue(value);
//		}
//		cell.setCellType(CellType.NUMERIC);
//		return cell;
//	}
//	
//	protected Cell addCell(Date value) {
//		Cell cell = row.createCell(cellCount++);
//		cell.setCellStyle(dateStyle);
//		if ( value != null ) {
//			cell.setCellValue(value);
//		}
//		return cell;
//	}
//
//	protected Cell addCell(Double number) {
//		Cell cell = row.createCell(cellCount++);
//		cell.setCellStyle(decimalStyle);
//		cell.setCellValue(number!=null?number:0.0);
//		cell.setCellType(CellType.NUMERIC);
//		return cell;
//	}

	public void finalize(OutputStream out) throws IOException {
		workbook.write(out);
	}

	protected abstract void headerRow();
	
	private static class AONTempFileCreationStrategy implements TempFileCreationStrategy {

        /** The directory where the temporary files will be created (<code>null</code> to use the default directory). */
        private File dir;

        @Override
		public File createTempFile(String prefix, String suffix) throws IOException {
            // Identify and create our temp dir, if needed
        	
            if (dir == null || !dir.canWrite()) {
                dir = new File(System.getProperty("java.io.tmpdir"), "poifiles");
                dir.mkdir();
                if (System.getProperty("poi.keep.tmp.files") == null)
                    dir.deleteOnExit();
            }

            // Generate a unique new filename 
            File newFile = File.createTempFile(prefix, suffix, dir);

            // Set the delete on exit flag, unless explicitly disabled
            if (System.getProperty("poi.keep.tmp.files") == null)
                newFile.deleteOnExit();

            // All done
            return newFile;
		}
		
        @Override
        public File createTempDirectory(String prefix) throws IOException {
        	// TODO Auto-generated method stub
            File dir = new File(System.getProperty("java.io.tmpdir"), prefix);
            dir.mkdir();
            if (System.getProperty("poi.keep.tmp.files") == null)
                dir.deleteOnExit();
            return dir;
        }
	}
	
	static {
		TempFile.setTempFileCreationStrategy(new AONTempFileCreationStrategy());
	}
	
	 
}
