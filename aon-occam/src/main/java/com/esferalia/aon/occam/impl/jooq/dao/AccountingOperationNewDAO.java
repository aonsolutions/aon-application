// LIBROS REGISTRO AEAT
// Libro Registro de IVA
// Libro Registro de IRPF
// Libro Registro Unificado de IVA e IRPF
// 
// Libro Registro de IVA
// Facturas contabilizadas, desglosadas por tipo de IVA y bien afecto (alquileres), cobros y pagos RECC van aparte de la factura
// 
// Libro Registro de IRPF
// Apuntes (sean facturas o no), de las cuentas de los grupos 6 y 7
//
// Libro Unificado de IVA e IRPF
// Facturas contabilizadas y apuntes de los grupos 6 y 7 que no son facturas

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
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.InvoiceTax;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdownNew;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingOperationNewDAO {
	
//	private static final byte FALSE_BYTE = 0;
	
	private static InvoiceTax retInvoiceTax = INVOICE_TAX.as("retInvoiceTax"); // Para la cuota de retención IRPF
	
	// Código de la cuenta contable, asociada a la línea de la factura. 
	// Se hace así con una subquery porque en algunas ocasiones esa tabla contiene más de un registro por cada línea de factura, 
	// aunque sea con la misma cuenta y cuando debería de contener un único registro por línea de factura
	private static Field<String> accountCode = DSL.field(DSL.select(ACCOUNT.CODE)
					     									.from(INVOICE_DETAIL_ACCOUNT)
					     									.join(ACCOUNT).on(ACCOUNT.ID.equal(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
					     									.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					     									.orderBy(INVOICE_DETAIL_ACCOUNT.ID.desc())
					     									.limit(1));
	
//	private static final Field<?>[] INVOICE_FIELDS = new Field[]{
//		 	 INVOICE.ID					,INVOICE.SERIES				,INVOICE.NUMBER		
//		 	,INVOICE.REFERENCE_CODE		,INVOICE.RDOCUMENT			,INVOICE.RDOCUMENT_TYPE		
//		 	,INVOICE.RDOCUMENT_COUNTRY	,INVOICE.RNAME				,INVOICE.ISSUE_DATE
//		 	,INVOICE.TAX_DATE			,INVOICE.TYPE				,INVOICE.RECTIFICATION_TYPE
//		 	,INVOICE.SERVICE			,INVOICE.TRANSACTION		,INVOICE.INVESTMENT
//		 	,INVOICE.WITHHOLDING_FARMER	,INVOICE.VAT_ACCRUAL_PAYMENT,INVOICE.WITHHOLDING
//			,INVOICE.RETENTION_QUOTA 	,INVOICE.REGISTRY			,INVOICE.TOTAL};
//		
//		private static final Field<?>[] INVOICE_DETAIL_FIELDS = new Field[]{
//			 INVOICE_DETAIL.TAXABLE_BASE 
//			,INVOICE_DETAIL.INVEST_ASSET 
//			,INVOICE_DETAIL.SOURCE};
//		
//		private static final Field<?>[] INVOICE_TAX_FIELDS = new Field[]{
//			 INVOICE_TAX.BASE				,INVOICE_TAX.PERCENTAGE		
//			,INVOICE_TAX.QUOTA				,INVOICE_TAX.SURCHARGE			
//			,INVOICE_TAX.SURCHARGE_QUOTA	,INVOICE_TAX.DEDUCTIBLE_PERCENT
//			,INVOICE_TAX.DEDUCTIBLE_QUOTA	,INVOICE_TAX.VAT_DEDUCTION_TYPE};
//		
//		private static final Field<?>[] INVOICE_DUA_FIELDS = new Field[]{
//			INVOICE_FISCAL.VAT_UNION, INVOICE_FISCAL.VAT_UNION_EXTERNAL, INVOICE_FISCAL.VAT_IMPORTATION, INVOICE_DUA.ID};
//
//		private static final Field<?>[] ENTERPRISE_ACTIVITY_FIELDS = new Field[]{
//			 ENTERPRISE_ACTIVITY.ID			,ENTERPRISE_ACTIVITY.DESCRIPTION
//			,ENTERPRISE_ACTIVITY.VAT_REGIME	,ENTERPRISE_ACTIVITY.SURCHARGE
//			,IAE.EPIGRAPH, IAE.SECTION
//			,ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, ACCOUNT_ENTRY.ENTRY_DATE
//			,ACCOUNT.CODE
//			,retInvoiceTax.PERCENTAGE
//			,retInvoiceTax.QUOTA
//		};
		
	private static final Field<?>[] SELECT_FIELDS = new Field[]{
		 IAE.SECTION
		,IAE.EPIGRAPH
		
		,INVOICE.TYPE
		,INVOICE.ISSUE_DATE
		,INVOICE.TAX_DATE
		,INVOICE.SERIES				
		,INVOICE.NUMBER		
	 	,INVOICE.REFERENCE_CODE		
	 	,INVOICE.RDOCUMENT_TYPE		
	 	,INVOICE.RDOCUMENT_COUNTRY
	 	,INVOICE.RDOCUMENT
	 	,INVOICE.RNAME
	 	,INVOICE.TRANSACTION
	 	,INVOICE.INVESTMENT
	 	,INVOICE.WITHHOLDING_FARMER	
	 	,INVOICE.VAT_ACCRUAL_PAYMENT
	 	,INVOICE.RECTIFICATION_TYPE
	 	
	 	,INVOICE_TAX.BASE				
	 	,INVOICE_TAX.PERCENTAGE		
		,INVOICE_TAX.QUOTA				
		,INVOICE_TAX.SURCHARGE			
		,INVOICE_TAX.SURCHARGE_QUOTA	
		,INVOICE_TAX.DEDUCTIBLE_PERCENT
		,INVOICE_TAX.DEDUCTIBLE_QUOTA	
		,INVOICE_TAX.VAT_DEDUCTION_TYPE
		
		,INVOICE_FISCAL.VAT_UNION
		,INVOICE_FISCAL.VAT_UNION_EXTERNAL
		,INVOICE_FISCAL.VAT_IMPORTATION
		
		,ACCOUNT_ENTRY.ENTRY_DATE
		
		,ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY
		
//		,ACCOUNT.CODE
		,accountCode
		
			,retInvoiceTax.PERCENTAGE
			,retInvoiceTax.QUOTA			
	};
	
	private static final Field<?>[] SELECT_FIELDS_BIS = new Field[]{
			 IAE.SECTION
			,IAE.EPIGRAPH
			
			,ACCOUNT_ENTRY.ENTRY_DATE
			
			,ACCOUNT.CODE
			
			,ACCOUNT_ENTRY_DETAIL.DEBIT
			,ACCOUNT_ENTRY_DETAIL.CREDIT
			
			,	ACCOUNT_ENTRY.JOURNAL 
			,	ACCOUNT_ENTRY_DETAIL.CONCEPT	
			,	ACCOUNT_ENTRY.ID	
			
		};
	
	private AccountingOperationNewDAO() {
		
	}
	
	public static Stream<OperationBreakdownNew> getOperationBreakdownNew(final AONContext ctx, int domain, OperationParamsNew params) {
		
//		ArrayList<OperationBreakdownNew> lista = new ArrayList<OperationBreakdownNew>();
//		for (int i=0; i<10; i++) {
//			lista.add(params.getTabType() == 0 ? prueba1() : prueba2());   
//		}
//		return lista.stream(); // PRUEBA PARA CREAR VARIAS LINEAS 
//		return Stream.empty(); // PRUEBA SIMULAR QUE NO HAY DATOS
		
		// SELECT DE FACTURAS
//		return getCommonOperationBreakdownBreakdown(ctx, params);
		
		// SELECT DE ASIENTOS QUE NO SON FACTURA
//		return getCommonOperationBreakdownBreakdownBis(ctx, params);
		
		if (params.getBookType() == 0)
			return getCommonOperationBreakdownBreakdown(ctx, params); // Libros de IVA
		else
			return Stream.concat(getCommonOperationBreakdownBreakdown(ctx, params), getCommonOperationBreakdownBreakdownBis(ctx, params))
					.sorted(Comparator.comparing(OperationBreakdownNew::getTaxDate))
					; // Libros de IRPF o UNIFICADOS
		
	}
	
	private static Stream<OperationBreakdownNew> getCommonOperationBreakdownBreakdownBis(AONContext ctx, OperationParamsNew params) {
		return getCommonSelectBis(ctx, params)
				.orderBy(ACCOUNT_ENTRY.ID) // FALTA - Ordenar por ID o por fecha
				.fetch()
				.stream()
				.map(new OperationBreakdownNewFillerBis());
	}

	private static Stream<OperationBreakdownNew> getCommonOperationBreakdownBreakdown(AONContext ctx, OperationParamsNew params) {
		return getCommonSelect(ctx, params)
//			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
//			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
//			.and(INVOICE.TYPE.equal( InvoiceType.SALES.value() )) // Facturas Emitidas
//			.and(INVOICE.TAX_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
//			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE ))	// No Criterio de Caja.
			.orderBy(InvoiceDAO.getOrderedType(), INVOICE.SERIES, INVOICE.NUMBER)
			.fetch()
			.stream()
			.map(new OperationBreakdownNewFiller());
	}
	
	private static SelectConditionStep<Record> getCommonSelect(AONContext ctx, OperationParamsNew params) {
		return getCommonSelect(ctx).where(getWhere(ctx, params));
	}
	
	private static SelectConditionStep<Record> getCommonSelectBis(AONContext ctx, OperationParamsNew params) {
		return getCommonSelectBis(ctx).where(getWhereBis(ctx, params));
	}
	
	// Facturas 
	private static SelectOnConditionStep<Record> getCommonSelect(AONContext ctx) {
		return ctx.getDslContext()
//			.select( INVOICE_FIELDS )
//			.select( INVOICE_DETAIL_FIELDS )
//			.select( INVOICE_TAX_FIELDS )
//			.select( ENTERPRISE_ACTIVITY_FIELDS )
//			.select( INVOICE_DUA_FIELDS )
//			.select( SELECT_FIELDS )
//			.from(INVOICE_TAX)
//			.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
//			.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
//			.join(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) // Facturas contabilizadas
//			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
//			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
//			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
//			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
//			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
//			.leftOuterJoin(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
//			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.equal(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
//			.leftOuterJoin(retInvoiceTax).on(retInvoiceTax.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(retInvoiceTax.TAX_TYPE.equal((byte)2)))  // Retención IRPF
			.select(SELECT_FIELDS)
			.from(INVOICE_DETAIL)
			.join(INVOICE).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.join(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) // Facturas contabilizadas
			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(retInvoiceTax).on(retInvoiceTax.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(retInvoiceTax.TAX_TYPE.equal((byte)2)))  // Retención IRPF
			;
	}
	
	private static SelectOnConditionStep<Record> getCommonSelectBis(AONContext ctx) {
		return ctx.getDslContext()
			.select(SELECT_FIELDS_BIS)
			.from(ACCOUNT_ENTRY_DETAIL)
			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.leftAntiJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID)) // Apuntes que no son facturas
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			;
	}

	
	private static Condition getWhere(AONContext ctx, OperationParamsNew params) {

		Condition condition = INVOICE_TAX.DOMAIN.equal(ctx.getDomainId())
						.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())));
		
		if (params.getActivity() != null) {
			condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
		}
		
		if (params.getTabType() == 0)
			condition = condition.and(INVOICE.TYPE.eq(InvoiceType.SALES.value())); // Ventas
		else 
			condition = condition.and(INVOICE.TYPE.ne(InvoiceType.SALES.value())); // Resto (Compras, Gastos, No deducibles)
		
		return condition;
	}
	
	private static Condition getWhereBis(AONContext ctx, OperationParamsNew params) {

		Condition condition = ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId())
						.and(ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())));
		
		if (params.getActivity() != null) {
			condition = condition.and( ACCOUNT_ENTRY.ACTIVITY.eq( params.getActivity() ));
		}
		
		if (params.getTabType() == 0)
			condition = condition.and(ACCOUNT.CODE.startsWith("7")); // Ingresos
		else 
			condition = condition.and(ACCOUNT.CODE.startsWith("6")); // Gastos
		
		return condition;
	}
	
	
	private static class OperationBreakdownNewFiller implements Function<Record,OperationBreakdownNew> {

		private boolean isSales;
		private InvoiceTransactionType invoiceTransactionType;
		private boolean isIntracommunity;
		private boolean isExtracommunity;
		private boolean isCanCeuMel;
		private boolean isIsp;
		private boolean isVatUnion;
		private boolean isNonTaxable;
		private boolean isSalesExempt;		

		@Override
		public OperationBreakdownNew apply(Record rec) {
			
//			InvoiceTransactionType 
//			InvoiceType invoiceType = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE));
//			boolean isService = (rec.getValue(INVOICE.SERVICE) == 1);
			
			isSales = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.SALES;
			invoiceTransactionType = InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION));
			isIntracommunity = (invoiceTransactionType == InvoiceTransactionType.INTRACOMMUNITY);
			isExtracommunity = (invoiceTransactionType == InvoiceTransactionType.EXTRACOMMUNITY);
			isCanCeuMel = (invoiceTransactionType == InvoiceTransactionType.CAN_CEU_MEL);
			isIsp = (invoiceTransactionType == InvoiceTransactionType.OTHER_ISP);
			isVatUnion = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION));
			isNonTaxable = (VatDeductionType.safeValueOf(rec.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)) == VatDeductionType.NON_TAXABLE) || isVatUnion; // No deducible
			isSalesExempt = isSales && (AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.PERCENTAGE)) == 0.0) && !isIsp && !isNonTaxable; // Ventas exentas
			
			String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
			String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
			String documentType = getDocumentType(rec);
			String invoiceType = getInvoiceType(rec);			
			String operationKey = getOperationKey(rec);
			String operationQualification = getOperationQualification(rec);
			String exemptOperation = getExemptOperation(rec);
			
			boolean investment = !isSales && AonEnumUtils.getBoolean(rec.getValue(INVOICE.INVESTMENT)); // Recibidas Bienes de Inversión
			boolean isp = !isSales && isIsp; // ISP Recibidas
			
