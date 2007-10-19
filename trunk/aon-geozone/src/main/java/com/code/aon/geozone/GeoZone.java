package com.code.aon.geozone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy=InheritanceType.JOINED )
public class GeoZone implements ITransferObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Name of the geozone.
     */
    private String name;

    /**
     * Void constructor.
     * 
     */
    public GeoZone() {
    }
    
    /**
     * Constructor for an id of geozone.
     * 
     * @param pk
     *            unique key.
     */
    public GeoZone(Integer pk) {
        this.id = pk;
    }

    /**
     * Reruens the unique key.
     * 
     * @return unique key.
     */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return this.id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param primaryKey
     *            Unique key.
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    /**
     * Returns the name of this geozone.
     * 
     * @return the name.
     */
    @Column(length=32,nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the name of the geozone.
     * 
     * @param name
     *            geozone name.
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