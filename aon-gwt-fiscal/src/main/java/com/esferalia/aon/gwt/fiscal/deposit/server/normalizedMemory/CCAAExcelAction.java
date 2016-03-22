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
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositDescription;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
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

	protected static final String TIC = "✔";
	
	protected Font idFont;
	protected XSSFCellStyle rowStyle;
	protected XSSFCellStyle idCellStyle;
	protected XSSFCellStyle boxCellStyle;
	protected XSSFCellStyle headerCellStyle;
	protected Font boxFont;

	D2Deposit d2Deposit;

	public void initialize() {
	
		workbook = new SXSSFWorkbook();

	  dataFormat = workbook.getCreationHelper().createDataFormat();
	  dateStyle = workbook.createCellStyle();
	  dateStyle.setDataFormat(dataFormat.getFormat(DATE_PATTERN));
	  dateStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );

	  numberStyle = workbook.createCellStyle();
	  numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
	  numberStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );

	  decimalStyle = workbook.createCellStyle();
	  decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	  decimalStyle.setAlignment( HSSFCellStyle.ALIGN_RIGHT );

		centerCellStyle = workbook.createCellStyle();
		centerCellStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER );

		defaulFont= workbook.createFont();
		defaulFont.setFontHeightInPoints((short) 9);
		
		smallFont = workbook.createFont();
		smallFont.setFontHeightInPoints((short) 8);

		boldFont= workbook.createFont();
		boldFont.setFontHeightInPoints((short) 9);
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

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
		idFont.setFontHeightInPoints((short) 9);

		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		idCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		idCellStyle.setFont(idFont);

		Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.index);
		headerFont.setFontHeightInPoints((short) 9);

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerCellStyle.setWrapText(true);
		headerCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerCellStyle.setFillForegroundColor(AON_BLUE);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(REGISTRADORES);
	}

	public void addSheet(String name){
		sheet = (SXSSFSheet) workbook.createSheet(name);
		rowCount = 0;
		cellCount = 0;

		sheet.setMargin(Sheet.LeftMargin, 0.5);
		sheet.setMargin(Sheet.RightMargin, 0.5);

		//header();
	}

	@Override
	protected void headerRow() {}

	protected void header(Integer n) {
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
		CellUtil.createCell(row, 1, getTitle(), headerCellStyle);

		CellUtil.createCell(row, n+2, getD2Deposit().getType(), headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, n+2, "", headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, n+2, AonNumberUtils.toString(getD2Deposit().getYear()), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, n+1));

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0,  n+2));
		row = sheet.createRow(rowCount++);

		CellUtil.createCell(row, 0, getD2Deposit().getCif()  + "-" +  getD2Deposit().getRazonSocial() , idCellStyle);

		row = sheet.createRow(rowCount++);
	}
	
	public void three(String column1, String column2, String column3, String column4,
				D2DepositHeaderKey[][] keys, D2DepositHeaderKey[][] keys2){
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, column1, headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		CellUtil.createCell(row, cellCount, column2, rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 6 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

		CellUtil.createCell(row, cellCount, column3, rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

		CellUtil.createCell(row, cellCount, column4, rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));
		
		for(Integer i = 0; i<2; i++){
			if(i.equals(1)) keys = keys2;
			for (D2DepositHeaderKey[] innerKeys : keys) {
				row = sheet.createRow(rowCount++);
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				String a = getD2Deposit().getMap().get(innerKeys[0].getCode());
				if(a == null) a = "0.0";
				String b = getD2Deposit().getMap().get(innerKeys[1].getCode());
				if(b == null) b = "0.0";
				String c = getD2Deposit().getMap().get(innerKeys[2].getCode());
				if(c == null) c = "";
				int l = AonStringUtils.length(description);
				if (l != 0) {
					int r = (int) (l / 50) + 1;
					int h = (r * 250);
					row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
				}
				row.setRowStyle(rowStyle);
				cellCount = 0;
				Cell cell = row.createCell(cellCount++);
				CellStyle style = workbook.createCellStyle();
				style.setWrapText(true);
				style.setFont(defaulFont );
				style.setBorderBottom(CellStyle.BORDER_THIN);
				style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
				cell.setCellStyle(style);
				cell.setCellValue(description);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

				
				cell = row.createCell(cellCount++);
				cell = row.createCell(cellCount++);
				style = workbook.createCellStyle();
				style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
				style.setFont(boxFont);
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
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

				cell = row.createCell(cellCount++);
				cell = row.createCell(cellCount++);
				double amount2 = Double.parseDouble(b);
				style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount2);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));
			}
		}
	}
	
	
	private Integer calculate(Integer pageMaxNumber, Integer number, Double coeficiente) {
		Integer calc = 0;
		Integer dif = pageMaxNumber - number;
		calc = 8 * 256;
		Double d = 4 * coeficiente * 256;
		calc = calc + d.intValue();
		for(Integer i = 0 ; i < dif+1; i++){
			Double d2 = coeficiente * 256;
			calc = calc + d2.intValue();	
		}		
		for(Integer i = 0; i < number; i++){
			Double d2 = coeficiente * 256;
			calc = calc + d2.intValue();
		}
		Integer calc2 = 23040 - calc;
		return calc2 / (5 + dif + number); 
	}
	
	private short calculateHeight(String[] strings, Integer number, Integer div, short height) {		
		Integer coeficiente1 = (150 * div)/number;
		Double n1 = ((strings[0].length() / coeficiente1) * (height / 1.5));
		
		Integer max = 0;
		for (Integer i = 1; i < strings.length; i++) {
			if(strings[i].length() > max)
				max = strings[i].length();
		}
	
		Integer coeficiente2 = (36 * div)/number;
		Double n2 = ((max / coeficiente2) * (height / 1.5));
		
		return n1 > n2 ? n1.shortValue() : n2.shortValue();
	}
	
	private XSSFCellStyle calculateHeaderFontSize(Integer pageMaxNumber) {
		XSSFCellStyle style = (XSSFCellStyle) headerCellStyle.clone();
		
		Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.index);
		if(pageMaxNumber >= 6)
			headerFont.setFontHeightInPoints((short) 8);
		else headerFont.setFontHeightInPoints((short) 9);	
		style.setFont(headerFont);
		return style;
	}
	
	public void generalHeader(Integer pageMaxNumber, Integer number, String[] strings, D2DepositHeaderKey[][] keys
			,Integer headerHeight, Boolean secondPart, Integer codelength){
		double coeficiente = 82 / (4 + pageMaxNumber +1); 
		Integer sumcoef = calculate(pageMaxNumber, number, coeficiente);
		
		Integer dif = pageMaxNumber - number;
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		XSSFCellStyle headerStyle = calculateHeaderFontSize(pageMaxNumber);
		
		CellUtil.createCell(row, cellCount, strings[0], headerStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		Double d = 4 * coeficiente * 256;
		sheet.setColumnWidth(cellCount++, d.intValue() + sumcoef * 4);
		for(Integer i = 0 ; i < dif+1; i++){
			Double d2 = coeficiente * 256;
			sheet.setColumnWidth(cellCount++, d2.intValue() + sumcoef);	
		}
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+2));
		
		for(Integer i = 0; i < number; i++){
			CellUtil.createCell(row, cellCount, strings[i+1], headerStyle);
			Double d2 = coeficiente * 256;
			sheet.setColumnWidth(cellCount++, d2.intValue() + sumcoef);
		}
		
		if(headerHeight > 1){
			Double x = (row.getHeight() * headerHeight) / 1.5;
			row.setHeight(x.shortValue());
		}
		
		for (D2DepositHeaderKey[] innerKeys : keys) {
			row = sheet.createRow(rowCount++);
			String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
			int l = AonStringUtils.length(description);
			if (l != 0) {
				int r = (int) (l / 50) + 1;
				int h = (r * 250);
				row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
			}	
			row.setRowStyle(rowStyle);
			cellCount = 0;
			Cell cell = row.createCell(cellCount++);
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setFont(defaulFont );
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(description);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
			
			for(Integer i = 0; i < dif+2; i++)
				cell = row.createCell(cellCount++);
			
			style = workbook.createCellStyle();
			style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			style.setFont(boxFont);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);

			cell.setCellStyle(style);
			String code = innerKeys[0].getCode();
			if(codelength != 0)
				code = code.substring(0, codelength);
			cell.setCellValue(code);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			
			if(secondPart){
				for(Integer i = 0; i < number; i++){
					cell = row.createCell(cellCount++);
					String a = getD2Deposit().getMap().get(innerKeys[i+7].getCode());
					if(a == null) a = "0.0";
					double amount = Double.parseDouble(a);
					style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					cell.setCellValue(amount);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				}
			} else{
				for(Integer i = 0; i < number; i++){
					cell = row.createCell(cellCount++);
					String a = getD2Deposit().getMap().get(innerKeys[i].getCode());
					if(a == null) a = "0.0";
					double amount = Double.parseDouble(a);
					style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					cell.setCellValue(amount);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				}
			}
		}
	}
	
	
	public void general(Integer pageMaxNumber, Integer number, String[] strings, D2DepositKey[][] keys
			,Integer headerHeight, Integer codeLength){
		double coeficiente = 82 / (4 + pageMaxNumber +1); 
		Integer sumcoef = calculate(pageMaxNumber, number, coeficiente);
		
		Integer dif = pageMaxNumber - number;
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		XSSFCellStyle headerStyle = calculateHeaderFontSize(pageMaxNumber);
		
		CellUtil.createCell(row, cellCount, strings[0], headerStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		Double d = 4 * coeficiente * 256;
		sheet.setColumnWidth(cellCount++, d.intValue() + sumcoef * 4);
		for(Integer i = 0 ; i < dif+1; i++){
			Double d2 = coeficiente * 256;
			sheet.setColumnWidth(cellCount++, d2.intValue() + sumcoef);	
		}
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+2));
		
		for(Integer i = 0; i < number; i++){
			CellUtil.createCell(row, cellCount, strings[i+1], headerStyle);
			Double d2 = coeficiente * 256;
			sheet.setColumnWidth(cellCount++, d2.intValue() + sumcoef);
		}
		
		if(headerHeight > 1){
			Double x = (row.getHeight() * headerHeight) / 1.5;
			row.setHeight(x.shortValue());
		}
		
		for (D2DepositKey[] innerKeys : keys) {
			row = sheet.createRow(rowCount++);
			String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
			int l = AonStringUtils.length(description);
			if (l != 0) {
				int r = (int) (l / 50) + 1;
				int h = (r * 250);
				row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
			}	
			row.setRowStyle(rowStyle);
			cellCount = 0;
			Cell cell = row.createCell(cellCount++);
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setFont(defaulFont );
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(description);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
			
			for(Integer i = 0; i < dif+2; i++)
				cell = row.createCell(cellCount++);
			
			style = workbook.createCellStyle();
			style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			style.setFont(boxFont);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			String code = innerKeys[0].getCode();
			if(codeLength != 0)
				code = code.substring(0,codeLength);
			cell.setCellValue(code);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			
			for(Integer i = 0; i < number; i++){
				cell = row.createCell(cellCount++);
				String a = getD2Deposit().getMap().get(innerKeys[i].getCode());
				if(a == null) a = "0.0";
				double amount = Double.parseDouble(a);
				style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			}
		}
	}
	
	public void special(Integer div, Integer pageMaxNumber, Integer number, String[] specialStrings, String[] strings,
			D2DepositKey[][] keys, Integer headerHeight1, Integer headerHeight2, Integer codeLength){
		Integer dif = pageMaxNumber - number;
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		XSSFCellStyle headerStyle = calculateHeaderFontSize(pageMaxNumber);

		CellUtil.createCell(row, cellCount, specialStrings[0], headerStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		for(Integer i = 0 ; i < dif; i++)
			sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 6 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+2));
		
		Integer h = 1;
		for(Integer i = 0; i < number; i = i+div){
			CellUtil.createCell(row, cellCount, specialStrings[h], headerStyle);
			h++;
			for(Integer j = 0; j < div; j++)
				sheet.setColumnWidth(cellCount++, 10 * 256);
			
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), dif+i+3, dif+i+div+2));
		}

		if(headerHeight1 > 1){
			Double x = (row.getHeight() * headerHeight1) / 1.5;
			row.setHeight(x.shortValue());
		}
		
		general(pageMaxNumber, number, strings, keys, headerHeight2, codeLength);
	}
	
	public void special2(Integer pageMaxNumber, Integer number, String title, String[] strings,	D2DepositKey[][] keys,
			 Integer headerHeight1, Integer headerHeight2, Integer codeLength){
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		XSSFCellStyle headerStyle = calculateHeaderFontSize(pageMaxNumber);
		
		CellUtil.createCell(row, cellCount, title, headerStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		for(Integer i = 0 ; i < pageMaxNumber; i++)
			sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, pageMaxNumber+2));
		
		if(headerHeight1 > 1){
			Double x = (row.getHeight() * headerHeight1) / 1.5;
			row.setHeight(x.shortValue());
		}
	
		general(pageMaxNumber, number, strings, keys, headerHeight2, codeLength);
	}
	
	private void freeText(String title, D2DepositKey key) {
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, title, headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		row = sheet.createRow(rowCount++);
		String text = getD2Deposit().getMap().get(key.getCode());
		if(text == null) text = "";
		
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cell.setCellStyle(style);
		cell.setCellValue(text);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		Integer height = row.getHeight() * 20;
		row.setHeight(height.shortValue());
	}
	
	private void ssHeader(String name, Integer pageMaxNumber){
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		XSSFCellStyle headerStyle = calculateHeaderFontSize(3);
		CellUtil.createCell(row, cellCount, name, headerStyle);
		for(Integer i = 0 ; i < 8; i++)
			sheet.setColumnWidth(cellCount++, 11 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
	}
	
	private void ssHeader(String[] strings, Integer number, Integer height){
		//pre - number tiene que ser par!!
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		if(height > 1){
			Double x = (row.getHeight() * height) / 1.5;
			row.setHeight(x.shortValue());
		}
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		XSSFCellStyle headerStyle = calculateHeaderFontSize(3);
		
		if((number % 2) == 0){
			for(Integer i = 0; i < 8; i = i+(8/number)){
				CellUtil.createCell(row, cellCount, strings[i/(8/number)], headerStyle);	
				for(Integer j = 0; j< (8/number); j++)
					sheet.setColumnWidth(cellCount++, 11 * 256);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i, i+(8/number)-1));
			}
		}
	}
	
	private void idaRow(Integer n, String[] strings) {
		row = sheet.createRow(rowCount++);
		row.setRowStyle(rowStyle);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		cellCount = 0;
		for(Integer i = 0; i < n; i++){
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(style);
			cell.setCellValue(strings[i]);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i*(8/n), i*(8/n) +(8/n)-1));
			for(Integer j = 0; j < (8/n)-1; j++){
				row.createCell(cellCount++);
			}
		}
	}
	
	private void idacell(String value, Integer start, Integer end) {
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		cell.setCellStyle(style);
		cell.setCellValue(value);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));
		for(Integer j = 0; j < end - start; j++){
			row.createCell(cellCount++);
		}
	}
	
	private void macell(String value, Integer start, Integer end) {
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cell.setCellStyle(style);
		cell.setCellStyle(style);
		cell.setCellValue(value);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));
		for(Integer j = 0; j < end - start; j++){
			row.createCell(cellCount++);
		}
	}

	
	public void IDA() {
		Integer pageMaxNumber = 5;
		addSheet("Hoja identificativa de la sociedad");
		header(pageMaxNumber);
		ssHeader("Identificación", pageMaxNumber);
	
		String sa = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01011.getCode());
		Boolean a = sa != null && sa.equals("1");
		if(a) sa = TIC; else sa = "-";
		String sl = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01012.getCode());
		Boolean b = sl != null && sl.equals("1");
		if(b) sl = TIC; else sa = "-";
		String other = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01013.getCode());
		idaRow(2, new String[]{"N.I.F. " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01010.getCode()),
				"SA " + sa + " SL " + sl + " Otras " + other});
		
		idaRow(2, new String[]{"Razón social " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01020.getCode()),
				"Domicilio social "+ getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01022.getCode())});
		
		idaRow(2, new String[]{"Municipio " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01023.getCode()),
				"Provincia "+ getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01025.getCode())});
		
		idaRow(2, new String[]{"Código postal " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01024.getCode()),
				"Teléfono " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01031.getCode())});
		
		idaRow(2, new String[]{"Dirección de e-mail de contacto de la empresa",
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01037.getCode())});
		
		row = sheet.createRow(rowCount++);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Pertenencia a un grupo de sociedades", 0, 2);
		idacell("Denominación social",3,4);
		idacell("NIF",5,6);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Sociedad dominante directa", 0, 2);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01041.getCode()),3,4);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01040.getCode()),5,6);

		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Sociedad dominante última del grupo", 0, 2);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01061.getCode()),3,4);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01060.getCode()),5,6);

		row = sheet.createRow(rowCount++);
		ssHeader("Actividad", pageMaxNumber);
		
		idaRow(2, new String[]{"Código CNAE",getD2Deposit().getMap().get(D2DepositHeaderKey.IDA02001.getCode()) +"-"+
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA02009.getCode())});
		row = sheet.createRow(rowCount++);
		ssHeader("Personal asalariado", pageMaxNumber);
		
		idaRow(1, new String[]{"a) Número medio de personas empleadas en el curso del ejercicio, por tipo de contrato y empleo con discapacidad"});
		idaRow(3, new String[]{"", "Ejercicio "+ getD2Deposit().getYear(),
				"Ejercicio "+ (getD2Deposit().getYear()-1)});
		idaRow(3, new String[]{"FIJO", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04001.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040019.getCode())});
		idaRow(3, new String[]{"NO FIJO", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04002.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040029.getCode())});
	
		idaRow(1, new String[]{"Del cual: Personas empleadas con discapacidad mayor o igual al 33% (o calificación equivalente local):"});
		idaRow(3 , new String[]{"", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04010.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040109.getCode())});
		
		idaRow(1, new String[]{"b) Personal asalariado al término del ejercicio, por tipo de contrato y por sexo"});
			
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("", 0, 0);
		idacell("Ejercicio "+ getD2Deposit().getYear(),1,2);
		idacell("Ejercicio "+ (getD2Deposit().getYear()-1),3,4);
		
		idaRow(5, new String[]{"","Hombres", "Mujeres","Hombres", "Mujeres"});
		idaRow(5, new String[]{"FIJO",getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04120.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04121.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA041209.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA041219.getCode())});
		idaRow(5, new String[]{"NO FIJO",getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04122.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04123.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA041229.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA041239.getCode())});
		
		row = sheet.createRow(rowCount++);
		ssHeader("Presentación de cuentas", pageMaxNumber);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("", 0, 3);
		idacell("Ejercicio "+ getD2Deposit().getYear(),4,5);
		idacell("Ejercicio "+ (getD2Deposit().getYear()-1),6,7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Fecha de inicio a la que van referidas las cuentas", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01102.getCode()),4,5);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA011029.getCode()),6,7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Fecha de cierre a la que van referidas las cuentas", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01101.getCode()),4,5);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA011019.getCode()),6,7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Número de páginas presentadas al depósito", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01901.getCode()),4,5);
		
		row = sheet.createRow(rowCount++);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		cellCount = 0;
		idacell("En caso de no figurar consignadas cifras en alguno de los ejercicios, indique la causa", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01903.getCode()),4,5);

		row = sheet.createRow(rowCount++);
		ssHeader("Unidades", pageMaxNumber);
		String euros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09001.getCode());
		Boolean a1 = sa != null && sa.equals("1");
		if(a1) euros = TIC; else sa = "-";
		String milesEuros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09002.getCode());
		Boolean b1 = sa != null && sa.equals("1");
		if(b1) milesEuros = TIC; else sa = "-";
		String millonesEuros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09003.getCode());
		Boolean c1 = sa != null && sa.equals("1");
		if(c1) millonesEuros = TIC; else sa = "-";
		
		idaRow(1, new String[]{"Euros " + euros + " Miles de euros " + milesEuros
				+" Millones de euros " + millonesEuros});	
	}
	
	public void BA(){
		BA1();
		BA2();
	}
	
	public void BA1() {
		addSheet("Balance Activo");
		header(5);
		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.BALANCE_ACTIVE_PYMES_KEYS;
		else keys = D2DepositConstants.BA_ABREVIATE_KEYS_1;

		three("Balance: Activo", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, new D2DepositHeaderKey[][]{});
	}

	public void BA2() {
		addSheet("Balance Pasivo");
		header(5);
		D2DepositHeaderKey[][] keys;
		D2DepositHeaderKey[][] keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_2;
		}
		else{
			keys = D2DepositConstants.BA_ABREVIATE_KEYS_2;
			keys2 = D2DepositConstants.BA_ABREVIATE_KEYS_3;
		}

		three("Balance: Patrimonio Neto y Pasivo", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, keys2);
	}
	
	public void PYG() {
		addSheet("Cuenta de pérdidas y ganancias");
		header(5);
		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.PYG_PYMES_KEYS;
		else keys = D2DepositConstants.PYG_ABREVIATE_KEYS;

		three("(Debe)/ Haber", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, new D2DepositHeaderKey[][]{});
	}
	
	public void ECPN(){
		ECPN1();
		ECPN2();
	}
	
	public void ECPN1() {
		if (!d2Deposit.getType().equalsIgnoreCase("PYMES")){
			addSheet("ECPN. Estado de ingresos y gastos reconocidos en el ejercicio");
			header(5);
			D2DepositHeaderKey[][] keys = D2DepositConstants.ECPN_INCOMES_KEYS;

			three("ECPN. Estado de ingresos y gastos reconocidos en el ejercicio", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, new D2DepositHeaderKey[][]{});
		}
	}
	
	public void ECPN2() {
		D2DepositHeaderKey[][] keys;
		if (!d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2DepositConstants.ECPN_ABREVIATE_KEYS;	
		}else keys = D2PDepositConstants.ECPN_PYMES_KEYS;
		
		addSheet("ECPN I. Estado total de cambios en el patrimonio neto");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		
		generalHeader(pageMaxNumber, 7, new String[]{"","Capital escriturado", "Capital no exigido",
				"Prima de emisión", "Reservas", "Acciones y participaciones en pratimonio propias",
				"Resultados de ejercicios anteriores", "Otras aportaciones de socios"}, keys, 6, false,3);
		
		addSheet("ECPN II. Estado total de cambios en el patrimonio neto");
		pageMaxNumber = 6;
		header(pageMaxNumber);
		
		generalHeader(pageMaxNumber, 6, new String[]{"","Resultados del ejercicio", "Dividendo a cuenta",
				"Otros instrumentos de patrmonio neto", "Ajustes por cambios de valor", "Subvenciones, donaciones y legados recibidos",
				"Total"}, keys, 7, true, 3);
	}
	
	public void DM() {
		String as = getD2Deposit().getMap().get(D2DepositHeaderKey.IMA8099000.getCode());
		String bs = getD2Deposit().getMap().get(D2DepositHeaderKey.IMA8099010.getCode());
		Boolean a = as != null && as.equals("1");
		Boolean b = bs != null && bs.equals("1");
		addSheet("Declaración medioambiental");
		header(5);
		String text1 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes "
				+ "cuentas anuales NO existe ninguna partida de naturaleza medioambiental"
				+ " que deba ser incluída de acuerdo a la norma de elaboración '4º Cuentas"
				+ " anuales abreviadas' en su punto 5, de la tercera parte del Plan General"
				+ " de Contabilidad (Real Decreto 1514/2007 de 16 de Noviembre).";
		String text2 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes"
				+ " cuentas anuales SI existen partidas de naturaleza medioambiental,"
				+ " y han sido incluídas en un Apartado adiciones de la Memoria de"
				+ " acuerdo a la norma de elaboración '4º Cuentas anuales abreviadas'"
				+ " en su punto 5, de la tercera parte del Plan General de Contabilidad "
				+ "(Real Decreto 1514/2007 de 16 de Noviembre).";
		String tic = TIC;
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Declaración Medioambiental", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));

		row = sheet.createRow(rowCount++);
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(style);
		cell.setCellValue(text1);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
	
		Integer height = row.getHeight() * 4;
		row.setHeight(height.shortValue());

		row = sheet.createRow(rowCount++);
		row.setRowStyle(rowStyle);
		cellCount = 0;
		cell = row.createCell(cellCount++);
		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(CellStyle.ALIGN_CENTER );
		cell.setCellStyle(style);
		cell.setCellValue(a ? tic : "-");
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		row = sheet.createRow(rowCount++);
		row.setRowStyle(rowStyle);
		cellCount = 0;
		cell = row.createCell(cellCount++);
		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(style);
		cell.setCellValue(text2);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		row.setHeight(height.shortValue());
		
		row = sheet.createRow(rowCount++);
		row.setRowStyle(rowStyle);
		cellCount = 0;
		cell = row.createCell(cellCount++);
		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(CellStyle.ALIGN_CENTER );
		cell.setCellStyle(style);
		cell.setCellValue(b ? tic : "-");
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
	}
	
	public void AP1(){
		addSheet("Apartado 1 - Actividad de la empresa");
		freeText("Apartado 1 - Actividad de la empresa", D2DepositKey.MAT19019001);
	}
	
	public void AP2(){
		addSheet("Apartado 2 - Bases de presentación de las cuentas anuales");
		freeText("Apartado 2 - Bases de presentación de las cuentas anuales", D2DepositKey.MAT29029001);
	}
	
	public void AP3(){
		AP3A();
		AP3B();
	}
	
	public void AP3A(){
		addSheet("Apartado 3 - Texto Libre");
		freeText("Apartado 3 - Aplicación de resultados", D2DepositKey.MAT39039001);
	}
	
	public void AP3B(){
		addSheet("Apartado 3 - Cuadros Normalizados");
		Integer pageMaxNumber = 2; 
		header(pageMaxNumber);
		D2DepositKey[][] keys;
		D2DepositKey[][] keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN_PYMES_KEYS_2;
		}
		else{
			keys = D2DepositConstants.MRN_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN_ABREVIATE_KEYS_2;		
		}
		// 1
		general(pageMaxNumber, 2, new String[]{"BASES DE REPARTO", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 2, new String[]{"APLICACIÓN A", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 1, 0);
	}
	
	public void AP4(){
		addSheet("Apartado 4 - Normas de registro y valoración");
		freeText("Apartado 4 - Normas de registro y valoración", D2DepositKey.MAT49049001);
	}
	
	public void AP5(){
		AP5A();
		AP5B();
	}
	
	public void AP5A(){
		addSheet("Apartado 5 - Texto Libre");
		freeText("Apartado 5 - Inmovilizado material, intangible, e inversiones inmobiliarias", D2DepositKey.MAT59059001);
	}
	
	public void AP5B(){
		addSheet("Apartado 5 - Cuadros Normalizados");
		Integer pageMaxNumber = 3;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_1;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys3 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_3;
		
		// 2
		general(pageMaxNumber, 3, new String[]{"Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio actual",
			"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys, 2, 4);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 3, new String[]{"Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio anterior",
				"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys2, 2, 4);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 1, new String[]{"Arrendamientos financieros y otras operaciones de naturaleza similar sobre activos no corrientes","Total Contratos"},
				keys3, 1, 0);
	}
	
	public void AP6(){
		AP6A();
		AP6B();
		AP6C();
	}
	
	public void AP6A(){
		addSheet("Apartado 6 - Texto Libre");
		freeText("Apartado 6 - Activos financieros", D2DepositKey.MAT69069001);
	}
	
	public void AP6B(){
		addSheet("Apartado 6.1 - Cuadros Normalizados");
		Integer pageMaxNumber = 8;
		header(pageMaxNumber);
		D2DepositKey[][] keys, keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN6_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN6_PYMES_KEYS_2;
		}
		else{
			keys = D2DepositConstants.MRN6_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN6_ABREVIATE_KEYS_2;		
		}
		D2DepositKey[][] keys3 = D2DepositConstants.MRN6_ABREVIATE_KEYS_3;
		
		String current = "Ejercicio " + getD2Deposit().getYear();
		String previous = "Ejercicio " + (getD2Deposit().getYear()-1);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Activos financieros a largo plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.",
				"Instrumentos de Patrimonio", "Valores representativos de deuda", "Créditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2, 4);
		sheet.createRow(rowCount++);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Activos financieros a corto plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.",
				"Instrumentos de Patrimonio", "Valores representativos de deuda", "Créditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2, 4);
		sheet.createRow(rowCount++);
		
		// 1 11
		special(3, pageMaxNumber, 6, new String[]{"Traspasos o reclasificaciones de activos financieros",current, previous},
				new String[]{"","Inversiones mantenidas hasta el vencimiento","Inversiones en el patrimonio de empresa del grupo, multigrupo y asociadas",
						"Activos financieros disponibles para la venta", "Inversiones mantenidas hasta el vencimiento",
						"Inversiones en el patrimonio de empresa del grupo, multigrupo y asociadas", "Activos financieros disponibles para la venta"},
				keys3, 1, 11, 4);
	}
	
	public void AP6C(){
		addSheet("Apartado 6.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 6;
		header(pageMaxNumber);
		D2DepositKey[][] keys, keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN6_PYMES_KEYS_4;
			keys2 = D2PDepositConstants.MRN6_PYMES_KEYS_5;
		}
		else{
			keys = D2DepositConstants.MRN6_ABREVIATE_KEYS_4;
			keys2 = D2DepositConstants.MRN6_ABREVIATE_KEYS_5;		
		}
		D2DepositKey[][] keys3 = D2DepositConstants.MRN6_ABREVIATE_KEYS_6;
		
		String current = "Largo plazo";
		String previous = "Corto plazo";

		// 3 2
		special(2, pageMaxNumber, 6, new String[]{"Correcciones por deterioro del valor originadas por el riesgo de crédito",
				"Valores representativos de deuda", "Créditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous}, keys,3, 2, 4);
		sheet.createRow(rowCount++);
		// 8
		general(pageMaxNumber, 4, new String[]{"Correcciones por deterioro del valor originadas por el riesgo de crédito"
				, "Activos a valor razonable con cambios en pérdidas y ganancias", "Activos mantenidos para negociar"
				, "Activos disponibles para la venta", "TOTAL"}, keys2, 8, 4);
		sheet.createRow(rowCount++);
		// 9
		general(pageMaxNumber, 6, new String[]{"Correcciones valorativas por deterioro registradas en las distintas participaciones",
				"Pérdidas por deteriodo al final del ejercicio X", "(+/-) Variación deteriodo a pérdidas y ganancias",
				"(+) Variación contra patrimonio neto", "(-) Salidas y reducciones", "(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)",
				"Pérdida por deteriodo al final del ejercicio Y"}, keys3, 9, 4);
	}
	
	public void AP7(){
		AP7A();
		AP7B();
	}
	
	public void AP7A(){
		addSheet("Apartado 7 - Texto Libre");
		freeText("Apartado 7 - Pasivos financieros", D2DepositKey.MAT79079001);	
	}
	
	public void AP7B(){
		addSheet("Apartado 7 - Cuadros Normalizados");
		Integer pageMaxNumber = 8;
		header(pageMaxNumber);
		
		D2DepositKey[][] keys, keys2, keys3;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN7_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN7_PYMES_KEYS_2;
			keys3 = D2PDepositConstants.MRN7_PYMES_KEYS_3;
		} else{
			keys = D2DepositConstants.MRN7_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN7_ABREVIATE_KEYS_2;
			keys3 = D2DepositConstants.MRN7_ABREVIATE_KEYS_3;
		}
		D2DepositKey[][] keys4 = D2DepositConstants.MRN7_ABREVIATE_KEYS_4;
		
		String current = "Ejercicio " + getD2Deposit().getYear();
		String previous = "Ejercicio " + (getD2Deposit().getYear()-1);
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a largo plazo", "Deudas con entidades de crédito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2, 4);
		sheet.createRow(rowCount++);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a corto plazos", "Deudas con entidades de crédito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2, 4);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 7, new String[]{"Vencimiento de las deudas al cierre del ejercicio"+getD2Deposit().getYear(),
				"Uno", "Dos", "Tres", "Cuatro", "Cinco", "Más de 5", "TOTAL"}, keys3, 1, 4);
		sheet.createRow(rowCount++);
		// 3
		general(pageMaxNumber, 3, new String[]{"Lineas de descuento y pólizas al cierre del ejercicio"+ getD2Deposit().getYear(),
				"Límite concedido", "Dispuesto", "Disponible"},keys4, 3, 4);
	}
	
	public void AP8(){
		addSheet("Apartado 8 - Fondos propios");
		freeText("Apartado 8 - Fondos propios", D2DepositKey.MAT89089001);	
	}
	
	public void AP9(){
		addSheet("Apartado 9 - Situación fiscal");
		freeText("Apartado 9 - Situación fiscal", D2DepositKey.MAT99099001);	
	}
	
	public void AP10(){
		addSheet("Apartado 10 - Ingresos y gastos");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN10_ABREVIATE_KEYS;
		// 1
		general(pageMaxNumber, 2, new String[]{"Detalle de la cuenta de pérdidas y ganancias", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0);
	}
	
	public void AP11(){
		AP11A();
		AP11B();
	}
	
	public void AP11A(){
		addSheet("Apartado 11 - Texto Libre");
		freeText("Apartado 11 - Subvenciones, donaciones y legados", D2DepositKey.MAT119119001);		
	}
	
	public void AP11B(){
		addSheet("Apartado 11 - Cuadros Normalizados");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys;
		D2DepositKey[][] keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN11_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN11_PYMES_KEYS_2;
		}
		else{
			keys = D2DepositConstants.MRN11_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN11_ABREVIATE_KEYS_2;		
		}
		// 1
		general(pageMaxNumber, 2, new String[]{"Subvenciones, donaciones y legados recibidos, otorgados por terceros distintos de los socios", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Subvenciones, donaciones y legados recogidos en el patrimonio neto del balance, otorgados por terceros distintos a los socios: análisis del movimiento", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 2, 0);
	}
	
	public void AP12(){
		AP12A();
		AP12B();
		AP12C();
		AP12D();
		AP12E();
		AP12F();
	}
	
	public void AP12A(){
		addSheet("Apartado 12 - Texto Libre");
		freeText("Apartado 12 - Operaciones con partes vinculadas", D2DepositKey.MAT129129001);	
	}
	
	public void AP12B(){
		addSheet("Apartado 12.1 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1;
		// 11
		general(pageMaxNumber, 7, new String[]{"Operaciones con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4);
	}
	
	public void AP12C(){
		addSheet("Apartado 12.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2;
		
		general(pageMaxNumber, 7, new String[]{"Operaciones con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4);
	}
	
	public void AP12D(){
		addSheet("Apartado 12.3 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		D2DepositKey [][] keys;
		if (getD2Deposit().getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.MRN12_PYMES_KEYS_3;
		else keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_3;
				
		general(pageMaxNumber, 7, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4);
	}
	
	public void AP12E(){
		addSheet("Apartado 12.4 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		D2DepositKey [][] keys;
		if (getD2Deposit().getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.MRN12_PYMES_KEYS_4;
		else keys = D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4;

		general(pageMaxNumber, 7, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4);
	}
	
	public void AP12F(){
		addSheet("Apartado 12.5 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		
		D2DepositKey [][] keys, keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN12_PYMES_KEYS_5;
			keys2 = D2PDepositConstants.MRN12_PYMES_KEYS_6;
		}
		else{
			keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_5;
			keys2 = D2DepositConstants.MRN12_ABREVIATE_KEYS_6;		
		}
		// 2
		general(pageMaxNumber, 2, new String[]{"Importes recibidos por el personal de alta dirección",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys, 2, 0);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Importes recibidos por los miembros de los órganos de administración",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys2, 2, 0);
	}
	
	public void AP13(){
		AP13A();
		AP13B();
	}
	
	public void AP13A(){
		addSheet("Apartado 13 - Texto Libre");
		freeText("Apartado 13 - Otra información", D2DepositKey.MAT139139001);	
	}
	
	public void AP13B(){
		addSheet("Apartado 13 - Cuadros Normalizados");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN13_ABREVIATE_KEYS;
		
		// 2
		general(pageMaxNumber, 2, new String[]{"Número medio de personas empleadas en el curso del ejercicio, por categorías (adaptadas a la CNO-11)", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 2, 0);
	}
	
	public void AP14(){
		AP14A();
		AP14B();
		AP14C();
	}
	
	public void AP14A(){
		addSheet("Apartado 14 - Texto Libre");
		freeText("Apartado 14 - Información sobre medio ambiente", D2DepositKey.MAT149149001);		}
	
	public void AP14B(){
		addSheet("Apartado 14.1 - Cuadros Normalizados");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_1;
		//  1
		general(pageMaxNumber, 2, new String[]{"DESCRIPCIÓN DEL CONCEPTO", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0);
	}
	
	public void AP14C(){
		addSheet("Apartado 14.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 1;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_3;	
		// 1 1
		special2(pageMaxNumber, 1,"Movimiento durante el ejercicio" ,new String[]{"DERECHOS DE EMISIÓN DE GASES DE EFECTO INVERNADERO", "Importe"}, keys, 1, 1, 0);
		sheet.createRow(rowCount++);
		// 1 1 
		special2(pageMaxNumber, 1, "Otra Información", new String[]{"CONCEPTO", "Importe"}, keys2, 1 ,1, 0);
	}
	
	public void AP15(){
		addSheet("Apartado 15 - Información sobre los aplazamientos de pago efectuados a proveedores");
		Integer pageMaxNumber = 4;
		header(pageMaxNumber);
		
		D2DepositKey[][] keys = D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS;
		// 1 1 
		special(2, pageMaxNumber, 4, new String[]{"Pagos realizados y pendientes de pago en la fecha de cierre del Balance",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio " + (getD2Deposit().getYear()-1)}, 
				new String[]{"PAGOS DEL EJERCICIO", "Importe", "%", "Importe",  "%"}, keys, 1, 1, 0);		
	}
	
	public void MA(){
		addSheet("Modelo de autocartera");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Modelo de autocartera", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		row = sheet.createRow(rowCount++);
		String text = "La sociedad no ha realizado durante el presente ejercicio operación alguna sobre acciones / participaciones propias";		

		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		cell.setCellStyle(style);
		cell.setCellValue(text);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		
		String a = getD2Deposit().getMap().get(D2DepositFooterKey.A18009050.getCode());
		Boolean a2 = a != null && a.equals(1);
		String value = "-";
		if(a2) value = TIC;
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		cell = row.createCell(cellCount++);
		cell.setCellStyle(style);
		cell.setCellValue(value);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		if(!a2){
			MA1();
			MA11();
			MA2();
			MA3();
			MA4();
			MA5();
			MA6();
			MA7();
		}
	}
	
	
	private void MA1() {
		addSheet("Página A1");
		header(5);
		ssHeader("Modelo de autocartera", 5);
		
		String t1 = getD2Deposit().getMap().get(D2DepositFooterKey.A18009010.getCode());
		String t2 = getD2Deposit().getMap().get(D2DepositFooterKey.A18009020.getCode());
		String t3 = getD2Deposit().getMap().get(D2DepositFooterKey.A18009030.getCode());
		String t4 = getD2Deposit().getMap().get(D2DepositFooterKey.A18009040.getCode());
	
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		idacell("Saldo al cierre del ejercicio precedente", 0, 1);
		idacell(t1, 2, 3);
		idacell("Acciones / participaciones", 4, 5);
		idacell(t2, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Saldo al cierre del ejercicio", 0, 1);
		idacell(t3, 2, 3);
		idacell("Acciones / participaciones", 4, 5);
		idacell(t4, 6, 7);
		
		row = sheet.createRow(rowCount++);
		ssHeader(new String[]{"Fecha", "Concepto", "Fecha de acuerdo de junta general", "Nº Acciones / participaciones",
				"Nominal", "Capital social %", "Precio o contraprestación", "Saldo después de operación"}, 8, 3);
		
		for(Integer i = 0; i< D2DepositConstants.A1_ABREVIATE_KEYS_1.length; i+=8){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+4],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+5],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+6],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+7],
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j, j);
			}
		}
		
	

	}
	
	private void MA11() {
		addSheet("Página A1.1");
		header(5);
		ssHeader(new String[]{"Fecha", "Concepto", "Fecha de acuerdo de junta general", "Nº Acciones / participaciones",
				"Nominal", "Capital social %", "Precio o contraprestación", "Saldo después de operación"}, 8, 3);

		for(Integer i = 0; i< D2DepositConstants.A11_ABREVIATE_KEYS.length; i+=8){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A11_ABREVIATE_KEYS[i],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+3],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+4],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+5],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+6],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+7]
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j, j);
			}
		}
	}
	
	private void MA2() {
		addSheet("Página A2");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Transcripción de acuerdos de Juntas generales, del último o anteriores ejercicios, autorizando negocios sobre acciones o participaciones propias realizados en el último ejercicio cerrado.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha acuerdo", "Transcripción literal del acuerdo"}, 2, 1);
		
		for(Integer i = 0; i< D2DepositConstants.A2_ABREVIATE_KEYS.length; i+=2){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A2_ABREVIATE_KEYS[i],
					D2DepositConstants.A2_ABREVIATE_KEYS[i+1]
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*4, (j*4)+3);
			}
		}
	}
	
	private void MA3() {
		addSheet("Página A3");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Relación de acciones o participaciones adquiridas al amparo de los artículos 140, 144 y 146 de la Ley de SOciedades de Capital, durante el ejercicio.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha", "Relación numerada de las acciones / participaciones", "Título de adquisición", "% sobre capital"}, 4, 2);

		for(Integer i = 0; i< D2DepositConstants.A3_ABREVIATE_KEYS.length; i+=4){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A3_ABREVIATE_KEYS[i],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+3]
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*2, (j*2)+1);
			}
		}
	}
	
	private void MA4() {
		addSheet("Página A4");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Relación de acciones o participaciones adquiridas por los mismo títulos, enajenadas o amortizadas durante el presente ejercicio.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha", "Relación numerada de las acciones / participaciones", "Título de adquisición", "% sobre capital"}, 4, 2);

		for(Integer i = 0; i< D2DepositConstants.A3_ABREVIATE_KEYS.length; i+=4){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A3_ABREVIATE_KEYS[i],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+3]
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*2, (j*2)+1);
			}
		}
	}
	
	private void MA5() {
		addSheet("Página A5");
		header(6);
		
		row = sheet.createRow(rowCount++);cellCount = 0;
		for (int i = 0; i < row.getLastCellNum(); i++) 
			sheet.autoSizeColumn(i);
		XSSFCellStyle headerStyle = calculateHeaderFontSize(3);
		CellUtil.createCell(row, cellCount, "Modelo de autocartera", headerStyle);
		for(Integer i = 0 ; i < 9; i++)
			sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 8));
		
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Negocios que han implicado la aceptación en garantía de acciones propias, con las excepciones legales (artículo 149 de la Ley de Sociedades de Capital).", 0, 8);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Fecha", "Descripción del negocio", "Número de acciones dadas en garantía"};
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		row.setHeight(x.shortValue());
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		headerStyle = calculateHeaderFontSize(3);
		
		for(Integer i = 0; i < 9; i = i+3){
			CellUtil.createCell(row, cellCount, strings[i/3], headerStyle);	
			for(Integer j = 0; j< 3; j++)
				sheet.setColumnWidth(cellCount++, 10 * 256);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i, i+2));
		}	
		
		for(Integer i = 0; i< D2DepositConstants.A5_ABREVIATE_KEYS.length; i+=3){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A5_ABREVIATE_KEYS[i],
					D2DepositConstants.A5_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A5_ABREVIATE_KEYS[i+2],
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*3, (j*3)+2);
			}
		}	
	}
	
	private void MA6() {
		addSheet("Página A6");
		header(6);
		
		row = sheet.createRow(rowCount++);cellCount = 0;
		for (int i = 0; i < row.getLastCellNum(); i++) 
			sheet.autoSizeColumn(i);
		XSSFCellStyle headerStyle = calculateHeaderFontSize(3);
		CellUtil.createCell(row, cellCount, "Modelo de autocartera", headerStyle);
		for(Integer i = 0 ; i < 9; i++)
			sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 8));
		
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Negocios que han implicado la asistencia finanaciera para la adquisicin de acciones propias salvo las excepciones legales (artículo 150 de la Ley de Sociedades de Capital).", 0, 8);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Fecha", "Descripción del negocio", "Número de acciones dadas en garantía"};
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		row.setHeight(x.shortValue());
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		headerStyle = calculateHeaderFontSize(3);
		
		for(Integer i = 0; i < 9; i = i+3){
			CellUtil.createCell(row, cellCount, strings[i/3], headerStyle);	
			for(Integer j = 0; j< 3; j++)
				sheet.setColumnWidth(cellCount++, 10 * 256);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i, i+2));
		}	
		
		for(Integer i = 0; i< D2DepositConstants.A6_ABREVIATE_KEYS.length; i+=3){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A6_ABREVIATE_KEYS[i],
					D2DepositConstants.A6_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A6_ABREVIATE_KEYS[i+2],
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*3, (j*3)+2);
			}
		}	
	}
	
	private void MA7() {
		addSheet("Página A7");
		header(7);
		
		row = sheet.createRow(rowCount++);cellCount = 0;
		for (int i = 0; i < row.getLastCellNum(); i++) 
			sheet.autoSizeColumn(i);
		XSSFCellStyle headerStyle = calculateHeaderFontSize(3);
		CellUtil.createCell(row, cellCount, "Modelo de autocartera", headerStyle);
		for(Integer i = 0 ; i < 10; i++)
			sheet.setColumnWidth(cellCount++, 9 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 9));
		
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Supuestos de infracción de las normas sobre participaciones recíprocas de capital (artículo 151 y siguiente de la Ley de Sociedades de Capital).", 0, 9);
		Double x1 = (row.getHeight() * 2) / 1.5;
		row.setHeight(x1.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Sociedad Comunicante", "Fecha Comunicación", "Porcentaje de participación en su capital a esa fecha", "Fecha Reducción", "Porcentaje Posterior"};
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		Double x = (row.getHeight() * 3) / 1.5;
		row.setHeight(x.shortValue());
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		headerStyle = calculateHeaderFontSize(3);
		
		for(Integer i = 0; i < 10; i = i+2){
			CellUtil.createCell(row, cellCount, strings[i/2], headerStyle);	
			for(Integer j = 0; j< 2; j++)
				sheet.setColumnWidth(cellCount++, 9 * 256);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i, i+1));
		}	
		
		for(Integer i = 0; i< D2DepositConstants.A7_ABREVIATE_KEYS.length; i+=5){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A7_ABREVIATE_KEYS[i],
					D2DepositConstants.A7_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A7_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A7_ABREVIATE_KEYS[i+3],
					D2DepositConstants.A7_ABREVIATE_KEYS[i+4]
			};
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			for (Integer j = 0; j < d2.length ;j++) {
				String text = getD2Deposit().getMap().get(d2[j].getCode());
				macell(text, j*2, (j*2)+1);
			}
		}
	}
	
	public void IP(){
		addSheet("Instancia de Presentación");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("SOLICITUD DE PRESENTACIÓN EN EL REGISTRO MERCANTIL DE " + getD2Deposit().getMap().get(D2DepositFooterKey.PR8081001.getCode()), 0, 7);
		
		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACIÓN DE LA ENTIDAD QUE PRESENTA LAS CUENTAS A DEPÓSITO", 5);
		row = sheet.createRow(rowCount++);
		
		cellCount = 0;
		idacell("Denominación de la entidad", 0, 2);
		idacell("", 3, 4);
		idacell("N.I.F.", 5, 5);
		idacell("", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Datos Registrales", 0, 2);
		idacell("", 3, 4);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Tomo", 0, 1);
		idacell("", 2, 3);
		idacell("Folio", 4, 5);
		idacell("", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;		
		idacell("Nº Hoja registral", 0, 1);
		idacell("", 2, 3);
		idacell("Fecha de cierre ejercicio social", 4, 5);
		idacell("", 6, 7);

		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACIÓN DE LOS DOCUMENTOS CONTABLES CUYO DEPÓSITO SE SOLICITA", 5);

		row = sheet.createRow(rowCount++);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		cellCount = 0;
		idacell("Balance", 0, 0);
		idacell("Pérdidas y Ganancias", 1, 2);
		idacell("Memoria", 3, 3);
		idacell("Estado cambios patrimonio neto", 4, 5);
		idacell("Estado de Flujos de efectivo", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("- Normal", 0, 0);
		idacell("- Normal", 1, 2);
		idacell("- Normal", 3, 3);
		idacell("- Normal", 4, 5);
		idacell("- Normal", 6, 7);

		Boolean b = d2Deposit.getType().equalsIgnoreCase("PYMES");

		row = sheet.createRow(rowCount++);
		cellCount = 0;
		String s = "-";
		if(!b) s = TIC;
		idacell(s + " Abreviado", 0, 0);
		idacell(s + " Abreviado", 1, 2);
		idacell(s + " Abreviado", 3, 3);
		idacell(s + " Abreviado", 4, 5);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		if(b) s = TIC;
		idacell(s + " PYME", 0, 0);
		idacell(s + " PYME", 1, 2);
		idacell(s + " PYME", 3, 3);
		idacell(s + " PYME", 4, 5);
		
		row = sheet.createRow(rowCount++);
		row.setHeight(x.shortValue());
		cellCount = 0;
		String a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080800.getCode());
		String b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080819.getCode());
		String c1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080807.getCode());
		String d1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080817.getCode());
		idacell(getBoolText(a1) + " Hoja de Identificación de la sociedad", 0, 1);
		idacell(getBoolText(b1) + " Declaración medioambiental", 2, 3);
		idacell(getBoolText(c1) + " Informe de gestion", 4, 5);
		idacell(getBoolText(d1) + " Informe de Auditoría", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080809.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080823.getCode());
		c1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080821.getCode());
		d1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080811.getCode());
		idacell(getBoolText(a1) + " Modelo de autocartera", 0, 1);
		idacell(getBoolText(b1) + " Anuncios de convocatoria", 2, 3);
		idacell(getBoolText(c1) + " Certificado SICAV", 4, 5);
		idacell(getBoolText(d1) + " Certificación acuerdo", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("- Otros Documentos", 0, 1);
		idacell("Nº ", 2, 3);
	
		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACIÓN DEL PRESENTANTE QUE HACE LA SOLICITUD", 5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081201.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081202.getCode());
		idacell("Nombre y Apellidos", 0, 1);
		idacell(a1, 2, 3);
		idacell("DNI", 4, 5);
		idacell(b1, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081203.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081205.getCode());
		idacell("Domicilio", 0, 1);
		idacell(a1, 2, 3);
		idacell("Cod. Postal", 4, 5);
		idacell(b1, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081204.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081206.getCode());
		idacell("Ciudad", 0, 1);
		idacell(a1, 2, 3);
		idacell("Provincia", 4, 5);
		idacell(b1, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081208.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081209.getCode());
		idacell("Teléfono", 0, 1);
		idacell(a1, 2, 3);
		idacell("Correo electronico", 4, 5);
		idacell(b1, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081207.getCode());
		idacell("Fax", 0, 1);
		idacell(a1, 2, 3);
	}
	
	public void CHD(){
		addSheet("Certificación de la huella digital");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Nombre de las personas que expiden la certificación", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.setColumnWidth(cellCount++, 4 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		

		D2DepositFooterKey[] keys = D2DepositConstants.H_ABREVIATE_KEYS;
		for (D2DepositFooterKey d2DepositFooterKey : keys) {
			String text = getD2Deposit().getMap().get(d2DepositFooterKey.getCode());
			row = sheet.createRow(rowCount++);
			row.setRowStyle(rowStyle);
			cellCount = 0;
			Cell cell = row.createCell(cellCount++);
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setFont(defaulFont );
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			cell.setCellStyle(style);
			cell.setCellValue(text != null ? text : "");
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));

		}
	}
	
	protected abstract String getTitle();

	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}

	public void setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}

	private String getDescription(String desc) {
		if(desc.contains("@")){
			Integer pos = desc.indexOf("@");
			return desc.substring(0, pos) + getD2Deposit().getYear() + desc.substring(pos+1);
		} else if(desc.contains("¬")){
			Integer pos = desc.indexOf("¬");
			return desc.substring(0, pos) + (getD2Deposit().getYear()-2) + desc.substring(pos+1);
		} else if(desc.contains("#")){
			Integer pos = desc.indexOf("#");
			return desc.substring(0, pos) + (getD2Deposit().getYear()-1) + desc.substring(pos+1);

		}else return desc;	
	}
	
	private String getBoolText(String a) {
		if(a != null && a.equals("1"))
			return TIC;
		else return "-";
	}
}
