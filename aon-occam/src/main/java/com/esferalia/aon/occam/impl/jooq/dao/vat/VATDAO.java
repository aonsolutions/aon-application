package com.esferalia.aon.occam.impl.jooq.dao.vat;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFilter;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class VATDAO  {
	private static final byte FALSE_BYTE = 0;
	private static final byte TRUE_BYTE = 1;

	private static final String MODEL_INVOICE = "modelInvoice";
	private static final Field<Integer> ALCATRAZ_INVOICE_ID = ALCATRAZ.INVOICE.as("alcatrazInvoice");
	private static final Field<Integer> ALCATRAZ_FINANCE_ID = ALCATRAZ.FINANCE.as("alcatrazFinance");
	private static final Field<Integer> ALCATRAZ_FINANCE_TRACKING_ID = ALCATRAZ.FINANCE_TRACKING.as("alcatrazFinanceTracking");
	
	private static final Field<?>[] INVOICE_FIELDS = new Field[]{
	 	 INVOICE.ID					,INVOICE.SERIES				,INVOICE.NUMBER		
	 	,INVOICE.REFERENCE_CODE		,INVOICE.RDOCUMENT			,INVOICE.RDOCUMENT_TYPE		
	 	,INVOICE.RDOCUMENT_COUNTRY	,INVOICE.RNAME				,INVOICE.ISSUE_DATE
	 	,INVOICE.TAX_DATE			,INVOICE.TYPE				,INVOICE.RECTIFICATION_TYPE
	 	,INVOICE.SERVICE			,INVOICE.TRANSACTION		,INVOICE.INVESTMENT
	 	,INVOICE.WITHHOLDING_FARMER	,INVOICE.VAT_ACCRUAL_PAYMENT,INVOICE.WITHHOLDING
		,INVOICE.RETENTION_QUOTA 	,INVOICE.REGISTRY			,INVOICE.TOTAL};
	
	private static final Field<?>[] INVOICE_DETAIL_FIELDS = new Field[]{
		 INVOICE_DETAIL.TAXABLE_BASE 
		,INVOICE_DETAIL.INVEST_ASSET 
		,INVOICE_DETAIL.SOURCE};
	
	private static final Field<?>[] INVOICE_TAX_FIELDS = new Field[]{
		 INVOICE_TAX.BASE				,INVOICE_TAX.PERCENTAGE		
		,INVOICE_TAX.QUOTA				,INVOICE_TAX.SURCHARGE			
		,INVOICE_TAX.SURCHARGE_QUOTA	,INVOICE_TAX.DEDUCTIBLE_PERCENT
		,INVOICE_TAX.DEDUCTIBLE_QUOTA	,INVOICE_TAX.VAT_DEDUCTION_TYPE};
	
	private static final Field<?>[] INVOICE_DUA_FIELDS = new Field[]{
		INVOICE_FISCAL.VAT_IMPORTATION	,INVOICE_DUA.ID};

	private static final Field<?>[] ENTERPRISE_ACTIVITY_FIELDS = new Field[]{
		 ENTERPRISE_ACTIVITY.ID			,ENTERPRISE_ACTIVITY.DESCRIPTION
		,ENTERPRISE_ACTIVITY.VAT_REGIME	,ENTERPRISE_ACTIVITY.SURCHARGE
		,IAE.EPIGRAPH};
	private static final Field<?>[] FINANCE_FIELDS = new Field[]{
			FINANCE.ID,	FINANCE.AMOUNT};
	private static final Field<?>[] FINANCE_TRACKING_FIELDS = new Field[]{
			FINANCE_TRACKING.ID	,FINANCE_TRACKING.TYPE	,FINANCE_TRACKING.AMOUNT};
	
	private VATDAO() {
	}
	
	private static java.sql.Date getYearFirstDay( final IFiscalModel fm ) {
		return AonDateUtils.toSql( AonDateUtils.getYearFirstDay(fm.getYear()));
	}
	private static java.sql.Date getStartDate( final IFiscalModel fm ) {
		return fm.isGenerateFromYearStart()
				?getYearFirstDay(fm)
				:AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));
	}
	private static java.sql.Date getEndDate( final IFiscalModel fm ) {
		return AonDateUtils.toSql( FiscalUtils.getPeriodEnd(fm));
	}
	
	
	/**
	 * Busqueda de datos en facturas NO CRITERIO DE CAJA
	 */
	private static SelectConditionStep<Record> getCommonSelect(AONContext ctx, AccountingReportParams params) {
		return getCommonSelect( ctx ).where( getWhere(params) );
	}
	private static SelectConditionStep<Record1<Integer>> getCommonCountSelect(AONContext ctx, AccountingReportParams params) {
		return getCommonCountSelect( ctx ).where( getWhere(params) );
	}
	private static SelectOnConditionStep<Record> getCommonSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select( INVOICE_FIELDS )
			.select( INVOICE_DETAIL_FIELDS )
			.select( INVOICE_TAX_FIELDS )
			.select( ENTERPRISE_ACTIVITY_FIELDS )
			.select( INVOICE_DUA_FIELDS )
			.from(INVOICE_TAX)
			.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			;
	}
	
	
	private static SelectOnConditionStep<Record1<Integer>> getCommonCountSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select(DSL.countDistinct(INVOICE.ID))
			.from(INVOICE_TAX)
			.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			;
	}

	private static Stream<VatContext> getCommonVatBreakdown(AONContext ctx, AccountingReportParams params) {
		return getCommonSelect(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE ))	// No Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextFiller());
	}
	
	private static Stream<VatContext> getCommonVatBreakdown(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return getCommonSelect(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE ))	// No Criterio de Caja.
			.and(INVOICE.ID.isNotNull())
			.and(condition)
			.groupBy(INVOICE.ID)
			.orderBy( INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.RNAME )
			.limit(invoiceFilter.getPerPage())
			.offset(invoiceFilter.getPage())
			.fetch()
			.stream()
			.map(new VatContextFiller());
	}
	
	private static Condition parseCondition(AONContext ctx, InvoiceFilter invoiceFilter) {
		Condition condition = INVOICE.DOMAIN.eq(ctx.getDomainId());
		
		if(null != invoiceFilter.getTypesByte())
			condition = condition.and(INVOICE.TYPE.in(invoiceFilter.getTypesByte()));
		
		if(null != invoiceFilter.getFrom())
			condition = condition.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(invoiceFilter.getFrom())));
		
		if(null != invoiceFilter.getTo())
			condition = condition.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(invoiceFilter.getTo())));
		
		if(AonStringUtils.isNotBlank(invoiceFilter.getDescription())) {
			condition = condition.and(
					INVOICE.REFERENCE_CODE.like("%" + invoiceFilter.getDescription() + "%")
					.or(INVOICE.RNAME.like("%" + invoiceFilter.getDescription() + "%"))
					.or(INVOICE.SERIES.like("%" + invoiceFilter.getDescription() + "%"))
					.or(INVOICE.RDOCUMENT.like("%" + invoiceFilter.getDescription() + "%"))
			);
		}
		
		return condition;
	}
	
	private static Integer getCommonVatBreakdownCount(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return getCommonCountSelect(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE ))	// No Criterio de Caja.
			.and(INVOICE.ID.isNotNull())
			.and(condition)
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER)
			.fetchOne()
			.value1();
	}
	
