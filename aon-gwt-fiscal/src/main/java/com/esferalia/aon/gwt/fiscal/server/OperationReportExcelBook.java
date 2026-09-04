package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Operation Report Excel Book", urlPatterns = { "/aon_gwt_fiscal/roms/OperationReportExcelBook" })
public class OperationReportExcelBook extends HttpServlet {
	
	private static final long serialVersionUID = 4002617139388558939L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {			
			String operationParams = req.getParameter("operationParams");
			String domainName = req.getParameter("domainName");			
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);

			OperationParams params = JsonParser.parseOperationParams(operationParams);			
			
			// Obtener NIF y Nombre de la Empresa (para el nombre del fichero)
			String companyDocument = "";
			String companyName = "";
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			companyDocument = company.getDocument();				
			
			String s = company.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}
			companyName = sb.toString();
			
			// NOMBRE DEL FICHERO (Libros Oficiales):			
            //	El nombre del fichero será formado por la concatenación de los siguientes campos y en el siguiente orden:
            //	 1) Ejercicio
            //	 2) NIF
            //	 3) Tipo de Libros Registro que contiene el fichero, mediante uno de los siguientes valores:
            //	    - C: Todos los Libros Registro del IVA requerido en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una C, correspondiente a la presentación de todos los libros del IVA, incluyendo las facturas expedidas en una pestaña denominada EXPEDIDAS, las facturas recibidas en otra pestaña denominada RECIBIDAS y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
            //	    - D: Todos los Libros Registro del IRPF requerido en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una D,	correspondiente a la presentación de todos los libros del IRPF, incluyendo las ventas e ingresos en una pestaña denominada INGRESOS, las compras y gastos en otra pestaña denominada GASTOS y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
            //	    - T: Todos los Libros Registro Unificados del IRPF e IVA requeridos en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una T, correspondiente a la presentación conjunta de todos los libros de ambos impuestos, incluyendo las "facturas expedidas" y "ventas e ingresos" en una pestaña denominada EXPEDIDAS_INGRESOS, las "facturas recibidas" y "compras y gastos" en otra pestaña denominada RECIBIDAS_GASTOS, y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
            //	 4) Nombre o Razón social			
			// El nombre del fichero y el nombre de las pestañas cambian si es borrador, para evitar que se pueda importar como libro oficial
			String sheetName1 = "";
			String sheetName2 = "";
			String filename = AonDateUtils.getYear(params.getFromDate()) + companyDocument;
			
			if (params.getBookType() == 0) {
				filename = params.isDraft() ? "LibroRegistroIVA" + filename : filename + "C";   // Libros Registro del IVA
			    sheetName1 = params.isDraft() ? "Facturas Emitidas" : "EXPEDIDAS";
				sheetName2 = params.isDraft() ? "Facturas Recibidas" : "RECIBIDAS";
			}
			else if (params.getBookType() == 1) {
				filename = params.isDraft() ? "LibroRegistroIRPF" + filename : filename + "D";   // Libros Registro del IRPF
			    sheetName1 = params.isDraft() ? "Ingresos" : "INGRESOS";
				sheetName2 = params.isDraft() ? "Gastos" : "GASTOS";
			}
			else {
				filename = params.isDraft() ? "LibroRegistroUnificado" + filename : filename + "T";   // Libros Registro Unificados del IRPF e IVA
			    sheetName1 = params.isDraft() ? "Facturas Emitidas e Ingresos" : "EXPEDIDAS_INGRESOS";
				sheetName2 = params.isDraft() ? "Facturas Recibidas y Gastos" : "RECIBIDAS_GASTOS";
			}
			
			filename = filename + companyName;
			
			// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos			
			params.setTabType(0);
			ExcelAction action = new ExcelAction(params);
			action.initialize(sheetName1);
			ACCOUNTING.getOperationBreakdown(occam, params).forEach(action);
			
			// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
			params.setTabType(1);
			action.createSheet(sheetName2);
			ACCOUNTING.getOperationBreakdown(occam, params).forEach(action);
			
            // Formato del fichero Excel 2007
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+filename+"."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}
		
	}

	private class ExcelAction extends AbsExcelAction implements Consumer<OperationBreakdown>{
		
		private Row row2;
		private OperationParams params;
		private XSSFCellStyle headerCellStyleDisabled;
		private XSSFCellStyle headerCellStyleSmall;
		private XSSFCellStyle draftHeaderCellStyle;
		
		public ExcelAction(OperationParams params) {
			this.params = params;
		}
		
		@Override
		public void createSheet(String name) {
			sheet = workbook.createSheet(name);
		    rowCount = 0;
		    cellCount = 0;
		    headerRow();		    
		}

		@Override
		protected void headerRow() {

			Font topHeaderFont = workbook.createFont();
			topHeaderFont.setBold(true);
			topHeaderFont.setFontHeightInPoints((short) 10);
			
			Font topHeaderFontDisabled = workbook.createFont();
			topHeaderFontDisabled.setBold(true);
			topHeaderFontDisabled.setFontHeightInPoints((short) 10);
			topHeaderFontDisabled.setColor( IndexedColors.GREY_50_PERCENT.index );
			
			Font topHeaderFontSmall = workbook.createFont();
			topHeaderFontSmall.setBold(true);
			topHeaderFontSmall.setFontHeightInPoints((short) 8);
			
			Font draftHeaderFont = workbook.createFont();
			draftHeaderFont.setBold(true);
			draftHeaderFont.setFontHeightInPoints((short) 16);
			draftHeaderFont.setColor(IndexedColors.RED.index);
			
		    headerCellStyle.setBorderBottom(BorderStyle.THIN);
		    headerCellStyle.setBorderTop(BorderStyle.THIN);
		    headerCellStyle.setBorderLeft(BorderStyle.THIN);
		    headerCellStyle.setBorderRight(BorderStyle.THIN);
		    headerCellStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerCellStyle.setFont(topHeaderFont);
		    headerCellStyle.setWrapText(true);
		    
		    headerCellStyleDisabled = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyleDisabled.setAlignment( HorizontalAlignment.CENTER );
			headerCellStyleDisabled.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerCellStyleDisabled.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerCellStyleDisabled.setBorderBottom(BorderStyle.THIN);
		    headerCellStyleDisabled.setBorderTop(BorderStyle.THIN);
		    headerCellStyleDisabled.setBorderLeft(BorderStyle.THIN);
		    headerCellStyleDisabled.setBorderRight(BorderStyle.THIN);
		    headerCellStyleDisabled.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerCellStyleDisabled.setFont(topHeaderFontDisabled);
		    headerCellStyleDisabled.setWrapText(true);
		    
		    headerCellStyleSmall = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyleSmall.setAlignment( HorizontalAlignment.CENTER );
			headerCellStyleSmall.setVerticalAlignment( VerticalAlignment.CENTER);
		    headerCellStyleSmall.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    headerCellStyleSmall.setBorderBottom(BorderStyle.THIN);
		    headerCellStyleSmall.setBorderTop(BorderStyle.THIN);
		    headerCellStyleSmall.setBorderLeft(BorderStyle.THIN);
		    headerCellStyleSmall.setBorderRight(BorderStyle.THIN);
		    headerCellStyleSmall.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerCellStyleSmall.setFont(topHeaderFontSmall);
		    headerCellStyleSmall.setWrapText(true);
		    
		    draftHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			draftHeaderCellStyle.setAlignment( HorizontalAlignment.CENTER );
			draftHeaderCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		    draftHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    draftHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
		    draftHeaderCellStyle.setBorderTop(BorderStyle.THIN);
		    draftHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
		    draftHeaderCellStyle.setBorderRight(BorderStyle.THIN);
		    draftHeaderCellStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    draftHeaderCellStyle.setFont(draftHeaderFont);
		    draftHeaderCellStyle.setWrapText(false);
		    
		    sheet.setRandomAccessWindowSize(params.isDraft() ? 3 : 2);  // La cabecera lleva 2 o 3 filas
		    sheet.setDefaultColumnWidth(10);
		    sheet.trackAllColumnsForAutoSizing();
		    
		    rowCount = 0;
		    
		    // CABECERA PARA INDICAR QUE ES BORRADOR Y NO ES VÁLIDO PARA PRESENTACIÓN OFICIAL
		    if (params.isDraft()) {
		    	row = sheet.createRow(rowCount++);
		    	String bookType = params.getBookType() == 0 ? "D E   I V A" : params.getBookType() == 1 ? "D E   I R P F" : "U N I F I C A D O   D E   I V A   E   I R P F";
		    	addHorizontalMergedRegion("* * *   L I B R O   R E G I S T R O   " + bookType + "   * * *   D O C U M E N T O   B O R R A D O R   * * *   N O   V Á L I D O   P A R A   P R E S E N T A C I Ó N   O F I C I A L   * * *   L I B R O   R E G I S T R O   " + bookType + "   * * *   D O C U M E N T O   B O R R A D O R   * * *   N O   V Á L I D O   P A R A   P R E S E N T A C I Ó N   O F I C I A L   * * *", workbook.getNumberOfSheets() == 1 ? 38 : 44, draftHeaderCellStyle);
		    }
		    
		    if (!params.isDraft())		    	
		    	row = sheet.createRow(rowCount++);
		    row2 = sheet.createRow(rowCount++);
			cellCount = 0;
						
			if (workbook.getNumberOfSheets() == 1) {
				// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
				if (params.isDraft()) {
					headerRowExpIngDraft(); 
				} else {
					headerRowExpIng(); 
				}
			} else {
				// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
				if (params.isDraft()) {
					headerRowRecGasDraft(); 
				} else {
					headerRowRecGas(); 
				}
			}
		    		    
		    sheet.setRandomAccessWindowSize(1);
		    
		}
		
		// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
		protected void headerRowExpIng() {
			addHorizontalMergedRegion("Autoliquidación", 2);
			addHeaderCell("Ejercicio");
			addHeaderCell("Periodo");
			addHorizontalMergedRegion("Actividad", 3);
			addHeaderCell("Código");
			addHeaderCell("Tipo");
			addHeaderCell("Grupo o Epígrafe del IAE");
		    addVerticalMergedRegion("Tipo de Factura");
    		addVerticalMergedRegion("Concepto de Ingreso", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		if (params.isDraft()) {
    			addVerticalMergedRegion("Descripción del Ingreso", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    			addVerticalMergedRegion("Cuenta Contable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		}
    		addVerticalMergedRegion("Ingreso Computable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Fecha Expedición");
		    addVerticalMergedRegion("Fecha Operación");
	    	addHorizontalMergedRegion("Identificación de la Factura", 3);
	    	addHeaderCell("Serie");
	    	addHeaderCell("Número");
	    	addHeaderCell("Número-Final");
	        addHorizontalMergedRegion("NIF Destinatario", 3);
		    addHeaderCell("Tipo");
		    addHeaderCell("Código País");
		    addHeaderCell("Identificación");
	    	addVerticalMergedRegion("Nombre Destinatario");
		    addVerticalMergedRegion("Clave de Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Calificación de la Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Operación Exenta", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Total Factura");
		    addVerticalMergedRegion("Base Imponible");
		    addVerticalMergedRegion("Tipo de IVA");
	    	addVerticalMergedRegion("Cuota IVA Repercutida");
		    addVerticalMergedRegion("Tipo de Recargo Eq.");		    
		    addVerticalMergedRegion("Cuota Recargo Eq.");		    
	    	addHorizontalMergedRegion("Cobro (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)", 4, headerCellStyleSmall);		    	
		    addHeaderCell("Fecha");
		    addHeaderCell("Importe");
		    addHeaderCell("Medio Utilizado");
		    addHeaderCell("Identificación Medio Utilizado");		    
	    	addVerticalMergedRegion("Tipo Retención del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addVerticalMergedRegion("Importe Retenido del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addVerticalMergedRegion("Registro Acuerdo Facturación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHorizontalMergedRegion("Inmueble", 2);
	    	addHeaderCell("Situación");
	    	addHeaderCell("Referencia Catastral");
	    	addVerticalMergedRegion("Referencia Externa");
		}
		
		// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
		protected void headerRowRecGas() {
			addHorizontalMergedRegion("Autoliquidación", 2);
			addHeaderCell("Ejercicio");
			addHeaderCell("Periodo");
			addHorizontalMergedRegion("Actividad", 3);
			addHeaderCell("Código");
			addHeaderCell("Tipo");
			addHeaderCell("Grupo o Epígrafe del IAE");
		    addVerticalMergedRegion("Tipo de Factura");
    		addVerticalMergedRegion("Concepto de Gasto", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		if (params.isDraft()) {
    			addVerticalMergedRegion("Descripción del Gasto", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    			addVerticalMergedRegion("Cuenta Contable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		}
    		addVerticalMergedRegion("Gasto Deducible", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Fecha Expedición");
		    addVerticalMergedRegion("Fecha Operación");
	    	addHorizontalMergedRegion("Identificación Factura del Expedidor", 2);
	    	addHeaderCell("(Serie-Número)");
	    	addHeaderCell("Número-Final");
	    	addVerticalMergedRegion("Fecha Recepción", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addVerticalMergedRegion("Número Recepción");
	    	addVerticalMergedRegion("Número Recepción Final");
	        addHorizontalMergedRegion("NIF Expedidor",3);
		    addHeaderCell("Tipo");
		    addHeaderCell("Código País");
		    addHeaderCell("Identificación");
	    	addVerticalMergedRegion("Nombre Expedidor");
		    addVerticalMergedRegion("Clave de Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Bien de Inversión", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Inversión del Sujeto Pasivo", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addVerticalMergedRegion("Deducible en Periodo Posterior", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHorizontalMergedRegion("Periodo Deducción", 2, params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle); 
	    	addHeaderCell("Ejercicio", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);                        
	    	addHeaderCell("Periodo", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);                          
		    addVerticalMergedRegion("Total Factura");
		    addVerticalMergedRegion("Base Imponible");
		    addVerticalMergedRegion("Tipo de IVA");
	    	addVerticalMergedRegion("Cuota IVA Soportado");
	    	addVerticalMergedRegion("Cuota Deducible");
		    addVerticalMergedRegion("Tipo de Recargo Eq.");		    
		    addVerticalMergedRegion("Cuota Recargo Eq.");		    
	    	addHorizontalMergedRegion("Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)", 4, headerCellStyleSmall);		    	
		    addHeaderCell("Fecha");
		    addHeaderCell("Importe");
		    addHeaderCell("Medio Utilizado");
		    addHeaderCell("Identificación Medio Utilizado");		    
	    	addVerticalMergedRegion("Tipo Retención del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addVerticalMergedRegion("Importe Retenido del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addVerticalMergedRegion("Registro Acuerdo Facturación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHorizontalMergedRegion("Inmueble", 2);
	    	addHeaderCell("Situación");
	    	addHeaderCell("Referencia Catastral");
	    	addVerticalMergedRegion("Referencia Externa");
		}
		
		// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos (Borrador)
		protected void headerRowExpIngDraft() {
			addHeaderCell("Autoliquidación Ejercicio");
			addHeaderCell("Autoliquidación Periodo");
			addHeaderCell("Actividad Código");
			addHeaderCell("Actividad Tipo");
			addHeaderCell("Actividad Grupo o Epígrafe del IAE");
		    addHeaderCell("Tipo de Factura");
    		addHeaderCell("Concepto de Ingreso", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
   			addHeaderCell("Descripción del Ingreso", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
   			addHeaderCell("Cuenta Contable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		addHeaderCell("Ingreso Computable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Fecha Expedición");
		    addHeaderCell("Fecha Operación");
	    	addHeaderCell("Identificación de la Factura Serie");
	    	addHeaderCell("Identificación de la Factura Número");
	    	addHeaderCell("Identificación de la Factura Número-Final");
		    addHeaderCell("NIF Destinatario Tipo");
		    addHeaderCell("NIF Destinatario Código País");
		    addHeaderCell("NIF Destinatario Identificación");
	    	addHeaderCell("Nombre Destinatario");
		    addHeaderCell("Clave de Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Calificación de la Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Operación Exenta", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Total Factura");
		    addHeaderCell("Base Imponible");
		    addHeaderCell("Tipo de IVA");
	    	addHeaderCell("Cuota IVA Repercutida");
		    addHeaderCell("Tipo de Recargo Eq.");		    
		    addHeaderCell("Cuota Recargo Eq.");		    
		    addHeaderCell("Cobro (RECC o IRPF) Fecha");
		    addHeaderCell("Cobro (RECC o IRPF) Importe");
		    addHeaderCell("Cobro (RECC o IRPF) Medio Utilizado");
		    addHeaderCell("Cobro (RECC o IRPF) Identificación Medio Utilizado");		    
	    	addHeaderCell("Tipo Retención del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Importe Retenido del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Registro Acuerdo Facturación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Inmueble Situación");
	    	addHeaderCell("Inmueble Referencia Catastral");
	    	addHeaderCell("Referencia Externa");
		}
		
		// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos (Borrador)
		protected void headerRowRecGasDraft() {
			addHeaderCell("Autoliquidación Ejercicio");
			addHeaderCell("Autoliquidación Periodo");
			addHeaderCell("Actividad Código");
			addHeaderCell("Actividad Tipo");
			addHeaderCell("Actividad Grupo o Epígrafe del IAE");
		    addHeaderCell("Tipo de Factura");
    		addHeaderCell("Concepto de Gasto", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
   			addHeaderCell("Descripción del Gasto", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
   			addHeaderCell("Cuenta Contable", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
    		addHeaderCell("Gasto Deducible", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Fecha Expedición");
		    addHeaderCell("Fecha Operación");
	    	addHeaderCell("Identificación Factura del Expedidor (Serie-Número)");
	    	addHeaderCell("Identificación Factura del Expedidor Número-Final");
	    	addHeaderCell("Fecha Recepción", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Número Recepción");
	    	addHeaderCell("Número Recepción Final");
		    addHeaderCell("NIF Expedidor Tipo");
		    addHeaderCell("NIF Expedidor Código País");
		    addHeaderCell("NIF Expedidor Identificación");
	    	addHeaderCell("Nombre Expedidor");
		    addHeaderCell("Clave de Operación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Bien de Inversión", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Inversión del Sujeto Pasivo", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
		    addHeaderCell("Deducible en Periodo Posterior", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Periodo Deducción Ejercicio", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);                        
	    	addHeaderCell("Periodo Deducción Periodo", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);                          
		    addHeaderCell("Total Factura");
		    addHeaderCell("Base Imponible");
		    addHeaderCell("Tipo de IVA");
	    	addHeaderCell("Cuota IVA Soportado");
	    	addHeaderCell("Cuota Deducible");
		    addHeaderCell("Tipo de Recargo Eq.");		    
		    addHeaderCell("Cuota Recargo Eq.");		    
		    addHeaderCell("Pago (RECC o IRPF) Fecha");
		    addHeaderCell("Pago (RECC o IRPF) Importe");
		    addHeaderCell("Pago (RECC o IRPF) Medio Utilizado");
		    addHeaderCell("Pago (RECC o IRPF) Identificación Medio Utilizado");		    
	    	addHeaderCell("Tipo Retención del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Importe Retenido del IRPF", params.getBookType() == 0 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Registro Acuerdo Facturación", params.getBookType() == 1 ? headerCellStyleDisabled : headerCellStyle);
	    	addHeaderCell("Inmueble Situación");
	    	addHeaderCell("Inmueble Referencia Catastral");
	    	addHeaderCell("Referencia Externa");
		}
		
		public void accept(OperationBreakdown op) {
			
			// Añadir linea de detalle al archivo Excel
			addDetailRow(op);
			
		}
		
		private void addDetailRow(OperationBreakdown op) {
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			if (workbook.getNumberOfSheets() == 1)
				addDetailRowExpIng(op);
			else 
				addDetailRowRecGas(op);
			
		}
		
		// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
		private void addDetailRowExpIng(OperationBreakdown op) {
			
			addCell(Integer.toString(AonDateUtils.getYear(op.getTaxDate()))).setCellStyle(centerCellStyle);     // Autoliquidación - Ejercicio
			addCell(Period.getQuarterlyPeriod(AonDateUtils.getMonth(op.getTaxDate())).getName()).setCellStyle(centerCellStyle);  // Autoliquidación - Periodo
			addCell(op.getActivityCode()).setCellStyle(centerCellStyle);  // Actividad - Código
			addCell(op.getActivityType()).setCellStyle(centerCellStyle);  // Actividad - Tipo
			addCell(AonStringUtils.trimToEmpty(op.getActivityIAE()).replace(".","")).setCellStyle(centerCellStyle); // Actividad - Grupo o Epígrafe IAE
			addCell(op.getInvoiceType()).setCellStyle(centerCellStyle);   // Tipo de Factura
			
			addCell(params.getBookType() == 0 ? "" : op.getConceptCode()).setCellStyle(centerCellStyle); // Concepto de Ingreso (excepto Libro de IVA)

			if (params.isDraft()) {
    			addCell(params.getBookType() == 0 ? "" : op.getConceptDescription());                          // Descripción del Ingreso (excepto Libro de IVA)
    			if (params.getBookType() != 0)
    				sheet.autoSizeColumn(cellCount-1);
    			addCell(params.getBookType() == 0 ? "" : op.getAccountCode()).setCellStyle(centerCellStyle);   // Cuenta Contable (excepto Libro de IVA)
    		}

			if (params.getBookType() == 0) {
				// Libro de IVA
				addCell(""); // Ingreso computable
			} else {
				// Resto
				if (AonStringUtils.isBlank(op.getConceptCode()))
					addCell(""); // Ingreso computable (si el código de concepto está vacio, no se pone importe)
				else
					addCell(op.getConceptAmount()); // Ingreso computable
			}
						
			addCell(op.getEntryDate());      // Fecha Expedición
			addCell("");                     // Fecha Operación (no se usa)
			addCell(op.getInvoiceSeries());  // Identificación de la Factura - Serie
		    addCell(op.getInvoiceNumber());  // Identificación de la Factura - Número
		    addCell("");                     // Identificación de la Factura - Número-Final (no se usa)
			
			if (AonStringUtils.isBlank(op.getDocumentCountry()) || op.getDocumentCountry().equals("ES")) {
				addCell("");
				addCell("");
			}
			else {
				addCell(op.getDocumentType()).setCellStyle(centerCellStyle);    // NIF - Tipo
				addCell(op.getDocumentCountry()).setCellStyle(centerCellStyle); // NIF - Código País
				// Si Tipo NIF = 02 NIF-IVA, comprobar que el documento lleva en sus dos primeras letras el pais
				if (op.getDocumentType().equals("02")) {
					String doc = op.getDocument();
					if(!AonStringUtils.equalsIgnoreCase(AonStringUtils.substring(doc, 0, 2), op.getDocumentCountry())) {
						doc = op.getDocumentCountry() + doc;
						op.setDocument(doc);
					}					
				}
			}
			
			addCell(op.getDocument()); // NIF - Identificación
			addCell(op.getName());     // Nombre Destinatario
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getBookType() == 1 ? "" : op.getOperationKey()).setCellStyle(centerCellStyle);           // Clave de Operación (excepto Libro de IRPF)
			addCell(params.getBookType() == 1 ? "" : op.getOperationQualification()).setCellStyle(centerCellStyle); // Calificador de la Operación (excepto Libro de IRPF)
			addCell(params.getBookType() == 1 ? "" : op.getExemptOperation()).setCellStyle(centerCellStyle);        // Operación Exenta (excepto Libro de IRPF)
			
			if (op.getPayDate() != null) {
				// Cobro RECC no lleva estos datos de la factura
				addCell("");   // Total Factura
				addCell("");   // Base Imponible
				addCell("");   // Tipo de IVA
				addCell("");   // Cuota IVA Repercutida
			} else {
				addCell(op.getTotal());   // Total Factura
				addCell(op.getBase());    // Base Imponible
				addCell(op.getPercent()); // Tipo de IVA
				addCell(op.getQuota());   // Cuota IVA Repercutida
			}
			
			addDoubleEmptyCell(op.getSurchargePercent()); // Tipo de Recargo Eq.
			addDoubleEmptyCell(op.getSurchargeQuota());   // Cuota Recargo Eq.
			
			addCell(op.getPayDate());                                 // Cobro RECC - Fecha
			addDoubleEmptyCell(op.getPayAmount());                    // Cobro RECC - Importe
			addCell(op.getPayMethod()).setCellStyle(centerCellStyle); // Cobro RECC - Medio Utilizado
			addCell(op.getPayMethodName());                           // Cobro RECC - Identificación Medio Utilizado
			sheet.autoSizeColumn(cellCount-1);
			
			addDoubleEmptyCell(params.getBookType() == 0 ? 0.0 : op.getRetentionPercent()); // Tipo Retención IRPF (excepto Libro de IVA)
			addDoubleEmptyCell(params.getBookType() == 0 ? 0.0 : op.getRetentionQuota());   // Importe Retenido IRPF (excepto Libro de IVA)
			
			addCell("");                                                     // Registro Acuerdo Facturacion (no se usa)
			addCell(op.getBuildingLocation()).setCellStyle(centerCellStyle); // Inmueble - Situación
			addCell(op.getCadasdralReference());                             // Inmueble - Referencia Catastral
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getEntryJournal());  								 // Referencia Externa (Número de diario del asiento)
			
		}
		
		// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
		private void addDetailRowRecGas(OperationBreakdown op) {
			
			addCell(Integer.toString(AonDateUtils.getYear(op.getTaxDate()))).setCellStyle(centerCellStyle);                      // Autoliquidación - Ejercicio
			addCell(Period.getQuarterlyPeriod(AonDateUtils.getMonth(op.getTaxDate())).getName()).setCellStyle(centerCellStyle);  // Autoliquidación - Periodo
			addCell(op.getActivityCode()).setCellStyle(centerCellStyle);                                            // Actividad - Código
			addCell(op.getActivityType()).setCellStyle(centerCellStyle);                                            // Actividad - Tipo
			addCell(AonStringUtils.trimToEmpty(op.getActivityIAE()).replace(".","")).setCellStyle(centerCellStyle); // Actividad - Grupo o Epígrafe IAE
			addCell(op.getInvoiceType()).setCellStyle(centerCellStyle);                                             // Tipo de Factura
			
			addCell(params.getBookType() == 0 ? "" : op.getConceptCode()).setCellStyle(centerCellStyle); // Concepto de Gasto (excepto Libro de IVA)
			
    		if (params.isDraft()) {
    			addCell(params.getBookType() == 0 ? "" : op.getConceptDescription());                         // Descripción del Gasto (excepto Libro de IVA)
    			if (params.getBookType() != 0)
    				sheet.autoSizeColumn(cellCount-1);
    			addCell(params.getBookType() == 0 ? "" : op.getAccountCode()).setCellStyle(centerCellStyle);  // Cuenta Contable (excepto Libro de IVA)
    		}

			if (params.getBookType() == 0) {
				// Libro de IVA
				addCell(""); // Gasto Deducible
			} else {
				// Resto
				if (AonStringUtils.isBlank(op.getConceptCode()))
					addCell(""); // Gasto Deducible (si el código de concepto está vacio, no se pone importe)
				else
					addCell(op.getConceptAmount()); // Gasto Deducible
			}
						
			addCell(op.getEntryDate());      // Fecha Expedición
			addCell("");                     // Fecha Operación (no se usa)
		    addCell(op.getInvoiceNumber());  // Identificación de la Factura del Expedidor - Serie-Número
		    sheet.autoSizeColumn(cellCount-1, true);
		    addCell("");                     // Identificación de la Factura - Número-Final (no se usa)
		    sheet.setColumnWidth(cellCount-1, 15 * 256);
		    
		    if (params.getBookType() == 1) {
		    	// Libro de IRPF
				addCell(""); // Fecha Recepción
		    } else {
		    	// Resto
		    	addCell(op.getReceptionDate());  // Fecha Recepción
		    }
		    
		    addCell(op.getReceptionNumber()); // Número Recepción
		    sheet.autoSizeColumn(cellCount-1);
		    addCell("");                      // Número Recepción Final (no se usa)
			
			if (AonStringUtils.isBlank(op.getDocumentCountry()) || op.getDocumentCountry().equals("ES")) {
				addCell("");
				addCell("");
			}
			else {
				addCell(op.getDocumentType()).setCellStyle(centerCellStyle);    // NIF - Tipo
				addCell(op.getDocumentCountry()).setCellStyle(centerCellStyle); // NIF - Código País
				// Si Tipo NIF = 02 NIF-IVA, comprobar que el documento lleva en sus dos primeras letras el pais
				if (op.getDocumentType().equals("02")) {
					String doc = op.getDocument();
					if(!AonStringUtils.equalsIgnoreCase(AonStringUtils.substring(doc, 0, 2), op.getDocumentCountry())) {
						doc = op.getDocumentCountry() + doc;
						op.setDocument(doc);
					}					
				}
			}
			
			addCell(op.getDocument()); // NIF - Identificación
			addCell(op.getName());     // Nombre Expedidor
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getBookType() == 1 ? "" : op.getOperationKey()).setCellStyle(centerCellStyle);           // Clave de Operación (excepto Libro de IRPF)
			addCell(params.getBookType() == 1 ? "" : op.isInvestment() ? "S" : "N").setCellStyle(centerCellStyle);  // Bien de Inversión	
			addCell(params.getBookType() == 1 ? "" : op.isIsp() ? "S" : "N").setCellStyle(centerCellStyle);         // Inversión del Sujeto Pasivo	
			addCell(""); // Deducible en Periodo Posterior (no se usa)	
			addCell(""); // Periodo Deducción - Ejercicio (no se usa)
			addCell(""); // Periodo Deducción - Periodo (no se usa)

			if (op.getPayDate() != null) {
				// Pago RECC no lleva estos datos de la factura
				addCell("");   // Total Factura
				addCell("");   // Base Imponible
				addCell("");   // Tipo de IVA
				addCell("");   // Cuota IVA Soportado
				addCell("");   // Cuota Deducible
			} else {
				addCell(op.getTotal());   			// Total Factura
				addCell(op.getBase());             	// Base Imponible
				addCell(op.getPercent());          	// Tipo de IVA
				addCell(op.getQuota());            	// Cuota IVA Soportado
				addCell(op.getDeductibleQuota());  	// Cuota Deducible
			}
			
			addDoubleEmptyCell(op.getSurchargePercent()); // Tipo de Recargo Eq.
			addDoubleEmptyCell(op.getSurchargeQuota());   // Cuota Recargo Eq.
			
			addCell(op.getPayDate());             						// Pago RECC - Fecha
			addDoubleEmptyCell(op.getPayAmount());     					// Pago RECC - Importe
			addCell(op.getPayMethod()).setCellStyle(centerCellStyle); 	// Pago RECC - Medio Utilizado
			addCell(op.getPayMethodName());       						// Pago RECC - Identificación Medio Utilizado
			sheet.autoSizeColumn(cellCount-1);
			
			addDoubleEmptyCell(params.getBookType() == 0 ? 0.0 : op.getRetentionPercent()); // Tipo Retención IRPF (excepto Libro de IVA)
			addDoubleEmptyCell(params.getBookType() == 0 ? 0.0 : op.getRetentionQuota());   // Importe Retenido IRPF (excepto Libro de IVA)
			
			addCell(""); 														// Registro Acuerdo Facturacion (no se usa)
			addCell(op.getBuildingLocation()).setCellStyle(centerCellStyle); 	// Inmueble - Situación
			addCell(op.getCadasdralReference());                             	// Inmueble - Referencia Catastral
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getEntryJournal());  									// Referencia Externa (Número de diario del asiento)
			
		}	

		// Solo se pone el importe si es distinto de cero
		private Cell addDoubleEmptyCell(Double number) {			
			if (number == 0.0)
				return addCell("");								
			else return addCell(number);  							
		}
					
		private void addHorizontalMergedRegion(String value, int cellsNumber) {
			addHorizontalMergedRegion(value, cellsNumber, headerCellStyle);
		}
		private void addHorizontalMergedRegion(String value, int cellsNumber, XSSFCellStyle cellStyle) {
			CellUtil.createCell(row, cellCount, value, cellStyle);
			sheet.addMergedRegion(setBordersToMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount, cellCount+cellsNumber-1)));
		}
		
		private void addVerticalMergedRegion(String value) {
			addVerticalMergedRegion(value, headerCellStyle);
		}
		private void addVerticalMergedRegion(String value, CellStyle cellStyle) {
		    CellUtil.createCell(row, cellCount, value, cellStyle);
		    sheet.addMergedRegion(setBordersToMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum()+1, cellCount, cellCount)));
			cellCount++;
		}
		
		private void addHeaderCell(String value) {
			addHeaderCell(value, headerCellStyle);
		}
		private void addHeaderCell(String value, XSSFCellStyle cellStyle) {
			CellUtil.createCell(row2, cellCount, value, cellStyle);
			cellCount++;			
		}
		
		private CellRangeAddress setBordersToMergedRegion(CellRangeAddress rangeAddress) {
			RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
			return rangeAddress;
		}	
		
	}
	
}
