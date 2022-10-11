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

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
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

public class VATDAO  {
	private static final String MODEL_INVOICE = "modelInvoice";
	private static final Field<Integer> ALCATRAZ_INVOICE_ID = ALCATRAZ.INVOICE.as("alcatrazInvoice");
	
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
			FINANCE.AMOUNT};
	private static final Field<?>[] FINANCE_TRACKING_FIELDS = new Field[]{
		FINANCE_TRACKING.TYPE	,FINANCE_TRACKING.AMOUNT};
	
	private VATDAO() {
	}
	
	private static final byte FALSE_BYTE = 0;
	private static final byte TRUE_BYTE = 1;
	
	private static java.sql.Date getYearFirstDay( final FiscalModel fm ) {
		return AonDateUtils.toSql( AonDateUtils.getYearFirstDay(fm.getYear()));
	}
	private static java.sql.Date getStartDate( final FiscalModel fm ) {
		return fm.isGenerateFromYearStart()
				?getYearFirstDay(fm)
				:AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));
	}
	private static java.sql.Date getEndDate( final FiscalModel fm ) {
		return AonDateUtils.toSql( FiscalUtils.getPeriodEnd(fm));
	}
	
	private static SelectOnConditionStep<Record> getNoAccrualSelect(AONContext ctx) {
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
	private static Stream<VatContext> getNoAccrualVatBreakdown(AONContext ctx, FiscalModel mod) {
		return getNoAccrualSelect(ctx)
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 0))	// No Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextFiller())
			;
	}
	
	private static SelectOnConditionStep<Record> getAccrualSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select( INVOICE_FIELDS )
			.select( INVOICE_DETAIL_FIELDS )
			.select( INVOICE_TAX_FIELDS )
			.select( ENTERPRISE_ACTIVITY_FIELDS )
			.select( INVOICE_DUA_FIELDS )
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
	
	
	private static Stream<VatContext> getAccrualVatBreakdown(AONContext ctx, FiscalModel mod) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		return getAccrualSelect(ctx)
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(FINANCE_TRACKING.TRACKING_DATE.between( getStartDate(mod), getEndDate(mod)))
				.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
				.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.map(new VatContextAccrualRegimeFiller())
				;
	}
	
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
				 getNoAccrualVatBreakdown(ctx,mod)
				,getAccrualVatBreakdown(ctx,mod)
				,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
					?getLastPeriodAccrualVatBreakdown(ctx, mod)
					:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}

	public static Stream<VatContext> getNotInModelVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
				 getNotInModelNoAccrualVatBreakdown(ctx,mod)
				,getNotInModelAccrualVatBreakdown(ctx,mod)
				,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
					?getNotInModelLastPeriodAccrualVatBreakdown(ctx, mod)
					:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}

	public static Stream<VatContext> getNotInModelNoAccrualVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getNoAccrualSelect(ctx)
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
	public static Stream<VatContext> getNotInModelAccrualVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date modYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(mod.getYear()) );
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getAccrualSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
//			.and(FINANCE_TRACKING.TRACKING_DATE.between( getStartDate(mod), getEndDate(mod)))
			.and(FINANCE_TRACKING.TRACKING_DATE.ge( modYearFirstDay ))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
			.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.stream()
			.map(new VatContextAccrualRegimeFiller())
			;
	}

	public static Stream<VatContext> getPreviousNotInModelVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getNoAccrualSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(mod.getDomain()))
			.and(INVOICE.TAX_DATE.ge(getYearFirstDay(mod)))
//			.and(INVOICE.TAX_DATE.lt(getStartDate(mod)))
			.and(INVOICE.TAX_DATE.lt(AonDateUtils.toSql( FiscalUtils.getPeriodStart(mod))))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
			.orderBy(INVOICE.TAX_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map(new VatContextFiller())
			;
	}
	
	private static SelectOnConditionStep<Record> getLastPeriodAccrualVatBreakdownSelect(AONContext ctx) {
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
	private static Stream<VatContext> getNotInModelLastPeriodAccrualVatBreakdown(AONContext ctx, final FiscalModel mod ) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.YEAR.eq(mod.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
			.asTable(MODEL_INVOICE)
		;
		return getLastPeriodAccrualVatBreakdownSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodAccrualRegimeFiller())
			;
	}

	private static Stream<VatContext> getLastPeriodAccrualVatBreakdown(AONContext ctx, final FiscalModel mod ) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		return getLastPeriodAccrualVatBreakdownSelect(ctx)
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodAccrualRegimeFiller())
		;
	}
	
	public static Stream<VatContext> getModelNoAccrualVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		return getNoAccrualSelect(ctx)
				.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
