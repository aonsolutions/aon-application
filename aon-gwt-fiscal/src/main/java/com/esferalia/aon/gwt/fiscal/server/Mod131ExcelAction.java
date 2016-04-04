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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131ExcelAction extends ModelIRPFExcelAction<Mod131,Mod131Key> {

	public Mod131ExcelAction(Mod131 mod131) {
		super(mod131);
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
		CellUtil.createCell(row, 1, getTitle(), headerCellStyle);

		CellUtil.createCell(row, 4, model.getModel().getName(model.getAdministration(), model.getPeriod()),
				headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 4, AonNumberUtils.toString(model.getYear()), headerCellStyle);
		row = sheet.createRow(rowCount++);
		CellUtil.createCell(row, 4, model.getPeriod().getDescription(), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 3));

		row = sheet.createRow(rowCount++);

		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
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
		rightHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 8 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 60 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 10 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 4 * 256);
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 10 * 256);
	}

	@Override
	public void accept(IModelScript<Mod131Key> ms) {
		if (ms.paintHeaderBefore()) {
			headerRow();
		}
		row = sheet.createRow(rowCount++);
		String concept = AonStringUtils.trimToEmpty(ms.getLabel());
		int l = AonStringUtils.length(concept);
		if (l != 0) {
			int r = (int) (l / ((ms.getKeys() == null)?131:102)) + 1; 
			int h = (r * 250);
			row.setHeight((h > Short.MAX_VALUE?Short.MAX_VALUE:(short) h));
		}
		row.setRowStyle(rowStyle);
		cellCount = 0;
		Cell cell = row.createCell(cellCount++);
		++cellCount;
		++cellCount;
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFont(ms.isTitle() ? boldFont : defaulFont );
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
		cell.setCellStyle(style);
		cell.setCellValue(concept);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));			
		if (ms.getKeys() == null) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));
		} else {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 1));
			Mod131Key key = ms.getKeys()[0];
			String box = (key.getBox() != 0)
					?AonStringUtils.leftPad(AonNumberUtils.toString(key.getBox()), 3, "0")
					:AonStringUtils.EMPTY;
			Cell boxCell = addCell(box);
			boxCell.setCellType(Cell.CELL_TYPE_STRING);
			boxCell.setCellStyle(boxCellStyle);

			cell = row.createCell(cellCount++);
			style = workbook.createCellStyle();
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
			style.setFont(ms.isTitle()?boldFont:defaulFont);
			style.setBorderBottom(CellStyle.BORDER_THIN);
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
		
		if (ms.paintHeaderBefore()) {
			CellStyle sty = workbook.createCellStyle();
			sty.cloneStyleFrom(style);
			sty.setFont(defaulFont);
			cellCount = 0;
			row = sheet.createRow(rowCount++);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
			addCell("Concepto").setCellStyle(headerCellStyle);
			++cellCount;
			addCell("Rto. Neto").setCellStyle(headerCellStyle);
			addCell("%").setCellStyle(headerCellStyle);
			addCell("Resultado").setCellStyle(headerCellStyle);
			for (Mod131Activity activity : model.getActivities()) {
				cellCount = 0;
				boolean empty = AonStringUtils.isBlank( activity.getEpigraph() ); 
				row = sheet.createRow(rowCount++);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
				String epi = AonStringUtils.abbreviate(activity.getFullDescription(), 80);
				row.setRowStyle(rowStyle);
				addCell(empty?"":epi).setCellStyle(sty);
				++cellCount;
				if (empty) {
					addCell("");	
					addCell("");	
					addCell("");	
				} else {
					sty.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					addCell(activity.getNet()).setCellStyle(sty);
					addCell(AonNumberUtils.toString(activity.getPor())).setCellStyle(sty);
					sty.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
					addCell(activity.getRes()).setCellStyle(sty);
				}
			}
			row = sheet.createRow(rowCount++);
			
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
			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
			row = sheet.createRow(rowCount++);
			CellUtil.createCell(row, 0, paymentInfo , idCellStyle);
			row = sheet.createRow(rowCount++);
		}
	}
	
	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod131Key key) {
		double amount = model.ensureDetail(key).getAmount();
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (key == Mod131Key.P2) {
			cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cell.setCellValue(amount == 1?"SI":"NO");
		}
	}
}
