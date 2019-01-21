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
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347ExcelAction extends AbsExcelAction { // ModelVAExcelAction<Mod303,Mod303Key> {

	private Mod347 mod347;
	
	public Mod347ExcelAction(Mod347 mod347) {
		super();
		this.mod347 = mod347;
	}
	
	@Override
	protected void headerRow() {
				
	}
	
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
	protected XSSFCellStyle idCellStyle;
	
	@Override
	public void initialize(String name, boolean printHeaders) {
		super.initialize(name, printHeaders);
		printModelInfo();
	}
	
	protected void printModelInfo() {
		
		Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.index);
		headerFont.setFontHeightInPoints((short) 10);
		
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(COLORS[mod347.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod347ExcelAction.class.getResourceAsStream(
					IMAGES[ mod347.getAdministration().ordinal()]);
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
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1), (rowCount), 0, 0));
		
		// Titulo completo del modelo
		CellUtil.createCell(row, 1, "Modelo 347. Declaración anual operaciones con terceras personas. Ejercicio " + AonNumberUtils.toString(mod347.getYear()),headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1,11));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 11));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod347.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod347.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		idCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod347.getDocument() + " - " + name , idCellStyle);

		// Ahora las cabeceras de las columnas
		sheet.createRow(rowCount++); // Linea vacia
		
		row = sheet.createRow(rowCount++);  // Línea con las cabeceras de columnas
	
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, "Clave", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "NIF", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Nombre o Razón Social", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Provincia", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "País", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Total", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Trim. 1", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Trim. 2", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Trim. 3", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Trim. 4", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Importe RECC", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Observaciones", headerCellStyle);
		
		// Definir ancho para las distintas columnas
		sheet.autoSizeColumn(0);             // Autosize columna Clave
		sheet.setColumnWidth(1, 11 * 256);   // Ancho para la columna NIF
		sheet.setColumnWidth(2, 30 * 256);   // Ancho para la columna Nombre o Razón Social
		sheet.setColumnWidth(3, 13 * 256);   // Ancho para la columna Provincia
		sheet.setColumnWidth(4, 13 * 256);   // Ancho para la columna País
		sheet.setColumnWidth(10, 14 * 256);  // Ancho para la columna Importe RECC
		sheet.setColumnWidth(11, 20 * 256);  // Ancho para la columna Observaciones
			
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod347) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
	
	public void accept(Mod347Declared declared) {
		
		row = sheet.createRow(rowCount++);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);		
		style.setFont(defaulFont );
		
		centerCellStyle.setFont(defaulFont);
		decimalStyle.setFont(defaulFont);
		
		cellCount = 0;
		
		CellUtil.createCell(row, cellCount++, Mod347Key.safeValue(declared.getType()) , centerCellStyle);
		CellUtil.createCell(row, cellCount++, AonStringUtils.isNotBlank(declared.getOperatorNif()) ? declared.getOperatorNif() : declared.getDocument(), style);
		CellUtil.createCell(row, cellCount++, declared.getName(), style);
		CellUtil.createCell(row, cellCount++, declared.getProvince() == null || declared.getProvince() == Province.DESCONOCIDO ? "" : declared.getProvince().getName(), style);
		CellUtil.createCell(row, cellCount++, declared.getCountry() == null ? "" : declared.getCountry().getName(), style);
		
		createAmountCell(declared.getAmount());
		createAmountCell(declared.getFirstQuarterAmount());
		createAmountCell(declared.getSecondQuarterAmount());
		createAmountCell(declared.getThirdQuarterAmount());
		createAmountCell(declared.getFourthQuarterAmount());
		createAmountCell(declared.getVatAccrualAmount());
		
		String observaciones = "";
		
		if (declared.isInsuranceOperation())
			observaciones = "Operaciones de seguros";
		else if (declared.isBusinessPremiseRental())
			observaciones = "Operaciones arrendamiento";
		else if (declared.isVatAccrual())
			observaciones = "Operaciones RECC";
		else if (declared.isIsp())
			observaciones = "Operaciones ISP";
		else if (declared.isDepositRegime())
			observaciones = "Operaciones reg. dep. distinto aduanero";
		
		CellUtil.createCell(row, cellCount++, observaciones, style);			
		
	}
	
	private void createAmountCell(double amount) {
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(decimalStyle);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(amount);
		
	}
	
}
