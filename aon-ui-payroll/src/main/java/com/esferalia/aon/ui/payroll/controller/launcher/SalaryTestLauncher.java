package com.esferalia.aon.ui.payroll.controller.launcher;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester.UnExpectedValue;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CustomerColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.launcher.ListSQLSalaryBuilderTesterListener.TestLogMessage;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryTestLauncher {
	private static final String LOG_FORMAT = "[{0}] {1}, {2} : {3}";
	
	private SalaryLauncherParams params;
	private ListSQLSalaryBuilderTesterListener listener;
	
	private boolean pollEnabled;
	
	private boolean saveLog;
	private boolean debugEnabled;
	private boolean refreshEnabled;
	private boolean testTotalPayment;
	private boolean testBaseIRPF;
	private boolean testBaseCGC;
	private Integer contractId;
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}	
	
	public boolean isPollEnabled() {
		return pollEnabled;
	}
	
	public boolean isSaveLog() {
		return saveLog;
	}
	public void setSaveLog(boolean saveLog) {
		this.saveLog = saveLog;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	
	public boolean isRefreshEnabled() {
		return refreshEnabled;
	}
	public void setRefreshEnabled(boolean refreshEnabled) {
		this.refreshEnabled = refreshEnabled;
	}

	public boolean isTestTotalPayment() {
		return testTotalPayment;
	}
	public void setTestTotalPayment(boolean testTotalPayment) {
		this.testTotalPayment = testTotalPayment;
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

	public SalaryLauncherParams getParams() {
		if (params == null) {
			params = new SalaryLauncherParams();
		}
		return params;
	}
	public void setParams(SalaryLauncherParams params) {
		this.params = params;
	}
	
	public void onStart(ActionEvent event) {
		setParams(null);
		listener = null;
		pollEnabled = false;
		setSaveLog(false);
		setDebugEnabled(false);
		setTestBaseCGC(true);
		setTestTotalPayment(true);
		setTestBaseIRPF(true);
	}
	
	public String getBeanName() {
		return IPayrollConstants.SALARY_TEST_LAUNCHER_NAME;
	}
	
	public void onExecute(ActionEvent event) {
		pollEnabled = true;
		Thread testThread = new TestThread(); 
		testThread.start();
	}
	
	private class TestThread extends Thread {

	    public void run() {
			Date startTime = new Date();
			try {
				execute(getParams());
				String msg = "Proceso Finalizado correctamente.";
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			} catch (Throwable e) {
				listener.onError(e.getLocalizedMessage());
				String msg = "Se produjeron errores en el cálculo de nóminas.";
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			}
			if (listener.getWarningCounter() > 0) {
				String msg = "Se produjeron " + listener.getWarningCounter() + " mesajes de aviso.";
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			}
			
			if (listener.getErrorCounter() > 0) {
				String msg = "Se produjeron " + listener.getErrorCounter() + " mesajes de error.";
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			}
			
			Date endTime = new Date();
			long milis = endTime.getTime() - startTime.getTime();
			
			long hora = milis/3600000;
			long restohora = milis%3600000;
			long minuto = restohora/60000;
			long restominuto = restohora%60000;
			long segundo = restominuto/1000;
			long restosegundo = restominuto%1000;
			String msg = "Tiempo de proceso: " + 
						(hora>0?""+hora + " hora"+(hora==1?"":"s"):"")
						+(minuto>0?" "+minuto + " minuto"+(minuto==1?"":"s"):"")
						+(segundo>0?" "+segundo + " segundo"+(segundo==1?"":"s"):"")
						+" " + restosegundo + " milisegundos.";				
			listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			pollEnabled = false;
	    }

		private void execute(SalaryLauncherParams parameters) throws SalaryException {
			try {
	
				String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
				Connection connection = HibernateUtil.getSQLConnection(sessionFactory);
				SQLSalaryBuilderTester salaryBuilder = new SQLSalaryBuilderTester(connection);
				salaryBuilder.setTestBaseCGC(isTestBaseCGC());
				salaryBuilder.setTestBaseIRPF(isTestBaseIRPF());
				salaryBuilder.setTestTotalPayment(isTestTotalPayment());
				listener = new ListSQLSalaryBuilderTesterListener(salaryBuilder);
				listener.setDebugEnabled(isDebugEnabled());
				listener.setSaveLog(isSaveLog());
				salaryBuilder.setListener(listener);
				ContractSalaryCalculator calculator = new ContractSalaryCalculator();
				calculator.setSalaryBuilder(salaryBuilder);
				
				Date startDate = parameters.getStartDate();  
				Date endDate = parameters.getEndDate(); 
				
				String msg = MessageFormat.format("Test de cálculo de nóminas {0}:{1}",new Object[] {startDate, endDate});
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
				
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(SQLConstants.CUSTOMER + "." + CustomerColumns.STATUS, 
						CustomerStatus.ACTIVE );
				if (parameters.getEnterprise() != null && parameters.getEnterprise().getId() != null ) {
					criteria.addEqualExpression(
							SQLContractSalaryCalculatorContext.ENTERPRISE_REGISTRY + "." + RegistryColumns.ID, 
							parameters.getEnterprise().getId());
				}
				if (parameters.getPerson() != null && parameters.getPerson().getId() != null ) {
					criteria.addEqualExpression(
							SQLContractSalaryCalculatorContext.PERSON_REGISTRY + "." + RegistryColumns.ID, 
							parameters.getPerson().getId());
				}

				SQLContractSalaryCalculatorContext sqlCtx = 
					new SQLContractSalaryCalculatorContext(connection, 
							startDate, 
							endDate,
							Calendar.getInstance().getTime(),
							criteria );
				while ( sqlCtx.next() ) {
					try {
						calculator.calculate(sqlCtx);
					} catch (UnExpectedValue e) {
						listener.onError(e.getLocalizedMessage());
					} catch ( SalaryException e ) {
						listener.onError(e.getLocalizedMessage());
					}
				}
				msg = MessageFormat.format("Total contratos procesados: {0} ",new Object[]{salaryBuilder.getContractCount()});
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
				msg = MessageFormat.format("Total nóminas comparadas: {0} ",new Object[]{salaryBuilder.getSalaryCount()});
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
				msg = MessageFormat.format("Total nóminas chequeadas sin detectar problemas: {0} ",new Object[]{salaryBuilder.getRightTestedsalariesCount()});
				listener.onInfo(new TestLogMessage(SalaryBuilderListenerLevel.INFO, msg));
			} catch (ExpressionException e) {
				throw new SalaryException(e);
			} catch (SQLException e) {
				throw new SalaryException(e);
			}
		}
	}

	public List<TestLogMessage> getMessages() {
		if (listener == null) return null;
		return listener.getTestList();
	}
	public String getStyle() {
		return listener.getStyle();
	}
	public boolean isEmptyLog() {
		File file = listener.getFile();
		return !(file != null && file.canRead());
	}
	
	public void downloadDisk(ActionEvent event) {
		try {
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			File file = listener.getFile();
			String fileName = file.getAbsolutePath();
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(file);
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			throw new AbortProcessingException("Imposible descargar fichero");
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
			controller.setCriteria(criteria);
			controller.onSearch(null);
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);
			controller.setBackAction(IPayrollConstants.SALARY_TESTER_LAUNCHER_FORM);
		} catch (ManagerBeanException e) {
			String msg = "Error al el borrador de la nómina.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	

}
