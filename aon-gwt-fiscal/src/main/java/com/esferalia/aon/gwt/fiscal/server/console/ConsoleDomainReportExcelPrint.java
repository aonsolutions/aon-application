package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.util.function.Consumer;

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
import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Console Domain Report Excel Print", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainReportExcelPrint" })
public class ConsoleDomainReportExcelPrint extends HttpServlet {
	

	private static final long serialVersionUID = 5908587637812355181L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String domainParams = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
			DomainParams params = JsonParser.parseDomainParams(domainParams);

			ExcelAction action = new ExcelAction();
			action.initialize("Dominios");
			CONSOLE.getDomains(params)
				.stream()
				.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			String balName = "Plan general contable";
			resp.setHeader("Content-disposition",
					"attachment; filename=\"" + balName + "." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<Domain> {
		private XSSFCellStyle entryHeaderStyle;
	
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(Sheet.LeftMargin, 0.3);
			sheet.setMargin(Sheet.RightMargin, 0.3);
			sheet.setMargin(Sheet.TopMargin, 0.3);
			Footer footer = sheet.getFooter();
			footer.setLeft("Dominios");
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
	
			CellUtil.createCell(row, cellCount, "ID", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 4 * 256);
	
			CellUtil.createCell(row, cellCount, "PARENT", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 4 * 256);
	
			CellUtil.createCell(row, cellCount, "TIPO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "NOMBRE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "DESCRIPCION", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "ACTIVO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);
	
			CellUtil.createCell(row, cellCount, "HEREN.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);
	
			CellUtil.createCell(row, cellCount, "CREA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);
			
			CellUtil.createCell(row, cellCount, "ACCESO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "EXPIRA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

		}
	
		@Override
		public void accept(Domain domain) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			addCell(domain.getId());
			addCell(domain.getParentId());
			addCell(domain.getDomainType() == null? "" : domain.getDomainType().getName());
			addCell(domain.getName());
			addCell(domain.getDescription());
			addCell(domain.isActive());
			addCell(domain.isEnableHeredity());
			addCell(domain.isDomainManagement());
			addCell(domain.getLastAccessDate());
			addCell(domain.getExpirationDate());
			try {
				if (rowCount % 100 == 0)
					sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException(e);
			}
		}
	}
	
}
