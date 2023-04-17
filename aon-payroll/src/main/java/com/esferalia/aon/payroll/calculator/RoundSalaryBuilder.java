package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_1_3;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_4_15;
import static java.math.BigDecimal.ZERO;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class RoundSalaryBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {
	
	private static final BigDecimal THIRTY = BigDecimal.valueOf(30.00);
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Variable {
		String name();
	}
	
	private static class Deductions {
		
		private static class Deduction{
			private String code;
			private Date endDate;
			private Date startDate;
			private BigDecimal amount;
			private DeductionType type;
		}
		
		private ArrayList<Deduction> deductions = new ArrayList<>();
		
		private BigDecimal getTotalSS(UnaryOperator<BigDecimal> f) {
			return deductions.stream()
			.filter(d -> d.type != null && d.type.isSsDeduction())
			.map(d -> f.apply(d.amount))
			.reduce(ZERO, RoundSalaryBuilder::add);
			//.collect(Collectors.summingDouble( d -> f.apply(d.amount)));
		}

		private BigDecimal getTotal(UnaryOperator<BigDecimal> f) {
			return deductions.stream()
			.map(d -> f.apply(d.amount))
			.reduce(ZERO, RoundSalaryBuilder::add);
		}

		private void addDeduction(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			getDeduction(code, type, startDate, endDate)
			.ifPresentOrElse( 
			deduction -> deduction.amount = deduction.amount.add(bigDecimalValue(amount)), 
			() -> deductions.add(newDeduction(code, type, startDate, endDate, amount))
			);
		}
		
		private Deduction newDeduction(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			
			Deduction deduction = new Deduction();
			deduction.type = type;
			deduction.code = code;
			deduction.endDate = endDate;
			deduction.startDate = startDate;
			deduction.amount = bigDecimalValue(amount);
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
		
		private BigDecimal getTotalEnterprise(UnaryOperator<BigDecimal> f) {
			return add(super.getTotalSS(f),getTotalECSS(f));
		}
		

		private BigDecimal getTotalECSS(UnaryOperator<BigDecimal> f) {
			return super.deductions.stream()
			.filter(d -> AonStringUtils.equalsIgnoreCase(d.code, "ATEP_E") 
					|| AonStringUtils.equalsIgnoreCase(d.code, "ECSS_E") )
			.map(d -> f.apply(d.amount)).reduce(ZERO, RoundSalaryBuilder::add);
		}		

		private void addCost(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			super.addDeduction(code, type, startDate, endDate, amount);
		}
		
		private Map<Period, BigDecimal> getCostsMap( Predicate<? super Deductions.Deduction> predicate ) {
			return 
			super.deductions.stream()
			.filter( predicate )
			.collect(Collectors.toMap(d -> new Period(d.startDate, d.endDate), d -> d.amount, RoundSalaryBuilder::add ))
			;
		}
	}

	private static class Embargos extends Deductions {

		private BigDecimal getTotal(UnaryOperator<BigDecimal> f) {
			return super.getTotal(f);
		}
		
		private void addEmbargo(String code, DeductionType type, Date startDate, Date endDate, double amount) {
			super.deductions.add(super.newDeduction(code, type, startDate, endDate, amount));
		}
		
	}
	
	private static class Bonuses {
		
		private static class Bonus{
			private String name;
			private Date endDate;
			private Date startDate;
			private BigDecimal amount;
		}
		
		private ArrayList<Bonus> bonuses = new ArrayList<>();
		
		private BigDecimal getTotal(UnaryOperator<BigDecimal> f) {
			return bonuses.stream()
			.map(d -> f.apply(d.amount)).reduce(ZERO, RoundSalaryBuilder::add);
		}

		private BigDecimal getTotal(String name, UnaryOperator<BigDecimal> f) {
			return bonuses.stream()
			.filter(d -> AonStringUtils.equals(d.name, name))
			.map(d -> f.apply(d.amount)).reduce(ZERO, RoundSalaryBuilder::add);
		}

		private void addBonus(String name, Date startDate, Date endDate, BigDecimal amount) {
			getBonus(name, startDate, endDate)
			.ifPresentOrElse( 
			bonus -> bonus.amount = add(bonus.amount,amount), 
			() -> bonuses.add(newBonus(name, startDate, endDate, amount))
			)
			;
		}
		
		private Bonus newBonus(String name, Date startDate, Date endDate, BigDecimal amount) {
			
			Bonus bonus = new Bonus();
			bonus.name = name;
			bonus.amount = amount;
			bonus.endDate = endDate;
			bonus.startDate = startDate;
			return bonus;
		}


		private Optional<Bonus> getBonus(String name, Date startDate, Date endDate) {
			for (Bonus bonus : bonuses) {
				if (AonStringUtils.equals(bonus.name, name)
					&& AonUtils.equals(bonus.endDate,endDate)
					&& AonUtils.equals(bonus.startDate,startDate)
					)
					return Optional.of(bonus);
			}
			return Optional.empty();
		}
		
	}

	private static class Payments {
	    	private static final String ZERO_PAYMENT = "ZERO_PAYMENT";
	    
		private static class Irpf{
			private BigDecimal base;
			private PaymentType type; 
		}
		
		private static class Payment{
			private BigDecimal tax;
			private BigDecimal quote;
			private BigDecimal amount;
			
			private Date endDate;
			private Date startDate;
			private IPayment payment;
			private String description;
			private Map<String, ITimedVariable<?>> context;
		}

		private ArrayList<Irpf> irpfs = new ArrayList<>();
		private ArrayList<Payment> payments = new ArrayList<>();
		
		public List<Irpf> getIrpfs() {
			return irpfs;
		}
		
		private void addPayment(BigDecimal amount, BigDecimal quote, BigDecimal tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context ) {
		    Payment p = new Payment();
		    p.amount = amount;
		    p.quote = quote;
		    p.tax = tax;
		    p.description = description;
		    p.startDate = start;
		    p.endDate = end;
		    p.payment = payment;
		    p.context = context;
			    
		    payments.add(p);
		}
		
		private void tryAddIrpf(PaymentType type, BigDecimal irpf) {
			try {
				addIrpf(type, irpf);
			} catch ( Throwable t ) {
				
			}
		}

		private void addIrpf(PaymentType type, BigDecimal irpf) {
			if ( isZero(irpf)) 
				return;
			
			PaymentType finalType = Objects.requireNonNullElse(type, PaymentType.CRA_0000);
			
			getPayment(finalType)
			.ifPresentOrElse( 
			payment -> payment.base = add(payment.base,irpf), 
			() -> irpfs.add(newPayment(finalType, irpf))
			)
			;
		}
		
		private Irpf newPayment(PaymentType type, BigDecimal base) {
			Irpf irpf = new Irpf();
			irpf.base = base;
			irpf.type = type;
			return irpf;
		}


		private Optional<Irpf> getPayment(PaymentType type) {
			for (Irpf irpf : irpfs) {
				if (AonUtils.equals(irpf.type,type))
					return Optional.of(irpf);
			}
			return Optional.empty();
		}
		
		
		private void roundAmount(UnaryOperator<BigDecimal> f, BigDecimal totalPayment) {
		    if ( payments.isEmpty()) {
			return;
		    }
		    // Already rounded, don't touch   
		    BigDecimal round = 
		    payments.stream()
		    .filter(p -> p.amount.compareTo(f.apply(p.amount)) == 0)
		    .map( p -> p.amount )
		    .reduce(ZERO, RoundSalaryBuilder::add);
		    
		    BigDecimal remain = totalPayment.subtract(round);
		    
		    // Need to round, go for it   
		    Payment [] unround =
		    payments.stream()
		    .filter(p -> p.amount.compareTo(f.apply(p.amount)) != 0)
		    .toArray(Payment[]::new);
		    
		    if ( unround.length == 0) {
			return;
		    }

		    Arrays.sort(unround, RoundSalaryBuilder::compare);

		    for (int i = 0; i < ( unround.length - 1 ); i++) {
			if ( unround[i].amount.compareTo(unround[i].quote) == 0) {
			    unround[i].quote = f.apply(unround[i].quote);
			}
			unround[i].amount = f.apply(unround[i].amount);

			remain = remain.subtract(unround[i].amount);
		    }
		    
		    // And de last one, all remain
		    for (int i = unround.length - 1; i < unround.length; i++) {
			if ( unround[i].amount.compareTo(unround[i].quote) == 0) {
			    unround[i].quote = remain;
			}
			unround[i].amount = remain;
		    }
		}

		private void roundQuote(UnaryOperator<BigDecimal> f, BigDecimal commonBase) {
		    if ( payments.isEmpty()) {
			return;
		    }
		    // Already rounded, don't touch   
		    BigDecimal round = 
		    payments.stream()
		    .filter(p -> p.quote.compareTo(f.apply(p.quote)) == 0)
		    .map( p -> p.quote )
		    .reduce(ZERO, RoundSalaryBuilder::add);
		    
		    BigDecimal remain = commonBase.subtract(round);
		    
		    // Need to round, go for it   
		    Payment [] unround =
		    payments.stream()
		    .filter(p -> p.quote.compareTo(f.apply(p.quote)) != 0)
		    .toArray(Payment[]::new);

		    if ( unround.length == 0) {
			return;
		    }

		    Arrays.sort(unround, RoundSalaryBuilder::compare);

		    for (int i = 0; i < ( unround.length - 1 ); i++) {
			unround[i].quote = f.apply(unround[i].quote);
			remain = remain.subtract(unround[i].quote);
		    }
		    
		    // And de last one, all remain
		    for (int i = unround.length - 1; i < unround.length; i++) {
			unround[i].quote = remain;
		    }
		}

		private void fireAdd(ISalaryBuilder<?> salaryBuilder) {
		    payments.forEach(p -> {
			if ( ZERO_PAYMENT.equals(p.description) )
        			salaryBuilder.addZeroPayment(
        			    doubleValue(p.quote), 
        			    doubleValue(p.tax), 
        			    p.startDate, 
        			    p.endDate, 
        			    p.payment, 
        			    p.context);
			else 
        			salaryBuilder.addPayment(
        			    doubleValue(p.amount), 
        			    doubleValue(p.quote), 
        			    doubleValue(p.tax), 
        			    p.description, 
        			    p.startDate, 
        			    p.endDate, 
        			    p.payment, 
        			    p.context);
		});
		}
	}

	protected UnaryOperator<BigDecimal> f;
	protected ISalaryBuilder<T> salaryBuilder;

	public RoundSalaryBuilder(ISalaryBuilder<T> salaryBuilder, UnaryOperator<BigDecimal> f) {
		this.f = f;
		this.salaryBuilder = salaryBuilder;
	}
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	@Override
	public void setContract(Object contract) {
		salaryBuilder.setContract(contract);
	}

	@Override
	public void setCcc(String ccc) {
		salaryBuilder.setCcc(ccc);
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryBuilder.setEnterpriseName(enterpriseName);
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryBuilder.setEnterpriseAddress(enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryBuilder.setEnterpriseDocument(enterpriseDocument);
	}

	@Override
	public void setRegistration(Integer registration) {
		salaryBuilder.setRegistration(registration);
	}

	@Override
	public void setEmployeeName(String employeeName) {
		salaryBuilder.setEmployeeName(employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salaryBuilder.setEmployeeDocument(employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryBuilder.setSocialSecurityNumber(socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		salaryBuilder.setCategory(category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		salaryBuilder.setQuoteGroup(quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salaryBuilder.setSeniorityDate(seniorityDate);
	}

	@Override
	public void setType(SalaryType type) {
		salaryBuilder.setType(type);
	}

	@Override
	public void setIssueDate(Date issueDate) {
		salaryBuilder.setIssueDate(issueDate);
	}

	@Override
	public void setChargeDate(Date issueDate) {
		salaryBuilder.setChargeDate(issueDate);
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		salaryBuilder.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		salaryBuilder.setEndDate(endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		salaryBuilder.setTimeUnits(timeUnits);
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		salaryBuilder.setListener(listener);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		try {
			salaryBuilder.setRawCgcBase(doubleValue(f.apply(bigDecimalValue(rawCgcBase))));
		} catch ( NullPointerException e) {
			salaryBuilder.setRawCgcBase(0.00);
		}
	}

	// ------------------------------------------------------------------------
	
	private Date endDate;
	private Date startDate;
	

	private BigDecimal remuneration;
	private BigDecimal proExtBase;
	private BigDecimal cgpBase;
	private BigDecimal cgcBase;
	private BigDecimal itBase;
	private BigDecimal irpfBase;
	private BigDecimal moneyIrpfBase;
	private BigDecimal inkindIrpfBase;
	private BigDecimal hExtraBase;
	private BigDecimal nonHExtraBase;
	private BigDecimal totalLiquid;
	private BigDecimal totalPayment;
	private BigDecimal totalDeduction;
	private BigDecimal totalIrpf;
	private BigDecimal totalSS;
	private BigDecimal totalEnterprise;
	
	private Costs costs;
	private Bonuses bonuses;
	private Payments payments;
	private Embargos embargos;
	private Deductions deductions;
	
	private ExpressionContext expressionContext;
	
	@Override
	public void createNewSalary() {

		this.remuneration = ZERO;
		this.proExtBase = ZERO;
		this.cgcBase = ZERO;
		this.cgpBase = ZERO;
		this.itBase = ZERO;
		this.irpfBase = ZERO;
		this.moneyIrpfBase = ZERO;
		this.inkindIrpfBase = ZERO;
		this.hExtraBase = ZERO;
		this.nonHExtraBase = ZERO;
		this.totalLiquid = ZERO;
		this.totalPayment = ZERO;
		this.totalDeduction = ZERO;
		this.totalIrpf = ZERO;
		this.totalSS = ZERO;
		this.totalEnterprise = ZERO;
		
		this.costs = new Costs();
		this.bonuses = new Bonuses();
		this.embargos = new Embargos();
		this.payments = new Payments();
		this.deductions = new Deductions();
		
		salaryBuilder.createNewSalary();
	}

	@Override
	public T getSalary() {
		round();
		return salaryBuilder.getSalary();
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		try {
			this.cgcBase = bigDecimalValue(cgcBase);
		} catch (NullPointerException e) {
			// cgcBase parameter is null
		}
		
		try {
			this.cgcBase = round(ContextVariable.CGC_BASE);			
			//round(ContextVariable.CGC_BASE);			
		} catch ( Exception e ) {
			// wrong CGC_BASE
		}
		
		for ( ContextVariable v : ContextVariable.ERE_BASES ) { 
			try {
				this.cgcBase = add(this.cgcBase, round(v));			
			} catch ( Exception e ) {
				// wrong ERE_BASE
			}
		}
		
		try {
			this.cgcBase = add(this.cgcBase, round(ContextVariable.DIRECT_BASE));			
		} catch ( Exception e ) {
			// wrong DIRECT_BASE
		}

		try {
			this.cgcBase = add(this.cgcBase, round(ContextVariable.MATERNITY_BASE));			
		} catch ( Exception e ) {
			// wrong MATERNITY_BASE
		}
		
		try {
			round(ContextVariable.CGC_BASE_ENTERPRISE);			
		} catch ( Exception e ) {
			// wrong CGC_BASE_ENTERPRISE
		}
		
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		try {
			this.cgpBase = bigDecimalValue(cgpBase);
		} catch (NullPointerException e) {
			// cgcBase parameter is null
		}
		try {
			this.cgpBase = round(ContextVariable.CGP_BASE);			
		} catch ( Exception e ) {
			// wrong CGP_BASE
			e.printStackTrace();
		}
		// sum ERE & MATERNIDAD
		for ( ContextVariable v : ContextVariable.ERE_BASES ) { 
			try {
				this.cgpBase = add(this.cgpBase,round(v));			
			} catch ( Exception e ) {
				// wrong ERE_BASE
			}
		}

		try {
			this.cgpBase = add(this.cgpBase, round(ContextVariable.DIRECT_BASE));			
		} catch ( Exception e ) {
			// wrong DIRECT_BASE
		}

		try {
			this.cgpBase = add(this.cgpBase, round(ContextVariable.MATERNITY_BASE));			
		} catch ( Exception e ) {
			// wrong MATERNITY_BASE
		}
		
		try {
			round(ContextVariable.CGP_BASE_ENTERPRISE);			
		} catch ( Exception e ) {
			// wrong CGP_BASE_ENTERPRISE 
		}
	}

	@Override
	public void setItBase(Double itBase) {
		try {
			this.itBase = bigDecimalValue(itBase);
		} catch (NullPointerException e) {
			// itBase parameter is null
		}
	}

	@Override
	public void setRemuneration(Double remuneration) {
		try {
			this.remuneration = bigDecimalValue(remuneration);
		} catch (NullPointerException e) {
			// remuneration parameter is null
		}
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		try {
			this.proExtBase = bigDecimalValue(proExtBase);
		} catch (NullPointerException e) {
			// proExtBase parameter is null
		}
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		try {
			this.irpfBase = bigDecimalValue(irpfBase);
		} catch (NullPointerException e) {
			// irpfBase parameter is null
		}
	}

	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		try {
			this.moneyIrpfBase = bigDecimalValue(moneyIrpfBase);
		} catch (NullPointerException e) {
			// moneyIrpfBase parameter is null
		}
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		try {
			this.inkindIrpfBase = bigDecimalValue(inkindIrpfBase);
		} catch (NullPointerException e) {
			// inkindIrpfBase parameter is null 
		}
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		try {
			this.hExtraBase = bigDecimalValue(hExtraBase);
		} catch (NullPointerException e) {
			// hExtraBase parameter is null
		}
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		try {
			this.nonHExtraBase = bigDecimalValue(nonHExtraBase);
		} catch (NullPointerException e) {
			// nonHExtraBase parameter is null
		}
		try {
			round(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE);			
		} catch ( Exception e ) {
			// wrong CGP_BASE_ENTERPRISE 
		}
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		try {
			this.totalLiquid = bigDecimalValue(totalLiquid);
		} catch (NullPointerException e) {
			// totalLiquid parameter is null
		}
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		try {
			this.totalPayment = bigDecimalValue(totalPayment);
		} catch (NullPointerException e) {
			// totalPayment parameter is null
		}
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		try {
			this.totalDeduction = bigDecimalValue(totalDeduction);
		} catch (NullPointerException e) {
			// totalDeduction parameter is null
		}
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		try {
			this.totalIrpf = bigDecimalValue(totalIrpf);
		} catch (NullPointerException e) {
			// totalIrpf parameter is null
		}
	}

	@Override
	public void setTotalSS(Double socialSecurityContributions) {
		try {
			this.totalSS = bigDecimalValue(socialSecurityContributions);
		} catch (NullPointerException e) {
			// socialSecurityContributions parameter is null
		}
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		try {
			this.totalEnterprise = bigDecimalValue(totalEnterprise);
		} catch (NullPointerException e) {
			// totalEnterprise parameter is null
		}
	}
	
	@Override
	public void setExpressionContext(ExpressionContext expressionContext) {
	    this.expressionContext = expressionContext;
	    override(expressionContext);
	    salaryBuilder.setExpressionContext(expressionContext);
	}

	// ------------------------------------------------------------------------

	@Override
	public void addData(String name, ITimedVariable<?> datas) {
		salaryBuilder.addData(name, datas);
	}

	@Override
	public void addCost(Double amount, String description, Date startDate, Date endDate, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		BigDecimal deduction = getDeductionAmount(cost.getName(), cost.getType(), startDate, endDate);
		amount = doubleValue(f.apply(f.apply( add(deduction,bigDecimalValue(amount)) ).subtract(f.apply(deduction))));
		costs.addCost(cost.getName(), cost.getType(), startDate, endDate, amount);
		salaryBuilder.addCost(amount, description, startDate, endDate, cost, context);

		
		// fix ENTERPRISE_QUOTA 
		expressionContext.removeVariable(ContextVariable.ENTERPRISE_QUOTA);
		costs.getCostsMap(RoundSalaryBuilder::isEnterpriseQuota)
		.forEach((p,v) -> expressionContext.setVariable(ContextVariable.ENTERPRISE_QUOTA, doubleValue(v), p.getStart(), p.getEnd()));
		
		// fix Cost Variable
		if ( AonStringUtils.isNotEmpty(cost.getName()) ) {
			expressionContext.removeVariable(cost.getName());
			costs.getCostsMap(d -> AonStringUtils.equals(cost.getName(), d.code))
			.forEach((p,v) -> expressionContext.setVariable(cost.getName(), doubleValue(v), p.getStart(), p.getEnd()));
		}
	}

	@Override
	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		String name = getName(bonus);
		
		BigDecimal value = bigDecimalValue(amount);

		try {
			if ( isLastDayOfMonth(endDate) ) {
				BigDecimal constant = getConstant(bonus);
				if ( constant != null  ) {
					BigDecimal previous  = bonuses.getTotal(name, f);
					BigDecimal calculated = f.apply(constant.divide(THIRTY, MathContext.DECIMAL128)).multiply(THIRTY);
					if ( previous.add(value).compareTo(calculated) == 0 ){
						value = constant.subtract(previous);
					}
				}
			}
		} catch ( Exception e ) {
			
		}

		bonuses.addBonus(name, startDate, endDate, value);
		
		BigDecimal rounded = f.apply(value);
		salaryBuilder.addBonus(doubleValue(rounded), description, startDate, endDate, bonus, context);
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		payments.addPayment(bigDecimalValue(amount), bigDecimalValue(quote), bigDecimalValue(tax), description, start, end, payment, context);
		payments.tryAddIrpf(payment.getType(), bigDecimalValue(tax));
		//salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context);
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
	    	payments.addPayment(BigDecimal.ZERO, bigDecimalValue(quote), bigDecimalValue(tax), Payments.ZERO_PAYMENT, startDate, endDate, payment, context);
		payments.tryAddIrpf(payment.getType(), bigDecimalValue(tax));		
		//salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
	}

	@Override
	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		deductions.addDeduction(deduction.getName(), deduction.getType(), start, end, amount);
		salaryBuilder.addDeduction(doubleValue(f.apply(bigDecimalValue(amount))), description, start, end, deduction, context);
	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroDeduction(start, end, deduction, context);
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		embargos.addEmbargo(embargo.getName(), embargo.getType(), null, null, amount);
		salaryBuilder.addEmbargo(id, doubleValue(f.apply(bigDecimalValue(amount))), description, embargo, context);
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroEmbargo(id, embargo, context);
	}

	// ------------------------------------------------------------------------
	// PRORATION
	
	public static Double proration(ExpressionContext context, Double amount) {
	   Double raw = ContextFunctions.proration(context, amount);
	   Double round = doubleValue(F.get().apply(bigDecimalValue(raw))); 
	   ROUND.get().put(round, raw);
	   return round;
	}
	
	@Variable(name=ContextFunctions._PRORATION)
	public static Double proration(ExpressionContext context, Double amount, int months) {
	    Double raw = ContextFunctions.proration(context, amount, months);
	    Double round = doubleValue(F.get().apply(bigDecimalValue(raw)));
	    ROUND.get().put(round, raw);
	    return round;
	}

	public static Double proration(ExpressionContext context, Double amount, int start, int end) {
	    Double raw = ContextFunctions.proration(context, amount, start, end);
	    Double round = doubleValue(F.get().apply(bigDecimalValue(raw)));
	    ROUND.get().put(round, raw);
	    return round;
	}
	
	// ------------------------------------------------------------------------
	
	private static final ThreadLocal<Map<Double,Double>> ROUND = new ThreadLocal<>();
	private static final ThreadLocal< UnaryOperator<BigDecimal>> F = new ThreadLocal<>();


	private void override(ExpressionContext expressionContext) {
	    F.set(f);
	    ROUND.set(new HashMap<>());
	    
	    Arrays.stream(RoundSalaryBuilder.class.getMethods() )
	    .filter(method -> method.isAnnotationPresent(Variable.class))
	    .forEach(method -> { 
		MethodStub methodStub = new MethodStub(method);
		String methodName = method.getAnnotation(Variable.class).name();
		expressionContext.getPeriods(methodName)
		.forEach( period ->  expressionContext.setVariable(methodName, methodStub, period.getStart(), period.getEnd()));
	    });
	}
	
	private BigDecimal getTotalSS() {
		return deductions.getTotalSS(f);
	}

	private BigDecimal getTotalDeduction() {
		return deductions.getTotal(f);
	}

	private BigDecimal getTotalEnterprise() {
		return costs.getTotalEnterprise(f);
	}
	
	private BigDecimal getTotalBonuses() {
		return bonuses.getTotal(f);
	}

	private BigDecimal getTotalEmbargo() {
		return embargos.getTotal(f);
	}
	
	private void roundPaymentsAmount(BigDecimal totalPayment) {
	    try {
		payments.roundAmount(f, totalPayment);
	    } catch ( Exception e ) {
	    }
	}

	private void roundPaymentsQuote(BigDecimal commonBase) {
	    try {
		payments.roundQuote(f, commonBase);
	    } catch ( Exception e ) {
	    }
	}

	private void fireAddPayments(ISalaryBuilder<?> salaryBuilder) {
	    payments.fireAdd(salaryBuilder);
	}

	private BigDecimal getDeductionAmount(String code, DeductionType type, Date startDate, Date endDate) {
		if ( AonStringUtils.endsWith(code, "_E"))
			code = AonStringUtils.removeEnd(code, "_E");
		return deductions.getDeduction(code, type, startDate, endDate).map(d -> d.amount).orElse(ZERO);
	}	

	protected void addIrpfQuotas() {
		try {
			getIrpfQuotas(irpfBase, totalIrpf, payments, f).forEach((name, quota) -> salaryBuilder.addData(name, new TimedObject<>(quota, startDate, endDate)));
		} catch ( Throwable t) {
		}
	}
	
	protected void fireRoundData() {
	    ROUND.get().entrySet().stream()
	    		.filter(entry -> entry.getValue() != null && entry.getValue() > 0.00 )
	    		.forEach(entry -> salaryBuilder.addData(ContextVariable.getDecimalNameFor(entry.getKey()),
		    new TimedObject<>(entry.getValue(), startDate, endDate)));
	}

	private void round() {
		
		totalEnterprise = add(getTotalEnterprise(), getTotalBonuses().negate());
		
		totalSS = getTotalSS();
		//totalSS = f.apply(totalSS);
		totalIrpf = f.apply(totalIrpf);
		totalDeduction = getTotalDeduction();
		salaryBuilder.setTotalSS(doubleValue(totalSS));
		salaryBuilder.setTotalIrpf(doubleValue(totalIrpf));
		salaryBuilder.setTotalDeduction(doubleValue(totalDeduction));

		cgcBase = f.apply(cgcBase);

		totalPayment = f.apply(totalPayment);
		roundPaymentsAmount(totalPayment);
		//roundPaymentsQuote(cgcBase);
		fireAddPayments(salaryBuilder);

		BigDecimal totalEmbargo = getTotalEmbargo();

		totalLiquid = add(totalPayment, totalDeduction.negate(),totalEmbargo.negate());
		salaryBuilder.setTotalPayment(doubleValue(totalPayment));
		salaryBuilder.setTotalLiquid(doubleValue(totalLiquid));

		irpfBase = f.apply(irpfBase);
		inkindIrpfBase = f.apply(inkindIrpfBase);
		moneyIrpfBase = add(irpfBase, inkindIrpfBase.negate());
		remuneration = f.apply(remuneration);
		salaryBuilder.setIrpfBase(doubleValue(irpfBase));
		salaryBuilder.setMoneyIrpfBase(doubleValue(moneyIrpfBase));
		salaryBuilder.setInkindIrpfBase(doubleValue(inkindIrpfBase));
		salaryBuilder.setRemuneration(doubleValue(remuneration));

		// ---
		itBase = f.apply(itBase);
		proExtBase = f.apply(proExtBase);
		salaryBuilder.setItBase(doubleValue(itBase));
		salaryBuilder.setProExtBase(doubleValue(proExtBase));

		hExtraBase = f.apply(hExtraBase);
		nonHExtraBase = f.apply(nonHExtraBase);
		salaryBuilder.setHExtraBase(doubleValue(hExtraBase));
		salaryBuilder.setCgcBase(doubleValue(cgcBase));
		salaryBuilder.setNonHExtraBase(doubleValue(nonHExtraBase));

		cgpBase = add(cgcBase,nonHExtraBase,hExtraBase).min(f.apply(cgpBase));
		salaryBuilder.setCgpBase(doubleValue(cgpBase));

		totalEnterprise = f.apply(totalEnterprise);
		salaryBuilder.setTotalEnterprise(doubleValue(totalEnterprise));
		
		addIrpfQuotas();
		fireRoundData();
		
	}

	protected double round(double d) {
		return doubleValue(f.apply(bigDecimalValue(d)));
	}
	
	private BigDecimal round(ContextVariable contextVariable) {
		return round(contextVariable.getName());
	}

	private BigDecimal round(String name) { 
		BigDecimal sum = ZERO;
		for (ITimedVariable<Object> v : /*expressionContext.*/getVariables(name)) {
			RoundVarible roundVarible = new RoundVarible(v, d -> f.apply(bigDecimalValue(d)));
			sum = add( sum, roundVarible.getValue(roundVarible.getPeriod()));
			expressionContext.putVariable(name, roundVarible);
		}
		return sum;
	}
	

	private List<ITimedVariable<Object>> getVariables(String name){
		LinkedList<ITimedVariable<Object>> variables = new LinkedList<>();
		
		List<ITimedVariable<Object>> expanded = expressionContext.getVariables(name);
		Collections.sort(expanded, (v1,v2) -> Period.compare(v1.getPeriod().getStart(), v2.getPeriod().getStart()));
		
		for (ITimedVariable<Object> variable : expanded) {
			if ( variables.isEmpty() ) {
				variables.addLast(variable);
				continue;
			}
			
			ITimedVariable<Object> previous = variables.peekLast();
			
			if ( isITFirst15Days(variable.getPeriod())) {
				if (isITFirst15Days(previous.getPeriod())) {
					variable = join(variable, variables.removeLast());
				} 
			} else if ( isDropDays(variable.getPeriod()) 
					&& isWorkedOrDropDays(previous.getPeriod()) ) {
					variable = join(variable, variables.removeLast());
			} else if ( isDropDays(previous.getPeriod()) 
					&& isWorkedOrDropDays(variable.getPeriod()) ) {
					variable = join(variable, variables.removeLast());
			}

			
			variables.addLast(variable);
		}
		
		return variables;
	}

	private ITimedVariable<Object> join(ITimedVariable<Object> current, ITimedVariable<Object> prev) {
		double value = add(current.getValue(current.getPeriod()) ,prev.getValue(prev.getPeriod()));
		current = new TimedObject<>(value, prev.getPeriod().getStart(), current.getPeriod().getEnd());
		return current;
	}
	
	private boolean isDropDays(Period p) {
		return contains(p, ContextVariable.DROP_DAYS);
	}

	private boolean isWorkedOrDropDays(Period p) {
		return contains(p, ContextVariable.WORKED_DAYS, ContextVariable.DROP_DAYS);
	}

	private boolean isITFirst15Days(Period p) {
		return contains(p, COMMON_DISEASE_DAYS_4_15, COMMON_DISEASE_DAYS_1_3);
	}

	private boolean contains(Period p, ContextVariable ...vars) {
		for ( ContextVariable var : vars ) {
			if ( expressionContext.containsVariable(var.getName(),p.getStart(),p.getEnd()) ) 
				return true;
		}
		return false;
	}
	
	private static boolean isZero(BigDecimal val) {
		return val == null || val.compareTo(ZERO) == 0;
	}
	
	private static double doubleValue(BigDecimal val) {
		return val.doubleValue();
	}

	private static BigDecimal bigDecimalValue(double val) {
		return BigDecimal.valueOf(val);
	}

	private static BigDecimal bigDecimalValue(Object val) {
		return BigDecimal.valueOf(((Number)val).doubleValue()).setScale(10, RoundingMode.HALF_UP);
	}

	private static BigDecimal bigDecimalValue(Double val) {
		return BigDecimal.valueOf(val);
	}

	private static BigDecimal add(BigDecimal ...vals) {
		BigDecimal add = ZERO;
		for (BigDecimal val : vals)
			add = add.add(val);
		return add;
	}

	private static double add(Object ...vals) {
		BigDecimal decimals [] = new BigDecimal[vals.length];
		for (int i = 0; i < vals.length; i++ )
			decimals[i] = bigDecimalValue(vals[i]);
		return doubleValue(add(decimals));
	}
	
	private static boolean isEnterpriseQuota(com.esferalia.aon.payroll.calculator.RoundSalaryBuilder.Deductions.Deduction d) {
		
		DeductionType type = d.type;
		
		if ( type == null ) 
			return false;
		
		switch (type) {
		case BONUS:
			return true;
		case FOGASA:
			return true;
		case IN_KIND: // ATEP_E & ECSS_E	
			return true;
		case UNEMPLOYMENT:
			return true;
		case JOB_TRAINING:
			return true;
		case COMMON_CONTINGENCY:
			return true;
		case STRUCTURAL_OVERTIME:
			return true;
		case NON_STRUCTURAL_OVERTIME:
			return true;
		case PROFESSIONAL_CONTINGENCY:
			return true;
		case IRPF:
			return false;
		case OTHER:
			return false;
		case EMBARGO:
			return false;
		case ADVANCE_PAYMENT:
			return false;
		default:
			return false;
		}
		
	}
	
	private static String getIrpfName( PaymentType type ) {
		return String.format("%s_IRPF", type.name());
	}

	private static String getBaseName( PaymentType type ) {
		return String.format("%s_BASE", type.name());
	}
	
	private static Map<String, BigDecimal> getIrpfQuotas(BigDecimal totalIrpfBase, BigDecimal totalIrpfQuota, Payments payments, UnaryOperator<BigDecimal> f ) {
		
		if ( isZero(totalIrpfBase))
			return Collections.emptyMap();
		//if ( isZero(totalIrpfQuota))
		//	return Collections.emptyMap();
		
		Map<String, BigDecimal> irpfQuotasMap = new HashMap<>();
		
		List<Payments.Irpf> paymentsIrpfs = payments.getIrpfs();		
		
		for ( int i = 0; i < paymentsIrpfs.size() -1 ; i++ ) {
			Payments.Irpf paymentIrpf = paymentsIrpfs.get(i);
			BigDecimal paymentIrpfBase = paymentIrpf.base;
			PaymentType paymentIrpfType = paymentIrpf.type;
			
			BigDecimal irpfQuota = f.apply(
					paymentIrpfBase
					.multiply(totalIrpfQuota)
					.divide(totalIrpfBase,MathContext.DECIMAL128));
			BigDecimal irpfBase = f.apply(paymentIrpfBase);

			irpfQuotasMap.put(getIrpfName(paymentIrpfType), irpfQuota);
			irpfQuotasMap.put(getBaseName(paymentIrpfType), irpfBase);
		}
		
		// last payment by difference .
		for ( int i = paymentsIrpfs.size()-1; i < paymentsIrpfs.size() ; i++ ) {
			Payments.Irpf paymentIrpf = paymentsIrpfs.get(i);
			PaymentType paymentIrpfType = paymentIrpf.type;
			BigDecimal sumIrpfQuota = irpfQuotasMap
					.entrySet().stream()
					.filter(entry -> entry.getKey().endsWith("IRPF"))
					.map(Entry::getValue).reduce(ZERO, RoundSalaryBuilder::add); 
			
			BigDecimal irpfQuota = add(totalIrpfQuota, sumIrpfQuota.negate()); 
			irpfQuotasMap.put(getIrpfName(paymentIrpfType), irpfQuota);
			
			BigDecimal sumIrpfBase = irpfQuotasMap
					.entrySet().stream()
					.filter(entry -> entry.getKey().endsWith("BASE"))
					.map(Entry::getValue).reduce(ZERO, RoundSalaryBuilder::add); 
			
			BigDecimal irpfBase = add(totalIrpfBase, sumIrpfBase.negate()); 
			irpfQuotasMap.put(getBaseName(paymentIrpfType), irpfBase);
		}
		
		return irpfQuotasMap;
	}
	
	private static String getName(IBonus bonus) {
		String name = bonus.getName();
		if ( AonStringUtils.isNotBlank(name))
			return AonStringUtils.trim(name);
		
		return AonStringUtils.trim(bonus.getExpression());
	}
	
	private static BigDecimal getConstant(IBonus bonus) {
		String description = bonus.getDescription();
		if ( AonStringUtils.isBlank(description) )
			return null;
		Pattern constantPattern = Pattern.compile("BON\\.P\\.F\\.EMPL\\.CUANTIA\\s*\\(\\s*(?<amount>[0-9,]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = constantPattern.matcher(description);
		if ( !matcher.matches() )
			return null;
		
		String amount = matcher.group("amount");
		amount = AonStringUtils.replace(amount, ".", "");
		amount = AonStringUtils.replace(amount, ",", ".");
		return new BigDecimal(amount);
	}
	
	private static boolean isLastDayOfMonth(Date date) {
		int dayOfMonth = AonDateUtils.get(date, Calendar.DAY_OF_MONTH);
		int lastDayOfMonth = AonDateUtils.getMax(date, Calendar.DAY_OF_MONTH);
		return dayOfMonth == lastDayOfMonth;
	}
	
	private static Pattern ORDER = Pattern.compile("^\\[(\\d+)\\].*$");

	private static int compare(Payments.Payment p0, Payments.Payment p1) {

	    	String description0 = p0.description;
		String description1 = p1.description;
		try { 
        		// By order
        		Matcher matcher0 = ORDER.matcher(description0);
        		Matcher matcher1 = ORDER.matcher(description1);
        		boolean order0 = matcher0.matches();
        		boolean order1 = matcher1.matches();
        		if ( order0 && !order1)
        		    return -1; 			//p0 < p1
        		else if ( order1 && !order0 )
        		    return 1;			//p0 > p1
        		else if ( order1 /*&& order0 != null*/ )
        		    return Integer.parseInt(matcher0.group(1)) - Integer.parseInt(matcher1.group(1));
        		else
        		    return 0;
		} catch( Exception e ){
		    return 0;
		}
		
	}
	

}
