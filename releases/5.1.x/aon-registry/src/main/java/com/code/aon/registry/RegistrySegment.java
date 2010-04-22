package com.code.aon.registry;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents a Registry Segment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008 
 */
@Entity
@Table(name="rsegment")
public class RegistrySegment implements ITransferObject {
	
	private static final long serialVersionUID = -2704634950055889788L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The segment. */
	private Segment segment;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="registry", nullable=false , updatable=false)
	@ForeignKey(name = "FK_REGISTRY_SEGMENT_REGISTRY")
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the segment.
	 * 
	 * @return the segment
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="segment", nullable=false )
	@ForeignKey(name = "FK_REGISTRY_SEGMENT_SEGMENT")
	public Segment getSegment() {
		return segment;
	}

	/**
	 * Sets the segment.
	 * 
	 * @param segment the segment
	 */
	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistrySegment o = (RegistrySegment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.segment, o.segment)
				.append(this.registry, o.registry)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(segment)
			.append(registry)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}