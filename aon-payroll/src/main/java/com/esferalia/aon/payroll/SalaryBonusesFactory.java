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
import com.esferalia.aon.salary.bonus.Bonuses;
import com.esferalia.aon.salary.bonus.IBonusesFactory;
import com.esferalia.aon.salary.bonus.IBonusesFactoryContext;

public class SalaryBonusesFactory implements IBonusesFactory {


	@Override
	public boolean accept(IBonusesFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();

		return (proxy instanceof Salary);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Bonuses getBonuses(IBonusesFactoryContext ctx)
			throws SalaryException {
		try {
			Bonuses bonuses = new Bonuses();
			Collection<SalaryBonus> salaryBonuses;
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
				salaryBonuses = salary.getSalaryBonus();
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryBonus.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID),
						salary.getId());
				List<?> list = bean.getList(c);
				salaryBonuses = (Collection<SalaryBonus>) list;
			}
			Double totalAmount = 0.0;
			for (SalaryBonus sb : salaryBonuses) {
				totalAmount += sb.getAmount();
			}
			bonuses.setTotal(totalAmount);
			return bonuses;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}
	
}
