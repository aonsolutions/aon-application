package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.jooq.Condition;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Operation Report Excel Book", urlPatterns = { "/aon_gwt_fiscal/roms/OperationReportExcelBook" })
public class OperationReportExcelBook extends HttpServlet {
	
	private static final long serialVersionUID = -8237842836135745934L;
	
	// Esta variable se utiliza para poder sacar los cobros/pagos en Facturas RECC, 
	// deben salir primero las lineas de la factura y despues los cobros/pagos 
	private OperationBreakdown opAccrual = null;
		
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
			params.setAeatBook(true);
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			// Obtener NIF y Nombre de la Empresa
			String companyDocument = "";
			String companyName = "";

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
			// Cada Libro Registro del IVA y del IRPF se presentará en un fichero 
			// diferente cuyo nombre será formado por la concatenación de los siguientes 
			// campos y en el siguiente orden:
			// 1) Ejercicio
			// 2) NIF
			// 3) Tipo del Libro Registro que contiene el fichero, mediante uno de los siguientes valores:
			//    I - Ventas e Ingresos (IRPF) (1 pestaña INGRESOS)
			//	  G - Compras y Gastos (IRPF) (1 pestaña GASTOS)
			//	  E - Facturas Expedidas (IVA) (1 pestaña EXPEDIDAS)
			//	  R - Facturas Recibidas (IVA) (1 pestaña RECIBIDAS)
			//	  U - Unificado de Facturas Expedidas (IVA) y de Ventas e Ingresos (IRPF) (1 pestaña EXPEDIDAS_INGRESOS)
			//	  V - Unificado de Facturas Recibidas (IVA) y de Compras y Gastos (IRPF) (1 pestaña RECIBIDAS_GASTOS)
			// 4) Nombre o Razón social
	
			String sheetName = "";
			String filename = AonDateUtils.getYear(params.getFromDate()) + companyDocument;
			
			if (params.getUnifiedBook()) {
				if (params.isExpenses()) {					
					filename = filename + "V";   // Libro de Facturas Recibidas (IVA) y Compras y Gastos (IRPF)
				    sheetName = "RECIBIDAS_GASTOS";
				}
				else {
					filename = filename + "U";  // Libro de Facturas Expedidas (IVA) y Ventas e Ingresos (IRPF)
					sheetName = "EXPEDIDAS_INGRESOS";
				}
			}
			else if (params.isIrpf()) {
				if (params.isExpenses()) {					
					filename = filename + "G";   // Libro de Compras y Gastos IRPF
				    sheetName = "GASTOS";
				}
				else {
					filename = filename + "I";  // Libro de Ventas e Ingresos IRPF
					sheetName = "INGRESOS";
				}
			}
			else {
				if (params.isExpenses()) {
					filename = filename + "R";   // Libro de Compras y Gastos IVA
					sheetName = "RECIBIDAS";
				}
									
				else {
					filename = filename + "E";  // Libro de Ventas e Ingresos IVA
					sheetName = "EXPEDIDAS";
				}
			}
			
			filename = filename + companyName;
			
			opAccrual = null;
			ExcelAction action = new ExcelAction(occam, params);
			action.initialize(sheetName);
			
			// Obtener datos	
			ACCOUNTING.getOperationBreakdown(occam, params).forEach(action);
			
			// Comprobar si quedan por poner cobros/pagos de la última factura
			if (opAccrual != null) {
				getInvoicePayments(occam, opAccrual, params).forEach(action);
			}
			
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
		private Occam occam;
		private OperationParams params;
		