//			String conceptCode = isSales ? "I01" : "G01"; // FALTA - PARA PROBAR
			double conceptAmount = rec.getValue(INVOICE_TAX.BASE); // FALTA - PARA PROBAR SE IGUALA A LA BASE, COMPROBAR SI HAY QUE HACER OTRA OPERACION
			
			return new OperationBreakdownNew()
				.setActivityCode(activityCode) 									// Actividad: Código
				.setActivityType(activityType) 									// Actividad: Tipo
				.setActivityIAE(rec.getValue(IAE.EPIGRAPH)) 					// Actividad: Grupo o Epígrafe del IAE
				.setInvoiceType(invoiceType) 									// Tipo de Factura	
//				.setConceptCode(getConceptCode(rec.getValue(ACCOUNT.CODE))) 			// Codigo Concepto de Ingreso o Gasto
				.setConceptCode(getConceptCode(rec.getValue(accountCode))) 		// Codigo Concepto de Ingreso o Gasto
				.setConceptAmount(conceptAmount) 								// Ingreso computable o Gasto deducible 	
				.setEntryDate(rec.getValue(INVOICE.ISSUE_DATE)) 				// Fecha Expedición
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))        				// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
				.setInvoiceSeries(isSales ? rec.getValue(INVOICE.SERIES) : "") 	// Identificación de la Factura: Serie (Emitidas)
				.setInvoiceNumber(isSales ? AonStringUtils.leftPad(AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER)), 6, "0") : rec.getValue(INVOICE.REFERENCE_CODE)) // Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
				.setReceptionNumber(isSales ? "" : FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)), rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) // Número recepción (Recibidas)
				.setReceptionDate(isSales ? null : rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))	// Fecha Recepción (Recibidas) (Fecha Asiento)
				.setDocumentType(documentType) 												// NIF Destinatario/Expedidor: Tipo
				.setDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY))  				// NIF Destinatario/Expedidor: Código País
				.setDocument(rec.getValue(INVOICE.RDOCUMENT)) 								// NIF Destinatario/Expedidor: Identificación
				.setName(rec.getValue(INVOICE.RNAME)) 										// Nombre Destinatario/Expedidor	
				.setOperationKey(operationKey) 												// Clave de Operación 	
				.setOperationQualification(operationQualification) 							// Calificación de la Operación (Emitidas)	
				.setExemptOperation(exemptOperation)  										// Operación Exenta (Emitidas)
				.setInvestment(investment) 													// Bien de Inversión (Recibidas)
				.setIsp(isp) 																// Inversión del Sujeto Pasivo (Recibidas)
				.setTotal(rec.getValue(INVOICE_TAX.BASE)+getQuota(rec)+getSurchargeQuota(rec))	// Total Factura (Base + IVA + REQ)	
				.setBase(rec.getValue(INVOICE_TAX.BASE))               			// Base Imponible	
				.setPercent(rec.getValue(INVOICE_TAX.PERCENTAGE))            	// Tipo de IVA	
				.setQuota(getQuota(rec))	           							// Cuota IVA Repercutido/Soportado
				.setDeductibleQuota(isSales ? 0.0 : getDeductibleQuota(rec))    // Cuota Deducible (Recibidas)
				.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE))	   	// Tipo de Recargo Eq.	
				.setSurchargeQuota(getSurchargeQuota(rec))     					// Cuota Recargo Eq.	
	//			.setpayDate() 					// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
	//			.setpayAmount() 				// Importe Cobro/Pago
	//			.setpayMethod() 				// Medio Utilizado Cobro/Pago
	//			.setpayMethodName() 			// Identificación Medio Utilizado Cobro/Pago
				.setRetentionPercent(AonNumberUtils.todouble(rec.getValue(retInvoiceTax.PERCENTAGE)))  	// Tipo Retención del IRPF	
				.setRetentionQuota(getRetentionQuota(rec))    					// Importe Retenido del IRPF	
	//			.setbuildingLocation() 			// Situación del Inmueble;	
	//			.setcadasdralReference() 		// Referencia Catastral del Inmueble
				.setEntryId(rec.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))	// ID del asiento
				// FALTA - SE PODRIA PONER COMO REFERENCIA EXTERNA EL NUMERO DE DIARIO (ASIENTO)
				;
					
		}

