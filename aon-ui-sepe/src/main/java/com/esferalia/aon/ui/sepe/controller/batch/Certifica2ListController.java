package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class Certifica2ListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2ListController.class);

	private boolean searchPanelExpanded;
	
	private Enterprise enterprise;
	private Person person;
	private Certifica2BatchListCheckHandler checkHandler;
	private Date endDateFrom;
	private Date endDateTo;
	
	private Map<Integer, RemesableContract> remesableContracts = new HashMap<Integer, RemesableContract>();
	
	private SuspensionCause suspensionCauseForAll;
	

	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public Map<Integer, RemesableContract> getRemesableContracts() {
		return remesableContracts;
	}

	public SuspensionCause getRowSuspensionCause() {
		try {
			(((Contract)getModel().getRowData())).getId();
			if( remesableContracts.containsKey(getRowContract().getId()) ){
				return remesableContracts.get( getRowContract().getId() ).getSuspensionCause();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}

	public void setRowSuspensionCause(SuspensionCause suspensionCause) {
		try {
			if ( suspensionCause!=null ) {
				if ( !remesableContracts.containsKey(getRowContract().getId()) ) {
					RemesableContract remesable = new RemesableContract();
					remesable.setContract(getRowContract());
					remesable.setSuspensionCause(suspensionCause);
					remesableContracts.put( getRowContract().getId(), remesable );
				}
			} else {
				if ( remesableContracts.containsKey(getRowContract().getId()) ) {
					remesableContracts.remove( getRowContract().getId() );
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on setRowRemesableContract: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	private Contract getRowContract() throws ManagerBeanException{
		return (Contract) getModel().getRowData();
	}
	
	public SuspensionCause getSuspensionCauseForAll() {
		return suspensionCauseForAll;
	}

	public void setSuspensionCauseForAll(SuspensionCause suspensionCauseForAll) {
		this.suspensionCauseForAll = suspensionCauseForAll;
	}
	
	public Certifica2BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new Certifica2BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(Certifica2BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public void init(Enterprise enterprise){
		try {
			if(enterprise!=null && enterprise.getId()!=null){
				setEnterprise( enterprise );
			} else {
				setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
			}
			setPerson( (Person) BeanManager.getManagerBean(Person.class).createNewTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setEndDateFrom(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), CommonUtil.getDay(new Date())-10));
		setEndDateTo(new Date());
	}
	
	public void checkValidEndDate(){
		if( (getEndDateFrom()!=null && getEndDateFrom().after(new Date()))
				|| (getEndDateTo()!=null && getEndDateTo().after(new Date()))){
			AonUtil.addErrorMessage("La fecha de fin no puede ser porterior al día de hoy.");
			throw new AbortProcessingException("La fecha de fin no puede ser porterior al día de hoy.");
		}
	}

	@Override
	public void initializeModel() {
		if( !DomainManager.isDomainManagementAvailable() || (getEnterprise()!=null && getEnterprise().getId()!=null) ){
			super.initializeModel();
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		checkValidEndDate();
		getCheckHandler().clearCheckedList();
		remesableContracts = new HashMap<Integer, RemesableContract>();
		setSuspensionCauseForAll(null);
		try {
			this.setCriteria( new Criteria() );
			SEPEUtils utils = new SEPEUtils();
			if(getPerson()!=null && getPerson().getId()!=null){
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());
			}
			getCriteria().addNotNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE));
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
			if(getEndDateFrom()!=null){
				getCriteria().addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateFrom());
			}
			if(getEndDateTo()!=null){
				getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateTo());
			} else {
				getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
			}
			LinesController controller = (LinesController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
			// ******************************
			// FIXME: this code is temporary, while the contract certificate status is not defined
			for(ITransferObject to: (List<ITransferObject>)controller.getModel().getWrappedData()){
				Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
				getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.CONTRACT_ID), detail.getContract().getId());
			}
			// ****************
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_NAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_END_DATE), false);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}
	
	public void onApplyAllSuspensionCause(ActionEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
	}
	
	public void onApplyAllSuspensionCause(ValueChangeEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
	}
	
	private void applyAllSuspensionCause(SuspensionCause suspensionCause) {
		for(Object o: getCheckHandler().getCheckedList()){
			Contract contract = (Contract) o;
			if ( suspensionCause!=null ) {
				if ( !remesableContracts.containsKey(contract.getId()) ) {
					RemesableContract remesable = new RemesableContract();
					remesable.setContract(contract);
					remesable.setSuspensionCause(suspensionCause);
					remesableContracts.put( contract.getId(), remesable );
				}
				if ( remesableContracts.containsKey(contract.getId()) ) {
					remesableContracts.get( contract.getId() ).setSuspensionCause(suspensionCause);
				}
			} 
		}
	}
	
	public class RemesableContract implements Serializable {
		
		private static final long serialVersionUID = -6700232454851910313L;

		private Contract contract;
		private SuspensionCause suspensionCause;
		
		public Contract getContract() {
			return contract;
		}
		public void setContract(Contract contract) {
			this.contract = contract;
		}
		public SuspensionCause getSuspensionCause() {
			return suspensionCause;
		}
		public void setSuspensionCause(SuspensionCause suspensionCause) {
			this.suspensionCause = suspensionCause;
		}
	}

}
