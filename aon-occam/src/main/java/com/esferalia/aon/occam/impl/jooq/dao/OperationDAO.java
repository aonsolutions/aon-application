package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;

import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.InvoiceTax;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

// Para el Panel de Compras y Gastos / Ventas e Ingresos 
public class OperationDAO extends FiscalModelDAO {
	
	// ---------- STREAM FUNCTIONS ----------
	
	public static Stream<OperationBreakdown> getOperationBreakdown(final AONContext ctx, int domain, OperationParams params) {
		
		Date dateFrom = params.getFromDate();
		Date dateTo = params.getToDate();
		boolean expenses = params.getExpenses();  // true=Listado Compras y Gastos, false=Listado Ventas e Ingresos
		boolean irpf = params.getIrpf(); 		  // true=Listado IRPF, false=Listado IVA
		boolean aeatBook = params.getAeatBook();       // Libro Registro AEAT
		
		InvoiceTax invoice_tax1 = INVOICE_TAX.as("invoice_tax1"); // Para la cuota de IVA y REQ
		InvoiceTax invoice_tax2 = INVOICE_TAX.as("invoice_tax2"); // Para la cuota de retención IRPF		
		
		// Campos que acumulan los diferentes importes
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
		Field<BigDecimal> sumBase = DSL.sum(invoice_tax1.BASE);
		
		// Cuotas de IVA y REQ. La recalculamos si es cero porque algunas facturas que se introducen desde gestión, no graban esas cuotas en INVOICE_TAX 
		Field<BigDecimal> sumQuota = DSL.sum(DSL.when(invoice_tax1.QUOTA.eq(0.0),DSL.round(invoice_tax1.BASE.mul(invoice_tax1.PERCENTAGE).div(100),2)).otherwise(invoice_tax1.QUOTA));
		Field<BigDecimal> sumDeductibleQuota = DSL.sum(DSL.when(invoice_tax1.DEDUCTIBLE_QUOTA.eq(0.0),DSL.round(DSL.round(invoice_tax1.BASE.mul(invoice_tax1.PERCENTAGE).div(100.0),2).mul(invoice_tax1.DEDUCTIBLE_PERCENT).div(100.0),2)).otherwise(invoice_tax1.DEDUCTIBLE_QUOTA));
		Field<BigDecimal> sumSurchargeQuota = DSL.sum(DSL.when(invoice_tax1.SURCHARGE_QUOTA.eq(0.0),DSL.round(invoice_tax1.BASE.mul(invoice_tax1.SURCHARGE).div(100),2)).otherwise(invoice_tax1.SURCHARGE_QUOTA));
		
		// Cuota de la Retencion. La recalculamos si es cero porque algunas facturas que se introducen desde gestión, no graban esas cuotas en INVOICE_TAX 
		Field<BigDecimal> sumRetentionQuota = DSL.sum(DSL.when(invoice_tax2.QUOTA.eq(0.0),DSL.round(invoice_tax2.BASE.mul(invoice_tax2.PERCENTAGE).div(100),2)).otherwise(invoice_tax2.QUOTA));
		
		// Concepto de los Libros Registro AEAT
		Field<String> conceptType = getConceptType();
		
		// Condicion para que salgan datos según el régimen de la actividad
		// Listado IRPF: No salen datos, si la actividad está en Regimen de IRPF exento
		// Listado IVA: No salen datos, si la actividad está en Regimen de IVA exento o en Recargo de Equivalencia
		Condition activityCondition;					
		if (irpf) {			
			activityCondition = ENTERPRISE_ACTIVITY.RETENTION_REGIME.isNull().or(ENTERPRISE_ACTIVITY.RETENTION_REGIME.notEqual(IRPFRegime.EXEMPT.value())); // Listado IRPF
		}
		else 
		{
			activityCondition = (ENTERPRISE_ACTIVITY.VAT_REGIME.isNull().or(ENTERPRISE_ACTIVITY.VAT_REGIME.notEqual(VATRegime.EXEMPT.value()))  
			                .and(ENTERPRISE_ACTIVITY.SURCHARGE.isNull().or(ENTERPRISE_ACTIVITY.SURCHARGE.equal((byte) 0))));	// Listado IVA				
		}
		
		// Si la actividad está en Regimen de IRPF exento (Listado IRPF) o Regimen de IVA exento o en recargo de equivalencia (Listado de IVA)
		// no sale ningún dato
		if (params.getActivity() != null &&
			ctx.getDslContext()
			.select()
			.from(ENTERPRISE_ACTIVITY)
			.where(ENTERPRISE_ACTIVITY.ID.equal(params.getActivity()))
			.and(activityCondition)
			.fetch()
			.isEmpty()) {
			return Stream.empty();
		}		
		
		// Numero de actividades de la empresa en la fecha del apunte o fecha de IVA (se usará en los apuntes imputados a todas las actividades -actividad es null-, en empresas que tengan mas de una actividad)
		Field<Integer> activityCount = irpf ? 
				DSL.selectCount()
					.from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain))
					.and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(ACCOUNT_ENTRY.ENTRY_DATE)))
					.and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(ACCOUNT_ENTRY.ENTRY_DATE)))
					.asField("activityCount") :
				DSL.selectCount()
					.from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain))
					.and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(INVOICE.TAX_DATE)))
					.and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(INVOICE.TAX_DATE)))
					.asField("activityCount");
					
		// Condición para que aparezcan los diferentes apuntes:
		// Listado IRPF: Aparecen todos los apuntes del grupo 6 (compras y gastos) o 7 (ventas e ingresos)
		// Listado IVA: Aparecen todos los apuntes que sean facturas
		Condition condition;					
		if (irpf) {
			condition = ACCOUNT.CODE.startsWith(expenses?"6":"7") ; // Listado IRPF
			if (aeatBook)
				condition = condition.and(INVOICE.ID.isNull().or(invoice_tax1.ID.isNotNull()));
		}
		else 
		{			
			condition = INVOICE.ID.isNotNull();
			if (expenses)
				condition = condition.and(invoice_tax1.ID.isNotNull()).and(INVOICE.TYPE.equal(InvoiceType.PURCHASE.value()).or(INVOICE.TYPE.equal(InvoiceType.EXPENSES.value()))); // Listado IVA (Compras)
			else condition = condition.and(INVOICE.TYPE.equal(InvoiceType.SALES.value())); // Listado IVA (Ventas)		
		}
         			                 					
		// Condicion de la fecha:
		// Listado IRPF: Fecha del apunte
		// Listado IVA: Fecha de IVA
		Condition dateCondition = irpf ? ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo)) :  // IRPF 
    				              		 INVOICE.TAX_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo));    // IVA
		
		// Condicion de la tabla INVOICE_DETAIL_ACCOUNT
		// Se pone de esta forma porque se ha detectado que esta tabla, por algunos fallos de grabacion de las
		// facturas de gestión, tiene mas de un registro por linea de factura, cuando debería tener solo un registro
		// por lo tanto si lo hacemos con un JOIN, se duplican (o multiplican), los importes
		Condition conditionIDA = INVOICE.ID.isNull().orExists(ctx.getDslContext().select(INVOICE_DETAIL_ACCOUNT.ID).from(INVOICE_DETAIL_ACCOUNT).where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT)))); 
		
		// Agrupamos por:
		// Listado IRPF: asiento + cuenta contable
		// Listado IVA:  asiento + porcentaje de IVA
		// Libro Registro AEAT (IRPF): asiento + concepto + porcentaje_iva
		GroupField[] groupBy = aeatBook && irpf ? new GroupField[]{ ACCOUNT_ENTRY.ID, conceptType, invoice_tax1.PERCENTAGE } : // Libro Registro AEAT (IRPF)
                            			   irpf ? new GroupField[]{ ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ACCOUNT } : // Listado IRPF 
									              new GroupField[]{ ACCOUNT_ENTRY.ID, invoice_tax1.PERCENTAGE }; // Listado IVA
				
		// Ordenamos por:
		// Listado IRPF: Fecha apunte + nº documento + numero asiento + linea apunte
		// Listado IVA: Fecha IVA + nº documento + nº asiento + linea apunte
		Field<?>[] orderBy = irpf ? new Field<?>[]{ACCOUNT_ENTRY.ENTRY_DATE, ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, ACCOUNT_ENTRY.JOURNAL, ACCOUNT_ENTRY_DETAIL.LINE}:  // Listado IRPF
			                        new Field<?>[]{INVOICE.TAX_DATE, ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, ACCOUNT_ENTRY.JOURNAL, ACCOUNT_ENTRY_DETAIL.LINE};  // Listado IVA
		
		// Obtenemos los datos
        return ctx.getDslContext()			
				.select(  ACCOUNT_ENTRY.ACTIVITY
						, ACCOUNT_ENTRY.ENTRY_DATE 
						, ACCOUNT_ENTRY_DETAIL.CONCEPT		                 
		                , ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER
		                , sumDebit
						, sumCredit
						, ACCOUNT.CODE
						, ACCOUNT.DESCRIPTION
						, INVOICE.ID
		                , INVOICE.TAX_DATE
						, INVOICE.REFERENCE_CODE 
						, INVOICE.RDOCUMENT 
						, INVOICE.RNAME												                
						, sumBase
						, sumQuota
						, sumDeductibleQuota
						, sumSurchargeQuota
						, activityCount
						, invoice_tax1.PERCENTAGE
						, invoice_tax1.SURCHARGE
						
						// Campos para los Libros Registro AEAT
						, IAE.SECTION
						, IAE.EPIGRAPH
						, INVOICE.TYPE
						, INVOICE.SERIES
						, INVOICE.NUMBER
						, INVOICE.RDOCUMENT_TYPE
						, INVOICE.RDOCUMENT_COUNTRY
						, INVOICE.VAT_ACCRUAL_PAYMENT
						, INVOICE.WITHHOLDING_FARMER						
						, INVOICE.RECTIFICATION_TYPE
						, invoice_tax2.PERCENTAGE
						, sumRetentionQuota
						, INVOICE.TRANSACTION
						, conceptType
						, INVOICE_DUA.ID
		                )				
		                .from(ACCOUNT_ENTRY_DETAIL)
						.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
		                .join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
		                .leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))		                
		                .leftOuterJoin(INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) 
		                .leftOuterJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		                .leftOuterJoin(invoice_tax1).on(invoice_tax1.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(invoice_tax1.TAX_TYPE.equal((byte)1)))  // Importes IVA y REq
		                .leftOuterJoin(invoice_tax2).on(invoice_tax2.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(invoice_tax2.TAX_TYPE.equal((byte)2)))  // Retención IRPF
		                .leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(params.getActivity()))
		                .leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
		                .leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
		                
		                .where(ACCOUNT_ENTRY.DOMAIN.equal(domain))		                
		                .and(dateCondition)
		                .and(ACCOUNT_ENTRY.ENTRY_TYPE.notEqual(AccountEntryType.OPERATING.getValue()))
		                .and(condition)
		                .and(conditionIDA)
		                .and(
			                params.getActivity() == null 
			                	? DSL.trueCondition()
	                			: ACCOUNT_ENTRY.ACTIVITY.equal(params.getActivity()).or(ACCOUNT_ENTRY.ACTIVITY.isNull())
	                		)  // Actividad null, quiere decir que el apunte o factura, se reparte entre todas las actividades		                
		                .groupBy(groupBy)
		                .orderBy(orderBy)
		                .fetch()
						.stream()
						.map( rec -> {						
							
							Integer invoice = rec.getValue(INVOICE.ID);
							String cuenta = rec.getValue(ACCOUNT.CODE);
							
							double base = rec.getValue(sumBase)==null?0.0:rec.getValue(sumBase).doubleValue();
							double quota = rec.getValue(sumQuota)==null?0.0:rec.getValue(sumQuota).doubleValue();					
							double deductibleQuota = rec.getValue(sumDeductibleQuota)==null?0.0:rec.getValue(sumDeductibleQuota).doubleValue();					
							double surchargeQuota = rec.getValue(sumSurchargeQuota)==null?0.0:rec.getValue(sumSurchargeQuota).doubleValue();
							double retentionQuota = rec.getValue(sumRetentionQuota)==null?0.0:rec.getValue(sumRetentionQuota).doubleValue();

							// El total es la suma de base + impuestos en facturas y el importe debe o haber en el resto de apuntes
							double total = 0;							
							if (invoice == null) {
								// Apuntes que no son facturas (base y total coinciden)
								double debit = rec.getValue(sumDebit) == null ? 0.0 : rec.getValue(sumDebit).doubleValue();
								double credit = rec.getValue(sumCredit) == null ? 0.0 : rec.getValue(sumCredit).doubleValue();
								if (cuenta.startsWith("6"))
									base = debit - credit;  // Compras y Gastos
								else base =  credit - debit; // Ventas e Ingresos
								total = base;
							}
							else {
								// Apuntes que son facturas (total es base + iva + recargo_equivalencia (no se tiene en cuenta la retencion)
								total = AonMathUtils.round(base + deductibleQuota + surchargeQuota);								
							}
							
							Integer act = rec.getValue(ACCOUNT_ENTRY.ACTIVITY);
							int count = rec.getValue(activityCount);
							
							// Facturas o apuntes que van a todas las actividades (activity=null),  
							// al sacarlas en cada actividad, debe salir la parte proporcional, de 
							// forma equitativa, segun las actividades que haya (1/2, 1/3, 1/4, ...)
							if (act == null && count > 1) {
								base = AonMathUtils.round(base/count);
								quota = AonMathUtils.round(quota/count);
								deductibleQuota = AonMathUtils.round(deductibleQuota/count);
								surchargeQuota = AonMathUtils.round(surchargeQuota/count);
								total = AonMathUtils.round(total/count);
								retentionQuota = AonMathUtils.round(retentionQuota/count);
							}
							
							// Fecha IVA (se pone siempre, aunque sean apuntes sin factura, porque se usa en los Libros Registro AEAT)
							Date taxDate = rec.getValue(INVOICE.TAX_DATE);
							if (taxDate == null)
								taxDate = rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE);
							
							// Tipo de Factura (Libros Registro AEAT)
							String invoiceType = "SF";  // Apuntes sin factura
							if (rec.getValue(INVOICE.ID) != null) {
								byte rectificationType = rec.getValue(INVOICE.RECTIFICATION_TYPE);
								if (rectificationType == RectificationType.NORMAL_RECTIFIER.value() || rectificationType == RectificationType.SPECIAL_RECTIFIER.value())
									invoiceType = "R0";  // Rectificativa
								else if (rec.getValue(INVOICE_DUA.ID) != null)
									invoiceType = "F5"; // DUA
								else if (rec.getValue(INVOICE.TYPE) == InvoiceType.UNDEDUCTIBLE.value())
									invoiceType = "F2"; // Factura sin identificación del destinatario (tickets, estan como no deducibles)
								else invoiceType = "F1";  // Resto
							}							
							
							// Tipo de Operación (Libros Registro AEAT)
							String operationType = "";
							if (rec.getValue(INVOICE.ID) != null) {
								if (rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
									operationType = "07";  // RECC
								else if (rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
										 operationType = "02";	// REAGYP							
							}							
							
							// Tipo NIF (Libros Registro AEAT)							
							// Se hace como se hace en el SII:
							// Factura intracomunitaria: 02-NIF-IVA
							// Pasp., P.T., T.C., Otr: El valor que lleva (3, 4, 5 ó 6)
							// Resto: no lleva tipo
						    String registryDocumentType = "";	
						    Byte it = rec.getValue(INVOICE.TRANSACTION); 
							if (it != null && it == InvoiceTransactionType.INTRACOMMUNITY.value())
								registryDocumentType = "02";
							else { 
								Byte dt = rec.getValue(INVOICE.RDOCUMENT_TYPE);
								if (dt != null && (dt == DocumentType.PASSPORT.value() || dt == DocumentType.WORK_PERMIT.value() ||	dt == DocumentType.COMMUNITY_CARD.value() || dt == DocumentType.OTHER.value())) {
									registryDocumentType = AonNumberUtils.toString(dt);
									registryDocumentType = AonStringUtils.leftPad(registryDocumentType, 2, '0');
								}
							}
							
							// Tipo de Actividad (según el IAE) (Libros Registro AEAT)
							// 1 - Actividades empresariales de carácter mercantil - Resto de Epígrafes IAE
							// 2 - Actividades agrícolas y ganaderas - Sección 1, Division 0 
							// 3 - Otras actividades empresariales de carácter no mercantil	- No contemplado en AON
							// 4 - Actividades profesionales de carácter artístico o deportivo - Sección 3
							// 5 - Restantes actividades profesionales - Sección 2
							String iae = AonStringUtils.trimToEmpty(rec.getValue(IAE.EPIGRAPH));
							String section = AonStringUtils.trimToEmpty(rec.getValue(IAE.SECTION));  
							String activityType = "1";
							if (section.equals("1") && iae.length() >= 1 && iae.startsWith("0"))
								activityType = "2";
							else if (section.equals("2"))
								activityType = "5";
							else if (section.equals("3"))
								activityType = "4";
							
							// Ingreso computable/Gasto deducible (Libros Registro AEAT)
							// base + iva no deducible
							double amount = base + (quota-deductibleQuota);
							
							// Completar todos los datos
							return new OperationBreakdown()
									.setEntryDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
									.setAccount(cuenta)
									.setAccountDescription(rec.getValue(ACCOUNT.DESCRIPTION))
									.setConcept(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
									.setTotal(total)					
									.setInvoice(rec.getValue(INVOICE.ID))
									.setDocNumber(rec.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
									.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
									.setRegistryName(rec.getValue(INVOICE.RNAME))
									.setTaxDate(taxDate)
									.setBase(base)
									.setQuota(quota)					
									.setDeductibleQuota(deductibleQuota)					
									.setSurchargeQuota(surchargeQuota)
									.setPercent(rec.getValue(invoice_tax1.PERCENTAGE)==null?0.0:rec.getValue(invoice_tax1.PERCENTAGE))
									.setSurchargePercent(rec.getValue(invoice_tax1.SURCHARGE)==null?0.0:rec.getValue(invoice_tax1.SURCHARGE))
							
									// Nuevos datos para el Libro Registro AEAT
									.setActivityType(activityType)               // Actividad - Tipo							
									.setActivityIAE(iae)                         // Actividad - Epígrafe							
									.setInvoiceType(invoiceType)                 // Tipo de Factura									
									.setConceptType(rec.getValue(conceptType))   // Concepto de Ingreso/Gasto
									.setAmount(amount)                           // Ingreso computable/Gasto deducible 
									.setInvoiceSeries(rec.getValue(INVOICE.SERIES))  // Factura - Serie							 
									.setInvoiceNumber(expenses ? rec.getValue(INVOICE.REFERENCE_CODE) : AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER))) // Factura - Número
									.setRegistryDocumentType(registryDocumentType)   // Tipo NIF 
									.setRegistryDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY))  // Pais
									.setOperationType(operationType)   	// Tipo de Operación
									.setPayDate(null)                   // Cobro/Pago - Fecha (Los cobros y pagos se obtienen al crear el libro)
									.setPayAmount(0) 					// Cobro/Pago - Amount
									.setPayMethod("")  					// Cobro/Pago - Medio de cobro/pago
									.setPayMethodName("") 				// Cobro/Pago - Identificación medio de cobro/pago									
									.setRetentionPercent(rec.getValue(invoice_tax2.PERCENTAGE)==null?0.0:rec.getValue(invoice_tax2.PERCENTAGE)) // Porcentaje Retención
									.setRetentionQuota(retentionQuota)																			// Cuota Retención
									;
							
						});
	}
	
	private static Field<String> getConceptType( ) {
		
		return DSL				
				// Conceptos de Ingreso
				.when(ACCOUNT.CODE.substring(1, 1).eq("7").and(INVOICE.ID.isNull()),"IX1") // IX1 - Otros Ingresos (incluidas subvenciones y otras transferencias) - Apuntes sin factura				
				.when(ACCOUNT.CODE.substring(1, 1).eq("7"),"I00") // I01 - Ingresos de explotación - Facturas
				
				// Conceptos de Gasto
				.when(ACCOUNT.CODE.substring(1, 3).eq("640"),"G04") // G04 - Sueldos y salarios - 640
				.when(ACCOUNT.CODE.substring(1, 3).eq("642"),"GX2") // GX2 - Seguridad Social a cargo de la empresa (incluidas las cotizaciones del titular) - 642				
				.when(ACCOUNT.CODE.substring(1, 3).eq("621"),"G12") // G12 - Arrendamientos y cánones - 621
				.when(ACCOUNT.CODE.substring(1, 3).eq("622"),"G13") // G13 - Reparaciones y conservación - 622
				.when(ACCOUNT.CODE.substring(1, 3).eq("628"),"GX4") // GX4 - Suministros (entre otros agua, gas, electricidad, telefonía, internet) - 628
				.when(ACCOUNT.CODE.substring(1, 3).eq("623"),"G19") // G19 - Servicios de profesionales independientes - 623
				.when(ACCOUNT.CODE.substring(1, 2).eq("60").or(ACCOUNT.CODE.substring(1, 2).eq("61")),"GX1") // GX1 - Consumos de explotación - 60, 61
				.when(ACCOUNT.CODE.substring(1, 2).eq("64"),"GX3") // GX3 - Otros gastos de personal - RESTO 64
				.when(ACCOUNT.CODE.substring(1, 2).eq("62"),"GX5") // GX5 - Otros servicios exteriores - RESTO 62
				.when(ACCOUNT.CODE.substring(1, 2).eq("66"),"GX6") // GX6 - Gastos financieros - 66
				.when(ACCOUNT.CODE.substring(1, 2).eq("63"),"GX7") // GX7 - Tributos fiscalmente deducibles - 63, IVA,REQ,REAGYP NO DED.
				.when(ACCOUNT.CODE.substring(1, 2).eq("68"),"GX8") // GX8 - Amortizaciones: dotaciones del ejercicio fiscalmente deducibles - 68
				.when(ACCOUNT.CODE.substring(1, 2).eq("65"),"G34") // G34 - Pérdidas por insolvencias de deudores - 65
				.otherwise("G37") // G37 - Otros conceptos fiscalmente deducibles (excepto provisiones) - RESTO
				;		
	}
	
}
