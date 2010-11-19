package com.code.aon.employee;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.LazyInitializationException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.Payments;

public class ContractPaymentsFactory implements IPaymentsFactory {

	@Override
	public boolean accept(IPaymentsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Contract); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Payments getPayments(IPaymentsFactoryContext ctx) throws SalaryException{
		try {
			Payments payments = new Payments();
			Collection<ContractPayment> contractPayments;
			Contract contract = (Contract) ctx.getSalaryProxy();
			try {
				contractPayments = contract.getContractPayments();
			} catch (LazyInitializationException  e) {
				IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
				List<?> list = bean.getList(c);
				contractPayments = (Collection<ContractPayment>) list;
			}
			Payments p = new Payments();
			for(ContractPayment sp: contractPayments){
				if(upToDate(sp.getStartDate(), sp.getEndDate())){
					if (sp.getType() == PaymentType.BASE_SALARY) {
						p.setBaseSalary(sp);
					} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
						p.addSalarySupplements(sp);
					} else if (sp.getType() == PaymentType.OVERTIME_HOURS) {
						p.setOvertimeHours(sp);
					} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
						p.setSpecialBonuses(sp);
					} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
						p.setSalaryInKind(sp);
					} else if (sp.getType() == PaymentType.COMPENSATION_OR_PREPAID_EXPENSES) {
						p.addCompensationOrPrepaidExpenses(sp);
					} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
						p.setSpecialSecurityBenefits(sp);
					} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
						p.setMovingCompensation(sp);
					} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
						p.setOtherNonWage(sp);
					}
				}
			}
			return payments;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}
	
	private boolean upToDate(Date startDate, Date endDate) {
		if(startDate != null && startDate.before(new Date())){
			if(endDate==null || endDate.after(new Date())){
				return true;
			}
		}
		return false;
	}

}
