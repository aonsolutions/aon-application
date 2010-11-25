package com.code.aon.employee;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;
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
			String sessionName = HibernateUtil.getSessionFactoryName(Contract.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
			// el FrameWork.
			if (session.contains(contract)) {
				contractPayments = contract.getContractPayments();
				for(ContractPayment cp: contractPayments){
					managePayments(ctx,payments,cp);
				}
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
				List<?> list = bean.getList(c);
				contractPayments = (Collection<ContractPayment>) list;
				for(ContractPayment cp: contractPayments){
					managePayments(ctx,payments,cp);
				}
			}
			return payments;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}
	
	private void managePayments(IPaymentsFactoryContext ctx,Payments payments, ContractPayment sp) {
		if(upToDate(sp.getStartDate(), sp.getEndDate())){
			if (sp.getType() == PaymentType.BASE_SALARY) {
				payments.setBaseSalary(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
				payments.addSalarySupplements(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.OVERTIME_HOURS) {
				payments.setOvertimeHours(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
				payments.setSpecialBonuses(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
				payments.setSalaryInKind(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.COMPENSATION_OR_PREPAID_EXPENSES) {
				payments.addCompensationOrPrepaidExpenses(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
				payments.setSpecialSecurityBenefits(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
				payments.setMovingCompensation(resolvePayment(ctx,sp));
			} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
				payments.setOtherNonWage(resolvePayment(ctx,sp));
			}
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

	private IPayment resolvePayment(IPaymentsFactoryContext ctx,IPayment p) {
		// TODO este método de resolución de los complementos es muy básico.
		// es necesario forzar a cada IPayment a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (p != null) {
			SalaryPayment sp = new SalaryPayment();
			sp.setDescription(p.getDescription() );
			sp.setFunction(p.getFunction() );
			sp.setType(p.getType()  );
			if (NumberUtils.isNumber(p.getFunction()) ) {
				sp.setAmount( NumberUtils.toDouble(p.getFunction()) );	
			} else {
				sp.setAmount(0.0);
			}
			return sp;
		}
		return null;
	}

}
