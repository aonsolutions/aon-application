package com.code.aon.ui.fiscal.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.fiscal.RentingDetail;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RentingDetailController extends LinesController {

	private static final String EXPENSE_INVOICE_CONTROLLER = "expenseInvoice";
	private static final String EXPENSE_INVOICE_VIEW = "expenseInvoice_form";
	private static final String VIEW_NAME = "renting_form";
	private String invoiceViewer;

	public String getInvoiceViewer() {
		return invoiceViewer;
	}
	// Action Method
	public String invoiceView() {
		return invoiceViewer;
	}
	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}

	public void onLoadInvoice(ActionEvent event) {
		try {
			RentingDetail detail = (RentingDetail) getModel().getRowData();
			setInvoiceViewer(EXPENSE_INVOICE_VIEW);
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(EXPENSE_INVOICE_CONTROLLER);
			invoiceController.setBackAction(VIEW_NAME);
			invoiceController.onEditSearch(event);
			String alias0 = invoiceController.getManagerBean().getFieldName(IEntityAlias.INVOICE_REGISTRY_DOCUMENT);
			String alias1 = invoiceController.getManagerBean().getFieldName(IEntityAlias.INVOICE_REGISTRY_NAME);
			String alias2 = invoiceController.getManagerBean().getFieldName(IEntityAlias.INVOICE_ISSUE_DATE);
			invoiceController.getCriteria().addEqualExpression(alias0, detail.getDocument());
			invoiceController.getCriteria().addEqualExpression(alias1, detail.getName());
			Date startDate = detail.getRenting().getPeriod().getStartDate(detail.getRenting().getYear());
			Date dueDate = detail.getRenting().getPeriod().getDueDate(detail.getRenting().getYear());
			invoiceController.getCriteria().addBetweenExpression(alias2, startDate,dueDate);
			invoiceController.onSearch(event);
			invoiceController.getModel().setRowIndex(0);
			invoiceController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la factura: " + e.getMessage();
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}
