package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AcademicSkillDB;

@Entity
@Table(name="academic_skill")
public class AcademicSkill extends AcademicSkillDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}

