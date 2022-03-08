package com.esferalia.aon.occam.impl.jooq.dao.irpf;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class IRPFDAO {
	
	private static final Field<Integer> ALCATRAZ_INVOICE_ID = ALCATRAZ.INVOICE.as("alcatrazInvoice");
	private static final Field<Integer> ALCATRAZ_SALARY_ID = ALCATRAZ.SALARY.as("alcatrazSalary");

	private IRPFDAO() {
	}
	
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
	
	// ********************************************************************
	// ******************************************************* [INVOICES]
	// ********************************************************************
	private static SelectConditionStep<? extends Record> getInvoiceIrpBreakdownSelectWhere(final AONContext ctx, final FiscalModel fm) {
		return getInvoiceIrpBreakdownSelectWhere(getInvoiceIrpBreakdownSelect(ctx),fm); 	
	}
	private static SelectConditionStep<? extends Record> getInvoiceIrpBreakdownSelectWhere(SelectOnConditionStep<? extends Record> select, final FiscalModel fm) {
		return select
			.where(INVOICE.DOMAIN.equal(fm.getDomain()))
			.and(INVOICE.TYPE.ne(InvoiceType.SALES.value()))
			.and(INVOICE.ISSUE_DATE.between(getStartDate(fm),getEndDate(fm)))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
			;		
	}
	private static SelectOnConditionStep<? extends Record> getInvoiceIrpBreakdownSelect(final AONContext ctx) {
		return ctx.getDslContext().select( INVOICE.ID
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
		)
		.from(INVOICE)
		.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
		.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
		.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
		;		
	}

	public static Stream<IrpfBreakdown> getInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInvoiceIrpBreakdownSelectWhere(ctx,fm)
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdownFiller() );
	}
	
	public static Stream<IrpfBreakdown> getModelInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInvoiceIrpBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(fm.getDomain()))
				.and(ALCATRAZ.FS_MODEL.eq(fm.getId()))
				.and(INVOICE.TYPE.ne(InvoiceType.SALES.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdownFiller() );
	}
	
	public static Stream<IrpfBreakdown> getPreviousNotInModelInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.asTable("modelInvoice")
		;
		return getInvoiceIrpBreakdownSelect(ctx)
			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(fm.getDomain()))
			.and(INVOICE.TYPE.ne(InvoiceType.SALES.value()))
			.and(INVOICE.ISSUE_DATE.ge(getYearFirstDay(fm)))
			.and(INVOICE.ISSUE_DATE.lt(getStartDate(fm)))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdownFiller() );
	}

	public static Stream<IrpfBreakdown> getNotInModelInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select( ALCATRAZ_INVOICE_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.asTable("modelInvoice")
		;
		return getInvoiceIrpBreakdownSelectWhere(
			getInvoiceIrpBreakdownSelect(ctx)
				.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID)),fm)
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdownFiller() );
	}

	private static class IrpfInvoiceBreakdownFiller implements Function<Record, IrpfBreakdown> {
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
					;
		}
	}
	 
	// ********************************************************************
	// ******************************************************* [SALARIES]
	// ********************************************************************
	
	private static SelectOnConditionStep<? extends Record> getSalaryIrpfBreakdownSelect(final AONContext ctx) {
		return ctx.getDslContext().select(
				 SALARY.ID	
				,SALARY.ISSUE_DATE
				,SALARY.EMPLOYEE_DOCUMENT
				,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE
				,SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE
				,SALARY.TOTAL_IRPF)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID));
	}

	public static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdownSelect(ctx) 
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(SALARY.ISSUE_DATE.between(getStartDate(fm),getEndDate(fm)))
				.and(SALARY.IRPF_BASE.ne( 0.0 ))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
			.fetch()
			.stream()
			.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
			.flatMap(List::stream)
			;
	}
	
	public static Stream<IrpfBreakdown> getPreviousNotInModelSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		Table<Record1<Integer>> modelSalary = ctx.getDslContext().select( ALCATRAZ_SALARY_ID )
				.from(ALCATRAZ)
				.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
				.where(FS_MODEL.YEAR.eq(fm.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
				.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
				.asTable("modelSalary")
			;
			return getSalaryIrpfBreakdownSelect(ctx)
				.leftAntiJoin(modelSalary).on(ALCATRAZ_SALARY_ID.equal(SALARY.ID))
				.where(SALARY.DOMAIN.equal(fm.getDomain()))
					.and(SALARY.ISSUE_DATE.ge(getYearFirstDay(fm)))
					.and(SALARY.ISSUE_DATE.lt(getStartDate(fm)))
					.and(SALARY.IRPF_BASE.ne( 0.0 ))
					.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
					.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.ISSUE_DATE,SALARY.ID,SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
				.flatMap(List::stream);
	}
	
	public static Stream<IrpfBreakdown> getNotInModelSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		Table<Record1<Integer>> modelSalary = ctx.getDslContext().select( ALCATRAZ_SALARY_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.asTable("modelSalary")
		;
		return getSalaryIrpfBreakdownSelect(ctx)
			 .leftAntiJoin(modelSalary).on(ALCATRAZ_SALARY_ID.equal(SALARY.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(SALARY.ISSUE_DATE.between(getStartDate(fm),getEndDate(fm)))
				.and(SALARY.IRPF_BASE.ne( 0.0 ))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
			.orderBy(SALARY.ISSUE_DATE,SALARY.ID,SALARY.EMPLOYEE_DOCUMENT)
			.fetch()
			.stream()
			.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
			.flatMap(List::stream);
	}
	
	public static Stream<IrpfBreakdown> getModelSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.SALARY.equal(SALARY.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(ALCATRAZ.FS_MODEL.eq(fm.getId()))
				.and(SALARY.IRPF_BASE.ne( 0.0 ))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
			.orderBy(SALARY.ISSUE_DATE,SALARY.ID,SALARY.EMPLOYEE_NAME)
			.fetch()
			.stream()
			.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
			.flatMap(List::stream);
	}

	private static class IrpfSalaryBreakdownFiller implements Function<Record, List<IrpfBreakdown>> {
		
		@Override
		public List<IrpfBreakdown> apply(Record rec) {
			final LinkedList<IrpfBreakdown> list = new LinkedList<>();
			IrpfBreakdown br = new IrpfBreakdown()
					.setSalary(rec.getValue(SALARY.ID))
					.setFromSalary(true)
					.setRegistryDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
					.setName(rec.getValue(SALARY.EMPLOYEE_NAME))
					.setIssueDate(rec.getValue(SALARY.ISSUE_DATE))
					.setTaxDate(rec.getValue(SALARY.ISSUE_DATE));
				double base = rec.getValue(SALARY.IRPF_BASE);
				double quota = rec.getValue(SALARY.TOTAL_IRPF);
				double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
				if (AonMathUtils.isZero(inKindBase)) {
					br.setInKind(false)
						.setBase(AonMathUtils.round( base))
						.setQuota(AonMathUtils.round( quota));
					list.add(br);
				} else {	
					double inKindQuota = 0;
					double moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
					double moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
					inKindQuota = AonMathUtils.round( quota - moneyQuota);
					br.setInKind(true)
					  .setBase(AonMathUtils.round( inKindBase))
					  .setQuota(inKindQuota);
					list.add(br);
					if (AonMathUtils.isNotZero(moneyBase) || AonMathUtils.isNotZero(moneyQuota)) {
						br = new IrpfBreakdown()
								.setSalary(rec.getValue(SALARY.ID))
								.setFromSalary(true)
								.setRegistryDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
								.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
								.setIssueDate(rec.getValue(SALARY.ISSUE_DATE))
								.setTaxDate(rec.getValue(SALARY.ISSUE_DATE))
								.setInKind(false)
								.setBase(AonMathUtils.round( moneyBase))
								.setQuota(moneyQuota);
						list.add(br);
					}
				}
				return list;
		}
		
	}
	
// ************************************************************************
// ************************************************************************
/*	
	public static Stream<IrpfBreakdown> getSalaryDiffIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdown(ctx, fm, true);	
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

	private static Stream<IrpfBreakdown> getGroupedInvoicesIrpfBreakdown(final AONContext ctx, int domain, Date dateFrom, Date dateTo, IRPFFilter filter) {
		return 	ctx.getDslContext()
			.select( INVOICE.ID
					,INVOICE.TYPE
					,INVOICE.SERIES
					
					,INVOICE.RDOCUMENT
					,INVOICE.RNAME
					
					,DSL.sum(INVOICE_TAX.BASE).as("INVOICE_TAX.BASE_SUM")
					,DSL.sum(INVOICE_TAX.QUOTA).as("INVOICE_TAX.QUOTA_SUM")
					,DSL.sum(INVOICE_TAX.DEDUCTIBLE_QUOTA).as("INVOICE_TAX.DEDUCTIBLE_QUOTA_SUM")
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
*/
}

