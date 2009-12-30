package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents an entity of authomatic concepts.
 * 
 * @author Consulting & Development. Eugenio Castellano - 27-ene-2005
 * @since 1.0
 * 
 */  
@Entity
@Table(name="auto_concept")
public class AutoConcept implements ITransferObject {

	private static final long serialVersionUID = -57443534394518460L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Description of the concept.
     */
    private String description;

    /**
     * Void constructor.
     * 
     */
    public AutoConcept() {
    }
    
    /**
     * Constructor for an id of geozone.
     * 
     * @param pk
     *            unique key.
     */
    public AutoConcept(Integer pk) {
        this.id = pk;
    }

    /**
     * Returns the unique key.
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
     * Returns the description of this concept.
     * 
     * @return the description.
     */
    @Column(length=15,nullable=false)
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the description of this concept..
     * 
     * @param description the description of this concept..
     *            geozone name.
     */
    public void setDescription(String description) {
        this.description = description;
    }

}