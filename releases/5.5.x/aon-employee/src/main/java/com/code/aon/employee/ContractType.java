package com.code.aon.employee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.employee.enumeration.ContractDuration;
import com.code.aon.employee.enumeration.ContractWorkingDay;

/**
 * Transfer Object that represents the Contract Type.
 * 
 */
@Entity
@Table(name="contract_type")
public class ContractType implements ITransferObject {
	
	private static final long serialVersionUID = 6750651890364319423L;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	private Integer id;
	
	@Column(length = 64, nullable = false)    
	private String description;
	
	@Column(nullable = false)
	private ContractDuration duration;
	
	@Column(name = "working_day", nullable = false)
	private ContractWorkingDay workingDay;
	
	@Column(name = "ccc_type", nullable = false)
	private CCCType cccType;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public ContractDuration getDuration() {
		return duration;
	}

	public void setDuration(ContractDuration duration) {
		this.duration = duration;
	}

	public ContractWorkingDay getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(ContractWorkingDay workingDay) {
		this.workingDay = workingDay;
	}
	
	public CCCType getCCCType() {
		return cccType;
	}

	public void setCCCType(CCCType cccType) {
		this.cccType = cccType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractType o = (ContractType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.cccType, o.cccType)
				.append(this.description, o.description)
				.append(this.duration, o.duration)
				.append(this.workingDay, o.workingDay)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(cccType)
			.append(description)
			.append(duration)
			.append(id)
			.append(workingDay)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
