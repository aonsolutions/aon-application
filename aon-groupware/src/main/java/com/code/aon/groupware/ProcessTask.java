package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProcessTaskDB;

@Entity
@Table(name="process_task")
public class ProcessTask extends ProcessTaskDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}