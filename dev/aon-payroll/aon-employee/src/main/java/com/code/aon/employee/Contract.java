package com.code.aon.employee;


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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.calculator.ContractSalaryCalculator;
import com.code.aon.employee.enumeration.ContractCode;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.person.Person;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.SalaryCalculatorManager;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

@Entity
@Table(name="contract")
public class Contract implements ITransferObject, ISalaryProxy {

	static {
		// CALCULADOR DEL BORRADOR DE NOMINA.
		SalaryCalculatorManager scm = SalaryCalculatorManager.getInstance(); 
		scm.addCalculator(new ContractSalaryCalculator());
		
		PaymentsFactoryManager payManager =  PaymentsFactoryManager.getInstance();
		payManager.addFactory( new ContractPaymentsFactory() );
		DeductionsFactoryManager dedManager =  DeductionsFactoryManager.getInstance();
		dedManager.addFactory( new ContractDeductionsFactory() );
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
	private Set<ContractPayment> contractPayments = new HashSet<ContractPayment>();
	private Set<ContractDeduction> contractDeductions = new HashSet<ContractDeduction>();

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
    @JoinColumn( name="ccc", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_CCC")
	@Index(name = "IDX_CONTRACT_CCC")
	public EnterpriseCCC getEnterpriseCCC() {
		return ccc;
	}
	public void setEnterpriseCCC(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
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
	public String getCode(){
	
		return ContractCode.C100.toString();
	}
	
	@Transient
	public String getType(){
		return ContractCode.C100.toString();
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
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}


	@Override
	@Transient
	public ISalary getSalary() throws SalaryException {
		SalaryCalculatorContext ctx = new SalaryCalculatorContext();
		ctx.setSalaryProxy(this);
		SalaryCalculatorManager factoryManager = SalaryCalculatorManager.getInstance();
		ISalaryCalculator sc = factoryManager.getCalculator(ctx);
		ISalary salary = sc.calculate( ctx );
		return salary;
	}

}
