package com.code.aon.faces.controller.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class AttachmentControllerListener extends ControllerAdapter implements ICommonConstants {

	private static final Logger LOGGER = Logger.getLogger(AttachmentControllerListener.class.getName());

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		AttachmentController controller = (AttachmentController) event.getController();
		controller.setAonFile(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		AttachmentController controller = (AttachmentController) event.getController();
		controller.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AttachmentController controller = (AttachmentController) event.getController();
		checkFileData(controller);
		updateAttachment(controller);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AttachmentController controller = (AttachmentController) event.getController();
		checkFileData(controller);
		updateAttachment(controller);
	}

	private void checkFileData( AttachmentController controller ) throws ControllerListenerException {
		boolean ok = true;
		if ( controller.isNew() ) {
			ok = controller.isUploaded();
		} else {
			IAttachment attach = (IAttachment)controller.getTo();
			ok = (attach.getData() != null) && (! ArrayUtils.isEmpty(attach.getData()));
		}
		if (! ok ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
			throw new ControllerListenerException( message.getSummary() );			
		} else if ( controller.isUploaded() && controller.isMaximumSizeExceeded() ) {
	        String message = AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, DOCUMENT_MAX_SIZE_ERROR, controller.getMaximumSize());
			throw new ControllerListenerException(message);			
		}
	}
	
	private void updateAttachment( AttachmentController controller ) throws ControllerListenerException {
		try {			
			if ( controller.isUploaded() ) {
				AonFile aonFile = controller.getAonFile();
				IAttachment attach = (IAttachment)controller.getTo();
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
			throw new ControllerListenerException(AonUtil.getMessage(FILE_UPLOAD_ERROR));
		}		
	}
	
}
