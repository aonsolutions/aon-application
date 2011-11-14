package com.code.aon.ui.document.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_SEARCH;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_TREE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.document.tree.EnterpriseTree;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseDocumentControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		try {
			edc.reset();
			EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
			if ( ec.isTreeView() ) {
				edc.getEnterpriseDocument().setEnterprise(ec.getEnterprise());
			} else {
				ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);				
				if (! mc.isMainEnterprise() ) {
					edc.getEnterpriseDocument().setEnterprise(mc.getLoggedUser().getEnterprise());
				}				
			}
			EnterpriseDocumentSearchListener edsl = (EnterpriseDocumentSearchListener) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_SEARCH);
			edsl.setCategories(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		try {
			edc.reset();
			EnterpriseDocumentSearchListener edsl = (EnterpriseDocumentSearchListener) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_SEARCH);
			edsl.setCategoryArray(edc.getEnterpriseDocument().getCategories());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		AttachmentUtil.checkFileData(edc, edc.isNew(), true);
		EnterpriseDocument ed = (EnterpriseDocument) edc.getTo();
		ed.setData( edc.getAonFile().getData() );
		ed.setMimeType( edc.getAonFile().getMimeType() );
		updateCategories(edc);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		updateCategories(edc);
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetCurrentNode();
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		resetCurrentNode();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		resetCurrentNode();
	}

	private void resetCurrentNode() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		if ( controller.isTreeView() ) {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.loadTree();
		}
	}

	private void updateCategories( EnterpriseDocumentController edc ) {
		EnterpriseDocumentSearchListener edsl = (EnterpriseDocumentSearchListener) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_SEARCH);
		edc.getEnterpriseDocument().setCategories(edsl.getCategoryArray());		
	}

	
	
}