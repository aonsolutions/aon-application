package com.code.aon.ui.finance.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerFeeControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		customerFee.setSecurityLevel(SecurityLevel.OFFICIAL);
		customerFee.setQuantity(1.0);

		try {
			String companyCollections = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
			CompanyCollectionsController compCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyCollections);
			if (compCollections.getWorkPlacesCount() == 1) {
				WorkPlace workPlace = (WorkPlace)compCollections.getWorkPlaces().get(0).getValue();
				customerFee.setWorkPlace(workPlace);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}

	private void validateFeeDates(CustomerFee customerFee) throws ControllerListenerException {
		if(customerFee.getFinalDate() != null){
			if(customerFee.getFinalDate().before(customerFee.getInitialDate())){
				throw new ControllerListenerException("Final Date can't be earlier than Initial Date");
			}
		}

		Calendar initialCalendar = new GregorianCalendar();
		initialCalendar.setTime(customerFee.getInitialDate());
		initialCalendar.set(Calendar.DAY_OF_MONTH, 1);
		if(customerFee.getBillingDate().before(initialCalendar.getTime())){
			throw new ControllerListenerException("Billing Date can't be earlier than Initial Date");
		}
	}

}