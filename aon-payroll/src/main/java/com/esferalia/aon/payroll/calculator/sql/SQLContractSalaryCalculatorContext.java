package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDeduction;
import com.esferalia.aon.payroll.calculator.sql.SQLContractPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.Period;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

public class SQLContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext {
	

	private static final String ID						= "id";

	private static final String START_DATE				= "start_date";
	private static final String END_DATE				= "end_date";
	
	private static final String CCC						= "ccc";
	private static final String SS_REGIME				= "ss_regime";
	private static final String ENTERPRISE_NAME			= "enterprise_name";
	private static final String ENTERPRISE_DOC			= "enterprise_doc";
	
	private static final String EMPLOYEE_NAME			= "employee_name";
	private static final String EMPLOYEE_FIRST_SURNAME	= "employee_first_surname";
	private static final String EMPLOYEE_SECOND_SURNAME	= "employee_second_surname";

	private static final String EMPLOYEE_DOC			= "employee_doc";
	private static final String SOCIAL_SECURITY_NUMBER	= "social_security";
	private static final String SENIORITY_DATE			= "seniority_date";
	private static final String CATEGORY				= "category";
	
	private static final String MAIN_SQL = "SELECT "
		+ "contract.id AS " + ID 
		+", contract.start_date AS " + START_DATE
		+", contract.end_date AS " + END_DATE
		+", contract.ss_regime AS " + SS_REGIME
		+", person.name AS " + EMPLOYEE_NAME
		+", person.first_surname AS " + EMPLOYEE_FIRST_SURNAME
		+", person.second_surname AS " + EMPLOYEE_SECOND_SURNAME
		+", person_registry.document AS " + EMPLOYEE_DOC
		+", person.social_security_num AS " + SOCIAL_SECURITY_NUMBER
		+", enterprise_ccc.ccc AS " + CCC
		+", enterprise_registry.name AS " + ENTERPRISE_NAME
		+", enterprise_registry.document AS " + ENTERPRISE_DOC
		+" FROM contract"
		+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
		+", person"
		+", registry AS person_registry"
		+", workplace"
		+", enterprise"
		+", registry AS enterprise_registry"
		+", raddress "
		+" WHERE contract.person = person.registry"				// INNER JOIN: person es NOT NULL
		+" AND person.registry = person_registry.id"			// INNER JOIN: registry es NOT NULL
		+" AND contract.workplace = workplace.id"				// INNER JOIN: workplace es NOT NULL
		+" AND workplace.enterprise = enterprise.registry"		// INNER JOIN: enterprise es NOT NULL
		+" AND enterprise.registry = enterprise_registry.id"	// INNER JOIN: registry es NOT NULL
		+" AND workplace.address = raddress.id"					// INNER JOIN: address es NOT NULL
		+" AND contract.start_date <= ? "					 
		+" AND ( contract.end_date  IS NULL"
		+" OR contract.end_date >= ? )";
	
	private static final String PAYMENT_SQL =
		"SELECT payment_concept AS " + SQLContractPayment.CONCEPT
		+", contract_payment.description AS " +  SQLContractPayment.DESCRIPTION
		+", contract_payment.expression AS " +  SQLContractPayment.EXPRESSION
		+", contract_payment.irpf_expression AS " +  SQLContractPayment.IRPF_EXPRESSION
		+", contract_payment.quote_expression AS " +  SQLContractPayment.QUOTE_EXPRESSION
		+", contract_payment.type AS " +  SQLContractPayment.TYPE
		+", contract_payment.month AS " +  SQLContractPayment.MONTH
		+", contract_payment.start_date AS " +  SQLContractPayment.START_DATE
		+", contract_payment.end_date AS " +  SQLContractPayment.END_DATE
		+" FROM contract_payment"
		+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
		+"	ON contract_payment.payment_concept = payment_concept.id"	
		+" WHERE contract_payment.contract = ? "
		+" AND contract_payment.start_date <= ? "
		+" AND ( contract_payment.end_date IS NULL "
		+" OR contract_payment.end_date >= ? )";

