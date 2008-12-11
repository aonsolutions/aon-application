package com.code.aon.commercial;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.commercial.enumeration.Advertising;
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
public class Target implements ITransferObject, ITaxInfo, IRegistry{
	
	private static final long serialVersionUID = -7492435795404962788L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The advertising. */
	private Advertising advertising;

	/** The segments. */
	private Set<TargetSegment> segments = new HashSet<TargetSegment>();

	/** The items. */
	private Set<TargetItem> items = new HashSet<TargetItem>();

	/** The sellers. */
	private Set<TargetSeller> sellers = new HashSet<TargetSeller>();

	/** The sellers. */
	private Set<CommercialTracking> trackings = new HashSet<CommercialTracking>();
	
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
	 * Gets the advertising.
	 * 
	 * @return the advertising
	 */
	@Column(nullable=false)
	public Advertising getAdvertising() {
		return advertising;
	}

	/**
	 * Sets the advertising.
	 * 
	 * @param advertising the new advertising
	 */
	public void setAdvertising(Advertising advertising) {
		this.advertising = advertising;
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

	/**
	 * Gets the segments.
	 * 
	 * @return the segments
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<TargetSegment> getSegments() {
		return segments;
	}

	/**
	 * Sets the segments.
	 * 
	 * @param segments the new segments
	 */
	public void setSegments(Set<TargetSegment> segments) {
		this.segments = segments;
	}

	/**
	 * Gets the items.
	 * 
	 * @return the items
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetItem> getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 * 
	 * @param items the new items
	 */
	public void setItems(Set<TargetItem> items) {
		this.items = items;
	}

	/**
	 * Gets the sellers.
	 * 
	 * @return the sellers
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetSeller> getSellers() {
		return sellers;
	}

	/**
	 * Sets the sellers.
	 * 
	 * @param sellers the new sellers
	 */
	public void setSellers(Set<TargetSeller> sellers) {
		this.sellers = sellers;
	}

	/**
	 * Gets the trackings.
	 * 
	 * @return the trackings
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<CommercialTracking> getTrackings() {
		return trackings;
	}

	/**
	 * Sets the trackings.
	 * 
	 * @param trackings the new trackings
	 */
	public void setTrackings(Set<CommercialTracking> trackings) {
		this.trackings = trackings;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Target) {
			Target o = (Target) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}