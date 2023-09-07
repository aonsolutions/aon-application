package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.Keys.FK_SALARY_BONUS_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_COST_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DATA_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DEDUCTION_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_PAYMENT_SALARY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.compare;
import static com.esferalia.aon.watson.util.AonDateUtils.max;
import static com.esferalia.aon.watson.util.AonDateUtils.min;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.lambda.Seq;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.SalaryBonusRecord;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDeductionRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;
import com.esferalia.aon.occam.api.model.Settle;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDAO {

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
			.and(SALARY.TYPE.in(SalaryType.SALARIES )) // Skip SLD ( L00, L13... )
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
					entry.setSalaryDescription(rec.get(SALARY.EMPLOYEE_NAME));
				}
				entry.setSalaryCount(entry.getSalaryCount()+1);
				entry.setEmployeeSocialInsurance( AonMathUtils.round(entry.getEmployeeSocialInsurance() 
						+ rec.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)));;
				entry.setCompanySocialInsurance( AonMathUtils.round(entry.getCompanySocialInsurance() 
						+ rec.get(SALARY.TOTAL_ENTERPRISE)));;
				double totalIrpf = rec.get(SALARY.TOTAL_IRPF);
				if (AonMathUtils.isZero(rec.get(SALARY.INKIND_IRPF_BASE))) {
					entry.setIrpf( AonMathUtils.round(entry.getIrpf() + totalIrpf));
				} else {
					double base = rec.get(SALARY.IRPF_BASE);
					double moneyBase = rec.get(SALARY.MONEY_IRPF_BASE);
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
						double amount = AonMathUtils.round(pay.get(SALARY_PAYMENT.AMOUNT));
						Byte type = pay.get(SALARY_PAYMENT.TYPE);
						// Puede ser nulo??
						if (type == null) {
							entry.setMoneySalary( AonMathUtils.round(entry.getMoneySalary() + amount));
						} else 
						// ----------------
						if (AonNumberUtils.between(type, 42, 50) ) {
							entry.setAllowance( AonMathUtils.round(entry.getAllowance()  + amount));
						} else if (AonNumberUtils.between(type, 51, 54) ) {
							entry.setSalaryCompensation( AonMathUtils.round(entry.getSalaryCompensation() + amount));
						} else if (AonNumberUtils.between(type, 13, 26) ) {
							entry.setInKindSalary( AonMathUtils.round(entry.getInKindSalary() + amount));
						// entry.setSalaryOtherDeductions( AonMathUtils.round(entry.getSalaryOtherDeductions() + amount));
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
						double amount = AonMathUtils.round(ded.get(SALARY_DEDUCTION.AMOUNT));
						Byte type = ded.get(SALARY_DEDUCTION.TYPE);
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
						double amount = AonMathUtils.round(emb.get(SALARY_EMBARGO.AMOUNT));
						entry.setSalaryDedSeize( AonMathUtils.round(entry.getSalaryDedSeize() + amount));
				});
			});
		return map.values().stream();
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
		Cursor<Record> paymentCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_PAYMENT)
		.onKey(FK_SALARY_PAYMENT_SALARY)
		.where(conditions)
		.orderBy(SALARY_PAYMENT.SALARY)
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
		
		//@formatter:off
		Cursor<Record> embargoCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_EMBARGO)
		.onKey()
		.where(conditions)
		.orderBy(SALARY_EMBARGO.SALARY)
		.fetchLazy();
		//@formatter:on

		BackIterator<Record> paymentIter = new BackIterator<>(paymentCursor.iterator());
		BackIterator<Record> dataIter = new BackIterator<>(dataCursor.iterator());
		BackIterator<Record> costIter = new BackIterator<>(costCursor.iterator());
		BackIterator<Record> deductionIter = new BackIterator<>(deductionCursor.iterator());
		BackIterator<Record> bonusIter = new BackIterator<>(bonusCursor.iterator());
		BackIterator<Record> embargoIter = new BackIterator<>(embargoCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
				Salary salary = supplier.get()
				.setId(rootRecord.get(SALARY.ID))		
				.setStartDate(rootRecord.get(SALARY.START_DATE))
				.setEndDate(rootRecord.get(SALARY.END_DATE))
				.setIssueDate(rootRecord.get(SALARY.ISSUE_DATE))
				.setSalaryDays(rootRecord.get(SALARY.TIME_UNITS))
				.setEmployeeName(rootRecord.get(SALARY.EMPLOYEE_NAME))
				.setEnterpriseCCC(rootRecord.get(SALARY.CCC))
				.setEnterpriseName(rootRecord.get(SALARY.ENTERPRISE_NAME))
				.setEnterpriseDocument(rootRecord.get(SALARY.ENTERPRISE_DOCUMENT))
				.setIrpfBase(rootRecord.get(SALARY.IRPF_BASE))
				.setEmployeeDocument(rootRecord.get(SALARY.EMPLOYEE_DOCUMENT))
				.setTotalLiquid(rootRecord.get(SALARY.TOTAL_LIQUID))
				.setTotalPayment(rootRecord.get(SALARY.TOTAL_PAYMENT))
				.setTotalIrpf(rootRecord.get(SALARY.TOTAL_IRPF))
				.setTotalDeduction(rootRecord.get(SALARY.TOTAL_DEDUCTION))
				.setCommonContingenciesBase(rootRecord.get(SALARY.CGC_BASE))
				.setProfessionalContingenciesBase(rootRecord.get(SALARY.CGP_BASE))
				.setIrpfBase(rootRecord.get(SALARY.IRPF_BASE))
				.setMoneyIrpfBase(rootRecord.get(SALARY.MONEY_IRPF_BASE))
				.setInkindIrpfBase(rootRecord.get(SALARY.INKIND_IRPF_BASE))
				.setTotalEnterprise(rootRecord.get(SALARY.TOTAL_ENTERPRISE))
				.setTotalSSContributions(rootRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS))
				.setEmployeeSeniorityDate(rootRecord.get(SALARY.SENIORITY_DATE))
				.setEnterpriseAddress(rootRecord.get(SALARY.ENTERPRISE_ADDRESS))
				.setEmployeeSSNumber(rootRecord.get(SALARY.SOCIAL_SECURITY_NUMBER))
				.setEmployeeCategory(rootRecord.get(SALARY.CATEGORY))
				.setSalaryType(rootRecord.get(SALARY.TYPE))
				.setEmployeeQuoteGroup(rootRecord.get(SALARY.QUOTE_GROUP))
				.setRemuneration(rootRecord.get(SALARY.REMUNERATION))
				.setExtraProrationBase(rootRecord.get(SALARY.PRO_EXT_BASE))
				;
				
				int salaryId = rootRecord.get(SALARY.ID);
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(dataIter), 
				r -> r.get(SALARY_DATA.SALARY) >= salaryId ),
				r -> r.get(SALARY_DATA.SALARY) == salaryId )
				.forEachOrdered(dataRecord->
					salary.setContextData(
					dataRecord.get(SALARY_DATA.NAME), 
					dataRecord.get(SALARY_DATA.EXPRESSION),
					dataRecord.get(SALARY_DATA.START_DATE),
					dataRecord.get(SALARY_DATA.END_DATE))
				);
				
				dataIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(paymentIter), 
				r -> r.get(SALARY_PAYMENT.SALARY) >= salaryId ),
				r -> r.get(SALARY_PAYMENT.SALARY) == salaryId )
				.forEachOrdered(paymentRecord->{
					salary.addPayment(
							paymentRecord.get(SALARY_PAYMENT.PAYMENT_CONCEPT), 
							paymentRecord.get(SALARY_PAYMENT.EXPRESSION), 
							paymentRecord.get(SALARY_PAYMENT.DESCRIPTION), 
							paymentRecord.get(SALARY_PAYMENT.AMOUNT),
							paymentRecord.get(SALARY_PAYMENT.QUOTE),
							paymentRecord.get(SALARY_PAYMENT.TYPE)
							);
				}
				);
				paymentIter.back();

				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(deductionIter), 
				r -> r.get(SALARY_DEDUCTION.SALARY) >= salaryId ),
				r -> r.get(SALARY_DEDUCTION.SALARY) == salaryId )
				.forEachOrdered(deductionRecord->{
					salary.addDeduction(
							deductionRecord.get(SALARY_DEDUCTION.TYPE), 
							deductionRecord.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), 
							deductionRecord.get(SALARY_DEDUCTION.DESCRIPTION), 
							deductionRecord.get(SALARY_DEDUCTION.AMOUNT),
							deductionRecord.get(SALARY_DEDUCTION.TYPE)
							);
				}
				);
				deductionIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(costIter), 
				r -> r.get(SALARY_COST.SALARY) >= salaryId ),
				r -> r.get(SALARY_COST.SALARY) == salaryId )
				.forEachOrdered(costRecord->
					salary.addCost(
							costRecord.get(SALARY_COST.TYPE), 
							costRecord.get(SALARY_COST.COST_CONCEPT), 
							costRecord.get(SALARY_COST.DESCRIPTION), 
							costRecord.get(SALARY_COST.AMOUNT),
							costRecord.get(SALARY_COST.TYPE)
							)
				);
				costIter.back();

				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(bonusIter), 
				r -> r.get(SALARY_BONUS.SALARY) >= salaryId ),
				r -> r.get(SALARY_BONUS.SALARY) == salaryId )
				.forEachOrdered(bonusRecord->
					salary.addBonus(
							bonusRecord.get(SALARY_BONUS.BONUS_CONCEPT), 
							bonusRecord.get(SALARY_BONUS.DESCRIPTION), 
							bonusRecord.get(SALARY_BONUS.AMOUNT)
							)
				);
				bonusIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(embargoIter), 
				r -> r.get(SALARY_EMBARGO.SALARY) >= salaryId ),
				r -> r.get(SALARY_EMBARGO.SALARY) == salaryId )
				.forEachOrdered(embargoRecord ->
					salary.addEmbargo(
							embargoRecord.get(SALARY_EMBARGO.DESCRIPTION),
							embargoRecord.get(SALARY_EMBARGO.AMOUNT)
							)
				);
				embargoIter.back();

				return salary;
				}
		);
		//@formatter:on

	}

	/**
	 * Get Settles from database
	 * @param ctx		- Context 
	 * @param filter	- The filter to apply
	 * @param supplier	- Supplier
	 * @return [Stream of Settles] The settles that fit the filter
	 */
	public static Stream<Settle> getSettles(AONContext ctx,
			SalaryFilter filter, Supplier<Settle> supplier) {

		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);

		if (ctx == null) {
			List<Settle> emptyList = Collections.emptyList();
			return emptyList.stream();
		}

		//@formatter:off
		Cursor<Record> rootCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.leftJoin(RDIR_STAFF)
		.on(SALARY.DOMAIN.eq(RDIR_STAFF.DOMAIN))
		.leftJoin(SALARY_DATA)
		.on(SALARY_DATA.SALARY.eq(SALARY.ID).and(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION")))
		.leftJoin(REGISTRY)
		.on(REGISTRY.DOCUMENT.eq(SALARY.ENTERPRISE_DOCUMENT))
		.leftJoin(RADDRESS)
		.on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
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
		Cursor<Record> paymentCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.join(SALARY_PAYMENT)
		.onKey(FK_SALARY_PAYMENT_SALARY)
		.where(conditions)
		.orderBy(SALARY_PAYMENT.SALARY)
		.fetchLazy();
		//@formatter:on
		
		Cursor<Record> embargoCursor = 
				ctx.getDslContext()
				.select()
				.from(SALARY)
				.join(SALARY_EMBARGO)
				.onKey()
				.where(conditions)
				.orderBy(SALARY_EMBARGO.SALARY)
				.fetchLazy();

		BackIterator<Record> paymentIter = new BackIterator<>(paymentCursor.iterator());
		BackIterator<Record> deductionIter = new BackIterator<>(deductionCursor.iterator());
		BackIterator<Record> embargoIter = new BackIterator<>(embargoCursor.iterator());
		BackIterator<Record> dataIter = new BackIterator<>(dataCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
				Salary salary = supplier.get()
				.setId(rootRecord.get(SALARY.ID))		
				.setStartDate(rootRecord.get(SALARY.START_DATE))
				.setEndDate(rootRecord.get(SALARY.END_DATE))
				.setIssueDate(rootRecord.get(SALARY.ISSUE_DATE))
				.setSalaryDays(rootRecord.get(SALARY.TIME_UNITS))
				.setEmployeeName(rootRecord.get(SALARY.EMPLOYEE_NAME))
				.setEnterpriseCCC(rootRecord.get(SALARY.CCC))
				.setEnterpriseName(rootRecord.get(SALARY.ENTERPRISE_NAME))
				.setEnterpriseDocument(rootRecord.get(SALARY.ENTERPRISE_DOCUMENT))
				.setIrpfBase(rootRecord.get(SALARY.IRPF_BASE))
				.setEmployeeDocument(rootRecord.get(SALARY.EMPLOYEE_DOCUMENT))
				.setTotalLiquid(rootRecord.get(SALARY.TOTAL_LIQUID))
				.setTotalPayment(rootRecord.get(SALARY.TOTAL_PAYMENT))
				.setTotalIrpf(rootRecord.get(SALARY.TOTAL_IRPF))
				.setTotalDeduction(rootRecord.get(SALARY.TOTAL_DEDUCTION))
				.setCommonContingenciesBase(rootRecord.get(SALARY.CGC_BASE))
				.setProfessionalContingenciesBase(rootRecord.get(SALARY.CGP_BASE))
				.setIrpfBase(rootRecord.get(SALARY.IRPF_BASE))
				.setMoneyIrpfBase(rootRecord.get(SALARY.MONEY_IRPF_BASE))
				.setInkindIrpfBase(rootRecord.get(SALARY.INKIND_IRPF_BASE))
				.setTotalEnterprise(rootRecord.get(SALARY.TOTAL_ENTERPRISE))
				.setTotalSSContributions(rootRecord.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS))
				.setEmployeeSeniorityDate(rootRecord.get(SALARY.SENIORITY_DATE))
				.setEnterpriseAddress(rootRecord.get(SALARY.ENTERPRISE_ADDRESS))
				.setEmployeeSSNumber(rootRecord.get(SALARY.SOCIAL_SECURITY_NUMBER))
				.setEmployeeCategory(rootRecord.get(SALARY.CATEGORY))
				.setSalaryType(rootRecord.get(SALARY.TYPE))
				.setEmployeeQuoteGroup(rootRecord.get(SALARY.QUOTE_GROUP))
				.setRemuneration(rootRecord.get(SALARY.REMUNERATION))
				.setExtraProrationBase(rootRecord.get(SALARY.PRO_EXT_BASE))
				;
				
				int salaryId = rootRecord.get(SALARY.ID);
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(dataIter), 
				r -> r.get(SALARY_DATA.SALARY) >= salaryId ),
				r -> r.get(SALARY_DATA.SALARY) == salaryId )
				.forEachOrdered(dataRecord->
					salary.setContextData(
					dataRecord.get(SALARY_DATA.NAME), 
					dataRecord.get(SALARY_DATA.EXPRESSION),
					dataRecord.get(SALARY_DATA.START_DATE),
					dataRecord.get(SALARY_DATA.END_DATE))
				);
				
				dataIter.back();
				
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(paymentIter), 
				r -> r.get(SALARY_PAYMENT.SALARY) >= salaryId ),
				r -> r.get(SALARY_PAYMENT.SALARY) == salaryId )
				.forEachOrdered(paymentRecord->{
					salary.addPayment(
							paymentRecord.get(SALARY_PAYMENT.PAYMENT_CONCEPT), 
							paymentRecord.get(SALARY_PAYMENT.EXPRESSION), 
							paymentRecord.get(SALARY_PAYMENT.DESCRIPTION), 
							paymentRecord.get(SALARY_PAYMENT.AMOUNT),
							paymentRecord.get(SALARY_PAYMENT.QUOTE),
							paymentRecord.get(SALARY_PAYMENT.TYPE)
							);
				}
				);
				paymentIter.back();

				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(deductionIter), 
				r -> r.get(SALARY_DEDUCTION.SALARY) >= salaryId ),
				r -> r.get(SALARY_DEDUCTION.SALARY) == salaryId )
				.forEachOrdered(deductionRecord->{
					salary.addDeduction(
							deductionRecord.get(SALARY_DEDUCTION.TYPE), 
							deductionRecord.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), 
							deductionRecord.get(SALARY_DEDUCTION.DESCRIPTION), 
							deductionRecord.get(SALARY_DEDUCTION.AMOUNT),
							deductionRecord.get(SALARY_DEDUCTION.TYPE)
							);
				}
				);
				deductionIter.back();
				
				Seq.limitWhile(
				Seq.skipUntil(Seq.seq(embargoIter), 
				r -> r.get(SALARY_EMBARGO.SALARY) >= salaryId ),
				r -> r.get(SALARY_EMBARGO.SALARY) == salaryId )
				.forEachOrdered(embargoRecord ->
					salary.addEmbargo(
							embargoRecord.get(SALARY_EMBARGO.DESCRIPTION),
							embargoRecord.get(SALARY_EMBARGO.AMOUNT)
							)
				);
				embargoIter.back();

				Settle settle = new Settle();
				settle.fillWithSalary(salary);
				
				settle.setRepresentativeName(rootRecord.get(RDIR_STAFF.NAME));
				settle.setRepresentativeDocument(rootRecord.get(RDIR_STAFF.DOCUMENT));
				settle.setCause(rootRecord.get(SALARY_DATA.EXPRESSION));
				settle.setLocation(rootRecord.get(RADDRESS.CITY));
				
				return settle;
				}
		);
		//@formatter:on

	}
	
	public static Stream<Salary> getContractData(AONContext ctx, SalaryFilter filter, Supplier<Salary> supplier){
		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);
		
		return 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.leftJoin(CONTRACT_DATA)
		.on(
			SALARY.CONTRACT.eq(CONTRACT_DATA.CONTRACT)
			.and(CONTRACT_DATA.START_DATE.le(SALARY.END_DATE))
			.and(CONTRACT_DATA.END_DATE.isNull()
				.or(CONTRACT_DATA.END_DATE.ge(SALARY.START_DATE))
				)
		)
		.where(conditions)
		.and(CONTRACT_DATA.END_DATE.isNull()
			.or(CONTRACT_DATA.END_DATE.ge(CONTRACT_DATA.START_DATE))
		)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchGroups(SALARY.ID)
		.entrySet()
		.stream()
		.map(entry -> {
			
			Salary salary = supplier.get();
			entry.getValue().forEach( contractDataRecord -> {
				salary.addContextData(
				contractDataRecord.get(CONTRACT_DATA.NAME), 
				contractDataRecord.get(CONTRACT_DATA.EXPRESSION),
				contractDataRecord.get(CONTRACT_DATA.START_DATE),
				contractDataRecord.get(CONTRACT_DATA.END_DATE));
				
			});
			
			return salary;
			
		})
		;
			
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
		.innerJoin(CONTRACT).onKey()
		.leftJoin(ENTERPRISE_CCC).onKey()
		.where(conditions)
		.groupBy(EMPLOYEE_DOCUMENT)
		.orderBy(EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> salaryDataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.leftJoin(SALARY_DATA)
		.onKey(FK_SALARY_DATA_SALARY)
		.where(conditions)
		.orderBy(
		EMPLOYEE_DOCUMENT, SALARY.ISSUE_DATE.asc())
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> contractDataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.leftJoin(CONTRACT_DATA)
		.on(
			SALARY.CONTRACT.eq(CONTRACT_DATA.CONTRACT)
			.and(CONTRACT_DATA.START_DATE.le(SALARY.END_DATE))
			.and(CONTRACT_DATA.END_DATE.isNull()
				.or(CONTRACT_DATA.END_DATE.ge(SALARY.START_DATE))
				)
		)
		.where(conditions)
		.and(CONTRACT_DATA.END_DATE.isNull()
			.or(CONTRACT_DATA.END_DATE.ge(CONTRACT_DATA.START_DATE))
		)
		.orderBy(EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> salaryImplicitDataCursor = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.innerJoin(CONTRACT).onKey()
		.leftJoin(ENTERPRISE_CCC).onKey()
		.where(conditions)
		.orderBy(EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		BackIterator<Record> salaryDataIter = new BackIterator<>(salaryDataCursor.iterator());
		BackIterator<Record> contractDataIter = new BackIterator<>(contractDataCursor.iterator());
		BackIterator<Record> salaryImlicitDataIter = new BackIterator<>(salaryImplicitDataCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
					
					
					String employeeDocument = getEmployeeDocument(rootRecord);	

					Salary salary = supplier.get()
					.setEmployeeDocument(rootRecord.get(SALARY.EMPLOYEE_DOCUMENT))
					.setEmployeeName(rootRecord.get(SALARY.EMPLOYEE_NAME))
					.setEmployeeSSNumber(rootRecord.get(SALARY.SOCIAL_SECURITY_NUMBER))
					.setEnterpriseDocument(rootRecord.get(SALARY.ENTERPRISE_DOCUMENT))
					.setEnterpriseName(rootRecord.get(SALARY.ENTERPRISE_NAME))
					.setEnterpriseCCC(rootRecord.get(SALARY.CCC))
					.setStartDate(rootRecord.get(SALARY.START_DATE))
					.setEndDate(rootRecord.get(SALARY.END_DATE))
					.setTotalPayment(rootRecord.get(SALARY.TOTAL_PAYMENT))
					.setSalaryType(rootRecord.get(SALARY.TYPE))
					;
					
					
					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(salaryDataIter), 
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) ),
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) )
					.filter(salaryDataRecord -> salaryDataRecord.get(SALARY_DATA.ID) != null )
					.forEachOrdered(salaryDataRecord->
						salary.setContextData(
						salaryDataRecord.get(SALARY_DATA.NAME), 
						salaryDataRecord.get(SALARY_DATA.EXPRESSION),
						salaryDataRecord.get(SALARY_DATA.START_DATE),
						salaryDataRecord.get(SALARY_DATA.END_DATE))
					);
					salaryDataIter.back();
					
					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(contractDataIter), 
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) ),
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) )
					.filter(contractDataRecord -> contractDataRecord.get(CONTRACT_DATA.ID) != null )
					.forEachOrdered(contractDataRecord->
						salary.addContextData(
						contractDataRecord.get(CONTRACT_DATA.NAME), 
						contractDataRecord.get(CONTRACT_DATA.EXPRESSION),
						contractDataRecord.get(CONTRACT_DATA.START_DATE),
						contractDataRecord.get(CONTRACT_DATA.END_DATE))
					);
					contractDataIter.back();


					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(salaryImlicitDataIter), 
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) ),
					r -> AonStringUtils.equalsIgnoreCase(getEmployeeDocument(r), employeeDocument) )
					.forEachOrdered(salaryRecord-> {
						Optional.ofNullable(salaryRecord.get(SALARY.TOTAL_PAYMENT))
						.ifPresent( d ->  {
							salary.setContextData(
									"TOTAL_DEVENGADO", 
									String.format(Locale.ROOT, "%f", d), 
									salaryRecord.get(SALARY.START_DATE), 
									salaryRecord.get(SALARY.END_DATE));
						});
						Optional.ofNullable(salaryRecord.get(ENTERPRISE_CCC.TYPE))
						.ifPresent( b ->  {
							salary.setContextData(
									"CCC_TYPE", 
									String.format(Locale.ROOT, "%d", b), 
									salaryRecord.get(SALARY.START_DATE), 
									salaryRecord.get(SALARY.END_DATE));
						});
					}
					);
					salaryImlicitDataIter.back();

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

	public static Collection<Salary> saveSalaries(AONContext ctx, Integer domainId, Collection<Salary> salaries ) {
		salaries.forEach( salary -> {
			removeSalary(ctx, domainId, salary);
			SalaryRecord salaryRecord = insertSalary(ctx, domainId, salary);
			salary.setId(salaryRecord.getId());
		});
		return salaries;
	}

	public static void deleteSalaries(AONContext ctx, SalaryFilter filter) {
		
		Condition conditions[] = SALARY_PROPERTIES.getConditions(filter);
		
		SelectConditionStep<Record1<Integer>> salariesSelect = 
		DSL
		.select(SALARY.ID)
		.from(SALARY)
		.where(conditions);
		
		ctx.getDslContext().transaction( t -> {
			DSLContext dslContext = t.dsl();				
			dslContext.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(conditions).execute();
			dslContext.delete(SALARY).where(conditions).execute();
			
		});

	}
	
	private static void removeSalary(AONContext ctx, Integer domainId, Salary salary ) {
		List<Integer> salaryIds = 
		ctx.getDslContext()
		.select()
		.from(SALARY)
		.where(SALARY.DOMAIN.eq(domainId))
		.and(SALARY.CCC.eq(salary.getEnterpriseCCC()))
		.and(SALARY.SOCIAL_SECURITY_NUMBER.eq(salary.getEmployeeSSNumber()))
		.and(SALARY.START_DATE.le(toSql(salary.getEndDate())))
		.and(SALARY.END_DATE.ge(toSql(salary.getStartDate())))
		.and(SALARY.TYPE.eq(value(salary.getSalaryType(), com.esferalia.aon.occam.api.model.type.SalaryType.class)))
		.fetch(SALARY.ID);
		
		ctx.getDslContext().transaction(t -> {
			DSLContext dslContext = t.dsl();
			
			dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(salaryIds)).execute();
			dslContext.delete(SALARY_COST).where(SALARY_COST.SALARY.in(salaryIds)).execute();
			dslContext.delete(SALARY_BONUS).where(SALARY_BONUS.SALARY.in(salaryIds)).execute();
			dslContext.delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(salaryIds)).execute();
			dslContext.delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(salaryIds)).execute();
			dslContext.delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(salaryIds)).execute();			
			dslContext.delete(SALARY).where(SALARY.ID.in(salaryIds)).execute();
			
		});
	}

	private static SalaryRecord insertSalary(AONContext ctx, Integer domainId, Salary salary ) {
		
		InsertSetStep<SalaryRecord> salaryInsert = 
		ctx.getDslContext().insertInto(SALARY);
		
			
		ContractRecord contractRecord = 
		getContract(ctx, domainId, salary)
		//TODO: EmployeeNotFoundException 
		.orElseThrow( () -> new IllegalArgumentException("") ); 
		
		SalaryRecord salaryRecord = 
		getSalaryRecord(salary);
		salaryRecord.setDomain(contractRecord.getDomain());
		salaryRecord.setContract(contractRecord.getId());
		
		salaryRecord = 
		salaryInsert.set(salaryRecord).returning().fetchOptional()
		//TODO: SalaryNotFoundException 
		.orElseThrow( () -> new IllegalArgumentException("") );
		
		int id = salaryRecord.getId();
		int domain = salaryRecord.getDomain();
		
		List<SalaryBonusRecord> bonusRecords = toList(salary.getBonuses(), bonus -> getSalaryBonusRecord(domain, id, bonus));
		insert(bonusRecords, ctx.getDslContext().insertInto(SALARY_BONUS));

		List<SalaryCostRecord> costRecords = toList(salary.getCosts(), cost -> getSalaryCostRecord(domain, id, cost));
		insert(costRecords, ctx.getDslContext().insertInto(SALARY_COST));
		
		List<SalaryDeductionRecord> deductionRecords = toList(salary.getDeductions(), deduction -> getSalaryDeductionRecord(domain, id, deduction));
		insert(deductionRecords, ctx.getDslContext().insertInto(SALARY_DEDUCTION));

		List<SalaryPaymentRecord> paymentRecords = toList(salary.getPayments(), payment -> getSalaryPaymentRecord(domain, id, payment));
		insert(paymentRecords, ctx.getDslContext().insertInto(SALARY_PAYMENT));
		
		List<SalaryDataRecord> dataRecords = salary.getContextData().entrySet().stream()
		.flatMap(entry -> toList(entry.getValue(), d -> getSalaryDataRecord(domain, id, entry.getKey(), d)).stream())
		.collect(Collectors.toList());		
		insert(dataRecords, ctx.getDslContext().insertInto(SALARY_DATA));
		
		return salaryRecord;
	}
	
	private static <R extends Record > void insert(List<R> records, InsertSetStep<R> insert ) {
		if  ( records.isEmpty() )
			return;
		
		int last = records.size() -1;
		
		for(int i = 0; i < last; i++ )
			insert = insert.set(records.get(i)).newRecord();
		
		insert.set(records.get(last)).execute();
	}
	

	private static Optional<ContractRecord> getContract(AONContext ctx, Integer domainId, Salary salary) {
		return 
		ctx.getDslContext()
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(CONTRACT.DOMAIN.eq(domainId))
		.and(ENTERPRISE_CCC.CCC.eq(salary.getEnterpriseCCC()))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(salary.getEmployeeSSNumber()))
		.and(CONTRACT.START_DATE.le(toSql(salary.getEndDate())))
		.and(CONTRACT.END_DATE.ge(toSql(salary.getStartDate())).or(CONTRACT.END_DATE.isNull()))
		.orderBy(DSL.abs(DSL.dateDiff(toSql(salary.getEndDate()), DSL.ifnull(CONTRACT.END_DATE, DSL.date(getNullDate())))).asc())
		.fetchStreamInto(CONTRACT)
		.findFirst();
		//.fetchOptionalInto(CONTRACT);
	}
	
	private static SalaryRecord getSalaryRecord(Salary salary) {
		SalaryRecord record  = new SalaryRecord();
		
		
		//record.setSsRegime(0);																					// not null & default 0
		record.setTimeUnits(0);	 																					// not null & without default value
		record.setRegistration(666); 																				// not null & without default value
		

		record.setStartDate(toSql(salary.getStartDate()));															// not null & without default value
		record.setEndDate(toSql(salary.getEndDate()));																// not null & without default value
		record.setIssueDate(toSql(salary.getIssueDate()));															// not null & without default value
		record.setChargeDate(toSql(salary.getIssueDate()));															// not null & without default value

		record.setTimeUnits(Optional.ofNullable(salary.getSalaryDays()).orElse(0));									// not null & default 0

		record.setCcc(salary.getEnterpriseCCC());																	// default null	
		record.setEnterpriseName(salary.getEnterpriseName());														// default null
		record.setEnterpriseAddress(salary.getEnterpriseAddress());													// default null
		record.setEnterpriseDocument(salary.getEnterpriseDocument());												// default null
		
		record.setEmployeeName(salary.getEmployeeName());															// default null
		record.setCategory(salary.getEmployeeCategory());															// default null
		record.setQuoteGroup(salary.getEmployeeQuoteGroup());														// default null	
		record.setSeniorityDate(toSql(salary.getEmployeeSeniorityDate()));											// default null
		record.setEmployeeDocument(salary.getEmployeeDocument());													// default null
		record.setSocialSecurityNumber(salary.getEmployeeSSNumber());												// default null
		
		record.setIrpfBase(Optional.ofNullable(salary.getIrpfBase()).orElse(0.00));									// not null & default 0						
		record.setMoneyIrpfBase(Optional.ofNullable(salary.getMoneyIrpfBase()).orElse(0.00));						// not null & default 0
		record.setInkindIrpfBase(Optional.ofNullable(salary.getInkindIrpfBase()).orElse(0.00));						// not null & default 0

		record.setProExtBase(Optional.ofNullable(salary.getExtraProrationBase()).orElse(0.00));						// not null & default 0
		record.setCgcBase(Optional.ofNullable(salary.getCommonContingenciesBase()).orElse(0.00));					// not null & default 0
		record.setCgpBase(Optional.ofNullable(salary.getProfessionalContingenciesBase()).orElse(0.00));				// not null & default 0

		record.setTotalIrpf(Optional.ofNullable(salary.getTotalIrpf()).orElse(0.00));								// not null & default 0
		record.setRemuneration(Optional.ofNullable(salary.getRemuneration()).orElse(0.00));							// not null & default 0
		record.setTotalLiquid(Optional.ofNullable(salary.getTotalLiquid()).orElse(0.00));							// not null & default 0
		record.setTotalDeduction(Optional.ofNullable(salary.getTotalDeduction()).orElse(0.00));						// not null & default 0	
		record.setTotalEnterprise(Optional.ofNullable(salary.getTotalEnterprise()).orElse(0.00));					// not null & default 0
		record.setTotalPayment(Optional.ofNullable(salary.getTotalPayment()).orElse(0.00));							// not null & default 0	
		record.setSocialSecurityContributions(Optional.ofNullable(salary.getTotalSSContributions()).orElse(0.00));	// not null & default 0


		record.setType(value(salary.getSalaryType(), com.esferalia.aon.occam.api.model.type.SalaryType.class));
		

		return record;
		
	}
	
	private static SalaryBonusRecord getSalaryBonusRecord(Integer domain, Integer salary, Salary.Bonus bonus) {
		SalaryBonusRecord record = new SalaryBonusRecord();
		
		record.setDomain(domain);
		record.setSalary(salary);
		record.setAmount(bonus.getAmount());
		record.setDescription(bonus.getDescription());
		//record.setType(value(BonusType.SOCIAL_SECURITY, BonusType.class));
		
		return record;
	}
	
	
	private static SalaryDataRecord getSalaryDataRecord(Integer domain, Integer salary, String name, Salary.ContextData data) {
		SalaryDataRecord record = new SalaryDataRecord();
		
		record.setDomain(domain);
		record.setSalary(salary);
		record.setName(name);
		record.setExpression(data.getExpression());
		record.setStartDate(toSql(data.getStartDate()));
		record.setEndDate(toSql(data.getEndDate()));
		
		return record;
	}

	private static SalaryCostRecord getSalaryCostRecord(Integer domain, Integer salary, Salary.Cost cost) {
		SalaryCostRecord record = new SalaryCostRecord();
		
		record.setDomain(domain);
		record.setSalary(salary);
		record.setAmount(cost.getAmount());
		record.setCostConcept(cost.getName());
		record.setDescription(cost.getDescription());
		record.setType(value(cost.getCostType(), DeductionType.class));
		
		return record;
	}
	
	private static SalaryDeductionRecord getSalaryDeductionRecord(Integer domain, Integer salary, Salary.Deduction deduction) {
		SalaryDeductionRecord record = new SalaryDeductionRecord();
		
		record.setDomain(domain);
		record.setSalary(salary);
		record.setAmount(deduction.getAmount());
		record.setDeductionConcept(deduction.getName());
		record.setDescription(deduction.getDescription());
		record.setType(value(deduction.getDeductionType(), DeductionType.class));
		
		return record;
	}

	private static SalaryPaymentRecord getSalaryPaymentRecord(Integer domain, Integer salary, Salary.Payment payment) {
		SalaryPaymentRecord record = new SalaryPaymentRecord();
		
		record.setDomain(domain);
		record.setSalary(salary);
		record.setQuote(payment.getQuote());
		//record.setIrpf(payment.getTax());
		record.setAmount(payment.getAmount());
		record.setPaymentConcept(payment.getName());
		record.setExpression(payment.getExpression());
		record.setDescription(payment.getDescription());
		record.setType(value(payment.getPaymentType(), PaymentType.class));
		
		return record;
	}
	
	private static <T,R> List<R> toList(Collection<T> list, Function<T, R> mapper) {
		return list.stream().map(mapper).collect(Collectors.toList());
	}

	private static void fixSalaryData(String name, Salary salary, TableField<SalaryRecord, Double> field,Record record){
		
		if ( record.get(field) == null ) 
			return;
		
		Date salaryStart = record.get(SALARY.START_DATE);
		Date salaryEnd = record.get(SALARY.END_DATE);
		
		List<ContextData> datas = salary.getContextData().get(name);
		if ( datas != null && !datas.isEmpty() )
			for(ContextData data: datas )
				if ( compare( 
						max(salaryStart,data.getStartDate()), 
						min(salaryEnd,data.getEndDate()))<= 0)
					return;
		
		salary.setContextData(name, 
				Double.toString(record.get(field)),
				salaryStart,
				salaryEnd);
	}
	

