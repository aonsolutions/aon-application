package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.person.Person;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractClause;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractSsStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.ui.payroll.controller.ContractInfoController;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractClauseController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.controller.ContrataContratosController;
import com.esferalia.aon.ui.sepe.controller.ContrataProrrogasController;
import com.esferalia.aon.ui.sepe.controller.ContrataTransformacionesController;
import com.esferalia.aon.ui.sepe.controller.IContrataController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setSelectedTab(null);
		Contract contract = (Contract) controller.getTo();
		if(controller.getParams().isRetaQuote()){
			contract.setRegimeType(SSRegimeType.SELF_EMPLOYED);
			contract.setEnterpriseCCC(null);
			contract.setActivity(null);
		} else if(controller.getParams().getContractCode().equals("000") && contract.getEnterpriseCCC().getType()!=CCCType.FELLOWS){
			throw new AbortProcessingException("No se ha podido dar de alta el contrato. Revise el tipo de contrato y la cuenta de cotizacion.");
		} else { 
			contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		if(controller.getParams().isRetaQuote()){
			contract.setRegimeType(SSRegimeType.SELF_EMPLOYED);
			contract.setEnterpriseCCC(null);
			contract.setActivity(null);
		} else {
			contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		ContractUtils utils = ContractUtils.getInstance();
		utils.removeContractData((Contract) controller.getTo(), controller.getParams());
		utils.removeContractInfo((Contract) controller.getTo(), controller.getParams());
		utils.removeContractLines(event);
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setSelectedTab(null);
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
		controller.setContractUtils(null);
		controller.setShowWorkdayHoursWindow(false);
		controller.setWorkdayManager(null);
		Contract contract = (Contract) controller.getTo();
		
		try {
			if(contract.getAgreementLevel()!=null 
					&& contract.getAgreementLevel().getAgreement()!=null 
					&& contract.getAgreementLevel().getAgreement().getId()!=null){
				controller.setAgreement(contract.getAgreementLevel().getAgreement());
				controller.setAgreementLevelCategoryOnly(
						controller.getAgreementLevelCategory(
								contract.getAgreementLevel(), 
								contract.getCategoryDescription()));
			} else {
				controller.setAgreement((Agreement) BeanManager.getManagerBean(Agreement.class).createNewTo());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error loading contract agreement";
			LOGGER.error(msg);
		}
		
		try {
			controller.getContractUtils().loadContractData((Contract) controller.getTo(), controller.getParams());
			controller.getContractUtils().loadContractInfo((Contract) controller.getTo(), controller.getParams());
			controller.getContractUtils().loadContractBonuses((Contract) controller.getTo(), controller.getParams());
		} catch (ManagerBeanException e) {
			String msg = "Error loading contract data";
			LOGGER.error(msg);
		}
		
		if(controller.getParams().getSpecialQuote()==null
			&& !controller.isContractInternship(contract)
			&& !controller.isContractRetaQuote(contract)
			&& !controller.isCooperativePartnerQuote(contract)){
			IContrataController contrataController = null;
			if(controller.isTransformedContract()){
				contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
			} else if(!controller.isTransformedContract() && controller.isExtendedContract()){
				contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
			} else {
				contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
			}
			
			contrataController.initialize((Contract) this.getController().getTo());
		}
		
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PayrollAppParamsController params = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		PayrollUtils utils = PayrollUtils.getInstance();
		controller.setEnterprise(utils.getCurrentDomainEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		contract.setStartDate(new Date());
		contract.setSeniorityDate(contract.getStartDate());
		controller.setShowWorkdayHoursWindow(false);
		controller.setWorkdayManager(null);
		try {
			ApplicationParameter defaultContractCode = params.getParameter(AppParam.PAY_default_contractCode_PAY.getValue());
			if(defaultContractCode!=null && defaultContractCode.getValue()!=null){
				controller.getParams().setContractModelCode(ContractModelCode.valueOf(defaultContractCode.getValue()));
			}
			contract.setPerson((Person) BeanManager.getManagerBean(Person.class).createNewTo());
			controller.getParams().setCno((CNO) BeanManager.getManagerBean(CNO.class).createNewTo());
			controller.getParams().setTrainingCourse((TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).createNewTo());
			controller.setAgreement((Agreement) BeanManager.getManagerBean(Agreement.class).createNewTo());
			controller.setAgreementLevelCategory(null);
		} catch (ManagerBeanException e) {
			String msg = "Error on afterBeanCreated";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		ContractUtils utils = ContractUtils.getInstance();
		utils.insertContractInfo((Contract) controller.getTo(), controller.getParams());
		try {
			if(!controller.getParams().isRetaQuote()){
				utils.insertContractData((Contract) controller.getTo(), controller.getParams());
				importContractClauses((Contract)controller.getTo());
				utils.insertContractInfo((Contract) controller.getTo(), ContractVariable.SEPE_CONTRACT.getValue(), ContractSepeStatus.PENDING.getValue());
				utils.insertContractInfo((Contract) controller.getTo(), ContractVariable.SS_MA.getValue(), ContractSsStatus.PENDING.getValue());
				ContractInfoController infoController = (ContractInfoController) FormUtil.getController("contractDocumentInfo");
				infoController.loadContractFields((Contract) this.getController().getTo(), true);
				updateContractDocumentFields();
			} else {
				utils.insertRetaContractData((Contract) controller.getTo(), controller.getParams());
			}
			utils.loadContractData((Contract) controller.getTo(), controller.getParams());
			utils.loadContractInfo((Contract) controller.getTo(), controller.getParams());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error saving contract status");
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		ContractUtils utils = ContractUtils.getInstance();
		if(!controller.getParams().isRetaQuote()){
			utils.updateContractData((Contract) controller.getTo(), controller.getParams());
			Contract contract = (Contract) controller.getTo();
			if(controller.getParams().getSpecialQuote()==null
					&& !controller.isContractInternship(contract)
					&& !controller.isContractRetaQuote(contract)
					&& !controller.isCooperativePartnerQuote(contract)){
				
				updateContrataData();
			}
			updateContractDocumentFields();
		} else {
			utils.updateRetaContractData((Contract) controller.getTo(), controller.getParams());
		}
		utils.updateContractInfo((Contract) controller.getTo(), controller.getParams());
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setShowNewContractModal(false);
	}
	
	private void updateContrataData() {
		IContrataController contrataController = null;
		ContractController controller = (ContractController) this.getController();
		
		if(controller.isTransformedContract()){
			contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
		} else if(!controller.isTransformedContract() && controller.isExtendedContract()){
			contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		} else {
			contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		}
		
		contrataController.onContrataAccept(null);
	}
	
	private void updateContractDocumentFields() {
		ContractInfoController controller = (ContractInfoController) AonUtil.getRegisteredBean("contractDocumentInfo");
		controller.saveContractFields();
	}
	
	private void importContractClauses(Contract contract) {
		try {
			ContractClauseController controller = (ContractClauseController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CLAUSE_CONTROLLER);
			List<ITransferObject> list = controller.obtainAvailableClausesModel(true);
			IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
			for(ITransferObject to: list){
				ContractClause enterpriseClause = (ContractClause) to;
				ContractClause clause = new ContractClause();
				clause.setContract(contract);
				clause.setDescription(enterpriseClause.getDescription());
				clause.setLine(enterpriseClause.getLine());
				clause.setName(enterpriseClause.getName());
				bean.insert(clause);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido importar las cláusulas generales.");
		}
	}
	
}
