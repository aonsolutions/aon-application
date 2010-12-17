package com.code.aon.employee.calculator;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractEvent;
import com.code.aon.employee.FunctionConstant;
import com.code.aon.employee.Salary;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.PaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

public class ContractSalaryCalculator implements ISalaryCalculator{

	@Override
	public boolean accept(SalaryCalculatorContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Contract); 
	}

	@Override
	public void initialize(SalaryCalculatorContext ctx) throws SalaryException {
		// TODO La manera de obtener el context es más compleja que lo programado aquí.
		// Será necesario sacarlo de éste método.
		try {
			ExpressionContext ectx = ctx.getExpressionContext();
			if (ectx == null) {
				ectx = new ExpressionContext();
				ctx.setExpressionContext(ectx);
				fillSystemExpressions(ctx);
				fillApplicationExpressions(ctx);
				fillContractExpressions(ctx);
				fillSalaryExpressions(ctx);
			}
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	@Override
	public ISalary calculate(SalaryCalculatorContext ctx) throws SalaryException {
		Contract contract = (Contract) ctx.getSalaryProxy();
		
		Salary salary = new Salary();
		salary.setContract(contract);
		fillEnterpriseData(salary,contract);
		fillEmployeeData(salary,contract);
		fillSalaryData(ctx,salary,contract);
		fillPayments(ctx,salary,contract);
		fillBasesData(ctx,salary,contract);
		fillDeductions(ctx,salary,contract);
		salary.setTotalLiquid(CommonUtil.round(salary.getTotalPayment() - salary.getTotalDeduction()));
		return salary;
	}

	private void fillEnterpriseData(Salary salary, Contract contract) {
		salary.setEnterpriseName(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		salary.setEnterpriseAddress(contract.getWorkPlace().getAddress().getFullAddress());
		salary.setEnterpriseDocument(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		salary.setCcc(contract.getEnterpriseCCC().getCcc());
	}

	private void fillEmployeeData(Salary salary, Contract contract) {
		salary.setEmployeeName(contract.getPerson().getFullName());
		salary.setEmployeeDocument(contract.getPerson().getRegistry().getDocument());
		salary.setRegistration(0); // TODO ¿?¿?¿?¿?¿?
		salary.setSocialSecurityNumber(contract.getPerson().getSocialSecurityNumber());
		salary.setCategory("XXX"); // TODO ¿?¿?¿?¿?¿?
		salary.setSeniorityDate(contract.getStartDate());
	}

	private void fillSalaryData(SalaryCalculatorContext ctx, Salary salary, Contract contract) {
		salary.setIssueDate( ctx.getIssueDate() );
		salary.setStartDate( ctx.getStartDate());
		salary.setEndDate( ctx.getEndDate());
		salary.setTimeUnits( ((int) CommonUtil.getDaysBetweenDates(salary.getStartDate(), salary.getEndDate())) + 1);
	}

	private void fillPayments(SalaryCalculatorContext ctx, Salary salary, Contract contract) throws SalaryException {
		PaymentsFactoryManager manager =  PaymentsFactoryManager.getInstance();
		PaymentsFactoryContext pfc = new PaymentsFactoryContext();
		pfc.setSalaryProxy(contract);
		pfc.setCurrentSalary(salary);
		pfc.setExpressionContext(ctx.getExpressionContext());
		IPaymentsFactory factory = manager.getFactory( pfc );
		salary.setPayments( factory.getPayments(pfc) );
		salary.setTotalPayment( salary.getPayments().getTotal() ); 
	}

	private void fillDeductions(SalaryCalculatorContext ctx, Salary salary, Contract contract) throws SalaryException {
		DeductionsFactoryManager manager =  DeductionsFactoryManager.getInstance();
		DeductionsFactoryContext dfc = new DeductionsFactoryContext();
		dfc.setSalaryProxy(contract);
		dfc.setCurrentSalary(salary);
		dfc.setExpressionContext(ctx.getExpressionContext());
		IDeductionsFactory factory = manager.getFactory( dfc );
		salary.setDeductions( factory.getDeductions(dfc) );
		salary.setTotalDeduction( salary.getDeductions().getTotal() );
	}

	private void fillBasesData(SalaryCalculatorContext ctx, Salary salary, Contract contract) {
		
		 ExpressionContext expressionContext = ctx.getExpressionContext();
		
		salary.setRemuneration(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		
		salary.setExtraPayProration(0.0); // TODO ¿?¿?¿?¿?¿?
		salary.setCommonBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		expressionContext.put("base_cc", salary.getCommonBase().toString());
		salary.setProfessionalBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		expressionContext.put("base_cp", salary.getProfessionalBase().toString());
		salary.setOvertimeBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		expressionContext.put("base_horas_extras", salary.getOvertimeBase().toString());
		salary.setIrpfBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		expressionContext.put("base_irpf", salary.getIrpfBase().toString());
	}

	private void fillSalaryExpressions(SalaryCalculatorContext ctx) {
		// TODO Auto-generated method stub
	}

	private void fillContractExpressions(SalaryCalculatorContext ctx) throws ManagerBeanException {
		Contract contract = (Contract) ctx.getSalaryProxy();
		IManagerBean bean = BeanManager.getManagerBean(ContractEvent.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_CONTRACT_ID), contract.getId());			
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_START_DATE), ctx.getEndDate());			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE), ctx.getStartDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			ContractEvent ce = (ContractEvent) to;
			ctx.getExpressionContext().put(ce);
		}
	}

	private void fillApplicationExpressions(SalaryCalculatorContext ctx) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FunctionConstant.class);
		Criteria c = new Criteria();
		c.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_START_DATE), ctx.getIssueDate());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_END_DATE), ctx.getIssueDate());  
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_END_DATE));
		c.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2) );
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to: list) {
			FunctionConstant fc = (FunctionConstant) to;
			ctx.getExpressionContext().put(fc);
		}
	}

	private void fillSystemExpressions(SalaryCalculatorContext ctx) {
		ExpressionImpl e = new ExpressionImpl();
		e.setName("dias_mes");
		e.setScope(ExpressionScope.SYSTEM);
		e.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate()) + 1));
		ctx.getExpressionContext().put(e);

		Contract contract = (Contract) ctx.getSalaryProxy();
		e = new ExpressionImpl();
		e.setName("dias_trabajados");
		e.setScope(ExpressionScope.SYSTEM);
		Date startDate = contract.getStartDate().after( ctx.getStartDate() )?contract.getStartDate():ctx.getStartDate();
		Date endDate = contract.getEndDate() != null && contract.getEndDate().before( ctx.getEndDate() )?contract.getEndDate():ctx.getEndDate();
		e.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(startDate, endDate) + 1));
		ctx.getExpressionContext().put(e);
}

}
