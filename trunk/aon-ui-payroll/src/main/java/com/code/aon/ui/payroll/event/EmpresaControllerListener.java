package com.code.aon.ui.payroll.event;


import com.code.aon.payroll.enumeration.Tipdom;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.DomicilioController;
import com.code.aon.ui.payroll.controller.EmpresaController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.TipDomicilioController;
import com.code.aon.ui.util.AonUtil;


public class EmpresaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		//((Empresa)(getController().getTo())).setCliente(((EmpresaController)getController()).getCliente());
		super.beforeBeanAdded(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {

		super.beforeBeanUpdated(event);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		getController().setNew(false);
		addDomicilio();
		
		super.afterBeanAdded(event);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((EmpresaController)getController()).generateCdg();
		((EmpresaController)getController()).setDefaultFields();
		super.afterBeanCreated(event);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
				
		super.afterBeanUpdated(event);
	}
	
	
	
	 /**
	 * Añade un domicilio para el cliente de la empresa seleccionada y
	 * Crea dos tipos de domicilio (social y fiscal) para el cliente de la empresa seleccionada
	 */
	private void addDomicilio(){
		
		Cliente cliente = ((Empresa)getController().getTo()).getCliente();
		
		try{
		/*
		 * Añade un domicilio para el cliente de la empresa seleccionada
		 */
			DomicilioController domicilioController = (DomicilioController)AonUtil.getController(IPayrollConstants.DOMICILIO_CONTROLLER_NAME);
			domicilioController.onReset(null);
			
			Domicilio domicilio = (Domicilio)domicilioController.getTo();
			
	    	domicilio.setCliente(cliente);
	    	//domicilio.setCdg(cdg); --> generado automaticamente
	    	domicilio.setTipovia(cliente.getTipovia());
	    	domicilio.setNomvia(cliente.getNomvia());
	    	domicilio.setNumero(cliente.getNumero());
	    	domicilio.setOtrdir(cliente.getOtrdir());
	        domicilio.setCodpos(cliente.getCodpos());
	        domicilio.setLocalidad(cliente.getLocalidad());
	        domicilio.setProvincia(cliente.getProvincia());
	        domicilio.setPersona(cliente.getPersona());
	        domicilio.setTelefono(cliente.getTelefono());
	        domicilio.setTelefono2(cliente.getTelefono2());
	        domicilio.setTelefono3(cliente.getTelefono3());
	        domicilio.setFax(cliente.getFax());
			domicilio.setEmail(cliente.getEmail()); 
			domicilio.setLinea1("");
			domicilio.setLinea2("");		
			domicilio.setAclaracion("");
			//y ..... a jugarsela
			domicilioController.onAccept(null);
			
		/*
		 * Crea dos tipos de domicilio (social y fiscal) para el cliente de la empresa seleccionada
		 */
			TipDomicilioController tipdomicilioController = (TipDomicilioController)AonUtil.getController(IPayrollConstants.TIPDOMICILIO_CONTROLLER_NAME);
			Emprdom tipdomicilio;
			
			//tipo domicilio social en tipdomicilio
			tipdomicilioController.onReset(null);
			tipdomicilio = (Emprdom)tipdomicilioController.getTo();
			
			tipdomicilio.setTipdom(Tipdom.SOCIAL);
			tipdomicilio.setCliente(cliente);
			tipdomicilio.setDomicilio(domicilio);
			tipdomicilio.setActividad(null);
			tipdomicilio.setEmpresa((Empresa)getController().getTo());
		
			tipdomicilioController.onAccept(null);
			
			//tipo domicilio fiscal en tipdomicilio
			tipdomicilioController.onReset(null);
			tipdomicilio = (Emprdom)tipdomicilioController.getTo();
			
			tipdomicilio.setTipdom(Tipdom.FISCAL);
			tipdomicilio.setCliente(cliente);
			tipdomicilio.setDomicilio(domicilio);
			//tipdomicilio.setActividad(null);
			tipdomicilio.setEmpresa((Empresa)getController().getTo());
			tipdomicilioController.onAccept(null);
			
		} catch (Exception e) {
			System.out.println("FALLOOOOO");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Actualiza un domicilio para el cliente de la empresa seleccionada y
	 */
	private void updateDomicilio(){
	
	/*
	 else if (vBolNuevo == false) then begin
        Sql.SqlExec ("UPDATE domicilio " + 
                         "SET tipovia   = ?, "+
                             "nomvia    = ?, "+
                             "numero    = ?, "+
                             "otrdir    = ?, "+ 
                             "codpos    = ?, "+
                             "localidad = ?, "+
                             "provincia = ?, "+
                             "persona   = ?, "+
                             "telefono  = ?, "+
                             "fax       = ?, "+
                             "email     = ?, "+
                             "linea1    = '', "+
                             "linea2    = '', "+
                             "aclaracion= ''  "+
                        "WHERE codcli = ? "+ 
                        "AND cdg= ?",
                            lChTipovia,
                            lChNomvia,
                            lChNumero,
                            lChOtrodir,
                            lChCodpos,
                            lChLocalidad,
                            lChProvincia,
                            lChPersona,
                            lChTelefono,
                            lChFax,
                            lChEmail,
                            vIntCodCli,
                            lIntDomicilio);
    end
	 */
		
		Cliente cliente = ((Empresa)getController().getTo()).getCliente();
		
		try{
			/*
			 * Añade un domicilio para el cliente de la empresa seleccionada
			 */
				DomicilioController domicilioController = (DomicilioController)AonUtil.getController(IPayrollConstants.DOMICILIO_CONTROLLER_NAME);
				domicilioController.onReset(null);
				
				Domicilio domicilio = (Domicilio)domicilioController.getTo();
				
		    	domicilio.setCliente(cliente);
		    	//domicilio.setCdg(cdg); --> generado automaticamente
		    	domicilio.setTipovia(cliente.getTipovia());
		    	domicilio.setNomvia(cliente.getNomvia());
		    	domicilio.setNumero(cliente.getNumero());
		    	domicilio.setOtrdir(cliente.getOtrdir());
		        domicilio.setCodpos(cliente.getCodpos());
		        domicilio.setLocalidad(cliente.getLocalidad());
		        domicilio.setProvincia(cliente.getProvincia());
		        domicilio.setPersona(cliente.getPersona());
		        domicilio.setTelefono(cliente.getTelefono());
		        domicilio.setTelefono2(cliente.getTelefono2());
		        domicilio.setTelefono3(cliente.getTelefono3());
		        domicilio.setFax(cliente.getFax());
				domicilio.setEmail(cliente.getEmail()); 
				domicilio.setLinea1("");
				domicilio.setLinea2("");		
				domicilio.setAclaracion("");
				//y ..... a jugarsela
				domicilioController.onAccept(null);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	
}
