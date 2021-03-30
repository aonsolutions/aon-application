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
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
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
		boolean expenses = params.getExpenses();  	// true=compras y gastos, false=ventas e ingresos
		boolean irpf = params.getIrpf(); 			// true=listado IRPF, false=listado IVA
		
		InvoiceTax invoice_tax1 = INVOICE_TAX.as("invoice_tax1"); // Para la cuota de IVA y REQ
		InvoiceTax invoice_tax2 = INVOICE_TAX.as("invoice_tax2"); // Para la retención IRPF		
		
		// Campos que acumulan los diferentes importes
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
		//Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE);
		Field<BigDecimal> sumBase = DSL.sum(invoice_tax1.BASE);
		
		// Recalculamos cuotas de IVA y REq, por que las facturas que se introducen desde gestión, no graban esas cuotas en INVOICE_TAX (solo cuando cuota es cero)
//		Field<BigDecimal> sumQuota = DSL.sum(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE).div(100),2));
//		Field<BigDecimal> sumDeductibleQuota = DSL.sum(DSL.round(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE).div(100.0),2).mul(INVOICE_TAX.DEDUCTIBLE_PERCENT).div(100.0),2));
//		Field<BigDecimal> sumSurchargeQuota = DSL.sum(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.SURCHARGE).div(100),2));
		Field<BigDecimal> sumQuota = DSL.sum(DSL.when(invoice_tax1.QUOTA.eq(0.0),DSL.round(invoice_tax1.BASE.mul(invoice_tax1.PERCENTAGE).div(100),2)).otherwise(invoice_tax1.QUOTA));
		Field<BigDecimal> sumDeductibleQuota = DSL.sum(DSL.when(invoice_tax1.DEDUCTIBLE_QUOTA.eq(0.0),DSL.round(DSL.round(invoice_tax1.BASE.mul(invoice_tax1.PERCENTAGE).div(100.0),2).mul(invoice_tax1.DEDUCTIBLE_PERCENT).div(100.0),2)).otherwise(invoice_tax1.DEDUCTIBLE_QUOTA));
		Field<BigDecimal> sumSurchargeQuota = DSL.sum(DSL.when(invoice_tax1.SURCHARGE_QUOTA.eq(0.0),DSL.round(invoice_tax1.BASE.mul(invoice_tax1.SURCHARGE).div(100),2)).otherwise(invoice_tax1.SURCHARGE_QUOTA));
		
		// Base y cuota de la Retencion 
