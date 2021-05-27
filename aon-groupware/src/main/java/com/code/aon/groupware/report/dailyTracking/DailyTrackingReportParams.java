package com.code.aon.groupware.report.dailyTracking;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.JobType;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.enumeration.DailyTrackingReportType;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectType;
import com.code.aon.registry.Registry;

public class DailyTrackingReportParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private DailyTrackingReportType reportType;
	
	private Registry registry;
	private WorkGroup workGroup;
	private TaskHolder taskHolder;
	private Project project;
	private Date fromDate;
	private Date toDate;
	private JobType jobType;
	private ProjectType projectType;
	private ActivityType activityType;
	
	public DailyTrackingReportType getReportType() {
		return reportType;
	}
	public void setReportType(DailyTrackingReportType reportType) {
		this.reportType = reportType;
	}
	
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	public WorkGroup getWorkGroup() {
		return workGroup;
	}
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public JobType getJobType() {
		return jobType;
	}
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}
	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
	}
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
}
