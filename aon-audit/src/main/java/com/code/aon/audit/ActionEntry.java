package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ActionEntryDB;

@Entity
@Table(name="action_entry")
public class ActionEntry extends ActionEntryDB implements IAction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
   
}