package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.Optional;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
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
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Amortization Excel Print", urlPatterns = { "/aon_gwt_fiscal/roms/AmortizationExcelServlet" })
public class AmortizationExcelServlet extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			Integer domainId = AonNumberUtils.toInteger( req.getParameter(IRequestParamsNames.DOMAIN_ID) );
			String paramsJson = req.getParameter(IRequestParamsNames.AMORTIZATION_PARAMS);
			AmortizationParams params = JsonParser.parseAmortizationParams(paramsJson); 
			
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			AonConfiguration config = AON.getConfiguration(occam);
			Company company = config.getCompany();
			String companyName = company == null ? "" : company.getName();

			ExcelAction action = new ExcelAction( companyName );
			MutableObject<String> nameHolder = new MutableObject<>();
			params.getId()
				.ifPresentOrElse( 
					amortizationId -> {
						action.initialize("Ficha de amortizaci\u00F3n ");
						Amortization am = ACCOUNTING.getAmortization(occam, domainId, amortizationId)
							.orElseThrow(() -> new AonCoreException("Amortizaci\u00F3n no encontrada: " + amortizationId));
						am.detailStream().forEach( det -> action.run(am, det));
						nameHolder.setValue("FICHA-"+amortizationId);
					}
					, () -> {
						action.initialize("Apuntes de amortizaci\u00F3n");
						AonCollectionUtils.stream(ACCOUNTING.getAmortizations(occam, params))
					      	.map(am -> ACCOUNTING.getAmortization(occam, domainId, am.getId()))
					      	.flatMap(Optional::stream)
			      			.forEach( am -> am.detailStream().forEach( det -> action.run(am, det)))
		      			;
						ACCOUNTING.getAmortizations(occam, params)
							.forEach( am -> am.detailStream().forEach( det -> action.run(am, det)));
						nameHolder.setValue("FICHA AMORTIZ.");
					}
			);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+nameHolder.getValue()+"."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private class ExcelAction extends AbsExcelAction {
		private XSSFCellStyle entryHeaderStyle;
		
		private String companyName;
		
		public ExcelAction(String companyName) {
			super();
			this.companyName = companyName;
		}

		@Override
		protected void headerRow() {
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			sheet.setMargin(PageMargin.LEFT, 0.3 );
			sheet.setMargin(PageMargin.RIGHT, 0.3 );
			Header header = sheet.getHeader();
			header.setLeft("&B" + companyName);
			header.setRight("&B&D");
			Footer footer = sheet.getFooter();
			footer.setLeft("&BFicha de amortizaci\u00F3n");
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
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 21));
			row.setHeight((short) 500);
			++rowCount;
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			CellUtil.createCell(row, cellCount, "Id", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "Descripci\u00F3n", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "Confidencial", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 5 * 256);
			
			CellUtil.createCell(row, cellCount, "Fec. Inicial", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Importe", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Fec. Venta", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Importe Venta", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Periodo", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "Porcent.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "Cuenta Inmovilizado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "Cuenta Acumulado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "Cuenta Dotaci\u00F3n", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);
			
			CellUtil.createCell(row, cellCount, "Bien de inversi\u00F3n", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "Desde Fec.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Hasta Fec.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Coefici.", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "Dotaci\u00F3n", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Acumulado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Pendiente", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Dotaci\u00F3n Fiscal", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Acumulado Fiscal", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Pendiente Fiscal", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, decimalStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
			
			CellUtil.createCell(row, cellCount, "Estado", headerStyle);
			sheet.setDefaultColumnStyle(cellCount, defaultStyle);
			sheet.setColumnWidth(cellCount++, 12 * 256);
		}
		
		public void run(Amortization am, AmortizationDetail det) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(am.getId());
			addCell(am.getDescription());
			addCell(am.isConfidential()?"SI":"NO");
			addCell(am.getInitialDate());
			addCell(am.getAmount());
			addCell(am.getDeadline());
			addCell(am.getSaleAmount());
			addCell(am.getFeePeriod().getDescription());
			addCell(am.getPercentage());
			addCell(am.getFixedAssetAccount().getFullName());
			addCell(am.getAccumulatedAccount().getFullName());
			addCell(am.getAllocationAccount().getFullName());
			addCell(am.getInvestAsset() != null ? am.getInvestAsset().getDescription() : "");
			addCell(det.getFromDate());
			addCell(det.getToDate());
			addCell(det.getCoefficient());
			addCell(det.getAllocation());
			addCell(det.getAccumulated());
			addCell(det.getPending());
			addCell(det.getFiscalAllocation());
			addCell(det.getFiscalAccumulated());
			addCell(det.getFiscalPending());
			addCell(det.getStatus().getDescription());
		}
		
	}
	
}
