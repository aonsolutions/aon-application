package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.AbstractSQL.EnterpriseCcc;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariableException;

public class SQLContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext{
	
	public static final String PERSON_REGISTRY = "person_registry";
	public static final String ENTERPRISE_REGISTRY = "enterprise_registry";
	
	public static final String EMBARGO_PAID = "embargo_paid";
	
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";
	
	private static final String MAIN_SQL = "SELECT * "
		+" FROM contract"
		+" LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
		+" LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
		+" LEFT JOIN agreement_level_category ON (contract.agreement_level_category = agreement_level_category.id)"
		+", person"
		+", registry AS " + PERSON_REGISTRY
		+", workplace"
		+", enterprise"
		+", registry AS " + ENTERPRISE_REGISTRY
		+", customer"
		+", raddress"
		+" WHERE contract.person = person.registry"				// INNER JOIN: person es NOT NULL
		+" AND person.registry = person_registry.id"			// INNER JOIN: registry es NOT NULL
		+" AND contract.workplace = workplace.id"				// INNER JOIN: workplace es NOT NULL
		+" AND workplace.enterprise = enterprise.registry"		// INNER JOIN: enterprise es NOT NULL
		+" AND enterprise.registry = enterprise_registry.id"	// INNER JOIN: registry es NOT NULL
		+" AND enterprise.registry = enterprise_registry.id"	// INNER JOIN: registry es NOT NULL
		+" AND customer.registry = enterprise_registry.id"		// INNER JOIN: registry es NOT NULL
		+" AND workplace.address = raddress.id"					// INNER JOIN: address es NOT NULL
		+" AND contract.start_date <= ? "					 
		+" AND ( contract.end_date  IS NULL"
		+" OR contract.end_date >= ? )";
	
	private static final String PAYMENT_SQL =
		"SELECT * "
		+" FROM contract_payment"
		+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
		+"	ON payment_concept = payment_concept.id"	
		+" WHERE contract = ? "
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";
	
	private static final String PAYMENTS_FILTER [] = {
		" AND salary_type IN ("+SalaryType.SALARY.ordinal()+" ,"+SalaryType.EXTRA.ordinal()+")" , 	//SalaryType.SALARY
		" AND salary_type = " + SalaryType.EXTRA.ordinal()+ " " , 									//SalaryType.EXTRA
		" AND salary_type = " + SalaryType.SETTLE.ordinal()+ " " , 									//SalaryType.SETTLE
		" AND salary_type = " + SalaryType.DELAY.ordinal()+ " " , 									//SalaryType.DELAY
		" AND salary_type = " + SalaryType.NOT_ENJOYED_VACATIONS.ordinal()+ " " , 					//SalaryType.NOT_ENJOYED_VACATIONS
	};
	//" AND salary_type IN ("+SalaryType.SALARY.ordinal()+" ,"+SalaryType.EXTRA.ordinal()+")"

	private static final String DEDUCTION_SQL =
		"SELECT *"
		+" FROM contract_deduction"
		+" LEFT JOIN  deduction_concept" 							// LEFT JOIN: deduction_concept puede ser NULL
		+"	ON deduction_concept = deduction_concept.id"	
		+" WHERE contract = ? "
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";

	private static final String BONUS_SQL =
		"SELECT *"
		+" FROM contract_bonus"
		+" LEFT JOIN  bonus_concept" 							// LEFT JOIN: bonus_concept puede ser NULL
		+"	ON bonus_concept = bonus_concept.id"	
		+" WHERE contract = ? "
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";

