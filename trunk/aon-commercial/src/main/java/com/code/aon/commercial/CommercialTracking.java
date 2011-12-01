package com.code.aon.commercial;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

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
	
	/** The project. */
	private ProjectCommercial project;
	
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
	
	/** The offer. */
	private Offer offer;
	
	/** The end date. */
	private Date endDate;
	
	/** The all day. */
	private boolean allDay;
	
	/** The location. */
	private String location;
	
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
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
	 * Gets the project.
	 * 
	 * @return the project
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="project", nullable=false)
	@ForeignKey(name = "FK_COMMERCIAL_TRACKING_PROJECT")
	@Index(name = "IDX_COMMERCIAL_TRACKING_PROJECT")
	public ProjectCommercial getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 * 
	 * @param project the project
	 */
	public void setProject(ProjectCommercial project) {
		this.project = project;
	}

	/**
	 * Gets the seller.
	 * 
	 * @return the seller
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="seller", nullable=false)
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_SELLER")
	@Index(name = "IDX_COMMERCAIL_TRACKING_SELLER")
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
	@JoinColumn(name="activity", nullable=false)
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_ACTIVITY")
	@Index(name = "IDX_COMMERCAIL_TRACKING_ACTIVITY")
	public CommercialActivity getActivity() {
		return activity;
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Lob
	@Type(type="stringClob")
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
	@JoinColumn(name="next_commercial_tracking")
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_NEXT")
	@Index(name = "IDX_COMMERCAIL_TRACKING_NEXT")
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
	
	/**
	 * Gets the offer.
	 * 
	 * @return the offer
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="offer")
	@ForeignKey(name = "FK_COMMERCAIL_TRACKING_OFFER")
	@Index(name = "IDX_COMMERCAIL_TRACKING_OFFER")
	public Offer getOffer() {
		return offer;
	}

	/**
	 * Sets the offer.
	 * 
	 * @param offer the new offer
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Checks if is all day.
	 *
	 * @return true, if is all day
	 */
	@Column(nullable=false)
	public boolean isAllDay() {
		return allDay;
	}

	/**
	 * Sets the all day.
	 *
	 * @param allDay the new all day
	 */
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	@Column(length=255)
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public void setLocation(String location) {
		this.location = location;
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
				.append(this.allDay, o.allDay)
				.append(this.comments, o.comments)
				.append(this.date, o.date)				
				.append(this.endDate, o.endDate)
				.append(this.location, o.location)
				.append(this.next, o.next)
				.append(this.offer, o.offer)
				.append(this.project, o.project)				
				.append(this.seller, o.seller)				
				.append(this.status, o.status)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(activity)
			.append(allDay)
			.append(comments)	
			.append(date)
			.append(endDate)
			.append(id)			
			.append(location)
			.append(next)
			.append(offer)
			.append(project)			
			.append(seller)
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}