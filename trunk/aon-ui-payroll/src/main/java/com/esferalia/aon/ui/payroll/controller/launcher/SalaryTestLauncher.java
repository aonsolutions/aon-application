package com.esferalia.aon.ui.payroll.controller.launcher;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.launcher.AbstractSalaryLauncher.SQLWrapperException;
import com.esferalia.aon.ui.payroll.controller.launcher.ListSalaryBuilderListener.LogMessage;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryTestLauncher extends AbstractSalaryLauncher {

	
	private boolean testEnterpriseCost;
	private boolean testTotalLiquid;
	private boolean testBaseIRPF;
	private boolean testBaseCGC;
	
	private Integer contractId;
	private Date startDate;
	private Date endDate;
	private LogMessage message;
	
	
	public void setMessage(LogMessage message) {
		this.message = message;
	}
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}	
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public boolean isTestEnterpriseCost() {
		return testEnterpriseCost;
	}
	public void setTestEnterpriseCost(boolean testEnterpriseCost) {
		this.testEnterpriseCost = testEnterpriseCost;
	}
	public boolean isTestTotalLiquid() {
		return testTotalLiquid;
	}
	public void setTestTotalLiquid(boolean testTotalLiquid) {
		this.testTotalLiquid = testTotalLiquid;
	}

	public boolean isTestBaseIRPF() {
		return testBaseIRPF;
	}
	public void setTestBaseIRPF(boolean testBaseIRPF) {
		this.testBaseIRPF = testBaseIRPF;
	}

	public boolean isTestBaseCGC() {
		return testBaseCGC;
	}

	public void setTestBaseCGC(boolean testBaseCGC) {
		this.testBaseCGC = testBaseCGC;
	}

	
	public void onStart(ActionEvent event) {
		super.onStart(event);
		setTestBaseCGC(true);
		setTestTotalLiquid(true);
		setTestBaseIRPF(true);
		setTestEnterpriseCost(false);
	}
	
	public String getBeanName() {
		return IPayrollConstants.SALARY_TEST_LAUNCHER_NAME;
	}
	
	public void onLaunch(ActionEvent event) {
		onExecute(event);
	}
	
	public void onSalaryDraft(ActionEvent event) {
		try {
			SalaryDraftController controller = 
				(SalaryDraftController) FormUtil.getController("salaryDraft");
			controller.onEditSearch(event);
			
			
			SalaryType type = getParams().getSalaryType();
			
			controller.setSalaryType( type );
			
			Date startDate = type == SalaryType.EXTRA ? 
					message.getIssueDate() : message.getStartDate();
			Date endDate = message.getEndDate();
			
			controller.setYear(CommonUtil.getYear(startDate));
			controller.setMonth(Month.getMonthByValue(CommonUtil.getMonth(startDate)));
			controller.setToYear(CommonUtil.getYear(endDate));
			controller.setToMonth(Month.getMonthByValue(CommonUtil.getMonth(endDate)));
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					controller.getManagerBean().
					getFieldName(IEntityAlias.CONTRACT_ID), 
					message.getContractId() );
			
			
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction(
					IPayrollConstants.SALARY_TESTER_LAUNCHER_FORM);
		} catch (ManagerBeanException e) {
			String msg = "Error al el borrador de la nómina.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public List<SelectItem> getSalaryTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> salaryTypes = new LinkedList<SelectItem>();
		for( SalaryType salaryType : SalaryType.values() ) {
			if(salaryType!=SalaryType.NOT_ENJOYED_VACATIONS){
				String name = salaryType.getName(locale);
				SelectItem item = new SelectItem(salaryType, name);
				salaryTypes.add(item);			
			}
		}
		return salaryTypes;
	}

	@Override
	protected OrderByList getOrderByList() {
		return ISQLContractSalaryCalculatorContext.OLDER;
	}
	
	
	@Override
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext()
			throws ExpressionException, SQLException {
		ISQLContractSalaryCalculatorContext context = super.getSQLContractSalaryCalculatorContext();
		
		return new OverrideSQLContractSalaryCalculatorContext(context, testEnterpriseCost);
	}
	
	@Override
	protected void execute(SalaryLauncherParams parameters) throws SalaryException {
		Connection connection = null;
		SQLSalaryBuilderTester salaryBuilder = null;
		try {
			connection = getConnection();
			salaryBuilder = 
				new SQLSalaryBuilderTester(connection);
			
			salaryBuilder.setTestTotalLiquid(isTestTotalLiquid());
			salaryBuilder.setTestEnterpriseCost(isTestEnterpriseCost());
			listener = new ListSQLSalaryBuilderTesterListener(salaryBuilder);
			listener.setDebugEnabled(isDebugEnabled());
			listener.setSaveLog(isSaveLog());
			salaryBuilder.setListener(listener);
			ContractSalaryCalculator calculator = new ContractSalaryCalculator();
			calculator.setSalaryBuilder(salaryBuilder);
			
			Date startDate = parameters.getStartDate();  
			Date endDate = parameters.getEndDate(); 
			
			String msg = MessageFormat.format("Test de cálculo de nóminas {0}:{1}",new Object[] {startDate, endDate});
			listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			
			calculate(calculator);
			
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		} finally {
			if ( salaryBuilder != null ) {
				String msg = MessageFormat.format("Total contratos procesados: {0} ",new Object[]{salaryBuilder.getContractCount()});
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
				msg = MessageFormat.format("Total nóminas comparadas: {0} ",new Object[]{salaryBuilder.getSalaryCount()});
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
				msg = MessageFormat.format("Total nóminas chequeadas sin detectar problemas: {0} ",new Object[]{salaryBuilder.getRightTestedsalariesCount()});
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			}
		}
	}
	
	protected static class OverrideSQLContractSalaryCalculatorContext 
		extends DelegateSQLContractCalculatorContext {
		
		private boolean enterpriseCosts;
		
		public OverrideSQLContractSalaryCalculatorContext(
				ISQLContractSalaryCalculatorContext cxt, 
				boolean enterpriseCosts) {
			this.ctx = cxt;
			this.enterpriseCosts = enterpriseCosts;
		}
		

		public Collection<IContractCost> getContractCosts() throws AonException {
			if ( enterpriseCosts )
				return super.getContractCosts(); 
			else 
				return Collections.emptyList();
		}

		public Collection<IContractBonus> getContractBonus()
				throws AonException {
			if ( enterpriseCosts )
				return super.getContractBonus(); 
			else 
				return Collections.emptyList();
		}

		
	}
	
	
	@Override
	public ISQLContractSalaryCalculatorContext visitExtra(SalaryType salaryType) {
		Criteria criteria = getCriteria();
		
		Connection connection = getConnection();
		
		try {
			ISQLContractSalaryCalculatorContext sqlCtx = 
				new AllSQLContractExtraCalculatorContext(connection, 
					criteria );
			return sqlCtx;
		} catch (SQLException e) {
			throw new SQLWrapperException(e);
		} 

	}

	@Override
	public ISQLContractSalaryCalculatorContext visitDelay(SalaryType salaryType) {
		Criteria criteria = getCriteria();
		
		Connection connection = getConnection();
		
		try {
			ISQLContractSalaryCalculatorContext sqlCtx = 
				new AllSQLContractDelayCalculatorContext(connection, 
					criteria );
			return sqlCtx;
		} catch (SQLException e) {
			throw new SQLWrapperException(e);
		} 

	}
	
	protected abstract static class DelegateSQLContractCalculatorContext 
	implements ISQLContractSalaryCalculatorContext {
		
		protected ISQLContractSalaryCalculatorContext ctx;
		
		
		public DelegateSQLContractCalculatorContext() {
			this(null);
		}

		public DelegateSQLContractCalculatorContext(ISQLContractSalaryCalculatorContext ctx) {
			this.ctx = ctx;
		}
		
		
		@Override
		public Date getIrpfDate() {
			return ctx.getIrpfDate();
		}

		@Override
		public Date getChargeDate() {
			return ctx.getChargeDate();
		}

		@Override
		public Date getIssueDate() {
			return ctx.getIssueDate();
		}

		@Override
		public Date getStartDate() {
			return ctx.getStartDate();
		}

		@Override
		public Date getEndDate() {
			return ctx.getEndDate();
		}

		@Override
		public ISalaryProxy getSalaryProxy() {
			return ctx.getSalaryProxy();
		}

		@Override
		public ExpressionContext getExpressionContext() {
			return ctx.getExpressionContext();
		}

		@Override
		public void close() throws SQLException {
			ctx.close();
		}

		@Override
		public SalaryType getSalaryType() {
			return ctx.getSalaryType();
		}

		@Override
		public boolean next() throws SQLException, ExpressionException {
			return ctx.next();
		}

		@Override
		public String getCcc() {
			return ctx.getCcc();
		}

		@Override
		public String getEnterpriseName() {
			return ctx.getEnterpriseName();
		}

		@Override
		public String getEnterpriseAddress() {
			return ctx.getEnterpriseAddress();
		}

		@Override
		public String getEnterpriseDocument() {
			return ctx.getEnterpriseDocument();
		}

		@Override
		public SSRegimeType getSSRegime() {
			return ctx.getSSRegime();
		}

		@Override
		public String getCategory() {
			return ctx.getCategory();
		}

		@Override
		public String getQuoteGroup() {
			return ctx.getQuoteGroup();
		}

		@Override
		public String getEmployeeName() {
			return ctx.getEmployeeName();
		}

		@Override
		public String getEmployeeDocument() {
			return ctx.getEmployeeDocument();
		}

		@Override
		public String getSocialSecurityNumber() {
			return ctx.getSocialSecurityNumber();
		}

		@Override
		public Integer getRegistration() {
			return ctx.getRegistration();
		}

		@Override
		public Date getSeniorityDate() {
			return ctx.getSeniorityDate();
		}

		@Override
		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			return ctx.getContractPayments();
		}

		@Override
		public Collection<IContractCost> getContractCosts() throws AonException {
			return ctx.getContractCosts();
		}

		@Override
		public Collection<IContractBonus> getContractBonus()
				throws AonException {
			return ctx.getContractBonus();
		}

		@Override
		public Collection<IContractEmbargo> getContractEmbargos()
				throws AonException {
			return ctx.getContractEmbargos();
		}

		@Override
		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			return ctx.getContractDeductions();
		}
		
		
	}
	
	private static class AllSQLContractDelayCalculatorContext 
			extends DelegateSQLContractCalculatorContext {
		
		private Criteria criteria;
		private Connection connection;
	
		
		private ResultSet delaysRs ;
		private PreparedStatement delaysStmt;
		
	
		public AllSQLContractDelayCalculatorContext(
				Connection connection, 
				Criteria criteria) throws SQLException {
	
			this.connection = connection;
			this.criteria= criteria;
			initDelayRs();
			ctx = nextCtx();
		}
		
		
		@Override
		public void close() throws SQLException {
			ctx.close();
			if ( delaysStmt != null ) {
				delaysStmt.close();
			}
			if ( delaysRs != null ) {
				delaysRs.close();
			}
		}
	
		@Override
		public boolean next() throws SQLException, ExpressionException {
			if ( ctx == null ){
				return false;
			}
			if ( ctx.next() ){
				return true;
			}
			ctx = nextCtx();
			return next();
		}
		
		private void initDelayRs() throws SQLException {
	
			String sql = "SELECT * "  
					+" FROM salary AS delay"
					+" WHERE type = ?"
					+ " AND start_date >= (SELECT start_date"
					+" FROM salary"
					+" WHERE type= ?"
					+" AND salary.contract = delay.contract"
					+" ORDER BY start_date"
					+" LIMIT 1 )";
			
			delaysStmt = connection.prepareStatement(sql);
			delaysStmt.setInt(1, SalaryType.DELAY.ordinal());
			delaysStmt.setInt(2, SalaryType.SALARY.ordinal());
			
			delaysRs = delaysStmt.executeQuery();
				
				
		}
		
		private ISQLContractSalaryCalculatorContext nextCtx() {
			try {
				
				if ( !delaysRs.next() ) {
					return null;
				}
				
				int contract = delaysRs.getInt(SalaryColumns.CONTRACT);
				Date startDate = delaysRs.getDate(SalaryColumns.START_DATE);
				Date endDate = delaysRs.getDate(SalaryColumns.END_DATE);
	
				Criteria contractCriteria = new Criteria();
				contractCriteria.addEqualExpression(
						SQLConstants.CONTRACT + "." + ContractColumns.ID, 
						contract);
				contractCriteria.addExpression(criteria.getExpression());
				
				
				
				return new SQLContractDelayCalculatorContext(connection, startDate, endDate, endDate, endDate, contractCriteria){
					@Override
					public Date getIrpfDate() {
						Date chargeDate = getChargeDate();
						return add(chargeDate, Calendar.DATE, 1 );
					}
					
				};
				
			} catch (ExpressionException e) {
				return nextCtx(); 
			}
			catch ( SQLException e ) {
				return null;
			}
		}
		
		private static Date add(Date date, int field, int amount ) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(field, 1);
			return calendar.getTime();
		}
	}

	private static class AllSQLContractExtraCalculatorContext 
	extends DelegateSQLContractCalculatorContext {

		private Criteria 			criteria;
		private Connection 			connection;
		
		
		private ResultSet 			extraRs ;
		private PreparedStatement 	extrasStmt;
		
		
		public AllSQLContractExtraCalculatorContext(
				Connection connection, 
				Criteria criteria) throws SQLException {
		
			this.connection = connection;
			this.criteria= criteria;
			initDelayRs();
			ctx = nextCtx();
		}
		
		
		@Override
		public void close() throws SQLException {
			ctx.close();
			if ( extrasStmt != null ) {
				extrasStmt.close();
			}
			if ( extraRs != null ) {
				extraRs.close();
			}
		}
		
		@Override
		public boolean next() throws SQLException, ExpressionException {
			if ( ctx == null ){
				return false;
			}
			if ( ctx.next() ){
				return true;
			}
			ctx = nextCtx();
			return next();
		}
		
		private void initDelayRs() throws SQLException {
		
			String sql = "SELECT * "  
					+" FROM salary AS delay"
					+" WHERE type = ?"
					;
			
			extrasStmt = connection.prepareStatement(sql);
			extrasStmt.setInt(1, SalaryType.EXTRA.ordinal());
			
			extraRs = extrasStmt.executeQuery();
				
				
		}
		
		private ISQLContractSalaryCalculatorContext nextCtx() {
			try {
				
				if ( !extraRs.next() ) {
					return null;
				}
				
				int contract = extraRs.getInt(SalaryColumns.CONTRACT);
				Date startDate = extraRs.getDate(SalaryColumns.START_DATE);
				Date endDate = extraRs.getDate(SalaryColumns.END_DATE);
				Date issueDate = extraRs.getDate(SalaryColumns.ISSUE_DATE);
				Date chargeDate = extraRs.getDate(SalaryColumns.CHARGE_DATE);
						
				Criteria contractCriteria = new Criteria();
				contractCriteria.addEqualExpression(
						SQLConstants.CONTRACT + "." + ContractColumns.ID, 
						contract);
				contractCriteria.addExpression(criteria.getExpression());
				
				
				
				return new SQLContractExtraCalculatorContext(connection, startDate, endDate, issueDate, chargeDate, contractCriteria);
				
			} catch (ExpressionException e) {
				return nextCtx(); 
			}
			catch ( SQLException e ) {
				return null;
			}
		}
		
	}

}
