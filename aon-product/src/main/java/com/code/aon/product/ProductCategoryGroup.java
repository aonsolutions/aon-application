package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents product's categories groups.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="pcategory_group")
public class ProductCategoryGroup implements ITransferObject {

	private static final long serialVersionUID = -871568980767955180L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Category group name.
     */
    private String name;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProductCategoryGroup o = (ProductCategoryGroup) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)								
			.append(name)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}