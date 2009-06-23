package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a Target Segment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008 
 */
@Entity
@Table(name="target_segment")
public class TargetSegment implements ITransferObject {
	
	private static final long serialVersionUID = -2704634950055889788L;

	/** The Id. */
	private Integer Id;
	
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
		return Id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		Id = id;
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
	 * @param item the segment
	 */
	public void setSegment(CommercialSegment segment) {
		this.segment = segment;
	}

}