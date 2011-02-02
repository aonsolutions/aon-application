package com.code.aon.employee;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;

/**
 * Transfer Object that represents the contract.
 * 
 */
@Entity
@Table(name="contract")
public class Contract implements ITransferObject {

	private static final long serialVersionUID = -2662643961209110809L;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;
	
	@ManyToOne
    @JoinColumn( name="person", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_PERSON")
	@Index(name = "IDX_CONTRACT_PERSON")
	private Person person;
	
	@ManyToOne
    @JoinColumn( name="workplace", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_WORK_PLACE")
	@Index(name = "IDX_CONTRACT_WORK_PLACE")
	private WorkPlace workPlace;
	
	@ManyToOne
    @JoinColumn( name="ccc", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_CCC")
	@Index(name = "IDX_CONTRACT_CCC")
	private EnterpriseCCC ccc;
	
	@ManyToOne
    @JoinColumn( name="type", nullable = false )	
	@ForeignKey(name = "FK_CONTRACT_TYPE")
	@Index(name = "IDX_CONTRACT_TYPE")
	private ContractType contractType;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    private Date startDate;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
    private Date endDate;		

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
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
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * Gets the ccc.
	 * 
	 * @return the ccc
	 */
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
	 * Gets the contract type.
	 * 
	 * @return the contract type
	 */
	public ContractType getContractType() {
		return contractType;
	}

	/**
	 * Sets the contract type.
	 * 
	 * @param contractType the new contract type
	 */
	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
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
				.append(this.contractType, o.contractType)
				.append(this.workPlace, o.workPlace)
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
			.append(contractType)
			.append(workPlace)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
