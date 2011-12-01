package com.esferalia.aon.payroll;

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
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.DeductionType;

/**
 * Transfer Object that represents the contract deduction.
 * 
 */
@Entity
@Table(name="system_deduction")
public class SystemDeduction implements ITransferObject {

	private static final long serialVersionUID = 4510451091870851884L;

	private Integer id;
	private DeductionType type;
	private DeductionConcept deductionConcept;
	private String description;
	private String expression;
	private Date startDate;	
	private Date endDate;	
	private Month month;
	private boolean descriptionDecorable;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public DeductionType getType() {
		return type;
	}
	
	public void setType(DeductionType type) {
		this.type = type;
	}
	
	@ManyToOne
	@JoinColumn(name = "deduction_concept")
	@ForeignKey(name = "FK_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT")
	@Index(name = "IDX_SYSTEM_DEDUCTION_DEDUCTION_CONCEPT")
	public DeductionConcept getDeductionConcept() {
		return deductionConcept;
	}
	public void setDeductionConcept(DeductionConcept deductionConcept) {
		this.deductionConcept = deductionConcept;
	}

	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
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
	
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	
	@Column( name = "description_decorable" )
	public boolean isDescriptionDecorable() {
		return descriptionDecorable;
	}
	public void setDescriptionDecorable(boolean descriptionDecorable) {
		this.descriptionDecorable = descriptionDecorable;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SystemDeduction o = (SystemDeduction) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.type, o.type)
				.append(this.deductionConcept,o.deductionConcept)
				.append(this.description, o.description)
				.append(this.expression, o.expression)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.month, o.month)
				.append(this.descriptionDecorable, o.descriptionDecorable)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(type)
			.append(deductionConcept)
			.append(description)
			.append(expression)
			.append(startDate)
			.append(endDate)
			.append(month)
			.append(descriptionDecorable)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
