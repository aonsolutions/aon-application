package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.academy.print.ReportAlumn;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.util.AonUtil;

public class AlumnObservationPrinter extends AlumnPrinter{

	private static final Logger LOGGER = Logger.getLogger(AlumnObservationPrinter.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	private static final String OBSEVATION_CONTROLLER_NAME = "customerObservation";

	public Collection getCollection() {
		List<ReportAlumn> reportAlumnList = new LinkedList<ReportAlumn>();
		try {
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ValueBinding vb = ctx.getApplication().createValueBinding("#{"+OBSEVATION_CONTROLLER_NAME+"}");
	        RegistryObservationController observationController = (RegistryObservationController)vb.getValue(ctx);

			CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Iterator iter = ((List)customerController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				Customer alumn = (Customer)iter.next();
				ReportAlumn reportAlumn = new ReportAlumn();
				reportAlumn.setAlumn(alumn);
				reportAlumn.setPhone(obtainPhone(alumn.getRegistry()));
				reportAlumn.setCellular(obtainCellular(alumn.getRegistry()));
				reportAlumn.setCourseCode(obtainCourseCode(alumn.getRegistry()));
				reportAlumn.setBirthDate(obtainBirthDate(alumn.getRegistry()));
				reportAlumn.setObservation(observationController.getRegistryObservation(alumn.getRegistry()));
				reportAlumnList.add(reportAlumn);
			}
			return reportAlumnList;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Collection", e);
		}
		return reportAlumnList;
	}

}