package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.registry.Registry;

@Entity
@Table(name="daily_tracking")
public class DailyTracking implements ITransferObject, IEnterprise {
	
	private static final long serialVersionUID = -9211987133575207355L;

	private Integer id;
	private Enterprise enterprise;
	private TaskHolder taskHolder;
    private Date trackingDate;
    private Double trackingDuration;
    private JobType jobType;
    private Registry registry;
    private Project project;
    private ActivityType activityType;
	private String comments;
    private Double cost;
	
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
    @ForeignKey(name = "FK_DT_ENTERPRISE")
    @Index(name = "IDX_DT_ENTERPRISE")
    @Override
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
    @Override
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

	@ManyToOne
	@JoinColumn(name="task_holder", nullable=false)
	@ForeignKey(name = "FK_DT_TASK_HOLDER")
	@Index(name = "IDX_DT_TASK_HOLDER")						
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

    @Column(name="tracking_date", nullable=false)
    @Temporal(TemporalType.DATE)
    public Date getTrackingDate() {
        return trackingDate;
    }
    public void setTrackingDate(Date trackingDate) {
        this.trackingDate = trackingDate;
    }

    @Column(name="tracking_duration", nullable=false)
    public Double getTrackingDuration() {
        return trackingDuration;
    }
    public void setTrackingDuration(Double trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    @ManyToOne
    @JoinColumn(name="job_type", nullable=false)
	@ForeignKey(name = "FK_DT_JOB_TYPE")
	@Index(name = "IDX_DT_JOB_TYPE")						        
    public JobType getJobType() {
        return jobType;
    }
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @ManyToOne
    @JoinColumn(name="registry")
	@ForeignKey(name = "FK_DT_REGISTRY")
	@Index(name = "IDX_DT_REGISTRY")						            
    public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@ManyToOne
    @JoinColumn(name="project")
	@ForeignKey(name = "FK_DT_DOSSIER")
	@Index(name = "IDX_DT_DOSSIER")						    
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    @ManyToOne
    @JoinColumn(name="activity_type")
	@ForeignKey(name = "FK_DT_ACTIVITY")
	@Index(name = "IDX_DT_ACTIVITY")						        
    public ActivityType getActivityType() {
        return activityType;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	@Transient
	public Double getAmount() {
		return CommonUtil.round(getTrackingDuration() * getCost());
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DailyTracking o = (DailyTracking) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)
				.append(this.taskHolder, o.taskHolder)
				.append(this.trackingDate, o.trackingDate)
				.append(this.trackingDuration, o.trackingDuration)
				.append(this.jobType, o.jobType)
				.append(this.registry, o.registry)
				.append(this.project, o.project)
				.append(this.activityType, o.activityType)
				.append(this.comments, o.comments)
				.append(this.cost, o.cost)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id)
			.append(this.enterprise)
			.append(this.taskHolder)
			.append(this.trackingDate)
			.append(this.trackingDuration)
			.append(this.jobType)
			.append(this.registry)
			.append(this.project)
			.append(this.activityType)
			.append(this.comments)
			.append(this.cost)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}