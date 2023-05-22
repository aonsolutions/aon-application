package com.esferalia.aon.ui.sepe.controller.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import jakarta.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeCollectionsController;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class Certifica2ListController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2ListController.class);

	private boolean searchPanelExpanded;	
	private Enterprise enterprise;
	private Person person;
	private Date endDateFrom;
	private Date endDateTo;
	
	private boolean showSuspensionCauseWindow;
	private boolean rowSuspensionCauseSelected;
	private SerializableListDataModel suspensionCauseModel;
	private String ereNumberForAll;
	private SuspensionCause suspensionCauseForAll;
	private Contract selectedRowContract;

	private Certifica2BatchListCheckHandler checkHandler;
	
	private Map<Integer, Certifica2BatchDetail> batchDetailList = new HashMap<Integer, Certifica2BatchDetail>();

	
	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
	}

	public boolean isShowSuspensionCauseWindow() {
		return showSuspensionCauseWindow;
	}

	public void setShowSuspensionCauseWindow(boolean showSuspensionCauseWindow) {
		this.showSuspensionCauseWindow = showSuspensionCauseWindow;
	}

	public boolean isRowSuspensionCauseSelected() {
		return rowSuspensionCauseSelected;
	}

	public void setRowSuspensionCauseSelected(boolean rowSuspensionCauseSelected) {
		this.rowSuspensionCauseSelected = rowSuspensionCauseSelected;
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

	public Contract getSelectedRowContract() {
		return selectedRowContract;
	}

	public void setSelectedRowContract(Contract selectedRowContract) {
		this.selectedRowContract = selectedRowContract;
	}

	public String getEreNumberForAll() {
		return ereNumberForAll;
	}

	public void setEreNumberForAll(String ereNumberForAll) {
		this.ereNumberForAll = ereNumberForAll;
	}

	public SuspensionCause getSuspensionCauseForAll() {
		return suspensionCauseForAll;
	}

	public void setSuspensionCauseForAll(SuspensionCause suspensionCauseForAll) {
		this.suspensionCauseForAll = suspensionCauseForAll;
	}
	
	public String getSuspensionCauseForAllValue() {
		return suspensionCauseForAll!=null?suspensionCauseForAll.getValue():null;
	}

	public void setSuspensionCauseForAllValue(String value) {
		setSuspensionCauseForAll(null);
		if(StringUtils.isNotBlank(value) && NumberUtils.isNumber(value)){
			Integer ordinal = Integer.parseInt(value);
			if(ordinal <= SuspensionCause.values().length){
				setSuspensionCauseForAll(SuspensionCause.valueOf("C"+ordinal));
			}
		}
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
	
	public Map<Integer, Certifica2BatchDetail> getBatchDetailList() {
		return batchDetailList;
	}

	public String getRowSuspensionCauseValue() {
		SuspensionCause row = getRowSuspensionCause();
		return row!=null?row.getValue():null;
	}

	public void setRowSuspensionCauseValue(String value) {
		if(StringUtils.isNotBlank(value) && NumberUtils.isNumber(value)){
			Integer ordinal = Integer.parseInt(value);
			if(ordinal <= SuspensionCause.values().length){				
				setRowSuspensionCause(SuspensionCause.valueOf("C"+ordinal));
			}
		}
	}
	
	public SuspensionCause getRowSuspensionCause() {
		try {
			(((Contract)getModel().getRowData())).getId();
			if( batchDetailList.containsKey(getRowContract().getId()) ){
				return batchDetailList.get( getRowContract().getId() ).getSuspensionCause();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}

	public boolean isRowEreSuspensionCause() {
		SuspensionCause suspensionCause = getRowSuspensionCause();
		return suspensionCause == SuspensionCause.C16 
				|| suspensionCause == SuspensionCause.C17 
				|| suspensionCause == SuspensionCause.C18 ;
	}

	public void setRowSuspensionCause(SuspensionCause suspensionCause) {
		try {
			if ( suspensionCause!=null ) {
				if ( !batchDetailList.containsKey(getRowContract().getId()) ) {
					Certifica2BatchDetail detail = new Certifica2BatchDetail();
					detail.setContract(getRowContract());
					detail.setSuspensionCause(suspensionCause);
					batchDetailList.put( getRowContract().getId(), detail );
				} else {
					batchDetailList.get( getRowContract().getId() ).setSuspensionCause(suspensionCause);
				}
			} else {
				if ( batchDetailList.containsKey(getRowContract().getId()) ) {
					batchDetailList.get( getRowContract().getId() ).setSuspensionCause(null);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on setRowRemesableContract: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public String getRowEreNumber() {
		try {
			(((Contract)getModel().getRowData())).getId();
			if( batchDetailList.containsKey(getRowContract().getId()) ){
				return batchDetailList.get( getRowContract().getId() ).getEreNumber();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}
	
	public void setRowEreNumber(String ereNumber) {
		try {
			if ( ereNumber !=null ) {
				if ( !batchDetailList.containsKey(getRowContract().getId()) ) {
					Certifica2BatchDetail detail = new Certifica2BatchDetail();
					detail.setContract(getRowContract());
					detail.setEreNumber(ereNumber);
					batchDetailList.put( getRowContract().getId(), detail );
				} else {
					batchDetailList.get( getRowContract().getId() ).setEreNumber(ereNumber);
				}
			} else {
				if ( batchDetailList.containsKey(getRowContract().getId()) ) {
					batchDetailList.get( getRowContract().getId() ).setEreNumber(null);
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
		//setEndDateTo(new Date());
		setSuspensionCauseForAll(null);
		setEreNumberForAll(null);
	}
	
	public void checkValidEndDate(){
		if( getEndDateFrom()!=null && getEndDateFrom().after(new Date()) ){
			setEndDateFrom(new Date());
			AonUtil.addErrorMessage("La fecha de fin no puede ser porterior al día de hoy.");
			throw new AbortProcessingException("La fecha de fin no puede ser porterior al día de hoy.");
		}
		if( getEndDateTo()!=null && getEndDateTo().after(new Date()) ){
			setEndDateTo(new Date());
			AonUtil.addErrorMessage("La fecha de fin no puede ser porterior al día de hoy.");
			throw new AbortProcessingException("La fecha de fin no puede ser porterior al día de hoy.");
		}
	}
	
	public boolean isRowDisabled(){
		try {
			if(this.getModel().isRowAvailable()){
				return isRowDisabled((Contract) this.getModel().getRowData());
			}
		} catch (ManagerBeanException e) {
			// nada
		}
		return false;
	}
	
	public boolean isRowDisabled(Contract contract) throws ManagerBeanException{
		LinesController controller = (LinesController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		// TODO: obtain complete list, not only list of the PageDataModel
		for(ITransferObject to: (List<ITransferObject>)controller.getWrappedList()){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			if(detail.getContract().getPerson().getId().equals(contract.getPerson().getId())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void initializeModel() {
		BasicController controller = (BasicController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_BATCH_CONTROLLER_NAME);
		if( !DomainManager.isDomainManagementAvailable() 
				|| (getEnterprise()!=null && getEnterprise().getId()!=null)
				|| controller.isNevv()){
			super.initializeModel();
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		checkValidEndDate();
		getCheckHandler().clearCheckedList();
		setSuspensionCauseForAll(null);
		setEreNumberForAll(null);
		try {
			this.setCriteria( new Criteria() );
			SEPEUtils utils = SEPEUtils.getInstance();
			if(getPerson()!=null && getPerson().getId()!=null){
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());
			}
			//getCriteria().addNotNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE));
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
			if(getEndDateFrom()!=null){
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(
				ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateFrom())
				, ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE))));
			}
			if(getEndDateTo()!=null){
				getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateTo());
				
			} else {				
				//getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
			}
			LinesController controller = (LinesController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
			// ******************************
			// FIXME: this code is temporary, while the contract certificate status is not defined
			for(ITransferObject to: (List<ITransferObject>)controller.getWrappedList()){
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
	
	
	
	public SerializableListDataModel getSuspensionCauseModel(){
		if(suspensionCauseModel==null){
			SepeCollectionsController controller = (SepeCollectionsController) AonUtil.getRegisteredBean(ISepeConstants.COLLECTIONS_CONTROLLER_NAME);
			suspensionCauseModel = new SerializableListDataModel(controller.getSuspensionCauses());
		}
		return suspensionCauseModel;
	}
	
	public void onSelectSuspensionCauseForAll(ActionEvent event) {
		if(suspensionCauseModel.isRowAvailable()){
			setSuspensionCauseForAll((SuspensionCause) ((SelectItem)suspensionCauseModel.getRowData()).getValue());
		}
		setShowSuspensionCauseWindow(false);
	}
	
	public void onSelectRowSuspensionCause(ActionEvent event) {
		if(suspensionCauseModel.isRowAvailable()
				&& getSelectedRowContract()!=null 
				&& getSelectedRowContract().getId()!=null){
			SuspensionCause suspensionCause = (SuspensionCause) ((SelectItem)suspensionCauseModel.getRowData()).getValue();
			if ( suspensionCause!=null ) {
				if ( !batchDetailList.containsKey(getSelectedRowContract().getId()) ) {
					Certifica2BatchDetail detail = new Certifica2BatchDetail();
					detail.setContract(getSelectedRowContract());
					detail.setSuspensionCause(suspensionCause);
					batchDetailList.put( getSelectedRowContract().getId(), detail );
				} else {
					batchDetailList.get( getSelectedRowContract().getId() ).setSuspensionCause(suspensionCause);
				}
			} else {
				if ( batchDetailList.containsKey(getSelectedRowContract().getId()) ) {
					batchDetailList.get( getSelectedRowContract().getId() ).setSuspensionCause(null);
				}
			}			
		}
		setShowSuspensionCauseWindow(false);
	}
	
	public void onApplyAllSuspensionCause(ActionEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
		applyAllEreNumber(getEreNumberForAll());
	}
	
	public void onApplyAllSuspensionCause(ValueChangeEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
		applyAllEreNumber(getEreNumberForAll());
	}
	
	private void applyAllSuspensionCause(SuspensionCause suspensionCause) {
		for(Integer id: batchDetailList.keySet()){
			((Certifica2BatchDetail)batchDetailList.get(id)).setSuspensionCause(suspensionCause);
		}
	}
	
	private void applyAllEreNumber(String ereNumber) {
		for(Integer id: batchDetailList.keySet()){
			((Certifica2BatchDetail)batchDetailList.get(id)).setEreNumber(ereNumber);
		}
	}

	public void checkAllSuspensionCauses() {
        boolean incomplete = false;
		for(Integer id: batchDetailList.keySet()){
			if( ((Certifica2BatchDetail)batchDetailList.get(id)).getSuspensionCause()==null ){
	    		incomplete = true;
	    	}
		}
		if(incomplete){
			String message = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}
	
}
