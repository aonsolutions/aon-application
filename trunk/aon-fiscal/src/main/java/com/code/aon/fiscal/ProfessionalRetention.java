package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProfessionalRetentionDB;

@Entity
@Table(name="fs_prof_retention")
public class ProfessionalRetention extends ProfessionalRetentionDB {

	private static final long serialVersionUID = 1L;
	
}
