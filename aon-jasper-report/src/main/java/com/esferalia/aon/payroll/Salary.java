package com.esferalia.aon.payroll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.BaseSalary;
import com.esferalia.aon.salary.payment.CompensationOrPrepaidExpenses;
import com.esferalia.aon.salary.payment.NoEstructuralOvertimeHours;
import com.esferalia.aon.salary.payment.OtherNonWages;
import com.esferalia.aon.salary.payment.OvertimeHours;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.salary.payment.SalaryInKind;
import com.esferalia.aon.salary.payment.SalarySupplements;
import com.esferalia.aon.salary.payment.SpecialBonuses;
import com.esferalia.aon.salary.payment.SpecialSecurityBenefits;

import net.aonsolutions.payroll.report.CompositeDeduction;
import net.aonsolutions.payroll.report.Deduction;
import net.aonsolutions.payroll.report.Payment;

public class Salary {
	
	private Integer id;
	private Contract contract;
	private WorkPlace workplace;
	private Enterprise enterprise;
	private RegistryAddress registryAddress;
	
	private Date startDate;
	private Date endDate;
	private Date issueDate;
	private Date chargeDate;
	private SalaryType type;
	private Integer timeUnits;
	
	private Double remuneration;
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	private Double totalEnterprise;
	private Double totalIrpf;

	private Double itBase;
	private Double irpfBase;
	private Double commonBase;
	private Double professionalBase;
	private Double overtimeBase;
	private Double extraPayProration;
	private Double nonEstructuralOvertimeBase;
	private Double socialSecurityContributions;
	private Double moneyIrpfBase;
	private Double inkindIrpfBase;
	


	private String ccc;
	private Integer enterpriseId;
	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;
	
	private String employeeName;
	private String employeeDocument;
	private String socialSecurityNumber;

	private String province;

	private String category;
	private String quoteGroup;
	private Date seniorityDate;
	
	private Collection<Deduction> costs;
	private Collection<Payment> payments;
	private Collection<Deduction> deductions;
	
	
	public Salary() {
		contract = new Contract(this);
		workplace = new WorkPlace(this);
		enterprise = new Enterprise(this);
		registryAddress = new RegistryAddress(this);
		costs = new ArrayList<Deduction>();
		payments = new ArrayList<Payment>();
		deductions = new ArrayList<Deduction>();
	}
	public Salary  getSalary() {
		return this;
	}
	
	public Payments  getPayments() {
		return new Payments(this);
	}

	public Deductions  getDeductions() {
		return new Deductions(this);
	}

	public Costs  getEnterpriseCosts() {
		return new Costs(this);
	}

	public Integer getId() {
		return id;
	}

	public Salary setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Contract getContract() {
		return contract;
	}
	
	public WorkPlace getWorkPlace() {
		return workplace;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	public RegistryAddress getRegistryAddress() {
		return registryAddress;
	}
	
	public SalaryType getType() {
		return type;
	}
	
	public Salary setType(Byte type) {
		this.type = SalaryType.values()[type];
		return this;
	}
	
	public Date getStartDate() {
		return this.startDate;
	}
	public Salary setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return this.endDate;
	}
	public Salary setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Date getChargeDate() {
		return chargeDate;
	}
	
