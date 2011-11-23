package com.code.aon.ui.document.tree;

import static com.code.aon.document.IAlfrescoConstants.ENTERPRISE_ID_SHORT;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.project.controller.IProjectConstants.PROJECT_CONTROLLER_NAME;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.richfaces.component.UITree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.ListRowKey;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
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
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.project.controller.ProjectController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private TreeNode<EnterpriseTreeData> currentNode;

	private TreeNode<EnterpriseTreeData> parentNode;
	
	private TreeState state;

	private String splitterPosition = "250";

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
		return new EnterpriseTreeData( e.getId(), e.getRegistry().getFullName(), EnterpriseTreeType.ENTERPRISE);
	}

	private EnterpriseTreeData getTreeData( Project project ) {
		return new EnterpriseTreeData( project.getId(), project.getName(), EnterpriseTreeType.PROJECT);
	}
	
	private EnterpriseTreeData getTreeData( EnterpriseDocument ed ) {
		EnterpriseTreeData etd = new EnterpriseTreeData( ed.getId(), ed.getName(), EnterpriseTreeType.DOCUMENT);
		etd.setMimeType(ed.getMimeType());
		return etd;
	}

	private void loadDocuments( TreeNode<EnterpriseTreeData> enterpriseNode, Map<Integer,TreeNode<EnterpriseTreeData>> projects ) throws ManagerBeanException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		IManagerBean bean = edc.getManagerBean();		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(ENTERPRISE_ID_SHORT, enterpriseNode.getData().getId());
		criteria.addOrder(bean.getFieldName(Constants.PROP_NAME));
		List<ITransferObject> list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			EnterpriseDocument ed = (EnterpriseDocument) to;
			TreeNodeImpl<EnterpriseTreeData> documentNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(ed);
			documentNode.setData(etd);
			TreeNode<EnterpriseTreeData> parent = null;
			if ( ed.getProject() != null ) {
				parent = projects.get(ed.getProject().getId());
			}
			if ( parent == null ) {
				parent = enterpriseNode;
			}
			parent.addChild( etd.getKey(), documentNode );	
			parent.getData().incCount();
		}	
		enterpriseNode.getData().setCount(list.size());
	}		
	
	private void loadProjects( TreeNode<EnterpriseTreeData> enterpriseNode, Enterprise enterprise ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Project.class);		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IProjectAlias.PROJECT_ENTERPRISE_ID), enterprise.getId());
		criteria.addOrder(bean.getFieldName(IProjectAlias.PROJECT_NAME));
		Map<Integer,TreeNode<EnterpriseTreeData>> projects = new HashMap<Integer, TreeNode<EnterpriseTreeData>>();
		List<ITransferObject> list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			Project project = (Project) to;
			TreeNodeImpl<EnterpriseTreeData> projectNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(project);
			projectNode.setData(etd);
			enterpriseNode.addChild( etd.getKey(), projectNode );
			projects.put(project.getId(), projectNode);
		}
		loadDocuments(enterpriseNode, projects);
	}
	
	public void loadTree() {
		setState( new TreeState() );
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		rootNode = new TreeNodeImpl<EnterpriseTreeData>();
		enterpriseNode = new TreeNodeImpl<EnterpriseTreeData>();
		EnterpriseTreeData etd = getTreeData(enterprise);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getKey(), enterpriseNode );
		try {
			loadProjects(enterpriseNode, enterprise);
			setCurrentNode( enterpriseNode );
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading documents for " + enterprise, e );
		}
	}
	
	public void reloadTree(ActionEvent event){
		loadTree();
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
		ListRowKey<?> treeRowKey = (ListRowKey<?>) tree.getRowKey();
        if (treeRowKey == null || treeRowKey.depth() <= 1) {
            return Boolean.TRUE;
        }		
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
		ListRowKey<String> parentKey = (ListRowKey<String>) key.getParentKey();
		TreeNode<EnterpriseTreeData> parent = tree.getTreeNode().getParent();
		Iterator<Map.Entry<Object, TreeNode<EnterpriseTreeData>>> i = parent.getChildren();
		while ( i.hasNext() ) {
			Map.Entry<Object, TreeNode<EnterpriseTreeData>> entry = i.next();
			String id = (String) entry.getKey();
			ListRowKey<String> nodeKey = new ListRowKey<String>(parentKey, id);
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
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		try {
			Reference id = (Reference) getCurrentNode().getId();
			edc.select(event, BasicAlfresco.getId(id));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeDocument exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
}