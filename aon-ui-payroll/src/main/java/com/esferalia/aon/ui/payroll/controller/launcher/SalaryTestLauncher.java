package com.esferalia.aon.ui.payroll.controller.launcher;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
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
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
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


	
	public void onSalaryDraft(ActionEvent event) {
		try {
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController("salaryDraft");
			controller.onEditSearch(event);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getParams().getIssueYear());
			c.set(Calendar.MONTH, getParams().getIssueMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			controller.setIssueDate(c.getTime());
			controller.setStartDate(getParams().getStartDate());
			controller.setEndDate(getParams().getEndDate());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IPayrollAlias.CONTRACT_ID), getContractId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction(IPayrollConstants.SALARY_TESTER_LAUNCHER_FORM);
		} catch (ManagerBeanException e) {
			String msg = "Error al el borrador de la nómina.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}
