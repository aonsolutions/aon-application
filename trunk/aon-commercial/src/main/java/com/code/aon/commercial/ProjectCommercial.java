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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.commercial.enumeration.ProjectSource;
import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.project.Project;
import com.code.aon.seller.Seller;

@Entity
@Table(name="project_commercial")
@PrimaryKeyJoinColumn(name="project")
public class ProjectCommercial implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private Project project;
	private Target target;
	private Seller seller;
	private String comments;
	private ProjectSource source;
	private ProjectStatus status;
	private Date statusDate;
	private Integer probability;

	private Set<CommercialTracking> trackings = new HashSet<CommercialTracking>();
	
	public ProjectCommercial() {
		this.status = ProjectStatus.PENDING;
	}

	@Id
	@Column(name="project")
	@GeneratedValue(generator="project_id")
	@GenericGenerator(name="project_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="project")})
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="target", nullable=false)
	@ForeignKey(name = "FK_PROJECT_COMMERCIAL_TARGET")
	@Index(name = "IDX_PROJECT_COMMERCIAL_TARGET")
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="seller")
	@ForeignKey(name = "FK_PROJECT_COMMERCIAL_SELLER")
	@Index(name = "IDX_PROJECT_COMMERCIAL_SELLER")
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(nullable=false)
	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}
	
	@Column(nullable=false)
	public ProjectSource getSource() {
		return source;
	}

	public void setSource(ProjectSource source) {
		this.source = source;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="status_date")	
	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Integer getProbability() {
		return probability;
	}

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
		final ProjectCommercial o = (ProjectCommercial) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.comments, o.comments)
				.append(this.probability, o.probability)
				.append(this.project, o.project)
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
			.append(id)			
			.append(probability)
			.append(project)
			.append(seller)
			.append(source)
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