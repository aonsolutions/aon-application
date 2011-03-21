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
@Table(name="system_data")
public class SystemData implements ITransferObject, IExpression{
	
	private static final long serialVersionUID = -3605182384674355773L;

	private Integer id;
	private String name;
	private String expression;
	private Date startDate;	
	private Date endDate;
	public boolean readOnly;
	private String comments;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length = 64)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	
	@Override
	@Column( name = "read_only" )
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Column( name = "comments" )
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SystemData o = (SystemData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.expression, o.expression)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.readOnly, o.readOnly)
				.append(this.comments, o.comments)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(name)
			.append(expression)
			.append(startDate)
			.append(endDate)
			.append(this.readOnly)
			.append(this.comments)
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
	
}
