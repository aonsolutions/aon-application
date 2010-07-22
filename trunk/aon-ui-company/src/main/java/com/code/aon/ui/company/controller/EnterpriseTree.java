package com.code.aon.ui.company.controller;

import java.util.List;

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
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Contract;
import com.code.aon.employee.dao.IEmployeeAlias;
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
	
	public EnterpriseTreeData getTreeData( EnterpriseActivity ea ) {
		return new EnterpriseTreeData( ea.getId(), ea.getDescription(), EnterpriseTreeType.ACTIVITY);
	}

	public EnterpriseTreeData getTreeData( Contract c ) {
		return new EnterpriseTreeData( c.getId(), c.getPerson().getRegistry().getFullName(), EnterpriseTreeType.CONTRACT);
	}
	
	private void loadContracts( TreeNodeImpl<EnterpriseTreeData> workPlaceNode, WorkPlace workPlace ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);		
		Criteria criteria = new Criteria();
		String workPlaceId = bean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ID);
		criteria.addEqualExpression(workPlaceId, workPlace.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			Contract contract = (Contract) to;
			TreeNodeImpl<EnterpriseTreeData> contractNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(contract);
			contractNode.setData(etd);
			workPlaceNode.addChild( etd.getType().toString() + etd.getId(), contractNode );
		}		
	}		
	
	private void loadActivities( TreeNode<EnterpriseTreeData> enterpriseNode, Enterprise enterprise ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseActivity.class);		
		Criteria criteria = new Criteria();
		String enterpriseId = bean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID);
		criteria.addEqualExpression(enterpriseId, enterprise.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			EnterpriseActivity ea = (EnterpriseActivity) to;
			TreeNodeImpl<EnterpriseTreeData> eaNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(ea);
			eaNode.setData(etd);
			enterpriseNode.addChild( etd.getType().toString() + etd.getId(), eaNode );
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
			loadContracts(wpNode, wp);
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
			loadActivities(enterpriseNode, enterprise);
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
		try {
			selectWorkPlace(event, currentNode.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectWorkPlace exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSelectEnterpriseActivity( ActionEvent event ) {
		try {
			selectEnterpriseActivity(event, currentNode.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectWorkPlace exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	@SuppressWarnings("unchecked")	
	private void selectWorkPlace( ActionEvent event, Integer id ) throws ManagerBeanException {
		LinesController controller = (LinesController) AonUtil.getRegisteredBean(ENTERPRISE_WORK_PLACE_CONTROLLER_NAME);
		List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
		int index;
		for( index = 0; index < list.size(); index++) {
			WorkPlace workPlace = (WorkPlace) list.get(index);
			if ( ObjectUtils.equals(workPlace.getId(), id) ) {
				break;
			}
		}
		controller.getModel().setRowIndex(index);
		controller.onSelect(event);			
	}

	public void selectEnterpriseActivity( ActionEvent event, Integer id ) throws ManagerBeanException {
		LinesController controller = (LinesController) AonUtil.getRegisteredBean(ENTERPRISE_ACTIVITY_CONTROLLER_NAME);
		List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
		int index;
		for( index = 0; index < list.size(); index++) {
			EnterpriseActivity activity = (EnterpriseActivity) list.get(index);
			if ( ObjectUtils.equals(activity.getId(), id) ) {
				break;
			}
		}
		controller.getModel().setRowIndex(index);
		controller.onSelect(event);			
	}
	
}
