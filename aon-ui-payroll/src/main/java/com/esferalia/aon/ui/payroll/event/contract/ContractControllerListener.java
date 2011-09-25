package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.searchContractAttachDocument((Contract) controller.getTo());
		controller.setEnterprise(((Contract) controller.getTo()).getWorkPlace().getEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		searchAgreement();
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo(); 
		try {
			IManagerBean bean = BeanManager.getManagerBean(Person.class);
			contract.setPerson((Person) bean.createNewTo());
			bean = BeanManager.getManagerBean(Enterprise.class);
			controller.setEnterprise((Enterprise) bean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Error on afterBeanCreated";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setContractOption(null);
		controller.setContractType(null);
		controller.setContractCode(null);
		controller.setQuoteGroup(null);
		controller.setIrpf(null);
		controller.setContractDuration(null);
		controller.setContractWorkingDay(null);
		controller.setTc2Code(null);
		contract.setStartDate(new Date());
		contract.setSeniorityDate(contract.getStartDate());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setStatus(ContractStatus.PENDING);
		if(controller.getAonFile()!=null){
			contract.setDocument(controller.getAonFile().getData());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		saveContractData();
		ContractController controller = (ContractController) this.getController();
		controller.setModalPanelVisible(false);
		EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_TREE_CONTROLLER);
		tree.loadTree();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setModalPanelVisible(false);
	}
	
	private void saveContractData() throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
		try {
			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.IRPF_PERCENT.getName() );
			data.setExpression(controller.getIrpf().toString());
			bean.insert(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.QUOTE_GROUP.getName() );
			data.setExpression("\"" + controller.getQuoteGroup().getValue() + "\"");
			bean.insert(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.TC2.getName() );
			data.setExpression("\"" + controller.getTc2Code().getValue() + "\"");
			bean.insert(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
//			data = new ContractData();
//			data.setContract(contract);
//			data.setStartDate(contract.getStartDate());
//			data.setName( ContractVariables.FULL_TIME.getName() );
//			data.setExpression("\"" + (controller.getContractWorkingDay()==ContractWorkingDay.FULL_TIME?true:false) + "\"");
//			bean.insert(data);
		
//			data = new ContractData();
//			data.setContract(contract);
//			data.setStartDate(contract.getStartDate());
//			data.setName( ContractVariables.INDEFINITE.getName() );
//			data.setExpression("\"" + (controller.getContractDuration()==ContractDuration.UNSPECIFIED?true:false) + "\"");
//			bean.insert(data);
	}
	
	private void searchAgreement() {
		try {
			ContractController controller = (ContractController) this.getController();
			Contract contract = (Contract) controller.getTo();
			if(contract.getAgreementLevelCategory()!=null 
					&& contract.getAgreementLevelCategory().getLevel()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement().getId()!=null){
				controller.setAgreement(contract.getAgreementLevelCategory().getLevel().getAgreement());
			} else {
				IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> list = bean.getList(criteria);
				Agreement agreement = ((PayrollWorkPlace)list.get(0)==null)?null:((PayrollWorkPlace)list.get(0)).getAgreement();
				if(agreement!=null){
					controller.setAgreement(agreement);
				} else {
					IManagerBean aBean = BeanManager.getManagerBean(Agreement.class);
					controller.setAgreement((Agreement) aBean.createNewTo());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se define ningun convenio
		}
	}
	
}
