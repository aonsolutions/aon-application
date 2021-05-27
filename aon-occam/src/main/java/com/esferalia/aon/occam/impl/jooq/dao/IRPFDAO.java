package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.Filters.IRPFFilter;
import com.esferalia.aon.occam.api.model.finance.Properties.IRPFProperties;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class IRPFDAO extends FiscalModelDAO {
	
	private static final IRPFPropertiesDAO IRPF_PROPERTIES = new IRPFPropertiesDAO();
	private static class IRPFPropertiesDAO extends VATDAO implements IRPFProperties {

		private Condition[] getConditions(IRPFFilter filter) {
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
		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE_TAX.WITHHOLDING_TYPE);}
		@Override public Property<Double> getPercentProperty() {return new FilterDAO.PropertyDAO<Double>(INVOICE_TAX.PERCENTAGE);}
		@Override public Property<Double> getSurchargePercentProperty() {return new FilterDAO.PropertyDAO<Double>(INVOICE_TAX.SURCHARGE);}
	}

	// -------------------------------------------------------------------- STREAM FUNCTIONS

	// -------------------------------------------------------------------- SALARY
	public static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdown(ctx, fm, false);	
	}
	public static Stream<IrpfBreakdown> getSalaryDiffIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdown(ctx, fm, true);	
	}

	private static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm, final boolean diff) {
		java.sql.Date dateFrom = diff
				?AonDateUtils.toSql( AonDateUtils.getYearFirstDay(fm.getYear()))
				:AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));	
		java.sql.Date dateTo = AonDateUtils.toSql( FiscalUtils.getPeriodEnd(fm));
		final LinkedList<IrpfBreakdown> list = new LinkedList<IrpfBreakdown>();
		ctx.getDslContext()
			.select(SALARY.ISSUE_DATE
				,SALARY.EMPLOYEE_DOCUMENT,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE, SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE,SALARY.TOTAL_IRPF)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(SALARY.ISSUE_DATE.between(dateFrom,dateTo))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().getValue()))
			.fetch()
			.stream()
			.forEach( rec -> {
				Date issueDate = rec.field(SALARY.ISSUE_DATE) != null ? rec.getValue(SALARY.ISSUE_DATE): null;
				IrpfBreakdown br = new IrpfBreakdown()
						.setFromSalary(true)
						.setRegistryDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
						.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
						.setIssueDate(issueDate)
						.setTaxDate(issueDate)
						.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, issueDate) );
				double base = rec.getValue(SALARY.IRPF_BASE);
				if (AonMathUtils.isNotZero(base)) {
					double quota = rec.getValue(SALARY.TOTAL_IRPF);
					double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
					if (AonMathUtils.isZero(inKindBase)) {
						br.setInKind(false)
						.setBase(AonMathUtils.round( base))
						.setQuota(AonMathUtils.round( quota));
						list.add(br);
					} else {	
						double moneyBase = base;
						double moneyQuota = quota;
						double inKindQuota = 0;
						moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
						moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
						inKindQuota = AonMathUtils.round( quota - moneyQuota);
						br.setInKind(true)
						  .setBase(AonMathUtils.round( inKindBase))
						  .setQuota(inKindQuota);
						list.add(br);
						if (AonMathUtils.isNotZero(moneyBase) || AonMathUtils.isNotZero(moneyQuota)) {
							br = new IrpfBreakdown()
									.setFromSalary(true)
									.setRegistryDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
									.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
									.setIssueDate(issueDate)
									.setTaxDate(issueDate)
									.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, issueDate) )
									.setInKind(false)
									.setBase(AonMathUtils.round( moneyBase))
									.setQuota(moneyQuota);
							list.add(br);
						}
					}
				}

			});
		return list.stream();
		
	}
	// -------------------------------------------------------------------- STREAM FUNCTIONS

	// -------------------------------------------------------------------- INVOICE

	public static Stream<IrpfBreakdown> getOutputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getOutputInvoicesBreakdown(ctx, fm, false);
	}
	
	public static Stream<IrpfBreakdown> getOutputInvoicesDiffIrpfBreakdown(final AONContext ctx, final  FiscalModel fm) {
		return getOutputInvoicesBreakdown(ctx, fm, true);	
	}

	private static Stream<IrpfBreakdown> getOutputInvoicesBreakdown(final AONContext ctx, final FiscalModel fm, final boolean diff) {
		Date dateFrom = diff
				?AonDateUtils.getYearFirstDay(fm.getYear())
				:FiscalUtils.getPeriodStart(fm);
		Date dateTo = FiscalUtils.getPeriodEnd(fm);
		return getInvoicesIrpfBreakdown(ctx,fm.getDomain(),dateFrom,dateTo, p -> p.getInvoiceTypeProperty().eq(InvoiceType.SALES.value()))
				.peek( br -> br.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, br.getTaxDate() )));
	}

	public static Stream<IrpfBreakdown> getInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInputInvoicesIrpfBreakdown(ctx, fm, false);
	}
	
	public static Stream<IrpfBreakdown> getInputInvoicesDiffIrpfBreakdown(final AONContext ctx, final  FiscalModel fm) {
		return getInputInvoicesIrpfBreakdown(ctx, fm, true);	
	}

	private static Stream<IrpfBreakdown> getInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm, final boolean diff) {
		Date dateFrom = diff
				?AonDateUtils.getYearFirstDay(fm.getYear())
				:FiscalUtils.getPeriodStart(fm);
		Date dateTo = FiscalUtils.getPeriodEnd(fm);
		return getInvoicesIrpfBreakdown(ctx,fm.getDomain(),dateFrom,dateTo, p -> p.getInvoiceTypeProperty().ne(InvoiceType.SALES.value()))
				.peek( br -> br.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, br.getTaxDate() )));
	}
	
	private static Stream<IrpfBreakdown> getInvoicesIrpfBreakdown(final AONContext ctx, int domain, Date dateFrom, Date dateTo, IRPFFilter filter) {
		return 	ctx.getDslContext()
				.select( INVOICE.ID
						,INVOICE.TYPE
						,INVOICE.SERIES
						,INVOICE.NUMBER
						,INVOICE.REFERENCE_CODE
						,INVOICE.ISSUE_DATE
						,INVOICE.TAX_DATE
						
						,INVOICE.RDOCUMENT
						,INVOICE.RDOCUMENT_TYPE
						,INVOICE.RDOCUMENT_COUNTRY
						,INVOICE.RNAME
						
						,ENTERPRISE_ACTIVITY.ID
						,ENTERPRISE_ACTIVITY.DESCRIPTION
						,ENTERPRISE_ACTIVITY.RETENTION_REGIME
						
						,IAE.EPIGRAPH
						
						,INVOICE_TAX.WITHHOLDING_TYPE
						,INVOICE_TAX.BASE
						,INVOICE_TAX.PERCENTAGE
						,INVOICE_TAX.QUOTA
						,INVOICE_TAX.DEDUCTIBLE_PERCENT
						,INVOICE_TAX.DEDUCTIBLE_QUOTA
						
						,RADDRESS.ZIP
						,RADDRESS.CITY
					)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
					.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
					.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
					.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
					.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(INVOICE.REGISTRY)).and(RADDRESS.TYPE.eq((byte) 0) )
					.where(IRPF_PROPERTIES.getConditions(filter))
						.and(INVOICE.DOMAIN.equal(domain))
						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo)))
						.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
						
					.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
					.fetch()
					.stream()
					.map( new IrpfInvoiceBreakdown() );		
	}

	private static Stream<IrpfBreakdown> getGroupedInvoicesIrpfBreakdown(final AONContext ctx, int domain, Date dateFrom, Date dateTo, IRPFFilter filter) {
		return 	ctx.getDslContext()
			.select( INVOICE.ID
					,INVOICE.TYPE
					,INVOICE.SERIES
					
					,INVOICE.RDOCUMENT
					,INVOICE.RNAME
					
					,INVOICE_TAX.BASE.sum().as("INVOICE_TAX.BASE_SUM")
					,INVOICE_TAX.QUOTA.sum().as("INVOICE_TAX.QUOTA_SUM")
					,INVOICE_TAX.DEDUCTIBLE_QUOTA.sum().as("INVOICE_TAX.DEDUCTIBLE_QUOTA_SUM")
				)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(IRPF_PROPERTIES.getConditions(filter))
					.and(INVOICE.DOMAIN.equal(domain))
					.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(dateFrom),AonDateUtils.toSql(dateTo)))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
				.groupBy(INVOICE.RDOCUMENT)
				.orderBy(INVOICE.ID,INVOICE.RDOCUMENT)
				.fetch()
				.stream()
				.map( new IrpfInvoiceGroupedBreakdown() );		
	}

	public static class IrpfInvoiceGroupedBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base =  ((BigDecimal) rec.getValue("INVOICE_TAX.BASE_SUM")).doubleValue();
			double quota = ((BigDecimal) rec.getValue("INVOICE_TAX.QUOTA_SUM")).doubleValue();
			double dedQuota = ((BigDecimal) rec.getValue("INVOICE_TAX.DEDUCTIBLE_QUOTA_SUM")).doubleValue();
			if (AonMathUtils.isZero(dedQuota)) {
				dedQuota = quota;
			}
			return new IrpfBreakdown()
				.setFromSalary(false)
				.setInvoice(rec.getValue(INVOICE.ID))
				.setInvoiceType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
				.setSeries(rec.getValue(INVOICE.SERIES))
				.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setName(rec.getValue(INVOICE.RNAME))
				.setBase(base)
				.setQuota(quota)
				.setDeductibleQuota(dedQuota)
				;
		}
	}

	public static class IrpfInvoiceBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			double dedPercent = rec.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT);
			if (AonMathUtils.isZero(quota)) {
				quota =  AonMathUtils.round(base * percent / 100);
			}
			if (AonMathUtils.isZero(dedPercent)) dedPercent = 100;
			double dedQuota = rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
			if (AonMathUtils.isZero(dedQuota)) {
				if (AonMathUtils.isZero(dedPercent) || dedPercent == 100) {
					dedQuota = quota;
				} else {
					dedQuota = AonMathUtils.round(quota * dedPercent / 100);
				}
			}
			Byte regime = rec.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME);
			return new IrpfBreakdown()
					.setFromSalary(false)
					.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
					.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
					.setEpigraph(rec.getValue(IAE.EPIGRAPH))
					.setInvoice(rec.getValue(INVOICE.ID))
					.setInvoiceType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
					.setSeries(rec.getValue(INVOICE.SERIES))
					.setNumber(rec.getValue(INVOICE.NUMBER))
					.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
					.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(DocumentType.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setName(rec.getValue(INVOICE.RNAME))
					.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
					.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
					.setIRPFRegime(regime == null? null : IRPFRegime.values()[regime])
					.setBase(base)
					.setPercent(percent)
					.setQuota(quota)
					.setDeductiblePercent(dedPercent)
					.setDeductibleQuota(dedQuota)
					.setZip(rec.getValue(RADDRESS.ZIP))
					.setCity(rec.getValue(RADDRESS.CITY))
					;
		}
	}

	public static LinkedList<IrpfBreakdown> getIRPFSummary(AONContext ctx, Date fromDate, Date toDate, IRPFFilter filter) {
		LinkedList<IrpfBreakdown> list = new LinkedList<IrpfBreakdown>();
		getInvoicesIrpfBreakdown(ctx, ctx.getDomainId(), fromDate, toDate, filter)
			.forEach( irpf -> {
				double percent = irpf.getPercent();
				IrpfBreakdown sum = null;
				for (IrpfBreakdown ite : list) {
					if ( ite.getWithholdingType() == irpf.getWithholdingType()
						&& ite.isSales() == irpf.isSales()
						&& AonNumberUtils.equals(ite.getPercent(), percent)) {
						sum = ite;
						break;
					}
				}
				if ( sum == null) {
					sum = new IrpfBreakdown()
						.setInvoiceType(irpf.getInvoiceType())
						.setWithholdingType(irpf.getWithholdingType())
						.setPercent(percent);
					list.add(sum);
				}
				sum.setBase( sum.getBase() + irpf.getBase()); 
				sum.setQuota( sum.getQuota() + irpf.getQuota());
				sum.setDeductibleQuota( sum.getDeductibleQuota() + irpf.getDeductibleQuota());				
			});
		return list;
	}

	public static Stream<IrpfBreakdown> getIRPFBreakdown(AONContext ctx, Date fromDate, Date toDate, int grouped, IRPFFilter filter) {
		if (grouped == 0) {
			return getInvoicesIrpfBreakdown(ctx, ctx.getDomainId(), fromDate, toDate, filter);
		} else {
			return getGroupedInvoicesIrpfBreakdown(ctx, ctx.getDomainId(), fromDate, toDate, filter);
		}
	}

}