//		private boolean hasRetention(Byte withholding, Byte source, Double retentionQuota) {
//			boolean retention =	AonEnumUtils.getBoolean(withholding);		
//			if (retention) {
//				InvoiceSource invoiceSource = InvoiceSource.safeValueOf(source);
//				if (invoiceSource != InvoiceSource.ACCOUNT && invoiceSource != InvoiceSource.TEDI) {  // Viene de gestión
//					retention = AonMathUtils.isNotZero(retentionQuota);
//				}
//			}
//			return retention;
//		}
		

		private double getQuota(Record rec) {
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			if (AonMathUtils.isZero(quota)) {
				double base = rec.getValue(INVOICE_TAX.BASE);
				double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
		private double getSurchargeQuota(Record rec) {
			double quota = rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA);
			if (AonMathUtils.isZero(quota)) {
				double base = rec.getValue(INVOICE_TAX.BASE);
				double percent = rec.getValue(INVOICE_TAX.SURCHARGE);
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
//		private double getDeductiblePercent( Record rec) {
//			double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
//			if (AonMathUtils.isZero(percent)) percent = 100;
//			return percent;
//		}
		private double getDeductibleQuota(Record rec) {
			double dedQuota = rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
			if (AonMathUtils.isZero(dedQuota)) {
				double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
				double quota = getQuota(rec);
				if (AonMathUtils.isZero(percent) || percent == 100) {
					dedQuota = quota;
				} else {
					dedQuota = AonMathUtils.round(quota * percent / 100);
				}
			}
			return dedQuota;
		}
		
		private double getRetentionQuota(Record rec) {
			double quota = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.QUOTA));
			if (AonMathUtils.isZero(quota)) {
				double base = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.BASE));
				double percent = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.PERCENTAGE));
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
		
		// Tipo de Actividad (según el IAE) (Para el Código A) 
		//	A01	Arrendadores de bienes inmuebles								Sección 1, Agrupación 86
		//	A02	Ganadería independiente											Sección 1, Division 0
		//	A03	Resto de actividades empresariales no incluidas en A01 y A02	Resto Sección 1
		//	A04	Actividades profesionales de carácter artístico o deportivo		Sección 3
		//	A05	Restantes actividades profesionales								Sección 2
		private String getActivityType(String section, String iae) {
			
			if ("3".equals(section)) {
				return "04";
			}
			else if ("2".equals(section)) {
				return "05";
			}
			else if (AonStringUtils.isNotBlank(iae)) {
				if (iae.startsWith("86"))
					return "01";
				else if (iae.startsWith("0"))
					return "02";
				else
					return "03";
			}				
			return "";
			
		}
		
		// Tipo NIF, se hace como se hace en el SII:
		// Factura intracomunitaria: 02-NIF-IVA
		// Pasp., P.T., T.C., Otr: El valor que lleva (3, 4, 5 ó 6)
		// Resto: no lleva tipo
		private String getDocumentType(Record rec) {
			
		    String registryDocumentType = "";	
//		    Byte it = rec.getValue(INVOICE.TRANSACTION); 
			if (invoiceTransactionType != null && invoiceTransactionType == InvoiceTransactionType.INTRACOMMUNITY)
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
		
		// Tipo de Factura: Se hace igual que en el SII
		private String getInvoiceType(Record rec) {
			
			boolean isRectification = (RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)) == RectificationType.NORMAL_RECTIFIER);
			
			if (isSales && AonStringUtils.isBlank(rec.getValue(INVOICE.RDOCUMENT))) {
				return "F2"; // Facturas Emitidas: Factura sin identificación del destinatario
			} else if (isRectification) {
				return "R1"; // Facturas Emitidas y Recibidas: Rectificativas
			} else {
				return "F1"; // Facturas Emitidas y Recibidas: Resto de facturas
			}
			
		}
		
		// Clave de Operación: Se hace igual que en el SII
		private String getOperationKey(Record rec) {
			
			boolean isVatAccrualRegime = AonEnumUtils.getBoolean(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT));
			
			if (isSales) {
				// Facturas Emitidas			
				boolean isVatUnionExternal = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION_EXTERNAL));
				boolean isVatImportation = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_IMPORTATION));
				
				if (isVatUnion || isVatUnionExternal || isVatImportation) {
					return "17"; // Regimenes especiales ventanilla única 
				} else if (isVatAccrualRegime) {
					return "07"; // RECC
				} else if (isExtracommunity || isCanCeuMel) {
					return "02"; // Extracomunitaria o Ceuta/Melilla
				} else {
					return "01"; // Resto
				}
			} else {					
				// Facturas Recibidas	
				boolean isFarmer = AonEnumUtils.getBoolean(rec.getValue(INVOICE.WITHHOLDING_FARMER));
				
				if (isVatAccrualRegime) {
					return "07"; // RECC
				} else if (isIntracommunity ) {
					return "09"; // Intracomunitarias
				} else if (isFarmer) {
					return "02"; // REAGYP
				} else {
					return "01"; // Resto
				}			
			}
			
		}

		// Calificador de la Operación (solo Emitidas): Se hace igual que el SII
		private String getOperationQualification(Record rec) {
			
			if (isSales && !isSalesExempt) {
				if (isVatUnion)
					return "N2"; // Operación No Sujeta por Reglas de localización.
				else if (isNonTaxable) 
					return "N1"; // Operación No Sujeta artículo 7, 14, otros.
				 else if (isIsp) 
					return "S2"; // ISP				
				else 
					return "S1"; // Resto				
			} else {
				return "";
			}
			
		}
		
		// Operación Exenta (solo Emitidas): Se hace igual que el SII
		private String getExemptOperation(Record rec) {
			
			if (isSalesExempt) {
				if (isIntracommunity) {
					return "E5";  // Exenta por el artículo 25 
				}else if (isExtracommunity || isCanCeuMel){
					return "E2";  // Exenta por el artículo 21
				} else {
					return "E6";  // Exenta por otros
				}
			} else {
				return "";
			}
			
		}
		
		// Código de Concepto de Ingreso o Gasto
		private String getConceptCode(String value) {
			
			if (AonStringUtils.isNotBlank(value)) {
				String code = value.trim();
				if (isSales) {
					// Facturas Emitidas / Ventas e Ingresos
					if (code.startsWith("70")) 					
						return "I01"; // Ingresos de explotación (70)
					else if (code.startsWith("76"))
						return "I02"; // Ingresos financieros derivados del aplazamiento o fraccionamiento de operaciones (76)
					else if (code.startsWith("746"))
						return "I04"; // Imputación de ingresos por subvenciones de capital	(746)
					else if (code.startsWith("74"))
						return "I03"; // Ingresos por subvenciones corrientes (RESTO 74)
					else if (code.startsWith("71"))
						return "I06"; // Variación de existencias (incremento de existencias finales) (71)
					else 
						return "I07"; // Otros ingresos	(RESTO)
				} else {
					// Facturas Recibidas / Compras y Gastos
					if (code.startsWith("60"))
						return "G01"; // Compra de existencias (60)
					else if (code.startsWith("61"))
						return "G02"; // Variación de existencias (disminución de existencias finales) (61)
					else if (code.startsWith("640"))
						return "G04"; // Sueldos y salarios (640)
					else if (code.startsWith("642"))
						return "G05"; // Seguridad Social a cargo de la empresa (642)
					else if (code.startsWith("641"))
						return "G07"; // Indemnizaciones (641)
					else if (code.startsWith("64"))
						return "G10"; // Otros gastos de personal (RESTO 64)
					else if (code.startsWith("621"))
						return "G12"; // Arrendamientos y cánones (621)
					else if (code.startsWith("622"))
						return "G13"; // Reparaciones y conservación (622)
					else if (code.startsWith("628"))
						return "GY4"; // Suministros (electricidad, agua, gas, telefonía e internet) (628)
					else if (code.startsWith("623"))
						return "G19"; // Servicios de profesionales independientes (623)
					else if (code.startsWith("625"))
						return "G20"; // Primas de seguros (625)
					else if (code.startsWith("62"))
						return "G22"; // Otros servicios exteriores (RESTO 62)
					else if (code.startsWith("662"))
						return "G23"; // Intereses de deudas (662)
					else if (code.startsWith("66"))
						return "G24"; // Otros gastos financieros (RESTO 66)
					else if (code.startsWith("63"))
						return "G26"; // Otros tributos fiscalmente deducibles (63)
					else if (code.startsWith("680"))
						return "G38"; // Dotaciones del ejercicio para amortización del inmovilizado inmaterial (680)
					else if (code.startsWith("68"))
						return "GY8"; // Dotaciones del ejercicio para amortización del inmovilizado material (RESTO 68)
					else if (code.startsWith("65"))
						return "G34"; // Pérdidas por insolvencias de deudores (65)
					else
						return "G37"; // Otros conceptos fiscalmente deducibles (excepto provisiones) (RESTO)					
				}
			}
			return "";
			
		}		

	}
	
	private static class OperationBreakdownNewFillerBis implements Function<Record,OperationBreakdownNew> {

		private boolean isIncomes;
//		private InvoiceTransactionType invoiceTransactionType;
//		private boolean isIntracommunity;
//		private boolean isExtracommunity;
//		private boolean isCanCeuMel;
//		private boolean isIsp;
//		private boolean isVatUnion;
//		private boolean isNonTaxable;
//		private boolean isSalesExempt;		

		@Override
		public OperationBreakdownNew apply(Record rec) {
			
			isIncomes = rec.getValue(ACCOUNT.CODE).startsWith("7");
//			invoiceTransactionType = InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION));
//			isIntracommunity = (invoiceTransactionType == InvoiceTransactionType.INTRACOMMUNITY);
//			isExtracommunity = (invoiceTransactionType == InvoiceTransactionType.EXTRACOMMUNITY);
//			isCanCeuMel = (invoiceTransactionType == InvoiceTransactionType.CAN_CEU_MEL);
//			isIsp = (invoiceTransactionType == InvoiceTransactionType.OTHER_ISP);
//			isVatUnion = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION));
//			isNonTaxable = (VatDeductionType.safeValueOf(rec.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)) == VatDeductionType.NON_TAXABLE) || isVatUnion; // No deducible
//			isSalesExempt = isSales && (AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.PERCENTAGE)) == 0.0) && !isIsp && !isNonTaxable; // Ventas exentas
			
			String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
			String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
//			String documentType = getDocumentType(rec);
//			String invoiceType = getInvoiceType(rec);			
//			String operationKey = getOperationKey(rec);
//			String operationQualification = getOperationQualification(rec);
//			String exemptOperation = getExemptOperation(rec);
			
//			boolean investment = !isSales && AonEnumUtils.getBoolean(rec.getValue(INVOICE.INVESTMENT)); // Recibidas Bienes de Inversión
//			boolean isp = !isSales && isIsp; // ISP Recibidas
			
			double debit = AonNumberUtils.todouble(rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT));
			double credit = AonNumberUtils.todouble(rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT));
			double amount = isIncomes ? credit - debit : debit - credit;  
			
			return new OperationBreakdownNew()
				.setActivityCode(activityCode) 									// Actividad: Código
				.setActivityType(activityType) 									// Actividad: Tipo
				.setActivityIAE(rec.getValue(IAE.EPIGRAPH)) 					// Actividad: Grupo o Epígrafe del IAE
