package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "AccountOperatingReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountOperatingReportExcelPrint" })
public class AccountOperatingReportExcelPrint extends HttpServlet {

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
			AccountOperatingReport report = ACCOUNTING.getAccountOperatingReport(domainName, user, domainId, params);
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
	
	private AccountingReportParams parseParams(String accountReportParams) throws ParseException {
		AccountingReportParams params = new AccountingReportParams();
		
		JSONObject jsonParams =  new JSONObject(accountReportParams);
		
		// ******************* DOMAIN ******************* 
		params.setDomain(JsonUtils.getInteger(jsonParams, IJsonNames.DOMAIN));

		// ******************* PERIOD ******************* 
		Integer period = JsonUtils.getInteger(jsonParams, IJsonNames.PERIOD);
		if (period != null) {
			params.setPeriod(period);	
		}
		// ******************* FROMDATE ******************* 
		String fromDate = JsonUtils.getString(jsonParams, IJsonNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFromDate( FORMATTER.parse(fromDate));			
		}
		// ******************* TODATE ******************* 
		String toDate = JsonUtils.getString(jsonParams, IJsonNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setToDate( FORMATTER.parse(toDate));			
		}
		// ******************* ACCOUNT ******************* 
		Integer accountId = JsonUtils.getInteger(jsonParams, IJsonNames.ACCOUNT);
		if (accountId != null) {
			params.setAccount( new Account().setId(accountId));
		}
		String accountCode = JsonUtils.getString(jsonParams, IJsonNames.ACCOUNT_CODE); 
		if (AonStringUtils.isNotBlank(accountCode)) {
			if (params.getAccount() == null) params.setAccount( new Account());
			params.getAccount().setCode(accountCode);
		}
		String accountDescription = JsonUtils.getString(jsonParams, IJsonNames.ACCOUNT_DESCRIPTION);
		if (AonStringUtils.isNotBlank(accountDescription)) {
			if (params.getAccount() == null) params.setAccount( new Account());
			params.getAccount().setDescription(accountDescription);
		}
		// ******************* LEVEL ******************* 
		Integer level = JsonUtils.getInteger(jsonParams, IJsonNames.LEVEL);
		if (level != null) {
			params.setLevel(level);	
		}
		// ******************* ACTIVITY ******************* 
		Integer activity = JsonUtils.getInteger(jsonParams, IJsonNames.ACTIVITY);
		if (activity != null) {
			params.setActivity(activity);	
		}
		// ******************* SECURITYLEVEL ******************* 
		Integer confidential = JsonUtils.getInteger(jsonParams, IJsonNames.CONFIDENTIAL);
		if (confidential != null) {
			params.setSecurityLevel( SecurityLevel.safeValueOf(confidential));
		}
		// ******************* DOCUMENTNUMBER ******************* 
		String document = JsonUtils.getString(jsonParams, IJsonNames.DOCUMENT);
		if (document != null) {
			params.setDocumentNumber(document);	
		}
		// ******************* PREVIOUSPERIODS ******************* 
		Integer previousPeriods = JsonUtils.getInteger(jsonParams, IJsonNames.PREVIOUS_PERIODS);
		if (previousPeriods != null) {
			params.setPreviousPeriods(previousPeriods);	
		}
		// ******************* LOWLEVELACCOUNTVISIBLE ******************* 
		Integer lowLevelAccountVisible = JsonUtils.getInteger(jsonParams, IJsonNames.LOW_LEVEL_ACCOUNT_VISIBLE);
		if (lowLevelAccountVisible != null) {
			params.setLowLevelAccountVisible(lowLevelAccountVisible.intValue() == 1);	
		}
		// ******************* NOACTIVITYACCOUNTVISIBLE ******************* 
		Integer noActivityAccountVisible = JsonUtils.getInteger(jsonParams, IJsonNames.NO_ACTIVITY_ACCOUNT_VISIBLE);
		if (noActivityAccountVisible != null) {
			params.setNoActivityAccountVisible(noActivityAccountVisible.intValue() == 1);	
		}
		// ******************* percentsEnabled ******************* 
		Integer percentsEnabled = JsonUtils.getInteger(jsonParams, IJsonNames.PERCENTS_ENABLED);
		if (percentsEnabled != null) {
			params.setPercentsEnabled(percentsEnabled.intValue() == 1);	
		}
		// *******************  BYMONTH ******************* 
		Integer byMonth = JsonUtils.getInteger(jsonParams, IJsonNames.BY_MONTH);
		if (byMonth != null) {
			params.setByMonth(byMonth.intValue() == 1);	
		}
		// *******************  COSTCENTERS *******************
		JSONArray costCenters = JsonUtils.getJSONArray(jsonParams, IJsonNames.COST_CENTERS);
		if (costCenters != null && costCenters.length() > 0) {
			for (int i = 0; i < costCenters.length(); i++) {
				params.addCostCenter(costCenters.get(i).toString());
			}
		}
		return params;
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<AccountOperatingAccount>{
		private XSSFCellStyle titleCellStyle;
		private CellStyle wrappedCellStyle;
		
		private AccountOperatingReport report;
		private String companyName; 
		
		public ExcelAction(String companyName, AccountOperatingReport report) {
			super();
			this.companyName = companyName;
			this.report = report;
		}

		@Override
		protected void headerRow() {
			int intervals = report.getIntervals().size();
			int columnsPerInterval = 2;
			if (report.getParams().isByMonth() ) {
				columnsPerInterval = 1;
			} else {
				if (report.showRatios() ) {
					columnsPerInterval = 5;	
				}
				if (report.showIncreasePercent() ) {
					columnsPerInterval = 3;
				}
			}
			int columns = 2 + (intervals * columnsPerInterval);
			if (intervals > 1 && report.showIncreasePercent()) {
				columns = columns - 1; 
			}
			
			sheet.setDisplayZeros(false);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape( (report.getIntervals().size() * columns) > 5);
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
			sheet.setColumnWidth(1, 40 * 256);
			
			
			// ----------------------------------------------- ROW 1 - Company
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			CellUtil.createCell(row, 0, companyName, titleStyle);
			row.setHeight((short) 500);
			++rowCount;

			// ----------------------------------------------- ROW 2 - Title
			row = sheet.createRow(rowCount);
			mergeHeaderRegion(rowCount, rowCount, 0, columns-1);
			String title = "CUENTA DE EXPLOTACI\u00D3N";
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
			if (!report.getParams().isByMonth() ) {
				row = sheet.createRow(rowCount);
				mergeHeaderRegion(rowCount, rowCount, 0, 1);
				CellUtil.createCell(row, cellCount, "", headerStyle);
				cellCount = 2;
				for (DateInterval inter : report.getIntervals()) {
					if (report.showIncreasePercent() && cellCount == 2) {
						mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+1);
						CellUtil.createCell(row, cellCount, inter.getName(), columnHeaderStyle);
						CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
						cellCount = cellCount + 1;
					} else {
						mergeHeaderRegion(rowCount, rowCount, cellCount, cellCount+columnsPerInterval-1);
						CellUtil.createCell(row, cellCount, inter.getName(), columnHeaderStyle);
						CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
						cellCount = cellCount + columnsPerInterval-1;
					}
					cellCount++;
				}
				++rowCount;
			}
			
			// ----------------------------------------------- ROW 7 - Co9lumn Header
			row = sheet.createRow(rowCount);
			cellCount = 0;
			CellUtil.createCell(row, cellCount, "CUENTA", columnHeaderStyle);
			CellUtil.createCell(row, cellCount+1, "", columnHeaderStyle);
			mergeHeaderRegion(rowCount, rowCount, 0, 1);
			//CellUtil.createCell(row, cellCount, "", columnHeaderStyle);
			cellCount = 2;
			int iter = 0;
			for (DateInterval inter : report.getIntervals()) {
				if (report.getParams().isByMonth() ) {
					CellUtil.createCell(row, cellCount, inter.getName(), columnHeaderStyle);
					sheet.setColumnWidth(cellCount++, 12 * 256);
				} else {
					CellUtil.createCell(row, cellCount, "S. Deudor", columnHeaderStyle);
					sheet.setColumnWidth(cellCount++, 12 * 256);
					
					CellUtil.createCell(row, cellCount, "S. Acreed", columnHeaderStyle);
					sheet.setColumnWidth(cellCount++, 12 * 256);
				
					if (report.showRatios() ) {
						CellUtil.createCell(row, cellCount, "% S/Vta.", columnHeaderStyle);
						sheet.setColumnWidth(cellCount++, 12 * 256);
						
						CellUtil.createCell(row, cellCount, "% S/Com.", columnHeaderStyle);
						sheet.setColumnWidth(cellCount++, 12 * 256);
						
						CellUtil.createCell(row, cellCount, "% S/Gst.", columnHeaderStyle);
						sheet.setColumnWidth(cellCount++, 12 * 256);
	
					}
					if (report.showIncreasePercent() && iter != 0) {
						CellUtil.createCell(row, cellCount, "% Incrm.", columnHeaderStyle);
						sheet.setColumnWidth(cellCount++, 12 * 256);
					}
				}
				iter++;
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
			
			int col = 0;
			for (DateInterval inter : report.getIntervals()) {
				AccountOperatingStatement aos = report.get(account.getCode(),inter);
				Double db = (aos != null)?aos.getDebitBalance() : 0.0;
				Double ub = (aos != null)?aos.getUnpaidBalance() : 0.0;
				if (report.getParams().isByMonth() ) {
					Double saldo = (aos != null)? AonMathUtils.round( aos.getUnpaidBalance() - aos.getDebitBalance() ) : 0.0;
					cell = addCell(saldo);
					if (title) cell.setCellStyle(titleCellStyle);
				} else {
					cell = addCell(db);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
					cell = addCell(ub);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
				}
				
				if (report.showRatios()) {
					double percent = (aos != null)?aos.getSalesRatio() : 0.0;
					cell = addCell(percent);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
					percent = (aos != null)?aos.getPurchasesRatio() : 0.0;
					cell = addCell(percent);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
					percent = (aos != null)?aos.getExpensesRatio() : 0.0;
					cell = addCell(percent);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
				}
				if (report.showIncreasePercent() && col != 2) {
					double percent = (aos != null)?aos.getIncreasePercent() : 0.0;
					cell = addCell(percent);
					if (title) cell.setCellStyle(titleCellStyle);
					++col;
				}
			}
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
		
	}
}
