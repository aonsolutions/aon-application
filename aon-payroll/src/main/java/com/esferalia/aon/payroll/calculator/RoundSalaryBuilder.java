package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_1_3;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_4_15;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class RoundSalaryBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {
	
	private static final BigDecimal ZERO = new BigDecimal(0); 
	

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
			private BigDecimal amount;
			private Date endDate;
			private Date startDate;
		}
		
		private ArrayList<Bonus> bonuses = new ArrayList<>();
		
		private BigDecimal getTotal(UnaryOperator<BigDecimal> f) {
			return bonuses.stream()
			.map(d -> f.apply(d.amount)).reduce(ZERO, RoundSalaryBuilder::add);
		}

		private void addBonus(Date startDate, Date endDate, BigDecimal amount) {
			getBonus(startDate, endDate)
			.ifPresentOrElse( 
			bonus -> bonus.amount = add(bonus.amount,amount), 
			() -> bonuses.add(newBonus(startDate, endDate, amount))
			)
			;
		}
		
		private Bonus newBonus(Date startDate, Date endDate, BigDecimal amount) {
			
			Bonus bonus = new Bonus();
			bonus.amount = amount;
			bonus.endDate = endDate;
			bonus.startDate = startDate;
			return bonus;
		}


		private Optional<Bonus> getBonus(Date startDate, Date endDate) {
			for (Bonus bonus : bonuses) {
				if (AonUtils.equals(bonus.endDate,endDate)
					&& AonUtils.equals(bonus.startDate,startDate)
					)
					return Optional.of(bonus);
			}
			return Optional.empty();
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
		salaryBuilder.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
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
		bonuses.addBonus(startDate, endDate, bigDecimalValue(amount));
		BigDecimal rounded = f.apply(bigDecimalValue(amount));
		salaryBuilder.addBonus(doubleValue(rounded), description, startDate, endDate, bonus, context);
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context);
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
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
	
	private BigDecimal getDeductionAmount(String code, DeductionType type, Date startDate, Date endDate) {
		if ( AonStringUtils.endsWith(code, "_E"))
			code = AonStringUtils.removeEnd(code, "_E");
		return deductions.getDeduction(code, type, startDate, endDate).map(d -> d.amount).orElse(ZERO);
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

		BigDecimal totalEmbargo = getTotalEmbargo();
		//totalEmbargo = f.apply(totalEmbargo);
		totalPayment = f.apply(totalPayment);
		totalLiquid = add(totalPayment, totalDeduction.negate(),totalEmbargo.negate());
		salaryBuilder.setTotalPayment(doubleValue(totalPayment));
		salaryBuilder.setTotalLiquid(doubleValue(totalLiquid));

		moneyIrpfBase = f.apply(moneyIrpfBase);
		inkindIrpfBase = f.apply(inkindIrpfBase);
		remuneration = f.apply(remuneration);
		irpfBase = add(moneyIrpfBase, inkindIrpfBase);
		salaryBuilder.setIrpfBase(doubleValue(irpfBase));
		salaryBuilder.setMoneyIrpfBase(doubleValue(moneyIrpfBase));
		salaryBuilder.setInkindIrpfBase(doubleValue(inkindIrpfBase));
		salaryBuilder.setRemuneration(doubleValue(remuneration));

		// ---
		itBase = f.apply(itBase);
		proExtBase = f.apply(proExtBase);
		salaryBuilder.setItBase(doubleValue(itBase));
		salaryBuilder.setProExtBase(doubleValue(proExtBase));

		cgcBase = f.apply(cgcBase);
		hExtraBase = f.apply(hExtraBase);
		nonHExtraBase = f.apply(nonHExtraBase);
		salaryBuilder.setHExtraBase(doubleValue(hExtraBase));
		salaryBuilder.setCgcBase(doubleValue(cgcBase));
		salaryBuilder.setNonHExtraBase(doubleValue(nonHExtraBase));

		cgpBase = add(cgcBase,nonHExtraBase,hExtraBase).min(f.apply(cgpBase));
		salaryBuilder.setCgpBase(doubleValue(cgpBase));

		totalEnterprise = f.apply(totalEnterprise);
		salaryBuilder.setTotalEnterprise(doubleValue(totalEnterprise));
		
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
	
	private static double doubleValue(BigDecimal val) {
		return val.doubleValue();
	}

	private static BigDecimal bigDecimalValue(double val) {
		return BigDecimal.valueOf(val);
	}

	private static BigDecimal bigDecimalValue(Object val) {
		return BigDecimal.valueOf(((Number)val).doubleValue()).setScale(10, RoundingMode.HALF_UP);
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
	

}
