package com.esferalia.aon.in.payroll.tgss.creta;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.SalaryCost;
import com.esferalia.aon.jooq.tables.SalaryData;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.jooq.tables.SalaryEmbargo;
import com.esferalia.aon.jooq.tables.SalaryPayment;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class Abstract2Aon {


	Condition where;
	DSLContext dslContext;
	Connection connection;
	
	public Abstract2Aon(Connection connection, Condition where ) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		this.where = where;
		this.connection = connection;
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
	}

	protected void fixAgreementLevel(Record r, int sdlAgreementLevelId) {
		dslContext
		.update(CONTRACT)
		.set(CONTRACT.AGREEMENT_LEVEL, sdlAgreementLevelId)
		.where(CONTRACT.ID.eq(r.get(CONTRACT.ID)))
		.execute()
		;
	}	
	
	
	protected static int compare(ContractDataRecord d1, ContractDataRecord d2) {
		return (d1.getStartDate().compareTo(d2.getStartDate()));
	}	

	protected static void join(List<ContractDataRecord> datas, ContextVariable var) {
		join(datas, var.getName());
	}

	private static void join(List<ContractDataRecord> datas, String name) {
		ContractDataRecord array [] =
		datas
		.stream()
		.filter(d -> AonStringUtils.equals(d.getName(), name ))		
		.sorted(Abstract2Aon::compare)
		.collect(Collectors.toList())
		.toArray(ContractDataRecord[]::new)
		;
		
		for (int i = 1; i < array.length; i++) {
			ContractDataRecord d1 = array[i-1];
			ContractDataRecord d2 = array[i];
			
			if ( notEquals(d1, d2) )
				continue;
	
			d2.setStartDate(d1.getStartDate());
			d2.store();
			
//			d1.setName("__JOIN_" + d1.getName());
//			d1.update();
			d1.delete();
			
		} 
		
		
		
	}

	private static boolean notEquals(ContractDataRecord d1, ContractDataRecord d2) {
		if ( AonStringUtils.equals(d1.getExpression(), d2.getExpression()) )
			return false;
		return true;
	}

	private static java.sql.Date toSQL(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	private static boolean intersects(ContractDataRecord d, Period p) {
		return p.intersects(new Period(d.getStartDate(), d.getEndDate()));
	}

	protected Optional<String> quietFix(String var, double tgssDouble, Period period, Record r, List<ContractDataRecord> datas) {
		if ( allMatch(var, tgssDouble, period, datas)) {
			return Optional.empty();
		}
				
		fixNoMatch(r.get(CONTRACT.DOMAIN), r.get(CONTRACT.ID), var, tgssDouble, period, datas);
		join(datas, var);
		
		return Optional.of(var);
	}

	protected Optional<String> fix(String var, double tgssDouble, Period period, Record r, List<ContractDataRecord> datas) {
		if ( allMatch(var, tgssDouble, period, datas)) {
			System.out.printf(",\"%.2f\"", tgssDouble);
			return Optional.empty();
		}
		
		System.out.printf(",\"%.2f(%.2f)\"",tgssDouble, findNoMatch(var, tgssDouble, period, datas).orElse(0.00));
		
		fixNoMatch(r.get(CONTRACT.DOMAIN), r.get(CONTRACT.ID), var, tgssDouble, period, datas);
		join(datas, var);
		
		return Optional.of(var);
	}

	protected Optional<ContextVariable> fix(ContextVariable var, String tgssStr, Period period, Record r, List<ContractDataRecord> datas) {
		if ( allMatch(var, tgssStr, period, datas)) {
			System.out.printf(",\"%s\"", AonStringUtils.defaultIfBlank(tgssStr, ""));
			return Optional.empty();
		}
		
		System.out.printf(",\"%s(%s)\"",tgssStr, findNoMatch(var, tgssStr, period, datas).orElse(""));
		
		fixNoMatch(r.get(CONTRACT.DOMAIN), r.get(CONTRACT.ID), var, tgssStr, period, datas);
		join(datas, var);
		
		return Optional.of(var);
	}

	private void fixNoMatch(int domain, int contract, String name, double n, Period p, List<ContractDataRecord> datas) {
		fixNoMatch(domain, contract, name, Double.toString(n), p, datas);
	}

	protected void fixNoMatch(int domain, int contract, ContextVariable var, double n, Period p, List<ContractDataRecord> datas) {
		fixNoMatch(domain, contract, var.getName(), Double.toString(n), p, datas);
	}

	private void fixNoMatch(int domain, int contract, ContextVariable var, String s, Period p, List<ContractDataRecord> datas) {
		fixNoMatch(domain, contract, var.getName(), String.format("\"%s\"", s), p, datas);
	}

	private void fixNoMatch(int domain, int contract, String name, String s, Period p, List<ContractDataRecord> datas) {
	
		datas
		.stream()
		.sorted(Abstract2Aon::compare)
		.filter(d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.filter(d -> !AonStringUtils.equals(d.getExpression(), s))
		.forEach(d -> {
			new Period(d.getStartDate(),d.getEndDate()).sub(p).forEach( sub -> {
				ContractDataRecord contractData = 
				dslContext.newRecord(CONTRACT_DATA);
				contractData.setDomain(domain);
				contractData.setContract(contract);
				contractData.setName(name);
				contractData.setExpression(d.getExpression());
				contractData.setStartDate(toSQL(sub.getStart()));
				contractData.setEndDate(toSQL(sub.getEnd()));
				contractData.store();
			});
			
//			d.setName("__FIX_" + d.getName());
//			d.update();
			d.delete();
		});
		
		ContractDataRecord newContractData = 
		dslContext.newRecord(CONTRACT_DATA);
		newContractData.setDomain(domain);
		newContractData.setContract(contract);
		newContractData.setName(name);
		newContractData.setExpression(s);
		newContractData.setStartDate(toSQL(p.getStart()));
		newContractData.setEndDate(toSQL(p.getEnd()));
		newContractData.store();
	
	
	}

	protected void expand(int contract, Date date, Set<ContextVariable> vars) {
		expand(contract, date, vars.stream().map(v->v.getName()).toArray(String[]::new));
	}

	private void expand(int contract, Date date, String ...names) {	
		dslContext
		.delete(CONTRACT_DATA)
//		.update(CONTRACT_DATA)
//		.set(CONTRACT_DATA.NAME, 
//		DSL.concat("__EXPAND_", CONTRACT_DATA.NAME))
		.where(CONTRACT_DATA.CONTRACT.eq(contract))
		.and(CONTRACT_DATA.NAME.in(names))
		.and(CONTRACT_DATA.START_DATE.gt(date))
		.execute();
	
		dslContext
		.update(CONTRACT_DATA)
		.set(CONTRACT_DATA.END_DATE, DSL.castNull(Date.class))
		.where(CONTRACT_DATA.CONTRACT.eq(contract))
		.and(CONTRACT_DATA.NAME.in(names))
		.and(CONTRACT_DATA.END_DATE.eq(date))
		.execute();
	}

	protected void findContract(String ccc, String naf, Date fromDate, Date toDate, Consumer<? super Record> action) {
		try {

			dslContext
			.select()
			.from(CONTRACT)
			.innerJoin(PERSON).onKey()
			.innerJoin(ENTERPRISE_CCC).onKey()
			.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
			.leftJoin(ENTERPRISE_ACTIVITY).on(CONTRACT.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
			.leftJoin(CNAE2009).onKey()
			.where(ENTERPRISE_CCC.CCC.equalIgnoreCase(ccc))
			.and(PERSON.SOCIAL_SECURITY_NUM.equalIgnoreCase(naf))
			.and(CONTRACT.START_DATE.le(toDate))
			.and(CONTRACT.END_DATE.isNull()
			.or(CONTRACT.END_DATE.ge(fromDate)))
			.and(where)
			.fetchOptional()
			.ifPresentOrElse(r -> { 
				try {
					action.accept(r);
				} catch ( Exception e  ) {
					System.out.println(e.getMessage());
				}
				
			}, () -> {
				
	//			System.out.printf(
	//					"\"%s\",\"-\",\"-\",\"%s\",\"%s\",\"%s\",\"%5$td/%5$tm/%5$tY\",\"%6$td/%6$tm/%6$tY\"\r\n",
	//					naf,
	//					tramo.getInformacionAfiliacion().getTipoContrato(),
	//					tramo.getInformacionAfiliacion().getGrupoCotizacion(),
	//					AonStringUtils.defaultIfBlank(tramo.getInformacionAfiliacion().getCoeficienteTiempoParcial(), ""),
	//					fromDate,
	//					toDate
	//					);			
			});
		} catch ( TooManyRowsException e ) {
						System.out.printf(
								"\"%s\",\"%s\",\"%3$td/%3$tm/%3$tY\",\"%4$td/%4$tm/%4$tY\",\"%5$s\",\r\n",
								ccc,
								naf,
								fromDate,
								toDate,
								e.getMessage()
								);			
			
		}
		}

	protected void calculateAndsave(Record r, Date startDate, Date endDate)
			throws ExpressionException, SQLException, SalaryException {
				JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
				new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(getFullSQLContractSalaryCalculatorContext(r, startDate, endDate));
				
				SelectConditionStep<Record1<Integer>> salary = 
				DSL.select(SALARY.ID).from(SALARY)
				.where(SALARY.CONTRACT.eq(r.get(CONTRACT.ID)))
				.and(SALARY.START_DATE.eq(startDate))
				.and(SALARY.END_DATE.eq(endDate))
				;
				
				dslContext.delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(salary)).execute();
				dslContext.delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(salary)).execute();
				dslContext.delete(SALARY_COST).where(SALARY_COST.SALARY.in(salary)).execute();
				dslContext.delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(salary)).execute();
				dslContext.delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(salary)).execute();
				dslContext.delete(SALARY)
				.where(SALARY.CONTRACT.eq(r.get(CONTRACT.ID)))
				.and(SALARY.START_DATE.eq(startDate))
				.and(SALARY.END_DATE.eq(endDate))
				.execute();
							
				jooqSalaryBuilder.execute();
	}

	protected Salary calculate(Record r, Date startDate, Date endDate)
			throws ExpressionException, SQLException, SalaryException {
				return new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getSQLContractSalaryCalculatorContext(r, startDate, endDate));
			}

	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext(Record r, Date startDate, Date endDate)
			throws ExpressionException, SQLException {
			
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(SQLConstants.CONTRACT + "." + SQLConstants.ContractColumns.ID, r.get(CONTRACT.ID));
				
				SQLContractSalaryCalculatorContext ctx = 
				new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria) {
					@Override
					public Collection<IContractCost> getContractCosts() throws AonException {
						return Collections.emptyList();
					}
					@Override
					public Collection<IContractBonus> getContractBonus() throws AonException {
						return Collections.emptyList();
					}
					
					@Override
					public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
						return Collections.emptyList();
					}
					
					@Override
					public Collection<IContractDeduction> getContractDeductions() throws AonException {
						return Collections.emptyList();
					}
				};
				
				ctx.next();
				return ctx;
			}
	
	protected ISQLContractSalaryCalculatorContext getFullSQLContractSalaryCalculatorContext(Record r, Date startDate, Date endDate)
			throws ExpressionException, SQLException {
			
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(SQLConstants.CONTRACT + "." + SQLConstants.ContractColumns.ID, r.get(CONTRACT.ID));
				
				SQLContractSalaryCalculatorContext ctx = 
				new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
				
				ctx.next();
				return ctx;
			}

	protected void fixBaseCgcMin(Record r, double baseCgcMin, Date startDate, Date endDate) {
		List<ContractDataRecord> datas = 
		dslContext
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
		.fetchStreamInto(CONTRACT_DATA)
		.filter(d -> ContextVariable.getVariableByName(d.getName()) == ContextVariable.CGC_BASE_MIN 
		|| ContextVariable.getVariableByName(d.getName()) == ContextVariable.CGP_BASE_MIN )
		.collect(Collectors.toList() )
		;
		
		Period period = new Period(startDate, endDate);
		quietFix(ContextVariable.CGC_BASE_MIN.getName(), baseCgcMin, period, r, datas)
		.ifPresent(var -> {});	
		
	}

	private static boolean allMatch(ContextVariable var, String str, Period p, List<ContractDataRecord> datas) {
		return allMatch(var.getName(), str, p, datas);
	}

	protected static boolean allMatch(ContextVariable var, Number number, Period p, List<ContractDataRecord> datas) {
		return allMatch(var.getName(), number, p, datas);
	}

	private static List<ContractDataRecord> filter(String name, Period p, List<ContractDataRecord> datas) {
		return 	
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.collect(Collectors.toList());
	}

	private static boolean allMatch(String name, String str, Period p, List<ContractDataRecord> datas) {
		List<ContractDataRecord> list = 
		filter(name, p, datas);
		
		if ( list.isEmpty() )
			return AonStringUtils.isBlank(str);
	
		return 
		list
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(d -> MVEL.evalToString(d.getExpression()))
		.allMatch( s -> AonStringUtils.equals(s, str))
		;
	}

	private static boolean allMatch(String name, Number number, Period p, List<ContractDataRecord> datas) {
		List<ContractDataRecord> list = 
				filter(name, p, datas);
		
		if ( list.isEmpty() )
			return false;
	
		return 
		list
		.stream()
		.map(Abstract2Aon::evalNumber)
		.allMatch( n -> n.doubleValue() == number.doubleValue())
		;
	}

	private static Number evalNumber(ContractDataRecord d) {
		try {
			return MVEL.eval(d.getExpression(), Number.class);
		} catch ( Exception e) {
			return Double.NaN;
		}
	}

	private static Optional<String> findNoMatch(ContextVariable var, String str, Period p, List<ContractDataRecord> datas) {
		return findNoMatch(var.getName(), str, p, datas);
	}

	protected static Optional<Number> findNoMatch(ContextVariable var, Number number, Period p, List<ContractDataRecord> datas) {
		return findNoMatch(var.getName(), number, p, datas);
	}

	private static Optional<String> findNoMatch(String name, String str, Period p, List<ContractDataRecord> datas) {
		return 
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(d -> MVEL.evalToString(d.getExpression()))
		.filter( s -> !AonStringUtils.equals(s, str))
		.findAny()
		;
	}

	private static Optional<Number> findNoMatch(String name, Number number, Period p, List<ContractDataRecord> datas) {
		return 
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(Abstract2Aon::evalNumber)
		.filter( n -> n.doubleValue() != number.doubleValue())
		.findAny()
		;
	}

	protected static boolean isLastDayOfMonth(Date date) {
		return getMax(date, DAY_OF_MONTH) == get(date, DAY_OF_MONTH);
	}

	protected static String lowerCase(final String str) {
		if (str == null) {
			return null;
		}
		return str.toLowerCase();
	}

	
}
