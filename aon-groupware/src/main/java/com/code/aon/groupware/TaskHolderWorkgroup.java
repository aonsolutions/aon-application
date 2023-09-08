package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.esferalia.aon.entity.master.TaskHolderWorkgroupDB;

@Entity
@Table(name="task_holder_workgroup")
public class TaskHolderWorkgroup extends TaskHolderWorkgroupDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isWorkGroupActive() {
		return (getWorkGroup().getStatus() == WorkGroupStatus.ACTIVE);
	}
}