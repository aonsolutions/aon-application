package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a product category.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name="product_category")
public class ProductCategory implements ITransferObject{

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Brand's name.
     */
    private String alias;

    /**
     * Active
     */
	private boolean active = true;

	private Section section;
	
    /**
     * Parent
     */
	private ProductCategory parent;

	private Set<ProductCategoryDetail> details;

    @Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "productCategory", cascade={CascadeType.REMOVE})
	public Set<ProductCategoryDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ProductCategoryDetail> details ) {
		this.details = details;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent", nullable = true)
	public ProductCategory getParent() {
		return parent;
	}

	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section", nullable = true)
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	

}