		public ExcelAction(Occam occam, OperationParams params) {
			this.occam = occam;
			this.params = params;
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
			
			addHorizontalMergedRegion("Autoliquidación", 2);
			addAutoSizeCell("Ejercicio");
			addAutoSizeCell("Periodo");
			
			addHorizontalMergedRegion("Actividad", 2);
			addAutoSizeCell("Tipo");
			addAutoSizeCell("Epígrafe IAE");
		    
		    addVerticalMergedRegion("Tipo de Factura");
		    
		    if (params.isIrpf() || params.getUnifiedBook()) {
		    	if (params.isExpenses()) {
		    		addVerticalMergedRegion("Concepto de Gasto");
		    		addVerticalMergedRegion("Gasto deducible");
		    	}
		    	else {
		    		addVerticalMergedRegion("Concepto de Ingreso");
		    		addVerticalMergedRegion("Ingreso Computable");
		    	}
		    }
		    
		    addVerticalMergedRegion("Fecha Expedición");
		    addVerticalMergedRegion("Fecha Operación");
		    
		    if (params.isExpenses()) {
		    	addHorizontalMergedRegion("Identificación Factura Expedidor", 2);		    	
		    	addAutoSizeCell(" Serie-Número ");
		    	addAutoSizeCell("Número-Final");

		    	addVerticalMergedRegion("Número Recepción");
		    	addVerticalMergedRegion("Número Recepción Final");
		    	
		    	addHorizontalMergedRegion("NIF Expedidor", 3);
		    }
		    else {
		    	addHorizontalMergedRegion("Identificación de la Factura", 3);
		    	addAutoSizeCell("Serie");
		    	addAutoSizeCell("Número");
		    	addAutoSizeCell("Número-Final");

		        addHorizontalMergedRegion("NIF Destinatario",3);
		    }
		    
		    addAutoSizeCell("Tipo");
		    addAutoSizeCell("Código País");
		    addAutoSizeCell("Identificación");
		    
		    if (params.isExpenses()) {
		    	addVerticalMergedRegion("Nombre Expedidor");
		    } 
		    else {
		    	addVerticalMergedRegion("Nombre Destinatario");
		    }		    
		    
		    if (!params.isIrpf() || params.getUnifiedBook()) {
		    	addVerticalMergedRegion("Clave de Operación");	
		    }		    
		    
		    addVerticalMergedRegion("Total Factura");
		    addVerticalMergedRegion("Base Imponible");
		    addVerticalMergedRegion("Tipo de IVA");
		    
		    if (params.isExpenses()) {
		    	addVerticalMergedRegion("Cuota IVA Soportado");
		    	addVerticalMergedRegion("Cuota Deducible");		    	
		    }
		    else {
		    	addVerticalMergedRegion("Cuota IVA Repercutida");
		    }
		    
		    addVerticalMergedRegion("Tipo de Recargo Eq.");		    
		    addVerticalMergedRegion("Cuota Recargo Equivalencia");		    
		    
		    if (params.isExpenses()) {
		    	addHorizontalMergedRegion("Pago (Operación Criterio de Caja)", 4);
		    }
		    else {
		    	addHorizontalMergedRegion("Cobro (Operación Criterio de Caja)", 4);		    	
		    }
		    addAutoSizeCell("Fecha");
		    addAutoSizeCell("Importe");
		    addAutoSizeCell("Medio Utilizado");
		    addAutoSizeCell("Identificación Medio Utilizado");		    
		    
		    if (params.isIrpf() || params.getUnifiedBook()) {
		    	addVerticalMergedRegion("Tipo Retención IRPF");
		    	addVerticalMergedRegion("Importe Retenido IRPF");
		    }
		    		    
		    sheet.setRandomAccessWindowSize(1);
		    
		}

		public void accept(OperationBreakdown op) {
			
			// Comprobar si hay que poner los cobros/pagos de la factura anterior
			if (opAccrual == null) {
				opAccrual = op;
			}
			
			if (op.getInvoice() != null && !op.getInvoice().equals(opAccrual.getInvoice())) {
				getInvoicePayments(occam, opAccrual, params).forEach(this);				
				opAccrual = op;				
			}
			
			// Añadir linea de detalle al archivo Excel
			addDetailRow(op);			
		}
		
