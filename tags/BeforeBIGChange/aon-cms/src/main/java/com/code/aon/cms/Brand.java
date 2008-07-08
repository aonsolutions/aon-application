package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a brand.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name="brand")
public class Brand implements ITransferObject{

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

	private Set<BrandDetail> details;

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

	@OneToMany(mappedBy = "brand", cascade={CascadeType.REMOVE})
	public Set<BrandDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<BrandDetail> details ) {
		this.details = details;
	}


}