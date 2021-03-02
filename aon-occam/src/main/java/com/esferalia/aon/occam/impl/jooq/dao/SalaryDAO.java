package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.Keys.FK_SALARY_BONUS_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_COST_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DATA_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_DEDUCTION_SALARY;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_PAYMENT_SALARY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
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

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.lambda.Seq;

import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

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

		BackIterator<Record> paymentIter = new BackIterator<>(paymentCursor.iterator());
		BackIterator<Record> dataIter = new BackIterator<>(dataCursor.iterator());
		BackIterator<Record> costIter = new BackIterator<>(costCursor.iterator());
		BackIterator<Record> deductionIter = new BackIterator<>(deductionCursor.iterator());
		BackIterator<Record> bonusIter = new BackIterator<>(bonusCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
				Salary salary = supplier.get()
				.setId(rootRecord.get(SALARY.ID))		
				.setStartDate(rootRecord.get(SALARY.START_DATE))
				.setEndDate(rootRecord.get(SALARY.END_DATE))
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

				return salary;
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
		.groupBy(SALARY.EMPLOYEE_DOCUMENT)
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
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
		SALARY.EMPLOYEE_DOCUMENT)
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
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
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
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetchLazy();
		//@formatter:on

		BackIterator<Record> salaryDataIter = new BackIterator<>(salaryDataCursor.iterator());
		BackIterator<Record> contractDataIter = new BackIterator<>(contractDataCursor.iterator());
		BackIterator<Record> salaryImlicitDataIter = new BackIterator<>(salaryImplicitDataCursor.iterator());

		//@formatter:off
		return Seq.seq(rootCursor)
				.map(rootRecord-> {
					
					
					String employeeDocument = rootRecord.get(SALARY.EMPLOYEE_DOCUMENT);	

					Salary salary = supplier.get()
					.setEmployeeDocument(employeeDocument)
					.setEmployeeName(rootRecord.get(SALARY.EMPLOYEE_NAME))
					.setEmployeeSSNumber(rootRecord.get(SALARY.SOCIAL_SECURITY_NUMBER))
					.setEnterpriseDocument(rootRecord.get(SALARY.ENTERPRISE_DOCUMENT))
					.setEnterpriseName(rootRecord.get(SALARY.ENTERPRISE_NAME))
					.setEnterpriseCCC(rootRecord.get(SALARY.CCC))
					.setStartDate(rootRecord.get(SALARY.START_DATE))
					.setEndDate(rootRecord.get(SALARY.END_DATE))
					.setTotalPayment(rootRecord.get(SALARY.TOTAL_PAYMENT))
					;
					
					
					Seq.limitWhile(
					Seq.skipUntil(Seq.seq(salaryDataIter), 
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
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
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
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
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) ),
					r -> r.get(SALARY.EMPLOYEE_DOCUMENT).equals(employeeDocument) )
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
