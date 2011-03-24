package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.WORKED_DAYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
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
import com.esferalia.aon.salary.expression.Period;

public class SQLContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext{
	
	public static final String PERSON_REGISTRY = "person_registry";
	public static final String ENTERPRISE_REGISTRY = "enterprise_registry";
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";
	
	private static final String MAIN_SQL = "SELECT * "
		+" FROM contract"
		+" LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
		+" LEFT JOIN agreement_level_category ON (contract.agreement_level_category = agreement_level_category.id)"
		+", person"
		+", registry AS " + PERSON_REGISTRY
		+", workplace"
		+", enterprise"
		+", registry AS " + ENTERPRISE_REGISTRY
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
		"SELECT * "
		+" FROM contract_payment"
		+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
		+"	ON payment_concept = payment_concept.id"	
		+" WHERE contract = ? "
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )"
		+" AND salary_type IN ("+SalaryType.SALARY.ordinal()+" ,"+SalaryType.EXTRA.ordinal()+")";

	private static final String DEDUCTION_SQL =
		"SELECT *"
		+" FROM contract_deduction"
		+" LEFT JOIN  deduction_concept" 							// LEFT JOIN: deduction_concept puede ser NULL
		+"	ON deduction_concept = deduction_concept.id"	
		+" WHERE contract = ? "
		+" AND start_date <= ? "
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
		+"( SELECT sum(DATEDIFF(end_date,start_date))"
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
	private abstract class LazyTimedObject<V> 
		implements ITimedObject<V> 
	{
		protected Period period =
			new Period(startDate, endDate);
		
		@Override
		public Period getPeriod() {
			return period;
		}
	}
	
	/**
	 * 'DIAS_EFECTIVOS'.
	 * 
	 * @author rtrepiana
	 *
	 */
	private class ActualDays extends LazyTimedObject<Long> {
		Long actual_days = null;
		
		@Override
		public Long getValue() {
			if ( actual_days == null ){
				actual_days = getActualDays();
			}
			return actual_days;
		}
	}
		

	private Criteria 										criteria;
	private Connection 										connection;

	private Date 											issueDate;
	private Date 											startDate;
	private Date 											endDate;
	
	private ResultSet 										resultSet;  
	private PreparedStatement 								ceventStmt;
	private PreparedStatement 								cleaveStmt;
	private PreparedStatement 								paymentStmt;
	private PreparedStatement 								deductionStmt;

	private SQLContractPayment 								sqlContractPayment;  
	private SQLContractDeduction 							sqlContractDeduction;  
	private ExpressionContext 								contractExpressionContext;
	private SQLContractLeaveLoader 							leaveLoader;
	
	private Collection<IContractDeduction> 					systemDeductions;
	private Collection<IContractPayment> 					systemPayments;
	
	private LRUCache<Integer, ICalendar> 					calendars;
	private LRUCache<Integer, Collection<IContractPayment>> agreementPayments;
	private LRUCache<Integer, ExpressionContext> 			agreementExpressionContexts;
	
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
		this.startDate = new Date ( startDate.getTime() );
		this.endDate = new Date ( endDate.getTime() );
		this.issueDate = new Date ( issueDate.getTime() );
		this.criteria = criteria;
		
		initResultSet();
		initPaymentStmt();
		initDeductionStmt();
		initCeventStmt();
		initLeaveStmt();
		initSystemDeductions();
		initSystemPayments();

		this.sqlContractPayment = 
			new SQLContractPayment();
		this.sqlContractDeduction = 
			new SQLContractDeduction();
		
		SQLCalendarFactory calendarFactory = 
			new SQLCalendarFactory(connection, startDate, endDate);
		this.calendars = 
			new LRUCache<Integer, ICalendar>(CACHE_SIZE, calendarFactory);
		calendarFactory.setCache(calendars); // TODO: Todo en la misma clase???
		
		SQLAgreementPaymentsFactory agreementPaymentsFactory =
			new SQLAgreementPaymentsFactory(connection, startDate, endDate);
		this.agreementPayments = 
			new LRUCache<Integer, Collection<IContractPayment>>(CACHE_SIZE, agreementPaymentsFactory);

		SQLAgreementContextFactory agreementContextFactory =
			new SQLAgreementContextFactory(connection, startDate, endDate);
		this.agreementExpressionContexts = 
			new LRUCache<Integer, ExpressionContext>(CACHE_SIZE, agreementContextFactory);
		this.leaveLoader = 
			new SQLContractLeaveLoader(this.startDate, this.endDate);

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
		// TODO Añadir la tabla y columnas a las constantes.
		String streetType = getString("raddress","street_type");
		String address = getString("raddress","address");
		Integer number = getInt("raddress","number");
		String address2 = getString("raddress","address2");
		String address3 = getString("raddress","address3");
		
    	StringBuffer buf = new StringBuffer();
    	buf.append(streetType==null?EMPTY:streetType);
    	buf.append(streetType==null?EMPTY:DOT);
    	buf.append(streetType==null?EMPTY:SPACE);
    	buf.append(StringUtils.isEmpty(address)?EMPTY:address);
    	buf.append(number==null?EMPTY:SPACE);
    	buf.append(number==null?EMPTY:number);
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
			return new HierarchyPayments(this.sqlContractPayment, 
					getAgreementPayments().iterator(),
					this.systemPayments.iterator() );
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
			HierarchyDeductions hierarchyDeductions = new HierarchyDeductions( this.sqlContractDeduction, this.systemDeductions.iterator());
			return hierarchyDeductions;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	
	public int getId() {
		return getInt(SQLConstants.CONTRACT, ContractColumns.ID);
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
		stmt.setDate(1, toSqlDate( this.endDate ) );
		stmt.setDate(2, toSqlDate( this.startDate ) );
		resultSet = stmt.executeQuery();
	}
	
	private void initPaymentStmt()
	throws SQLException {
		this.paymentStmt  = 
			this.connection.prepareStatement(PAYMENT_SQL);
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
	
	private long getAvailableDays() {
		Date dbStart = getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE);
		Date start = Period.max ( this.startDate, dbStart );
		Date dbEnd = getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
		Date end = Period.min ( this.endDate, dbEnd);
		long workedDays = 
			CommonUtil.getDaysBetweenDates(start, 
					end);
		workedDays += 1;
		return workedDays;
	}
	
	private boolean isActualDay( DayType type ) {
		return type == DayType.WORKING_DAY || 
			type == DayType.CONTINUOUS_TIME ||
			type == DayType.OTHER; // TODO: Estos tipos de dias son un cachondeo ¿ OTHER, CONTINUOUS_TIME ?
	}
	
	/* 
	 * Calcula los 'DIAS_EFECTIVOS' para el contrato (trabajador), 
	 * según su calendario y/o bajas. 
	 */
	// TODO : ¿ Y las vaciones ?
	private long getActualDays() {
		long days = 0;

		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while  (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			if ( isActualDay(type) &&  
					!leaveLoader.isLeaveDay(day) ) {
				days++;
			} 
			day.add(Calendar.DATE, 1);
		}
		return days;
	}

	/*
	 * Inicializa el contexto dentro del cual se calcularán ejecutarán las
	 * percepciones y deducciones de trabajador.
	 */
	private void initContractExpressionCtx() 
	throws SQLException, ExpressionException  {
		this.contractExpressionContext = 
			new ExpressionContext(getAgreementContext());
		

		loadContractData(contractExpressionContext);
		loadContractLeave(contractExpressionContext);
		
		Long leaveDays = leaveLoader.getLeavesDays();  
		Long availableDays = getAvailableDays();
		Long workedDays = availableDays - leaveDays; 
		
		this.contractExpressionContext.addVariable(WORKED_DAYS, workedDays, startDate, endDate);
		this.contractExpressionContext.addVariable(LEAVE_DAYS, leaveDays, startDate, endDate);
		
		// Los 'DIAS_EFECTIVOS' son pesados de calcular ( necesitan de querys adicionales...)
		this.contractExpressionContext.addVariable(ACTUAL_DAYS, new ActualDays() );
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
				Date start = Period.max ( rs.getDate(ContractDataColumns.START_DATE), startDate );
				Date end = Period.min( rs.getDate(ContractDataColumns.END_DATE), endDate );
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
