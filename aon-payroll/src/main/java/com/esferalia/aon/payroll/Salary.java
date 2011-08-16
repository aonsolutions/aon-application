package com.esferalia.aon.payroll;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Session;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.salary.payment.PaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

@Entity
@Table(name="salary")
public class Salary implements ITransferObject , ISalary, ISalaryProxy {

	static {
		PaymentsFactoryManager payManager =  PaymentsFactoryManager.getInstance();
		payManager.addFactory( new SalaryPaymentsFactory() );
		DeductionsFactoryManager dedManager =  DeductionsFactoryManager.getInstance();
		dedManager.addFactory( new SalaryDeductionsFactory() );
	}

	private static final long serialVersionUID = 628669216993025202L;

	// AON DATA
	private Integer id;
	private Contract contract;

	// Empresa
	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;
	private String ccc;

	// Empleado
	private String employeeName;
	private String employeeDocument;
	private Integer registration;
	private String socialSecurityNumber;	
	private String category;
	private String quoteGroup;
	private Date seniorityDate;

	// Nomina
	private SalaryType type;
	private Date issueDate;
	private int issueMonth;
	private int issueYear;
	private Month month;
	private Integer year;
	private Date startDate;
	private Date endDate;
	//private boolean fullTime; //TODO Dar soporte
	private Integer timeUnits;

	//COSTOS
	private Set<SalaryCost> salaryCosts = new HashSet<SalaryCost>();
	private Double totalCost;

	//BONIFICACIONES
	private Set<SalaryBonus> salaryBonus = new HashSet<SalaryBonus>();
	private Double totalBonus;

	//EMBARGOS
	private Set<SalaryEmbargo> salaryEmbargos = new HashSet<SalaryEmbargo>();
	private Double salaryEmbargo;

	//DEVENGOS
	private Set<SalaryPayment> salaryPayments = new HashSet<SalaryPayment>();
	private Double totalPayment;

	//DEDUCCIONES
	private Set<SalaryDeduction> salaryDeductions = new HashSet<SalaryDeduction>();
	private Double socialSecurityContributions;
	private Double totalDeduction;

	// TOTAL LIQUIDO
	private Double totalLiquid;
	
	// CUOTA TOTAL DE LA EMPRESA
	private Double totalEnterprise;

	//BASES
	private Double remuneration; //Remuneración mensual
	private Double extraPayProration; // Prorrata de pagas extraordinarias
	private Double commonBase; //Base de cotización por contigencias comunes
	private Double professionalBase; //Base de cotización por contigencias profesionales (A.T. y E.P.) y conceptos de recaudación conjunta (Desemp., F.P., F.G.S.)
	private Double overtimeBase; //Base de cotización adicional por horas extraordinarias estructurales
	private Double nonEstructuralOvertimeBase; //Base de cotización adicional por horas extraordinarias no estructurales
	private Double irpfBase; //Base sujeta a retención del I.R.P.F.

	// OTHERS
	private IDeductionsFactoryContext dedContext;
	private IPaymentsFactoryContext payContext;
	
