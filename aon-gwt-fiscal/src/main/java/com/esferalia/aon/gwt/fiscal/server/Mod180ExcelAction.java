package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod180ExcelAction extends AbsExcelAction { 

	private Mod180 mod180;
	
	public Mod180ExcelAction(Mod180 mod180) {
		super();
		this.mod180 = mod180;
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
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.index);
		headerFont.setFontHeightInPoints((short) 10);
		
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(COLORS[mod180.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod180ExcelAction.class.getResourceAsStream(
					IMAGES[ mod180.getAdministration().ordinal()]);
			byte[] imageBytes = AonIOUtils.toByteArray(inputStream);
			int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			inputStream.close();
			CreationHelper helper = workbook.getCreationHelper();
			SXSSFDrawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
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
		CellUtil.createCell(row, 1, "Modelo 180. Resumen anual. Rendimientos procedentes del arrendamiento de inmuebles urbanos. Ejercicio " + AonNumberUtils.toString(mod180.getYear()),headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1,8));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 8));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod180.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod180.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod180.getDocument() + " - " + name , idCellStyle);

		// Ahora las cabeceras de las columnas
		sheet.createRow(rowCount++); // Linea vacia
		
		row = sheet.createRow(rowCount++);  // Línea con las cabeceras de columnas
	
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, "NIF Perceptor", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "NIF Representante", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Apellidos y nombre o denominación", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Provincia", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Modalidad", headerCellStyle);  
		CellUtil.createCell(row, cellCount++, "Percepciones integras", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Porcentaje", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Retenciones", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Ej.Devengo", headerCellStyle);
		
		// Definir ancho para las distintas columnas
		
		sheet.setColumnWidth(0, 12 * 256);  // Ancho para la columna NIF Perceptor
		sheet.setColumnWidth(1, 15 * 256);  // Ancho para la columna NIF Representante
		sheet.setColumnWidth(2, 35 * 256);  // Ancho para la columna Apellidos y nombre o denominación
		sheet.setColumnWidth(3, 13 * 256);  // Ancho para la columna Provincia
		sheet.setColumnWidth(4, 13 * 256);  // Ancho para la columna Modalidad
		sheet.setColumnWidth(5, 17 * 256);  // Ancho para la columna Percepciones integras
		sheet.setColumnWidth(6, 10 * 256);  // Ancho para la columna Porcentaje
		sheet.setColumnWidth(7, 14 * 256);  // Ancho para la columna Retenciones
		sheet.setColumnWidth(8, 10 * 256);  // Ancho para la columna Ej.Devengo
			
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod180) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
	
	public void accept(Mod180Detail detail) {
		
		row = sheet.createRow(rowCount++);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(defaulFont );
		
		centerCellStyle.setFont(defaulFont);
		decimalStyle.setFont(defaulFont);
		
		cellCount = 0;
		
		CellUtil.createCell(row, cellCount++, detail.getDocument(), style);  // NIF Perceptor
		CellUtil.createCell(row, cellCount++, detail.getRepresentativeDocument(), style); // NIF Representante
		CellUtil.createCell(row, cellCount++, detail.getName(), style);      // Apellidos y nombre o denominación
		CellUtil.createCell(row, cellCount++, Province.safeValueOf(detail.getProvince()) == null || Province.safeValueOf(detail.getProvince()) == Province.DESCONOCIDO ? "" : Province.safeValueOf(detail.getProvince()).getName(), style); // Provincia
		CellUtil.createCell(row, cellCount++, detail.isInKind() ? "En Especie" : "Dinerario", style); // Modalidad (Dinerario / En Especie)  
		createAmountCell(detail.getPerception());                            // Percepciones Integras
		createAmountCell(detail.getPercent());                               // Porcentaje
		createAmountCell(detail.getRetention());  		                     // Retenciones
		CellUtil.createCell(row, cellCount++, detail.getAccrualYear() == 0 ? "" : Integer.toString(detail.getAccrualYear()) , style);  // Ej.Devengo 
		
	}
	
	private void createAmountCell(double amount) {
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(decimalStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(amount);
		
	}
	
}
