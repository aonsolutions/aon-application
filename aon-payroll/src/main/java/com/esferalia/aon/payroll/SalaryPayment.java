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
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * Transfer Object that represents the salary payment.
 * 
 */
@Entity
@Table(name="salary_payment")
public class SalaryPayment implements ITransferObject, IPayment, IExpression {
	
	private static final long serialVersionUID = 7062556672670928116L;

	private Integer id;

	private Salary salary;

	private PaymentType type;
	
	private String description;

	private String paymentConcept;

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
	public PaymentType getType() {
		return type;
	}
	
	public void setType(PaymentType type) {
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
	
	@Column(name = "payment_concept", length = 5)
	public String getPaymentConcept() {
		return paymentConcept;
	}
	public void setPaymentConcept(String paymentConcept) {
		this.paymentConcept = paymentConcept;
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
		final SalaryPayment o = (SalaryPayment) obj;
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

	@Override
	@Transient
	public String getName() {
		return getPaymentConcept();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.SALARY;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isSalaryInKind() {
		return getType()==PaymentType.SALARY_IN_KIND;
	}
}
