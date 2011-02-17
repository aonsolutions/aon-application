package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.FunctionConstant;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

public class ContractSalaryCalculatorContext extends SalaryCalculatorContext implements IContractSalaryCalculatorContext{
	
	
	
	
	private Contract getContract() {
		return  ( Contract ) getSalaryProxy();
	}
	
	private boolean up2Date( Date startDate, Date endDate) {
		Date issueDate = getIssueDate();
		if(startDate != null && !startDate.after(issueDate) ){
			if(endDate==null || !endDate.before(issueDate)){
				return true;
			}
		}
		return false;
	}
	
	
	public ContractSalaryCalculatorContext(Contract contract) 
	throws AonException {
		super();
		setSalaryProxy(contract);
	}
	
	@Override
	public ExpressionContext getExpressionContext() {
		ExpressionContext ctx = super.getExpressionContext();
		if ( ctx == null ) {
			ctx = new ExpressionContext();
			try {
				loadSystemExpressions(ctx);
				loadApplicationExpressions(ctx);
				loadCrontactExpressions(ctx);
			} catch ( Exception e ) {}
			setExpressionContext(ctx);
		}
		return ctx;
	}
	
	@Override
	public String getCcc() {
		return getContract().getEnterpriseCCC().getCcc();
	}
	
	@Override
	public String getEnterpriseName(){
		return getContract().getWorkPlace().getEnterprise().getRegistry().getFullName();
	}

	@Override
	public String getEnterpriseAddress() {
		return getContract().getWorkPlace().getAddress().getFullAddress();
	}
	
	@Override
	public String getEnterpriseDocument() {
		return getContract().getWorkPlace().getEnterprise().getRegistry().getDocument();
	}
	
	@Override
	public String getCategory() {
		return "XXX";// TODO ¿?¿?¿?¿?¿?
	}
	
	@Override
	public String getEmployeeName() {
		return getContract().getPerson().getFullName();
	}
	
	@Override
	public String getEmployeeDocument() {
		return getContract().getPerson().getRegistry().getDocument();
	}
	
	@Override
	public Integer getRegistration() {
		return 0;// TODO ¿?¿?¿?¿?¿?
	}
	
	@Override
	public String getSocialSecurityNumber() {
		return getContract().getPerson().getSocialSecurityNumber();
	}
	
	@Override
	public Date getSeniorityDate() {
		return getContract().getStartDate();
	}
	
	
	@Override
	public SSRegimeType getSSRegime() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void loadCrontactExpressions(ExpressionContext expressionContext) throws AonException {
		Contract contract = getContract();
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());			
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_START_DATE), getEndDate());			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), getStartDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			ContractData ce = (ContractData) to;
			expressionContext.addExpression(ce, ce.getStartDate(), ce.getEndDate());
		}
	}
	
	public void loadApplicationExpressions(ExpressionContext expressionContext)
			throws AonException {

		IManagerBean bean = BeanManager.getManagerBean(FunctionConstant.class);
		Criteria c = new Criteria();
		c.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.FUNCTION_CONSTANT_START_DATE), getIssueDate());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.FUNCTION_CONSTANT_END_DATE),getIssueDate());   
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.FUNCTION_CONSTANT_END_DATE));
		c.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2) );
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to: list) {
			FunctionConstant fc = (FunctionConstant) to;
			expressionContext.addExpression(fc, fc.getStartDate(), fc.getEndDate());
		}
		
	}
	
	public void loadSystemExpressions(ExpressionContext expressionContext) throws AonException{
		
		ExpressionImpl monthDays = new ExpressionImpl();
		monthDays.setName(MONTH_DAYS.getName());
		monthDays.setScope(ExpressionScope.SYSTEM);
		monthDays.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(getStartDate(), getEndDate()) + 1));
		
		expressionContext.addExpression(monthDays, getStartDate(), getEndDate());
		
		Contract contract = getContract();
		ExpressionImpl jobDays = new ExpressionImpl();
		jobDays.setName(WORKED_DAYS.getName());
		jobDays.setScope(ExpressionScope.SYSTEM);
		Date startDate = contract.getStartDate().after( getStartDate() )?contract.getStartDate():getStartDate();
		Date endDate = contract.getEndDate() != null && contract.getEndDate().before( getEndDate() )?contract.getEndDate():getEndDate();
		jobDays.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(startDate, endDate) + 1));

		expressionContext.addExpression(jobDays, getStartDate(), getEndDate());
	}
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IContractPayment> getContractPayments() 
	throws AonException {
		Collection<ContractPayment> contractPayments;
		Contract contract = (Contract) getSalaryProxy();
		String sessionName = HibernateUtil.getSessionFactoryName(Contract.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (session.contains(contract)) {
			contractPayments = contract.getContractPayments();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
			Criteria c = new Criteria();
			
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			List<?> list = bean.getList(c);
			contractPayments = (Collection<ContractPayment>) list;
		}
		
		Collection<IContractPayment> up2DatePayments = 
			new LinkedList<IContractPayment>();
		
		for (ContractPayment contractPayment : contractPayments) {
			Date startDate =  contractPayment.getStartDate();
			Date endDate = contractPayment.getEndDate();
			if ( up2Date(startDate, endDate )){
				up2DatePayments.add(contractPayment);
			}
		}
		
		return up2DatePayments;
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<IContractDeduction> getContractDeductions() 
	throws AonException{
		Collection<ContractDeduction> contractDeductions;
		Contract contract = (Contract) getSalaryProxy();
		String sessionName = HibernateUtil.getSessionFactoryName(Contract.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Contract está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (session.contains(contract)) {
			contractDeductions = contract.getContractDeductions();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(ContractDeduction.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
			List<?> list = bean.getList(c);
			contractDeductions = (Collection<ContractDeduction>) list;
		}
		
		Collection<IContractDeduction> up2DateDeductions = 
			new LinkedList<IContractDeduction>();
		
		for (ContractDeduction contractDeduction : contractDeductions) {
			Date startDate =  contractDeduction.getStartDate();
			Date endDate = contractDeduction.getEndDate();
			if ( up2Date(startDate, endDate )){
				up2DateDeductions.add(contractDeduction);
			}
		}
		
		return up2DateDeductions;
	}
	
	
}
