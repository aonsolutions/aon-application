package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190ExcelAction extends AbsExcelAction { 

	private Mod190 mod190;
	
	public Mod190ExcelAction(Mod190 mod190) {
		super();
		this.mod190 = mod190;
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
		headerCellStyle.setFillForegroundColor(COLORS[mod190.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod190ExcelAction.class.getResourceAsStream(
					IMAGES[ mod190.getAdministration().ordinal()]);
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
		
		int maxCol = 16;
		if (mod190.getAdministration() == Administration.GIPUZKOA)
			maxCol = 14;
		
		// Titulo completo del modelo
		CellUtil.createCell(row, 1, "Modelo 190. Rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de rentas. Ejercicio " + AonNumberUtils.toString(mod190.getYear()),headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1,maxCol));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, maxCol));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod190.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod190.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod190.getDocument() + " - " + name , idCellStyle);
		
		sheet.createRow(rowCount++); // Linea vacia
		
		// Ahora las cabeceras de las columnas
		XSSFCellStyle headerCellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
		headerCellStyle2.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle2.setVerticalAlignment( VerticalAlignment.CENTER);	    
	    headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);	    
	    headerCellStyle2.setFont(headerFont);
		headerCellStyle2.setFillForegroundColor(COLORS[mod190.getAdministration().ordinal()]);
		
		if (mod190.getAdministration() != Administration.GIPUZKOA) {
			row = sheet.createRow(rowCount++);
			
			sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 12, 16));
			CellUtil.createCell(row, 12, "Percepciones derivadas de incapacidad temporal", headerCellStyle2);
		}
		
		row = sheet.createRow(rowCount++);
		
		headerCellStyle2.setBorderLeft(BorderStyle.MEDIUM);		
		
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 6, 7));
		CellUtil.createCell(row, 6, "Dinerarias", headerCellStyle2);

		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 8, 10));
		CellUtil.createCell(row, 8, "En Especie", headerCellStyle2);
		
		// Gipuzkoa solo tiene 3 casillas en Percepciones IL
		if (mod190.getAdministration() == Administration.GIPUZKOA) {						
			sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 12, 14));
			CellUtil.createCell(row, 12, "Percepciones derivadas de incapacidad temporal", headerCellStyle2);
		}
		else {
			sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 12, 13));
			CellUtil.createCell(row, 12, "Dinerarias", headerCellStyle2);
			
			sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount-1), 14, 16));
			CellUtil.createCell(row, 14, "En Especie", headerCellStyle2);
		}
		
		row = sheet.createRow(rowCount++);  // Línea con las cabeceras de columnas
	
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, "NIF Perceptor", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "NIF Representante", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Apellidos y nombre o denominación", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Provincia", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Clave", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Subclave", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Percepc. Integras", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Retenciones", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Valoración", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Ingr. a cta. efectuados", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Ingr. a cta. repercutidos", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Ej. Devengo", headerCellStyle);
				
		if (mod190.getAdministration() == Administration.GIPUZKOA) {
			CellUtil.createCell(row, cellCount++, "Perc.íntegra/Valoración", headerCellStyle); 
			CellUtil.createCell(row, cellCount++, "Ret.aplic./Ing.a cta.", headerCellStyle);
			CellUtil.createCell(row, cellCount++, "Ingr. a cta. repercutidos", headerCellStyle);
		}
		else {
			CellUtil.createCell(row, cellCount++, "Percep. Integras", headerCellStyle); 
			CellUtil.createCell(row, cellCount++, "Retenciones", headerCellStyle);
			CellUtil.createCell(row, cellCount++, "Valoración", headerCellStyle);
			CellUtil.createCell(row, cellCount++, "Ingr. a cta. efectuados", headerCellStyle);
			CellUtil.createCell(row, cellCount++, "Ingr. a cta. repercutidos", headerCellStyle);
		}
		
		// Definir ancho para las distintas columnas
		sheet.setColumnWidth( 0, 12 * 256);  // Ancho para la columna NIF Perceptor
		sheet.setColumnWidth( 1, 15 * 256);  // Ancho para la columna NIF Representante
		sheet.setColumnWidth( 2, 35 * 256);  // Ancho para la columna Apellidos y nombre o denominación
		sheet.setColumnWidth( 3, 13 * 256);  // Ancho para la columna Provincia
		sheet.setColumnWidth( 4,  5 * 256);  // Ancho para la columna Clave
		sheet.setColumnWidth( 5,  8 * 256);  // Ancho para la columna Subclave
		sheet.setColumnWidth( 6, 17 * 256);  // Ancho para la columna Dinerarias - Percepciones Integras
		sheet.setColumnWidth( 7, 17 * 256);  // Ancho para la columna Dinerarias - Retenciones
		sheet.setColumnWidth( 8, 17 * 256);  // Ancho para la columna En Especie - Valoración
		sheet.setColumnWidth( 9, 25 * 256);  // Ancho para la columna En Especie - Ingr. a cta. efectuados
		sheet.setColumnWidth(10, 25 * 256);  // Ancho para la columna En Especie - Ingr. a cta. repercutidos
		sheet.setColumnWidth(11, 10 * 256);  // Ancho para la columna Ejercicio Devengo
		if (mod190.getAdministration() == Administration.GIPUZKOA) {
			sheet.setColumnWidth(12, 19 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - Perc.Integra / Valoracion
			sheet.setColumnWidth(13, 19 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - Retenciones / Ing. a cta.
			sheet.setColumnWidth(14, 19 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - En Especie - Ingr. a cta. repercutidos
		}
		else {
			sheet.setColumnWidth(12, 17 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - Dinerarias - Perc.Integra 
			sheet.setColumnWidth(13, 17 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - Dinerarias - Retenciones
			sheet.setColumnWidth(14, 17 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - En Especie - Valoración
			sheet.setColumnWidth(15, 19 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - En Especie - Ingr. a cta. efectuados
			sheet.setColumnWidth(16, 19 * 256);  // Ancho para la columna Percepciones derivadas de incapacidad temporal - En Especie - Ingr. a cta. repercutidos
		}
		
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod190) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
	
	public void accept(Mod190Detail detail) {
		
		row = sheet.createRow(rowCount++);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(defaulFont );
		
		centerCellStyle.setFont(defaulFont);
		decimalStyle.setFont(defaulFont);
		
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, detail.getDocument(), style); // 0 NIF Perceptor
		CellUtil.createCell(row, cellCount++, detail.getRepresentativeDocument(), style); // 1 NIF Representante
		CellUtil.createCell(row, cellCount++, detail.getName(), style);  // 2 Apellidos y nombre o denominación
		CellUtil.createCell(row, cellCount++, Province.safeValueOf(detail.getProvince()) == null || Province.safeValueOf(detail.getProvince()) == Province.DESCONOCIDO ? "" : Province.safeValueOf(detail.getProvince()).getName(), style); // 3 Provincia
		CellUtil.createCell(row, cellCount++, detail.getKey(), style);  // 4 Clave
		CellUtil.createCell(row, cellCount++, detail.getSubKey(), style); // 5 Subclave
		createAmountCell(detail.getPerception()); // 6 Dinerarias - Percepciones Integras
		createAmountCell(detail.getRetention()); // 7 Dinerarias - Retenciones
		createAmountCell(detail.getInKindPerception());  // 8 En Especie - Valoración
		createAmountCell(detail.getInKindDeposit());   // 9 En Especie - Ingr. a cta. efectuados
		createAmountCell(detail.getInKindOutputDeposit()); // 10 En Especie - Ingr. a cta. repercutidos
		CellUtil.createCell(row, cellCount++, detail.getAccrualYear() == 0 ? "" : Integer.toString(detail.getAccrualYear()) , style);  // 11 Ejercicio Devengo
		if (mod190.getAdministration() == Administration.GIPUZKOA) {
			createAmountCell(detail.getPerceptionIL()); // 12 Percepciones derivadas de incapacidad temporal - Perc.Integra / Valoración  
			createAmountCell(detail.getRetentionIL()); // 13 Percepciones derivadas de incapacidad temporal - Retenc. aplic. / Ing. a cta.
			createAmountCell(detail.getOutputRetentionIL()); // 14 Percepciones derivadas de incapacidad temporal - Ingr. a cta. repercutidos
		}
		else {
			createAmountCell(detail.getPerceptionIL()); // 12 Percepciones derivadas de incapacidad temporal - Dinerarias - Perc.Integra  
			createAmountCell(detail.getRetentionIL()); // 13 Percepciones derivadas de incapacidad temporal - Dinerarias - Retenciones
			createAmountCell(detail.getInKindPerceptionIL()); // 14 Percepciones derivadas de incapacidad temporal - En Especie - Valoración
			createAmountCell(detail.getInKindDepositIL()); // 15 Percepciones derivadas de incapacidad temporal - En Especie - Ingr. a cta. efectuados
			createAmountCell(detail.getInKindOutputDepositIL()); // 16 Percepciones derivadas de incapacidad temporal - En Especie - Ingr. a cta. repercutidos
		}
		
	}
	
	private void createAmountCell(double amount) {
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(decimalStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(amount);
		
	}
	
}
