package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;
import com.esferalia.aon.ui.payroll.controller.launcher.ListIrpfBuilderListener.LogMessage;



public class IrpfLauncher extends AbstractIrpfLauncher {
	

	private Integer contractId;
	private String irpf;
	private SQLIrpfBuilder irpfBuilder;
	private Object message;
	
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


	
	@Override
	protected void execute(IrpfLauncherParams params)
			throws SalaryException {
		try {

			irpfBuilder = new SQLIrpfBuilder(getConnection());
			listener = new ListIrpfBuilderListener(irpfBuilder);
			listener.setDebugEnabled(isDebugEnabled());
			listener.setSaveLog(isSaveLog());
			irpfBuilder.setListener(listener);
			IrpfCalculator calculator = new IrpfCalculator(params.getDate());
			calculator.setIrpfBuilder(irpfBuilder);
			
			String msg = MessageFormat.format("Cálculo de IRPF {0}:{1}",new Object[] {params.getDate(), params.getDate()});
			listener.onInfo(msg);
			
			calculate(calculator);
			
			if(isSaveEnabled()){
				try {
					irpfBuilder.commit();
				} catch (Throwable e) {
					listener.onError(e.getLocalizedMessage());
					irpfBuilder.rollback();
				}
				msg = MessageFormat.format("Total variables insertadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			} else {
				irpfBuilder.rollback();
				msg = MessageFormat.format("Total variables calculadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			}
			listener.onInfo(msg);
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		}
	}
	
	public void onIrpfUpdate(ActionEvent event) {
		IrpfDataController controller = (IrpfDataController) FormUtil.getController(IPayrollConstants.IRPF_DATA_CONTROLLER_NAME);
		controller.getParams().setContractId(contractId);
		controller.getParams().setDate(getParams().getDate());
		controller.getParams().setNewIrpf(Double.parseDouble(irpf));
		try {
			if(controller.updateIrpf()){
				ListIrpfBuilderListener.LogMessage msg = (LogMessage) getMessage();
				msg.getLevel();
				msg.setLevel(SalaryBuilderListenerLevel.INFO);
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
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IPayrollAlias.CONTRACT_ID),getContractId());
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

}
