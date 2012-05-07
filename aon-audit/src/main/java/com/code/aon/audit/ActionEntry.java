package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ActionEntryDB;

@Entity
@Table(name="action_entry")
public class ActionEntry extends ActionEntryDB {

	private static final long serialVersionUID = 1L;
   
}