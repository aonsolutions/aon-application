package com.esferalia.aon.ui.payroll.controller.launcher;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class SalaryLauncher extends AbstractSalaryLauncher{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SalaryLauncher.class);
	
	private static final String LOG_FORMAT = "[{0}] {1}, {2} : {3}";
	
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

	public String getBeanName() {
		return IPayrollConstants.SALARY_TEST_LAUNCHER_NAME;
	}
	
	@Override
	public void onExecute(ActionEvent event) {
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
		super.onExecute(event);
	}
	
	private void removeSalaryes() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		for(ITransferObject to: getExistingSalaries()){
			bean.remove(to);
		}
	}
	
	public void onLaunch(ActionEvent event) {
		onShowLauncherConfirm(event); 
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
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), getParams().getStartDate());
			}
			if(getParams().getEndDate()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), getParams().getEndDate());
			}
			setExistingSalaries(bean.getList(criteria));
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido comprobar la existencia de nominas en este periodo";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void saveSalary(SalaryLauncherParams parameters) throws SalaryException {
		setParams(parameters);
		execute(parameters);
	}
	
	@Override
	protected void execute(SalaryLauncherParams parameters) throws SalaryException {
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
			
			String msg = MessageFormat.format("Cálculo de nóminas {0}:{1}",new Object[] {startDate, endDate});
			listener.onInfo(msg);
			
			calculate(calculator);
			
			try {
				salaryBuilder.commit();
			} catch (Throwable e) {
				listener.onError(e.getLocalizedMessage());
				salaryBuilder.rollback();
			}
			msg = MessageFormat.format("Total nóminas insertadas: {0} ",new Object[]{salaryBuilder.getInsertedSalaries()});
			listener.onInfo(msg);
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
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

}
