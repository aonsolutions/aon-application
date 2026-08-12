// LIBROS REGISTRO AEAT
// - Libro Registro de IVA: Facturas contabilizadas, desglosadas por tipo de IVA y bien afecto (alquileres), cobros y pagos RECC van aparte de la factura
// - Libro Registro de IRPF: Apuntes (sean facturas o no), de las cuentas de los grupos 6 y 7
// - Libro Unificado de IVA e IRPF: Facturas contabilizadas y apuntes de los grupos 6 y 7 que no son facturas

package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.EnterpriseActivity;
import com.esferalia.aon.jooq.tables.FsModelDetail;
import com.esferalia.aon.jooq.tables.Iae;
import com.esferalia.aon.jooq.tables.InvoiceTax;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdownNew;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class AccountingOperationNewDAO {
	
	private static final byte TRUE_BYTE = 1;
	private static InvoiceTax retInvoiceTax = INVOICE_TAX.as("retInvoiceTax"); // Para la cuota de retención IRPF
	
	private static EnterpriseActivity allEnterpriseActivity = ENTERPRISE_ACTIVITY.as("allEnterpriseActivity");
	private static Iae otherIae = IAE.as("otherIae");

	// Códigos de concepto donde es obligatorio identificar al destinatario/expedidor, se usa en los asientos sin factura para poner el NIF y Nombre de la empresa
	private static final String[] REQUIRED_CONCEPTS = {"I07","G01","G03","G04","G05","G45","G46","G07","G08","G09","G10","G11","G12","G13","GY4","G14","G15","G16","G17","G18","G19","G40","G41","G42","G20","G22","G44","G23","G24","G25","G26","G34","G35","G36","G43","G37"};
	
	// Código de la cuenta contable, asociada a la línea de la factura. 
	// Se hace así con una subquery porque en algunas ocasiones la tabla INVOICE_DETAIL_ACCOUNT contiene más de un registro 
	// por cada línea de factura, aunque sea con la misma cuenta y debería contener un único registro por línea de factura
	private static Field<String> accountCodeField = DSL.field(DSL.select(ACCOUNT.CODE)
					     									.from(INVOICE_DETAIL_ACCOUNT)
					     									.join(ACCOUNT).on(ACCOUNT.ID.equal(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
					     									.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					     									.orderBy(INVOICE_DETAIL_ACCOUNT.ID.desc())
					     									.limit(1));
	
	// Prorrata de IVA
	private static Field<Double> proratePercentageField = DSL.field(DSL.select(FS_MODEL_DETAIL.AMOUNT)
				.from(FS_MODEL_DETAIL)
				.join(ALCATRAZ).on(ALCATRAZ.FS_MODEL.equal(FS_MODEL_DETAIL.FS_MODEL))
				.where(ALCATRAZ.INVOICE.eq(INVOICE.ID)).and(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.CM_003.getValue())) // Porcentaje de prorrata
				.orderBy(FS_MODEL_DETAIL.ID.desc())
				.limit(1));
	private static Field<String> prorateTypeField = DSL.field(DSL.select(FS_MODEL_DETAIL.DESCRIPTION)
				.from(FS_MODEL_DETAIL)
				.join(ALCATRAZ).on(ALCATRAZ.FS_MODEL.equal(FS_MODEL_DETAIL.FS_MODEL))
				.where(ALCATRAZ.INVOICE.eq(INVOICE.ID)).and(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.CM_006.getValue())) // Tipo de prorrata
				.orderBy(FS_MODEL_DETAIL.ID.desc())
				.limit(1));
	private static Field<Double> prorateField = DSL.field(DSL.select(FS_MODEL_DETAIL.AMOUNT)
				.from(FS_MODEL_DETAIL)
				.join(FS_MODEL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
				.join(ALCATRAZ).on(ALCATRAZ.FS_MODEL.equal(FS_MODEL_DETAIL.FS_MODEL))
				.where(ALCATRAZ.INVOICE.eq(INVOICE.ID)).and(FS_MODEL.YEAR.ge(2026)).and(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.CM_008.getValue())) // Aplicar prorrata (desde 2026)
				.orderBy(FS_MODEL_DETAIL.ID.desc())
				.limit(1));
		
	// Facturas
	private static final Field<?>[] SELECT_FIELDS_FAC = new Field[] {
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
	 	,INVOICE.ACTIVITY
	 	,INVOICE_DETAIL.TAXABLE_BASE
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
		,ACCOUNT_ENTRY.ID
		,ACCOUNT_ENTRY.JOURNAL
		,ACCOUNT_ENTRY.ENTRY_DATE
		,accountCodeField
		,retInvoiceTax.PERCENTAGE
		,retInvoiceTax.QUOTA
		,retInvoiceTax.WITHHOLDING_TYPE
		,INVEST_ASSET.TYPE
		,INVEST_ASSET.REGIME		
		,INVEST_ASSET.PROPERTIES		
		,INVEST_ASSET.RETENTION_PERCENT
		,proratePercentageField
		,prorateTypeField
		,prorateField
		,allEnterpriseActivity.VAT_REGIME
		,otherIae.SECTION
		,otherIae.EPIGRAPH	
	};
	
	// Cobros/Pagos RECC
	private static final Field<?>[] SELECT_FIELDS_RECC = new Field[] {
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
	 	,INVOICE.VAT_ACCRUAL_PAYMENT
	 	,INVOICE.RECTIFICATION_TYPE
		,ACCOUNT_ENTRY.ID
		,ACCOUNT_ENTRY.JOURNAL
		,ACCOUNT_ENTRY.ENTRY_DATE  
		,FINANCE.AMOUNT
		,FINANCE_TRACKING.AMOUNT
		,FINANCE_TRACKING.TRACKING_DATE
		,PAY_METHOD.TYPE
		,PAY_METHOD.NAME
		,RBANK.BANK_ACCOUNT
	};
	
	// Asientos
	private static final Field<?>[] SELECT_FIELDS_AST = new Field[] {
		 IAE.SECTION
		,IAE.EPIGRAPH
		,ACCOUNT_ENTRY.ID
		,ACCOUNT_ENTRY.JOURNAL
		,ACCOUNT_ENTRY.ENTRY_DATE
		,ACCOUNT_ENTRY_DETAIL.DEBIT
		,ACCOUNT_ENTRY_DETAIL.CREDIT
		,ACCOUNT_ENTRY_DETAIL.CONCEPT
		,ACCOUNT.CODE
		,REGISTRY.DOCUMENT
		,REGISTRY.NAME
		,FINANCE.RDOCUMENT
		,FINANCE.RNAME
	};
	
	private AccountingOperationNewDAO() {
		
	}
	
	public static Stream<OperationBreakdownNew> getOperationBreakdownNew(final AONContext ctx, OperationParamsNew params) {
		
		// Ordenar los datos por: Fecha IVA, Serie, Número Factura, Número Diario
		Comparator<OperationBreakdownNew> comparator = Comparator.comparing(OperationBreakdownNew::getTaxDate).thenComparing(OperationBreakdownNew::getInvoiceSeries).thenComparing(OperationBreakdownNew::getInvoiceNumber).thenComparing(OperationBreakdownNew::getEntryJournal);
		
		// Obtener los datos, según el tipo de libro solicitado
		if (params.getBookType() == 0) {
			// Libros de IVA (Facturas y Cobros/Pagos RECC)			
			return Stream.concat(getOperationBreakdownFac(ctx, params), getOperationBreakdownRecc(ctx, params)).sorted(comparator);			
		} else if (params.getBookType() == 1) {
			// Libros de IRPF (Apuntes, sean facturas o no, de los grupos 7 o 6)
			return Stream.concat(getOperationBreakdownFac(ctx, params), getOperationBreakdownAst(ctx, params)).sorted(comparator);
		} else {
			// Libros UNIFICADOS de IVA e IRPF (Facturas, Cobros/Pagos RECC y Asientos que no son facturas)
			return Stream.concat(Stream.concat(getOperationBreakdownFac(ctx, params), getOperationBreakdownRecc(ctx, params)), getOperationBreakdownAst(ctx, params)).sorted(comparator);
		}
		
	}

	// Facturas
	private static Stream<OperationBreakdownNew> getOperationBreakdownFac(AONContext ctx, OperationParamsNew params) {
		
		// Obtener porcentaje y tipo de prorrata del último modelo 303
		getLastProrate(ctx, params);
		
		// Si se aplica la regla de prorrata, ver si hay dos actividades una exenta y la otra no, 
		// para repartir las facturas imputadas a todas las actividades, entre las dos actividades, si la factura lleva prorrata
		if (params.getLastProratePercentage() > 0.0) {
			mustDistributeInvoice(ctx, params);
		}
		
		// Obtener facturas
		Stream<OperationBreakdownNew> invoices = getSelectFac(ctx, params)
				.where(getWhereFac(ctx, params))
				.fetch()
				.stream()
				.map(rec -> new OperationBreakdownFacFiller().apply(rec, params));
		
		// Obtener ajuste de la prorrata (del modelo 303), si procede
		Stream<OperationBreakdownNew> prorate = getProrateAdjustment(ctx, params);
		
		// Devolver resultado (facturas + ajuste de la prorrata)
		return Stream.concat(invoices, prorate);
		
	}
	
	// Se comprueba si se deben repartir las facturas, en aquellas facturas imputadas a todas las actividades
	// Por ahora solo si existen unicamente dos actividades, una de ellas con Régimen Exento de IVA y la otra no
	private static void mustDistributeInvoice(AONContext ctx, OperationParamsNew params) {
		
		params.setDistributeInvoice(false);

		// Obtenemos las actividades de la empresa (activas en el periodo que se le pasa)
		int countNormal = ctx.getDslContext()
				   .selectCount()
				   .from(ENTERPRISE_ACTIVITY)
				   .where(ENTERPRISE_ACTIVITY.DOMAIN.eq(ctx.getDomainId()))
				   .and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(AonDateUtils.toSql(params.getToDate()))))
				   .and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(AonDateUtils.toSql(params.getFromDate()))))
				   .and(ENTERPRISE_ACTIVITY.VAT_REGIME.notEqual(VATRegime.EXEMPT.value()))
				   .fetchOne()
				   .value1();
		
		int countExenta = ctx.getDslContext()
				   .selectCount()
				   .from(ENTERPRISE_ACTIVITY)
				   .where(ENTERPRISE_ACTIVITY.DOMAIN.eq(ctx.getDomainId()))
				   .and(ENTERPRISE_ACTIVITY.START_DATE.isNull().or(ENTERPRISE_ACTIVITY.START_DATE.lessOrEqual(AonDateUtils.toSql(params.getToDate()))))
				   .and(ENTERPRISE_ACTIVITY.END_DATE.isNull().or(ENTERPRISE_ACTIVITY.END_DATE.greaterOrEqual(AonDateUtils.toSql(params.getFromDate()))))
				   .and(ENTERPRISE_ACTIVITY.VAT_REGIME.equal(VATRegime.EXEMPT.value()))
				   .fetchOne()
				   .value1();
		
		// Si solo hay dos actividades y una es régimen exento de IVA y la otra no, entonces se reparte 
		if (countNormal == 1 && countExenta == 1) {
			params.setDistributeInvoice(true);
		}
		
	}

	// Obtener ajuste de la prorrata del modelo 303 del ultimo periodo, si estamos obteniendo los datos hasta final del ejercicio, solo libro de IVA y Libro Unificado y solo en compras y gastos
	private static Stream<OperationBreakdownNew> getProrateAdjustment(AONContext ctx, OperationParamsNew params) {
		
		if (params.getBookType() != 1 && params.getTabType() == 1 && params.getToDate().equals(AonDateUtils.getYearLastDay(params.getToDate()))) {

			Record rec = ctx.getDslContext()
					   .select(FS_MODEL_DETAIL.AMOUNT, IAE.EPIGRAPH, IAE.SECTION)
					   .from(FS_MODEL_DETAIL)
					   .join(FS_MODEL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
					   .leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.DOMAIN.eq(ctx.getDomainId()).and(ENTERPRISE_ACTIVITY.PRINCIPAL.eq((byte) 1)))
					   .leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
					   .where(FS_MODEL.DOMAIN.eq(ctx.getDomainId()))
					   	 .and(FS_MODEL.YEAR.eq(AonDateUtils.getYear(params.getToDate())))
					   	 .and(FS_MODEL.PERIOD.eq(Period.M12.value()).or(FS_MODEL.PERIOD.eq(Period.T4.value())))
					   	 .and(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.CT_C44.getValue()).or(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.BZ_C029.getValue()).or(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.NF_450.getValue()).or(FS_MODEL_DETAIL.TYPE.eq(Mod303Key.CA_C039.getValue())))))
					   .orderBy(FS_MODEL.YEAR.desc(), FS_MODEL.PERIOD.desc(), FS_MODEL.ID.desc())
					   .fetchAny();
			
			if (rec == null) {
				return Stream.empty();
			} else {
				Double prorateAmount = rec.getValue(FS_MODEL_DETAIL.AMOUNT);
				
				if (AonMathUtils.isZero(prorateAmount)) {
					return Stream.empty();
				}
				
				// Se ponen los datos de la actividad principal en esta línea del ajuste de la prorrata
				String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
				String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
				
				OperationBreakdownNew prorateAdjustment = new OperationBreakdownNew()
						.setActivityCode(activityCode) 					// Actividad: Código
						.setActivityType(activityType) 					// Actividad: Tipo
						.setActivityIAE(rec.getValue(IAE.EPIGRAPH))     // Actividad: Grupo o Epígrafe del IAE
						.setInvoiceType("SF") 							// Tipo de Factura (Ajuste de la prorrata de IVA)	
//						.setConceptCode()								// Codigo Concepto de Ingreso o Gasto
//						.setConceptAmount() 							// Ingreso computable o Gasto deducible 	
						.setEntryDate(params.getToDate()) 		        // Fecha Expedición
						.setTaxDate(params.getToDate())        	        // Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
						.setInvoiceSeries("") 	                        // Identificación de la Factura: Serie (Emitidas)
						.setInvoiceNumber("")                           // Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas) 
//						.setReceptionNumber()                           // Número recepción (Recibidas)
						.setReceptionDate(params.getToDate())			// Fecha Recepción (Recibidas) (Fecha Asiento)
//						.setDocumentType() 								// NIF Destinatario/Expedidor: Tipo
//						.setDocumentCountry()  				            // NIF Destinatario/Expedidor: Código País
//						.setDocument() 								    // NIF Destinatario/Expedidor: Identificación
						.setName("AJUSTE PRORRATA DEFINITIVA") 			// Nombre Destinatario/Expedidor	
						.setOperationKey("01") 							// Clave de Operación 	
//						.setOperationQualification()					// Calificación de la Operación (Emitidas)	
//						.setExemptOperation()  							// Operación Exenta (Emitidas)
//						.setInvestment() 								// Bien de Inversión (Recibidas)
//						.setIsp() 										// Inversión del Sujeto Pasivo (Recibidas)
//						.setTotal()	                                    // Total Factura (Base + IVA + REQ)	
//						.setBase()                                      // Base Imponible	
//						.setPercent()                                   // Tipo de IVA	
//						.setQuota()	                                    // Cuota IVA Repercutido/Soportado
						.setDeductibleQuota(prorateAmount)              // Cuota Deducible (Recibidas)
//						.setSurchargePercent()	   	                    // Tipo de Recargo Eq.	
//						.setSurchargeQuota()     	                    // Cuota Recargo Eq.	
//						.setPayDate() 				                    // Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
//						.setPayAmount() 			                    // Importe Cobro/Pago
//						.setPayMethod() 			                    // Medio Utilizado Cobro/Pago
//						.setPayMethodName() 		                    // Identificación Medio Utilizado Cobro/Pago
//						.setRetentionPercent()  	                    // Tipo Retención del IRPF	
//						.setRetentionQuota()    	                    // Importe Retenido del IRPF	
//						.setBuildingLocation() 		                    // Situación del Inmueble;	
//						.setCadasdralReference() 	                    // Referencia Catastral del Inmueble
//						.setEntryId()				    				// ID del asiento
//						.setEntryJournal()	    						// Número de diario del asiento
				;
				return Stream.of(prorateAdjustment);
			}
		} else {
			return Stream.empty();	
		}
		
	}

	// Obtener porcentaje y tipo de prorrata del último modelo 303
	private static void getLastProrate(AONContext ctx, OperationParamsNew params) {
		
		params.setLastProratePercentage(0.0);
		params.setLastProrateType("");
		
		FsModelDetail detail1 = FS_MODEL_DETAIL.as("detail1"); // Porcentaje de prorrata
		FsModelDetail detail2 = FS_MODEL_DETAIL.as("detail2"); // Tipo de prorrata
		FsModelDetail detail3 = FS_MODEL_DETAIL.as("detail3"); // Aplicar prorrata (desde 2026)
		
		ctx.getDslContext()
		   .select(FS_MODEL.YEAR, detail1.AMOUNT, detail2.DESCRIPTION, detail3.AMOUNT)
		   .from(FS_MODEL)
		   .leftJoin(detail1).on(FS_MODEL.ID.eq(detail1.FS_MODEL).and(detail1.TYPE.eq(Mod303Key.CM_003.getValue()))) // Porcentaje de prorrata
		   .leftJoin(detail2).on(FS_MODEL.ID.eq(detail2.FS_MODEL).and(detail2.TYPE.eq(Mod303Key.CM_006.getValue()))) // Tipo de prorrata
		   .leftJoin(detail3).on(FS_MODEL.ID.eq(detail3.FS_MODEL).and(detail3.TYPE.eq(Mod303Key.CM_008.getValue()))) // Aplicar prorrata
		   .where(FS_MODEL.DOMAIN.eq(ctx.getDomainId())).and(FS_MODEL.MODEL.eq(FiscalModelType.M303.getValue()))
		   .orderBy(FS_MODEL.YEAR.desc(), FS_MODEL.PERIOD.desc(), FS_MODEL.ID.desc())
		   .limit(1)
		   .fetch()			   
		   .stream()			   
		   .forEach(rec -> {
			   
			   // Solo se leen los modelos 303 de este ejercicio o del anterior, según params.getFromDate()
			   int modelYear = rec.getValue(FS_MODEL.YEAR);
			   int fromYear = AonDateUtils.getYear(params.getFromDate());
			   if (modelYear >= fromYear - 1 && modelYear <= fromYear) {
				   Double percentage = rec.getValue(detail1.AMOUNT);
				   String type = rec.getValue(detail2.DESCRIPTION);
				   Double applyProrate = rec.getValue(detail3.AMOUNT);
				   
				   // A partir del 2026 hay un check para indicar si se aplica prorrata o no
				   if (modelYear >= 2026) {
					   if (percentage != null && type != null && applyProrate != null) {
						   if (applyProrate.byteValue() == TRUE_BYTE) {
							   params.setLastProratePercentage(percentage);
							   params.setLastProrateType(AonStringUtils.trimToEmpty(type));
						   }
					   }
				   } else {
					   // Hasta 2025, si el porcentaje es cero o cien se asume que no se aplica prorrata
					   if (percentage != null && type != null) {
						   if (percentage > 0.0 && percentage < 100.0) {
							   params.setLastProratePercentage(percentage);
							   params.setLastProrateType(AonStringUtils.trimToEmpty(type));
						   }
					   }
				   }
			   }
			   
		   });
		
	}

	// Cobros y Pagos de Facturas RECC
	private static Stream<OperationBreakdownNew> getOperationBreakdownRecc(AONContext ctx, OperationParamsNew params) {
		return getSelectRecc(ctx)
			.where(getWhereRecc(ctx, params))
			.fetch()
			.stream()
			.map(rec -> new OperationBreakdownReccFiller().apply(rec, params));
	}
	
	// Asientos (que no son facturas)
	private static Stream<OperationBreakdownNew> getOperationBreakdownAst(AONContext ctx, OperationParamsNew params) {
		return getSelectAst(ctx).where(getWhereAst(ctx, params))
				.fetch()
				.stream()
				.map(new OperationBreakdownAstFiller());
	}
	
	// Facturas 
	private static SelectOnConditionStep<Record> getSelectFac(AONContext ctx, OperationParamsNew params) {

		// SE HACE UN LEFT JOIN CON INVOIVE_TAX, PARA QUE SALGAN LAS FACTURAS DE GASTOS NO DEDUCIBLES EN IVA, PUES 
		// ACTUALMENTE NO CREAN REGISTRO EN INVOICE_TAX Y SI SE HACE UN INNER JOIN NO SALDRIAN

		return ctx.getDslContext()
			.select(SELECT_FIELDS_FAC)
			.from(INVOICE_DETAIL)
			.join(INVOICE).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
//			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value())))
			.join(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) // Facturas contabilizadas
			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value())))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(retInvoiceTax).on(retInvoiceTax.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID).and(retInvoiceTax.TAX_TYPE.equal(TaxType.RETENTION.value())))  // Retención IRPF
			.leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.equal(INVOICE_DETAIL.INVEST_ASSET))
			// ACTIVIDAD NULA EN INVOICE SE COGEN LAS ACTIVIDADES DE LA EMPRESA, SOLO SI DEBEN REPARTIRSE ESAS FACTURAS
			.leftOuterJoin(allEnterpriseActivity).on(
					allEnterpriseActivity.DOMAIN.equal(INVOICE.DOMAIN)
						.and(INVOICE.ACTIVITY.isNull())
						.and(INVOICE.TYPE.notEqual(InvoiceType.SALES.value()))  
						.and(allEnterpriseActivity.START_DATE.isNull().or(INVOICE.TAX_DATE.greaterOrEqual(allEnterpriseActivity.START_DATE)))
						.and(allEnterpriseActivity.END_DATE.isNull().or(INVOICE.TAX_DATE.lessOrEqual(allEnterpriseActivity.END_DATE)))
						.and(params.isDistributeInvoice() ? DSL.trueCondition() :  DSL.falseCondition()) // PARA QUE SE APLIQUE O NO EL LEFT JOIN
			)
			.leftOuterJoin(otherIae).on(otherIae.ID.equal(allEnterpriseActivity.IAE))
			
			;
	}
	
	// Cobros y Pagos de Facturas RECC
	private static SelectOnConditionStep<Record> getSelectRecc(AONContext ctx) {
		return ctx.getDslContext()
			.select(SELECT_FIELDS_RECC)
			.from(FINANCE)
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.join(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.equal(INVOICE.ID)) // Facturas contabilizadas
			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)) // Asiento de la factura
			.leftOuterJoin(FINANCE_TRACKING).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			.leftOuterJoin(PAY_METHOD).on(PAY_METHOD.ID.equal(FINANCE.PAY_METHOD))
			.leftOuterJoin(RBANK).on(RBANK.ID.equal(FINANCE_TRACKING.RBANK))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			;
	}
	
	// Asientos (que no son facturas)
	private static SelectOnConditionStep<Record> getSelectAst(AONContext ctx) {
		return ctx.getDslContext()
			.select(SELECT_FIELDS_AST)
			.from(ACCOUNT_ENTRY_DETAIL)
			.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.leftAntiJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID)) // Apuntes que no son facturas
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
//			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(ENTERPRISE_ACTIVITY.ENTERPRISE))
			.leftOuterJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.equal(ACCOUNT_ENTRY_DETAIL.DOMAIN))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(ENTERPRISE.REGISTRY))
			
			.leftOuterJoin(ACCOUNT_ENTRY_FINANCE_TRACKING).on(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))
			.leftOuterJoin(FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			;
	}

	// Facturas 
	private static Condition getWhereFac(AONContext ctx, OperationParamsNew params) {

		// Todas las facturas del periodo indicado		
		Condition condition1 = INVOICE.TAX_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate()));
		
		// Facturas RECC de antes del periodo indicado, que tengan pagos RECC en el periodo indicado o fecha limite devengo el último día del ejercicio (excepto Libro de IRPF)

		int prevYear = AonDateUtils.getYear(params.getFromDate())-1;
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(prevYear));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.addDays(params.getFromDate(), -1));
		
		// Cobros/Pagos de facturas RECC entre el periodo indicado
		Condition condition21 = FINANCE_TRACKING.TYPE.equal(FinanceTrackingType.PAID.value())
  								.and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())));
		
		// Cobros/Pagos pendientes de facturas RECC antes del periodo indicado, si fin periodo indicado es ultimo día del ejercicio actual
		Condition condition22 = DSL.falseCondition();
		if (AonDateUtils.isSameDay(params.getToDate(), AonDateUtils.getYearLastDay(params.getToDate()))) {
			condition22 = FINANCE.STATUS.eq(FinanceStatus.PENDING.value())
						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)));
		}
		
		Condition condition2 = DSL.falseCondition();
		if (params.getBookType() != 1) {
			condition2 = DSL.exists(
					DSL.selectOne()
						.from(FINANCE)
						.leftOuterJoin(FINANCE_TRACKING).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
						.where(FINANCE.INVOICE.equal(INVOICE.ID)
								.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal(TRUE_BYTE))
								.and(condition21.or(condition22)))
					);
		}
		
		// Condicion completa
		Condition condition = INVOICE_DETAIL.DOMAIN.equal(ctx.getDomainId())
							.and(INVOICE_DETAIL.PREPAYMENT.equal((byte) 0)) // Lineas que no son suplidos
							.and(INVOICE.NUMBER.ge(0))   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
							.and(InvoiceDAO.NOT_ANNULLED) // No facturas anuladas
							.and(condition1.or(condition2));
		
		if (params.getActivity() != null) {
			condition = condition.and(INVOICE.ACTIVITY.eq( params.getActivity()));
		}
		
		if (params.getTabType() == 0)
			condition = condition.and(INVOICE.TYPE.eq(InvoiceType.SALES.value())); // Ventas
		else 
			condition = condition.and(INVOICE.TYPE.ne(InvoiceType.SALES.value())); // Resto (Compras, Gastos, No deducibles)
		
		// Libro de IVA (RECIBIDAS): No salen las facturas que son Gastos No Deducibles
		if (params.getBookType() == 0 && params.getTabType() == 1) {
			condition = condition.and(INVOICE.TYPE.ne(InvoiceType.UNDEDUCTIBLE.value()));
		}
		
		// Libro de IRPF: No salen las facturas que no son de cuentas del grupo 6 o 7
		if (params.getBookType() == 1) {
			if (params.getTabType() == 0)
				condition = condition.and(accountCodeField.startsWith("7"));
			else
				condition = condition.and(accountCodeField.startsWith("6"));
		}
		
		return condition;
	}
	
	// Cobros y Pagos de Facturas RECC 
	private static Condition getWhereRecc(AONContext ctx, OperationParamsNew params) {
		
		int prevYear = AonDateUtils.getYear(params.getFromDate())-1;
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(prevYear));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(prevYear));
		
		// Cobros/Pagos de facturas RECC entre el periodo indicado
		Condition condition1 = FINANCE_TRACKING.TYPE.equal(FinanceTrackingType.PAID.value())
  								.and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())));
		
		// Cobros/Pagos pendientes de facturas RECC del ejercicio anterior, si fin periodo indicado es ultimo día del ejercicio actual
		Condition condition2 = DSL.falseCondition();
		if (AonDateUtils.isSameDay(params.getToDate(), AonDateUtils.getYearLastDay(params.getToDate()))) {
			condition2 = FINANCE.STATUS.eq(FinanceStatus.PENDING.value())
						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)));
		}
		
		// Condiciones completas 
		Condition condition = FINANCE.DOMAIN.equal(ctx.getDomainId())
								.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal(TRUE_BYTE))
								.and(INVOICE.NUMBER.ge(0))   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
								.and(InvoiceDAO.NOT_ANNULLED) // No facturas anuladas
								.and(condition1.or(condition2));	
		
		if (params.getActivity() != null) {
			condition = condition.and(INVOICE.ACTIVITY.eq(params.getActivity()));
		}
		
		if (params.getTabType() == 0)
			condition = condition.and(INVOICE.TYPE.eq(InvoiceType.SALES.value())); // Ventas
		else 
			condition = condition.and(INVOICE.TYPE.ne(InvoiceType.SALES.value())); // Resto (Compras, Gastos, No deducibles)
		
		return condition;
	}
	
	// Asientos (que no son facturas)
	private static Condition getWhereAst(AONContext ctx, OperationParamsNew params) {

		Condition condition = ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId())
						.and(ACCOUNT_ENTRY.ENTRY_DATE.between(AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())));
		
		if (params.getActivity() != null) {
			condition = condition.and(ACCOUNT_ENTRY.ACTIVITY.eq( params.getActivity()));
		}
		
		if (params.getTabType() == 0)
			condition = condition.and(ACCOUNT.CODE.startsWith("7")); // Ingresos
		else 
			condition = condition.and(ACCOUNT.CODE.startsWith("6")); // Gastos
		
		return condition;
	}
	
	// Facturas
	private static class OperationBreakdownFacFiller implements BiFunction<Record,OperationParamsNew,OperationBreakdownNew> {

		private boolean isSales;
		private boolean isIntracommunity;
		private boolean isExtracommunity;
		private boolean isCanCeuMel;
		private boolean isIsp;
		private boolean isVatUnion;
		private boolean isNonTaxable;
		private boolean isSalesExempt;		

		@Override
		public OperationBreakdownNew apply(Record rec, OperationParamsNew params) {
   
			isSales = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.SALES;
			InvoiceTransactionType invoiceTransactionType = InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION));
			isIntracommunity = (invoiceTransactionType == InvoiceTransactionType.INTRACOMMUNITY);
			isExtracommunity = (invoiceTransactionType == InvoiceTransactionType.EXTRACOMMUNITY);
			isCanCeuMel = (invoiceTransactionType == InvoiceTransactionType.CAN_CEU_MEL);
			isIsp = (invoiceTransactionType == InvoiceTransactionType.OTHER_ISP);
			isVatUnion = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION));
			isNonTaxable = (VatDeductionType.safeValueOf(rec.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)) == VatDeductionType.NON_TAXABLE) || isVatUnion; // No deducible
			isSalesExempt = isSales && (AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.PERCENTAGE)) == 0.0) && !isIsp && !isNonTaxable; // Ventas exentas
			
			String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
			String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
			String activityIAE = rec.getValue(IAE.EPIGRAPH);
			
			// Actividad nula, el IAE se coge de las actividades definidas en la empresa (solo compras y gastos y si se deben repartir esas facturas)
			if (!isSales && params.isDistributeInvoice() && rec.getValue(INVOICE.ACTIVITY) == null) {
				activityCode = AonStringUtils.isBlank(rec.getValue(otherIae.EPIGRAPH)) ? "" : "A";
				activityType = getActivityType(rec.getValue(otherIae.SECTION), rec.getValue(otherIae.EPIGRAPH));
				activityIAE = rec.getValue(otherIae.EPIGRAPH);
			}
			
			String documentType = getDocumentType(rec);
			String invoiceType = getInvoiceType(rec, isVatUnion);			
			String operationKey = getOperationKey(rec);
			String operationQualification = getOperationQualification();
			String exemptOperation = getExemptOperation();
			
			boolean investment = !isSales && AonEnumUtils.getBoolean(rec.getValue(INVOICE.INVESTMENT)); // Recibidas Bienes de Inversión
			boolean isp = !isSales && isIsp; // ISP Recibidas
			
			String conceptCode = getConceptCode(rec.getValue(accountCodeField));
			// LA BASE SE COGE DE INVOICE_DETAIL, PORQUE LAS FACTURAS DE GASTOS NO DEDUCIBLES EN IVA, NO CREAN REGISTRO EN INVOICE_TAX
