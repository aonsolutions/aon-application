package com.esferalia.aon.gwt.payroll.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ql.ast.Expression;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

public class DBSalary  implements ISalary, ISalaryProxy {
	
	
	private static class DBSalaryItem<T extends Enum<T> & IResourceable> implements ISalaryItem<T>{
		
		private T type;
		private String name;
		private Double amount;
		private String expression;
		private String description;

		@Override
		public T getType() {
			return type;
		}
		public DBSalaryItem<T> setType(T type) {
			this.type = type;
			return this;
		}

		@Override
		public String getName() {
			return name;
		}
		public DBSalaryItem<T> setName(String name) {
			this.name = name;
			return this;
		}
		
		@Override
		public double getAmount() {
			return amount;
		}
		public DBSalaryItem<T> setAmount(Double amount) {
			this.amount = amount;
			return this;
		}

		@Override
		public String getDescription() {
			return description;
		}
		public DBSalaryItem<T> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public String getExpression() {
			return expression;
		}
		public DBSalaryItem<T> setExpression(String expression) {
			this.expression = expression;
			return this;
		}
		
	}
	
	private static class DBPayment extends DBSalaryItem<PaymentType> implements IPayment{
	}
	
	private static class  DBDeduction extends DBSalaryItem<DeductionType> implements IDeduction{
	}

