package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProcessTaskDB;

@Entity
@Table(name="process_task")
public class ProcessTask extends ProcessTaskDB {

	private static final long serialVersionUID = 1L;

}