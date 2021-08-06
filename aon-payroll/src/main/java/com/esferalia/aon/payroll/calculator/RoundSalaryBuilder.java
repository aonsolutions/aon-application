package com.esferalia.aon.payroll.calculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class RoundSalaryBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {
	
	private static class Deductions {
		
		private static class Deduction{
			private String code;
			private double amount;
			private Date endDate;
			private Date startDate;
			private DeductionType type;
		}
		
		private ArrayList<Deduction> deductions = new ArrayList<Deduction>();
		
		private double getTotalSS(UnaryOperator<Double> f) {
			return deductions.stream()
			.filter(d -> d.type != null && d.type.isSsDeduction())
			.collect(Collectors.summingDouble( d -> f.apply(d.amount)));
		}
		
		private void addDeduction(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			getDeduction(code, type, startDate, endDate)
			.ifPresentOrElse( 
			deduction -> deduction.amount += amount, 
			() -> deductions.add(newDeduction(code, type, startDate, endDate, amount))
			);
		}
		
		private Deduction newDeduction(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			
			Deduction deduction = new Deduction();
			deduction.type = type;
			deduction.code = code;
			deduction.amount = amount;
			deduction.endDate = endDate;
			deduction.startDate = startDate;
			return deduction;
		}

		private Optional<Deduction> getDeduction(String code, DeductionType type, Date startDate, Date endDate) {
			for (Deduction deduction : deductions) {
				if ( AonUtils.equals(deduction.type,type)
					&& AonStringUtils.equals(deduction.code, code)
					&& AonUtils.equals(deduction.endDate,endDate)
					&& AonUtils.equals(deduction.startDate,startDate)
					)
					return Optional.of(deduction);
			}
			return Optional.empty();
		}
		
	}
	
	private static class Costs extends Deductions {
		
		private double getTotalEnterprise(UnaryOperator<Double> f) {
			return super.getTotalSS(f);
		}

		private void addCost(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			super.addDeduction(code, type, startDate, endDate, amount);
		}
	}
	

	protected UnaryOperator<Double> f;
	protected ISalaryBuilder<T> salaryBuilder;

	public RoundSalaryBuilder(ISalaryBuilder<T> salaryBuilder, UnaryOperator<Double> f) {
		this.f = f;
		this.salaryBuilder = salaryBuilder;
	}
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------

	public void setContract(Object contract) {
		salaryBuilder.setContract(contract);
	}

	public void setCcc(String ccc) {
		salaryBuilder.setCcc(ccc);
	}

	public void setEnterpriseName(String enterpriseName) {
		salaryBuilder.setEnterpriseName(enterpriseName);
	}

	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryBuilder.setEnterpriseAddress(enterpriseAddress);
	}

	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryBuilder.setEnterpriseDocument(enterpriseDocument);
	}

	public void setRegistration(Integer registration) {
		salaryBuilder.setRegistration(registration);
	}

	public void setEmployeeName(String employeeName) {
		salaryBuilder.setEmployeeName(employeeName);
	}

	public void setEmployeeDocument(String employeeDocument) {
		salaryBuilder.setEmployeeDocument(employeeDocument);
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryBuilder.setSocialSecurityNumber(socialSecurityNumber);
	}

	public void setCategory(String category) {
		salaryBuilder.setCategory(category);
	}

	public void setQuoteGroup(String quoteGroup) {
		salaryBuilder.setQuoteGroup(quoteGroup);
	}

	public void setSeniorityDate(Date seniorityDate) {
		salaryBuilder.setSeniorityDate(seniorityDate);
	}

	public void setType(SalaryType type) {
		salaryBuilder.setType(type);
	}

	public void setIssueDate(Date issueDate) {
		salaryBuilder.setIssueDate(issueDate);
	}

	public void setChargeDate(Date issueDate) {
		salaryBuilder.setChargeDate(issueDate);
	}

	public void setStartDate(Date startDate) {
		salaryBuilder.setStartDate(startDate);
	}

	public void setEndDate(Date endDate) {
		salaryBuilder.setEndDate(endDate);
	}

	public void setTimeUnits(Integer timeUnits) {
		salaryBuilder.setTimeUnits(timeUnits);
	}

	public void setListener(ISalaryBuilderListener listener) {
		salaryBuilder.setListener(listener);
	}

	public void setRawCgcBase(Double rawCgcBase) {
		try {
			salaryBuilder.setRawCgcBase(f.apply(rawCgcBase));
		} catch ( NullPointerException e) {
			salaryBuilder.setRawCgcBase(0.00);
		}
	}

	// ------------------------------------------------------------------------

	private double remuneration;
	private double proExtBase;
	private double cgpBase;
	private double cgcBase;
	private double itBase;
	private double irpfBase;
	private double moneyIrpfBase;
	private double inkindIrpfBase;
	private double hExtraBase;
	private double nonHExtraBase;
	private double totalLiquid;
	private double totalPayment;
	private double totalDeduction;
	private double totalIrpf;
	private double totalSS;
	private double totalEnterprise;
	
	private Costs costs;
	private Deductions deductions;
	
	private ExpressionContext expressionContext;
	
	
	public void createNewSalary() {

		this.remuneration = 0;
		this.proExtBase = 0;
		this.cgcBase = 0;
		this.cgpBase = 0;
		this.itBase = 0;
		this.irpfBase = 0;
		this.moneyIrpfBase = 0;
		this.inkindIrpfBase = 0;
		this.hExtraBase = 0;
		this.nonHExtraBase = 0;
		this.totalLiquid = 0;
		this.totalPayment = 0;
		this.totalDeduction = 0;
		this.totalIrpf = 0;
		this.totalSS = 0;
		this.totalEnterprise = 0;
		
		this.costs = new Costs();
		this.deductions = new Deductions();
		
		salaryBuilder.createNewSalary();
	}

	public T getSalary() {
		round();
		return salaryBuilder.getSalary();
	}

	public void setCgcBase(Double cgcBase) {
		try {
			this.cgcBase = cgcBase;
		} catch (NullPointerException e) {
		}
		
		try {
			this.cgcBase = round(ContextVariable.CGC_BASE);			
		} catch ( Exception e ) {
		}
		
		for ( ContextVariable v : ContextVariable.ERE_BASES ) { 
			try {
				this.cgcBase += round(v);			
			} catch ( Exception e ) {
			}
		}
		try {
			this.cgcBase += round(ContextVariable.MATERNITY_BASE);			
		} catch ( Exception e ) {
		}
		
		try {
			round(ContextVariable.CGC_BASE_ENTERPRISE);			
		} catch ( Exception e ) {
		}
	}

	public void setCgpBase(Double cgpBase) {
		try {
			this.cgpBase = cgpBase;
		} catch (NullPointerException e) {
		}
		try {
			this.cgpBase = round(ContextVariable.CGP_BASE);			
		} catch ( Exception e ) {
		}
		// sum ERE & MATERNIDAD
		for ( ContextVariable v : ContextVariable.ERE_BASES ) { 
			try {
				this.cgpBase += round(v);			
			} catch ( Exception e ) {
			}
		}
		try {
			this.cgpBase += round(ContextVariable.MATERNITY_BASE);			
		} catch ( Exception e ) {
		}
		
		try {
			round(ContextVariable.CGP_BASE_ENTERPRISE);			
		} catch ( Exception e ) {
		}
	}

	public void setItBase(Double itBase) {
		try {
			this.itBase = itBase;
		} catch (NullPointerException e) {
		}
	}

	public void setRemuneration(Double remuneration) {
		try {
			this.remuneration = remuneration;
		} catch (NullPointerException e) {
		}
	}

	public void setProExtBase(Double proExtBase) {
		try {
			this.proExtBase = proExtBase;
		} catch (NullPointerException e) {
		}
	}

	public void setIrpfBase(Double irpfBase) {
		try {
			this.irpfBase = irpfBase;
		} catch (NullPointerException e) {
		}
	}

	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		try {
			this.moneyIrpfBase = moneyIrpfBase;
		} catch (NullPointerException e) {
		}
	}

	public void setInkindIrpfBase(Double inkindIrpfBase) {
		try {
			this.inkindIrpfBase = inkindIrpfBase;
		} catch (NullPointerException e) {
		}
	}

	public void setHExtraBase(Double hExtraBase) {
		try {
			this.hExtraBase = hExtraBase;
		} catch (NullPointerException e) {
		}
	}

	public void setNonHExtraBase(Double nonHExtraBase) {
		try {
			this.nonHExtraBase = nonHExtraBase;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalLiquid(Double totalLiquid) {
		try {
			this.totalLiquid = totalLiquid;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalPayment(Double totalPayment) {
		try {
			this.totalPayment = totalPayment;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalDeduction(Double totalDeduction) {
		try {
			this.totalDeduction = totalDeduction;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalIrpf(Double totalIrpf) {
		try {
			this.totalIrpf = totalIrpf;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalSS(Double socialSecurityContributions) {
		try {
			this.totalSS = socialSecurityContributions;
		} catch (NullPointerException e) {
		}
	}

	public void setTotalEnterprise(Double totalEnterprise) {
		try {
			this.totalEnterprise = totalEnterprise;
		} catch (NullPointerException e) {
		}
	}
	
	public void setExpressionContext(ExpressionContext expressionContext) {
		this.expressionContext = expressionContext;
	}

	// ------------------------------------------------------------------------

	public void addData(String name, ITimedVariable<?> datas) {
		salaryBuilder.addData(name, datas);
	}

	public void addCost(Double amount, String description, Date startDate, Date endDate, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		double deduction = getDeductionAmount(cost.getName(), cost.getType(), startDate, endDate);
		amount = f.apply(f.apply( deduction + amount ) - f.apply(deduction));
		costs.addCost(cost.getName(), cost.getType(), startDate, endDate, amount);
		salaryBuilder.addCost(amount, description, startDate, endDate, cost, context);
	}

	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		Double rounded = f.apply(amount);
		salaryBuilder.addBonus(rounded, description, startDate, endDate, bonus, context);
	}

	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context);
	}

	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
	}

	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		deductions.addDeduction(deduction.getName(), deduction.getType(), start, end, amount);
		salaryBuilder.addDeduction(f.apply(amount), description, start, end, deduction, context);
	}

	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroDeduction(start, end, deduction, context);
	}

	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addEmbargo(id, amount, description, embargo, context);
	}

	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroEmbargo(id, embargo, context);
	}

	// ------------------------------------------------------------------------
	
	private double getTotalSS() {
		return deductions.getTotalSS(f);
	}
	
	private double getTotalEnterprise() {
		return costs.getTotalEnterprise(f);
	}

	private double getDeductionAmount(String code, DeductionType type, Date startDate, Date endDate) {
		if ( AonStringUtils.endsWith(code, "_E"))
			code = AonStringUtils.removeEnd(code, "_E");
		return deductions.getDeduction(code, type, startDate, endDate).map(d -> d.amount).orElse(0.00);
	}

	private void round() {
		
		totalSS = getTotalSS();
		totalEnterprise = getTotalEnterprise();
		
		double totalOther = totalDeduction - (totalIrpf + totalSS);
		totalSS = f.apply(totalSS);
		totalIrpf = f.apply(totalIrpf);
		totalOther = f.apply(totalOther);
		totalDeduction = totalIrpf + totalSS + totalOther;
		salaryBuilder.setTotalIrpf(totalIrpf);
		salaryBuilder.setTotalSS(totalSS);
		salaryBuilder.setTotalDeduction(totalDeduction);

		double totalEmbargo = totalPayment - totalLiquid - totalDeduction;
		totalEmbargo = f.apply(totalEmbargo);
		totalPayment = f.apply(totalPayment);
		totalLiquid = totalPayment - totalDeduction - totalEmbargo;
		salaryBuilder.setTotalPayment(totalPayment);
		salaryBuilder.setTotalLiquid(totalLiquid);

		moneyIrpfBase = f.apply(moneyIrpfBase);
		inkindIrpfBase = f.apply(inkindIrpfBase);
		remuneration = f.apply(remuneration);
		irpfBase = moneyIrpfBase + inkindIrpfBase;
		salaryBuilder.setIrpfBase(irpfBase);
		salaryBuilder.setMoneyIrpfBase(moneyIrpfBase);
		salaryBuilder.setInkindIrpfBase(inkindIrpfBase);
		salaryBuilder.setRemuneration(remuneration);

		// ---
		itBase = f.apply(itBase);
		proExtBase = f.apply(proExtBase);
		salaryBuilder.setItBase(itBase);
		salaryBuilder.setProExtBase(proExtBase);

		cgcBase = f.apply(cgcBase);
		hExtraBase = f.apply(hExtraBase);
		nonHExtraBase = f.apply(nonHExtraBase);
		salaryBuilder.setHExtraBase(hExtraBase);
		salaryBuilder.setCgcBase(cgcBase);
		salaryBuilder.setNonHExtraBase(nonHExtraBase);

		cgpBase = Math.min(cgcBase + nonHExtraBase + hExtraBase, f.apply(cgpBase));
		salaryBuilder.setCgpBase(cgpBase);

		totalEnterprise = f.apply(totalEnterprise);
		salaryBuilder.setTotalEnterprise(totalEnterprise);

	}
	
	private double round(ContextVariable contextVariable ) {
		double sum = 0.00;
		for (ITimedVariable<Object> v : expressionContext.getVariables(contextVariable.getName())) {
			RoundVarible roundVarible = new RoundVarible(v, f);
			sum += roundVarible.getValue(roundVarible.getPeriod());
			expressionContext.putVariable(contextVariable.getName(), roundVarible);
		}
		return sum;
	}
	

}
