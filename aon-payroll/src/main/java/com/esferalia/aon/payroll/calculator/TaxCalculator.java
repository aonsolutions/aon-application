package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;

public abstract class TaxCalculator {

	double irpfBase = 0 ;
	double renumeration = 0;
	double totalPayment = 0;
	double inKindIrpfBase = 0 ;


	public double getIrpfBase() {
		return irpfBase;
	}

	public double getRenumeration() {
		return renumeration;
	}


	public double getTotalPayment() {
		return totalPayment;
	}
	
	public double getInKindIrpfBase() {
		return inKindIrpfBase;
	}
		
	public double getMoneyIrpfBase() {
		return irpfBase - inKindIrpfBase;
	}
	
	
	public abstract double tax(IContractPayment payment, Date start, Date end, 
			Date issueDate, double amount ) throws AonException;

	
	public static class NotNowException extends  AonException {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
	}
	
	public static class ExtraException extends  NotNowException {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
	}
	
	public static class YesExtraException extends  AonException {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Double tax;
		
		public YesExtraException(Double tax) {
			this.tax = tax;
		}
		
		public Double getTax() {
			return tax;
		}
		
	}
	
	
	private static class DefaultTaxCalculator extends TaxCalculator {
		
		private IContractSalaryCalculatorContext context;
		
		public DefaultTaxCalculator(IContractSalaryCalculatorContext context) {
			this.context = context;
		}
		
		
		private double getTax(IContractPayment payment, Date start, Date end, double amount ) 
		throws AonException 
		{
			String irpfExpr = payment.getIrpfExpression();
			if ( irpfExpr == null ) {
				return 0;
			}
			String name = payment.getName();
			if ( name != null && irpfExpr.equals(name)) {
				return amount;
			}
			
			ExpressionContext expressionContext = context.getExpressionContext();
			List<ITimedResult<Double>> taxes;
			taxes = expressionContext.eval(irpfExpr, start, end, Double.class);
			double total = 0.00;
			for (ITimedObject<Double> quote : taxes) {
				total += quote.getValue();
			}
			return total;
		}

		@Override
		public double tax(IContractPayment contractPayment, Date start, Date end,
				Date issue, final double amount) throws AonException {
			
			SalaryType salaryType = contractPayment.getSalaryType();
			if ( salaryType != context.getSalaryType() ) {
				if ( salaryType == SalaryType.EXTRA)
					throw new ExtraException();
					
				throw new NotNowException();
			}
			
			Month issueMonth =  getMonth(issue);
			Month paymentMonth = contractPayment.getMonth();
			
			if ( paymentMonth != null && paymentMonth != issueMonth ) {
				throw new NotNowException();
			}
			
			DefaultTaxCalculator.this.renumeration += amount;
			
			final double  tax  = getTax(contractPayment, start, end, amount);
			
			
			PaymentType paymentType = contractPayment.getType();
			PaymentTypeVisitor typeVisitor = new PaymentTypeVisitor() {
				
				
				@Override
				public void visitOther(PaymentType type) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}

				@Override
				public void visitStructuralHours(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					DefaultTaxCalculator.this.irpfBase += tax;
					DefaultTaxCalculator.this.inKindIrpfBase += tax;
				}
				
				@Override
				public void visitNonStructuralHours(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
			};			
			try {
				paymentType.accept(typeVisitor);
			} catch ( NullPointerException e) {
				typeVisitor.visitOther(PaymentType.CRA_0001);
			}
			return tax;
		}
	}
	
	public static TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
		return new DefaultTaxCalculator(ctx);
	}

	protected static Month getMonth(Date date ) {
		if ( date == null )
			return null;
		int monthValue = CommonUtil.getMonth(date);
		return Month.getMonthByValue(monthValue);
	}
	
	
	

}
