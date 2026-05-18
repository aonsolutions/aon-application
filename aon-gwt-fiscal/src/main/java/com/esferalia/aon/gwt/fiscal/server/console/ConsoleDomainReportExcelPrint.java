package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
			params.initializeOffsets();
			params.setLimit(Integer.MAX_VALUE);
			CONSOLE.getDomains(params)
				.forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			String balName = "Console - Dominios";
			resp.setHeader("Content-disposition",
					"attachment; filename=\"" + balName + "." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<ConsoleDomain> {
		private XSSFCellStyle entryHeaderStyle;
	
		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(PageMargin.LEFT, 0.3);
			sheet.setMargin(PageMargin.RIGHT, 0.3);
			sheet.setMargin(PageMargin.TOP, 0.3);
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
	
			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerStyle.copy();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
	
			row = sheet.createRow(rowCount++);
			cellCount = 0;
	
			CellUtil.createCell(row, cellCount, "BD", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 20 * 256);

			CellUtil.createCell(row, cellCount, "ID", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);
			
			CellUtil.createCell(row, cellCount, "NOMBRE", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "DESCRIPCION", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
	
			CellUtil.createCell(row, cellCount, "TIPO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);

			
			CellUtil.createCell(row, cellCount, "ACTIVO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);
			
			CellUtil.createCell(row, cellCount, "EXPIRA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "HEREN.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);
	
			CellUtil.createCell(row, cellCount, "CREA", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 3 * 256);

			CellUtil.createCell(row, cellCount, "ACCESO", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "PARENT", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);
			
			CellUtil.createCell(row, cellCount, "DOMINIO PARENT", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "PAGADOR", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);
			
			CellUtil.createCell(row, cellCount, "DOMINIO PAGADOR", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

		}
	
		@Override
		public void accept(ConsoleDomain domain) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			Integer payerId = domain.getPayerDomain() == null ? null : domain.getPayerDomain().getId();
			String payerDescription = domain.getPayerDomain() == null 
				? "" 
				: (domain.getPayerDomain().getName() + " (" + domain.getPayerDomain().getDescription() + ")");
			addCell(domain.getSchema());
			addCell(domain.getId());
			addCell(domain.getName());
			addCell(domain.getDescription());
			addCell(domain.getDomainType() == null? "" : domain.getDomainType().getName());
			addCell(domain.isActive());
			addCell(domain.getExpirationDate());
			addCell(domain.isEnableHeredity());
			addCell(domain.isDomainManagement());
			addCell(domain.getLastAccessDate());
			addCell(domain.getParentId());
			addCell(domain.getParent() == null 
				? ""
				: (domain.getParent().getName() + " (" + domain.getParent().getDescription() + ")") );
			addCell(payerId);
			addCell(payerDescription);
			try {
				if (rowCount % 100 == 0)
					sheet.flushRows();
			} catch (IOException e) {
				throw new AonCoreException(e);
			}
		}
	}
	
}
