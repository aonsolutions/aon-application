package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.BonusType;

/**
 * Transfer Object that represents the salary bonus.
 * 
 */
@Entity
@Table(name="salary_bonus")
public class SalaryBonus implements ITransferObject , ISalaryItem<BonusType>{
	
	private static final long serialVersionUID = 3117718151672116287L;


	
	private Integer id;

	private Salary salary;

	private double amount;
	
	private String description;
	
	private String bonusConcept;
	
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
	@JoinColumn(name = "salary", nullable = false, updatable = false)
	@ForeignKey(name = "FK_COST_SALARY")
	@Index(name = "IDX_COST_SALARY")
	public Salary getSalary() {
		return salary;
	}
	
	public void setSalary(Salary salary) {
		this.salary = salary;
	}
	

	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(precision = 15, scale = 3)
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@Column(name = "bonus_concept", length = 5)
	public String getBonusConcept() {
		return bonusConcept;
	}
	
	public void setBonusConcept(String bonusConcept) {
		this.bonusConcept = bonusConcept;
	}
	
	@Override
	@Transient
	public String getName() {
		return getBonusConcept();
	}
	
	@Override
	@Transient
	public BonusType getType() {
		return BonusType.SOCIAL_SECURITY;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SalaryBonus o = (SalaryBonus) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.salary, o.salary)
				.append(this.description, o.description)
				.append(this.amount, o.amount)
				.append(this.bonusConcept, o.bonusConcept)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(salary)
			.append(description)
			.append(amount)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
		
}
