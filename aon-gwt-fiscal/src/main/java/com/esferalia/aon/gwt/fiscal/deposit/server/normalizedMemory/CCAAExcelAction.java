package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Cities;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositDescription;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public abstract class CCAAExcelAction extends AbsExcelAction {
	/*
	\u00e1 -> á		\u00c1 -> Á
	\u00e9 -> é	 	\u00c9 -> É
	\u00ed -> í		\u00cd -> Í
	\u00f3 -> ó		\u00d3 -> Ó
	\u00fa -> ú		\u00da -> Ú	
	\u00f1 -> ñ 	\u00d1 -> Ñ
	*/
	public CCAAExcelAction(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}

	protected static final XSSFColor REGISTRADORES = new XSSFColor(new java.awt.Color(Integer.valueOf("fa", 16 )
			,Integer.valueOf("58", 16 ),Integer.valueOf("58", 16 )));

	protected  static final String[] IMAGES = new String[] {
		"/com/esferalia/aon/gwt/common/client/css/images/aon-registro-mercantil-image.png"
	};

	protected static final String TIC = "Si";
	
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
	  dateStyle.setAlignment( HorizontalAlignment.CENTER );

	  numberStyle = workbook.createCellStyle();
	  numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
	  numberStyle.setAlignment( HorizontalAlignment.CENTER );

	  decimalStyle = workbook.createCellStyle();
	  decimalStyle.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	  decimalStyle.setAlignment( HorizontalAlignment.RIGHT );

		centerCellStyle = workbook.createCellStyle();
		centerCellStyle.setAlignment( HorizontalAlignment.CENTER );

		defaulFont= workbook.createFont();
		defaulFont.setFontHeightInPoints((short) 9);
		
		smallFont = workbook.createFont();
		smallFont.setFontHeightInPoints((short) 8);

		boldFont= workbook.createFont();
		boldFont.setFontHeightInPoints((short) 9);
		boldFont.setBold(true);

		rowStyle = (XSSFCellStyle) workbook.createCellStyle();
		rowStyle.setWrapText(true);

		boxFont = workbook.createFont();
		boxFont.setFontHeightInPoints((short) 8);
		boxFont.setColor(IndexedColors.GREY_40_PERCENT.index);

		boxCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		boxCellStyle.setAlignment(HorizontalAlignment.CENTER);
		boxCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		boxCellStyle.setBorderBottom(BorderStyle.THIN);
		boxCellStyle.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		boxCellStyle.setFont(boxFont);

		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 9);

		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.index);
		headerFont.setFontHeightInPoints((short) 9);

		headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setWrapText(true);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
			SXSSFDrawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
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

		CellUtil.createCell(row, n+2, getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), headerCellStyle);
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
				D2DepositHeaderKey[][] keys, D2DepositHeaderKey[][] keys2, Map<Integer, String> rows){
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
		rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

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
				if(rows != null && rows.containsKey(rowCount)){
					while(rows.containsKey(rowCount)){
						int l = AonStringUtils.length(rows.get(rowCount));
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
						cell.setCellStyle(style);
						cell.setCellValue(rows.get(rowCount));
						cell.setCellType(CellType.STRING);
						sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
						row = sheet.createRow(rowCount++);cellCount=0;
					}
				}
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
				style.setBorderBottom(BorderStyle.THIN);
				style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
				cell.setCellStyle(style);
				cell.setCellValue(description);
				cell.setCellType(CellType.STRING);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

				
				cell = row.createCell(cellCount++);
				cell = row.createCell(cellCount++);
				style = workbook.createCellStyle();
				style.setVerticalAlignment(VerticalAlignment.BOTTOM);
				style.setFont(boxFont);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
				cell.setCellStyle(style);
				cell.setCellValue(innerKeys[0].getCode());
				cell.setCellType(CellType.STRING);
			
				cell = row.createCell(cellCount++);
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(c);
				cell.setCellType(CellType.STRING);
			
				cell = row.createCell(cellCount++);
				double amount1 = Double.parseDouble(a);
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount1);
				cell.setCellType(CellType.NUMERIC);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

				cell = row.createCell(cellCount++);
				cell = row.createCell(cellCount++);
				double amount2 = Double.parseDouble(b);
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount2);
				cell.setCellType(CellType.NUMERIC);
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
	
	private XSSFCellStyle calculateHeaderFontSize(Integer pageMaxNumber) {
		XSSFCellStyle style = (XSSFCellStyle) headerCellStyle.clone();
		
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
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
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(description);
			cell.setCellType(CellType.STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
			
			for(Integer i = 0; i < dif+2; i++)
				cell = row.createCell(cellCount++);
			
			style = workbook.createCellStyle();
			style.setVerticalAlignment(VerticalAlignment.BOTTOM);
			style.setFont(boxFont);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);

			cell.setCellStyle(style);
			String code = innerKeys[0].getCode();
			if(codelength != 0)
				code = code.substring(0, codelength);
			cell.setCellValue(code);
			cell.setCellType(CellType.STRING);
			
			if(secondPart){
				for(Integer i = 0; i < number; i++){
					cell = row.createCell(cellCount++);
					String a = getD2Deposit().getMap().get(innerKeys[i+7].getCode());
					if(a == null) a = "0.0";
					double amount = Double.parseDouble(a);
					style.setAlignment(HorizontalAlignment.RIGHT);
					style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					cell.setCellValue(amount);
					cell.setCellType(CellType.NUMERIC);
				}
			} else{
				for(Integer i = 0; i < number; i++){
					cell = row.createCell(cellCount++);
					String a = getD2Deposit().getMap().get(innerKeys[i].getCode());
					if(a == null) a = "0.0";
					double amount = Double.parseDouble(a);
					style.setAlignment(HorizontalAlignment.RIGHT);
					style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					cell.setCellValue(amount);
					cell.setCellType(CellType.NUMERIC);
				}
			}
		}
	}
	
	
	public void general(Integer pageMaxNumber, Integer number, String[] strings, D2DepositKey[][] keys
			,Integer headerHeight, Integer codeLength, Map<Integer, String> rows){
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
			row = sheet.createRow(rowCount++);cellCount=0;
			if(rows != null && rows.containsKey(rowCount)){
				while(rows.containsKey(rowCount)){
					int l = AonStringUtils.length(rows.get(rowCount));
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
					cell.setCellStyle(style);
					cell.setCellValue(rows.get(rowCount));
					cell.setCellType(CellType.STRING);
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
					row = sheet.createRow(rowCount++);cellCount=0;
				}
			}
			
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
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			cell.setCellValue(description);
			cell.setCellType(CellType.STRING);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
			
			for(Integer i = 0; i < dif+2; i++)
				cell = row.createCell(cellCount++);
			
			style = workbook.createCellStyle();
			style.setVerticalAlignment(VerticalAlignment.BOTTOM);
			style.setFont(boxFont);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			String code = innerKeys[0].getCode();
			if(codeLength != 0)
				code = code.substring(0,codeLength);
			cell.setCellValue(code);
			cell.setCellType(CellType.STRING);
			
			for(Integer i = 0; i < number; i++){
				cell = row.createCell(cellCount++);
				String a = getD2Deposit().getMap().get(innerKeys[i].getCode());
				if(a == null) a = "0.0";
				double amount = Double.parseDouble(a);
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount);
				cell.setCellType(CellType.NUMERIC);
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
		
		general(pageMaxNumber, number, strings, keys, headerHeight2, codeLength,null);
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
	
		general(pageMaxNumber, number, strings, keys, headerHeight2, codeLength,null);
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
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(VerticalAlignment.TOP);
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
				if(((i+(8/number)-1) - i) > 0)
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
			cell.setCellType(CellType.STRING);
			if(((i*(8/n) +(8/n)-1) - (i*(8/n))) > 0){
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), i*(8/n), i*(8/n) +(8/n)-1));
				for(Integer j = 0; j < (8/n)-1; j++){
					row.createCell(cellCount++);
				}
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
		cell.setCellType(CellType.STRING);
		if((end - start) > 0) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));
			for(Integer j = 0; j < end - start; j++){
				row.createCell(cellCount++);
			}
		}
	}
	
	private void macell(String value, Integer start, Integer end) {
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		cell.setCellStyle(style);
		cell.setCellStyle(style);
		cell.setCellValue(value);
		cell.setCellType(CellType.STRING);
		if((end - start) > 0) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));
			for(Integer j = 0; j < end - start; j++){
				row.createCell(cellCount++);
			}
		}
	}

	
	public void IDA() {
		Integer pageMaxNumber = 5;
		addSheet("Hoja identificativa de la sociedad");
		header(pageMaxNumber);
		ssHeader("Identificaci\u00f3n", pageMaxNumber);
	
		String sa = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01011.getCode());
		String sl = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01012.getCode());
		String other = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01013.getCode());
		if(other == null || other.equalsIgnoreCase("null")) other = "";
		idaRow(2, new String[]{"N.I.F. " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01010.getCode()),
				"SA " + getBoolText(sa) + " SL " + getBoolText(sl) + " Otras " + other});
		
		if(getD2Deposit().getYear() >= 2015){
			row = sheet.createRow(rowCount++);cellCount=0;
			idacell("LEI", 0, 0);
			idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01009.getCode()), 1, 1);
			idacell("Solo para las empresas que dispongan de c\u00f3digo LEI (Legal Entity Identifier)", 2, 7);
		}
		
		idaRow(2, new String[]{"Raz\u00f3n social " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01020.getCode()),
				"Domicilio social "+ getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01022.getCode())});
		
		String province = "";
		for(Provinces p : Provinces.values()){
			if(p.getId().equals(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01025.getCode())))
				province = p.getName();
		}
		idaRow(2, new String[]{"Municipio " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01023.getCode()),
				"Provincia "+ province});
		
		idaRow(2, new String[]{"C\u00f3digo postal " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01024.getCode()),
				"Tel\u00e9fono " + getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01031.getCode())});
		
		idaRow(2, new String[]{"Direcci\u00f3n de e-mail de contacto de la empresa",
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01037.getCode())});
		
		row = sheet.createRow(rowCount++);
		if(!isPymes()){
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			idacell("Pertenencia a un grupo de sociedades", 0, 2);
			idacell("Denominaci\u00f3n social",3,4);
			idacell("NIF",5,6);
		
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			idacell("Sociedad dominante directa", 0, 2);
			idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01041.getCode()),3,4);
			idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01040.getCode()),5,6);

			row = sheet.createRow(rowCount++);
			cellCount = 0;
			idacell("Sociedad dominante \u00faltima del grupo", 0, 2);
			idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01061.getCode()),3,4);
			idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01060.getCode()),5,6);
		}
		row = sheet.createRow(rowCount++);
		ssHeader("Actividad", pageMaxNumber);
		row = sheet.createRow(rowCount++);cellCount=0;
		idacell("C\u00f3digo CNAE", 0, 0);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA02001.getCode()) +"-"+
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA02009.getCode()), 1, 7);
		
		row = sheet.createRow(rowCount++);
		ssHeader("Personal asalariado", pageMaxNumber);
		
		idaRow(1, new String[]{"a) N\u00famero medio de personas empleadas en el curso del ejercicio, por tipo de contrato y empleo con discapacidad"});
		idaRow(3, new String[]{"", "Ejercicio "+ getD2Deposit().getYear(),
				"Ejercicio "+ (getD2Deposit().getYear()-1)});
		idaRow(3, new String[]{"FIJO", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04001.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040019.getCode())});
		idaRow(3, new String[]{"NO FIJO", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04002.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040029.getCode())});
	
		idaRow(1, new String[]{"Del cual: Personas empleadas con discapacidad mayor o igual al 33% (o calificaci\u00f3n equivalente local):"});
		idaRow(3 , new String[]{"", getD2Deposit().getMap().get(D2DepositHeaderKey.IDA04010.getCode()),
				getD2Deposit().getMap().get(D2DepositHeaderKey.IDA040109.getCode())});
		
		idaRow(1, new String[]{"b) Personal asalariado al t\u00e9rmino del ejercicio, por tipo de contrato y por sexo"});
			
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
		ssHeader("Presentaci\u00f3n de cuentas", pageMaxNumber);
		
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
		idacell("N\u00famero de p\u00e1ginas presentadas al dep\u00f3sito", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01901.getCode()),4,5);
		
		row = sheet.createRow(rowCount++);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		cellCount = 0;
		idacell("En caso de no figurar consignadas cifras en alguno de los ejercicios, indique la causa", 0, 3);
		idacell(getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01903.getCode()),4,5);

		row = sheet.createRow(rowCount++);cellCount = 0;
		if(!isPymes()){
			ssHeader("Unidades", pageMaxNumber);
			
			String euros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09001.getCode());
			String milesEuros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09002.getCode());
			String millonesEuros = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA09003.getCode());
			
			idaRow(1, new String[]{"Euros " + getBoolText(euros) + " Miles de euros " + getBoolText(milesEuros)
				+" Millones de euros " + getBoolText(millonesEuros)});	
		}else{
			ssHeader("Microempresas", pageMaxNumber);
			row = sheet.createRow(rowCount++);cellCount = 0;
			String a = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01902.getCode());
			row.setHeight(x.shortValue());
			idacell("Marque con una X si la empresa ha optado por la adopci\u00f3n conjunta de los criterios espec\u00ed�ficos, aplicables por microempresas, previstos en el Plan General de Contabilidad de PYMES (6)", 0, 7);
			row = sheet.createRow(rowCount++);cellCount = 0;
			idacell(getBoolText(a), 0, 7);
		}
	}
	
	public void ITR() {
		
	}
	
	public void SRA() {
		
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
				, keys, new D2DepositHeaderKey[][]{}, null);
	}

	public void BA2() {
		addSheet("Balance Pasivo");
		header(5);
		D2DepositHeaderKey[][] keys;
		D2DepositHeaderKey[][] keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			if(getD2Deposit().getYear().equals(2014))
				keys = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1;
			else keys = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1_2015;
			keys2 = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_2;
		}
		else{
			if(getD2Deposit().getYear().equals(2014))
				keys = D2DepositConstants.BA_ABREVIATE_KEYS_2;
			else keys = D2DepositConstants.BA_ABREVIATE_KEYS_2_2015;
			keys2 = D2DepositConstants.BA_ABREVIATE_KEYS_3;
		}

		three("Balance: Patrimonio Neto y Pasivo", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, keys2, null);
	}
	
	public void PYG() {
		addSheet("Cuenta de p\u00e9rdidas y ganancias");
		header(5);
		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.PYG_PYMES_KEYS;
		else keys = D2DepositConstants.PYG_ABREVIATE_KEYS;

		three("(Debe)/ Haber", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, new D2DepositHeaderKey[][]{}, null);
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

			HashMap< Integer, String> map = new HashMap<Integer, String>();
			map.put(9, "INGRESOS Y GASTOS IMPUTADOS DIRECTAMENTE AL PATRIMONIO NETO");
			map.put(18, "TRANSFERENCIAS A LA CUENTA DE P\u00c9RDIDAS Y GANANCIAS");
			three("ECPN. Estado de ingresos y gastos reconocidos en el ejercicio", "Notas de la memoria", "Ejercicio "
				+getD2Deposit().getYear(), "Ejercicio "+(getD2Deposit().getYear()-1)
				, keys, new D2DepositHeaderKey[][]{}, map);
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
				"Prima de emisi\u00f3n", "Reservas", "Acciones y participaciones en pratimonio propias",
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
		addSheet("Declaraci\u00f3n medioambiental");
		header(5);
		String text1 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes "
				+ "cuentas anuales NO existe ninguna partida de naturaleza medioambiental"
				+ " que deba ser inclu\u00edda de acuerdo a la norma de elaboraci\u00f3n '4º Cuentas"
				+ " anuales abreviadas' en su punto 5, de la tercera parte del Plan General"
				+ " de Contabilidad (Real Decreto 1514/2007 de 16 de Noviembre).";
		String text2 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes"
				+ " cuentas anuales SI existen partidas de naturaleza medioambiental,"
				+ " y han sido inclu\u00eddas en un Apartado adiciones de la Memoria de"
				+ " acuerdo a la norma de elaboraci\u00f3n '4º Cuentas anuales abreviadas'"
				+ " en su punto 5, de la tercera parte del Plan General de Contabilidad "
				+ "(Real Decreto 1514/2007 de 16 de Noviembre).";
		String tic = TIC;
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Declaraci\u00f3n Medioambiental", headerCellStyle);
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
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setAlignment(HorizontalAlignment.CENTER);
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
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(HorizontalAlignment.CENTER );
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
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(HorizontalAlignment.CENTER);
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
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setAlignment(HorizontalAlignment.CENTER );
		cell.setCellStyle(style);
		cell.setCellValue(b ? tic : "-");
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
	}
	
	public void AP1(){
		addSheet("Apartado 1 - Actividad de la empresa");
		freeText("Apartado 1 - Actividad de la empresa", D2DepositKey.MAT19019001);
	}
	
	public void AP2(){
		addSheet("Apartado 2 - Bases de presentaci\u00f3n de las cuentas anuales");
		freeText("Apartado 2 - Bases de presentaci\u00f3n de las cuentas anuales", D2DepositKey.MAT29029001);
	}
	
	public void AP3(){
		if(getD2Deposit().getYear() < 2016){
			AP3A();
			AP3B();
		} else {
			AP3C();
		}
	}
	
	public void AP3A(){
		addSheet("Apartado 3 - Texto Libre");
		freeText("Apartado 3 - Aplicaci\u00f3n de resultados", D2DepositKey.MAT39039001);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, null);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 2, new String[]{"APLICACI\u00d3N A", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 1, 0, null);
	}
	
	public void AP3C(){
		addSheet("Aplicaci\u00f3n de resultados");
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
		D2DepositKey[][] keys3 = D2DepositConstants.MRN_ABREVIATE_KEYS_3;
	
		general(pageMaxNumber, 2, new String[]{"BASES DE REPARTO", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, null);
		sheet.createRow(rowCount++);

		general(pageMaxNumber, 2, new String[]{"APLICACI\u00d3N A", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 1, 0, null);
		sheet.createRow(rowCount++);

		general(pageMaxNumber, 2, new String[]{"INFORMACI\u00d3N SOBRE EL PER\u00cdODO MEDIO DE PAGO A PROVEEDORES DURANTE EL EJERCICIO",
				"Ejercicio " + getD2Deposit().getYear(), "Ejercicio " + (getD2Deposit().getYear()-1)}, keys3, 1, 0, null);
	}
	
	public void AP4(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "4" : "3") + " - Normas de registro y valoraci\u00f3n");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "4" : "3") + " - Normas de registro y valoraci\u00f3n", D2DepositKey.MAT49049001);
	}
	
	public void AP5(){
		AP5A();
		AP5B();
	}
	
	public void AP5A(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "5" : "4") + " - Texto Libre");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "5" : "4") + " - Inmovilizado material, intangible, e inversiones inmobiliarias", D2DepositKey.MAT59059001);
	}
	
	public void AP5B(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "5" : "4") + " - Cuadros Normalizados");
		Integer pageMaxNumber = 3;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_1;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys3 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_3;
		
		// 2
		general(pageMaxNumber, 3, new String[]{"Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio actual",
			"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys, 2, 4, null);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 3, new String[]{"Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio anterior",
				"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys2, 2, 4, null);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 1, new String[]{"Arrendamientos financieros y otras operaciones de naturaleza similar sobre activos no corrientes","Total Contratos"},
				keys3, 1, 0, null);
	}
	
	public void AP6(){
		AP6A();
		if(getD2Deposit().getYear() < 2016) AP6B();
		AP6C();
	}
	
	public void AP6A(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "6" : "5") + " - Texto Libre");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "6" : "5") + " - Activos financieros", D2DepositKey.MAT69069001);
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
				"Instrumentos de Patrimonio", "Valores representativos de deuda", "Cr\u00e9ditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2, 4);
		sheet.createRow(rowCount++);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Activos financieros a corto plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.",
				"Instrumentos de Patrimonio", "Valores representativos de deuda", "Cr\u00e9ditos, derivados y otros", "TOTAL"},
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
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "6.2" : "5") + " - Cuadros Normalizados");
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
		special(2, pageMaxNumber, 6, new String[]{"Correcciones por deterioro del valor originadas por el riesgo de cr\u00e9dito",
				"Valores representativos de deuda", "Cr\u00e9ditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous}, keys,3, 2, 4);
		sheet.createRow(rowCount++);
		// 8
		general(pageMaxNumber, 4, new String[]{"Correcciones por deterioro del valor originadas por el riesgo de cr\u00e9dito"
				, "Activos a valor razonable con cambios en p\u00e9rdidas y ganancias", "Activos mantenidos para negociar"
				, "Activos disponibles para la venta", "TOTAL"}, keys2, 8, 4, null);
		sheet.createRow(rowCount++);
		// 9
		general(pageMaxNumber, 6, new String[]{"Correcciones valorativas por deterioro registradas en las distintas participaciones",
				"P\u00e9rdidas por deteriodo al final del ejercicio X", "(+/-) Variaci\u00f3n deteriodo a p\u00e9rdidas y ganancias",
				"(+) Variaci\u00f3n contra patrimonio neto", "(-) Salidas y reducciones", "(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)",
				"P\u00e9rdida por deteriodo al final del ejercicio Y"}, keys3, 9, 4, null);
	}
	
	public void AP7(){
		AP7A();
		AP7B();
	}
	
	public void AP7A(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "7" : "6") + " - Texto Libre");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "7" : "6") + " - Pasivos financieros", D2DepositKey.MAT79079001);	
	}
	
	public void AP7B(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "7" : "6") + " - Cuadros Normalizados");
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
		
		if(getD2Deposit().getYear() < 2016){
			// 3 2
			special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a largo plazo", "Deudas con entidades de cr\u00e9dito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2, 4);
			sheet.createRow(rowCount++);
		
			// 3 2
			special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a corto plazos", "Deudas con entidades de cr\u00e9dito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2, 4);
			sheet.createRow(rowCount++);
		}
		// 1
		general(pageMaxNumber, 7, new String[]{"Vencimiento de las deudas al cierre del ejercicio"+getD2Deposit().getYear(),
				"Uno", "Dos", "Tres", "Cuatro", "Cinco", "M\u00e1s de 5", "TOTAL"}, keys3, 1, 4, null);
		sheet.createRow(rowCount++);
		// 3
		if(getD2Deposit().getYear() < 2016){
			general(pageMaxNumber, 3, new String[]{"Lineas de descuento y p\u00f3lizas al cierre del ejercicio"+ getD2Deposit().getYear(),
				"L\u00ed�mite concedido", "Dispuesto", "Disponible"},keys4, 3, 4, null);
		}
	}
	
	public void AP8(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "8" : "7") + " - Fondos propios");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "8" : "7") + " - Fondos propios", D2DepositKey.MAT89089001);	
	}
	
	public void AP9(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "9" : "8") + " - Situaci\u00f3n fiscal");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "9" : "8") + " - Situaci\u00f3n fiscal", D2DepositKey.MAT99099001);	
	}
	
	public void AP10(){
		addSheet("Apartado 10 - Ingresos y gastos");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN10_ABREVIATE_KEYS;
		// 1
		general(pageMaxNumber, 2, new String[]{"Detalle de la cuenta de p\u00e9rdidas y ganancias", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, null);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, null);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Subvenciones, donaciones y legados recogidos en el patrimonio neto del balance, otorgados por terceros distintos a los socios: an\u00e1lisis del movimiento", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 2, 0, null);
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
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + " - Texto Libre");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + " - Operaciones con partes vinculadas", D2DepositKey.MAT129129001);	
	}
	
	public void AP12B(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + ".1 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys =  getD2Deposit().getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1_2016;
		// 11
		if(getD2Deposit().getYear() < 2016){
			general(pageMaxNumber, 7, new String[]{"Operaciones con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4, null);
		} else {
			general(pageMaxNumber, 6, new String[]{"Operaciones con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"}, keys, 11, 4, null);
		}
	}
	
	public void AP12C(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + ".2 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys =  getD2Deposit().getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2_2016;
		
		if(getD2Deposit().getYear() < 2016){
			general(pageMaxNumber, 7, new String[]{"Operaciones con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4, null);
		} else {
			general(pageMaxNumber, 6, new String[]{"Operaciones con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
					"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"}, keys, 11, 4, null);
		}
	}
	
	public void AP12D(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + ".3 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		D2DepositKey [][] keys;
		if (getD2Deposit().getType().equalsIgnoreCase("PYMES"))
			 keys =  getD2Deposit().getYear() < 2016 ? D2PDepositConstants.MRN12_PYMES_KEYS_3 :  D2PDepositConstants.MRN12_PYMES_KEYS_3_2016;
		else keys =  getD2Deposit().getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_KEYS_3 : D2DepositConstants.MRN12_ABREVIATE_KEYS_3_2016;
				
		if(getD2Deposit().getYear() < 2016){
			general(pageMaxNumber, 7, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4, null);
		} else {
			general(pageMaxNumber, 6, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+getD2Deposit().getYear(),
				"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"}, keys, 11, 4, null);
		}
	}
	
	public void AP12E(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + ".4 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		D2DepositKey [][] keys;
		if (getD2Deposit().getType().equalsIgnoreCase("PYMES"))
			keys = getD2Deposit().getYear() < 2016 ? D2PDepositConstants.MRN12_PYMES_KEYS_4 : D2PDepositConstants.MRN12_PYMES_KEYS_4_2016;
		else keys = getD2Deposit().getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4_2016;
		if(getD2Deposit().getYear() < 2016){
			general(pageMaxNumber, 7, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11, 4, null);
		} else {
			general(pageMaxNumber, 6, new String[]{"Saldos pendientes con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
					"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"}, keys, 11, 4, null);
		}
	}
	
	public void AP12F(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "12" : "9") + ".5 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
		
		D2DepositKey [][] keys, keys2;
		if(getD2Deposit().getYear() < 2016){
			if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
				keys = D2PDepositConstants.MRN12_PYMES_KEYS_5;
				keys2 = D2PDepositConstants.MRN12_PYMES_KEYS_6;
			}
			else{
				keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_5;
				keys2 = D2DepositConstants.MRN12_ABREVIATE_KEYS_6;		
			}
		} else {
			keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_5_2016;
			keys2 = D2DepositConstants.MRN12_ABREVIATE_KEYS_6_2016;		
		}
		// 2
		general(pageMaxNumber, 2, new String[]{"Importes recibidos por el personal de alta direcci\u00f3n",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys, 2, 0, null);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Importes recibidos por los miembros de los \u00f3rganos de administraci\u00f3n",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys2, 2, 0, null);
	}
	
	public void AP13(){
		AP13A();
		AP13B();
	}
	
	public void AP13A(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "13" : "10") + " - Texto Libre");
		freeText("Apartado " + (getD2Deposit().getYear() < 2016 ? "13" : "10") + " - Otra informaci\u00f3n", D2DepositKey.MAT139139001);	
	}
	
	public void AP13B(){
		addSheet("Apartado " + (getD2Deposit().getYear() < 2016 ? "13" : "10") + " - Cuadros Normalizados");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = null;
		if(getD2Deposit().getYear() < 2016){
			keys = D2DepositConstants.MRN13_ABREVIATE_KEYS;
		} else {
			keys = D2DepositConstants.MRN13_ABREVIATE_KEYS_2016;
		}
		// 2
		general(pageMaxNumber, 2, new String[]{"N\u00famero medio de personas empleadas en el curso del ejercicio, por categor\u00edas (adaptadas a la CNO-11)", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 2, 0, null);
	}
	
	public void AP14(){
		AP14A();
		AP14B();
		AP14C();
	}
	
	public void AP14A(){
		addSheet("Apartado 14 - Texto Libre");
		freeText("Apartado 14 - Informaci\u00f3n sobre medio ambiente", D2DepositKey.MAT149149001);		}
	
	public void AP14B(){
		addSheet("Apartado 14.1 - Cuadros Normalizados");
		Integer pageMaxNumber = 2;
		header(pageMaxNumber);
		D2DepositKey[][] keys = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_1;
		//  1
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(new Integer(8), new String("A) ACTIVOS DE NATURALEZA MEDIOAMBIENTAL"));
		map.put(new Integer(11), new String("3.Correcciones valorativas por deterioro"));
		map.put(new Integer(15), new String("C)Riesgos cubiertos por las provisiones para actuaciones medioambientales"));
		map.put(new Integer(16), new String("1.Provisi\u00F3n para actuaciones medioambientales, inclu\u00EDdas en provisiones"));
		general(pageMaxNumber, 2, new String[]{"DESCRIPCI\u00d3N DEL CONCEPTO", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, map);
	}
	
	public void AP14C(){
		addSheet("Apartado 14.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 1;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_3;	
		// 1 1
		special2(pageMaxNumber, 1,"Movimiento durante el ejercicio" ,new String[]{"DERECHOS DE EMISI\u00d3N DE GASES DE EFECTO INVERNADERO", "Importe"}, keys, 1, 1, 0);
		sheet.createRow(rowCount++);
		// 1 1 
		special2(pageMaxNumber, 1, "Otra Informaci\u00f3n", new String[]{"CONCEPTO", "Importe"}, keys2, 1 ,1, 0);
	}
	
	public void AP15(){
		addSheet("Apartado 15 - Informaci\u00f3n sobre los aplazamientos de pago efectuados a proveedores");
		if(getD2Deposit().getYear().equals(2014)){
			header(4);
			D2DepositKey[][] keys = D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS;
			special(2, 4, 4, new String[]{"Pagos realizados y pendientes de pago en la fecha de cierre del Balance",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio " + (getD2Deposit().getYear()-1)}, 
				new String[]{"PAGOS DEL EJERCICIO", "Importe", "%", "Importe",  "%"}, keys, 1, 1, 0);		
		}
		else{
			header(2);
			D2DepositKey[][] keys = D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS_2015;
			general(2, 2, new String[]{"Pagos realizados y pendientes de pago en la fecha de cierre del Balance",
					"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1, 0, null);	
		}
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
		String text = "La sociedad no ha realizado durante el presente ejercicio operaci\u00f3n alguna sobre acciones / participaciones propias";		

		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setAlignment(HorizontalAlignment.CENTER);
		cell.setCellStyle(style);
		cell.setCellValue(text);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		
		String a = getD2Deposit().getMap().get(D2DepositFooterKey.A18009050.getCode());
		String value = getBoolText(a);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		cell = row.createCell(cellCount++);
		cell.setCellStyle(style);
		cell.setCellValue(value);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		if(value.equals("-")){
			MA1();
			MA11();
			MA2();
			MA3();
			MA4();
			MA5();
			MA6();
			MA7();
			MA8();
		}
	}
	
	
	private void MA1() {
		addSheet("P\u00e1gina A1");
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
				"Nominal", "Capital social %", "Precio o contraprestaci\u00f3n", "Saldo despu\u00e9s de operaci\u00f3n"}, 8, 3);
		
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
		addSheet("P\u00e1gina A1.1");
		header(5);
		ssHeader(new String[]{"Fecha", "Concepto", "Fecha de acuerdo de junta general", "Nº Acciones / participaciones",
				"Nominal", "Capital social %", "Precio o contraprestaci\u00f3n", "Saldo despu\u00e9s de operaci\u00f3n"}, 8, 3);

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
		addSheet("P\u00e1gina A2");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Transcripci\u00f3n de acuerdos de Juntas generales, del \u00faltimo o anteriores ejercicios, autorizando negocios sobre acciones o participaciones propias realizados en el \u00faltimo ejercicio cerrado.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha acuerdo", "Transcripci\u00f3n literal del acuerdo"}, 2, 1);
		
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
		addSheet("P\u00e1gina A3");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Relaci\u00f3n de acciones o participaciones adquiridas al amparo de los art\u00edculos 140, 144 y 146 de la Ley de SOciedades de Capital, durante el ejercicio.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha", "Relaci\u00f3n numerada de las acciones / participaciones", "T\u00edtulo de adquisici\u00f3n", "% sobre capital"}, 4, 2);

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
		addSheet("P\u00e1gina A4");
		header(5);
		
		ssHeader("Modelo de autocartera", 5);
		row = sheet.createRow(rowCount++); cellCount = 0;
		idacell("Relaci\u00f3n de acciones o participaciones adquiridas por los mismo t\u00edtulos, enajenadas o amortizadas durante el presente ejercicio.", 0, 7);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		ssHeader(new String[]{"Fecha", "Relaci\u00f3n numerada de las acciones / participaciones", "Causa de la baja", "% sobre capital"}, 4, 2);

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
		addSheet("P\u00e1gina A5");
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
		idacell("Negocios que han implicado la aceptaci\u00f3n en garant\u00eda de acciones propias, con las excepciones legales (art\u00edculo 149 de la Ley de Sociedades de Capital).", 0, 8);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Fecha", "Descripci\u00f3n del negocio", "N\u00famero de acciones dadas en garant\u00eda"};
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
		addSheet("P\u00e1gina A6");
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
		idacell("Negocios que han implicado la asistencia finanaciera para la adquisicin de acciones propias salvo las excepciones legales (art\u00edculo 150 de la Ley de Sociedades de Capital).", 0, 8);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Fecha", "Descripci\u00f3n del negocio", "N\u00famero de acciones adquiridas"};
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
		addSheet("P\u00e1gina A7");
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
		idacell("Supuestos de infracci\u00f3n de las normas sobre participaciones rec\u00edprocas de capital (art\u00edculo 151 y siguiente de la Ley de Sociedades de Capital).", 0, 9);
		Double x1 = (row.getHeight() * 2) / 1.5;
		row.setHeight(x1.shortValue());
		row = sheet.createRow(rowCount++); cellCount = 0;
		
		String[] strings = new String[]{"Sociedad Comunicante", "Fecha Comunicaci\u00f3n", "Porcentaje de participaci\u00f3n en su capital a esa fecha", "Fecha Reducci\u00f3n", "Porcentaje Posterior"};
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
	
	private void MA8() {
		addSheet("P\u00e1gina A8");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount,
				"Espacio destinado para las firmas con identificaci\u00f3n de los administradores, n\u00famero de hojas, y fecha de comunicaci\u00f3n.",
				headerCellStyle);
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
		String text = "";
		
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(defaulFont );
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		cell.setCellStyle(style);
		cell.setCellValue(text);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		Integer height = row.getHeight() * 20;
		row.setHeight(height.shortValue());	}
	
	public void IP(){
		addSheet("Instancia de Presentaci\u00f3n");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		String city = "";
		for(Cities c : Cities.values()){
			if(c.getId().equals(getD2Deposit().getMap().get(D2DepositFooterKey.PR8081001.getCode())))
				city = c.getName();
		}
		
		idacell("SOLICITUD DE PRESENTACI\u00d3N EN EL REGISTRO MERCANTIL DE " + city, 0, 7);
		
		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACI\u00d3N DE LA ENTIDAD QUE PRESENTA LAS CUENTAS A DEP\u00d3SITO", 5);
		row = sheet.createRow(rowCount++);
		
		String name = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01020.getCode());
		String nif = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01010.getCode());
		String date = getD2Deposit().getMap().get(D2DepositHeaderKey.IDA01101.getCode());
		String tomo = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081002.getCode());
		String folio = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081003.getCode());
		String hojasReg = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081004.getCode());
		
		cellCount = 0;
		idacell("Denominaci\u00f3n de la entidad", 0, 1);
		idacell(name, 2, 4);
		idacell("N.I.F.", 5, 5);
		idacell(nif, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Datos Registrales", 0, 1);
		idacell("", 2, 4);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("Tomo", 0, 1);
		idacell(tomo, 2, 3);
		idacell("Folio", 4, 5);
		idacell(folio, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;		
		idacell("Nº Hoja registral", 0, 1);
		idacell(hojasReg, 2, 3);
		idacell("Fecha de cierre ejercicio social", 4, 5);
		idacell(date, 6, 7);

		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACI\u00d3N DE LOS DOCUMENTOS CONTABLES CUYO DEP\u00d3SITO SE SOLICITA", 5);

		row = sheet.createRow(rowCount++);
		Double x = (row.getHeight() * 2) / 1.5;
		row.setHeight(x.shortValue());
		cellCount = 0;
		idacell("Balance", 0, 0);
		idacell("P\u00e9rdidas y Ganancias", 1, 2);
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

		row = sheet.createRow(rowCount++);
		cellCount = 0;

		String s = !isPymes() ? TIC : "-" ;
		idacell(s + " Abreviado", 0, 0);
		idacell(s + " Abreviado", 1, 2);
		idacell(s + " Abreviado", 3, 3);
		if(getD2Deposit().getYear() < 2016){
			idacell(s + " Abreviado", 4, 5);
		}

		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		s = isPymes() ? TIC : "-" ;
		idacell(s + " PYME", 0, 0);
		idacell(s + " PYME", 1, 2);
		idacell(s + " PYME", 3, 3);
		if(getD2Deposit().getYear() < 2016){
			idacell(s + " PYME", 4, 5);
		}
		
		row = sheet.createRow(rowCount++);
		row.setHeight(x.shortValue());
		cellCount = 0;
		String a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080800.getCode());
		String b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080819.getCode());
		String c1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080807.getCode());
		String d1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080817.getCode());
		idacell(getBoolText(a1) + " Hoja de Identificaci\u00f3n de la sociedad", 0, 1);
		idacell(getBoolText(b1) + " Declaraci\u00f3n medioambiental", 2, 3);
		idacell(getBoolText(c1) + " Informe de gestion", 4, 5);
		idacell(getBoolText(d1) + " Informe de Auditor\u00eda", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080809.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080823.getCode());
		c1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080821.getCode());
		d1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8080811.getCode());
		idacell(getBoolText(a1) + " Modelo de autocartera", 0, 1);
		idacell(getBoolText(b1) + " Anuncios de convocatoria", 2, 3);
		idacell(getBoolText(c1) + " Certificado SICAV", 4, 5);
		idacell(getBoolText(d1) + " Certificaci\u00f3n acuerdo", 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		idacell("- Otros Documentos", 0, 1);
		idacell("Nº ", 2, 3);
	
		if(getD2Deposit().getYear() > 2014){
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			idacell("Codigo ROAC del Auditor firmante ",0, 2);
			idacell(getD2Deposit().getMap().get(D2DepositFooterKey.PR8081320.getCode()),3, 4);
		}
		
		row = sheet.createRow(rowCount++);
		ssHeader("IDENTIFICACI\u00d3N DEL PRESENTANTE QUE HACE LA SOLICITUD", 5);
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
		String province = "";
		for(Provinces p : Provinces.values()){
			if(p.getId().equals(getD2Deposit().getMap().get(D2DepositFooterKey.PR8081206.getCode())))
				province = p.getName();
		}
		idacell("Ciudad", 0, 1);
		idacell(a1, 2, 3);
		idacell("Provincia", 4, 5);
		idacell(province, 6, 7);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		a1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081208.getCode());
		b1 = getD2Deposit().getMap().get(D2DepositFooterKey.PR8081209.getCode());
		idacell("Tel\u00e9fono", 0, 1);
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
		addSheet("Certificaci\u00f3n de la huella digital");
		header(5);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "Nombre de las personas que expiden la certificaci\u00f3n", headerCellStyle);
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
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			style.setVerticalAlignment(VerticalAlignment.TOP);
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
		if(a != null && !a.equalsIgnoreCase("null")
				&& (a.equals("1") || a.equalsIgnoreCase("true")))
			return TIC;
		else return "-";
	}
	private Boolean isPymes() {
		return getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE).equalsIgnoreCase("Pymes");
	}
	
}
