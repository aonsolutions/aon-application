package com.esferalia.aon.ui.payroll.controller.launcher;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;



public class IrpfLauncher extends AbstractIrpfLauncher {
	

	private String irpf;
	private Object message;
	private Integer contractId;

	
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}	
	public String getIrpf() {
		return irpf;
	}
	public void setIrpf(String irpf) {
		this.irpf = irpf;
	}

	
	public void onIrpfUpdate(ActionEvent event) {
		IrpfDataController controller = (IrpfDataController) FormUtil.getController(IPayrollConstants.IRPF_DATA_CONTROLLER_NAME);
		controller.getParams().setContractId(contractId);
		controller.getParams().setDate(getParams().getDate());
		controller.getParams().setNewIrpf(Double.parseDouble(irpf));
		try {
			if(controller.updateIrpf()){
				LogMessage msg = (LogMessage) getMessage();
				//msg.setLevel( LogMessage.Level.INFO);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al actualizar el irpf.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onShowContract(ActionEvent event) {
		try {
			ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			controller.onEditSearch(event);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IEntityAlias.CONTRACT_ID),getContractId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction("irpfLauncher_form");
		} catch (ManagerBeanException e) {
			String msg = "Error al navegar al contrato.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	@Override
	public void onWarn(TipoRetenedorError2011 retenedorError2011,
			TipoRetenidoError2011 retenidoError2011) {
		// TODO Auto-generated method stub
		
	}
	
	

}
