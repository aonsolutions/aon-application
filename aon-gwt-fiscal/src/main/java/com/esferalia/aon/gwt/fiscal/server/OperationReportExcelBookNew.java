package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdownNew;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Operation Report Excel Book New", urlPatterns = { "/aon_gwt_fiscal/roms/OperationReportExcelBookNew" })
public class OperationReportExcelBookNew extends HttpServlet {
	
//	private static final long serialVersionUID = -8237842836135745934L;
	
	// Esta variable se utiliza para poder sacar los cobros/pagos en Facturas RECC, 
	// deben salir primero las lineas de la factura y despues los cobros/pagos 
//	private OperationBreakdown opAccrual = null;
		
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

			OperationParamsNew params = JsonParser.parseOperationParamsNew(operationParams);			
//			params.setAeatBook(true);
			
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
			
			// NOMBRE DEL FICHERO			
//			El nombre del fichero será formado por la concatenación de los siguientes campos y en el siguiente orden:
//				1) Ejercicio
//				2) NIF
//				3) Tipo de Libros Registro que contiene el fichero, mediante uno de los siguientes valores:
//				- C: Todos los Libros Registro del IVA requerido en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una C, correspondiente a la presentación de todos los libros del IVA, incluyendo las facturas expedidas en una pestaña denominada EXPEDIDAS, las facturas recibidas en otra pestaña denominada RECIBIDAS y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
//				- D: Todos los Libros Registro del IRPF requerido en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una D,	correspondiente a la presentación de todos los libros del IRPF, incluyendo las ventas e ingresos en una pestaña denominada INGRESOS, las compras y gastos en otra pestaña denominada GASTOS y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
//				- T: Todos los Libros Registro Unificados del IRPF e IVA requeridos en un solo fichero Excel (XLSX), en cuyo caso en la posición del Tipo del Libro debe consignar una T, correspondiente a la presentación conjunta de todos los libros de ambos impuestos, incluyendo las "facturas expedidas" y "ventas e ingresos" en una pestaña denominada EXPEDIDAS_INGRESOS, las "facturas recibidas" y "compras y gastos" en otra pestaña denominada RECIBIDAS_GASTOS, y, en su caso, los bienes de inversión en otra pestaña denominada BIENES-INVERSIÓN.
//				4) Nombre o Razón social				
	
			String sheetName1 = "";
			String sheetName2 = "";
			String filename = AonDateUtils.getYear(params.getFromDate()) + companyDocument;
			
			if (params.getType() == 0) {
				filename = filename + "C";   // Libros Registro del IVA
			    sheetName1 = "EXPEDIDAS";
				sheetName2 = "RECIBIDAS";
			}
			else if (params.getType() == 1) {
				filename = filename + "D";   // Libros Registro del IRPF
			    sheetName1 = "INGRESOS";
				sheetName2 = "GASTOS";
			}
			else {
				filename = filename + "T";   // Libros Registro Unificados del IRPF e IVA
			    sheetName1 = "EXPEDIDAS_INGRESOS";
				sheetName2 = "RECIBIDAS_GASTOS";
			}
			
			filename = filename + companyName;
			
//			opAccrual = null;
			
			// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos			
			ExcelAction action = new ExcelAction(occam, params);
			action.initialize(sheetName1);
			ACCOUNTING.getOperationBreakdownNew(occam, params).forEach(action);
			
			// Comprobar si quedan por poner cobros/pagos de la última factura
//			if (opAccrual != null) {
//				getInvoicePayments(occam, opAccrual, params).forEach(action);
//			}
			
			// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
			action.createSheet(sheetName2);
			ACCOUNTING.getOperationBreakdownNew(occam, params).forEach(action);
			