//			double conceptAmount = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.BASE));
			double conceptAmount = AonNumberUtils.todouble(rec.getValue(INVOICE_DETAIL.TAXABLE_BASE));
			
			// Compras y Gastos: Si lleva bien afecto, comprobar si grado de afectación IRPF es menor de 100%
			if (!isSales && rec.getValue(INVEST_ASSET.TYPE) != null) {
				Double retentionPercent = AonNumberUtils.toDouble(rec.getValue(INVEST_ASSET.RETENTION_PERCENT)); 
				if (retentionPercent != null && retentionPercent != 100.0) {
					conceptAmount = AonMathUtils.round(conceptAmount * retentionPercent / 100);
				}
			}
			
			// Libro Unificado de IVA e IRPF: Facturas cuya cuenta no es 7 o 6, el ingreso o gasto es cero y no lleva concepto
			if (params.getBookType() == 2 && 
				((params.getTabType() == 0 && !rec.getValue(accountCodeField).startsWith("7")) ||   // Ventas o Ingresos
				 (params.getTabType() == 1 && !rec.getValue(accountCodeField).startsWith("6")))) {  // Compras o Gastos
				conceptAmount = 0.0;
				conceptCode = ""; 
			}
			
			// Repartir gasto, si deben repartirse las facturas, por ahora solo se reparten en función del porcentaje de prorrata
			if (!isSales && params.isDistributeInvoice() && rec.getValue(INVOICE.ACTIVITY) == null && conceptAmount != 0.0) {
				conceptAmount = getAmountDistributed(rec, params, conceptAmount);
			}
			
			// Total factura = base + IVA + REQ (excepto recibidas ISP o intracomunitarias o UOSS)
