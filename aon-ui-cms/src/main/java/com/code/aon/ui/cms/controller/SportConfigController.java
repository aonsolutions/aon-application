package com.code.aon.ui.cms.controller;


import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.SportConfig;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class SportConfigController extends BasicController implements ICMSConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(SportConfigController.class);
	
	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(SportConfig.class);
		super.onSearch(event);
		try{
			super.onSelectFirst(event);
		}catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
		}
	}
	
}