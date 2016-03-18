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
	
	
	public void general(Integer pageMaxNumber, Integer number, String[] strings, D2DepositKey[][] keys
			,Integer headerHeight){
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
			String description = D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]);
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
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, dif+1));
			
			for(Integer i = 0; i < dif+2; i++)
				cell = row.createCell(cellCount++);
			
			style = workbook.createCellStyle();
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
			style.setFont(defaulFont);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
//			String code = innerKeys[0].getCode().substring(0,4);
			String code = innerKeys[0].getCode();
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
			D2DepositKey[][] keys, Integer headerHeight1, Integer headerHeight2){
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
		
		general(pageMaxNumber, number, strings, keys, headerHeight2);
	}
	
	public void special2(Integer pageMaxNumber, Integer number, String title, String[] strings,	D2DepositKey[][] keys,
			 Integer headerHeight1, Integer headerHeight2){
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
	
		general(pageMaxNumber, number, strings, keys, headerHeight2);
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
		//Boolean[] isTitle = D2DepositBehaviour.BEHAVIOUR_KEYS_MAP.get(innerKeys[0]);
		style.setFont(/*isTitle[0] ? boldFont : */defaulFont );
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
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 40 * 256);
		for(Integer i = 0 ; i < 3; i++)
			sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));
	}
	
	public void IDA() {
		Integer pageMaxNumber = 3;
		addSheet("Hoja identificativa de la sociedad");
		header(pageMaxNumber);
		ssHeader("Identificación", pageMaxNumber);
		// TODO
		
		
		ssHeader("Actividad", pageMaxNumber);
		
		ssHeader("Personal asalariado", pageMaxNumber);
		
		ssHeader("Presentación de cuentas", pageMaxNumber);
		
		ssHeader("Unidades", pageMaxNumber);
		
		
		
		
		// TODO ****************************************************************
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
		//ECPN2();
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
		// TODO ****************************************************************
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
		//String tic = "&#10004;";
		String tic = "✔";
		
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
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 2, new String[]{"APLICACIÓN A", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 1);
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
			"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys, 2);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 3, new String[]{"Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio anterior",
				"Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias"}, keys2, 2);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 1, new String[]{"Arrendamientos financieros y otras operaciones de naturaleza similar sobre activos no corrientes","Total Contratos"},
				keys3, 1);
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
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2);
		sheet.createRow(rowCount++);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Activos financieros a corto plazo, salvo inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas.",
				"Instrumentos de Patrimonio", "Valores representativos de deuda", "Créditos, derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2);
		sheet.createRow(rowCount++);
		
		// 1 11
		special(3, pageMaxNumber, 6, new String[]{"Traspasos o reclasificaciones de activos financieros",current, previous},
				new String[]{"","Inversiones mantenidas hasta el vencimiento","Inversiones en el patrimonio de empresa del grupo, multigrupo y asociadas",
						"Activos financieros disponibles para la venta", "Inversiones mantenidas hasta el vencimiento",
						"Inversiones en el patrimonio de empresa del grupo, multigrupo y asociadas", "Activos financieros disponibles para la venta"},
				keys3, 1, 11);
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
				new String[]{"", current, previous, current, previous, current, previous}, keys,3, 2);
		sheet.createRow(rowCount++);
		// 8
		general(pageMaxNumber, 4, new String[]{"Correcciones por deterioro del valor originadas por el riesgo de crédito"
				, "Activos a valor razonable con cambios en pérdidas y ganancias", "Activos mantenidos para negociar"
				, "Activos disponibles para la venta", "TOTAL"}, keys2, 8);
		sheet.createRow(rowCount++);
		// 9
		general(pageMaxNumber, 6, new String[]{"Correcciones valorativas por deterioro registradas en las distintas participaciones",
				"Pérdidas por deteriodo al final del ejercicio X", "(+/-) Variación deteriodo a pérdidas y ganancias",
				"(+) Variación contra patrimonio neto", "(-) Salidas y reducciones", "(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)",
				"Pérdida por deteriodo al final del ejercicio Y"}, keys3, 9);
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
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2);
		sheet.createRow(rowCount++);
		
		// 3 2
		special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a corto plazos", "Deudas con entidades de crédito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2);
		sheet.createRow(rowCount++);
		// 1
		general(pageMaxNumber, 7, new String[]{"Vencimiento de las deudas al cierre del ejercicio"+getD2Deposit().getYear(),
				"Uno", "Dos", "Tres", "Cuatro", "Cinco", "Más de 5", "TOTAL"}, keys3, 1);
		sheet.createRow(rowCount++);
		// 3
		general(pageMaxNumber, 3, new String[]{"Lineas de descuento y pólizas al cierre del ejercicio"+ getD2Deposit().getYear(),
				"Límite concedido", "Dispuesto", "Disponible"},keys4, 3);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Subvenciones, donaciones y legados recogidos en el patrimonio neto del balance, otorgados por terceros distintos a los socios: análisis del movimiento", "Ejercicio " + getD2Deposit().getYear(), 
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys2, 2);
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
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11);
	}
	
	public void AP12C(){
		addSheet("Apartado 12.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 7;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2;
		
		general(pageMaxNumber, 7, new String[]{"Operaciones con partes vinculadas en el ejercicio "+(getD2Deposit().getYear()-1),
				"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11);
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
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11);
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
				"Personal clave de la direcció de la empresa o de la entidad dominante", "Otras partes vinculadas"}, keys, 11);
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
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys, 2);
		sheet.createRow(rowCount++);
		// 2
		general(pageMaxNumber, 2, new String[]{"Importes recibidos por los miembros de los órganos de administración",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio "+ (getD2Deposit().getYear()-1)}, keys2, 2);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 2);
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
				"Ejercicio " + (getD2Deposit().getYear()-1)}, keys, 1);
	}
	
	public void AP14C(){
		addSheet("Apartado 14.2 - Cuadros Normalizados");
		Integer pageMaxNumber = 1;
		header(pageMaxNumber);
	
		D2DepositKey[][] keys = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_3;	
		// 1 1
		special2(pageMaxNumber, 1,"Movimiento durante el ejercicio" ,new String[]{"DERECHOS DE EMISIÓN DE GASES DE EFECTO INVERNADERO", "Importe"}, keys, 1, 1);
		sheet.createRow(rowCount++);
		// 1 1 
		special2(pageMaxNumber, 1, "Otra Información", new String[]{"CONCEPTO", "Importe"}, keys2, 1 ,1);
	}
	
	public void AP15(){
		addSheet("Apartado 15 - Información sobre los aplazamientos de pago efectuados a proveedores");
		Integer pageMaxNumber = 4;
		header(pageMaxNumber);
		
		D2DepositKey[][] keys = D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS;
		// 1 1 
		special(2, pageMaxNumber, 4, new String[]{"Pagos realizados y pendientes de pago en la fecha de cierre del Balance",
				"Ejercicio "+ getD2Deposit().getYear(), "Ejercicio " + (getD2Deposit().getYear()-1)}, 
				new String[]{"PAGOS DEL EJERCICIO", "Importe", "%", "Importe",  "%"}, keys, 1, 1);		
	}
	
	public void MA(){
		// TODO ****************************************************************
	}
	
	public void IP(){
		// TODO ****************************************************************
	}
	
	public void CHD(){
		// TODO ****************************************************************
	}
	
	protected abstract String getTitle();

	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}

	public void setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}


}
