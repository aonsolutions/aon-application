package com.esferalia.aon.ui.sepe.controller.batch;

import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
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
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContrataBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataListController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ContrataListController.class);

	private boolean searchPanelExpanded;
	
	private Person person;
	private Enterprise enterprise;
	private Date startDateFrom;
	private Date startDateTo;
	
	private BatchListCheckHandler checkHandler;
	
	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
	}
	
	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
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
	
	public String getRowContractCode(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract contract = (Contract) this.getModel().getRowData();
				if(contract!=null){
					String code = SEPEUtils.getInstance().getDataCurrentValue(contract, ContextVariable.TC2.getName());
					ContractCode contractCode = ContractCode.getContractCodeByValue(code);
					return contractCode!=null?code + " - " + contractCode.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()):"";
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	
	public void init(){
		try {
//			if(enterprise!=null && enterprise.getId()!=null){
//				setEnterprise( enterprise );
//			} else {
//				setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
//			}
			setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
			setPerson( (Person) BeanManager.getManagerBean(Person.class).createNewTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setStartDateFrom(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), CommonUtil.getDay(new Date())-10));
		setStartDateTo(new Date());
	}
	
	public void checkValidStartDate(){
		if( getStartDateFrom()!=null && getStartDateFrom().after(new Date()) ){
			setStartDateFrom(new Date());
			AonUtil.addErrorMessage("La fecha inicial no puede ser porterior al día de hoy.");
			throw new AbortProcessingException("La fecha inicial no puede ser porterior al día de hoy.");
		}
		if( getStartDateTo()!=null && getStartDateTo().after(new Date()) ){
			setStartDateTo(new Date());
			AonUtil.addErrorMessage("La fecha inicial no puede ser porterior al día de hoy.");
			throw new AbortProcessingException("La fecha inicial no puede ser porterior al día de hoy.");
		}
	}

	@Override
	public void onSearch(ActionEvent event) {
		checkValidStartDate();
		getCheckHandler().clearCheckedList();
		try {
			this.setCriteria( new Criteria() );
			SEPEUtils utils = SEPEUtils.getInstance();
//			clearCriteria();
			if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
//			ContrataBatchController controller = (ContrataBatchController) FormUtil.getController(ISepeConstants.CONTRATA_BATCH_CONTROLLER_NAME);
//			ContrataBatch batch = (ContrataBatch) controller.getTo();
//			getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_START_DATE), batch.getDate());
//			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_SEPE_STATUS),ContractStatus.PENDING);
			if ((getPerson() != null) && (getPerson().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());			
			}
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
			if(getStartDateFrom()!=null){
				getCriteria().addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_START_DATE), getStartDateFrom());
			}
			if(getStartDateTo()!=null){
				getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_START_DATE), getStartDateTo());
			} else {
				getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_START_DATE), new Date());
			}
			
			LinesController controller = (LinesController) AonUtil.getRegisteredBean(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
			// ******************************
			// FIXME: this code is temporary, while the contract contrata status is not defined
			if(controller.getModel()!=null){
				for(ITransferObject to: (List<ITransferObject>)controller.getModel().getWrappedData()){
					ContrataBatchDetail detail = (ContrataBatchDetail) to;
					getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.CONTRACT_ID), detail.getContract().getId());
				}
			}
			
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

}