	@Transient
	private Payments payments;
	@Transient
	private Deductions deductions;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SALARY_RECCEIPT_CONTRACT")
	@Index(name = "FK_SALARY_RECCEIPT_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}

	// *******************************************************
	// ****************** EMPRESA ****************************
	// *******************************************************

	@Override
	@Column(name = "enterprise_name",length=64)
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	@Override
	@Column(name = "enterprise_address", length=64)
	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}
	public void setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
	}

	@Override
	@Column(name = "enterprise_document", length=16)
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	
	@Override
	@Column(name = "ccc", length=11)
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	// *******************************************************
	// ****************** EMPLEADO ***************************
	// *******************************************************

	@Override
	@Column(name = "employee_name",length=64)
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	@Column(name = "employee_document",length=64)
	public String getEmployeeDocument() {
		return employeeDocument;
	}
	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}

	@Override
	@Column( nullable = false)
	public Integer getRegistration() {
		return registration;
	}
	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	@Override
	@Column(name = "social_security_number",length=32)
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	@Override
	@Column(length=64)
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	@Column(name = "quote_group",length=2)
	public String getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	@Override
	@Temporal(TemporalType.DATE)
	@Column( name = "seniority_date")
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	// *******************************************************
	// ****************** NOMINA ***************************
	// *******************************************************
	
	@Override
	@Column( name = "type")
	public SalaryType getType() {
		return type;
	}
	public void setType(SalaryType type) {
		this.type = type;
	}

	@Override
	@Temporal(TemporalType.DATE)
	@Column( name = "issue_date", nullable = false )
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	@Formula("month(issue_date)")
	public int getIssueMonth() {
		return issueMonth;
	}
	public void setIssueMonth(int issueMonth) {
		this.issueMonth = issueMonth;
	}
	@Transient
	public Month getMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getEndDate());
		month = Month.getMonthByValue(cal.get(Calendar.MONTH));
		return month;
	}
	@Transient
	public Integer getYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getEndDate());
		year = cal.get(Calendar.YEAR);
		return year;
	}
	
	@Formula("year(issue_date)")
	public int getIssueYear() {
		return issueYear;
	}
	public void setIssueYear(int issueYear) {
		this.issueYear = issueYear;
	}
	
	@Override
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable = false )
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date", nullable = false )
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	@Column(name = "time_units",  nullable = false)
	public Integer getTimeUnits() {
		return timeUnits;
	}
	public void setTimeUnits(Integer timeUnits) {
		this.timeUnits = timeUnits;
	}

	@Override
	@Transient
	public boolean isFullTime() {
		// TODO Identificar cuando la nomina es por dias u horas
		return true;
	}

	// *******************************************************
	// ********************* COSTOS **************************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryCost> getSalaryCosts() {
		return salaryCosts;
	}
	public void setSalaryCosts(Set<SalaryCost> salaryCosts) {
		this.salaryCosts = salaryCosts;
	}

	// *******************************************************
	// ******************** EMBARGOS *************************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryEmbargo> getSalaryEmbargos() {
		return salaryEmbargos;
	}
	public void setSalaryEmbargos(Set<SalaryEmbargo> salaryEmbargos) {
		this.salaryEmbargos= salaryEmbargos;
	}

	// *******************************************************
	// **************** BONIFICACIONES **********************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryBonus> getSalaryBonus() {
		return salaryBonus;
	}
	public void setSalaryBonus(Set<SalaryBonus> salaryBonus) {
		this.salaryBonus = salaryBonus;
	}

	// *******************************************************
	// ****************** DEVENGOS ***************************
	// *******************************************************
	
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryPayment> getSalaryPayments() {
		return salaryPayments;
	}
	public void setSalaryPayments(Set<SalaryPayment> salaryPayments) {
		this.salaryPayments = salaryPayments;
	}

	@Override
	@Column(name = "total_payment", precision = 15, scale = 3, nullable = false)
	public Double getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	// *******************************************************
	// ****************** DEDUCCIONES ************************
	// *******************************************************

	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryDeduction> getSalaryDeductions() {
		return salaryDeductions;
	}
	public void setSalaryDeductions(Set<SalaryDeduction> salaryDeductions) {
		this.salaryDeductions = salaryDeductions;
	}

	@Override
	@Column(name = "social_security_contributions", precision = 15, scale = 3, nullable = false)
	public Double getSocialSecurityContributions() {
		return socialSecurityContributions;
	}
	public void setSocialSecurityContributions(Double socialSecurityContributions) {
		this.socialSecurityContributions = socialSecurityContributions;
	}

	@Override
	@Column(name = "total_deduction", precision = 15, scale = 3, nullable = false)
	public Double getTotalDeduction() {
		return totalDeduction;
	}
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	// *******************************************************
	// **************** TOTAL LIQUIDO ************************
	// *******************************************************
	@Override
	@Column(name = "total_liquid", precision = 15, scale = 3, nullable = false)
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	
	// *******************************************************
	// **************** CUOTA TOTAL EMPRESA ******************
	// *******************************************************
	@Column(name = "total_enterprise", precision = 15, scale = 3, nullable = false)
	public Double getTotalEnterprise() {
		return totalEnterprise;
	}
	public void setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise = totalEnterprise;
	}

	// *******************************************************
	// ********************* BASES ***************************
	// *******************************************************
	@Override
	@Column( precision = 15, scale = 3, nullable = false)
	public Double getRemuneration() {
		return remuneration;
	}
	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	@Override
	@Column(name = "pro_ext_base", precision = 15, scale = 3, nullable = false)
	public Double getExtraPayProration() {
		return extraPayProration;
	}
	public void setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
	}

	@Override
	@Column(name = "cgc_base", precision = 15, scale = 3, nullable = false)
	public Double getCommonBase() {
		return commonBase;
	}
	public void setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
	}

	@Override
	@Column(name = "cgp_base", precision = 15, scale = 3, nullable = false)
	public Double getProfessionalBase() {
		return professionalBase;
	}
	public void setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
	}
	
	@Override
	@Column(name = "hextra_base", precision = 15, scale = 3, nullable = false)
	public Double getOvertimeBase() {
		return overtimeBase;
	}
	public void setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
	}
	
	@Override
	@Column(name = "non_hextra_base", precision = 15, scale = 3, nullable = false)
	public Double getNonEstructuralOvertimeBase() {
		return nonEstructuralOvertimeBase;
	}
	public void setNonEstructuralOvertimeBase(Double nonEstructuralOvertimeBase) {
		this.nonEstructuralOvertimeBase = nonEstructuralOvertimeBase;
	}

	@Override
	@Column(name = "irpf_base", precision = 15, scale = 3, nullable = false)
	public Double getIrpfBase() {
		return irpfBase;
	}
	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Salary o = (Salary) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.enterpriseName,o.enterpriseName)
				.append(this.enterpriseAddress,o.enterpriseAddress)
				.append(this.enterpriseDocument,o.enterpriseDocument)
				.append(this.ccc,o.ccc)
				.append(this.employeeName,o.employeeName)
				.append(this.employeeDocument,o.employeeDocument)
				.append(this.registration,o.registration)
				.append(this.socialSecurityNumber,o.socialSecurityNumber)	
				.append(this.category,o.category)
				.append(this.quoteGroup,o.quoteGroup)
				.append(this.seniorityDate,o.seniorityDate)
				.append(this.type,o.type)
				.append(this.issueDate,o.issueDate)
				.append(this.startDate,o.startDate)
				.append(this.endDate,o.endDate)
				.append(this.timeUnits,o.timeUnits)
				.append(this.totalPayment,o.totalPayment)
				.append(this.totalDeduction,o.totalDeduction)
				.append(this.totalLiquid,o.totalLiquid)
				.append(this.remuneration,o.remuneration)
				.append(this.extraPayProration,o.extraPayProration)
