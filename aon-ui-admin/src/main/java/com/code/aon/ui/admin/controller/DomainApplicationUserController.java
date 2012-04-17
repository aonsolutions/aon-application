package com.code.aon.ui.admin.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.code.aon.admin.ApplicationUser;
import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationUserController extends BasicController {
	
	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			ApplicationUser user = (ApplicationUser) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID);
			criteria.addEqualExpression(alias, user.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				Set<String> profiles = new HashSet<String>();
				for( ITransferObject to : list ) {
					ApplicationUserProfile aup = (ApplicationUserProfile) to;
					profiles.add( aup.getProfile().getName() );
				}
				return StringUtils.join(profiles, ", ");
			}
		}
		return null;
	}
	
}
