package com.code.aon.groupware;


import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskPeriod;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.esferalia.aon.entity.master.TaskDB;

@Entity
@Table(name="task")
public class Task extends TaskDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ProcessTask processTask;
	
	public Task() {
		this.setStartDate(new Date());
		this.setStatus(TaskStatus.PENDING);
		this.setPercent(0);
		this.setSource(TaskSource.MANUAL);
		this.setPriority(Priority.NORMAL);
	}
	

	@OneToOne(mappedBy = "task", cascade={CascadeType.REFRESH})
	public ProcessTask getProcessTask() {
		return this.processTask;
	}
	public void setProcessTask(ProcessTask processTask) {
		this.processTask = processTask;
	}
	@Transient
	public String getProcessComments() {
		return (getProcessTask() != null && getProcessTask().getProcessDetail() != null)
				?getProcessTask().getProcessDetail().getComments()
				:null; 
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
        return getStatus() == TaskStatus.PENDING;
    }
	@Transient
    public boolean isDeleted() {
        return getStatus() == TaskStatus.DELETED;
    }
	@Transient
    public boolean isInProgress() {
        return getStatus() == TaskStatus.IN_PROGRESS;
    }
	@Transient
    public boolean isFinished() {
        return getStatus() == TaskStatus.FINISHED;
    }
	@Transient
    public boolean isHighPriority() {
        return getPriority() == Priority.HIGH;
    }
	@Transient
    public boolean isLowPriority() {
        return getPriority() == Priority.LOW;
    }
	@Transient
    public boolean isNormalPriority() {
        return getPriority() == Priority.NORMAL;
    }
	@Transient
    public boolean isNonePriority() {
        return getPriority() == Priority.NONE;
    }
	@Transient
    public boolean isSourceProcess() {
        return getSource() == TaskSource.PROCESS;
    }
	@Transient
    public boolean isSourceAssigned() {
        return getSource() == TaskSource.ASSIGNED;
    }
	@Transient
    public boolean isSourceManual() {
        return getSource() == TaskSource.MANUAL;
    }
	@Transient
    public boolean isRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.NONE);
    }
	@Transient
    public boolean isDailyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.DAILY);
    }
	@Transient
    public boolean isWeeklyRepeatable() {
        return (getRepeatPeriod() == TaskPeriod.WEEKLY);
    }
	@Transient
	public boolean isBiWeeklyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.BI_WEEKLY);
    }
	@Transient
	public boolean isMonthlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.MONTHLY);
    }
	@Transient
	public boolean isBiMonthlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.BI_MONTHLY);
    }
	@Transient
	public boolean isThreeMonthlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.THREE_MONTHLY);
    }
	@Transient
	public boolean isFourMonthlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.FOUR_MONTHLY);
    }
	@Transient
	public boolean isHalfYearlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.HALF_YEARLY);
    }
	@Transient
	public boolean isYearlyRepeatable() {
        return (getRepeatPeriod() != TaskPeriod.YEARLY);
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
		return StringUtils.isNotBlank(getComments());
	}
	@Transient
	public boolean isProcessCommentsNotEmpty() {
		return StringUtils.isNotBlank(getProcessComments());
	}
	
}