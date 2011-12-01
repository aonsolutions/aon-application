package com.esferalia.aon.ui.payroll.controller.launcher;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
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
			int year = getParams().getIssueYear();
			Month month = getParams().getIssueMonth();
			controller.setYear(year);
			controller.setMonth(month);
			controller.setSalaryType(
					getParams().getSalaryType());

			controller.setStartDate(message.getStartDate());
			controller.setEndDate(message.getEndDate());
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					controller.getManagerBean().
					getFieldName(IPayrollAlias.CONTRACT_ID), 
					getContractId());
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
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext()
			throws ExpressionException, SQLException {
		ISQLContractSalaryCalculatorContext context = super.getSQLContractSalaryCalculatorContext();
		return new DelegateSQLContractSalaryCalculatorContext(context, testEnterpriseCost);
	}
	
	@Override
	protected void execute(SalaryLauncherParams parameters) throws SalaryException {
		Connection connection = null;
		try {
			connection = getConnection();
			SQLSalaryBuilderTester salaryBuilder = 
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
			
			
			msg = MessageFormat.format("Total contratos procesados: {0} ",new Object[]{salaryBuilder.getContractCount()});
			listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			msg = MessageFormat.format("Total nóminas comparadas: {0} ",new Object[]{salaryBuilder.getSalaryCount()});
			listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			msg = MessageFormat.format("Total nóminas chequeadas sin detectar problemas: {0} ",new Object[]{salaryBuilder.getRightTestedsalariesCount()});
			listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		} finally {
			
		}
	}
	
	protected static class DelegateSQLContractSalaryCalculatorContext 
		implements ISQLContractSalaryCalculatorContext{
		
		private ISQLContractSalaryCalculatorContext context;
		private boolean 							enterpriseCosts;
		
		public DelegateSQLContractSalaryCalculatorContext(
				ISQLContractSalaryCalculatorContext context, 
				boolean enterpriseCosts) {
			this.context = context;
			this.enterpriseCosts = enterpriseCosts;
		}
		
		@Override
		public Date getIrpfDate() {
			return context.getChargeDate();
		}

		public Date getChargeDate() {
			return context.getChargeDate();
		}

		public Date getIssueDate() {
			return context.getIssueDate();
		}

		public Date getStartDate() {
			return context.getStartDate();
		}

		public Date getEndDate() {
			return context.getEndDate();
		}

		public void close() throws SQLException {
			context.close();
		}

		public ISalaryProxy getSalaryProxy() {
			return context.getSalaryProxy();
		}

		public boolean next() throws SQLException, ExpressionException {
			return context.next();
		}

		public ExpressionContext getExpressionContext() {
			return context.getExpressionContext();
		}

		public SalaryType getSalaryType() {
			return context.getSalaryType();
		}

		public String getCcc() {
			return context.getCcc();
		}

		public String getEnterpriseName() {
			return context.getEnterpriseName();
		}

		public String getEnterpriseAddress() {
			return context.getEnterpriseAddress();
		}

		public String getEnterpriseDocument() {
			return context.getEnterpriseDocument();
		}

		public SSRegimeType getSSRegime() {
			return context.getSSRegime();
		}

		public String getCategory() {
			return context.getCategory();
		}

		public String getQuoteGroup() {
			return context.getQuoteGroup();
		}

		public String getEmployeeName() {
			return context.getEmployeeName();
		}

		public String getEmployeeDocument() {
			return context.getEmployeeDocument();
		}

		public String getSocialSecurityNumber() {
			return context.getSocialSecurityNumber();
		}

		public Integer getRegistration() {
			return context.getRegistration();
		}

		public Date getSeniorityDate() {
			return context.getSeniorityDate();
		}

		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			return context.getContractPayments();
		}

		public Collection<IContractCost> getContractCosts() throws AonException {
			if ( enterpriseCosts )
				return context.getContractCosts(); 
			else 
				return Collections.emptyList();
		}

		public Collection<IContractBonus> getContractBonus()
				throws AonException {
			if ( enterpriseCosts )
				return context.getContractBonus(); 
			else 
				return Collections.emptyList();
		}

		public Collection<IContractEmbargo> getContractEmbargos()
				throws AonException {
			return context.getContractEmbargos();
		}

		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			return context.getContractDeductions();
		}
	
		
	}
}
