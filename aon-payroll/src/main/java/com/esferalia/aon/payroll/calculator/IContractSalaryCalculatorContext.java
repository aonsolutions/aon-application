package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedVariable;

public interface IContractSalaryCalculatorContext extends
		ISalaryCalculatorContext {

	public static interface IListener {

		void onIrpf(IrpfOutcome irpfOutcome);


		default void onUndefinedData(IExpression expression,
				String variableName, String message, Date start, Date end) {
		};

		default void onRedefinedImplicit(String name,
				ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
		}

		default <T> T onConstantParameter(String func, T constant, ExpressionContext ctx) {
			return constant;
		}

		default void onMistakenPartialFactor(double monthHours, double workedHours, double factor) {
		}
	}

	public IListener getListener();

	public void setListener(IListener listener);

	public SalaryType getSalaryType();

	public String getCcc();

	public String getEnterpriseName();
	
	public String getEnterpriseCity();

	public String getEnterpriseAddress();

	public String getEnterpriseDocument();

	public SSRegimeType getSSRegime();

	public String getCategory();

	public String getQuoteGroup();

	public String getEmployeeName();

	public String getEmployeeCity();

	public String getEmployeeAddress();

	public String getEmployeeDocument();

	public String getSocialSecurityNumber();

	public Integer getRegistration();

	public Date getSeniorityDate();

	public ExpressionContext getSystemExpressionContext();

	public ExpressionContext getImplicitExpressionContext();

	public ExpressionContext getAgreementExpressionContext();

	public Collection<ISystemPayment> getSystemPayments();

	public Collection<IContractPayment> getAgreementPayments();

	public Collection<IContractPayment> getContractPayments()
			throws AonException;

	public Collection<IContractCost> getContractCosts() throws AonException;

	public Collection<IContractBonus> getContractBonus() throws AonException;

	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException;

	public Collection<IContractDeduction> getContractDeductions()
			throws AonException;

}
