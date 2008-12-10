package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Percniv;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class PercnivControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercnivControllerListener -------> afterBeanCreated");
		
		
		Nivel nivel = (Nivel)(FormUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		((Percniv)(event.getController().getTo())).getId().setCdg(nivel.getConvenio().getCdg());
		
		
		Percniv p = (Percniv)(event.getController().getTo());
		BigDecimal zero = new BigDecimal(0);
		p.setNivel(nivel);
		p.setConvenio(nivel.getConvenio());
		p.setCalculo("0");
		p.setMes(0);
		p.setUnidades(zero);
		p.setImporte(zero);
		p.setImpuni(zero);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercnivControllerListener -------> beforeBeanAdded");
		
		setRequiredData(event);
		
		Percniv p = (Percniv)(event.getController().getTo());
		
		p.setFecnew(currentDate());
		p.setHornew(currentDate());
		
		
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		setRequiredData(event);
		
		Percniv p = (Percniv)(event.getController().getTo());
		
		p.setFecmod(currentDate());
		p.setHormod(currentDate());
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercnivControllerListener -------> afterBeanCanceled");
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercnivControllerListener -------> beforeBeanCanceled");
	}
	
	/**
	 * Establece atributos necesarios para la clave principal obtenidos del maestro
	 * @param event
	 */
	private void setRequiredData(ControllerEvent event){
		Nivel nivel = (Nivel)(FormUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		Percniv p = (Percniv)(event.getController().getTo());
		
		p.getId().setNivel(nivel.getId().getCdg());
		p.getId().setCodcom(((Percniv)(event.getController().getTo())).getComplemento1().getCdg());
		
		if(p.getComplemento().getCdg()=="")
			p.setComplemento(null);
	}
	
	/**
	 * Devuelve la fecha actual
	 * @return Date
	 */
	private Date currentDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		return calendar.getTime();
	}


}
