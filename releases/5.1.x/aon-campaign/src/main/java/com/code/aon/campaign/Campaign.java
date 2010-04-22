package com.code.aon.campaign;

import java.util.Date;

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

import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.campaign.enumeration.CampaignType;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.WorkGroup;
import com.code.aon.project.ActivityType;

@Entity
@Table(name="campaign")
public class Campaign implements ITransferObject {
	
	private static final long serialVersionUID = 4650721068543504917L;

	private Integer id;
	private String description;
	private Process process;
	private ActivityType activityType;
	private Date startDate;
    private Date endDate;
    private WorkGroup workGroup;
    private CampaignType type;
    private CampaignStatus status;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name="process", nullable=false)
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	@ManyToOne
	@JoinColumn(name="activity_type")
	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	@Column(name="start_date", nullable=false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(name="end_date", nullable=false)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

    @ManyToOne
    @JoinColumn(name="workgroup", nullable=false)
    public WorkGroup getWorkGroup() {
        return workGroup;
    }
    
    public void setWorkGroup(WorkGroup workGroup) {
        this.workGroup = workGroup;
    }
    
    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }
    
	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Campaign o = (Campaign) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.id, o.id)
				.append(this.description, o.description)
				.append(this.process, o.process)
				.append(this.activityType, o.activityType)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.workGroup, o.workGroup)
				.append(this.type, o.type)
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.description)
			.append(this.process)
			.append(this.activityType)
			.append(this.startDate)
			.append(this.endDate)
			.append(this.workGroup)
			.append(this.type)
			.append(this.status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}