package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {

	private Pos pos;
	private Seller seller;
	private boolean showPosSelectionWindow;
	private boolean showFinishTicketWindow;
	private boolean showRecoverTicketWindow;

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

	public boolean isShowRecoverTicketWindow() {
		return showRecoverTicketWindow;
	}

	public void setShowRecoverTicketWindow(boolean value) {
		this.showRecoverTicketWindow = value;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		FinanceCollectionsController financeCollections = (FinanceCollectionsController)AonUtil.getRegisteredBean(IFinanceConstants.COLLECTIONS_CONTROLLER_NAME);
		if (financeCollections.getCurrentUserPosCount() == 1) {
			setPos((Pos)financeCollections.getCurrentUserPosList().get(0));
		} else {
			setPos((Pos)BeanManager.getManagerBean(Pos.class).createNewTo());
		}
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		onReset(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(null);
	}

	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	public DataModel getTicketModel() {
		List<Invoice> ticketList = new LinkedList<Invoice>();
		ticketList.add(getInvoice());
		return new ListDataModel(ticketList);
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

		PosInvoiceFinanceController financeController = (PosInvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		financeController.resetFinances();
		financeController.onNewFinance(event);
	}

	public void onFinishTicket(ActionEvent event) {
		PosInvoiceFinanceController financeController = (PosInvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		if (!financeController.isFinancesPayMethodOk()) {
			String msg = "Especifique la Forma de Pago.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (!financeController.isFinancesCardOk()) {
			String msg = "Importe incorrecto en Pago con Tarjetas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		double returnChange = financeController.getFinancesCashChange();
		Invoice invoice = getInvoice();
		for (Finance finance : financeController.getFinances()) {
			finance.setPayment(false);
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setRegistryName(invoice.getRegistryName());
			finance.setRegistryDocument(invoice.getRegistryDocument());
			finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
			finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
	        finance.setConcept(invoice.getDocumentNumber()); 
			finance.setDueDate(invoice.getIssueDate());
			finance.setSecurityLevel(invoice.getSecurityLevel());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS && returnChange > 0) {
				finance.setAmount(CommonUtil.round(financeController.getFinancesCashAmount() - returnChange));
			}

			try {
				BeanManager.getManagerBean(Finance.class).insert(finance);
			} catch (ManagerBeanException ex) {
				String msg = "Error al cobrar el Ticket.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		financeController.onSearch(event);
	}

	public void onCancelTicket(ActionEvent event) {
		try {
			cancelTicket(true);

			refresh(event);
			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			detailController.onSearch(event);
			detailController.onReset(event);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar el Ticket.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelLine(ActionEvent event) {
		try {
			cancelTicket(false);

			refresh(event);
			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			detailController.onSearch(event);
			detailController.onReset(event);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar el Ticket.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void cancelTicket(boolean entireTicket) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), entireTicket);
		List<ITransferObject> invoiceDetailList = invoiceDetailBean.getList(criteria);
		int line = invoiceDetailList.size();
		for (ITransferObject ito : invoiceDetailList) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.fillTaxDataInDetail();
			if (entireTicket) {
				invoiceDetail.getInvoice().setUpdateEnabled(invoiceDetailList.indexOf(invoiceDetail) == (invoiceDetailList.size()-1));
			} else {
				if (invoiceDetail.getQuantity() < 0 || line != invoiceDetailList.size()) {
					break;
				}
			}

			invoiceDetail.setId(null);
			invoiceDetail.setLine(++line);
			invoiceDetail.setQuantity(invoiceDetail.getQuantity() * (-1));
			invoiceDetail.setTaxableBase(invoiceDetail.getTaxableBase() * (-1));
			invoiceDetail.setTaxDataInDetail(true);
			invoiceDetail.setVatQuota(invoiceDetail.getVatQuota() * (-1));
			invoiceDetail.setRetentionQuota(invoiceDetail.getRetentionQuota() * (-1));

			invoiceDetailBean.insert(invoiceDetail);
		}
	}

}