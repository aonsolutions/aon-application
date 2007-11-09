package com.code.aon.webinfo.event;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webinfo.controller.ICECompanyController;
import com.code.aon.webinfo.controller.ICECompanyImagesController;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;

public class ICECompanyImagesControllerListener extends ControllerAdapter {
	
	private static final String COMPANY_CONTROLLER_NAME = "company"; 

	@Override
	public void beforeModelInitialized(ControllerEvent event)throws ControllerListenerException {
		try {
			ICECompanyImagesController imagesController = (ICECompanyImagesController) event.getController();
			imagesController.getCriteria().addEqualExpression(imagesController.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
							RegistryAttachmentType.ADDITIONAL_IMAGE);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error loading asociated Images", e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ICECompanyImagesController imagesController = (ICECompanyImagesController)event.getController();
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		ICECompanyController companyController = (ICECompanyController)AonUtil.getController(COMPANY_CONTROLLER_NAME);
		attach.setRegistry((Company)companyController.getTo());
		attach.setRegistryAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
		attach.setCategory(null);
		imagesController.setPercent(-1);
		imagesController.setState(PersistentFacesState.getInstance());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			ICECompanyImagesController imagesController = (ICECompanyImagesController)event.getController();
			RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
			InputFile inputFile = imagesController.getInputFile();
			InputStream fileStream = new FileInputStream(inputFile.getFile());
			attach.setData(inputStream2ByteArray(fileStream));
			attach.setMimeType(MimeType.getByExtension(imagesController.getFileName().substring(imagesController.getFileName().lastIndexOf(".") + 1)));
			if(attach.getDescription()!= null && attach.getDescription().equals("")){
				int index = imagesController.getFileName().lastIndexOf(".");
				attach.setDescription((index==-1?imagesController.getFileName():imagesController.getFileName().substring(0, index)));
			}
		} catch (FileNotFoundException e) {
			throw new ControllerListenerException("Error saving uploaded file",e);
		} catch (IOException e) {
			throw new ControllerListenerException("Error saving uploaded file",e);
		}
	}
	
	private static byte[] inputStream2ByteArray(InputStream is) throws IOException {
		int BUFFER_SIZE = 8192;
		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] content = new byte[0];
		int length;
		while ((length = is.read(buffer)) > 0) {
			byte[] tmp = content;
			content = new byte[tmp.length + length];
			System.arraycopy(tmp, 0, content, 0, tmp.length);
			System.arraycopy(buffer, 0, content, tmp.length, length);
		}
		return content;
	}
}
