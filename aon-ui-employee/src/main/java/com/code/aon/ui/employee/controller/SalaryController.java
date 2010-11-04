package com.code.aon.ui.employee.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class SalaryController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class.getName());

	
	public SalaryController() {
		
	}

	    
    public void onPrintAllLast( ActionEvent event ) {
		
	}
    
    public void onPrintActual( ActionEvent event ) {
    	
    }
    

//	@Override
//	public Collection getCollection() {
//		
//		return super.getCollection();
//	}
//
//	@Override
//	public Collection getCollection(boolean forceRefresh)
//			throws ManagerBeanException {
//		
//		return super.getCollection(forceRefresh);
//	}

}
