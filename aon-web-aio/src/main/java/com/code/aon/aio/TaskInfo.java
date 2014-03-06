package com.code.aon.aio;

import java.io.Serializable;

import com.code.aon.common.AonVersion;
import com.code.aon.groupware.enumeration.TaskStatus;


public class TaskInfo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int inProgressUserTasks;
	private int pendingUserTasks;
	private int expiredInProgressUserTasks;
	private int expiredPendingUserTasks;

	private int pendingGroupTasks;
	private int expiredPendingGroupTasks;
	
	public int getInProgressUserTasks() {
		return inProgressUserTasks;
	}
	public void setInProgressUserTasks(int inProgressUserTasks) {
		this.inProgressUserTasks = inProgressUserTasks;
	}
	public int getPendingUserTasks() {
		return pendingUserTasks;
	}
	public void setPendingUserTasks(int pendingUserTasks) {
		this.pendingUserTasks = pendingUserTasks;
	}
	public int getExpiredInProgressUserTasks() {
		return expiredInProgressUserTasks;
	}
	public void setExpiredInProgressUserTasks(int expiredInProgressUserTasks) {
		this.expiredInProgressUserTasks = expiredInProgressUserTasks;
	}
	public int getExpiredPendingUserTasks() {
		return expiredPendingUserTasks;
	}
	public void setExpiredPendingUserTasks(int expiredPendingUserTasks) {
		this.expiredPendingUserTasks = expiredPendingUserTasks;
	}
	public int getPendingGroupTasks() {
		return pendingGroupTasks;
	}
	public void setPendingGroupTasks(int pendingGroupTasks) {
		this.pendingGroupTasks = pendingGroupTasks;
	}
	public int getExpiredPendingGroupTasks() {
		return expiredPendingGroupTasks;
	}
	public void setExpiredPendingGroupTasks(int expiredPendingGroupTasks) {
		this.expiredPendingGroupTasks = expiredPendingGroupTasks;
	}
	
	public void add(boolean userTask, TaskStatus status, int count, boolean expired) {
		if (userTask && expired && status == TaskStatus.IN_PROGRESS) {
			setExpiredInProgressUserTasks(count);
		} else if (userTask && !expired && status == TaskStatus.IN_PROGRESS) {
			setInProgressUserTasks(count);
		} else if (userTask && expired && status == TaskStatus.PENDING) {
			setExpiredPendingUserTasks(count);
		} else if (userTask && !expired && status == TaskStatus.PENDING) {
			setPendingUserTasks(count);
		} else if (!userTask && expired && status == TaskStatus.PENDING) {
			setExpiredPendingGroupTasks(count);
		} else if (!userTask && !expired && status == TaskStatus.PENDING) {
			setPendingGroupTasks(count);
		}
	}
}
