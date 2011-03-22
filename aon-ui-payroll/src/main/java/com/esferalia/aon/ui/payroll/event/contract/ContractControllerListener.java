package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo(); 
		controller.setEnterprise(new Enterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setContractOption(null);
		controller.setContractType(null);
		controller.setContractCode(null);
		controller.setQuoteGroup(null);
		controller.setIrpf(null);
		contract.setStartDate(new Date());
		contract.setSeniorityDate(contract.getStartDate());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setStatus(ContractStatus.PROCESSED);
		if(controller.getAonFile()!=null){
			contract.setDocument(controller.getAonFile().getData());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		saveContractData();
	}
	
	private void saveContractData() throws ControllerListenerException {
		try {
			ContractController controller = (ContractController) this.getController();
			Contract contract = (Contract) controller.getTo();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
	
			ContractData data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.IRPF_PERCENT.getName() );
			data.setExpression(controller.getIrpf().toString());
			bean.insert(data);

			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.QUOTE_GROUP.getName() );
			data.setExpression("\"" + controller.getQuoteGroup().getValue() + "\"");
			bean.insert(data);
		
			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setName( ContractVariables.TC2.getName() );
			data.setExpression("\"" + controller.getContractCode().getValue() + "\"");
			bean.insert(data);

		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
}
