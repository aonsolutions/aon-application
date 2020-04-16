package com.esferalia.aon.in.payroll.salary.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Table;

import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class JooqSalary2Contract {
	
	private static final String DATAS [] = new String [] {
			ContextVariable.TC2.getName(),
			ContextVariable.QUOTE_GROUP.getName(),
			ContextVariable.IRPF_PERCENT.getName(),
	};
	
	public static void pullUp(DSLContext dslContext, Condition where) {
		pullUpData(dslContext, where);
		//pullUpPayments(dslContext, where);
	}
	
	private static void pullUpPayments(DSLContext dslContext, Condition where){
		try (Cursor<Record> cursor = 
		dslContext
		.select()
		.from(SALARY_PAYMENT)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.where(where)
		.and(CONTRACT.AGREEMENT_LEVEL.isNull())
		.orderBy(SALARY.CONTRACT,SALARY.START_DATE)
		.fetchLazy();
		) {
			if ( !cursor.hasNext() )
				return ;
			//dslContext.execute("set global max_allowed_packet=100000000 ;");
			                                                  
			
			Map<String, List<PaymentConceptRecord>> conceptsMap = getConceptsMap(dslContext);
			
			Record record = cursor.fetchNext();
			SalaryRecord salary = record.into(SALARY);
			ContractRecord contract = record.into(CONTRACT);
			Map<SalaryRecord, List<SalaryPaymentRecord>> salaryPaymentsMap = 
			new HashMap<SalaryRecord, List<SalaryPaymentRecord>>();
			salaryPaymentsMap.put(salary, new ArrayList<SalaryPaymentRecord>());
			salaryPaymentsMap.get(salary).add(record.into(SALARY_PAYMENT));

			int deleted = 0;
			int inserted = 0;
			InsertSetMoreStep<ContractPaymentRecord> insert = null;
					
			while ( cursor.hasNext() ) {
				record = cursor.fetchNext();
				SalaryRecord nextSalary = record.into(SALARY);
				ContractRecord nextContract = record.into(CONTRACT);
				
				if ( !salary.getContract().equals(nextSalary.getContract())) {
					
					List<ContractPaymentRecord> dbContractPayments = 
							getContractPayments(dslContext, salary.getContract());
					List<ContractPaymentRecord> newContractPayments = 
							sub(dbContractPayments, salaryPaymentsMap.keySet());
					for (Map.Entry<SalaryRecord, List<SalaryPaymentRecord>> entry : salaryPaymentsMap.entrySet())
						newContractPayments = add(newContractPayments, entry.getValue(), entry.getKey(), conceptsMap);
					Collections.sort(newContractPayments, (p1,p2)->p1.getStartDate().compareTo(p2.getStartDate()));
					expandPayments(newContractPayments, contract.getEndDate());
					
					insert = insert(dslContext, insert, newContractPayments, CONTRACT_PAYMENT);
					deleted += deleteContractPayment(dslContext, salary.getContract());
					
				System.out.printf("%s, %s %4$td/%4$tm/%4$ty..%5$td/%5$tm/%5$ty\r\n", salary.getEnterpriseName(), salary.getEmployeeName(), contract.getId(), contract.getStartDate(), contract.getEndDate());
//					if ( dbContractPayments.size() != newContractPayments.size() ) {
//						for (ContractPaymentRecord contractPaymentRecord : newContractPayments) {
//							System.out.printf("\t[%s]%s = %s %4$td/%4$tm/%4$ty..%5$td/%5$tm/%5$ty (%6$s,%7$s)\r\n", 
//							contractPaymentRecord.getType() == null ? "-" :PaymentType.values()[contractPaymentRecord.getType()].name(), 
//							contractPaymentRecord.getDescription(), 
//							contractPaymentRecord.getExpression(), 
//							contractPaymentRecord.getStartDate(), 
//							contractPaymentRecord.getEndDate(),
//							contractPaymentRecord.getIrpfExpression(),
//							contractPaymentRecord.getQuoteExpression()
//							);
//						}
//						System.out.println("-----------------------------------------------------------------------");
//						for (ContractPaymentRecord contractPaymentRecord : dbContractPayments) {
//							System.out.printf("\t[%s]%s = %s %4$td/%4$tm/%4$ty..%5$td/%5$tm/%5$ty (%6$s,%7$s)\r\n", 
//							contractPaymentRecord.getType() == null ? "-" :PaymentType.values()[contractPaymentRecord.getType()].name(), 
//							contractPaymentRecord.getDescription(), 
//							contractPaymentRecord.getExpression(), 
//							contractPaymentRecord.getStartDate(), 
//							contractPaymentRecord.getEndDate(),
//							contractPaymentRecord.getIrpfExpression(),
//							contractPaymentRecord.getQuoteExpression()
//							);
//						}
//					}
					
					
					
					salaryPaymentsMap.clear();
				}
				
				salary = nextSalary;
				contract = nextContract;
				salaryPaymentsMap.putIfAbsent(salary, new ArrayList<SalaryPaymentRecord>());
				salaryPaymentsMap.get(salary).add(record.into(SALARY_PAYMENT));
			}

			List<ContractPaymentRecord> dbContractPayments = 
					getContractPayments(dslContext, salary.getContract());
			List<ContractPaymentRecord> newContractPayments = 
					sub(dbContractPayments, salaryPaymentsMap.keySet());
			for (Map.Entry<SalaryRecord, List<SalaryPaymentRecord>> entry : salaryPaymentsMap.entrySet())
				newContractPayments = add(newContractPayments, entry.getValue(), entry.getKey(), conceptsMap);
			Collections.sort(newContractPayments, (p1,p2)->p1.getStartDate().compareTo(p2.getStartDate()));
			expandPayments(newContractPayments, contract.getEndDate());
			insert = insert(dslContext, insert, newContractPayments, CONTRACT_PAYMENT);
			deleted += deleteContractPayment(dslContext, salary.getContract());

			inserted += insert.execute();
			System.out.printf("%d payment deleted\r\n", deleted);
			System.out.printf("%d payment inserted\r\n", inserted);
			
		};
	}
	
	
	private static Map<String, List<PaymentConceptRecord>> getConceptsMap(DSLContext dslContext) {
		return
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.fetchGroups(PAYMENT_CONCEPT.CODE, r -> r.into(PAYMENT_CONCEPT))
		;
	}
	
	private static void pullUpData(DSLContext dslContext, Condition where){
		try (Cursor<Record> cursor = 
		dslContext
		.select()
		.from(SALARY_DATA)
		.innerJoin(SALARY).onKey()
		.innerJoin(CONTRACT).onKey()
		.where(where)
		.and(SALARY_DATA.NAME.in(DATAS))
		.orderBy(SALARY.CONTRACT, SALARY_DATA.NAME,SALARY_DATA.START_DATE)
		.fetchLazy();
		) {
			if ( !cursor.hasNext() )
				return ;
			
			Record record = cursor.fetchNext();
			ContractRecord contract = record.into(CONTRACT);
			SalaryDataRecord salaryData = record.into(SALARY_DATA);
			Stack<SalaryDataRecord> salaryDatas = new Stack<SalaryDataRecord>();
			salaryDatas.push(salaryData);
			
			int deleted = 0;
			InsertSetMoreStep<ContractDataRecord> insert = null;
			
			while ( cursor.hasNext() ) {
				record = cursor.fetchNext();
				ContractRecord nextContract = record.into(CONTRACT);
				SalaryDataRecord nextSalaryData = record.into(SALARY_DATA);
				
				if ( !nextContract.getId().equals(contract.getId())
					|| !AonStringUtils.equals(nextSalaryData.getName(), salaryData.getName())  ) {
					
					List<ContractDataRecord> dbContractDatas = 
					getContractDatas(dslContext, contract.getId(), salaryData.getName());
					List<ContractDataRecord> newContractDatas = pullUp(dbContractDatas, salaryDatas, contract);										
					insert = insert(dslContext, insert, newContractDatas, CONTRACT_DATA);
					deleted += deleteContractData(dslContext, contract.getId(), salaryData.getName());
					
					salaryDatas.clear();
				}
				
				contract = nextContract;
				salaryData = nextSalaryData;
				salaryDatas.push(nextSalaryData);
			}

			List<ContractDataRecord> dbContractDatas = 
			getContractDatas(dslContext, contract.getId(), salaryData.getName());
			List<ContractDataRecord> newContractDatas = pullUp(dbContractDatas, salaryDatas, contract);
			insert = insert(dslContext, insert, newContractDatas, CONTRACT_DATA);
			deleted += deleteContractData(dslContext, contract.getId(), salaryData.getName());
			
			int inserted = insert.execute();
			System.out.printf("%d data deleted\r\n", deleted);
			System.out.printf("%d data inserted\r\n", inserted);
			
		}
	}
	
	private static List<ContractDataRecord> pullUp (List<ContractDataRecord> dbContractDatas, List<SalaryDataRecord> salaryDatas, ContractRecord contract) {
		List<ContractDataRecord> newContractDatas = 
		merge(dbContractDatas, salaryDatas);
		newContractDatas = join(newContractDatas);
		newContractDatas = expandDatas(newContractDatas, contract.getEndDate());
		newContractDatas = fill(newContractDatas, contract);
		return newContractDatas;
	}
	
	private static int deleteContractPayment(DSLContext dslContext, int contract) {
		return dslContext
		.delete(CONTRACT_PAYMENT)
		.where(CONTRACT_PAYMENT.CONTRACT.eq(contract))
		.execute();
	}

	private static int deleteContractData(DSLContext dslContext, int contract, String name) {
		return dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contract))
		.and(CONTRACT_DATA.NAME.eq(name))
		.execute();
	}
	
	private static <R extends Record> InsertSetMoreStep<R> insert(DSLContext dslContext,InsertSetMoreStep<R> insert, List<R> datas, Table<R> table) {
		return Optional.ofNullable(insert).map(i -> insert(i, datas)).orElseGet(() -> insert(dslContext.insertInto(table), datas));
	}

	private static <R extends Record> InsertSetMoreStep<R> insert( InsertSetStep<R> insert, List<R> datas) {
		InsertSetMoreStep<R> insertMore = insert.set(datas.get(0));
		for (int i = 1; i < datas.size(); i++ )
			insertMore = insertMore.newRecord().set(datas.get(i));
		return insertMore;
	}

	private static <R extends Record> InsertSetMoreStep<R> insert( InsertSetMoreStep<R> insert, List<R> datas) {
		for (R data : datas) {
			insert = insert.newRecord().set(data);
		}
		return insert;
		
	}


	private static List<ContractDataRecord> getContractDatas(DSLContext dslContext, int contract, String name) {
		return dslContext
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contract))
		.and(CONTRACT_DATA.NAME.eq(name))
		.fetchInto(CONTRACT_DATA);
	}
		
	private static List<ContractPaymentRecord> getContractPayments(DSLContext dslContext, int contract) {
		return dslContext
		.select()
		.from(CONTRACT_PAYMENT)
		.where(CONTRACT_PAYMENT.CONTRACT.eq(contract))
		.fetchInto(CONTRACT_PAYMENT);
	}
	
	
	
	private static List<ContractDataRecord> join(List<ContractDataRecord> list) {
		Stack<ContractDataRecord> joined = new Stack<ContractDataRecord>(); 
		
		joined.push(list.get(0));
		for ( int i = 1; i < list.size(); i++ ) {
			ContractDataRecord prev = list.get(i-1);
			ContractDataRecord next = list.get(i);
			
			if ( AonStringUtils.equals(
					prev.getExpression(),
					next.getExpression()) )
				joined.peek().setEndDate(next.getEndDate());
			else 
				joined.push(next);
		}
		
		return joined;
	}

	private static List<ContractDataRecord> fill(List<ContractDataRecord> list, ContractRecord contract) {
		list.forEach(d -> d.setContract(contract.getId()));
		list.forEach(d -> d.setDomain(contract.getDomain()));
		return list;
	}

	private static List<ContractDataRecord> expandDatas(List<ContractDataRecord> list, Date endDate) {
		ContractDataRecord last = list.get(list.size()-1);
		if ( Period.compare(last.getEndDate(), endDate ) < 0 )
			last.setEndDate(endDate);
		return list;
	}

	private static List<ContractPaymentRecord> expandPayments(List<ContractPaymentRecord> list, Date endDate) {
		
		
		
		ContractPaymentRecord last = list.get(list.size()-1);
		list.stream()
		.filter( p -> p.getStartDate().equals(last.getStartDate()))
		.forEach(p -> {
			if ( Period.compare(p.getEndDate(), endDate ) < 0 )
				p.setEndDate(endDate);
		});
		;
		
		return list;
	}

	private static List<ContractDataRecord> merge(List<ContractDataRecord> contractDatas, List<SalaryDataRecord> salaryDatas) {
		
		List<Period> salaryPeriods =
		salaryDatas.stream().map(d -> new Period(d.getStartDate(), d.getEndDate())).sorted().collect(Collectors.toList());
		
		List<ContractDataRecord> merged = new ArrayList<ContractDataRecord>();
		
		for (ContractDataRecord contractData : contractDatas) {
			try {
				Period contractPeriod = new Period(contractData.getStartDate(), contractData.getEndDate());
				List<Period> contractPeriods = Period.sub(contractPeriod,  salaryPeriods);
				contractPeriods.forEach(p -> merged.add(copy(contractData, p)));
				merged.size();
			} catch ( IllegalArgumentException e ) {
				//TODO: 
			}
		}
		salaryDatas.forEach((d) -> merged.add(map(d)));
		
		Collections.sort(merged, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()));		

		return merged;
	}
	
	private static List<ContractPaymentRecord> sub(List<ContractPaymentRecord> contractPayments, Collection<SalaryRecord> salaries) {
		
		List<Period> salaryPeriods =
		salaries.stream().map(s -> new Period(s.getStartDate(), s.getEndDate())).sorted().collect(Collectors.toList());
		
		List<ContractPaymentRecord> sub = new ArrayList<ContractPaymentRecord>();
		
		for (ContractPaymentRecord contractPayment : contractPayments) {
			try {
				Period contractPeriod = new Period(contractPayment.getStartDate(), contractPayment.getEndDate());
				List<Period> contractPeriods = Period.sub(contractPeriod,  salaryPeriods);
				contractPeriods.forEach(p -> sub.add(copy(contractPayment, p)));
				sub.size();
			} catch ( IllegalArgumentException e ) {
				//TODO: 
			}
		}
		
		return sub;
	}

	private static List<ContractPaymentRecord> add(List<ContractPaymentRecord> contractPayments, List<SalaryPaymentRecord> salariesPayments, SalaryRecord salary, Map<String, List<PaymentConceptRecord>> conceptsMap) {
		List<ContractPaymentRecord> all = new ArrayList<ContractPaymentRecord>(contractPayments);
		salariesPayments.forEach(p -> all.add(map(p, salary, conceptsMap)));
		return all;
		
	}

	private static ContractDataRecord copy(ContractDataRecord contractData, Period period) {
		ContractDataRecord copy = contractData.copy();
		copy.setStartDate(toSQL(period.getStart()));
		copy.setEndDate(toSQL(period.getEnd()));		
		return copy;
	}
	

	private static ContractPaymentRecord copy(ContractPaymentRecord contractPayment, Period period) {
		ContractPaymentRecord copy = contractPayment.copy();
		copy.setStartDate(toSQL(period.getStart()));
		copy.setEndDate(toSQL(period.getEnd()));		
		return copy;
	}

	private static ContractDataRecord map(SalaryDataRecord salaryData) {
		ContractDataRecord contractData = new ContractDataRecord();
		contractData.setDomain(salaryData.getDomain());
		contractData.setName(salaryData.getName());
		contractData.setStartDate(salaryData.getStartDate());
		contractData.setEndDate(salaryData.getEndDate());
		contractData.setExpression(toMVEL(salaryData));
		return contractData;
	}
	
	private static ContractPaymentRecord map(SalaryPaymentRecord salaryPayment, SalaryRecord salary, Map<String, List<PaymentConceptRecord>> conceptsMap) {
		
		ContractPaymentRecord contractPayment = new ContractPaymentRecord();
		contractPayment.setDomain(salary.getDomain());
		contractPayment.setContract(salary.getContract());
		contractPayment.setStartDate(salary.getStartDate());
		contractPayment.setEndDate(salary.getEndDate());

		contractPayment.setType(salaryPayment.getType());
		contractPayment.setDescription(salaryPayment.getDescription());		
		Double amount = salaryPayment.getAmount();
		Double irpf = salaryPayment.getIrpf();
		Double quote = salaryPayment.getQuote();
		contractPayment.setExpression(formatReadOnlyExpr(amount));
		contractPayment.setIrpfExpression(AonUtils.equals(amount, irpf) ? "_P" : formatReadOnlyExpr(irpf));
		contractPayment.setQuoteExpression(AonUtils.equals(amount, quote) ? "_P" : formatReadOnlyExpr(quote));

		contractPayment.setMonth(null);
		contractPayment.setDescriptionDecorable((byte)0);
		
		
		contractPayment.setSalaryType(bite(SalaryType.SALARY));
		
		List<PaymentConceptRecord> concepts = conceptsMap.getOrDefault(salaryPayment.getPaymentConcept(), Collections.emptyList());
		for (PaymentConceptRecord concept : concepts) {
			contractPayment.setPaymentConcept(concept.getId());
			break;
		}

		//contractPayment.setPaymentConcept(salaryPayment.getPaymentConcept());
		return contractPayment;
	}

	private static String toMVEL(SalaryDataRecord salaryData) {
		ContextVariable contextVariable = 
		ContextVariable.getVariableByName(salaryData.getName());
		String expr  = salaryData.getExpression();
		switch (contextVariable) {
		case TC2:
		case QUOTE_GROUP:
			return String.format("\"%s\"", expr);
		case IRPF_PERCENT:
		default:
			return expr;
		
		}
	}
	
	private static String formatReadOnlyExpr( Double d ) {
		if ( d == null || d.isNaN() || d.isInfinite()) 
			d = 0.00;
		
		return String.format(Locale.ROOT, "/*read-only*/%.2f/**/", d);
	}
	
	private static <E extends Enum<?>> byte bite(E e ) {
		return (byte) e.ordinal();
	}
	
	private static Date toSQL(java.util.Date date) {
		return date == null ? null : new Date(date.getTime());
	}


