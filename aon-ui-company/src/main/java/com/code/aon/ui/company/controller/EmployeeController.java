package com.code.aon.ui.company.controller;

import com.code.aon.company.WorkActivity;
import com.code.aon.ui.registry.controller.RegistryController;

public class EmployeeController extends RegistryController {

	private WorkActivity workActivity;
	
	public WorkActivity getWorkActivity() {
		return workActivity;
	}

	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}
	
}
