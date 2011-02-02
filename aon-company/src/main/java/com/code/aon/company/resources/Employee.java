package com.code.aon.company.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.IEntity;
import com.code.aon.company.IEntityVisitor;
import com.code.aon.company.WorkActivity;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

/**
 * Transfer Object que representa un empleado.
 * 
 * @author Consulting & Development. Joseba Urkiri - 11-nov-2005
 *  
 * @since 1.0
 * 
 */
@Entity
@Table(name="employee")
public class Employee implements ITransferObject, IEntity, IRegistry {

	private static final long serialVersionUID = 1244821002402216187L;

	/** Employee identifier that is equal to the registry one. */
	private Integer id;

	private Registry registry;
	
	/** Indicates the working activity that this employee belongs to */
    private WorkActivity workActivity; 	

	/** Social Security number */
	private String socialSecurityNumber;

    /** Agreement Working hours. */
    private int agreementTime;

    /** Indicates if the employee is currently active. */
	private boolean active;

	/**
     * @return Returns the id
	 */
	@Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})			
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return Returns the registry.
	 */
	@OneToOne
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
										org.hibernate.annotations.CascadeType.MERGE, 
										org.hibernate.annotations.CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name="registry")				
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * @param registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(name="social_security_num", length = 32)
	@Index(name = "IDX_EMPLOYEE_SOCIAL_SECURITY_NUM")
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

    /**
	 * @return the agreementTime
	 */
	@Column(name="agreement_time")
	public int getAgreementTime() {
		return agreementTime;
	}

	/**
	 * @param agreementTime the agreementAmount to set
	 */
	public void setAgreementTime(int agreementTime) {
		this.agreementTime = agreementTime;
	}

    /**
     * Returns if the employee is currently in the company.
     * 
	 * @return the active
	 */
	@Column(nullable=true)
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/*(non-Javadoc)
     * @see com.code.aon.employee.INode#accept(com.code.aon.employee.INodeVisitor)
     */
	public void accept(IEntityVisitor visitor) {
		visitor.visitEmployee( this );
	}

	@ManyToOne
    @JoinColumn(name="workactivity", nullable = false, updatable = false)
    @ForeignKey(name = "FK_EMPLOYEE_WORKACTIVITY")
    @Index(name = "IDX_EMPLOYEE_WORKACTIVITY")    
	public WorkActivity getWorkActivity() {
		return workActivity;
	}

	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Employee o = (Employee) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.agreementTime, o.agreementTime)			
				.append(this.registry, o.registry)
				.append(this.socialSecurityNumber, o.socialSecurityNumber)
				.append(this.workActivity, o.workActivity)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(agreementTime)
			.append(id)
			.append(registry)
			.append(socialSecurityNumber)
			.append(workActivity)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}