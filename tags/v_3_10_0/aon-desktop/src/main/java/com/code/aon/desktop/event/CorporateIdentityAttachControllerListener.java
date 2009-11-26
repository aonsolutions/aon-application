package com.code.aon.desktop.event;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.desktop.controller.CorporateIdentityAttachController;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class CorporateIdentityAttachControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(CorporateIdentityAttachControllerListener.class.getName());
	
	private static final String COMPANY_CONTROLLER_NAME = "company"; 

    /** BASE_NAME. */
    private static final String BASE_NAME = "com.code.aon.desktop.i18n.messages";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Expression imageExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.CORPORATE_IDENTITY);
			event.getController().getCriteria().addExpression(imageExp);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CorporateIdentityAttachController ciaController = (CorporateIdentityAttachController)event.getController();
		ciaController.setAonFile(null);
		RegistryAttachment attach = (RegistryAttachment) ciaController.getTo();
		CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
		attach.setRegistryAttachmentType(RegistryAttachmentType.CORPORATE_IDENTITY);				
		attach.setRegistry((Company)companyController.getTo());
		attach.setCategory(null);		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CorporateIdentityAttachController ciaController = (CorporateIdentityAttachController)event.getController();
		ciaController.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CorporateIdentityAttachController ciaController = (CorporateIdentityAttachController)event.getController();
		checkFileData(ciaController);
		updateRegistryAttachment(ciaController);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CorporateIdentityAttachController ciaController = (CorporateIdentityAttachController)event.getController();
		checkFileData(ciaController);
		updateRegistryAttachment(ciaController);
	}

	private void checkFileData( CorporateIdentityAttachController ciaController ) throws ControllerListenerException {
		boolean ok = true;
		if ( ciaController.isNew() ) {
			ok = ciaController.isUploaded();
		} else {
			RegistryAttachment attach = (RegistryAttachment)ciaController.getTo();
			ok = (attach.getData() != null) && (attach.getData().length > 0);
		}
		if (! ok ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage("aon_fileupload_element") );
			throw new ControllerListenerException( message.getSummary() );			
		}
	}
	
	private void updateRegistryAttachment( CorporateIdentityAttachController ciaController ) throws ControllerListenerException {
		try {
			AonFile aonFile = ciaController.getAonFile();			
			if( (aonFile != null) && (aonFile.getData() != null) ) { 
				if ( aonFile.getSize() > 1048576 ) {
			        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
					throw new ControllerListenerException(bundle.getString("company_image_max_size_error"));
				}
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
			} else {
				throw new ControllerListenerException("Borracho !!");
			}
		} catch (Throwable th) {
			throw new ControllerListenerException("Error uploading file");
		}		
	}
	
}
