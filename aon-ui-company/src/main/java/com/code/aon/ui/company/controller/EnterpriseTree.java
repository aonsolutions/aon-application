package com.code.aon.ui.company.controller;

import java.util.Date;

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
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.company.util.EnterpriseTreeData;
import com.code.aon.ui.company.util.EnterpriseTreeType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private EnterpriseTreeData currentNode;
	
	private WorkPlace workPlace;
	
	private Contract contract;
	
	private RegistryMedia phone;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;	
	
	private boolean showContractHeader;
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}
	
	public EnterpriseTreeData getCurrentNode() {
		return currentNode;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	
	public Contract getContract() {
		return contract;
	}

	public RegistryMedia getPhone() {
		return phone;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}
	
	public boolean isShowContractHeader() {
		return showContractHeader;
	}

	public void setCurrentNode(EnterpriseTreeData currentNode) {
		this.currentNode = currentNode;
	}
	
	public TreeNode<EnterpriseTreeData> getEnterpriseNode() {
		return enterpriseNode;
	}

	private EnterpriseTreeData getTreeData( Enterprise e ) {
		return new EnterpriseTreeData( e.getId(), e.getRegistry().getFullName(), EnterpriseTreeType.ENTERPRISE);
	}

	private EnterpriseTreeData getTreeData( WorkPlace wp ) {
		return new EnterpriseTreeData( wp.getId(), wp.getDescription(), EnterpriseTreeType.WORKPLACE);
	}
	
	private EnterpriseTreeData getTreeData( Contract c ) {
		return new EnterpriseTreeData( c.getId(), c.getPerson().getFullName(), EnterpriseTreeType.CONTRACT);
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
	
	private boolean updateRegistryMedias( Registry registry ) throws ManagerBeanException {
		IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteriaMedia = new Criteria();
		String registryIdFieldName = beanMedia.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
		criteriaMedia.addEqualExpression(registryIdFieldName, registry.getId());
		this.phone = null;
		this.fax = null;
		this.email = null;
		for( ITransferObject to : beanMedia.getList(criteriaMedia) ) {
			RegistryMedia rmedia = (RegistryMedia) to;
			switch (rmedia.getMediaType()) {
			case FIXED_PHONE:
				phone = rmedia;
				break;
			case FAX:
				fax = rmedia;
				break;
			case EMAIL:
				email = rmedia;
				break;
			}			
		}
		return (phone != null) || (fax != null) || (email != null);
	}
	
	public void onSelectTreeContract( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = (Contract) bean.get( currentNode.getId() );
			this.showContractHeader = updateRegistryMedias( this.contract.getPerson().getRegistry() );
			this.showContractHeader |= selectSalaries(event, this.contract);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
		
	public boolean selectSalaries( ActionEvent event, Contract contract ) {
		try {
			IController controller = FormUtil.getController(SALARY_CONTROLLER_NAME);
			controller.clearCriteria();
			Criteria criteria = controller.getCriteria();
			String contractAlias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_ID);
			criteria.addEqualExpression(contractAlias, contract.getId());
			criteria.addOrder(controller.getFieldName(IEmployeeAlias.SALARY_END_DATE), false);
			controller.initializeModel();
			if ( controller.getModel().getRowCount() > 0 ) {
				controller.getModel().setRowIndex(0);
				controller.onSelect(event);
				return true;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectSalaries exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		return false;
	}	

	public void onEditPerson( ActionEvent event ) {
		try {
			BasicController controller = (BasicController) FormUtil.getController(IRegistryConstants.PERSON_CONTROLLER_NAME);
			controller.select(event, this.contract.getPerson());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEditPerson exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}				
	}

}
