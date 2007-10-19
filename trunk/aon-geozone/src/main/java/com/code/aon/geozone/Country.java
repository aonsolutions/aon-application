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
 * Transfer Object that represents an entity of a country.
 * 
 * @author Consulting & Development. Iñigo Gayarre - 23-mar-2006
 * @since 1.0
 * 
 */  
@Entity
@Table(name="country")
@Inheritance(strategy=InheritanceType.JOINED )
public class Country implements ITransferObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * The name of the country.
     */
    private String name;

    /**
     * Code.
     */
    private String code;

    /**
     * Void constructor.
     * 
     */
    public Country() {
    }
    
    /**
     * Constructor for a country key.
     * 
     * @param pk
     *            Country key.
     */
    public Country(Integer pk) {
        this.id = pk;
    }

    
    /**
     * Returns the code
     * 
	 * @return Returns the code.
	 */
    @Column(length=2,nullable=false)
	public String getCode() {
		return code;
	}

	/**
	 * Assigns the code
	 * 
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
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
     * Returns the name of this country.
     * 
     * @return The name.
     */
    @Column(length=32,nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the name of this country.
     * 
     * @param name
     *            The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * Returns the string that represents an object.
     * 
     * @return String represents the object.
     * @see java.lang.Object#toString()
     */
	public String toString() {
		return "COUNTRYTO[id=" + this.id + ",code=" + this.code + ",name=" + this.name + "]";
	}
}