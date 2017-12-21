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
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.VATFilter;
import com.esferalia.aon.occam.api.model.finance.VATProperties;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VATDAO  {
	
	
	private static final VATPropertiesDAO VAT_PROPERTIES = new VATPropertiesDAO();
	private static class VATPropertiesDAO extends VATDAO implements VATProperties {

		private Condition[] getConditions(VATFilter filter) {
			if (filter == null) {
				return new Condition[]{DSL.trueCondition()};
			}
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[]{DSL.trueCondition()};

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getInvoiceIdProperty() { return new FilterDAO.PropertyDAO<Integer>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty() { return new FilterDAO.PropertyDAO<Integer>(INVOICE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() { return new FilterDAO.PropertyDAO<Integer>(INVOICE.REGISTRY);}
		@Override public Property<Byte> getInvoiceTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.TYPE);}
		@Override public Property<Byte> getInvoiceTransactionProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.TRANSACTION);}
		@Override public Property<Integer> getActivityProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE.ACTIVITY);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.INVESTMENT);}
		@Override public Property<Byte> getServiceProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.SERVICE);}
		@Override public Property<Byte> getRectifiedProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Byte> getAccrualRegimeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getFarmerRegimeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE.SURCHARGE);}
		@Override public Property<Double> getPercentProperty() {return new FilterDAO.PropertyDAO<Double>(INVOICE_TAX.PERCENTAGE);}
		@Override public Property<Double> getSurchargePercentProperty() {return new FilterDAO.PropertyDAO<Double>(INVOICE_TAX.SURCHARGE);}
	}

	public static LinkedList<VatSummaryContext> getVatSummary(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		LinkedList<VatSummaryContext> list = new LinkedList<VatSummaryContext>();
		getVatBreakdown(ctx, fromDate, toDate, filter)
				.peek( vat -> { 
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
		return list;
	}
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate) {
		return getVatBreakdown(ctx, fromDate, toDate,null , null );
	}
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate, Mod303 mod303) {
		return getVatBreakdown(ctx, fromDate, toDate,null , mod303);
	}
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		return getVatBreakdown(ctx, fromDate, toDate,filter , null);
	}
	
	public static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter, Mod303 mod303) {
		return Stream.concat(
				 getNoAccrualVatBreakdown(ctx,fromDate,toDate,filter)
				,getAccrualVatBreakdown	 (ctx,fromDate,toDate,filter)
				
				)
			.peek( vat -> vat.setInsidePeriod(mod303==null ? false :FiscalUtils.isInPeriodRange(mod303, vat.getTaxDate() ) ))
		;
	}

	private static Stream<VatContext> getNoAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
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
				,INVOICE_TAX.BASE
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_PERCENT
				,INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
				.where(VAT_PROPERTIES.getConditions(filter))
				.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 0))	// No Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.map(new VatContextFiller())
				;
	}
	public static Stream<VatContext> getAccrualBreakdown(AONContext ctx, Date fromDate, Date toDate, Mod303 mod303) {
		return getAccrualBreakdown(ctx, fromDate, toDate, null, mod303); 
	}
	private static Stream<VatContext> getAccrualBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter , Mod303 mod303) {
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
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
				,INVOICE_TAX.BASE
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_PERCENT
				,INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
				.where(VAT_PROPERTIES.getConditions(filter))
				.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.map(new VatContextFiller())
				.peek( vat -> vat.setInsidePeriod(mod303==null ? false :FiscalUtils.isInPeriodRange(mod303, vat.getTaxDate() ) ))
				;
	}
	
	private static Stream<VatContext> getAccrualVatBreakdown(AONContext ctx, Date fromDate, Date toDate, VATFilter filter) {
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
		int prevYear = AonDateUtils.getYear(fromDate) - 1;
		java.sql.Date prevYearFirstDay = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(prevYear) );
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
			,INVOICE_TAX.BASE
			,INVOICE_TAX.PERCENTAGE
			,INVOICE_TAX.QUOTA
			,INVOICE_TAX.SURCHARGE
			,INVOICE_TAX.SURCHARGE_QUOTA
			,INVOICE_TAX.DEDUCTIBLE_PERCENT
			,INVOICE_TAX.DEDUCTIBLE_QUOTA
			,INVOICE_TAX.VAT_DEDUCTION_TYPE
			
			,INVOICE.TOTAL
			,FINANCE_TRACKING.TYPE
			,FINANCE_TRACKING.AMOUNT
			)
			.from(FINANCE_TRACKING)
			.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
			.join(INVOICE).on(INVOICE.ID.equal(FINANCE.INVOICE))
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
			.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
			.fetch()
			.stream()
			.map(new VatContextAccrualRegimeFiller())
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
				
				,INVOICE_DETAIL.TAXABLE_BASE, INVOICE_DETAIL.INVEST_ASSET, INVOICE_DETAIL.DESCRIPTION
				
				,INVOICE_TAX.BASE, INVOICE_TAX.PERCENTAGE, INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE, INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_PERCENT, INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				
				,DATA_RESPONSE_DETAIL.DATA_VALUE
				
				,AMORTIZATION.PERCENTAGE, AMORTIZATION.DESCRIPTION, AMORTIZATION.INITIAL_DATE
			)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.leftOuterJoin(DATA_RESPONSE).on(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()).and(DATA_RESPONSE.SOURCE_ID.eq(INVOICE.ID)))
			.leftOuterJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq(status).and(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE.ID)))
			.leftOuterJoin(AMORTIZATION_INVOICE).on(AMORTIZATION_INVOICE.INVOICE.eq(INVOICE.ID))
			.leftOuterJoin(AMORTIZATION).on(AMORTIZATION.ID.eq(AMORTIZATION_INVOICE.AMORTIZATION))
			.where(VAT_PROPERTIES.getConditions(filter))
			.and(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
			.orderBy(InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
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
		double ded_quota = rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
		if (AonMathUtils.isZero(ded_quota)) {
			double percent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
			double quota = getQuota(rec);
			if (AonMathUtils.isZero(percent) || percent == 100) {
				ded_quota = quota;
			} else {
				ded_quota = AonMathUtils.round(quota * percent / 100);
			}
		}
		return ded_quota;
	}
	
	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Date fromDate,Date toDate) {
		return ctx.getDslContext().select( INVOICE_TAX.BASE )
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
				.and(INVOICE.TYPE.equal( InvoiceType.SALES.value() )) // VENTAS
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(fromDate),AonDateUtils.toSql(toDate)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
				.fetch()
				.stream()
				.mapToDouble( rec -> rec.getValue(INVOICE_TAX.BASE ) )
				.sum();
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Date fromDate,Date toDate) {
		return ctx.getDslContext().select( INVOICE_TAX.BASE,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.QUOTA)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
				.and(INVOICE.TYPE.equal( InvoiceType.SALES.value() )) // VENTAS
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(fromDate),AonDateUtils.toSql(toDate)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( (byte) 1) ) // Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.mapToDouble( rec -> {
					double quota = rec.getValue(INVOICE_TAX.QUOTA);
					if (AonMathUtils.isZero(quota)) {
						double base = rec.getValue(INVOICE_TAX.BASE);
						double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
						quota = AonMathUtils.round(base * percent / 100);
					}
					return quota;
				})
				.sum();
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Date fromDate,Date toDate) {
		return ctx.getDslContext().select( INVOICE_TAX.BASE )
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))
				.and(INVOICE.TYPE.notEqual( InvoiceType.SALES.value() )) // NO VENTAS
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(fromDate),AonDateUtils.toSql(toDate)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
				.fetch()
				.stream()
				.mapToDouble( rec -> rec.getValue(INVOICE_TAX.BASE ) )
				.sum();
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Date fromDate,Date toDate) {
		return ctx.getDslContext().select( INVOICE_TAX.BASE,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.QUOTA)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.VAT.value() ))
				.and(INVOICE.TYPE.notEqual( InvoiceType.SALES.value() )) // NO VENTAS
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(fromDate),AonDateUtils.toSql(toDate)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal( (byte) 1) ) // Criterio de Caja.
				.orderBy( InvoiceDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )
				.fetch()
				.stream()
				.mapToDouble( rec -> {
					double quota = rec.getValue(INVOICE_TAX.QUOTA);
					if (AonMathUtils.isZero(quota)) {
						double base = rec.getValue(INVOICE_TAX.BASE);
						double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
						quota = AonMathUtils.round(base * percent / 100);
					}
					return quota;
				})
				.sum();
	}
	
	
	
	public static class VatContextAccrualRegimeFiller  extends VatContextFiller {
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
			vat.setBase(base);
			vat.setQuota(quota);
			vat.setSurchargeQuota(surchargeQuota);
			vat.setDeductibleQuota(deductibleQuota);
			return vat;
		}
		
	}
	public static class VatContextFiller  implements Function<Record,VatContext> {

		@Override
		public VatContext apply(Record rec) {
			return new VatContext()
				.setInvoice(rec.getValue(INVOICE.ID))
				.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
				.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setEpigraph(rec.getValue(IAE.EPIGRAPH))
				.setVatRegime( VATRegime.safeValueOf( rec.getValue(ENTERPRISE_ACTIVITY.VAT_REGIME) ))
				.setVatSurchargeRegime( AonEnumUtils.getBoolean(rec.getValue(ENTERPRISE_ACTIVITY.SURCHARGE) ) )		
				.setDocumentNumber(FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE))
						, rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 
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
				
				.setBase( rec.getValue(INVOICE_TAX.BASE) )
				.setPercentage(rec.getValue(INVOICE_TAX.PERCENTAGE))
				.setQuota( getQuota(rec) )
				
				.setSurcharge(AonMathUtils.round(rec.getValue(INVOICE_TAX.SURCHARGE)) > 0)
				.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE))
				.setSurchargeQuota(getSurchargeQuota(rec))
	
				.setDeductiblePercent(getDeductiblePercent(rec))
				.setDeductibleQuota(getDeductibleQuota(rec))
			;
		}
	}
	
	public static class SiiVatContextFiller  implements Function<Record,VatContext> {

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
				
				.setSiiStatus(rec.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE) != null ? rec.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE) : "Pendiente")
				
				.setBase( rec.getValue(INVOICE_TAX.BASE) )
				.setPercentage(rec.getValue(INVOICE_TAX.PERCENTAGE))
				.setQuota( getQuota(rec) )
				.setSurcharge(AonMathUtils.round(rec.getValue(INVOICE_TAX.SURCHARGE)) > 0)
				.setSurchargePercent(rec.getValue(INVOICE_TAX.SURCHARGE))
				.setSurchargeQuota(getSurchargeQuota(rec))
	
				.setDeductiblePercent(getDeductiblePercent(rec))
				.setDeductibleQuota(getDeductibleQuota(rec))
				
				.setAmortizationDescription(rec.getValue(AMORTIZATION.DESCRIPTION))
				.setAmortizationInitialDate(rec.getValue(AMORTIZATION.INITIAL_DATE))
				.setAmortizationPercentage(rec.getValue(AMORTIZATION.PERCENTAGE))
			;
		}
	}
}
