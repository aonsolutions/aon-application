package com.esferalia.aon.payroll;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.Payments;

public class SalaryPaymentsFactory implements IPaymentsFactory {

	@Override
	public boolean accept(IPaymentsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Salary);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Payments getPayments(IPaymentsFactoryContext ctx)
			throws SalaryException {
		try {
			Payments payments = new Payments();
			Collection<SalaryPayment> salaryPayments;
			Salary salary = (Salary) ctx.getSalaryProxy().getSalary();
			String sessionName = HibernateUtil
					.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos
			// la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso
			// contrario vamos por
			// el FrameWork.
			if (session.contains(salary) || salary.getId() == null) {
				salaryPayments = salary.getSalaryPayments();
				for (SalaryPayment sp : salaryPayments) {
					managePayment(payments, sp);
				}
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryPayment.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID),
						salary.getId());
				List<?> list = bean.getList(c);
				salaryPayments = (Collection<SalaryPayment>) list;
				for (SalaryPayment sp : salaryPayments) {
					managePayment(payments, sp);
				}
			}
			return payments;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	public static void managePayment(Payments payments, SalaryPayment sp) {

		PaymentType type = sp.getType();
		if (type == null) {
			payments.addSalarySupplements(sp);
			return;
		}

		if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
			payments.addMovingCompensation(sp);
			return;
		}

		int value = type.ordinal();

		if (value >= 13 && value <= 26) {
			payments.addSalaryInKind(sp);
			return;
		} else if (value >= 4 && value <= 5) {
			payments.addSpecialBonuses(sp);
			return;
		} else if (value >= 27 && value <= 41) {
			payments.addOtherNonWages(sp);
			return;
		} else if (value >= 51 && value <= 54) {
			payments.addCompensationOrPrepaidExpenses(sp);
			return;
		} else if (value == 1
				&& StringUtils.equals(sp.getName(),
						ContextVariable.BASE_SALARY)) {
			payments.addBaseSalary(sp);
			return;
		} else if (value == 1 && isSpecialSecurityBenefits(sp) ) {			
			payments.addSpecialSecurityBenefits(sp);
		} else if (value == 2) {
			payments.addNoEstructuralOvertimeHours(sp);
		} else if (value == 3) {
			payments.addOvertimeHours(sp);
		} else {
			payments.addSalarySupplements(sp);
		}

	}
	
	private static boolean isSpecialSecurityBenefits(SalaryPayment sp) {
		String name = sp.getName();
		if ( StringUtils.isBlank(name) ) 
			return false ;		
		return Arrays.stream(ContextVariable.ERES).anyMatch( v -> StringUtils.equals(v.getName(), name ));
	}
	

}
