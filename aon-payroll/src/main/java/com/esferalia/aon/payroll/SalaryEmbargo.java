package com.esferalia.aon.payroll;

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

/**
 * Transfer Object that represents the salary embargo.
 * 
 */
@Entity
@Table(name="salary_embargo")
public class SalaryEmbargo implements ITransferObject {
	
	private static final long serialVersionUID = 2810974860730292917L;

	private Integer id;

	private Salary salary;

	private ContractEmbargo contractEmbargo;

	private double amount;
	
	private String description;
	
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
	@ForeignKey(name = "FK_EMBARGO_SALARY")
	@Index(name = "IDX_EMBARGO_SALARY")
	public Salary getSalary() {
		return salary;
	}
	
	public void setSalary(Salary salary) {
		this.salary = salary;
	}
	
	@ManyToOne
	@JoinColumn(name = "contract_embargo", nullable = false, updatable = false)
	@ForeignKey(name = "FK_SALARY_EMBARGO_CONTRACT_EMBARGO")
	@Index(name = "IDX_SALARY_EMBARGO_CONTRACT_EMBARGO")
	public ContractEmbargo getContractEmbargo() {
		return contractEmbargo;
	}
	
	public void setContractEmbargo(ContractEmbargo contractEmbargo) {
		this.contractEmbargo = contractEmbargo;
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
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SalaryEmbargo o = (SalaryEmbargo) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.salary, o.salary)
				.append(this.description, o.description)
				.append(this.amount, o.amount)
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
