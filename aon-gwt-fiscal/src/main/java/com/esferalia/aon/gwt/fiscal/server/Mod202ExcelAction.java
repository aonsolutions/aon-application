package com.esferalia.aon.gwt.fiscal.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202ExcelAction extends ModelIRPFExcelAction<Mod202,Mod202Key> {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public Mod202ExcelAction(Mod202 mod202) {
		super(mod202);
	}

	@Override
	protected String getTitle() {
		return "Impuesto sobre Sociedades. Pago fraccionado. ";
	}
	 
	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}
	@Override
	protected void printModelInfo() {
		super.printModelInfo();
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Concepto", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 30 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
		
		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

		CellUtil.createCell(row, cellCount, "", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

		CellUtil.createCell(row, cellCount, "", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

		CellUtil.createCell(row, cellCount, "", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));
		
		row = sheet.createRow(rowCount++);
		row.createCell(0);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		sheet.setRepeatingRows(new CellRangeAddress(0, 7, 0, 7));
		
		
	}
	
	@Override
	protected void headerRow() {
		sheet.setRowBreak(rowCount - 1);

	}
	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod202Key key) {
		double amount = model.ensureDetail(key).getAmount();
		style.setAlignment(HorizontalAlignment.LEFT);
		cell.setCellType(CellType.STRING);
		if (key == Mod202Key.P02) {
			cell.setCellValue(model.getInitialDate() != null
					?DATE_FORMAT.format(model.getInitialDate())
					:"");
			row = sheet.createRow(rowCount++);
		} else if (key == Mod202Key.P03) {
			String cnae = model.ensureDetail(key).getDescription();
			cell.setCellValue(AonStringUtils.defaultString(cnae));
		} else if (key == Mod202Key.X15) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X16) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X17) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X18) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X19) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X01) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X02) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X04) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X12) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X06) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X13) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X11) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X14) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.X08) {
			String type = model.ensureDetail(key).getDescription();
			cell.setCellValue(AonStringUtils.defaultString(type));
		} else if (key == Mod202Key.X09) {
			style.setFont(smallFont);
			String option = "NO CONSTA";
			if (amount == 1)  option = ">= 10 mill\u20AC < 20 mill\u20AC";
			else if (amount == 2) option = ">= 20 mill\u20AC < 60 mill\u20AC";
			else if (amount == 3) option = ">= 60 mill\u20AC";
			cell.setCellValue( option);
		} else if (key == Mod202Key.X00) {
			style.setFont(smallFont);
			String option = "A)";
			if (amount == 1)  option = "B.1)";
			else if (amount == 2) option = "B.2)";
			cell.setCellValue( option);
		} else if (key == Mod202Key.A01) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod202Key.A02) {
			String type = model.ensureDetail(key).getDescription();
			cell.setCellValue(AonStringUtils.defaultString(type));
		}
	}
}
