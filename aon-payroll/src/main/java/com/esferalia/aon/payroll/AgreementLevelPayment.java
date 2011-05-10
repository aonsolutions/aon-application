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
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="agreement_level_payment")
public class AgreementLevelPayment implements ITransferObject, IContractPayment  {

	private static final long serialVersionUID = 2714371281487806164L;

	private Integer id;
	private AgreementLevel level;
	private Date startDate;	
	private Date endDate;
	private PaymentType type;
	private PaymentConcept paymentConcept;
	private String description;
	private String expression;
	private String irpfExpression;
	private String quoteExpression;
	private Month month;
	private SalaryType salaryType;
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
    @JoinColumn( name="agreement_level", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_PAYMENT_AGREEMENT_LEVEL")
	@Index(name = "FK_PAYMENT_AGREEMENT_LEVEL")
	public AgreementLevel getLevel() {
		return level;
	}
	public void setLevel(AgreementLevel level) {
		this.level = level;
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

	public PaymentType getType() {
		return type;
	}
	public void setType(PaymentType type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn(name = "payment_concept")
	@ForeignKey(name = "FK_AGREE_LEVEL_PAYMENT_PAYMENT_CONCEPT")
	@Index(name = "IDX_AGREE_LEVEL_PAYMENT_PAYMENT_CONCEPT")
	public PaymentConcept getPaymentConcept() {
		return paymentConcept;
	}
	public void setPaymentConcept(PaymentConcept paymentConcept) {
		this.paymentConcept = paymentConcept;
	}

	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Column(name = "irpf_expression",length = 128)
	public String getIrpfExpression() {
		return irpfExpression;
	}
	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}

	@Column(name = "quote_expression", length = 128)
	public String getQuoteExpression() {
		return quoteExpression;
	}
	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
	}

	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column( name = "description_decorable" )
	public boolean isDescriptionDecorable() {
		return descriptionDecorable;
	}
	public void setDescriptionDecorable(boolean descriptionDecorable) {
		this.descriptionDecorable = descriptionDecorable;
	}
	
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}

	@Column( name = "salary_type" )
	public SalaryType getSalaryType() {
		return salaryType;
	}
	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementLevelPayment o = (AgreementLevelPayment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.level, o.level)			
				.append(this.type, o.type)
				.append(this.paymentConcept,o.paymentConcept)
				.append(this.description, o.description)			
				.append(this.expression, o.expression)
				.append(this.irpfExpression,o.irpfExpression)
				.append(this.quoteExpression,o.quoteExpression)
				.append(this.descriptionDecorable,o.descriptionDecorable)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(level)
			.append(type)
			.append(paymentConcept)
			.append(description)
			.append(expression)
			.append(irpfExpression)
			.append(quoteExpression)
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
		throw new UnsupportedOperationException();
	}
	@Override
	@Transient
	public String getName() {
		return getPaymentConcept()==null?null:getPaymentConcept().getCode();
	}
	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.AGREEMENT;
	}
	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}

}
