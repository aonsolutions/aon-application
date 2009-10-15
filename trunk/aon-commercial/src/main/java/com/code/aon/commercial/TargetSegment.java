package com.code.aon.commercial;

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
 * Transfer Object that represents a Target Segment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008 
 */
@Entity
@Table(name="target_segment")
public class TargetSegment implements ITransferObject {
	
	private static final long serialVersionUID = -2704634950055889788L;

	/** The id. */
	private Integer id;
	
	/** The target. */
	private Target target;
	
	/** The segment. */
	private CommercialSegment segment;

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
	 * Gets the target.
	 * 
	 * @return the target
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="target", nullable=false , updatable=false)
	@ForeignKey(name = "FK_TARGET_SEGMENT_TARGET")
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target the target
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * Gets the segment.
	 * 
	 * @return the segment
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="segment", nullable=false )
	@ForeignKey(name = "FK_TARGET_SELLER_SEGMENT")
	public CommercialSegment getSegment() {
		return segment;
	}

	/**
	 * Sets the segment.
	 * 
	 * @param segment the segment
	 */
	public void setSegment(CommercialSegment segment) {
		this.segment = segment;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TargetSegment o = (TargetSegment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.segment, o.segment)
				.append(this.target, o.target)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(segment)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}