            // Formato del fichero Excel 2007
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+filename+"."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}
		
	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<OperationBreakdownNew>{
		
		private Row row2;
		private Occam occam;
		private OperationParamsNew params;
		
		public ExcelAction(Occam occam, OperationParamsNew params) {
			this.occam = occam;
			this.params = params;
		}
		
		@Override
		public void createSheet(String name) {
			
			//sheet = (SXSSFSheet) workbook.createSheet(name);
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
			
		    headerCellStyle.setBorderBottom(BorderStyle.THIN);
		    headerCellStyle.setBorderTop(BorderStyle.THIN);
		    headerCellStyle.setBorderLeft(BorderStyle.THIN);
		    headerCellStyle.setBorderRight(BorderStyle.THIN);
		    headerCellStyle.setFillForegroundColor(AON_LIGHT_GRAY);
		    headerCellStyle.setFont(topHeaderFont);
		    headerCellStyle.setWrapText(true);
		    
		    sheet.setRandomAccessWindowSize(2);  // La cabecera lleva 2 filas
		    sheet.setDefaultColumnWidth(11);
		    sheet.trackAllColumnsForAutoSizing();
		    
		    rowCount = 0;
		    row = sheet.createRow(rowCount++);
		    row2 = sheet.createRow(rowCount++);
		    
			cellCount = 0;
						
			if (workbook.getNumberOfSheets() == 1)
				headerRowExpIng(); // Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
			else
				headerRowRecGas(); // Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
		    		    
		    sheet.setRandomAccessWindowSize(1);
		    
		}
		
		// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
		protected void headerRowExpIng() {
			addHorizontalMergedRegion("Autoliquidación", 2);
			addAutoSizeCell("Ejercicio");
			addAutoSizeCell("Periodo");
			addHorizontalMergedRegion("Actividad", 3);
			addAutoSizeCell("Código");
			addAutoSizeCell("Tipo");
			addAutoSizeCell("Epígrafe IAE");
		    addVerticalMergedRegion("Tipo de Factura");
    		addVerticalMergedRegion("Concepto de Ingreso");
    		addVerticalMergedRegion("Ingreso Computable");
		    addVerticalMergedRegion("Fecha Expedición");
		    addVerticalMergedRegion("Fecha Operación");
	    	addHorizontalMergedRegion("Identificación de la Factura", 3);
	    	addAutoSizeCell("Serie");
	    	addAutoSizeCell("Número");
	    	addAutoSizeCell("Número-Final");
	        addHorizontalMergedRegion("NIF Destinatario",3);
		    addAutoSizeCell("Tipo");
		    addAutoSizeCell("Código País");
		    addAutoSizeCell("Identificación");
	    	addVerticalMergedRegion("Nombre Destinatario");
		    addVerticalMergedRegion("Clave de Operación");
		    addVerticalMergedRegion("Calificación de la Operación");
		    addVerticalMergedRegion("Operación Exenta");
		    addVerticalMergedRegion("Total Factura");
		    addVerticalMergedRegion("Base Imponible");
		    addVerticalMergedRegion("Tipo de IVA");
	    	addVerticalMergedRegion("Cuota IVA Repercutida");
		    addVerticalMergedRegion("Tipo de Recargo Eq.");		    
		    addVerticalMergedRegion("Cuota Recargo Equivalencia");		    
	    	addHorizontalMergedRegion("Cobro (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)", 4);		    	
		    addAutoSizeCell("Fecha");
		    addAutoSizeCell("Importe");
		    addAutoSizeCell("Medio Utilizado");
		    addAutoSizeCell("Identificación Medio Utilizado");		    
	    	addVerticalMergedRegion("Tipo Retención IRPF");
	    	addVerticalMergedRegion("Importe Retenido IRPF");
	    	addVerticalMergedRegion("Registro Acuerdo Facturación");
	    	addHorizontalMergedRegion("Inmueble", 2);
	    	addAutoSizeCell("Situación");
	    	addAutoSizeCell("Referencia Catastral");
	    	addVerticalMergedRegion("Referencia Externa");
		}
		
		// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
		protected void headerRowRecGas() {
			addHorizontalMergedRegion("Autoliquidación", 2);
			addAutoSizeCell("Ejercicio");
			addAutoSizeCell("Periodo");
			addHorizontalMergedRegion("Actividad", 3);
			addAutoSizeCell("Código");
			addAutoSizeCell("Tipo");
			addAutoSizeCell("Epígrafe IAE");
		    addVerticalMergedRegion("Tipo de Factura");
    		addVerticalMergedRegion("Concepto de Gasto");
    		addVerticalMergedRegion("Gasto Deducible");
		    addVerticalMergedRegion("Fecha Expedición");
		    addVerticalMergedRegion("Fecha Operación");
	    	addHorizontalMergedRegion("Identificación Factura del Expedidor", 2);
	    	addAutoSizeCell("Serie-Número");
	    	addAutoSizeCell("Número-Final");
	    	addVerticalMergedRegion("Fecha Recepción");
	    	addVerticalMergedRegion("Número Recepción");
	    	addVerticalMergedRegion("Número Recepción Final");
	        addHorizontalMergedRegion("NIF Expedidor",3);
		    addAutoSizeCell("Tipo");
		    addAutoSizeCell("Código País");
		    addAutoSizeCell("Identificación");
	    	addVerticalMergedRegion("Nombre Expedidor");
		    addVerticalMergedRegion("Clave de Operación");
		    addVerticalMergedRegion("Bien de Inversión");
		    addVerticalMergedRegion("Inversión del Sujeto Pasivo");
		    addVerticalMergedRegion("Deducible en Periodo Posterior");
	    	addHorizontalMergedRegion("Periodo Deducción", 2);
	    	addAutoSizeCell("Ejercicio");
	    	addAutoSizeCell("Periodo");
		    addVerticalMergedRegion("Total Factura");
		    addVerticalMergedRegion("Base Imponible");
		    addVerticalMergedRegion("Tipo de IVA");
	    	addVerticalMergedRegion("Cuota IVA Soportado");
	    	addVerticalMergedRegion("Cuota Deducible");
		    addVerticalMergedRegion("Tipo de Recargo Eq.");		    
		    addVerticalMergedRegion("Cuota Recargo Equivalencia");		    
	    	addHorizontalMergedRegion("Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)", 4);		    	
		    addAutoSizeCell("Fecha");
		    addAutoSizeCell("Importe");
		    addAutoSizeCell("Medio Utilizado");
		    addAutoSizeCell("Identificación Medio Utilizado");		    
	    	addVerticalMergedRegion("Tipo Retención IRPF");
	    	addVerticalMergedRegion("Importe Retenido IRPF");
	    	addVerticalMergedRegion("Registro Acuerdo Facturación");
	    	addHorizontalMergedRegion("Inmueble", 2);
	    	addAutoSizeCell("Situación");
	    	addAutoSizeCell("Referencia Catastral");
	    	addVerticalMergedRegion("Referencia Externa");
		}
		
		public void accept(OperationBreakdownNew op) {
			
			// Comprobar si hay que poner los cobros/pagos de la factura anterior
//			if (opAccrual == null) {
//				opAccrual = op;
//			}
			
//			if (op.getInvoice() != null && !op.getInvoice().equals(opAccrual.getInvoice())) {
//				getInvoicePayments(occam, opAccrual, params).forEach(this);				
//				opAccrual = op;				
//			}
			
			// Añadir linea de detalle al archivo Excel
			addDetailRow(op);			
		}
		
		private void addDetailRow(OperationBreakdownNew op) {
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			if (workbook.getNumberOfSheets() == 1)
				addDetailRowExpIng(op);
			else 
				addDetailRowRecGas(op);
			
		}
		
		// Facturas Expedidas / Ventas e Ingresos / Expedidas e Ingresos
		private void addDetailRowExpIng(OperationBreakdownNew op) {
			
			addCell(Integer.toString(AonDateUtils.getYear(op.getTaxDate()))).setCellStyle(centerCellStyle);     // Autoliquidación - Ejercicio
			addCell(Period.getQuarterlyPeriod(AonDateUtils.getMonth(op.getTaxDate())).getName()).setCellStyle(centerCellStyle);  // Autoliquidación - Periodo
			addCell(op.getActivityCode()).setCellStyle(centerCellStyle);  // Actividad - Código
			addCell(op.getActivityType()).setCellStyle(centerCellStyle);  // Actividad - Tipo
			addCell(AonStringUtils.trimToEmpty(op.getActivityIAE()).replace(".","")).setCellStyle(centerCellStyle); // Actividad - Grupo o Epígrafe IAE
			addCell(op.getInvoiceType()).setCellStyle(centerCellStyle);   // Tipo de Factura
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getType() == 0 ? "" : op.getConceptCode()).setCellStyle(centerCellStyle); // Concepto de Ingreso (excepto Libro de IVA)
			if (params.getType() == 0) {
				// Libro de IVA
				addCell(""); // Ingreso computable
			} else {
				// Resto
				addCell(op.getConceptAmount()); // Ingreso computable
			}
						
			addCell(op.getEntryDate());  // Fecha Expedición
			addCell("");                 // Fecha Operación (no se usa)
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
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getName());     // Nombre Destinatario
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getType() == 1 ? "" : op.getOperationKey()).setCellStyle(centerCellStyle);           // Clave de Operación (excepto Libro de IRPF)
			addCell(params.getType() == 1 ? "" : op.getOperationQualification()).setCellStyle(centerCellStyle); // Calificador de la Operación (excepto Libro de IRPF)
			addCell(params.getType() == 1 ? "" : op.getExemptOperation()).setCellStyle(centerCellStyle);        // Operación Exenta (excepto Libro de IRPF)
			
			addCell(op.getTotal());   // Total Factura
			addCell(op.getBase());    // Base Imponible
			addCell(op.getPercent()); // Tipo de IVA
			addCell(op.getQuota());   // Cuota IVA Repercutida				
			
			addDoubleCell(op.getSurchargePercent()); // Tipo de Recargo Eq.
			addDoubleCell(op.getSurchargeQuota());   // Cuota Recargo Eq.
			
			addCell(op.getPayDate());             // Cobro RECC - Fecha
			sheet.autoSizeColumn(cellCount-1);
			addDoubleCell(op.getPayAmount());     // Cobro RECC - Importe
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getPayMethod()).setCellStyle(centerCellStyle); // Cobro RECC - Medio Utilizado
			addCell(op.getPayMethodName());       // Cobro RECC - Identificación Medio Utilizado
			sheet.autoSizeColumn(cellCount-1);
			
			addDoubleCell(params.getType() == 0 ? 0.0 : op.getRetentionPercent()); // Tipo Retención IRPF (excepto Libro de IVA)
			addDoubleCell(params.getType() == 0 ? 0.0 : op.getRetentionQuota());   // Importe Retenido IRPF (excepto Libro de IVA)
			
			addCell(""); // Registro Acuerdo Facturacion (no se usa)
			addCell(op.getBuildingLocation()).setCellStyle(centerCellStyle); // Inmueble - Situación
			addCell(op.getCadasdralReference());                             // Inmueble - Referencia Catastral
			addCell(""); // Referencia Externa (no se usa)	
			
		}
		
		// Facturas Recibidas / Compras y Gastos / Recibidas y Gastos
		private void addDetailRowRecGas(OperationBreakdownNew op) {
			
			addCell(Integer.toString(AonDateUtils.getYear(op.getTaxDate()))).setCellStyle(centerCellStyle);     // Autoliquidación - Ejercicio
			addCell(Period.getQuarterlyPeriod(AonDateUtils.getMonth(op.getTaxDate())).getName()).setCellStyle(centerCellStyle);  // Autoliquidación - Periodo
			addCell(op.getActivityCode()).setCellStyle(centerCellStyle);  // Actividad - Código
			addCell(op.getActivityType()).setCellStyle(centerCellStyle);  // Actividad - Tipo
			addCell(AonStringUtils.trimToEmpty(op.getActivityIAE()).replace(".","")).setCellStyle(centerCellStyle); // Actividad - Grupo o Epígrafe IAE
			addCell(op.getInvoiceType()).setCellStyle(centerCellStyle);   // Tipo de Factura
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getType() == 0 ? "" : op.getConceptCode()).setCellStyle(centerCellStyle); // Concepto de Gasto (excepto Libro de IVA)
			if (params.getType() == 0) {
				// Libro de IVA
				addCell(""); // Gasto Deducible
			} else {
				// Resto
				addCell(op.getConceptAmount()); // Gasto Deducible
			}
						
			addCell(op.getEntryDate());  // Fecha Expedición
			addCell("");                 // Fecha Operación (no se usa)
		    addCell(op.getInvoiceNumber());  // Identificación de la Factura del Expedidor - Serie-Número
		    addCell("");                     // Identificación de la Factura - Número-Final (no se usa)
		    addCell(op.getReceptionDate());  // Fecha Recepción
		    addCell(op.getReceptionNumber()); // Número Recepción
		    addCell("");                     // Número Recepción Final (no se usa)
			
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
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getName());     // Nombre Expedidor
			sheet.autoSizeColumn(cellCount-1);
			
			addCell(params.getType() == 1 ? "" : op.getOperationKey()).setCellStyle(centerCellStyle);           // Clave de Operación (excepto Libro de IRPF)
			addCell(params.getType() == 1 ? "" : op.isInvestment() ? "S" : "N").setCellStyle(centerCellStyle);  // Bien de Inversión	
			addCell(params.getType() == 1 ? "" : op.isIsp() ? "S" : "N").setCellStyle(centerCellStyle);         // Inversión del Sujeto Pasivo	
			addCell(""); // Deducible en Periodo Posterior (no se usa)	
			addCell(""); // Periodo Deducción - Ejercicio (no se usa)
			addCell(""); // Periodo Deducción - Periodo (no se usa)
			
			addCell(op.getTotal());   // Total Factura
			addCell(op.getBase());    // Base Imponible
			addCell(op.getPercent()); // Tipo de IVA
			addCell(op.getQuota());   // Cuota IVA Soportado
			addCell(op.getDeductibleQuota());   // Cuota Deducible
			
			addDoubleCell(op.getSurchargePercent()); // Tipo de Recargo Eq.
			addDoubleCell(op.getSurchargeQuota());   // Cuota Recargo Eq.
			
			addCell(op.getPayDate());             // Pago RECC - Fecha
			sheet.autoSizeColumn(cellCount-1);
			addDoubleCell(op.getPayAmount());     // Pago RECC - Importe
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getPayMethod()).setCellStyle(centerCellStyle); // Pago RECC - Medio Utilizado
			addCell(op.getPayMethodName());       // Pago RECC - Identificación Medio Utilizado
			sheet.autoSizeColumn(cellCount-1);
			
			addDoubleCell(params.getType() == 0 ? 0.0 : op.getRetentionPercent()); // Tipo Retención IRPF (excepto Libro de IVA)
			addDoubleCell(params.getType() == 0 ? 0.0 : op.getRetentionQuota());   // Importe Retenido IRPF (excepto Libro de IVA)
			
			addCell(""); // Registro Acuerdo Facturacion (no se usa)
			addCell(op.getBuildingLocation()).setCellStyle(centerCellStyle); // Inmueble - Situación
			addCell(op.getCadasdralReference());                             // Inmueble - Referencia Catastral
			addCell(""); // Referencia Externa (no se usa)	
			
		}	

		// Solo se pone el importe si es distinto de cero
		private Cell addDoubleCell(Double number) {			
			if (number == 0.0)
				return addCell("");								
			else return addCell(number);  							
		}
					
		private void addHorizontalMergedRegion(String value, int cellsNumber) {
			CellUtil.createCell(row, cellCount, value, headerCellStyle);			
			sheet.addMergedRegion(setBordersToMergedRegion(new CellRangeAddress(0, 0, cellCount, cellCount+cellsNumber-1)));
		}
		
		private void addVerticalMergedRegion(String value) {
		    CellUtil.createCell(row, cellCount, value, headerCellStyle);
			sheet.addMergedRegion(setBordersToMergedRegion(new CellRangeAddress(0, 1, cellCount, cellCount)));
			cellCount++;
		}
		
		private void addAutoSizeCell(String value) {
			CellUtil.createCell(row2, cellCount, value, headerCellStyle); 
			sheet.autoSizeColumn(cellCount);
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

	// Obtiene los cobros/pagos de una factura en Regimen Especial de Criterio de Caja
//	private Stream<OperationBreakdown> getInvoicePayments(Occam occam, final OperationBreakdown op, final OperationParams params) {
//		
//		// Solo para Libro de IVA (o unificado) y para Facturas RECC
//		if ((params.isIrpf() && !params.getUnifiedBook()) || !("07".equals(op.getOperationType()))) {
//			return Stream.empty();	
//		}
//
//		// Condicion para que aparezcan los vecimientos
//		// Está cobrado/pagado y la fecha de cobro/pago está entre los filtros
//		Condition condition = (FINANCE_TRACKING.TYPE.equal(FinanceTrackingType.PAID.value()).and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(params.getFromDate()),AonDateUtils.toSql(params.getToDate()))));
//		
//		try ( CloseableAONContext ctx = AONContext.getAONContext(occam)) { 
//			return ctx.getDslContext().select(
//	      			 FINANCE_TRACKING.TRACKING_DATE
//					,FINANCE_TRACKING.AMOUNT
//					,FINANCE_TRACKING.TYPE
//					,PAY_METHOD.TYPE
//					,PAY_METHOD.NAME
//					,RBANK.BANK_ACCOUNT
//					)
//					.from(FINANCE)
//					.join(FINANCE_TRACKING).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))				
//					.leftJoin(PAY_METHOD).on(PAY_METHOD.ID.equal(FINANCE.PAY_METHOD))
//					.leftJoin(RBANK).on(RBANK.ID.equal(FINANCE_TRACKING.RBANK))				
//					.where(FINANCE.INVOICE.equal(op.getInvoice()))
//					.and(condition)
//					.orderBy(FINANCE_TRACKING.TRACKING_DATE)
//					.fetch()
//					.stream()
//					.map( rec -> {
//						
//						// Metodo de Cobro/Pago
//						String payMethod = ""; 
//						String payMethodName = ""; 
//						Byte pm = rec.getValue(PAY_METHOD.TYPE);
//						if (pm != null) {
//							switch (pm) {
//								case 1:  // Negociable 
//									payMethod = "05"; // Domiciliacion
//									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
//									break;
//								case 4:  // Cheque 
//									payMethod = "02"; // Cheque
//									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
//									break;
//								case 5:  // Transferencia 
//									payMethod = "01";  // Transferencia
//									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
//									break;
//								default: // Resto
//									payMethod = "04"; // Otros medios de pago		
//									payMethodName = rec.getValue(PAY_METHOD.NAME);
//									break;
//							}
//						}
//						
//						return new OperationBreakdown()
//								.setTaxDate(rec.getValue(FINANCE_TRACKING.TRACKING_DATE))
//								.setActivityType(op.getActivityType())
//								.setActivityIAE(op.getActivityIAE())
//								.setInvoice(op.getInvoice())
//								.setInvoiceType(op.getInvoiceType())
//								.setConceptType(op.getConceptType())													
//								.setEntryDate(op.getEntryDate())
//								.setInvoiceSeries(op.getInvoiceSeries())
//								.setInvoiceNumber(op.getInvoiceNumber())
//								.setDocNumber(op.getDocNumber())
//								.setRegistryDocumentType(op.getRegistryDocumentType())
//								.setRegistryDocumentCountry(op.getRegistryDocumentCountry())
//								.setRegistryDocument(op.getRegistryDocument())
//								.setRegistryName(op.getRegistryName())
//								.setOperationType(op.getOperationType())
//								.setPayDate(rec.getValue(FINANCE_TRACKING.TRACKING_DATE))
//								.setPayAmount(rec.getValue(FINANCE_TRACKING.AMOUNT))
//								.setPayMethod(payMethod)
//								.setPayMethodName(payMethodName)							
//								;
//								
//					 });
//		}
//	}
}
