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
 * Transfer Object that represents product's categories groups.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="pcategory_group")
public class ProductCategoryGroup implements ITransferObject, ILookupObject  {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Category group name.
     */
    private String name;

    /**
     * Void contructor.
     * 
     */
    public ProductCategoryGroup() {
    }

    /**
     * Constructor for this unique key.
     * 
     * @param pk
     *            Unique key.
     */
    public ProductCategoryGroup(Integer pk) {
        this.id = pk;
    }

    /**
     * Return the unique key.
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
     * Assigns thew unique key.
     * 
     * @param primaryKey
     *            unique key.
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    /**
     * Returns the product category group name.
     * 
     * @return Category group name.
     */
    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the product category group name.
     * 
     * @param name
     *            Category group name.
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
        map.put(IProductAlias.PRODUCT_CATEGORY_GROUP_ID, getId());
        map.put(IProductAlias.PRODUCT_CATEGORY_GROUP_NAME, getName());
        return map;
    }

}