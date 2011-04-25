package com.esferalia.aon.payroll.calculator;


import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
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
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariableException;

public class ContractSalaryCalculator implements ISalaryCalculator{

	
	private ISalaryBuilder salaryBuilder ;
	
	
	private static class TimedVariable 
		implements ITimedVariable<Double>
	{
		
		private Period period;
		private Double value;
		
		
		public TimedVariable(Date start, Date end , Double value) {
			this.value = value;
			this.period = new Period(start, end);
		}
		
		@Override
		public Period getPeriod() {
			return period;
		}
		
		@Override
		public Double getValue(Period p) {
			return period.compareTo(p) == 0 ? value : getPortionValue(p);
		}
		
		private Double getPortionValue(Period p) {
			double alldays = 
				CommonUtil.getDaysBetweenDates(period.getStart(), period.getEnd()) +1;
			double portionDays = 
				CommonUtil.getDaysBetweenDates(p.getStart(), p.getEnd()) +1;
			return value * portionDays / alldays;
		}
	}
	
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
		
		ExpressionContext expressionContext = ctx.getExpressionContext();
		
		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(ctx.getSalaryProxy());
		
		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);
		
		expressionContext.addVariable(TOTAL_LIQUID, totalPayment - totalDeduction, ctx.getStartDate(),ctx.getEndDate());
		Double totalEmbargos = fillEmbargos(contractSalaryCalculatorContext);
		
		salaryBuilder.setTotalLiquid(totalPayment - totalDeduction - totalEmbargos);
		
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
		salaryBuilder.setQuoteGroup(ctx.getQuoteGroup()); 
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
					
					String concept = contractPayment.getName(); 
					PaymentType type = contractPayment.getType();
					
					double total = 0.00;
					
					for (ITimedObject<Double> amount : amounts) {
						
						Date amountStart = amount.getPeriod().getStart();
						Date amountEnd = amount.getPeriod().getEnd();
						
						String description  = null;

						Double value = amount.getValue();
						
						Double payment = taxCalculator.tax(contractPayment, amountStart, amountEnd, value);
						
						
						//System.out.printf("%s=%s %.3f \r\n", concept, contractPayment.getExpression(), payment);

						try  {
							description = expressionContext.evalTemplate(contractPayment.getDescription(), amountStart, amountEnd);
						} catch (Exception e ) {
							//TODO : Log ???
						}
						salaryBuilder.addPayment(type, concept, payment, description, null);
						total += value;
					}
				
					quoteCalculator.quote(contractPayment, paymentStart, paymentEnd, total);

				} catch ( UndefinedVariableException e ) {
					// TODO : notificar ??? 
				}
			}

			salaryBuilder.setRemuneration(taxCalculator.getRenumeration());
			
			double totalPayment = taxCalculator.getTotalPayment();
			salaryBuilder.setTotalPayment(totalPayment);
			expressionContext.addVariable(TOTAL_PAYMENT, totalPayment, start, end );

			salaryBuilder.setItBase(quoteCalculator.getItBase()); 

			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase()); 
			TimedVariable irpfBaseVar = new TimedVariable(start, end, taxCalculator.getIrpfBase());
			expressionContext.addVariable(IRPF_BASE, irpfBaseVar);

			salaryBuilder.setRawCgcBase(quoteCalculator.getRawCgcBase());

			salaryBuilder.setCgcBase(quoteCalculator.getCgcBase());
			double cgcBaseVar = quoteCalculator.getCgcBase() - quoteCalculator.getMaternityBase();
			expressionContext.addVariable(CGC_BASE, cgcBaseVar , start, end );
			//System.out.printf("CGC_BASE=%.3f \r\n", cgcBaseVar);
			
			salaryBuilder.setCgpBase(quoteCalculator.getCgpBase());
			double cgpBaseVar = quoteCalculator.getCgpBase() - quoteCalculator.getMaternityBase();
			expressionContext.addVariable(CGP_BASE, cgpBaseVar, start, end );

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
				//TODO : Log ???
			}
			salaryBuilder.addDeduction(type, concept, value, description, expression);
			total += value;
		}
		
		amounts = null;
		
		//System.out.printf("%s=%s %.3f \r\n", concept, expression, total);
		return total ;
	}
	
	private Double fillEmbargos(IContractSalaryCalculatorContext ctx) 
	throws SalaryException {
		
		double total = 0.00;
		

		try {

			Collection<IContractDeduction> contractEmbargos = 
				ctx.getContractEmbargos();

			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();

			ExpressionContext expressionContext = 
				ctx.getExpressionContext();
			
			for (IContractDeduction contractEmbargo : contractEmbargos) {
				
				Date embargoStart = Period.max(contractEmbargo.getStartDate(), start);
				Date embargoEnd= Period.min(contractEmbargo.getEndDate(), end );
				
				expressionContext.addVariable(EMBARGO_PAID, total, start, end);
				// Un poco tricky, ejecutamos un string del contexto para 
				double embargoLimit = 0.00;
				String embargoMax = 
					expressionContext.getVariable(EMBARGO_MAX, embargoStart, embargoEnd, String.class);
				List<ITimedObject<Double>> embargosMax = 
					expressionContext.eval(embargoMax, embargoStart, embargoEnd, Double.class );
				for (ITimedObject<Double> amout : embargosMax) {
					embargoLimit+= amout.getValue();
				}
				if ( embargoLimit  <= 0.00 )  {
					break;
				} // Ya no se puede embargar mas.
				
				expressionContext.addVariable(EMBARGO_LIMIT, embargoLimit, start, end);
				
				double left = contractEmbargo.getAmount();
				expressionContext.addVariable(EMBARGO_LEFT, left, embargoStart, embargoEnd);
				
				List<ITimedObject<Double>> amounts = 
					expressionContext.eval(contractEmbargo.getExpression(), embargoStart, embargoEnd, Double.class);
				
				double embargo = 0.00;
				for (ITimedObject<Double> amount : amounts) {
					embargo += amount.getValue();
				}
				total += embargo;
				
			}
			
		} catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
		
		return total;
		
	}
	
}
