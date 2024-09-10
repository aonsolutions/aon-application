package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.AmortizationInvoice.AMORTIZATION_INVOICE;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.Filters.VATFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Properties.VATProperties;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

/**
 * @deprecated use com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO
 *
 */
@Deprecated
public class OLDVATDAO  {
	
	private OLDVATDAO() {
		
	}
	
	private static final VATPropertiesDAO VAT_PROPERTIES = new VATPropertiesDAO();
	private static class VATPropertiesDAO implements VATProperties {

		private Condition[] getConditions(VATFilter filter) {
			if (filter == null) {
				return new Condition[]{DSL.trueCondition()};
			}
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[]{DSL.trueCondition()};

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getInvoiceIdProperty() { return new FilterDAO.PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty() { return new FilterDAO.PropertyDAO<>(INVOICE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() { return new FilterDAO.PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<Byte> getInvoiceTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<Byte> getInvoiceTransactionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TRANSACTION);}
		@Override public Property<Integer> getActivityProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ACTIVITY);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.INVESTMENT);}
		@Override public Property<Byte> getServiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERVICE);}
		@Override public Property<Byte> getRectifiedProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Byte> getAccrualRegimeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getFarmerRegimeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SURCHARGE);}
		@Override public Property<Double> getPercentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.PERCENTAGE);}
		@Override public Property<Double> getSurchargePercentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.SURCHARGE);}
	}

	public static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate, IFiscalModel mod) {
		return getVatBreakdown(ctx, fromDate, toDate,null , mod);
	}

	private static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter, IFiscalModel mod) {
		if (mod != null && (mod.isLastPeriod() || mod.getPeriod() == Period.YEAR) ) {
			return Stream.concat(Stream.concat(getNoAccrualVatBreakdown(ctx,fromDate,toDate,filter)
											  ,getAccrualVatBreakdown	 (ctx,fromDate,toDate,filter))
								,getLastPeriodAccrualVatBreakdown(ctx,fromDate,toDate,filter))
					.map( vat -> vat.setInsidePeriod(mod!=null && FiscalUtils.isInPeriodRange(mod, vat.getTaxDate() ) ));
		} else {
			return Stream.concat(getNoAccrualVatBreakdown(ctx,fromDate,toDate,filter)
								,getAccrualVatBreakdown	 (ctx,fromDate,toDate,filter))
					.map( vat -> vat.setInsidePeriod(mod!=null && FiscalUtils.isInPeriodRange(mod, vat.getTaxDate() ) ));
		}
	}
	
	private static SelectSelectStep<Record> getCommonSelect( AONContext ctx ) {
		return ctx.getDslContext().select(
				 INVOICE.ID
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.SERVICE
				,INVOICE.TRANSACTION
				,INVOICE.INVESTMENT
				,INVOICE.WITHHOLDING_FARMER
				,INVOICE.VAT_ACCRUAL_PAYMENT
				,ENTERPRISE_ACTIVITY.ID
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,ENTERPRISE_ACTIVITY.VAT_REGIME
				,ENTERPRISE_ACTIVITY.SURCHARGE
				,IAE.EPIGRAPH
				,INVOICE_DETAIL.TAXABLE_BASE
				,INVOICE_DETAIL.INVEST_ASSET
				,INVOICE_DETAIL.SOURCE
				,INVOICE_TAX.BASE
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_PERCENT
				,INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				,INVOICE_FISCAL.VAT_IMPORTATION
				,INVOICE_DUA.ID
				,INVOICE.WITHHOLDING
				,INVOICE.RETENTION_QUOTA
				,INVOICE.REGISTRY
			);		
	}

	private static Stream<VatContext> getNoAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
		return getCommonSelect( ctx )
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
				.where(VAT_PROPERTIES.getConditions(filter))
				.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 0))	// No Criterio de Caja.
				.orderBy( InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.map(new VatContextFiller())
				;
	}

	private static Stream<VatContext> getAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
		int prevYear = AonDateUtils.getYear(fromDate) - 1;
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		return getCommonSelect(ctx)
			.select(INVOICE.TOTAL
				,FINANCE.ID
				,FINANCE_TRACKING.TYPE
				,FINANCE_TRACKING.AMOUNT)
			.from(FINANCE_TRACKING)
			.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(VAT_PROPERTIES.getConditions(filter))
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE_TRACKING.TYPE.in(FinanceTrackingType.PAID.value(),FinanceTrackingType.RETURNED.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.and(INVOICE.TAX_DATE.ge(prevYearFirstDay))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
			.orderBy( InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextAccrualRegimeFiller())
		;
	}
	
	private static Stream<VatContext> getLastPeriodAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		int prevYear = AonDateUtils.getYear(fromDate) - 1;
		java.sql.Date firstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
		java.sql.Date lastDay = AonDateUtils.toSql( AonDateUtils.getYearLastDay(prevYear) );
		return getCommonSelect(ctx)
			.select(
				INVOICE.TOTAL
				,FINANCE.ID
				,FINANCE.AMOUNT
			)
			.from(FINANCE)
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(VAT_PROPERTIES.getConditions(filter))
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
			.orderBy( InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodAccrualRegimeFiller())
		;
	}

	// ** **
	public static Stream<VatContext> getPeriodPendingAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate );
		return getCommonSelect(ctx)
			.select(
				INVOICE.TOTAL
				,FINANCE.ID
				,FINANCE.AMOUNT
			)
			.from(FINANCE)
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_DUA).on(INVOICE_DUA.INVOICE_IMPORT.equal(INVOICE.ID))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(VAT_PROPERTIES.getConditions(filter))
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(FINANCE.STATUS.eq(FinanceStatus.PENDING.value()))
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
			.orderBy( InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextLastPeriodAccrualRegimeFiller())
			.map(vat -> vat.setFinancePending(true))
		;
	}

	public static Stream<VatContext> getSiiVatContext(AONContext ctx, VATFilter filter, String sii) {
		String status = "status";
		if("intracomunitarias".equals(sii)) status = "status_intra";
		if("bienes".equals(sii)) status = "status_bienes";
		
		return ctx.getDslContext().select(
				 INVOICE.ID, INVOICE.SERIES, INVOICE.NUMBER, INVOICE.REFERENCE_CODE
				,INVOICE.RDOCUMENT, INVOICE.RDOCUMENT_TYPE, INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME, INVOICE.ISSUE_DATE, INVOICE.TAX_DATE, INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE, INVOICE.SERVICE, INVOICE.TRANSACTION
				,INVOICE.INVESTMENT, INVOICE.WITHHOLDING_FARMER, INVOICE.VAT_ACCRUAL_PAYMENT
				,INVOICE.TOTAL, INVOICE.REGISTRY, INVOICE.RECTIFICATION_INVOICE, INVOICE.CREATION_DATE
				,INVOICE.TAXABLE_BASE, INVOICE.VAT_QUOTA, INVOICE.CREATION_DATE
				,INVOICE_DETAIL.TAXABLE_BASE, INVOICE_DETAIL.INVEST_ASSET, INVOICE_DETAIL.DESCRIPTION
				
				,INVOICE_TAX.BASE, INVOICE_TAX.PERCENTAGE, INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE, INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_PERCENT, INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				
				,DATA_RESPONSE_DETAIL.DATA_VALUE
				
				,AMORTIZATION.PERCENTAGE, AMORTIZATION.DESCRIPTION, AMORTIZATION.INITIAL_DATE, INVOICE_DETAIL.PREPAYMENT
			)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID)
					.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
					.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1)))
			.leftOuterJoin(DATA_RESPONSE).on(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()).and(DATA_RESPONSE.SOURCE_ID.eq(INVOICE.ID)))
			.leftOuterJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq(status).and(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE.ID)))
			.leftOuterJoin(AMORTIZATION_INVOICE).on(AMORTIZATION_INVOICE.INVOICE.eq(INVOICE.ID))
			.leftOuterJoin(AMORTIZATION).on(AMORTIZATION.ID.eq(AMORTIZATION_INVOICE.AMORTIZATION))
			.where(VAT_PROPERTIES.getConditions(filter))
