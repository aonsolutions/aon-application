package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class GeneratorApplicationController implements ICMSConstants {

    private int cuenta = 0;
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		GeneratorController generator = (GeneratorController)AonUtil.getRegisteredBean(GENERATOR);
		generator.getStatus().onInit(event);
		generator.getStatus().info("ALL PROCESS WORKING...PLEASE WAIT TO START.");
        lock();
        generator.onGenerate(this);
		unlock(); 
	}
	
	private synchronized void lock() {		
    	while (cuenta == 2) {
    		try {
    			this.wait();
    		} catch (InterruptedException e) {}
    	}
    	cuenta++;
	}
	
	public synchronized void unlock() {
		cuenta--;
		this.notifyAll();
	}

}
