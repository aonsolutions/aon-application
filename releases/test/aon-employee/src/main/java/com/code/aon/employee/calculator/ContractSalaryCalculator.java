package com.code.aon.employee.calculator;


import java.util.Collection;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;

public class ContractSalaryCalculator implements ISalaryCalculator{

	private ISalaryBuilder salaryBuilder ;
	
	
	public void setSalaryBuilder( ISalaryBuilder salaryBuilder){
		this.salaryBuilder = salaryBuilder;
	}
	
	@Override
	public boolean accept(ISalaryCalculatorContext ctx) {
		return (ctx instanceof IContractSalaryCalculatorContext); 
	}

	@Override
	public void initialize(ISalaryCalculatorContext ctx) throws SalaryException {
		// TODO La manera de obtener el context es más compleja que lo programado aquí.
		// Será necesario sacarlo de éste método.
	}

	@Override
	public ISalary calculate(ISalaryCalculatorContext ctx) throws SalaryException {
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = 
			( IContractSalaryCalculatorContext)ctx;
		
		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(ctx.getSalaryProxy());
		
		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(ctx);
		Double totalPayments = fillPayments(contractSalaryCalculatorContext);
		fillBasesData(ctx,totalPayments);
		Double totalDeductions = fillDeductions(contractSalaryCalculatorContext);

		salaryBuilder.setTotalLiquid(CommonUtil.round(totalPayments - totalDeductions));
		
		return salaryBuilder.getSalary();
	}

	private void fillEnterpriseData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setCcc(ctx.getCcc());
		salaryBuilder.setEnterpriseName(ctx.getEnterpriseName());
		salaryBuilder.setEnterpriseAddress(ctx.getEnterpriseAddress());
		salaryBuilder.setEnterpriseDocument(ctx.getEnterpriseDocument());
	}

	private void fillEmployeeData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setEmployeeName(ctx.getEmployeeName());
		salaryBuilder.setEmployeeDocument(ctx.getEmployeeDocument());
		salaryBuilder.setRegistration(ctx.getRegistration()); 
		salaryBuilder.setSocialSecurityNumber(ctx.getSocialSecurityNumber());
		salaryBuilder.setCategory(ctx.getCategory()); 
		salaryBuilder.setSeniorityDate(ctx.getSeniorityDate());
	}

	private void fillSalaryData(ISalaryCalculatorContext ctx) {
		salaryBuilder.setIssueDate( ctx.getIssueDate() );
		salaryBuilder.setStartDate( ctx.getStartDate());
		salaryBuilder.setEndDate( ctx.getEndDate());
		salaryBuilder.setTimeUnits( ((int) CommonUtil.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate())) + 1);
	}

	private Double fillPayments(IContractSalaryCalculatorContext ctx) 
	throws SalaryException{
		try {
			double totalPayment = 0; 
			Collection<IContractPayment> payments =  ctx.getContractPayments();
			for (IContractPayment contractPayment : payments) {
					totalPayment  += resolvePayment(ctx.getExpressionContext(), contractPayment);
			}
			
			salaryBuilder.setTotalPayment( totalPayment  );
			return totalPayment ;
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}

	private Double fillDeductions(IContractSalaryCalculatorContext ctx) 
	throws SalaryException {
		try {
			double totalDeduction = 0; 
			
			
			Collection<IContractDeduction> contractDeductions = 
				ctx.getContractDeductions();
			ExpressionContext expressionContext = ctx.getExpressionContext();
			for (IContractDeduction contractDeduction : contractDeductions) {
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
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}


	private void fillBasesData(ISalaryCalculatorContext ctx, Double totalPayments) throws SalaryException{
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


	
	private Double resolvePayment(ExpressionContext ctx,IContractPayment cp) throws ExpressionException {
		// TODO este método de resolución de los complementos es muy básico.
		// es necesario forzar a cada IPayment a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (cp != null) {
			String expression = cp.getExpression() ;
			PaymentType type = cp.getType();
			Double amount = ctx.resolve(cp) ;
			String concept = cp.getName(); 
			String description  = null;
			try  {
				Object result = ctx.eval(cp.getDescription());
				description = result != null ? result.toString() : null;
			} catch (ExpressionException e ) {
				description = cp.getDescription();
			}
			
			salaryBuilder.addPayment(type, concept, amount, description, expression);
			
			return amount;
		}
		return null;
	}
	
	private Double resolveDeduction(ExpressionContext ctx,IContractDeduction d) throws ExpressionException {
		// TODO este método de resolución de las deducciones es muy básico.
		// es necesario forzar a cada IDeduction a que se resulva a sí mismo  
		// en función del contexto "ctx".
		if (d != null) {
			String concept = d.getName();
			String expression = d.getExpression() ;
			DeductionType type = d.getType()  ;
			Double amount = ctx.resolve(d);
			String description  = null;
			try  {
				Object result = ctx.eval(d.getDescription());
				description = result != null ? result.toString() : null;
			} catch (ExpressionException e ) {
				description = d.getDescription();
			}
			
			salaryBuilder.addDeduction(type, concept, amount, description, expression);
			
			return amount ;
		}
		return null;
	}
	
	private IContractDeduction getCommonContingency() {
		SimpleContractDeduction contractDeduction = 
			new SimpleContractDeduction();
		contractDeduction.setType(DeductionType.COMMON_CONTINGENCY);
		contractDeduction.setExpression("base_cc * 4.7 / 100 ");
		contractDeduction.setDescription("4.7%");
		return contractDeduction;
	}

	private IContractDeduction getUnemployment() {
		SimpleContractDeduction contractDeduction = 
			new SimpleContractDeduction();
		contractDeduction.setType(DeductionType.UNEMPLOYMENT);
		contractDeduction.setExpression("base_cp * 1.5 / 100 ");
		contractDeduction.setDescription("1.5%");
		return contractDeduction;
	}


	private IContractDeduction getJobTraining() {
		SimpleContractDeduction contractDeduction = 
			new SimpleContractDeduction();
		contractDeduction.setType(DeductionType.JOB_TRAINING);
		contractDeduction.setExpression("base_cp * 0.10 / 100 ");
		contractDeduction.setDescription("0.10%");
		return contractDeduction;
	}


}