//				.setInvoiceType("") 									// Tipo de Factura	
				.setConceptCode(getConceptCode(rec.getValue(ACCOUNT.CODE))) 		// Codigo Concepto de Ingreso o Gasto
				.setConceptAmount(amount) 								// Ingreso computable o Gasto deducible 	
				.setEntryDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE)) 				// Fecha Expedición
				.setTaxDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))        				// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
//				.setInvoiceSeries("") 	// Identificación de la Factura: Serie (Emitidas)
				.setInvoiceNumber(AonNumberUtils.toString(rec.getValue(ACCOUNT_ENTRY.JOURNAL))) // Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas) // FALTA - PONEMOS EL NUMERO DE DIARIO U OTRO NUMERO O LO DEJAMOS EN BLANCO Y PONER EL NUMERO DE DIARIO COMO REFERENCIA EXTERNA, PUES REALMENTE NO ES UNA FACTURA
//				.setReceptionNumber("") // Número recepción (Recibidas)
//				.setReceptionDate(null)	// Fecha Recepción (Recibidas) (Fecha Asiento)
//				.setDocumentType("") 												// NIF Destinatario/Expedidor: Tipo
//				.setDocumentCountry("")  				// NIF Destinatario/Expedidor: Código País
//				.setDocument("") 								// NIF Destinatario/Expedidor: Identificación
				.setName(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT)) 										// Nombre Destinatario/Expedidor // FALTA - CONCEPTO DEL APUNTE O SE QUEDA EN BLANCO	
