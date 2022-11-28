package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod303 Box Info Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model303BoxInfoPrint" })
public class Mod303BoxInfoPrint extends HttpServlet {

	private static final long serialVersionUID = 1105648961423707713L;
	private static final String YES = "SI";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod303"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod303 mod303 = MODEL303.get(occam,id);

			String keyString = req.getParameter("mod303Box");
			Mod303Key key = Mod303Key.valueOf(keyString);
			String fileName = "MOD303_" + AonStringUtils.defaultIfBlank(key.getBoxCode(), keyString);
			ExcelAction action = new ExcelAction();
			action.initialize(fileName);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			Stream<VatContext> stream = MODEL303.getInfo(occam, mod303, key); 
			stream.forEach(action);						
			action.finalize(resp.getOutputStream());
			stream.close();
			resp.flushBuffer();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<VatContext>{
		private double sumBase;
		private double sumQuota;
		private double sumSurchargeQuota;
		private double sumDeductibleQuota;

		@Override
		protected void headerRow() {
			row = sheet.createRow(rowCount++);
			cellCount = 0;

			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			CellUtil.createCell(row, cellCount, "TIPO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "TRANSACCI\u00D3N", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "SERVICIO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "INVERSI\u00D3N", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "R\u00C9G. AGR\u00CDC.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "RECTIFIC.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CRIT. CAJA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "EPIGR.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 6 * 256);

			CellUtil.createCell(row, cellCount, "FACTURA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "PAIS", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5 * 256);

			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "TITULAR FACTURA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 45 * 256);

			CellUtil.createCell(row, cellCount, "FECHA FAC.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "FECHA IMP.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "TIPO IVA.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 18 * 256);

			CellUtil.createCell(row, cellCount, "BASE IMP.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "% IVA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CUOTA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "% RE", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CUOTA RE", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "% DED.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CUOTA DED.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "N. REFERENCIA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 45 * 256);
			
		}

		@Override
		public void accept(VatContext vat) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(vat.getInvoiceType().getDescription());
			addCell(vat.getTransaction().getDescription());
			alignCenter(addCell(vat.isService()?YES:AonStringUtils.EMPTY)); 
			alignCenter(addCell(vat.isInvestment()?YES:AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isFarmerRegime()?YES:AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isRectification()?YES:AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isVatAccrualRegime()?YES:AonStringUtils.EMPTY));
			addCell(AonStringUtils.defaultIfBlank(vat.getEpigraph(), AonStringUtils.EMPTY));
			addCell(vat.getDocumentNumber());
			addCell(vat.getRegistryDocumentCountry());
			addCell(vat.getRegistryDocument());
			addCell(vat.getRegistryName());
			addCell(vat.getIssueDate());
			addCell(vat.getTaxDate());
			addCell(vat.getVatDeductionType()!=null?vat.getVatDeductionType().getName():AonStringUtils.SPACE);
			addCell(vat.getBase());
			addCell(vat.getPercentage());
			addCell(vat.getQuota());
			addCell(vat.getSurchargePercent());
			addCell(vat.getSurchargeQuota());
			if (vat.isSales()) {
				addCell(AonStringUtils.EMPTY);
				addCell(AonStringUtils.EMPTY);
			} else {
				addCell(vat.getDeductiblePercent());
				addCell(vat.getDeductibleQuota());
			}
			addCell(vat.getReferenceCode());
			
			sumBase = AonMathUtils.round(sumBase + vat.getBase());   
			sumQuota = AonMathUtils.round(sumQuota + vat.getQuota());
			sumSurchargeQuota = AonMathUtils.round(sumSurchargeQuota + vat.getSurchargeQuota()); 
			sumDeductibleQuota = AonMathUtils.round(sumDeductibleQuota + vat.getDeductibleQuota());
		}
		
		public void finalize(OutputStream out) throws IOException {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY); 
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
			addCell(sumBase);
			addCell(AonStringUtils.EMPTY);
			addCell(sumQuota);
			addCell(AonStringUtils.EMPTY);
			addCell(sumSurchargeQuota);
			addCell(AonStringUtils.EMPTY);
			addCell(sumDeductibleQuota);
			addCell(AonStringUtils.EMPTY);
			super.finalize(out);
		}
	}
	
	
}
