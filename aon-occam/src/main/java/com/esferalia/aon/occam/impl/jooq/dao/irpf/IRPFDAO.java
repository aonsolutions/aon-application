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
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.IRPFFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRPFParamsOrderByVisitor;
import com.esferalia.aon.occam.api.model.finance.Properties.IRPFProperties;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsGroupedBy;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsOrderBy;
import com.esferalia.aon.occam.api.model.fiscal.ISalaryFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class IRPFDAO {
	
	private static final Byte[] INPUT_TYPES = new Byte[]{ InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
	
	private static final Field<Integer> INVOICE_NUMDOC_TYPE =  InvoiceDAO.getOrderedType();
	private static final Field<Integer> ALCATRAZ_SALARY_ID = ALCATRAZ.SALARY.as("alcatrazSalary");
	private static final Field<Integer> ALCATRAZ_INVOICE_ID = ALCATRAZ.INVOICE.as("alcatrazInvoice");

	private IRPFDAO() {
	}
	
	private static final IRPFPropertiesDAO IRPF_PROPERTIES = new IRPFPropertiesDAO();
	private static class IRPFPropertiesDAO implements IRPFProperties {

		private Condition[] getConditions(IRPFFilter filter) {
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
		@Override public Property<Date> getInvoiceIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getInvoiceTransactionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TRANSACTION);}
		@Override public Property<Integer> getActivityProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ACTIVITY);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.INVESTMENT);}
		@Override public Property<Byte> getServiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERVICE);}
		@Override public Property<Byte> getRectifiedProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Byte> getAccrualRegimeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.WITHHOLDING_TYPE);}
		@Override public Property<Double> getPercentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.PERCENTAGE);}
		@Override public Property<Double> getSurchargePercentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.SURCHARGE);}
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
	
	// ********************************************************************
	// ******************************************************* [INVOICES]
	// ********************************************************************
	private static SelectConditionStep<? extends Record> getInputInvoiceIrpBreakdownSelectWhere(final AONContext ctx, final FiscalModel fm) {
		return getInputInvoiceIrpBreakdownSelectWhere(getInvoiceIrpBreakdownSelect(ctx),fm);
	}
	private static SelectConditionStep<? extends Record> getInputInvoiceIrpBreakdownSelectWhere(SelectOnConditionStep<? extends Record> select, final FiscalModel fm) {
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
			,INVOICE_NUMDOC_TYPE
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
	
	private static Field<?>[] getOrderBy( IRPFParams params ) {
		if ( params.getGroupedBy() == IRPFParamsGroupedBy.REGISTRY) {
			return new Field<?>[] {INVOICE.REGISTRY,INVOICE.ID };
		} else if (params.getOrderBy() != null) {
			return params.getOrderBy().visit(new IRPFParamsOrderByVisitor<Field<?>[],Void>() {

				@Override
				public Field<?>[] visitInvoiceIssueDate(Void t) {
					return new Field<?>[] {INVOICE.ISSUE_DATE,INVOICE.ID};
				}

				@Override
				public Field<?>[] visitInvoiceNumber(Void t) {
					return new Field<?>[] {INVOICE_NUMDOC_TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.ID};
				}

				@Override
				public Field<?>[] visitInvoiceRegistryName(Void t) {
					return new Field<?>[] {INVOICE.RNAME,INVOICE.ID };
				}

				@Override
				public Field<?>[] visitInvoiceRegistryDocument(Void t) {
					return new Field<?>[] {INVOICE.RDOCUMENT,INVOICE.ID};
				}
			}, null); 
		}
		return new Field<?>[] {INVOICE.ISSUE_DATE,INVOICE.ID};
	}
	
	public static Stream<IrpfBreakdown> getInvoicesIrpfBreakdown(final AONContext ctx, IRPFParams params) {
		Stream<IrpfBreakdown> stream = getInvoiceIrpBreakdownSelect(ctx)
				.where(IRPF_PROPERTIES.getConditions(p -> getIRPFFilter(p, params)))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
				.orderBy( getOrderBy(params) )
				.fetch()
				.stream()
				.map( new IrpfInvoiceBreakdownFiller() ); 
		
		if (params.getGroupedBy() == IRPFParamsGroupedBy.INVOICE) {
			return stream
				.collect( new InvoiceCollector(params) )
				.values()
				.stream()
				;
		} else  if (params.getGroupedBy() == IRPFParamsGroupedBy.REGISTRY) {
			return stream
				.collect( new NifCollector(params) )
				.values()
				.stream()
				;
		}
		return stream;
	}
	
	public static Stream<IrpfBreakdown> getOutputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		IRPFParams params = new IRPFParams()
			.setDomain(fm.getDomain())
			.setOutput(true)
			.setFromDate(getStartDate(fm))
			.setToDate(getEndDate(fm))
			.setGroupedBy(IRPFParamsGroupedBy.INVOICE)
			.setOrderBy(IRPFParamsOrderBy.INVOICE_ISSUE_DATE);
		return getInvoicesIrpfBreakdown(ctx, params);
	}
	public static Stream<IrpfBreakdown> getModelOutputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInvoiceIrpBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.equal(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(fm.getDomain()))
				.and(ALCATRAZ.FS_MODEL.eq(fm.getId()))
				.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
			.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdownFiller() );
	}

	public static Stream<IrpfBreakdown> getInputInvoicesIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInputInvoiceIrpBreakdownSelectWhere(ctx,fm)
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
			.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
			.and(FS_MODEL.YEAR.eq(fm.getYear()))
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
			.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
			.and(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.asTable("modelInvoice")
		;
		return getInputInvoiceIrpBreakdownSelectWhere(
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
			double dedQuota = rec.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
			if (AonMathUtils.isZero(quota)) {
				quota =  AonMathUtils.round(base * percent / 100);
			}
			if (AonMathUtils.isZero(dedPercent)) dedPercent = 100;
			if (AonMathUtils.isZero(dedQuota)) {
				dedQuota = (AonMathUtils.isZero(dedPercent) || dedPercent == 100)
					?quota
					:AonMathUtils.round(quota * dedPercent / 100);
			}
			return new IrpfBreakdown()
				.setFromSalary(false)
				.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
				.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setEpigraph(rec.getValue(IAE.EPIGRAPH))
				.setInvoice(rec.getValue(INVOICE.ID))
				.setInvoiceType(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)))
				.setSeries(rec.getValue(INVOICE.SERIES))
				.setNumber(rec.getValue(INVOICE.NUMBER))
				.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
				.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setName(rec.getValue(INVOICE.RNAME))
				.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
				.setWithholdingType(WithholdingType.safeValueOf( rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
				.setIRPFRegime( IRPFRegime.safeValueOf(rec.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME)) )
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
				,SALARY.CHARGE_DATE
				,SALARY.EMPLOYEE_DOCUMENT
				,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE
				,SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE
				,SALARY.TOTAL_IRPF
				,CONTRACT_DATA.EXPRESSION)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.leftJoin(CONTRACT_DATA).on(SALARY.CONTRACT.equal(CONTRACT_DATA.CONTRACT).and(CONTRACT_DATA.NAME.equal("IRPF_TYPE")));
	}

	private static Field<java.sql.Date> getSalaryDateField(final ISalaryFiscalModel fm) {
		return (fm.mustUseChargeDate()?SALARY.CHARGE_DATE:SALARY.ISSUE_DATE);
	}
	
