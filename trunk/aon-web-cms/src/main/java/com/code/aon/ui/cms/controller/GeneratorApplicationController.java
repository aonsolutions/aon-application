package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class GeneratorApplicationController {

	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		GeneratorController generator = (GeneratorController)AonUtil.getRegisteredBean("generator");
		generator.getStatus().onInit(event);
		generator.getStatus().addMessage("ALL PROCESS WORKING...PLEASE WAIT TO START.");
		waitIfNedded(generator);
	}

	private synchronized void waitIfNedded(GeneratorController generator) throws ManagerBeanException {
		generator.onGenerate(null);
	}
	
}
