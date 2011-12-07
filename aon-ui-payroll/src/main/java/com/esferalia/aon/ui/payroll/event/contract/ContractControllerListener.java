package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
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
		controller.setParams(null);
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
			bean = BeanManager.getManagerBean(CNO.class);
			controller.getParams().setCno((CNO) bean.createNewTo());
			bean = BeanManager.getManagerBean(Agreement.class);
			controller.setAgreement((Agreement) bean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Error on afterBeanCreated";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.getParams().setContractOption(null);
		controller.getParams().setContractType(null);
		controller.getParams().setContractCode(null);
		controller.getParams().setQuoteGroup(null);
		controller.getParams().setIrpf(null);
		controller.getParams().setContractDuration(null);
		controller.getParams().setContractWorkingDay(null);
		controller.getParams().setTc2Code(null);
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
		if(controller.isModalPanelVisible()){
			controller.setModalPanelVisible(false);
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_TREE_CONTROLLER);
			tree.loadTree();
		}
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
			if(controller.getParams().getIrpf()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.IRPF_PERCENT.getName() );
				data.setExpression(controller.getParams().getIrpf().toString());
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getQuoteGroup()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.QUOTE_GROUP.getName() );
				data.setExpression("\"" + controller.getParams().getQuoteGroup().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getOccupationType()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.OCCUPATION.getName() );
				data.setExpression("\"" + controller.getParams().getOccupationType().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la ocupacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getCno()!=null && controller.getParams().getCno().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.CNO.getName() );
				data.setExpression("\"" + controller.getParams().getCno().getCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo nacional de ocupaciones. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(!controller.getParams().isAgreementSalaryCheck() && controller.getParams().getGrossSalary()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( "SALARIO_BRUTO"  );
				data.setExpression( String.valueOf(CommonUtil.round(controller.getParams().getGrossSalary())) );
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el salario bruto. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getTc2Code()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + controller.getParams().getTc2Code().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
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
