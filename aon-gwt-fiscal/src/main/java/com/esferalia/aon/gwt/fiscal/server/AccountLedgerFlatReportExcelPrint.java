package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Account Ledger Flat Report Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountLedgerFlatReportExcelPrint" })
public class AccountLedgerFlatReportExcelPrint extends HttpServlet {

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

			ExcelAction action = new ExcelAction( );
			action.initialize("Listado mayor de cuentas");
			ACCOUNTING.getLedgerStream(domainName, domainId, user, params, 0, Integer.MAX_VALUE)
					.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Mayor_cuentas."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
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

	private class ExcelAction extends AbsExcelAction implements Consumer<FlatAccountEntryDetail>{
		private XSSFCellStyle entryHeaderStyle;
		
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			sheet.setMargin(Sheet.TopMargin, 0.3 );
			Footer footer = sheet.getFooter();
			footer.setLeft("Listado diario de movimientos ");
			footer.setRight("P\u00E1g: &P/&N");
			
			
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			
			decimalStyle.setFont(smallFont);

			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			journalHeaderFont.setFontHeightInPoints((short) 8);
			
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
			headerStyle.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER);
		    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerStyle.setFont(journalHeaderFont);
		    headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    
		    
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );
			entryHeaderStyle.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER);
			entryHeaderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			entryHeaderStyle.setFont(defaulFont);

			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "EJER.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 4 * 256);

			CellUtil.createCell(row, cellCount, "N\u00BA DIARIO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "FECHA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "TIPO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "SEGURIDAD", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);

			CellUtil.createCell(row, cellCount, "ACTIVIDAD", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "CUENTA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "DESC. CUENTA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);

			CellUtil.createCell(row, cellCount, "CONCEPTO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);

			CellUtil.createCell(row, cellCount, "DEBE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "HABER", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "S.DEUDOR", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "S.ACREED.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CONTRAP.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "DESC. CONTRAP.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);

			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);
			
			CellUtil.createCell(row, cellCount, "USR. CREAC.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "FEC. CREAC.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "USR. MODIF.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "FEC. MODIF.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "COMENTARIOS", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 25 * 256);
			
		}

		@Override
		public void accept(FlatAccountEntryDetail entry) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			addCell(entry.getEntryPeriodName());
			addCell(AonNumberUtils.toString( entry.getJournal()));
			addCell(entry.getEntryDate());
			addCell(entry.getEntryType().getDescription());
			addCell(entry.getEntrySecurityLevel().getName());
			addCell(entry.getActivityName());

			addCell(entry.getAccountCode());
			addCell(entry.getAccountDescription());
			addCell(entry.getConcept());
			addCell(entry.getDebit());
			addCell(entry.getCredit());
			addCell(AonMathUtils.round( entry.getDebitBalance()  + entry.getInitialDebitBalance()  - entry.getInitialUnpaidBalance()));
			addCell(AonMathUtils.round( entry.getUnpaidBalance() + entry.getInitialUnpaidBalance() - entry.getInitialDebitBalance() ));
			addCell(entry.getBalancingAccountCode());
			addCell(entry.getBalancingAccountDescription());
			addCell(entry.getDocumentNumber());
			
			addCell(entry.getEntryCreationUser());
			addCell(entry.getEntryCreationDate());
			addCell(entry.getEntryModificationUser());
			addCell(entry.getEntryModificationDate());
			
			addCell(entry.getComments());

			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}
		
	}
	
}