//				.setOperationKey("") 												// Clave de Operación 	
//				.setOperationQualification("") 							// Calificación de la Operación (Emitidas)	
//				.setExemptOperation("")  										// Operación Exenta (Emitidas)
//				.setInvestment(false) 													// Bien de Inversión (Recibidas)
//				.setIsp(false) 																// Inversión del Sujeto Pasivo (Recibidas)
				.setTotal(amount)	// Total Factura (Base + IVA + REQ)	
				.setBase(amount)               			// Base Imponible	
//				.setPercent(0.0)            	// Tipo de IVA	
//				.setQuota(0.0)	           							// Cuota IVA Repercutido/Soportado
//				.setDeductibleQuota(0.0)    // Cuota Deducible (Recibidas)
//				.setSurchargePercent(0.0)	   	// Tipo de Recargo Eq.	
//				.setSurchargeQuota(0.0)     					// Cuota Recargo Eq.	
	//			.setpayDate() 					// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
	//			.setpayAmount() 				// Importe Cobro/Pago
	//			.setpayMethod() 				// Medio Utilizado Cobro/Pago
	//			.setpayMethodName() 			// Identificación Medio Utilizado Cobro/Pago
//				.setRetentionPercent(0.0)  	// Tipo Retención del IRPF	
//				.setRetentionQuota(0.0)    					// Importe Retenido del IRPF	
	//			.setbuildingLocation() 			// Situación del Inmueble;	
	//			.setcadasdralReference() 		// Referencia Catastral del Inmueble
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))	// ID del asiento
				// FALTA - SE PODRIA PONER COMO REFERENCIA EXTERNA EL NUMERO DE DIARIO (ASIENTO)
				;
					
		}


