package com.code.aon.ui.document.event;

import static com.code.aon.document.IAlfrescoConstants.CONSUMER;
import static com.code.aon.document.IAlfrescoConstants.ENTERPRISE_GROUP;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoUserManager;
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
		ParentReference root = dao.getRootReference();
		String name = EnterpriseDocumentDAO.getName(enterprise);
		try {
			Reference space = dao.createSpace(root, name, enterprise.getRegistry().getFullName());
			AlfrescoUserManager um = mc.getUserManager();
			um.createGroup(name, ENTERPRISE_GROUP);
			um.addGroupAccess(root, name, CONSUMER);
			um.addGroupAccess(space, name, Constants.COORDINATOR);
			String parentEnterprise = EnterpriseDocumentDAO.getName(mc.getParentEnterprise());
			um.addGroupAccess(space, parentEnterprise, Constants.COORDINATOR);
			um.setInheritPermission(space, false);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);			
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Enterprise enterprise = (Enterprise) event.getController().getTo();
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		EnterpriseDocumentDAO dao = edc.getAlfrescoDAO();
		ParentReference reference = dao.getEnterpriseReference(enterprise);
		String name = EnterpriseDocumentDAO.getName(enterprise);
		try {		
			dao.removeSpace(reference);
			mc.getUserManager().deleteGroup(name, ENTERPRISE_GROUP);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);			
		}
	}
	
}