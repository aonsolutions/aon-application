package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.QualificationDB;

@Entity
@Table(name="qualification")
public class Qualification extends QualificationDB {

	private static final long serialVersionUID = 1L;	

}
