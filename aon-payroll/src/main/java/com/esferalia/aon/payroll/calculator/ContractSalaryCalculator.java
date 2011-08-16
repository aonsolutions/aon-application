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
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
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
		return (ctx instanceof ContractSalaryCalculatorContext); 
	}

	@Override
	public void initialize(ISalaryCalculatorContext ctx) throws SalaryException {
		// TODO : Borrar.
	}

	@Override
	public ISalary calculate(ISalaryCalculatorContext ctx) throws SalaryException {
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = 
			( IContractSalaryCalculatorContext)ctx;
		
		Date start  = ctx.getStartDate();
		Date end  = ctx.getEndDate();
		
		ExpressionContext expressionContext = ctx.getExpressionContext();
		
		salaryBuilder.createNewSalary();
		salaryBuilder.setContract(contractSalaryCalculatorContext.getSalaryProxy());
		
		fillEnterpriseData(contractSalaryCalculatorContext);
		fillEmployeeData(contractSalaryCalculatorContext);
		fillSalaryData(contractSalaryCalculatorContext);
		Double totalPayment = fillPayments(contractSalaryCalculatorContext);
		Double totalDeduction = fillDeductions(contractSalaryCalculatorContext);
		
		expressionContext.addVariable(TOTAL_LIQUID, totalPayment - totalDeduction, start,end);
		Double totalEmbargos = fillEmbargos(contractSalaryCalculatorContext);
		
		Double totalCost = fillCosts(contractSalaryCalculatorContext);
		expressionContext.addVariable(ENTERPRISE_QUOTA, totalCost, start, end);
		Double totalBonus = fillBonus(contractSalaryCalculatorContext);
		
		Double totalEnterprise = totalCost - totalBonus;
		salaryBuilder.setTotalEnterprise(totalEnterprise);

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
		salaryBuilder.setType(ctx.getSalaryType());
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
			
			Date issueDate = ctx.getIssueDate();
			
			Collection<IContractPayment> payments =  ctx.getContractPayments();
			for (IContractPayment contractPayment : payments) {
				
				Date paymentStart = Period.max(contractPayment.getStartDate(), start);
				Date paymentEnd= Period.min(contractPayment.getEndDate(), end );
				if ( paymentEnd.before(paymentStart) ) 
				{
					continue; //TODO : must be done in context ?
				}
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
						total += value;
						Double payment = taxCalculator.tax(contractPayment, amountStart, amountEnd, issueDate, value);

						if ( payment != 0.00 ){
							try  {
								description = expressionContext.evalTemplate(contractPayment.getDescription(), amountStart, amountEnd);
							} catch (Exception e ) {
								//TODO : Log ???
							}

							salaryBuilder.addPayment(type, concept, payment, description, null);
						}
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

			Date chargeDate = ctx.getChargeDate();
			salaryBuilder.setIrpfBase(taxCalculator.getIrpfBase()); 
			expressionContext.addVariable(IRPF_BASE, taxCalculator.getIrpfBase(), chargeDate, chargeDate);

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
					DeductionType type = contractDeduction.getType();
					
					Date deductionStart = null;
					Date deductionEnd = null;

					if ( type.isTaxDeduction() ) {
						deductionStart  = ctx.getChargeDate();
						deductionEnd = ctx.getChargeDate();
					} else {
						deductionStart = Period.max(contractDeduction.getStartDate(), start);
						deductionEnd= Period.min(contractDeduction.getEndDate(), end ); 
						if ( deductionEnd.before(deductionStart) ) 
						{
							continue; //TODO : must be done in context ?
						}
					}
					
					totalDeduction += resolveDeduction(expressionContext, contractDeduction, deductionStart, deductionEnd );
					
					if ( type.isSsDeduction() ) {
						ssContributions += totalDeduction;
					}
			}
			
			salaryBuilder.setSocialSecurityContributions(ssContributions);
			expressionContext.addVariable(EMPLOYEE_QUOTA, ssContributions, start, end);
		
			salaryBuilder.setTotalDeduction( totalDeduction );
			return totalDeduction;
		}catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}catch (Exception e) {
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

			Collection<IContractEmbargo> contractEmbargos = 
				ctx.getContractEmbargos(); 

			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();

			ExpressionContext expressionContext = 
				ctx.getExpressionContext();
			
			for (IContractEmbargo contractEmbargo : contractEmbargos) {
				
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
				
				String expression = contractEmbargo.getExpression();
				List<ITimedObject<Double>> amounts = 
					expressionContext.eval(expression, embargoStart, embargoEnd, Double.class);

				String description = contractEmbargo.getDescription();

				double embargo = 0.00;
				for (ITimedObject<Double> amount : amounts) {
					Double value = amount.getValue();
					if ( value != null ) {
						salaryBuilder.addEmbargo(contractEmbargo.getEmbargo(), value, description);
						embargo += value;
					}
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
	
	private Double fillCosts(IContractSalaryCalculatorContext ctx) 
	throws SalaryException {
		double total = 0.00; // TODO; mejor null ???
		try {

			Collection<IContractCost> contractCosts = 
				ctx.getContractCosts(); 

			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();

			ExpressionContext expressionContext = 
				ctx.getExpressionContext();
			
			for (IContractCost contractCost : contractCosts) {
				
				Date costStart = Period.max(contractCost.getStartDate(), start);
				Date costEnd= Period.min(contractCost.getEndDate(), end );
				
				List<ITimedObject<Double>> amounts = 
					expressionContext.addExpression(contractCost, costStart, costEnd, Double.class);
				
				double cost = 0.00;
				for (ITimedObject<Double> amount : amounts) {
					Double value = amount.getValue();
					if ( value != null ) {
						String description  = null;
						try  {
							Period period = amount.getPeriod();
							description = expressionContext.evalTemplate(contractCost.getDescription(), period.getStart(), period.getEnd());
						} catch (Exception e ) {
							//TODO : Log ???
						}
						salaryBuilder.addCost(contractCost.getType(), contractCost.getName(), value, description);
						cost += value;
					}
				}
				total += cost;
				
			}
			
		} catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
		
		return total;
	}

	private Double fillBonus(IContractSalaryCalculatorContext ctx) 
	throws SalaryException {
		double total = 0.00; // TODO; mejor null ???
		try {

			Collection<IContractBonus> contractBonuses = 
				ctx.getContractBonus(); 

			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();

			ExpressionContext expressionContext = 
				ctx.getExpressionContext();
			
			for (IContractBonus contractBonus : contractBonuses) {
				
				Date bonusStart = Period.max(contractBonus.getStartDate(), start);
				Date bonusEnd= Period.min(contractBonus.getEndDate(), end );
				
				if ( bonusEnd.before(bonusStart) ) 
				{
					continue; //TODO : must be done in context ?
				}

				List<ITimedObject<Double>> amounts = 
					expressionContext.eval(contractBonus.getExpression(), bonusStart, bonusEnd, Double.class);
				
				double bonus = 0.00;
				for (ITimedObject<Double> amount : amounts) {
					Double value = amount.getValue();
					if ( value != null ) {
						String description  = null;
						try  {
							Period period = amount.getPeriod();
							description = expressionContext.evalTemplate(contractBonus.getDescription(), period.getStart(), period.getEnd());
							salaryBuilder.addBonus(contractBonus.getName(), value, description);
						} catch (Exception e ) {
							//TODO : Log ???
						}
					}
					bonus += amount.getValue();
				}
				total += bonus;
				
			}
			
		} catch ( ExpressionException e ) {
			throw new SalaryException(e.getMessage(),e);			
		} catch (AonException e) {
			throw new SalaryException(e.getMessage(),e);			
		}
		
		return total;
	}

}
