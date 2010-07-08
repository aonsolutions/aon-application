package com.code.aon.company.resources;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.IEntity;
import com.code.aon.company.IEntityVisitor;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.registry.Registry;
import com.code.aon.registry.dao.IRegistryAlias;

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
public class Employee implements ITransferObject, IEntity, ILookupObject {

	/** Employee identifier that is equal to the registry one. */
	private Integer id;

	private Registry registry;

	/** Social Security number */
	private String socialSecurityNumber;

    /** Indicates the employee calendar identifier. */
    private Integer calendar;

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
	@OneToOne()
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
										org.hibernate.annotations.CascadeType.MERGE, 
										org.hibernate.annotations.CascadeType.PERSIST})
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * @param registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(name="social_security_num")
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	/**
	 * Return calendar identifier.
	 * 
	 * @return
	 */
	public Integer getCalendar() {
		return calendar;
	}

	/**
	 * Set calendar identifier.
	 * 
	 * @param calendar
	 */
	public void setCalendar(Integer calendar) {
		this.calendar = calendar;
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

	@Transient
	public Map<String, Object> getLookups() {
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(ICompanyAlias.EMPLOYEE_ID, getId());
        map.put(ICompanyAlias.EMPLOYEE_REGISTRY_ID, getRegistry().getId());
        map.put(IRegistryAlias.REGISTRY_NAME, getRegistry().getName());
        map.put(IRegistryAlias.REGISTRY_SURNAME, getRegistry().getSurname());
        map.put(IRegistryAlias.REGISTRY_DOCUMENT, getRegistry().getDocument());
        map.put(IRegistryAlias.REGISTRY_ALIAS, getRegistry().getAlias());
        return map;
	}

}