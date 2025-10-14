package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;

public class ScopeController extends BasicController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ScopeController.class);
	
	@Override
	public void initializeModel() {
		try {
			UserUtils.getInstance().addForceHeredityDomainCondition(getCriteria(), getFieldName(IEntityAlias.SCOPE_DOMAIN) );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
		super.initializeModel();
	}
	
	public boolean isAyudaT() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ds.getDomainNameURL().contains("ayudat");
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		Scope scope = (Scope) getTo();
		Integer scopeId = scope.getId();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Occam occam = new Occam()
			.setDomainName(ds.getCurrentDomainName())
			.setDomain(ds.getDomainId())
			.setUser(ds.getCurrentUser());
		if (AON.canScopeBeDeleted( occam, ds.getDomainId(), scopeId)) {
			super.onRemove(event);
		} else {
			String message = "El \u00E1mbito no puede ser borrado, tiene dependencias en otras entidades.";
			AonUtil.addErrorMessage(message);
		};
	}
	
}
