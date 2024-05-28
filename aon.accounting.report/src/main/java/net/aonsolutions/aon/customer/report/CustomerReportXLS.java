package net.aonsolutions.aon.customer.report;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class CustomerReportXLS  {

	private final ReportMetadata metadata;

	public CustomerReportXLS(Occam occam) {
		AonConfiguration config = AON.getConfiguration(occam);
		this.metadata = new ReportMetadata()
			.setCompanyName(Optional.ofNullable(config.getCompany()).map(c -> c.getName()).orElse(""));

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<CustomerFull>{
		
		private String companyName;
		
		@Override
		protected void headerRow() {
			XSSFCellStyle entryHeaderStyle;
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			Header header = sheet.getHeader();
			header.setLeft("&B" + companyName);
			header.setRight("&B&D");	
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerStyle.setFont(journalHeaderFont);
		    headerStyle.setBorderTop(BorderStyle.THIN);
		    headerStyle.setBorderRight(BorderStyle.THIN);
		    headerStyle.setBorderLeft(BorderStyle.THIN);
		    headerStyle.setBorderBottom(BorderStyle.THIN);
		    
		    
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment( HorizontalAlignment.CENTER );
			entryHeaderStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);

			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			CellUtil.createCell(row, 0, "LISTADO DE CLIENTES", headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 6));
			row.setHeight((short) 500);
			++rowCount;
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "C\u00F3digo", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "N.I.F.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 30 * 256);

			CellUtil.createCell(row, cellCount, "Nombre", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);

			CellUtil.createCell(row, cellCount, "Alias", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "Tel\u00E9fono", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "Estado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);
	
			sheet.setRepeatingRows(new CellRangeAddress(0, 1, 0, 6));
		}


		
		@Override
		public void accept(CustomerFull customer) {
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(customer.getId().toString());
			addCell(customer.getRegistry().getDocument());
			addCell(customer.getRegistry().getName());
			addCell(customer.getRegistry().getAlias());
			addCell(
				AonCollectionUtils.stream(  customer.getMedias() )
					.map( rm -> rm.getValue())
					.findFirst()
					.orElse(null));
			addCell(
				Optional.ofNullable(customer.getRegistry().getStatus())
					.map(rs -> rs.getDescription())
					.orElse( "" ));
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}


		
	}
}