//		private double getQuota(Record rec) {
//			double quota = rec.getValue(INVOICE_TAX.QUOTA);
//			if (AonMathUtils.isZero(quota)) {
//				double base = rec.getValue(INVOICE_TAX.BASE);
//				double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
//				quota = AonMathUtils.round(base * percent / 100);
//			}
//			return quota;
//		}
//		private double getSurchargeQuota(Record rec) {
//			double quota = rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA);
//			if (AonMathUtils.isZero(quota)) {
//				double base = rec.getValue(INVOICE_TAX.BASE);
//				double percent = rec.getValue(INVOICE_TAX.SURCHARGE);
//				quota = AonMathUtils.round(base * percent / 100);
//			}
//			return quota;
//		}
//		private double getDeductiblePercent( Record rec) {
//			double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
//			if (AonMathUtils.isZero(percent)) percent = 100;
//			return percent;
//		}
//		private double getDeductibleQuota(Record rec) {
//			double dedQuota = rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
//			if (AonMathUtils.isZero(dedQuota)) {
//				double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
//				double quota = getQuota(rec);
//				if (AonMathUtils.isZero(percent) || percent == 100) {
//					dedQuota = quota;
//				} else {
//					dedQuota = AonMathUtils.round(quota * percent / 100);
//				}
//			}
//			return dedQuota;
//		}
//		
//		private double getRetentionQuota(Record rec) {
//			double quota = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.QUOTA));
//			if (AonMathUtils.isZero(quota)) {
//				double base = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.BASE));
//				double percent = AonNumberUtils.todouble(rec.getValue(retInvoiceTax.PERCENTAGE));
//				quota = AonMathUtils.round(base * percent / 100);
//			}
//			return quota;
//		}
		
		// Tipo de Actividad (según el IAE) (Para el Código A) 
		//	A01	Arrendadores de bienes inmuebles								Sección 1, Agrupación 86
		//	A02	Ganadería independiente											Sección 1, Division 0
		//	A03	Resto de actividades empresariales no incluidas en A01 y A02	Resto Sección 1
		//	A04	Actividades profesionales de carácter artístico o deportivo		Sección 3
		//	A05	Restantes actividades profesionales								Sección 2
		private String getActivityType(String section, String iae) {
			
			if ("3".equals(section)) {
				return "04";
			}
			else if ("2".equals(section)) {
				return "05";
			}
			else if (AonStringUtils.isNotBlank(iae)) {
				if (iae.startsWith("86"))
					return "01";
				else if (iae.startsWith("0"))
					return "02";
				else
					return "03";
			}				
			return "";
			
		}
		
		// Tipo NIF, se hace como se hace en el SII:
		// Factura intracomunitaria: 02-NIF-IVA
		// Pasp., P.T., T.C., Otr: El valor que lleva (3, 4, 5 ó 6)
		// Resto: no lleva tipo
//		private String getDocumentType(Record rec) {
//			
//		    String registryDocumentType = "";	
////		    Byte it = rec.getValue(INVOICE.TRANSACTION); 
//			if (invoiceTransactionType != null && invoiceTransactionType == InvoiceTransactionType.INTRACOMMUNITY)
//				registryDocumentType = "02";
//			else { 
//				Byte dt = rec.getValue(INVOICE.RDOCUMENT_TYPE);
//				if (dt != null && (dt == DocumentType.PASSPORT.value() || dt == DocumentType.WORK_PERMIT.value() ||	dt == DocumentType.COMMUNITY_CARD.value() || dt == DocumentType.OTHER.value())) {
//					registryDocumentType = AonNumberUtils.toString(dt);
//					registryDocumentType = AonStringUtils.leftPad(registryDocumentType, 2, '0');
//				}
//			}
//			return registryDocumentType;
//			
//		}
		
		// Tipo de Factura: Se hace igual que en el SII
