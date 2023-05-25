package com.code.aon.groupware;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.JobTypeDB;

@Entity
@Table(name="job_type")
public class JobType extends JobTypeDB  {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}