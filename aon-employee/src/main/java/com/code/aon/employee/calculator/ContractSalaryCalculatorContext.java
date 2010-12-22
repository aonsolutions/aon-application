package com.code.aon.employee.calculator;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.events.EndDocument;

import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;

public class ContractSalaryCalculatorContext extends SalaryCalculatorContext {
	
	private boolean up2Date( Date startDate, Date endDate) {
		Date issueDate = getIssueDate();
		if(startDate != null && !startDate.after(issueDate) ){
			if(endDate==null || !endDate.before(issueDate)){
				return true;
			}
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<ContractPayment> getContractPayments() 
	throws ManagerBeanException {
		
		Date issueDate = getIssueDate();
		
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
			
			c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			List<?> list = bean.getList(c);
			contractPayments = (Collection<ContractPayment>) list;
		}
		
		Collection<ContractPayment> up2DatePayments = 
			new LinkedList<ContractPayment>();
		
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
	public Collection<ContractDeduction> getContractDeductions() throws ManagerBeanException{
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
			c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
			List<?> list = bean.getList(c);
			contractDeductions = (Collection<ContractDeduction>) list;
		}
		
		Collection<ContractDeduction> up2DateDeductions = 
			new LinkedList<ContractDeduction>();
		
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
