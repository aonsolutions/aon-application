package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.SelectTransferObject;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class UserOverloadController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserOverloadController.class);
	
	private List<SelectItem> availableUsers;
	
	private List<SelectTransferObject<Profile,ApplicationUserProfile>> userProfiles;
	
	private DomainApplication domainApplication;
	
	private Domain getParentDomain() {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		return dc.getParentDomain();		
	}

	public List<SelectTransferObject<Profile, ApplicationUserProfile>> getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(List<SelectTransferObject<Profile, ApplicationUserProfile>> userProfiles) {
		this.userProfiles = userProfiles;
	}	
	
	private DomainApplication loadDomainApplication() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		Integer id = AdminUtil.getDomainApplication(DomainManager.getCurrentDomain(), principal.getApplicationId());
		if ( id != null ) {
			try {
				return (DomainApplication) BeanManager.getManagerBean(DomainApplication.class).get(id);	
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	private void updateUserProfiles() throws ManagerBeanException {
		this.userProfiles = DomainApplicationUserController.loadUserProfiles( getDomainApplication(), (ApplicationUser) getTo() );
	}	

	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			return DomainApplicationUserController.getProfileList( (ApplicationUser) getSelectedTO() );
		}
		return null;
	}

	public List<SelectItem> getAvailableUsers() throws ManagerBeanException {
		return this.availableUsers;
	}
	
	private void updateAvailableUsers( Integer domain ) throws ManagerBeanException {
		availableUsers = DomainApplicationController.loadAvailableUsers(domain, getDomainApplication());
	}

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		try {
			updateUserProfiles();
			updateAvailableUsers(getParentDomain().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onReset ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		try {
			updateUserProfiles();
			updateAvailableUsers(getParentDomain().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelect ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onRemove(ActionEvent event) {
		try {		
			ApplicationUser appUser = (ApplicationUser) getTo();
			DomainApplicationUserController.removeUserProfiles(appUser);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		super.remove(event);
	}

	@Override
	public void accept(ActionEvent event) {
		ApplicationUser appUser = (ApplicationUser) getTo();
		if ( isNew() ) {
			appUser.setDomainApplication(getDomainApplication());
		}
		super.accept(event);
		try {
			DomainApplicationUserController.insertUserProfiles( appUser, this.userProfiles );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public DomainApplication getDomainApplication() {
		if ( domainApplication == null ) {
			this.domainApplication = loadDomainApplication();
		}
		return domainApplication;
	}		
	
	public void onInit( ActionEvent event ) {
		List<Expression> list = new LinkedList<Expression>();
		Expression expr = ExpressionUtilities.getEqualExpression("ApplicationUser.user.domain", getParentDomain().getId());
		list.add(expr);
		setInitExpressions(list);
		onEditSearch(event);
		onSearch(event);
	}
	
}