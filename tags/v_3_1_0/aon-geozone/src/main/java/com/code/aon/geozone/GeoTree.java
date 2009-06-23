package com.code.aon.geozone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="geotree")
public class GeoTree implements ITransferObject {

	private static final long serialVersionUID = -3277559566165396535L;

	/** The id. */
    private Integer id;

	/** The parent geozone. */
    private GeoZone parent;

	/** The child geozone. */
    private GeoZone child;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return this.id;
    }

	/**
	 * Sets the id.
	 * 
	 * @param primaryKey the primary key
	 */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

	/**
	 * Gets the parent geozone.
	 * 
	 * @return the parent geozone
	 */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="parent")
    @ForeignKey(name = "FK_GEOTREE_PARENT")
    @Index(name = "IDX_GEOTREE_PARENT")        
	public GeoZone getParent() {
		return parent;
	}

	/**
	 * Sets the parent geozone.
	 * 
	 * @param parent the parent geozone
	 */
	public void setParent(GeoZone parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets the child geozone.
	 * 
	 * @return the child geozone
	 */
    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade(value={org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="child", nullable = false)
    @ForeignKey(name = "FK_GEOTREE_CHILD")
    @Index(name = "IDX_GEOTREE_CHILD")    
    public GeoZone getChild() {
		return child;
	}

	/**
	 * Sets the child geozone.
	 * 
	 * @param child the child geozone
	 */
	public void setChild(GeoZone child) {
		this.child = child;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GeoTree o = (GeoTree) obj;
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