package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.master.ActionTargetDB;

@Entity
@Table(name="mk_action_target")
public class ActionTarget extends ActionTargetDB implements IRegistry {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    public ActionTarget() {
    	setStatus( ActionTargetStatus.PENDING );
    }

    @Override
	@Transient
	public Registry getRegistry() {
		return getTarget().getRegistry();
	}

	@Override
	public void setRegistry(Registry registry) {
		getTarget().setRegistry(registry);
	}
	
}