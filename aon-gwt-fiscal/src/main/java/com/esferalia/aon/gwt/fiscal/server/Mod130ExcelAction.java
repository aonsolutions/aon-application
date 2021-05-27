package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod130ExcelAction extends ModelIRPFExcelAction<Mod130,Mod130Key> {

	public Mod130ExcelAction(Mod130 mod130) {
		super(mod130);
	}

	@Override
	protected String getTitle() {
		return "IRPF. Empresarios y profesionales en estimación directa. Pago fraccionado.";
	}

	@Override
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
			SXSSFDrawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
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
		CellUtil.createCell(row, 1, getTitle(), headerCellStyle);
		
		CellUtil.createCell(row, 3, FiscalModelUtils.getModelName(model),
				headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 3, AonNumberUtils.toString(model.getYear()), headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 3, model.getPeriod().getDescription(), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 2));

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(model.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(model.getSurname(), AonStringUtils.EMPTY));

		CellUtil.createCell(row, 0, model.getDocument() + "-" + name , idCellStyle);

		row = sheet.createRow(rowCount++);
	}

	@Override
	protected void headerRow() {
		XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
		rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 8 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 50 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 4 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 10 * 256);

	}

	protected int getConceptLength() {
		return 85;
	}

	@Override
	public void accept(IModelScript<Mod130Key> ms) {
		if (ms.paintHeaderBefore()) {
			headerRow();
		}
		row = sheet.createRow(rowCount++);
		String concept = AonStringUtils.trimToEmpty(ms.getLabel());
		int l = AonStringUtils.length(concept);
		if (l != 0) {
			int r = (int) AonMathUtils.floor( ((double) l) / getConceptLength() , 0);
			int h = 230 + (r * 230);
			row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
		}
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		++cellCount;
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(ms.isTitle() ? modBoldFont: modFont );
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		cell.setCellStyle(style);
		cell.setCellValue(concept);
		cell.setCellType(CellType.STRING);
		if (ms.getKeys() == null) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
		} else {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
			Mod130Key key = ms.getKeys()[0];
			String box = (key.getBox() != 0)
					?AonStringUtils.leftPad(AonNumberUtils.toString(key.getBox()), 3, "0")
					:AonStringUtils.EMPTY;
			Cell boxCell = addCell(box);
			boxCell.setCellType(CellType.STRING);
			boxCell.setCellStyle(boxCellStyle);

			cell = row.createCell(cellCount++);
			style = workbook.createCellStyle();
			style.setVerticalAlignment(VerticalAlignment.BOTTOM);
			style.setFont(ms.isTitle()?modBoldFont:modFont);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
			cell.setCellStyle(style);
			if (ms.hasGraphicParticularity()) {
				fillParticularityCell(cell,style,key);
			} else {
				double amount = model.ensureDetail(key).getAmount();
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
				cell.setCellValue(amount);
				cell.setCellType(CellType.NUMERIC);
			}

		}
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
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
			row = sheet.createRow(rowCount++);
			CellUtil.createCell(row, 0, paymentInfo , idCellStyle);
			row = sheet.createRow(rowCount++);
		}
	}
	
	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod130Key key) {
		double amount = model.ensureDetail(key).getAmount();
		cell.setCellType(CellType.STRING);
		if (key == Mod130Key.P0) {
			style.setAlignment(HorizontalAlignment.RIGHT);
			cell.setCellValue(amount == 1
					?IRPFRegime.SIMPLIFIED.getDescription()
					:IRPFRegime.NORMAL.getDescription());
		} else if (key == Mod130Key.P1) {
			style.setAlignment(HorizontalAlignment.RIGHT);
			style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
			cell.setCellValue(amount);
			cell.setCellType(CellType.NUMERIC);
		} else if (key == Mod130Key.P2) {
			cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
			cell.setCellValue(amount == 1?"SI":"NO");
		}
	}
}
