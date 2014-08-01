package com.esferalia.aon.ui.payroll.event.contract;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getFamilySituation()!=FamilySituation.MARRIED){
			data.setSpouseDocument(null);
		}
		data.setIssueDate(data.getStartDate());
		completeHandicap();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getFamilySituation()!=FamilySituation.MARRIED){
			data.setSpouseDocument(null);
		}
		completeHandicap();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		completeHandicap();
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
