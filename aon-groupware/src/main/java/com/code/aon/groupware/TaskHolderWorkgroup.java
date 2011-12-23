package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.config.enumeration.WorkGroupStatus;
import com.esferalia.aon.entity.master.TaskHolderWorkgroupDB;

@Entity
@Table(name="task_holder_workgroup")
public class TaskHolderWorkgroup extends TaskHolderWorkgroupDB {

	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isWorkGroupActive() {
		return (getWorkGroup().getStatus() == WorkGroupStatus.ACTIVE);
	}
}