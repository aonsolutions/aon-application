package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Accounting Amortization Excel Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/AccountingAmortizationExcelServlet" })
public class AccountingAmortizationExcelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			Integer domainId = AonNumberUtils.toInteger(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			AmortizationParams params = JsonParser.parseAmortizationParams(req.getParameter(IRequestParamsNames.AMORTIZATION_PARAMS));

			Occam occam = new Occam().setDomainName(domainName).setDomain(domainId).setUser(user);
			AonConfiguration config = AON.getConfiguration(occam);
			Company company = config.getCompany();
			String companyName = company == null ? "" : company.getName();

			ExcelAction action = new ExcelAction(companyName);
			action.initialize("Apuntes de amortizaci\u00F3n");
			ACCOUNTING.getAccountingAmortizations(occam, domainId, params)
				.forEach(aam -> action.run(aam.getAmortization(), aam.getDetail()));

			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"APUNTES-AMORTIZACION." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	private class ExcelAction extends AbsExcelAction {

		private final String companyName;

		public ExcelAction(String companyName) {
			super();
			this.companyName = companyName;
		}

		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(PageMargin.LEFT, 0.3);
			sheet.setMargin(PageMargin.RIGHT, 0.3);
			sheet.getHeader().setLeft("&B" + companyName);
			sheet.getHeader().setRight("&B&D");
			sheet.getFooter().setLeft("&BApuntes de amortizaci\u00F3n");
			sheet.getFooter().setRight("&BP\u00E1g: &P/&N");

			CellStyle defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			decimalStyle.setFont(smallFont);

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 8);

			XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(AON_LIGHT_GRAY);
			headerStyle.setFont(headerFont);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderBottom(BorderStyle.THIN);

			row = sheet.createRow(rowCount);
			CellUtil.createCell(row, 0, "APUNTES DE AMORTIZACI\u00D3N", headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 10));
			row.setHeight((short) 500);
			++rowCount;

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			header("Estado",               8, defaultStyle, headerStyle);
			header("Descripci\u00F3n",     50, defaultStyle, headerStyle);
			header("Cuenta Inmovilizado",  45, defaultStyle, headerStyle);
			header("Cuenta Dotaci\u00F3n", 45, defaultStyle, headerStyle);
			header("Cuenta Acumulado",     45, defaultStyle, headerStyle);
			header("Desde",                12, defaultStyle, headerStyle);
			header("Hasta",                12, defaultStyle, headerStyle);
			header("%",                     8, decimalStyle, headerStyle);
			header("Dotaci\u00F3n",        12, decimalStyle, headerStyle);
			header("Acumulado",            12, decimalStyle, headerStyle);
			header("Pendiente",            12, decimalStyle, headerStyle);
		}

		private void header(String label, int widthChars, CellStyle colStyle, XSSFCellStyle headerStyle) {
			CellUtil.createCell(row, cellCount, label, headerStyle);
			sheet.setDefaultColumnStyle(cellCount, colStyle);
			sheet.setColumnWidth(cellCount++, widthChars * 256);
		}

		public void run(Amortization am, AmortizationDetail det) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(det.getStatus() != null ? det.getStatus().getDescription() : "");
			addCell(am.getDescription());
			addCell(am.getFixedAssetAccount().getFullName());
			addCell(am.getAllocationAccount().getFullName());
			addCell(am.getAccumulatedAccount().getFullName());
			addCell(det.getFromDate());
			addCell(det.getToDate());
			addCell(det.getCoefficient());
			addCell(det.getAllocation());
			addCell(det.getAccumulated());
			addCell(det.getPending());
		}
	}
}
