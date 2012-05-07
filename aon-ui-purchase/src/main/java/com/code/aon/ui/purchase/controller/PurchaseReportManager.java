package com.code.aon.ui.purchase.controller;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class PurchaseReportManager  {

	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseReportManager.class);
	
	private boolean valued;
	
	public boolean isValued() {
		return valued;
	}

	public void setValued(boolean valued) {
		this.valued = valued;
	}

	public String onExecute() {
		try {
			ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
			String outcome = manager.onExecute();
			return outcome;
		} catch (Throwable e) {
			String msg = "Error al ejecutar el listado. " + e.getMessage();
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}			
	}

}