	private static final String DEDUCTION_SQL =
		"SELECT deduction_concept AS " + SQLContractDeduction.CONCEPT
		+", contract_deduction.description AS " +  SQLContractDeduction.DESCRIPTION
		+", contract_deduction.expression AS " +  SQLContractDeduction.EXPRESSION
		+", contract_deduction.type AS " +  SQLContractDeduction.TYPE
		+", contract_deduction.start_date AS " +  SQLContractDeduction.START_DATE
		+", contract_deduction.end_date AS " +  SQLContractDeduction.END_DATE
		+" FROM contract_deduction"
		+" LEFT JOIN  deduction_concept" 							// LEFT JOIN: deduction_concept puede ser NULL
		+"	ON contract_deduction.deduction_concept = deduction_concept.id"	
		+" WHERE contract_deduction.contract = ? "
		+" AND contract_deduction.start_date <= ? "
		+" AND ( contract_deduction.end_date IS NULL"
		+" OR contract_deduction.end_date >= ? )"
		+" AND ( contract_deduction.month IS NULL " 
		+" OR contract_deduction.month BETWEEN ? AND ?  )";
	
	private static final String FCONSTANT_NAME 			= "name";
	private static final String FCONSTANT_EXPR 			= "expr";
	private static final String FCONSTANT_START_DATE 	= "start_date";
	private static final String FCONSTANT_END_DATE 		= "end_date";
	
	private static final String FCONSTANTS_SQL =
		"SELECT name AS " + FCONSTANT_NAME
		+", expression AS " + FCONSTANT_EXPR
		+", start_date AS " + FCONSTANT_START_DATE
		+", end_date AS " + FCONSTANT_END_DATE
		+" FROM function_constant"
		+" WHERE start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";

	private static final String CDATA_NAME = "name";
	private static final String CDATA_EXPR = "expr";
	private static final String CDATA_START_DATE 	= "start_date";
	private static final String CDATA_END_DATE 		= "end_date";
	
	private static final String CDATA_SQL =
		"SELECT name AS " + CDATA_NAME
		+", expression AS " + CDATA_EXPR
		+", start_date AS " + CDATA_START_DATE
		+", end_date AS " + CDATA_END_DATE
		+" FROM contract_data"
		+" WHERE contract_data.contract = ? " 
		+ "AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";

	private Date issueDate;
	private Date startDate;
	private Date endDate;
	
	private ResultSet resultSet;  
	private PreparedStatement ceventStmt;
	private PreparedStatement paymentStmt;
	private PreparedStatement deductionStmt;

	private SQLContractPayment sqlContractPayment;  
	private SQLContractDeduction sqlContractDeduction;  
	
