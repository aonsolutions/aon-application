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
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "AccountJournal Flat Report Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountJournalFlatReportExcelPrint" })
public class AccountJournalFlatReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountEntryParams = req.getParameter( IRequestParamsNames.ACCOUNT_ENTRY_PARAMS );
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			
			
			AccountEntryParams params = JsonParser.parse(accountEntryParams);
			ExcelAction action = new ExcelAction( );
			action.initialize("Diario");
			Stream<FlatAccountEntryDetail> stream =  ACCOUNTING.getFlatAccountEntries(domainName, domainId, user, params,0,Integer.MAX_VALUE);
			stream.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"DIARIO."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			stream.close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

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
