package com.code.aon.employee.calculator;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
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

import static com.esferalia.aon.salary.enumeration.PaymentType.*;

public class ContractSalaryCalculator implements ISalaryCalculator{

	public static final String YEAR_DAYS 	= "DIAS_AÑO";
	public static final String MONTH_DAYS 	= "DIAS_MES";
	public static final String HOLIDAYS 	= "DIAS_VACACIONES";
	public static final String WORKED_DAYS 	= "DIAS_TRABAJADOS";
	public static final String ACTUAL_DAYS 	= "DIAS_EFECTIVOS";
	public static final String SPECIAL_DAYS = "DIAS_ESPECIALES";
	public static final String SENIOR_BASE 	= "BASE_ANTIGUEDAD";
	
	public static final String CGC_CODE 					= "CGC";
	public static final String FP_CODE 						= "FP";
	public static final String UNEMPLOYMENT_CODE 			= "DESMP";
	public static final String NON_STRUCTURAL_OVERTIME_CODE	= "NESTR";
	public static final String STRUCTURAL_OVERTIME_CODE 	= "ESTR";
	public static final String IRPF_CODE 					= "IRPF";
	public static final String IRPF_PERCENT 				= "PORCENTAJE_IRPF";

	public static final String CGC_BASE 					= "BASE_CGC";
	public static final String CGP_BASE 					= "BASE_CGP";
	public static final String STRUCTURAL_OVERTIME_BASE 	= "BASE_ESTR";
	public static final String NON_STRUCTURAL_OVERTIME_BASE = "BASE_NESTR";
	public static final String IRPF_BASE 					= "BASE_IRPF";

	public static final String CGC_BASE_MIN 				= "BASE_CGC_MIN";
	public static final String CGC_BASE_MAX 				= "BASE_CGC_MAX";
	
	public static final String AMOUNT 						= "IMPORTE";

	private class SalaryBases {
		
		double cgcBase = 0 ;
		double cgpBase = 0 ;
		double irpfBase = 0 ;
		
		double structuralBase = 0;
		double nonStructuralBase = 0;

		double renumeration = 0;
		double totalPayment = 0;
		
	}
	
	
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
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);

		salaryBuilder.setTotalLiquid(totalPayment - totalDeduction);
		
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

	private void fillSalaryData(IContractSalaryCalculatorContext ctx) {
		salaryBuilder.setIssueDate( ctx.getIssueDate() );
		salaryBuilder.setStartDate( ctx.getStartDate());
		salaryBuilder.setEndDate( ctx.getEndDate());
		long days  = CommonUtil.getDaysBetweenDates(ctx.getStartDate(), ctx.getEndDate()) + 1;
		salaryBuilder.setTimeUnits( (int) days );
	}

	private Double fillPayments(IContractSalaryCalculatorContext ctx) 
	throws SalaryException{
		ExpressionContext expressionContext = 
			ctx.getExpressionContext();
		try {
			
			SalaryBases salaryBases = new SalaryBases();
			
			Month issueMonth = getMonth(ctx.getIssueDate());
			
			Collection<IContractPayment> payments =  ctx.getContractPayments();
			for (IContractPayment contractPayment : payments) {
				resolvePayment(expressionContext, contractPayment, issueMonth, salaryBases);
			}
			salaryBuilder.setRemuneration(salaryBases.renumeration);
			salaryBuilder.setTotalPayment(salaryBases.totalPayment);

			salaryBuilder.setIrpfBase(salaryBases.irpfBase); 
			expressionContext.put(IRPF_BASE, salaryBases.irpfBase);

			double cgcBase = salaryBases.cgcBase ;
			salaryBuilder.setCommonBase(cgcBase); 
			expressionContext.put(CGC_BASE, cgcBase);
			
			double cgpBase = salaryBases.cgpBase ;
			salaryBuilder.setProfessionalBase(cgpBase);
			expressionContext.put(CGP_BASE, cgpBase);

			salaryBuilder.setNonStructuralBase(salaryBases.nonStructuralBase); 
			expressionContext.put(NON_STRUCTURAL_OVERTIME_BASE, salaryBases.nonStructuralBase);
			
			expressionContext.put(STRUCTURAL_OVERTIME_BASE, salaryBases.structuralBase);


			return salaryBases.totalPayment ;
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
			//salaryBuilder.setSocialSecurityContributions(ssContributions);
		
			salaryBuilder.setTotalDeduction( totalDeduction );
			return totalDeduction;
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}
	
	private Month getMonth(Date date ) {
		if ( date == null )
			return null;
		int monthValue = CommonUtil.getMonth(date); 
		return Month.getMonthByValue(monthValue);
	}
	
	private void taxPayment(ExpressionContext ctx,IContractPayment cp, SalaryBases bases) throws ExpressionException {
		String irpfExpr = cp.getIrpfExpression();
		double irpf = ctx.eval(irpfExpr, Double.class);
		bases.irpfBase += irpf;
	}

	private void quotePayment(ExpressionContext ctx,IContractPayment cp, SalaryBases bases) throws ExpressionException {
		String quoteExpr = cp.getQuoteExpression();
		double quote = ctx.eval(quoteExpr, Double.class) ;
		switch (cp.getType()) {
			case STRUCTURAL_HOURS:
				bases.structuralBase += quote;
				break;
			case NON_STRUCTURAL_HOURS:
				bases.nonStructuralBase += quote;
				break;
			default:
				bases.cgcBase += quote;
		}
		bases.cgpBase += quote;
	}
	
	private void resolvePayment(ExpressionContext ctx,IContractPayment cp, Month salaryMonth, SalaryBases bases) throws ExpressionException {
		
		String expression = cp.getExpression() ;
		PaymentType type = cp.getType();
		Double amount = ctx.resolve(cp) ;
		
		// TODO: ¿ Deberiamos crear un contexto nuevo ?
		ctx.put(AMOUNT, amount);
		
		quotePayment(ctx, cp, bases);

		Month month = cp.getMonth();
		if ( month != null && month != salaryMonth ) {
			return;
		}
			
		taxPayment(ctx, cp, bases);
		
		bases.renumeration += amount; 
		if ( type != SALARY_IN_KIND ) {
			bases.totalPayment += amount;
		}
		
		String concept = cp.getName(); 
		String description  = null;
		try  {
			Object result = ctx.eval(cp.getDescription());
			description = result != null ? result.toString() : null;
		} catch (ExpressionException e ) {
			description = cp.getDescription();
		}
		
		salaryBuilder.addPayment(type, concept, amount, description, expression);
		
	}
	
	
	
	private Double resolveDeduction(ExpressionContext ctx,IContractDeduction d) throws ExpressionException {
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
	
}
