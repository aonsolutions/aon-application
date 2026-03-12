package com.esferalia.aon.occam.api.model;

import static com.esferalia.aon.watson.util.AonDateUtils.compare;
import static com.esferalia.aon.watson.util.AonDateUtils.max;
import static com.esferalia.aon.watson.util.AonDateUtils.min;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class Salary implements Serializable {

	public static class ContextData {
		String expression;
		Date startDate;
		Date endDate;

		public Date getEndDate() {
			return endDate;
		}

		public Date getStartDate() {
			return startDate;
		}

		public String getExpression() {
			return expression;
		}
		
		private boolean intersectsWith(Date startDate, Date endDate){
			return compare(max(startDate,this.startDate),min(endDate,this.endDate)) <= 0;
		}
	}
	public static class Payment {
		String name;
		Double irpf;
		Double quote;
		Double amount;
		String expression;
		String description;
		PaymentType paymentType;

		public Payment(Double amount, Double quote, String expression, String description, String name, PaymentType paymentType) {
			super();
			this.quote = quote;
			this.amount = amount;
			this.name = name;
			this.description = description;
			this.expression = expression;
			this.paymentType = paymentType;
		}

		public Double getQuote() {
			return quote;
		}
		
		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}
		
		public String getExpression() {
			return expression;
		}
		
		public String getName() {
			return name;
		}
		
		public PaymentType getPaymentType() {
			return paymentType;
		}
		
		public void setPaymentType(PaymentType paymentType) {
			this.paymentType = paymentType;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null ) {
				return false;
			}
			if ( !(obj instanceof Payment) ){
				return false;
			}
			
			Payment payment = (Payment) obj;
			
			return 
			AonUtils.equals(this.name, payment.name)
//			&& AonUtils.equals(this.irpf, payment.irpf)
			&& AonUtils.equals(this.quote, payment.quote)
			&& AonUtils.equals(this.amount, payment.amount)
			&& AonUtils.equals(this.expression, payment.expression)
			&& AonUtils.equals(this.description, payment.description)
			&& AonUtils.equals(this.paymentType, payment.paymentType)
			;
		}
		
		@Override
		public int hashCode() {
			return AonUtils.hashCode(description);
		}

	}

	public static class Deduction {
		String name;
		Double amount;
		String description;
		DeductionType deductionType;

		public Deduction(Double amount, String description, String name, DeductionType deductionType) {
			super();
			this.name = name;
			this.amount = amount;
			this.description = description;
			this.deductionType =deductionType;
		}
		
		public String getName() {
			return name;
		}

		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}

		public boolean isMei() {
			return false;
		}

		public boolean isIrpf() {
			return false;
		}

		public boolean isSolidarity() {
			return false;
		}

		public boolean isJobTraining() {
			return false;
		}

		public boolean isUnemployment() {
			return false;
		}

		public boolean isCommonContingency() {
			return false;
		}

		public boolean isProfesionalContingency() {
			return false;
		}
		
		public DeductionType getDeductionType() {
			return deductionType;
		}
		

	}


	public static class CommonContingecyDeduction extends Deduction {

		public CommonContingecyDeduction(Double amount, String description) {
			super(amount, description, "CGC", DeductionType.COMMON_CONTINGENCY);
		}

		@Override
		public boolean isCommonContingency() {
			return true;
		}
	}
	
	public static class MeiDeduction extends Deduction {

		public MeiDeduction(Double amount, String description) {
			super(amount, description, "MEI", DeductionType.MEI);
		}

		@Override
		public boolean isMei() {
			return true;
		}
	}

	public static class ProfessionalContingecyDeduction extends Deduction {

		public ProfessionalContingecyDeduction(Double amount, String description) {
			super(amount, description, "CGP", DeductionType.PROFESSIONAL_CONTINGENCY);
		}

		@Override
		public boolean isProfesionalContingency() {
			return true;
		}
	}
	
	public static class UnemploymentDeduction extends Deduction{

		public UnemploymentDeduction(Double amount, String description) {
			super(amount, description, "DESMPL", DeductionType.UNEMPLOYMENT);
		}
		
		@Override
		public boolean isUnemployment() {
			return true;
		}

	}

	public static class JobTrainingDeduction extends Deduction{

		public JobTrainingDeduction(Double amount, String description) {
			super(amount, description, "FP", DeductionType.JOB_TRAINING);
		}
		
		@Override
		public boolean isJobTraining() {
			return true;
		}

	}

	public static class SolidarityDeduction extends Deduction {

		public SolidarityDeduction(Double amount, String name, String description) {
			super(amount, description, name, DeductionType.SOLIDARITY);
		}

		@Override
		public boolean isSolidarity() {
			return true;
		}
	}


	public static class Cost {
		
		String name;
		Double amount;
		String description;
		DeductionType costType;

		public Cost(Double amount, String description, String name, DeductionType costType) {
			super();
			this.name = name;
			this.amount = amount;
			this.description = description;
			this.costType = costType;
		}
		
		public String getName() {
			return name;
		}
		
		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}


		public boolean isIT() {
			return false;
		}

		public boolean isIMS() {
			return false;
		}

		public boolean isSEA() {
			return false;
		}

		public boolean isMei() {
			return false;
		}

		public boolean isFogasa() {
			return false;
		}

		public boolean isSolidarity() {
			return false;
		}

		public boolean isJobTraining() {
			return false;
		}

		public boolean isUnemployment() {
			return false;
		}

		public boolean isCommonContingency() {
			return false;
		}

		public boolean isProfesionalContingency() {
			return false;
		}
		
		public DeductionType getCostType() {
			return costType;
		}
	}

	public static class CommonContingecyCost extends Cost {

		public CommonContingecyCost(Double amount, String description, String code) {
			super(amount, description, code, DeductionType.COMMON_CONTINGENCY);
		}

		public CommonContingecyCost(Double amount, String description) {
			super(amount, description, "CGC_E", DeductionType.COMMON_CONTINGENCY);
		}

		@Override
		public boolean isCommonContingency() {
			return true;
		}
	}
	
	public static class MeiCost extends Cost {

		public MeiCost(Double amount, String description) {
			super(amount, description, "MEI_E", DeductionType.MEI);
		}

		@Override
		public boolean isMei() {
			return true;
		}
	}

	public static class UnemploymentCost extends Cost{

		public UnemploymentCost(Double amount, String description) {
			super(amount, description, "DESMPL_E", DeductionType.UNEMPLOYMENT);
		}
		
		@Override
		public boolean isUnemployment() {
			return true;
		}

	}

	public static class JobTrainingCost extends Cost{

		public JobTrainingCost(Double amount, String description) {
			super(amount, description, "FP_E", DeductionType.JOB_TRAINING);
		}
		
		@Override
		public boolean isJobTraining() {
			return true;
		}

	}
	
	public static class ITCost extends Cost {

		public ITCost(Double amount, String description) {
			super(amount, description, "IT_E", DeductionType.IT);
		}

		@Override
		public boolean isIT() {
			return true;
		}
	}
	
	public static class IMSCost extends Cost {

		public IMSCost(Double amount, String description) {
			super(amount, description, "IMS_E", DeductionType.IMS);
		}

		@Override
		public boolean isIMS() {
			return true;
		}
	}

	public static class FogasaCost extends Cost {

		public FogasaCost(Double amount, String description) {
			super(amount, description, "FOGASA_E", DeductionType.FOGASA);
		}

		@Override
		public boolean isFogasa() {
			return true;
		}
	}
	
	public static class SolidarityCost extends Cost {

		public SolidarityCost(Double amount, String name, String description) {
			super(amount, description, name, DeductionType.SOLIDARITY);
		}

		@Override
		public boolean isSolidarity() {
			return true;
		}
	}

	public static class SEACost extends Cost {

		public SEACost(Double amount, String name, String description) {
			super(amount, description, name, DeductionType.SEA);
		}

		@Override
		public boolean isSEA() {
			return true;
		}
	}

	public static class Bonus {
		Double amount;
		String description;

		public Bonus(Double amount, String description) {
			super();
			this.amount = amount;
			this.description = description;
		}

		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}
		
	}
	
	public static class Embargo {
		Double amount;
		String description;

		public Embargo(Double amount, String description) {
			super();
			this.amount = amount;
			this.description = description;
		}

		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}
		
	}
	
	
	private Integer id;
	private SalaryType salaryType;

	private Date startDate;
	private Date endDate;
	private Date issueDate;
	private Date chargeDate;
	private int salaryDays;

	// Enterprise related data
	private String enterpriseCCC;
	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;

	// Employee related data
	private String employeeName;
	private String employeeSSNumber;
	private String employeeDocument;
	private Date employeeSeniorityDate;
	private String employeeQuoteGroup;
	private String employeeCategory;

	// Totals
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	private Double totalEnterprise;
	private Double totalIrpf;
	private Double totalSSContributions;
	private Double remuneration;

	// Tax (I.R.P.F) bases
	private Double irpfBase;
	private double moneyIrpfBase;
	private double inkindIrpfBase;

	// Quote ( SS ) bases
	private Double commonContingenciesBase;
	private Double professionalContingenciesBase;
	private Double estructuralOvertimeBase;
	private Double nonEstructuralOvertimeBase;
	private Double extraProrationBase;

	private List<Cost> costs;
	private List<Embargo> embargos;
	private List<Bonus> bonuses;
	private List<Payment> payments;
	private List<Deduction> deductions;
	private Map<String, List<ContextData>> contextdata;

	public Salary() {
		costs = new ArrayList<Cost>();
		embargos = new ArrayList<Embargo>();
		bonuses = new ArrayList<Bonus>();
		payments = new ArrayList<Payment>();
		deductions = new ArrayList<Deduction>();
		contextdata = new HashMap<String, List<ContextData>>();
	}

	public Integer getId() {
		return id;
	}

	public Salary setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public SalaryType getSalaryType() {
		return salaryType;
	}
	
	public Salary setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
		return this;
	}
	
	public Salary setSalaryType(Byte salaryType) {
		this.salaryType = typeOf(salaryType, SalaryType.class);
		return this;
	}

	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public Salary setEnterpriseCCC(String enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
		return this;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public Salary setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public Salary setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}

	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}

	public Salary setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public Salary setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}

	public String getEmployeeSSNumber() {
		return employeeSSNumber;
	}

	public Salary setEmployeeSSNumber(String employeeSSNumber) {
		this.employeeSSNumber = employeeSSNumber;
		return this;
	}

	public String getEmployeeDocument() {
		return employeeDocument;
	}

	public Salary setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
		return this;
	}

	public Date getEmployeeSeniorityDate() {
		return employeeSeniorityDate;
	}

	public Salary setEmployeeSeniorityDate(Date employeeSeniorityDate) {
		this.employeeSeniorityDate = employeeSeniorityDate;
		return this;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public Salary setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getChargeDate() {
		return chargeDate;
	}

	public Salary setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
		return this;
	}

	public String getEmployeeQuoteGroup() {
		return employeeQuoteGroup;
	}

	public Salary setEmployeeQuoteGroup(String employeeQuoteGroup) {
		this.employeeQuoteGroup = employeeQuoteGroup;
		return this;
	}

	public String getEmployeeCategory() {
		return employeeCategory;
	}

	public Salary setEmployeeCategory(String employeeCategory) {
		this.employeeCategory = employeeCategory;
		return this;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public Salary setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
		return this;
	}

	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public Salary setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
		return this;
	}

	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public Salary setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
		return this;
	}

	public Double getTotalEnterprise() {
		return totalEnterprise;
	}

	public Salary setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise = totalEnterprise;
		return this;
	}

	public Double getTotalIrpf() {
		return totalIrpf;
	}

	public Salary setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
		return this;
	}

	public Double getTotalSSContributions() {
		return totalSSContributions;
	}

	public Salary setTotalSSContributions(Double totalSSContributions) {
		this.totalSSContributions = totalSSContributions;
		return this;
	}

	public Double getRemuneration() {
		return remuneration;
	}

	public Salary setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
		return this;
	}

	public Double getIrpfBase() {
		return irpfBase;
	}

	public Salary setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
		return this;
	}

	public double getMoneyIrpfBase() {
		return moneyIrpfBase;
	}

	public Salary setMoneyIrpfBase(double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
		return this;
	}

	public double getInkindIrpfBase() {
		return inkindIrpfBase;
	}

	public Salary setInkindIrpfBase(double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
		return this;
	}

	public Double getCommonContingenciesBase() {
		return commonContingenciesBase;
	}

	public Salary setCommonContingenciesBase(Double commonContingenciesBase) {
		this.commonContingenciesBase = commonContingenciesBase;
		return this;
	}

	public Double getProfessionalContingenciesBase() {
		return professionalContingenciesBase;
	}

	public Salary setProfessionalContingenciesBase(
			Double professionalContingenciesBase) {
		this.professionalContingenciesBase = professionalContingenciesBase;
		return this;
	}

	public Double getEstructuralOvertimeBase() {
		return estructuralOvertimeBase;
	}

	public Salary setEstructuralOvertimeBase(Double estructuralOvertimeBase) {
		this.estructuralOvertimeBase = estructuralOvertimeBase;
		return this;
	}

	public Double getNonEstructuralOvertimeBase() {
		return nonEstructuralOvertimeBase;
	}

	public Salary setNonEstructuralOvertimeBase(
			Double nonEstructuralOvertimeBase) {
		this.nonEstructuralOvertimeBase = nonEstructuralOvertimeBase;
		return this;
	}

	public Double getExtraProrationBase() {
		return extraProrationBase;
	}

	public Salary setExtraProrationBase(Double extraProrationBase) {
		this.extraProrationBase = extraProrationBase;
		return this;
	}

	public int getSalaryDays() {
		return salaryDays;
	}

	public Salary setSalaryDays(int salaryDays) {
		this.salaryDays = salaryDays;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Salary setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Salary setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public List<Bonus> getBonuses() {
		return Collections.unmodifiableList(bonuses);
	}

	public void addBonus(String code, String description,
			Double amount) {
		System.out.println(code + " = " + amount);
		bonuses.add(new Bonus(amount, description));
	}
	
	public List<Embargo> getEmbargos() {
		return Collections.unmodifiableList(embargos);
	}
	
	public void addEmbargo(String description,
			Double amount) {
		embargos.add(new Embargo(amount, description));
	}
	
	public List<Payment> getPayments() {
		return Collections.unmodifiableList(payments);
	}

	public void addPayment(String name, String expression, String description,
			Double amount, Double quote, PaymentType paymentType) {
		Payment payment = new Payment(amount, quote, expression, description, name, paymentType);
		
		payments.add(payment);
	}
	public void addPayment(String name, String expression, String description,
			Double amount, Double quote, Byte paymentType) {
		addPayment(name, expression, description, amount, quote, typeOf(paymentType,PaymentType.class));
	}
	
	public List<Cost> getCosts() {
		return Collections.unmodifiableList(costs);
	}
	
	public void addCost(
			DeductionType type,
			String code,
			Double amount,
			String description
			) {
		addCost((byte)type.ordinal(), code, description, amount, type);
	}

	public void addCost(Byte type, String code, String description,
			Double amount, Byte costType) {
		addCost(costType, code, description, amount, typeOf(costType, DeductionType.class));
	}
	
	public void addCost(Byte type, String code, String description,
			Double amount, DeductionType costType) {
		Cost cost = null ;
		if ( AonStringUtils.equals("IT_E", code) ) 
			type = (byte)DeductionType.IT.ordinal();
		else if ( AonStringUtils.equals("IMS_E", code) ) 
			type = (byte)DeductionType.IMS.ordinal();

		try {
			cost = DeductionType.deductionTypeOf(type)
				.accept(new DeductionType.Visitor<Cost>() {
				@Override
				public Cost visitCommonContigency(DeductionType deductionType) {
					return new CommonContingecyCost(amount, description, code );
				}
				
				@Override
				public Cost visitMEI(DeductionType deductionType) {
				    return new MeiCost(amount, description);
				}

				@Override
				public Cost visitUnemployent(
						DeductionType deductionType) {
					return new UnemploymentCost(amount, description);
				}

				@Override
				public Cost visitFogasa(
						DeductionType deductionType) {
					return new FogasaCost(amount, description);
				}
				
				@Override
				public Cost visitJobTraining(
						DeductionType deductionType) {
					return new JobTrainingCost(amount, description);
				}
				
				@Override
				public Cost visitIT(DeductionType deductionType) {
					return new ITCost(amount, description);
				}

				@Override
				public Cost visitIMS(DeductionType deductionType) {
					return new IMSCost(amount, description);
				}
				
				@Override
				public Cost visitSolidarity(DeductionType deductionType) {
					return new SolidarityCost(amount, code, description);
				}
				
				@Override
				public Cost visitSEA(DeductionType deductionType) {
					return new SEACost(amount, code, description);
				}
				
			});
		} catch (Exception e) {
		}
		if ( cost == null )
			cost = new Cost(amount, description, code, costType);
		
		costs.add(cost);
	}

	public List<Deduction> getDeductions() {
		return Collections.unmodifiableList(deductions);
	}
	
	public void addDeduction(String code, String description,
			Double amount) {
		deductions.add(new Deduction(amount, description, code,  null));
	}

	public void addDeduction(DeductionType deductionType, String description,
			Double amount) {
		addDeduction((byte)deductionType.ordinal(), null, description, amount, deductionType);
	}

	public void addDeduction(Byte type, String code, String description,
			Double amount, Byte deductionType) {
		addDeduction(deductionType, code, description, amount, typeOf(deductionType, DeductionType.class));
	}
	
	public void addDeduction(Byte type, String code, String description,
			Double amount, DeductionType deductionType) {
		Deduction deduction = null ;
		try {
			deduction = DeductionType.deductionTypeOf(type)
				.accept(new DeductionType.Visitor<Deduction>() {
				@Override
				public Deduction visitCommonContigency(DeductionType deductionType) {
					return new CommonContingecyDeduction(amount, description);
				}
				
				@Override
				public Deduction visitMEI(DeductionType deductionType) {
				    return new MeiDeduction(amount, description);
				}
				
				@Override
				public Deduction visitUnemployent(
						DeductionType deductionType) {
					return new UnemploymentDeduction(amount, description);
				}
				
				@Override
				public Deduction visitJobTraining(
						DeductionType deductionType) {
					return new JobTrainingDeduction(amount, description);
				}
				
				@Override
				public Deduction visitProfessionalContigency(
						DeductionType deductionType) {
					
					return new ProfessionalContingecyDeduction(amount, description);
				}
				
				@Override
				public Deduction visitSolidarity(DeductionType deductionType) {
					return new SolidarityDeduction(amount, code, description);
				}
				
			});
		} catch (Exception e) {
		}
		if ( deduction == null )
			deduction = new Deduction(amount, description, code, deductionType);
		
		deductions.add(deduction);
	}

	public void addDeduction(
			DeductionType type,
			Double amount,
			String code,
			String description
			) {
		addDeduction((byte)type.ordinal(), code, description, amount, type);
	}

	public Map<String, List<ContextData>> getContextData() {
		return Collections.unmodifiableMap(contextdata);
	}

	public <A, R> R getContextData(String name,
			Collector<? super String, A, R> collector) {
		List<ContextData> datas = contextdata.get(name);
		if (datas == null)
			return null;
		return datas.stream().map(d -> d.expression).collect(collector);
	}

	public Salary setContextData(String name, String value, Date startDate,
			Date endDate) {

		List<ContextData> datas = contextdata.computeIfAbsent(name, k -> new ArrayList<>());
		
		ContextData contextData = new ContextData();
		contextData.endDate = endDate;
		contextData.startDate = startDate;
		contextData.expression = value;
		int index = Collections.binarySearch(datas, contextData,
				(d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		if (index >= 0) {
			datas.add(index, contextData);
		} else {
			// index = -(insertion_point) - 1
			// index + insertion_point = -1
			// insertion_point = -index -1
			int insertionPoint = -index - 1;
			datas.add(insertionPoint, contextData);
		}
		return this;
	}
	
	public final List<ContextData> getContextData(String name,  Date startDate,
			Date endDate) {
		List<ContextData> ret = new ArrayList<>();
		
		List<ContextData> datas = contextdata.get(name);
		
		if ( datas != null && !datas.isEmpty() )
			for(ContextData data: datas )
				if ( data.intersectsWith(startDate, endDate))
					ret.add(data);
		
		return ret;
	}
	
	public final void addContextData(String name,  String value, Date startDate,
			Date endDate) {
		List<ContextData> list = getContextData(name, startDate, endDate);
		if ( list.isEmpty() ) {
			setContextData(name, value, startDate, endDate);
		}
	}
	
	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
		if ( ordinal == null )
			return null;
		try {
			return type.getEnumConstants()[ordinal];
		} catch ( Exception e ) {
			return null;
		}
	}
}
