package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.Keys.FK_SALARY_DATA_SALARY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLine;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDAO {

	private static final String DEFAULT_SALARY_CONCEPT = "NÓMINAS";
	private static final String SUM_IRPF_BASE = "SUM_IRPF_BASE";
	private static final String SUM_TOTAL_IRPF = "SUM_TOTAL_IRPF";
	private static final String SUM_SOCIAL_SECURITY_CONTRIBUTIONS = "SUM_SOCIAL_SECURITY_CONTRIBUTIONS";
	private static final String SUM_TOTAL_ENTERPRISE = "SUM_TOTAL_ENTERPRISE";
	private static final String SUM_TOTAL_LIQUID = "SUM_TOTAL_LIQUID";
	private static final String ALLOWANCE_SUM = "ALLOWANCE_SUM";
	private static final String COMPENSATION_SUM = "COMPENSATION_SUM";
	private static final String SEIZE_SUM = "DEDUCTION_SEIZE_SUM";
	private static final String DED_ADVANCE_SUM = "DEDUCTION_ADVANCE_SUM";
	private static final String DED_IN_KIND_SUM = "DED_IN_KIND_SUM";
	private static final String DED_OTHER_SUM = "DEDUCTION_TYPE_OTHER_SUM";

	// TODO ARRRGGGGGHHH!!!
	private static final Byte DEDUCTION_ADVANCE = 7;
	private static final Byte DEDUCTION_IN_KIND = 8;
	private static final Byte DEDUCTION_TYPE_OTHER = 9;

	// --------------------

	public static SalaryAccountEntry getSalaryEntry(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank) {
		ctx.checkRead();
		if (enterprise == null)
			throw new AonCoreException(AonError.EMPTY_ENTERPRISE.getMessage());
		if (from == null)
			throw new AonCoreException(AonError.EMPTY_DATE_FROM.getMessage());
		if (to == null)
			throw new AonCoreException(AonError.EMPTY_DATE_TO.getMessage());
		Field<BigDecimal> sueldosYSalarios = DSL.round(DSL.sum(SALARY.IRPF_BASE), 2).as(SUM_IRPF_BASE);
		Field<BigDecimal> totalIRPF = DSL.round(DSL.sum(SALARY.TOTAL_IRPF), 2).as(SUM_TOTAL_IRPF);
		Field<BigDecimal> segSocEmployee = DSL.round(DSL.sum(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS), 2).as(SUM_SOCIAL_SECURITY_CONTRIBUTIONS);
		Field<BigDecimal> segSocCompany = DSL.round(DSL.sum(SALARY.TOTAL_ENTERPRISE), 2).as(SUM_TOTAL_ENTERPRISE);
		Field<BigDecimal> totalLiquid = DSL.round(DSL.sum(SALARY.TOTAL_LIQUID), 2).as(SUM_TOTAL_LIQUID);

		AggregateFunction<BigDecimal> salaryPaymentAmountSum = DSL.sum(SALARY_PAYMENT.AMOUNT);
		// DIETAS
		Field<BigDecimal> dietas = DSL.round(DSL.sum(
				DSL.select(salaryPaymentAmountSum).from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
						.and(SALARY_PAYMENT.TYPE.between((byte) 42, (byte) 50))
						.asField()), 2).as(ALLOWANCE_SUM);

		// INDEMNIZACIONES
		Field<BigDecimal> indemnizaciones = DSL.round(DSL.sum(
				DSL.select(salaryPaymentAmountSum).from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
						.and(SALARY_PAYMENT.TYPE.between((byte) 51, (byte) 54))
						.asField()), 2).as(COMPENSATION_SUM);

		AggregateFunction<BigDecimal> salaryDeductionAmountSum = DSL.sum(DSL
				.round(SALARY_DEDUCTION.AMOUNT, 2));
		// OTRAS DEDUCCIONES
		Field<BigDecimal> otherDeductions = DSL.round(DSL.sum(
				DSL.select(salaryDeductionAmountSum).from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_TYPE_OTHER))
						.asField()), 2).as(DED_OTHER_SUM);
		// DEDUCCIONES EN ESPECIE
		Field<BigDecimal> inKindDeductions = DSL.round(DSL.sum(
				DSL.select(salaryDeductionAmountSum).from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_IN_KIND))
						.asField()), 2).as(DED_IN_KIND_SUM);
		// DEDUCCIONES de ANTICIPOS
		Field<BigDecimal> advanceDeductions = DSL.round(DSL.sum(
				DSL.select(salaryDeductionAmountSum).from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_ADVANCE))
						.asField()), 2).as(DED_ADVANCE_SUM);

		AggregateFunction<BigDecimal> salaryEmbargoAmountSum = DSL.sum(SALARY_EMBARGO.AMOUNT);
		// DIETAS
		Field<BigDecimal> seize = DSL.round(DSL.sum(
				DSL.select(salaryEmbargoAmountSum).from(SALARY_EMBARGO)
						.where(SALARY_EMBARGO.SALARY.equal(SALARY.ID))
						.asField()), 2).as(SEIZE_SUM);

		String sql = ctx
				.getDslContext()
				.select(sueldosYSalarios, totalIRPF, segSocEmployee,
						segSocCompany, totalLiquid, dietas, indemnizaciones,
						otherDeductions, inKindDeductions, advanceDeductions,
						seize)
				.from(SALARY)
				.join(CONTRACT)
				.on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE)
				.on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(WORKPLACE.ENTERPRISE.equal(enterprise))
				.and(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(from),
						AonDateUtils.toSql(to))).getSQL();
		SalaryAccountEntry sae = new SalaryAccountEntry();
		PreparedStatement stmt;
		try {
			stmt = ctx.getDslContext().configuration().connectionProvider()
					.acquire().prepareStatement(sql);
			sae.setDate(to);
			sae.setSecurityLevel(SecurityLevel.OFFICIAL);
			// TODO Un pelín escaso ...
			sae.setConcept((AonStringUtils.isNotEmpty(concept)) ? concept
					: DEFAULT_SALARY_CONCEPT);
			// ------------------------
			sae.setRegistryBank(registryBank);
			Function<ResultSet, SalaryAccountEntry> function = Unchecked
					.function(rs -> {
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.SALARY, null, rs
										.getDouble(SUM_IRPF_BASE)));
						sae.addLine(new SalaryAccountEntryLine(
								SalaryAccountEntryLineType.RETENTION, null, rs
										.getDouble(SUM_TOTAL_IRPF)));
						// IN KIND
						// sae.addLine( new
						// SalaryAccountEntryLine(SalaryAccountEntryLineType.SALARY_IN_KIND,null,rs.getDouble(XX)));
						// sae.addLine( new
						// SalaryAccountEntryLine(SalaryAccountEntryLineType.RETENTION_IN_KIND,null,rs.getDouble(XX)));

					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.EMPLOYEE_SOC_INS, null,
							rs.getDouble(SUM_SOCIAL_SECURITY_CONTRIBUTIONS)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.DEFAULT_PENDING_SALARY,
							null, rs.getDouble(SUM_TOTAL_LIQUID)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.COMPANY_SOC_INS, null,
							rs.getDouble(SUM_TOTAL_ENTERPRISE)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.ALLOWANCE, null, rs
									.getDouble(ALLOWANCE_SUM)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.COMPENSATION, null, rs
									.getDouble(COMPENSATION_SUM)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.DED_ADVANCE_PAYMENT,
							null, rs.getDouble(DED_ADVANCE_SUM)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.DED_IN_KIND, null, rs
									.getDouble(DED_IN_KIND_SUM)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.DED_OTHER, null, rs
									.getDouble(DED_OTHER_SUM)));
					sae.addLine(new SalaryAccountEntryLine(
							SalaryAccountEntryLineType.DED_SEIZE, null, rs
									.getDouble(SEIZE_SUM)));
					return sae;
				});

			SQL.seq(stmt, function).forEach(System.out::println);

		} catch (DataAccessException | SQLException e) {
			throw new AonCoreException(e.getMessage(), e);
		}
		return sae;
	}

	public static Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {

		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);
		
		if ( ctx == null ){
				List<Salary> emptyList = Collections.emptyList();
				return emptyList.stream();
		}
		
		
		//@formatter:off
		Cursor<Record> rootCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.where(conditions)
		.orderBy(SALARY.ID)
		.fetchLazy();
		//@formatter:on
		
		//@formatter:off
		Cursor<Record> dataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_DATA)
		.onKey(FK_SALARY_DATA_SALARY)
		.where(conditions)
		.orderBy(SALARY.ID)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
				Salary salary = supplier.get()
				.setId(rootRecord.getValue(SALARY.ID))		
				.setStartDate(rootRecord.getValue(SALARY.START_DATE))
				.setEndDate(rootRecord.getValue(SALARY.END_DATE))
				.setSalaryDays(rootRecord.getValue(SALARY.TIME_UNITS))
				.setEnterpriseCCC(rootRecord.getValue(SALARY.CCC))
				.setEnterpriseName(rootRecord.getValue(SALARY.ENTERPRISE_NAME))
				.setEnterpriseDocument(rootRecord.getValue(SALARY.ENTERPRISE_DOCUMENT))
				.setIrpfBase(rootRecord.getValue(SALARY.IRPF_BASE))
				.setEmployeeDocument(rootRecord.getValue(SALARY.EMPLOYEE_DOCUMENT))
				.setTotalLiquid(rootRecord.getValue(SALARY.TOTAL_LIQUID))
				.setTotalPayment(rootRecord.getValue(SALARY.TOTAL_PAYMENT))
				.setCommonContingenciesBase(rootRecord.getValue(SALARY.CGC_BASE))
				.setProfessionalContingenciesBase(rootRecord.getValue(SALARY.CGP_BASE))
				;
				
				int salaryId = rootRecord.getValue(SALARY.ID);
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(dataCursor), 
				r -> r.getValue(SALARY_DATA.SALARY) == salaryId ),
				r -> r.getValue(SALARY_DATA.SALARY) == salaryId )
				.forEachOrdered(dataRecord->
					salary.setContextData(
					dataRecord.getValue(SALARY_DATA.NAME), 
					dataRecord.getValue(SALARY_DATA.EXPRESSION),
					dataRecord.getValue(SALARY_DATA.START_DATE),
					dataRecord.getValue(SALARY_DATA.END_DATE))
				);
				
				return salary;
				}
		);
		//@formatter:on

	}
	
	public static Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {

		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);
		
		if ( ctx == null ){
				List<Salary> emptyList = Collections.emptyList();
				return emptyList.stream();
		}
		
		
		//@formatter:off
		Cursor<Record> rootCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.where(conditions)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on
		
		//@formatter:off
		Cursor<Record> dataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_DATA)
		.onKey(FK_SALARY_DATA_SALARY)
		.where(conditions)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
				String employeeDocument = rootRecord.getValue(SALARY.EMPLOYEE_DOCUMENT);	
				Salary salary = supplier.get()
				.setEmployeeDocument(employeeDocument)
				.setEmployeeName(rootRecord.getValue(SALARY.EMPLOYEE_NAME))
				.setEmployeeSSNumber(rootRecord.getValue(SALARY.SOCIAL_SECURITY_NUMBER))
				;
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(dataCursor), 
				r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
				r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
				.forEachOrdered(dataRecord->
					salary.setContextData(
					dataRecord.getValue(SALARY_DATA.NAME), 
					dataRecord.getValue(SALARY_DATA.EXPRESSION),
					dataRecord.getValue(SALARY_DATA.START_DATE),
					dataRecord.getValue(SALARY_DATA.END_DATE))
				);
				
				return salary;
				}
		);
		//@formatter:on

	}

	
	private enum SalaryType {
		SALARY, EXTRA, SETTLE, DELAY, NOT_ENJOYED_VACATIONS;

		byte value() {
			return (byte) ordinal();
		}
	}

	private static final SalaryPropertiesDAO SALARY_PROPERTIES = new SalaryPropertiesDAO();

	private static class SalaryPropertiesDAO implements
			SalaryProperties {

		private Condition[] getConditions(SalaryFilter filter) {
			FilterDAO filterDAO = (FilterDAO)filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(SALARY.ID);
		}

		@Override
		public Property<String> getCCCProperty() {
			return new FilterDAO.PropertyDAO<String>(SALARY.CCC);
		}

		@Override
		public Property<Integer> getContractProperty() {
			return new FilterDAO.PropertyDAO<Integer>(SALARY.CONTRACT);
		}

		@Override
		public Property<Date> getStartDateProperty() {
			return new FilterDAO.DatePropertyDAO(SALARY.START_DATE);
		}

		@Override
		public Property<Date> getEndDateProperty() {
			return new FilterDAO.DatePropertyDAO(SALARY.END_DATE);
		}

		@Override
		public Property<Boolean> getIsSalaryProperty() {
			return new FilterDAO.PropertyValueDAO<Byte>(SALARY.TYPE,
					SalaryType.SALARY.value());
		}

		@Override
		public Property<Boolean> getIsExtraProperty() {
			return new FilterDAO.PropertyValueDAO<Byte>(SALARY.TYPE,
					SalaryType.EXTRA.value());
		}

		@Override
		public Property<Boolean> getIsDelayProperty() {
			return new FilterDAO.PropertyValueDAO<Byte>(SALARY.TYPE,
					SalaryType.DELAY.value());
		}

		@Override
		public Property<Boolean> getIsSettlementProperty() {
			return new FilterDAO.PropertyValueDAO<Byte>(SALARY.TYPE,
					SalaryType.SETTLE.value());
		}
	}


}
