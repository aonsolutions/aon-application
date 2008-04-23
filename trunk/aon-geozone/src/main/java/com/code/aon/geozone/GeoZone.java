package com.code.aon.geozone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents an entity of GeoZone.
 * 
 * @author Consulting & Development. Eugenio Castellano - 27-ene-2005
 * @since 1.0
 * 
 */  
@Entity
@Table(name="geozone")
public class GeoZone implements ITransferObject {

	/** The id. */
    private Integer id;

	/** The name. */
    private String name;

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
	 * Gets the name.
	 * 
	 * @return the name
	 */
    @Column(length=32,nullable=false)
    public String getName() {
        return name;
    }

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the string that represents an object.
     * 
     * @return String 
     * @see java.lang.Object#toString()
     */
	public String toString() {
		return "GeoZoneTO[id=" + this.id + ",name=" + this.name + "]";
	}
}