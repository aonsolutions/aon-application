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
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Month;
import com.code.aon.employee.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * Transfer Object that represents the contract payment.
 * 
 */
@Entity
@Table(name="contract_payment")
public class ContractPayment implements ITransferObject, IContractPayment {
	
	private static final long serialVersionUID = 2831598401831457550L;

	private Integer id;
	private Contract contract;
	private PaymentType type;
	private PaymentConcept paymentConcept;
	private String description;
	private String expression;
	private String irpfExpression;
	private String quoteExpression;
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
	
	@ManyToOne
	@JoinColumn(name = "contract", nullable = false, updatable = false)
	@ForeignKey(name = "FK_DEDUCTION_CONTRACT")
	@Index(name = "FK_DEDUCTION_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Override
	public PaymentType getType() {
		return type;
	}
	public void setType(PaymentType type) {
		this.type = type;
	}
	
	@ManyToOne
	@JoinColumn(name = "payment_concept")
	@ForeignKey(name = "FK_CONTRACT_PAYMENT_PAYMENT_CONCEPT")
	@Index(name = "IDX_CONTRACT_PAYMENT_PAYMENT_CONCEPT")
	public PaymentConcept getPaymentConcept() {
		return paymentConcept;
	}
	public void setPaymentConcept(PaymentConcept paymentConcept) {
		this.paymentConcept = paymentConcept;
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
	@Column(name = "irpf_expression", length = 128)
	public String getIrpfExpression() {
		return irpfExpression;
	}
	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}

	@Override
	@Column(name = "quote_expression", length = 128)
	public String getQuoteExpression() {
		return quoteExpression;
	}
	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
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

	@Override
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
		final ContractPayment o = (ContractPayment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.type, o.type)
				.append(this.description, o.description)
				.append(this.paymentConcept,o.paymentConcept)
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
			.append(contract)
			.append(type)
			.append(paymentConcept)
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

	@Override
	@Transient
	public double getAmount() {
		if (NumberUtils.isNumber(getExpression()) ) {
			return NumberUtils.toDouble(getExpression());	
		}
		return 0;
	}
	
	@Override
	@Transient
	public String getName() {
		return getPaymentConcept()==null?null:getPaymentConcept().getCode();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}

	@Transient
	public String getFullDescription() {
		return (getPaymentConcept() == null || StringUtils.isEmpty(getPaymentConcept().getCode()))?
				getDescription():
				getPaymentConcept().getCode()+ " - " + getDescription();
	}
	
	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
}
