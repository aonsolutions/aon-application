package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ContractListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractListController.class);

	private boolean searchPanelExpanded;
	
	private Person person;
	private Enterprise enterprise;
	
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
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		try {
			this.setCriteria( new Criteria() );
			SEPEUtils utils = SEPEUtils.getInstance();
			
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
			
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_NAME));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}

}