		private void addDetailRow(OperationBreakdown op) {
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			addCell(Integer.toString(AonDateUtils.getYear(op.getTaxDate()))).setCellStyle(centerCellStyle);  ;   // Autoliquidación - Ejercicio
			addCell(Period.getQuarterlyPeriod(AonDateUtils.getMonth(op.getTaxDate())).getName()).setCellStyle(centerCellStyle);  // Autoliquidación - Periodo
			addCell(op.getActivityType()).setCellStyle(centerCellStyle);  // Actividad - Tipo
			addCell(AonStringUtils.trimToEmpty(op.getActivityIAE()).replace(".","")).setCellStyle(centerCellStyle); // Actividad - Epígrafe IAE
			addCell(op.getInvoiceType()).setCellStyle(centerCellStyle);   // Tipo de Factura
			sheet.autoSizeColumn(cellCount-1);
			
			if (params.isIrpf() || params.getUnifiedBook()) {
				addCell(op.getConceptType()).setCellStyle(centerCellStyle); // Concepto de Ingreso/Gasto
			    addCell(op.getAmount());                                    // Ingreso computable/Gasto deducible
     		}
						
			addCell(op.getEntryDate());  // Fecha Expedición
			addCell("");                 // Fecha Operación (no se usa)
			
			if (params.isExpenses()) {
			    addCell(op.getInvoiceNumber()); // Identificación Factura del Expedidor - Serie-Numero
			    addCell("");                    // Identificación Factura del Expedidor - Número-Final (no se usa)
				addCell(op.getDocNumber());     // Número Recepción
				sheet.autoSizeColumn(cellCount-1);
				addCell("");                    // Número Recepción Final (no se usa)				
			}
			else {
				addCell(op.getInvoiceSeries());  // Identificación de la Factura - Serie
			    addCell(op.getInvoiceNumber());  // Identificación de la Factura - Número
			    addCell("");                     // Identificación de la Factura - Número-Final (no se usa)
			}
			
			if (AonStringUtils.isBlank(op.getRegistryDocumentCountry()) || op.getRegistryDocumentCountry().equals("ES")) {
				addCell("");
				addCell("");
			}
			else {
				addCell(op.getRegistryDocumentType()).setCellStyle(centerCellStyle);    // NIF - Tipo
				addCell(op.getRegistryDocumentCountry()).setCellStyle(centerCellStyle); // NIF - Código País
				// Si Tipo NIF = 02 NIF-IVA, comprobar que el documento lleva en sus dos primeras letras el pais
				if (op.getRegistryDocumentType().equals("02")) {
					String doc = op.getRegistryDocument();
					if(!AonStringUtils.equalsIgnoreCase(AonStringUtils.substring(doc, 0, 2), op.getRegistryDocumentCountry())) {
//					if(!doc.substring(0,2).equalsIgnoreCase(op.getRegistryDocumentCountry())) {
						doc = op.getRegistryDocumentCountry() + doc;
						op.setRegistryDocument(doc);
					}					
				}
			}
			
			addCell(op.getRegistryDocument()); // NIF - Identificación
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getRegistryName()); // Nombre
			sheet.autoSizeColumn(cellCount-1);
			
			if (!params.isIrpf() || params.getUnifiedBook()) {
				addCell(op.getOperationType()).setCellStyle(centerCellStyle); // Clave de Operación
			}
			
			boolean isInvoiceLine = (op.getInvoice() != null ) && (op.getPayDate() == null);
			
			if (isInvoiceLine) {
				addCell(op.getTotal());   // Total Factura
				addCell(op.getBase());    // Base Imponible
				addCell(op.getPercent()); // Tipo de IVA
				addCell(op.getQuota());   // IVA Repercutido/Soportado				
			}
			else {
				addCell("");   // Total Factura
				addCell("");   // Base Imponible
				addCell("");   // Tipo de IVA
				addCell("");   // IVA Repercutido/Soportado
			}

			if (params.isExpenses()) {
				if (isInvoiceLine)
					addCell(op.getDeductibleQuota()); // Cuota Deducible									
				else addCell(""); 
			}
			
			addDoubleCell(op.getSurchargePercent()); // Tipo de Recargo Eq.
			addDoubleCell(op.getSurchargeQuota());   // Cuota Recargo Eq.
			
			addCell(op.getPayDate());       // Cobro/Pago RECC - Fecha
			sheet.autoSizeColumn(cellCount-1);
			addDoubleCell(op.getPayAmount());     // Cobro/Pago RECC - Importe
			sheet.autoSizeColumn(cellCount-1);
			addCell(op.getPayMethod()).setCellStyle(centerCellStyle); // Cobro/Pago RECC - Medio Utilizado
			addCell(op.getPayMethodName()); // Cobro/Pago RECC - Identificación Medio Utilizado
			sheet.autoSizeColumn(cellCount-1);
			