	public Salary setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
		return this;
	}
	

	public Date getIssueDate() {
		return issueDate;
	}
	
	public Salary setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	
	public int getIssueMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.issueDate);
		return calendar.get(Calendar.MONTH);
	}

	public int getIssueYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.issueDate);
		return calendar.get(Calendar.YEAR);
	}

	public Integer getTimeUnits() {
		return this.timeUnits;
	}
	public Salary setTimeUnits(Integer timeUnits) {
		this.timeUnits = timeUnits;
		return this;
	}

	public String getCcc() {
		return this.ccc;
	}
	public Salary setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	
	public Salary setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
		return this;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}
	
	public Salary setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public String getProvince() {
		return this.province;
	}
	public Salary setProvince(String province) {
		this.province = province;
		return this;
	}

	public String getEnterpriseAddress() {
		return this.enterpriseAddress;
	}
	public Salary setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}

	public String getEnterpriseDocument() {
		return this.enterpriseDocument;
	}
	public Salary setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}
	
	public String getCategory() {
		return this.category;
	}
	public Salary setCategory(String category) {
		this.category = category;
		return this;
	}

	public String getQuoteGroup() {
		return this.quoteGroup;
	}
	public Salary setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
		return this;
	}
	
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	
	public Salary setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		return this;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}
	public Salary setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}
	
	public String getEmployeeDocument() {
		return this.employeeDocument;
	}
	public Salary setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
		return this;
	}

	public String getSocialSecurityNumber() {
		return this.socialSecurityNumber;
	}
	public Salary setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
		return this;
	}

	public double getItBase() {
		return this.itBase;
	}
	public Salary setItBase(Double itBase) {
		this.itBase = itBase;
		return this;
	}

	public Double getIrpfBase() {
		return this.irpfBase;
	}
	public Salary setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
		return this;
	}

	public Double getMoneyIrpfBase() {
		return this.moneyIrpfBase;
	}
	public Salary setMoneyIrpfBase(Double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
		return this;
	}

	public double getInkindIrpfBase() {
		return this.inkindIrpfBase;
	}
	
	public Double getInKindIrpfBase() {
		return this.inkindIrpfBase;
	}

	public Salary setInKindIrpfBase(Double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
		return this;
	}

	public Double getCommonBase() {
		return this.commonBase;
	}
	public Salary setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
		return this;
	}

	public Double getProfessionalBase() {
		return this.professionalBase;
	}
	public Salary setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
		return this;
	}

	public Double getOvertimeBase() {
		return this.overtimeBase;
	}
	public Salary setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
		return this;
	}

	public Double getNonEstructuralOvertimeBase() {
		return this.nonEstructuralOvertimeBase;
	}
	public Salary setNonEstructuralOvertimeBase(Double nonEstructuralOvertimeBase) {
		this.nonEstructuralOvertimeBase = nonEstructuralOvertimeBase;
		return this;
	}

	public Double getTotalIrpf() {
		return totalIrpf;
	}
	
	public Salary setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
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

	public Double getSocialSecurityContributions() {
		return this.socialSecurityContributions;
	}
	public Salary setSocialSecurityContributions(Double socialSecurityContributions) {
		this.socialSecurityContributions = socialSecurityContributions;
		return this;
	}

	public Double getRemuneration() {
		return this.remuneration;
	}
	public Salary setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
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

	public Double getExtraPayProration() {
		return this.extraPayProration;
	}
	public Salary setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
		return this;
	}

	public Salary addPayment(Payment payment) {
		payments.add(payment);
		return this;
	}

	public Salary addCost(Deduction deduction) {
		costs.add(deduction);
		return this;
	}

	public Salary addDeduction(Deduction deduction) {
		deductions.add(deduction);
		return this;
	}
	
	public BaseSalary getBaseSalary() {
		List<Payment> salaryInKind =  
		payments.stream()
		.filter( p -> p.isBaseSalary() ) 
		.collect(Collectors.toList())
		;
		return new BaseSalary(salaryInKind);
	}

	public SalaryInKind getSalaryInKind() {
		List<Payment> salaryInKind =  
		payments.stream()
		.filter( p -> p.isSalaryInKind() ) 
		.collect(Collectors.toList())
		;
		return new SalaryInKind(salaryInKind);
	}

	public OtherNonWages getOtherNonWages() {
		List<Payment> otherNonWages =  
		payments.stream()
		.filter( p -> p.isOtherNonWage() ) 
		.collect(Collectors.toList())
		;
		return new OtherNonWages(otherNonWages);
	}

	public OvertimeHours getOvertimeHours() {
		List<Payment> overTimeHours =  
		payments.stream()
		.filter( p -> p.isOvertimeHour() ) 
		.collect(Collectors.toList())
		;
		return new OvertimeHours(overTimeHours);
	}

	public NoEstructuralOvertimeHours getNoEstructuralOvertimeHours() {
		List<Payment> overTimeHours =  
		payments.stream()
		.filter( p -> p.isNonOvertimeHour() ) 
		.collect(Collectors.toList())
		;
		return new NoEstructuralOvertimeHours(overTimeHours);
	}
	
	public SpecialBonuses getSpecialBonuses() {
		List<Payment> bonuses =  
		payments.stream()
		.filter( p -> p.isSpecialBonus() ) 
		.collect(Collectors.toList())
		;
		return new SpecialBonuses(bonuses);
	}

	public SalarySupplements getSalarySupplements() {
		List<Payment> supplements =  
		payments.stream()
		.filter( p -> p.isSupplement() ) 
		.collect(Collectors.toList())
		;
		return new SalarySupplements(supplements);
	}

	public SpecialSecurityBenefits getSpecialSecurityBenefits() {
		List<Payment> expenses =  
		payments.stream()
		.filter( p -> p.isExpense() ) 
		.collect(Collectors.toList())
		;
		return new SpecialSecurityBenefits(expenses);
	}

	public CompensationOrPrepaidExpenses getCompensationOrPrepaidExpenses() {
		List<Payment> expenses =  
		payments.stream()
		.filter( p -> p.isExpense() ) 
		.collect(Collectors.toList())
		;
		return new CompensationOrPrepaidExpenses(expenses);
	}
	
	public IDeduction getIrpf() {
		List<Deduction> irpfs =
		deductions.stream()
		.filter( d -> d.isIrpf() )
		.collect(Collectors.toList())
		;
		return irpfs.isEmpty() ? null : new CompositeDeduction<Deduction>(irpfs);
	}

	public IDeduction getOther() {
		List<Deduction> others =
		deductions.stream()
		.filter( d -> d.isOther() )
		.collect(Collectors.toList())
		;
		return others.isEmpty() ? null : new CompositeDeduction<Deduction>(others);
	}

	public IDeduction getJobTrainning() {
		List<Deduction> jobTrainnings =
		deductions.stream()
		.filter( d -> d.isJobtraining() )
		.collect(Collectors.toList())
		;
		return jobTrainnings.isEmpty() ? null : new CompositeDeduction<Deduction>(jobTrainnings);
	}

	public IDeduction getAdvancePayment() {
		List<Deduction> advances =
		deductions.stream()
		.filter( d -> d.isAdvancePayment() )
		.collect(Collectors.toList())
		;
		return advances.isEmpty() ? null : new CompositeDeduction<Deduction>(advances);
	}

	public IDeduction getUnemployment() {
		List<Deduction> unemployment =    
		deductions.stream()
		.filter( d -> d.isUnemployment() )
		.collect(Collectors.toList())
		;
		return unemployment.isEmpty() ? null : new CompositeDeduction<Deduction>(unemployment);
	}

	public IDeduction getCommonContingency() {
		List<Deduction> cgcs =
		deductions.stream()
		.filter( d -> d.isCommonContingency() )
		.collect(Collectors.toList())
		;
		return cgcs.isEmpty() ? null : new CompositeDeduction<Deduction>(cgcs);
	}

	public IDeduction getStructuralOvertime() {
		List<Deduction> overtimes =
		deductions.stream()
		.filter( d -> d.isStructuralOvertime() )
		.collect(Collectors.toList())
		;
		return overtimes.isEmpty() ? null : new CompositeDeduction<Deduction>(overtimes);
	}

	public IDeduction getNonStructuralOvertime() {
		List<Deduction> overtimes =
		deductions.stream()
		.filter( d -> d.isNonStructuralOvertime() )
		.collect(Collectors.toList())
		;
		return overtimes.isEmpty() ? null : new CompositeDeduction<Deduction>(overtimes);
	}

	public IDeduction getAtepIt() {
		List<Deduction> its =
		deductions.stream()
		.filter( d -> d.isIT() )
		.collect(Collectors.toList())
		;
		return its.isEmpty() ? null : new CompositeDeduction<Deduction>(its);
	}

	public IDeduction getAtepIms() {
		List<Deduction> imss =
		deductions.stream()
		.filter( d -> d.isIMS() )
		.collect(Collectors.toList())
		;
		return imss.isEmpty() ? null : new CompositeDeduction<Deduction>(imss);
	}

	public IDeduction getFogasa() {
		List<Deduction> fogasas =
		deductions.stream()
		.filter( d -> d.isFogasa() )
		.collect(Collectors.toList())
		;
		return fogasas.isEmpty() ? null : new CompositeDeduction<Deduction>(fogasas);
	}
	
	public IDeduction getJobTrainningCost() {
		List<Deduction> jobTrainnings =
		costs.stream()
		.filter( d -> d.isJobtraining() )
		.collect(Collectors.toList())
		;
		return  jobTrainnings.isEmpty() ? null : new CompositeDeduction<Deduction>(jobTrainnings);
	}

	public IDeduction getStructuralOvertimeCost() {
		List<Deduction> overtimes =
		costs.stream()
		.filter( d -> d.isStructuralOvertime() )
		.collect(Collectors.toList())
		;
		return new CompositeDeduction<Deduction>(overtimes);
	}

	public IDeduction getNonStructuralOvertimeCost() {
		List<Deduction> overtimes =
		costs.stream()
		.filter( d -> d.isNonStructuralOvertime() )
		.collect(Collectors.toList())
		;
		return new CompositeDeduction<Deduction>(overtimes);
	}

	public IDeduction getUnemploymentCost() {
		List<Deduction> unemployments =
		costs.stream()
		.filter( d -> d.isUnemployment() )
		.collect(Collectors.toList())
		;
		return new CompositeDeduction<Deduction>(unemployments);
	}

	public IDeduction getCommonContingencyCost() {
		List<Deduction> gcs =
		costs.stream()
		.filter( d -> d.isCommonContingency() )
		.collect(Collectors.toList())
		;
		return new CompositeDeduction<Deduction>(gcs);
	}
	
	public Collection<Payment> getOrderedPayment() {
		List<Payment> list = new LinkedList<Payment>();
		list.addAll(getSalarySupplements().getValues());
		Collections.sort(list, new Comparator<Payment>() {
			@Override
			public int compare(Payment p1, Payment p2) {
				if ( p1.getCRA() == p2.getCRA() ) {
					return p1.getDescription().compareTo(p2.getDescription());
				}
				if ( p2 == null || p1.getCRA() > p2.getCRA() )
					return 1;
				else //if ( p1 == null || p1.getCRA() < p2.getCRA() )
					return -1;
			}
		});
		return list;
	}
	
	
	public Set<Payment> getSalaryPayments() {
		return payments.stream().collect(Collectors.toSet());
	}
	
	public Set<Deduction> getSalaryDeductions() {
		return deductions.stream().collect(Collectors.toSet());
	}

	public Set<Deduction> getSalaryEmbargos() {
		return Collections.emptySet(); //deductions.stream().collect(Collectors.toSet());
	}
}