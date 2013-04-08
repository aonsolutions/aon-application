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
import com.code.aon.config.ApplicationParameter;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.ui.payroll.controller.AbstractVariableHandler.VariableData;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setStatus(ContractStatus.PENDING);
		if(controller.getParams().getContractModelCode()!=null){
			contract.setModel(controller.getParams().getContractModelCode().getModel());
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeChildData(event);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setEnterprise(((Contract) controller.getTo()).getWorkPlace().getEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		controller.onShowVariables(null);
		searchAgreement();
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PayrollAppParamsController params = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		PayrollUtils utils = new PayrollUtils();
		controller.setEnterprise(utils.getCurrentDomainEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		contract.setStartDate(new Date());
		contract.setSeniorityDate(contract.getStartDate());
		try {
			ApplicationParameter defaultContractCode = params.getParameter(PayrollAppParamsController.DEFAULT_CONTRACT_CODE);
			if(defaultContractCode!=null && defaultContractCode.getValue()!=null){
				controller.getParams().setContractModelCode(ContractModelCode.valueOf(defaultContractCode.getValue()));
			}
			IManagerBean bean = BeanManager.getManagerBean(Person.class);
			contract.setPerson((Person) bean.createNewTo());
			bean = BeanManager.getManagerBean(CNO.class);
			controller.getParams().setCno((CNO) bean.createNewTo());
			bean = BeanManager.getManagerBean(Agreement.class);
			controller.setAgreement((Agreement) bean.createNewTo());
			if( controller.getParams().getContractModelCode() != null
					&& controller.getParams().getContractModelCode().getCode() == ContractCode.C421 
					&& params.getDefaultTrainingCenter()!=null && params.getDefaultTrainingCenter().getId()!=null){
				controller.getParams().setTrainingCenter(params.getDefaultTrainingCenter());
			} else {
				bean = BeanManager.getManagerBean(TrainingCenter.class);
				controller.getParams().setTrainingCenter((TrainingCenter) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error on afterBeanCreated";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		saveContractData();
		ContractController controller = (ContractController) this.getController();
		controller.getHandler().initializeVariables(null);
		if(controller.isShowNewContractModal()){
			controller.setShowNewContractModal(false);
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_TREE_CONTROLLER);
			tree.loadTree();
		}
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setShowNewContractModal(false);
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
//		try {
//			if(controller.getParams().getTc2Code()!=null){
//				data = new ContractData();
//				data.setContract(contract);
//				data.setStartDate(contract.getStartDate());
//				data.setName( ContextVariable.TC2.getName() );
//				data.setExpression("\"" + controller.getParams().getTc2Code().getValue() + "\"");
//				bean.insert(data);
//			}
//		} catch (ManagerBeanException e) {
//			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
//			LOGGER.error(msg);
//		}
		try {
			if(controller.getParams().getContractModelCode()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + controller.getParams().getContractModelCode().getCode().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getSubsidized()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.SUBSIDIZED.getName() );
				data.setExpression(controller.getParams().getSubsidized()?"true":"false");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar si el contrato se acoge a la reduccion de cuotas a la S.S. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getTrainingCenter()!=null && controller.getParams().getTrainingCenter().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setName( ContextVariable.TRAINING_CENTER.getName() );
				data.setExpression("\"" + controller.getParams().getTrainingCenter().getId() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
	}
	
	private void removeChildData(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		List list = (List) controller.getHandler().getVariablesModel().getWrappedData();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			for(Object o: list){
				ContractData data = (ContractData) ((VariableData) o).getVariableData();
				bean.remove(data);
				
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible eliminar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
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
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> list = bean.getList(criteria);
				Agreement agreement = null;
				if(!list.isEmpty()){
					agreement = (list.get(0)==null)?null:((PayrollWorkPlace)list.get(0)).getAgreement();
				}
				if(agreement!=null){
					controller.setAgreement(agreement);
				} else {
					IManagerBean aBean = BeanManager.getManagerBean(Agreement.class);
					controller.setAgreement((Agreement) aBean.createNewTo());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se define ningun convenio
			String msg = "Error al buscar el convenio. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
	}
	
}
