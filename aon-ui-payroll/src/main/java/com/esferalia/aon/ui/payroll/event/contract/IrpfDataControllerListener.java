package com.esferalia.aon.ui.payroll.event.contract;


import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IrpfData data = ((IrpfData) getController().getTo());
		data.setIssueDate(data.getStartDate());

		completeFamilySituation();
		completeHandicap();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		completeFamilySituation();
		completeHandicap();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		completeCustomIrpf();
		completeHandicap();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		checkCustomIrpf(true);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		checkCustomIrpf(false);
	}
	
	
	private void checkCustomIrpf(boolean isNew) {
		IrpfDataController controller = (IrpfDataController) this.getController();
		IrpfData data = ((IrpfData) controller.getTo());
		if(controller.getIrpfCustomPercent()){
			try {
					createCustomPercent(data);
			} catch (ManagerBeanException e) {
				LOGGER.error("No se ha podido guardar el porcentaje de IRPF");
			}
		} else {
			try {
				removeCustomPercent(data);
			} catch (ManagerBeanException e) {
				LOGGER.error("No se ha podido borrar el porcentaje de IRPF");
			}
		}
	}

	private void createCustomPercent(IrpfData irpf) throws ManagerBeanException {
		IrpfDataController controller = (IrpfDataController) this
				.getController();
		ContractData data = SEPEUtils
				.getInstance()
				.getContractDataMap(irpf.getContract(), irpf.getStartDate(),
						irpf.getEndDate(), true)
				.get(ContextVariable.IRPF_PERCENT.name());
		if (data == null || data.getId() == null) {
			data = new ContractData();
			data.setContract(irpf.getContract());
			data.setName(ContextVariable.IRPF_PERCENT.name());
		}
		data.setStartDate(irpf.getStartDate());
		data.setEndDate(irpf.getEndDate());
		data.setExpression(controller.getIrpfPercent().toString());
		BeanManager.getManagerBean(ContractData.class).insertOrUpdate(data);
	}
	
	private void removeCustomPercent(IrpfData irpf) throws ManagerBeanException {
		ContractData data = SEPEUtils
				.getInstance()
				.getContractDataMap(irpf.getContract(), irpf.getStartDate(),
						irpf.getEndDate(), true)
				.get(ContextVariable.IRPF_PERCENT.name());
		if(data!=null && data.getId()!=null){
			BeanManager.getManagerBean(ContractData.class).remove(data);
		}
	}

	private void completeCustomIrpf() {
		IrpfDataController controller = (IrpfDataController) this.getController();
		IrpfData irpf = ((IrpfData) controller.getTo());
		ContractData data = SEPEUtils
				.getInstance()
				.getContractDataMap(irpf.getContract(), irpf.getStartDate(),
						irpf.getEndDate(), true)
						.get(ContextVariable.IRPF_PERCENT.name());
		if(data!=null && data.getId()!=null){
			controller.setIrpfCustomPercent(true);
			if(NumberUtils.isNumber(data.getExpression())){
				controller.setIrpfPercent(Double.parseDouble(data.getExpression()));
			}
		}
	}
	
	private void completeFamilySituation() {
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getFamilySituation()!=FamilySituation.MARRIED){
			data.setSpouseDocument(null);
		}
	}

	private void completeHandicap() {
		IrpfDataController controller = (IrpfDataController) getController();
		IrpfData data = ((IrpfData) getController().getTo());
		if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65){
			if(data.isDependence()){
				data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE);
			} else {
				data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65);
			}
		} else if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE){
			data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65);
			data.setDependence(true);
		} else if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_65){
			data.setDisabilityLevel(DisabilityLevel.GT_EQ_65);
			data.setDependence(false);
		} else {
			data.setDisabilityLevel(null);
			data.setDependence(false);
		}
	}
	
}
