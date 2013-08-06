package com.esferalia.aon.ui.pms.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.ui.common.ICommonConstants;
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
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class EarlyCheckOutController implements IPmsConstants {

	private ProjectReservation reservation;
	private ReservationInvoiceTo reservationInvoiceTo;
	private List<Finance> reservationFinances;
	private Map<Tax, Double> reservationUsedServices;
	private boolean showEarlyCheckOutWindow;
	private boolean chargeCheckOut;
	private Integer earlyCheckOutPenalty;

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

	public List<Finance> getReservationFinances() {
		return reservationFinances;
	}

	public void setReservationFinances(List<Finance> reservationFinances) {
		this.reservationFinances = reservationFinances;
	}

	public Map<Tax, Double> getReservationUsedServices() {
		return reservationUsedServices;
	}

	public void setReservationUsedServices(Map<Tax, Double> reservationUsedServices) {
		this.reservationUsedServices = reservationUsedServices;
	}

	public boolean isShowEarlyCheckOutWindow() {
		return showEarlyCheckOutWindow;
	}

	public void setShowEarlyCheckOutWindow(boolean showEarlyCheckOutWindow) {
		this.showEarlyCheckOutWindow = showEarlyCheckOutWindow;
	}

	public boolean isChargeCheckOut() {
		return chargeCheckOut;
	}

	public void setChargeCheckOut(boolean chargeCheckOut) {
		this.chargeCheckOut = chargeCheckOut;
	}

	public Integer getEarlyCheckOutPenalty() {
		return earlyCheckOutPenalty;
	}

	public void setEarlyCheckOutPenalty(Integer earlyCheckOutPenalty) {
		this.earlyCheckOutPenalty = earlyCheckOutPenalty;
	}

	public void onInit() {
		if (getReservation().getHotelReservation().getItemPenalty() == null || getReservation().getHotelReservation().getItemPenalty().getId() == null) {
			setShowEarlyCheckOutWindow(false);
			String msg = "El Hotel no tiene definido Producto para Salidas Anticipadas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		try {
			setChargeCheckOut(true);
			setEarlyCheckOutPenalty(null);
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
			getReservationInvoiceTo().setDirectCustomer(true);
			getReservationInvoiceTo().setEarlyCheckOut(true);
			getReservationInvoiceTo().setEarlyCheckOutDate(DateUtils.truncate(new Date(), Calendar.DATE));
			getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
			setReservationFinances(obtainReservationFinances());
			setReservationUsedServices(obtainReservationUsedServices());
			onNewFinance(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private List<Finance> obtainReservationFinances() throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), getReservation().getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		if (getReservation().isAgencyHolder()) {
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_SERVICE), true);
		}
		criteria.addNotEqualExpression("Finance.invoice<lines.item.product.type", ProductType.EXTERNAL_WORK);
		criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE), false);
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			financeList.add(finance);
		}
		return financeList;
	}

	private Map<Tax, Double> obtainReservationUsedServices() throws ManagerBeanException {
		Map<Tax, Double> usedServicesMap = new HashMap<Tax, Double>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, getReservation().getId());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE);
		criteria.addLessThanExpression(alias, getReservationInvoiceTo().getEarlyCheckOutDate());
		if (getReservation().isAgencyHolder()) {
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, true);
		}
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			if (!isDepositOrDamage(reservationServiceDetail.getProjectReservationService())) {
				Tax vat = reservationServiceDetail.getItem().getProduct().getVat();
				double taxableBase = reservationServiceDetail.getTaxableBase();
				if (usedServicesMap.containsKey(vat)) {
					taxableBase += usedServicesMap.get(vat).doubleValue();
				}
				usedServicesMap.put(vat, CommonUtil.round(taxableBase, 4));
			}
		}

		if (getEarlyCheckOutPenalty() != null) {
			Tax vat = getReservation().getHotelReservation().getItemPenalty().getProduct().getVat();
			double amount = getEarlyCheckOutPenaltyAmount();
			if (usedServicesMap.containsKey(vat)) {
				amount += usedServicesMap.get(vat).doubleValue();
			}
			usedServicesMap.put(vat, CommonUtil.round(amount, 4));
		}
		return usedServicesMap;
	}

	public boolean isEarlyCheckOutDateEditable() throws ManagerBeanException {
		BasicRoleManager roleManager = AonUtil.getRoleManager();
		return ((roleManager.isConfig() || roleManager.isFinanceOperator()) && DateUtils.addDays(getReservation().getEndDate(), 1).before(new Date()));
	}

	public double getReservationUsedServicesAmount() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double amount = 0;
		for (Tax vat : reservationUsedServices.keySet()) {
			double vatPercent = reservationUtils.getTaxPercentage(vat, getReservationInvoiceTo().getIssueDate());
			amount += CommonUtil.round(reservationUsedServices.get(vat) * (1 + vatPercent / 100));
		}
		return amount;
	}

	private boolean isDepositOrDamage(ProjectReservationService reservationService) {
		return (reservationService.isExtra() && reservationService.getItem().getProduct().getType() != ProductType.SERVICE);
	}

	private int getEarlyCheckOutPenaltyDays() throws ManagerBeanException {
		return (getEarlyCheckOutPenalty() != null) ? getEarlyCheckOutPenalty() : 0;
	}

	private double getEarlyCheckOutPenaltyAmount() throws ManagerBeanException {
		if (getEarlyCheckOutPenalty() != null) {
			switch (getEarlyCheckOutPenalty().intValue()) {
			case 1:
				return getReservation().getOneNightPenaltyTaxableBase();
			case 2:
				return getReservation().getTwoNightPenaltyTaxableBase();
			}
		}
		return 0;
	}

	public void onCheckOutDateChanged(ActionEvent event) {
		try {
			setReservationUsedServices(obtainReservationUsedServices());
			getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
			onNewFinance(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onCheckOutPenaltyChanged(ValueChangeEvent event) {
		setEarlyCheckOutPenalty((Integer)event.getNewValue());
		try {
			setReservationUsedServices(obtainReservationUsedServices());
			getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
			onNewFinance(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public List<SelectItem> getCheckOutPenaltyDays() throws ManagerBeanException {
		NumberFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_price_pattern"));
		List<SelectItem> penaltyDays = new LinkedList<SelectItem>();
		penaltyDays.add(new SelectItem("0", "0 - 0,00 EUR."));
		if (DateUtils.addDays(getReservationInvoiceTo().getEarlyCheckOutDate(), 1).compareTo(getReservation().getEndDate()) <= 0) {
			penaltyDays.add(new SelectItem("1", "1 - " + formatter.format(getReservation().getOneNightPenaltyPrice()) + " EUR."));
		}
		if (DateUtils.addDays(getReservationInvoiceTo().getEarlyCheckOutDate(), 2).compareTo(getReservation().getEndDate()) <= 0) {
			penaltyDays.add(new SelectItem("2", "2 - " + formatter.format(getReservation().getTwoNightPenaltyPrice()) + " EUR."));
		}
		return penaltyDays;
	}

	public List<PaymentSummaryTo> getReturns() {
		List<PaymentSummaryTo> returns = new LinkedList<PaymentSummaryTo>();
		for (Finance finance : getReservationFinances()) {
			PaymentSummaryTo returnSummaryTo = new PaymentSummaryTo();
			returnSummaryTo.setPayMethod(finance.getPayMethod());
			if (returns.contains(returnSummaryTo)) {
				returnSummaryTo = returns.get(returns.indexOf(returnSummaryTo));
			}
			if (!finance.getInvoice().isService()) {
				returnSummaryTo.setReservationReturnAmount(CommonUtil.round(returnSummaryTo.getReservationReturnAmount() + finance.getTotalAmount()));
			} else {
				returnSummaryTo.setServicesReturnAmount(CommonUtil.round(returnSummaryTo.getServicesReturnAmount() + finance.getTotalAmount()));
			}
			if (!returns.contains(returnSummaryTo)) {
				returns.add(returnSummaryTo);
			}
		}
		return returns;
	}

	public double getReservationReturnAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			if (!finance.getInvoice().isService()) {
				amount += finance.getTotalAmount();
			}
		}
		return CommonUtil.round(amount);
	}

	public double getServicesReturnAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			if (finance.getInvoice().isService()) {
				amount += finance.getTotalAmount();
			}
		}
		return CommonUtil.round(amount);
	}

	public double getReturnsAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			amount += finance.getTotalAmount();
		}
		return CommonUtil.round(amount);
	}

	public List<PaymentSummaryTo> getTotals() {
		List<PaymentSummaryTo> totals = getReturns();
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getPayMethod() != null) {
				PaymentSummaryTo totalSummaryTo = new PaymentSummaryTo();
				totalSummaryTo.setPayMethod(finance.getPayMethod());
				if (totals.contains(totalSummaryTo)) {
					totalSummaryTo = totals.get(totals.indexOf(totalSummaryTo));
				}
				totalSummaryTo.setPaymentAmount(CommonUtil.round(totalSummaryTo.getPaymentAmount() + finance.getTotalAmount()));
				if (totalSummaryTo.getTotalAmount() != 0) {
					if (!totals.contains(totalSummaryTo)) {
						totals.add(totalSummaryTo);
					}
				} else {
					if (totals.contains(totalSummaryTo)) {
						totals.remove(totalSummaryTo);
					}
				}
			}
		}
		return totals;
	}

	public void onNewFinance(ActionEvent event) throws ManagerBeanException {
		double amount = CommonUtil.round(getReservationUsedServicesAmount() - getFinancesAmount());
		if (getReservationFinances().size() > 0 && getReservationInvoiceTo().getFinancesCount() == 0) {
			List<PaymentSummaryTo> returns = getReturns();
			do {
				Finance finance = new Finance();
				finance.setAmount(amount);

				PaymentSummaryTo returnSummary = returns.get(getReservationInvoiceTo().getFinancesCount());
				if (returnSummary != null) {
					finance.setPayMethod(returnSummary.getPayMethod());
					if (returnSummary.getTotalReturnAmount() < finance.getAmount()) {
						finance.setAmount(returnSummary.getTotalReturnAmount());
					}
				}
				getReservationInvoiceTo().getFinances().add(finance);

				amount = CommonUtil.round(amount - finance.getAmount());
			} while (amount != 0 && returns.size() > getReservationInvoiceTo().getFinancesCount());

			if (amount != 0) {
				Finance finance = new Finance();
				finance.setAmount(amount);
				getReservationInvoiceTo().getFinances().add(finance);
			}
		} else {
			Finance finance = new Finance();
			finance.setAmount(amount);
			getReservationInvoiceTo().getFinances().add(finance);
		}
	}

	public void onRemoveFinance(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int financeIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("hotelInvoiceFinanceIndex"));

        Finance financeToRemove = getReservationInvoiceTo().getFinances().get(financeIndex);
        getReservationInvoiceTo().getFinances().remove(financeIndex);

        Finance previousFinance = getReservationInvoiceTo().getFinances().get(financeIndex-1);
        previousFinance.setAmount(CommonUtil.round(previousFinance.getAmount() + financeToRemove.getAmount()));
	}

	public double getFinancesAmount() {
		double amount = 0;
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			amount += CommonUtil.round(finance.getAmount());
		}
		return CommonUtil.round(amount);
	}

	public void onEarlyCheckOut(ActionEvent event) {
		try {
			if (validateEarlyCheckOut()) {
				if (getReservationInvoiceTo().getEarlyCheckOutDate().compareTo(getReservation().getEndDate()) != 0) {
					getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
					getReservationInvoiceTo().setComments("SALIDA ANTICIPADA");
					getReservationInvoiceTo().setPenaltyDays(getEarlyCheckOutPenaltyDays());
					getReservationInvoiceTo().setPenaltyAmount(getEarlyCheckOutPenaltyAmount());
					getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());

					if (getReservation().isAgencyHolder() || !isChargeCheckOut()) {
						getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
						getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
						getReservationInvoiceTo().setRegistry(getReservation().getHotelReservation().getCustomer().getRegistry());

						PenalizationInvoicing penalizationInvoicing = new PenalizationInvoicing();
						penalizationInvoicing.agencyCheckOutInvoice(getReservationInvoiceTo(), getReservation());
			    	}

					ReservationUtils reservationUtils = new ReservationUtils();
			    	reservationUtils.releaseProjectReservationResources(getReservation(), true, getReservationInvoiceTo().getEarlyCheckOutDate());

			    	if (isChargeCheckOut() && getReservationFinances().size() > 0) {
						ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
						for (ITransferObject ito : obtainReservationInvoiceList(getReservation())) {
				    		Invoice invoiceToRectify = (Invoice)ito;
					    	if (invoiceToRectify != null) {
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

				    	if (getReservationUsedServices().size() > 0) {
				    		getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
							getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
							getReservationInvoiceTo().setIssueDate(getReservation().getStartDate());
							reservationInvoicing.invoice(getReservationInvoiceTo(), getReservation());
				    	}
			    	}
				}

		    	ProjectReservationController reservationController = (ProjectReservationController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.setInvoiceModel(null);
				reservationController.onCheckOut(event);
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
		Date earlyCheckOutDate = getReservationInvoiceTo().getEarlyCheckOutDate();
		if (getReservation().getStartDate().after(earlyCheckOutDate) || getReservation().getEndDate().before(earlyCheckOutDate)) {
			String msg = "Fecha de Salida Anticipada incorrecta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isChargeCheckOut() && !getReservation().isAgencyHolder() && getEarlyCheckOutPenalty() == null) {
			String msg = "Indique los Días de Penalización.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isChargeCheckOut() && !isFinancesAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el Total a Pagar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isChargeCheckOut() && !isPayMethodOk()) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (isChargeCheckOut() && isCashOrCardPayment() && !PosUtils.isUserPosShiftOpened()) {
			String msg = "No se puede Facturar en Metálico/Tarjetas. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	private boolean isCashOrCardPayment() {
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getTotalAmount() != 0) {
				PayMethodType type = finance.getPayMethod().getType();
				if (type == PayMethodType.CASH_BASIS || type == PayMethodType.CREDIT_CARD || type == PayMethodType.DEBIT_CARD) {
					return true;
				}
			 }
		}
		return false;
	}

	public boolean isFinancesAmountOk() throws ManagerBeanException {
		return CommonUtil.round(getReservationUsedServicesAmount() - getFinancesAmount()) == 0;
	}

	public boolean isPayMethodOk() {
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getPayMethod() == null && finance.getTotalAmount() != 0) {
				return false;
			}
		}
		return true;
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

	private String obtainHotelInvoiceSeries() throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelInvoiceSeries();
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	public List<SelectItem> getHotelInvoiceSeries() throws ManagerBeanException {
		return getHotelSeries(getReservation().getHotelReservation().getScope(), false);
	}

	private String obtainHotelRectificationSeries(Invoice invoiceToRectify) throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelRectificationSeries(invoiceToRectify);
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	public List<SelectItem> getHotelRectificationSeries(Invoice invoiceToRectify) throws ManagerBeanException {
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

	private List<ITransferObject> obtainReservationInvoiceList(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservation.getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		if (reservation.isAgencyHolder()) {
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), true);
		}
		criteria.addNotEqualExpression("Invoice.lines.item.product.type", ProductType.EXTERNAL_WORK);
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
		return invoiceBean.getList(criteria);
	}

}
