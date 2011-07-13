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
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester.UnExpectedValue;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class SalaryLauncher {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SalaryLauncher.class);
	
	private static final String LOG_FORMAT = "[{0}] {1}, {2} : {3}";
	
	private SalaryLauncherParams params;
	private ListSalaryBuilderListener listener;
	
	private boolean pollEnabled;
	
	private boolean saveLog;
	private boolean debugEnabled;
	private boolean refreshEnabled;
	
	private boolean showLauncherConfirmWindow;
	private List<ITransferObject> existingSalaries;
	
	public List<ITransferObject> getExistingSalaries() {
		return existingSalaries;
	}

	public void setExistingSalaries(List<ITransferObject> existingSalaries) {
		this.existingSalaries = existingSalaries;
	}
	
	public boolean isExistSalariesToOverride(){
		return !getExistingSalaries().isEmpty();
	}
	
	public Integer getExistingSalariesSize() {
		return getExistingSalaries().size();
	}
	
	public boolean isShowLauncherConfirmWindow() {
		return showLauncherConfirmWindow;
	}

	public void setShowLauncherConfirmWindow(boolean showLauncherConfirmWindow) {
		this.showLauncherConfirmWindow = showLauncherConfirmWindow;
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
	}
	
	public String getBeanName() {
		return IPayrollConstants.SALARY_TEST_LAUNCHER_NAME;
	}
	
	public void onExecute(ActionEvent event) {
		if(getParams().getSalaryType()==SalaryType.SALARY){
			if(isExistSalariesToOverride()){
				try {
					removeSalaryes();
				} catch (ManagerBeanException e) {
					String msg = "No se han podido borrar las nominas existentes";
					LOGGER.error(msg);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			pollEnabled = true;
			(new TestThread()).start();
		} else {
			String msg = "No implementado";
			AonUtil.addInfoMessage(msg);
		}
	}
	
	private void removeSalaryes() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		for(ITransferObject to: getExistingSalaries()){
			bean.remove(to);
		}
	}

	public void onShowLauncherConfirm(ActionEvent event) {
		searchExistingSalaries();
	}
	
	private void searchExistingSalaries() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), getParams().getSalaryType());
			if(getParams().getPerson()!=null && getParams().getPerson().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_PERSON_ID), getParams().getPerson().getId());
			}
			if(getParams().getEnterprise()!=null && getParams().getEnterprise().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID), getParams().getEnterprise().getId());
			}
			if(getParams().getStartDate()!=null){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_START_DATE), getParams().getStartDate());
			}
			if(getParams().getEndDate()!=null){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), getParams().getEndDate());
			}
			setExistingSalaries(bean.getList(criteria));
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido comprobar la existencia de nominas en este periodo";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private class TestThread extends Thread {

	    public void run() {
			Date startTime = new Date();
			try {
				execute(getParams());
				String msg = "Proceso Finalizado correctamente.";
				listener.onInfo(msg);
			} catch (Throwable e) {
				listener.onError(e.getLocalizedMessage());
				String msg = "Se produjeron errores en el c�lculo de n�minas.";
				listener.onError(msg);
			}
			if (listener.getWarningCounter() > 0) {
				String msg = "Se produjeron " + listener.getWarningCounter() + " mesajes de aviso.";
				listener.onInfo(msg);	
			}
			
			if (listener.getErrorCounter() > 0) {
				String msg = "Se produjeron " + listener.getErrorCounter() + " mesajes de error.";
				listener.onInfo(msg);	
			}
			
			Date endTime = new Date();
			long milis = endTime.getTime() - startTime.getTime();
			
			long hora = milis/3600000;
			long restohora = milis%3600000;
			long minuto = restohora/60000;
			long restominuto = restohora%60000;
			long segundo = restominuto/1000;
			long restosegundo = restominuto%1000;
			listener.onInfo("Tiempo de proceso: " + 
						(hora>0?""+hora + " hora"+(hora==1?"":"s"):"")
						+(minuto>0?" "+minuto + " minuto"+(minuto==1?"":"s"):"")
						+(segundo>0?" "+segundo + " segundo"+(segundo==1?"":"s"):"")
						+" " + restosegundo + " milisegundos.");				
			pollEnabled = false;
	    }

		private void execute(SalaryLauncherParams parameters) throws SalaryException {
			try {
	
				String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
				Connection connection = HibernateUtil.getSQLConnection(sessionFactory);
				SQLSalaryBuilder salaryBuilder = new SQLSalaryBuilder(connection);
				listener = new ListSalaryBuilderListener();
				listener.setDebugEnabled(isDebugEnabled());
				listener.setSaveLog(isSaveLog());
				salaryBuilder.setListener(listener);
				ContractSalaryCalculator calculator = new ContractSalaryCalculator();
				calculator.setSalaryBuilder(salaryBuilder);
				
				Date startDate = parameters.getStartDate();  
				Date endDate = parameters.getEndDate(); 
				
				String msg = MessageFormat.format("Test de c�lculo de n�minas {0}:{1}",new Object[] {startDate, endDate});
				listener.onInfo(msg);
				
				Criteria criteria = null;
				if (parameters.getEnterprise() != null && parameters.getEnterprise().getId() != null ) {
					criteria = new Criteria();
					criteria.addEqualExpression("enterprise_registry.id", parameters.getEnterprise().getId());
				}
				if (parameters.getPerson() != null && parameters.getPerson().getId() != null ) {
					criteria = criteria==null?new Criteria():criteria;
					criteria.addEqualExpression("person_registry.id", parameters.getPerson().getId());
				}
				SQLContractSalaryCalculatorContext sqlCtx = 
					new SQLContractSalaryCalculatorContext(connection, 
							startDate, 
							endDate,
							Calendar.getInstance().getTime(),
							criteria );
				salaryBuilder.begin();
				while ( sqlCtx.next() ) {
					try {
						calculator.calculate(sqlCtx);
					} catch (UnExpectedValue e) {
						msg = MessageFormat.format(LOG_FORMAT,
								new Object[]{
								sqlCtx.getEmployeeDocument(),
								sqlCtx.getEnterpriseName(),
								sqlCtx.getEmployeeName(),
								e.getLocalizedMessage()});
						listener.onError(msg);
					} catch ( SalaryException e ) {
						msg = MessageFormat.format(LOG_FORMAT,
								new Object[]{
								sqlCtx.getEmployeeDocument(),
								sqlCtx.getEnterpriseName(),
								sqlCtx.getEmployeeName(),
								e.getLocalizedMessage()});
						listener.onError(msg);
					}
				}
				try {
					salaryBuilder.commit();
				} catch (Throwable e) {
					listener.onError(e.getLocalizedMessage());
					salaryBuilder.rollback();
				}
				msg = MessageFormat.format("Total n�minas insertadas: {0} ",new Object[]{salaryBuilder.getInsertedSalaries()});
				listener.onInfo(msg);
			} catch (ExpressionException e) {
				throw new SalaryException(e);
			} catch (SQLException e) {
				throw new SalaryException(e);
			}
		}
	}

	public List<String> getMessages() {
		if (listener == null) return null;
		return listener.getList();
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
	
}
