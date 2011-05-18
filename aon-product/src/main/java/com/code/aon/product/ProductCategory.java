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

@Entity
@Table(name="pcategory")
public final class ProductCategory implements ITransferObject {

	private static final long serialVersionUID = 5868168904695715154L;

    private Integer id;
    private String name;
    /**
     * Shows the pattern of the product's complementary description.  
     * For example, 
     * if <code>itemPattern</code> is "%TALLA% %COLOR%" and the description of the product
     * is "PANTALON LEVI'S", the description of the product will be "PANTALON
     * LEVI'S" + "42 AZUL".
     */
    private String itemPattern;
    private ProductCategoryGroup group;

    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="detail_pattern", length=64)
    public String getItemPattern() {
        return itemPattern;
    }

    public void setItemPattern(String itemPattern) {
        this.itemPattern = itemPattern;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pcategory_group")
    @ForeignKey(name = "FK_PCATEGORY_PCATEGORY_GROUP")
    @Index(name = "IDX_PCATEGORY_PCATEGORY_GROUP")    	            
    public ProductCategoryGroup getGroup() {
        return group;
    }

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
			.append(id)		
			.append(this.group)
			.append(this.itemPattern)						
			.append(this.name)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}