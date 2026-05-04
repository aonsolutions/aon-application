package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "AccountJournalReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountJournalReportExcelPrint" })
public class AccountJournalReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
//	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountEntryParams = req.getParameter( IRequestParamsNames.ACCOUNT_ENTRY_PARAMS );
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			
			
			
			AccountEntryParams params = JsonParser.parse(accountEntryParams);
			
			AonConfiguration config = AON.getConfiguration(domainName, domainId, user);
			Company company = config.getCompany();
			String companyName = company == null ? "" : company.getName();
			
			ExcelAction action = new ExcelAction( companyName );
			action.initialize("Diario");
			Stream<FlatAccountEntryDetail> stream = ACCOUNTING.getFlatAccountEntries(domainName, domainId, user, params,0,Integer.MAX_VALUE);
			stream.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"DIARIO."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
			stream.close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<FlatAccountEntryDetail>{
		private XSSFCellStyle entryHeaderStyle;
		private SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);
		
		private String companyName;
		
		public ExcelAction(String companyName) {
			super();
			this.companyName = companyName;
		}

		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3 );
			sheet.setMargin(Sheet.RightMargin, 0.3 );
			Header header = sheet.getHeader();
			header.setLeft("&B" + companyName);
			header.setRight("&B&D");
			Footer footer = sheet.getFooter();
			footer.setLeft("&BListado diario de movimientos");
			footer.setRight("&BP\u00E1g: &P/&N");
			
			
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

			
		    
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.copy();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			CellUtil.createCell(row, 0, "LISTADO DIARIO DE MOVIMIENTOS", headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 6));
			row.setHeight((short) 500);
			++rowCount;
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "CUENTA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "DESCRIPCI\u00D3N", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 30 * 256);

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

			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);
			
			
			sheet.setRepeatingRows(new CellRangeAddress(0, 1, 0, 6));
		}

		private Integer oldId;
		private double sumDebit = 0.0;
		private double sumCredit = 0.0;
		
		@Override
		public void accept(FlatAccountEntryDetail entry) {
			if (!AonNumberUtils.equals(oldId, entry.getEntryId())) {
				if (oldId != null) {
					paintEntryTotals();
				}
				paintEntryHeader( entry );
			}
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(entry.getAccountCode());
			addCell(entry.getAccountDescription());
			addCell(entry.getConcept());
			addCell(entry.getDebit());
			addCell(entry.getCredit());
			addCell(entry.getBalancingAccountCode());
			addCell(entry.getDocumentNumber());
			sumDebit +=  entry.getDebit();
			sumCredit +=  entry.getCredit();
			
			try {
				if (rowCount % 100 == 0) sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException( e );
			}
		}

		private void paintEntryHeader(FlatAccountEntryDetail entry) {
			oldId = entry.getEntryId();
			sumDebit = 0.0;
			sumCredit= 0.0;
			row = sheet.createRow(rowCount);
			cellCount = 0;
			String header = "Fecha: " + DATE_FORMATTER.format(entry.getEntryDate()) + " N\u00BA diario: " +  entry.getJournal();
			CellUtil.createCell(row, 0, header, entryHeaderStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 6));
			row.setHeight((short) 350);
			rowCount++;
		}
		private void paintEntryTotals() {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell("");
			addCell("");
			addCell("");
			addCell(sumDebit);
			addCell(sumCredit);
			addCell("");
			addCell("");
			rowCount++;
		}
		
	}
}
