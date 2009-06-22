package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RecordDataController;

public class RecordDataAttachControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		try {
			if(recordDataController.getAonFile() != null && recordDataController.getAonFile().getData() != null){
				AonFile aonFile = recordDataController.getAonFile(); 
				RecordData recordData = (RecordData)recordDataController.getTo();
				if(recordData.getAttach() == null){
					recordData.setAttach(new RegistryAttachment());
				}
				recordData.getAttach().setData( aonFile.getData() );
				recordData.getAttach().setMimeType(MimeType.getByExtension(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1)));
				recordData.getAttach().setRegistry(recordData.getRegistry());
				recordData.getAttach().setDescription(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf("\\")+ 1));
				recordData.getAttach().setCategory(null);
				IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(recordData.getAttach().getId() == null){
					rAttachBean.insert(recordData.getAttach());
				}else{
					rAttachBean.update(recordData.getAttach());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		RecordDataController recordDataController = (RecordDataController)event.getController();
		try {
			if(recordDataController.getAonFile() != null && recordDataController.getAonFile().getData() != null){
				AonFile aonFile = recordDataController.getAonFile(); 
				RecordData recordData = (RecordData)recordDataController.getTo();
				if(recordData.getAttach() == null){
					recordData.setAttach(new RegistryAttachment());
				}
				recordData.getAttach().setData(aonFile.getData());
				recordData.getAttach().setMimeType(MimeType.getByExtension(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1)));
				recordData.getAttach().setRegistry(recordData.getRegistry());
				recordData.getAttach().setDescription(" ");
				recordData.getAttach().setDescription(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf("\\")+ 1));
				recordData.getAttach().setCategory(null);
				IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(recordData.getAttach().getId() == null){
					rAttachBean.insert(recordData.getAttach());
				}else{
					rAttachBean.update(recordData.getAttach());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			RecordDataController recordDataController = (RecordDataController)event.getController();
			if(((RecordData)recordDataController.getTo()).getAttach() != null && ((RecordData)recordDataController.getTo()).getAttach().getId() != null){
				recordDataController.onAttachRemove(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before bean removed", e);
		}
	}
}
