package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

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
public class Brand implements IActivableObject {

	private static final long serialVersionUID = 7360145800785775502L;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Brand o = (Brand) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)		
			.append(alias)
			.append(id)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

}