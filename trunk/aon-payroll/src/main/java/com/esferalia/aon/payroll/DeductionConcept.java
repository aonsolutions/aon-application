package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;

@Entity
@Table(name="deduction_concept")
public class DeductionConcept implements ITransferObject{
	
	private static final long serialVersionUID = 3933542592010025754L;

	private Integer id;
	private String code;
	private String description;
	private DeductionType type;
	private String expression;
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
	
	@Column(length = 5)
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
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
		final DeductionConcept o = (DeductionConcept) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.code, o.code)
				.append(this.description, o.description)
				.append(this.type, o.type)
				.append(this.expression, o.expression)
				.append(this.descriptionDecorable, o.descriptionDecorable)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.append(description)
			.append(type)
			.append(expression)
			.append(descriptionDecorable)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
