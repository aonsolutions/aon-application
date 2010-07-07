package com.code.aon.ui.company.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.ListRowKey;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.util.EnterpriseTreeData;
import com.code.aon.ui.company.util.EnterpriseTreeType;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private EnterpriseTreeData currentNode;
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}
	
	public EnterpriseTreeData getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(EnterpriseTreeData currentNode) {
		this.currentNode = currentNode;
	}
	
	public TreeNode<EnterpriseTreeData> getEnterpriseNode() {
		return enterpriseNode;
	}

	public EnterpriseTreeData getTreeData( Enterprise e ) {
		return new EnterpriseTreeData( e.getId(), e.getRegistry().getFullName(), EnterpriseTreeType.ENTERPRISE);
	}

	public EnterpriseTreeData getTreeData( WorkPlace wp ) {
		return new EnterpriseTreeData( wp.getId(), wp.getDescription(), EnterpriseTreeType.WORKPLACE);
	}
	
	public EnterpriseTreeData getTreeData( WorkActivity wa ) {
		return new EnterpriseTreeData( wa.getId(), wa.getDescription(), EnterpriseTreeType.ACTIVITY);
	}

	public EnterpriseTreeData getTreeData( Employee e ) {
		return new EnterpriseTreeData( e.getId(), e.getRegistry().getFullName(), EnterpriseTreeType.EMPLOYEE);
	}
	
	private void loadEmployees( TreeNodeImpl<EnterpriseTreeData> workActivityNode, WorkActivity workActivity ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Employee.class);		
		Criteria criteria = new Criteria();
		String workActivityId = bean.getFieldName(ICompanyAlias.EMPLOYEE_CALENDAR);
		criteria.addEqualExpression(workActivityId, workActivity.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			Employee e = (Employee) to;
			TreeNodeImpl<EnterpriseTreeData> employeeNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(e);
			employeeNode.setData(etd);
			workActivityNode.addChild( etd.getType().toString() + etd.getId(), employeeNode );
		}		
	}	
	
	private void loadWorkActivities( TreeNodeImpl<EnterpriseTreeData> workPlaceNode, WorkPlace workPlace ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkActivity.class);		
		Criteria criteria = new Criteria();
		String workPlaceId = bean.getFieldName(ICompanyAlias.WORK_ACTIVITY_WORK_PLACE_ID);
		criteria.addEqualExpression(workPlaceId, workPlace.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			WorkActivity wa = (WorkActivity) to;
			TreeNodeImpl<EnterpriseTreeData> waNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(wa);
			waNode.setData(etd);
			workPlaceNode.addChild( etd.getType().toString() + etd.getId(), waNode );
			loadEmployees(waNode, wa);
		}		
	}

	private void loadWorkPlaces( TreeNode<EnterpriseTreeData> enterpriseNode, Enterprise enterprise ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);		
		Criteria criteria = new Criteria();
		String enterpriseId = bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(enterpriseId, enterprise.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			WorkPlace wp = (WorkPlace) to;
			TreeNodeImpl<EnterpriseTreeData> wpNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(wp);
			wpNode.setData(etd);
			enterpriseNode.addChild( etd.getType().toString() + etd.getId(), wpNode );
			loadWorkActivities( wpNode, wp );
		}
	}
	
	public void loadTree() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		rootNode = new TreeNodeImpl<EnterpriseTreeData>();
		enterpriseNode = new TreeNodeImpl<EnterpriseTreeData>();
		EnterpriseTreeData etd = getTreeData(enterprise);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getType().toString() + etd.getId(), enterpriseNode );
		try {
			loadWorkPlaces(enterpriseNode, enterprise);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading work places for " + enterprise, e );
		}
		currentNode = etd;
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			EnterpriseTreeData etd = (EnterpriseTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, currentNode);
		}
		return selected;
	}	
	
	@SuppressWarnings("unchecked")
	public Boolean adviseNodeOpened(UITree tree) {
		ListRowKey treeRowKey = (ListRowKey) tree.getRowKey();
        if (treeRowKey == null || treeRowKey.depth() <= 2) {
            return Boolean.TRUE;
        }		
		return null;
	}		

	public void processSelection(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		currentNode = (EnterpriseTreeData) tree.getRowData();
	}
	
	public void onSelectWorkPlace( ActionEvent event ) {
		LinesController controller = (LinesController) AonUtil.getRegisteredBean(ENTERPRISE_WORK_PLACE_CONTROLLER_NAME);
		try {
			ITransferObject to = controller.getManagerBean().get(currentNode.getId());
			controller.select(event, to);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectWorkPlace exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSelectWorkActivity( ActionEvent event ) {
		LinesController controller = (LinesController) AonUtil.getRegisteredBean(WORK_ACTIVITY_CONTROLLER_NAME);
		try {
			WorkActivity wa = (WorkActivity) controller.getManagerBean().get(currentNode.getId());
			LinesController wpController = (LinesController) AonUtil.getRegisteredBean(ENTERPRISE_WORK_PLACE_CONTROLLER_NAME);
			wpController.select(event, wa.getWorkPlace());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectActivity exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
