package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
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
		RegistryAttachment attachment = recordData.getAttach();
		if ( attachment.getId() != null ) {
			AonFile f = new AonFile();
			f.setAttachment(attachment);
			f.setFileName(attachment.getDescription());
			MimeType mimeType = attachment.getMimeType();
			if ( mimeType == null ) {
				mimeType = f.resolveMimeType();
			}
			f.setMimeType(mimeType);			
			recordDataController.setAonFile(f);
		} else {
			recordDataController.setAonFile( null );
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		recordDataController.setAonFile(null);
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
		RegistryAttachment attach = recordData.getAttach();
		AonFile aonFile = recordDataController.getAonFile();
		if ( (aonFile != null) && aonFile.getSize() > 0 ) {
			if ( attach == null ) {
				attach = new RegistryAttachment();
				recordData.setAttach(attach);
			}
			attach.setCategory(null);
			attach.setData(aonFile.getData());
			attach.setDescription(aonFile.getFileName());
			attach.setRegistry(recordData.getRegistry());
			attach.setMimeType(aonFile.getMimeType());			
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			rAttachBean.insertOrUpdate(attach);
			aonFile.setAttachment(attach);
		} else {
			recordDataController.removeAttachment();
		}
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