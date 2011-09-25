package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.payroll.dao.IPayrollAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID;
import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.IRPF_LAUNCHER_CONTROLLER_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeExpandedEvent;
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
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.RegistryInfo;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractBonusController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDeductionController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractEmbargoController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractPaymentController;
import com.esferalia.aon.ui.payroll.controller.launcher.IrpfLauncher;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.controller.wizard.ContractGenerationWizard;
import com.esferalia.aon.ui.payroll.utils.EnterpriseTreeData;
import com.esferalia.aon.ui.payroll.utils.EnterpriseTreeType;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private TreeNode<EnterpriseTreeData> currentNode;

	private TreeNode<EnterpriseTreeData> parentNode;
	
	private WorkPlace workPlace;
	
	private Contract contract;
	
	private ContractPayment contractPayment;
	
	private ContractDeduction contractDeduction;
	
	private ContractBonus contractBonus;
	
	private ContractEmbargo contractEmbargo;
	
	private RegistryInfo personInfo = new RegistryInfo();
	
	private boolean showContractHeader;
	
	private TreeState state;
	
	private boolean activeContract;
	
	private boolean inactiveContract;
	
	private Date inactiveDate;
	
	private InactiveLastPeriod inactiveLastPeriod;
	
	private boolean searchCurrent;
	
	private IControllerListener contractListener;
	
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	
	public InactiveLastPeriod getInactiveLastPeriod() {
		return inactiveLastPeriod;
	}

	public void setInactiveLastPeriod(InactiveLastPeriod inactiveLastPeriod) {
		this.inactiveLastPeriod = inactiveLastPeriod;
	}

	public Date getInactiveDate() {
		return inactiveDate;
	}

	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

	public boolean isActiveContract() {
		return activeContract;
	}

	public void setActiveContract(boolean activeContract) {
		this.activeContract = activeContract;
	}

	public boolean isInactiveContract() {
		return inactiveContract;
	}
	
	public void setInactiveContract(boolean inactiveContract) {
		this.inactiveContract = inactiveContract;
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
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	
	public Contract getContract() {
		return contract;
	}
	
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public ContractPayment getContractPayment() {
		return contractPayment;
	}
	
	public ContractDeduction getContractDeduction() {
		return contractDeduction;
	}
	
	public ContractBonus getContractBonus() {
		return contractBonus;
	}
	
	public ContractEmbargo getContractEmbargo() {
		return contractEmbargo;
	}

    public RegistryMedia getPhone() {
		return personInfo.getPhone();
	}

	public RegistryMedia getFax() {
		return personInfo.getFax();
	}

	public RegistryMedia getEmail() {
		return personInfo.getEmail();
	}

	public RegistryMedia getWeb() {
		return personInfo.getWeb();
	}
	
	public boolean isShowContractHeader() {
		return showContractHeader;
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

	private EnterpriseTreeData getTreeData( WorkPlace wp ) {
		return new EnterpriseTreeData( wp.getId(), wp.getDescription(), EnterpriseTreeType.WORKPLACE);
	}
	
	private EnterpriseTreeData getTreeData( Contract c ) {
		if(c.getEndDate()!=null && c.getEndDate().before(new Date())){
			return new EnterpriseTreeData( c.getId(), c.getPerson().getFullName(), EnterpriseTreeType.END_CONTRACT);
		} else {
			return new EnterpriseTreeData( c.getId(), c.getPerson().getFullName(), EnterpriseTreeType.CONTRACT);
		}
	}
	
	private void addMainNode( TreeNode<EnterpriseTreeData> contractNode ) {
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_ECONOMIC_DATA );
		EnterpriseTreeData etd = new EnterpriseTreeData( contractNode.getData().getId(), label, EnterpriseTreeType.MAIN);
		node.setData(etd);
		contractNode.addChild( etd.getKey(), node);
	}
	
	private void addSalaryNode( TreeNode<EnterpriseTreeData> contractNode ) {
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_SALARY );
		EnterpriseTreeData etd = new EnterpriseTreeData( contractNode.getData().getId(), label, EnterpriseTreeType.SALARY);
		node.setData(etd);
		contractNode.addChild( etd.getKey(), node);
	}

	private void addSalaryDraftNode( TreeNode<EnterpriseTreeData> contractNode ) {
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_SALARY_DRAFT );
		EnterpriseTreeData etd = new EnterpriseTreeData( contractNode.getData().getId(), label, EnterpriseTreeType.SALARY_DRAFT);
		node.setData(etd);
		contractNode.addChild( etd.getKey(), node);
	}

	private void addDocumentNode( TreeNode<EnterpriseTreeData> contractNode ) {
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_DOCUMENTS);
		EnterpriseTreeData etd = new EnterpriseTreeData( contractNode.getData().getId(), label, EnterpriseTreeType.DOCUMENT);
		node.setData(etd);
		contractNode.addChild( etd.getKey(), node);
	}
	
	private void loadContracts( TreeNodeImpl<EnterpriseTreeData> workPlaceNode, WorkPlace workPlace ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);		
		Criteria criteria = new Criteria();
		completeContractCriteria(bean, criteria);
		String workPlaceId = bean.getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ID);
		criteria.addEqualExpression(workPlaceId, workPlace.getId());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_PERSON_FIRST_SURNAME));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_PERSON_SECOND_SURNAME));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_PERSON_REGISTRY_NAME));
		for( ITransferObject to : bean.getList(criteria) ) {
			Contract contract = (Contract) to;
			TreeNodeImpl<EnterpriseTreeData> contractNode = new TreeNodeImpl<EnterpriseTreeData>();
			EnterpriseTreeData etd = getTreeData(contract);
			contractNode.setData(etd);
			workPlaceNode.addChild( etd.getKey(), contractNode );
			addMainNode(contractNode);
			addSalaryNode(contractNode);
			addSalaryDraftNode(contractNode);
			addDocumentNode(contractNode);
		}	
	}		
	
	private void completeContractCriteria(IManagerBean bean, Criteria criteria) throws ManagerBeanException{
		if(isActiveContract() && isInactiveContract()){
			if(getInactiveDate()!=null){
				String endDate = bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE);
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, getInactiveDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			}
		} else if(!isActiveContract() && !isInactiveContract()){
			String alias = bean.getFieldName(IPayrollAlias.CONTRACT_ID);
			criteria.addEqualExpression(alias, null);
		} else if(isActiveContract()){
			String endDate = bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} else if(isInactiveContract()){
			String endDate = bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE);
			if(getInactiveDate()==null){
				criteria.addNotNullExpression(endDate);
			} else {
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, getInactiveDate());
				Expression expr2 = ExpressionUtilities.getNotNullExpression(endDate);
				criteria.addExpression(ExpressionUtilities.getAndExpression(expr1, expr2));
			}
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
			enterpriseNode.addChild( etd.getKey(), wpNode );
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
	
	private ListRowKey<String> getRowKey( TreeNode<EnterpriseTreeData> treeNode ) {
		ArrayList<String> list = new ArrayList<String>();
		TreeNode<EnterpriseTreeData> node = treeNode;
		while ( node.getData() != null ) {
			list.add( 0, node.getData().getKey() );
			node = node.getParent();
		}
		return new ListRowKey<String>(list);
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
		if ( controller.isShowActivityNode() ) {
			addAcitivityNode(enterpriseNode);	
		}
		try {
			loadWorkPlaces(enterpriseNode, enterprise);
			if ( contract == null ) {
				setCurrentNode( enterpriseNode );	
			} else {
				onSelectTreeContract(null, contract);
				TreeNode<EnterpriseTreeData> node = getTreeNode(contract);
				selectTreeNode(node);			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading work places for " + enterprise, e );
		}
	}
	
	public void reloadTree(ActionEvent event){
		loadTree();
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			EnterpriseTreeData etd = (EnterpriseTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, getCurrentNode());
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
	
	public void onSelectTreeWorkPlace( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			this.workPlace = (WorkPlace) bean.get( getCurrentNode().getId() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectWorkPlace exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onSelectTreeContract( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			Contract contract = (Contract) bean.get( getCurrentNode().getId() );
			onSelectTreeContract(event, contract);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	public void onSelectTreeContract( ActionEvent event, Contract contract ) throws ManagerBeanException {
		this.contract = contract;
		this.personInfo.init( this.contract.getPerson().getRegistry() );
		this.showContractHeader = this.personInfo.hasMedias();
		this.showContractHeader |= selectSalaries(event, this.contract);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.select(event, this.contract);
		c.onShowVariables(event);
	}	
	
	
	private void selectContract(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = (Contract) bean.get( getParentNode().getId() );
			ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			c.select(event, this.contract);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void selectSalary(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = (Contract) bean.get( getParentNode().getId() );
			this.personInfo.init( this.contract.getPerson().getRegistry() );
			this.showContractHeader = this.personInfo.hasMedias();
			this.showContractHeader |= selectSalaries(event, this.contract);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void selectSalaryDraft(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = 
				(Contract) bean.get( getParentNode().getId() );
			SalaryDraftController sc = 
				(SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);

			if ( sc.getMonth() == null ) {
				Date drafDate = this.contract.getEndDate();
				Date now = Calendar.getInstance().getTime();
				if ( drafDate == null || drafDate.after(now)) {
					drafDate = now;
				} // If contract ends after now, show 'current' draft
				
				sc.setYear(CommonUtil.getYear(drafDate));
				sc.setMonth(Month.getMonthByValue(CommonUtil.getMonth(drafDate)));
			}
			if ( sc.getSalaryType() == null ) {
				sc.setSalaryType(SalaryType.SALARY); 
			}
			sc.select(event, this.contract.getId());

		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> selectSalaryDraft exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onSelectTreeMainData(ActionEvent event) {
		selectContract(event);
		reloadTreeMainData(event);
	}
	public void reloadTreeMainData(ActionEvent event) {
		ContractPaymentController payment = (ContractPaymentController) FormUtil.getController(IPayrollConstants.CONTRACT_PAYMENT_CONTROLLER);
		payment.setSearchCurrent(isSearchCurrent());
		payment.setInactiveDate(getInactiveDate());
		ContractDeductionController deduction = (ContractDeductionController) FormUtil.getController(IPayrollConstants.CONTRACT_DEDUCTION_CONTROLLER);
		deduction.setSearchCurrent(isSearchCurrent());
		deduction.setInactiveDate(getInactiveDate());
		ContractBonusController bonus = (ContractBonusController) FormUtil.getController(IPayrollConstants.CONTRACT_BONUS_CONTROLLER);
		bonus.setSearchCurrent(isSearchCurrent());
		bonus.setInactiveDate(getInactiveDate());
		ContractEmbargoController embargo = (ContractEmbargoController) FormUtil.getController(IPayrollConstants.CONTRACT_EMBARGO_CONTROLLER);
		embargo.setSearchCurrent(isSearchCurrent());
		embargo.setInactiveDate(getInactiveDate());
		
		ContractController contract = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		contract.onShowPayments(event);
		contract.onShowDeductions(event);
		contract.onShowBonus(event);
		contract.onShowEmbargos(event);
	}
		
	public void onSelectTreePayments(ActionEvent event) {
		selectContract(event);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.onShowPayments(event);
	}

	public void onSelectTreeDeductions(ActionEvent event) {
		selectContract(event);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.onShowDeductions(event);
	}

	public void onSelectTreeBonus(ActionEvent event) {
		selectContract(event);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.onShowBonus(event);
	}

	public void onSelectTreeEmbargos(ActionEvent event) {
		selectContract(event);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.onShowEmbargos(event);
	}
	
	public void onSelectTreeSalary(ActionEvent event) {
		selectSalary(event);
	}
	
	public void onSelectTreeSalaryDraft(ActionEvent event) {
		selectContract(event);
		selectSalaryDraft(event);
	}
	
	public void onSelectTreeDocuments(ActionEvent event) {
		selectContract(event);
		ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		c.onShowDocuments(event);
	}
	
	public boolean selectSalaries( ActionEvent event, Contract contract ) {
		try {
			IController controller = FormUtil.getController(SALARY_CONTROLLER_NAME);
			controller.clearCriteria();
			Criteria criteria = controller.getCriteria();
			String contractAlias = controller.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID);
			criteria.addEqualExpression(contractAlias, contract.getId());
			criteria.addOrder(controller.getFieldName(IPayrollAlias.SALARY_END_DATE), false);
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

	public void onInit( ActionEvent event ) {
		setCurrentNode( this.enterpriseNode );
	}
	
	public TreeState getState() {
		return state;
	}

	public void setState(TreeState state) {
		this.state = state;
	}
	
	public void onLoadWorkPlaceCalendar( ActionEvent event ) {
		// TODO implementar la busqueda del calendario. si la entidad no tiene calendario, 
		// buscar el calendario en sus entidades superiores
		WorkPlace wp = getWorkPlace();
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICompanyConstants.CALENDAR_CONTROLLER_NAME);
		controller.setEnterpriseName(wp.getEnterprise().getRegistry().getFullName());
		controller.setWorkPlaceName(wp.getDescription());
		controller.setCalendarId(getPayrollWorkPlace().getCalendar().getId());
		controller.onInitialize(event);
	}

	public PayrollWorkPlace getPayrollWorkPlace() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), getWorkPlace().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (PayrollWorkPlace) list.get(0); 
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getPayrollWorkPlace exception: ",e);
		}
		return new PayrollWorkPlace();
	}	

	public void onChangeLastPeriod( ActionEvent event ) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_MONTH){
			cal.add(Calendar.MONTH, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_QUARTER){
			cal.add(Calendar.MONTH, -3);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_SEMESTER){
			cal.add(Calendar.MONTH, -6);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_YEAR){
			cal.add(Calendar.YEAR, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.ALL){
			cal = null;
		}
		setInactiveDate(cal!=null?cal.getTime():null);
	}
	
	public void onChangeInactiveDate( ActionEvent event ) {
		if(isInactiveContract() && getInactiveDate()==null && getInactiveLastPeriod()!=InactiveLastPeriod.ALL){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			setInactiveDate(cal!=null?cal.getTime():null);
		}
	}

	public void onResetContract( ActionEvent event ) {
		EnterpriseController ec= (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onReset(event);
		controller.setEnterprise((Enterprise) ec.getTo());
		controller.onShowNewContractModal(event);
	}
	
	public void expandChanged( NodeExpandedEvent event ) {
		selectNode( (UITree) event.getComponent() );
	}

	private void selectNode( UITree tree ) {
		EnterpriseTreeData node = (EnterpriseTreeData) tree.getRowData();
		if ( node != getCurrentNode() ) {
			parentNode = tree.getTreeNode().getParent();
			setCurrentNode( (TreeNode<EnterpriseTreeData>) tree.getTreeNode() );
			getCurrentNode().actionListener(null);
		}
	}
	
	private Contract getShowContract() throws ManagerBeanException {
		IrpfLauncher launcher = (IrpfLauncher) AonUtil.getRegisteredBean(IRPF_LAUNCHER_CONTROLLER_NAME);
		Integer id = launcher.getContractId();
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		return (Contract) bean.get(id);
	}
	
	private TreeNode<EnterpriseTreeData> getTreeNode( WorkPlace workPlace ) {
		TreeNode<EnterpriseTreeData> node = enterpriseNode;
		Iterator<Map.Entry<Object, TreeNode<EnterpriseTreeData>>> i = node.getChildren();
		while ( i.hasNext() ) {
			Map.Entry<Object, TreeNode<EnterpriseTreeData>> entry = i.next();
			EnterpriseTreeData etd = entry.getValue().getData();
			if ( (etd.getType() == EnterpriseTreeType.WORKPLACE) && (etd.getId().equals(workPlace.getId()))) {
				return entry.getValue();
			}
			
		}
		return null;
	}	
	
	private TreeNode<EnterpriseTreeData> getTreeNode( Contract contract ) {
		TreeNode<EnterpriseTreeData> node = getTreeNode(contract.getWorkPlace());
		if ( node != null ) {
			Iterator<Map.Entry<Object, TreeNode<EnterpriseTreeData>>> i = node.getChildren();
			while ( i.hasNext() ) {
				Map.Entry<Object, TreeNode<EnterpriseTreeData>> entry = i.next();
				EnterpriseTreeData etd = entry.getValue().getData();
				if ( (etd.getType() == EnterpriseTreeType.CONTRACT) && (etd.getId().equals(contract.getId()))) {
					return entry.getValue();
				}
				
			}			
		}
		return null;
	}
	
	private void selectTreeNode( TreeNode<EnterpriseTreeData> node ) {
		try {
			getState().collapseAll(null);
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}
		setCurrentNode(node);
		ListRowKey<String> key = getRowKey(node);
		getState().setSelected(key);
		TreeRowKey<String> _key = key;
		while ( _key != null ) {
			if (! getState().isExpanded(_key) ) {
				getState().makeExpanded(_key);		
			}
			_key = _key.getParentKey();
		}
	}	

	public void onShowContract( ActionEvent event ) throws ManagerBeanException {
		setContract( getShowContract() );
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		ec.setTreeView(true);
		ec.select(event, contract.getWorkPlace().getEnterprise() );			
	}

	public IControllerListener getContractListener() {
		if ( contractListener == null ) {
			contractListener = new ControllerAdapter() {

				@Override
				public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
					try {
						IController controller = event.getController();
						Criteria criteria = controller.getCriteria();
						completeContractCriteria(controller.getManagerBean(), criteria);
						EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
						Enterprise enterprise = (Enterprise) ec.getTo();
						String alias = controller.getFieldName(CONTRACT_WORK_PLACE_ENTERPRISE_ID);
						criteria.addEqualExpression(alias, enterprise.getId());
					} catch (ManagerBeanException e) {
						LOGGER.error( e.getMessage(), e );
						throw new ControllerListenerException(e.getMessage(), e);
					}
				}
				
			};
		}
		return contractListener;
	}

	public void onContractChanged(LookupChangeEvent event) {
		if(event.getNewValue()!=null){
			Contract contract = (Contract) event.getNewValue();
			try {			
				onSelectTreeContract(null, contract);
				TreeNode<EnterpriseTreeData> node = getTreeNode(contract);
				selectTreeNode(node); 
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
	}

}