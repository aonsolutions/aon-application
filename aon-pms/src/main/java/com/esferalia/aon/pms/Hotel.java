package com.esferalia.aon.pms;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;

@Entity
@Table(name="hotel")
public class Hotel implements ITransferObject, IScopable {

	private static final long serialVersionUID = 5078033612665053464L;

	private Integer id;
	private String code;
    private Scope scope;
    private WorkPlace workPlace;
    private Customer customer;
	private boolean active;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="code", length = 16, nullable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@ManyToOne
    @JoinColumn(name="scope", nullable = false)
    public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	@ManyToOne
    @JoinColumn(name="workplace", nullable = false)
    @AonPOJOInitializationInvalidateRestoreNull
    public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@ManyToOne
    @JoinColumn(name="customer")
    public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@Column(nullable = true)
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Hotel o = (Hotel) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.code, o.code)			
				.append(this.customer, o.customer)
				.append(this.scope, o.scope)
				.append(this.workPlace, o.workPlace)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(code)
			.append(customer)
			.append(id)
			.append(scope)
			.append(workPlace)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