//	public static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final ISalaryFiscalModel fm) {
//		return getSalaryIrpfBreakdownSelect(ctx) 
//			.where(SALARY.DOMAIN.equal(fm.getDomain()))
//				.and(getSalaryDateField(fm).between(getStartDate(fm),getEndDate(fm)))
//				.and(SALARY.IRPF_BASE.ne( 0.0 ))
//				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
//				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
//			.fetch()
//			.stream()
//			.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
//			.flatMap(List::stream)
//			;
//	}
	
	public static Stream<IrpfBreakdown> getPreviousNotInModelSalaryIrpfBreakdown(final AONContext ctx, final ISalaryFiscalModel fm) {
		Table<Record1<Integer>> modelSalary = ctx.getDslContext().select( ALCATRAZ_SALARY_ID )
				.from(ALCATRAZ)
				.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
				.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
				.and(FS_MODEL.YEAR.eq(fm.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
				.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
				.asTable("modelSalary")
			;
			return getSalaryIrpfBreakdownSelect(ctx)
				.leftAntiJoin(modelSalary).on(ALCATRAZ_SALARY_ID.equal(SALARY.ID))
				.where(SALARY.DOMAIN.equal(fm.getDomain()))
					.and(getSalaryDateField(fm).ge(getYearFirstDay(fm)))
					.and(getSalaryDateField(fm).lt(getStartDate(fm)))
					.and(SALARY.IRPF_BASE.ne( 0.0 ))
					.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
					.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
					.and(CONTRACT_DATA.EXPRESSION.isNull().or(CONTRACT_DATA.EXPRESSION.ne("\"3\"")))  // No tener en cuenta los no residentes
				.orderBy(getSalaryDateField(fm),SALARY.ID,SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
				.flatMap(List::stream);
	}
	
	public static Stream<IrpfBreakdown> getNotInModelSalaryIrpfBreakdown(final AONContext ctx, final ISalaryFiscalModel fm) {
		Table<Record1<Integer>> modelSalary = ctx.getDslContext().select( ALCATRAZ_SALARY_ID )
			.from(ALCATRAZ)
			.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
			.and(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.asTable("modelSalary")
		;
		return getSalaryIrpfBreakdownSelect(ctx)
			 .leftAntiJoin(modelSalary).on(ALCATRAZ_SALARY_ID.equal(SALARY.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(getSalaryDateField(fm).between(getStartDate(fm),getEndDate(fm)))
				.and(SALARY.IRPF_BASE.ne( 0.0 ))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.and(CONTRACT_DATA.EXPRESSION.isNull().or(CONTRACT_DATA.EXPRESSION.ne("\"3\"")))  // No tener en cuenta los no residentes
			.orderBy(getSalaryDateField(fm),SALARY.ID,SALARY.EMPLOYEE_DOCUMENT)
			.fetch()
			.stream()
			.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
			.flatMap(List::stream);
	}
	
	public static Stream<IrpfBreakdown> getModelSalaryIrpfBreakdown(final AONContext ctx, final ISalaryFiscalModel fm) {
		return getSalaryIrpfBreakdownSelect(ctx)
			.innerJoin(ALCATRAZ).on(ALCATRAZ.SALARY.equal(SALARY.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(ALCATRAZ.FS_MODEL.eq(fm.getId()))
				.and(SALARY.IRPF_BASE.ne( 0.0 ))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
			.orderBy(getSalaryDateField(fm),SALARY.ID,SALARY.EMPLOYEE_NAME)
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
				.setChargeDate(rec.getValue(SALARY.CHARGE_DATE))
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
						.setChargeDate(rec.getValue(SALARY.CHARGE_DATE))
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

	public static IrpfSummary getIRPFSummary(AONContext ctx, IRPFParams params) {
		IrpfSummary summary = new IrpfSummary();
		params.setGroupedBy(null);
		getInvoicesIrpfBreakdown(ctx, params).forEach(summary::add);
		return summary;
	}
	
	private static Filter getIRPFFilter(final IRPFProperties p, final IRPFParams params) {
		Filter filter = p.getDomainProperty().eq(params.getDomain());
		filter = AonObjectUtils.computeIfTrue(Objects.isNull(params.getFromDate())
			, filter , prop ->  prop.and(p.getInvoiceIssueDateProperty().ge(params.getFromDate())));
		filter = AonObjectUtils.computeIfTrue(Objects.isNull(params.getToDate())
			, filter , prop ->  prop = prop.and(p.getInvoiceIssueDateProperty().le(params.getToDate())));
		filter = AonObjectUtils.computeIfTrue(Objects.isNull(params.getInvoices())
			, filter , prop -> prop = prop.and(p.getInvoiceIdProperty().in(params.getInvoices())));
		filter = AonObjectUtils.computeIfTrue((Objects.isNull( params.getRegistry()) || params.getRegistry().intValue() == 0 )
			, filter , prop ->  prop.and(p.getRegistryProperty().eq(params.getRegistry())));
		
		if (params.getActivity()  != null && params.getActivity().intValue() != 0 ) {
			if (params.getActivity() < 0 ) {
				filter = filter.and(p.getActivityProperty().isNull());
			} else {
				filter = filter.and(p.getActivityProperty().eq(params.getActivity()));
			}
		}
		filter = AonObjectUtils.computeIfTrue(Objects.isNull( params.getAccrualRegime() )
			, filter, prop ->  prop.and(p.getAccrualRegimeProperty().eq( AonEnumUtils.getByte(params.getAccrualRegime()))));
		filter = AonObjectUtils.computeIfTrue(Objects.isNull( params.getInvestment() )
			, filter, prop ->  prop.and(p.getInvestmentProperty().eq( AonEnumUtils.getByte(params.getInvestment()))));
		
		
		if (params.getWithholdingTypeGroup() != null 
			&& params.getWithholdingTypeGroup().getValueTypes() != null
			&& params.getWithholdingTypeGroup().getValueTypes().length > 0) {
			filter = filter.and(p.getWithholdingTypeProperty().in( params.getWithholdingTypeGroup().getValueTypes() ));
		}
		filter = AonObjectUtils.computeIfTrue(Objects.isNull( params.getWithholdingType() )
			, filter, prop ->  prop.and(p.getWithholdingTypeProperty().eq( AonEnumUtils.getByte(params.getWithholdingType()))));

		
		filter = AonObjectUtils.computeIfTrue(Objects.isNull( params.getRectificationType() )
			, filter, prop ->  prop.and(p.getRectifiedProperty().eq( AonEnumUtils.getByte(params.getRectificationType()))));
		if (params.getService() != null) {
			if ( params.isService() ) {
				filter = filter.and(p.getServiceProperty().eq((byte)1).or( p.getInvoiceTypeProperty().eq( InvoiceType.EXPENSES.value())));
			} else {
				filter = filter.and(p.getServiceProperty().ne((byte)1).and(p.getInvoiceTypeProperty().ne( InvoiceType.EXPENSES.value())));
			}
		}
		filter = AonObjectUtils.computeIfTrue(Objects.isNull( params.getPercent() )
			, filter, prop ->  prop.and(p.getPercentProperty().eq( params.getPercent())));
		
		if (params.isOutput()) {
				filter = filter.and(p.getInvoiceTypeProperty().eq( InvoiceType.SALES.value()));
		}
		if (params.isInput()) {
				filter = filter.and(p.getInvoiceTypeProperty().in( INPUT_TYPES ));
		}
		return filter;
	}
}


