package com.code.aon.ui.groupware.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.ui.form.LinesController;

public class ProcessDetailTransitionController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAvailableProcessDetails() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		List<ProcessDetail> list = (List<ProcessDetail>) getMasterController().getModel().getWrappedData();
		for (ProcessDetail pd: list){
			if (pd != getMasterController().getTo() && pd.isActive()) {
				SelectItem item = new SelectItem(pd, pd.getDescription());
				processList.add(item);
			}
		}
		return processList;
	}
	
}
