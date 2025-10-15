package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
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
	
	private boolean showReassignWindow;
	private Scope oldScope; 
	private Scope newScope;
	private boolean removeOldScope;
	
	public boolean isShowReassignWindow() {
		return showReassignWindow;
	}
	public void setShowReassignWindow(boolean showReassignWindow) {
		this.showReassignWindow = showReassignWindow;
	}
	public Scope getOldScope() {
		return oldScope;
	}
	public void setOldScope(Scope oldScope) {
		this.oldScope = oldScope;
	}
	public Scope getNewScope() {
		return newScope;
	}
	public void setNewScope(Scope newScope) {
		this.newScope = newScope;
	}
	public boolean isRemoveOldScope() {
		return removeOldScope;
	}
	public void setRemoveOldScope(boolean removeOldScope) {
		this.removeOldScope = removeOldScope;
	}
	
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

	public void onReassignShow(ActionEvent event) {
		setOldScope( null );
		setNewScope( null );
		setRemoveOldScope( true );
	}
	
	private Occam getOccam() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return new Occam()
			.setDomainName(ds.getCurrentDomainName())
			.setDomain(ds.getDomainId())
			.setUser(ds.getCurrentUser());
		
	}
	
	public void onReassign(ActionEvent event) {
		try {
			if (getOldScope() == null || getNewScope() == null) {
				String message = "Debe seleccionar los \u00E1mbitos origen y destino.";
				AonUtil.addErrorMessage(message);
			} else if (getOldScope().getId().equals(getNewScope().getId())) {
				String message = "El \u00E1mbito origen y destino no pueden ser iguales.";
				AonUtil.addErrorMessage(message);
			} else {
				Occam occam = getOccam();
				if (isRemoveOldScope()) {
					AON.reassignAndDeleteScope( occam, occam.getDomain(), getOldScope().getId(), getNewScope().getId() );
					onSearch(event);
				} else {
					AON.reassignScope( occam, occam.getDomain(), getOldScope().getId(), getNewScope().getId() );
				}
			}
		} catch (Exception e) {
			String msg = "No se han podido reasignar los registros del \u00E1mbito. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		Scope scope = (Scope) getTo();
		Integer scopeId = scope.getId();
		Occam occam = getOccam();
		if (AON.canScopeBeDeleted( occam, occam.getDomain(), scopeId)) {
			super.onRemove(event);
		} else {
			String message = "El \u00E1mbito no puede ser borrado, tiene dependencias en otras entidades.";
			AonUtil.addErrorMessage(message);
		};
	}
	
}
