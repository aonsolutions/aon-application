package com.code.aon.employee.calculator;


import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.ContractEvent;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.DeductionConcept;
import com.code.aon.employee.FunctionConstant;
import com.code.aon.employee.PaymentConcept;
import com.code.aon.employee.Salary;
import com.code.aon.employee.SalaryBuilder;
import com.code.aon.employee.SalaryDeduction;
import com.code.aon.employee.SalaryPayment;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.CommonContingencyDeduction;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.JobTrainingDeduction;
import com.esferalia.aon.salary.deduction.UnemployementDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

public class ContractSalaryCalculator implements ISalaryCalculator{

	private ISalaryBuilder salaryBuilder = 
		new SalaryBuilder();
	
	@Override
	public boolean accept(ISalaryCalculatorContext ctx) {
		return (ctx instanceof ContractSalaryCalculatorContext); 
	}

	@Override
	public void initialize(ISalaryCalculatorContext ctx) throws SalaryException {
		// TODO La manera de obtener el context es más compleja que lo programado aquí.
		// Será necesario sacarlo de éste método.
		try {
			fillSystemExpressions(ctx);
			fillApplicationExpressions(ctx);
			fillContractExpressions(ctx);
			fillSalaryExpressions(ctx);
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(),e);
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	@Override
	public ISalary calculate(ISalaryCalculatorContext ctx) throws SalaryException {
		Contract contract = (Contract) ctx.getSalaryProxy();
		ContractSalaryCalculatorContext contractSalaryCalculatorContext = 
			( ContractSalaryCalculatorContext)ctx;
		
		salaryBuilder.createNewSalary();
		
		salaryBuilder.setContract(contract);
		
		fillEnterpriseData(contract);
		fillEmployeeData(contract);
		fillSalaryData(ctx,contract);
		Double totalPayments = fillPayments(contractSalaryCalculatorContext,contract);
		fillBasesData(ctx,contract, totalPayments);
		Double totalDeductions = fillDeductions(contractSalaryCalculatorContext,contract);

		salaryBuilder.setTotalLiquid(CommonUtil.round(totalPayments - totalDeductions));
		
		return salaryBuilder.getSalary();
	}

	private void fillEnterpriseData(Contract contract) {
		salaryBuilder.setEnterpriseName(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		salaryBuilder.setEnterpriseAddress(contract.getWorkPlace().getAddress().getFullAddress());
		salaryBuilder.setEnterpriseDocument(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		salaryBuilder.setCcc(contract.getEnterpriseCCC().getCcc());
	}

	private void fillEmployeeData(Contract contract) {
		salaryBuilder.setEmployeeName(contract.getPerson().getFullName());
		salaryBuilder.setEmployeeDocument(contract.getPerson().getRegistry().getDocument());
		salaryBuilder.setRegistration(0); // TODO ¿?¿?¿?¿?¿?
		salaryBuilder.setSocialSecurityNumber(contract.getPerson().getSocialSecurityNumber());
		salaryBuilder.setCategory("XXX"); // TODO ¿?¿?¿?¿?¿?
		salaryBuilder.setSeniorityDate(contract.getStartDate());
	}

	private void fillSalaryData(ISalaryCalculatorContext ctx, Contract contract) {
		salaryBuilder.setIssueDate( ctx.getIssueDate() );
		salaryBuilder.setStartDate( ctx.getStartDate());
		salaryBuilder.setEndDate( ctx.getEndDate());
		salaryBuilder.setTimeUnits( ((int) CommonUtil.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate())) + 1);
	}

	private Double fillPayments(ContractSalaryCalculatorContext ctx, Contract contract) 
	throws SalaryException{
		try {
			double totalPayment = 0; 
			Collection<ContractPayment> payments =  ctx.getContractPayments();
			for (ContractPayment contractPayment : payments) {
					totalPayment  += resolvePayment(ctx.getExpressionContext(), contractPayment);
			}
			
			salaryBuilder.setTotalPayment( totalPayment  );
			return totalPayment ;
		}catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(),e);			
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}

	private Double fillDeductions(ContractSalaryCalculatorContext ctx, Contract contract) 
	throws SalaryException {
		try {
			double totalDeduction = 0; 
			
			
			Collection<ContractDeduction> contractDeductions = 
				ctx.getContractDeductions();
			ExpressionContext expressionContext = ctx.getExpressionContext();
			for (ContractDeduction contractDeduction : contractDeductions) {
					totalDeduction += resolveDeduction(expressionContext, contractDeduction);
			}
			
			Double ssContributions = resolveDeduction(expressionContext, 
					getCommonContingency());
			ssContributions += resolveDeduction(expressionContext, 
					getUnemployment());
			ssContributions += resolveDeduction(expressionContext, 
					getJobTraining());
			salaryBuilder.setSocialSecurityContributions(ssContributions);
		
			
			totalDeduction += ssContributions;
			salaryBuilder.setTotalDeduction( totalDeduction );
			return totalDeduction;
		}catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(),e);			
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}


