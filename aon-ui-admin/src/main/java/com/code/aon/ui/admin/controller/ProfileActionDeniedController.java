package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.APPLICATION_OPTION_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.AUDIT_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.Profile;
import com.code.aon.audit.Action;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.AuditController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * The Class FavoriteOptionController.
 */
public class ProfileActionDeniedController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ProfileActionDeniedController.class);
	
	private Profile profile;
	
	private List<ProfileActionDenied> deniedActions;
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> selected;
	
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public List<ApplicationOption> getOptions() {
		return options;
	}

	public void setOptions(List<ApplicationOption> options) {
		this.options = options;
	}

	public List<ApplicationOption> getSelected() {
		return selected;
	}

	public void setSelected(List<ApplicationOption> selected) {
		this.selected = selected;
	}
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ProfileActionDenied> getDeniedActions( Profile profile ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProfileActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_ACTION_DENIED_PROFILE_ID), profile.getId());
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading actions denied", e);
		}
		return null;		
	}
	
	private List<ApplicationOption> getOptions( List<ProfileActionDenied> deniedActions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( ITransferObject to : deniedActions ) {
				String action = ((ProfileActionDenied) to).getAction().getName();
				ApplicationOption option = options.get(action);
				if ( option != null ) {
					list.add(option);
				} else {
					try {
						IManagerBean bean = BeanManager.getManagerBean(ProfileActionDenied.class);
						bean.remove(to);
						LOGGER.warn( "ProfileActionDenied removed, action {}", action );
					} catch (ManagerBeanException e) {
						LOGGER.error( "Error removing profile action denied " + to, e);
					}
				}
			}
		}
		return list;		
	}	
	
	public void update() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProfileActionDenied.class);
			Map<String,ApplicationOption> map = new HashMap<String, ApplicationOption>();
			for( ApplicationOption option : this.selected ) {
				map.put( option.getAction(), option );
			}
			for( ProfileActionDenied pad : this.deniedActions ) {
				String action = pad.getAction().getName();
				if ( map.containsKey(action) ) {
					map.remove(action);
				} else {
					bean.remove( pad );
				}
			}
			AuditController auditController = (AuditController) AonUtil.getRegisteredBean(AUDIT_CONTROLLER_NAME);
			for( ApplicationOption option : map.values() ) {
				ProfileActionDenied pad = new ProfileActionDenied();
				Action action = auditController.getAction(option.getAction());
				pad.setAction(action);
				pad.setProfile(profile);
				bean.insert(pad);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating favorite action list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void init( Profile profile, Set<Module> enabledModules ) {
		setProfile(profile);
		this.deniedActions = getDeniedActions( getProfile() );
		List<ApplicationOption> deniedList = getOptions( this.deniedActions );
		ActionDeniedController actionDeniedController =  (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		List<ApplicationCategory> categories = actionDeniedController.getCategories(enabledModules);
		this.options = new ArrayList<ApplicationOption>( actionDeniedController.getOptions(categories, true) );
		this.selected = new LinkedList<ApplicationOption>();
		for( ApplicationOption option : this.options ) {
			if ( deniedList.contains(option) ) {
				this.selected.add(option);
			}
		}
		this.options.removeAll(deniedList);		
	}
	
}