	private static final String EMBARGO_SQL =
		"SELECT *" 
		+ ", ( SELECT sum(amount) FROM salary_embargo WHERE contract_embargo=contract_embargo.id  ) AS " + EMBARGO_PAID
		+" FROM contract_embargo"
		+" WHERE contract = ? "
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )"
		+" ORDER BY start_date";									// ORDER BY : El primero que llega cobra

	private static final String SYSTEM_COST_SQL =
		"SELECT *"
		+" FROM system_cost"
		+" WHERE start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";

	private static final String SYSTEM_DEDUCTION_SQL =
		"SELECT *"
		+" FROM system_deduction"
		+" LEFT JOIN  deduction_concept" 							// LEFT JOIN: deduction_concept puede ser NULL
		+"	ON deduction_concept = deduction_concept.id"	
		+" WHERE start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";

	private static final String SYSTEM_PAYMENT_SQL =
		"SELECT *"
		+" FROM system_payment"
		+" LEFT JOIN  payment_concept" 								// LEFT JOIN: payment_concept puede ser NULL
		+"	ON payment_concept = payment_concept.id"	
		+" WHERE start_date <= ? "
		+" AND ( end_date IS NULL"
		+" OR end_date >= ? )";
	
	private static final String CDATA_SQL =
		"SELECT * " 
		+" FROM contract_data"
		+" WHERE contract = ? " 
		+ "AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";
	
	public static final String CLEAVE_SQL_PARENT_DAYS = "dias";
	
	private static final String CLEAVE_SQL =
		"SELECT * ,"
		+"( SELECT sum(DATEDIFF(end_date,start_date)+1)"
		+" FROM contract_leave AS parent"
		+" WHERE ( parent.id=contract_leave.parent"+
		"  OR parent=contract_leave.parent )"+
		" AND parent.start_date < contract_leave.start_date )"+
		" AS " + CLEAVE_SQL_PARENT_DAYS
		+" FROM contract_leave"
		+" WHERE contract = ? " 
		+ "AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";
	
	
	private static final int CACHE_SIZE = 25;
	
	
	/**
	 * Clase base para implementar variables pesadas con evaluación perezosa.
	 * Las clases hijas únicamente deberán implementar el método 'V getValue()'. 
	 * 
	 * @author rtrepiana
	 *
	 * @param <V>
	 */
	private abstract class LazyTimedVariable<V> 
		implements ITimedVariable<V> 
	{
		private V value ;
		private boolean initialized = false;
		
		protected Period period =
			new Period(startDate, endDate);
		
		@Override
		public Period getPeriod() {
			return period;
		}
		
		@Override
		public V getValue(Period p ) {
			if ( !initialized ){
				value = create();
				initialized=true;
			}
			
			return value;
		}
		
		public abstract V create() ;
	}
	
	private abstract class ActiveTimedVariable<V> 
	implements ITimedVariable<V> 
	{
		
		protected Period period =
			new Period(startDate, endDate);
		
		@Override
		public Period getPeriod() {
			return period;
		}
		
	}

	private Criteria 										criteria;
	private Connection 										connection;
	
	private SalaryType										salaryType;
	
	private Date 											issueDate;
	private Date 											startDate;
	private Date 											endDate;
	private ResultSet 										resultSet;  
	private PreparedStatement 								ceventStmt;
	private PreparedStatement 								cleaveStmt;
	private PreparedStatement 								paymentStmt;
	private PreparedStatement 								deductionStmt;
	private PreparedStatement 								bonusStmt;
	private PreparedStatement 								embargoStmt;

	private SQLContractPayment 								sqlContractPayment;  
	private SQLContractDeduction 							sqlContractDeduction;  
	private SQLContractBonus								sqlContractBonus;  
	private SQLContractEmbargo 								sqlContractEmbargo;  
	private ExpressionContext 								contractExpressionContext;
	private SQLContractLeaveLoader 							leaveLoader;
	
	private Date 											contractStartDate;
	private Date 											contractEndDate;
	private Collection<IContractCost> 						systemCosts;
	private Collection<IContractDeduction> 					systemDeductions;
	private Collection<IContractPayment> 					systemPayments;
	
	private SQLCnae2009										cnae2009;
	private LRUCache<Integer, ICalendar> 					calendars;
	private SQLCalendarFactory 								calendarFactory; 
	private LRUCache<Integer, Collection<IContractPayment>> agreementPayments;
	private SQLAgreementPaymentsFactory 					agreementPaymentsFactory;
	private LRUCache<Integer, ExpressionContext> 			agreementExpressionContexts;
	private SQLAgreementContextFactory 						agreementContextFactory ;
	
	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate) 
	throws SQLException, ExpressionException {
		this(connection, startDate, endDate, Calendar.getInstance().getTime(), null, SalaryType.SALARY);
	}
	
	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate) 
	throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null, SalaryType.SALARY);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate, Criteria criteria)
	throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria, SalaryType.SALARY);
	}
	
	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate, Criteria criteria, SalaryType salaryType) 
	throws SQLException, ExpressionException {
		this.connection = connection;
		
		this.startDate = new Date ( DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH).getTime() );
		this.endDate = new Date ( DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH).getTime() );
		this.issueDate = new Date ( issueDate.getTime() );
		
		this.criteria = criteria;
		
		this.salaryType = salaryType;
		
		initResultSet();
		initPaymentStmt();
		initDeductionStmt();
		initBonusStmt();
		initEmbargoStmt();
		initCeventStmt();
		initLeaveStmt();
		initSystemCosts();
		initSystemDeductions();
		initSystemPayments();

		this.sqlContractPayment = 
			new SQLContractPayment();
		this.sqlContractDeduction = 
			new SQLContractDeduction();
		this.sqlContractBonus = 
			new SQLContractBonus();
		this.sqlContractEmbargo = 
			new SQLContractEmbargo();
		
		this.cnae2009 = 
			new SQLCnae2009(connection, this.startDate, this.endDate);
		
		calendarFactory = 
			new SQLCalendarFactory(connection, this.startDate, this.endDate);
		this.calendars = 
			new LRUCache<Integer, ICalendar>(CACHE_SIZE, calendarFactory);
		calendarFactory.setCache(calendars); // TODO: Todo en la misma clase???
		
		agreementPaymentsFactory =
			new SQLAgreementPaymentsFactory(connection, this.startDate, this.endDate);
		this.agreementPayments = 
			new LRUCache<Integer, Collection<IContractPayment>>(CACHE_SIZE, agreementPaymentsFactory);

		agreementContextFactory =
			new SQLAgreementContextFactory(connection, this.startDate, this.endDate);
		this.agreementExpressionContexts = 
			new LRUCache<Integer, ExpressionContext>(CACHE_SIZE, agreementContextFactory);
		this.leaveLoader = 
			new SQLContractLeaveLoader(this.startDate, this.endDate);

	}
	
	@Override
	public SalaryType getSalaryType() {
		return this.salaryType;
	}

	@Override
	public Date getIssueDate() {
		return this.issueDate;
	}

	@Override
	public Date getStartDate() {
		return this.contractStartDate;
	}

	@Override
	public Date getEndDate() {
		return this.contractEndDate;
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
		return getString(SQLConstants.ENTERPRISE_CCC ,EnterpriseCccColumns.CCC);
	}

	@Override
	public String getEnterpriseName() {
		String name = getString(ENTERPRISE_REGISTRY,RegistryColumns.NAME);
		if ( ! StringUtils.isEmpty(name) ) {
			return name;
		}
		else {
			return StringUtils.EMPTY;
		}
	}

	@Override
	public String getEnterpriseAddress() {
		/* TODO Añadir la tabla y columnas a las constantes.		*/

		String streetType = getString("raddress","street_type");
		String address = getString("raddress","address");
		String number = getString("raddress","number");
		String address2 = getString("raddress","address2");
		String address3 = getString("raddress","address3");
		
    	StringBuffer buf = new StringBuffer();
    	buf.append(streetType==null?EMPTY:streetType);
    	buf.append(streetType==null?EMPTY:DOT);
    	buf.append(streetType==null?EMPTY:SPACE);
    	buf.append(StringUtils.isEmpty(address)?EMPTY:address);
    	buf.append(StringUtils.isEmpty(number)?EMPTY:SPACE);
    	buf.append(StringUtils.isEmpty(number)?EMPTY:number);
    	buf.append(StringUtils.isEmpty(address2)?EMPTY:COMMA);
    	buf.append(StringUtils.isEmpty(address2)?EMPTY:SPACE);
    	buf.append(StringUtils.isEmpty(address2)?EMPTY:address2);
    	buf.append(StringUtils.isEmpty(address3)?EMPTY:SPACE);
    	buf.append(StringUtils.isEmpty(address3)?EMPTY:OPEN_BRACKET);
    	buf.append(StringUtils.isEmpty(address3)?EMPTY:address3);
    	buf.append(StringUtils.isEmpty(address3)?EMPTY:CLOSE_BRACKET);
    	return StringUtils.abbreviate(buf.toString(), 64); // Avoid truncate
	}

	@Override
	public String getEnterpriseDocument() {
		return getString(ENTERPRISE_REGISTRY,RegistryColumns.DOCUMENT);
	}
	
	@Override
	public SSRegimeType getSSRegime() {
		int ordinal =  getInt(SQLConstants.CONTRACT,ContractColumns.SS_REGIME); // 'ss_regime' is NOT NULL
		return SSRegimeType.values()[ordinal];
	}

	@Override
	public String getCategory() {
		return contractExpressionContext.getVariable(ContractVariables.CATEGORY, startDate, endDate, String.class);
	}

	@Override
	public String getQuoteGroup() {
		return contractExpressionContext.getVariable(ContractVariables.QUOTE_GROUP, startDate, endDate, String.class);
	}
	
	
	@Override
	public String getEmployeeName() {
		String name = getString(SQLConstants.PERSON , PersonColumns.NAME);
		String firstSurname = getString(SQLConstants.PERSON , PersonColumns.FIRST_SURNAME);
		String secondSurname = getString(SQLConstants.PERSON , PersonColumns.SECOND_SURNAME);
		
		StringBuffer employeeName = new StringBuffer();
		
		if (!StringUtils.isEmpty(firstSurname)){
			employeeName.append(firstSurname);
		}
		if (!StringUtils.isEmpty(secondSurname)){
			employeeName.append(SPACE);
			employeeName.append(secondSurname);
		}
		if (!StringUtils.isEmpty(name)){
			employeeName.append(COMMA);
			employeeName.append(SPACE);
			employeeName.append(name);
		}
			
		return employeeName.toString();
	}

	@Override
	public String getEmployeeDocument() {
		return getString(PERSON_REGISTRY,RegistryColumns.DOCUMENT);
	}

	@Override
	public String getSocialSecurityNumber() {
		return getString(SQLConstants.PERSON , PersonColumns.SOCIAL_SECURITY_NUM);
	}

	@Override
	public Integer getRegistration() {
		return getInt(SQLConstants.CONTRACT,ContractColumns.REGISTRATION);
	}

	@Override
	public Date getSeniorityDate() {
		return getDate(SQLConstants.CONTRACT,ContractColumns.SENIORITY_DATE);
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
			return new HierarchyPayments(
					this.sqlContractPayment, 
					getAgreementPayments().iterator(),
					this.systemPayments.iterator());
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
			HierarchyDeductions hierarchyDeductions = 
				new HierarchyDeductions( 
						this.sqlContractDeduction,
						this.systemDeductions.iterator());
			return hierarchyDeductions;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		try {
			
			this.sqlContractEmbargo.close();
			int id = getId();
			embargoStmt.setInt(1,id);
			ResultSet rs = embargoStmt.executeQuery();
			this.sqlContractEmbargo.setResultSet(rs);
			return this.sqlContractEmbargo;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	@Override
	public Collection<IContractCost> getContractCosts()
			throws AonException {
		return this.systemCosts;
	}
	
	@Override
	public Collection<IContractBonus> getContractBonus()
	throws AonException {
		try {
			this.sqlContractBonus.close();
			int id = getId();
			bonusStmt.setInt(1,id);
			ResultSet rs = bonusStmt.executeQuery();
			this.sqlContractBonus.setResultSet(rs);
			return sqlContractBonus;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	public int getId() {
		return getInt(SQLConstants.CONTRACT, ContractColumns.ID);
	}
	
	public Integer getCnae2009() {
		Object cna2009 = getObject(SQLConstants.ENTERPRISE_ACTIVITY, 
				EnterpriseActivityColumns.CNAE2009);
		return cna2009 != null ? ( Integer ) cna2009 : null; 
	}

	public boolean next() throws SQLException, ExpressionException{
		
		boolean next =  this.resultSet.next();
		if ( next ) {
			initContractExpressionCtx();
		}
		else {
			close();
		}
		return next;
	}
	
	
	public void close() throws SQLException {
		if ( this.resultSet != null ) {
			this.resultSet.close();
			this.resultSet = null;
		}
		if ( this.ceventStmt != null ) {
			this.ceventStmt.close();
			this.ceventStmt = null;
		}
		if ( this.cleaveStmt != null ) {
			this.cleaveStmt.close();
			this.cleaveStmt = null;
		}
		if ( this.paymentStmt != null ) {
			this.paymentStmt.close();
			this.paymentStmt = null;
		}
		if ( this.deductionStmt != null ) {
			this.deductionStmt.close();
			this.deductionStmt = null;
		}
		if ( this.bonusStmt != null ) {
			this.bonusStmt.close();
			this.bonusStmt = null;
		}
		if ( this.embargoStmt != null ) {
			this.embargoStmt.close();
			this.embargoStmt = null;
		}
		if ( this.agreementExpressionContexts != null ){
			this.agreementExpressionContexts.clear();
			this.agreementExpressionContexts = null;
		}
		if ( this.agreementContextFactory != null ) {
			this.agreementContextFactory.close();
			this.agreementContextFactory = null;
		}
		if ( this.agreementExpressionContexts != null ){
			this.agreementExpressionContexts.clear();
			this.agreementExpressionContexts = null;
		}
		if ( this.agreementPaymentsFactory != null ) {
			this.agreementPaymentsFactory.close();
			this.agreementPaymentsFactory = null;
		}
		if ( this.calendars != null ){
			this.calendars.clear();
			this.calendars = null;
		}
		if ( this.calendarFactory != null ) {
			this.calendarFactory.close();
			this.calendarFactory = null;
		}
		if ( this.systemDeductions != null ) {
			this.systemDeductions.clear();
			this.systemDeductions = null;
		}
		if ( this.systemPayments != null ) {
			this.systemPayments.clear();
			this.systemPayments = null;
		}
		if ( this.systemCosts != null ) {
			this.systemCosts.clear();
			this.systemCosts = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
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
		stmt.setDate(1, toSqlDate( this.endDate ) );
		stmt.setDate(2, toSqlDate( this.startDate ) );
		resultSet = stmt.executeQuery();
	}
	
	private void initPaymentStmt()
	throws SQLException {
		String paymentSql = PAYMENT_SQL + PAYMENTS_FILTER[salaryType.ordinal()];
		this.paymentStmt  = 
			this.connection.prepareStatement(paymentSql);
		this.paymentStmt.setDate(2, toSqlDate( this.endDate ) );
		this.paymentStmt.setDate(3, toSqlDate( this.startDate ) );
	}

	private void initDeductionStmt()
	throws SQLException {
		this.deductionStmt  = 
			this.connection.prepareStatement(DEDUCTION_SQL);
		this.deductionStmt.setDate(2, toSqlDate( this.endDate) );
		this.deductionStmt.setDate(3, toSqlDate( this.startDate) );
	}
	
	private void initBonusStmt()
	throws SQLException {
		this.bonusStmt  = 
			this.connection.prepareStatement(BONUS_SQL);
		this.bonusStmt.setDate(2, toSqlDate( this.endDate) );
		this.bonusStmt.setDate(3, toSqlDate( this.startDate) );
	}
	

	private void initEmbargoStmt()
	throws SQLException {
		this.embargoStmt  = 
			this.connection.prepareStatement(EMBARGO_SQL);
		this.embargoStmt.setDate(2, toSqlDate( this.endDate) );
		this.embargoStmt.setDate(3, toSqlDate( this.startDate) );
	}

	private void initCeventStmt()
	throws SQLException {
		this.ceventStmt  = 
			this.connection.prepareStatement(CDATA_SQL);
		this.ceventStmt.setDate(2, toSqlDate( this.endDate) );
		this.ceventStmt.setDate(3, toSqlDate( this.startDate) );
	}
	
	private void initLeaveStmt()
	throws SQLException {
		this.cleaveStmt  = 
			this.connection.prepareStatement(CLEAVE_SQL);
		this.cleaveStmt.setDate(2, toSqlDate( this.endDate) );
		this.cleaveStmt.setDate(3, toSqlDate( this.startDate) );
	}


	private  Integer getAgreementLevel() {
		Object value = getObject(SQLConstants.AGREEMENT_LEVEL_CATEGORY, AgreementLevelCategoryColumns.AGREEMENT_LEVEL);
		return value == null ? null : ( Integer ) value ;
	}
	
	private ExpressionContext getAgreementContext()
	throws SQLException, ExpressionException {
		Integer agreementLevelId = 
			getAgreementLevel();
		return  agreementExpressionContexts.get(agreementLevelId);
	}

	private Collection<IContractPayment> getAgreementPayments()
	throws SQLException, ExpressionException {
		Integer agreementLevelId = 
			getAgreementLevel();
		return agreementPayments.get(agreementLevelId);
	}
	
	/*
	 * Devuelve el <code>ICalendar</code> asociado con el contrato (trabajador), 
	 * si no tiene calendario propio devuelve el de su centro de trabajo o el 
	 * del sistema ( calendario estatal ) si el centro tampoco tiene calendario
	 * propio. 
	 */
	private ICalendar getCalendar() {
		Object calendarId = getObject(SQLConstants.CONTRACT ,ContractColumns.CALENDAR);
		if ( calendarId == null ){
			calendarId = getObject(SQLConstants.WORKPLACE ,WorkplaceColumns.CALENDAR);
		}
		return calendars.get((Integer) calendarId); // (Integer) null devuelve  null, perfecto. 
	}
	
	private long getAvailableDays(Date start, Date end) {
		long workedDays = 
			CommonUtil.getDaysBetweenDates(start,end);
		workedDays += 1;
		return workedDays;
	}
	
	private boolean isActualDay( DayType type ) {
		return type == DayType.WORKING_DAY || 
			type == DayType.CONTINUOUS_TIME ||
			type == DayType.OTHER; // TODO: Estos tipos de dias son un cachondeo ¿ OTHER, CONTINUOUS_TIME ?
	}
	
	private boolean isHoliday(Calendar day) {
		Date date = day.getTime();
		Object holidays = 
			this.contractExpressionContext.getVariable(HOLIDAYS, date, date, Object.class);
		return holidays != null ;
	}
	
	private boolean isSaturday(Calendar day) {
		return day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}
	
	
	/* 
	 * Calcula los 'DIAS_EFECTIVOS' para el contrato (trabajador), 
	 * según su calendario y/o bajas. 
	 */
	private double getActualDays() {
		long days = 0;

		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(contractEndDate);
		Calendar day = Calendar.getInstance();
		day.setTime(contractStartDate);
		while  (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			if ( isActualDay(type) &&
					!leaveLoader.isLeaveDay(day) &&
					!isHoliday(day) ) {
				days++;
			} 
			day.add(Calendar.DATE, 1);
		}
		return days;
	}

	
	/* 
	 */
	private double getWorkDays(Period p) {

		Long availableDays = 
			getAvailableDays(p.getStart(), p.getEnd());
		Long leaveDays = leaveLoader.getLeavesDays();  
		
		Long workedDays = availableDays - leaveDays; 
		
		return workedDays;
	}
	
	/* 
	 * Calcula los 'DIAS_TRABAJADOS' (DIAS_MES - DIAS_BAJA). 
	 */
	private double getSalaryDays() {

		Long availableDays = 
			getAvailableDays(contractStartDate, contractEndDate);
		
		return availableDays;
	}
	
	
	private double getSalaryHours() {
		Number weekHours = getVariable(WEEK_HOURS, Number.class);
		if ( weekHours == null ) {
			weekHours = 40.00;
		}
		Double salaryDays = getVariable(SALARY_DAYS, Double.class );
		
		return salaryDays == null ? null : Math.ceil(salaryDays * weekHours.doubleValue() / 7); // TODO : ¿ Se redondean las horas hacia arriba ?  
	}
	
	private boolean isIndefinite() {
		String tc2 = getVariable(TC2, String.class);
		return tc2 == null ? true : "123".indexOf(tc2.charAt(0)) != -1; 
	}

	private boolean isFullTime() {
		String tc2 = getVariable(TC2, String.class);
		return tc2 == null ? true : "14".indexOf(tc2.charAt(0)) != -1; 
	}

	private boolean isShortContract() {
		Date endDate = getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
		if ( endDate == null ) {
			return false;
		}
		Date startDate = getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE);
		long naturalDays = 
			CommonUtil.getDaysBetweenDates(startDate,endDate) + 1;
		if ( naturalDays < 7 ) {
			return true;
		}
		return false;
	}

	private boolean isAssimilatted() {
		Object object = getObject(SQLConstants.ENTERPRISE_CCC, EnterpriseCccColumns.TYPE);
		if ( object == null ){
			return false;
		}
		Integer ordinal = (Integer ) object;
		return ordinal == CCCType.ASSIMILATEDS.ordinal();
	}
	
	private Double getItRate() {
		Integer cnae2009 = getCnae2009();
		return cnae2009 != null ? this.cnae2009.getItRate(cnae2009) : 0.00;
	}
	
	private Double getImsRate() {
		Integer cnae2009 = getCnae2009();
		return cnae2009 != null ? this.cnae2009.getImsRate(cnae2009) : 0.00;
	}

	private double getDoubleVariable(String name) {
		Double value =  this.contractExpressionContext.getVariable(name, 
				this.contractStartDate, this.contractEndDate, Double.class);
		return value != null ? value : 0.00;
	}

	private <T> T getVariable(ContractVariables var, Class<T> toType ) {
		return getVariable(var.getName(), toType);
	}

	private <T> T getVariable(String name, Class<T> toType ) {
		return this.contractExpressionContext.getVariable(name, 
				this.contractStartDate, this.contractEndDate, toType);
	}

	private boolean containsVariable(Object name) {
		return this.contractExpressionContext.containsVariable(name, 
				this.contractStartDate, this.contractEndDate);
	}

	private double getTotalBenefitsIt() {
		double totalBenefitsIt = 0.00;
		
		totalBenefitsIt += getDoubleVariable("ECEMP");
		totalBenefitsIt += getDoubleVariable("ECSS");
		totalBenefitsIt += getDoubleVariable("ATEP");
		
		return totalBenefitsIt;
	}
	
	private double getGuarenteed() {
		double guarenteed = 0.00;
		
		Set<String> vars = 
			contractExpressionContext.variablesSet();
		for (String var : vars) {
			if ( var.endsWith("_GARANTIZADO")) {
				guarenteed += getDoubleVariable(var);
			}
		}
		
		return guarenteed;
	}

	/*
	 * Inicializa el contexto dentro del cual se calcularán ejecutarán las
	 * percepciones y deducciones de trabajador.
	 */
	private void initContractExpressionCtx() 
	throws SQLException, ExpressionException  {
		
		this.contractStartDate = 
			Period.max(getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE), startDate);
		this.contractEndDate = 
			Period.min(getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE), endDate);
		
		if ( this.contractExpressionContext != null ){
			this.contractExpressionContext  = null;
		}
		
		this.contractExpressionContext = 
			new ExpressionContext(getAgreementContext());
		

		ActiveTimedVariable<Double> workedDays =  new ActiveTimedVariable<Double>(){
			@Override
			public Double getValue(Period p) {
				return getWorkDays(p);
			}
		};
		
		ActiveTimedVariable<Double> bonusDays =  new ActiveTimedVariable<Double>(){
			@Override
			public Double getValue(Period p) {
				return ( double )getAvailableDays(p.getStart(), p.getEnd());
			}
		};
		
		this.contractExpressionContext.addVariable(WORKED_DAYS, 
				workedDays
		);

		this.contractExpressionContext.addVariable(QUOTE_DAYS, 
				workedDays
		);

		this.contractExpressionContext.addVariable(SALARY_DAYS, 
				new LazyTimedVariable<Double>(){
					@Override
					public Double create(){
						return getSalaryDays();
					}
				}
		);

		this.contractExpressionContext.addVariable(SALARY_HOURS, 
				new LazyTimedVariable<Double>(){
					@Override
					public Double create() {
						return getSalaryHours();
					}
				}
		);
		this.contractExpressionContext.addVariable(BONUS_DAYS, 
				bonusDays
		);
		
		this.contractExpressionContext.addVariable(INDEFINITE, 
				new LazyTimedVariable<Boolean>(){
					@Override
					public Boolean create() {
						return isIndefinite();
					}
				}
		);

		this.contractExpressionContext.addVariable(FULL_TIME, 
				new LazyTimedVariable<Boolean>(){
					@Override
					public Boolean create() {
						return isFullTime();
					}
				}
		);

		this.contractExpressionContext.addVariable(ASSIMILATED, 
				new LazyTimedVariable<Boolean>(){
					@Override
					public Boolean create() {
						return isAssimilatted();
					}
				}
		);

		this.contractExpressionContext.addVariable(MORE_THAN_65, 
				new LazyTimedVariable<Boolean>(){
					@Override
					public Boolean create() {
						return false;
					}
				}
		);

		this.contractExpressionContext.addVariable(SHORT_CONTRACT, 
				new LazyTimedVariable<Boolean>(){
					@Override
					public Boolean create() {
						return isShortContract();
					}
				}
		);

		this.contractExpressionContext.addVariable(IT_RATE, 
				new LazyTimedVariable<Double>(){
					@Override
					public Double create() {
						return getItRate();
					}
				}
		);
		
		this.contractExpressionContext.addVariable(IMS_RATE, 
				new LazyTimedVariable<Double>(){
					@Override
					public Double create() {
						return getImsRate();
					}
				}
		);

		loadContractLeave(this.contractExpressionContext);
		loadContractData(this.contractExpressionContext);
		
		if ( ! containsVariable(ACTUAL_DAYS )) {
			// Los 'DIAS_EFECTIVOS' son pesados de calcular ( necesitan de querys adicionales...)
			this.contractExpressionContext.addVariable(ACTUAL_DAYS, 
					new LazyTimedVariable<Double>(){
						@Override
						public Double create() {
							return getActualDays();
						}
					}
			);
		}

		
		if ( leaveLoader.getLeavesDays() == 0 ) {
			return;
		} // Si no hay bajas... 
			
		this.contractExpressionContext.addVariable(LEAVE_DAYS, 
			leaveLoader.getLeavesDays(), this.startDate, this.endDate );
		
		
		long guarenteedDays = leaveLoader.getCommonDiseaseDays() + 
			leaveLoader.getProfessionalDiseaseDays();
		
		if ( guarenteedDays == 0  ) {
			return ;
		} // No hay bajas por enfermedad común y/o profesional.
		
		double guaranteed = getGuarenteed();
		
		if ( guaranteed == 0.00 ) {
			return;
		}

		this.contractExpressionContext.addVariable(GUARANTEED_DAYS, 
				guarenteedDays, this.startDate, this.endDate );

		this.contractExpressionContext.addVariable(GUARANTEED, 
				guaranteed, this.startDate, this.endDate );

		this.contractExpressionContext.addVariable(TOTAL_BENEFITS_IT, 
				new LazyTimedVariable<Double>(){
					@Override
					public Double create() {
						return getTotalBenefitsIt();
					}
				}
		);
	}
	
	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresión ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
	 */
	private void loadContractData(ExpressionContext ctx ) throws SQLException{
		ResultSet rs = null;
		try{ 
			ceventStmt.setInt(1, getId());
			rs = ceventStmt.executeQuery();
			while ( rs.next() ) {
				ExpressionImpl expr = 
					new ExpressionImpl();
				expr.setName(rs.getString(ContractDataColumns.NAME));
				expr.setExpression(rs.getString(ContractDataColumns.EXPRESSION));
				expr.setScope(ExpressionScope.CONTRACT );
				Date start = Period.max(rs.getDate(ContractDataColumns.START_DATE), contractStartDate);
				Date end = Period.min(rs.getDate(ContractDataColumns.END_DATE), contractEndDate);
				try {
					ctx.addExpression(expr, start, end );
				} catch (Exception e) {
					//TODO: ¿ Que hacemos con esta excepcion ? 
				}
			}
			
		}finally {
			if ( rs != null ){
				rs.close();
			}
		}
	}
	
	private void loadContractLeave(ExpressionContext ctx ) throws SQLException{
		ResultSet rs = null;
		try{ 
			cleaveStmt.setInt(1, getId());
			rs = cleaveStmt.executeQuery();
			leaveLoader.loadContractLevae(rs, ctx);
		}finally {
			if ( rs != null ){
				rs.close();
			}
		}
	}

	private void initSystemCosts() throws SQLException {
		ResultSet rs = null ;
		PreparedStatement stmt= null ;
		try {
			stmt = 
				connection.prepareStatement(SYSTEM_COST_SQL);
			java.sql.Date sqlEndDate = 
				new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = 
				new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			rs = stmt.executeQuery();
			systemCosts = SQLCollections.costsCollection(rs);
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}

	private void initSystemDeductions() throws SQLException {
		ResultSet rs = null ;
		PreparedStatement stmt= null ;
		try {
			stmt = 
				connection.prepareStatement(SYSTEM_DEDUCTION_SQL);
			java.sql.Date sqlEndDate = 
				new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = 
				new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			rs = stmt.executeQuery();
			systemDeductions = SQLCollections.deductionsCollection(rs);
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}
	
	private void initSystemPayments() throws SQLException {
		ResultSet rs = null ;
		PreparedStatement stmt= null ;
		try {
			stmt = 
				connection.prepareStatement(SYSTEM_PAYMENT_SQL);
			java.sql.Date sqlEndDate = 
				new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = 
				new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			rs = stmt.executeQuery();
			systemPayments = SQLCollections.paymentsCollection(rs);
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}

	private Object getObject(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getObject(tableLabel +"."+columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Integer getInt(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getInt(tableLabel +"."+columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Date getDate(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getDate(tableLabel +"."+columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private String getString(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getString(tableLabel +"."+columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime()); 
	}

}
