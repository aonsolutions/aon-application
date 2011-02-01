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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

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
    @ForeignKey(name = "FK_PCATEGORY_PCATEGORY_GROUP")
    @Index(name = "IDX_PCATEGORY_PCATEGORY_GROUP")    	            
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
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProductCategory o = (ProductCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.group, o.group)			
				.append(this.itemPattern, o.itemPattern)								
				.append(this.name, o.name)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(group)
			.append(id)		
			.append(itemPattern)						
			.append(name)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}