package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
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

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

// Para el Listado de Ventas/Ingresos y Compras/Gastos
public class OperationDAO extends FiscalModelDAO {
	
	// -------------------------------------------------------------------- STREAM FUNCTIONS
	
	// Para el Listado Ingresos/Ventas y Compras/Gastos (IRPF e IVA)
	// expenses -> true=gastos/compras, false=ventas/ingresos
	// irpf -> true=listado IRPF, false=listado IVA
	public static Stream<OperationBreakdown> getOperationBreakdownIRPF(final AONContext ctx, int domain, Date dateFrom, Date dateTo, Integer activity, boolean expenses, boolean irpf) {

		// Campos que acumulan los diferentes importes
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE);		
		// Recalculamos cuotas de IVA y REq, por que las facturas que se introducen desde gestión, no graban esas cuotas en INVOICE_TAX
		Field<BigDecimal> sumQuota = DSL.sum(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE).div(100),2));
		Field<BigDecimal> sumDeductibleQuota = DSL.sum(DSL.round(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE).div(100.0),2).mul(INVOICE_TAX.DEDUCTIBLE_PERCENT).div(100.0),2));
		Field<BigDecimal> sumSurchargeQuota = DSL.sum(DSL.round(INVOICE_TAX.BASE.mul(INVOICE_TAX.SURCHARGE).div(100),2));
		
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
		// Listado IVA: Aparecen todos los apuntes del grupo 6 o 7 que sean facturas
		Condition condition = irpf ? INVOICE.ID.isNull().or(INVOICE_TAX.PERCENTAGE.isNotNull()) : // IRPF
		                   			 INVOICE_TAX.PERCENTAGE.isNotNull(); // IVA
		
		// Condicion de la fecha:
		// Listado IRPF: Fecha del apunte
		// Listado IVA: Fecha de IVA
		Condition dateCondition = irpf ? ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo)) :  // IRPF 
			                   			 INVOICE.TAX_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo));    // IVA
		
		// Agrupamos por:
		// Listado IRPF: asiento + cuenta contable
		// Listado IVA:  asiento + cuenta contable + porcentaje de IVA
		GroupField[] groupBy = irpf ? new GroupField[]{ ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ACCOUNT } : // IRPF
		                 			  new GroupField[]{ ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ACCOUNT, INVOICE_TAX.PERCENTAGE }; // IVA		
		
		// Ordenamos por:
		// Listado IRPF: Fecha apunte + id asiento + id apunte
		// Listado IVA: Fecha IVA +  + id asiento + id apunte
		// FALTA - Este sería el orden por defecto, habría que poner los otros posibles ordenes que se van a 
		// permitir segun los filtros
		Field<?>[] orderBy = irpf ? new Field<?>[]{ACCOUNT_ENTRY.ENTRY_DATE, ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ID}:
			                        new Field<?>[]{INVOICE.TAX_DATE, ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY_DETAIL.ID};			
		
		return 	ctx.getDslContext()
				
				.select(  ENTERPRISE_ACTIVITY.DESCRIPTION						
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
						,INVOICE_TAX.PERCENTAGE
						,INVOICE_TAX.SURCHARGE						
		                )
				
		                .from(ACCOUNT_ENTRY_DETAIL)
						.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
		                .join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
		                .leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))		                
		                .leftOuterJoin(INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) 
		                .leftOuterJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		                .leftOuterJoin(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT)))		                
		                .leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL).and(INVOICE_TAX.TAX_TYPE.equal((byte)1)))
		                .leftOuterJoin(ENTERPRISE_ACTIVITY).on(ACCOUNT_ENTRY.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
		                
		                .where(ACCOUNT_ENTRY.DOMAIN.equal(domain))		                
		                .and(dateCondition)
		                .and(ACCOUNT_ENTRY.ENTRY_TYPE.notEqual((byte)2))
		                .and(ACCOUNT.CODE.startsWith(expenses?"6":"7")) // Compras/Gastos o Ventas/Ingresos 
		                .and(condition)
		                .and(ACCOUNT_ENTRY.ACTIVITY.equal(activity).or(ACCOUNT_ENTRY.ACTIVITY.isNull()))  // Actividad Null, quiere decir que el apunte o factura, se reparte entre todas las actividades
		                .groupBy(groupBy)
		                .orderBy(orderBy)
						
		                .fetch()
						.stream()
						.map( rec -> {						
							
							Integer invoice = rec.getValue(INVOICE.ID);
							String cuenta = rec.getValue(ACCOUNT.CODE);

							// El total es la suma de base + impuestos en facturas y el importe debe o haber en el resto de apuntes
							double total = 0;							
							if (invoice == null) {
								// Apuntes que no son facturas
								if (cuenta.startsWith("6"))
									total = rec.getValue(sumDebit).doubleValue() - rec.getValue(sumCredit).doubleValue();  // Compras y Gastos
								else total =  rec.getValue(sumCredit).doubleValue() - rec.getValue(sumDebit).doubleValue(); // Ventas e Ingresos
							}
							else {
								// Apuntes que son facturas
								total = AonMathUtils.round(rec.getValue(sumBase).doubleValue() + rec.getValue(sumDeductibleQuota).doubleValue() + rec.getValue(sumSurchargeQuota).doubleValue());								
							}
							
							// Facturas o apuntes que van a todas las actividades, se supone que 
							// al sacarlas en cada actividad, debe salir la parte proporcional, de 
							// forma equitativa, segun las actividades que haya (1/2, 1/3, 1/4, ...)
							double base = rec.getValue(sumBase)==null?0.0:rec.getValue(sumBase).doubleValue();
							double quota = rec.getValue(sumQuota)==null?0.0:rec.getValue(sumQuota).doubleValue();					
							double deductibleQuota = rec.getValue(sumDeductibleQuota)==null?0.0:rec.getValue(sumDeductibleQuota).doubleValue();					
							double surchargeQuota = rec.getValue(sumSurchargeQuota)==null?0.0:rec.getValue(sumSurchargeQuota).doubleValue();
							
							int count = rec.getValue(activityCount);
							
							if (count > 1) {
								base = AonMathUtils.round(base/count);
								quota = AonMathUtils.round(quota/count);
								deductibleQuota = AonMathUtils.round(deductibleQuota/count);
								surchargeQuota = AonMathUtils.round(surchargeQuota/count);
								total = AonMathUtils.round(total/count);
							}
							
							return new OperationBreakdown()
									.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))					
									.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
									.setEntryDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
									.setAccount(cuenta)
									.setAccountDescription(rec.getValue(ACCOUNT.DESCRIPTION))
									.setConcept(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
									.setTotal(total)					
									.setInvoice(rec.getValue(INVOICE.ID))
									.setDocNumber(rec.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
									.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
									.setRegistryName(rec.getValue(INVOICE.RNAME))
									.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
									.setBase(base)
									.setQuota(quota)					
									.setDeductibleQuota(deductibleQuota)					
									.setSurchargeQuota(surchargeQuota)
									.setPercent(rec.getValue(INVOICE_TAX.PERCENTAGE)==null?0.0:rec.getValue(INVOICE_TAX.PERCENTAGE))
									.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE)==null?0.0:rec.getValue(INVOICE_TAX.SURCHARGE));							
						});
					
	}
	
}

