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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.commercial.enumeration.ProjectSource;
import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.common.ITransferObject;
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
	
    /** The name. */
    private String name;	
    
    /** The comments. */
    private String comments;
	
	/** The date. */
	private Date date;
	
	/** The target. */
	private Target target;
	
	/** The seller. */
	private Seller seller;

	/** The status. */
	private ProjectStatus status;
	
	/** The source. */
	private ProjectSource source;

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
	
	@Column(nullable = false, length = 64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	@Temporal(TemporalType.DATE)
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
	 * Gets the source.
	 *
	 * @return the source
	 */
	@Column(nullable=false)
	public ProjectSource getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(ProjectSource source) {
		this.source = source;
	}

	/**
	 * Gets the status date.
	 *
	 * @return the status date
	 */
	@Temporal(TemporalType.DATE)
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
				.append(this.comments, o.comments)
				.append(this.date, o.date)
				.append(this.name, o.name)
				.append(this.probability, o.probability)
				.append(this.seller, o.seller)				
				.append(this.source, o.source)
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
			.append(comments)
			.append(date)
			.append(name)
			.append(id)			
			.append(probability)
			.append(seller)
			.append(source)
			.append(status)
			.append(statusDate)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("comments", StringUtils.abbreviate(comments, 64)).
			append("date", date).
			append("name", name).
			append("id", id).
			append("probability", probability).
			append("seller", (seller != null) ? seller.getId() : null).
			append("source", source).
			append("status", status).
			append("statusDate", statusDate).
			append("target", target.getId()).
			toString();
	}
	
}