	private Criteria criteria;
	private Connection connection;
	
	
	private ExpressionContext systemExpressionContext;
	private ExpressionContext contractExpressionContext;
	
	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate) 
	throws SQLException, ExpressionException {
		this(connection, startDate, endDate, Calendar.getInstance().getTime(), null);
	}
	
	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate) 
	throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate, Criteria criteria) 
	throws SQLException, ExpressionException {
		this.connection = connection;
		this.startDate = startDate;
		this.endDate = endDate;
		this.criteria = criteria;
		
		initResultSet();
		initPaymentStmt();
		initDeductionStmt();
		initCeventStmt();
		initSystemExpressionCtx();

		this.sqlContractPayment = 
			new SQLContractPayment();
		this.sqlContractDeduction = 
			new SQLContractDeduction();
	}

	@Override
	public Date getIssueDate() {
		return this.issueDate;
	}

	@Override
	public Date getStartDate() {
		return this.startDate;
	}

	@Override
	public Date getEndDate() {
		return this.endDate;
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		return new SQLSalaryProxy(getId());
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return contractExpressionContext;
	}

	@Override
	public String getCcc() {
		return getString(CCC);
	}

	@Override
	public String getEnterpriseName() {
		String name = getString(ENTERPRISE_NAME );
		if ( ! StringUtils.isEmpty(name) ) {
			return name;
		}
		else {
			return StringUtils.EMPTY;
		}
	}

	@Override
	public String getEnterpriseAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseDocument() {
		return getString(ENTERPRISE_DOC );
	}
	
	@Override
	public SSRegimeType getSSRegime() {
		int ordinal =  getInt(SS_REGIME); // 'ss_regime' is NOT NULL
		return SSRegimeType.values()[ordinal];
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public String getEmployeeName() {
		String name = getString(EMPLOYEE_NAME);
		String firstSurname = getString(EMPLOYEE_FIRST_SURNAME);
		String secondSurname = getString(EMPLOYEE_SECOND_SURNAME);
		
		StringBuffer employeeName = new StringBuffer();
		
		if (!StringUtils.isEmpty(firstSurname)){
			employeeName.append(firstSurname);
		}
		if (!StringUtils.isEmpty(secondSurname)){
			employeeName.append(" ");
			employeeName.append(secondSurname);
		}
		if (!StringUtils.isEmpty(name)){
			employeeName.append(", ");
			employeeName.append(name);
		}
			
		return employeeName.toString();
	}

	@Override
	public String getEmployeeDocument() {
		return getString(EMPLOYEE_DOC);
	}

	@Override
	public String getSocialSecurityNumber() {
		return getString(SOCIAL_SECURITY_NUMBER);
	}

	@Override
	public Integer getRegistration() {
		// TODO Add 'seniory_date' column to table 'contract_data'
		return 0;
	}

	@Override
	public Date getSeniorityDate() {
		// TODO Add 'seniory_date' column to table 'contract_data'
		return null;
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		try {
			this.sqlContractPayment.close();
			int id = getId();
			paymentStmt.setInt(1,id);
			ResultSet rs = paymentStmt.executeQuery();
			this.sqlContractPayment.setResultSet(rs);
			return this.sqlContractPayment;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		try {
			this.sqlContractDeduction.close();
			int id = getId();
			deductionStmt.setInt(1,id);
			ResultSet rs = deductionStmt.executeQuery();
			this.sqlContractDeduction.setResultSet(rs);
			return this.sqlContractDeduction;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	
	public int getId() {
		return getInt(ID);
	}
	

	public boolean next() throws SQLException, ExpressionException{
		boolean next =  this.resultSet.next();
		if ( next ) {
			initContractExpressionCtx();
		}
		return next;
	}

	//----------------------------------------------------------------------------------------
	// don't look it's private
	//----------------------------------------------------------------------------------------
	
	
	private void initResultSet() 
	throws SQLException {
		String sql = MAIN_SQL;
		if ( this.criteria != null ){
			sql = 
				CriteriaUtilities.toSQLString(this.criteria, sql);
		}
		PreparedStatement stmt = 
			connection.prepareStatement(sql);
		java.sql.Date sqlEndDate = 
			new java.sql.Date(this.endDate.getTime());
		stmt.setDate(1, sqlEndDate );
		stmt.setDate(2, sqlEndDate );
		resultSet = stmt.executeQuery();
	}
	
	private void initPaymentStmt()
	throws SQLException {
		this.paymentStmt  = 
			this.connection.prepareStatement(PAYMENT_SQL);
		java.sql.Date sqlEndDate = 
			new java.sql.Date(this.endDate.getTime());
		this.paymentStmt.setDate(2, sqlEndDate);
		this.paymentStmt.setDate(3, sqlEndDate);
	}

	private void initDeductionStmt()
	throws SQLException {
		this.deductionStmt  = 
			this.connection.prepareStatement(DEDUCTION_SQL);
		java.sql.Date sqlEndDate = 
			new java.sql.Date(this.endDate.getTime());
		this.deductionStmt.setDate(2, sqlEndDate);
		this.deductionStmt.setDate(3, sqlEndDate);
		int startMonth = CommonUtil.getMonth(startDate); 
		this.deductionStmt.setInt(4, startMonth);
		int endMonth = CommonUtil.getMonth(endDate);
		this.deductionStmt.setInt(5, endMonth);
	}
	
	private void initCeventStmt()
	throws SQLException {
		this.ceventStmt  = 
			this.connection.prepareStatement(CDATA_SQL);
		java.sql.Date sqlEndDate = 
			new java.sql.Date(this.endDate.getTime());
		this.ceventStmt.setDate(2, sqlEndDate);
		this.ceventStmt.setDate(3, sqlEndDate);
	}
	
	private IExpression getYearDays() {
		ExpressionImpl yearDaysExpr = 
			new ExpressionImpl();
		yearDaysExpr.setName(YEAR_DAYS.getName());
		yearDaysExpr.setScope(ExpressionScope.SYSTEM);
		Date startDate = CommonUtil.getYearFirstDay(this.startDate);
		Date endDate = CommonUtil.getYearLastDay(this.endDate);
		long yearDays = 
			CommonUtil.getDaysBetweenDates(startDate, endDate);
		yearDays += 1;
		yearDaysExpr.setExpression(Long.toString(yearDays));
		return yearDaysExpr;
	}

	private IExpression getMonthDays() {
		ExpressionImpl monthDaysExpr = 
			new ExpressionImpl();
		monthDaysExpr.setName(MONTH_DAYS.getName());
		monthDaysExpr.setScope(ExpressionScope.SYSTEM);
		Date startDate = CommonUtil.getMonthFirstDay(this.startDate);
		Date endDate = CommonUtil.getMonthLastDay(this.endDate);
		long monthDays = 
			CommonUtil.getDaysBetweenDates(startDate, endDate);
		monthDays += 1;
		monthDaysExpr.setExpression(Long.toString(monthDays));
		return monthDaysExpr;
	}

	private IExpression getZeroExpression( String name, ExpressionScope scope ) {
		ExpressionImpl monthDaysExpr = 
			new ExpressionImpl();
		monthDaysExpr.setName(name);
		monthDaysExpr.setScope(ExpressionScope.SYSTEM);
		monthDaysExpr.setExpression("0");
		return monthDaysExpr;
	}
	

	private void initSystemExpressionCtx() throws SQLException, ExpressionException {
		this.systemExpressionContext = 
			new ExpressionContext();
		
		IExpression yearDaysExpr = getYearDays(); 
		systemExpressionContext.addExpression(yearDaysExpr, startDate, endDate);

		IExpression monthDaysExpr = getMonthDays(); 
		systemExpressionContext.addExpression(monthDaysExpr, startDate, endDate);
		
		IExpression holidays = 
			getZeroExpression (HOLIDAYS.getName(), ExpressionScope.SYSTEM);
		systemExpressionContext.addExpression(holidays, startDate, endDate);
		
		loadFunctionConstants(systemExpressionContext);
	}
	
	private void loadFunctionConstants(ExpressionContext expressionCtx) 
	throws SQLException, ExpressionException {
		ResultSet rs = null;
		PreparedStatement stmt = null; 
		try {
			stmt = 
				connection.prepareStatement(FCONSTANTS_SQL);
			java.sql.Date sqlEndDate = 
				new java.sql.Date(this.endDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlEndDate);
			rs = stmt.executeQuery();
			while ( rs.next() ) {
				ExpressionImpl expr = 
					new ExpressionImpl();
				expr.setName(rs.getString(FCONSTANT_NAME));
				expr.setExpression(rs.getString(FCONSTANT_EXPR));
				expr.setScope(ExpressionScope.APPLICATION);
				Date start = Period.max(rs.getDate(FCONSTANT_START_DATE), startDate);
				Date end = Period.min ( rs.getDate(FCONSTANT_END_DATE), endDate );
				expressionCtx.addExpression(expr, start, end );
			}
		}finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}
	
	private IExpression getWorkedDays() {
		ExpressionImpl workedDaysExpr = 
			new ExpressionImpl();
		workedDaysExpr.setName(WORKED_DAYS.getName());
		workedDaysExpr.setScope(ExpressionScope.CONTRACT);
		Date start = Period.max ( this.startDate, getDate(START_DATE));
		Date end = Period.min ( this.endDate, getDate(END_DATE));
		long workedDays = 
			CommonUtil.getDaysBetweenDates(start, 
					end);
		workedDays += 1;
		workedDaysExpr.setExpression(Long.toString(workedDays));
		return workedDaysExpr;
	}

	private void initContractExpressionCtx() 
	throws SQLException, ExpressionException  {
		this.contractExpressionContext = 
			new ExpressionContext(systemExpressionContext);
		
		IExpression workedDaysExpression = 
			getWorkedDays();
		this.contractExpressionContext.addExpression(workedDaysExpression, startDate, endDate);
		
		ResultSet rs = null;
		try{ 
			ceventStmt.setInt(1, getId());
			rs = ceventStmt.executeQuery();
			while ( rs.next() ) {
				ExpressionImpl expr = 
					new ExpressionImpl();
				expr.setName(rs.getString(CDATA_NAME));
				expr.setExpression(rs.getString(CDATA_EXPR));
				expr.setScope(ExpressionScope.CONTRACT );
				Date start = Period.max ( rs.getDate(CDATA_START_DATE), startDate );
				Date end = Period.min( rs.getDate(CDATA_END_DATE), endDate );
				try {
					this.contractExpressionContext.addExpression(expr, start, end );
				} catch (Exception e) {
				}
			}
			
		}finally {
			if ( rs != null ){
				rs.close();
			}
		}
		
		
	}
	

	private int getInt(String columnLabel) {
		try {
			return this.resultSet.getInt(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Date getDate(String columnLabel) {
		try {
			return this.resultSet.getDate(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private String getString(String columnLabel) {
		try {
			return this.resultSet.getString(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
