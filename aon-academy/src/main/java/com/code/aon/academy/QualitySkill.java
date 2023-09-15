package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.QualitySkillDB;

@Entity
@Table(name="quality_skill")
public class QualitySkill extends QualitySkillDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;	

}