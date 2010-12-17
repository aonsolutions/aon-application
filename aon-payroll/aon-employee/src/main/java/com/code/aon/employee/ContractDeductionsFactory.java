package com.code.aon.employee;

import java.util.Collection;
import java.util.Date;
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
import com.esferalia.aon.salary.deduction.CommonContingencyDeduction;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.JobTrainingDeduction;
import com.esferalia.aon.salary.deduction.UnemployementDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;

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
					manageDeductions(ctx,deductions,sd);
				}
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContractDeduction.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
				List<?> list = bean.getList(c);
				contractDeductions = (Collection<ContractDeduction>) list;
				for(ContractDeduction sd: contractDeductions){
					manageDeductions(ctx,deductions,sd);
				}
			}
			ensureDeductions(ctx,deductions);
			return deductions;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		} catch (ExpressionException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	private void ensureDeductions(IDeductionsFactoryContext ctx, Deductions deductions) {
		if ( deductions.getCommonContingency() == null ){
			IDeduction commonContingencyDeduction = new CommonContingencyDeduction(ctx);
			deductions.setCommonContingency(getSalaryDeduction(commonContingencyDeduction));

			double total = deductions.getTotal();
			deductions.setTotal(total + commonContingencyDeduction.getAmount() );
			double socialSecuritytotal = deductions.getSocialSecurityContributions();
			deductions.setSocialSecurityContributions(socialSecuritytotal + commonContingencyDeduction.getAmount() );
		}
		if ( deductions.getUnemployment() == null ){
			IDeduction unemploymentDeduction = new UnemployementDeduction(ctx);
			deductions.setUnemployment(getSalaryDeduction(unemploymentDeduction));

			double total = deductions.getTotal();
			deductions.setTotal(total + unemploymentDeduction.getAmount() );
			double socialSecuritytotal = deductions.getSocialSecurityContributions();
			deductions.setSocialSecurityContributions(socialSecuritytotal + unemploymentDeduction.getAmount() );
		}
		if ( deductions.getJobTraining() == null ){
			IDeduction jobTrainingDeduction = new JobTrainingDeduction(ctx);
			deductions.setJobTraining(getSalaryDeduction(jobTrainingDeduction));

			double total = deductions.getTotal();
			deductions.setTotal(total + jobTrainingDeduction.getAmount() );
			double socialSecuritytotal = deductions.getSocialSecurityContributions();
			deductions.setSocialSecurityContributions(socialSecuritytotal + jobTrainingDeduction.getAmount() );
		}
	}

	private void manageDeductions(IDeductionsFactoryContext ctx,Deductions deductions, ContractDeduction sd) throws SalaryException, ExpressionException {
		if(upToDate(ctx,sd.getStartDate(), sd.getEndDate())){
			IDeduction deduction = resolveDeduction(ctx, sd);
			if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
				deductions.setCommonContingency(deduction);
			} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
				deductions.setUnemployment(deduction);
			} else if (sd.getType() == DeductionType.JOB_TRAINING) {
				deductions.setJobTraining(deduction);
			} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
				deductions.setStructuralOvertime(deduction);
			} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
				deductions.setNonStructuralOvertime(deduction);
			} else if (sd.getType() == DeductionType.IRPF) {
				deductions.setIrpf(deduction);
			} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
				deductions.setAdvancePayment(deduction);
			} else if (sd.getType() == DeductionType.IN_KIND) {
				deductions.setInKind(deduction);
			} else if (sd.getType() == DeductionType.OTHER) {
				deductions.setOther(deduction);
			}
			double total = deductions.getTotal();
			deductions.setTotal(total + deduction.getAmount() );
		}
	}

	private boolean upToDate(IDeductionsFactoryContext ctx,Date startDate, Date endDate) {
		Date issueDate = ctx.getCurrentSalary().getIssueDate();
		if(startDate != null && !startDate.after(issueDate) ){
			if(endDate==null || !endDate.before(issueDate)){
				return true;
			}
		}
		return false;
	}

	private SalaryDeduction getSalaryDeduction ( IDeduction deduction ) {
		SalaryDeduction salaryDeduction = new SalaryDeduction();
		
		salaryDeduction.setType(deduction.getType());
		salaryDeduction.setAmount(deduction.getAmount());
		salaryDeduction.setExpression(deduction.getExpression());
		salaryDeduction.setDescription(deduction.getDescription());
		
		return salaryDeduction;
	}
	
	private IDeduction resolveDeduction(IDeductionsFactoryContext ctx,ContractDeduction d) throws SalaryException, ExpressionException {
		// TODO este método de resolución de las deducciones es muy básico.
		// es necesario forzar a cada IDeduction a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (d != null) {
			SalaryDeduction sd = new SalaryDeduction();
			if (d.isDescriptionDecorable()) {
				sd.setDescription(d.getDescription()  + " ("+ d.getExpression()+")");	
			} else {
				sd.setDescription(d.getDescription() );
			}
			sd.setExpression(d.getExpression() );
			sd.setType(d.getType()  );
			sd.setAmount( ctx.getExpressionContext().resolve(d));
			return sd;
		}
		return null;
	}
	
}
