package com.code.aon.ui.cms.controller;


import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportConfig;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class SportConfigController extends BasicController {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(SportConfig.class);
		super.onSearch(event);
		try{
			super.onSelectFirst(event);
		}catch (Exception e) {
		}
	}
	
}