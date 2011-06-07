package com.code.aon.commercial;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.seller.Seller;

/**
 * Transfer Object that represents a Project.
 * 
 * @author Consulting & Development. Aimar Tellitu - 03-jun-2011
 */
@Entity
@Table(name="project")
public class Project implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	/** The id. */
	private Integer id;
	
    private String description;	
	
	/** The date. */
	private Date date;
	
	/** The target. */
	private Target target;
	
	/** The seller. */
	private Seller seller;

	/** The status. */
	private ProjectStatus status;

	/** The status date. */
	private Date statusDate;
	
	/** The probability. */
	private Integer probability;

	private Set<CommercialTracking> trackings = new HashSet<CommercialTracking>();
	
	public Project() {
		this.status = ProjectStatus.PENDING;
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(nullable = false, length = 64)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}	
	
	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	@Temporal(TemporalType.TIMESTAMP)
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
	@JoinColumn(name="target", nullable=false)
	@ForeignKey(name = "FK_PROJECT_TARGET")
	@Index(name = "IDX_PROJECT_TARGET")
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
	@JoinColumn(name="seller")
	@ForeignKey(name = "FK_PROJECT_SELLER")
	@Index(name = "IDX_PROJECT_SELLER")
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
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Column(nullable=false)
	public ProjectStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the new status
	 */
	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	/**
	 * Gets the status date.
	 *
	 * @return the status date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="status_date")	
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * Sets the status date.
	 *
	 * @param statusDate the new status date
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * Gets the probability.
	 *
	 * @return the probability
	 */
	public Integer getProbability() {
		return probability;
	}

	/**
	 * Sets the probability.
	 *
	 * @param probability the new probability
	 */
	public void setProbability(Integer probability) {
		this.probability = probability;
	}

	@OneToMany(mappedBy = "project", cascade={CascadeType.REMOVE})
	public Set<CommercialTracking> getTrackings() {
		return trackings;
	}

	public void setTrackings(Set<CommercialTracking> trackings) {
		this.trackings = trackings;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Project o = (Project) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.date, o.date)
				.append(this.description, o.description)
				.append(this.probability, o.probability)
				.append(this.seller, o.seller)				
				.append(this.status, o.status)
				.append(this.statusDate, o.statusDate)
				.append(this.target, o.target)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(date)
			.append(description)
			.append(id)			
			.append(probability)
			.append(seller)
			.append(status)
			.append(statusDate)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}