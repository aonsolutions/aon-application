package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_ALREADY_RECORDED_ERROR;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class InvoiceControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		InvoiceController controller = (InvoiceController)event.getController();
		controller.resetListTotals();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			InvoiceController invoiceController = (InvoiceController)this.getController(); 
			Invoice invoice = (Invoice)invoiceController.getTo();
			invoice.setStatus(InvoiceStatus.PENDING);
			invoice.setRectificationType(RectificationType.NONE);
			invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
			invoice.setTaxDate(invoice.getIssueDate());
			invoice.setTransaction(InvoiceTransactionType.NATIONAL);
			invoice.setSkipCalculateMainActivity(companyColls.getActiveCompanyActivitiesCount() > 1);

			invoiceController.initSeries();
			invoiceController.loadAddresses(null);
			invoiceController.setProjects(null);
			invoiceController.setSavedProject(null);
			invoiceController.setShowProjectLookup(false);
			invoiceController.setSavedSeller(null);
			invoiceController.setSavedInvestAsset(null);
			invoiceController.setFinanceGenerationMode(0);
			invoiceController.setInvoiceAttachFile(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceController invoiceController = (InvoiceController)this.getController(); 
			Invoice invoice = (Invoice)invoiceController.getTo();

			invoiceController.initSeries(false);
			invoiceController.loadAddresses(invoice.getRegistry().getId());
			invoiceController.setProjects(null);
			invoiceController.setSavedProject(invoice.getProject());
			invoiceController.setShowProjectLookup(true);
			invoiceController.setSavedSeller(invoice.getSeller());
			invoiceController.setSavedInvestAsset(invoice.getInvestAsset());
			invoiceController.setFinanceGenerationMode(0);
			invoiceController.setInvoiceAttachFile(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();

		invoiceController.setSavedProject(invoice.getProject());
		invoiceController.setShowProjectLookup(true);
		invoiceController.setSavedSeller(invoice.getSeller());
		invoiceController.setSavedInvestAsset(invoice.getInvestAsset());
		saveAttach();
	}

	private void saveAttach() throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		AonFile aonFile = invoiceController.getInvoiceAttachFile();
		if(aonFile!=null && aonFile.getSize()>0){
			IManagerBean attachBean = invoiceController.getAttachmentBean();
			InvoiceAttachment attach = (InvoiceAttachment) invoiceController.newAttachment(invoice, aonFile.getMimeType());
			attach.setData(aonFile.getData());
			attach.setDescription(aonFile.getFileName());
			attach.setAttachDate(new Date());
			try {
				attachBean.insert(attach);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		if (!invoice.isRecorded() && invoiceController.checkRecorded(invoice)) {
			invoiceController.refreshEntireInvoice();
			throw new ControllerListenerException(AonUtil.getMessage(FINANCE_INVOICE_ALREADY_RECORDED_ERROR));
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		try {
			invoiceController.linkProject(invoiceController.getInvoice(), true);
			invoiceController.linkSeller(invoiceController.getInvoice(), true);
			invoiceController.linkInvestAsset(invoiceController.getInvoice(), true);
			invoiceController.autoGenerateIncreases();
			invoiceController.autoGenerateFinances();
			invoiceController.resetListTotals();

			IController invoiceDetailController = FormUtil.getController(invoiceController.getInvoiceDetailControllerName());
			invoiceDetailController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}
