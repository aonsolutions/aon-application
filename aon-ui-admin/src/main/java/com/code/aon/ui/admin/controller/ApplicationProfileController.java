package com.code.aon.ui.admin.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationRole;
import com.code.aon.admin.Profile;
import com.code.aon.admin.ProfileRole;
import com.code.aon.audit.ProfileModuleDenied;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ApplicationProfileController extends LinesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DBManagerController.class);
	
	public String getRoleList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Profile profile = (Profile) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(ProfileRole.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.PROFILE_ROLE_PROFILE_ID);
			criteria.addEqualExpression(alias, profile.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				Set<String> roles = new HashSet<String>();
				for( ITransferObject to : list ) {
					ProfileRole pr = (ProfileRole) to;
					roles.add( pr.getApplicationRole().getRole().getName() );
				}
				return StringUtils.join(roles, ", ");
			}
		}
		return null;
	}

	public String getModuleDeniedList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Profile profile = (Profile) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(ProfileModuleDenied.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.PROFILE_MODULE_DENIED_PROFILE_ID);
			criteria.addEqualExpression(alias, profile.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				Set<String> modules = new HashSet<String>();
				Locale locale = AonUtil.getCurrentLocale();
				for( ITransferObject to : list ) {
					ProfileModuleDenied pmd = (ProfileModuleDenied) to;
					modules.add( pmd.getModule().getName(locale) );
				}
				return StringUtils.join(modules, ", ");
			}
		}
		return null;
	}
	
	private Application getApplication() {
		ITransferObject to = getMasterController().getTo();
		if ( to instanceof Application ) {
			return (Application) to;
		}
		return ((DomainApplication)to).getApplication();
	}
	
    /**
     * Available roles list defined in application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableRoles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
        	Application app = getApplication();
			IManagerBean bean = BeanManager.getManagerBean(ApplicationRole.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.APPLICATION_ROLE_APPLICATION_ID);
			criteria.addEqualExpression(alias, app.getId());
			List<ITransferObject> roles = bean.getList(criteria);
			for( ITransferObject to : roles ) {
				ApplicationRole ar = (ApplicationRole) to;
			    SelectItem item = new SelectItem( ar, ar.getRole().getName() );
			    list.add(item);
			}
			AonUtil.sortSelectItems(list);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return list;
    }		
	
}