//		private String getInvoiceType(Record rec) {
//			
//			boolean isRectification = (RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)) == RectificationType.NORMAL_RECTIFIER);
//			
//			if (isIncomes && AonStringUtils.isBlank(rec.getValue(INVOICE.RDOCUMENT))) {
//				return "F2"; // Facturas Emitidas: Factura sin identificación del destinatario
//			} else if (isRectification) {
//				return "R1"; // Facturas Emitidas y Recibidas: Rectificativas
//			} else {
//				return "F1"; // Facturas Emitidas y Recibidas: Resto de facturas
//			}
//			
//		}
//		
//		// Clave de Operación: Se hace igual que en el SII
//		private String getOperationKey(Record rec) {
//			
//			boolean isVatAccrualRegime = AonEnumUtils.getBoolean(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT));
//			
//			if (isIncomes) {
//				// Facturas Emitidas			
//				boolean isVatUnionExternal = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION_EXTERNAL));
//				boolean isVatImportation = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_IMPORTATION));
//				
//				if (isVatUnion || isVatUnionExternal || isVatImportation) {
//					return "17"; // Regimenes especiales ventanilla única 
//				} else if (isVatAccrualRegime) {
//					return "07"; // RECC
//				} else if (isExtracommunity || isCanCeuMel) {
//					return "02"; // Extracomunitaria o Ceuta/Melilla
//				} else {
//					return "01"; // Resto
//				}
//			} else {					
//				// Facturas Recibidas	
//				boolean isFarmer = AonEnumUtils.getBoolean(rec.getValue(INVOICE.WITHHOLDING_FARMER));
//				
//				if (isVatAccrualRegime) {
//					return "07"; // RECC
//				} else if (isIntracommunity ) {
//					return "09"; // Intracomunitarias
//				} else if (isFarmer) {
//					return "02"; // REAGYP
//				} else {
//					return "01"; // Resto
//				}			
//			}
//			
//		}
//
//		// Calificador de la Operación (solo Emitidas): Se hace igual que el SII
//		private String getOperationQualification(Record rec) {
//			
//			if (isIncomes && !isSalesExempt) {
//				if (isVatUnion)
//					return "N2"; // Operación No Sujeta por Reglas de localización.
//				else if (isNonTaxable) 
//					return "N1"; // Operación No Sujeta artículo 7, 14, otros.
//				 else if (isIsp) 
//					return "S2"; // ISP				
//				else 
//					return "S1"; // Resto				
//			} else {
//				return "";
//			}
//			
//		}
//		
//		// Operación Exenta (solo Emitidas): Se hace igual que el SII
//		private String getExemptOperation(Record rec) {
//			
//			if (isSalesExempt) {
//				if (isIntracommunity) {
//					return "E5";  // Exenta por el artículo 25 
//				}else if (isExtracommunity || isCanCeuMel){
//					return "E2";  // Exenta por el artículo 21
//				} else {
//					return "E6";  // Exenta por otros
//				}
//			} else {
//				return "";
//			}
//			
//		}
		
		// Código de Concepto de Ingreso o Gasto
		private String getConceptCode(String value) {
			
			if (AonStringUtils.isNotBlank(value)) {
				String code = value.trim();
				if (isIncomes) {
					// Facturas Emitidas / Ventas e Ingresos
					if (code.startsWith("70")) 					
						return "I01"; // Ingresos de explotación (70)
					else if (code.startsWith("76"))
						return "I02"; // Ingresos financieros derivados del aplazamiento o fraccionamiento de operaciones (76)
					else if (code.startsWith("746"))
						return "I04"; // Imputación de ingresos por subvenciones de capital	(746)
					else if (code.startsWith("74"))
						return "I03"; // Ingresos por subvenciones corrientes (RESTO 74)
					else if (code.startsWith("71"))
						return "I06"; // Variación de existencias (incremento de existencias finales) (71)
					else 
						return "I07"; // Otros ingresos	(RESTO)
				} else {
					// Facturas Recibidas / Compras y Gastos
					if (code.startsWith("60"))
						return "G01"; // Compra de existencias (60)
					else if (code.startsWith("61"))
						return "G02"; // Variación de existencias (disminución de existencias finales) (61)
					else if (code.startsWith("640"))
						return "G04"; // Sueldos y salarios (640)
					else if (code.startsWith("642"))
						return "G05"; // Seguridad Social a cargo de la empresa (642)
					else if (code.startsWith("641"))
						return "G07"; // Indemnizaciones (641)
					else if (code.startsWith("64"))
						return "G10"; // Otros gastos de personal (RESTO 64)
					else if (code.startsWith("621"))
						return "G12"; // Arrendamientos y cánones (621)
					else if (code.startsWith("622"))
						return "G13"; // Reparaciones y conservación (622)
					else if (code.startsWith("628"))
						return "GY4"; // Suministros (electricidad, agua, gas, telefonía e internet) (628)
					else if (code.startsWith("623"))
						return "G19"; // Servicios de profesionales independientes (623)
					else if (code.startsWith("625"))
						return "G20"; // Primas de seguros (625)
					else if (code.startsWith("62"))
						return "G22"; // Otros servicios exteriores (RESTO 62)
					else if (code.startsWith("662"))
						return "G23"; // Intereses de deudas (662)
					else if (code.startsWith("66"))
						return "G24"; // Otros gastos financieros (RESTO 66)
					else if (code.startsWith("63"))
						return "G26"; // Otros tributos fiscalmente deducibles (63)
					else if (code.startsWith("680"))
						return "G38"; // Dotaciones del ejercicio para amortización del inmovilizado inmaterial (680)
					else if (code.startsWith("68"))
						return "GY8"; // Dotaciones del ejercicio para amortización del inmovilizado material (RESTO 68)
					else if (code.startsWith("65"))
						return "G34"; // Pérdidas por insolvencias de deudores (65)
					else
						return "G37"; // Otros conceptos fiscalmente deducibles (excepto provisiones) (RESTO)					
				}
			}
			return "";
			
		}		

	}
	
	
	// PRUEBA EMITIDAS
