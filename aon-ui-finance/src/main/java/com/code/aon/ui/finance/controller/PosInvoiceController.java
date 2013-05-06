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

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.report.ReportException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.finance.util.print.TicketPrinter;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {

	private Pos pos;
	private Seller seller;
	private List<Invoice> suspendedInvoiceList;
	private boolean showPosSelectionWindow;
	private boolean showFinishTicketWindow;
	private boolean showRecoverTicketWindow;
	private boolean showPrintTicketWindow;
	private boolean giftTicket;
	private String recoverSeries;
	private Integer recoverNumber;

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

	public String getRecoverSeries() {
		return recoverSeries;
	}

	public void setRecoverSeries(String recoverSeries) {
		this.recoverSeries = recoverSeries;
	}

	public Integer getRecoverNumber() {
		return recoverNumber;
	}

	public void setRecoverNumber(Integer recoverNumber) {
		this.recoverNumber = recoverNumber;
	}

	public String getRecoverInvoiceCode() {
		return ((getRecoverSeries() != null) ? getRecoverSeries() + "/" : "") + StringUtils.leftPad(Integer.toString(getRecoverNumber()), 6, "0");
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

	@Override
	public void refresh(ActionEvent event) throws ManagerBeanException {
		super.refresh(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onSearch(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	public DataModel getTicketModel() {
		List<Invoice> ticketList = new LinkedList<Invoice>();
		ticketList.add(getInvoice());
		return new ListDataModel(ticketList);
	}

	public void onNewTicket(ActionEvent event) {
		try {
			if (!isNew() && getInvoice().getDetailList().size() == 0) {
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
		financeController.setPendingAmount(getPendingAmount());
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
		if (!financeController.isFinancesAmountOk()) {
			String msg = "El Importe de los Pagos es incorrecto.";
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
		FormUtil.getController(getInvoiceFinanceControllerName()).onSearch(null);
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

	public void onSuspendTicket(ActionEvent event) {
		accept(event);
		onNewTicket(event);
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
		recoverTicket(suspendedInvoiceId);
	}

	private void recoverTicket(Integer invoiceId) {
		try {
			load(null, invoiceId);
			FormUtil.getController(getInvoiceDetailControllerName()).onReset(null);
			setSuspendedInvoiceList(null);
		} catch (ManagerBeanException ex) {
			String msg = "Error al recuperar la Factura aparcada.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowRecoverTicket(ActionEvent event) {
		setRecoverSeries(null);
		setRecoverNumber(null);
	}

	public void onRecoverTicket(ActionEvent event) {
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_POS_ID), getPos().getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			if (getRecoverSeries() == null) {
				criteria.addNullExpression(getFieldName(IEntityAlias.INVOICE_SERIES));
			} else {
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_SERIES), getRecoverSeries());
			}
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_NUMBER), getRecoverNumber());
			int recoverCount = getManagerBean().getCount(criteria);
			if (recoverCount == 0) {
				String msg = "No se ha encontrado la Factura " + getRecoverInvoiceCode() + " para este TPV.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if (recoverCount > 1) {
				String msg = "Error al recuperar la Factura " + getRecoverInvoiceCode() + ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				Invoice recoverInvoice = (Invoice)getManagerBean().getList(criteria).get(0);
				recoverTicket(recoverInvoice.getId());
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al recuperar la Factura " + getRecoverInvoiceCode() + ".";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelTicket(ActionEvent event) {
		try {
			cancelTicket(true);
			if (!isNew()) {
				refresh(event);
			} else {
				FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar la Factura.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelLine(ActionEvent event) {
		try {
			cancelTicket(false);
			if (!isNew()) {
				refresh(event);
			} else {
				FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
			}
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

	public void onReturnTicket(ActionEvent event) {
		Invoice invoice = getInvoice();
		try {
			Invoice returnInvoice = new Invoice();
			returnInvoice.setProject(invoice.getProject());
			returnInvoice.setSeries(invoice.getSeries());
			returnInvoice.setNumber(obtainMaxInvoiceNumber(invoice.getSeries()));
			returnInvoice.setRegistry(invoice.getRegistry());
			returnInvoice.setRegistryDocument(invoice.getRegistryDocument());
			returnInvoice.setRegistryDocumentType(invoice.getRegistryDocumentType());
			returnInvoice.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
			returnInvoice.setRegistryName(invoice.getRegistryName());
			returnInvoice.setRegistryAddress(invoice.getRegistryAddress());
			returnInvoice.setSecurityLevel(invoice.getSecurityLevel());
			returnInvoice.setStatus(InvoiceStatus.PENDING);
			returnInvoice.setType(InvoiceType.SALES);
			returnInvoice.setSurcharge(invoice.isSurcharge());
			returnInvoice.setWithholding(invoice.isWithholding());
			returnInvoice.setTransaction(invoice.getTransaction());
			returnInvoice.setPos(invoice.getPos());
			returnInvoice.setSeller(getSeller());
			getManagerBean().restoreNullSubPOJOs(returnInvoice);
			returnInvoice = (Invoice)getManagerBean().insert(returnInvoice);

			IController addressController = FormUtil.getController(getInvoiceAddressControllerName());
			InvoiceAddress returnAddress = (InvoiceAddress)addressController.getTo();
			if (returnAddress != null && returnAddress.getId() != null) {
				returnAddress.setId(null);
				returnAddress.setInvoice(returnInvoice);
				addressController.getManagerBean().restoreNullSubPOJOs(returnAddress);
				addressController.getManagerBean().insert(returnAddress);
			}

			int line = 0;
			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			for (InvoiceDetail returnDetail : detailController.getCheckedDetails()) {
				returnDetail.setId(null);
				returnDetail.setInvoice(returnInvoice);
				returnDetail.setLine(++line);
				returnDetail.setQuantity(CommonUtil.round(returnDetail.getQuantity() * (-1), 3));
				returnDetail.setTaxableBase(CommonUtil.round(returnDetail.getTaxableBase() * (-1), 4));
				returnDetail.setTaxDataInDetail(true);
				returnDetail.setVatQuota(returnDetail.getVatQuota() * (-1));
				returnDetail.setRetentionQuota(returnDetail.getRetentionQuota() * (-1));
				returnDetail.setUpdateEnabled(line == detailController.getCheckedCount());
				detailController.getManagerBean().restoreNullSubPOJOs(returnDetail);
				detailController.getManagerBean().insert(returnDetail);
			}

			recoverTicket(returnInvoice.getId());
		} catch (ManagerBeanException ex) {
			String msg = "Error al efectuar la Devolución.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public boolean isShowPrintTicketWindow() {
		return showPrintTicketWindow;
	}

	public void setShowPrintTicketWindow(boolean showPrintTicketWindow) {
		this.showPrintTicketWindow = showPrintTicketWindow;
	}

	public void onShowPrintTicket(ActionEvent event) {
		setShowPrintTicketWindow(true);
		this.giftTicket = false;
	}

	public void onShowPrintGiftTicket(ActionEvent event) {
		setShowPrintTicketWindow(true);
		this.giftTicket = true;
	}
	
	public String getTicketText() {
		try {
			TicketPrinter tp = new TicketPrinter();
			return tp.execute( getInvoice(), giftTicket );			
		} catch (ReportException ex) {
			String msg = AonUtil.getMessage(BUNDLE_NAME, POS_ERROR_PRINT_TICKET);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}