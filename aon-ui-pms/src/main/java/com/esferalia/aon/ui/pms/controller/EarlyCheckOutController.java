package com.esferalia.aon.ui.pms.controller;

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
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
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
	private Date earlyCheckOutDate;
	private int earlyCheckOutPenalty;

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

	public Date getEarlyCheckOutDate() {
		return earlyCheckOutDate;
	}

	public void setEarlyCheckOutDate(Date earlyCheckOutDate) {
		this.earlyCheckOutDate = earlyCheckOutDate;
	}

	public int getEarlyCheckOutPenalty() {
		return earlyCheckOutPenalty;
	}

	public void setEarlyCheckOutPenalty(int earlyCheckOutPenalty) {
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
			setEarlyCheckOutDate(DateUtils.truncate(new Date(), Calendar.DATE));
			setEarlyCheckOutPenalty(0);
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getReservationInvoiceTo().setDirectCustomer(true);
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
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), getReservation().getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		if (getReservation().isAgencyHolder()) {
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_SERVICE), true);
		}
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
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
		criteria.addLessThanExpression(alias, getEarlyCheckOutDate());
		if (getReservation().isAgencyHolder()) {
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, true);
		}
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			Tax vat = reservationServiceDetail.getItem().getProduct().getVat();
			double taxableBase = reservationServiceDetail.getTaxableBase();
			if (usedServicesMap.containsKey(vat)) {
				taxableBase += usedServicesMap.get(vat).doubleValue();
			}
			usedServicesMap.put(vat, CommonUtil.round(taxableBase, 4));
		}

		if (getEarlyCheckOutPenalty() > 0) {
			Tax vat = getReservation().getHotelReservation().getItemPenalty().getProduct().getVat();
			double amount = getEarlyCheckOutPenaltyAmount();
			if (usedServicesMap.containsKey(vat)) {
				amount += usedServicesMap.get(vat).doubleValue();
			}
			usedServicesMap.put(vat, CommonUtil.round(amount));
		}
		return usedServicesMap;
	}

	private double getEarlyCheckOutPenaltyAmount() throws ManagerBeanException {
		switch (getEarlyCheckOutPenalty()) {
		case 1:
			return getReservation().getOneNightPenaltyTaxableBase();
		case 2:
			return getReservation().getTwoNightPenaltyTaxableBase();
		default:
			return 0;
		}
	}

	public double getReservationUsedServicesAmount() {
		double amount = 0;
		for (Tax vat : reservationUsedServices.keySet()) {
			amount += CommonUtil.round(reservationUsedServices.get(vat) * (1 + vat.getPercentage() / 100));
		}
		return amount;
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
		setEarlyCheckOutPenalty(((Integer)event.getNewValue()).intValue());
		try {
			setReservationUsedServices(obtainReservationUsedServices());
			getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
			onNewFinance(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public List<PaymentSummaryTo> getPayments() {
		List<PaymentSummaryTo> payments = new LinkedList<PaymentSummaryTo>();
		for (Finance finance : getReservationFinances()) {
			PaymentSummaryTo paymentSummaryTo = new PaymentSummaryTo();
			paymentSummaryTo.setPayMethod(finance.getPayMethod());
			if (payments.contains(paymentSummaryTo)) {
				paymentSummaryTo = payments.get(payments.indexOf(paymentSummaryTo));
			}
			if (!finance.getInvoice().isService()) {
				paymentSummaryTo.setReservationAmount(CommonUtil.round(paymentSummaryTo.getReservationAmount() + finance.getTotalAmount()));
			} else {
				paymentSummaryTo.setServicesAmount(CommonUtil.round(paymentSummaryTo.getServicesAmount() + finance.getTotalAmount()));
			}
			if (!payments.contains(paymentSummaryTo)) {
				payments.add(paymentSummaryTo);
			}
		}
		return payments;
	}

	public double getReservationAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			if (!finance.getInvoice().isService()) {
				amount += finance.getTotalAmount();
			}
		}
		return CommonUtil.round(amount);
	}

	public double getServicesAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			if (finance.getInvoice().isService()) {
				amount += finance.getTotalAmount();
			}
		}
		return CommonUtil.round(amount);
	}

	public double getPaymentsAmount() {
		double amount = 0;
		for (Finance finance : getReservationFinances()) {
			amount += finance.getTotalAmount();
		}
		return CommonUtil.round(amount);
	}

	public void onNewFinance(ActionEvent event) throws ManagerBeanException {
		Finance finance = new Finance();
		if (getReservationInvoiceTo().getFinancesCount() == 0 && getReservationFinances().size() > 0) {
			finance.setPayMethod(getReservationFinances().get(0).getPayMethod());
		}
		finance.setAmount(CommonUtil.round(getReservationUsedServicesAmount() - getFinancesAmount()));
		getReservationInvoiceTo().getFinances().add(finance);
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
			if (validateEarlyCheckOut(getReservation())) {
				if (getEarlyCheckOutDate().compareTo(getReservation().getEndDate()) != 0) {
					getReservationInvoiceTo().setEarlyCheckOut(true);
					getReservationInvoiceTo().setPenaltyAmount(getEarlyCheckOutPenaltyAmount());

					if (getReservation().isAgencyHolder()) {
						getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
						getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
						getReservationInvoiceTo().setRegistry(getReservation().getHotelReservation().getCustomer().getRegistry());
						getReservationInvoiceTo().setIssueDate(new Date());
						getReservationInvoiceTo().setComments("SALIDA ANTICIPADA");
	
						PenalizationInvoicing penalizationInvoicing = new PenalizationInvoicing();
						penalizationInvoicing.agencyCheckOutInvoice(getReservationInvoiceTo(), getReservation(), getEarlyCheckOutDate());
			    	}

					ReservationUtils reservationUtils = new ReservationUtils();
			    	reservationUtils.releaseProjectReservationResources(getReservation(), true, getEarlyCheckOutDate());

			    	if (getReservationFinances().size() > 0) {
						ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			    		for (ITransferObject ito : obtainReservationInvoiceList(getReservation())) {
				    		Invoice invoiceToRectificate = (Invoice)ito;
					    	if (invoiceToRectificate != null) {
								getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries(invoiceToRectificate));
								getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
								getReservationInvoiceTo().setIssueDate(new Date());
								getReservationInvoiceTo().setComments("SALIDA ANTICIPADA");

								getReservationInvoiceTo().setRegistry(invoiceToRectificate.getRegistry());
								getReservationInvoiceTo().getRegistry().setName(invoiceToRectificate.getRegistryName());
								getReservationInvoiceTo().getRegistry().setDocumentType(invoiceToRectificate.getRegistryDocumentType());
								getReservationInvoiceTo().getRegistry().setDocumentCountry(invoiceToRectificate.getRegistryDocumentCountry());
								getReservationInvoiceTo().getRegistry().setDocument(invoiceToRectificate.getRegistryDocument());
								getReservationInvoiceTo().setAddress(obtainInvoiceAddress(invoiceToRectificate));
	
								reservationInvoicing.rectify(invoiceToRectificate, getReservationInvoiceTo());
					    	}
			    		}

						getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
						getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
						reservationInvoicing.invoice(getReservationInvoiceTo(), getReservation());
			    	}
				}

		    	ProjectReservationController reservationController = (ProjectReservationController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.setInvoiceModel(null);
				reservationController.onCheckOut(event);
				reservationController.setSelectedTab(INVOICE);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateEarlyCheckOut(ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.getStartDate().after(getEarlyCheckOutDate()) || reservation.getEndDate().before(getEarlyCheckOutDate())) {
			String msg = "Fecha de Salida Anticipada incorrecta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (!isFinancesAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el Total a Pagar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (!isPayMethodOk()) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
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

	private String obtainHotelRectificationSeries(Invoice invoiceToRectificate) throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelRectificationSeries(invoiceToRectificate);
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	public List<SelectItem> getHotelRectificationSeries(Invoice invoiceToRectificate) throws ManagerBeanException {
		return getHotelSeries(obtainRectifiedInvoiceScope(invoiceToRectificate), true);
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
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
		return invoiceBean.getList(criteria);
	}

}
