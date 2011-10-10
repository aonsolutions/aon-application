package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.project.Project;

@Entity
@Table(name="campaign_project")
public class CampaignProject implements ITransferObject {

	private static final long serialVersionUID = -2268140702529400027L;

	private Integer id;
	private Campaign campaign;
	private Project project ;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="campaign", nullable=false)
    @ForeignKey(name = "FK_CAMPAIGN_PROJECT_CAMPAIGN")
    @Index(name = "IDX_CAMPAIGN_PROJECT_CAMPAIGN")
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name="project", nullable=false)
    @ForeignKey(name = "FK_CAMPAIGN_PROJECT_PROJECT")
    @Index(name = "IDX_CAMPAIGN_PROJECT_PROJECT")
	public Project getProject() {
		return project;
	}
	public void setProject (Project project) {
		this.project = project;
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CampaignProject o = (CampaignProject) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.id, o.id)
				.append(this.campaign, o.campaign)
				.append(this.project , o.project )
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.campaign)
			.append(this.project )
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}