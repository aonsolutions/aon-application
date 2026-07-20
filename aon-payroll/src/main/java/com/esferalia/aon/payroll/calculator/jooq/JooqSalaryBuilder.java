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
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.UNPAID;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertReturningStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
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
import com.esferalia.aon.payroll.calculator.ISystemPayment;
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
import com.esferalia.aon.salary.expression.IWrapTimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.Variables;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqSalaryBuilder<T extends ISalary> implements ISalaryBuilder<T> {

	private static List<String> ALREADY_AT_SALARY = null;

	private DSLContext dslContext;

	private InsertSetStep<SalaryRecord> insertSalary;
	
	private InsertSetMoreStep<SalaryCostRecord> insertMoreCost;
	private InsertSetMoreStep<SalaryDataRecord> insertMoreData;
	private InsertSetMoreStep<SalaryBonusRecord> insertMoreBonus;
	private InsertSetMoreStep<SalaryPaymentRecord> insertMorePayment;
	private InsertSetMoreStep<SalaryDeductionRecord> insertMoreDeduction;
	private InsertSetMoreStep<SalaryEmbargoRecord> insertMoreEmbargo;

	private int salaryId = -1;
	private int domainId = -1;

	private Variables variables;

	private SalaryRecord salaryRecord; 
	private SalaryPaymentRecord prevPaymentRecord;
	private SalaryDeductionRecord prevDeductionRecord;

	public JooqSalaryBuilder(Connection connection) {
		this(DSL.using(connection, JooqCommon.getDefaultSettings()));
	}

	public JooqSalaryBuilder(DSLContext dslContext) {
		this.dslContext = dslContext;
		this.variables = new Variables(null);
		this.prevPaymentRecord = new SalaryPaymentRecord();
		this.prevDeductionRecord = new SalaryDeductionRecord();
	}

	protected int getNextSalaryId() {
		return (salaryId == -1 ? max(dslContext, SALARY.getIdentity()) : salaryId) + 1;
	}
	
	@Override
	public T getSalary() {
		return null;
	}

	@Override
	public void createNewSalary() {
		
		insertVariables();
		variables.clear();

		if ( insertSalary == null ) {
			insertSalary = dslContext.insertInto(SALARY);
		} else {
			insertSalary = insertSalary.set(salaryRecord).newRecord();
		}
		

		this.salaryRecord = new SalaryRecord();
		this.prevPaymentRecord = new SalaryPaymentRecord();
		this.prevDeductionRecord = new SalaryDeductionRecord();
		
		salaryId = getNextSalaryId();
		salaryRecord.set(SALARY.ID, salaryId);
	}

	@Override
	public void setContract(Object contract) {
		SQLSalaryProxy salaryProxy = (SQLSalaryProxy) contract;

		setDomain(salaryProxy.getDomainId());
		setContract(salaryProxy.getContractId());
	}

	@Override
	public void setCcc(String ccc) {
		salaryRecord.set(SALARY.CCC, ccc);
	}
	
	@Override
	public void setRegime(String regime) {
		//TODO: salaryRecord.set(SALARY.SS_REGIME, SSRegimeType);	
	}
	
	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryRecord.set(SALARY.ENTERPRISE_NAME, enterpriseName);
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryRecord.set(SALARY.ENTERPRISE_ADDRESS, enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryRecord.set(SALARY.ENTERPRISE_DOCUMENT, enterpriseDocument);

	}

	@Override
	public void setRegistration(Integer registration) {
		salaryRecord.set(SALARY.REGISTRATION, registration);
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
		salaryRecord.set(SALARY.EMPLOYEE_NAME, employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salaryRecord.set(SALARY.EMPLOYEE_DOCUMENT, employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryRecord.set(SALARY.SOCIAL_SECURITY_NUMBER, socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		salaryRecord.set(SALARY.CATEGORY, category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		salaryRecord.set(SALARY.QUOTE_GROUP, quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salaryRecord.set(SALARY.SENIORITY_DATE, toSqlDate(seniorityDate));
	}

	@Override
	public void setType(SalaryType type) {
		salaryRecord.set(SALARY.TYPE, (byte) type.ordinal());
	}

	@Override
	public void setIssueDate(Date issueDate) {
		salaryRecord.set(SALARY.ISSUE_DATE, toSqlDate(issueDate));
	}

	@Override
	public void setChargeDate(Date issueDate) {
		salaryRecord.set(SALARY.CHARGE_DATE, toSqlDate(issueDate));

	}

	@Override
	public void setStartDate(Date startDate) {
		salaryRecord.set(SALARY.START_DATE, toSqlDate(startDate));

	}

	@Override
	public void setEndDate(Date endDate) {
		salaryRecord.set(SALARY.END_DATE, toSqlDate(endDate));
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		salaryRecord.set(SALARY.TIME_UNITS, timeUnits);

	}

	@Override
	public void setItBase(Double itBase) {
		salaryRecord.set(SALARY.IT_BASE, itBase != null ? itBase : 0.00);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		salaryRecord.set(SALARY.RAW_CGC_BASE, rawCgcBase != null ? rawCgcBase : 0.00);

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		salaryRecord.set(SALARY.CGC_BASE, cgcBase != null ? cgcBase : 0.00);

	}

	@Override
	public void setCgpBase(Double cgpBase) {
		salaryRecord.set(SALARY.CGP_BASE, cgpBase != null ? cgpBase : 0.00);

	}

	@Override
	public void setRemuneration(Double remuneration) {
		salaryRecord.set(SALARY.REMUNERATION, remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		salaryRecord.set(SALARY.PRO_EXT_BASE, proExtBase != null ? proExtBase : 0.00);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		salaryRecord.set(SALARY.IRPF_BASE, irpfBase != null ? irpfBase : 0.00);
	}

	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		salaryRecord.set(SALARY.MONEY_IRPF_BASE, moneyIrpfBase != null ? moneyIrpfBase : 0.00);
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		salaryRecord.set(SALARY.INKIND_IRPF_BASE,
				inkindIrpfBase != null ? inkindIrpfBase : 0.00);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		salaryRecord.set(SALARY.HEXTRA_BASE, hExtraBase != null ? hExtraBase : 0.00);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		salaryRecord.set(SALARY.NON_HEXTRA_BASE, nonHExtraBase != null ? nonHExtraBase : 0.00);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		salaryRecord.set(SALARY.TOTAL_LIQUID, totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		salaryRecord.set(SALARY.TOTAL_PAYMENT, totalPayment);

	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		salaryRecord.set(SALARY.TOTAL_DEDUCTION, totalDeduction);
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		salaryRecord.set(SALARY.TOTAL_IRPF, round(totalIrpf));
	}

	@Override
	public void setTotalSS(Double socialSecurityContributions) {
		salaryRecord.set(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS, socialSecurityContributions);
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		salaryRecord.set(SALARY.TOTAL_ENTERPRISE, totalEnterprise);
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
		InsertSetStep<SalaryEmbargoRecord> insertEmbargo = insertMoreEmbargo == null
				? dslContext.insertInto(SALARY_EMBARGO) : insertMoreEmbargo.newRecord();

		insertMoreEmbargo = insertEmbargo
				.set(SALARY_EMBARGO.DOMAIN, this.domainId)
				.set(SALARY_EMBARGO.SALARY, salaryId)
				.set(SALARY_EMBARGO.AMOUNT, amount)
				.set(SALARY_EMBARGO.DESCRIPTION, description)
				.set(SALARY_EMBARGO.CONTRACT_EMBARGO, id < 0 ? null: id );

		putContext(context);
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		addEmbargo(id, 0.00, embargo.getDescription(), embargo, context);
	}

	@Override
	public void addCost(Double amount, String description, Date start, Date end, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		InsertSetStep<SalaryCostRecord> insertCost = insertMoreCost == null ? dslContext.insertInto(SALARY_COST)
				: insertMoreCost.newRecord();

		DeductionType type = cost.getType();

		insertMoreCost = insertCost.set(SALARY_COST.DOMAIN, this.domainId).set(SALARY_COST.SALARY, salaryId)
				.set(SALARY_COST.AMOUNT, amount).set(SALARY_COST.COST_CONCEPT, cost.getName())
				// .set(SALARY_COST.DESCRIPTION, description) For what ?
				.set(SALARY_COST.TYPE, AonEnumUtils.getByte(type));
		putContext(context);

	}

	@Override
	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		InsertSetStep<SalaryBonusRecord> insertBonus = insertMoreBonus == null ? dslContext.insertInto(SALARY_BONUS)
				: insertMoreBonus.newRecord();

		insertMoreBonus = insertBonus.set(SALARY_BONUS.DOMAIN, this.domainId).set(SALARY_BONUS.SALARY, salaryId)
				.set(SALARY_BONUS.AMOUNT, amount).set(SALARY_BONUS.BONUS_CONCEPT, bonus.getName())
				.set(SALARY_BONUS.DESCRIPTION, description);
		putContext(context);

	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate, Date endDate,
			IPayment payment, Map<String, ITimedVariable<?>> context) {

		putContext(context);

		if (!isUnpaid(payment) &&
			!isPrestIT(payment) &&
			isSiblingOfPrevious(payment) ) {
			if (prevPaymentRecord.getIrpf() != null) {
				tax += prevPaymentRecord.getIrpf();
			}
			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.IRPF, tax != null ? tax : 0.00);
			if (prevPaymentRecord.getQuote() != null) {
				quote += prevPaymentRecord.getQuote();
			}
			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.QUOTE, quote != null ? quote : 0.00);
			if (prevPaymentRecord.getAmount() != null) {
				amount += prevPaymentRecord.getAmount();
			}
			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.AMOUNT, amount != null ? amount : 0.00);
			
			if ( !Objects.equals(prevPaymentRecord.getDescription(), description)) {
				try {
					description = ExpressionContext.evalTemplate(payment.getDescription(), variables);
					insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.DESCRIPTION, description);
				} catch ( Exception e  ) {
					
				}
			}
			
		} else {

			InsertSetStep<SalaryPaymentRecord> insertPayment = insertMorePayment == null
					? dslContext.insertInto(SALARY_PAYMENT) : insertMorePayment.newRecord();
			PaymentType type = payment.getType();

			insertMorePayment = insertPayment.set(SALARY_PAYMENT.DOMAIN, this.domainId)
					.set(SALARY_PAYMENT.SALARY, salaryId).set(SALARY_PAYMENT.PAYMENT_CONCEPT, payment.getName())
					.set(SALARY_PAYMENT.DESCRIPTION, description)
					.set(SALARY_PAYMENT.TYPE, AonEnumUtils.getByte(type));

			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.IRPF, tax != null ? tax : 0.00);
			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.QUOTE, quote != null ? quote : 0.00);
			insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.AMOUNT, amount != null ? amount : 0.00);
			
			if ( !(payment instanceof ISystemPayment) ) {
				insertMorePayment = insertMorePayment.set(SALARY_PAYMENT.EXPRESSION, AonStringUtils.substring(payment.getExpression(), 0 , SALARY_PAYMENT.EXPRESSION.getDataType().length()));
			}
		}

		prevPaymentRecord.setIrpf(tax);
		prevPaymentRecord.setQuote(quote);
		prevPaymentRecord.setAmount(amount);
		
		if ( payment instanceof IContractPayment )
			prevPaymentRecord.setId(((IContractPayment) payment).getId());

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
			if (prevDeductionRecord.getAmount() != null)
				amount += prevDeductionRecord.getAmount();
			insertMoreDeduction = insertMoreDeduction.set(SALARY_DEDUCTION.AMOUNT, amount != null ? amount : 0.00);
		} else {

			InsertSetStep<SalaryDeductionRecord> insertDeduction = insertMoreDeduction == null
					? dslContext.insertInto(SALARY_DEDUCTION) : insertMoreDeduction.newRecord();

			DeductionType type = deduction.getType();
			if (type != null && (type.isSsDeduction() || type.isTaxDeduction()))
				description = null;

			// TODO: start & end dates ???
			insertMoreDeduction = insertDeduction.set(SALARY_DEDUCTION.DOMAIN, this.domainId)
					.set(SALARY_DEDUCTION.SALARY, salaryId).set(SALARY_DEDUCTION.DESCRIPTION, description)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, deduction.getName())
					.set(SALARY_DEDUCTION.TYPE, AonEnumUtils.getByte(type));

			insertMoreDeduction = insertMoreDeduction.set(SALARY_DEDUCTION.AMOUNT, amount != null ? amount : 0.00);
		}

		prevDeductionRecord.setAmount(amount);
		
		if ( deduction instanceof IContractDeduction )
			prevDeductionRecord.setId(((IContractDeduction) deduction).getId());

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

	public int execute() {
		int salaries = 0;
		insertVariables();

		if (insertSalary != null)
			salaries = set(insertSalary, salaryRecord).execute();
		if (insertMorePayment != null)
			insertMorePayment.execute();
		if (insertMoreDeduction != null)
			insertMoreDeduction.execute();
		if (insertMoreBonus != null)
			insertMoreBonus.execute();
		if (insertMoreCost != null)
			insertMoreCost.execute();
		if (insertMoreEmbargo != null)
			insertMoreEmbargo.execute();
		if (insertMoreData != null)
			insertMoreData.execute();
		return salaries;
	}
	
