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
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "AccountBalanceReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountBalanceReportExcelPrint" })
public class AccountBalanceReportExcelPrint extends HttpServlet {

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
			AccountBalanceReport report = ACCOUNTING.getAccountBalanceReport(domainName, domainId, user, params);
			ExcelAction action = new ExcelAction( companyName, report );
			action.initialize("Balance de situación");
			if (!report.isEmpty()) {
				report.getBalances()
					.keySet()
					.stream()
					.forEach(action);
			}
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Balance_Situacion."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private class ExcelAction extends AbsExcelAction implements Consumer<String>{
		private XSSFCellStyle titleCellStyle;
		private CellStyle wrappedCellStyle;
		private CellStyle wrappedBoldCellStyle;
		private CellStyle wrappedItalicCellStyle;
		
		private AccountBalanceReport report;
		private String companyName; 
		
		public ExcelAction(String companyName, AccountBalanceReport report) {
			super();
			this.companyName = companyName;
			this.report = report;
		}

		@Override
		protected void headerRow() {
			int columns = 1 + report.getPeriods().size() + (report.getParams().isBreakdownEnabled()?2:0);
			
			sheet.setDisplayZeros(false);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape( false );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Generado el &D");
			footer.setRight("P\u00E1g: &P/&N");
			
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			CellStyle defaultBodlStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallBoldFont);

			wrappedCellStyle = workbook.createCellStyle();
			wrappedCellStyle.cloneStyleFrom(defaultStyle);
			wrappedCellStyle.setWrapText(true);
			
			wrappedBoldCellStyle = workbook.createCellStyle();
			wrappedBoldCellStyle.cloneStyleFrom(defaultBodlStyle);
			wrappedBoldCellStyle.setWrapText(true);

			wrappedItalicCellStyle = workbook.createCellStyle();
			wrappedItalicCellStyle.cloneStyleFrom(defaultBodlStyle);
			wrappedItalicCellStyle.setFont(italicSmallFont);
			wrappedItalicCellStyle.setWrapText(true);

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
			
			// ----------------------------------------------- ROW 1 - Company
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			CellUtil.createCell(row, 0, companyName, titleStyle);
			row.setHeight((short) 500);
			++rowCount;

			// ----------------------------------------------- ROW 2 - Title
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			String title = AonStringUtils.upperCase( report.getParams().getBalanceType().getName());
			if (report.getSelectedPeriod() != null) {
				title = title + " - " + report.getSelectedPeriod().getName();
			}
			CellUtil.createCell(row, 0, title, titleStyle);
			row.setHeight((short) 500);
			++rowCount;
			
			// ----------------------------------------------- ROW 3 - Activity
			String activityDescription = null;
			if (report.getParams().getActivity() != null && report.getParams().getActivity() < 0) {
				activityDescription = "Actividad: Sin Actividad";
			}
			if (report.getSelectedActivity() != null) {
				activityDescription = "Actividad: " + report.getSelectedActivity().getDescription();
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
			sheet.setColumnWidth(0, (report.getParams().isBreakdownEnabled()?40:50) * 256);
			CellUtil.createCell(row, cellCount, "", headerStyle);
			cellCount = 1;
			if (report.getParams().isBreakdownEnabled()) {
				CellUtil.createCell(row, cellCount, "S.Deudor", columnHeaderStyle);
				sheet.setColumnWidth(cellCount, 12 * 256);
				++cellCount;
				
				CellUtil.createCell(row, cellCount, "S.Acreedor", columnHeaderStyle);
				sheet.setColumnWidth(cellCount, 12 * 256);
				++cellCount;
			}
			for (String period : report.getPeriods()) {
				CellUtil.createCell(row, cellCount, period, columnHeaderStyle);
				sheet.setColumnWidth(cellCount, 12 * 256);
				++cellCount;
			}
			++rowCount;
			
			sheet.setRepeatingRows(new CellRangeAddress(0, rowCount - 1 , 0, columns-1));
		}

		private CellRangeAddress mergeHeaderRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
			CellRangeAddress secondRowRange = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(secondRowRange);
			return secondRowRange;
		}

		@Override
		public void accept(String key) {
			BalanceLine bal = report.getBalances().get(key);
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			Cell cell = addCell(bal.getPrefix() + ".- " + bal.getDescription());
			
			if (bal.isLeaf()) {
				cell.setCellStyle(wrappedBoldCellStyle);
			} else if (bal.isBreakdown()) {
				cell.setCellStyle(wrappedItalicCellStyle);
			}else {
				cell.setCellStyle(wrappedCellStyle);
			}
			
			if (report.getParams().isBreakdownEnabled()) {
				AccountBalance b = bal.getBreakdown();
				cell = addCell(b!=null?b.getDebitBalance():0);
				cell.setCellStyle(wrappedItalicCellStyle);
				cell = addCell(b!=null?b.getCreditBalance():0);
				cell.setCellStyle(wrappedItalicCellStyle);
			}
			for (String period : report.getPeriods()) {
				addCell(bal.getAmounts().get(period));
			}
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
	}
}
