package com.code.aon.project;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.enumeration.TaskPeriod;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;

/**
 * The Class Task.
 */
@Entity
@Table(name = "task")
public class Task implements ITransferObject {

	private static final long serialVersionUID = 7790266586372796095L;

	/** The id. */
	private Integer id;

	/** The description. */
	private String description;

	/** The start date. */
	private Date startDate;

	/** The end date. */
	private Date endDate;

	/** The due date. */
	private Date dueDate;

	/** The priority. */
	private Priority priority;

	/** The status. */
	private TaskStatus status;

	/** The percent. */
	private int percent;

	/** The user. */
	private User user;

	/** The work group. */
	private WorkGroup workGroup;

	/** The source. */
	private TaskSource source;

	/** The dossier. */
	private Dossier dossier;

	/** The activity. */
	private Activity activity;

	/** The sender. */
	private User sender;

	/** The comments. */
	private String comments;

	/** The source. */
	private TaskPeriod repeatPeriod;
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length = 128, nullable = false)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate
	 *            the start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate
	 *            the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the due date.
	 * 
	 * @return the due date
	 */
	@Column(name = "due_date", nullable = false)
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * Sets the due date.
	 * 
	 * @param dueDate
	 *            the due date
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Gets the priority.
	 * 
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Sets the priority.
	 * 
	 * @param priority
	 *            the priority
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	/**
	 * Gets the percent.
	 * 
	 * @return the percent
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * Sets the percent.
	 * 
	 * @param percent
	 *            the percent
	 */
	public void setPercent(int percent) {
		this.percent = percent;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Fetch(FetchMode.JOIN)
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the work group.
	 * 
	 * @return the work group
	 */
	@ManyToOne
	@JoinColumn(name = "workgroup")
	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	/**
	 * Sets the work group.
	 * 
	 * @param workGroup
	 *            the work group
	 */
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	/**
	 * Gets the source.
	 * 
	 * @return the source
	 */
	public TaskSource getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 * 
	 * @param source
	 *            the source
	 */
	public void setSource(TaskSource source) {
		this.source = source;
	}

	/**
	 * Gets the dossier.
	 * 
	 * @return the dossier
	 */
	@ManyToOne
	@JoinColumn(name = "dossier")
	public Dossier getDossier() {
		return dossier;
	}

	/**
	 * Sets the dossier.
	 * 
	 * @param dossier
	 *            the dossier
	 */
	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	/**
	 * Gets the activity.
	 * 
	 * @return the activity
	 */
	@ManyToOne
	@JoinColumn(name = "activity")
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Sets the activity.
	 * 
	 * @param activity
	 *            the activity
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Gets the sender.
	 * 
	 * @return the sender
	 */
	@ManyToOne
	@JoinColumn(name = "sender")
	@Fetch(FetchMode.JOIN)
	public User getSender() {
		return sender;
	}

	/**
	 * Sets the sender.
	 * 
	 * @param sender
	 *            the sender
	 */
	public void setSender(User sender) {
		this.sender = sender;
	}

	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Column(length = 65535)
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the comments
	 */
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

}