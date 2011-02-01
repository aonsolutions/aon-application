package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="commission_type")
public class CommissionType implements ITransferObject {

	private static final long serialVersionUID = -2998975747712164021L;

    private Integer id;
    private String name;
    private double rate;

    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Column(precision = 6, scale = 2)
	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CommissionType o = (CommissionType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)								
				.append(this.rate, o.rate)								
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()		
			.append(id)								
			.append(name)						
			.append(rate)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}