package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

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
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class Certifica2ListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2ListController.class);

	private Enterprise enterprise;
	private Person person;
	private BatchListCheckHandler checkHandler;
	
	private Map<Integer, RemesableContract> remesableContracts = new HashMap<Integer, RemesableContract>();
	
	private SuspensionCause suspensionCauseForAll;
	
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
	
	public BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
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

	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		remesableContracts = new HashMap<Integer, RemesableContract>();
		setSuspensionCauseForAll(null);
		try {
			PayrollUtils utils = new PayrollUtils();
			
			clearCriteria();
			if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_DOMAIN), utils.getCurrentChildDomainIds());
			}
			
			getCriteria().addNotNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE));
			getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.CONTRACT_STATUS), ContractStatus.BATCHED);
//			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_STATUS), ContractStatus.PENDING);
//			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), ((Certifica2Batch)controller.getTo()).getEnterprise().getId());
			getCriteria().addOrder(getFieldName(IEntityAlias.CONTRACT_END_DATE), false);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}
	
	public boolean isAnyEmpleadoSelected(){
		return !getCheckHandler().getCheckedList().isEmpty();
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
