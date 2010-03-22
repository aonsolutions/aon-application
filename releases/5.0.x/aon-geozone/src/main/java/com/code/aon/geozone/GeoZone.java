package com.code.aon.geozone;

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
 * Transfer Object that represents an entity of GeoZone.
 * 
 * @author Consulting & Development. Eugenio Castellano - 27-ene-2005
 * @since 1.0
 * 
 */  
@Entity
@Table(name="geozone")
public class GeoZone implements ITransferObject {

	private static final long serialVersionUID = 8190182167605884507L;

	/** The id. */
    private Integer id;

	/** The name. */
    private String name;

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
	 * Gets the name.
	 * 
	 * @return the name
	 */
    @Column(length=32,nullable=false)
    public String getName() {
        return name;
    }

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
    public void setName(String name) {
        this.name = name;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GeoZone o = (GeoZone) obj;
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