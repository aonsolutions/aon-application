package com.code.aon.ui.document.event;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Enterprise;
import com.code.aon.document.dao.EnterpriseDocumentDAO;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseAlfrescoListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Enterprise enterprise = (Enterprise) event.getController().getTo();
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		EnterpriseDocumentDAO dao = edc.getAlfrescoDAO();
		ParentReference parent = dao.getParent(enterprise);
		String name = EnterpriseDocumentDAO.getName(enterprise);
		try {
			dao.createSpace(parent, name, enterprise.getRegistry().getFullName());
			mc.getUserManager().createGroup(name);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);			
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Enterprise enterprise = (Enterprise) event.getController().getTo();
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		EnterpriseDocumentDAO dao = edc.getAlfrescoDAO();
		String path = dao.getEnterprisePath(enterprise);
		Reference reference = dao.getReference(path);
		String name = EnterpriseDocumentDAO.getName(enterprise);
		try {		
			dao.removeSpace(reference);
			mc.getUserManager().deleteGroup(name);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);			
		}
	}
	
	
	
}