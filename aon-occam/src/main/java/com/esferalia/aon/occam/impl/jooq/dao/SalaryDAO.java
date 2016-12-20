package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.Keys.FK_SALARY_BONUS_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_COST_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DATA_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DEDUCTION_SALARY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.watson.util.AonDateUtils.compare;
import static com.esferalia.aon.watson.util.AonDateUtils.max;
import static com.esferalia.aon.watson.util.AonDateUtils.min;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.lambda.Seq;

import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLine;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDAO {

	private static final String DEFAULT_SALARY_CONCEPT = "NÓMINAS";

	// TODO ARRRGGGGGHHH!!!
	private static final Byte DEDUCTION_ADVANCE = 7;
	private static final Byte DEDUCTION_IN_KIND = 8;
	private static final Byte DEDUCTION_TYPE_OTHER = 9;

	// --------------------
	public static Stream<SalaryEntry> getSalaryEntries(AONContext ctx, Date from, Date to) {
		return getSalaryEntries(ctx, from, to, true);
	}
	public static Stream<SalaryEntry> getSalaryEntries(AONContext ctx, Date from, Date to, boolean aggregated) {
		final TreeMap<String,SalaryEntry> map = new TreeMap<String,SalaryEntry>();
		ctx.getDslContext()
			.select(SALARY.ID
					,SALARY.ISSUE_DATE
					,SALARY.EMPLOYEE_NAME
					,SALARY.MONEY_IRPF_BASE
					,SALARY.INKIND_IRPF_BASE
					,SALARY.IRPF_BASE
					,SALARY.TOTAL_IRPF
					,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS
					,SALARY.TOTAL_ENTERPRISE
					,SALARY.TOTAL_LIQUID)
			.from(SALARY)
			.where(SALARY.DOMAIN.equal(ctx.getDomainId()))
			.and(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)))
			.fetch()
			.stream()
			.forEach(rec -> {
				int salaryId = rec.get(SALARY.ID);
				Date issueDate = rec.get(SALARY.ISSUE_DATE);
				String keyMap = AonDateUtils.orderFormat( issueDate )
						+ (aggregated?"":("-"+AonNumberUtils.toString(salaryId)));
				if (!map.containsKey(keyMap)) {
					SalaryEntry entry = new SalaryEntry();
					entry.setAccountEntry(new AccountEntry().setEntryDate(issueDate));
					map.put(keyMap,entry);	
				}
				final SalaryEntry entry = map.get(keyMap);
				if (!aggregated) {
					entry.setSalaryDescription(rec.getValue(SALARY.EMPLOYEE_NAME));
				}
				entry.setSalaryCount(entry.getSalaryCount()+1);
				entry.setEmployeeSocialInsurance( AonMathUtils.round(entry.getEmployeeSocialInsurance() 
						+ rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)));;
				entry.setCompanySocialInsurance( AonMathUtils.round(entry.getCompanySocialInsurance() 
						+ rec.getValue(SALARY.TOTAL_ENTERPRISE)));;
				double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
				if (AonMathUtils.isZero(rec.getValue(SALARY.INKIND_IRPF_BASE))) {
					entry.setIrpf( AonMathUtils.round(entry.getIrpf() + totalIrpf));
				} else {
					double base = rec.getValue(SALARY.IRPF_BASE);
					double moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
					double irpf = AonMathUtils.round( moneyBase * totalIrpf  / base ); 	
					entry.setIrpf( AonMathUtils.round(entry.getIrpf() + irpf));
					entry.setInKindIrpf( AonMathUtils.round(entry.getInKindIrpf() + AonMathUtils.round( totalIrpf - irpf)));
				}
				ctx.getDslContext().select(SALARY_PAYMENT.TYPE,SALARY_PAYMENT.AMOUNT)
					.from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.equal(salaryId))
					.fetch()
					.stream()
					.forEach(pay -> {
						double amount = AonMathUtils.round(pay.getValue(SALARY_PAYMENT.AMOUNT));
						Byte type = pay.getValue(SALARY_PAYMENT.TYPE);
						if (AonNumberUtils.between(type, 42, 50) ) {
							entry.setAllowance( AonMathUtils.round(entry.getAllowance()  + amount));
						} else if (AonNumberUtils.between(type, 51, 54) ) {
							entry.setSalaryCompensation( AonMathUtils.round(entry.getSalaryCompensation() + amount));
						} else if (AonNumberUtils.between(type, 13, 26) ) {
							entry.setInKindSalary( AonMathUtils.round(entry.getInKindSalary() + amount));
							entry.setSalaryOtherDeductions( AonMathUtils.round(entry.getSalaryOtherDeductions() + amount));
						} else {
							entry.setMoneySalary( AonMathUtils.round(entry.getMoneySalary() + amount));
						}
				});
				ctx.getDslContext().select(SALARY_DEDUCTION.TYPE,SALARY_DEDUCTION.AMOUNT)
					.from(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.equal(salaryId))
					.fetch()
					.stream()
					.forEach(ded -> {
						double amount = AonMathUtils.round(ded.getValue(SALARY_DEDUCTION.AMOUNT));
						Byte type = ded.getValue(SALARY_DEDUCTION.TYPE);
						if (AonNumberUtils.equals(type, DEDUCTION_ADVANCE) ) {
							entry.setSalaryDedAdvPayment(AonMathUtils.round(entry.getSalaryDedAdvPayment() + amount));
						} else if (AonNumberUtils.equals(type,DEDUCTION_TYPE_OTHER) 
								|| AonNumberUtils.equals(type,DEDUCTION_IN_KIND) ){
							entry.setSalaryOtherDeductions( AonMathUtils.round(entry.getSalaryOtherDeductions() + amount));
						}
				});
				ctx.getDslContext().select(SALARY_EMBARGO.AMOUNT)
					.from(SALARY_EMBARGO)
					.where(SALARY_EMBARGO.SALARY.equal(salaryId))
					.fetch()
					.stream()
					.forEach(emb -> {
						double amount = AonMathUtils.round(emb.getValue(SALARY_EMBARGO.AMOUNT));
						entry.setSalaryDedSeize( AonMathUtils.round(entry.getSalaryDedSeize() + amount));
				});
			});
		return map.values().stream();
	}
	
	@Deprecated
	public static Stream<SalaryAccountEntry> getSalaryEntries(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank) {
		ctx.checkRead();
		if (enterprise == null)
			throw new AonCoreException(AonError.EMPTY_ENTERPRISE.getMessage());
		if (from == null)
			throw new AonCoreException(AonError.EMPTY_DATE_FROM.getMessage());
		if (to == null)
			throw new AonCoreException(AonError.EMPTY_DATE_TO.getMessage());
		Field<BigDecimal> sueldosYSalarios = DSL.sum(SALARY.IRPF_BASE);
		Field<BigDecimal> totalIRPF = DSL.sum(SALARY.TOTAL_IRPF);
		Field<BigDecimal> segSocEmployee = DSL.sum(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
		Field<BigDecimal> segSocCompany = DSL.sum(SALARY.TOTAL_ENTERPRISE);
		Field<BigDecimal> totalLiquid = DSL.sum(SALARY.TOTAL_LIQUID);

		AggregateFunction<BigDecimal> salaryPaymentAmountSum = DSL
				.sum(SALARY_PAYMENT.AMOUNT);
		// DIETAS
		Field<BigDecimal> dietas = 
				DSL.sum(DSL.select(salaryPaymentAmountSum).from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
						.and(SALARY_PAYMENT.TYPE.between((byte) 42, (byte) 50))
						.asField());

		// INDEMNIZACIONES
		Field<BigDecimal> indemnizaciones = 
				DSL.sum(DSL.select(salaryPaymentAmountSum).from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
						.and(SALARY_PAYMENT.TYPE.between((byte) 51, (byte) 54))
						.asField());

		AggregateFunction<BigDecimal> salaryDeductionAmountSum = DSL.sum(DSL
				.round(SALARY_DEDUCTION.AMOUNT, 2));
		// OTRAS DEDUCCIONES
		Field<BigDecimal> otherDeductions = 
				DSL.sum(DSL.select(salaryDeductionAmountSum)
						.from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_TYPE_OTHER))
						.asField());
		// DEDUCCIONES EN ESPECIE
		Field<BigDecimal> inKindDeductions = 
				DSL.sum(DSL.select(salaryPaymentAmountSum)
						.from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
						.and(SALARY_PAYMENT.TYPE.between((byte) 13, (byte) 26))
						.asField());
