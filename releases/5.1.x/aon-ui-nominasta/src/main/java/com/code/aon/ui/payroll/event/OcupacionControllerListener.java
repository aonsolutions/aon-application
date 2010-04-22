package com.code.aon.ui.payroll.event;



import java.math.BigDecimal;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class OcupacionControllerListener extends ControllerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Ocupacion elem = (Ocupacion)event.getController().getTo();
		
		if(elem.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			elem.setFecfin(c.getTime());
		}else if(elem.getFecfin().before(elem.getId().getFecini())){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
		//inicializa los campos "it" e "ims" a cero si son nulos y guarda su suma en "total"
		BigDecimal big=new BigDecimal(0);
		if(elem.getPctit()==null)
			elem.setPctit(big);
		if(elem.getPctims()==null)
			elem.setPctims(big);
		elem.setPcttotal(elem.getPctit().add(elem.getPctims()));
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}
	
}
