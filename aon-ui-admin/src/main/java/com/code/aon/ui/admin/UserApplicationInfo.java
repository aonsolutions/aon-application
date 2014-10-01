package com.code.aon.ui.admin;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserApplicationInfo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean checked;
	
	private DomainApplication domainApplication;
	
	private User user;
	
	private ApplicationUser applicationUser;
	
	private List<UserProfileInfo> profileInfos;

	public UserApplicationInfo(DomainApplication domainApplication, User user) {
		this.domainApplication = domainApplication;
		this.user = user;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public DomainApplication getDomainApplication() {
		return domainApplication;
	}

	public ApplicationUser getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(ApplicationUser applicationUser) {
		this.applicationUser = applicationUser;
	}

	public List<UserProfileInfo> getProfileInfos() {
		return profileInfos;
	}

	public void setProfileInfos(List<UserProfileInfo> profileInfos) {
		this.profileInfos = profileInfos;
	}

	public boolean register() throws ManagerBeanException {
		boolean changed = false;
		ApplicationUser au = getApplicationUser();
		if ( au == null ) {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
			au = new ApplicationUser();
			au.setActive(true);
			au.setDomainApplication(getDomainApplication());
			au.setUser(user);
			bean.insert(au);
			setApplicationUser(au);
			changed = true;
		}
		IManagerBean aupBean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		for( UserProfileInfo upi : getProfileInfos() ) {
			ApplicationUserProfile aup = upi.getUserProfile();
			if ( upi.isChecked() ) {
				if ( aup == null ) {
					aup = new ApplicationUserProfile();
					aup.setApplicationUser(au);
					aup.setProfile(upi.getProfile());
					aupBean.insert(aup);
					upi.setUserProfile(aup);
					changed = true;
				}
			} else {
				if ( aup != null ) {
					aupBean.remove(aup);
					upi.setUserProfile(null);
					changed = true;
				}
			}
		}
		return changed;
	}

	public boolean unregister() throws ManagerBeanException {
		boolean changed = false;
		if ( getApplicationUser() != null ) {
			IManagerBean aupBean = BeanManager.getManagerBean(ApplicationUserProfile.class);
			for( UserProfileInfo upi : getProfileInfos() ) {
				if ( upi.getUserProfile() != null ) {
					aupBean.remove( upi.getUserProfile() );
				}
			}
			IManagerBean auBean = BeanManager.getManagerBean(ApplicationUser.class);
			auBean.remove(getApplicationUser());
			changed = true;
		}
		return changed;
	}
	
	private static ApplicationUser getApplicationUser( DomainApplication da, User user ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_USER_ID), user.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_DOMAIN_APPLICATION_ID), da.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (ApplicationUser) list.get(0);
		}
		return null;		
	}

	private static ApplicationUserProfile getApplicationUserProfile( Profile profile, ApplicationUser user ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_PROFILE_ID), profile.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID), user.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (ApplicationUserProfile) list.get(0);
		}
		return null;		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Profile> getProfiles( DomainApplication da ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Profile.class);
		Criteria criteria = new Criteria();
		Expression expr1 = ExpressionUtilities.getEqualExpression("Profile.domain<id", da.getDomain());
		Expression expr2 = ExpressionUtilities.getNullExpression("Profile.domain");
		Expression expr3 = ExpressionUtilities.getOrExpression(expr1, expr2);
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		Integer parent = ds.getParentDomainId();
		if ( parent != null ) {
			Expression expr4 = ExpressionUtilities.getEqualExpression("Profile.domain<id", parent);
			criteria.addOrExpression(ExpressionUtilities.getOrExpression(expr3, expr4));
		} else {
			criteria.addExpression(expr3);
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID), da.getApplication().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROFILE_NAME));
		return (List) bean.getList(criteria);
	}		
	
	public static List<UserApplicationInfo> getApplicationInfos( User user ) throws ManagerBeanException {
		List<UserApplicationInfo> list = new LinkedList<UserApplicationInfo>();
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_ACTIVE), Boolean.TRUE);
		criteria.addOrder("DomainApplication.application.name");
		for( ITransferObject to : bean.getList(criteria) ) {
			DomainApplication da = (DomainApplication) to;
			UserApplicationInfo uai = new UserApplicationInfo(da, user);
			ApplicationUser appUser = getApplicationUser(da, user); 
			uai.setApplicationUser( appUser );
			uai.setChecked( appUser != null );
			List<UserProfileInfo> infos = new LinkedList<UserProfileInfo>();
			for( Profile profile : getProfiles(da) ) {
				UserProfileInfo upi = new UserProfileInfo(profile);
				if ( appUser != null ) {
					ApplicationUserProfile aup = getApplicationUserProfile(profile, appUser); 
					upi.setUserProfile( aup );
					upi.setChecked( aup != null );
				}
				infos.add(upi);
			}
			uai.setProfileInfos(infos);
			list.add(uai);
		}
		return list;
	}
 
	
}
