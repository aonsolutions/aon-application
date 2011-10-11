package com.code.aon.groupware;

import java.util.Calendar;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskPeriod;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.registry.Registry;

@Entity
@Table(name = "task")
public class Task implements ITransferObject, IEnterprise {

	private static final long serialVersionUID = 7790266586372796095L;

	private Integer id;
	private Enterprise enterprise;
	private String description;
	private Date startDate;
	private Date endDate;
	private Date dueDate;
	private Priority priority;
	private TaskStatus status;
	private int percent;
	private TaskHolder taskHolder;
	private WorkGroup workGroup;
	private TaskSource source;
	private Project project;
	private Registry registry;
	private ActivityType activityType;
	private TaskHolder sender;
	private String comments;
	private TaskPeriod repeatPeriod;
	
	public Task() {
		this.setStartDate(new Date());
		this.setStatus(TaskStatus.PENDING);
		this.setPercent(0);
		this.setSource(TaskSource.MANUAL);
		this.setPriority(Priority.NORMAL);
	}
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_TASK_ENTERPRISE")
    @Index(name = "IDX_TASK_ENTERPRISE")
    @Override
	public Enterprise getEnterprise() {
		return enterprise;
	}
    @Override
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

	@Column(length = 128, nullable = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "start_date")
	@Temporal(TemporalType.DATE)
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "due_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	@Column(nullable=true)
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}

	@ManyToOne
	@JoinColumn(name = "task_holder")
	@ForeignKey(name = "FK_TASK_TASK_HOLDER")
	@Index(name = "IDX_TASK_TASK_HOLDER")						            			
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	@ManyToOne
	@JoinColumn(name = "workgroup")
	@ForeignKey(name = "FK_TASK_WORKGROUP")
	@Index(name = "IDX_TASK_WORKGROUP")						            				
	public WorkGroup getWorkGroup() {
		return workGroup;
	}
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	public TaskSource getSource() {
		return source;
	}
	public void setSource(TaskSource source) {
		this.source = source;
	}

	@ManyToOne
	@JoinColumn(name = "project")
	@ForeignKey(name = "FK_TASK_PROJECT")
	@Index(name = "IDX_TASK_PROJECT")						            					
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne
	@JoinColumn(name = "registry")
	@ForeignKey(name = "FK_TASK_REGISTRY")
	@Index(name = "IDX_TASK_REGISTRY")						            					
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@ManyToOne
	@JoinColumn(name = "activity_type")
	@ForeignKey(name = "FK_TASK_ACTIVITY_TYPE")
	@Index(name = "IDX_TASK_ACTIVITY_TYPE")						            						
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	@ManyToOne
	@JoinColumn(name = "sender")
	@ForeignKey(name = "FK_TASK_SENDER")
	@Index(name = "IDX_TASK_SENDER")						            							
	public TaskHolder getSender() {
		return sender;
	}
	public void setSender(TaskHolder sender) {
		this.sender = sender;
	}

	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Column(name = "repeat_period")
	public TaskPeriod getRepeatPeriod() {
		return repeatPeriod;
	}
	public void setRepeatPeriod(TaskPeriod repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	@Transient
	public boolean isExpired() {
		if (this.getStatus() != TaskStatus.FINISHED && this.getStatus() != TaskStatus.DELETED) {
			Date date = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set( Calendar.HOUR, 0);
			c.set( Calendar.MINUTE, 0);
			c.set( Calendar.SECOND, 0);
			c.set( Calendar.MILLISECOND, 0);
			c.set( Calendar.AM_PM, Calendar.AM);
			return (this.getDueDate().before(c.getTime()));
		}
		return false;
	}
	
	@Transient
    public boolean isPending() {
        return getStatus().equals(TaskStatus.PENDING);
    }
	@Transient
    public boolean isDeleted() {
        return getStatus().equals(TaskStatus.DELETED);
    }
	@Transient
    public boolean isInProgress() {
        return getStatus().equals(TaskStatus.IN_PROGRESS);
    }
	@Transient
    public boolean isFinished() {
        return getStatus().equals(TaskStatus.FINISHED);
    }
	@Transient
    public boolean isHighPriority() {
        return getPriority().equals(Priority.HIGH);
    }
	@Transient
    public boolean isLowPriority() {
        return getPriority().equals(Priority.LOW);
    }
	@Transient
    public boolean isNormalPriority() {
        return getPriority().equals(Priority.NORMAL);
    }
	@Transient
    public boolean isNonePriority() {
        return getPriority().equals(Priority.NONE);
    }
	@Transient
    public boolean isSourceProcess() {
        return getSource().equals(TaskSource.PROCESS);
    }
	@Transient
    public boolean isSourceAssigned() {
        return getSource().equals(TaskSource.ASSIGNED);
    }
	@Transient
    public boolean isSourceManual() {
        return getSource().equals(TaskSource.MANUAL);
    }
	@Transient
    public boolean isRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.NONE));
    }
	@Transient
    public boolean isDailyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.DAILY));
    }
	@Transient
    public boolean isWeeklyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.WEEKLY));
    }
	@Transient
	public boolean isBiWeeklyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.BI_WEEKLY));
    }
	@Transient
	public boolean isMonthlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.MONTHLY));
    }
	@Transient
	public boolean isBiMonthlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.BI_MONTHLY));
    }
	@Transient
	public boolean isThreeMonthlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.THREE_MONTHLY));
    }
	@Transient
	public boolean isFourMonthlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.FOUR_MONTHLY));
    }
	@Transient
	public boolean isHalfYearlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.HALF_YEARLY));
    }
	@Transient
	public boolean isYearlyRepeatable() {
        return (!getRepeatPeriod().equals(TaskPeriod.YEARLY));
    }
	@Transient
	public boolean isMine(TaskHolder holder) {
		if (holder == null) {
			return false;
		}
		return (this.getTaskHolder() == null) ? false : holder.equals(this.getTaskHolder());
	}
	@Transient
	public boolean isUnassigned() {
		return (this.getTaskHolder() == null || this.getTaskHolder().getId() == null);
	}
	@Transient
	public boolean isCommentsNotEmpty() {
		return StringUtils.isNotBlank(this.comments);
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Task o = (Task) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)
				.append(this.description, o.description)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.dueDate, o.dueDate)
				.append(this.priority, o.priority)
				.append(this.status, o.status)
				.append(this.percent, o.percent)
				.append(this.taskHolder, o.taskHolder)
				.append(this.workGroup, o.workGroup)
				.append(this.source, o.source)
				.append(this.project, o.project)
				.append(this.registry, o.registry)
				.append(this.activityType, o.activityType)
				.append(this.sender, o.sender)
				.append(this.comments, o.comments)
				.append(this.repeatPeriod, o.repeatPeriod)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id)
			.append(this.enterprise)
			.append(this.description)
			.append(this.startDate)
			.append(this.endDate)
			.append(this.dueDate)
			.append(this.priority)
			.append(this.status)
			.append(this.percent)
			.append(this.taskHolder)
			.append(this.workGroup)
			.append(this.source)
			.append(this.project)
			.append(this.registry)
			.append(this.activityType)
			.append(this.sender)
			.append(this.comments)
			.append(this.repeatPeriod)
			.toHashCode();
	}	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}