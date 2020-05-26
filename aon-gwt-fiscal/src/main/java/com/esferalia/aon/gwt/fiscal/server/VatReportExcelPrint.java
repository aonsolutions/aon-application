package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "VatReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportExcelPrint" })
public class VatReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2782900860290220524L;
	
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String vatParams = req.getParameter("vatParams");
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			AccountingReportParams params = new AccountingReportParams();
			JSONParser parser = new JSONParser();
			JSONObject jsonParams =  (JSONObject) parser.parse(vatParams);
			Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
			params.setDomain(domain.intValue());
			Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
			if (registry != null) {
				params.setRegistry(registry.intValue());	
			}
			Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
			if (activity != null) {
				params.setActivity(activity.intValue());	
			}
			String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
			if (AonStringUtils.isNotBlank(fromDate)) {
				params.setFromDate( FORMATTER.parse(fromDate));			
			}
			String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
			if (AonStringUtils.isNotBlank(toDate)) {
				params.setToDate( FORMATTER.parse(toDate));			
			}
			Double percent = (Double) jsonParams.get(IRequestParamsNames.PERCENT);
			if (percent != null) {
				params.setPercent(percent);	
			}
			Long type = (Long) jsonParams.get(IRequestParamsNames.VAT_SUMMARY_TYPE);
			if (type != null) {
				params.setVatSummaryType( VatSummaryType.safeValueOf(type.intValue()));
			}
			Long output = (Long) jsonParams.get(IRequestParamsNames.OUTPUT);
			if (output != null) {
				params.setOutput(output==1);
			}
			Long surcharge = (Long) jsonParams.get(IRequestParamsNames.SURCHARGE);
			if (surcharge != null) {
				params.setSurcharge(surcharge==1);
			}
			Long farmerRegime = (Long) jsonParams.get(IRequestParamsNames.FARMER_REGIME);
			if (farmerRegime != null) {
				params.setFarmerRegime(farmerRegime==1);
			}
			Long accrualRegime = (Long) jsonParams.get(IRequestParamsNames.ACCRUAL_REGIME);
			if (accrualRegime != null) {
				params.setAccrualRegime(accrualRegime==1);
			}
			Long investment = (Long) jsonParams.get(IRequestParamsNames.INVESTMENT);
			if (investment != null) {
				params.setInvestment(investment==1);
			}
			Long service = (Long) jsonParams.get(IRequestParamsNames.SERVICE);
			if (service != null) {
				params.setService(service==1);
			}
			Long rectification = (Long) jsonParams.get(IRequestParamsNames.RECTIFICATION);
			if (rectification != null) {
				params.setRectificationType(RectificationType.safeValueOf( rectification.intValue() ));
			}
			
			ExcelAction action = new ExcelAction( );
			action.initialize("IVA");
			FISCAL.getVatContext(domainName, domainId, user, params)
					.forEach(action)						
			;
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IVA."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<VatContext>{

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
			alignCenter(addCell(vat.isService()?"SI":AonStringUtils.EMPTY)); 
			alignCenter(addCell(vat.isInvestment()?"SI":AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isFarmerRegime()?"SI":AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isRectification()?"SI":AonStringUtils.EMPTY));
			alignCenter(addCell(vat.isVatAccrualRegime()?"SI":AonStringUtils.EMPTY));
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
		}
	}
}
