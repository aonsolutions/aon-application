package com.code.aon.employee;

import java.util.Collection;
import java.util.List;

import org.hibernate.LazyInitializationException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class ContractDeductionsFactory implements IDeductionsFactory {

	@Override
	public boolean accept(IDeductionsFactoryContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Salary); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Deductions getDeductions(IDeductionsFactoryContext ctx) throws SalaryException {
		try {
			Deductions deductions = new Deductions();
			Collection<SalaryDeduction> salaryDeductions;
			Salary salary = (Salary) ctx.getSalaryProxy().getSalary();
			try {
				salaryDeductions = salary.getSalaryDeductions();
			} catch (LazyInitializationException  e) {
				IManagerBean bean = BeanManager.getManagerBean(SalaryDeduction.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEmployeeAlias.SALARY_DEDUCTION_SALARY_ID), salary.getId());
				List<?> list = bean.getList(c);
				salaryDeductions = (Collection<SalaryDeduction>) list;
			}
			for(SalaryDeduction sd: salaryDeductions){
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
					deductions.setOther(sd);
				}
			}
			return deductions;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

}
