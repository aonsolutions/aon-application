package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;

import com.code.aon.payroll.resultados.salarios.Nominaex;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.NominaexController;

public class NominaexControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		((NominaexController)getController()).verifyNullFields();
		((NominaexController)getController()).generarNumero(null);
		
		Nominaex to = ((Nominaex)(getController().getTo()));
		to.setTotalDeducir(to.getImpirpf());
		to.setLiquido(to.getImporte().add(to.getTotalDeducir().negate()));
		
		if(((Nominaex)(getController().getTo())).getFecemi()==null){
			Calendar calendar = Calendar.getInstance();
			((Nominaex)(getController().getTo())).setFecemi(calendar.getTime());
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((NominaexController)getController()).verifyNullFields();
				
		Nominaex to = ((Nominaex)(getController().getTo()));
		to.setTotalDeducir(to.getImpirpf());
		to.setLiquido(to.getImporte().add(to.getTotalDeducir().negate()));
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
		BigDecimal importe = ((Nominaex)getController().getTo()).getImporte();
		
		((NominaexController)getController()).setBaseIrpf(importe);
		((NominaexController)getController()).setTotal(importe);
		
		super.afterBeanSelected(event);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		
		if(getController().isNew() && ((Nominaex)(getController().getTo())).getFecemi()==null){
			Calendar calendar = Calendar.getInstance();
			((Nominaex)(getController().getTo())).setFecemi(calendar.getTime());
		}
		
		super.afterBeanReset(event);
	}
	
	

}