//	private static OperationBreakdownNew prueba1() {
//		Date date = AonDateUtils.getDate(2025, 7, 1);
//		Date date2 = AonDateUtils.getDate(2025, 8, 1);
//		OperationBreakdownNew op1 = new OperationBreakdownNew();
//		op1.setActivityCode("A"); 			// Actividad: Código
//		op1.setActivityType("03"); 			// Actividad: Tipo
//		op1.setActivityIAE("411.1");  			// Actividad: Grupo o Epígrafe del IAE
//		op1.setInvoiceType("F1"); 			// Tipo de Factura	
//		op1.setConceptCode("I01"); 			// Codigo Concepto de Ingreso o Gasto
//		op1.setConceptAmount(1100.0); 			// Ingreso computable o Gasto deducible 	
//		op1.setEntryDate(date); 				// Fecha Expedición
//		op1.setTaxDate(date2);        			// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
//		op1.setInvoiceSeries("A20"); 			// Identificación de la Factura: Serie (Emitidas)
//		op1.setInvoiceNumber("00001"); 			// Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
//		op1.setReceptionNumber(""); 		// Número recepción (Recibidas)
//		op1.setReceptionDate(date); 			// Fecha Recepción (Fecha Asiento)
//		op1.setDocumentType(""); 			// NIF Destinatario/Expedidor: Tipo
//		op1.setDocumentCountry("");  		// NIF Destinatario/Expedidor: Código País
//		op1.setDocument("12345678Z"); 				// NIF Destinatario/Expedidor: Identificación
//		op1.setName("CLIENTE CLIENTE, PRUEBA"); 					// Nombre Destinatario/Expedidor	
//		op1.setOperationKey("01"); 			// Clave de Operación 	
//		op1.setOperationQualification("S1"); 	// Calificación de la Operación (Emitidas)	
//		op1.setExemptOperation("");  		// Operación Exenta (Emitidas)
//		op1.setInvestment(false); 			// Bien de Inversión
//		op1.setIsp(false); 					// Inversión del Sujeto Pasivo
//		op1.setTotal(1210.0); 					// Total Factura (Base + IVA + REQ)	
//		op1.setBase(1000.0);               		// Base Imponible	
//		op1.setPercent(21.0);            		// Tipo de IVA	
//		op1.setQuota(210.0);	           		// Cuota IVA Repercutido/Soportado
//		op1.setDeductibleQuota(0.0);    		// Cuota Deducible (Recibidas)
//		op1.setSurchargePercent(0.0);	   	// Tipo de Recargo Eq.	
//		op1.setSurchargeQuota(0.0);     		// Cuota Recargo Eq.	
//		op1.setPayDate(null); 					// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
//		op1.setPayAmount(0.0); 				// Importe Cobro/Pago
//		op1.setPayMethod(""); 				// Medio Utilizado Cobro/Pago
//		op1.setPayMethodName(""); 			// Identificación Medio Utilizado Cobro/Pago
//		op1.setRetentionPercent(0.0);   		// Tipo Retención del IRPF	
//		op1.setRetentionQuota(0.0);    		// Importe Retenido del IRPF	
//		op1.setBuildingLocation("1"); 		// Situación del Inmueble;	
//		op1.setCadasdralReference("9872023VH5797S0001WX"); 		// Referencia Catastral del Inmueble
//		op1.setEntryId(0);                // ID del apunte
//		return op1;
//	}
	
	// PRUEBA RECIBIDAS
//	private static OperationBreakdownNew prueba2() {
//		Date date = AonDateUtils.getDate(2025, 7, 1);
//		Date date2 = AonDateUtils.getDate(2025, 8, 1);
//		OperationBreakdownNew op1 = new OperationBreakdownNew();
//		op1.setActivityCode("A"); 			// Actividad: Código
//		op1.setActivityType("03"); 			// Actividad: Tipo
//		op1.setActivityIAE("411.1");  			// Actividad: Grupo o Epígrafe del IAE
//		op1.setInvoiceType("F1"); 			// Tipo de Factura	
//		op1.setConceptCode("G01"); 			// Codigo Concepto de Ingreso o Gasto
//		op1.setConceptAmount(1200.0); 			// Ingreso computable o Gasto deducible 	
//		op1.setEntryDate(date); 				// Fecha Expedición
//		op1.setTaxDate(date2);        			// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
//		op1.setInvoiceSeries(""); 			// Identificación de la Factura: Serie (Emitidas)
//		op1.setInvoiceNumber("25H00001"); 			// Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
//		op1.setReceptionNumber("R2025/00001"); 		// Número recepción (Recibidas)
//		op1.setReceptionDate(date); 			// Fecha Recepción (Fecha Asiento)
//		op1.setDocumentType(""); 			// NIF Destinatario/Expedidor: Tipo
//		op1.setDocumentCountry("");  		// NIF Destinatario/Expedidor: Código País
//		op1.setDocument("B50111111"); 				// NIF Destinatario/Expedidor: Identificación
//		op1.setName("PROVEEDOR PROVEEDOR, S.L."); 					// Nombre Destinatario/Expedidor	
//		op1.setOperationKey("01"); 			// Clave de Operación 	
//		op1.setOperationQualification(""); 	// Calificación de la Operación (Emitidas)	
//		op1.setExemptOperation("");  		// Operación Exenta (Emitidas)
//		op1.setInvestment(false); 			// Bien de Inversión
//		op1.setIsp(false); 					// Inversión del Sujeto Pasivo
//		op1.setTotal(1210.0); 					// Total Factura (Base + IVA + REQ)	
//		op1.setBase(1000.0);               		// Base Imponible	
//		op1.setPercent(21.0);            		// Tipo de IVA	
//		op1.setQuota(210.0);	           		// Cuota IVA Repercutido/Soportado
//		op1.setDeductibleQuota(205.0);    		// Cuota Deducible (Recibidas)
//		op1.setSurchargePercent(0.0);	   	// Tipo de Recargo Eq.	
//		op1.setSurchargeQuota(0.0);     		// Cuota Recargo Eq.	
//		op1.setPayDate(null); 					// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
//		op1.setPayAmount(0.0); 				// Importe Cobro/Pago
//		op1.setPayMethod(""); 				// Medio Utilizado Cobro/Pago
//		op1.setPayMethodName(""); 			// Identificación Medio Utilizado Cobro/Pago
//		op1.setRetentionPercent(15.0);   		// Tipo Retención del IRPF	
//		op1.setRetentionQuota(150.0);    		// Importe Retenido del IRPF	
//		op1.setBuildingLocation("4"); 		// Situación del Inmueble;	
//		op1.setCadasdralReference(""); 		// Referencia Catastral del Inmueble
//		op1.setEntryId(0);                // ID del apunte
//		return op1;
//	}
	
}