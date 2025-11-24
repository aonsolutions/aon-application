package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025Description;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKeyGroup;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod4252025ExcelAction extends AbsExcelAction {
	
	private Mod4252025 mod425;
	
	public Mod4252025ExcelAction(Mod4252025 mod425) {
		super();
		this.mod425 = mod425;
	}
	
	@Override
	protected void headerRow() {
				
	}
	
	protected static final XSSFColor ARABA_BG = new XSSFColor(new java.awt.Color(163, 12, 81), new DefaultIndexedColorMap());
	protected static final XSSFColor BIZKAIA_BG = new XSSFColor(new java.awt.Color(215, 0, 4), new DefaultIndexedColorMap());
	protected static final XSSFColor GIPUZKOA_BG = new XSSFColor(new java.awt.Color(161, 192, 49), new DefaultIndexedColorMap());
	protected static final XSSFColor NAVARRA_BG = new XSSFColor(new java.awt.Color(218, 0, 42), new DefaultIndexedColorMap());
	protected static final XSSFColor AEAT_BG = new XSSFColor(new java.awt.Color(58, 133, 195), new DefaultIndexedColorMap());
	protected static final XSSFColor CANARIAS_BG = new XSSFColor(new java.awt.Color(251, 186, 0), new DefaultIndexedColorMap());

	protected  static final XSSFColor[] COLORS = new XSSFColor[] { ARABA_BG, BIZKAIA_BG, GIPUZKOA_BG, NAVARRA_BG, AEAT_BG, CANARIAS_BG };

//	protected  static final String[] IMAGES = new String[] { 
//			"/com/esferalia/aon/gwt/common/client/css/images/aon-araba-header-image.png"
//			,"/com/esferalia/aon/gwt/common/client/css/images/aon-bizkaia-header-image.png"
//			,"/com/esferalia/aon/gwt/common/client/css/images/aon-gipuzkoa-header-image.png"
//			,"/com/esferalia/aon/gwt/common/client/css/images/aon-navarra-header-image.png"
//			,"/com/esferalia/aon/gwt/common/client/css/images/aon-aeat-header-image.png"
//	};
	
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
//		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setFontHeightInPoints((short) 14);
		
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(COLORS[mod425.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
//		try {
//			InputStream inputStream = Mod4252025ExcelAction.class.getResourceAsStream(
//					IMAGES[mod425.getAdministration().ordinal()]);
//			byte[] imageBytes = AonIOUtils.toByteArray(inputStream);
//			int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
//			inputStream.close();
//			CreationHelper helper = workbook.getCreationHelper();
//			SXSSFDrawing drawing = sheet.createDrawingPatriarch();
//			ClientAnchor anchor = helper.createClientAnchor();
//			anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
//			anchor.setCol1(0);
//			anchor.setRow1(rowCount - 1 );
//			anchor.setDx1(10);
//			anchor.setDy1(10);
//			Picture pict = drawing.createPicture(anchor, pictureureIdx);
//			pict.resize();
//		} catch (IOException e) {
//			e.printStackTrace();
//			// Sin Imagen,.
//		}
		
		// Modelo
//		CellUtil.createCell(row, 0, "");
		CellUtil.createCell(row, 0, FiscalModelUtils.getModelName(mod425), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1), (rowCount), 0, 0));
		
		// Descripción
//		CellUtil.createCell(row, 1, "Modelo 425. IGIC. Declaración Resumen Anual. Ejercicio " + AonNumberUtils.toString(mod425.getYear()),headerCellStyle);
//		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1,4));
		CellUtil.createCell(row, 1, FiscalModelUtils.getModelDescription(mod425), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1), (rowCount), 1, 3));
		
		// Ejercicio
		CellUtil.createCell(row, 4, AonNumberUtils.toString(mod425.getYear()), headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1), (rowCount), 4, 4));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod425.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod425.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod425.getDocument() + " - " + name , idCellStyle);
		
		// Ahora las cabeceras de las columnas
		sheet.createRow(rowCount++); // Linea vacia
		
		row = sheet.createRow(rowCount++);  // Línea con las cabeceras de columnas
	
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, "Apartado", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Descripción", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "Base", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Tipo", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Cuota/Importe", headerCellStyle);
		
		// Definir ancho para las distintas columnas
		sheet.setColumnWidth(0, 30 * 256);  // Ancho para la columna Apartado
		sheet.setColumnWidth(1, 90 * 256);  // Ancho para la columna Descripción
		sheet.setColumnWidth(2, 17 * 256);  // Ancho para la columna Base
		sheet.setColumnWidth(3,  7 * 256);  // Ancho para la columna Tipo
		sheet.setColumnWidth(4, 17 * 256);  // Ancho para la columna Cuota/Importe
		
		Footer footer = sheet.getFooter();
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod425) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
		
	public void accept(Mod4252025 m425) {
		
		// Regimen General
		for (Mod4252025DetailKeyGroup key : Mod4252025DetailKeyGroup.values()) {
			String s = "";
			if (key.toString().startsWith("DEV")) 
				s = "IGIC DEVENGADO - ";
			else if (key.toString().startsWith("DED"))
				s = "IGIC DEDUCIBLE - ";
				
			for (Mod4252025DetailKey key2 : key.getKeys())		
			  addEnsure("Régimen General", s + key.getLabel(), mod425, key2);
		}
		
		// Régimen Simplificado (Totales)
		addBox("Régimen Simplificado", Mod4252025Description.BOX_103_TEXT, m425.getBox103());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_104_TEXT, m425.getBox104());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_105_TEXT, m425.getBox105());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_106_TEXT, m425.getBox106());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_107_TEXT, m425.getBox107(), true);
		addBox("Régimen Simplificado", Mod4252025Description.BOX_108_TEXT, m425.getBox108());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_109_TEXT, m425.getBox109());
		addBox("Régimen Simplificado", Mod4252025Description.BOX_110_TEXT, m425.getBox110(), true);
		addBox("Régimen Simplificado", Mod4252025Description.BOX_111_TEXT.toUpperCase(), m425.getBox111(), true);
	
		// Resultado Liquidación Anual
		addBox("Resultado Liquidación Anual", Mod4252025Description.BOX_112_TEXT, m425.getBox112());
		addBox("Resultado Liquidación Anual", Mod4252025Description.BOX_113_TEXT, m425.getBox113());
		addBox("Resultado Liquidación Anual", Mod4252025Description.BOX_114_TEXT, m425.getBox114());
		addBox("Resultado Liquidación Anual", Mod4252025Description.BOX_115_TEXT, m425.getBox115(), true);
		
		// Resultado de las Autoliquidaciones
		addBox("Resultado Autoliquidaciones", Mod4252025Description.BOX_116_TEXT, m425.getBox116());
		addBox("Resultado Autoliquidaciones", Mod4252025Description.BOX_117_TEXT, m425.getBox117());
		addBox("Resultado Autoliquidaciones", Mod4252025Description.BOX_118_TEXT, m425.getBox118());
		addBox("Resultado Autoliquidaciones", Mod4252025Description.BOX_119_TEXT, m425.getBox119());
		
		// Operaciones Específicas
		addBox("Operaciones Específicas", Mod4252025Description.BOX_120_TEXT, m425.getBox120());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_121_TEXT, m425.getBox121());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_122_TEXT, m425.getBox122());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_123_TEXT, m425.getBox123());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_124_TEXT, m425.getBox124());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_125_TEXT, m425.getBox125());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_126_TEXT, m425.getBox126());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_127_TEXT, m425.getBox127());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_128_TEXT, m425.getBox128());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_129_TEXT, m425.getBox129());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_130_TEXT, m425.getBox130());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_131_TEXT, m425.getBox131());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_132_TEXT, m425.getBox132());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_133_TEXT, m425.getBox133());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_134_TEXT, m425.getBox134(),true);
		addBox("Operaciones Específicas", Mod4252025Description.BOX_135_TEXT, m425.getBox135());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_136_TEXT, m425.getBox136());
		addBox("Operaciones Específicas", Mod4252025Description.BOX_137_TEXT, m425.getBox137());
		
		// Regimen Especial del Criterio de Caja
		addBox("Importes RECC", Mod4252025Description.BOX_138_139_TEXT, m425.getBox138(), m425.getBox139());
		addBox("Importes RECC", Mod4252025Description.BOX_140_141_TEXT, m425.getBox140(), m425.getBox141());
		
		// Régimen especial del pequeño empresario o profesional (REPEP)
		addBox("Operaciones REPEP", Mod4252025Description.BOX_142_TEXT, m425.getBox142());
		addBox("Operaciones REPEP", Mod4252025Description.BOX_143_TEXT, m425.getBox143());
		addBox("Operaciones REPEP", Mod4252025Description.BOX_144_TEXT, m425.getBox144());
		addBox("Operaciones REPEP", Mod4252025Description.BOX_145_TEXT, m425.getBox145());
		addBox("Operaciones REPEP", Mod4252025Description.BOX_146_TEXT, m425.getBox146());
		addBox("Operaciones REPEP", Mod4252025Description.BOX_147_TEXT, m425.getBox147());
		
	}
	
	private void createAmountCell(double amount, boolean isBold) {
		
		CellStyle style = workbook.createCellStyle();
	    style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	    style.setAlignment( HorizontalAlignment.RIGHT );
	    style.setFont(isBold ? boldFont : defaulFont);
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(style);
		cell.setCellValue(amount);
		
	}
	
    private void addEnsure(String s1, String s2, Mod4252025 m425, Mod4252025DetailKey key) {
    	
		row = sheet.createRow(rowCount++);
		
		// Algunas lineas van en negrita
		boolean isBold = (key == Mod4252025DetailKey.C074 || key == Mod4252025DetailKey.C079 || key == Mod4252025DetailKey.C094 || key == Mod4252025DetailKey.C095);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(isBold ? boldFont : defaulFont);
		
		cellCount = 0;
		
		// Apartado y Descripción
		CellUtil.createCell(row, cellCount++, s1, style);
		CellUtil.createCell(row, cellCount++, key == Mod4252025DetailKey.C095 ? s2.toUpperCase() : s2, style);
		
		// Base - Determinadas casillas solo llevan cuota
		if (key == Mod4252025DetailKey.C079 || key == Mod4252025DetailKey.C090 || 
				key == Mod4252025DetailKey.C091 || 
				key == Mod4252025DetailKey.C092 || 
				key == Mod4252025DetailKey.C093 ||
				key == Mod4252025DetailKey.C094 ||
				key == Mod4252025DetailKey.C095)
			CellUtil.createCell(row, cellCount++, "", style);
		else 
			createAmountCell(m425.ensure(key).getTaxableBase(), isBold);
		
		// Tipo - Determinadas casillas solo llevan base y/o cuota
		if (m425.ensure(key).getPercent() == 0 &&
				key != Mod4252025DetailKey.C003 &&
				key != Mod4252025DetailKey.C021 &&
				key != Mod4252025DetailKey.C036 &&
				key != Mod4252025DetailKey.C051 
				) 
			CellUtil.createCell(row, cellCount++, "", style);
		else createAmountCell(m425.ensure(key).getPercent(), isBold);
		
		// Cuota - Alguna casilla no lleva cuota
		if (key != Mod4252025DetailKey.C074)
			createAmountCell(m425.ensure(key).getQuota(), isBold);
    	
    }
    
    private void addBox(String s1, String s2, double amount) {    	
    	addBox(s1, s2, 0.0, amount, false);    	
    }
    
    private void addBox(String s1, String s2, double amount, boolean isBold) {
    	addBox(s1, s2, 0.0, amount, isBold);    	
    }
    
	private void addBox(String s1, String s2, double amount1, double amount2) {
		addBox(s1, s2, amount1, amount2, false);
	}
    
	private void addBox(String s1, String s2, double amount1, double amount2, boolean isBold) {
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(isBold ? boldFont : defaulFont);
		
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		CellUtil.createCell(row, cellCount++, s1, style);
		CellUtil.createCell(row, cellCount++, s2, style);		
		if (amount1 == 0.0) 
			CellUtil.createCell(row, cellCount++, "", style);
		else createAmountCell(amount1, isBold);
		CellUtil.createCell(row, cellCount++, "", style);
		createAmountCell(amount2, isBold);
		
	}
	
}
