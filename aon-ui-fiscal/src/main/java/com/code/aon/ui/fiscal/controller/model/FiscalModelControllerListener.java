package com.code.aon.ui.fiscal.controller.model;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FiscalModelControllerListener extends ControllerAdapter {

	private FiscalModelController getController(ControllerEvent event) {
		FiscalModelController c = (FiscalModelController) event.getController();
		return c;
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.load();
			c.setFileOutput(null);
			c.setSelectedTab(c.getLiquidationTabName());
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.unload();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.initialize();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.unload();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FiscalModelController c = getController(event);
		FiscalModel fiscalModel = (FiscalModel) c.getTo();
		if ( (fiscalModel.getModel() == FiscalModelType.M310 || fiscalModel.getModel() == FiscalModelType.M311) 
				&& fiscalModel.getYear() > 2013) {
			throw new ControllerListenerException("El modelo " + fiscalModel.getModel().getValue() 
				+ " no es válido para ejercicios posteriores al 2013. "
				+ " Realice el modelo 303, en su versión de Regimen Simplificado." );
		}
		if (c.getDeclaration().isWithoutActivityDeclarationAvailable() && fiscalModel.isWithoutActivity()) {
			fiscalModel.setStatus(FiscalModelStatus.FINISHED);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			FiscalModel fiscalModel = (FiscalModel) c.getTo();
			c.getFiscalModelManager().initializeFiscalModel(fiscalModel);
			c.setSelectedTab(c.getLiquidationTabName());
			c.setFileOutput(null);
			c.initializeDetails();
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.insertOrUpdateDetails();
			c.setFileOutput(null);
			FiscalModel fiscalModel = (FiscalModel) c.getTo(); 
			c.getFiscalModelManager().refreshFiscalModel(c.getDeclaration(), fiscalModel);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			getController(event).removeDetails();
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
}
