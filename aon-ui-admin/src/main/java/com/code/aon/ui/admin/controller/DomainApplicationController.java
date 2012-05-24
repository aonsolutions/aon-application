package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.AON_AIO_APPLICATION;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.admin.Profile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.Application;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.UserApplicationInfo;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationController extends BasicController {
	
	private String selectedTab;
	
	private List<SelectItem> availableUsers;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public DomainApplication getDomainApplication() {
		return (DomainApplication) getTo();
	}
	
	public boolean isShowModules() {
		return AON_AIO_APPLICATION.equals(getDomainApplication().getApplication().getName());
	}

	public List<SelectItem> getProfiles() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( ITransferObject to : UserApplicationInfo.getProfiles(getDomainApplication()) ) {
			Profile profile = (Profile) to;
			SelectItem item = new SelectItem(profile, profile.getName() );
			list.add(item);
		}		
		return list;
	}

	private Set<Integer> getRegisteredUsers( Integer domainApplication ) {
		Set<Integer> users = new HashSet<Integer>();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		Session session = HibernateUtil.getSession(sessionFactoryName);
		Query query = session.createQuery("SELECT au.user.id FROM ApplicationUser au WHERE au.domainApplication = ?");
		query.setInteger(0, domainApplication);
		for (Object id : query.list()) {
			users.add( (Integer) id );
		}
		return users;
	}	
		
	public List<SelectItem> getAvailableUsers() throws ManagerBeanException {
		return this.availableUsers;
	}
	
	@SuppressWarnings("unchecked")
	private List<User> getActiveUsers( Integer domain ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		Session session = HibernateUtil.getSession(sessionFactoryName);
		Query query = session.createQuery("FROM User u WHERE u.active = true and u.domain = ?");
		query.setInteger(0, domain);
		return query.list();
	}
	
	public void updateAvailableUsers( Integer domain ) {
		availableUsers = new LinkedList<SelectItem>();
		List<User> users = getActiveUsers(domain);
		if (! users.isEmpty() ) {
			Set<Integer> registeredUsers = getRegisteredUsers(getDomainApplication().getId());
			for (User user : users) {
				if (! registeredUsers.contains(user.getId()) ) {				
					SelectItem item = new SelectItem(user, user.getLogin() );
					availableUsers.add(item);									
				}
			}			
		}
	}		
	
	private Set<Application> getRegisteredApplications() throws ManagerBeanException {
		Set<Application> applications = new HashSet<Application>();
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		for (ITransferObject to : bean.getList(null)) {
			DomainApplication da = (DomainApplication) to;
			applications.add(da.getApplication());
		}
		return applications;
	}		
	
	public List<SelectItem> getAvailableApplications() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Application.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_CONTRATABLE), Boolean.TRUE);
		criteria.addOrder(bean.getFieldName(IEntityAlias.APPLICATION_NAME));
		List<ITransferObject> applications = bean.getList(criteria);
		if (! applications.isEmpty() ) {
			Set<Application> registeredApplications = getRegisteredApplications();
			for (ITransferObject to : applications) {
				Application application = (Application) to;
				if (! registeredApplications.contains(application) ) {
					SelectItem item = new SelectItem(application, application.getName() );
					list.add(item);									
				}
			}			
		}
		return list;
	}
	
}
