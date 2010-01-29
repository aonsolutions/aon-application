package com.code.aon.ui.campaign.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.enumeration.ProcessDetailStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;

public class ProcessDetailTransitionController extends LinesController {

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
	
	
	
}
