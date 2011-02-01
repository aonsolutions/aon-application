package com.code.aon.ui.manager;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.BasicSessionManager;
import com.code.aon.common.dao.hibernate.HibernateDAO;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class ManagerBeanWrapper implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerBeanWrapper.class);
	
	private BasicSessionManager sessionManager;
	
	private BasicManagerBean managerBean;

	@SuppressWarnings("unchecked")
	public ManagerBeanWrapper( String pojo ) {
		try {
			Class<? extends ITransferObject> pojoClass = (Class<? extends ITransferObject>) Class.forName( pojo );
			init( pojoClass );
		} catch (ClassNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}
	
	public ManagerBeanWrapper( Class<? extends ITransferObject> pojoClass ) {
		init( pojoClass );
	}

	public void init( Class<? extends ITransferObject> pojoClass ) {
		SessionFactory sessionFactory = getManagerController().getSessionFactory();
		this.sessionManager = new BasicSessionManager(sessionFactory);
		HibernateDAO dao = new HibernateDAO( pojoClass, sessionManager );		
		this.managerBean = new BasicManagerBean(dao);			
	}
	
	private ManagerController getManagerController() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}

	public IManagerBean getManagerBean() {
		if ( sessionManager.getSessionFactory().isClosed() ) {
			SessionFactory sessionFactory = getManagerController().getSessionFactory();
			this.sessionManager.setSessionFactory(sessionFactory);
		}
		return managerBean;
	}
	
}
