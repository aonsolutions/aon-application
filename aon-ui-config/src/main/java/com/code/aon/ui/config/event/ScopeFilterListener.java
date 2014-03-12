package com.code.aon.ui.config.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ScopeFilterListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String aliasName;

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			if (!DomainManager.isParentDomainUserInChildDomain()) {
				Expression exp = UserUtils.getInstance().getNullableScopeExpression( event.getController().resolveAlias(this.aliasName) );
				event.getController().getCriteria().addExpression(exp);
			} 
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding scopeFilter",e);
		}
	}

}