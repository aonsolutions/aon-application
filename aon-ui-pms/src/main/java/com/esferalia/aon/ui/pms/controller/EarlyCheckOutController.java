package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PMS_EARLY_CHECK_OUT;
import static com.code.aon.ui.common.ICommonMessages.PRICE_PATTERN;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.IAddress;
import com.code.aon.ui.common.role.BasicRoleManager;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.invoicing.PaymentSummaryTo;
import com.esferalia.aon.pms.invoicing.PenalizationInvoicing;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo;
import com.esferalia.aon.pms.invoicing.ReservationInvoicing;
import com.esferalia.aon.pms.invoicing.TotalSummaryTo;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class EarlyCheckOutController implements IPmsConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ReservationUtils reservationUtils;
	private ProjectReservation reservation;
	private ReservationInvoiceTo reservationInvoiceTo;
	private List<ProjectReservationServiceDetail> reservationUsedServices;
	private List<ProjectReservationServiceDetail> reservationNotUsedServices;
	private List<ITransferObject> reservationInvoicesToRectify;
	private List<Finance> reservationFinances;
	private List<PaymentSummaryTo> reservationPaymentSummary;
	private List<PaymentSummaryTo> servicesPaymentSummary;
	private boolean showEarlyCheckOutWindow;
	private boolean payCheckOut;
	private Integer penaltyDays;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils((getReservation() != null) ? getReservation().getDomain() : DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public ProjectReservation getReservation() {
		return reservation;
	}
	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
	}

	public ReservationInvoiceTo getReservationInvoiceTo() {
		return reservationInvoiceTo;
	}
	public void setReservationInvoiceTo(ReservationInvoiceTo reservationInvoiceTo) {
		this.reservationInvoiceTo = reservationInvoiceTo;
	}

	public List<ProjectReservationServiceDetail> getReservationUsedServices() {
		return reservationUsedServices;
	}
	public void setReservationUsedServices(List<ProjectReservationServiceDetail> reservationUsedServices) {
		this.reservationUsedServices = reservationUsedServices;
	}

	public List<ProjectReservationServiceDetail> getReservationNotUsedServices() {
		return reservationNotUsedServices;
	}
	public void setReservationNotUsedServices(List<ProjectReservationServiceDetail> reservationNotUsedServices) {
		this.reservationNotUsedServices = reservationNotUsedServices;
	}

	public List<ITransferObject> getReservationInvoicesToRectify() {
		return reservationInvoicesToRectify;
	}
	public void setReservationInvoicesToRectify(List<ITransferObject> reservationInvoicesToRectify) {
		this.reservationInvoicesToRectify = reservationInvoicesToRectify;
	}

	public List<Finance> getReservationFinances() {
		return reservationFinances;
	}
	public void setReservationFinances(List<Finance> reservationFinances) {
		this.reservationFinances = reservationFinances;
	}

	public List<PaymentSummaryTo> getReservationPaymentSummary() {
		return reservationPaymentSummary;
	}
	public void setReservationPaymentSummary(List<PaymentSummaryTo> reservationPaymentSummary) {
		this.reservationPaymentSummary = reservationPaymentSummary;
	}

	public List<PaymentSummaryTo> getServicesPaymentSummary() {
		return servicesPaymentSummary;
	}
	public void setServicesPaymentSummary(List<PaymentSummaryTo> servicesPaymentSummary) {
		this.servicesPaymentSummary = servicesPaymentSummary;
	}

	public boolean isShowEarlyCheckOutWindow() {
		return showEarlyCheckOutWindow;
	}
	public void setShowEarlyCheckOutWindow(boolean showEarlyCheckOutWindow) {
		this.showEarlyCheckOutWindow = showEarlyCheckOutWindow;
	}

	public boolean isPayCheckOut() {
		return payCheckOut;
	}
	public void setPayCheckOut(boolean payCheckOut) {
		this.payCheckOut = payCheckOut;
	}

	public Integer getPenaltyDays() {
		return penaltyDays;
	}
	public void setPenaltyDays(Integer penaltyDays) {
		this.penaltyDays = penaltyDays;
	}

	public Date getEarlyCheckOutDate() {
		return getReservationInvoiceTo().getEarlyCheckOutDate();
	}

	public void onInit(ActionEvent event) {
		onInit();
	}

	public void onInit() {
		try {
			fillReservationInvoiceTo();
			setPenaltyDays(obtainCheckOutPenaltyDays());
			fillReservationServiceLists();
			if (validateEarlyCheckOutShow()) {
				setPayCheckOut(true);
				setReservationFinances(obtainReservationFinances());
				setReservationPaymentSummary(obtainPaymentSummary(false));
				setServicesPaymentSummary(obtainPaymentSummary(true));
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private void fillReservationInvoiceTo() throws ManagerBeanException {
		setReservationInvoiceTo(new ReservationInvoiceTo(false));
		getReservationInvoiceTo().setHotel(getReservation().getHotel());
		getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
		getReservationInvoiceTo().setDirectCustomer(getReservation().isGuestHolder());
		getReservationInvoiceTo().setEarlyCheckOut(true);
		getReservationInvoiceTo().setEarlyCheckOutDate(DateUtils.truncate(new Date(), Calendar.DATE));
		getReservationInvoiceTo().setPenaltyItem(getReservationUtils().obtainEarlyCheckOutItem());
		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
	}

	private void fillReservationServiceLists() throws ManagerBeanException {
		List<ProjectReservationServiceDetail> usedServicesList = new LinkedList<ProjectReservationServiceDetail>();
		List<ProjectReservationServiceDetail> notUsedServicesList = new LinkedList<ProjectReservationServiceDetail>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, getReservation().getId());
		if (getReservation().isAgencyHolder() && getEarlyCheckOutPenaltyDays() < 0) {
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, Boolean.TRUE);
		}
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_REMOVED);
		criteria.addEqualExpression(alias, Boolean.FALSE);
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_ID));
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			ProjectReservationService reservationService = reservationServiceDetail.getProjectReservationService();
			if (!reservationService.isExtra() || reservationService.getItem().getProduct().getType() == ProductType.SERVICE) {
				if (reservationServiceDetail.getEffectiveDate().before(getEarlyCheckOutDate())) {
					usedServicesList.add(reservationServiceDetail);
				} else {
					notUsedServicesList.add(reservationServiceDetail);
				}
			}
		}

		setReservationUsedServices(usedServicesList);
		setReservationNotUsedServices(notUsedServicesList);
		setReservationInvoicesToRectify(obtainReservationInvoicesToRectify(getReservation()));
		excludeNotRectifiedExtraServices();
	}

	private void excludeNotRectifiedExtraServices() throws ManagerBeanException {
		List<Integer> serviceDetailsToRectifyIds = obtainReservationExtraServiceDetailsToRectifyIds();
		List<ProjectReservationServiceDetail> serviceDetailsToExclude = new LinkedList<ProjectReservationServiceDetail>();
		for (ProjectReservationServiceDetail reservationServiceDetail : getReservationUsedServices()) {
			if (reservationServiceDetail.getProjectReservationService().isExtra() && !serviceDetailsToRectifyIds.contains(reservationServiceDetail.getId())) {
				serviceDetailsToExclude.add(reservationServiceDetail);
			}
		}
		getReservationUsedServices().removeAll(serviceDetailsToExclude);
	}

	private List<Integer> obtainReservationExtraServiceDetailsToRectifyIds() throws ManagerBeanException {
		List<Integer> serviceInvoicesToRectifyIds = new LinkedList<Integer>();
		for (ITransferObject ito : getReservationInvoicesToRectify()) {
			Invoice invoice = (Invoice)ito;
			if (invoice.isService()) {
				serviceInvoicesToRectifyIds.add(invoice.getId());
			}
		}

		List<Integer> extraServiceDetailsToRectifyIds = new LinkedList<Integer>();
		if (serviceInvoicesToRectifyIds.size() > 0) {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addInExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), serviceInvoicesToRectifyIds);
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				if (invoiceDetail.isReservationSource() && invoiceDetail.getSourceId() != null) {
					extraServiceDetailsToRectifyIds.add(invoiceDetail.getSourceId());
				}
			}
		}
		return extraServiceDetailsToRectifyIds;
	}

	private boolean validateEarlyCheckOutShow() throws ManagerBeanException {
		if (!PosUtils.isUserPosShiftOpened()) {
			setShowEarlyCheckOutWindow(false);
			String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getReservationInvoiceTo().getPenaltyItem() == null) {
			setShowEarlyCheckOutWindow(false);
			String msg = "No esta definido el Producto para Salidas Anticipadas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		for (ITransferObject ito : getReservationInvoicesToRectify()) {
    		Invoice invoiceToRectify = (Invoice)ito;
    		if (!FinanceUtil.isValidLimitRectificationDate(invoiceToRectify)) {
				setShowEarlyCheckOutWindow(false);
    			String msg = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
    			throw new AbortProcessingException(msg);
    		}
		}

		return true;
	}

	private List<ITransferObject> obtainReservationInvoicesToRectify(ProjectReservation reservation) throws ManagerBeanException {
		boolean onlyServices = reservation.isAgencyHolder() && getEarlyCheckOutPenaltyDays() < 0;
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservation.getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		criteria.addNotEqualExpression("Invoice.lines.item.product.type", ProductType.EXTERNAL_WORK);
		if (getReservationNotUsedExtraServiceIds().size() == 0) {
			if (!onlyServices) {
				criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), Boolean.FALSE);
			} else {
				return new LinkedList<ITransferObject>();
			}
		} else {
			Expression serviceExpr = ExpressionUtilities.getEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), onlyServices);
			Expression sourceExpr = ExpressionUtilities.getEqualExpression("Invoice.lines.source", InvoiceSource.RESERVATION);
			Expression sourceIdExpr = ExpressionUtilities.getInExpression("Invoice.lines.sourceId", getReservationNotUsedExtraServiceIds());
			sourceExpr = ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr);
			if (onlyServices) {
				criteria.addExpression(ExpressionUtilities.getAndExpression(serviceExpr, ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr)));
			} else {
				criteria.addExpression(ExpressionUtilities.getOrExpression(serviceExpr, ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr)));
			}
		}
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), Boolean.FALSE);
		return invoiceBean.getList(criteria);
	}

	private Integer obtainCheckOutPenaltyDays() throws ManagerBeanException {
		if (getReservation().isAgencyHolder() && !getEarlyCheckOutDate().after(getReservation().getEndDate())) {
			String penaltyValue = getReservationUtils().obtainEarlyCheckOutPenaltyValue(getReservation(), getEarlyCheckOutDate());
			return getReservation().getPenaltyDays(penaltyValue);
		}
		return null;
	}

	private List<Finance> obtainReservationFinances() throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), Boolean.FALSE);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), getReservation().getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		criteria.addNotEqualExpression("Finance.invoice<lines.item.product.type", ProductType.EXTERNAL_WORK);
		if (getReservationNotUsedExtraServiceIds().size() == 0) {
			if (!getReservation().isAgencyHolder()) {
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_SERVICE), Boolean.FALSE);
			} else {
				return financeList;
			}
		} else {
			Expression serviceExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_SERVICE), reservation.isAgencyHolder());
			Expression sourceExpr = ExpressionUtilities.getEqualExpression("Finance.invoice<lines.source", InvoiceSource.RESERVATION);
			Expression sourceIdExpr = ExpressionUtilities.getInExpression("Finance.invoice<lines.sourceId", getReservationNotUsedExtraServiceIds());
			sourceExpr = ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr);
			if (reservation.isAgencyHolder()) {
				criteria.addExpression(ExpressionUtilities.getAndExpression(serviceExpr, ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr)));
			} else {
				criteria.addExpression(ExpressionUtilities.getOrExpression(serviceExpr, ExpressionUtilities.getAndExpression(sourceExpr, sourceIdExpr)));
			}
		}
		criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE));
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			financeList.add(finance);
		}
		return financeList;
	}

	private List<PaymentSummaryTo> obtainPaymentSummary(boolean service) throws ManagerBeanException {
		List<PaymentSummaryTo> paymentSummary = new LinkedList<PaymentSummaryTo>();
		for (Finance finance : getReservationFinances()) {
			if (service == finance.getInvoice().isService()) {
				PaymentSummaryTo paymentSummaryTo = new PaymentSummaryTo();
				paymentSummaryTo.setPayMethod(finance.getPayMethod());
				paymentSummaryTo.setService(service);
				if (paymentSummary.contains(paymentSummaryTo)) {
					paymentSummaryTo = paymentSummary.get(paymentSummary.indexOf(paymentSummaryTo));
				}
				paymentSummaryTo.setReturnAmount(CommonUtil.round(paymentSummaryTo.getReturnAmount() + finance.getTotalAmount()));
				if (!paymentSummary.contains(paymentSummaryTo)) {
					paymentSummary.add(paymentSummaryTo);
				}
			}
		}

		double paymentAmount = getReservationUsedServicesAmount(service);
		for (PaymentSummaryTo paymentSummaryTo : paymentSummary) {
			if (paymentAmount > paymentSummaryTo.getReturnAmount()) {
				paymentSummaryTo.setPaymentAmount(paymentSummaryTo.getReturnAmount());
				paymentAmount = CommonUtil.round(paymentAmount - paymentSummaryTo.getReturnAmount());
			} else {
				paymentSummaryTo.setPaymentAmount(paymentAmount);
				break;
			}				
		}
		return paymentSummary;
	}

	private List<ProjectReservationServiceDetail> getReservationUsedExtraServices() throws ManagerBeanException {
		List<ProjectReservationServiceDetail> usedExtraServicesList = new LinkedList<ProjectReservationServiceDetail>();
		for (ProjectReservationServiceDetail reservationServiceDetail : getReservationUsedServices()) {
			if (reservationServiceDetail.getProjectReservationService().isExtra()) {
				usedExtraServicesList.add(reservationServiceDetail);
			}
		}
		return usedExtraServicesList;
	}

	private List<Integer> getReservationNotUsedExtraServiceIds() throws ManagerBeanException {
		List<Integer> notUsedExtraServicesList = new LinkedList<Integer>();
		for (ProjectReservationServiceDetail reservationServiceDetail : getReservationNotUsedServices()) {
			if (reservationServiceDetail.getProjectReservationService().isExtra()) {
				notUsedExtraServicesList.add(reservationServiceDetail.getId());
			}
		}
		return notUsedExtraServicesList;
	}

	private Map<Tax, Double> getReservationUsedServicesAmountMap(boolean service) throws ManagerBeanException {
		Map<Tax, Double> usedServicesAmountMap = new HashMap<Tax, Double>();
		for (ProjectReservationServiceDetail reservationServiceDetail : getReservationUsedServices()) {
			if (service == reservationServiceDetail.getProjectReservationService().isExtra()) {
				Tax vat = reservationServiceDetail.getItem().getProduct().getVat();
				double taxableBase = reservationServiceDetail.getTaxableBase();
				if (usedServicesAmountMap.containsKey(vat)) {
					taxableBase += usedServicesAmountMap.get(vat).doubleValue();
				}
				usedServicesAmountMap.put(vat, CommonUtil.round(taxableBase, 4));
			}
		}

		if (!service && getReservation().isGuestHolder() && getPenaltyDays() != null) {
			Tax vat = getReservationInvoiceTo().getPenaltyItem().getProduct().getVat();
			double amount = getEarlyCheckOutPenaltyAmount();
			if (usedServicesAmountMap.containsKey(vat)) {
				amount += usedServicesAmountMap.get(vat).doubleValue();
			}
			usedServicesAmountMap.put(vat, CommonUtil.round(amount, 4));
		}
		return usedServicesAmountMap;
	}

	private double getReservationUsedServicesAmount(boolean service) throws ManagerBeanException {
		double amount = 0;
		Map<Tax, Double> reservationUsedServicesAmountMap = getReservationUsedServicesAmountMap(service);
		for (Tax vat : reservationUsedServicesAmountMap.keySet()) {
			double vatPercent = vat.getDatedPercentage(getReservationInvoiceTo().getIssueDate());
			amount = CommonUtil.round(amount + reservationUsedServicesAmountMap.get(vat) * (1 + vatPercent / 100));
		}
		return amount;
	}

	public double getReservationTotalPaymentAmount() throws ManagerBeanException {
		return (getReservation().isGuestHolder()) ? getReservationUsedServicesAmount(false) : 0;
	}

	public double getReservationCurrentPaymentAmount() throws ManagerBeanException {
		double amount = 0;
		for (PaymentSummaryTo paymentSummaryTo : getReservationPaymentSummary()) {
			amount = CommonUtil.round(amount + paymentSummaryTo.getPaymentAmount());
		}
		return amount;
	}

	public double getReservationTotalReturnAmount() {
		double amount = 0;
		for (PaymentSummaryTo paymentSummaryTo : getReservationPaymentSummary()) {
			amount = CommonUtil.round(amount + paymentSummaryTo.getReturnAmount());
		}
		return amount;
	}

	public double getServicesTotalPaymentAmount() throws ManagerBeanException {
		return getReservationUsedServicesAmount(true);
	}

	public double getServicesCurrentPaymentAmount() throws ManagerBeanException {
		double amount = 0;
		for (PaymentSummaryTo paymentSummaryTo : getServicesPaymentSummary()) {
			amount = CommonUtil.round(amount + paymentSummaryTo.getPaymentAmount());
		}
		return amount;
	}

	public double getServicesTotalReturnAmount() {
		double amount = 0;
		for (PaymentSummaryTo paymentSummaryTo : getServicesPaymentSummary()) {
			amount = CommonUtil.round(amount + paymentSummaryTo.getReturnAmount());
		}
		return amount;
	}

	public List<TotalSummaryTo> getTotalSummary() throws ManagerBeanException {
		List<TotalSummaryTo> totalSummary = new LinkedList<TotalSummaryTo>();
		if (getReservationPaymentSummary().size() > 0 && getServicesPaymentSummary().size() > 0) {
			for (PaymentSummaryTo paymentSummaryTo : getReservationPaymentSummary()) {
				TotalSummaryTo totalSummaryTo = new TotalSummaryTo();
				totalSummaryTo.setPayMethod(paymentSummaryTo.getPayMethod());
				int index = totalSummary.indexOf(totalSummaryTo);
				if (index >= 0) {
					totalSummaryTo = totalSummary.get(index);
				} else {
					totalSummary.add(totalSummaryTo);
				}
				totalSummaryTo.setReservationAmount(CommonUtil.round(totalSummaryTo.getReservationAmount() + paymentSummaryTo.getLiquidationAmount()));
			}
			for (PaymentSummaryTo paymentSummaryTo : getServicesPaymentSummary()) {
				TotalSummaryTo totalSummaryTo = new TotalSummaryTo();
				totalSummaryTo.setPayMethod(paymentSummaryTo.getPayMethod());
				int index = totalSummary.indexOf(totalSummaryTo);
				if (index >= 0) {
					totalSummaryTo = totalSummary.get(index);
				} else {
					totalSummary.add(totalSummaryTo);
				}
				totalSummaryTo.setServicesAmount(CommonUtil.round(totalSummaryTo.getServicesAmount() + paymentSummaryTo.getLiquidationAmount()));
			}
		}
		return totalSummary;
	}

	public double getTotalAmount() throws ManagerBeanException {
		double amount = 0;
		for (TotalSummaryTo totalSummaryTo : getTotalSummary()) {
			amount = CommonUtil.round(amount + totalSummaryTo.getTotalAmount());
		}
		return amount;
	}

	public double getTotalAmountAbs() throws ManagerBeanException {
		return Math.abs(getTotalAmount());
	}

	public boolean isEarlyCheckOutDateEditable() throws ManagerBeanException {
		BasicRoleManager roleManager = AonUtil.getRoleManager();
		return ((roleManager.isConfig() || roleManager.isFinanceOperator()) && DateUtils.addDays(getReservation().getEndDate(), 1).before(new Date()));
	}

	public void onCheckOutDateChanged(ActionEvent event) {
		try {
			setPenaltyDays(obtainCheckOutPenaltyDays());
			fillReservationServiceLists();
			setReservationFinances(obtainReservationFinances());
			setReservationPaymentSummary(obtainPaymentSummary(false));
			setServicesPaymentSummary(obtainPaymentSummary(true));
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onCheckOutPenaltyChanged(ValueChangeEvent event) {
		setPenaltyDays((Integer)event.getNewValue());
		try {
			setReservationPaymentSummary(obtainPaymentSummary(false));
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public List<SelectItem> getCheckOutPenaltyDays() throws ManagerBeanException {
		NumberFormat formatter = new DecimalFormat(AonUtil.getMessage(PRICE_PATTERN));
		List<SelectItem> penaltyDays = new LinkedList<SelectItem>();
		penaltyDays.add(new SelectItem("0", "0 - 0,00 EUR."));
		if (DateUtils.addDays(getEarlyCheckOutDate(), 1).compareTo(getReservation().getEndDate()) <= 0) {
			double penaltyPrice = getReservation().getEarlyCheckOutPenaltyPrice(getEarlyCheckOutDate(), getEarlyCheckOutDate());
			penaltyDays.add(new SelectItem("1", "1 - " + formatter.format(penaltyPrice) + " EUR."));
		}
		if (DateUtils.addDays(getEarlyCheckOutDate(), 2).compareTo(getReservation().getEndDate()) <= 0) {
			double penaltyPrice = getReservation().getEarlyCheckOutPenaltyPrice(getEarlyCheckOutDate(), DateUtils.addDays(getEarlyCheckOutDate(), 1));
			penaltyDays.add(new SelectItem("2", "2 - " + formatter.format(penaltyPrice) + " EUR."));
		}
		return penaltyDays;
	}

	private double getEarlyCheckOutPenaltyAmount() throws ManagerBeanException {
		if (getPenaltyDays() != null && getPenaltyDays() != 0) {
			if (getPenaltyDays() > 0) {
				return getReservation().getEarlyCheckOutPenaltyTaxableBase(getEarlyCheckOutDate(), DateUtils.addDays(getEarlyCheckOutDate(), getPenaltyDays()-1));
			} else {
				return getReservation().getEarlyCheckOutPenaltyTaxableBase(getEarlyCheckOutDate(), getReservation().getEndDate());
			}
		}
		return 0;
	}

	private int getEarlyCheckOutPenaltyDays() throws ManagerBeanException {
		return (getPenaltyDays() != null) ? getPenaltyDays() : -1;
	}

	public void onEarlyCheckOut(ActionEvent event) {
		try {
			if (validateEarlyCheckOut()) {
				if (getEarlyCheckOutDate().compareTo(getReservation().getEndDate()) != 0) {
					getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
					getReservationInvoiceTo().setComments(AonUtil.getMessage(PMS_EARLY_CHECK_OUT).toUpperCase());
					getReservationInvoiceTo().setPenaltyDays(getEarlyCheckOutPenaltyDays());
					getReservationInvoiceTo().setPenaltyAmount(getEarlyCheckOutPenaltyAmount());
					getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());

					if ((getReservation().isAgencyHolder() && getEarlyCheckOutPenaltyDays() < 0) || (getReservation().isGuestHolder() && !isPayCheckOut())) {
						getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
						getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
						getReservationInvoiceTo().setRegistry(getReservation().getHotelReservation().getCustomer().getRegistry());

						PenalizationInvoicing penalizationInvoicing = new PenalizationInvoicing();
						penalizationInvoicing.agencyCheckOutInvoice(getReservationInvoiceTo(), getReservation());
			    	}

					List<Item> inventoryItems = getReservationUtils().getProjectReservationRoomDetailItems(getReservation(), getEarlyCheckOutDate());
					getReservationUtils().releaseProjectReservationResources(getReservation(), true, getEarlyCheckOutDate());
					sendInventoryData(getReservation(), inventoryItems, getEarlyCheckOutDate(), DateUtils.addDays(reservation.getEndDate(), -1));

			    	if ((getReservation().isAgencyHolder() && getEarlyCheckOutPenaltyDays() >= 0) || (isPayCheckOut() && getReservationFinances().size() > 0)) {
						ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
						for (ITransferObject ito : getReservationInvoicesToRectify()) {
				    		Invoice invoiceToRectify = (Invoice)ito;
					    	if (invoiceToRectify != null && !invoiceToRectify.isService()) {
								getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries(invoiceToRectify));
								getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
								getReservationInvoiceTo().setIssueDate(new Date());

								getReservationInvoiceTo().setRegistry(invoiceToRectify.getRegistry());
								getReservationInvoiceTo().getRegistry().setName(invoiceToRectify.getRegistryName());
								getReservationInvoiceTo().getRegistry().setDocumentType(invoiceToRectify.getRegistryDocumentType());
								getReservationInvoiceTo().getRegistry().setDocumentCountry(invoiceToRectify.getRegistryDocumentCountry());
								getReservationInvoiceTo().getRegistry().setDocument(invoiceToRectify.getRegistryDocument());
								getReservationInvoiceTo().setAddress(obtainInvoiceAddress(invoiceToRectify));
	
								reservationInvoicing.rectify(invoiceToRectify, getReservationInvoiceTo(), false);
					    	}
			    		}
				    	if (getReservationUsedServices().size() > 0 || getEarlyCheckOutPenaltyDays() > 0) {
				    		getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
							getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
							getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
							getReservationInvoiceTo().setFinances(obtainFinances(getReservationPaymentSummary()));
							reservationInvoicing.invoice(getReservationInvoiceTo(), getReservation());
				    	}

				    	if (isPayCheckOut()) {
				    		for (ITransferObject ito : getReservationInvoicesToRectify()) {
					    		Invoice invoiceToRectify = (Invoice)ito;
						    	if (invoiceToRectify != null && invoiceToRectify.isService()) {
									getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries(invoiceToRectify));
									getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
									getReservationInvoiceTo().setIssueDate(new Date());
	
									getReservationInvoiceTo().setRegistry(invoiceToRectify.getRegistry());
									getReservationInvoiceTo().getRegistry().setName(invoiceToRectify.getRegistryName());
									getReservationInvoiceTo().getRegistry().setDocumentType(invoiceToRectify.getRegistryDocumentType());
									getReservationInvoiceTo().getRegistry().setDocumentCountry(invoiceToRectify.getRegistryDocumentCountry());
									getReservationInvoiceTo().getRegistry().setDocument(invoiceToRectify.getRegistryDocument());
									getReservationInvoiceTo().setAddress(obtainInvoiceAddress(invoiceToRectify));
		
									reservationInvoicing.rectify(invoiceToRectify, getReservationInvoiceTo(), false);
						    	}
				    		}
					    	if (getReservationUsedExtraServices().size() > 0) {
					    		getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
								getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
								getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
								getReservationInvoiceTo().setFinances(obtainFinances(getServicesPaymentSummary()));
								getReservationInvoiceTo().setDirectCustomer(true);
								getReservationInvoiceTo().setService(true);
								getReservationInvoiceTo().setServicesIds(obtainReservationExtraServiceDetailsToRectifyIds());
								reservationInvoicing.invoice(getReservationInvoiceTo(), getReservation());
					    	}
				    	}
			    	}
				}

		    	ProjectReservationController reservationController = (ProjectReservationController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.setInvoiceModel(null);
				reservationController.onEarlyCheckOut(getEarlyCheckOutDate());
				reservationController.setSelectedTab(INVOICE);

				String msg = "Se ha realizado correctamente la Salida Anticipada."; 
				AonUtil.addInfoMessage(msg);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateEarlyCheckOut() throws ManagerBeanException {
		if (getReservation().getStartDate().after(getEarlyCheckOutDate()) || getReservation().getEndDate().before(getEarlyCheckOutDate())) {
			String msg = "Fecha de Salida Anticipada incorrecta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isPayCheckOut() && !getReservation().isAgencyHolder() && getPenaltyDays() == null) {
			String msg = "Indique los Días de Penalización.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isPayCheckOut() && !isPaymentsAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el Total a Pagar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isPayCheckOut() && !isPayMethodOk()) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isPayCheckOut() && isCashOrCardPayment() && !PosUtils.isUserPosShiftOpened()) {
			String msg = "No se puede Facturar en Metálico/Tarjetas. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public boolean isPaymentsAmountOk() throws ManagerBeanException {
		double totalPayment = CommonUtil.round(getReservationTotalPaymentAmount() + getServicesTotalPaymentAmount());
		double currentPayment = CommonUtil.round(getReservationCurrentPaymentAmount() + getServicesCurrentPaymentAmount());
		return CommonUtil.round(totalPayment - currentPayment) == 0;
	}

	public boolean isPayMethodOk() {
		for (PaymentSummaryTo paymentSummaryTo : getReservationPaymentSummary()) {
			if (paymentSummaryTo.getPayMethod() == null && paymentSummaryTo.getPaymentAmount() != 0) {
				return false;
			}
		}
		for (PaymentSummaryTo paymentSummaryTo : getServicesPaymentSummary()) {
			if (paymentSummaryTo.getPayMethod() == null && paymentSummaryTo.getPaymentAmount() != 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isCashOrCardPayment() {
		for (PaymentSummaryTo paymentSummaryTo : getReservationPaymentSummary()) {
			if (paymentSummaryTo.getPaymentAmount() != 0) {
				PayMethodType type = paymentSummaryTo.getPayMethod().getType();
				if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
					return true;
				}
			}
		}
		for (PaymentSummaryTo paymentSummaryTo : getServicesPaymentSummary()) {
			if (paymentSummaryTo.getPaymentAmount() != 0) {
				PayMethodType type = paymentSummaryTo.getPayMethod().getType();
				if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
					return true;
				}
			}
		}
		return false;
	}

    private void sendInventoryData(ProjectReservation reservation, List<Item> inventoryItems, Date startDate, Date endDate) throws ManagerBeanException {
    	InventoryManager manager = new InventoryManager();
		for (Item item : inventoryItems) {
	    	manager.processInventoryQuery(reservation.getHotel(), item, reservation.getAllotmentRateCode(), startDate, endDate);
		}
    }

	private String obtainHotelInvoiceSeries() throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelInvoiceSeries();
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	private List<SelectItem> getHotelInvoiceSeries() throws ManagerBeanException {
		return getHotelSeries(getReservation().getHotelReservation().getScope(), false);
	}

	private String obtainHotelRectificationSeries(Invoice invoiceToRectify) throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelRectificationSeries(invoiceToRectify);
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	private List<SelectItem> getHotelRectificationSeries(Invoice invoiceToRectify) throws ManagerBeanException {
		return getHotelSeries(obtainRectifiedInvoiceScope(invoiceToRectify), true);
	}

	public List<SelectItem> getHotelSeries(Scope scope, boolean rectification) throws ManagerBeanException {
		List<SelectItem> seriesList = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), scope.getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), true);
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
		}
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series series = (Series)ito;
			SelectItem selectItem = new SelectItem(series.getCode(), series.getCode());
			seriesList.add(selectItem);
		}
		return seriesList;
	}

	private Scope obtainRectifiedInvoiceScope(Invoice rectifiedInvoice) throws ManagerBeanException {
		for (ITransferObject ito : rectifiedInvoice.getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), invoiceDetail.getWorkPlace().getId());
			for (ITransferObject itr : hotelBean.getList(criteria)) {
				return ((Hotel)itr).getScope();
			}
			return invoiceDetail.getWorkPlace().getScope();
		}
		return rectifiedInvoice.getScope();
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private IAddress obtainInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceAddressBean.getList(criteria)) {
			return (InvoiceAddress)ito;
		}
		return invoice.getRegistryAddress();
	}

	private List<Finance> obtainFinances(List<PaymentSummaryTo> paymentSummaryList) {
		List<Finance> financeList = new LinkedList<Finance>();
		for (PaymentSummaryTo paymentSummaryTo : paymentSummaryList) {
			Finance finance = new Finance();
			finance.setPayMethod(paymentSummaryTo.getPayMethod());
			finance.setAmount(paymentSummaryTo.getPaymentAmount());
			financeList.add(finance);
		}
		return financeList;
	}

}
