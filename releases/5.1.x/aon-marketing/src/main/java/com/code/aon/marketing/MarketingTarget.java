package com.code.aon.marketing;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;

@Entity
@Table(name="target")
public class MarketingTarget implements ITransferObject {

	private static final long serialVersionUID = 2023116097455093989L;
	
	/** The id. */
	private Integer id;

	/** The target. */
	private Target target;	
	
	private boolean customer;
	
	/** The sellers. */
	private Set<TargetProfile> profiles = new HashSet<TargetProfile>();

	/**
	 * The empty constructor.
	 */
	public MarketingTarget(){
		this.target = new Target();
	}
	
	/**
	 * The constructor using a Target.
	 * 
	 * @param target the target
	 */
	public MarketingTarget(Target target) {
		this.target = target;
		this.id = target.getId();
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
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
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "registry")
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@OneToOne
	@JoinColumn(name = "registry", updatable = false, insertable = false)
	public Registry getRegistry() {
		return getTarget().getRegistry();
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		getTarget().setRegistry(registry);
	}

	/**
	 * Gets the advertising.
	 * 
	 * @return the advertising
	 */
	@Transient
	public Advertising getAdvertising() {
		return getTarget().getAdvertising();
	}

	/**
	 * Sets the advertising.
	 * 
	 * @param advertising the new advertising
	 */
	public void setAdvertising(Advertising advertising) {
		getTarget().setAdvertising(advertising);
	}
	
	
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<TargetProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<TargetProfile> profiles) {
		this.profiles = profiles;
	}

	@Formula("(select COUNT(*) from customer c where registry = c.registry)")
	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}
	
}
