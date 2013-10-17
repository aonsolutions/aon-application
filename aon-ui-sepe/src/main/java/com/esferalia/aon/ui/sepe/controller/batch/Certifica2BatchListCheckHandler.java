package com.esferalia.aon.ui.sepe.controller.batch;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;


public class Certifica2BatchListCheckHandler extends BatchListCheckHandler {

	public Certifica2BatchListCheckHandler(IController controller) {
		super(controller);
	}
	
	@Override
	public void setRowChecked(boolean rowChecked) {
		try {
			super.setRowChecked(rowChecked);
			if(!rowChecked){
				Certifica2ListController controller = (Certifica2ListController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
				Contract contract = (Contract) getController().getModel().getRowData();
				if(controller.getRemesableContracts().containsKey(contract.getId())){
					controller.getRemesableContracts().remove(contract.getId());
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void clearCheckedList() {
		super.clearCheckedList();
		Certifica2ListController controller = (Certifica2ListController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		controller.getRemesableContracts().clear();
	}
	
}
