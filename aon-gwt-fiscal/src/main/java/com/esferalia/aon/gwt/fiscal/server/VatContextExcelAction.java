package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class VatContextExcelAction extends AbsExcelAction implements Consumer<VatContext>{
	private static final String YES = "SI";
	
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
		addCell("TOTAL");
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
