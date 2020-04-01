package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.AfiActionType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ContractListController extends BasicController implements BatchListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractListController.class);

	private boolean searchPanelExpanded;
	
	private Person person;
	private Enterprise enterprise;
	private Date dateFrom;
	private Date dateTo;
	
	private List<ITransferObject> pendingList;
	private BatchListCheckHandler checkHandler;
	
	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
	}
	
	public BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public void init(){
		try {
			setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
			setPerson( (Person) BeanManager.getManagerBean(Person.class).createNewTo() );
			setDateFrom(new Date());
//			setStartDateTo(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), CommonUtil.getDay(new Date())+30));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<ITransferObject> getAllList() throws ManagerBeanException {
		return pendingList;
	}

	@Override
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		pendingList = new LinkedList<>();
		searchMA();
		searchMB();
		searchMG();
		searchMC();
		searchMT();
		searchCCP();
		searchMJR();
		searchCIT();
		searchMHU();
		searchMIN();
		setModel(new SerializableListDataModel(pendingList));
	}
	
	private void completeContractCriteria(Criteria criteria) {
		try {
			SEPEUtils utils = SEPEUtils.getInstance();
			
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			if ((getPerson() != null) && (getPerson().getId() != null)) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());			
			}
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				criteria.setSkipDomainFilter( true );
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				criteria.setSkipDomainFilter( true );
				criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void addPendingList(Contract contract, AfiActionType action, Date date) {
		ContractBatchDetail detail = new ContractBatchDetail();
		detail.setActionType(action);
		detail.setContract(contract);
		detail.setRealDate(date);
		pendingList.add(detail);
	}
	
	/**
	 * MA - Alta sucesiva
	 */
	private void searchMA() {
		try {
			Criteria criteria = new Criteria();
			completeContractCriteria(criteria);
			
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE));
			if(getDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), getDateFrom());
			}
			if(getDateTo()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), getDateTo());
			}
			
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
			
			bean.getList(criteria).forEach(to -> {
				Contract contract = (Contract) to;
				addPendingList(contract, AfiActionType.MA, contract.getStartDate());
			});
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	/**
	 * MB - Baja
	 */
	private void searchMB() {
		try {
			Criteria criteria = new Criteria();
			completeContractCriteria(criteria);
			
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
			if(getDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), getDateFrom());
			}
			if(getDateTo()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), getDateTo());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
			
			bean.getList(criteria).forEach(to -> {
				Contract contract = (Contract) to;
				addPendingList(contract, AfiActionType.MB, contract.getEndDate());
			});
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	/**
	 * MG - Cambio de grupo de cotización
	 */
	private void searchMG(){
		// TODO
	}
	
	/**
	 * MC - Cambio de contrato (tipo/coeficiente)
	 */
	private void searchMC(){
		// TODO
	}
	
	/**
	 * MT  - Cambio de ocupación
	 */
	private void searchMT(){
		// TODO
	}
	
	/**
	 * CCP - Cambio de Categoría Profesional
	 */
	private void searchCCP(){
		// TODO
	}
	
	/**
	 * MJR - Mecanización de Jornadas Reales (régimen 0163)
	 */
	private void searchMJR(){
		// TODO
	}
	
	/**
	 * CIT - Cierre de Períodos de Incapacidad Temporal
	 */
	private void searchCIT(){
		// TODO
	}
	
	/**
	 * MHU - Mecanización de HUelga
	 */
	private void searchMHU(){
		try {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.STRIKE_FACTOR.getName());
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION));
			if(getDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), getDateFrom());
			}
			if(getDateTo()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), getDateTo());
			}
			bean.getList(criteria).forEach(to -> {
				ContractData data = (ContractData) to;
				addPendingList(data.getContract(), AfiActionType.MHU, data.getStartDate());
			});
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	/**
	 * MIN - Mecanización de Inactividad
	 */
	private void searchMIN(){
		try {
			ArrayList<String> names = new ArrayList<String>();
			names.add(ContextVariable.ERE_FACTOR.getName());
			names.add("COEFICIENTE_ERE_FZA_EXONERADO");
			
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), names);
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION));
			if(getDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), getDateFrom());
			}
			if(getDateTo()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), getDateTo());
			}
			bean.getList(criteria).forEach(to -> {
				ContractData data = (ContractData) to;
				addPendingList(data.getContract(), AfiActionType.MIN, data.getStartDate());
			});
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

}