//	private static Stream<VatContext> getCommonVatBreakdown(AONContext ctx, IFiscalModel mod) {
//		return getCommonVatBreakdown(ctx, 
//			new AccountingReportParams()
//				.setFromDate(getStartDate(mod))
//				.setToDate(getEndDate(mod)));
//	}
	
	/**
	 * Busqueda de datos en facturas CRITERIO DE CAJA
	 */
	private static SelectConditionStep<Record> getCritCajaSelect(AONContext ctx, AccountingReportParams params) {
		return getCritCajaSelect( ctx ).where( getWhere(params) );
	}
	private static SelectConditionStep<Record1<Integer>> getCritCajaSelectCount(AONContext ctx, AccountingReportParams params) {
		return getCritCajaSelectCount( ctx ).where( getWhere(params) );
	}
	private static SelectOnConditionStep<Record> getCritCajaSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select( INVOICE_FIELDS )
			.select( INVOICE_DETAIL_FIELDS )
			.select( INVOICE_TAX_FIELDS )
			.select( ENTERPRISE_ACTIVITY_FIELDS )
			.select( INVOICE_DUA_FIELDS )
			.select( FINANCE_FIELDS )
			.select( FINANCE_TRACKING_FIELDS )
			.from(FINANCE_TRACKING)
			.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
		;
	}
	private static SelectOnConditionStep<Record1<Integer>> getCritCajaSelectCount(AONContext ctx) {
		return ctx.getDslContext()
			.select(DSL.countDistinct(FINANCE.INVOICE))
			.from(FINANCE_TRACKING)
			.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
		;
	}
	private static Stream<VatContext> getCritCajaVatBreakdown(AONContext ctx, AccountingReportParams params) {
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(params.getFromDate()) );
		return getCritCajaSelect(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextCritCajaFiller())
			;
	}
	private static Stream<VatContext> getCritCajaVatBreakdown(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(params.getFromDate()) );
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return getCritCajaSelect(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.and(INVOICE.ID.isNotNull())
			.and(condition)
			.groupBy(INVOICE.ID)
			.orderBy( INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.RNAME )
			.limit(invoiceFilter.getPerPage())
			.offset(invoiceFilter.getPage())
			.fetch()
			.stream()
			.map(new VatContextCritCajaFiller())
			;
	}
	
	private static Integer getCritCajaVatBreakdownCount(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(params.getFromDate()) );
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return getCritCajaSelectCount(ctx, params)
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between( AonDateUtils.toSql(params.getFromDate()), AonDateUtils.toSql(params.getToDate())))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.and(INVOICE.ID.isNotNull())
			.and(condition)
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetchOne()
			.value1()
			;
	}