	private Integer id;
	private Contract contract;
	private int domain;
	private SalaryType type;
	private Date startDate;
	private Date endDate;
	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;
	private String ccc;
	private byte ssRegime;
	private String employeeName;
	private String socialSecurityNumber;
	private String employeeDocument;
	private Date seniorityDate;
	private String quoteGroup;
	private String category;
	private Integer registration;
	private Integer timeUnits;
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	private Double totalEnterprise;
	private Date issueDate;
	private Double remuneration;
	private Double extraPayProration;
	private double itBase;
	private Double rawCommonBase;
	private Double commonBase;
	private Double overtimeBase;
	private Double nonEstructuralOvertimeBase;
	private Double professionalBase;
	private double moneyIrpfBase;
	private double inkindIrpfBase;
	private Double irpfBase;
	private Double socialSecurityContributions;
	private Double totalIrpf;
	private Date chargeDate;
	
	
	private List<IDeduction> costs;
	private List<IPayment> payments;
	private List<IDeduction> deductions;
	
	
	public DBSalary() {
		costs = new ArrayList<IDeduction>();
		payments = new ArrayList<IPayment>();
		deductions = new ArrayList<IDeduction>();
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	public DBSalary setId(Integer id) {
		this.id = id;
		return this;
	}
	
	@Override
	public SalaryType getType() {
		return type;
	}
	public DBSalary setType(SalaryType type) {
		this.type = type;
		return this;
	}
	@Override
	public Date getStartDate() {
		return startDate;
	}
	public DBSalary setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	@Override
	public Date getEndDate() {
		return endDate;
	}
	public DBSalary setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	@Override
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public DBSalary setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}
	@Override
	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}
	public DBSalary setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}
	@Override
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public DBSalary setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}
	@Override
	public String getCcc() {
		return ccc;
	}
	public DBSalary setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	@Override
	public String getEmployeeName() {
		return employeeName;
	}
	public DBSalary setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}
	@Override
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}
	public DBSalary setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
		return this;
	}
	@Override
	public String getEmployeeDocument() {
		return employeeDocument;
	}
	public DBSalary setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
		return this;
	}
	@Override
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	public DBSalary setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		return this;
	}
	@Override
	public String getQuoteGroup() {
		return quoteGroup;
	}
	public DBSalary setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
		return this;
	}
	@Override
	public String getCategory() {
		return category;
	}
	public DBSalary setCategory(String category) {
		this.category = category;
		return this;
	}
	@Override
	public Integer getRegistration() {
		return registration;
	}
	public DBSalary setRegistration(Integer registration) {
		this.registration = registration;
		return this;
	}
	@Override
	public Integer getTimeUnits() {
		return timeUnits;
	}
	public DBSalary setTimeUnits(Integer timeUnits) {
		this.timeUnits = timeUnits;
		return this;
	}
	@Override
	public Double getTotalPayment() {
		return totalPayment;
	}
	public DBSalary setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
		return this;
	}
	@Override
	public Double getTotalDeduction() {
		return totalDeduction;
	}
	public DBSalary setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
		return this;
	}
	@Override
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public DBSalary setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
		return this;
	}
	@Override
	public Double getTotalEnterprise() {
		return totalEnterprise;
	}
	public DBSalary setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise = totalEnterprise;
		return this;
	}
	@Override
	public Date getIssueDate() {
		return issueDate;
	}
	
	public DBSalary setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	@Override
	public Double getRemuneration() {
		return remuneration;
	}
	public DBSalary setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
		return this;
	}
	@Override
	public Double getExtraPayProration() {
		return extraPayProration;
	}
	public DBSalary setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
		return this;
	}
	@Override
	public Double getRawCommonBase() {
		return rawCommonBase;
	}
	public DBSalary setRawCommonBase(Double rawCommonBase) {
		this.rawCommonBase = rawCommonBase;
		return this;
	}
	@Override
	public Double getCommonBase() {
		return commonBase;
	}
	public DBSalary setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
		return this;
	}
	@Override
	public Double getOvertimeBase() {
		return overtimeBase;
	}
	public DBSalary setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
		return this;
	}
	@Override
	public Double getNonEstructuralOvertimeBase() {
		return nonEstructuralOvertimeBase;
	}
	public DBSalary setNonEstructuralOvertimeBase(Double nonEstructuralOvertimeBase) {
		this.nonEstructuralOvertimeBase = nonEstructuralOvertimeBase;
		return this;
	}
	@Override
	public Double getProfessionalBase() {
		return professionalBase;
	}
	public DBSalary setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
		return this;
	}

	@Override
	public Double getIrpfBase() {
		return irpfBase;
	}
	public DBSalary setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
		return this;
	}
	@Override
	public Double getSocialSecurityContributions() {
		return socialSecurityContributions;
	}
	public DBSalary setSocialSecurityContributions(Double socialSecurityContributions) {
		this.socialSecurityContributions = socialSecurityContributions;
		return this;
	}
	@Override
	public Double getTotalIrpf() {
		return totalIrpf;
	}
	public DBSalary setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
		return this;
	}
	@Override
	public Date getChargeDate() {
		return chargeDate;
	}
	public DBSalary setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
		return this;
	}
	
	@Override
	public Double getInKindIrpfBase() {
		return inkindIrpfBase;
	}
	public DBSalary setInkindIrpfBase(double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
		return this;
	}
	
	@Override
	public boolean isFullTime() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Payments getPayments() throws SalaryException {
		throw new NoSuchMethodError();
	}
	@Override
	public Deductions getDeductions() throws SalaryException {
		throw new NoSuchMethodError();
	}
	@Override
	public Costs getEnterpriseCosts() throws SalaryException {
		throw new NoSuchMethodError();
	}
	
	@Override
	public <T extends IDeduction> Collection<T> getEmbargoS() throws SalaryException {
		throw new NoSuchMethodError();
	}
	
	@Override
	public Collection<IDeduction> getCostS() throws SalaryException {
		return costs;
	}
	public DBSalary addCost(DeductionType type, String name, Double amount, String description, String expression) {
		IDeduction c =
		(IDeduction)
		new DBDeduction()
		.setType(type)
		.setName(name)
		.setAmount(amount)
		.setExpression(expression)
		.setDescription(description)
		;
		
		costs.add(c);
		return this;
	}

	@Override
	public Collection<IPayment> getPaymentS() throws SalaryException {
		return payments;
	}
	public DBSalary addPayment(PaymentType type, String name, Double amount, String description, String expression) {
		IPayment p = 
		(IPayment)
		new DBPayment()
		.setType(type)
		.setName(name)
		.setAmount(amount)
		.setExpression(expression)
		.setDescription(description);
		payments.add(p);
		return this;
	}

	@Override
	public Collection<IDeduction> getDeductionS() throws SalaryException {
		return deductions;
	}
	public DBSalary addDeduction(DeductionType type, String name, Double amount, String description, String expression) {
		IDeduction d =
		(IDeduction)
		new DBDeduction()
		.setType(type)
		.setName(name)
		.setAmount(amount)
		.setExpression(expression)
		.setDescription(description);
		
		deductions.add(d);
		return this;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public ISalary getSalary() throws SalaryException {
		return this;
	}
	
	// ------------------------------------------------------------------------
	
	public double getItBase() {
		return itBase;
	}
	public DBSalary setItBase(double itBase) {
		this.itBase = itBase;
		return this;
	}

	public double getMoneyIrpfBase() {
		return moneyIrpfBase;
	}
	public DBSalary setMoneyIrpfBase(double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
		return this;
	}

	public byte getSsRegime() {
		return ssRegime;
	}
	public DBSalary setSsRegime(byte ssRegime) {
		this.ssRegime = ssRegime;
		return this;
	}
	public Contract getContract() {
		return contract;
	}
	public DBSalary setContract(Contract contract) {
		this.contract = contract;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	public DBSalary setDomain(int domain) {
		this.domain = domain;
		return this;
	}

}
