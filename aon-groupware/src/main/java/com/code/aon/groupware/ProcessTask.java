package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ProcessTaskDB;

@Entity
@Table(name="process_task")
public class ProcessTask extends ProcessTaskDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}