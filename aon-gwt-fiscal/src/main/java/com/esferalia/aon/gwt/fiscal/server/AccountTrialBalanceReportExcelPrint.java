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
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "AccountTrialBalanceReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountTrialBalanceReportExcelPrint" })
public class AccountTrialBalanceReportExcelPrint extends HttpServlet {

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
			AccountingReportParams params = parseParams(accountReportParams);

			AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
			Company company = config.getCompany();
			String companyName = company == null ? "" : company.getName();
			AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(domainName, domainId, user, params);
			ExcelAction action = new ExcelAction( companyName, report );
			action.initialize("Balance de sumas y saldos");
			if (!report.isEmpty()) {
				report.getBalances()
					.keySet()
					.stream()
					.forEach(action);
				action.totals(report.getTotalBalance());
			}
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Balance_SyS."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private AccountingReportParams parseParams(String accountReportParams) throws ParseException, java.text.ParseException {
		AccountingReportParams params = new AccountingReportParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(accountReportParams);
		
		// ******************* DOMAIN ******************* 
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
		params.setDomain(domain.intValue());

		// ******************* PERIOD ******************* 
		Long period = (Long) jsonParams.get(IRequestParamsNames.PERIOD);
		if (period != null) {
			params.setPeriod(period.intValue());	
		}
		// ******************* FROMDATE ******************* 
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));			
		}
		// ******************* TODATE ******************* 
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		// ******************* ACCOUNT ******************* 
		Long accountId = (Long) jsonParams.get(IRequestParamsNames.ACCOUNT);
		if (accountId != null) {
			params.setAccount( new Account().setId(accountId.intValue()));
		}
		String accountCode = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_CODE);
		if (AonStringUtils.isNotBlank(accountCode)) {
			if (params.getAccount() == null) params.setAccount( new Account());
			params.getAccount().setCode(accountCode);
		}
		String accountDescription = (String) jsonParams.get(IRequestParamsNames.ACCOUNT_DESCRIPTION);
		if (AonStringUtils.isNotBlank(accountDescription)) {
			if (params.getAccount() == null) params.setAccount( new Account());
			params.getAccount().setDescription(accountDescription);
		}
		// ******************* LEVEL ******************* 
		Long level = (Long) jsonParams.get(IRequestParamsNames.LEVEL);
		if (level != null) {
			params.setLevel(level.intValue());	
		}
		// ******************* ACTIVITY ******************* 
		Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity.intValue());	
		}
		// ******************* SECURITYLEVEL ******************* 
		Long confidential = (Long) jsonParams.get(IRequestParamsNames.CONFIDENTIAL);
		if (confidential != null) {
			params.setSecurityLevel( SecurityLevel.safeValueOf( confidential.intValue() ));
		}
		// ******************* DOCUMENTNUMBER ******************* 
		String document = (String) jsonParams.get(IRequestParamsNames.DOCUMENT);
		if (document != null) {
			params.setDocumentNumber(document);	
		}
		// ******************* PREVIOUSPERIODS ******************* 
		Long previousPeriods = (Long) jsonParams.get(IRequestParamsNames.PREVIOUS_PERIODS);
		if (previousPeriods != null) {
			params.setPreviousPeriods(previousPeriods.intValue());	
		}
		// ******************* LOWLEVELACCOUNTVISIBLE ******************* 
		Long lowLevelAccountVisible = (Long) jsonParams.get(IRequestParamsNames.LOW_LEVEL_ACCOUNT_VISIBLE);
		if (lowLevelAccountVisible != null) {
			params.setLowLevelAccountVisible(lowLevelAccountVisible.intValue() == 1);	
		}
		// ******************* NOACTIVITYACCOUNTVISIBLE ******************* 
		Long noActivityAccountVisible = (Long) jsonParams.get(IRequestParamsNames.NO_ACTIVITY_ACCOUNT_VISIBLE);
		if (noActivityAccountVisible != null) {
			params.setNoActivityAccountVisible(noActivityAccountVisible.intValue() == 1);	
		}
		// ******************* percentsEnabled ******************* 
		Long percentsEnabled = (Long) jsonParams.get(IRequestParamsNames.PERCENTS_ENABLED);
		if (percentsEnabled != null) {
			params.setPercentsEnabled(percentsEnabled.intValue() == 1);	
		}
		// *******************  BYMONTH ******************* 
		Long byMonth = (Long) jsonParams.get(IRequestParamsNames.BY_MONTH);
		if (byMonth != null) {
			params.setByMonth(byMonth.intValue() == 1);	
		}
		// *******************  COSTCENTERS *******************
		JSONArray costCenters = (JSONArray) jsonParams.get(IRequestParamsNames.COST_CENTERS);
		if (costCenters != null && costCenters.size() > 0) {
			for (int i = 0; i < costCenters.size(); i++) {
				params.addCostCenter(costCenters.get(i).toString());
			}
		}
		return params;
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<String>{
		private XSSFCellStyle titleCellStyle;
		private CellStyle wrappedCellStyle;
		
		private AccountTrialBalanceReport report;
		private String companyName; 
		
		public ExcelAction(String companyName, AccountTrialBalanceReport report) {
			super();
			this.companyName = companyName;
			this.report = report;
		}

		@Override
		protected void headerRow() {
			int columns = 6
				+ (report.hasBeforePeriodAmounts()?2:0)
				+ (report.hasOpeningAmounts()?2:0)
				+ (report.hasInPeriodPreviousAmounts()?2:0)
			;
			sheet.setDisplayZeros(false);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape( true );
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
			
			// ----------------------------------------------- ROW 1 - Company
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			CellUtil.createCell(row, 0, companyName, titleStyle);
			row.setHeight((short) 500);
			++rowCount;

			// ----------------------------------------------- ROW 2 - Title
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			String title = "BALANCE DE SUMAS Y SALDOS";
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
			
			// ----------------------------------------------- ROW 4 - Centro de costo
			if (report.getParams().getCostCenters() != null && !report.getParams().getCostCenters().isEmpty()) {
				StringBuilder buf = new StringBuilder("Centro costo: ");
				boolean first = true;
				for (String cc : report.getParams().getCostCenters()) {
					if (!first) buf.append(", ");
					buf.append(cc);
					first = false;
				}
				row = sheet.createRow(rowCount);
				mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
				CellUtil.createCell(row, 0, buf.toString(), subTitleStyle);
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
			
			if (report.hasBeforePeriodAmounts()) {
				mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
				String msg = report.getParams().getSelectedPeriod() != null
					?"Saldos anter. al " + FORMATTER.format(report.getParams().getSelectedPeriod().getInitiationDate())
					:"Saldos anteriores";
				CellUtil.createCell(row, cellCount, msg, columnHeaderStyle);
				CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
				cellCount = cellCount + 2;
			}
			if (report.hasOpeningAmounts()) {
				mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
				CellUtil.createCell(row, cellCount, "Saldo apertura", columnHeaderStyle);
				CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
				cellCount = cellCount + 2;
			}
			if (report.hasInPeriodPreviousAmounts()) {
				mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
				String msg = "Saldo hasta " + FORMATTER.format(report.getParams().getFromDate());
				CellUtil.createCell(row, cellCount, msg, columnHeaderStyle);
				CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
				cellCount = cellCount + 2;
			}
			
			mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
			CellUtil.createCell(row, cellCount, "Sumas periodo", columnHeaderStyle);
			CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
			cellCount = cellCount + 2;
						
			mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
			CellUtil.createCell(row, cellCount, "Saldos finales", columnHeaderStyle);
			CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
			cellCount = cellCount + 2;
			++rowCount;
			
			// ----------------------------------------------- ROW 7 - Column Header
			row = sheet.createRow(rowCount);
			sheet.setColumnWidth(0, 9 * 256);
			sheet.setColumnWidth(1, 25 * 256);
			cellCount = 0;
			mergeHeaderRegion(rowCount, rowCount, 0, 1);
			CellUtil.createCell(row, cellCount, "CUENTA", columnHeaderStyle);
			cellCount = 2;
			
			if (report.hasBeforePeriodAmounts()) {
				CellUtil.createCell(row, cellCount, "S. Deudor", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
				CellUtil.createCell(row, cellCount, "S. Acreed", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
			if (report.hasOpeningAmounts()) {
				CellUtil.createCell(row, cellCount, "S. Deudor", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
				CellUtil.createCell(row, cellCount, "S. Acreed", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
			if (report.hasInPeriodPreviousAmounts()) {	
				CellUtil.createCell(row, cellCount, "S. Deudor", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
				CellUtil.createCell(row, cellCount, "S. Acreed", columnHeaderStyle);
				sheet.setColumnWidth(cellCount++, 12 * 256);
			}
				
			CellUtil.createCell(row, cellCount, "Debe", columnHeaderStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			CellUtil.createCell(row, cellCount, "Haber", columnHeaderStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);

			CellUtil.createCell(row, cellCount, "S. Deudor", columnHeaderStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			CellUtil.createCell(row, cellCount, "S. Acreed", columnHeaderStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			++rowCount;
			sheet.setRepeatingRows(new CellRangeAddress(0, rowCount - 1 , 0, columns-1));
		}

		private CellRangeAddress mergeHeaderRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
			CellRangeAddress secondRowRange = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(secondRowRange);
			return secondRowRange;
		}

		@Override
		public void accept(String account) {
			AccountTrialBalance bal = report.getBalances().get(account);
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			Cell cell = addCell(bal.getCode());
			cell.setCellStyle(wrappedCellStyle);
			
			cell = addCell(bal.getDescription());
			cell.setCellStyle(wrappedCellStyle);
			
			if (report.hasBeforePeriodAmounts()) {
				addCell(bal.getBeforePeriodDebitBalance());
				addCell(bal.getBeforePeriodUnpaidBalance());
			}
			if (report.hasOpeningAmounts()) {
				addCell(bal.getInPeriodOpeningDebitBalance());
				addCell(bal.getInPeriodOpeningUnpaidBalance());
			}
			// Saldo previo
			if (report.hasInPeriodPreviousAmounts()) {
				addCell(bal.getInPeriodBeforeDebitBalance());
				addCell(bal.getInPeriodUnpaidBalance());
			}
			addCell(bal.getInPeriodDebit());
			addCell(bal.getInPeriodCredit());
			
			addCell(bal.getAfterPeriodDebitBalance());
			addCell(bal.getAfterPeriodUnpaidBalance());
			
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
		
		public void totals(AccountTrialBalance tot) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			Cell cell = addCell("");
			cell = addCell("TOTALES");
			cell.setCellStyle(titleCellStyle);
			
			if (report.hasBeforePeriodAmounts()) {
				cell = addCell(tot.getBeforePeriodDebitBalance());
				cell.setCellStyle(titleCellStyle);
				cell = addCell(tot.getBeforePeriodUnpaidBalance());
				cell.setCellStyle(titleCellStyle);
			}
			if (report.hasOpeningAmounts()) {
				cell = addCell(tot.getInPeriodOpeningDebitBalance());
				cell.setCellStyle(titleCellStyle);
				cell = addCell(tot.getInPeriodOpeningUnpaidBalance());
				cell.setCellStyle(titleCellStyle);
			}
			// Saldo previo
			if (report.hasInPeriodPreviousAmounts()) {
				cell = addCell(tot.getInPeriodBeforeDebitBalance());
				cell.setCellStyle(titleCellStyle);
				cell = addCell(tot.getInPeriodUnpaidBalance());
				cell.setCellStyle(titleCellStyle);
			}
			cell = addCell(tot.getInPeriodDebit());
			cell.setCellStyle(titleCellStyle);
			cell = addCell(tot.getInPeriodCredit());
			cell.setCellStyle(titleCellStyle);
			
			cell = addCell(tot.getAfterPeriodDebitBalance());
			cell.setCellStyle(titleCellStyle);
			cell = addCell(tot.getAfterPeriodUnpaidBalance());
			cell.setCellStyle(titleCellStyle);
		}
	}
}
