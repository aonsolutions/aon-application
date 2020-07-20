package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349ExcelAction extends AbsExcelAction { 

	private Mod349 mod349;
	
	public Mod349ExcelAction(Mod349 mod349) {
		super();
		this.mod349 = mod349;
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
		headerCellStyle.setFillForegroundColor(COLORS[mod349.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod349ExcelAction.class.getResourceAsStream(
					IMAGES[ mod349.getAdministration().ordinal()]);
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
		
		int maxCol = 7;
		if (mod349.getAdministration() == Administration.GIPUZKOA)
			maxCol = 6;
		
		// Titulo completo del modelo
		CellUtil.createCell(row, 1, "Modelo 349. Declaración recapitulativa de operaciones intracomunitarias. Ejercicio " + AonNumberUtils.toString(mod349.getYear()) + ". Periodo " + mod349.getPeriod().getName(), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1, maxCol));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, maxCol));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod349.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod349.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod349.getDocument() + " - " + name , idCellStyle);

		// Ahora las cabeceras de las columnas		
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 5, maxCol));
		
		XSSFCellStyle headerCellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle2.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle2.setVerticalAlignment( VerticalAlignment.CENTER);	    
	    headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);	    
	    headerCellStyle2.setFont(headerFont);
		headerCellStyle2.setFillForegroundColor(COLORS[mod349.getAdministration().ordinal()]);
		
		CellUtil.createCell(row, 5, "Rectificaciones", headerCellStyle2);
		
		row = sheet.createRow(rowCount++);  // Línea con las cabeceras de columnas
		
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, "Clave", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "País", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Documento", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Apellidos y nombre o denominación", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Base imponible", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Año", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Periodo", headerCellStyle); 
		if (mod349.getAdministration() != Administration.GIPUZKOA)
			CellUtil.createCell(row, cellCount++, "Importe declarado anteriormente", headerCellStyle); 
	
		// Definir ancho para las distintas columnas
		sheet.setColumnWidth(0,  5 * 256);  // Ancho para la columna Clave		
		sheet.setColumnWidth(1, 13 * 256);  // Ancho para la columna País
		sheet.setColumnWidth(2, 11 * 256);  // Ancho para la columna Documento
		sheet.setColumnWidth(3, 30 * 256);  // Ancho para la columna Nombre o Razón Social
		sheet.setColumnWidth(4, 14 * 256);  // Ancho para la columna Base imponnible
		sheet.setColumnWidth(5,  5 * 256);  // Ancho para la columna Rectificación - Año
		sheet.setColumnWidth(6,  7 * 256);  // Ancho para la columna Rectificación - Periodo
		if (mod349.getAdministration() != Administration.GIPUZKOA)
			sheet.setColumnWidth(7, 31 * 256);  // Ancho para la columna Rectificación - Base declarada anteriormente
		
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod349) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
	
	public void accept(Mod349Detail detail) {
		
		row = sheet.createRow(rowCount++);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(defaulFont );
		
		centerCellStyle.setFont(defaulFont);
		decimalStyle.setFont(defaulFont);
		
		cellCount = 0;
		
		CellUtil.createCell(row, cellCount++, Mod349Key.safeValue(detail.getType()) , centerCellStyle);                 // Clave
		CellUtil.createCell(row, cellCount++, detail.getCountry() == null ? "" : detail.getCountry().getName(), style); // Pais
		CellUtil.createCell(row, cellCount++, detail.getDocument(), style); 											// Documento
		CellUtil.createCell(row, cellCount++, detail.getName(), style); 												// Nombre
		createAmountCell(detail.getAmount()); 																			// Base imponible
		
		if (detail.isRectification()) {
			if (detail.getRectifiedYear() != null)
				CellUtil.createCell(row, cellCount++, detail.getRectifiedYear().toString(), centerCellStyle); // Rectificación - Año
			else cellCount++;
			if (detail.getRectifiedPeriod() != null) 
			   CellUtil.createCell(row, cellCount++, detail.getRectifiedPeriod().getName(), centerCellStyle); // Rectificación - Periodo
			else cellCount++;
			if (mod349.getAdministration() != Administration.GIPUZKOA)
				createAmountCell(detail.getRectifiedAmount()); // Rectificación - Importe declarado anteriormente
		}
		
	}
	
	private void createAmountCell(double amount) {
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(decimalStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(amount);
		
	}
	
}
