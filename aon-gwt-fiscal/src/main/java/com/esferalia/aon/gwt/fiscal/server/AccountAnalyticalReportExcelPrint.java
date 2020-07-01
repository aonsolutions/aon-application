package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "AccountAnalytical Report Excel Print", urlPatterns = { "/aon_gwt_fiscal/roms/AccountAnalyticalReportExcelPrint" })
public class AccountAnalyticalReportExcelPrint extends HttpServlet {
	         
	private static final long serialVersionUID = -4737903276711035815L;
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountReportParams = req.getParameter( IRequestParamsNames.ACCOUNT_REPORT_PARAMS );
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			AccountingReportParams params = JsonParser.parseAccountingParams(accountReportParams);

			AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
			Company company = config.getCompany();
			String companyName = company == null ? "" : company.getName();
			AccountingAnalyticalReport report = ACCOUNTING.getAccountAnalyticalReport(domainName, user, domainId, params); 
			ExcelAction action = new ExcelAction( companyName, report );
			action.initialize("Cuenta de explotaci\u00F3n");
			report.getAccounts()
				.stream()
				.forEach(action)						
			;
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"PyG."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<AccountOperatingAccount>{
		private XSSFCellStyle titleCellStyle;
		private CellStyle wrappedCellStyle;
		
		private AccountingAnalyticalReport report;
		private String companyName; 
		
		public ExcelAction(String companyName, AccountingAnalyticalReport report2) {
			super();
			this.companyName = companyName;
			this.report = report2;
		}

		@Override
		protected void headerRow() {
			int columnsPerColumn = 2;
			int columns = 1 + (report.getColumns().size() * columnsPerColumn);
			
			sheet.setDisplayZeros(false);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape( columns > 5);
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Generado el &D");
			footer.setRight("P\u00E1g: &P/&N");
			
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			wrappedCellStyle = workbook.createCellStyle();
			wrappedCellStyle.cloneStyleFrom(defaultStyle);
			wrappedCellStyle.setWrapText(true);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HorizontalAlignment.CENTER );
			headerStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerStyle.setFont(journalHeaderFont);
		    
		    XSSFCellStyle columnHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
		    columnHeaderStyle.cloneStyleFrom(headerStyle);
		    columnHeaderStyle.setBorderTop(BorderStyle.THIN);
		    columnHeaderStyle.setBorderBottom(BorderStyle.THIN);
		    columnHeaderStyle.setBorderLeft(BorderStyle.THIN);
		    columnHeaderStyle.setBorderRight(BorderStyle.THIN);
		    columnHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    columnHeaderStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    
		    
			Font titleFont= workbook.createFont();
			titleFont.setFontHeightInPoints((short) 10);
			titleFont.setBold(true);

			XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
		    titleStyle.cloneStyleFrom(headerStyle);
		    titleStyle.setFont(titleFont);
		    titleStyle.setBorderBottom(BorderStyle.NONE);
		    
			Font subTitleFont= workbook.createFont();
			subTitleFont.setFontHeightInPoints((short) 8);
			subTitleFont.setBold(true);
			
			XSSFCellStyle subTitleStyle = (XSSFCellStyle) workbook.createCellStyle();
			subTitleStyle.cloneStyleFrom(headerStyle);
			subTitleStyle.setFont(subTitleFont);
			subTitleStyle.setBorderBottom(BorderStyle.NONE);

			Font smallBoldFont= workbook.createFont();
			smallBoldFont.setFontHeightInPoints((short) 8);
			smallBoldFont.setBold(true);

			titleCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			titleCellStyle.cloneStyleFrom(decimalStyle);
			titleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			titleCellStyle.setFont(smallBoldFont);
			titleCellStyle.setWrapText(true);
			
			sheet.setColumnWidth(0, 9 * 256);
			sheet.setColumnWidth(1, (columns>=9?30:40) * 256);
			
			
			// ----------------------------------------------- ROW 1 - Company
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			CellUtil.createCell(row, 0, companyName, titleStyle);
			row.setHeight((short) 500);
			++rowCount;

			// ----------------------------------------------- ROW 2 - Title
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			String title = "CUENTA DE EXPLOTACI\u00D3N ANAL\u00CDTICA";
			if (report.getParams().getSelectedPeriod() != null) {
				title = title + " - " + report.getParams().getSelectedPeriod().getName();
			}
			CellUtil.createCell(row, 0, title, titleStyle);
			row.setHeight((short) 500);
			++rowCount;
			
			// ----------------------------------------------- ROW 3 - Activity
			String activityDescription = null;
			if (report.getParams().getActivity() != null && report.getParams().getActivity() < 0) {
				activityDescription = "Actividad: Sin Actividad";
			}
			if (report.getParams().getSelectedActivity() != null) {
				activityDescription = "Actividad: " + report.getParams().getSelectedActivity().getDescription();
			}
			if (AonStringUtils.isNotBlank(activityDescription)) {
				row = sheet.createRow(rowCount);
				mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
				CellUtil.createCell(row, 0, activityDescription, subTitleStyle);
				++rowCount;
			}
			
			// ----------------------------------------------- ROW 5 - Dates
			StringBuilder buf = new StringBuilder();
			AccountingReportParams params = report.getParams();
			if (params.getFromDate()  != null) {
				buf.append("Desde el ");
				buf.append(FORMATTER.format( params.getFromDate() ));
			}
			if (params.getToDate()  != null) {
				buf.append(" hasta el ");
				buf.append(FORMATTER.format( params.getToDate() ));
			}
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			CellUtil.createCell(row, cellCount, buf.toString(), subTitleStyle);
			++rowCount;
			
			// ----------------------------------------------- ROW 6 - Intervals
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, 1);
			CellUtil.createCell(row, cellCount, "", headerStyle);
			cellCount = 2;
			for (AccountingAnalyticalColumn column : report.getColumns()) {
				if (!column.isTotalColumn()) {
					mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+columnsPerColumn-1);
				}
				CellUtil.createCell(row, cellCount, column.getName(), columnHeaderStyle);
				CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
				cellCount = cellCount + columnsPerColumn-1;
				if (!column.isTotalColumn()) {
					cellCount++;
				}
			}
			++rowCount;
			
			// ----------------------------------------------- ROW 7 - Co9lumn Header
			row = sheet.createRow(rowCount);
			cellCount = 0;
			CellUtil.createCell(row, cellCount, "CUENTA", columnHeaderStyle);
			CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
			mergeHeaderRegion(rowCount, rowCount, 0, 1);
			//CellUtil.createCell(row, cellCount, "", columnHeaderStyle);
			cellCount = 2;
			for (AccountingAnalyticalColumn column : report.getColumns()) {
				if (!column.isTotalColumn()) {
					CellUtil.createCell(row, cellCount, "%", columnHeaderStyle);
					sheet.setColumnWidth(cellCount++, 6 * 256);
				}
				
				CellUtil.createCell(row, cellCount, "Saldo", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
			++rowCount;
			sheet.setRepeatingRows(new CellRangeAddress(0, rowCount - 1 , 0, columns-1));
		}

		private CellRangeAddress mergeHeaderRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
			CellRangeAddress range = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(range);
			return range;
		}

		@Override
		public void accept(AccountOperatingAccount account) {
			
			row = sheet.createRow(rowCount++);
			boolean title = account.getId() == null;
			cellCount = 0;
			
			String label = (title?"":account.getCode()) + " ";
			Cell cell = addCell(label);
			cell.setCellStyle((title)?titleCellStyle:wrappedCellStyle);
			
			cell = addCell(account.getDescription());
			cell.setCellStyle((title)?titleCellStyle:wrappedCellStyle);
			
			for (AccountingAnalyticalColumn column : report.getColumns()) {
				AccountingAnalyticalStatement aos = report.get(account.getCode(),column);
				Double percent = (aos != null)?aos.getPercent() : 0.0;
				Double balance = (aos != null)?aos.getBalance() : 0.0;
				
				if (!column.isTotalColumn()) {
					if (!account.getType().isCalculated()) {
						cell = addCell(percent);
						if (title) cell.setCellStyle(titleCellStyle);
						balance = (aos != null)?aos.getAmount() : 0.0;	
					} else {
						cell = addEmptyCell();
					}
				}
				
				cell = addCell(balance);
				if (title) cell.setCellStyle(titleCellStyle);
			}
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
			
		}
}
