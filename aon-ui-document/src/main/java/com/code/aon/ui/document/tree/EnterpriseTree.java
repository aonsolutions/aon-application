package com.code.aon.ui.document.tree;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_SEARCH;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.project.controller.IProjectConstants.PROJECT_CONTROLLER_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.alfresco.webservice.types.Reference;
import org.richfaces.component.UITree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.ListRowKey;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.document.BasicAlfresco;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.document.event.EnterpriseDocumentSearchListener;
import com.code.aon.ui.project.controller.ProjectController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private TreeNode<EnterpriseTreeData> currentNode;

	private TreeNode<EnterpriseTreeData> parentNode;
	
	private TreeState state;
	
	private EnterpriseDocument document;

	private String splitterPosition = "250";
	
	public EnterpriseTree() {
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		ec.setSkipResetButton(! mc.isAdministrator());
		ec.setSkipRemoveButton(! mc.isAdministrator());
	}

	public EnterpriseDocument getDocument() {
		return document;
	}

	public void setDocument(EnterpriseDocument document) {
		this.document = document;
	}

	public TreeState getState() {
		return state;
	}

	public void setState(TreeState state) {
		this.state = state;
	}
	
	public String getSplitterPosition() {
		return splitterPosition;
	}
	
	public void setSplitterPosition(String splitterPosition) {
		this.splitterPosition = splitterPosition;
	}
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}
	
	public EnterpriseTreeData getCurrentNode() {
		return currentNode.getData();
	}
	
	public EnterpriseTreeData getParentNode() {
		return parentNode.getData();
	}
	
	public void setCurrentNode(TreeNode<EnterpriseTreeData> currentNode) {
		this.currentNode = currentNode;
	}
	
	public TreeNode<EnterpriseTreeData> getEnterpriseNode() {
		return enterpriseNode;
	}

	private EnterpriseTreeData getTreeData( Enterprise e ) {
		return new EnterpriseTreeData( e.getId(), new AonTreeKey(e) );
	}

	private EnterpriseTreeData getTreeData( Project project ) {
		return new EnterpriseTreeData( project.getId(), new AonTreeKey(project) );
	}
	
	private EnterpriseTreeData getTreeData( EnterpriseDocument ed ) {
		EnterpriseTreeData etd = new EnterpriseTreeData( ed.getId(), new AonTreeKey(ed) );
		etd.setMimeType(ed.getMimeType());
		return etd;
	}

	private void loadDocuments( TreeNode<EnterpriseTreeData> enterpriseNode, Enterprise enterprise, Map<Integer,TreeNode<EnterpriseTreeData>> projects ) throws ManagerBeanException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		IManagerBean bean = edc.getManagerBean();
		edc.clearCriteria();
		Criteria criteria = edc.getCriteria();
		EnterpriseDocumentSearchListener search = (EnterpriseDocumentSearchListener) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_SEARCH);
		search.setEnterprise(enterprise);
		search.completeCriteria(criteria);
		List<ITransferObject> list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			addToTree( (EnterpriseDocument) to );
		}	
	}		
	
	private void loadProjects( TreeNode<EnterpriseTreeData> enterpriseNode, Enterprise enterprise ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Project.class);		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_NAME));
		Map<Integer,TreeNode<EnterpriseTreeData>> projects = new HashMap<Integer, TreeNode<EnterpriseTreeData>>();
		List<ITransferObject> list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			Project project = (Project) to;
			TreeNode<EnterpriseTreeData> projectNode = null;
			if ( project.isActive() ) {
				projectNode = new AonTreeNode<EnterpriseTreeData>();
				EnterpriseTreeData etd = getTreeData(project);
				projectNode.setData(etd);
				enterpriseNode.addChild( etd.getKey(), projectNode );				
			}
			projects.put(project.getId(), projectNode);
		}
		loadDocuments(enterpriseNode, enterprise, projects);
	}
	
	public void loadTree() {
		setState( new TreeState() );
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		rootNode = new AonTreeNode<EnterpriseTreeData>();
		enterpriseNode = new AonTreeNode<EnterpriseTreeData>();
		EnterpriseTreeData etd = getTreeData(enterprise);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getKey(), enterpriseNode );
		try {
			loadProjects(enterpriseNode, enterprise);
			if ( (getDocument() != null) && (getDocument().getId() != null) ) {
				onSelectTreeDocument(null, document.getId());
				TreeNode<EnterpriseTreeData> node = getTreeNode(getDocument());
				selectTreeNode(node);			
				setDocument(null);
			} else {
				setCurrentNode( enterpriseNode );	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading documents for " + enterprise, e );
		}
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() && (currentNode != null) ) {
			EnterpriseTreeData etd = (EnterpriseTreeData) tree.getRowData();
			selected = (etd == getCurrentNode());
		}
		return selected;
	}	
	
	public Boolean adviseNodeOpened(UITree tree) {
		ListRowKey<?> key = (ListRowKey<?>) tree.getRowKey();
        if ( key.depth() <= 1 ) {
            return Boolean.TRUE;
        }
        /*
        ListRowKey<?> selected = (ListRowKey<?>) getState().getSelectedNode();
        if ( key.depth() <= selected.depth() ) {
        	for( int i = 0; i < key.depth(); i++ ) {
        		Object o1 = selected.get(i);
        		Object o2 = key.get(i);
        		if (! o1.equals(o2) ) {
        			return null;
        		}
        	}
        	return Boolean.TRUE;
        }
        */
		return null;
	}		

	private void selectNode( UITree tree ) {
		EnterpriseTreeData node = (EnterpriseTreeData) tree.getRowData();
		if ( node != getCurrentNode() ) {
			parentNode = tree.getTreeNode().getParent();
			setCurrentNode( (TreeNode<EnterpriseTreeData>) tree.getTreeNode() );
			getCurrentNode().actionListener(null);
		}
	}
	
	public void processSelection(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent() ;
		selectNode( tree );
		TreeRowKey key = (TreeRowKey) tree.getRowKey();
		ListRowKey<AonTreeKey> parentKey = (ListRowKey<AonTreeKey>) key.getParentKey();
		TreeNode<EnterpriseTreeData> parent = tree.getTreeNode().getParent();
		Iterator<Map.Entry<Object, TreeNode<EnterpriseTreeData>>> i = parent.getChildren();
		while ( i.hasNext() ) {
			Map.Entry<Object, TreeNode<EnterpriseTreeData>> entry = i.next();
			AonTreeKey _key = (AonTreeKey) entry.getKey();
 			ListRowKey<AonTreeKey> nodeKey = new ListRowKey<AonTreeKey>(parentKey, _key );
			if (! key.equals(nodeKey) ) {
				if ( state.isExpanded(nodeKey) ) {
					tree.queueNodeCollapse( nodeKey );	
				}
			} else {
				if (! tree.isExpanded() ) {
					tree.queueNodeExpand( key );
				}				
			}
		}
	}

	public void onSelectTreeProject( ActionEvent event ) {
		try {
			ProjectController pc = (ProjectController) AonUtil.getRegisteredBean(PROJECT_CONTROLLER_NAME);
			pc.select(event, getCurrentNode().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeProject exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void onSelectTreeDocument( ActionEvent event ) {
		try {
			onSelectTreeDocument(event, (Reference) getCurrentNode().getId()); 
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeDocument exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	public void onSelectTreeDocument( ActionEvent event, Reference ref ) throws ManagerBeanException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		edc.select(event, BasicAlfresco.getId(ref));
	}	

	public void onShowDocument( ActionEvent event ) throws ManagerBeanException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		setDocument( (EnterpriseDocument) edc.getModel().getRowData() );
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		ec.setTreeView(true);
		ec.select(event, getDocument().getEnterprise() );			
	}
	
	private TreeNode<EnterpriseTreeData> getParentTreeNode( EnterpriseDocument ed ) {
		TreeNode<EnterpriseTreeData> parent = enterpriseNode;
		if ( (ed.getProject() != null) && (ed.getProject().getId() != null) ) {
			AonTreeKey key = new AonTreeKey(ed.getProject());
			parent = parent.getChild(key);
		}
		return parent;
	}	
	
	private TreeNode<EnterpriseTreeData> getTreeNode( EnterpriseDocument ed ) {
		TreeNode<EnterpriseTreeData> parent = getParentTreeNode(ed);
		if ( parent != null ) {
			AonTreeKey key = new AonTreeKey(ed);
			return parent.getChild(key);
		}
		return null;
	}

	public TreeNode<EnterpriseTreeData> addToTree( EnterpriseDocument ed ) {
		TreeNode<EnterpriseTreeData> documentNode = null;
		TreeNode<EnterpriseTreeData> parent = getParentTreeNode(ed);
		if ( parent != null ) {
			documentNode = new AonTreeNode<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(ed);
			documentNode.setData(etd);
			parent.addChild( etd.getKey(), documentNode );
			if ( parent != enterpriseNode ) {
				parent.getData().incCount();
			}
			enterpriseNode.getData().incCount();
		}	
		return documentNode; 
	}
	
	public void removeCurrentNodeFromTree() {
		TreeNode<EnterpriseTreeData> parent = currentNode.getParent();
		parent.removeChild(currentNode.getData().getKey());
		if ( parent != enterpriseNode ) {
			parent.getData().decCount();
		}
		enterpriseNode.getData().decCount();
		setCurrentNode(enterpriseNode);
	}
	
	private ListRowKey<AonTreeKey> getRowKey( TreeNode<EnterpriseTreeData> treeNode ) {
		ArrayList<AonTreeKey> list = new ArrayList<AonTreeKey>();
		TreeNode<EnterpriseTreeData> node = treeNode;
		while ( node.getData() != null ) {
			list.add( 0, node.getData().getKey() );
			node = node.getParent();
		}
		return new ListRowKey<AonTreeKey>(list);
	}
	
	public void selectTreeNode( TreeNode<EnterpriseTreeData> node ) {
		setCurrentNode(node);
		ListRowKey<AonTreeKey> key = getRowKey(node);
		getState().setSelected(key);
		TreeRowKey<AonTreeKey> _key = key;
		while ( _key.depth() > 0 ) {
			if (! getState().isExpanded(_key) ) {
				getState().makeExpanded(_key);		
			}
			_key = _key.getParentKey();
		}
	}	

	public void onReloadTree( ActionEvent event ) {
		setDocument(null);
		loadTree();
	}	

	public void onBack( ActionEvent event ) {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		controller.onBack(event);
		onReloadTree(event);
	}		

	public void onTreeViewSelect( ActionEvent event ) {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		controller.onTreeViewSelect(event);
		controller.setTreeTemplateSuffix("DocumentTree");
	}
	
}
