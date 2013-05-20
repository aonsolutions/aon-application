package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractListController.class);

	private Person person;
	private Enterprise enterprise;
	private BatchListCheckHandler checkHandler;
	
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
		try {
			if(person == null){
				IManagerBean bean = BeanManager.getManagerBean(Person.class);
				person = (Person) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getPerson: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Enterprise getEnterprise() {
		try {
			if(enterprise == null){
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				enterprise = (Enterprise) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getEnterprise: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	@Override
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		try {
			PayrollUtils utils = new PayrollUtils();
			
			clearCriteria();
			if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
			
//			ContractBatchController controller = (ContractBatchController) FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_CONTROLLER_NAME);
//			ContractBatch batch = (ContractBatch) controller.getTo();
//			getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DATE), batch.getDate());
//			getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.CONTRACT_STATUS),ContractStatus.PROCESSED);
			if ((getPerson() != null) && (getPerson().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());			
			}
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}

}
