package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="system_cost")
public class SystemCost implements ITransferObject, IExpression{
	
	private static final long serialVersionUID = -2825053732432789926L;

	private Integer id;
	private String code;
	private String description;
	private String expression;
	private Date startDate;	
	private Date endDate;
	private Integer type;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length = 10)
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	@Column( name = "description", length = 64 )
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SystemCost o = (SystemCost) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.code, o.code)
				.append(this.expression, o.expression)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.description, o.description)
				.append(this.type, o.type)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.code)
			.append(this.expression)
			.append(this.startDate)
			.append(this.endDate)
			.append(this.description)
			.append(this.type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.APPLICATION;
	}

	@Override
	@Transient
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
