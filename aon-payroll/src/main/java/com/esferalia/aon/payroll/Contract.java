package com.esferalia.aon.payroll;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.SalaryCalculatorManager;

@Entity
@Table(name="contract")
public class Contract implements ITransferObject, ISalaryProxy {

	static {
		// CALCULADOR DEL BORRADOR DE NOMINA.
		SalaryCalculatorManager scm = SalaryCalculatorManager.getInstance();
		scm.addCalculator(new ContractSalaryCalculator());
	}

	private static final long serialVersionUID = -2662643961209110809L;
	
	private Integer id;
	private Person person;
	private WorkPlace workPlace;
	private EnterpriseCCC ccc;
	private Date startDate;	
	private Date endDate;	
	private byte[] document;
	private ContractStatus status;
	private Calendar calendar;
	private Integer registration;
	private Date seniorityDate;	
	
	private Set<ContractPayment> contractPayments = new HashSet<ContractPayment>();
	private Set<ContractDeduction> contractDeductions = new HashSet<ContractDeduction>();
	@Transient
	private SalaryCalculatorContext ctx;

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
    @JoinColumn( name="person", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_PERSON")
	@Index(name = "IDX_CONTRACT_PERSON")
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne
    @JoinColumn( name="workplace", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_WORK_PLACE")
	@Index(name = "IDX_CONTRACT_WORK_PLACE")
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	@ManyToOne
    @JoinColumn( name="ccc")	
	@ForeignKey(name = "FK_CONTRACT_CCC")
	@Index(name = "IDX_CONTRACT_CCC")
	public EnterpriseCCC getEnterpriseCCC() {
		return ccc;
	}
	public void setEnterpriseCCC(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
    public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	@Lob
	public byte[] getDocument() {
		return document;
	}
	public void setDocument(byte[] document) {
		this.document = document;
	}
	
	public ContractStatus getStatus() {
		return status;
	}
	public void setStatus(ContractStatus status) {
		this.status = status;
	}
	
	@ManyToOne
    @JoinColumn( name="calendar")	
	@ForeignKey(name = "FK_CONTRACT_CALENDAR")
	@Index(name = "IDX_CONTRACT_CALENDAR")
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Set<ContractPayment> getContractPayments() {
		return this.contractPayments;
	}
	public void setContractPayments( Set<ContractPayment> contractPayments ) {
		this.contractPayments = contractPayments;
	}
	
	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Set<ContractDeduction> getContractDeductions() {
		return this.contractDeductions;
	}
	public void setContractDeductions( Set<ContractDeduction> contractDeductions ) {
		this.contractDeductions = contractDeductions;
	}
	
	@Transient
	public boolean isBlocked() {
		return getStatus() == ContractStatus.BLOCKED;
	}
	
	@Transient
	public boolean isPending() {
		return getStatus() == ContractStatus.PENDING;
	}
	
	@Transient
	public boolean isProcessed() {
		return getStatus() == ContractStatus.PROCESSED;
	}
	
	@Column(nullable = false )
	public Integer getRegistration() {
		return registration;
	}
	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "seniority_date", nullable = false )
    public Date getSeniorityDate() {
		return seniorityDate;
	}
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Contract o = (Contract) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.person, o.person)			
				.append(this.workPlace, o.workPlace)
				.append(this.ccc, o.ccc)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.status, o.status)
				.append(this.calendar, o.calendar)
				.append(this.registration, o.registration)
				.append(this.seniorityDate, o.seniorityDate)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(person)
			.append(workPlace)
			.append(ccc)
			.append(startDate)
			.append(endDate)
			.append(status)
			.append(calendar)
			.append(registration)
			.append(seniorityDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}


	@Override
	@Transient
	public ISalary getSalary() throws SalaryException {
		SalaryCalculatorManager factoryManager = SalaryCalculatorManager.getInstance();
		ISalaryCalculator sc = factoryManager.getCalculator(getSalaryCalculatorContext());
		sc.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = sc.calculate( getSalaryCalculatorContext() );
		
		return salary;
	}

	@Override
	@Transient
	public SalaryCalculatorContext getSalaryCalculatorContext() throws SalaryException {
		if (ctx == null) {
			try {
				ctx = new ContractSalaryCalculatorContext(this);
			} catch (AonException e) {
				throw new SalaryException(e.getMessage(), e);
			}
		}
		return ctx;
	}

}
