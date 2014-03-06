package com.code.aon.groupware;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.groupware.enumeration.TaskHolderType;
import com.code.aon.registry.IRegistry;
import com.esferalia.aon.entity.master.TaskHolderDB;

@Entity
@Table(name="task_holder")
public class TaskHolder extends TaskHolderDB implements IRegistry{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public TaskHolder() {
		setType(TaskHolderType.INTERNAL);
		setActive(true);
	}

}