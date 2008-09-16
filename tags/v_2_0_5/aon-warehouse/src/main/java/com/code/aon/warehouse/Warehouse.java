package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a Warehouse.
 * 
 * @author igayarre
 *
 */
@Entity
@Table(name="warehouse")
public class Warehouse implements ITransferObject{
	
	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * The name of the warehose
	 */
	private String name;
	
    /**
     * Returns the unique key.
     * 
     * @return Unique key.
     */
    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }

    /**
     * Assigns unique key
     * 
     * @param primaryKey
     *            unique key.
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    /**
     * Returns the warehouse name.
     * 
     * @return name.
     */
    @Column(length=32, nullable = false)
	public String getName() {
        return name;
    }

    /**
     * Assigns a name to the warehouse.
     * 
     * @param name
     *            warehouse name.
     */
    public void setName(String name) {
        this.name = name;
    }

}