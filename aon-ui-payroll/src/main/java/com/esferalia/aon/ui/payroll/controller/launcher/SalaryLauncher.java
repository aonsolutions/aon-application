package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.calculator.test.SalaryBuilderTester;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class SalaryLauncher {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryLauncher.class);
	
	private SalaryLauncherParams params;
	
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
	}
	
	public String getBeanName() {
		return IPayrollConstants.SALARY_LAUNCHER_NAME;
	}
	
	public void onExecute(ActionEvent event) {
		try {
			Date startTime = new Date(); 
			int count = execute(getParams());
			Date endTime = new Date();
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
			String msg = "Proceso Finalizado correctamente. Inicio: " + timeFormat.format(startTime)	
			            + " Fin: " + timeFormat.format(endTime) + " Nóminas calculadas: " + count;
			AonUtil.addInfoMessage(msg);
			LOGGER.error(msg);
		} catch (SalaryException e) {
			String msg = "Se produjeron errores en el cáclulo de nóminas.";
			AonUtil.addInfoMessage(msg);
			LOGGER.error(msg,e);
		}
		
	}

	private int execute(SalaryLauncherParams parameters) throws SalaryException {
		try {
			//SQLSalaryBuilder salaryBuilder = new SQLSalaryBuilder();

			String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
			Connection connection = HibernateUtil.getSQLConnection(sessionFactory);
			SalaryBuilderTester salaryBuilder = new SalaryBuilderTester(connection);
			
			ContractSalaryCalculator calculator = new ContractSalaryCalculator();
			calculator.setSalaryBuilder(salaryBuilder);
			
			Date startDate = parameters.getStartDate();  
			Date endDate = parameters.getEndDate(); 
			
			LOGGER.info("Salary Calculate {}:{}",startDate, endDate);
			
			Criteria criteria = null;
			if (parameters.getEnterprise() != null && parameters.getEnterprise().getId() != null ) {
				criteria = new Criteria();
				criteria.addEqualExpression("enterprise_id", parameters.getEnterprise().getId());
			}
			if (parameters.getPerson() != null && parameters.getPerson().getId() != null ) {
				criteria = criteria==null?new Criteria():criteria;
				criteria.addEqualExpression("person_id", parameters.getPerson().getId());
			}
//			if (criteria == null) {
//				String msg = "Indique una empresa o una persona";
//				LOGGER.error(msg);
//				AonUtil.addErrorMessage(msg);
//				throw new SalaryException(msg);
//			}
			SQLContractSalaryCalculatorContext sqlCtx = 
				new SQLContractSalaryCalculatorContext(connection, 
						startDate, 
						endDate,
						Calendar.getInstance().getTime(),
						criteria );
			
			int count ;
			for ( count = 0;  sqlCtx.next() ; count++ ) {
				try {
					calculator.calculate(sqlCtx);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{} [{}] {}, {} ",
								new Object[]{
								Integer.toString(count),
								sqlCtx.getEmployeeDocument(),
								sqlCtx.getEnterpriseName(),
								sqlCtx.getEmployeeName()});
					}
					salaryBuilder.test();
				}catch ( SalaryException e ) {
					LOGGER.error("{} [{}] {}, {} : {}",
							new Object[]{
							Integer.toString(count),
							sqlCtx.getEmployeeDocument(),
							sqlCtx.getEnterpriseName(),
							sqlCtx.getEmployeeName(),
							e.getLocalizedMessage()});
				}
			}
			LOGGER.info("salarys {} ",count);
			return count;
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		}
	}
	
}
