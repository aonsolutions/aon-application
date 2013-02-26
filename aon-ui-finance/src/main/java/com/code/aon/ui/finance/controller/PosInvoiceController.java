package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.Pos;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class PosInvoiceController extends SaleInvoiceController {

	private Pos pos;
	private Seller seller;
	private boolean showPosSelectionWindow;
	private boolean showFinishTicketWindow;

	public PosInvoiceController() {
		setInvoiceAddressControllerName(POS_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(POS_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(POS_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos= pos;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller= seller;
	}

	public boolean isShowPosSelectionWindow() {
		return showPosSelectionWindow;
	}

	public void setShowPosSelectionWindow(boolean value) {
		this.showPosSelectionWindow = value;
	}

	public boolean isShowFinishTicketWindow() {
		return showFinishTicketWindow;
	}

	public void setShowFinishTicketWindow(boolean value) {
		this.showFinishTicketWindow = value;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		setPos((Pos)BeanManager.getManagerBean(Pos.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		onReset(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(null);
	}

	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	public void onNewTicket(ActionEvent event) {
		if (!isNew() && getInvoice().getDetailList().size() == 0) {
			try {
				getManagerBean().remove(getInvoice());
			} catch (ManagerBeanException ex) {
				String msg = "Error al Borrar Ticket vacio.";
				AonUtil.addErrorMessage(msg);
			}
		}
		onReset(event);

		Invoice invoice = getInvoice();
		if (invoice.getRegistry() == null || invoice.getRegistry().getId() == null) {
			String msg = "No hay Cliente Contado definido.";
			AonUtil.addErrorMessage(msg);
		} else {
			accept(event);
		}
	}

	public void onShowFinishTicket(ActionEvent event) {
		accept(event);

		FormUtil.getController(getInvoiceFinanceControllerName()).onReset(event);
	}

	public void onFinishTicket(ActionEvent event) {
		InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		if (financeController.getPaidAmount() < getInvoice().getTotal() && financeController.getPaidAmount() > 0) {
			((Finance)financeController.getTo()).setAmount(financeController.getPaidAmount());
		}
		financeController.onAccept(event);
	}

}