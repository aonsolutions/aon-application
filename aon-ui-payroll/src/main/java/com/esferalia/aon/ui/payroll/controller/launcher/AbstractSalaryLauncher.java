/********************************************************************
* Copyright (c) 2011, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

package com.esferalia.aon.ui.payroll.controller.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester.UnExpectedValue;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CustomerColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.launcher.ListSalaryBuilderListener.LogMessage;

public abstract class AbstractSalaryLauncher 
	implements SalaryTypeVisitor<ISQLContractSalaryCalculatorContext>{
	
	private boolean saveLog;
	private boolean pollEnabled;
	private boolean debugEnabled;
	private boolean refreshEnabled;

	private SalaryLauncherParams params;
	
	protected ListSalaryBuilderListener listener;
	
	private Connection connection;
	private Criteria criteria;
	
	public void onStart(ActionEvent event) {
		setParams(null);
		setListener(null);
		setSaveLog(false);
		setDebugEnabled(false);
		setPollEnabled(false);
	}

	public final boolean isSaveLog() {
		return saveLog;
	}
	
	public final void setSaveLog(boolean saveLog) {
		this.saveLog = saveLog;
	}

	public final boolean isDebugEnabled() {
		return debugEnabled;
	}
	public final void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	
	public final boolean isRefreshEnabled() {
		return refreshEnabled;
	}
	public final void setRefreshEnabled(boolean refreshEnabled) {
		this.refreshEnabled = refreshEnabled;
	}

	public final boolean isPollEnabled() {
		return pollEnabled;
	}
	
	public final void setPollEnabled(boolean pollEnabled) {
		this.pollEnabled = pollEnabled;
	}

	public final SalaryLauncherParams getParams() {
		if (params == null) {
			params = new SalaryLauncherParams();
		} // end-if : Lazy init. 
		return params;
	}
	public final  void setParams(SalaryLauncherParams params) {
		this.params = params;
	}
	
	public void setListener(ListSalaryBuilderListener listener) {
		this.listener = listener;
	}
	
	public void clearMessages() {
		setListener(null);
	}

	public List<LogMessage> getMessages() {
		return listener != null ? listener.getList() : null;
	}
	

	public boolean isEmptyLog() {
		if ( listener == null ) {
			return true;
		}
		File file = listener.getFile();
		if ( file == null ) {
			return true;
		}
		
		return file.canRead();
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
	
	public void onClear(ActionEvent event) {
		clearMessages();
	}
	
	public void onExecute(ActionEvent event) {
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = null;
		try {
			session = HibernateUtil.getSession(sessionName);
			connection = session.connection();

			buildCriteria();

			setPollEnabled(true);
			TestThread thread =  
				new TestThread();
			thread.start();
		} finally {
			if (HibernateUtil.mustCloseSession()) {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
					}
				}
				HibernateUtil.closeSession(sessionName);
			}
		}
	}
	
	protected Connection getConnection(){
		return connection;
	}
	
	
	protected void calculate ( ContractSalaryCalculator calculator ) 
		throws SalaryException, ExpressionException, SQLException {
		try {
			ISQLContractSalaryCalculatorContext sqlCtx = 
				getSQLContractSalaryCalculatorContext();
			
			while ( sqlCtx.next() ) {
				try {
					calculator.calculate(sqlCtx);
				} catch (UnExpectedValue e) {
					listener.onError(e.getLocalizedMessage());
				} catch ( SalaryException e ) {
					listener.onError(e.getLocalizedMessage());
				}
			}
		}
		catch ( SQLWrapperException e ) {
			throw e.getSQLException();
		}
		catch (ExpressionWrapperException e) {
			throw e.getExpressionException();
		}
	}
	
	protected abstract void execute(SalaryLauncherParams parameters) throws SalaryException ;
	
	private class TestThread extends Thread {

	    public void run() {
			Date startTime = new Date();
			try {
				execute(getParams());
				String msg = "Proceso Finalizado correctamente.";
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			} catch (Throwable e) {
				listener.onError(e.getLocalizedMessage());
				String msg = "Se produjeron errores en el calculo de nóminas.";
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.ERROR, msg));
			}
			if (listener.getWarningCounter() > 0) {
				String msg = "Se produjeron " + listener.getWarningCounter() + " mesajes de aviso.";
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.WARNING, msg));
			}
			
			if (listener.getErrorCounter() > 0) {
				String msg = "Se produjeron " + listener.getErrorCounter() + " mesajes de error.";
				listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.ERROR, msg));
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
			listener.onMessage(new LogMessage(SalaryBuilderListenerLevel.INFO, msg));
			setPollEnabled(false);
			
	    }

	}

	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext() 
	throws ExpressionException, SQLException {
		SalaryType  salaryType = params.getSalaryType();
		return salaryType.accept(this);
	}
	
	protected Criteria getCriteria() {
		return criteria;
	}
	protected Criteria buildCriteria() {
		criteria = new Criteria();
		
		Expression customerActive = 
			ExpressionUtilities.getEqualExpression(SQLConstants.CUSTOMER + "." + CustomerColumns.STATUS, 
					CustomerStatus.ACTIVE.ordinal());
		Expression customerUnknown = 
			ExpressionUtilities.getNullExpression((SQLConstants.CUSTOMER + "." + CustomerColumns.STATUS));
		
		criteria.addExpression(ExpressionUtilities.getOrExpression(customerActive, customerUnknown));
		
		if (params.getPerson() != null && params.getPerson().getId() != null ) {
			criteria.addEqualExpression(
					SQLContractSalaryCalculatorContext.PERSON_REGISTRY + "." + RegistryColumns.ID, 
					params.getPerson().getId());
		}

		if ( DomainManager.isDomainManagementAvailable() ){
			if(!getParams().getEnterpriseFilter().getIncludedList().isEmpty()){
				List<Integer> selectedList = new LinkedList<Integer>();
				for(Enterprise enterprise: getParams().getEnterpriseFilter().getIncludedList()){
					selectedList.add(enterprise.getDomain());
				}
				criteria.setSkipDomainFilter( true );
				criteria.addInExpression(SQLConstants.CONTRACT + "." + RegistryColumns.DOMAIN, selectedList);
			} else {
				criteria.addNullExpression(SQLConstants.CONTRACT + "." + RegistryColumns.DOMAIN);
			}
		} else {
			criteria.addEqualExpression(
					SQLConstants.CONTRACT + "." + RegistryColumns.DOMAIN, 
					DomainManager.getCurrentDomain());
		}
		
		return criteria;
	}

	protected OrderByList getOrderByList() {
		return ISQLContractSalaryCalculatorContext.NEWER;
	}

	@Override
	public ISQLContractSalaryCalculatorContext visitNotEnjoyedVacations(
			SalaryType salaryType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ISQLContractSalaryCalculatorContext visitSalary(SalaryType salaryType) {
		
		Criteria criteria = getCriteria();
		
		OrderByList orderByList = getOrderByList();
		
		Date startDate = params.getStartDate();  
		Date endDate = params.getEndDate(); 
		Connection connection = getConnection();
		
		try {
			SQLContractSalaryCalculatorContext sqlCtx = 
				new SQLContractSalaryCalculatorContext(connection, 
					startDate, 
					endDate,
					endDate,
					criteria ,
					orderByList);
			return sqlCtx;
		} catch (SQLException e) {
			throw new SQLWrapperException(e);
		} catch (ExpressionException e) {
			throw new ExpressionWrapperException(e);
		}
	}
	
	@Override
	public ISQLContractSalaryCalculatorContext visitDelay(SalaryType salaryType) {
		Criteria criteria = getCriteria();
		
		Date startDate = params.getStartDate();  
		Date endDate = params.getEndDate(); 
		Connection connection = getConnection();
		
		try {
			ISQLContractSalaryCalculatorContext sqlCtx = 
				new SQLContractDelayCalculatorContext(connection, 
						startDate, 
						endDate, 
						endDate, 
						endDate, 
						criteria);
			return sqlCtx;
		} catch (SQLException e) {
			throw new SQLWrapperException(e);
		} catch ( ExpressionException e ) {
			throw new ExpressionWrapperException(e);
		}

	}

	@Override
	public ISQLContractSalaryCalculatorContext visitExtra(SalaryType salaryType) {
		
		Criteria criteria = getCriteria();
		
		int year = params.getIssueYear();
		Month month = params.getIssueMonth();
		
		Connection connection = getConnection();
		
		
		try {
			SQLExtraSalaryCalculatorContext sqlCtx = 
				new SQLExtraSalaryCalculatorContext(connection, 
						year, 
						month, 
						null, 
						null, 
						criteria);
			return sqlCtx;
		} catch (SQLException e) {
			throw new SQLWrapperException(e);
		} 
	}
	
	@Override
	public ISQLContractSalaryCalculatorContext visitSettle(SalaryType salaryType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected class SQLWrapperException extends RuntimeException {
		
		public SQLWrapperException(SQLException e){
			super(e);
		}
		
		public SQLException getSQLException() {
			return (SQLException) getCause();
		}
	}

	protected class ExpressionWrapperException extends RuntimeException {
		
		public ExpressionWrapperException(ExpressionException e){
			super(e);
		}
		
		public ExpressionException getExpressionException() {
			return (ExpressionException) getCause();
		}
	}
	
}
