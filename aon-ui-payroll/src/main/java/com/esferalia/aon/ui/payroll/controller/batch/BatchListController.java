package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;


public interface BatchListController extends IController {

	public List<ITransferObject> getAllList() throws ManagerBeanException;
	
}
