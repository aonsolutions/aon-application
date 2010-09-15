package com.code.aon.ui.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.BasicSessionManager;
import com.code.aon.common.dao.hibernate.HibernateDAO;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DBBasicController extends BasicController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DBBasicController.class);
	
	private BasicSessionManager sessionManager;
	
	private BasicManagerBean managerBean;
	
	@Override
	@SuppressWarnings("unchecked")	
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( managerBean == null ) {
			Class<? extends ITransferObject> pojoClass;
			try {
				pojoClass = (Class<? extends ITransferObject>) Class.forName( getPojo() );
				ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
				this.sessionManager = new BasicSessionManager(manager.getSessionFactory());
				HibernateDAO dao = new HibernateDAO( pojoClass, sessionManager );		
				this.managerBean = new BasicManagerBean(dao);			
			} catch (ClassNotFoundException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return managerBean;
	}

	public void updateDAO() {
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		this.sessionManager.setSessionFactory(manager.getSessionFactory());
	}
	
}