//		Field<BigDecimal> sumRetentionBase = DSL.sum(invoice_tax2.BASE); // FALTA - Si la cuota retencion no se debe calcular, no necesito la base
		Field<BigDecimal> sumRetentionQuota = DSL.sum(invoice_tax2.QUOTA);  // FALTA - No sé si las facturas desde gestión que llevan retención graban la cuota en INVOICE_TAX??
		
		// Condicion para que salgan datos según el régimen de la actividad
		// Listado IRPF: No salen datos, si la actividad está en Regimen de IRPF exento
		// Listado IVA: No salen datos, si la actividad está en Regimen de IVA exento o en Recargo de Equivalencia
		Condition activityCondition;					
		if (irpf) {			
			activityCondition = ENTERPRISE_ACTIVITY.RETENTION_REGIME.isNull().or(ENTERPRISE_ACTIVITY.RETENTION_REGIME.notEqual(IRPFRegime.EXEMPT.value())); // IRPF
		}
		else 
		{
			activityCondition = (ENTERPRISE_ACTIVITY.VAT_REGIME.isNull().or(ENTERPRISE_ACTIVITY.VAT_REGIME.notEqual(VATRegime.EXEMPT.value()))  
			                .and(ENTERPRISE_ACTIVITY.SURCHARGE.isNull().or(ENTERPRISE_ACTIVITY.SURCHARGE.equal((byte) 0))));	// IVA				
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
		// No se tienen en cuenta las actividades que están exentas de iva o en recargo de equivalencia (para listado IVA) o está exenta de IRPF (para Listado IRPF)
		Field<Integer> activityCount = irpf ? 
				DSL.selectCount()
					.from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain))
					.and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(ACCOUNT_ENTRY.ENTRY_DATE)))
					.and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(ACCOUNT_ENTRY.ENTRY_DATE)))
					.and(activityCondition)
					.asField("activityCount") :
				DSL.selectCount()
					.from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain))
					.and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(INVOICE.TAX_DATE)))
					.and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(INVOICE.TAX_DATE)))
					.and(activityCondition)
					.asField("activityCount");
					
		// Condición para que aparezcan los diferentes apuntes:
		// Listado IRPF: Aparecen todos los apuntes del grupo 6 (compras y gastos) o 7 (ventas e ingresos)
		// Listado IVA: Aparecen todos los apuntes que sean facturas
		Condition condition;					
		if (irpf) {
			condition = ACCOUNT.CODE.startsWith(expenses?"6":"7") ; // IRPF
		}
		else 
		{
			//condition = INVOICE_TAX.PERCENTAGE.isNotNull();
			condition = INVOICE.ID.isNotNull();
			if (expenses)				
				condition = condition.and(INVOICE.TYPE.equal(InvoiceType.PURCHASE.value()).or(INVOICE.TYPE.equal(InvoiceType.EXPENSES.value()))); // IVA (Compras y Gastos)
			else condition = condition.and(INVOICE.TYPE.equal(InvoiceType.SALES.value())); // IVA (Ventas)		
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
		GroupField[] groupBy = irpf ? new GroupField[]{ ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ACCOUNT } : // IRPF
//		                 			  new GroupField[]{ ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ACCOUNT, INVOICE_TAX.PERCENTAGE }; // IVA
                            		  new GroupField[]{ ACCOUNT_ENTRY.ID, invoice_tax1.PERCENTAGE }; // IVA
		
		// Ordenamos por:
		// Listado IRPF: Fecha apunte + id asiento + id apunte
		// Listado IVA: Fecha IVA + id asiento + id apunte
		Field<?>[] orderBy = irpf ? new Field<?>[]{ACCOUNT_ENTRY.ENTRY_DATE, ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ID}:
			                        new Field<?>[]{INVOICE.TAX_DATE, ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ID};			
		
		return 	ctx.getDslContext()			
				.select(  //ENTERPRISE_ACTIVITY.DESCRIPTION
						//, 
						  ACCOUNT_ENTRY.ACTIVITY
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
						, IAE.EPIGRAPH
						, INVOICE.SERIES
						, INVOICE.NUMBER
						, INVOICE.RDOCUMENT_TYPE
						, INVOICE.RDOCUMENT_COUNTRY
						, INVOICE.VAT_ACCRUAL_PAYMENT
						, INVOICE.WITHHOLDING_FARMER						
						, invoice_tax2.PERCENTAGE
						, sumRetentionQuota
						
		                )
				
		                .from(ACCOUNT_ENTRY_DETAIL)
						.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
		                .join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
		                .leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))		                
		                .leftOuterJoin(INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) 
		                .leftOuterJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		                
		                //.leftOuterJoin(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT)))
		                ////.leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL).and(INVOICE_TAX.TAX_TYPE.equal((byte)1)))
		                //.leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_TAX.TAX_TYPE.equal((byte)1)))
		                
		                .leftOuterJoin(invoice_tax1).on(invoice_tax1.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(invoice_tax1.TAX_TYPE.equal((byte)1)))  // Importes IVA y REq
		                .leftOuterJoin(invoice_tax2).on(invoice_tax2.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(invoice_tax2.TAX_TYPE.equal((byte)2)))  // Retención IRPF
		                
		                //.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ACCOUNT_ENTRY.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
		                .leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(params.getActivity()))
		                .leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
		                
		                .where(ACCOUNT_ENTRY.DOMAIN.equal(domain))		                
		                .and(dateCondition)
		                .and(ACCOUNT_ENTRY.ENTRY_TYPE.notEqual(AccountEntryType.OPERATING.getValue()))
		                .and(condition)
		                .and(conditionIDA)
		                .and(
			                params.getActivity() == null 
			                	? DSL.trueCondition()
	                			: ACCOUNT_ENTRY.ACTIVITY.equal(params.getActivity()).or(ACCOUNT_ENTRY.ACTIVITY.isNull())
	                		)  // Actividad Null, quiere decir que el apunte o factura, se reparte entre todas las actividades		                
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
							
							Date taxDate = rec.getValue(INVOICE.TAX_DATE);
							if (taxDate == null)
								taxDate = rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE);
							
							String operationType = "";
							if (rec.getValue(INVOICE.ID) != null) {
								if (rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
									operationType = "07";
								else if (rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
										 operationType = "02";								
							}							
							
							// FALTA - NO COINCIDEN LOS VALORES DE RDOCUMENT_TYPE CON LOS VALORES QUE DEBEN SER SEGUN EL FORMATO
							String registryDocumentType = AonNumberUtils.toString(rec.getValue(INVOICE.RDOCUMENT_TYPE));
							registryDocumentType = AonStringUtils.leftPad(registryDocumentType, 2, '0');
							
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
//									.setPercent(rec.getValue(INVOICE_TAX.PERCENTAGE)==null?0.0:rec.getValue(INVOICE_TAX.PERCENTAGE))
//									.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE)==null?0.0:rec.getValue(INVOICE_TAX.SURCHARGE))
									.setPercent(rec.getValue(invoice_tax1.PERCENTAGE)==null?0.0:rec.getValue(invoice_tax1.PERCENTAGE))
									.setSurchargePercent(rec.getValue(invoice_tax1.SURCHARGE)==null?0.0:rec.getValue(invoice_tax1.SURCHARGE))
									
							
							// NUEVOS CAMPOS LIBROS REGISTRO AEAT
							.setActivityType(0)  // FALTA - Actividad - Tipo							
							.setActivityIAE(rec.getValue(IAE.EPIGRAPH))  // Actividad - Epígrafe							
							.setInvoiceType("")  // FALTA - Tipo de Factura
							.setConceptType("")  // FALTA - Concepto de Ingreso/Gasto
							.setAmount(base)  // FALTA - Añadir cuota no deducible, si actividad recargo equivalencia o compensacion agraria ??
							.setInvoiceSeries(rec.getValue(INVOICE.SERIES))  // Factura - Serie
							.setInvoiceNumber(AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER))) // Factura - Número
							.setRegistryDocumentType(registryDocumentType) 
							.setRegistryDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY))
							.setOperationType(operationType)
							.setPayDate(null)  // FALTA - Cobro/Pago - Fecha
							.setPayAmount(0) // FALTA - Cobro/Pago - Amount
							.setPayMethod("")  // FALTA - Cobro/Pago - Medio de cobro/pago
							.setPayMethodName("") // FALTA - Cobro/Pago - Identificación medio de cobro/pago									
							.setRetentionPercent(rec.getValue(invoice_tax2.PERCENTAGE)==null?0.0:rec.getValue(invoice_tax2.PERCENTAGE))
							.setRetentionQuota(retentionQuota)
							;
							
						});
	}
	
}