	private void fillBasesData(ISalaryCalculatorContext ctx, Contract contract, Double totalPayments) throws SalaryException{
		try {
			ExpressionContext expressionContext = ctx.getExpressionContext();
			
			salaryBuilder.setRemuneration(totalPayments); // TODO ¿?¿?¿?¿?¿?
			salaryBuilder.setExtraPayProration(0.0); // TODO ¿?¿?¿?¿?¿?
			salaryBuilder.setCommonBase(totalPayments); // TODO ¿?¿?¿?¿?¿?
			expressionContext.put("base_cc", totalPayments.toString());
			salaryBuilder.setProfessionalBase(totalPayments); // TODO ¿?¿?¿?¿?¿?
			expressionContext.put("base_cp", totalPayments.toString());
			salaryBuilder.setOvertimeBase(totalPayments); // TODO ¿?¿?¿?¿?¿?
			expressionContext.put("base_horas_extras", totalPayments.toString());
			salaryBuilder.setIrpfBase(totalPayments); // TODO ¿?¿?¿?¿?¿?
			expressionContext.put("base_irpf", totalPayments.toString());
		
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	private void fillSalaryExpressions(ISalaryCalculatorContext ctx) {
		// TODO Auto-generated method stub
	}

	private void fillContractExpressions(ISalaryCalculatorContext ctx) throws ManagerBeanException, ExpressionException {
		Contract contract = (Contract) ctx.getSalaryProxy();
		IManagerBean bean = BeanManager.getManagerBean(ContractEvent.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_CONTRACT_ID), contract.getId());			
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_START_DATE), ctx.getEndDate());			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE), ctx.getStartDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			ContractEvent ce = (ContractEvent) to;
			ctx.getExpressionContext().put(ce);
		}
	}

	private void fillApplicationExpressions(ISalaryCalculatorContext ctx) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(FunctionConstant.class);
		Criteria c = new Criteria();
		c.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_START_DATE), ctx.getIssueDate());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_END_DATE), ctx.getIssueDate());  
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.FUNCTION_CONSTANT_END_DATE));
		c.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2) );
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to: list) {
			FunctionConstant fc = (FunctionConstant) to;
			ctx.getExpressionContext().put(fc);
		}
	}

	private void fillSystemExpressions(ISalaryCalculatorContext ctx) throws ExpressionException {
		ExpressionImpl e = new ExpressionImpl();
		e.setName("dias_mes");
		e.setScope(ExpressionScope.SYSTEM);
		e.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate()) + 1));
		ctx.getExpressionContext().put(e);

		Contract contract = (Contract) ctx.getSalaryProxy();
		e = new ExpressionImpl();
		e.setName("dias_trabajados");
		e.setScope(ExpressionScope.SYSTEM);
		Date startDate = contract.getStartDate().after( ctx.getStartDate() )?contract.getStartDate():ctx.getStartDate();
		Date endDate = contract.getEndDate() != null && contract.getEndDate().before( ctx.getEndDate() )?contract.getEndDate():ctx.getEndDate();
		e.setExpression(Long.toString(CommonUtil.getDaysBetweenDates(startDate, endDate) + 1));
		ctx.getExpressionContext().put(e);
	}
	
	private Double resolvePayment(ExpressionContext ctx,ContractPayment cp) throws ExpressionException {
		// TODO este método de resolución de los complementos es muy básico.
		// es necesario forzar a cada IPayment a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (cp != null) {
			String description ;
			if (cp.isDescriptionDecorable()) {
				description = cp.getDescription()  + " ("+ cp.getExpression()+")";	
			} else {
				description =  cp.getDescription() ;
			}
			String expression = cp.getExpression() ;
			PaymentType type = cp.getType();
			Double amount = ctx.resolve(cp) ;
			PaymentConcept paymentConcept = cp.getPaymentConcept();
			String concept = paymentConcept != null ? paymentConcept.getCode() : null; 
			
			salaryBuilder.addPayment(type, concept, amount, description, expression);
			
			return amount;
		}
		return null;
	}
	
	private Double resolveDeduction(ExpressionContext ctx,ContractDeduction d) throws ExpressionException {
		// TODO este método de resolución de las deducciones es muy básico.
		// es necesario forzar a cada IDeduction a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (d != null) {
			String expression = d.getExpression() ;
			DeductionType type = d.getType()  ;
			Double amount = ctx.resolve(d);
			DeductionConcept deductionConcept = d.getDeductionConcept();
			String description  = null;
			try  {
				description = ( String ) ctx.eval(d.getDescription());
			} catch (ExpressionException e ) {
				description = d.getDescription();
			}
			String concept = deductionConcept != null ? deductionConcept.getCode() : null;
			
			salaryBuilder.addDeduction(type, concept, amount, description, expression);
			
			return amount ;
		}
		return null;
	}
	
	private ContractDeduction getCommonContingency() {
		ContractDeduction contractDeduction = new ContractDeduction();
		contractDeduction.setType(DeductionType.COMMON_CONTINGENCY);
		contractDeduction.setExpression("base_cc * 4.7 / 100 ");
		contractDeduction.setDescriptionDecorable(true);
		contractDeduction.setDescription("4.7%");
		return contractDeduction;
	}

	private ContractDeduction getUnemployment() {
		ContractDeduction contractDeduction = new ContractDeduction();
		contractDeduction.setType(DeductionType.UNEMPLOYMENT);
		contractDeduction.setExpression("base_cp * 1.5 / 100 ");
		contractDeduction.setDescriptionDecorable(true);
		contractDeduction.setDescription("1.5%");
		return contractDeduction;
	}


	private ContractDeduction getJobTraining() {
		ContractDeduction contractDeduction = new ContractDeduction();
		contractDeduction.setType(DeductionType.JOB_TRAINING);
		contractDeduction.setExpression("base_cp * 0.10 / 100 ");
		contractDeduction.setDescriptionDecorable(true);
		contractDeduction.setDescription("0.10%");
		return contractDeduction;
	}


}
