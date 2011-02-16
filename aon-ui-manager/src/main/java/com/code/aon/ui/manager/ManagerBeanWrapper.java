package com.code.aon.ui.manager;

import org.hibernate.SessionFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.dao.hibernate.BasicSessionManager;
import com.code.aon.common.dao.hibernate.HibernateDAO;

public class ManagerBeanWrapper {
	
	private BasicSessionManager sessionManager;
	
	private BasicManagerBean managerBean;

	public ManagerBeanWrapper( Class<?> pojoClass, SessionFactory sessionFactory ) {
		this.sessionManager = new BasicSessionManager(sessionFactory);
		HibernateDAO dao = new HibernateDAO( pojoClass, sessionManager );		
		this.managerBean = new BasicManagerBean(dao);			
	}
	
	public void setSessionFactory( SessionFactory sessionFactory ) {
		this.sessionManager.setSessionFactory(sessionFactory);
	}

	public IManagerBean getManagerBean() {
		return managerBean;
	}
	
}
