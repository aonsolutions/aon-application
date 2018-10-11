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
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class SalaryDeductionsFactory implements IDeductionsFactory {


	@Override
	public boolean accept(IDeductionsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();

		return (proxy instanceof Salary);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Deductions getDeductions(IDeductionsFactoryContext ctx)
			throws SalaryException {
		try {
			Deductions deductions = new Deductions();
			Collection<SalaryDeduction> salaryDeductions;
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
				salaryDeductions = salary.getSalaryDeductions();
				for (SalaryDeduction sd : salaryDeductions) {
					manageDeductions(deductions, sd);
				}
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryDeduction.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_DEDUCTION_SALARY_ID),
						salary.getId());
				List<?> list = bean.getList(c);
				salaryDeductions = (Collection<SalaryDeduction>) list;
				for (SalaryDeduction sd : salaryDeductions) {
					manageDeductions(deductions, sd);
				}
			}
			deductions.setTotal(salary.getTotalDeduction());
			deductions.setSocialSecurityContributions(salary
					.getSocialSecurityContributions());
			return deductions;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	public static void manageDeductions(Deductions deductions, SalaryDeduction sd) {
		if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
			deductions.setCommonContingency(sd);
		} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
			deductions.setUnemployment(sd);
		} else if (sd.getType() == DeductionType.JOB_TRAINING) {
			deductions.setJobTraining(sd);
		} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
			deductions.setStructuralOvertime(sd);
		} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
			deductions.setNonStructuralOvertime(sd);
		} else if (sd.getType() == DeductionType.IRPF) {
			deductions.setIrpf(sd);
		} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
			deductions.setAdvancePayment(sd);
		} else if (sd.getType() == DeductionType.IN_KIND) {
			deductions.setInKind(sd);
		} else if (sd.getType() == DeductionType.OTHER) {
			deductions.addOther(sd);
		}
	}

}
