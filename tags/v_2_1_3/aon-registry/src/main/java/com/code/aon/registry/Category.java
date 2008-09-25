package com.code.aon.registry;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.dao.IRegistryAlias;

/**
 * Transfer Object that represents the Category.
 * 
 * @author Consulting & Development. Aimar Tellitu - 03-ene-2006
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="category")
public class Category implements ITransferObject, ILookupObject  {

    /** The id. */
    private Integer id;

    /** The name. */
    private String name;

    /**
     * The empty constructor.
     */
    public Category() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Category(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
	@Column(nullable=false)
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
     * Gets the name.
     * 
     * @return the name
     */
    @Column(length=32, nullable = false)
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
	 * Gets the map of values used by the lookup.
	 * 
	 * @return the map
	 */
    @Transient
	public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(IRegistryAlias.CATEGORY_ID, getId());
        map.put(IRegistryAlias.CATEGORY_NAME, getName());
        return map;
    }
}