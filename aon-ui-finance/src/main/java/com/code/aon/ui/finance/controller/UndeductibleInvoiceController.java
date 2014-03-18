package com.code.aon.ui.finance.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.util.CreditorValidationManager;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UndeductibleInvoiceController extends InvoiceController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UndeductibleInvoiceController.class.getName());
	
	private RegistryValidationManager vm;

	public UndeductibleInvoiceController() {
		setInvoiceAddressControllerName(UNDEDUCTIBLE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(UNDEDUCTIBLE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(UNDEDUCTIBLE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new CreditorValidationManager(); 
		}
		return vm;
	}
	
	public void creditorData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Creditor creditor = (Creditor)event.getNewValue();
			isBlocked(creditor); // Saca el mensaje de bloqueo.
			getInvoice().setRegistryName(creditor.getRegistry().getFullName());
			getInvoice().setRegistryDocument(creditor.getRegistry().getDocument());
			getInvoice().setRegistryDocumentType(creditor.getRegistry().getDocumentType());
			getInvoice().setRegistryDocumentCountry(creditor.getRegistry().getDocumentCountry());
			getInvoice().setRegistry(creditor.getRegistry());
			loadAddresses(creditor.getId());
			loadProjects(creditor.getId());
			validateInvoice();
		} else {
			Invoice invoice = getInvoice();
			invoice.setRegistryAddress(null);
			invoice.setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());

			setAddresses(null);	
			setProjects(null);	
		}
	}

	private boolean isBlocked(Creditor creditor) {
		return getRegistryValidationManager().isBlocked(creditor);
	}

	@Override
	public IAttachment generateReportAttachment( ITransferObject to ) {
		IManagerBean bean = getAttachmentBean();
		try {
			Criteria criteria = new Criteria();
			String invoiceAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_INVOICE_ID);
			criteria.addEqualExpression(invoiceAlias, ((Invoice)to).getId());
			String typeAlias;
			typeAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_MIME_TYPE);
			criteria.addEqualExpression(typeAlias, MimeType.MIME_PDF);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (IAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error getting invoice pdf file " + to, e );
		}
		return null;
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		IAttachment attachment = generateReportAttachment(to);
		if ( attachment == null ) {
			attachment = super.generateReportAttachment(to);
		}
		return attachment;
	}		

	@Override
	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(UNDEDUCTIBLE_INVOICE_SIGNER_CONTROLLER_NAME);
	}

}