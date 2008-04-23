package com.code.aon.geozone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="geotree")
public class GeoTree implements ITransferObject {

	/** The id. */
    private Integer id;

	/** The parent geozone. */
    private GeoZone parent;

	/** The child geozone. */
    private GeoZone child;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return this.id;
    }

	/**
	 * Sets the id.
	 * 
	 * @param primaryKey the primary key
	 */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

	/**
	 * Gets the parent geozone.
	 * 
	 * @return the parent geozone
	 */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="parent")
	public GeoZone getParent() {
		return parent;
	}

	/**
	 * Sets the parent geozone.
	 * 
	 * @param parent the parent geozone
	 */
	public void setParent(GeoZone parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets the child geozone.
	 * 
	 * @return the child geozone
	 */
    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade(value={org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="child")
    public GeoZone getChild() {
		return child;
	}

	/**
	 * Sets the child geozone.
	 * 
	 * @param child the child geozone
	 */
	public void setChild(GeoZone child) {
		this.child = child;
	}
	
}