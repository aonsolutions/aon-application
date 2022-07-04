package com.esferalia.aon.payroll.calculator.jooq;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ALL;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CATEGORY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_ENTERPRISE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;

import java.sql.Connection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SalaryBonusRecord;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDeductionRecord;
import com.esferalia.aon.jooq.tables.records.SalaryEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.Variables;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class DSLSalaryBuilder<T extends ISalary> implements ISalaryBuilder<T> {

	private static List<String> ALREADY_AT_SALARY = null;
	private LinkedList<SalaryRecord> insertMoreSalary;
	private LinkedList<SalaryCostRecord> insertMoreCost;
	private LinkedList<SalaryDataRecord> insertMoreData;
	private LinkedList<SalaryBonusRecord> insertMoreBonus;
	private LinkedList<SalaryPaymentRecord> insertMorePayment;
	private LinkedList<SalaryDeductionRecord> insertMoreDeduction;
	private LinkedList<SalaryEmbargoRecord> insertMoreEmbargo;

	private Variables variables;

	private SalaryPaymentRecord prevPayment;
	private SalaryDeductionRecord prevDeduction;


	public DSLSalaryBuilder() {
		this.variables = new Variables(null);
		this.prevPayment = new SalaryPaymentRecord();
		this.prevDeduction = new SalaryDeductionRecord();

		this.insertMoreSalary = new LinkedList<>();
		this.insertMoreCost = new LinkedList<>();
		this.insertMoreData = new LinkedList<>();
		this.insertMoreBonus = new LinkedList<>();
		this.insertMorePayment = new LinkedList<>();
		this.insertMoreDeduction = new LinkedList<>();
		this.insertMoreEmbargo = new LinkedList<>();
	}
	
	public SalaryRecord getSalaryRecord(Predicate<SalaryRecord> filter) {
		for (SalaryRecord salaryRecord : insertMoreSalary) {
			if (filter.test(salaryRecord))
				return salaryRecord;
		}
		return null;
	}
	

	@Override
	public T getSalary() {
		return null;
	}

	@Override
	public void createNewSalary() {
		
		insertVariables();
		variables.clear();

		this.prevPayment = new SalaryPaymentRecord();
		this.prevDeduction = new SalaryDeductionRecord();

		insertMoreSalary.push(new SalaryRecord());
		
		insertMoreSalary.peek().set(SALARY.ID, getSalaryId());
	}

	@Override
	public void setContract(Object contract) {
		SQLSalaryProxy salaryProxy = (SQLSalaryProxy) contract;

		insertMoreSalary.peek().setDomain(salaryProxy.getDomainId());
		insertMoreSalary.peek().setContract(salaryProxy.getContractId());
	}

	@Override
	public void setCcc(String ccc) {
		insertMoreSalary.peek().set(SALARY.CCC, ccc);
	}
	
	@Override
	public void setRegime(String regime) {
		//TODO: insertMoreSalary.peek().set(SALARY.SS_REGIME, SSRegimeType);	
	}
	
	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setEnterpriseName(String enterpriseName) {
		insertMoreSalary.peek().set(SALARY.ENTERPRISE_NAME, enterpriseName);
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		insertMoreSalary.peek().set(SALARY.ENTERPRISE_ADDRESS, enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		insertMoreSalary.peek().set(SALARY.ENTERPRISE_DOCUMENT, enterpriseDocument);

	}

	@Override
	public void setRegistration(Integer registration) {
		insertMoreSalary.peek().set(SALARY.REGISTRATION, registration);
	}
	
	@Override
	public void setEmployeeCity(String employeeCity) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setEmployeeAddress(String employeeAddress) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setEmployeeName(String employeeName) {
		insertMoreSalary.peek().set(SALARY.EMPLOYEE_NAME, employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		insertMoreSalary.peek().set(SALARY.EMPLOYEE_DOCUMENT, employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		insertMoreSalary.peek().set(SALARY.SOCIAL_SECURITY_NUMBER, socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		insertMoreSalary.peek().set(SALARY.CATEGORY, category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		insertMoreSalary.peek().set(SALARY.QUOTE_GROUP, quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		insertMoreSalary.peek().set(SALARY.SENIORITY_DATE, toSqlDate(seniorityDate));
	}

	@Override
	public void setType(SalaryType type) {
		insertMoreSalary.peek().set(SALARY.TYPE, (byte) type.ordinal());
	}

	@Override
	public void setIssueDate(Date issueDate) {
		insertMoreSalary.peek().set(SALARY.ISSUE_DATE, toSqlDate(issueDate));
	}

	@Override
	public void setChargeDate(Date issueDate) {
		insertMoreSalary.peek().set(SALARY.CHARGE_DATE, toSqlDate(issueDate));

	}

	@Override
	public void setStartDate(Date startDate) {
		insertMoreSalary.peek().set(SALARY.START_DATE, toSqlDate(startDate));

	}

	@Override
	public void setEndDate(Date endDate) {
		insertMoreSalary.peek().set(SALARY.END_DATE, toSqlDate(endDate));
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		insertMoreSalary.peek().set(SALARY.TIME_UNITS, timeUnits);

	}

	@Override
	public void setItBase(Double itBase) {
		insertMoreSalary.peek().set(SALARY.IT_BASE, itBase != null ? itBase : 0.00);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		insertMoreSalary.peek().set(SALARY.RAW_CGC_BASE, rawCgcBase != null ? rawCgcBase : 0.00);

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		insertMoreSalary.peek().set(SALARY.CGC_BASE, cgcBase != null ? cgcBase : 0.00);

	}

	@Override
	public void setCgpBase(Double cgpBase) {
		insertMoreSalary.peek().set(SALARY.CGP_BASE, cgpBase != null ? cgpBase : 0.00);

	}

	@Override
	public void setRemuneration(Double remuneration) {
		insertMoreSalary.peek().set(SALARY.REMUNERATION, remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		insertMoreSalary.peek().set(SALARY.PRO_EXT_BASE, proExtBase != null ? proExtBase : 0.00);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		insertMoreSalary.peek().set(SALARY.IRPF_BASE, irpfBase != null ? irpfBase : 0.00);
	}

	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		insertMoreSalary.peek().set(SALARY.MONEY_IRPF_BASE, moneyIrpfBase != null ? moneyIrpfBase : 0.00);
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		insertMoreSalary.peek().set(SALARY.INKIND_IRPF_BASE,
				inkindIrpfBase != null ? inkindIrpfBase : 0.00);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		insertMoreSalary.peek().set(SALARY.HEXTRA_BASE, hExtraBase != null ? hExtraBase : 0.00);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		insertMoreSalary.peek().set(SALARY.NON_HEXTRA_BASE, nonHExtraBase != null ? nonHExtraBase : 0.00);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		insertMoreSalary.peek().set(SALARY.TOTAL_LIQUID, totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		insertMoreSalary.peek().set(SALARY.TOTAL_PAYMENT, totalPayment);

	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		insertMoreSalary.peek().set(SALARY.TOTAL_DEDUCTION, totalDeduction);
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		insertMoreSalary.peek().set(SALARY.TOTAL_IRPF, totalIrpf);
	}

	@Override
	public void setTotalSS(Double socialSecurityContributions) {
		insertMoreSalary.peek().set(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS, socialSecurityContributions);
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		insertMoreSalary.peek().set(SALARY.TOTAL_ENTERPRISE, totalEnterprise);
	}

	@Override
	public void addData(String name, ITimedVariable<?> data) {
		if (!filter(name, data))
			return;
		addVariable(name, data);
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		
		SalaryEmbargoRecord salaryEmbargoRecord = new SalaryEmbargoRecord();
		salaryEmbargoRecord.set(SALARY_EMBARGO.DOMAIN, getDomainId());
		salaryEmbargoRecord.set(SALARY_EMBARGO.SALARY, getSalaryId());
		salaryEmbargoRecord.set(SALARY_EMBARGO.AMOUNT, amount);
		salaryEmbargoRecord.set(SALARY_EMBARGO.CONTRACT_EMBARGO, id);
		salaryEmbargoRecord.set(SALARY_EMBARGO.DESCRIPTION, description);

		insertMoreEmbargo.push(salaryEmbargoRecord);

		putContext(context);
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		addEmbargo(id, 0.00, embargo.getDescription(), embargo, context);
	}

	@Override
	public void addCost(Double amount, String description, Date start, Date end, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		
		DeductionType type = cost.getType();
		
		SalaryCostRecord salaryCostRecord = new SalaryCostRecord();

		salaryCostRecord.set(SALARY_COST.DOMAIN, getDomainId());
		salaryCostRecord.set(SALARY_COST.SALARY, getSalaryId());
		salaryCostRecord.set(SALARY_COST.AMOUNT, amount);
		salaryCostRecord.set(SALARY_COST.COST_CONCEPT, cost.getName());
		salaryCostRecord.set(SALARY_COST.TYPE, type != null ? (byte) type.ordinal() : null);
		// .set(SALARY_COST.DESCRIPTION, description) For what ?
		
		insertMoreCost.push(salaryCostRecord);

		putContext(context);

	}

	@Override
	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {

		SalaryBonusRecord salaryBonusRecord = new SalaryBonusRecord();
		
		salaryBonusRecord.set(SALARY_BONUS.DOMAIN, getDomainId());
		salaryBonusRecord.set(SALARY_BONUS.SALARY, getSalaryId());
		salaryBonusRecord.set(SALARY_BONUS.AMOUNT, amount);
		salaryBonusRecord.set(SALARY_BONUS.BONUS_CONCEPT, bonus.getName());
		salaryBonusRecord.set(SALARY_BONUS.DESCRIPTION, description);
		
		insertMoreBonus.push(salaryBonusRecord);

		putContext(context);
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate, Date endDate,
			IPayment payment, Map<String, ITimedVariable<?>> context) {

		if (isSiblingOfPrevious(payment)) {
			if (prevPayment.getIrpf() != null) {
				tax += prevPayment.getIrpf();
			}
			insertMorePayment.peek().set(SALARY_PAYMENT.IRPF, tax != null ? tax : 0.00);
			if (prevPayment.getQuote() != null) {
				quote += prevPayment.getQuote();
			}
			insertMorePayment.peek().set(SALARY_PAYMENT.QUOTE, quote != null ? quote : 0.00);
			if (prevPayment.getAmount() != null) {
				amount += prevPayment.getAmount();
			}
			insertMorePayment.peek().set(SALARY_PAYMENT.AMOUNT, amount != null ? amount : 0.00);
		} else {

			SalaryPaymentRecord salaryPaymentRecord = new SalaryPaymentRecord();
			
			PaymentType type = payment.getType();

			salaryPaymentRecord.set(SALARY_PAYMENT.DOMAIN, getDomainId());
			salaryPaymentRecord.set(SALARY_PAYMENT.SALARY, getSalaryId());
			salaryPaymentRecord.set(SALARY_PAYMENT.PAYMENT_CONCEPT, payment.getName());
			salaryPaymentRecord.set(SALARY_PAYMENT.DESCRIPTION, description);
			salaryPaymentRecord.set(SALARY_PAYMENT.TYPE, type != null ? (byte) type.ordinal() : null);

			salaryPaymentRecord.set(SALARY_PAYMENT.IRPF, tax != null ? tax : 0.00);
			salaryPaymentRecord.set(SALARY_PAYMENT.QUOTE, quote != null ? quote : 0.00);
			salaryPaymentRecord.set(SALARY_PAYMENT.AMOUNT, amount != null ? amount : 0.00);
			
			insertMorePayment.push(salaryPaymentRecord);
		}

		prevPayment.setIrpf(tax);
		prevPayment.setQuote(quote);
		prevPayment.setAmount(amount);
		
		if ( payment instanceof IContractPayment )
			prevPayment.setId(((IContractPayment) payment).getId());

		putContext(context);
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		addPayment(0.00, quote, tax, payment.getDescription(), null, null, payment, context);
	}

	@Override
	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {

		if (isSiblingOfPrevious(deduction)) {
			if (prevDeduction.getAmount() != null)
				amount += prevDeduction.getAmount();
			insertMoreDeduction.peek().set(SALARY_DEDUCTION.AMOUNT, amount != null ? amount : 0.00);
		} else {

			DeductionType type = deduction.getType();
			if (type != null && (type.isSsDeduction() || type.isTaxDeduction()))
				description = null;
			
			SalaryDeductionRecord salaryDeductionRecord = new SalaryDeductionRecord();

			// TODO: start & end dates ???
			salaryDeductionRecord.set(SALARY_DEDUCTION.DOMAIN, getDomainId());
			salaryDeductionRecord.set(SALARY_DEDUCTION.SALARY, getSalaryId());
			salaryDeductionRecord.set(SALARY_DEDUCTION.DESCRIPTION, description);
			salaryDeductionRecord.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, deduction.getName());
			salaryDeductionRecord.set(SALARY_DEDUCTION.TYPE, type != null ? (byte) type.ordinal() : null);
			salaryDeductionRecord.set(SALARY_DEDUCTION.AMOUNT, amount != null ? amount : 0.00);
			
			insertMoreDeduction.push(salaryDeductionRecord);
		}

		prevDeduction.setAmount(amount);
		
		if ( deduction instanceof IContractDeduction )
			prevDeduction.setId(((IContractDeduction) deduction).getId());

		putContext(context);
	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setExpressionContext(ExpressionContext context) {
		// TODO Auto-generated method stub
		
	}

	public int execute(DSLContext dslContext) {
		int salaries = 0;
		insertVariables();
		
		Integer maxSalaryId = dslContext.select(DSL.max(SALARY.ID)).from(SALARY).forUpdate().fetchOptional().map(Record1<Integer>::value1).orElse(0);
		syncSalaryId(maxSalaryId);
		syncDomainId();
		
		if (!insertMoreSalary.isEmpty())
			salaries = dslContext.execute(insertInto(SALARY,insertMoreSalary));
		if (!insertMorePayment.isEmpty())
			dslContext.execute(insertInto(SALARY_PAYMENT,insertMorePayment));
		if (!insertMoreDeduction.isEmpty())
			dslContext.execute(insertInto(SALARY_DEDUCTION, insertMoreDeduction));
		if (!insertMoreBonus.isEmpty())
			dslContext.execute(insertInto(SALARY_BONUS, insertMoreBonus));
		if (!insertMoreCost.isEmpty())
			dslContext.execute(insertInto(SALARY_COST, insertMoreCost));
		if (!insertMoreEmbargo.isEmpty())
			dslContext.execute(insertInto(SALARY_EMBARGO, insertMoreEmbargo));
		if (!insertMoreData.isEmpty())
			dslContext.execute(insertInto(SALARY_DATA, insertMoreData));

		return salaries;
	}
	
	public void execute ( Connection connection) {
		execute(DSL.using(connection, JooqCommon.getDefaultSettings()));
	}

	// ------------------------------------------------------------------------

	protected String getSQL() {
		String LN = "\r\n";
		StringBuffer sql = new StringBuffer();
		if (!insertMoreSalary.isEmpty())
			sql.append(insertInto(SALARY,insertMoreSalary).getSQL(ParamType.INLINED));
		if (!insertMorePayment.isEmpty())
			sql.append(LN + insertInto(SALARY_PAYMENT,insertMorePayment).getSQL(ParamType.INLINED));
		if (!insertMoreDeduction.isEmpty())
			sql.append(LN + insertInto(SALARY_DEDUCTION, insertMoreDeduction).getSQL(ParamType.INLINED));
		if (!insertMoreBonus.isEmpty())
			sql.append(LN + insertInto(SALARY_BONUS, insertMoreBonus).getSQL(ParamType.INLINED));
		if (!insertMoreCost.isEmpty())
			sql.append(LN + insertInto(SALARY_COST, insertMoreCost).getSQL(ParamType.INLINED));
		if (!insertMoreEmbargo.isEmpty())
			sql.append(LN + insertInto(SALARY_EMBARGO, insertMoreEmbargo).getSQL(ParamType.INLINED));
		if (!insertMoreData.isEmpty())
			sql.append(LN + insertInto(SALARY_DATA, insertMoreData).getSQL(ParamType.INLINED));

		return sql.toString();
	}

	// ------------------------------------------------------------------------
	
	private void syncSalaryId(int maxSalaryId) {
		insertMoreSalary.forEach(r -> r.set(SALARY.ID, maxSalaryId + r.get(SALARY.ID)));
		insertMorePayment.forEach(r -> r.set(SALARY_PAYMENT.SALARY, maxSalaryId + r.get(SALARY_PAYMENT.SALARY)));
		insertMoreDeduction.forEach(r -> r.set(SALARY_DEDUCTION.SALARY, maxSalaryId + r.get(SALARY_DEDUCTION.SALARY)));
		insertMoreBonus.forEach(r -> r.set(SALARY_BONUS.SALARY, maxSalaryId + r.get(SALARY_BONUS.SALARY)));
		insertMoreCost.forEach(r -> r.set(SALARY_COST.SALARY, maxSalaryId + r.get(SALARY_COST.SALARY)));
		insertMoreEmbargo.forEach(r -> r.set(SALARY_EMBARGO.SALARY, maxSalaryId + r.get(SALARY_EMBARGO.SALARY)));
		insertMoreData.forEach(r -> r.set(SALARY_DATA.SALARY, maxSalaryId + r.get(SALARY_DATA.SALARY)));
	}
	
	private void syncDomainId() {
		insertMoreSalary.forEach(s -> {
			insertMorePayment.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
			insertMoreDeduction.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
			insertMoreBonus.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
			insertMoreCost.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
			insertMoreEmbargo.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
			insertMoreData.stream().filter(p -> AonNumberUtils.equals(p.getSalary(),s.getId())).forEach( p -> p.setDomain(s.getDomain()));
		});
	}
	
	private int getSalaryId() {
		return insertMoreSalary.size();
	}
	
	private int getDomainId() {
		return insertMoreSalary.peek().getDomain();
	}

	private <R extends Record> InsertSetMoreStep<R> insertInto(Table<R> table, LinkedList<R> records) {

		InsertSetMoreStep<R> insertSetMoreStep = 
		DSL.insertInto(table).set(records.get(0));

		for (int i = 1; i < records.size(); i++) {
			insertSetMoreStep.newRecord().set(records.get(i));
		}
		
		return insertSetMoreStep;
		
		
	}

	private void putContext(Map<String, ITimedVariable<?>> ctx) {
		for (Map.Entry<String, ITimedVariable<?>> entry : ctx.entrySet())
			if (filter(entry)) {
				ITimedVariable<?> var = entry.getValue();
				addVariable(entry.getKey(), entry.getValue());
				if (var instanceof ITimedResult<?>)
					putContext(((ITimedResult<?>) var).getContext());
				if (var instanceof IExpressionVariable<?>)
					putContext(((IExpressionVariable<?>) var).getContext());
			}
	}

	private void addVariable(String name, ITimedVariable<?> variable) {
		variables.put(name, variable);
	}
	
	private void insertVariables() {
		for (String name : variables.varsSet() ) {
			for ( ITimedVariable<?> variable : variables.getVariables(name)){
				try {
					Object value = variable.getValue(variable.getPeriod());
					String expression = String.valueOf(value);
					
					SalaryDataRecord salaryDataRecord = new SalaryDataRecord();
		
					salaryDataRecord.set(SALARY_DATA.DOMAIN, getDomainId());
					salaryDataRecord.set(SALARY_DATA.SALARY, getSalaryId());
					salaryDataRecord.set(SALARY_DATA.NAME, name);
					salaryDataRecord.set(SALARY_DATA.EXPRESSION, expression);
					salaryDataRecord.set(SALARY_DATA.START_DATE, toSqlDate(variable.getPeriod().getStart()));
					salaryDataRecord.set(SALARY_DATA.END_DATE, toSqlDate(variable.getPeriod().getEnd()));
					
					insertMoreData.push(salaryDataRecord);
					
				} catch ( Throwable t ){
					//TODO: 
				}
			}

		}
	}
	
	/**
	 * 
	 * @param payment
	 * @return true if this Builder did not already insert the specified
	 *         payment.
	 */
	private boolean isSiblingOfPrevious(IPayment payment) {
		
		return (prevPayment.getId() != null) 
				&& (payment instanceof IContractPayment )
				&& (((IContractPayment) payment).getId() != null)
				&& ((IContractPayment) payment).getId().equals(prevPayment.getId());
	}

	private boolean isSiblingOfPrevious(IDeduction deduction) {
		return (prevDeduction.getId() != null) 
				&& (deduction instanceof IContractDeduction )
				&& (((IContractDeduction) deduction).getId() != null)
				&& ((IContractDeduction) deduction).getId().equals(prevDeduction.getId());
	}

	// ------------------------------------------------------------------------

	private boolean filter(String name, ITimedVariable<?> var) {
		Period period = var.getPeriod();
		Object value = var.getValue(period);
		if (value == null)
			return false;
		// end-if: Not insert NULL data

		boolean knowType = value instanceof Number || value instanceof String || value instanceof Boolean
				|| value instanceof Enum<?>;
		if (!knowType)
			return false;

		// end-if: Not insert complex object, functions...

		if (isAlreadyAtSalary(name))
			return false;

		return true;
	}

	protected boolean filter(Map.Entry<String, ITimedVariable<?>> entry) {
		return filter(entry.getKey(), entry.getValue());
	}
	
	
	// ------------------------------------------------------------------------

	protected static java.sql.Date toSqlDate(Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	private static boolean isAlreadyAtSalary(String name) {
		if (ALREADY_AT_SALARY == null) {
			ALREADY_AT_SALARY = new LinkedList<String>();
			ALREADY_AT_SALARY.add(ALL);
			ALREADY_AT_SALARY.add(CATEGORY.getName());
			ALREADY_AT_SALARY.add(CGC_ENTERPRISE.getName());
			ALREADY_AT_SALARY.add(IRPF_BASE.getName());
			ALREADY_AT_SALARY.add(TOTAL_LIQUID.getName());
			ALREADY_AT_SALARY.add(TOTAL_PAYMENT.getName());

			Collections.sort(ALREADY_AT_SALARY);
		}
		return Collections.binarySearch(ALREADY_AT_SALARY, name) >= 0;

	}

	// ------------------------------------------------------------------------

}
