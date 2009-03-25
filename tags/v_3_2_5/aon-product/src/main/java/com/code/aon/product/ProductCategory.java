package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents product's categories.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="pcategory")
public final class ProductCategory implements ITransferObject {

	private static final long serialVersionUID = 5868168904695715154L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Category name.
     */
    private String name;

    /**
     * Shows the pattern of the product's complementary description.  
     * For example, 
     * if <code>itemPattern</code> is "%TALLA% %COLOR%" and the description of the product
     * is "PANTALON LEVI'S", the description of the product will be "PANTALON
     * LEVI'S" + "42 AZUL".
     */
    private String itemPattern;

    /**
     * Category group.
     */
    private ProductCategoryGroup group;

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
     * Returns the product category name.
     * 
     * @return Category name.
     */
    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the product category name.
     * 
     * @param name
     *            Category name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the pattern for the product description.
     * 
     * @return pattern.
     */
    @Column(name="detail_pattern", length=64)
    public String getItemPattern() {
        return itemPattern;
    }

    /**
     * Assigns the product description pattern.
     * 
     * @param itemPattern
     *            pattern.
     */
    public void setItemPattern(String itemPattern) {
        this.itemPattern = itemPattern;
    }

    /**
     * Returns the category group.
     * 
     * @return category group.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pcategory_group")
    public ProductCategoryGroup getGroup() {
        return group;
    }

    /**
     * Assigns the category group.
     * 
     * @param group
     *            category group.
     */
    public void setGroup(ProductCategoryGroup group) {
        this.group = group;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof ProductCategory) {
			ProductCategory o = (ProductCategory) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}