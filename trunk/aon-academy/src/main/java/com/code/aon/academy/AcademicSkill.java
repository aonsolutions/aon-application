package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AcademicSkillDB;

@Entity
@Table(name="academic_skill")
public class AcademicSkill extends AcademicSkillDB {
	
	private static final long serialVersionUID = 1L;

}

