package com.code.aon.employee;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
		return (proxy instanceof Contract); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Deductions getDeductions(IDeductionsFactoryContext ctx) throws SalaryException {
		try {
			Deductions deductions = new Deductions();
			Collection<ContractDeduction> contractDeductions;
			Contract contract = (Contract) ctx.getSalaryProxy();
			String sessionName = HibernateUtil.getSessionFactoryName(Contract.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Contract está conectado a la session de Hibernate utilizamos la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
			// el FrameWork.
			if (session.contains(contract)) {
				contractDeductions = contract.getContractDeductions();
				for(ContractDeduction sd: contractDeductions){
					manageDeductions(deductions,sd);
				}
			} else {
				IManagerBean bean = BeanManager.getManagerBean(SalaryDeduction.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
				List<?> list = bean.getList(c);
				contractDeductions = (Collection<ContractDeduction>) list;
				for(ContractDeduction sd: contractDeductions){
					manageDeductions(deductions,sd);
				}
			}
			return deductions;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	private void manageDeductions(Deductions deductions, ContractDeduction sd) {
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

}
