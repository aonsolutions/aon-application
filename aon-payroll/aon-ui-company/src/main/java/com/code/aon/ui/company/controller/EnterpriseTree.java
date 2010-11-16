package com.code.aon.ui.company.controller;

import java.io.Serializable;
import java.util.Date;
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
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Contract;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.util.EnterpriseTreeData;
import com.code.aon.ui.company.util.EnterpriseTreeType;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseTree implements ICompanyConstants {

	private static final String CONTRACT_CONTROLLER_NAME = "contract";

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private EnterpriseTreeData currentNode;
	
	private WorkPlace workPlace;
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}
	
	public EnterpriseTreeData getCurrentNode() {
		return currentNode;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
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
	
	public EnterpriseTreeData getTreeData( Contract c ) {
		return new EnterpriseTreeData( c.getId(), c.getPerson().getRegistry().getFullName(), EnterpriseTreeType.CONTRACT);
	}
	
	private void loadContracts( TreeNodeImpl<EnterpriseTreeData> workPlaceNode, WorkPlace workPlace ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);		
		Criteria criteria = new Criteria();
		String endDate = bean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE);
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		String workPlaceId = bean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ID);
		criteria.addEqualExpression(workPlaceId, workPlace.getId());
		criteria.addOrder(bean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_FIRST_SURNAME));
		criteria.addOrder(bean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_SECOND_SURNAME));
		criteria.addOrder(bean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_REGISTRY_NAME));
		for( ITransferObject to : bean.getList(criteria) ) {
			Contract contract = (Contract) to;
			TreeNodeImpl<EnterpriseTreeData> contractNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(contract);
			contractNode.setData(etd);
			workPlaceNode.addChild( etd.getType().toString() + etd.getId(), contractNode );
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

	private void addAcitivityNode( TreeNode<EnterpriseTreeData> rootNode ) {
		String id = ICompanyConstants.ENTERPRISE_ACTIVITY_CONTROLLER_NAME;
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( ICompanyConstants.BUNDLE_NAME, ICompanyConstants.COMPANY_ACTIVITY_MODULE );
		EnterpriseTreeData etd = new EnterpriseTreeData( id, label, EnterpriseTreeType.ACTIVITY);
		node.setData(etd);
		enterpriseNode.addChild( id, node);
}
	
	public void loadTree() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		rootNode = new TreeNodeImpl<EnterpriseTreeData>();
		enterpriseNode = new TreeNodeImpl<EnterpriseTreeData>();
		EnterpriseTreeData etd = getTreeData(enterprise);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getType().toString() + etd.getId(), enterpriseNode );
		if ( controller.isShowActivityNode() ) {
			addAcitivityNode(enterpriseNode);	
		}
		try {
			loadWorkPlaces(enterpriseNode, enterprise);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading work places for " + enterprise, e );
		}
		currentNode = etd;
		
		try {
			IController cController = FormUtil.getController(CONTRACT_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			String endDate = cController.getFieldName(IEmployeeAlias.CONTRACT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			String erp = cController.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			criteria.addEqualExpression(erp, enterprise.getId());
			criteria.addOrder(cController.getFieldName(IEmployeeAlias.CONTRACT_PERSON_FIRST_SURNAME));
			criteria.addOrder(cController.getFieldName(IEmployeeAlias.CONTRACT_PERSON_SECOND_SURNAME));
			criteria.addOrder(cController.getFieldName(IEmployeeAlias.CONTRACT_PERSON_REGISTRY_NAME));
			cController.setCriteria(criteria);
			cController.initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading contracts for " + enterprise, e );
		}
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			EnterpriseTreeData etd = (EnterpriseTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, currentNode);
		}
		return selected;
	}	
	
	@SuppressWarnings("rawtypes")
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
	
	public void onSelectTreeWorkPlace( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			this.workPlace = (WorkPlace) bean.get( currentNode.getId() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectWorkPlace exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onSelectTreeContract( ActionEvent event ) {
		try {
			selectTreeContracts(event, currentNode.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	public void onSelectContract( ActionEvent event ) {
		try {
			IController controller = (IController)AonUtil.getRegisteredBean(CONTRACT_CONTROLLER_NAME);
			Contract c = (Contract)controller.getModel().getRowData();
			EnterpriseTreeData data = new EnterpriseTreeData(c.getId(), c.getPerson().getRegistry().getFullName(), EnterpriseTreeType.CONTRACT);
			setCurrentNode(data);
			selectTreeContracts(event, currentNode.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	@SuppressWarnings("unchecked")
	private void selectNode( ActionEvent event, String controllerName, Serializable id ) throws ManagerBeanException {
		IController controller = FormUtil.getController(controllerName);
		List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
		int index;
		for( index = 0; index < list.size(); index++) {
			ITransferObject to = list.get(index);
			Serializable currentId = controller.getManagerBean().getId(to);
			if ( ObjectUtils.equals(currentId, id) ) {
				break;
			}
		}
		controller.getModel().setRowIndex(index);
		controller.onSelect(event);			
	}	
	
	private void selectTreeContracts( ActionEvent event, Serializable id ) throws ManagerBeanException {
		IController controller = FormUtil.getController(CONTRACT_CONTROLLER_NAME);
		try {
			Contract contract = (Contract) controller.getManagerBean().get(currentNode.getId());
			LinesController wpController = (LinesController) AonUtil.getRegisteredBean(ENTERPRISE_WORK_PLACE_CONTROLLER_NAME);
			wpController.select(event, contract.getWorkPlace());
			selectTreeContracts( contract.getWorkPlace().getId() );
			selectContracts(contract.getPerson());
			selectSalaries(contract);
			selectNode(event, CONTRACT_CONTROLLER_NAME, id);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectActivity exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void selectTreeContracts( Serializable id ) {
		try {
			IController controller = FormUtil.getController(CONTRACT_CONTROLLER_NAME);
			controller.clearCriteria();
			Criteria criteria = controller.getCriteria();
			String wpAlias = controller.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ID);
			criteria.addEqualExpression(wpAlias, id);
			String endDate = controller.getFieldName(IEmployeeAlias.CONTRACT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			criteria.addOrder(controller.getFieldName(IEmployeeAlias.CONTRACT_PERSON_FIRST_SURNAME));
			criteria.addOrder(controller.getFieldName(IEmployeeAlias.CONTRACT_PERSON_SECOND_SURNAME));
			criteria.addOrder(controller.getFieldName(IEmployeeAlias.CONTRACT_PERSON_REGISTRY_NAME));
			controller.initializeModel();
			
//			Criteria criteria = new Criteria();
//			String erp = cController.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
//			criteria.addEqualExpression(erp, enterprise.getId());
			
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectContracts exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void selectContracts( Person person ) {
		try {
			IController controller = FormUtil.getController(CONTRACT_CONTROLLER_NAME);
			controller.clearCriteria();
			Criteria criteria = controller.getCriteria();
			String personAlias = controller.getFieldName(IEmployeeAlias.CONTRACT_PERSON_ID);
			criteria.addEqualExpression(personAlias, person.getId());
			String endDate = controller.getFieldName(IEmployeeAlias.CONTRACT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			controller.initializeModel();
			initializeSalaries();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectContracts exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void selectSalaries( Contract contract) {
		try {
			IController controller = FormUtil.getController(SALARY_CONTROLLER_NAME);
			controller.clearCriteria();
			Criteria criteria = controller.getCriteria();
			String contractAlias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_ID);
			criteria.addEqualExpression(contractAlias, contract.getId());
			controller.initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectSalaries exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	private void initializeSalaries(){
		IController controller = FormUtil.getController(SALARY_CONTROLLER_NAME);
		controller.onCancel(null);
	}
	
}
