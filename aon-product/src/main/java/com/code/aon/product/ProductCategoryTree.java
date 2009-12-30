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

/**
 * Transfer Object that represents a node between categories.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="pcategory_tree")
public class ProductCategoryTree implements ITransferObject {

	private static final long serialVersionUID = 4085534951360322369L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Parent category.
     */
    private ProductCategory parent;

    /**
     * Child category.
     */
    private ProductCategory child;

    /**
     * Constructor for this node.
     * 
     * @param pk
     *            unique key.
     * @param parent
     *            Parent category.
     * @param child
     *            Child category.
     */
    public ProductCategoryTree(Integer pk, ProductCategory parent, ProductCategory child) {
        this.id = pk;
        this.parent = parent;
        this.child = child;
    }

    /**
     * Returns the unique key.
     * 
     * @return unique key.
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param id
     *            unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the child category of this node.
     * 
     * @return child category.
     */
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "child")
    @ForeignKey(name = "FK_PCATEGORY_TREE_CHILD")
    @Index(name = "IDX_PCATEGORY_TREE_CHILD")    	        	
    public ProductCategory getChild() {
        return child;
    }

    /**
     * Assigns the child category of this node.
     * 
     * @param child
     *            child category.
     */
    public void setChild(ProductCategory child) {
        this.child = child;
    }

    /**
     * Returns the parent category of this node.
     * 
     * @return parent category.
     */
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent")
    @ForeignKey(name = "FK_PCATEGORY_TREE_PARENT")
    @Index(name = "IDX_PCATEGORY_TREE_PARENT")    	        		
    public ProductCategory getParent() {
        return parent;
    }

    /**
     * Assigns the parent category of this node.
     * 
     * @param parent
     *            parent category.
     */
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
			.append(child)		
			.append(id)								
			.append(parent)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}