//			double base = rec.getValue(INVOICE_TAX.BASE);
			double base = AonNumberUtils.todouble(rec.getValue(INVOICE_DETAIL.TAXABLE_BASE));
			double total = isp || isIntracommunity || isVatUnion ? base : base + getQuota(rec) + getSurchargeQuota(rec);
			
			// Datos del bien afecto (Arrendamientos)
			// Se considera arrendamiento si Emitidas con clave de operación 11, 12 O 13, Recibidas con clave de operación 12, o Actvidad A01 o D
			// Emitidas
			//  11 - Operaciones de arrendamiento de local de negocio sujetas a retención.
			//  12 - Operaciones de arrendamiento de local de negocio no sujetos a retención.
			//  13 - Operaciones de arrendamiento de local de negocio sujetas y no sujetas a retención. (ESTA NO SE PONE NUNCA PORQUE EN AON LA LINEA DE LA FACTURA O LLEVA RETENCION O NO LLEVA RETENCION)
			// Recibidas
			//  12 - Operaciones de arrendamiento de local de negocio.
			// Actividad 
			//  A01 - Arrendadores de bienes inmuebles
			//  D - Arrendadores de inmuebles no incluidos en los códigos anteriores
			String buildingLocation = "";
			String cadasdralReference = "";
			boolean isRenting = (isSales && (Arrays.asList("11", "12", "13").contains(operationKey))) ||
								(!isSales && ("12".equals(operationKey))) ||					
								("A01".equals(activityCode+activityType)) ||
								("D".equals(activityCode));   
			String properties = rec.getValue(INVEST_ASSET.PROPERTIES);
			if (isRenting && AonStringUtils.isNotBlank(properties)) {				
				JSONObject jsonObject = new JSONObject(properties);

				cadasdralReference = jsonObject.optString("catastral");
				String provinceCode = jsonObject.optString("province");
				Province province = Province.safeValueOf(provinceCode);
				String countryCode = jsonObject.optString("country");
				
				// Situación del inmueble (según datos del bien afecto indicado en la línea de la factura)
				//	1 - Inmueble con referencia catastral situado en cualquier punto del territorio español excepto PV y N: Con referencia catastral y resto de provincias
				//	2 - Inmueble con referencia catastral situado en la Comunidad Autónoma del País Vasco: Con referencia catastral y provincias del País Vasco
				//	3 - Inmueble con referencia catastral situado en la Comunidad Foral de Navarra: Con referencia catastral y provincia de Navarra
				//	4 - Inmueble situado en cualquier punto del territorio español, pero sin tener asignada referencia catastral: Sin referencia catastral (Pais en blanco o España)
				//	5 - Inmueble situado en el extranjero: Pais distinto de España
				if (AonStringUtils.isNotBlank(countryCode) && AonStringUtils.notEquals(countryCode, "ES")) {
					buildingLocation = "5"; // Resto de Paises
				} else if (AonStringUtils.isNotBlank(cadasdralReference)) {
					switch (province) {
						case ARABA, BIZKAIA, GIPUZKOA -> buildingLocation = "2"; 
						case NAVARRA -> buildingLocation = "3"; 
						default -> buildingLocation = "1";  					
					}
				} else {
					buildingLocation = "4"; // Sin Referencia Catastral (País España o sin País)
				}
			}
			
			// Cuando se trate de facturas simplificadas en las que no sea necesario identificar al destinatario, 
			// NIF Destinatario podrá venir sin contenido, pero en tal caso en la columna Nombre Destinatario 
			// se consignará "VENTAS A CONSUMIDOR FINAL"
			String name = rec.getValue(INVOICE.RNAME);
			if (isSales && "F2".equals(invoiceType) && AonStringUtils.isBlank(rec.getValue(INVOICE.RDOCUMENT))) {
				name = "VENTAS A CONSUMIDOR FINAL";
			}
			
			return new OperationBreakdownNew()
				.setActivityCode(activityCode) 									// Actividad: Código
				.setActivityType(activityType) 									// Actividad: Tipo
				.setActivityIAE(activityIAE) 									// Actividad: Grupo o Epígrafe del IAE
				.setInvoiceType(invoiceType) 									// Tipo de Factura	
				.setConceptCode(conceptCode) 									// Codigo Concepto de Ingreso o Gasto
				.setConceptAmount(conceptAmount) 								// Ingreso computable o Gasto deducible 	
				.setEntryDate(rec.getValue(INVOICE.ISSUE_DATE)) 				// Fecha Expedición
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))        				// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
				.setInvoiceSeries(isSales ? getSalesInvoiceSeries(rec) : "") 									// Identificación de la Factura: Serie (Emitidas)
				.setInvoiceNumber(isSales ? getSalesInvoiceNumber(rec) : rec.getValue(INVOICE.REFERENCE_CODE)) 	// Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
				.setReceptionNumber(isSales ? "" : FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)), rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 	// Número recepción (Recibidas)
				.setReceptionDate(isSales ? null : rec.getValue(INVOICE.TAX_DATE))			// Fecha Recepción (Recibidas) (Fecha IVA)
				.setDocumentType(documentType) 												// NIF Destinatario/Expedidor: Tipo
				.setDocumentCountry(getDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))  // NIF Destinatario/Expedidor: Código País
				.setDocument(rec.getValue(INVOICE.RDOCUMENT)) 								// NIF Destinatario/Expedidor: Identificación
				.setName(name) 																// Nombre Destinatario/Expedidor	
				.setOperationKey(operationKey) 												// Clave de Operación 	
				.setOperationQualification(operationQualification) 							// Calificación de la Operación (Emitidas)	
				.setExemptOperation(exemptOperation)  										// Operación Exenta (Emitidas)
				.setInvestment(investment) 													// Bien de Inversión (Recibidas)
				.setIsp(isp) 																// Inversión del Sujeto Pasivo (Recibidas)
				.setTotal(total )															// Total Factura (Base + IVA + REQ)	
				.setBase(base)               												// Base Imponible	
				.setPercent(isVatUnion ? 0.0 : AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.PERCENTAGE))) // Tipo de IVA (porcentaje)	
				.setQuota(isVatUnion ? 0.0 : getQuota(rec))	           						// Cuota IVA Repercutido/Soportado
				.setDeductibleQuota(isSales ? 0.0 : getDeductibleQuota(rec, params))  // Cuota Deducible (Recibidas)
				.setSurchargePercent(AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.SURCHARGE)))  // Tipo de Recargo Eq. (porcentaje)	
				.setSurchargeQuota(getSurchargeQuota(rec))     								// Cuota Recargo Eq.	
				.setPayDate(null) 															// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
				.setPayAmount(0.0) 															// Importe Cobro/Pago
				.setPayMethod("") 															// Medio Utilizado Cobro/Pago
				.setPayMethodName("") 														// Identificación Medio Utilizado Cobro/Pago
				.setRetentionPercent(AonNumberUtils.todouble(rec.getValue(retInvoiceTax.PERCENTAGE)))  	// Tipo Retención del IRPF (porcentaje)	
				.setRetentionQuota(getRetentionQuota(rec))    											// Importe Retenido del IRPF
				.setBuildingLocation(buildingLocation) 													// Situación del Inmueble;	
				.setCadasdralReference(cadasdralReference) 												// Referencia Catastral del Inmueble
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))												// ID del asiento
				.setEntryJournal(rec.getValue(ACCOUNT_ENTRY.JOURNAL))									// Número de diario del asiento
				;
					
		}

		private double getQuota(Record rec) {
			double quota = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.QUOTA));
			if (AonMathUtils.isZero(quota)) {
				double base = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.BASE));
				double percent = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.PERCENTAGE));
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}

		private double getSurchargeQuota(Record rec) {
			double quota = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA));
			if (AonMathUtils.isZero(quota)) {
				double base = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.BASE));
				double percent = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.SURCHARGE));
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
		
		private double getDeductibleQuota(Record rec, OperationParamsNew params) {
			double dedQuota = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA));
			if (AonMathUtils.isZero(dedQuota)) {
				double percent = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT));
				double quota = getQuota(rec);
				if (AonMathUtils.isZero(percent) || percent == 100) {
					dedQuota = quota;
				} else {
					dedQuota = AonMathUtils.round(quota * percent / 100);
				}
			}			
			// Comprobar si la factura lleva porcentaje de prorrata y debe aplicarse:
			// Prorrata General: Se aplica a todas las facturas
			// Prorrata Especial: Se aplica solo a las facturas que no tienen actividad (es decir que se imputan a todas las actividades)
			Pair<Double, String> prorateData = getProrateData(rec, params);
			double proratePer = prorateData.getLeft();
			String prorateTyp = prorateData.getRight();
			boolean mustApplyProrate = AonStringUtils.equals(prorateTyp, "G") || (AonStringUtils.equals(prorateTyp, "E") && rec.getValue(INVOICE.ACTIVITY) == null);   
			//if (mustApplyProrate && AonMathUtils.isNotZero(proratePer) && proratePer < 100) {
			if (mustApplyProrate) {
				if (params.isDistributeInvoice() && rec.getValue(allEnterpriseActivity.VAT_REGIME) != null && rec.getValue(allEnterpriseActivity.VAT_REGIME) == VATRegime.EXEMPT.value())
					dedQuota = 0.0;
				else
					dedQuota = AonMathUtils.round(dedQuota * proratePer / 100);
			}			
			return dedQuota;
		}
		
		private Pair<Double, String> getProrateData(Record rec, OperationParamsNew params) {			
			double proratePer = 0.0;
			String prorateTyp = ""; 
			if (rec.getValue(proratePercentageField) == null) {
				// Si la factura aún no está declarada en el modelo 303, entonces se coge el porcentaje de prorrata del último modelo 303 creado
				proratePer = params.getLastProratePercentage();
				prorateTyp = params.getLastProrateType(); 
			} else {
				// Si la factura ya está declarada en el modelo 303, se cogen los datos de dicho modelo
				proratePer = AonNumberUtils.todouble(rec.getValue(proratePercentageField));
				prorateTyp = rec.getValue(prorateTypeField);
				if (rec.getValue(prorateField) != null) {
					// A partir de 2026, existe un campo específico en el modelo 303 que indica si se aplica prorrata, sea el porcentaje que sea
					if (AonNumberUtils.todouble(rec.getValue(prorateField)) == 0.0) {
						proratePer = 0.0;
						prorateTyp = "";
					}	
				} else {
					// Hasta 2025, si el porcentaje de prorrata es 0 o 100, no se aplica prorrata
					if (proratePer == 0.0 || proratePer == 100.0) {
						proratePer = 0.0;
						prorateTyp = "";
					}
				}
			}
			return Pair.of(proratePer, prorateTyp);			
		}

		private double getAmountDistributed(Record rec, OperationParamsNew params, double amount) {
			double dedQuota = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA));
			if (AonMathUtils.isZero(dedQuota)) {
				double percent = AonNumberUtils.todouble(rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT));
				double quota = getQuota(rec);
				if (AonMathUtils.isZero(percent) || percent == 100) {
					dedQuota = quota;
				} else {
					dedQuota = AonMathUtils.round(quota * percent / 100);
				}
			}		
			// Comprobar si la factura lleva porcentaje de prorrata y debe aplicarse:
			// Prorrata General: Se aplica a todas las facturas
			// Prorrata Especial: Se aplica solo a las facturas que no tienen actividad (es decir que se imputan a todas las actividades)
			Pair<Double, String> prorateData = getProrateData(rec, params);
			double proratePer = prorateData.getLeft();
			String prorateTyp = prorateData.getRight();
			boolean mustApplyProrate = AonStringUtils.equals(prorateTyp, "G") || (AonStringUtils.equals(prorateTyp, "E") && rec.getValue(INVOICE.ACTIVITY) == null);   
			//if (mustApplyProrate && AonMathUtils.isNotZero(proratePer) && proratePer < 100) {
			if (mustApplyProrate) {
				if (params.isDistributeInvoice() && rec.getValue(allEnterpriseActivity.VAT_REGIME) != null && rec.getValue(allEnterpriseActivity.VAT_REGIME) == VATRegime.EXEMPT.value())
					amount = (amount - AonMathUtils.round(amount * proratePer / 100)) + (dedQuota - AonMathUtils.round(dedQuota * proratePer / 100)) ;
				else
					amount = AonMathUtils.round(amount * proratePer / 100);
			}			
			return amount;
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
		
		// Clave de Operación: Se hace igual que en el SII
		private String getOperationKey(Record rec) {
			boolean isVatAccrualRegime = AonEnumUtils.getBoolean(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT));
			
			if (isSales) {
				// Facturas Emitidas	
				boolean isVatUnionExternal = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_UNION_EXTERNAL));
				boolean isVatImportation = AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_IMPORTATION));
				// SE PONE LA CLAVE 11 SI ES ARRENDAMIENTO CON RETENCION. 
				// SE PODRIA PONER TAMBIEN LA CLAVE 12 ARRENDAMIENTO SIN RETENCION, SI TUVIERAMOS EL BIEN AFECTO EN LAS FACTURAS EMITIDAS, PERO ACTUALMENTE NO LO TENEMOS EN AON
				boolean isRetentionRenting =
						AonNumberUtils.notEquals(rec.getValue(retInvoiceTax.PERCENTAGE), 0.0) && AonNumberUtils.equals(rec.getValue(retInvoiceTax.WITHHOLDING_TYPE), 1);
				boolean isPremisesRenting = // ESTO AHORA NO SE DARA NUNCA PORQUE NO SE PUEDEN PONER BIEN AFECTO EN LAS FACTURAS EMITIDAS
						AonNumberUtils.equals(rec.getValue(INVEST_ASSET.TYPE), 0);
				
				if (isVatUnion || isVatUnionExternal || isVatImportation) {
					return "17"; // Regimenes especiales ventanilla única 
				} else if (isVatAccrualRegime) {
					return "07"; // RECC
				} else if (isExtracommunity || isCanCeuMel) {
					return "02"; // Extracomunitaria o Ceuta/Melilla
				} else  if (isRetentionRenting) {
					return "11"; // Operaciones de arrendamiento de local de negocio sujetas a retención
				} else if (isPremisesRenting) {
					return "12"; // Operaciones de arrendamiento de local de negocio no sujetos a retención
				} else {
					return "01"; // Resto
				}
			} else {					
				// Facturas Recibidas	
				boolean isFarmer = AonEnumUtils.getBoolean(rec.getValue(INVOICE.WITHHOLDING_FARMER)); // REAGYP
				boolean isArrendamiento = // Arrendamiento: Bien afecto Tipo = Local y Régimen = Alquiler o Lleva retención arrendamientos
						(AonNumberUtils.equals(rec.getValue(INVEST_ASSET.TYPE), 0) && AonNumberUtils.equals(rec.getValue(INVEST_ASSET.REGIME), 1)) ||
						(AonNumberUtils.notEquals(rec.getValue(retInvoiceTax.PERCENTAGE), 0.0) && AonNumberUtils.equals(rec.getValue(retInvoiceTax.WITHHOLDING_TYPE), 1));
				
				if (isVatAccrualRegime) {
					return "07"; // RECC
				} else if (isIntracommunity ) {
					return "09"; // Intracomunitarias
				} else if (isFarmer) {
					return "02"; // REAGYP
				} else if (isArrendamiento) {
					return "12"; // Operaciones de arrendamiento de local de negocio
				} else {
					return "01"; // Resto
				}			
			}
		}

		// Calificador de la Operación (solo Emitidas): Se hace igual que el SII
		private String getOperationQualification() {
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
		private String getExemptOperation() {
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
		
	}
	
	// Cobros y Pagos de Facturas RECC
	private static class OperationBreakdownReccFiller implements BiFunction<Record,OperationParamsNew,OperationBreakdownNew> {

		@Override
		public OperationBreakdownNew apply(Record rec, OperationParamsNew params) {
			
			boolean isSales = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.SALES;
			
			String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
			String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
			String documentType = getDocumentType(rec);
			String invoiceType = getInvoiceType(rec, false);			
			boolean investment = !isSales && AonEnumUtils.getBoolean(rec.getValue(INVOICE.INVESTMENT)); // Recibidas Bienes de Inversión
			Date payDate = rec.getValue(FINANCE_TRACKING.TRACKING_DATE) == null ? params.getToDate() : rec.getValue(FINANCE_TRACKING.TRACKING_DATE);
			double payAmount = rec.getValue(FINANCE_TRACKING.AMOUNT) == null ? rec.getValue(FINANCE.AMOUNT) : rec.getValue(FINANCE_TRACKING.AMOUNT); 
			String payMethod = ""; 
			String payMethodName = "";
			
			if (rec.getValue(FINANCE_TRACKING.TRACKING_DATE) == null) {
				payMethod = "03"; // No se cobra (fecha límite de devengo, 31-12 del año siguiente)	
				payMethodName = "FECHA LIMITE DE DEVENGO";
			} else {
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
			}
			
			return new OperationBreakdownNew()
				.setActivityCode(activityCode) 									// Actividad: Código
				.setActivityType(activityType) 									// Actividad: Tipo
				.setActivityIAE(rec.getValue(IAE.EPIGRAPH)) 					// Actividad: Grupo o Epígrafe del IAE
				.setInvoiceType(invoiceType) 									// Tipo de Factura	
//				.setConceptCode() 												// Codigo Concepto de Ingreso o Gasto
//				.setConceptAmount() 											// Ingreso computable o Gasto deducible 	
				.setEntryDate(rec.getValue(INVOICE.ISSUE_DATE)) 				// Fecha Expedición
				.setTaxDate(payDate)        									// Fecha Iva (Ejercicio y Periodo de Autoliquidación) (Fecha de pago o limite de devengo del Vencimiento)	
				.setInvoiceSeries(isSales ? getSalesInvoiceSeries(rec) : "") 								   	// Identificación de la Factura: Serie (Emitidas)
				.setInvoiceNumber(isSales ? getSalesInvoiceNumber(rec) : rec.getValue(INVOICE.REFERENCE_CODE)) 	// Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
				.setReceptionNumber(isSales ? "" : FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)), rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 	// Número recepción (Recibidas)
				.setReceptionDate(isSales ? null : rec.getValue(INVOICE.TAX_DATE))			// Fecha Recepción (Recibidas) (Fecha IVA de la factura)
				.setDocumentType(documentType) 												// NIF Destinatario/Expedidor: Tipo
				.setDocumentCountry(getDocumentCountry(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))  // NIF Destinatario/Expedidor: Código País
				.setDocument(rec.getValue(INVOICE.RDOCUMENT)) 								// NIF Destinatario/Expedidor: Identificación
				.setName(rec.getValue(INVOICE.RNAME)) 										// Nombre Destinatario/Expedidor	
				.setOperationKey("07") 														// Clave de Operación 	
				.setOperationQualification("S1") 											// Calificación de la Operación (Emitidas)	
//				.setExemptOperation()  														// Operación Exenta (Emitidas)
				.setInvestment(investment) 													// Bien de Inversión (Recibidas)
//				.setIsp() 																    // Inversión del Sujeto Pasivo (Recibidas)
//				.setTotal()																    // Total Factura (Base + IVA + REQ)	
//				.setBase()               												    // Base Imponible	
//				.setPercent()            												    // Tipo de IVA (porcentaje)	
//				.setQuota()	           							                		    // Cuota IVA Repercutido/Soportado
//				.setDeductibleQuota()                    								    // Cuota Deducible (Recibidas)
//				.setSurchargePercent()	   	                							    // Tipo de Recargo Eq. (porcentaje)	
//				.setSurchargeQuota()     					                			    // Cuota Recargo Eq.	
				.setPayDate(payDate) 											            // Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
				.setPayAmount(payAmount) 										            // Importe Cobro/Pago
				.setPayMethod(payMethod) 										            // Medio Utilizado Cobro/Pago
				.setPayMethodName(payMethodName) 								            // Identificación Medio Utilizado Cobro/Pago
//				.setRetentionPercent()  													// Tipo Retención del IRPF (porcentaje)	
//				.setRetentionQuota()    													// Importe Retenido del IRPF
//				.setBuildingLocation() 														// Situación del Inmueble;	
//				.setCadasdralReference() 													// Referencia Catastral del Inmueble
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))									// ID del asiento
				.setEntryJournal(rec.getValue(ACCOUNT_ENTRY.JOURNAL))						// Número de diario del asiento
				;
		}
		
	}	
	
	// Asientos
	private static class OperationBreakdownAstFiller implements Function<Record,OperationBreakdownNew> {

		@Override
		public OperationBreakdownNew apply(Record rec) {
			
			boolean isIncomes = rec.getValue(ACCOUNT.CODE).startsWith("7");
			
			String activityCode = AonStringUtils.isBlank(rec.getValue(IAE.EPIGRAPH)) ? "" : "A";
			String activityType = getActivityType(rec.getValue(IAE.SECTION), rec.getValue(IAE.EPIGRAPH));
			String conceptCode = getConceptCode(rec.getValue(ACCOUNT.CODE));
			
			double debit = AonNumberUtils.todouble(rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT));
			double credit = AonNumberUtils.todouble(rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT));
			double amount = isIncomes ? credit - debit : debit - credit;
			
			// CLAVE DE OPERACION DEBE ESTAR CUMPLIMENTADA (LE PONGO 01)
			String operationKey = "01";
			
			// CALIFICADOR DE LA OPERACION Y OPERACION EXENTA NO PUEDEN ESTAR VACIOS LOS DOS (LE PONGO EXENTA E6)
			String exemptOperation = isIncomes ? "E6" : "";
			
			// Para determinados conceptos es obligatorio poner el identificador del destinatario/expedidor
			// Ver si el asiento está unido a un vencimiento, de donde se pueda obtener la identificación del destinatario/expedidor
			// si no, se pone el NIF y Nombre de la empresa
			String document = "";
			String name = "";
			if (Arrays.asList(REQUIRED_CONCEPTS).contains(conceptCode)) {
				// Se intenta obtener primero de finance
				document = rec.getValue(FINANCE.RDOCUMENT);
				name = rec.getValue(FINANCE.RNAME);
				// Si no está en finance, se ponen los datos de la empresa
				if (AonStringUtils.isBlank(document)) {
					document = rec.getValue(REGISTRY.DOCUMENT);
					name = rec.getValue(REGISTRY.NAME);
				}
			}
			
			// LA FECHA DE RECEPCION EN LOS GASTOS ES OBLIGATORIA AUNQUE SEA EXCLUSIVA DEL LIBRO DE IVA (LE PONGO LA FECHA DEL ASIENTO)
			Date receptionDate = isIncomes ? null : rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE);
			
			// TIPO DE FACTURA PARA LOS GASTOS PONEMOS F6 SINO DA ERROR LA VALIDACION EN EL UNIFICADO DICIENDO QUE SF SE USA PARA EL AJUSTE DE PRORRATA DE IVA
			String invoiceType = isIncomes ? "SF" : "F6";
			
			return new OperationBreakdownNew()
				.setActivityCode(activityCode) 								// Actividad: Código
				.setActivityType(activityType) 								// Actividad: Tipo
				.setActivityIAE(rec.getValue(IAE.EPIGRAPH)) 				// Actividad: Grupo o Epígrafe del IAE
				.setInvoiceType(invoiceType) 								// Tipo de Factura (Asientos sin factura)	
				.setConceptCode(conceptCode)								// Codigo Concepto de Ingreso o Gasto
				.setConceptAmount(amount) 									// Ingreso computable o Gasto deducible 	
				.setEntryDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE)) 		// Fecha Expedición
				.setTaxDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))        	// Fecha Iva (Ejercicio y Periodo de Autoliquidación)	
				.setInvoiceSeries("") 	                                    // Identificación de la Factura: Serie (Emitidas)
				.setInvoiceNumber("")                                       // Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas) 
