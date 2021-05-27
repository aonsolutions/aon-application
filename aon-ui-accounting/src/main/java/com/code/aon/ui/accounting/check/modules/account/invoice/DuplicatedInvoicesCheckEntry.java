package com.code.aon.ui.accounting.check.modules.account.invoice;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class DuplicatedInvoicesCheckEntry extends CheckEntryAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String fixLabel = "Ver facturas";

	private InvoiceType type;
	private Date issueDate;
	private String document;
	private Double total;
	private String invoiceViewer = null;
	
	public DuplicatedInvoicesCheckEntry(InvoiceType type,Date issueDate,String document,Double total) {
		this.type = type; 
		this.issueDate = issueDate; 
		this.document = document;
		this.total = total;
	}
	

	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel;
	}
	
	@Override
	public void onFix(ActionEvent event) throws AonCheckException{
		try {
			String invoiceControllerName = null;
			if (type == InvoiceType.SALES) {
				invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.SALE_INVOICE_FORM_NAME;
			} else if (type == InvoiceType.PURCHASE) {
				invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.PURCHASE_INVOICE_FORM_NAME;
			} else if (type == InvoiceType.EXPENSES) {
				invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.EXPENSE_INVOICE_FORM_NAME;
			} else if (type == InvoiceType.UNDEDUCTIBLE) {
				invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME;
			}
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.onEditSearch(event);

			String documentAlias = invoiceController.getFieldName(IEntityAlias.INVOICE_REGISTRY_DOCUMENT);
			String issueDateAlias = invoiceController.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE);
			String totalAlias = invoiceController.getFieldName(IEntityAlias.INVOICE_TOTAL);
			
			invoiceController.getCriteria().addEqualExpression(issueDateAlias, issueDate);
			invoiceController.getCriteria().addEqualExpression(documentAlias, document);
			invoiceController.getCriteria().addEqualExpression(totalAlias, total);
			invoiceController.onSearch(event);
			invoiceController.getModel().setRowIndex(0);
			invoiceController.select(event);		
			invoiceController.setBackAction("check_list");
			invoiceController.setBackActionListener("accountCheck.onExecute");
		} catch (ManagerBeanException e) {
			String message = "No se pudo desmarcar la factura. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	@Override
	public String fixAction() throws AonCheckException {
		return invoiceViewer;
	}
	
}
