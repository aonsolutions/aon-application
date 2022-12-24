package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

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
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Page03.Mod390DetailKeyGroup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018DetailKey;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod3902018ExcelAction extends AbsExcelAction {
	
	private Mod3902018 mod390;
	
	public Mod3902018ExcelAction(Mod3902018 mod390) {
		super();
		this.mod390 = mod390;
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
		headerCellStyle.setFillForegroundColor(COLORS[mod390.getAdministration().ordinal()]);
				
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		try {
			InputStream inputStream = Mod3902018ExcelAction.class.getResourceAsStream(
					IMAGES[ mod390.getAdministration().ordinal()]);
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
		CellUtil.createCell(row, 1, "Modelo 390. IVA. Declaración Resumen Anual. Ejercicio " + AonNumberUtils.toString(mod390.getYear()),headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress((rowCount-1),(rowCount), 1,4));
		
		// NIF y Nombre de la Empresa
		row = sheet.createRow(rowCount++);
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
		
		row = sheet.createRow(rowCount++);
		
		String name = AonStringUtils.trim(
				AonStringUtils.defaultIfBlank(mod390.getName(), AonStringUtils.EMPTY)
				+AonStringUtils.SPACE
				+AonStringUtils.defaultIfBlank(mod390.getSurname(), AonStringUtils.EMPTY));
		
		idFont = workbook.createFont();
		idFont.setBold(true);
		idFont.setFontHeightInPoints((short) 12);
		
		idCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		idCellStyle.setAlignment(HorizontalAlignment.CENTER);
		idCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		idCellStyle.setFont(idFont);

		CellUtil.createCell(row, 0, mod390.getDocument() + " - " + name , idCellStyle);
		
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
		footer.setLeft("Modelo " + FiscalModelUtils.getModelName(mod390) );
		footer.setRight("P\u00E1g: &P/&N");
		
	}
		
	public void accept(Mod3902018 m390) {
		
		// Regimen General
		for (Mod390DetailKeyGroup key : Mod390DetailKeyGroup.values()) {
			
			String s = "";
			if (key.toString().startsWith("DEV")) 
				s = "IVA DEVENGADO - ";
			else if (key.toString().startsWith("DED") && key != Mod390DetailKeyGroup.DED_026)
				s = "IVA DEDUCIBLE - ";
				
			for (Mod3902018DetailKey key2 : key.getKeys())		
			  addEnsure("Régimen General", s + key.getLabel(), mod390, key2);
			
		}
		
		// Régimen Simplificado (Totales)
		addBox("Régimen Simplificado", "IVA DEVENGADO - Suma de cuotas derivadas régimen simplificado (Actividades no agrícolas)", m390.getBox74());
		addBox("Régimen Simplificado", "IVA DEVENGADO - Suma de cuotas derivadas régimen simplificado (Actividades agrícolas)", m390.getBox75());
		addBox("Régimen Simplificado", "IVA DEVENGADO - IVA devengado en adquisiciones intracomunitarias de bienes", m390.getBox76());
		addBox("Régimen Simplificado", "IVA DEVENGADO - IVA devengado por inversión de sujeto pasivo (adquisiciones intracomunitarias de servicios y otros supuestos)", m390.getBox77());
		addBox("Régimen Simplificado", "IVA DEVENGADO - IVA devengado en entregas de activos fijos", m390.getBox78());
		addBox("Régimen Simplificado", "IVA DEVENGADO - Total Cuota Resultante", m390.getBox79());
		addBox("Régimen Simplificado", "IVA DEDUCIBLE - IVA soportado en adquisición de activos fijos", m390.getBox80());
		addBox("Régimen Simplificado", "IVA DEDUCIBLE - Regularización de bienes de inversión", m390.getBox81());
		addBox("Régimen Simplificado", "IVA DEDUCIBLE - Suma de deducciones", m390.getBox82());
		addBox("Régimen Simplificado", "RESULTADO DEL REGIMEN SIMPLIFICADO", 0.0, m390.getBox83(), true);
	
		// Resultado Liquidación Anual
		addBox("Resultado Liq. Anual", "Regularización cuotas art. 80.Cinco.5º LIVA", m390.getBox658());
		addBox("Resultado Liq. Anual", "Suma de Resultados", m390.getBox84());
		addBox("Resultado Liq. Anual", "IVA a la importación liquidado por la Aduana (sólo sujetos pasivos con opción de diferimiento)", m390.getBox659());
		addBox("Resultado Liq. Anual", "Compensación de cuotas del ejercicio anterior", m390.getBox85());
		addBox("Resultado Liq. Anual", "RESULTADO DE LA LIQUIDACIÓN", 0.0, m390.getBox86(), true);
		
		// Tributación por razón del territorio
		addBox("Tributación Conjunta", "Regularización cuotas art. 80.Cinco.5º LIVA", m390.getBox658());
		addBox("Tributación Conjunta", "Suma de Resultados", m390.getBox84());	
		addBox("Tributación Conjunta", "Territorio Común (%)", m390.getBox87());
		addBox("Tributación Conjunta", "Araba/Álava (%)", m390.getBox88());
		addBox("Tributación Conjunta", "Gipuzkoa (%)", m390.getBox89());
		addBox("Tributación Conjunta", "Bizkaia (%)", m390.getBox90());
		addBox("Tributación Conjunta", "Navarra (%)", m390.getBox91());
		addBox("Tributación Conjunta", "Resultado atribuible al territorio común", m390.getBox92());
		addBox("Tributación Conjunta", "IVA a la importación liquidado por la Aduana (sólo sujetos pasivos con opción de diferimiento)", m390.getBox659());
		addBox("Tributación Conjunta", "Compensación de cuotas del ejercicio anterior atribuible a territorio común", m390.getBox93());
		addBox("Tributación Conjunta", "Resultado de la declaración anual atribuible a territorio común".toUpperCase(), 0.0, m390.getBox94(), true);
		
		// Resultado de las Liquidaciones
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Total resultados a ingresar en las autoliquidaciones de IVA del ejercicio", m390.getBox95());
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Total devoluciones mensuales de IVA solicitadas por sujetos pasivos inscritos en el Registro de devolución mensual", m390.getBox96());
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Total devoluciones solicitadas por cuotas soportadas en la adquisición de elementos de transporte (Art. 30 bis RIVA)", m390.getBox524());
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Resultado de la autoliquidación del último periodo - A compensar", m390.getBox97());
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Resultado de la autoliquidación del último periodo - A devolver", m390.getBox98());
		addBox("Resultado de las Liquidaciones", "Periodos No Tributan R.E. Grupo Entidades - Cuotas pendientes de compensación al término del ejercicio", m390.getBox662());		
		addBox("Resultado de las Liquidaciones", "Periodos Tributan R.E. Grupo Entidades - Total resultados positivos autoliquidaciones del ejercicio (modelo 322)", m390.getBox525());
		addBox("Resultado de las Liquidaciones", "Periodos Tributan R.E. Grupo Entidades - Total resultados negativos autoliquidaciones del ejercicio (modelo 322)", m390.getBox526());
		
		// Volumen de Operaciones
		addBox("Volumen de Operaciones", "Operaciones en régimen general", m390.getBox99());
		addBox("Volumen de Operaciones", "Operaciones a las que habiéndoles aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art.75 LIVA", m390.getBox653());
		addBox("Volumen de Operaciones", "Entregas intracomunitarias exentas", m390.getBox103());
		addBox("Volumen de Operaciones", "Exportaciones y otras operaciones exentas con derecho a deducción", m390.getBox104());
		addBox("Volumen de Operaciones", "Operaciones exentas sin derecho a deducción", m390.getBox105());
		addBox("Volumen de Operaciones", "Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo", m390.getBox110());	
		addBox("Volumen de Operaciones", "Entregas de bienes objeto de instalación o montaje en otros Estados miembros", m390.getBox112());
		addBox("Volumen de Operaciones", "Operaciones en régimen simplificado", m390.getBox100());	
		addBox("Volumen de Operaciones", "Operaciones en régimen especial de la agricultura, ganadería y pesca", m390.getBox101());
		addBox("Volumen de Operaciones", "Operaciones realizadas por sujetos pasivos acogidos al régimen especial del recargo de equivalencia", m390.getBox102());				
		addBox("Volumen de Operaciones", "Operaciones en Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección", m390.getBox227());
		addBox("Volumen de Operaciones", "Operaciones en régimen especial de Agencias de Viajes", m390.getBox228());
		addBox("Volumen de Operaciones", "Entregas de bienes inmuebles y operaciones financieras no habituales", m390.getBox106());	
		addBox("Volumen de Operaciones", "Entregas de bienes de inversión", m390.getBox107());	
		addBox("Volumen de Operaciones", "Total volumen de operaciones (Art. 121 Ley IVA)", m390.getBox108());
		
		// Operaciones Especificas
		addBox("Operaciones Especificas", "Adquisiciones interiores exentas", m390.getBox230());
		addBox("Operaciones Especificas", "Adquisiciones intracomunitarias exentas", m390.getBox109());
		addBox("Operaciones Especificas", "Importaciones exentas", m390.getBox231());
		addBox("Operaciones Especificas", "Bases imponibles del IVA soportado no deducible", m390.getBox232());
		addBox("Operaciones Especificas", "Operaciones sujetas y no exentas que originan el derecho a la devolución mensual", m390.getBox111());
		addBox("Operaciones Especificas", "Entregas interiores de bienes devengadas por inversión del sujeto pasivo como consecuencia de operaciones triangulares", m390.getBox113());
		addBox("Operaciones Especificas", "Servicios localizados en el territorio de aplicación del impuesto por inversión de sujeto pasivo", m390.getBox523());		
		
		// Regimen Especial del Criterio de Caja
		addBox("Régimen Esp. Criterio de Caja", "Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA", m390.getBox654(), m390.getBox655(),false);
		addBox("Régimen Esp. Criterio de Caja", "Importe de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 75 LIVA", m390.getBox656(), m390.getBox657(),false);
		
	}
	
	private void createAmountCell(double amount, boolean isBold) {
		
		CellStyle style = workbook.createCellStyle();
	    style.setDataFormat(dataFormat.getFormat(DECIMAL_PATTERN));
	    style.setAlignment( HorizontalAlignment.RIGHT );
	    style.setFont(isBold ? boldFont : defaulFont);
		
		Cell cell = row.createCell(cellCount++);
		cell.setCellStyle(style);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(amount);
		
	}
	
    private void addEnsure(String s1, String s2, Mod3902018 m390, Mod3902018DetailKey key ) {
    	
		row = sheet.createRow(rowCount++);
		
		// Algunas lineas van en negrita
		boolean isBold = (key == Mod3902018DetailKey.C0047 || key == Mod3902018DetailKey.C0064 || key == Mod3902018DetailKey.C0065);
		
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.BOTTOM);		
		style.setFont(isBold ? boldFont : defaulFont);
		
		cellCount = 0;
		
		CellUtil.createCell(row, cellCount++, s1, style);
		CellUtil.createCell(row, cellCount++, key == Mod3902018DetailKey.C0065 ? s2.toUpperCase() : s2, style);
		
		// Base - Determinadas casillas solo llevan cuota
		if (key == Mod3902018DetailKey.C0047 || key == Mod3902018DetailKey.C0063 || key == Mod3902018DetailKey.C0522 || key == Mod3902018DetailKey.C0064 || key == Mod3902018DetailKey.C0065 )
			CellUtil.createCell(row, cellCount++, "", style);
		else createAmountCell(m390.ensure(key).getTaxableBase(), isBold);
		
		// Tipo - Determinadas casillas solo llevan base y cuota
		if (m390.ensure(key).getPercent() == 0) 
			CellUtil.createCell(row, cellCount++, "", style);
		else createAmountCell(m390.ensure(key).getPercent(), isBold);
		
		// Cuota
		createAmountCell(m390.ensure(key).getQuota(), isBold);
    	
    }
    
    private void addBox(String s1, String s2, double amount) {    	
    	addBox(s1, s2, 0.0, amount, false);    	
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
