package com.code.aon.ui.cvitae.event;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CurriculumPhotoControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CurriculumPhotoControllerListener.class.getName());
    private static final String BASE_NAME = "com.code.aon.ui.employee.i18n.messages";

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CurriculumController curriculumController = (CurriculumController)event.getController();
		if(curriculumController.getFile() != null){
			UploadedFile file = curriculumController.getFile();
			if (file.getSize()>100000){
		        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
				throw new ControllerListenerException(bundle.getString("curriculum_photo_max_size_error"));
			}
			try {
				RegistryAttachment attach = new RegistryAttachment();
				attach.setCategory(null);
				attach.setData(file.getBytes());
				attach.setDescription("PHOTO");
				attach.setRegistry(((Curriculum)event.getController().getTo()).getRegistry());
				attach.setMimeType(MimeType.getByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				curriculumController.setAttach((RegistryAttachment)attachBean.insert(attach));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error adding photo", e);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding photo", e);
			}
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CurriculumController curriculumController = (CurriculumController)event.getController();
		if(curriculumController.getFile() != null){
			UploadedFile file = curriculumController.getFile();
			if (file.getSize()>100000){
		        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
				throw new ControllerListenerException(bundle.getString("curriculum_photo_max_size_error"));
			}
			RegistryAttachment attach = obtainRegistryAttachment(((Curriculum)event.getController().getTo()).getId());
			try {
				if(attach == null){
					attach = new RegistryAttachment();
				}
				attach.setCategory(null);
				attach.setData(file.getBytes());
				attach.setDescription("PHOTO");
				attach.setRegistry(((Curriculum)event.getController().getTo()).getRegistry());
				attach.setMimeType(MimeType.getByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(attach.getId() == null){
					curriculumController.setAttach((RegistryAttachment)attachBean.insert(attach));
				}else{
					curriculumController.setAttach((RegistryAttachment)attachBean.update(attach));
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error updating photo", e);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating photo", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CurriculumController curriculumController = (CurriculumController)event.getController();
		curriculumController.setAttach(obtainRegistryAttachment(((Curriculum)curriculumController.getTo()).getId()));
	}
	
	private RegistryAttachment obtainRegistryAttachment(Integer id) {
		try {
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), id);
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_DESCRIPTION), "PHOTO");
			Iterator iter = attachBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAttachment)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining attach for curriculum", e);
		}
		return null;
	}
}
