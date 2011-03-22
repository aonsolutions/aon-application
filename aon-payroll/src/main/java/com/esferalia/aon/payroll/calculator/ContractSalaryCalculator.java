package com.esferalia.aon.payroll.calculator;


import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.IRPF_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.STRUCTURAL_OVERTIME_BASE;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariableException;

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
		// TODO : Borrar.
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
		try {
			
			ExpressionContext expressionContext = 
				ctx.getExpressionContext();
			
			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();
			
			QuoteCalculator quoteCalculator = 
				QuoteCalculator.getQuoteCalculator(ctx);
			
			TaxCalculator taxCalculator = 
				TaxCalculator.getTaxCalculator(ctx);
			
			Collection<IContractPayment> payments =  ctx.getContractPayments();
			for (IContractPayment contractPayment : payments) {
				
				Date paymentStart = Period.max(contractPayment.getStartDate(), start);
				Date paymentEnd= Period.min(contractPayment.getEndDate(), end );
				
				try {
					List<ITimedObject<Double>> amounts = 
						expressionContext.addExpression(contractPayment, paymentStart, paymentEnd, Double.class ) ;
					Double amount = summarize(amounts);
					
					quoteCalculator.quote(contractPayment, paymentStart, paymentEnd, amount);
					
					double payment = taxCalculator.tax(contractPayment, paymentStart, paymentEnd, amount);

					if ( payment != 0 ) {
						String description  = 
							expressionContext.evalTemplate(contractPayment.getDescription(), paymentStart, paymentEnd);
						
						String concept = contractPayment.getName(); 
						PaymentType type = contractPayment.getType();
						salaryBuilder.addPayment(type, concept, payment, description, null);
					} // end-if: Sólo si la cantidad a pagar es mayor que cero...
				
				} catch ( UndefinedVariableException e ) {
					// TODO : notificar ??? 
				}
			}

			salaryBuilder.setRemuneration(taxCalculator.getRenumeration());
			salaryBuilder.setTotalPayment(taxCalculator.getTotalPayment());

			salaryBuilder.setItBase(quoteCalculator.getItBase()); 

			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase()); 
			expressionContext.addVariable(IRPF_BASE, taxCalculator.getIrpfBase(), start, end );

			salaryBuilder.setCgcBase(quoteCalculator.getCgcBase()); 
			salaryBuilder.setRawCgcBase(quoteCalculator.getCgcBase()); 
			expressionContext.addVariable(CGC_BASE, quoteCalculator.getCgcBase(), start, end );
			
			salaryBuilder.setCgpBase(quoteCalculator.getCgpBase());
			expressionContext.addVariable(CGP_BASE, quoteCalculator.getCgpBase(), start, end );

			salaryBuilder.setNonHExtraBase(quoteCalculator.getNonStructuralBase()); 
			expressionContext.addVariable(NON_STRUCTURAL_OVERTIME_BASE, quoteCalculator.getNonStructuralBase(), start, end );
			
			salaryBuilder.setHExtraBase(quoteCalculator.getStructuralBase()); 
			expressionContext.addVariable(STRUCTURAL_OVERTIME_BASE, quoteCalculator.getStructuralBase(), start, end );
			
			salaryBuilder.setProExtBase(quoteCalculator.getProExtBase());

			return taxCalculator.getTotalPayment() ;
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
			double ssContributions = 0;
	
			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();
	 			
			Collection<IContractDeduction> contractDeductions = 
				ctx.getContractDeductions();
			ExpressionContext expressionContext = ctx.getExpressionContext();
			for (IContractDeduction contractDeduction : contractDeductions) {
					Date deductionStart = Period.max(contractDeduction.getStartDate(), start);
					Date deductionEnd= Period.min(contractDeduction.getEndDate(), end );
					totalDeduction += resolveDeduction(expressionContext, contractDeduction, deductionStart, deductionEnd );
					if ( contractDeduction.getType().isSsDeduction() ) {
						ssContributions += totalDeduction;
					}
			}
			
			salaryBuilder.setSocialSecurityContributions(ssContributions);
		
			salaryBuilder.setTotalDeduction( totalDeduction );
			return totalDeduction;
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
	}
	
	
	private Double resolveDeduction(ExpressionContext ctx,IContractDeduction d, Date start, Date end ) throws ExpressionException {
		String concept = d.getName();
		String expression = d.getExpression() ;
		DeductionType type = d.getType()  ;

		List<ITimedObject<Double>> amounts =  
			ctx.addExpression(d, start, end, Double.class);
		
		Double total = 0.00;
		
		for (ITimedObject<Double> amount : amounts) {
			Double value = amount.getValue();
			if ( value == 0 ) 
				continue;
			String description  = null;
			try  {
				Period period = amount.getPeriod();
				description = ctx.evalTemplate(d.getDescription(), period.getStart(), period.getEnd());
			} catch (Exception e ) {
				System.err.println(d.getDescription() + " = " + e.getMessage() );
				//TODO : Log ???
			}
			salaryBuilder.addDeduction(type, concept, value, description, expression);
			total += value;
		}
		
		return total ;
	}
	
	private static Double summarize(List<ITimedObject<Double>> list) {
		double  sum = 0.00;
		for (ITimedObject<Double> timedObject : list) {
			 sum += timedObject.getValue();
		}
		return sum;
	}
	
	
}
