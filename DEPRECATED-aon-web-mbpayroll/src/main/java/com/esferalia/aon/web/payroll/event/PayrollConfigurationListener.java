package com.esferalia.aon.web.payroll.event;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.PayrollException;

public class PayrollConfigurationListener implements ServletContextListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(PayrollConfigurationListener.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		try {
			LOGGER.info("Payroll Configuration.");
			AonPayroll.configure();
		} catch (PayrollException e) {
			LOGGER.error("Error configurando payroll.");
			e.printStackTrace();
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
}
