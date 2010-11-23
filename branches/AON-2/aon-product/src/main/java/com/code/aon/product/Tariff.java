package com.code.aon.product;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.dao.IProductAlias;

/**
 * Transfer Object that represents a tariff
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="tariff")
@Inheritance(strategy=InheritanceType.JOINED )
public class Tariff implements ITransferObject, ILookupObject {

    /**
     * Primary key.
     */
    private Integer id;

    /**
     * Name of the tariff.
     */
    private String name;

    /**
     * Void constructor.
     * 
     */
    public Tariff() {
    }

    /**
     * Constructor for this unique key.
     * 
     * @param pk
     *            Unique key.
     */
    public Tariff(Integer pk) {
        this.id = pk;
    }

    /**
     * Returns unique key.
     * 
     * @return unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of the tariff.
     * 
     * @return name of thhe tariff.
     */
    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assign the name for this tariff.
     * 
     * @param name
     *            name of the tariff.
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.common.ILookupObject#lookups()
     */
    @Transient
    public Map<String,Object> getLookups() {
    	Map<String,Object> map = new HashMap<String,Object>();
        map.put(IProductAlias.TARIFF_ID, getId());
        map.put(IProductAlias.TARIFF_NAME, getName());
        return map;
    }

}