//					.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( FALSE_BYTE) )	// No Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}

	public static Stream<VatContext> getAccrualInvoices(final AONContext ctx, final FiscalModel mod) {
		return getNoAccrualSelect(ctx)	// Faturas con criterio de caja.
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE) )	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}

	public static Stream<VatContext> getModelAccrualInvoices(final AONContext ctx, final FiscalModel mod) {
		return getNoAccrualSelect(ctx)	// Faturas con criterio de caja.
				.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
					.and(INVOICE.TAX_DATE.between( getStartDate(mod), getEndDate(mod)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE) )	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.stream()
				.map(new VatContextFiller())
				;
	}

	public static Stream<VatContext> getNotInModelAccrualInvoices(final AONContext ctx, final FiscalModel mod) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
				.from(ALCATRAZ)
				.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
				.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
				.and(FS_MODEL.YEAR.eq(mod.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
				.and(FS_MODEL.MODEL.eq(mod.getModel().getValue()))
				.asTable(MODEL_INVOICE)
			;
		return getNoAccrualSelect(ctx)	// Faturas con criterio de caja.
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

	public static Stream<VatContext> getModelAccrualVatBreakdown(final AONContext ctx, final FiscalModel mod) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		return getAccrualSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
				.and(FINANCE_TRACKING.TRACKING_DATE.between( getStartDate(mod), getEndDate(mod)))
				.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
				.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.stream()
			.map(new VatContextAccrualRegimeFiller())
			;
	}
	
	private static Stream<VatContext> getModelLastPeriodAccrualVatBreakdown(AONContext ctx, final FiscalModel mod ) {
		int prevYear = mod.getYear() - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		return getLastPeriodAccrualVatBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
			.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(ALCATRAZ.FS_MODEL.eq(mod.getId()))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value()))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( TRUE_BYTE ))	// Criterio de Caja.
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodAccrualRegimeFiller())
			;
	}
	
	public static Stream<VatContext> getModelVatBreakdown(AONContext ctx, FiscalModel mod) {
		return Stream.of(
				 getModelNoAccrualVatBreakdown(ctx,mod)
				,getModelAccrualVatBreakdown(ctx,mod)
				,(mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) 
					?getModelLastPeriodAccrualVatBreakdown(ctx, mod)
					:Stream. <VatContext> empty()
			).flatMap(vt -> vt);
	}
	

	private static class VatContextAccrualRegimeFiller extends VatContextFiller {
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
				.setBase(base)
				.setQuota(quota)
				.setSurchargeQuota(surchargeQuota)
				.setDeductibleQuota(deductibleQuota)
				.setAmount347(!vat.isOtherISP()
					?(base + quota + surchargeQuota)
					:base);
		}
		
	}

	private static class VatContextLastPeriodAccrualRegimeFiller  extends VatContextFiller {
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
				.setBase(base)
				.setQuota(quota)
				.setSurchargeQuota(surchargeQuota)
				.setDeductibleQuota(deductibleQuota)
				.setAmount347(!vat.isOtherISP()
					?(base + quota + surchargeQuota)
					:base);
		}
		
	}

	private static class VatContextFiller implements Function<Record,VatContext> {

		@Override
		public VatContext apply(Record rec) {
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
				
				.setAmount347(InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)) != InvoiceTransactionType.OTHER_ISP ? ( rec.getValue(INVOICE_TAX.BASE) + getQuota(rec) + getSurchargeQuota(rec)) : rec.getValue(INVOICE_TAX.BASE))
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
	
}
