package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ActionDeniedDB;

@Entity
@Table(name="action_denied")
public class ActionDenied extends ActionDeniedDB implements IAction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
