package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

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
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositDescription;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class CCAAExcelAction extends AbsExcelAction {

	public CCAAExcelAction(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}
	
	protected static final XSSFColor REGISTRADORES = new XSSFColor(new java.awt.Color(Integer.valueOf("fa", 16 )
			,Integer.valueOf("58", 16 ),Integer.valueOf("58", 16 )));
	
	protected  static final String[] IMAGES = new String[] { 
		"/com/esferalia/aon/gwt/common/client/css/images/aon-registro-mercantil-image.png"
	};
	
	protected Font idFont;
	protected XSSFCellStyle rowStyle;
	protected XSSFCellStyle idCellStyle;
	protected XSSFCellStyle boxCellStyle;
	protected XSSFCellStyle headerCellStyle;
	protected Font boxFont;
	
	D2Deposit d2Deposit;
	
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
		headerCellStyle.setFillForegroundColor(REGISTRADORES);

		printModelInfo();

	}

	protected void printModelInfo() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		try {
			InputStream inputStream = CCAAExcelAction.class.getResourceAsStream(IMAGES[0]);
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
			pict.resize(0.8,3);
		} catch (IOException e) {
			e.printStackTrace();
			// Sin Imagen,.
		}
		CellUtil.createCell(row, 0,"");
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
		CellUtil.createCell(row, 1, getTitle(),
				headerCellStyle);

		CellUtil.createCell(row, 7, getD2Deposit().getType(),
				headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, "", headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, AonNumberUtils.toString(getD2Deposit().getYear()), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 6));

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 7));
		row = sheet.createRow(rowCount++);

		CellUtil.createCell(row, 0, getD2Deposit().getCif()  + "-" +  getD2Deposit().getRazonSocial() , idCellStyle);

		row = sheet.createRow(rowCount++);
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
		sheet.setColumnWidth(cellCount++, 40 * 256);
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
	
	
	public void BA1() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Balance: Activo", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
		
		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		CellUtil.createCell(row, cellCount, "Notas de la memoria", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

		CellUtil.createCell(row, cellCount, "Ejercicio "+getD2Deposit().getYear(), rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

		CellUtil.createCell(row, cellCount, "Ejercicio "+(getD2Deposit().getYear()-1), rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));

		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")) {
			keys = D2PDepositConstants.BALANCE_ACTIVE_PYMES_KEYS;
		} else {
			keys = D2DepositConstants.BA_ABREVIATE_KEYS_1;
		}
		for (D2DepositHeaderKey[] innerKeys : keys) {
			row = sheet.createRow(rowCount++);
			String description = D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]);
			String a = getD2Deposit().getMap().get(innerKeys[0].getCode());
			if(a == null) a = "0.0";
			String b = getD2Deposit().getMap().get(innerKeys[1].getCode());
			if(b == null) b = "0.0";
			String c = getD2Deposit().getMap().get(innerKeys[2].getCode());
			if(c == null) c = "";
			int l = AonStringUtils.length(description);
			if (l != 0) {
				int r = (int) (l / 70) + 1; 
				int h = (r * 250);
				row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
			}
			row.setRowStyle(rowStyle);
			cellCount = 0;
			Cell cell = row.createCell(cellCount++);
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			//Boolean[] isTitle = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(innerKeys[0]);
			style.setFont(/*isTitle[0] ? boldFont : */defaulFont );
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(description);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
			
			/*Cell boxCell = addCell(AonStringUtils.leftPad(innerKeys[0].getCode(), 3, "0"));
			boxCell.setCellType(Cell.CELL_TYPE_STRING);
			boxCell.setCellStyle(boxCellStyle);
			*/
			cell = row.createCell(cellCount++);
			cell = row.createCell(cellCount++);
			style = workbook.createCellStyle();
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
			style.setFont(defaulFont);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(innerKeys[0].getCode());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			
			cell = row.createCell(cellCount++);
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
			cell.setCellValue(c);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			
			cell = row.createCell(cellCount++);
			double amount1 = Double.parseDouble(a);
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
			cell.setCellValue(amount1);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 5, 6));

			cell = row.createCell(cellCount++);
			double amount2 = Double.parseDouble(b);
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
			cell.setCellValue(amount2);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));

		}
		
	}
	
	private void BA2() {

	}
	
	protected abstract String getTitle();

	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}

	public void setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}
	
	
}
