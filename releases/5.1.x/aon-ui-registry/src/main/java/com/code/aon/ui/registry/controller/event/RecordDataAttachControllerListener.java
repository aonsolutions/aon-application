package com.code.aon.ui.registry.controller.event;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RecordDataController;

public class RecordDataAttachControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		RecordData recordData = (RecordData)recordDataController.getTo();
		if ( recordData.getAttach().getId() != null ) {
			recordDataController.setAttach( recordData.getAttach() );
		} else {
			recordDataController.setAttach( null );
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		recordDataController.setAttach( null );
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {			
			updateAttachment(event);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {			
			updateAttachment(event);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	private void updateAttachment(ControllerEvent event) throws ManagerBeanException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		RecordData recordData = (RecordData) recordDataController.getTo();
		RegistryAttachment attach = recordDataController.getAttach();
		if ( attach != null ) {
			attach.setRegistry(recordData.getRegistry());
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			rAttachBean.insertOrUpdate(attach);
		}
		if (! ObjectUtils.equals(attach, recordData.getAttach()) ) {
			recordDataController.removeAttachment();
		}
		recordData.setAttach( attach );
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			RecordDataController recordDataController = (RecordDataController)event.getController();
			recordDataController.removeAttachment();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before bean removed", e);
		}
	}
	
}