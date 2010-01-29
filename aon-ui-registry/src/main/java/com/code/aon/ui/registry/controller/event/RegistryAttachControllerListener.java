package com.code.aon.ui.registry.controller.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class RegistryAttachControllerListener extends ControllerAdapter implements IRegistryConstants {

	private static final Logger LOGGER = Logger.getLogger(RegistryAttachControllerListener.class.getName());

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			RegistryAttachController raController = (RegistryAttachController) event.getController();
			if ( raController.getType() != null ) {
				IManagerBean rAttachBean = raController.getManagerBean();
				Criteria criteria = raController.getCriteria();
				criteria.addEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), raController.getType());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		raController.setAonFile(null);
		RegistryAttachment attach = (RegistryAttachment) raController.getTo();
		if ( raController.getType() != null ) {
			attach.setRegistryAttachmentType( raController.getType() );
		}
		attach.setCategory(null);		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		raController.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		checkFileData(raController);
		updateRegistryAttachment(raController);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		checkFileData(raController);
		updateRegistryAttachment(raController);
	}

	private void checkFileData( RegistryAttachController ciaController ) throws ControllerListenerException {
		boolean ok = true;
		if ( ciaController.isNew() ) {
			ok = ciaController.isUploaded();
		} else {
			RegistryAttachment attach = (RegistryAttachment)ciaController.getTo();
			ok = (attach.getData() != null) && (! ArrayUtils.isEmpty(attach.getData()));
		}
		if (! ok ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage("aon_fileupload_element") );
			throw new ControllerListenerException( message.getSummary() );			
		} else if ( ciaController.isUploaded() && ciaController.isMaximumSizeExceeded() ) {
	        String message = AonUtil.getMessage(BUNDLE_NAME, REGISTRY_DOCUMENT_MAX_SIZE_ERROR, ciaController.getMaximumSize());
			throw new ControllerListenerException(message);			
		}
	}
	
	private void updateRegistryAttachment( RegistryAttachController ciaController ) throws ControllerListenerException {
		try {			
			if ( ciaController.isUploaded() ) {
				AonFile aonFile = ciaController.getAonFile();
				RegistryAttachment attach = (RegistryAttachment)ciaController.getTo();
				attach.setData(aonFile.getData());				
				String ext = FilenameUtils.getExtension(aonFile.getFileName());
				MimeType mt = null;
				if ( StringUtils.isEmpty(ext) ) {
					try {
						MagicMatch match = Magic.getMagicMatch(aonFile.getData());
						ext = match.getExtension();
						mt = MimeType.get(match.getMimeType());
					} catch (Throwable th) {
						LOGGER.log(Level.SEVERE, "Error finding file Mime Type", th );
					}
				} else {
					mt = MimeType.getByExtension(ext);	
				}
				attach.setMimeType(mt);
				if ( StringUtils.isBlank(attach.getDescription()) ) {
					attach.setDescription(FilenameUtils.getBaseName(aonFile.getFileName()));
				} else {
					if (attach.getDescription().indexOf(".") < 0) {
						attach.setDescription(attach.getDescription() + "." + ext);
					}
				}
			}
		} catch (Throwable th) {
			throw new ControllerListenerException("Error uploading file");
		}		
	}
	
}
