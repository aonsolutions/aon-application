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
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="agreement_data")
public class AgreementData extends AbstractVariableData implements ITransferObject, IExpression {
	
	private static final long serialVersionUID = -1985562229267143435L;

	private Integer id;
	private String name;
	private Agreement agreement;
	private String expression;
	private Date startDate;	
	private Date endDate;	
	
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
    @JoinColumn( name="agreement", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_AGREEMENT_DATA_AGREEMENT")
	@Index(name = "IDX_AGREEMENT_DATA_AGREEMENT")
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}

	@Column(length=16)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(length=128)
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementData o = (AgreementData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.agreement, o.agreement)			
				.append(this.name, o.name)			
				.append(this.expression, o.expression)			
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(this.agreement)			
			.append(this.name)			
			.append(this.expression)			
			.append(this.startDate)
			.append(this.endDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
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
