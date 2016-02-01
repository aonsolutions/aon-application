package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Base.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalModelExcelAction extends AbsExcelAction {

	protected static final XSSFColor ARABA_BG = new XSSFColor(new java.awt.Color(163, 12, 81));
	protected static final XSSFColor BIZKAIA_BG = new XSSFColor(new java.awt.Color(215, 0, 4));
	protected static final XSSFColor GIPUZKOA_BG = new XSSFColor(new java.awt.Color(161, 192, 49));
	protected static final XSSFColor NAVARRA_BG = new XSSFColor(new java.awt.Color(218, 0, 42));
	protected static final XSSFColor AEAT_BG = new XSSFColor(new java.awt.Color(58, 133, 195));

	private static final XSSFColor[] COLORS = new XSSFColor[] { ARABA_BG, BIZKAIA_BG, GIPUZKOA_BG, NAVARRA_BG,
			AEAT_BG };
	
	private static final String[] IMAGES = new String[] { 
			"/com/esferalia/aon/gwt/common/client/css/images/aon-araba-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-bizkaia-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-gipuzkoa-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-navarra-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-aeat-header-image.png"
	};
	
	

	private Mod111 mod111;

	public FiscalModelExcelAction(Mod111 mod111) {
		this.mod111 = mod111;
	}

	protected Font idFont;
	protected XSSFCellStyle rowStyle;
	protected XSSFCellStyle idCellStyle;
	protected XSSFCellStyle boxCellStyle;
	protected XSSFCellStyle headerCellStyle;
	protected Font boxFont;

	@Override
	public void initialize(String name) {
		this.initialize(name, false);
	}

	@Override
	public void initialize(String name, boolean printHeaders) {
		super.initialize(name, printHeaders);
		rowStyle = (XSSFCellStyle) workbook.createCellStyle();
		rowStyle.setWrapText(true);

		boxFont = workbook.createFont();
		boxFont.setFontHeightInPoints((short) 8);
		boxFont.setColor(IndexedColors.GREY_40_PERCENT.index);

		boxCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		boxCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		boxCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		boxCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		boxCellStyle.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		boxCellStyle.setFont(boxFont);

		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 10);

		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		idCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		idCellStyle.setFont(idFont);

		sheet.setMargin(Sheet.LeftMargin, 0.5);
		sheet.setMargin(Sheet.RightMargin, 0.5);

		Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.index);
		headerFont.setFontHeightInPoints((short) 10);

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerCellStyle.setWrapText(true);
		headerCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerCellStyle.setFillForegroundColor(AON_BLUE);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(COLORS[mod111.getAdministration().ordinal()]);

		printModelInfo();

		headerRow();
	}

	@Override
	protected void headerRow() {

		row = sheet.createRow(rowCount++);
		cellCount = 0;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Concepto", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 45 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
		
		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		CellUtil.createCell(row, cellCount, "N. Percep.", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

		CellUtil.createCell(row, cellCount, "Percepciones", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

		CellUtil.createCell(row, cellCount, "Ret. e Ingr. Cta.", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));

	}

	public void accept(IModelScript ms) {
		row = sheet.createRow(rowCount++);
		row.setHeight((short) 600);
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(ms.isEnabled() ? defaulFont : boldFont);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		cell.setCellStyle(style);
		cell.setCellValue(AonStringUtils.trimToEmpty(ms.getLabel()));
		cell.setCellType(Cell.CELL_TYPE_STRING);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));			
		if (ms.getKeys() == null) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 1, 7));
		} else {
			if (ms.getKeys().length == 1) {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 1, 5));
				cellCount = 6;
			} else if (ms.getKeys().length == 2) {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 1, 3));
				cellCount = 4;
			} else {
				cellCount = 2;
			}
			for (Mod111Key key : ms.getKeys()) {
				Cell boxCell = addCell(AonStringUtils.leftPad(AonNumberUtils.toString(key.getBox()), 3, "0"));
				boxCell.setCellType(Cell.CELL_TYPE_STRING);
				boxCell.setCellStyle(boxCellStyle);

				double amount = mod111.ensureDetail(key).getAmount();
				cell = row.createCell(cellCount++);
				style = workbook.createCellStyle();
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
				style.setFont(ms.isEnabled() ? defaulFont : boldFont);
				style.setBorderBottom(CellStyle.BORDER_THIN);
				style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
				cell.setCellStyle(style);
				cell.setCellValue(amount);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			}

		}
	}

	private void printModelInfo() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		try {
			InputStream inputStream = FiscalModelExcelAction.class.getResourceAsStream(
					IMAGES[ mod111.getAdministration().ordinal()]);
			byte[] imageBytes = AonIOUtils.toByteArray(inputStream);
			int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			inputStream.close();
			CreationHelper helper = workbook.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(2);
			anchor.setCol1(0);
			anchor.setRow1(0);
			anchor.setDx1(20);
			anchor.setDy1(20);
			Picture pict = drawing.createPicture(anchor, pictureureIdx);
			pict.resize();
		} catch (IOException e) {
			e.printStackTrace();
			// Sin Imagen,.
		}
		CellUtil.createCell(row, 0,"");
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
		CellUtil.createCell(row, 1,"                          " +
				"Retenciones e ingresos a cuenta. " + "Rendimientos del trabajo y de actividades econ\u00F3micas.",
				headerCellStyle);

		CellUtil.createCell(row, 7, mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()),
				headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, AonNumberUtils.toString(mod111.getYear()), headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, mod111.getPeriod().getDescription(), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 6));

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 7));
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 0, mod111.getDocument() + "-" + mod111.getName(), idCellStyle);

		row = sheet.createRow(rowCount++);
		
	}

}
