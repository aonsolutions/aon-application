package com.code.aon.product;

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
@Table(name="pcategory_tree")
public class ProductCategoryTree implements ITransferObject {

	private static final long serialVersionUID = 4085534951360322369L;

    private Integer id;
    private ProductCategory parent;
    private ProductCategory child;

    public ProductCategoryTree(Integer pk, ProductCategory parent, ProductCategory child) {
        this.id = pk;
        this.parent = parent;
        this.child = child;
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "child")
    @ForeignKey(name = "FK_PCATEGORY_TREE_CHILD")
    @Index(name = "IDX_PCATEGORY_TREE_CHILD")    	        	
    public ProductCategory getChild() {
        return child;
    }

    public void setChild(ProductCategory child) {
        this.child = child;
    }

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent")
    @ForeignKey(name = "FK_PCATEGORY_TREE_PARENT")
    @Index(name = "IDX_PCATEGORY_TREE_PARENT")    	        		
    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProductCategoryTree o = (ProductCategoryTree) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.child, o.child)				
				.append(this.parent, o.parent)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)								
			.append(this.child)		
			.append(this.parent)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}