//	private Map<String, > getSalaryRecord(InsertSetMoreStep<SalaryRecord> insertSalary) {
//		insertSalary.
//		SalaryRecord salaryRecord = new SalaryRecord(); 
//		insertSalary.getParams().forEach( (name,param) -> {
//			Salary.SALARY.get
//		});
//		return salaryRecord;
//	}

	public DSLContext getDSLContext() {
		return dslContext;
	}

	// ------------------------------------------------------------------------

	protected String getSQL() {
		String LN = "\r\n";
		StringBuffer sql = new StringBuffer();
		sql.append(set(insertSalary, salaryRecord).getSQL(ParamType.INLINED));
		if (insertMorePayment != null)
			sql.append(LN + insertMorePayment.getSQL(ParamType.INLINED));
		if (insertMoreDeduction != null)
			sql.append(LN + insertMoreDeduction.getSQL(ParamType.INLINED));
		if (insertMoreBonus != null)
			sql.append(LN + insertMoreBonus.getSQL(ParamType.INLINED));
		if (insertMoreCost != null)
			sql.append(LN + insertMoreCost.getSQL(ParamType.INLINED));
		if (insertMoreEmbargo != null)
			sql.append(LN + insertMoreEmbargo.getSQL(ParamType.INLINED));
		if (insertMoreData != null)
			sql.append(LN + insertMoreData.getSQL(ParamType.INLINED));

		return sql.toString();
	}
	
	protected  InsertReturningStep<SalaryRecord> set(InsertSetStep<SalaryRecord> insertSalary, SalaryRecord salaryRecord) {
		return insertSalary.set(salaryRecord);
	}

	public SalaryRecord getSalaryRecord() {
		return salaryRecord;
	}

	// ------------------------------------------------------------------------

	protected void setDomain(Integer domainId) {
		this.domainId = domainId;
		salaryRecord.set(SALARY.DOMAIN, domainId);
	}

	protected void setContract(Integer contractId) {
		salaryRecord.set(SALARY.CONTRACT, contractId);
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

	private void addVariable(String name, ITimedVariable<?> var) {
	    if ( var instanceof IExpressionVariable<?> ) {
	    	// Skip SISTEMA(... ) .
	    } else if ( var instanceof IWrapTimedVariable ) {
			ITimedVariable<?> wrapVar =((IWrapTimedVariable<?>) var).getVariable();
			if ( Objects.equals(var.getValue(var.getPeriod()),  wrapVar.getValue(wrapVar.getPeriod()))) {
			    var = wrapVar;
			}
	    } 
	    variables.put(name, var );
		
	}
	
	private void insertVariables() {
		for (String name : variables.varsSet() ) {
			for ( ITimedVariable<?> variable : variables.getVariables(name)){
				try {
					Object value = variable.getValue(variable.getPeriod());
					String expression = String.valueOf(value);
		
					InsertSetStep<SalaryDataRecord> insertData = insertMoreData == null ? dslContext.insertInto(SALARY_DATA)
							: insertMoreData.newRecord();
					
					insertMoreData = insertData.set(SALARY_DATA.DOMAIN, this.domainId)
							.set(SALARY_DATA.SALARY, this.salaryId)
							.set(SALARY_DATA.NAME, AonStringUtils.substring(name, 0, 32))
							.set(SALARY_DATA.EXPRESSION, expression)
							.set(SALARY_DATA.START_DATE, toSqlDate(variable.getPeriod().getStart()))
							.set(SALARY_DATA.END_DATE, toSqlDate(variable.getPeriod().getEnd()));
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
		
		return (prevPaymentRecord.getId() != null) 
				&& (payment instanceof IContractPayment )
				&& (((IContractPayment) payment).getId() != null)
				&& ((IContractPayment) payment).getId().equals(prevPaymentRecord.getId());
	}

	private boolean isSiblingOfPrevious(IDeduction deduction) {
		return (prevDeductionRecord.getId() != null) 
				&& (deduction instanceof IContractDeduction )
				&& (((IContractDeduction) deduction).getId() != null)
				&& ((IContractDeduction) deduction).getId().equals(prevDeductionRecord.getId());
	}

	private boolean isPrestIT(IPayment payment) {		
		return AonStringUtils.equals(payment.getName(),PREST_IT);
	}

	private boolean isUnpaid(IPayment payment) {
		return AonStringUtils.equals(payment.getName(),UNPAID.getName());
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

		// if ( variables.get(name, var.getPeriod()) != null )
		// return false;
		//
		// variables.put(name, var);

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
			// Implicit
			ALREADY_AT_SALARY.add(ALL);
			// Bases
			ALREADY_AT_SALARY.add(CATEGORY.getName());
			// ALREADY_AT_SALARY.add(CGC_BASE.getName());
			ALREADY_AT_SALARY.add(CGC_ENTERPRISE.getName());
			// ALREADY_AT_SALARY.add(CGP_BASE.getName());
			ALREADY_AT_SALARY.add(IRPF_BASE.getName());

			//ALREADY_AT_SALARY.add(STRUCTURAL_OVERTIME_BASE.getName());
			//ALREADY_AT_SALARY.add(NON_STRUCTURAL_OVERTIME_BASE.getName());

			// I.R.P.F
			//ALREADY_AT_SALARY.add(IRPF_PERCENT.getName());
			// Totals
			ALREADY_AT_SALARY.add(TOTAL_LIQUID.getName());
			ALREADY_AT_SALARY.add(TOTAL_PAYMENT.getName());

			Collections.sort(ALREADY_AT_SALARY);
		}
		return Collections.binarySearch(ALREADY_AT_SALARY, name) >= 0;

	}

	private static int max(DSLContext dslContext, Identity<?, Integer> identity) {
		AggregateFunction<Integer> maxFunc = DSL.max(identity.getField());

		Integer max = dslContext.select(maxFunc).from(identity.getTable()).forUpdate().fetchOne(maxFunc);

		return max == null ? 0 : max; // null if the query returned no records.
	}

	private static Double round(Double value) {
		return value != null ? BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_DOWN).doubleValue() : null;
	}

	private static boolean isExtra(IPayment payment) {
		return ( PaymentType.CRA_0004 == payment.getType() || 
				 AonStringUtils.equals(payment.getName(), "PAGA_EXTRA") );  
	}

	// ------------------------------------------------------------------------
	public static class  ReplaceJooqSalaryBuilder<T extends ISalary>  extends JooqSalaryBuilder<T> {
			
			private Integer domainId;
			private Integer salaryId;
		
			public ReplaceJooqSalaryBuilder(Connection connection, Integer domainId, Integer salaryId ) {
				super(connection);
				this.domainId = domainId;
				this.salaryId = salaryId;
			}

			public ReplaceJooqSalaryBuilder(DSLContext dslContext, Integer domainId, Integer salaryId ) {
				super(dslContext);
				this.domainId = domainId;
				this.salaryId = salaryId;
			}

			@Override
			public int execute() {
				deleteSalaryDetails();
				return super.execute();
			}
			
			@Override
			protected int getNextSalaryId() {
				return salaryId != null ? salaryId : super.getNextSalaryId();
			}
			
			@Override
			protected InsertReturningStep<SalaryRecord> set(InsertSetStep<SalaryRecord> insertSalary,
				SalaryRecord salaryRecord) {
				return insertSalary
				.set(salaryRecord)
				.onDuplicateKeyUpdate()
				.set(salaryRecord);
			}
			
			private void deleteSalaryDetails() {
				if ( salaryId != null ) {
					getDSLContext().delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
					getDSLContext().delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
					getDSLContext().delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
					getDSLContext().delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
					getDSLContext().delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
					getDSLContext().delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(salaryId)).and(SALARY.DOMAIN.eq(domainId)).execute();
				}
			}
			
	}
}
