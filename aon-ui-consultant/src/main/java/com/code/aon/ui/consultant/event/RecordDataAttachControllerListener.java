package com.code.aon.ui.consultant.event;

import java.io.IOException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.consultant.RecordData;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.consultant.controller.RecordDataController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RecordDataAttachControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		try {
			if(recordDataController.getFile() != null){
				RecordData recordData = (RecordData)recordDataController.getTo();
				if(recordData.getAttach() == null){
					recordData.setAttach(new RegistryAttachment());
				}
				recordData.getAttach().setData(recordDataController.getFile().getBytes());
				recordData.getAttach().setMimeType(MimeType.getByExtension(recordDataController.getFile().getName().substring(recordDataController.getFile().getName().lastIndexOf(".") + 1)));
				recordData.getAttach().setRegistry(recordData.getRegistry());
				recordData.getAttach().setDescription(recordDataController.getFile().getName().substring(recordDataController.getFile().getName().lastIndexOf("\\")+ 1));
				recordData.getAttach().setCategory(null);
				IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(recordData.getAttach().getId() == null){
					rAttachBean.insert(recordData.getAttach());
				}else{
					rAttachBean.update(recordData.getAttach());
				}
			}
		} catch (IOException e) {
			throw new ControllerListenerException(e);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		try {
			if(recordDataController.getFile() != null){
				RecordData recordData = (RecordData)recordDataController.getTo();
				if(recordData.getAttach() == null){
					recordData.setAttach(new RegistryAttachment());
				}
				recordData.getAttach().setData(recordDataController.getFile().getBytes());
				recordData.getAttach().setMimeType(MimeType.getByExtension(recordDataController.getFile().getName().substring(recordDataController.getFile().getName().lastIndexOf(".") + 1)));
				recordData.getAttach().setRegistry(recordData.getRegistry());
				recordData.getAttach().setDescription(" ");
				recordData.getAttach().setDescription(recordDataController.getFile().getName().substring(recordDataController.getFile().getName().lastIndexOf("\\")+ 1));
				recordData.getAttach().setCategory(null);
				IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(recordData.getAttach().getId() == null){
					rAttachBean.insert(recordData.getAttach());
				}else{
					rAttachBean.update(recordData.getAttach());
				}
			}
		} catch (IOException e) {
			throw new ControllerListenerException(e);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			RecordDataController recordDataController = (RecordDataController)event.getController();
			recordDataController.onAttachRemove(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before bean removed", e);
		}
	}
}
