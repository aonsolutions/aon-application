package com.esferalia.aon.payroll.calculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;

public class CollectSalaryBuilder<T  extends ISalary> implements ISalaryBuilder<T> {

	private Object contract;
	private String ccc;
	private String enterpriseName;
	private String enterpriseAddress;
	private String category;
	private String socialSecurityNumber;
	private String employeeDocument;
	private String employeeName;
	private String quoteGroup;
	private Integer registration;
	private String enterpriseDocument;
	private Date startDate;
	private Date chargeDate;
	private Date issueDate;
	private SalaryType type;
	private Date seniorityDate;
	private Date endDate;

	private Double totalEnterprise ;
	private Double totalPayment ;
	private Double totalLiquid;
	private Double totalDeduction;
	private Double totalIrpf;
	private Double totalSS;
	private Double nonHExtraBase;
	private Double hExtraBase;
	private Double inkindIrpfBase;
	private Double moneyIrpfBase;
	private Double irpfBase;
	private Double proExtBase;
	private Double remuneration;
	private Double cgpBase;
	private Double cgcBase;
	private Double rawCgcBase;
	private Double itBase;
	private Integer timeUnits;
	
	
	
	private Collection<Data> datas;
	private Collection<Cost> costs;
	private Collection<Bonus> bonuses;
	private Collection<Embargo> embargos;
	private Collection<Embargo> zeroEmbargos;
	private Collection<Payment> payments;
	private Collection<Payment> zeroPayments;
	private Collection<Deduction> deductions;
	private Collection<Deduction> zeroDeductions;
	
	
	
	private static class Data {
		String name;
		ITimedVariable<?> var;

		public Data(String name, ITimedVariable<?> var) {
			this.name = name;
			this.var = var instanceof IExpressionVariable<?> ? 
					new ExpressionVariable((IExpressionVariable<?>)var): 
					new TimedVariable(var);
			
		}
		
	}
	
	private static class TimedVariable<V> implements ITimedVariable<V> {
		
		V value ;
		Period period;
		
		public TimedVariable(ITimedVariable<V> var) {
			this.period = var.getPeriod();
			this.value = var.getValue(this.period);
		}

		@Override
		public Period getPeriod() {
			return period;
		}

		@Override
		public V getValue(Period period) {
			return value;
		}

	}
	private static class ExpressionVariable<V> extends TimedVariable<V> implements IExpressionVariable<V> {
		
		IExpression expression;
		Map<String, ITimedVariable<?>> context;
		
		public ExpressionVariable(IExpressionVariable<V> var) {
			super(var);
			this.context = var.getContext();
			this.expression = var.getExpression();
		}

		@Override
		public IExpression getExpression() {
			return expression;
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return context;
		}
		
	}
	
	private static class Cost {
		Double amount; 
		IDeduction cost;		
		String description;
		Map<String, ITimedVariable<?>> context;
		
		public Cost(Double amount, IDeduction cost, String description, Map<String, ITimedVariable<?>> context) {
			super();
			this.amount = amount;
			this.description = description;
			this.context = context;
			this.cost = new ContractDeduction(cost);
		}
		
	}
	
	private static class Bonus {
		Double amount; 
		IBonus bonus;		
		String description;
		Map<String, ITimedVariable<?>> context;
		
		public Bonus(Double amount, IBonus bonus, String description, Map<String, ITimedVariable<?>> context) {
			super();
			this.amount = amount;
			this.bonus = bonus;
			this.description = description;
			this.context = context;
		}
		
	}

	private static class Deduction {
		Date start;
		Date end;
		Double amount; 
		String description;
		IDeduction deduction;		
		Map<String, ITimedVariable<?>> context;
		public Deduction(Date start, Date end, Double amount, String description, IDeduction deduction,
				Map<String, ITimedVariable<?>> context) {
			super();
			this.start = start;
			this.end = end;
			this.amount = amount;
			this.description = description;
			this.context = context;
			this.deduction = new ContractDeduction(deduction);
		}
		
	}

