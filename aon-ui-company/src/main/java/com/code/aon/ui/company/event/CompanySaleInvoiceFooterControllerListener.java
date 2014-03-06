package com.code.aon.ui.company.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_TEXT;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanySaleInvoiceFooterController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Listener added to the CompanySaleInvoiceFooterController
 * 
 */
public class CompanySaleInvoiceFooterControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySaleInvoiceFooterControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			String alias = this.getController().getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
			this.getController().getCriteria().addEqualExpression(alias, RegistryAttachmentType.INVOICE_FOOTER_TEXT);
		} catch (ManagerBeanException e) {
			String msg = "Se ha priducido un error de lectura. Vuela a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			CompanySaleInvoiceFooterController controller = (CompanySaleInvoiceFooterController) this.getController();
			if(controller.getModel().getRowCount()==0){
				controller.onReset(null);
				controller.setText("");
			} else {
				controller.onSelectFirst(null);
				controller.setText(new String(((RegistryAttachment)controller.getTo()).getData()));
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha priducido un error de lectura. Vuela a intentarlo pasados unos segundos.";
			LOGGER.error( msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		completeAttachInfo();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		completeAttachInfo();
	}

	private void completeAttachInfo() {
		CompanySaleInvoiceFooterController controller = (CompanySaleInvoiceFooterController) this.getController();
		RegistryAttachment attach = (RegistryAttachment)controller.getTo(); 
		attach.setData(controller.getText().getBytes());
		attach.setRegistryAttachmentType(RegistryAttachmentType.INVOICE_FOOTER_TEXT);
		attach.setDescription(AonUtil.getMessage(COMPANY_SALE_INVOICE_FOOTER_TEXT));
		attach.setAttachDate(new Date());
		attach.setMimeType(MimeType.MIME_TXT);
	}
	
}
