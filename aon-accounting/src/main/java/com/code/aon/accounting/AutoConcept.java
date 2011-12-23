package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AutoConceptDB;

@Entity
@Table(name="auto_concept")
public class AutoConcept extends AutoConceptDB {

	private static final long serialVersionUID = 1L;

}