	private static class Embargo {
		Integer id; 
		Double amount; 
		String description;
		IDeduction embargo;		
		Map<String, ITimedVariable<?>> context;
		
		public Embargo(Integer id, Double amount, String description, IDeduction embargo,
				Map<String, ITimedVariable<?>> context) {
			super();
			this.id = id;
			this.amount = amount;
			this.description = description;
			this.context = context;
			this.embargo = new ContractDeduction(embargo);
		}
		
	}

	private static class Payment {
		

		Double tax; 
		Double quote; 
		Double amount; 
		String description;
		IPayment payment;
		Date start;
		Date end;
		Map<String, ITimedVariable<?>> context;
		
		public Payment(Double tax, Double quote, Double amount, String description, IPayment payment, Date start,
				Date end, Map<String, ITimedVariable<?>> context) {
			super();
			this.tax = tax;
			this.quote = quote;
			this.amount = amount;
			this.description = description;
			this.start = start;
			this.end = end;
			this.context = context;
			this.payment = new ContractPayment(payment);
		}
		
		
	}

	private static class ContractPayment implements IContractPayment{
		String name;
		double amount;
		String description;
		PaymentType type;
		String expression;
		
		Integer id;
		Integer conceptId;
		Month month;
		Date endDate;
		Date startDate;
		boolean readOnly;
		boolean descriptionDecorable;
		ExpressionScope scope;
		String irpfExpression;
		String quoteExpression;
		SalaryType salaryType;
		
		public ContractPayment(IPayment payment) {
			type = payment.getType();
			name = payment.getName();
			description = payment.getDescription();
			expression = payment.getExpression();
			try {
				amount = payment.getAmount();
			} catch ( NullPointerException e) {
			} catch ( UnsupportedOperationException e) {
			}
			
			if ( !(payment instanceof IContractPayment))
				return;
			IContractPayment contractPayment = (IContractPayment)payment;
			id = contractPayment.getId();
			conceptId = contractPayment.getConceptId();
			salaryType = contractPayment.getSalaryType();
			scope = contractPayment.getScope();
			endDate = contractPayment.getEndDate();
			startDate = contractPayment.getStartDate();
			//readOnly = contractPayment.isReadOnly();
			descriptionDecorable = contractPayment.isDescriptionDecorable();
			month = contractPayment.getMonth();
			irpfExpression = contractPayment.getIrpfExpression();
			quoteExpression = contractPayment.getQuoteExpression();
			
		}
		
		
		
		@Override
		public PaymentType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return expression;
		}



		@Override
		public ExpressionScope getScope() {
			return scope;
		}



		@Override
		public boolean isReadOnly() {
			return readOnly;
		}



		@Override
		public Date getStartDate() {
			return startDate;
		}



		@Override
		public Date getEndDate() {
			return endDate;
		}



		@Override
		public Integer getId() {
			return id;
		}



		@Override
		public Month getMonth() {
			return month;
		}



		@Override
		public Integer getConceptId() {
			return conceptId;
		}



		@Override
		public String getIrpfExpression() {
			return irpfExpression;
		}



		@Override
		public String getQuoteExpression() {
			return quoteExpression;
		}



		@Override
		public SalaryType getSalaryType() {
			return salaryType;
		}