//				.append(this.commonBase,o.commonBase)
				.append(this.professionalBase,o.professionalBase)
				.append(this.overtimeBase,o.overtimeBase)
				.append(this.nonEstructuralOvertimeBase,o.nonEstructuralOvertimeBase)
				.append(this.irpfBase,o.irpfBase)
				.isEquals();			
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(contract)
			.append(enterpriseName)
			.append(enterpriseAddress)
			.append(enterpriseDocument)
			.append(ccc)
			.append(employeeName)
			.append(employeeDocument)
			.append(registration)
			.append(socialSecurityNumber)	
			.append(category)
			.append(quoteGroup)
			.append(seniorityDate)
			.append(type)
			.append(issueDate)
			.append(startDate)
			.append(endDate)
			.append(timeUnits)
			.append(totalPayment)
			.append(totalDeduction)
			.append(totalLiquid)
			.append(remuneration)
			.append(extraPayProration)
//			.append(commonBase)
			.append(professionalBase)
			.append(overtimeBase)
			.append(nonEstructuralOvertimeBase)
			.append(irpfBase)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Override
	@Transient
	public ISalary getSalary(){
		return this;
	}

	@Transient
	@Override
	public Deductions getDeductions() throws SalaryException  {
		if (deductions == null) {
			DeductionsFactoryManager manager =  DeductionsFactoryManager.getInstance();
			IDeductionsFactory factory = manager.getFactory( getDeductionsFactoryContext() );
			setDeductions( factory.getDeductions(getDeductionsFactoryContext()));
		}
		return deductions;
	}
	public void setDeductions(Deductions deductions) throws SalaryException {
		this.deductions = deductions;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Collection<SalaryCost> getCosts() throws SalaryException {
		try {
			Collection<SalaryCost> costs ;
			String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
			// el FrameWork.
			if (  session.contains(this)  || this.getId() == null ) {
				costs =  this.getSalaryCosts();
			} else {
				IManagerBean bean = BeanManager.getManagerBean(SalaryCost.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_COST_SALARY_ID), this.getId());
				List<?> list = bean.getList(c);
				costs = (Collection<SalaryCost>) list;
			}
			return costs;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Collection<SalaryBonus> getBonus() throws SalaryException {
		try {
			Collection<SalaryBonus> bonus ;
			String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
			// el FrameWork.
			if (  session.contains(this)  || this.getId() == null ) {
				bonus =  this.getSalaryBonus();
			} else {
				IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_BONUS_SALARY_ID), this.getId());
				List<?> list = bean.getList(c);
				bonus = (Collection<SalaryBonus>) list;
			}
			return bonus;
		} catch (ManagerBeanException  e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}

	@Transient
	@Override
	public Payments getPayments() throws SalaryException {
		if (payments == null) {
			PaymentsFactoryManager manager =  PaymentsFactoryManager.getInstance();
			IPaymentsFactory factory = manager.getFactory( getPaymentsFactoryContext() );
			setPayments(factory.getPayments(getPaymentsFactoryContext()));
		}
		return payments;
	}

	public void setPayments(Payments payments) throws SalaryException {
		this.payments = payments;
	}
	
	@Transient
	public IDeductionsFactoryContext getDeductionsFactoryContext() {
		if (dedContext == null) {
			DeductionsFactoryContext dfc = new DeductionsFactoryContext();
			dfc.setSalaryProxy(this);
			dedContext = dfc;			
		}
		return dedContext;
	}
	@Transient
	public IPaymentsFactoryContext getPaymentsFactoryContext() {
		if (payContext == null) {
			PaymentsFactoryContext pfc = new PaymentsFactoryContext();
			pfc.setSalaryProxy(this);
			payContext = pfc;			
		}
		return payContext;
	}
}
