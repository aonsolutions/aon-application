package com.code.aon.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProfileActionDeniedDB;

@Entity
@Table(name="profile_action_denied")
public class ProfileActionDenied extends ProfileActionDeniedDB implements IAction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
