package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.APPLICATION_CONTROLLER_NAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationRole;
import com.code.aon.admin.Profile;
import com.code.aon.admin.ProfileRole;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
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
	
    /**
     * Available roles list defined in application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableRoles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
        	Application app = (Application) FormUtil.getController(APPLICATION_CONTROLLER_NAME).getTo();
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
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return list;
    }		
	
}
