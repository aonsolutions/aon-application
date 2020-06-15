package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "AccountReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountReportExcelPrint" })
public class AccountReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 5908587637812355181L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String accountParams = req.getParameter(IRequestParamsNames.ACCOUNT_PARAMS);
			AccountParams params = JsonParser.parseAccountParams(accountParams);

			ExcelAction action = new ExcelAction();
			action.initialize("Plan general contable");
			params.setOffset(0);
			params.setLimit(Integer.MAX_VALUE);
			Stream<Account> stream =  ACCOUNTING.getAccounts(params);
			stream.forEach(action);

			resp.setContentType(MimeType.MS_EXCEL.getName());
			String balName = "Plan general contable";
			resp.setHeader("Content-disposition",
					"attachment; filename=\"" + balName + "." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			stream.close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<Account> {
		private XSSFCellStyle entryHeaderStyle;
	
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3);
			sheet.setMargin(Sheet.RightMargin, 0.3);
			sheet.setMargin(Sheet.TopMargin, 0.3);
			Footer footer = sheet.getFooter();
			footer.setLeft("Listado diario de movimientos ");
			footer.setRight("P\u00E1g: &P/&N");
	
			row = sheet.createRow(rowCount);
			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
	
			decimalStyle.setFont(smallFont);
	
			Font journalHeaderFont = workbook.createFont();
			journalHeaderFont.setBold(true);
			journalHeaderFont.setFontHeightInPoints((short) 8);
	
			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
			headerStyle.setFont(journalHeaderFont);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderBottom(BorderStyle.THIN);
	
			entryHeaderStyle = (XSSFCellStyle) workbook.createCellStyle();
			entryHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
			entryHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			entryHeaderStyle.setBorderBottom(BorderStyle.THIN);
			entryHeaderStyle.setFont(defaulFont);
	
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
	
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			CellUtil.createCell(row, cellCount, "NIVEL", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 4 * 256);
	
			CellUtil.createCell(row, cellCount, "CODIGO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 9 * 256);
	
			CellUtil.createCell(row, cellCount, "DESCRIPCION", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
	
			CellUtil.createCell(row, cellCount, "ALIAS", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
	
			CellUtil.createCell(row, cellCount, "C.COSTE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
	
		}
	
		@Override
		public void accept(Account account) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			addCell((short) account.getLevel());
			addCell(account.getCode());
			addCell(account.getDescription());
			addCell(account.getAlias());
			addCell(account.getCostCenter());
			try {
				if (rowCount % 100 == 0)
					sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException(e);
			}
		}
	}
	
}