//	private enum SalaryType {
//		SALARY, EXTRA, SETTLE, DELAY, NOT_ENJOYED_VACATIONS, L00;
//
//		byte value() {
//			return (byte) ordinal();
//		}
//	}

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
		public Property<String> getSSProperty() {
			return new FilterDAO.PropertyDAO<String>(SALARY.SOCIAL_SECURITY_NUMBER);
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

		@Override
		public Property<Boolean> getIsL00Property() {
			return new FilterDAO.PropertyValueDAO<Byte>(SALARY.TYPE,
					SalaryType.L00.value());
		}

		@Override
		public Property<Date> getIssueDateProperty() {
			return new FilterDAO.DatePropertyDAO(SALARY.ISSUE_DATE);
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
	
	private static java.sql.Date toSql(Date date) {
		if ( date == null )
			return null;
		return new java.sql.Date(date.getTime());
	}
	
	private static <T extends Enum<?>> Byte value(T t, Class<T> clazz){
		T constants [] = clazz.getEnumConstants();
		for (byte i = 0; i < constants.length; i++)
			if ( constants[i] == t )
				return i;
		
		return null;
	}
	
	private static java.sql.Date getNullDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 100);
		return new java.sql.Date(calendar.getTimeInMillis());
	}
	
	private  static final Field<String> EMPLOYEE_DOCUMENT = DSL.lpad(DSL.trim(SALARY.EMPLOYEE_DOCUMENT), 16, '0');
	
	private static String getEmployeeDocument(Record record) {
		return AonStringUtils.leftPad(AonStringUtils.trim(record.get(SALARY.EMPLOYEE_DOCUMENT)), 16, '0');
	}
	
	

}
