package com.code.aon.employee;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
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
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.enumeration.ContractCode;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.employee.enumeration.DeductionType;
import com.code.aon.employee.enumeration.PaymentType;
import com.code.aon.person.Person;

/**
 * Transfer Object that represents the contract.
 * 
 */
@Entity
@Table(name="contract")
public class Contract implements ITransferObject, ISalary {

	private static final long serialVersionUID = -2662643961209110809L;
	
	private Integer id;
	
	private Person person;
	
	private WorkPlace workPlace;
	
	private EnterpriseCCC ccc;
	
	private Date startDate;	

	private Date endDate;	
	
	private byte[] document;

	private ContractStatus status;

	private Set<ContractPayment> payments = new HashSet<ContractPayment>();
	
	private Set<ContractDeduction> deductions = new HashSet<ContractDeduction>();


	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the person.
	 * 
	 * @return the person
	 */
	@ManyToOne
    @JoinColumn( name="person", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_PERSON")
	@Index(name = "IDX_CONTRACT_PERSON")
	public Person getPerson() {
		return person;
	}

	/**
	 * Sets the person.
	 * 
	 * @param person the new person
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Gets the work place.
	 * 
	 * @return the work place
	 */
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	/**
	 * Sets the work place.
	 * 
	 * @param workPlace the new work place
	 */
	@ManyToOne
    @JoinColumn( name="workplace", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_WORK_PLACE")
	@Index(name = "IDX_CONTRACT_WORK_PLACE")
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * Gets the ccc.
	 * 
	 * @return the ccc
	 */
	@ManyToOne
    @JoinColumn( name="ccc", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_CCC")
	@Index(name = "IDX_CONTRACT_CCC")
	public EnterpriseCCC getCcc() {
		return ccc;
	}

	/**
	 * Sets the ccc.
	 * 
	 * @param ccc the new ccc
	 */
	public void setCcc(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
    public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the new end date
	 */
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
	
	public Set<ContractPayment> getContractPayments() {
		return this.payments;
	}
	public void setContractPayments( Set<ContractPayment> payments ) {
		this.payments = payments;
	}
	
	public Set<ContractDeduction> getContractDeductions() {
		return this.deductions;
	}
	public void setContractDeductions( Set<ContractDeduction> deductions ) {
		this.deductions = deductions;
	}
	
	
	@Transient
	public String getCode(){
	
		return ContractCode.C100.toString();
	}
	
	@Transient
	public String getType(){
		
//		return ContractCode.C100.getName(Locale.getDefault());
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
				.append(this.ccc, o.ccc)
				.append(this.endDate, o.endDate)
				.append(this.person, o.person)			
				.append(this.startDate, o.startDate)
				.append(this.workPlace, o.workPlace)
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(ccc)
			.append(endDate)
			.append(id)
			.append(person)
			.append(startDate)
			.append(workPlace)
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

	@Override
	public String getAddress() {
		try {
			return getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress();
		} catch (ManagerBeanException e) {
			
		}
		return null;
	}

	@Override
	public Date getBroadcastDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getCommonBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployee() {
		return getPerson().getRegistry().getFullName();
	}

	@Override
	public Double getExtraPayProration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getIrpfBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getOvertimeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getProfessionalBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRegistration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getRemuneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISalary getSalary() {
		return this;
	}

	@Override
	public Contract getContract() {
		return this;
	}

	@Override
	public Double getTotal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getTotalDaysHours() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalDeduction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalLiquid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalPayment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	public String getStartDateDay(Locale locale){
		return DateFormatUtils.format(startDate, "dd", locale);
	}
	@Transient
	public String getStartDateMonth(Locale locale){
		return DateFormatUtils.format(startDate, "MMMMM", locale);
	}
	@Transient
	public String getStartDateYear(Locale locale){
		return DateFormatUtils.format(startDate, "yyyy", locale);
	}
	@Transient
	public String getEndDateDay(Locale locale){
		return DateFormatUtils.format(endDate, "dd", locale);
	}
	@Transient
	public String getEndDateMonth(Locale locale){
		return DateFormatUtils.format(endDate, "MMMMM", locale);
	}
	@Transient
	public String getEndDateYear(Locale locale){
		return DateFormatUtils.format(endDate, "yyyy", locale);
	}
	
	@Override
	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Deductions getDeductions() {
		Deductions d = new Deductions();
		for(Object o: getContractDeductions().toArray()){
			ContractDeduction sd = (ContractDeduction) o;
			if(upToDate(sd.getStartDate(), sd.getEndDate())){
				if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
					d.setCommonContingency(sd);
				} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
					d.setUnemployment(sd);
				} else if (sd.getType() == DeductionType.JOB_TRAINING) {
					d.setJobTraining(sd);
				} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
					d.setStructuralOvertime(sd);
				} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
					d.setNonStructuralOvertime(sd);
				} else if (sd.getType() == DeductionType.IRPF) {
					d.setIrpf(sd);
				} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
					d.setAdvancePayment(sd);
				} else if (sd.getType() == DeductionType.IN_KIND) {
					d.setInKind(sd);
				} else if (sd.getType() == DeductionType.OTHER) {
					d.setOther(sd);
				}
			}
		}
		return d;
	}

	@Override
	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Payments getPayments() {
		Payments p = new Payments();
		p.setSalarySupplements(new LinkedList<IPayment>());
		p.setComplementarySuply(new LinkedList<IPayment>());
		for(Object o: getContractPayments().toArray()){
			ContractPayment sp = (ContractPayment) o;
			if(upToDate(sp.getStartDate(), sp.getEndDate())){
				if (sp.getType() == PaymentType.BASE_SALARY) {
					p.setBaseSalary(sp);
				} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
					p.getSalarySupplements().add(sp);
				} else if (sp.getType() == PaymentType.OVERTIME_HOURS) {
					p.setOvertimeHours(sp);
				} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
					p.setSpecialBonuses(sp);
				} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
					p.setSalaryInKind(sp);
				} else if (sp.getType() == PaymentType.COMPENSATION_SUPLY) {
					p.getComplementarySuply().add(sp);
				} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
					p.setSpecialSecurityBenefits(sp);
				} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
					p.setMovingCompensation(sp);
				} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
					p.setOtherNonWage(sp);
				}
			}
		}
		return p;
	}

	private boolean upToDate(Date startDate, Date endDate) {
		if(startDate.before(new Date())){
			if(endDate==null || endDate.after(new Date())){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getQuoteGroup() {
		// TODO Auto-generated method stub
		return null;
	}

}
