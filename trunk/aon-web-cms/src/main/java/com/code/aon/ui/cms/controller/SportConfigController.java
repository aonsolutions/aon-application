package com.code.aon.ui.cms.controller;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportConfig;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class SportConfigController extends BasicController implements ICMSConstants {

	private static final Logger LOGGER = Logger.getLogger(SportConfigController.class.getName());
	
	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(SportConfig.class);
		super.onSearch(event);
		try{
			super.onSelectFirst(event);
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
	}
	
}