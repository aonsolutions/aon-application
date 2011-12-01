package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.enumeration.CampaignStatus;

@Entity
@Table(name="campaign")
public class Campaign implements ITransferObject, IEnterprise {
	
	private static final long serialVersionUID = 4650721068543504917L;

	private Integer id;
	private Enterprise enterprise;
	private String description;
	private Process process;
	private Date startDate;
    private Date endDate;
    private WorkGroup workGroup;
    private CampaignType campaignType;
    private boolean manual;
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

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_CAMPAIGN_ENTERPRISE")
    @Index(name = "IDX_CAMPAIGN_ENTERPRISE")
    @Override
	public Enterprise getEnterprise() {
		return enterprise;
	}
    @Override
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
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
    @ForeignKey(name = "FK_CAMPAIGN_PROCESS")
    @Index(name = "IDX_CAMPAIGN_PROCESS")
	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
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
    @ForeignKey(name = "FK_CAMPAIGN_WORKGROUP")
    @Index(name = "IDX_CAMPAIGN_WORKGROUP")
    public WorkGroup getWorkGroup() {
        return workGroup;
    }
    public void setWorkGroup(WorkGroup workGroup) {
        this.workGroup = workGroup;
    }
    
    @ManyToOne
    @JoinColumn(name="campaign_type")
    @ForeignKey(name = "FK_CAMPAIGN_CAMPAIGN_TYPE")
    @Index(name = "IDX_CAMPAIGN_CAMPAIGN_TYPE")
    public CampaignType getCampaignType() {
        return campaignType;
    }
    public void setCampaignType(CampaignType campaignType) {
        this.campaignType = campaignType;
    }

    public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}
	
	public CampaignStatus getStatus() {
        return status;
    }
    public void setStatus(CampaignStatus status) {
        this.status = status;
    }
    
    @Transient
    public boolean isPending() {
        return (this.getStatus() == CampaignStatus.PENDING);
    }
    
    @Transient
    public boolean isInProgress() {
        return (this.getStatus() == CampaignStatus.IN_PROGRESS);
    }

    @Transient
    public boolean isFinished() {
        return (this.getStatus() == CampaignStatus.FINISHED);
    }

    @Transient
    public boolean isDeleted() {
        return (this.getStatus() == CampaignStatus.DELETED);
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
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.workGroup, o.workGroup)
				.append(this.campaignType, o.campaignType)
				.append(this.manual, o.manual)
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
			.append(this.startDate)
			.append(this.endDate)
			.append(this.workGroup)
			.append(this.campaignType)
			.append(this.manual)
			.append(this.status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}