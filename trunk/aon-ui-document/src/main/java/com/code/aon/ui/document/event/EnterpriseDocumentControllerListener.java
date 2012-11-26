package com.code.aon.ui.document.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_TREE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import org.richfaces.model.TreeNode;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.document.tree.EnterpriseTree;
import com.code.aon.ui.document.tree.EnterpriseTreeData;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseDocumentControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		EnterpriseDocument ed = edc.getEnterpriseDocument();
		if ( edc.getLastDocument() != null ) {
			ed.setTitle(edc.getLastDocument().getTitle());
			ed.setDescription(edc.getLastDocument().getDescription());			
			ed.setCategories(edc.getLastDocument().getCategories());
		}
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		if ( ec.isTreeView() ) {
			ed.setEnterprise(ec.getEnterprise());
		} else {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);				
			if (! mc.isMainEnterprise() ) {
				ed.setEnterprise(mc.getEnterprise());
			}				
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		try {
			edc.reset();
			edc.setCategoryArray(edc.getEnterpriseDocument().getCategories());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		try {
			edc.reset();
			edc.setCategoryArray(edc.getEnterpriseDocument().getCategories());
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
		ed.setCategories(edc.getCategoryArray());
		edc.setUpdateContent(false);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		EnterpriseDocument ed = (EnterpriseDocument) edc.getTo();
		if ( edc.isUpdateContent() ) {
			ed.setData( edc.getAonFile().getData() );
			ed.setMimeType( edc.getAonFile().getMimeType() );
			try {
				edc.getAlfrescoDAO().updateContent(ed);
			} catch (DAOException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
		ed.setCategories(edc.getCategoryArray());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		if ( isTreeView() ) {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.removeCurrentNodeFromTree();		
			tree.setCurrentNode(tree.getEnterpriseNode());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		if ( isTreeView() ) {
			EnterpriseDocument ed = (EnterpriseDocument) event.getController().getTo();			
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			TreeNode<EnterpriseTreeData> node = tree.addToTree(ed);
			tree.selectTreeNode(node);
		}		
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		if ( isTreeView() ) {					
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.removeCurrentNodeFromTree();
			EnterpriseDocument ed = (EnterpriseDocument) event.getController().getTo();			
			TreeNode<EnterpriseTreeData> node = tree.addToTree(ed);
			tree.selectTreeNode(node);
		}
	}

	@Override
	public void afterModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		try {
			EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
			edc.clearCriteriaEx();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private boolean isTreeView() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		return controller.isTreeView();
	}
	
}