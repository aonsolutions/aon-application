package com.code.aon.ui.campaign.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.enumeration.ProcessDetailStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;

public class ProcessDetailTransitionController extends LinesController {
	
	private boolean newPanelVisible = false;

	public boolean isNewPanelVisible() {
		return newPanelVisible;
	}

	public void setNewPanelVisible(boolean newPanelVisible) {
		this.newPanelVisible = newPanelVisible;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getAvailableProcessDetails() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		List<ProcessDetail> list = (List) getMasterController().getModel().getWrappedData();
		for (ProcessDetail pd: list){
			if (pd != getMasterController().getTo() && pd.getStatus() != ProcessDetailStatus.INACTIVE) {
				SelectItem item = new SelectItem(pd, pd.getDescription());
				processList.add(item);
			}
		}
		return processList;
	}
	
	@Override
	public void onReset(ActionEvent event) {
		setNewPanelVisible(true);
		super.onReset(event);
	}	
	@Override
	public void onAccept(ActionEvent event) {
		setNewPanelVisible(false);
		super.onAccept(event);
	}	
	@Override
	public void onCancel(ActionEvent event) {
		setNewPanelVisible(false);
		super.onCancel(event);
	}	
	
}
