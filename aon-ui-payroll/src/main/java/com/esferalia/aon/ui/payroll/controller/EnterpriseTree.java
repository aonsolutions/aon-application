package com.esferalia.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.DataComponentState;
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
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.RegistryInfo;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
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
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.controller.wizard.ContractGenerationWizard;
import com.esferalia.aon.ui.payroll.utils.EnterpriseTreeData;
import com.esferalia.aon.ui.payroll.utils.EnterpriseTreeType;

public class EnterpriseTree implements ICompanyConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	private TreeNode<EnterpriseTreeData> enterpriseNode;
	
	private EnterpriseTreeData currentNode;

	private EnterpriseTreeData parentNode;
	
	private WorkPlace workPlace;
	
	private Contract contract;
	
	private ContractPayment contractPayment;
	
	private ContractDeduction contractDeduction;
	
	private ContractBonus contractBonus;
	
	private ContractEmbargo contractEmbargo;
	
	private RegistryInfo personInfo = new RegistryInfo();
	
	private boolean showContractHeader;
	
	private DataComponentState state;
	
	private boolean activeContract;
	
	private boolean inactiveContract;
	
	private Date inactiveDate;
	
	private InactiveLastPeriod inactiveLastPeriod;
	
	private boolean searchCurrent;
	
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
		return currentNode;
	}
	
	public EnterpriseTreeData getParentNode() {
		return parentNode;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	
	public Contract getContract() {
		return contract;
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

	public void setCurrentNode(EnterpriseTreeData currentNode) {
		this.currentNode = currentNode;
	}
	
	public void setParentNode(EnterpriseTreeData parentNode) {
		this.parentNode = parentNode;
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
		String id = IPayrollConstants.CONTRACT_CONTROLLER;
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_ECONOMIC_DATA );
		EnterpriseTreeData etd = new EnterpriseTreeData( id+contractNode.getData().getId(), label, EnterpriseTreeType.MAIN);
		node.setData(etd);
		contractNode.addChild( id, node);
	}
	private void addSalaryNode( TreeNode<EnterpriseTreeData> contractNode ) {
		String id = IPayrollConstants.SALARY_CONTROLLER;
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_SALARY );
		EnterpriseTreeData etd = new EnterpriseTreeData( id+contractNode.getData().getId(), label, EnterpriseTreeType.SALARY);
		node.setData(etd);
		contractNode.addChild( id, node);
	}
	private void addSalaryDraftNode( TreeNode<EnterpriseTreeData> contractNode ) {
		String id = IPayrollConstants.SALARY_DRAFT_CONTROLLER;
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_SALARY_DRAFT );
		EnterpriseTreeData etd = new EnterpriseTreeData( id+contractNode.getData().getId(), label, EnterpriseTreeType.SALARY_DRAFT);
		node.setData(etd);
		contractNode.addChild( id, node);
	}
	private void addDocumentNode( TreeNode<EnterpriseTreeData> contractNode ) {
		String id = IPayrollConstants.CONTRACT_GENERATION_WIZARD_CONTROLLER;
		TreeNodeImpl<EnterpriseTreeData> node = new TreeNodeImpl<EnterpriseTreeData>();
		String label = AonUtil.getMessage( IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_DOCUMENTS);
		EnterpriseTreeData etd = new EnterpriseTreeData( id+contractNode.getData().getId(), label, EnterpriseTreeType.DOCUMENT);
		node.setData(etd);
		contractNode.addChild( id, node);
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
			workPlaceNode.addChild( etd.getType().toString() + etd.getId(), contractNode );
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
	
	public void reloadTree(ActionEvent event){
		loadTree();
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			EnterpriseTreeData etd = (EnterpriseTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, currentNode);
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
		UITree tree = (UITree) event.getComponent();
		parentNode = (EnterpriseTreeData) tree.getTreeNode().getParent().getData();
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
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = (Contract) bean.get( currentNode.getId() );
			this.personInfo.init( this.contract.getPerson().getRegistry() );
			this.showContractHeader = this.personInfo.hasMedias();
			this.showContractHeader |= selectSalaries(event, this.contract);
			ContractController c = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			c.select(event, this.contract);
			c.onShowVariables(event);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectTreeContract exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	private void selectContract(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			this.contract = (Contract) bean.get( parentNode.getId() );
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
			this.contract = (Contract) bean.get( parentNode.getId() );
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
				(Contract) bean.get( parentNode.getId() );
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
		ContractGenerationWizard c = (ContractGenerationWizard) FormUtil.getController(IPayrollConstants.CONTRACT_GENERATION_WIZARD_CONTROLLER);
		try {
			c.select(event, this.contract);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		this.currentNode = this.enterpriseNode.getData();
	}
	
	public DataComponentState getState() {
		return state;
	}

	public void setState(DataComponentState state) {
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
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onReset(event);
		controller.setEnterprise((Enterprise) ec.getTo());
		controller.onShowNewContractModal(event);
	}
	
}
