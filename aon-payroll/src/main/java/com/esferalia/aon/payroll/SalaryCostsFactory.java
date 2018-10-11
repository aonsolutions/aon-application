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
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.cost.ICostsFactory;
import com.esferalia.aon.salary.cost.ICostsFactoryContext;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class SalaryCostsFactory implements ICostsFactory {


	@Override
	public boolean accept(ICostsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();

		return (proxy instanceof Salary);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Costs getCosts(ICostsFactoryContext ctx)
			throws SalaryException {
		try {
			Costs costs = new Costs();
			Collection<SalaryCost> salaryCosts;
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
				salaryCosts = salary.getSalaryCosts();
				for (SalaryCost sc : salaryCosts) {
					manageCosts(costs, sc);
				}
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryCost.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_COST_SALARY_ID),
						salary.getId());
				List<?> list = bean.getList(c);
				salaryCosts = (Collection<SalaryCost>) list;
				for (SalaryCost sc : salaryCosts) {
					manageCosts(costs, sc);
				}
			}
			return costs;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	public static void manageCosts(Costs costs, SalaryCost sc) {
		if (sc.getType() == DeductionType.COMMON_CONTINGENCY) {
			costs.setCommonContingency(sc);
		} else if (sc.getType() == DeductionType.PROFESSIONAL_CONTINGENCY) {
			if(sc.getCostConcept()!=null && sc.getCostConcept().equals("IT_E")){
				costs.setAtepIt(sc);
			} else if(sc.getCostConcept()!=null && sc.getCostConcept().equals("IMS_E")){
				costs.setAtepIms(sc);	
			}
		} else if (sc.getType() == DeductionType.UNEMPLOYMENT) {
			costs.setUnemployment(sc);
		} else if (sc.getType() == DeductionType.JOB_TRAINING) {
			costs.setJobTraining(sc);
		} else if (sc.getType() == DeductionType.FOGASA) {
			costs.setFogasa(sc);
		} else if (sc.getType() == DeductionType.STRUCTURAL_OVERTIME) {
			costs.setStructuralOvertime(sc);
		} else if (sc.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
			costs.setNonStructuralOvertime(sc);
		} else if (sc.getType() == DeductionType.IRPF) {
			costs.setIrpf(sc);
		}
	}

}
