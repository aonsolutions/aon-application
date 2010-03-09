package com.code.aon.ui.company.event;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyImagesControllerListener extends ControllerAdapter {
	
    /** BASE_NAME. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Expression imageExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
			Expression bannerExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.BANNER);
			Expression orExpr = ExpressionUtilities.getOrExpression(imageExp, bannerExp);
			event.getController().getCriteria().addExpression(orExpr);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyImagesController imagesController = (CompanyImagesController)event.getController();
			if(imagesController.getFile() != null){
				UploadedFile file = imagesController.getFile();
				if (file.getSize()>1000000){
			        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
					throw new ControllerListenerException(bundle.getString("aon_company_image_max_size_error"));
				}
				RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
				attach.setCategory(null);
				attach.setData(file.getBytes());
				attach.setDescription(file.getName().substring(file.getName().lastIndexOf("\\") + 1, file.getName().lastIndexOf(".")));
				attach.setMimeType(MimeType.getByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
}
