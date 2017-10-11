package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class ModelVAExcelAction extends AbsExcelAction {

	protected static final XSSFColor ARABA_BG = new XSSFColor(new java.awt.Color(163, 12, 81));
	protected static final XSSFColor BIZKAIA_BG = new XSSFColor(new java.awt.Color(215, 0, 4));
	protected static final XSSFColor GIPUZKOA_BG = new XSSFColor(new java.awt.Color(161, 192, 49));
	protected static final XSSFColor NAVARRA_BG = new XSSFColor(new java.awt.Color(218, 0, 42));
	protected static final XSSFColor AEAT_BG = new XSSFColor(new java.awt.Color(58, 133, 195));

	protected  static final XSSFColor[] COLORS = new XSSFColor[] { ARABA_BG, BIZKAIA_BG, GIPUZKOA_BG, NAVARRA_BG,
			AEAT_BG };
	
	protected  static final String[] IMAGES = new String[] { 
			"/com/esferalia/aon/gwt/common/client/css/images/aon-araba-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-bizkaia-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-gipuzkoa-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-navarra-header-image.png"
			,"/com/esferalia/aon/gwt/common/client/css/images/aon-aeat-header-image.png"
	};
	
	protected Font idFont;
	protected XSSFCellStyle rowStyle;
	protected XSSFCellStyle idCellStyle;
	protected XSSFCellStyle boxCellStyle;
	protected XSSFCellStyle headerCellStyle;
	protected Font boxFont;
	
	protected Font vatFont;
	protected Font vatBoldFont;
	
	protected Mod303 model;
	
	public ModelVAExcelAction(Mod303 model) {
		this.model = model;
	}
	
	@Override
	public void initialize(String name) {
		this.initialize(name, false);
	}

	@Override
	public void initialize(String name, boolean printHeaders) {
		super.initialize(name, printHeaders);
		sheet.setZoom(14,10);	// 140%
		
		vatFont = workbook.createFont();
		vatFont.setFontName(FONT_FAMILY);
		vatFont.setFontHeightInPoints((short) 7);

		vatBoldFont = workbook.createFont();
		vatBoldFont.setFontName(FONT_FAMILY);
		vatBoldFont.setFontHeightInPoints((short) 7);
		vatBoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		
		rowStyle = (XSSFCellStyle) workbook.createCellStyle();
		rowStyle.setWrapText(true);

		boxFont = workbook.createFont();
		boxFont.setFontHeightInPoints((short) 7);
		boxFont.setColor(IndexedColors.GREY_40_PERCENT.index);

		boxCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		boxCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		boxCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		boxCellStyle.setBorderBottom(CellStyle.BORDER_HAIR);
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
		headerCellStyle.setFillForegroundColor(COLORS[model.getAdministration().ordinal()]);

		printModelInfo();
	}

	protected void printModelInfo() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod115ExcelAction.class.getResourceAsStream(
					IMAGES[ model.getAdministration().ordinal()]);
			byte[] imageBytes = AonIOUtils.toByteArray(inputStream);
			int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			inputStream.close();
			CreationHelper helper = workbook.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(2);
			anchor.setCol1(0);
			anchor.setRow1(rowCount - 1 );
			anchor.setDx1(10);
			anchor.setDy1(10);
			Picture pict = drawing.createPicture(anchor, pictureureIdx);
			pict.resize();
		} catch (IOException e) {
			e.printStackTrace();
			// Sin Imagen,.
		}
		CellUtil.createCell(row, 0,"");
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1), (rowCount+1), 0, 0));
		
		CellUtil.createCell(row, 1, getTitle(),headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount + 1), 1, 6));
		
		CellUtil.createCell(row, 7, FiscalModelUtils.getModelName(model),headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, AonNumberUtils.toString(model.getYear()), headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 7, model.getPeriod().getDescription(), headerCellStyle);
		

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 7));
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(model.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(model.getSurname(), AonStringUtils.EMPTY));

		CellUtil.createCell(row, 0, model.getDocument() + "-" + name , idCellStyle);

		row = sheet.createRow(rowCount++);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		CellUtil.createCell(row, cellCount, "", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 8 * 256);
		sheet.setColumnWidth(cellCount++, 30 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
		
		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		Font vatHeaderFont= workbook.createFont();
		vatHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		vatHeaderFont.setFontHeightInPoints((short) 8);
		vatHeaderFont.setColor( IndexedColors.WHITE.index );
		rightHeaderCellStyle.setFont(vatHeaderFont);
		rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		CellUtil.createCell(row, cellCount, "Base Imp.", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

		CellUtil.createCell(row, cellCount, " % ", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));

		CellUtil.createCell(row, cellCount, "Cuota", rightHeaderCellStyle);
		sheet.setColumnWidth(cellCount++, 3 * 256);
		sheet.setColumnWidth(cellCount++, 10 * 256);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));

		
		row = sheet.createRow(rowCount++);
		row.createCell(0);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		
		sheet.setRepeatingRows(new CellRangeAddress(0, 7, 0, 7));
		
		
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(model) + " / " + model.getPeriod().getDescription() );
		footer.setRight("P\u00E1g: &P/&N");
		
	}

	@Override
	protected void headerRow() {
		sheet.setRowBreak(rowCount - 1);
	}
	
	public void beforeFinalize() {
		if (model.isFinished()) {
			DecimalFormat format = new DecimalFormat("#,##0.00");
			String paymentInfo = "Resultado: " + format.format(model.getResult())
					+ AonStringUtils.SPACE + getDeclarationType()
					+ AonStringUtils.SPACE + AonStringUtils.trimToEmpty( model.getFinanceBankAlias())
					+ AonStringUtils.SPACE + AonStringUtils.trimToEmpty( model.getFinanceMaskedIban())
					;
			row = sheet.createRow(rowCount++);
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 7));
			row = sheet.createRow(rowCount++);
			CellUtil.createCell(row, 0, paymentInfo , idCellStyle);
			row = sheet.createRow(rowCount++);
		}
	}
	
	public void accept(IModelScript<Mod303Key> ms) {
		if (ms.paintHeaderBefore()) {
			headerRow();
		}
		row = sheet.createRow(rowCount++);
		String concept = AonStringUtils.trimToEmpty(ms.getLabel());
		concept = AonStringUtils.abbreviate(concept, 100);
		int l = AonStringUtils.length(concept);
		if (l != 0) {
			int r = (int) AonMathUtils.floor( ((double) l) / getConceptLength() , 0);
			int h = 230 + (r * 230);
			row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
		}
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(ms.isTitle() ? vatBoldFont : vatFont );
		if (ms.getKeys() != null) {
			style.setBorderBottom(CellStyle.BORDER_HAIR);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		}
		cell.setCellStyle(style);
		cell.setCellValue(concept);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (ms.getKeys() == null) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));
		} else {
			if (ms.getKeys().length == 1) {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));
				cellCount = 6;
			} else if (ms.getKeys().length == 2) {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
				cellCount = 4;
			} else {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));			
				cellCount = 2;
			}
			for (Mod303Key key : ms.getKeys()) {
				if (key == null) {
					Cell boxCell = addCell("");
					style = workbook.createCellStyle();
					boxCell.setCellStyle(boxCellStyle);
					
					cell = addCell("");
					style = workbook.createCellStyle();
					style.setBorderBottom(CellStyle.BORDER_HAIR);
					style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
					cell.setCellStyle(style);
				} else{
					Cell boxCell = addCell((key.getBox() == 0
							?""
							:AonStringUtils.leftPad(AonNumberUtils.toString(key.getBox()), 3, "0")));
					boxCell.setCellType(Cell.CELL_TYPE_STRING);
					boxCell.setCellStyle(boxCellStyle);

					cell = row.createCell(cellCount++);
					style = workbook.createCellStyle();
					style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
					style.setFont(ms.isTitle()?vatBoldFont:vatFont);
					style.setBorderBottom(CellStyle.BORDER_HAIR);
					style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
					cell.setCellStyle(style);
					if (ms.hasGraphicParticularity()) {
						fillParticularityCell(cell,style,key);
					} else {
						double amount = model.ensureDetail(key).getAmount();
						style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
						style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
						cell.setCellValue(amount);
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					}
				}
			}

		}
	}

	protected int getConceptLength() {
		return 60;
	}

	protected String getDeclarationType() {
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}
	
	protected abstract String getTitle();
	protected abstract void fillParticularityCell(Cell cell ,CellStyle style,Mod303Key key);
}
