package com.code.aon.ui.company.controller;

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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.util.EnterpriseTreeData;
import com.code.aon.ui.company.util.EnterpriseTreeType;

public class EnterpriseTree {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}
	
	private void loadWorkActivities( TreeNodeImpl<EnterpriseTreeData> workPlaceNode, WorkPlace workPlace ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkActivity.class);		
		Criteria criteria = new Criteria();
		String workPlaceId = bean.getFieldName(ICompanyAlias.WORK_ACTIVITY_WORK_PLACE_ID);
		criteria.addEqualExpression(workPlaceId, workPlace.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			WorkActivity wa = (WorkActivity) to;
			TreeNodeImpl<EnterpriseTreeData> waNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = new EnterpriseTreeData( wa.getId(), wa.getDescription(), EnterpriseTreeType.ACTIVITY);
			waNode.setData(etd);
			workPlaceNode.addChild( etd.getType().toString() + etd.getId(), waNode );
		}		
	}

	private void loadWorkPlaces( TreeNodeImpl<EnterpriseTreeData> enterpriseNode, Enterprise enterprise ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);		
		Criteria criteria = new Criteria();
		String enterpriseId = bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(enterpriseId, enterprise.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			WorkPlace wp = (WorkPlace) to;
			TreeNodeImpl<EnterpriseTreeData> wpNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = new EnterpriseTreeData( wp.getId(), wp.getDescription(), EnterpriseTreeType.WORKPLACE);
			wpNode.setData(etd);
			enterpriseNode.addChild( etd.getType().toString() + etd.getId(), wpNode );
			loadWorkActivities( wpNode, wp );
		}
	}
	
	public void loadTree( Enterprise enterprise ) {
		rootNode = new TreeNodeImpl<EnterpriseTreeData>();
		TreeNodeImpl<EnterpriseTreeData> enterpriseNode = new TreeNodeImpl<EnterpriseTreeData>();
		EnterpriseTreeData etd = new EnterpriseTreeData(enterprise.getId(), enterprise.getRegistry().getFullName(), EnterpriseTreeType.ENTERPRISE);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getType().toString() + etd.getId(), enterpriseNode );
		try {
			loadWorkPlaces(enterpriseNode, enterprise);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading work places for " + enterprise, e );
		}
	}

}
