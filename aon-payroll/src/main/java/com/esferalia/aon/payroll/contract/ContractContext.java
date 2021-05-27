package com.esferalia.aon.payroll.contract;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ContractEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;

public class ContractContext {

	private static Settings SETTINGS = null;
//	private static List<String> ALREADY_AT_SALARY = null;

	private DSLContext dslContext;

	private InsertSetStep<ContractRecord> insertContract;
	private InsertSetMoreStep<ContractDataRecord> insertMoreData;
	private InsertSetMoreStep<ContractInfoRecord> insertMoreInfo;
	private InsertSetMoreStep<ContractBonusRecord> insertMoreBonus;
	private InsertSetMoreStep<ContractEmbargoRecord> insertMoreEmbargo;
	
	private int contractId 	= -1;
	private int domainId 	= -1;
	
	
	public ContractContext(Connection connection) {
		this(DSL.using(connection, getDefaultSettings()));
	}
		
	public ContractContext(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	private SelectConditionStep<ContractRecord> contract;
	private SelectConditionStep<ContractDataRecord> contractData;
	private SelectConditionStep<ContractInfoRecord> contractInfo;
	private SelectConditionStep<ContractBonusRecord> contractBonus;
	private SelectConditionStep<ContractEmbargoRecord> contractEmbargo;
	
	
	public void load(Integer contractId){
		this.contractId = contractId;
		contract = dslContext.selectFrom(CONTRACT).where(CONTRACT.ID.equal(contractId));
		
		 for (Record r : contract.fetch()) {
             Integer id = r.getValue(CONTRACT.ID);
             Integer domain = r.getValue(CONTRACT.DOMAIN);
             java.sql.Date startDate = r.getValue(CONTRACT.START_DATE);
             java.sql.Date endDate = r.getValue(CONTRACT.END_DATE);

             System.out.println("ID: " + id + " domain: " + domain + " start_date: " + startDate+ " end_date: " + endDate);
         }

         loadData();

         loadInfo();

         loadBonus();
         
         loadEmbargo();
		
	}


	private void loadData(){
		contractData = dslContext.selectFrom(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.equal(contractId));
		for (Record r : contractData.fetch()) {
			System.out.println(r.getValue(CONTRACT_DATA.NAME) +"  "+ r.getValue(CONTRACT_DATA.EXPRESSION));
		}
	}
	
	private void loadInfo() {
		contractInfo = dslContext.selectFrom(CONTRACT_INFO).where(CONTRACT_INFO.CONTRACT.equal(contractId));
		for (Record r : contractInfo.fetch()) {
			System.out.println(r.getValue(CONTRACT_INFO.NAME) +"  "+ r.getValue(CONTRACT_INFO.NAME));
		}
	}
	
	private void loadBonus() {
		contractBonus = dslContext.selectFrom(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.equal(contractId));
		for (Record r : contractBonus.fetch()) {
			System.out.println(r.getValue(CONTRACT_BONUS.ID));
		}
	}
	
	private void loadEmbargo() {
		contractEmbargo = dslContext.selectFrom(CONTRACT_EMBARGO).where(CONTRACT_EMBARGO.CONTRACT.equal(contractId));
		for (Record r : contractEmbargo.fetch()) {
			System.out.println(r.getValue(CONTRACT_EMBARGO.ID));
		}
	}

//	@Override
//	public ISalary getSalary() {
//		return null;
//	}

//	@Override
//	public void createNewSalary() {
//
//		if (variables.size() > 0) {
//			addVariables(variables);
//			variables.clear();
//		}
//
//		InsertSetStep<SalaryRecord> insertSalary = insertMoreSalary == null ? dslContext
//				.insertInto(SALARY) : insertMoreSalary.newRecord();
//
//		salaryId = (salaryId == -1 ? max(dslContext, SALARY.getIdentity())
//				: salaryId) + 1;
//		insertMoreSalary = insertSalary.set(SALARY.ID, salaryId);
//	}

//	@Override
//	public void setContract(Object contract) {
//		SQLSalaryProxy salaryProxy = (SQLSalaryProxy) contract;
//
//		setDomain(salaryProxy.getDomainId());
//		setContract(salaryProxy.getContractId());
//	}

//	@Override
//	public void addPayment(Double amount, Double quote, Double tax,
//			String description, IPayment payment,
//			Map<String, ITimedVariable<?>> context) {
//
//		InsertSetStep<SalaryPaymentRecord> insertPayment = insertMorePayment == null ? dslContext
//				.insertInto(SALARY_PAYMENT) : insertMorePayment.newRecord();
//
//		PaymentType type = payment.getType();
//
//		insertMorePayment = insertPayment
//				.set(SALARY_PAYMENT.DOMAIN, this.domainId)
//				.set(SALARY_PAYMENT.SALARY, salaryId)
//				.set(SALARY_PAYMENT.AMOUNT, amount)
//				.set(SALARY_PAYMENT.QUOTE, quote)
//				.set(SALARY_PAYMENT.IRPF, tax)
//				.set(SALARY_PAYMENT.PAYMENT_CONCEPT, payment.getName())
//				// .set(SALARY_PAYMENT.EXPRESSION, payment.getExpression())
//				.set(SALARY_PAYMENT.DESCRIPTION, description)
//				.set(SALARY_PAYMENT.TYPE,
//						type != null ? (byte) type.ordinal() : null);
//
//		putContext(context);
//	}
//
//	@Override
//	public void addZeroPayment(Double quote, Double tax, IPayment payment,
//			Map<String, ITimedVariable<?>> context) {
//		addPayment(0.00, quote, tax, payment.getDescription(), payment, context);
//	}

	

	public int execute() {
		int salaries = 0;
//		if (insertMoreSalary != null)
//			salaries = insertMoreSalary.execute();
		if (insertMoreData!= null)
			insertMoreData.execute();
		if (insertMoreInfo!= null)
			insertMoreInfo.execute();
		if (insertMoreBonus != null)
			insertMoreBonus.execute();
		if (insertMoreEmbargo != null)
			insertMoreEmbargo.execute();
		return salaries;
	}

	// ------------------------------------------------------------------------

//	protected String getSQL() {
//		String LN = "\r\n";
//		StringBuffer sql = new StringBuffer();
//		sql.append(insertMoreContract.getSQL(ParamType.INLINED));
//		if (insertMoreData != null)
//			sql.append(LN + insertMoreData.getSQL(ParamType.INLINED));
//		if (insertMoreInfo != null)
//			sql.append(LN + insertMoreInfo.getSQL(ParamType.INLINED));
//		if (insertMoreBonus != null)
//			sql.append(LN + insertMoreBonus.getSQL(ParamType.INLINED));
//		if (insertMoreEmbargo != null)
//			sql.append(LN + insertMoreEmbargo.getSQL(ParamType.INLINED));
//
//		return sql.toString();
//	}
	
	public DSLContext getDSLContext() {
		return dslContext;
	}
	
	// ------------------------------------------------------------------------

//	private void setDomain(Integer domainId) {
//		this.domainId = domainId;
//		insertMoreSalary = insertMoreSalary.set(SALARY.DOMAIN, domainId);
//	}

//	private void setContract(Integer contractId) {
//		insertMoreSalary = insertMoreSalary.set(SALARY.CONTRACT, contractId);
//	}

//	private void putContext(Map<String, ITimedVariable<?>> ctx) {
//		for (Map.Entry<String, ITimedVariable<?>> entry : ctx.entrySet())
//			if (filter(entry))
//				variables.put(entry.getKey(), entry.getValue());
//	}

//	private void addVariables(Variables vars) {
//		for (String name : vars.varsSet())
//			for (ITimedVariable<?> var : vars.getValues(name))
//				addVariable(name, var);
//	}

//	private void addVariable(String name, ITimedVariable<?> variable) {
//		Period period = variable.getPeriod();
//		Object value = variable.getValue(period);
//		String expression = String.valueOf(value);
//
//		InsertSetStep<SalaryDataRecord> insertData = insertMoreData == null ? dslContext
//				.insertInto(SALARY_DATA) : insertMoreData.newRecord();
//
//		insertMoreData = insertData.set(SALARY_DATA.DOMAIN, this.domainId)
//				.set(SALARY_DATA.SALARY, this.salaryId)
//				.set(SALARY_DATA.NAME, name)
//				.set(SALARY_DATA.EXPRESSION, expression)
//				.set(SALARY_DATA.START_DATE, toSqlDate(period.getStart()))
//				.set(SALARY_DATA.END_DATE, toSqlDate(period.getEnd()));
//
//	}

	// ------------------------------------------------------------------------

	private static Settings getDefaultSettings() {
		if ( SETTINGS == null ) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	private static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

//	private boolean filter(String name, ITimedVariable<?> var) {
//		Period period = var.getPeriod();
//		Object value = var.getValue(period);
//		if (value == null)
//			return false;
//		// end-if: Not insert NULL data
//
//		boolean knowType = value instanceof Number || value instanceof String
//				|| value instanceof Boolean || value instanceof Enum<?>;
//		if (!knowType)
//			return false;
//
//		// end-if: Not insert complex object, functions...
//
//		if (isAlreadyAtSalary(name))
//			return false;
//		return true;
//	}
//
//	private boolean filter(Map.Entry<String, ITimedVariable<?>> entry) {
//		return filter(entry.getKey(), entry.getValue());
//	}

//	private static boolean isAlreadyAtSalary(String name) {
//		if (ALREADY_AT_SALARY == null) {
//			ALREADY_AT_SALARY = new LinkedList<String>();
//			// Bases
//			ALREADY_AT_SALARY.add(CATEGORY.getName());
//			ALREADY_AT_SALARY.add(CGC_BASE.getName());
//			ALREADY_AT_SALARY.add(CGC_ENTERPRISE.getName());
//			ALREADY_AT_SALARY.add(CGP_BASE.getName());
//			ALREADY_AT_SALARY.add(IRPF_BASE.getName());
//			ALREADY_AT_SALARY.add(STRUCTURAL_OVERTIME_BASE.getName());
//			ALREADY_AT_SALARY.add(NON_STRUCTURAL_OVERTIME_BASE.getName());
//
//			// I.R.P.F
//			ALREADY_AT_SALARY.add(IRPF_PERCENT.getName());
//			// Totals
//			ALREADY_AT_SALARY.add(TOTAL_LIQUID.getName());
//			ALREADY_AT_SALARY.add(TOTAL_PAYMENT.getName());
//
//			Collections.sort(ALREADY_AT_SALARY);
//		}
//		return Collections.binarySearch(ALREADY_AT_SALARY, name) >= 0;
//
//	}

	private static int max(DSLContext dslContext, Identity<?, Integer> identity) {
		AggregateFunction<Integer> maxFunc = DSL.max(identity.getField());

		Integer max = dslContext.select(maxFunc).from(identity.getTable())
				.forUpdate().fetchOne(maxFunc);

		return max == null ? 0 : max; // null if the query returned no records.
	}
	

}
