package com.esferalia.aon.ui.payroll.event.contract;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractAttachController;
import com.esferalia.aon.ui.payroll.controller.wizard.ContractGenerationWizard;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;

public class ContractGenerationControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationControllerListener.class.getName());
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractGenerationWizard controller = (ContractGenerationWizard) this.getController();
		controller.setContract((Contract) controller.getTo());
		controller.setEnterprise(controller.getContract().getWorkPlace().getEnterprise());
		controller.setParams(new ContrataParams());
		controller.getContractBuilder().setContractFields(null);
		if(getContractDataMap().get(ContractVariables.TC2.getName())!=null){
			controller.setCode(ContractCode.getContractCodeByValue(getContractDataMap().get(ContractVariables.TC2.getName())));
			// TODO EN DESARROLLO, solo se contempla cuando el TC2 es 100
			if(controller.getCode()==ContractCode.C100){
				for (ContractOption contractOption:ContractOption.values()) {
					for (ContractType contractType:contractOption.getTypes()) {
						for (ContractCode c : contractType.getCodes()) {
							if(contractType==ContractType.PE170){
								controller.setContractType(contractType);
							}
							if(contractOption==ContractOption.INDEFINITE){
								controller.setContractOption(contractOption);
							}
						}
					}
				}
			} else {
				controller.setCode(null);
				controller.setContractType(null);
				controller.setContractOption(null);
			}
		}
		try {
			controller.readXml();
		} catch (JAXBException e) {
			String msg = "Error al seleccionar el contrato";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractGenerationWizard controller = (ContractGenerationWizard) this.getController();
		ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
		try {
			updateContractData();
			controller.generateXml();
			ContractAttachment attach = new ContractAttachment();
			attach = controller.getContrataAttach();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setAttachDate(new Date());
			bean.insertOrUpdate(attach);
			attachController.initializeModel();
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractGenerationWizard controller = (ContractGenerationWizard) this.getController();
		Contract contract = (Contract) controller.getTo();
		if(contract.getSeniorityDate()==null){
			contract.setSeniorityDate(contract.getStartDate());
		}
		controller.setContract((Contract) controller.getTo());
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		ContractGenerationWizard controller = (ContractGenerationWizard) this.getController();
		controller.setEnterprise(null);
	}
	
	private void updateContractData() throws ManagerBeanException {
		ContractGenerationWizard controller = (ContractGenerationWizard) this.getController();
		if(controller.getCode()!=ContractCode.C100){
			String msg = "Implementación hecha sólo para contratos de código 100";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ContractData data = getContractData();
		Contract contract = (Contract) controller.getTo();
		data.setContract(contract);
		data.setStartDate(contract.getStartDate());
		data.setEndDate(contract.getEndDate());
		data.setName(ContractVariables.TC2.getName());
		data.setExpression(controller.getCode().getValue());
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		bean.insertOrUpdate(data);
	}
	
	private ContractData getContractData(){
		Contract contract = (Contract)this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), ContractVariables.TC2.getName());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			if(contract.getEndDate()!=null){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
			} else {
				criteria.addNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
			}
			List<ITransferObject>  list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractData) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve una nueva instancia
		}
		return new ContractData();
	}
	
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap() {
		if(contractDataMap==null){
			contractDataMap = new HashMap<String, String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), ((Contract)this.getController().getTo()).getId());
				criteria.addNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
				for(ITransferObject to: bean.getList(criteria)){
					ContractData data = (ContractData) to;
					contractDataMap.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			} catch (ManagerBeanException e) {
				// NADA, se devuelve un mapa vacio
			}
		}
		return contractDataMap;
	}
	
}
