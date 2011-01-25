package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractData;
import com.code.aon.person.Person;
import com.code.aon.ui.employee.controller.ContractController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ContractControllerListener extends ControllerAdapter{
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo(); 
		controller.setEnterprise(new Enterprise());
		controller.setContractData(new ContractData());
		controller.getContractType();
		controller.setContractOption(null);
		contract.setPerson(new Person());
		contract.setStartDate(new Date());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		if(controller.getAonFile()!=null){
			contract.setDocument(controller.getAonFile().getData());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		ContractData data = controller.getContractData();
		data.setContract(contract);
		data.setStartDate(contract.getStartDate());
		data.setEndDate(contract.getEndDate());
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.insert(data);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
