package com.code.aon.product;

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
import com.code.aon.product.dao.IProductAlias;

/**
 * Transfer Object that represents a brand.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name="brand")
public class Brand implements ITransferObject, ILookupObject {

    /**
     * Unique key.
     */
	
    private Integer id;

    /**
     * Brand's name.
     */
    private String name;

    /**
     * Constructor.
     * 
     */
    public Brand() {
    }

    /**
     * Constructor for this unique key.
     * 
     * @param pk
     *            Unique key.
     */
    public Brand(Integer pk) {
        this.id = pk;
    }

    /**
     * Returns the unique key.
     * 
     * @return Unique key.
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Asigns the unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrun this brand's name.
     * 
     * @return brand's name.
     */
    @Column(length=64, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Asigns brand's name.
     * 
     * @param name
     *            brand's name.
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
        map.put(IProductAlias.BRAND_ID, getId());
        map.put(IProductAlias.BRAND_NAME, getName());
        return map;
    }

}