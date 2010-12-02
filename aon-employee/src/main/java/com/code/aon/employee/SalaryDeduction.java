package com.code.aon.employee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

/**
 * Transfer Object that represents the salary deduction.
 * 
 */
@Entity
@Table(name="salary_deduction")
public class SalaryDeduction implements ITransferObject, IDeduction {

	private static final long serialVersionUID = 605359641808170785L;

	private Integer id;

	private Salary salary;

	private DeductionType type;
	
	private String description;

	private String expression;

	private double amount;

	
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
	@ForeignKey(name = "FK_DEDUCTION_SALARY")
	@Index(name = "FK_DEDUCTION_SALARY")
	public Salary getSalary() {
		return salary;
	}
	
	public void setSalary(Salary salary) {
		this.salary = salary;
	}
	
	@Override
	public DeductionType getType() {
		return type;
	}
	
	public void setType(DeductionType type) {
		this.type = type;
	}
	
	@Override
	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Override
	@Column(precision = 15, scale = 3)
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SalaryDeduction o = (SalaryDeduction) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.salary, o.salary)
				.append(this.type, o.type)
				.append(this.description, o.description)
				.append(this.expression, o.expression)
				.append(this.amount, o.amount)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(salary)
			.append(type)
			.append(description)
			.append(expression)
			.append(amount)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
