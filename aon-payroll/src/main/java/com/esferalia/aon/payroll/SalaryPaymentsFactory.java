package com.esferalia.aon.payroll;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.Payments;

public class SalaryPaymentsFactory implements IPaymentsFactory {
	private static final boolean IREPORT = SalaryDeductionsFactory.class
			.getResource("/hibernate.cfg.xml") != null;

	@Override
	public boolean accept(IPaymentsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Salary); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Payments getPayments(IPaymentsFactoryContext ctx) throws SalaryException{
		try {
			Payments payments = new Payments();
			Collection<SalaryPayment> salaryPayments;
			Salary salary = (Salary) ctx.getSalaryProxy().getSalary();
			String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
			// el FrameWork.
			if (  session.contains(salary)  || salary.getId() == null || IREPORT ) {
				salaryPayments = salary.getSalaryPayments();
				for(SalaryPayment sp: salaryPayments){
					managePayment(payments,sp);
				}
			} else {
				IManagerBean bean = BeanManager.getManagerBean(SalaryPayment.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID), salary.getId());
				List<?> list = bean.getList(c);
				salaryPayments = (Collection<SalaryPayment>) list;
				for(SalaryPayment sp: salaryPayments){
					managePayment(payments,sp);
				}
			}
			return payments;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	private void managePayment(Payments payments, SalaryPayment sp) {
		if (sp.getType() == PaymentType.BASE_SALARY) {
			payments.addBaseSalary(sp);
		} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
			payments.addSalarySupplements(sp);
		} else if (sp.getType() == PaymentType.STRUCTURAL_HOURS) {
		} else if (sp.getType() == PaymentType.NON_STRUCTURAL_HOURS) {
			payments.addOvertimeHours(sp);
		} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
			payments.addSpecialBonuses(sp);
		} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
			payments.addSalaryInKind(sp);
		} else if (sp.getType() == PaymentType.COMPENSATION_OR_PREPAID_EXPENSES) {
			payments.addCompensationOrPrepaidExpenses(sp);
		} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
			payments.addSpecialSecurityBenefits(sp);
		} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
			payments.addMovingCompensation(sp);
		} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
			payments.addOtherNonWages(sp);
		}
	}

}