//				.setReceptionNumber()                                       // Número recepción (Recibidas)
				.setReceptionDate(receptionDate)							// Fecha Recepción (Recibidas) (Fecha Asiento)
//				.setDocumentType() 											// NIF Destinatario/Expedidor: Tipo
//				.setDocumentCountry()  				                        // NIF Destinatario/Expedidor: Código País
				.setDocument(document) 								        // NIF Destinatario/Expedidor: Identificación
				.setName(name) 												// Nombre Destinatario/Expedidor (Concepto del apunte)	
				.setOperationKey(operationKey) 								// Clave de Operación 	
//				.setOperationQualification()								// Calificación de la Operación (Emitidas)	
				.setExemptOperation(exemptOperation)  						// Operación Exenta (Emitidas)
//				.setInvestment() 											// Bien de Inversión (Recibidas)
//				.setIsp() 													// Inversión del Sujeto Pasivo (Recibidas)
				.setTotal(amount)	                                        // Total Factura (Base + IVA + REQ)	
				.setBase(amount)                                            // Base Imponible	
//				.setPercent()                                               // Tipo de IVA	
//				.setQuota()	                                                // Cuota IVA Repercutido/Soportado
//				.setDeductibleQuota()                                       // Cuota Deducible (Recibidas)
//				.setSurchargePercent()	   	                                // Tipo de Recargo Eq.	
//				.setSurchargeQuota()     	                                // Cuota Recargo Eq.	
//				.setPayDate() 				                                // Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
//				.setPayAmount() 			                                // Importe Cobro/Pago
//				.setPayMethod() 			                                // Medio Utilizado Cobro/Pago
//				.setPayMethodName() 		                                // Identificación Medio Utilizado Cobro/Pago
//				.setRetentionPercent()  	                                // Tipo Retención del IRPF	
//				.setRetentionQuota()    	                                // Importe Retenido del IRPF	
//				.setBuildingLocation() 		                                // Situación del Inmueble;	
//				.setCadasdralReference() 	                                // Referencia Catastral del Inmueble
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))				    // ID del asiento
				.setEntryJournal(rec.getValue(ACCOUNT_ENTRY.JOURNAL))	    // Número de diario del asiento
				;
		}

	}
	
	// Tipo de Actividad (según el IAE) (Para el Código A) 
	//	A01	Arrendadores de bienes inmuebles								Sección 1, Epígrafes 861.x
	//	A02	Ganadería independiente											Sección 1, Division 0
	//	A03	Resto de actividades empresariales no incluidas en A01 y A02	Resto Sección 1
	//	A04	Actividades profesionales de carácter artístico o deportivo		Sección 3
	//	A05	Restantes actividades profesionales								Sección 2
	private static String getActivityType(String section, String iae) {
		
		if ("3".equals(section)) {
			return "04";
		}
		else if ("2".equals(section)) {
			return "05";
		}
		else if (AonStringUtils.isNotBlank(iae)) {
			if (iae.startsWith("861"))
				return "01";
			else if (iae.startsWith("0"))
				return "02";
			else
				return "03";
		}				
		return "";
		
	}


	// Grecia se pone como EL, no como su codigo ISO2 que es GR
	public static String getDocumentCountry(String value) {
		if ("GR".equals(value))
			return "EL";
		else 
			return value;
	}

	// Código de Concepto de Ingreso o Gasto
	private static String getConceptCode(String value) {
		
		if (AonStringUtils.isNotBlank(value)) {
			String code = value.trim();
			if (code.startsWith("7")) {
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
			} else if (code.startsWith("6")) {
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
	
	// Tipo de Factura: Se hace igual que en el SII
	private static String getInvoiceType(Record rec, boolean isVatUnion) {
		
		boolean isRectification = (RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)) == RectificationType.NORMAL_RECTIFIER);
		boolean isSales = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.SALES;
		
		if (isSales && !isVatUnion && AonStringUtils.isBlank(rec.getValue(INVOICE.RDOCUMENT))) {
			return "F2"; // Facturas Emitidas: Factura sin identificación del destinatario
		} else if (isRectification) {
			return "R1"; // Facturas Emitidas y Recibidas: Rectificativas
		} else {
			return "F1"; // Facturas Emitidas y Recibidas: Resto de facturas
		}
		
	}
	
	// Tipo NIF, se hace como se hace en el SII:
	// Factura intracomunitaria: 02-NIF-IVA
	// Pasp., P.T., T.C., Otr: El valor que lleva (3, 4, 5 ó 6)
	// Resto: no lleva tipo
	private static String getDocumentType(Record rec) {
		
		InvoiceTransactionType invoiceTransactionType = InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION));		
		
	    String registryDocumentType = "";	
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
	
	public static String getSalesInvoiceSeries(Record rec) {
		
		String series = AonStringUtils.trimToEmpty(rec.getValue(INVOICE.SERIES));
		String number = AonStringUtils.trimToEmpty(AonStringUtils.leftPad(AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER)), 6, "0"));
		String referenceCode = AonStringUtils.trimToEmpty(rec.getValue(INVOICE.REFERENCE_CODE));
		
		// Si series no está vacio y coincide series + "/" + number con referenceCode, entonces se devuelve series, si no, se devuelve cadena vacia
		if (AonStringUtils.isNotBlank(series) && AonStringUtils.equals(series + "/" + number, referenceCode)) {
			return series;
		} else {
			return "";
		}
		
	}

	public static String getSalesInvoiceNumber(Record rec) {
		
		String series = AonStringUtils.trimToEmpty(rec.getValue(INVOICE.SERIES));
		String number = AonStringUtils.trimToEmpty(AonStringUtils.leftPad(AonNumberUtils.toString(rec.getValue(INVOICE.NUMBER)), 6, "0"));
		String referenceCode = AonStringUtils.trimToEmpty(rec.getValue(INVOICE.REFERENCE_CODE));
		
		// Si series no está vacio y coincide series + "/" + number con referenceCode, entonces se devuelve number, si no, se devuelve referenceCode
		if (AonStringUtils.isNotBlank(series) && AonStringUtils.equals(series + "/" + number, referenceCode)) {
			return number;
		} else {
			return referenceCode;
		}
			
	}
	
}
