package com.esferalia.aon.ui.payroll.event.contract;

import org.apache.commons.lang.StringUtils;

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
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		manageName();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		manageName();
	}
		
	private void manageName(){
		PaymentConcept pc = (PaymentConcept) this.getController().getTo();
		if(StringUtils.contains(pc.getCode()," ")){
			pc.setCode(StringUtils.replace(pc.getCode(), " ", ""));
		}
		pc.setCode(pc.getCode().toUpperCase());
	}
    
}