//	private static Stream<VatContext> getCritCajaVatBreakdown(AONContext ctx, IFiscalModel mod) {
//		return getCritCajaVatBreakdown(ctx, 
//			new AccountingReportParams()
//				.setFromDate(getStartDate(mod))
//				.setToDate(getEndDate(mod)));
//	}
	
	/**
	 * Busqueda de datos en facturas CRITERIO DE CAJA pendientes del Ejercicio anterior
	 */
	private static SelectOnConditionStep<Record> getLastPeriodCritCajaVatBreakdownSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select( INVOICE_FIELDS )
			.select( INVOICE_DETAIL_FIELDS )
			.select( INVOICE_TAX_FIELDS )
			.select( ENTERPRISE_ACTIVITY_FIELDS )
			.select( INVOICE_DUA_FIELDS )
			.select( FINANCE_FIELDS )
			.from(FINANCE)
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			;		
	}
	
	private static Stream<VatContext> getLastPeriodCritCajaVatBreakdown(AONContext ctx, final IFiscalModel mod ) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		return getLastPeriodCritCajaVatBreakdownSelect(ctx)
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodCritCajaFiller())
		;
	}
	
	
	/**
	 * Busqueda de datos en facturas no declaradas NO CRITERIO DE CAJA
	 */
	public static Stream<VatContext> getNotInModelCommonVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(ALCATRAZ.FINANCE.isNull())
			.and(ALCATRAZ.FINANCE_TRACKING.isNull())
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getCommonSelect(ctx)
				.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
				.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE) )	// No Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}
	
	/**
	 * Busqueda de datos en facturas/ tracking no declaradas CRITERIO DE CAJA
	 */
	public static Stream<VatContext> getNotInModelCritCajaVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		java.sql.Date modYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(mod.getYear()) );
		Table<Record3<Integer,Integer,Integer>> modelInvoice = ctx.getDslContext()
			.select( ALCATRAZ_INVOICE_ID, ALCATRAZ_FINANCE_ID, ALCATRAZ_FINANCE_TRACKING_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(ALCATRAZ.FINANCE.isNotNull())
			.and(ALCATRAZ.FINANCE_TRACKING.isNotNull())
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getCritCajaSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID)
				.and(ALCATRAZ_FINANCE_ID.equal(FINANCE.ID))
				.and(ALCATRAZ_FINANCE_TRACKING_ID.equal(FINANCE_TRACKING.ID)))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between( modYearFirstDay, getEndDate(mod) ))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.stream()
			.map(new VatContextCritCajaFiller())
			;
	}
	
	/**
	 * Busqueda de datos en facturas/finance no declaradas CRITERIO DE CAJA
	 */
	private static Stream<VatContext> getNotInModelLastPeriodCritCajaVatBreakdown(AONContext ctx, final FiscalModel mod ) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		Table<Record3<Integer, Integer, Integer>> modelInvoice = ctx.getDslContext()
			.select( ALCATRAZ_INVOICE_ID, ALCATRAZ_FINANCE_ID, ALCATRAZ_FINANCE_TRACKING_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getLastPeriodCritCajaVatBreakdownSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID)
				.and(ALCATRAZ_FINANCE_ID.equal(FINANCE.ID)))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodCritCajaFiller())
			;
	}
	
	/**
	 * Busqueda de datos declarados no CRITERIO DE CAJA
	 */
	private static Stream<VatContext> getModelCommonVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		return getCommonSelect(ctx)
				.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
					.and(ALCATRAZ.FINANCE.isNull())
					.and(ALCATRAZ.FINANCE_TRACKING.isNull())
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE) )	// No Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}
	
	/**
	 * Busqueda de datos declarados CRITERIO DE CAJA
	 */
	public static Stream<VatContext> getModelCritCajaVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		return getCritCajaSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID)
				.and(ALCATRAZ.FINANCE.equal(FINANCE.ID))
				.and(ALCATRAZ.FINANCE_TRACKING.equal(FINANCE_TRACKING.ID)))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.stream()
			.map(new VatContextCritCajaFiller())
			;
	}
	
	/**
	 * Busqueda de datos declarados CRITERIO DE CAJA (vencimientos pendientes)
	 */
	private static Stream<VatContext> getModelLastPeriodCritCajaVatBreakdown(AONContext ctx, final FiscalModel mod ) {
		return getLastPeriodCritCajaVatBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID)
				.and(ALCATRAZ.FINANCE.equal(FINANCE.ID))
				.and(ALCATRAZ.FINANCE_TRACKING.isNull()))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodCritCajaFiller())
			;
	}

	// ***********************************
	// ***** FILLER CRITERIO DE CAJA *****
	// ***********************************
	
	private static class VatContextCritCajaFiller extends VatContextFiller {
		@Override
		public VatContext apply(Record rec) {
			VatContext vat = super.apply(rec);
			double invoiceTotal = rec.getValue(INVOICE.TOTAL);			
			double financeAmount = rec.getValue(FINANCE_TRACKING.AMOUNT);
			FinanceTrackingType type = FinanceTrackingType.safeValueOf(rec.getValue(FINANCE_TRACKING.TYPE));			
			if (type == FinanceTrackingType.RETURNED) {
				financeAmount = -financeAmount;
			}
			double base = AonMathUtils.round(financeAmount * vat.getBase() / invoiceTotal,4);
			double quota = AonMathUtils.round(base * vat.getPercentage() / 100);
			double surchargeQuota = AonMathUtils.round(base * vat.getSurchargePercent() / 100);
			double deductibleQuota = AonMathUtils.round( (quota + surchargeQuota)  * vat.getDeductiblePercent() / 100);
			return vat
				.setFinance(rec.getValue(FINANCE.ID))
				.setFinanceTracking(rec.getValue(FINANCE_TRACKING.ID))
				.setBase(base)
				.setQuota(quota)
				.setSurchargeQuota(surchargeQuota)
				.setDeductibleQuota(deductibleQuota)
				.setAmount347( vat.isOtherISP() || ((vat.isExtracommunityPurchase() || vat.isExtracommunityExpenses()) && vat.isService()) ? base : (base + quota + surchargeQuota) )
				.setInvoice(rec.getValue(INVOICE.ID));
//				.setAmount347(!vat.isOtherISP()
//					?(base + quota + surchargeQuota)
//					:base);
		}
		
	}

	// ****************************************************
	// ***** FILLER CRITERIO DE CAJA VTOS. PENDIENTES *****
	// ****************************************************
	private static class VatContextLastPeriodCritCajaFiller  extends VatContextFiller {
		@Override
		public VatContext apply(Record rec) {
			VatContext vat = super.apply(rec);
			double invoiceTotal = rec.getValue(INVOICE.TOTAL);			
			double financeAmount = rec.getValue(FINANCE.AMOUNT);
			double base = AonMathUtils.round(financeAmount * vat.getBase() / invoiceTotal,4);
			double quota = AonMathUtils.round(base * vat.getPercentage() / 100);
			double surchargeQuota = AonMathUtils.round(base * vat.getSurchargePercent() / 100);
			double deductibleQuota = AonMathUtils.round( (quota + surchargeQuota)  * vat.getDeductiblePercent() / 100);
			return vat
				.setFinance(rec.getValue(FINANCE.ID))
				.setBase(base)
				.setQuota(quota)
				.setSurchargeQuota(surchargeQuota)
				.setDeductibleQuota(deductibleQuota)
				.setAmount347( vat.isOtherISP() || ((vat.isExtracommunityPurchase() || vat.isExtracommunityExpenses()) && vat.isService()) ? base : (base + quota + surchargeQuota) );
//				.setAmount347(!vat.isOtherISP()
//					?(base + quota + surchargeQuota)
//					:base);
		}
		
	}

	// **************************************
	// ***** FILLER NO CRITERIO DE CAJA *****
	// **************************************
	private static class VatContextFiller implements Function<Record,VatContext> {

		@Override
		public VatContext apply(Record rec) {
			
			InvoiceTransactionType invoiceTransactionType = InvoiceTransactionType.safeValueOf( rec.getValue(INVOICE.TRANSACTION));
			InvoiceType invoiceType = InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE));
			boolean isService = (rec.getValue(INVOICE.SERVICE) == 1);

			return new VatContext()
				.setInvoice(rec.getValue(INVOICE.ID))
				.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
				.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setEpigraph(rec.getValue(IAE.EPIGRAPH))
				.setVatRegime( VATRegime.safeValueOf( rec.getValue(ENTERPRISE_ACTIVITY.VAT_REGIME) ))
				.setVatSurchargeRegime( AonEnumUtils.getBoolean(rec.getValue(ENTERPRISE_ACTIVITY.SURCHARGE) ) )		
				.setDocumentNumber(FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)), rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 
				.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
				.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(rec.getValue(INVOICE.RNAME))	
				.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
				.setInvoiceType(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)))
				.setRectificationType(RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)))
				.setService(rec.getValue(INVOICE.SERVICE) == 1 || InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.EXPENSES)
				.setTransaction(InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)))
				.setInvestment(rec.getValue(INVOICE.INVESTMENT) == 1)
				.setVatAccrualRegime(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
				.setFarmerRegime(rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
				.setVatDeductionType(VatDeductionType.safeValueOf(rec.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
				.setInvestAsset(rec.getValue(INVOICE_DETAIL.INVEST_ASSET))
				
				.setVatImportation(AonEnumUtils.getBoolean(rec.getValue(INVOICE_FISCAL.VAT_IMPORTATION)))
				.setDuaLinked(rec.getValue(INVOICE_DUA.ID) != null)
				
				.setBase( rec.getValue(INVOICE_TAX.BASE) )
				.setPercentage(rec.getValue(INVOICE_TAX.PERCENTAGE))
				.setQuota( getQuota(rec) )
				
				.setSurcharge(AonMathUtils.round(rec.getValue(INVOICE_TAX.SURCHARGE)) > 0)
				.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE))
				.setSurchargeQuota(getSurchargeQuota(rec))
	
				.setDeductiblePercent(getDeductiblePercent(rec))
				.setDeductibleQuota(getDeductibleQuota(rec))
				
				.setAmount347( invoiceTransactionType == InvoiceTransactionType.OTHER_ISP 
				  || ((invoiceType == InvoiceType.PURCHASE || invoiceType == InvoiceType.EXPENSES) && invoiceTransactionType == InvoiceTransactionType.EXTRACOMMUNITY && isService)				
				? rec.getValue(INVOICE_TAX.BASE) : (rec.getValue(INVOICE_TAX.BASE) + getQuota(rec) + getSurchargeQuota(rec)) )
				
//				.setAmount347(
//					InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)) != InvoiceTransactionType.OTHER_ISP 
//						? (rec.getValue(INVOICE_TAX.BASE) + getQuota(rec) + getSurchargeQuota(rec)) 
//						: rec.getValue(INVOICE_TAX.BASE))
				
				.setHasRetention( hasRetention(
						rec.getValue(INVOICE.WITHHOLDING),
						rec.getValue(INVOICE_DETAIL.SOURCE),
						rec.getValue(INVOICE.RETENTION_QUOTA)))
				.setRegistry(rec.getValue(INVOICE.REGISTRY))
			;
		}
		
		private boolean hasRetention(Byte withholding, Byte source, Double retentionQuota) {
			boolean retention =	AonEnumUtils.getBoolean(withholding);		
			if (retention) {
				InvoiceSource invoiceSource = InvoiceSource.safeValueOf(source);
				if (invoiceSource != InvoiceSource.ACCOUNT && invoiceSource != InvoiceSource.TEDI) {  // Viene de gestión
					retention = AonMathUtils.isNotZero(retentionQuota);
				}
			}
			return retention;
		}
		
		private double getQuota( Record rec) {
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			if (AonMathUtils.isZero(quota)) {
				double base = rec.getValue(INVOICE_TAX.BASE);
				double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
		private double getSurchargeQuota( Record rec) {
			double quota = rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA);
			if (AonMathUtils.isZero(quota)) {
				double base = rec.getValue(INVOICE_TAX.BASE);
				double percent = rec.getValue(INVOICE_TAX.SURCHARGE);
				quota = AonMathUtils.round(base * percent / 100);
			}
			return quota;
		}
		private double getDeductiblePercent( Record rec) {
			double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
			if (AonMathUtils.isZero(percent)) percent = 100;
			return percent;
		}
		private double getDeductibleQuota( Record rec) {
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

	}
	
	
	
	private static Condition getWhere(AccountingReportParams params) {
		Condition condition = INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() );
		
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// Sólo las comunes. Los "sin activdad".
				condition = condition.and( INVOICE.ACTIVITY.isNull());
			} else {
				condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
			}
		}
		if(params.getInvoices() != null){
			condition = condition.and( INVOICE.ID.in( params.getInvoices() ));
		}
			
		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			condition = condition.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
		}
		
		if (params.getAccrualRegime() != null) {
			condition = condition.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		
		if (params.getInvestment() != null) {
			condition = condition.and( INVOICE.INVESTMENT.eq( AonEnumUtils.getByte(params.getInvestment())));
		}
		
		if (params.getRectificationType() != null) {
			condition = condition.and( INVOICE.RECTIFICATION_TYPE.eq( params.getRectificationType().value()));
		}
		
		if (params.getService() != null) {
			if ( params.getService().booleanValue() ) {
				condition = condition.and( INVOICE.SERVICE.eq((byte)1).or( INVOICE.TYPE.eq( InvoiceType.EXPENSES.value())));
			} else {
				condition = condition.and( INVOICE.SERVICE.ne((byte)1).and( INVOICE.TYPE.ne( InvoiceType.EXPENSES.value())));
			}
		}
		
		if (params.getPercent() != null) {
			condition = condition.and( INVOICE_TAX.PERCENTAGE.eq( params.getPercent()));
		}
		
		if (params.getSurchargePercent() != null) {
			condition = condition.and( INVOICE_TAX.SURCHARGE.eq( params.getSurchargePercent()));
		}
		
		if (params.getOutput() != null) {
			if (params.getOutput().booleanValue()) {
				condition = condition.and( INVOICE.TYPE.eq( InvoiceType.SALES.value() ));
			} else {
				condition = condition.and( INVOICE.TYPE.in( InvoiceType.EXPENSES.value(), InvoiceType.PURCHASE.value() ));
			}
		}
			
		if (params.getVatSummaryType() != null) {
			if (params.getVatSummaryType() == VatSummaryType.NATIONAL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(false)));
			} else if (params.getVatSummaryType() == VatSummaryType.SURCHARGE){	
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.SURCHARGE.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.FARMER) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.INTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.INTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.EXTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.EXTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.CAN_CEU_MEL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.CAN_CEU_MEL.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.OTHER_ISP) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.OTHER_ISP.value() ));
			}
		}
		return condition;
	}
	
	// ***********************************************
	// ****** RESUMEN PANEL DE CONTROL DE IVA ********
	// ***********************************************
	
	public static Double getVatSummaryEstimation(AONContext ctx, AccountingReportParams params) {
		Stream<VatSummaryContext> vatSummaryContexts = getVatSummary(ctx, params);
		TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>> map = sort( vatSummaryContexts.collect(Collectors.toList()) );
		
		double estimation = 0;

		for (Entry<VatSummaryType, TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>>> entry :  map.entrySet() ) {
			VatSummaryType type = entry.getKey();
			TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>> value = entry.getValue();
			
			double typeOutputQuota = 0;
			double typeInputDeductibleQuota = 0;

			for (Pair<VatSummaryContext,VatSummaryContext> pair : value.values() ) {
				if (pair.getLeft() != null) typeOutputQuota = typeOutputQuota + pair.getLeft().getQuota();
				if (pair.getRight() != null) typeInputDeductibleQuota = typeInputDeductibleQuota + pair.getRight().getDeductibleQuota();
			}
				
			if (type == VatSummaryType.NATIONAL
				|| type == VatSummaryType.SURCHARGE
				|| type == VatSummaryType.FARMER) {
					estimation = estimation + typeOutputQuota;	
					estimation = estimation - typeInputDeductibleQuota;
				}
		}
		
		return estimation;
	}

	private static TreeMap<VatSummaryType, TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>>> sort(List<VatSummaryContext> data) {
		TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>> map = new TreeMap<>();
		for (VatSummaryContext vat : data){
			TreeMap<Double,Pair<VatSummaryContext,VatSummaryContext>> block = map.get(vat.getSummaryType());
			if (block == null) {
				block = new TreeMap<>();
				map.put(vat.getSummaryType(), block);
			}
			Pair<VatSummaryContext,VatSummaryContext> line = block.get(vat.getPercentage());
			if (line == null) {
				line = Pair.of(vat.isOutput()?vat:null, vat.isOutput()?null:vat);
			} else {
				line = Pair.of(vat.isOutput()?vat:line.getLeft(), vat.isOutput()?line.getRight():vat);
			}
			block.put(vat.getPercentage(), line);					
		}
		return map;
	}
	
	public static Stream<VatSummaryContext> getVatSummary(AONContext ctx, AccountingReportParams params) {
		LinkedList<VatSummaryContext> list = new LinkedList<>();
		getVatBreakdown(ctx, params)
			.map( vat -> { 
				if (vat.isSurcharge()) {
					VatSummaryType type = VatSummaryType.SURCHARGE;
					double percent = vat.getSurchargePercent();
					VatSummaryContext sum = null;
					for (VatSummaryContext ite : list) {
						if ( (ite.isOutput() == vat.isSales()) 
							&& ite.getSummaryType() == type 
							&& AonNumberUtils.equals(ite.getPercentage(), percent)) {
							sum = ite;
							break;
						}
					}
					if ( sum == null) {
						sum = new VatSummaryContext()
							.setOutput(vat.isSales())
							.setSummaryType(type)
							.setPercentage(percent);
						list.add(sum);
					}
					sum.setBase( sum.getBase() + vat.getBase()); 
					sum.setQuota( sum.getQuota() + vat.getSurchargeQuota());
				}
				return vat;
			})
			.forEach( vat -> {
				VatSummaryType type = VatSummaryType.accept(vat);
				double percent = vat.getPercentage();
				VatSummaryContext sum = null;
				for (VatSummaryContext ite : list) {
					if ( (ite.isOutput() == vat.isSales()) 
						&& ite.getSummaryType() == type 
						&& AonNumberUtils.equals(ite.getPercentage(), percent)) {
						sum = ite;
						break;
					}
				}
				if ( sum == null) {
					sum = new VatSummaryContext()
						.setOutput(vat.isSales())
						.setSummaryType(type)
						.setPercentage(percent);
					list.add(sum);
				}
				sum.setBase( sum.getBase() + vat.getBase()); 
				sum.setQuota( sum.getQuota() + vat.getQuota());
				sum.setDeductibleQuota( sum.getDeductibleQuota() + vat.getDeductibleQuota());
			});
		return list.stream();
	}
	
	// ******************************************
	// ******** DESGLOSE DE IVA *****************
	// ******************************************
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, AccountingReportParams params) {
		return Stream.of(
			 getCommonVatBreakdown(ctx,params)
			,getCritCajaVatBreakdown(ctx,params))
			.flatMap(vt -> vt);
	}
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, IFiscalModel mod) {
		AccountingReportParams params = new AccountingReportParams()
			.setFromDate(getStartDate(mod))
			.setToDate(getEndDate(mod));
		return Stream.of(
			 getCommonVatBreakdown(ctx,params)
			,getCritCajaVatBreakdown(ctx,params)
			,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
				?getLastPeriodCritCajaVatBreakdown(ctx, mod)
				:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}

	public static Stream<VatContext> getVatBreakdown(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		return Stream.of(
			 getCommonVatBreakdown(ctx,params,invoiceFilter)
			,getCritCajaVatBreakdown(ctx,params,invoiceFilter))
			.flatMap(vt -> vt);
	}
	
	public static Integer getVatBreakdownCount(AONContext ctx, AccountingReportParams params, InvoiceFilter invoiceFilter) {
		return getCommonVatBreakdownCount(ctx,params,invoiceFilter) + getCritCajaVatBreakdownCount(ctx,params,invoiceFilter);
	}
	
	// *************************************
	// **** DESGLOSE DE IVA NO ALCATRAZ ****
	// *************************************
	public static Stream<VatContext> getNotInModelVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
			 getNotInModelCommonVatBreakdown(ctx,mod)
			,getNotInModelCritCajaVatBreakdown(ctx,mod)
			,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
				?getNotInModelLastPeriodCritCajaVatBreakdown(ctx, mod)
				:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}

	// **********************************
	// **** DESGLOSE DE IVA ALCATRAZ ****
	// **********************************
	public static Stream<VatContext> getModelVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
				 getModelCommonVatBreakdown(ctx,mod)
				,getModelCritCajaVatBreakdown(ctx,mod)
				,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
					?getModelLastPeriodCritCajaVatBreakdown(ctx, mod)
					:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}
	


	// ******************************************************************	
	// ************ Facturas CRITERIO DE CAJA ***************************	
	// ******************************************************************	
	public static Stream<VatContext> getCritCajaInvoices(final AONContext ctx, final FiscalModel mod) {
		return getCommonSelect(ctx)	// Faturas con criterio de caja.
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE) )	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}

	// ******************************************************************	
	// ************ Facturas CRITERIO DE CAJA Declaradas ****************	
	// ******************************************************************	
	public static Stream<VatContext> getModelCritCajaInvoices(final AONContext ctx, final FiscalModel mod) {
		return getCommonSelect(ctx)	// Facturas con criterio de caja.
				.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID)
					.and(ALCATRAZ.FINANCE.isNull())
					.and(ALCATRAZ.FINANCE_TRACKING.isNull()))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE) )	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}

	// ******************************************************************	
	// ************ Facturas CRITERIO DE CAJA No Declaradas *************	
	// ******************************************************************	
	public static Stream<VatContext> getNotInModelCritCajaInvoices(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
				.from(ALCATRAZ)
				.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
				.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
				.and(ALCATRAZ.FINANCE.isNull())
				.and(ALCATRAZ.FINANCE_TRACKING.isNull())
				.and(FS_MODEL.YEAR.eq(mod.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
				.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
				.asTable(MODEL_INVOICE)
			;
		return getCommonSelect(ctx)	// Faturas con criterio de caja.
				.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE) )	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}



	/**
	 * Busqueda de datos pendientes por declarar en facturas NO CRITERIO DE CAJA
	 */
	private static Stream<VatContext> getPreviousNotInModelCommonVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(ALCATRAZ.FINANCE.isNull())
			.and(ALCATRAZ.FINANCE_TRACKING.isNull())
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getCommonSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(mod.getDomain()))
			.and(INVOICE.TAX_DATE.ge(getYearFirstDay(mod)))
			.and(INVOICE.TAX_DATE.lt(AonDateUtils.toSql( FiscalUtils.getPeriodStart(mod))))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
			.orderBy(INVOICE.TAX_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map(new VatContextFiller())
			;
	}
	private static Stream<VatContext> getPreviousNotInModelCritCajaVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		java.sql.Date modYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(mod.getYear()) );
		java.sql.Date periodFirstDay = AonDateUtils.toSql( FiscalUtils.getPeriodStart(mod));
		Table<Record3<Integer,Integer,Integer>> modelInvoice = ctx.getDslContext()
			.select( ALCATRAZ_INVOICE_ID, ALCATRAZ_FINANCE_ID, ALCATRAZ_FINANCE_TRACKING_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(ALCATRAZ.FINANCE.isNotNull())
			.and(ALCATRAZ.FINANCE_TRACKING.isNotNull())
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getCritCajaSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID)
				.and(ALCATRAZ_FINANCE_ID.equal(FINANCE.ID))
				.and(ALCATRAZ_FINANCE_TRACKING_ID.equal(FINANCE_TRACKING.ID)))
			.where(INVOICE.DOMAIN.equal(mod.getDomain()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between( modYearFirstDay, periodFirstDay ))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy(INVOICE.TAX_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map(new VatContextFiller())
			;
	}
	
	// ********************************************
	// **** DESGLOSE DE IVA PREVIO NO ALCATRAZ ****
	// ********************************************
	public static Stream<VatContext> getPreviousNotInModelVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
			 getPreviousNotInModelCommonVatBreakdown(ctx,mod)
			,getPreviousNotInModelCritCajaVatBreakdown(ctx,mod)
			).flatMap(vt -> vt);
	}
}
