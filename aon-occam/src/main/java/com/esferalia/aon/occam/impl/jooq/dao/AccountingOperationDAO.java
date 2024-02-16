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
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

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
import com.esferalia.aon.watson.util.Pair;

public class AccountingOperationDAO {
	
	private AccountingOperationDAO() {
		
	}
	private static final Field<Integer> OP_ID = ACCOUNT_ENTRY.ID.as("accountingEntryID");
	private static final Field<Integer> OP_DOMAIN = ACCOUNT_ENTRY.DOMAIN.as("accountingEntryDomain");
	private static final Field<Byte> OP_TYPE = ACCOUNT_ENTRY.ENTRY_TYPE.as("accountingEntryType");
	private static final Field<Integer> OP_JOURNAL = ACCOUNT_ENTRY.JOURNAL.as("accountingEntryJournal");
	private static final Field<Integer> OP_ACTIVITY = ACCOUNT_ENTRY.ACTIVITY.as("accountingEntryActivity");
	private static final Field<java.sql.Date> OP_DATE = ACCOUNT_ENTRY.ENTRY_DATE .as("accountingEntryDate");
	private static final Field<UInteger> OP_DETAIL_LINE = ACCOUNT_ENTRY_DETAIL.LINE.as("accountingDetailLine");
	private static final Field<String> OP_DETAIL_CONCEPT = ACCOUNT_ENTRY_DETAIL.CONCEPT.as("accountingDetailConcept");		                 
	private static final Field<String> OP_DETAIL_DOCUMENT_NUMBER = ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER.as("accountingDetailNumber");
	private static final Field<BigDecimal> OP_DETAIL_DEBIT = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT).as("accountingDetailDebit");
	private static final Field<BigDecimal> OP_DETAIL_CREDIT = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT).as("accountingDetailCredit");
	private static final Field<Integer> OP_DETAIL_ACC_ID = ACCOUNT.ID.as("accountId");
	private static final Field<String> OP_DETAIL_ACC_CODE = ACCOUNT.CODE.as("accountCode");
	private static final Field<String> OP_DETAIL_ACC_DESCRIPTION = ACCOUNT.DESCRIPTION.as("accountDescription");
	
	private static InvoiceTax vatInvoiceTax = INVOICE_TAX.as("vatInvoiceTax"); // Para la cuota de IVA y REQ
	private static InvoiceTax retInvoiceTax = INVOICE_TAX.as("retInvoiceTax"); // Para la cuota de retención IRPF		
	private static Field<BigDecimal> sumBase = DSL.sum(vatInvoiceTax.BASE);
	private static Field<BigDecimal> sumQuota = DSL.sum(DSL.when(vatInvoiceTax.QUOTA.eq(0.0),DSL.round(vatInvoiceTax.BASE.mul(vatInvoiceTax.PERCENTAGE).div(100),2)).otherwise(vatInvoiceTax.QUOTA));
	private static Field<BigDecimal> sumDeductibleQuota = DSL.sum(DSL.when(vatInvoiceTax.DEDUCTIBLE_QUOTA.eq(0.0),DSL.round(DSL.round(vatInvoiceTax.BASE.mul(vatInvoiceTax.PERCENTAGE).div(100.0),2).mul(vatInvoiceTax.DEDUCTIBLE_PERCENT).div(100.0),2)).otherwise(vatInvoiceTax.DEDUCTIBLE_QUOTA));
	private static Field<BigDecimal> sumSurchargeQuota = DSL.sum(DSL.when(vatInvoiceTax.SURCHARGE_QUOTA.eq(0.0),DSL.round(vatInvoiceTax.BASE.mul(vatInvoiceTax.SURCHARGE).div(100),2)).otherwise(vatInvoiceTax.SURCHARGE_QUOTA));
	private static Field<BigDecimal> sumRetentionQuota = DSL.sum(DSL.when(retInvoiceTax.QUOTA.eq(0.0),DSL.round(retInvoiceTax.BASE.mul(retInvoiceTax.PERCENTAGE).div(100),2)).otherwise(retInvoiceTax.QUOTA));
	private static Field<String> conceptType = DSL				
		// Conceptos de Ingreso
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 1).eq("7").and(INVOICE.ID.isNull()),"IX1") // IX1 - Otros Ingresos (incluidas subvenciones y otras transferencias) - Apuntes sin factura				
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 1).eq("7"),"I00") // I01 - Ingresos de explotación - Facturas
		
		// Conceptos de Gasto
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("640"),"G04") // G04 - Sueldos y salarios - 640
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("642"),"GX2") // GX2 - Seguridad Social a cargo de la empresa (incluidas las cotizaciones del titular) - 642				
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("621"),"G12") // G12 - Arrendamientos y cánones - 621
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("622"),"G13") // G13 - Reparaciones y conservación - 622
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("628"),"GX4") // GX4 - Suministros (entre otros agua, gas, electricidad, telefonía, internet) - 628
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 3).eq("623"),"G19") // G19 - Servicios de profesionales independientes - 623
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("60").or(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("61")),"GX1") // GX1 - Consumos de explotación - 60, 61
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("64"),"GX3") // GX3 - Otros gastos de personal - RESTO 64
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("62"),"GX5") // GX5 - Otros servicios exteriores - RESTO 62
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("66"),"GX6") // GX6 - Gastos financieros - 66
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("63"),"GX7") // GX7 - Tributos fiscalmente deducibles - 63, IVA,REQ,REAGYP NO DED.
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("68"),"GX8") // GX8 - Amortizaciones: dotaciones del ejercicio fiscalmente deducibles - 68
		.when(DSL.substring(OP_DETAIL_ACC_CODE,1, 2).eq("65"),"G34") // G34 - Pérdidas por insolvencias de deudores - 65
		.otherwise("G37") // G37 - Otros conceptos fiscalmente deducibles (excepto provisiones) - RESTO
	;
	
	public static Stream<OperationBreakdown> getOperationBreakdown(final AONContext ctx, int domain, OperationParams params) {
		
		if ( !isActivityEnabledForReport(ctx,params)) {
			return Stream.empty();
		}
		// Numero de actividades de la empresa en la fecha del apunte o fecha de IVA (se usará en los apuntes imputados a todas las actividades -actividad es null-, en empresas que tengan mas de una actividad)
		Field<java.sql.Date> date = params.isIrpf() ? OP_DATE:INVOICE.TAX_DATE;
		Field<Integer> activityCount =DSL.selectCount()
			.from(ENTERPRISE_ACTIVITY)
			.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain))
			.and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(date)))
			.and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(date)))
			.asField("activityCount");
					
		// Condición para que aparezcan los diferentes apuntes:
		// Listado IRPF: Aparecen todos los apuntes del grupo 6 (compras y gastos) o 7 (ventas e ingresos)
		// Listado IVA: Aparecen todos los apuntes que sean facturas
		// Condicion de la fecha:
		// Listado IRPF: Fecha del apunte
		// Listado IVA: Fecha de IVA
		Condition condition = getCondition( params );					
         			                 					
		
		// Condicion de la tabla INVOICE_DETAIL_ACCOUNT
		// Se pone de esta forma porque se ha detectado que esta tabla, por algunos fallos de grabacion de las
		// facturas de gestión, tiene mas de un registro por linea de factura, cuando debería tener solo un registro
		// por lo tanto si lo hacemos con un JOIN, se duplican (o multiplican), los importes
		Condition conditionIDA = 
				INVOICE.ID.isNull().orExists(ctx.getDslContext()
						.select(INVOICE_DETAIL_ACCOUNT.ID)
						.from(INVOICE_DETAIL_ACCOUNT)
						.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID)
						  .and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.equal(OP_DETAIL_ACC_ID)))); 
		
		// Agrupamos por:
		// Listado IRPF: asiento + cuenta contable
		// Listado IVA:  asiento + porcentaje de IVA
		// Libro Registro AEAT (IRPF): asiento + concepto + porcentaje_iva
		GroupField[] groupBy = getGroupBy( params );
				
		// Ordenamos por:
		// Listado IRPF: Fecha apunte + nº documento + numero asiento + linea apunte
		// Listado IVA: Fecha IVA + nº documento + nº asiento + linea apunte
		Field<?>[] orderBy = getOrderBy( params );
		
		Table<Record14<Integer,Integer,Integer,Byte,Integer,java.sql.Date,UInteger,String,String,BigDecimal,BigDecimal,Integer,String,String>> 
			accountingSelect = DSL.select(  
					  OP_ID
					, OP_DOMAIN
					, OP_JOURNAL
					, OP_TYPE
					, OP_ACTIVITY
					, OP_DATE 
					, OP_DETAIL_LINE
					, OP_DETAIL_CONCEPT
					, OP_DETAIL_DOCUMENT_NUMBER 
					, OP_DETAIL_DEBIT
					, OP_DETAIL_CREDIT
					, OP_DETAIL_ACC_ID
					, OP_DETAIL_ACC_CODE
					, OP_DETAIL_ACC_DESCRIPTION
					)
				.from(ACCOUNT_ENTRY_DETAIL)
				.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq( domain ))
				.and(params.isIrpf()
					?(ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(params.getFromDate()),AonDateUtils.toSql(params.getToDate())).and(ACCOUNT.CODE.startsWith(params.getAccountPrefix())))
					:DSL.trueCondition()
						)
				.groupBy(OP_ID,OP_DETAIL_ACC_ID)
				.asTable("accounting");

		// Obtenemos los datos
        return ctx.getDslContext()
    		.select(accountingSelect.fields())
			.select(
				  INVOICE.ID
                , INVOICE.TYPE
                , INVOICE.TAX_DATE
				, INVOICE.REFERENCE_CODE 
				, INVOICE.RDOCUMENT
				, INVOICE.RNAME												                
				, sumBase
				, sumQuota
				, sumDeductibleQuota
				, sumSurchargeQuota
				, activityCount
				, vatInvoiceTax.PERCENTAGE
				, vatInvoiceTax.SURCHARGE
				
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
				, retInvoiceTax.PERCENTAGE
				, sumRetentionQuota
				, INVOICE.TRANSACTION
				, conceptType
				, INVOICE_DUA.ID
				, INVOICE_TAX_ACCOUNT.ACCOUNT
                )				
                .from(accountingSelect)
                .leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(OP_ID))		                
                .leftOuterJoin(INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) 
                .leftOuterJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
                
                .leftOuterJoin(vatInvoiceTax)	// Importes IVA y REq
                	.on(	 vatInvoiceTax.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID)
                		.and(vatInvoiceTax.TAX_TYPE.equal((byte)1)))  
                	
                .leftOuterJoin(INVOICE_TAX_ACCOUNT).on(
                		INVOICE_TAX_ACCOUNT.INVOICE_TAX.equal(vatInvoiceTax.ID)
                		.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(OP_DETAIL_ACC_ID))
                		)
                .leftOuterJoin(retInvoiceTax).on(retInvoiceTax.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(retInvoiceTax.TAX_TYPE.equal((byte)2)))  // Retención IRPF
                
                //.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(params.getActivity()))
                .leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal( OP_ACTIVITY ))
                .leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
                
                .leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
                
                .where(OP_DOMAIN.equal(domain))		                
                .and(condition)
                .and(conditionIDA)
                .and(
	                params.getActivity() == null 
	                	? DSL.trueCondition()
            			: OP_ACTIVITY.equal(params.getActivity()).or(OP_ACTIVITY.isNull())
            		)  // Actividad null, quiere decir que el apunte o factura, se reparte entre todas las actividades		                
                .groupBy(groupBy)
                .orderBy(orderBy)
                .fetch()
				.stream()
				.map(rec -> new Pair<Record,OperationBreakdown>(rec,new OperationBreakdown()))
				.map(pair -> {
					Record rec = pair.getLeft();
					pair.getRight()
						.setEntryId(rec.getValue(OP_ID))
						.setEntryDate(rec.getValue(OP_DATE))
						.setAccount(rec.getValue(OP_DETAIL_ACC_CODE))
						.setAccountDescription(rec.getValue(OP_DETAIL_ACC_DESCRIPTION))
						.setConcept(rec.getValue(OP_DETAIL_CONCEPT))
						.setInvoice(rec.getValue(INVOICE.ID))
						.setDocNumber(rec.getValue(OP_DETAIL_DOCUMENT_NUMBER))
						.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
						.setRegistryName(rec.getValue(INVOICE.RNAME))
						.setTaxDate(Objects.requireNonNullElse(rec.getValue(INVOICE.TAX_DATE), rec.getValue(OP_DATE)))
						.setPercent(Objects.requireNonNullElse(rec.getValue(vatInvoiceTax.PERCENTAGE), 0.0))
						.setSurchargePercent(Objects.requireNonNullElse(rec.getValue(vatInvoiceTax.SURCHARGE),0.0))
						// Nuevos datos para el Libro Registro AEAT
						
						.setActivityType(getActivityType(rec.getValue(IAE.SECTION),rec.getValue(IAE.EPIGRAPH))) // Actividad - Tipo							
						.setActivityIAE(AonStringUtils.trimToEmpty(rec.getValue(IAE.EPIGRAPH)))					// Actividad - Epígrafe
						
						.setInvoiceType(getInvoiceType( rec ))                 // Tipo de Factura									
						.setConceptType(rec.getValue(conceptType))   // Concepto de Ingreso/Gasto
						.setInvoiceSeries(rec.getValue(INVOICE.SERIES))  // Factura - Serie							 
						.setInvoiceNumber(params.isExpenses() 
								? rec.getValue(INVOICE.REFERENCE_CODE) 
								: AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER))) // Factura - Número
						.setRegistryDocumentType(getRegistryDocumentType(rec))   // Tipo NIF 
						.setRegistryDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY))  // Pais
						.setOperationType(getOperationType(rec))   	// Tipo de Operación
						.setPayDate(null)                   // Cobro/Pago - Fecha (Los cobros y pagos se obtienen al crear el libro)
						.setPayAmount(0) 					// Cobro/Pago - Amount
						.setPayMethod("")  					// Cobro/Pago - Medio de cobro/pago
						.setPayMethodName("") 				// Cobro/Pago - Identificación medio de cobro/pago									
						.setRetentionPercent(Objects.requireNonNullElse(rec.getValue(retInvoiceTax.PERCENTAGE), 0.0));
					return pair;
				})
				.map( pair -> {
					Record rec = pair.getLeft();
					
					double base = AonNumberUtils.todouble(rec.getValue(sumBase));
					double quota = AonNumberUtils.todouble(rec.getValue(sumQuota));
					double deductibleQuota = AonNumberUtils.todouble(rec.getValue(sumDeductibleQuota));
					double surchargeQuota = AonNumberUtils.todouble(rec.getValue(sumSurchargeQuota));
					double retentionQuota = AonNumberUtils.todouble(rec.getValue(sumRetentionQuota));
					double total = 0; // El total es la suma de base + impuestos en facturas y el importe debe o haber en el resto de apuntes

					// Apuntes que no son facturas (base y total coinciden) o facturas UNDEDUCTIBLE
					if (pair.getRight().getInvoice() == null || AonStringUtils.equals("F2",pair.getRight().getInvoiceType())) {
						base = getAccountEntryBalance( pair );
						total = base;
					} else {
						// Apuntes que son facturas (total es base + iva + recargo_equivalencia (no se tiene en cuenta la retencion)
						total = AonMathUtils.round(base + deductibleQuota + surchargeQuota);
						
						// En facturas no nos podemos fiar de lo que viene en deductibleQuota, porque parece ser que ese dato no es 
						// posible grabarlo en la factura en estos momentos en determinados asientos de facturas, cuando es un gasto 
						// por ejemplo con IVA no deducible, incluso en las facturas de gestión, no está grabada ni siquiera la cuota 
						// de IVA, asi que se hace por ahora que si no hay cuenta de IVA o si la cuenta de IVA es la misma que la de gasto
						// se asume que el IVA no es deducible
						if (rec.getValue(INVOICE.TYPE) == InvoiceType.PURCHASE.value() 
							|| rec.getValue(INVOICE.TYPE) == InvoiceType.EXPENSES.value()) {
							Integer idTaxAccount = rec.getValue(INVOICE_TAX_ACCOUNT.ACCOUNT);
							if (idTaxAccount == null || idTaxAccount.intValue() == rec.getValue(OP_DETAIL_ACC_ID).intValue()) {
								deductibleQuota = 0;
							}									
						}								
					}
					
					Integer act = rec.getValue(OP_ACTIVITY);
					int count = rec.getValue(activityCount);
					
					// Facturas o apuntes que van a todas las actividades (activity=null),  
					// al sacarlas en cada actividad, debe salir la parte proporcional, de 
					// forma equitativa, segun las actividades que haya (1/2, 1/3, 1/4, ...)
					if (params.getActivity() != null && act == null && count > 1) {
						base = AonMathUtils.round(base/count);
						quota = AonMathUtils.round(quota/count);
						deductibleQuota = AonMathUtils.round(deductibleQuota/count);
						surchargeQuota = AonMathUtils.round(surchargeQuota/count);
						total = AonMathUtils.round(total/count);
						retentionQuota = AonMathUtils.round(retentionQuota/count);
					}
					// Ingreso computable/Gasto deducible (Libros Registro AEAT)
					// base + iva no deducible
					double amount = base + (quota-deductibleQuota);
					
					// Completar todos los datos
					return pair.getRight()
						.setTotal(total)
						.setBase(base)
						.setQuota(quota)					
						.setDeductibleQuota(deductibleQuota)					
						.setSurchargeQuota(surchargeQuota)
						.setAmount(amount)
						.setRetentionQuota(retentionQuota) 
							;
				});
	}
	
	private static double getAccountEntryBalance(Pair<Record, OperationBreakdown> pair) {
		Record rec = pair.getLeft();
		double debit = AonNumberUtils.todouble(rec.getValue(OP_DETAIL_DEBIT));
		double credit = AonNumberUtils.todouble(rec.getValue(OP_DETAIL_CREDIT));
		return (AonStringUtils.startsWith(pair.getRight().getAccount(), "6")) 
			? debit - credit   // Compras y Gastos
			: credit - debit; // Ventas e Ingresos
	}

	private static String getOperationType(Record rec) {
		// Tipo de Operación (Libros Registro AEAT)
		String operationType = "";
		if (rec.getValue(INVOICE.ID) != null) {
			if (rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
				operationType = "07";  // RECC
			else if (rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
					 operationType = "02";	// REAGYP							
		}							
		return operationType;
	}

	/**
	 * Tipo NIF (Libros Registro AEAT)
	 * Se hace como se hace en el SII:
	 * Factura intracomunitaria: 02-NIF-IVA
	 * Pasp., P.T., T.C., Otr: El valor que lleva (3, 4, 5 ó 6)
	 * Resto: no lleva tipo
	 * 
	 * @param rec
	 * @return
	 */
	private static String getRegistryDocumentType(Record rec) {
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
		return registryDocumentType;
	}

	private static String getInvoiceType(Record rec) {
		String invoiceType = "SF";  // Apuntes sin factura
		if (rec.getValue(INVOICE.ID) != null) {
			byte rectificationType = rec.getValue(INVOICE.RECTIFICATION_TYPE);
			if (rectificationType == RectificationType.NORMAL_RECTIFIER.value() || rectificationType == RectificationType.SPECIAL_RECTIFIER.value()) {
				invoiceType = "R0";  // Rectificativa
			} else if (rec.getValue(INVOICE_DUA.ID) != null) {
				invoiceType = "F5"; // DUA
			} else if (rec.getValue(INVOICE.TYPE) == InvoiceType.UNDEDUCTIBLE.value()) {
				invoiceType = "F2"; // Factura sin identificación del destinatario (tickets, estan como no deducibles)
			} else {
				invoiceType = "F1";  // Resto
			}
		}
		return invoiceType;
	}

	private static Field<?>[] getOrderBy(OperationParams params) {
		return params.isIrpf()
			? new Field<?>[]{OP_DATE, OP_DETAIL_DOCUMENT_NUMBER, OP_JOURNAL, OP_DETAIL_LINE}
			: new Field<?>[]{OP_DETAIL_DOCUMENT_NUMBER, INVOICE.TAX_DATE, OP_JOURNAL, OP_DETAIL_LINE};
	}

	private static GroupField[] getGroupBy(OperationParams params) {
		GroupField[] groupBy = new GroupField[]{ OP_ID, vatInvoiceTax.PERCENTAGE };
		if (params.getAeatBook() && params.isIrpf()) {
			groupBy = new GroupField[]{ OP_ID, conceptType, vatInvoiceTax.PERCENTAGE };
		} else if (params.isIrpf()) {
			groupBy = new GroupField[]{ OP_ID, OP_DETAIL_ACC_ID };
		}
		return groupBy;
	}

	private static Condition getCondition(OperationParams params) {
		Condition condition = OP_TYPE.notEqual(AccountEntryType.OPERATING.getValue());
		if (params.isIrpf()) {
			if (params.getAeatBook()) {
				condition = condition.and(INVOICE.ID.isNull().or(vatInvoiceTax.ID.isNotNull()));
			}
		}
		else {			
			condition = condition
				.and( INVOICE.ID.isNotNull() )
				.and( INVOICE.TAX_DATE.between(AonDateUtils.toSql(params.getFromDate()),AonDateUtils.toSql(params.getToDate())));
			if (params.isExpenses()) {
				condition = condition.and(vatInvoiceTax.ID.isNotNull())
						.and(INVOICE.TYPE.in(InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value())); // Listado IVA (Compras)
			} else {
				condition = condition.and(INVOICE.TYPE.equal(InvoiceType.SALES.value())); // Listado IVA (Ventas)		
			}
		}
		return condition;
	}

	private static boolean isActivityEnabledForReport(AONContext ctx, OperationParams params) {
		// Condicion para que salgan datos según el régimen de la actividad
		// Listado IRPF: No salen datos, si la actividad está en Regimen de IRPF exento
		// Listado IVA: No salen datos, si la actividad está en Regimen de IVA exento o en Recargo de Equivalencia
		Condition activityCondition;					
		if (params.isIrpf()) {			
			activityCondition = ENTERPRISE_ACTIVITY.RETENTION_REGIME.isNull().or(ENTERPRISE_ACTIVITY.RETENTION_REGIME.notEqual(IRPFRegime.EXEMPT.value())); // Listado IRPF
		} else {
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
			return false;
		}		
		return true;
	}
	
	private static String getActivityType(String section, String iae) {
		// Tipo de Actividad (según el IAE) (Libros Registro AEAT)
		// 1 - Actividades empresariales de carácter mercantil - Resto de Epígrafes IAE
		// 2 - Actividades agrícolas y ganaderas - Sección 1, Division 0 
		// 3 - Otras actividades empresariales de carácter no mercantil	- No contemplado en AON
		// 4 - Actividades profesionales de carácter artístico o deportivo - Sección 3
		// 5 - Restantes actividades profesionales - Sección 2
		String activityType = "1";
		if (AonStringUtils.isEmpty(section)) return activityType;
		if (iae == null) iae = "";
		if (section.equals("1") && iae.length() >= 1 && iae.startsWith("0"))
			activityType = "2";
		else if (section.equals("2"))
			activityType = "5";
		else if (section.equals("3"))
			activityType = "4";
		return activityType;
	}
	
	
}
