package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractClausesController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ContrataController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setSepeStatus(ContractStatus.PENDING);
		contract.setSsStatus(ContractStatus.PENDING);
		contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		ContractUtils utils = ContractUtils.getInstance();
		utils.removeContractData((Contract) controller.getTo());
		
		removeContrataAttach(event);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setSelectedTab(null);
		controller.setEnterprise(((Contract) controller.getTo()).getWorkPlace().getEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		
		ContrataController contrataController = (ContrataController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) controller.getTo());

		CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
		certificadosController.initialize((Contract) controller.getTo());
		
		try {
			ContractUtils utils = ContractUtils.getInstance();
			utils.loadContractData((Contract) controller.getTo(), controller.getParams());
//			utils.searchAgreement();
		} catch (ManagerBeanException e) {
			String msg = "Error loading contract data";
			LOGGER.error(msg);
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
		try {
			ApplicationParameter defaultContractCode = params.getParameter(PayrollAppParamsController.DEFAULT_CONTRACT_CODE);
			if(defaultContractCode!=null && defaultContractCode.getValue()!=null){
				controller.getParams().setContractModelCode(ContractModelCode.valueOf(defaultContractCode.getValue()));
			}
			contract.setPerson((Person) BeanManager.getManagerBean(Person.class).createNewTo());
			controller.getParams().setCno((CNO) BeanManager.getManagerBean(CNO.class).createNewTo());
			controller.getParams().setTrainingCourse((TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).createNewTo());
			controller.setAgreement((Agreement) BeanManager.getManagerBean(Agreement.class).createNewTo());
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
		utils.insertContractData((Contract) controller.getTo(), controller.getParams());
		ContrataController contrataController = (ContrataController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) controller.getTo());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		ContractUtils utils = ContractUtils.getInstance();
		utils.updateContractData((Contract) controller.getTo(), controller.getParams());
		updateContrataData();
		updateAdditionalClauses();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setShowNewContractModal(false);
	}
	
	private void updateContrataData() {
		ContrataController contrataController = (ContrataController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		contrataController.onContrataAccept(null);
	}
	
	private void updateAdditionalClauses() {
		ContractClausesController controller = (ContractClausesController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CLAUSES_CONTROLLER);
		if( StringUtils.isNotBlank(controller.getAdditionalClauses()) ){
			controller.generateAdditionalClauseDocument();
		}
	}
	
	private void removeContrataAttach(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		Contract contract = (Contract) controller.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			Expression exp1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID);
			Expression exp2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_CONTRACT_RESPONSE);
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			for(ITransferObject to: bean.getList(criteria)){
				ContractAttachment attach = (ContractAttachment) to;
				bean.remove(attach);
				
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible eliminar los datos obtenidos del SEPE del contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
	
}
