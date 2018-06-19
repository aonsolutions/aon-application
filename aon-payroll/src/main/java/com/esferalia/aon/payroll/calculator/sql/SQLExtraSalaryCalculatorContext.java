package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.sql.SQLConstants.AGREEMENT_EXTRA;
import static com.esferalia.aon.payroll.sql.SQLConstants.AGREEMENT_LEVEL;
import static com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns.AGREEMENT;
import static com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns.END_DATE;
import static com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns.ISSUE_DATE;
import static com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns.START_DATE;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.Agreement;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

public class SQLExtraSalaryCalculatorContext implements
		ISQLContractSalaryCalculatorContext {

	private static final String EXTRAS_SQL_FORMAT = "SELECT * "
		+ " FROM " + AGREEMENT_EXTRA 
		+ " WHERE " + AgreementExtraColumns.ID + " = %d"
		;
	
	private ResultSet rs;
	private Statement stmt;

	private int year;
	private int extra;
	private Date endDate;
	private Date chargeDate;
	private Criteria criteria;
	private Connection connection ;
	private ISQLContractSalaryCalculatorContext ctx;
	
	public SQLExtraSalaryCalculatorContext(Connection connection,
			int extra,
			int year,
			Date chargeDate, 
			Criteria criteria) 
	throws SQLException {
		this(connection, extra, year, null, chargeDate, criteria);
	}
	
	public SQLExtraSalaryCalculatorContext(Connection connection,
			int extra,
			int year,
			Date endDate, 
			Date chargeDate, 
			Criteria criteria) 
	throws SQLException {

		this.extra = extra;
		this.year = year;
		this.endDate = endDate;
		this.criteria = criteria;
		this.connection = connection;
		this.chargeDate = chargeDate;

		initExtrasResultSet();
	}
	
	
	@Override
	public void close() throws SQLException {
		if (this.ctx != null)
			this.ctx.close();
		if ( this.rs != null ) 
			this.rs.close();
		if ( this.stmt != null )
			this.stmt.close();
	}
	
	@Override
	public boolean next() throws SQLException, ExpressionException {
		while ( ctx == null || !this.ctx.next() ) {
			if ( ! nextContractSalaryCalculatorContext() ) {
				return false;
			}
		}
		return true;
	}

	private void initExtrasResultSet() throws SQLException{
		String sql = 
			String.format(EXTRAS_SQL_FORMAT, extra);
		this.stmt = connection.createStatement();
		this.rs = stmt.executeQuery(sql);
	}
	
	private boolean nextContractSalaryCalculatorContext() throws SQLException, ExpressionException {
		if ( this.ctx != null ) {
			this.ctx.close();
			this.ctx = null;
		}
		if ( !this.rs.next() ) {
			return false;
		}
		

		Date extraStartDate = 
			AgreementExtra.parseAgreementDate(this.rs.getString(START_DATE), this.year);
		Date extraEndDate  = AgreementExtra.parseAgreementDate(this.rs.getString(END_DATE), this.year);
		Date extraIssueDate  = AgreementExtra.parseAgreementDate(this.rs.getString(ISSUE_DATE), this.year);
		
		if ( extraStartDate.after(extraEndDate)) {
			return nextContractSalaryCalculatorContext();
		}
		
		Criteria agreementCriteria = new Criteria();
		agreementCriteria.addExpression(this.criteria.getExpression());
		agreementCriteria.addEqualExpression(
						AGREEMENT_LEVEL + "." + AGREEMENT, 
						rs.getInt(AGREEMENT));
		
		this.ctx = new SQLContractExtraCalculatorContext(
				this.connection, 
				extraStartDate, 
				extraEndDate, 
				extraIssueDate, 
				chargeDate, 
				agreementCriteria) {
			@Override
			protected Date getContractEndDate() {
				return Period.min(endDate, super.getContractEndDate());
			}
		}; 
		
		return true;
	}
	
	
	//-------------------------------------------
	// Delegate methods
	//-------------------------------------------
	
	@Override
	public int getId() {
		return ctx.getId();
	}
	
	@Override
	public Date getIrpfDate() {
		return ctx.getIrpfDate();
	}
	
	public Date getChargeDate() {
		return ctx.getChargeDate();
	}

	public Date getIssueDate() {
		return ctx.getIssueDate();
	}

	public Date getStartDate() {
		return ctx.getStartDate();
	}

	public Date getEndDate() {
		return ctx.getEndDate();
	}


	public ISalaryProxy getSalaryProxy() {
		return ctx.getSalaryProxy();
	}

	public ExpressionContext getExpressionContext() {
		return ctx.getExpressionContext();
	}

	public SalaryType getSalaryType() {
		return ctx.getSalaryType();
	}

	public String getCcc() {
		return ctx.getCcc();
	}

	public String getEnterpriseName() {
		return ctx.getEnterpriseName();
	}

	public String getEnterpriseAddress() {
		return ctx.getEnterpriseAddress();
	}

	public String getEnterpriseDocument() {
		return ctx.getEnterpriseDocument();
	}

	public SSRegimeType getSSRegime() {
		return ctx.getSSRegime();
	}

	public String getCategory() {
		return ctx.getCategory();
	}

	public String getQuoteGroup() {
		return ctx.getQuoteGroup();
	}

	public String getEmployeeName() {
		return ctx.getEmployeeName();
	}

	public String getEmployeeDocument() {
		return ctx.getEmployeeDocument();
	}

	public String getSocialSecurityNumber() {
		return ctx.getSocialSecurityNumber();
	}

	public Integer getRegistration() {
		return ctx.getRegistration();
	}

	public Date getSeniorityDate() {
		return ctx.getSeniorityDate();
	}
	
	@Override
	public Collection<IContractPayment> getAgreementPayments() {
		return ctx.getAgreementPayments();
	}

	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return ctx.getContractPayments();
	}

	public Collection<IContractCost> getContractCosts() throws AonException {
		return ctx.getContractCosts();
	}

	public Collection<IContractBonus> getContractBonus() throws AonException {
		return ctx.getContractBonus();
	}

	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return ctx.getContractEmbargos();
	}

	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return ctx.getContractDeductions();
	}

	public ExpressionContext getSystemExpressionContext() {
		return ctx.getSystemExpressionContext();
	}

	public ExpressionContext getImplicitExpressionContext() {
		return ctx.getImplicitExpressionContext();
	}

	public ExpressionContext getAgreementExpressionContext() {
		return ctx.getAgreementExpressionContext();
	}
	
	@Override
	public Integer getInt(String table, String column) {
		return ctx.getInt(table, column);
	}
	
	@Override
	public Date getDate(String table, String column) {
		return ctx.getDate(table, column);
	}
	
	@Override
	public String getString(String table, String column) {
		return ctx.getString(table, column);
	}
	
	@Override
	public Object getObject(String table, String column) {
		return ctx.getObject(table, column);
	}
	
	@Override
	public IListener getListener() {
		return ctx.getListener();
	}
	
	@Override
	public void setListener(IListener listener) {
		ctx.setListener(listener);
	}

	@Override
	public double getIrpf() {
		return ctx.getIrpf();
	}
	
	@Override
	public Object liquid(double liquid, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		return ctx.liquid(liquid, start, end);
	}
	
	@Override
	public Connection getConnection() {
		return ctx.getConnection();
	}
	
	
	@Override
	public ISQLContractSalaryCalculatorContext getNoItContractSalaryCalculatorContext() {
		return ctx.getNoItContractSalaryCalculatorContext();
	}
	
	// ------------------------------------------------------------------------
	
	
	
	// ------------------------------------------------------------------------
	
	private static Month getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return Month.getMonthByValue(calendar.get(Calendar.MONTH));
	}

	private static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
}
