package com.code.aon.commercial;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
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

import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a Target.
 * 
 * @author Consulting & Development. Joseba Urkiri - 11-nov-2005
 * @since 1.0
 */
@Entity
@Table(name="target")
public class Target implements ITransferObject, ILookupObject, ITaxInfo, IRegistry{
	
	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;

	/**
	 * The empty onstructor.
	 */
	public Target(){
		this.registry = new Registry();
	}
	
	/**
	 * The constructor using a Registry.
	 * 
	 * @param registry the registry
	 */
	public Target(Registry registry) {
		this.registry = registry;
		this.id = registry.getId();
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

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the map of values used by the lookup.
	 * 
	 * @return the map
	 */
	@Transient
	public Map<String, Object> getLookups() {
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(ICommercialAlias.TARGET_REGISTRY_ID, getId());
        map.put(ICommercialAlias.TARGET_REGISTRY_DOCUMENT, getRegistry().getDocument());
        map.put(ICommercialAlias.TARGET_REGISTRY_NAME, getRegistry().getName());
        map.put(ICommercialAlias.TARGET_REGISTRY_SURNAME, getRegistry().getSurname());
        map.put(ICommercialAlias.TARGET_REGISTRY_ALIAS, getRegistry().getAlias());
        return map;
	}

	/**
	 * Checks if a surcharge has to be applied to the target. 
	 * Necessary to implement <code>ITaxInfo</code>
	 * 
	 * @return true, if is surcharge
	 */
	@Transient
	public boolean isSurcharge() {
		return false;
	}

	/**
	 * Checks if is tax free. Necessary to implement <code>ITaxInfo</code>
	 * 
	 * @return true, if is tax free
	 */
	@Transient
	public boolean isTaxFree() {
		return false;
	}

	/**
	 * Checks if a withholding is applied. Necessary to implement <code>ITaxInfo</code>
	 * 
	 * @return true, if a withholding is applied
	 */
	@Transient
	public boolean isWithholding() {
		return false;
	}
}