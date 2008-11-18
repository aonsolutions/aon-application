package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Percepcion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class PercepcionControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("PercepcionControllerListener -------> afterBeanCreated");
		
		
		Nivel nivel = (Nivel)(AonUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		((Percepcion)(event.getController().getTo())).getId().setCdg(nivel.getConvenio().getCdg());
		
		
		Percepcion p = (Percepcion)(event.getController().getTo());
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
		// TODO Auto-generated method stub
		System.out.println("PercepcionControllerListener -------> beforeBeanAdded");
		
		setRequiredData(event);
		
		Percepcion p = (Percepcion)(event.getController().getTo());
		
		p.setFecnew(currentDate());
		p.setHornew(currentDate());
		
		
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		setRequiredData(event);
		
		Percepcion p = (Percepcion)(event.getController().getTo());
		
		p.setFecmod(currentDate());
		p.setHormod(currentDate());
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercepcionControllerListener -------> afterBeanCanceled");
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercepcionControllerListener -------> beforeBeanCanceled");
	}
	
	/**
	 * Establece atributos necesarios para la clave principal obtenidos del maestro
	 * @param event
	 */
	private void setRequiredData(ControllerEvent event){
		Nivel nivel = (Nivel)(AonUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		Percepcion p = (Percepcion)(event.getController().getTo());
		
		p.getId().setNivel(nivel.getId().getCdg());
		p.getId().setCodcom(((Percepcion)(event.getController().getTo())).getComplemento1().getCdg());
		
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
