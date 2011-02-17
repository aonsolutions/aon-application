package com.esferalia.aon.payroll.calculator;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
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

import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

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
			
			double irpfBase = 0 ;

			double cgcBase = 0 ;
			double cgpBase = 0 ;
			
			double structuralBase = 0;
			double nonStructuralBase = 0;

			double renumeration = 0;

			double totalPayment = 0;
			
			Date start  = ctx.getStartDate();
			Date end  = ctx.getEndDate();
			Month issueMonth = getMonth(ctx.getIssueDate());
			
			SSRegimeType ssRegimen = ctx.getSSRegime();
			
			Collection<IContractPayment> payments =  ctx.getContractPayments();
			for (IContractPayment contractPayment : payments) {
				
				Date paymentStart = Period.max(contractPayment.getStartDate(), start);
				Date paymentEnd= Period.min(contractPayment.getEndDate(), end );
				
				
				List<ITimedObject<Double>> amounts = 
					expressionContext.addExpression(contractPayment, paymentStart, paymentEnd, Double.class ) ;
				
				Double amount = sum(amounts);
				// TODO: ¿ Deberiamos crear un contexto nuevo ?
				expressionContext.addVariable(AMOUNT, amount, paymentStart, paymentEnd );
				
				PaymentType type = contractPayment.getType();
				
				if ( ssRegimen != SSRegimeType.SELF_EMPLOYED ) {
					String quoteExpr = contractPayment.getQuoteExpression();
					List<ITimedObject<Double>> quotes = expressionContext.eval(quoteExpr, paymentStart, paymentEnd, Double.class) ;
					Double quote = sum(quotes);
					if ( type == PaymentType.STRUCTURAL_HOURS ){
						structuralBase += quote;
					}else if ( type == PaymentType.NON_STRUCTURAL_HOURS){
							nonStructuralBase += quote;
					}else {
						cgcBase += quote;
					}
					cgpBase += quote;
				} // TODO : Esto es muy primitivo, demasiado if 
				
				Month month = contractPayment.getMonth();
				if ( month != null && month != issueMonth ) {
					continue;
				}
					
				renumeration += amount; 
				if ( type != PaymentType.SALARY_IN_KIND ) {
					totalPayment += amount;
				}
				
				String irpfExpr = contractPayment.getIrpfExpression();
				List<ITimedObject<Double>> irpfs = 
					expressionContext.eval(irpfExpr, paymentStart, paymentEnd, Double.class);
				double irpf = sum(irpfs);
				irpfBase += irpf;

				String concept = contractPayment.getName(); 
				String description  = null;
				try  {
					Object result = expressionContext.eval(contractPayment.getDescription(),paymentStart, paymentEnd );
					description = result != null ? result.toString() : null;
				} catch (ExpressionException e ) {
					description = contractPayment.getDescription();
				}
				
				String expression = contractPayment.getExpression() ;
				salaryBuilder.addPayment(type, concept, amount, description, expression);
			}

			salaryBuilder.setRemuneration(renumeration);
			salaryBuilder.setTotalPayment(totalPayment);

			salaryBuilder.setIrpfBase(irpfBase); 
			expressionContext.addVariable(IRPF_BASE, irpfBase, start, end );

			salaryBuilder.setRawCgcBase(cgcBase); 
			
			expressionContext.addVariable(CGC_BASE, cgcBase, start, end );
			
			salaryBuilder.setCgpBase(cgpBase);
			expressionContext.addVariable(CGP_BASE, cgpBase, start, end );

			salaryBuilder.setNonHExtraBase(nonStructuralBase); 
			expressionContext.addVariable(NON_STRUCTURAL_OVERTIME_BASE, nonStructuralBase, start, end );
			
			expressionContext.addVariable(STRUCTURAL_OVERTIME_BASE, structuralBase, start, end );


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
	
	
	
	private Double resolveDeduction(ExpressionContext ctx,IContractDeduction d) throws ExpressionException {
		String concept = d.getName();
		String expression = d.getExpression() ;
		DeductionType type = d.getType()  ;
		Date start = d.getStartDate();
		Date end = d.getEndDate();
		List<ITimedObject<Double>> amounts =  
			ctx.addExpression(d, start, end, Double.class);
		Double amount = sum(amounts);
		String description  = null;
		try  {
			Object result = ctx.eval(d.getDescription(), start, end);
			description = result != null ? result.toString() : null;
		} catch (ExpressionException e ) {
			description = d.getDescription();
		}
		
		salaryBuilder.addDeduction(type, concept, amount, description, expression);
		
		return amount ;
	}
	
	private static Double sum(List<ITimedObject<Double>> list) {
		double  sum = 0.00;
		for (ITimedObject<Double> timedObject : list) {
			 sum += timedObject.getValue();
		}
		return sum;
	}
}
