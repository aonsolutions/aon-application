package com.esferalia.aon.gwt.fiscal.server.irpf;

import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IrpfBreakdownExcelAction extends AbsExcelAction implements Consumer<IrpfBreakdown>{

	@Override
	protected void headerRow() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

		CellUtil.createCell(row, cellCount, "TIPO", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);

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

		CellUtil.createCell(row, cellCount, "TIPO RET.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 18 * 256);

		CellUtil.createCell(row, cellCount, "BASE IMP.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10 * 256);

		CellUtil.createCell(row, cellCount, "% IRPF", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10 * 256);

		CellUtil.createCell(row, cellCount, "CUOTA", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10 * 256);

		CellUtil.createCell(row, cellCount, "% DED.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10 * 256);

		CellUtil.createCell(row, cellCount, "CUOTA DED.", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		
		CellUtil.createCell(row, cellCount, "N. REFERENCIA", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 45 * 256);
		
	}

	@Override
	public void accept(IrpfBreakdown irpf) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		addCell(irpf.getInvoiceType().getDescription());
		addCell(AonStringUtils.defaultIfBlank(irpf.getEpigraph()));
		if ( irpf.getNumber() != null) {
			addCell(irpf.getDocumentNumber());
		} else {
			addCell(AonStringUtils.EMPTY);
		}
		addCell(irpf.getRegistryDocumentCountry());
		addCell(irpf.getRegistryDocument());
		addCell(irpf.getName());
		addCell(irpf.getIssueDate());
		addCell(irpf.getTaxDate());
		addCell(irpf.getWithholdingType()!=null?irpf.getWithholdingType().getAbbreviatedDescription():AonStringUtils.SPACE);
		addCell(irpf.getBase());
		addCell(irpf.getPercent());
		addCell(irpf.getQuota());
		if (irpf.isSales()) {
			addCell(AonStringUtils.EMPTY);
			addCell(AonStringUtils.EMPTY);
		} else {
			addCell(irpf.getDeductiblePercent());
			addCell(irpf.getDeductibleQuota());
		}
		addCell(AonStringUtils.defaultIfBlank(irpf.getReferenceCode()));
	}
}
