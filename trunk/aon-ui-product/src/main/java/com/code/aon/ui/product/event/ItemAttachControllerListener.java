package com.code.aon.ui.product.event;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.product.ItemAttachment;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.IItemMessages;
import com.code.aon.ui.product.controller.ItemAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.sun.faces.util.MessageFactory;

public class ItemAttachControllerListener extends ControllerAdapter implements IItemMessages {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			ItemAttachController iaController = (ItemAttachController) event.getController();
			if ( iaController.getType() != null ) {
				IManagerBean iAttachBean = iaController.getManagerBean();
				Criteria criteria = iaController.getCriteria();
				criteria.addEqualExpression(iAttachBean.getFieldName(IEntityAlias.ITEM_ATTACHMENT_TYPE), iaController.getType());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ItemAttachController iaController = (ItemAttachController) event.getController();
		iaController.setAonFile(null);
		ItemAttachment attach = (ItemAttachment) iaController.getTo();
		if ( iaController.getType() != null ) {
			attach.setType( iaController.getType() );
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ItemAttachController iaController = (ItemAttachController) event.getController();
		iaController.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ItemAttachController iaController = (ItemAttachController) event.getController();
		checkFileData(iaController);
		updateItemAttachment(iaController);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ItemAttachController iaController = (ItemAttachController) event.getController();
		checkFileData(iaController);
		updateItemAttachment(iaController);
	}

	private void checkFileData( ItemAttachController ciaController ) throws ControllerListenerException {
		boolean ok = true;
		if ( ciaController.isNew() ) {
			ok = ciaController.isUploaded();
		} else {
			ItemAttachment attach = (ItemAttachment)ciaController.getTo();
			ok = (attach.getData() != null) && (! ArrayUtils.isEmpty(attach.getData()));
		}
		if (! ok ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage("aon_fileupload_element") );
			throw new ControllerListenerException( message.getSummary() );			
		} else if ( ciaController.isUploaded() && ciaController.isMaximumSizeExceeded() ) {
	        String message = AonUtil.getMessage(BUNDLE_NAME, PRODUCT_DOCUMENT_MAX_SIZE_ERROR, ciaController.getMaximumSize());
			throw new ControllerListenerException(message);			
		}
	}
	
	private void updateItemAttachment( ItemAttachController ciaController ) throws ControllerListenerException {
		try {			
			if ( ciaController.isUploaded() ) {
				AonFile aonFile = ciaController.getAonFile();
				ItemAttachment attach = (ItemAttachment)ciaController.getTo();
				attach.setData(aonFile.getData());				
				MimeType mt = MimeResolver.getMimeTypeByExtension(aonFile.getFileName());
				if ( mt == null ) {
					mt = MimeResolver.getMimeType(aonFile.getData());
				}
				attach.setMimeType(mt);
				if ( StringUtils.isBlank(attach.getDescription()) ) {
					attach.setDescription(FilenameUtils.getBaseName(aonFile.getFileName()));
				} else {
					if (attach.getDescription().indexOf(".") < 0) {
						String ext = FilenameUtils.getExtension(aonFile.getFileName());
						attach.setDescription(attach.getDescription() + "." + ext);
					}
				}
			}
		} catch (Throwable th) {
			throw new ControllerListenerException("Error uploading file");
		}		
	}
}