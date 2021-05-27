package com.code.aon.ui.config.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ScopeFilterListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String aliasName;
	
	private boolean forceHeredity;

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	public boolean isForceHeredity() {
		return forceHeredity;
	}

	public void setForceHeredity(boolean forceHeredity) {
		this.forceHeredity = forceHeredity;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController controller = event.getController();
			UserUtils.getInstance().addNullableScopeExpression( controller.getCriteria(), controller.resolveAlias(this.aliasName), forceHeredity );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding scopeFilter",e);
		}
	}

}