//				DSL.sum(DSL.select(salaryDeductionAmountSum)
//						.from(SALARY_DEDUCTION)
//						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
//						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_IN_KIND))
//						.asField());
		// DEDUCCIONES de ANTICIPOS
		Field<BigDecimal> advanceDeductions = 
				DSL.sum(DSL.select(salaryDeductionAmountSum)
						.from(SALARY_DEDUCTION)
						.where(SALARY_DEDUCTION.SALARY.equal(SALARY.ID))
						.and(SALARY_DEDUCTION.TYPE.equal(DEDUCTION_ADVANCE))
						.asField());

		AggregateFunction<BigDecimal> salaryEmbargoAmountSum = DSL
				.sum(SALARY_EMBARGO.AMOUNT);
		// DIETAS
		Field<BigDecimal> seize = 
				DSL.sum(DSL.select(salaryEmbargoAmountSum).from(SALARY_EMBARGO)
						.where(SALARY_EMBARGO.SALARY.equal(SALARY.ID))
						.asField());

		// Se busca la cuenta asociada al registryBank pasado en caso de no ser nulo.
		Integer account = null;
		if (registryBank != null) {
			Record record = ctx.getDslContext()
				.select(RBANK.ACCOUNT)
				.from(RBANK)
				.where(RBANK.ID.eq(registryBank))
				.fetchOne();
			account = record.getValue(RBANK.ACCOUNT);
		}
		final Integer accountId = (registryBank == null)?null:account;
		
		return ctx.getDslContext()
			.select(SALARY.ISSUE_DATE,sueldosYSalarios, totalIRPF, segSocEmployee,
				segSocCompany, totalLiquid, dietas, indemnizaciones,
				otherDeductions, inKindDeductions, advanceDeductions,
				seize)
			.from(SALARY)
			.join(CONTRACT)
			.on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE)
			.on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(WORKPLACE.ENTERPRISE.equal(enterprise))
			.and(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)))
			.groupBy(SALARY.ISSUE_DATE)
			.fetch()
			.stream()
			.map(record -> new SalaryAccountEntry()
				.setDate(record.getValue(SALARY.ISSUE_DATE))
				.setSecurityLevel(SecurityLevel.OFFICIAL)
				.setConcept((AonStringUtils.isNotEmpty(concept)) ? concept : DEFAULT_SALARY_CONCEPT)
				.setRegistryBank(registryBank)
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.SALARY, null, record.getValue(sueldosYSalarios)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.RETENTION, null, record.getValue(totalIRPF)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.EMPLOYEE_SOC_INS, null,record.getValue(segSocEmployee)))
				.addLine(registryBank==null?
					 new SalaryAccountEntryLine(SalaryAccountEntryLineType.DEFAULT_PENDING_SALARY,null, record.getValue(totalLiquid))
					:new SalaryAccountEntryLine(SalaryAccountEntryLineType.BANK_ACCOUNT,accountId, record.getValue(totalLiquid))
				)
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.COMPANY_SOC_INS, null,record.getValue(segSocCompany)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.ALLOWANCE, null, record.getValue(dietas)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.COMPENSATION, null, record.getValue(indemnizaciones)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.DED_ADVANCE_PAYMENT,null, record.getValue(advanceDeductions)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.DED_IN_KIND, null, record.getValue(inKindDeductions)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.DED_OTHER, null, record.getValue(otherDeductions)))
				.addLine(new SalaryAccountEntryLine(SalaryAccountEntryLineType.DED_SEIZE, null, record.getValue(seize)))
				);
	}

	public static Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {

		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);

		if (ctx == null) {
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
		Cursor<Record> deductionCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_DEDUCTION)
		.onKey(FK_SALARY_DEDUCTION_SALARY)
		.where(conditions)
		.orderBy(SALARY_DEDUCTION.SALARY)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> costCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_COST)
		.onKey(FK_SALARY_COST_SALARY)
		.where(conditions)
		.orderBy(SALARY_COST.SALARY)
		.fetchLazy();
		//@formatter:on
		
		//@formatter:off
		Cursor<Record> bonusCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_BONUS)
		.onKey(FK_SALARY_BONUS_SALARY)
		.where(conditions)
		.orderBy(SALARY_BONUS.SALARY)
		.fetchLazy();
		//@formatter:on

		BackIterator<Record> dataIter = new BackIterator<>(dataCursor.iterator());
		BackIterator<Record> costIter = new BackIterator<>(costCursor.iterator());
		BackIterator<Record> deductionIter = new BackIterator<>(deductionCursor.iterator());
		BackIterator<Record> bonusIter = new BackIterator<>(bonusCursor.iterator());

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
				.setTotalIrpf(rootRecord.getValue(SALARY.TOTAL_IRPF))
				.setTotalDeduction(rootRecord.getValue(SALARY.TOTAL_DEDUCTION))
				.setCommonContingenciesBase(rootRecord.getValue(SALARY.CGC_BASE))
				.setProfessionalContingenciesBase(rootRecord.getValue(SALARY.CGP_BASE))
				.setIrpfBase(rootRecord.getValue(SALARY.IRPF_BASE))
				.setMoneyIrpfBase(rootRecord.getValue(SALARY.MONEY_IRPF_BASE))
				.setInkindIrpfBase(rootRecord.getValue(SALARY.INKIND_IRPF_BASE))

				.setTotalEnterprise(rootRecord.getValue(SALARY.TOTAL_ENTERPRISE))
				.setTotalSSContributions(rootRecord.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS))				
				;
				
				int salaryId = rootRecord.getValue(SALARY.ID);
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(dataIter), 
				r -> r.getValue(SALARY_DATA.SALARY) >= salaryId ),
				r -> r.getValue(SALARY_DATA.SALARY) == salaryId )
				.forEachOrdered(dataRecord->
					salary.setContextData(
					dataRecord.getValue(SALARY_DATA.NAME), 
					dataRecord.getValue(SALARY_DATA.EXPRESSION),
					dataRecord.getValue(SALARY_DATA.START_DATE),
					dataRecord.getValue(SALARY_DATA.END_DATE))
				);
				
				dataIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(deductionIter), 
				r -> r.getValue(SALARY_DEDUCTION.SALARY) >= salaryId ),
				r -> r.getValue(SALARY_DEDUCTION.SALARY) == salaryId )
				.forEachOrdered(deductionRecord->{
					salary.addDeduction(
							deductionRecord.getValue(SALARY_DEDUCTION.TYPE), 
							deductionRecord.getValue(SALARY_DEDUCTION.DESCRIPTION), 
							deductionRecord.getValue(SALARY_DEDUCTION.AMOUNT));
				}
				);
				deductionIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(costIter), 
				r -> r.getValue(SALARY_COST.SALARY) >= salaryId ),
				r -> r.getValue(SALARY_COST.SALARY) == salaryId )
				.forEachOrdered(costRecord->
					salary.addCost(
							costRecord.getValue(SALARY_COST.TYPE), 
							costRecord.getValue(SALARY_COST.COST_CONCEPT), 
							costRecord.getValue(SALARY_COST.DESCRIPTION), 
							costRecord.getValue(SALARY_COST.AMOUNT))
				);
				costIter.back();

				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(bonusIter), 
				r -> r.getValue(SALARY_BONUS.SALARY) >= salaryId ),
				r -> r.getValue(SALARY_BONUS.SALARY) == salaryId )
				.forEachOrdered(bonusRecord->
					salary.addBonus(
							bonusRecord.getValue(SALARY_BONUS.BONUS_CONCEPT), 
							bonusRecord.getValue(SALARY_BONUS.DESCRIPTION), 
							bonusRecord.getValue(SALARY_BONUS.AMOUNT))
				);
				bonusIter.back();

				return salary;
				}
		);
		//@formatter:on

	}

	public static Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {

		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);

		if (ctx == null) {
			List<Salary> emptyList = Collections.emptyList();
			return emptyList.stream();
		}


		//@formatter:off
		Cursor<Record> rootCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.where(conditions)
		.groupBy(SALARY.EMPLOYEE_DOCUMENT)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> salaryDataCursor = 
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
		Cursor<Record> contractDataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(CONTRACT_DATA)
		.on(
			SALARY.CONTRACT.eq(CONTRACT_DATA.CONTRACT)
			.and(CONTRACT_DATA.START_DATE.le(SALARY.END_DATE))
			.and(CONTRACT_DATA.END_DATE.isNull()
				.or(CONTRACT_DATA.END_DATE.ge(SALARY.START_DATE))
				)
		)
		.where(conditions)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		BackIterator<Record> salaryDataIter = new BackIterator<>(salaryDataCursor.iterator());
		BackIterator<Record> contractDataIter = new BackIterator<>(contractDataCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
					
					
					String employeeDocument = rootRecord.getValue(SALARY.EMPLOYEE_DOCUMENT);	

					Salary salary = supplier.get()
					.setEmployeeDocument(employeeDocument)
					.setEmployeeName(rootRecord.getValue(SALARY.EMPLOYEE_NAME))
					.setEmployeeSSNumber(rootRecord.getValue(SALARY.SOCIAL_SECURITY_NUMBER))
					.setEnterpriseDocument(rootRecord.getValue(SALARY.ENTERPRISE_DOCUMENT))
					.setEnterpriseName(rootRecord.getValue(SALARY.ENTERPRISE_NAME))
					.setEnterpriseCCC(rootRecord.getValue(SALARY.CCC))
					;
					
					
					
					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(salaryDataIter), 
					r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
					r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
					.forEachOrdered(salaryDataRecord->
						salary.setContextData(
						salaryDataRecord.getValue(SALARY_DATA.NAME), 
						salaryDataRecord.getValue(SALARY_DATA.EXPRESSION),
						salaryDataRecord.getValue(SALARY_DATA.START_DATE),
						salaryDataRecord.getValue(SALARY_DATA.END_DATE))
					);
					salaryDataIter.back();
					
					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(contractDataIter), 
					r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
					r -> r.getValue(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
					.forEachOrdered(contractDataRecord->
						salary.addContextData(
						contractDataRecord.getValue(CONTRACT_DATA.NAME), 
						contractDataRecord.getValue(CONTRACT_DATA.EXPRESSION),
						contractDataRecord.getValue(CONTRACT_DATA.START_DATE),
						contractDataRecord.getValue(CONTRACT_DATA.END_DATE))
					);
					contractDataIter.back();

					//TODO: Delete this fix for old/incomplete salaries. 
					fixSalaryData("BASE_CGC", salary, SALARY.CGC_BASE, rootRecord);
					fixSalaryData("BASE_CGP", salary, SALARY.CGP_BASE, rootRecord);
					fixSalaryData("BASE_ESTR", salary, SALARY.HEXTRA_BASE, rootRecord);
					fixSalaryData("BASE_NESTR", salary, SALARY.NON_HEXTRA_BASE, rootRecord);
					
					return salary;
				}
		);
		//@formatter:on

	}
	
	private static void fixSalaryData(String name, Salary salary, TableField<SalaryRecord, Double> field,Record record){
		
		if ( record.getValue(field) == null ) 
			return;
		
		Date salaryStart = record.getValue(SALARY.START_DATE);
		Date salaryEnd = record.getValue(SALARY.END_DATE);
		
		List<ContextData> datas = salary.getContextData().get(name);
		if ( datas != null && !datas.isEmpty() )
			for(ContextData data: datas )
				if ( compare( 
						max(salaryStart,data.getStartDate()), 
						min(salaryEnd,data.getEndDate()))<= 0)
					return;
		
		salary.setContextData(name, 
				Double.toString(record.getValue(field)),
				salaryStart,
				salaryEnd);
	}
	

	private enum SalaryType {
		SALARY, EXTRA, SETTLE, DELAY, NOT_ENJOYED_VACATIONS;

		byte value() {
			return (byte) ordinal();
		}
	}

	private static final SalaryPropertiesDAO SALARY_PROPERTIES = new SalaryPropertiesDAO();

	private static class SalaryPropertiesDAO implements SalaryProperties {

		private Condition[] getConditions(SalaryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
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

	private static class BackIterator<T> implements Iterator<T> {

		private T next;
		private T prev;
		private Iterator<T> iterator;

		public BackIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public T next() {
			prev = next != null ? next : iterator.next();
			next = null;
			return prev;
		}

		@Override
		public boolean hasNext() {
			return next != null || iterator.hasNext();
		}

		public void back(){
			next = prev;
		}
	}

}