			if (params.isIrpf() || params.getUnifiedBook()) {
				addDoubleCell(op.getRetentionPercent()); // Tipo Retención IRPF
				addDoubleCell(op.getRetentionQuota());   // Importe Retenido IRPF
			}
			
		}		
		
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
	private Stream<OperationBreakdown> getInvoicePayments(Occam occam, final OperationBreakdown op, final OperationParams params) {
		
		// Solo para Libro de IVA (o unificado) y para Facturas RECC
		if ((params.isIrpf() && !params.getUnifiedBook()) || !("07".equals(op.getOperationType()))) {
			return Stream.empty();	
		}

		// Condicion para que aparezcan los vecimientos
		// Está cobrado/pagado y la fecha de cobro/pago está entre los filtros
		Condition condition = (FINANCE_TRACKING.TYPE.equal(FinanceTrackingType.PAID.value()).and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(params.getFromDate()),AonDateUtils.toSql(params.getToDate()))));
		
		try ( CloseableAONContext ctx = AONContext.getAONContext(occam)) { 
			return ctx.getDslContext().select(
	      			 FINANCE_TRACKING.TRACKING_DATE
					,FINANCE_TRACKING.AMOUNT
					,FINANCE_TRACKING.TYPE
					,PAY_METHOD.TYPE
					,PAY_METHOD.NAME
					,RBANK.BANK_ACCOUNT
					)
					.from(FINANCE)
					.join(FINANCE_TRACKING).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))				
					.leftJoin(PAY_METHOD).on(PAY_METHOD.ID.equal(FINANCE.PAY_METHOD))
					.leftJoin(RBANK).on(RBANK.ID.equal(FINANCE_TRACKING.RBANK))				
					.where(FINANCE.INVOICE.equal(op.getInvoice()))
					.and(condition)
					.orderBy(FINANCE_TRACKING.TRACKING_DATE)
					.fetch()
					.stream()
					.map( rec -> {
						
						// Metodo de Cobro/Pago
						String payMethod = ""; 
						String payMethodName = ""; 
						Byte pm = rec.getValue(PAY_METHOD.TYPE);
						if (pm != null) {
							switch (pm) {
								case 1:  // Negociable 
									payMethod = "05"; // Domiciliacion
									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
									break;
								case 4:  // Cheque 
									payMethod = "02"; // Cheque
									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
									break;
								case 5:  // Transferencia 
									payMethod = "01";  // Transferencia
									payMethodName = rec.getValue(RBANK.BANK_ACCOUNT);
									break;
								default: // Resto
									payMethod = "04"; // Otros medios de pago		
									payMethodName = rec.getValue(PAY_METHOD.NAME);
									break;
							}
						}
						
						return new OperationBreakdown()
								.setTaxDate(rec.getValue(FINANCE_TRACKING.TRACKING_DATE))
								.setActivityType(op.getActivityType())
								.setActivityIAE(op.getActivityIAE())
								.setInvoice(op.getInvoice())
								.setInvoiceType(op.getInvoiceType())
								.setConceptType(op.getConceptType())													
								.setEntryDate(op.getEntryDate())
								.setInvoiceSeries(op.getInvoiceSeries())
								.setInvoiceNumber(op.getInvoiceNumber())
								.setDocNumber(op.getDocNumber())
								.setRegistryDocumentType(op.getRegistryDocumentType())
								.setRegistryDocumentCountry(op.getRegistryDocumentCountry())
								.setRegistryDocument(op.getRegistryDocument())
								.setRegistryName(op.getRegistryName())
								.setOperationType(op.getOperationType())
								.setPayDate(rec.getValue(FINANCE_TRACKING.TRACKING_DATE))
								.setPayAmount(rec.getValue(FINANCE_TRACKING.AMOUNT))
								.setPayMethod(payMethod)
								.setPayMethodName(payMethodName)							
								;
								
					 });
		}
	}
}
