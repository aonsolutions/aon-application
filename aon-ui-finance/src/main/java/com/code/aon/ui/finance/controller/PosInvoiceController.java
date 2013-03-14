package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.finance.event.PosInvoiceControllerListener;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {

	private Pos pos;
	private Seller seller;
	private List<Invoice> suspendedInvoiceList;
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
	public void onReset(ActionEvent event) {
		super.onReset(event);
		setSuspendedInvoiceList(null);
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
		try {
			if (isNew()) {
				PosInvoiceControllerListener invoiceControllerListener = (PosInvoiceControllerListener)AonUtil.getRegisteredBean(POS_INVOICE_CONTROLLER_LISTENER_NAME);
				Series series = invoiceControllerListener.obtainPosSeries();

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_POS_ID), getPos().getId());
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
				if (series == null) {
					criteria.addNullExpression(getFieldName(IEntityAlias.INVOICE_SERIES));
				} else {
					criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_SERIES), series.getCode());
				}
				criteria.addOrder(getFieldName(IEntityAlias.INVOICE_NUMBER), false);
				for (ITransferObject ito : getManagerBean().getList(criteria)) {
					Invoice invoice = (Invoice)ito;
					if (!invoice.isSigned() && !invoice.isRecorded() && invoice.getDetailList().size() == 0) {
						getManagerBean().remove(invoice);
					}
					break;
				}
			} else if (getInvoice().getDetailList().size() == 0) {
				getManagerBean().remove(getInvoice());
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al Borrar Factura vacia.";
			AonUtil.addErrorMessage(msg);
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
				String msg = "Error al cobrar la Factura.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}

		getInvoice().setComments("Cobrado: " + new Date() + "\n");
		accept(event);
	}

	public void onCancelTicket(ActionEvent event) {
		try {
			cancelTicket(true);

			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			if (!isNew()) {
				refresh(event);
				detailController.onSearch(event);
			}
			detailController.onReset(event);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar la Factura.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelLine(ActionEvent event) {
		try {
			cancelTicket(false);

			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			if (!isNew()) {
				refresh(event);
				detailController.onSearch(event);
			}
			detailController.onReset(event);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar la Factura.";
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

		if (entireTicket && invoiceDetailList.size() == 0) {
			getManagerBean().remove(getInvoice());
			onReset(null);
		}
	}

	public List<Invoice> getSuspendedInvoiceList() throws ManagerBeanException {
		if (suspendedInvoiceList == null) {
			suspendedInvoiceList = new LinkedList<Invoice>();
			Criteria criteria = new Criteria();
			if (!isNew()) {
				criteria.addNotEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), getInvoice().getId());
			}
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_POS_ID), getPos().getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), new Date());
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
			criteria.addNullExpression(getFieldName(IEntityAlias.INVOICE_COMMENTS));
			for (ITransferObject ito : getManagerBean().getList(criteria)) {
				Invoice invoice = (Invoice)ito;
				suspendedInvoiceList.add(invoice);
			}
		}
		return suspendedInvoiceList;
	}

	public void setSuspendedInvoiceList(List<Invoice> suspendedInvoiceList) {
		this.suspendedInvoiceList = suspendedInvoiceList;
	}

	public int getSuspendedInvoiceCount() throws ManagerBeanException {
		return getSuspendedInvoiceList().size();
	}

	public void onRecoverSuspendedInvoice(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Integer suspendedInvoiceId = new Integer(params.get("suspendedInvoice"));
		try {
			load(event, suspendedInvoiceId);
		} catch (ManagerBeanException ex) {
			String msg = "Error al recuperar la Factura aparcada.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}