//			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
//			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.orderBy(InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch().stream().map(new SiiVatContextFiller())
		;
	}

		
	private static double getQuota( Record rec) {
		double quota = rec.getValue(INVOICE_TAX.QUOTA);
		if (AonMathUtils.isZero(quota)) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
			quota = AonMathUtils.round(base * percent / 100);
		}
		return quota;
	}
	private static double getSurchargeQuota( Record rec) {
		double quota = rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA);
		if (AonMathUtils.isZero(quota)) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.SURCHARGE);
			quota = AonMathUtils.round(base * percent / 100);
		}
		return quota;
	}
	private static double getDeductiblePercent( Record rec) {
		double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
		if (AonMathUtils.isZero(percent)) percent = 100;
		return percent;
	}

	private static double getDeductibleQuota( Record rec) {
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
	
	private static class VatContextAccrualRegimeFiller  extends VatContextFiller {
		@Override
		public VatContext apply(Record rec) {
			VatContext vat = super.apply(rec);
			double invoiceTotal = rec.getValue(INVOICE.TOTAL);			
			double financeAmount = rec.getValue(FINANCE_TRACKING.AMOUNT);
			FinanceTrackingType type = FinanceTrackingType.safeValueOf(rec.getValue(FINANCE_TRACKING.TYPE));			
			if (type == FinanceTrackingType.RETURNED) {
				financeAmount = -financeAmount;
			}
			return getVat( rec, vat, invoiceTotal, financeAmount);
		}
	}
	
	private static class VatContextLastPeriodAccrualRegimeFiller  extends VatContextFiller {
		@Override
		public VatContext apply(Record rec) {
			VatContext vat = super.apply(rec);
			double invoiceTotal = rec.getValue(INVOICE.TOTAL);			
			double financeAmount = rec.getValue(FINANCE.AMOUNT);
			return getVat( rec, vat, invoiceTotal, financeAmount);
		}
		
	}
	
	private static VatContext getVat(Record rec, VatContext vat, double invoiceTotal, double financeAmount) {
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
			//.setAmount347(!vat.isOtherISP()?(base + quota + surchargeQuota):base);
	}
	
	private static class VatContextFiller  implements Function<Record,VatContext> {

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
				
				//.setAmount347(InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)) != InvoiceTransactionType.OTHER_ISP ? ( rec.getValue(INVOICE_TAX.BASE) + getQuota(rec) + getSurchargeQuota(rec)) : rec.getValue(INVOICE_TAX.BASE))
				.setAmount347( invoiceTransactionType == InvoiceTransactionType.OTHER_ISP 
				  || ((invoiceType == InvoiceType.PURCHASE || invoiceType == InvoiceType.EXPENSES) && invoiceTransactionType == InvoiceTransactionType.EXTRACOMMUNITY && isService)				
				? rec.getValue(INVOICE_TAX.BASE) : (rec.getValue(INVOICE_TAX.BASE) + getQuota(rec) + getSurchargeQuota(rec)) )
								
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
		
	}
	
	
	private static class SiiVatContextFiller  implements Function<Record,VatContext> {

		@Override
		public VatContext apply(Record rec) {
			return new VatContext()
				.setInvoice(rec.getValue(INVOICE.ID))
				.setDocumentNumber(FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE))
						, rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 
				.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
				.setRegistry(rec.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(rec.getValue(INVOICE.RNAME))	
				.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
				.setRegContableDate(rec.getValue(INVOICE.CREATION_DATE))
				.setInvoiceType(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)))
				.setRectificationType(RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)))
				.setService(rec.getValue(INVOICE.SERVICE) == 1 || InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.EXPENSES)
				.setTransaction(InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)))
				.setInvestment(rec.getValue(INVOICE.INVESTMENT) == 1)
				.setVatAccrualRegime(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
				.setFarmerRegime(rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
				.setRectificationInvoice(rec.getValue(INVOICE.RECTIFICATION_INVOICE))
				.setVatDeductionType(VatDeductionType.safeValueOf(rec.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
				.setInvestAsset(rec.getValue(INVOICE_DETAIL.INVEST_ASSET))
				.setDetailDescription(rec.getValue(INVOICE_DETAIL.DESCRIPTION))
				.setPrepayment(rec.getValue(INVOICE_DETAIL.PREPAYMENT) == 1)
				.setSiiStatus(rec.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE) != null ? rec.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE) : "Pendiente")
				
				.setBase(rec.getValue(INVOICE_TAX.BASE) != null ? rec.getValue(INVOICE_TAX.BASE) : rec.getValue(INVOICE_DETAIL.TAXABLE_BASE))
				.setPercentage(rec.getValue(INVOICE_TAX.PERCENTAGE) != null ? rec.getValue(INVOICE_TAX.PERCENTAGE) : 0.0)
				.setQuota(rec.getValue(INVOICE_TAX.QUOTA) != null ? getQuota(rec): rec.getValue(INVOICE.VAT_QUOTA))
				.setSurcharge(rec.getValue(INVOICE_TAX.SURCHARGE) != null && AonMathUtils.round(rec.getValue(INVOICE_TAX.SURCHARGE)) > 0)
				.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE) != null ? rec.getValue(INVOICE_TAX.SURCHARGE) : 0.0)
				.setSurchargeQuota( rec.getValue(INVOICE_TAX.SURCHARGE_QUOTA) != null ? getSurchargeQuota(rec): 0.0)
	
				.setDeductiblePercent(rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT) != null ? getDeductiblePercent(rec) : 0.0)
				.setDeductibleQuota(rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA) != null ? getDeductibleQuota(rec) : 0.0)
				
				.setAmortizationDescription(rec.getValue(AMORTIZATION.DESCRIPTION))
				.setAmortizationInitialDate(rec.getValue(AMORTIZATION.INITIAL_DATE))
				.setAmortizationPercentage(rec.getValue(AMORTIZATION.PERCENTAGE))
				.setCreationDate(rec.getValue(INVOICE.CREATION_DATE));
		}
	}

}
