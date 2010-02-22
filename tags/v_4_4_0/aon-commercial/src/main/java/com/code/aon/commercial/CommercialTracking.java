package com.code.aon.commercial;

import java.util.Date;

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

import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.seller.Seller;

/**
 * Transfer Object that represents a Commercail Tracking.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008
 */
@Entity
@Table(name="commercial_tracking")
public class CommercialTracking implements ITransferObject {
	
	private static final long serialVersionUID = -3912751271868799471L;

	/** The id. */
	private Integer id;
	
	/** The date. */
	private Date date;
	
	/** The target. */
	private Target target;
	
	/** The seller. */
	private Seller seller;

	/** The activity. */
	private CommercialActivity activity;
	
	/** The comments. */
	private String comments;
	
	/** The status. */
	private CommercialTrackingStatus status;
	
	/** The next. */
	private CommercialTracking next;
	
	public CommercialTracking() {
		this.status = CommercialTrackingStatus.PENDING;
	}

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
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="target", nullable=false , updatable=false)
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_TARGET")
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
	 * Gets the seller.
	 * 
	 * @return the seller
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="seller", nullable=false )
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_SELLER")
	public Seller getSeller() {
		return seller;
	}

	/**
	 * Sets the seller.
	 * 
	 * @param seller the seller
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	/**
	 * Gets the activity.
	 * 
	 * @return the activity
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="activity", nullable=false )
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_ACTIVITY")
	public CommercialActivity getActivity() {
		return activity;
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Column(length=255)
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments the new comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Sets the activity.
	 * 
	 * @param activity the activity
	 */
	public void setActivity(CommercialActivity activity) {
		this.activity = activity;
	}
	
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Column(nullable=false)
	public CommercialTrackingStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the new status
	 */
	public void setStatus(CommercialTrackingStatus status) {
		this.status = status;
	}

	/**
	 * Gets the next.
	 * 
	 * @return the next
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="next_commercial_tracking" )
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_NEXT_COMMERCIAL_TRACKING")
	public CommercialTracking getNext() {
		return next;
	}

	/**
	 * Sets the next.
	 * 
	 * @param next the new next
	 */
	public void setNext(CommercialTracking next) {
		this.next = next;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CommercialTracking o = (CommercialTracking) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.activity, o.activity)				
				.append(this.comments, o.comments)
				.append(this.date, o.date)				
				.append(this.next, o.next)
				.append(this.seller, o.seller)				
				.append(this.status, o.status)
				.append(this.target, o.target)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(activity)
			.append(comments)	
			.append(date)
			.append(id)			
			.append(next)
			.append(seller)
			.append(status)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}