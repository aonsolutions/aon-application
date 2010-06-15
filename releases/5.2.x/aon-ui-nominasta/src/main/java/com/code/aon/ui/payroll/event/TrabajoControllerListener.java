package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.principales.personas.Trabajo;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.TrabajoController;

public class TrabajoControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		int cdg = ((Trabajo)this.getController().getTo()).getEmprper().getCdg(); 
		((Trabajo)this.getController().getTo()).getId().setCdg(cdg);
		
		Convenio convenio = ((TrabajoController)this.getController()).getConvenio();
		((Trabajo)this.getController().getTo()).setConvenio(convenio);
	
		checkNullFields();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println(" beforeBeanSelected ");
		
		Convenio convenio = ((Trabajo)this.getController().getTo()).getConvenio();
		((TrabajoController)this.getController()).setConvenio(convenio);
		
		super.afterBeanSelected(event);
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		super.beforeModelInitialized(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		
		Convenio convenio = ((Trabajo)this.getController().getTo()).getConvenio();
		((TrabajoController)this.getController()).setConvenio(convenio);
		
		checkNullFields();
	}

	private void checkNullFields() {
		Trabajo trabajo = ((Trabajo)this.getController().getTo());
		
		// Comprueba lookup nulos
		if(trabajo.getTipaut()!=null && trabajo.getTipaut().getCdg()== "")
			trabajo.setTipaut(null);
		if(trabajo.getBasecoti()!=null && trabajo.getBasecoti().getCdg() == "")
			trabajo.setBasecoti(null);
		if(trabajo.getColectivos()!=null && trabajo.getColectivos().getCdg() == "")
			trabajo.setColectivos(null);
		if(trabajo.getConvenio()!=null && trabajo.getConvenio().getCdg() == "")
			trabajo.setConvenio(null);
		if(trabajo.getTipocont()!=null && trabajo.getTipocont().getCdg() == "")
			trabajo.setTipocont(null);
		if(trabajo.getEntidad()!=null && trabajo.getEntidad().getCdg() == "")
			trabajo.setEntidad(null);
		if(trabajo.getEpigrafe()!=null && trabajo.getEpigrafe().getCdg() == "")
			trabajo.setEpigrafe(null);
		if(trabajo.getPorcoti()!=null && trabajo.getPorcoti().getCdg() == "")
			trabajo.setPorcoti(null);
		if(trabajo.getSucursal()!=null && trabajo.getSucursal().getId().getCdg() == "")
			trabajo.setSucursal(null);
		if(trabajo.getTipcotc2()!=null && trabajo.getTipcotc2().getCdg() == "")
			trabajo.setTipcotc2(null);
		
	}
}
