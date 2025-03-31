package com.esferalia.aon.in.payroll.excel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class AbsExcelAction  {
	
	protected static final String FONT_FAMILY = "DejaVu Sans Mono";
	protected static final String DATE_PATTERN = "dd/MM/yyyy";
	protected static final String DECIMAL_PATTERN = "#,##0.00";
	protected static final String NUMBER_PATTERN = "#,###";
	protected static final XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(186,186,186), new DefaultIndexedColorMap());
	protected static final XSSFColor AON_LIGHT_GRAY = new XSSFColor(new java.awt.Color(240,240,240),  new DefaultIndexedColorMap());
	protected static final XSSFColor AON_TEAL = new XSSFColor(new java.awt.Color(79,149,157), new DefaultIndexedColorMap());
	protected static final XSSFColor AON_LIGHT_TEAL = new XSSFColor(new java.awt.Color(152,210,192), new DefaultIndexedColorMap());
	protected static final XSSFColor AON_LIGHT_BLUE = new XSSFColor(new java.awt.Color(224,241,236), new DefaultIndexedColorMap());
	protected static final XSSFColor AON_WHITE = new XSSFColor(new java.awt.Color(255,255,255), new DefaultIndexedColorMap());
	protected static final XSSFColor AON_TOTAL = new XSSFColor(new java.awt.Color(182,223,210), new DefaultIndexedColorMap());
	
	protected SXSSFWorkbook workbook;
	protected SXSSFSheet sheet;
	protected Row row;
	protected int rowCount;
	protected int cellCount;
	protected DataFormat dataFormat;
	
	protected XSSFCellStyle headerCellStyle;
	
	protected XSSFCellStyle infoCellStyle;
	
	protected XSSFCellStyle totalStyle;
	protected XSSFCellStyle totalCenterStyle;
	
	protected XSSFCellStyle headerEvenMonthCellStyle;
	protected XSSFCellStyle headerOddMonthCellStyle;
	
	protected XSSFCellStyle headerEvenMonthCenterCellStyle;
	protected XSSFCellStyle headerOddMonthCenterCellStyle;
	
	protected XSSFCellStyle totalEvenMonthCellStyle;
	protected XSSFCellStyle totalOddMonthCellStyle;
	
	protected XSSFCellStyle totalEvenMonthCenterCellStyle;
	protected XSSFCellStyle totalOddMonthCenterCellStyle;
	
	protected XSSFCellStyle blankCellStyle;
	
	protected XSSFFont boldFont;
	
	public void initializeEmpty() {
		workbook = new SXSSFWorkbook(1);
	}
	
	public void createSheet(String name) {
		sheet = (SXSSFSheet) workbook.createSheet(name);
	    dataFormat = workbook.getCreationHelper().createDataFormat();
	    
	    rowCount = 0;
	    cellCount = 0;
		
		boldFont = (XSSFFont) workbook.createFont();
		boldFont.setColor(IndexedColors.BLACK.getIndex());
		boldFont.setBold(true);

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerCellStyle.setFillForegroundColor(AON_TEAL);
	    headerCellStyle.setFont(boldFont);
	    
	    infoCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    infoCellStyle.setAlignment( HorizontalAlignment.CENTER );
	    infoCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    infoCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    infoCellStyle.setFillForegroundColor(AON_LIGHT_GRAY);
	    infoCellStyle.setFont(boldFont);
	    
	    totalCenterStyle = (XSSFCellStyle) workbook.createCellStyle();
	    totalCenterStyle.setAlignment( HorizontalAlignment.CENTER );
	    totalCenterStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    totalCenterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    totalCenterStyle.setFillForegroundColor(AON_TOTAL);
	    
	    totalStyle = (XSSFCellStyle) workbook.createCellStyle();
	    totalStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    totalStyle.setFillForegroundColor(AON_TOTAL);
	    
	    headerEvenMonthCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    headerEvenMonthCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerEvenMonthCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerEvenMonthCellStyle.setFillForegroundColor(AON_LIGHT_TEAL);
	    
	    headerOddMonthCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    headerOddMonthCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerOddMonthCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerOddMonthCellStyle.setFillForegroundColor(AON_LIGHT_BLUE);
	    
	    headerEvenMonthCenterCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    headerEvenMonthCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerEvenMonthCenterCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerEvenMonthCenterCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerEvenMonthCenterCellStyle.setFillForegroundColor(AON_LIGHT_TEAL);
	    
	    headerOddMonthCenterCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    headerOddMonthCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerOddMonthCenterCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerOddMonthCenterCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerOddMonthCenterCellStyle.setFillForegroundColor(AON_LIGHT_BLUE);
	    
	    totalEvenMonthCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    totalEvenMonthCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    totalEvenMonthCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    totalEvenMonthCellStyle.setFillForegroundColor(AON_LIGHT_TEAL);
	    totalEvenMonthCellStyle.setFont(boldFont);
		
		totalOddMonthCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		totalOddMonthCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		totalOddMonthCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		totalOddMonthCellStyle.setFillForegroundColor(AON_LIGHT_BLUE);
		totalOddMonthCellStyle.setFont(boldFont);
	    	    
	    totalEvenMonthCenterCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    totalEvenMonthCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    totalEvenMonthCenterCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    totalEvenMonthCenterCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    totalEvenMonthCenterCellStyle.setFillForegroundColor(AON_LIGHT_TEAL);
	    totalEvenMonthCenterCellStyle.setFont(boldFont);
	    
	    totalOddMonthCenterCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    totalOddMonthCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    totalOddMonthCenterCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    totalOddMonthCenterCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    totalOddMonthCenterCellStyle.setFillForegroundColor(AON_LIGHT_BLUE);
	    totalOddMonthCenterCellStyle.setFont(boldFont);
	    
	    blankCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    blankCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    blankCellStyle.setFillForegroundColor(AON_WHITE);
	}
	
	protected Cell addEmptyCell() {
		return addCell("");
	}
	
	protected Cell addCell(String value) {
		Cell cell = row.createCell(cellCount++);
		cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
		cell.setCellType(CellType.STRING);
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