//	if ( dbContractDatas.size() != newContractDatas.size())
//		System.out.printf(
//		"%d - %s : %s [%s](  %5$td/%5$tm/%5$ty .. "
//		+ (contract.getEndDate() == null ? "" : "%6$td/%6$tm/%6$ty")
//		+ ") \r\n", 
//		contract.getId(), 
//		salaryData.getName(), 
//		dbContractDatas
//		.stream()
//		.map(d -> String.format(
//		"%s %2$td/%2$tm/%2$ty .." 
//		+ (d.getEndDate() == null ? "" : "%3$td/%3$tm/%3$ty"),
//		d.getExpression(), 
//		d.getStartDate() ,
//		d.getEndDate())
//		)
//		.collect(Collectors.joining(", "))
//		,
//		newContractDatas
//		.stream()
//		.map(d -> String.format(
//		"%s %2$td/%2$tm/%2$ty .." 
//		+ (d.getEndDate() == null ? "" : "%3$td/%3$tm/%3$ty"),
//		d.getExpression(), 
//		d.getStartDate() ,
//		d.getEndDate())
//		)
//		.collect(Collectors.joining(", "))
//		,
//		contract.getStartDate(),
//		contract.getEndDate()
//		);
//
//	
	
	
//	if ( contractDatas.size() > 1 
//	|| 
//	Period.compare(contractDatas.get(0).getStartDate(), contract.getStartDate()) != 0 ||
//	Period.compare(contractDatas.get(0).getEndDate(), contract.getEndDate()) != 0 
//	)
//	System.out.printf(
//	"%d - %s : %s (  %4$td/%4$tm/%4$ty .. "
//	+ (contract.getEndDate() == null ? "" : "%5$td/%5$tm/%5$ty")
//	+ ") \r\n", 
//	contract.getId(), 
//	salaryData.getName(), 
//	contractDatas
//	.stream()
//	.map(d -> String.format(
//	"%s %2$td/%2$tm/%2$ty .." 
//	+ (d.getEndDate() == null ? "" : "%3$td/%3$tm/%3$ty"),
//	d.getExpression(), 
//	d.getStartDate() ,
//	d.getEndDate())
//	)
//	.collect(Collectors.joining(", "))
//	,
//	contract.getStartDate(),
//	contract.getEndDate()
//	);
	
}