		@Override
		public boolean isDescriptionDecorable() {
			return descriptionDecorable;
		}
		
	}
	
	private static class ContractDeduction implements IContractDeduction{
		String name;
		double amount;
		String description;
		DeductionType type;
		String expression;
		
		Integer id;
		Month month;
		Date endDate;
		Date startDate;
		ExpressionScope scope;
		
		public ContractDeduction(IDeduction deduction) {
			type = deduction.getType();
			name = deduction.getName();
			description = deduction.getDescription();
			expression = deduction.getExpression();
			try {
				amount = deduction.getAmount();
			} catch ( NullPointerException e) {
			} catch ( UnsupportedOperationException e) {
			}
			
			if ( !(deduction instanceof IContractDeduction))
				return;
			IContractDeduction contractDeduction = (IContractDeduction)deduction;
			id = contractDeduction.getId();
			scope = contractDeduction.getScope();
			endDate = contractDeduction.getEndDate();
			startDate = contractDeduction.getStartDate();
			
		}
		
		
		
		@Override
		public DeductionType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return expression;
		}



		@Override
		public ExpressionScope getScope() {
			return scope;
		}



		@Override
		public Date getStartDate() {
			return startDate;
		}



		@Override
		public Date getEndDate() {
			return endDate;
		}



		@Override
		public Integer getId() {
			return id;
		}



		@Override
		public boolean isReadOnly() {
			return false;
		}



	}
	
	
	
	public CollectSalaryBuilder() {
		reset();
	}
	
	public void collect(ISalaryBuilder<?>  builder) {
		builder.createNewSalary();
		// 
		builder.setContract(contract);
		builder.setCcc(ccc);
		builder.setEnterpriseName(enterpriseName);
		builder.setEnterpriseAddress(enterpriseAddress);
		builder.setCategory(category);
		builder.setSocialSecurityNumber(socialSecurityNumber);
		builder.setEmployeeDocument(employeeDocument);
		builder.setEmployeeName(employeeName);
		builder.setQuoteGroup(quoteGroup);
		builder.setRegistration(registration);
		builder.setEnterpriseDocument(enterpriseDocument);
		builder.setStartDate(startDate);
		builder.setChargeDate(chargeDate);
		builder.setIssueDate(issueDate);
		builder.setType(type);
		builder.setSeniorityDate(seniorityDate);
		builder.setEndDate(endDate);

		// amounts, bases...
		builder.setTotalEnterprise(totalEnterprise);
		builder.setTotalPayment(totalPayment);
		builder.setTotalLiquid(totalLiquid);
		builder.setTotalDeduction(totalDeduction);
		builder.setTotalIrpf(totalIrpf);
		builder.setTotalSS(totalSS);
		builder.setNonHExtraBase(nonHExtraBase);
		builder.setHExtraBase(hExtraBase);
		builder.setInkindIrpfBase(inkindIrpfBase);
		builder.setMoneyIrpfBase(moneyIrpfBase);
		builder.setIrpfBase(irpfBase);
		builder.setProExtBase(proExtBase);
		builder.setRemuneration(remuneration);
		builder.setCgpBase(cgpBase);
		builder.setCgcBase(cgcBase);
		builder.setRawCgcBase(rawCgcBase);
		builder.setItBase(itBase);
		builder.setTimeUnits(timeUnits);
		
		//
		
		for ( Data data: datas )
			builder.addData(
					data.name, 
					data.var);
		
		for ( Payment payment: payments )
			builder.addPayment(
					payment.amount, 
					payment.quote, 
					payment.tax, 
					payment.description, 
					payment.start, 
					payment.end, 
					payment.payment, 
					payment.context);
		
		for ( Payment zeroPayment: zeroPayments )
			builder.addZeroPayment(
					zeroPayment.quote, 
					zeroPayment.tax, 
					zeroPayment.start, 
					zeroPayment.end, 
					zeroPayment.payment, 
					zeroPayment.context);
		
		for ( Deduction deduction: deductions )
			builder.addDeduction(
					deduction.amount, 
					deduction.description, 
					deduction.start, 
					deduction.end, 
					deduction.deduction, 
					deduction.context);
		
		for ( Deduction zeroDeduction:  zeroDeductions)
			builder.addZeroDeduction(
					zeroDeduction.start, 
					zeroDeduction.end, 
					zeroDeduction.deduction, 
					zeroDeduction.context);
		
		for ( Cost cost: costs ) 
			builder.addCost(
					cost.amount, 
					cost.description, 
					cost.cost, 
					cost.context);
		
		for ( Bonus bonus: bonuses ) 
			builder.addBonus(
					bonus.amount, 
					bonus.description, 
					bonus.bonus, 
					bonus.context);
		
		for ( Embargo embargo: embargos )
			builder.addEmbargo(
					embargo.id, 
					embargo.amount, 
					embargo.description, 
					embargo.embargo, 
					embargo.context);
		
		builder.getSalary();
	}
	
	// --------------------------------------------------------- ISalaryBuilder

	@Override
	public T getSalary() {
		return null;
	}

	@Override
	public void createNewSalary() {
	}
	
	@Override
	public void setContract(Object contract) {
		this.contract = contract;
	}
	
	@Override
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	
	@Override
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}

	@Override
	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	@Override
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	@Override
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	@Override
	public void setType(SalaryType type) {
		this.type = type;
	}

	@Override
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	@Override
	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = Period.min(startDate, this.startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = Period.max(endDate, this.endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		this.timeUnits += timeUnits;
	}

	@Override
	public void setItBase(Double itBase) {
		this.itBase += itBase;
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		this.rawCgcBase += rawCgcBase;
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		this.cgcBase += cgcBase;
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		this.cgpBase += cgpBase;
	}

	@Override
	public void setRemuneration(Double remuneration) {
		this.remuneration += remuneration;
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		this.proExtBase += proExtBase;
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		this.irpfBase += irpfBase;
	}

	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		this.moneyIrpfBase += moneyIrpfBase;
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		this.inkindIrpfBase += inkindIrpfBase;
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		this.hExtraBase += hExtraBase;
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		this.nonHExtraBase += nonHExtraBase;
	}

	@Override
	public void setTotalSS(Double totalSS) {
		this.totalSS += totalSS;
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		this.totalIrpf += totalIrpf;
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction += totalDeduction;
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid += totalLiquid;
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment += totalPayment;
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise += totalEnterprise;
	}

	@Override
	public void addData(String name, ITimedVariable<?> var) {
		datas.add(new Data(name, var));
	}

	@Override
	public void addCost(Double amount, String description, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		costs.add(new Cost(amount, cost, description, context));
	}

	@Override
	public void addBonus(Double amount, String description, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		bonuses.add(new Bonus(amount, bonus, description, context));
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		payments.add( new Payment(tax, quote, amount, description, payment, start, end, context));
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		zeroPayments.add(new Payment(tax, quote, null, null, payment, startDate, endDate, context));
	}

	@Override
	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		deductions.add(new Deduction(start, end, amount, description, deduction, context));
	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		zeroDeductions.add(new Deduction(start, end, null, null, deduction, context));
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		embargos.add(new Embargo(id, amount, description, embargo, context));
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		zeroEmbargos.add(new Embargo(id, null, null, embargo, context));
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub
	}

	
	private void reset() {
		
		this.startDate = null;
		this.endDate = new Date(0); // January 1, 1970, 00:00:00 GMT
		
		this.totalEnterprise = 0.00 ;
		this.totalPayment = 0.00  ;
		this.totalLiquid = 0.00 ;
		this.totalDeduction = 0.00 ;
		this.totalIrpf = 0.00 ;
		this.totalSS = 0.00 ;
		this.nonHExtraBase = 0.00 ;
		this.hExtraBase = 0.00 ;
		this.inkindIrpfBase = 0.00 ;
		this.moneyIrpfBase = 0.00 ;
		this.irpfBase = 0.00 ;
		this.proExtBase = 0.00 ;
		this.remuneration = 0.00 ;
		this.cgpBase = 0.00 ;
		this.cgcBase = 0.00 ;
		this.rawCgcBase = 0.00 ;
		this.itBase = 0.00 ;
		this.timeUnits = 0 ;
		
		datas = new ArrayList<Data>();
		costs = new ArrayList<Cost>();
		bonuses = new ArrayList<Bonus>();
		embargos = new ArrayList<Embargo>();
		payments = new ArrayList<Payment>();
		zeroPayments = new ArrayList<Payment>();
		deductions = new ArrayList<Deduction>();
		zeroDeductions = new ArrayList<Deduction>();
		
	}

}
