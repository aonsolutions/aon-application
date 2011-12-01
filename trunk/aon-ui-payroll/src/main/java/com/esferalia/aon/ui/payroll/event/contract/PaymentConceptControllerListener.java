package com.esferalia.aon.ui.payroll.event.contract;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class PaymentConceptControllerListener extends ControllerAdapter{
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((PaymentConcept)getController().getTo()).setType(PaymentType.SALARY_SUPPLEMENTS);
	}
    
}
