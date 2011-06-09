package com.esferalia.aon.ui.payroll.controller.salary;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class SettleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleController.class);
	
	private SettleParams params;
	
	public SettleParams getParams() {
		return params;
	}
	public void setParams(SettleParams params) {
		this.params = params;
	}
	
	public void onSelectContract(ActionEvent event){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		setParams(new SettleParams());
		getParams().setContract((Contract) controller.getTo());
		getParams().setSeniorityDate(((Contract) controller.getTo()).getSeniorityDate());
		initializeParams();
	}
	
	public void onGenerate(ActionEvent event){
		generateSettle();
		if(getParams().getContract().getEndDate()==null){
			finalizeContract();
		}
	}
	
	private void initializeParams() {
		// TODO inicializar los parametros obteniendo los datos del calculador
	}
	
	private void generateSettle() {
		// TODO lanzar el calculo de finiquitos
		AonUtil.addErrorMessage("sin implementar: generar el finiquito");
		LOGGER.error("sin implementar: generar el finiquito");
	}
	
	private void finalizeContract(){
		AonUtil.addErrorMessage("sin implementar: finalizar el contrato");
		LOGGER.error("sin implementar: finalizar el contrato");
		// TODO finalizar el contrato estableciendo su fecha fin
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
//			getParams().getContract().setEndDate(getParams().getSuspensionDate());
//			bean.update(getParams().getContract());
//		} catch (ManagerBeanException e) {
//			String msg = "Imposible actualizar el contrato.";
//			AonUtil.addErrorMessage(msg);
//			LOGGER.error(msg);
//		}
	}
}
