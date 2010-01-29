package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class GeneratorApplicationController implements ICMSConstants {

	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		GeneratorController generator = (GeneratorController)AonUtil.getRegisteredBean(GENERATOR);
		generator.getStatus().onInit(event);
		generator.getStatus().addMessage("ALL PROCESS WORKING...PLEASE WAIT TO START.");
        this.limitar();
        this.incCuenta();
        this.process(generator, event);
		this.decCuenta(); // 
	    this.desbloquear();	}

	private void process(GeneratorController generator, ActionEvent event) throws ManagerBeanException {
		generator.onGenerate(event);
	}

    private int cuenta = 0;
    
    public synchronized void incCuenta() {
        cuenta++;
    }
    public synchronized void decCuenta() {
        cuenta--; 
    }
    
    public synchronized void limitar() {
    	while (cuenta == 2) {
    		try {
    			this.wait();
    		}catch (InterruptedException e) {}
    	}
    }

    public synchronized void desbloquear() {
        this.notifyAll();             
    }

}
