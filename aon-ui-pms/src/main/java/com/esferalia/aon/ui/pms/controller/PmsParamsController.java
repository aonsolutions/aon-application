package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Tariff;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class PmsParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Map<AppParam, ApplicationParameter> parameters;
	private ReservationUtils reservationUtils;
	
	private ProductCategory roomCategory;
	private ProductCategory boardCategory;
	private ProductCategory serviceCategory;
	private ProductCategory extraPaxCategory;
	private Item undefinedRoomItem;
	private Item undefinedServiceItem;
	private Item advanceItem;
	private Item earlyCheckOutItem;
	private Item noShowItem;
	private Item cancellationItem;
	private Item touristTaxItem;
	private Item bestPriceItem;
	private Item autoDiscountItem;
	private String simpleAvailabilityURL;
	private String bookingURL;
	private String hhgURL;
	private String navSaleInvoiceURL;
	private String navFinanceBatchURL;
	private String navProductionURL;
	private String navUsername;
	private String navPassword;
	private ProductCategory productionReportCategory;
	private String productionReportOpenHour;
	private String productionReportCloseHour;
	private Tariff undefinedTariff;
	private RegistryBank autoFbatchBank;
	private PayMethodType autoFbatchPayMethod0;
	private PayMethodType autoFbatchPayMethod1;
	private PayMethodType autoFbatchPayMethod2;
	private String dailyCashLimit;
	private String cleanDays;
	private String policeCount;

	public Map<AppParam, ApplicationParameter> getParameters() {
		if (parameters == null) {
			parameters = new TreeMap<AppParam, ApplicationParameter>();
		}
		return parameters;
	}

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public ProductCategory getRoomCategory() {
		return roomCategory;
	}
	public void setRoomCategory(ProductCategory roomCategory) {
		this.roomCategory = roomCategory;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_ROOM_CATEGORY);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_ROOM_CATEGORY.getValue());
		}
		appParam.setValue((roomCategory != null) ? roomCategory.getId().toString() : null);
		getParameters().put(AppParam.PMS_ROOM_CATEGORY, appParam);
	}
	private void initializeRoomCategory() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_ROOM_CATEGORY);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setRoomCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setRoomCategory(null);
			}
		}
	}

	public ProductCategory getBoardCategory() {
		return boardCategory;
	}
	public void setBoardCategory(ProductCategory boardCategory) {
		this.boardCategory = boardCategory;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_BOARD_CATEGORY);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_BOARD_CATEGORY.getValue());
		}
		appParam.setValue((boardCategory != null) ? boardCategory.getId().toString() : null);
		getParameters().put(AppParam.PMS_BOARD_CATEGORY, appParam);
	}
	private void initializeBoardCategory() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_BOARD_CATEGORY);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setBoardCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setBoardCategory(null);
			}
		}
	}

	public ProductCategory getServiceCategory() {
		return serviceCategory;
	}
	public void setServiceCategory(ProductCategory serviceCategory) {
		this.serviceCategory = serviceCategory;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_SERVICE_CATEGORY);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_SERVICE_CATEGORY.getValue());
		}
		appParam.setValue((serviceCategory != null) ? serviceCategory.getId().toString() : null);
		getParameters().put(AppParam.PMS_SERVICE_CATEGORY, appParam);
	}
	private void initializeServiceCategory() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_SERVICE_CATEGORY);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setServiceCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setServiceCategory(null);
			}
		}
	}

	public ProductCategory getExtraPaxCategory() {
		return extraPaxCategory;
	}
	public void setExtraPaxCategory(ProductCategory extraPaxCategory) {
		this.extraPaxCategory = extraPaxCategory;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_EXTRAPAX_CATEGORY);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_EXTRAPAX_CATEGORY.getValue());
		}
		appParam.setValue((extraPaxCategory != null) ? extraPaxCategory.getId().toString() : null);
		getParameters().put(AppParam.PMS_EXTRAPAX_CATEGORY, appParam);
	}
	private void initializeExtraPaxCategory() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_EXTRAPAX_CATEGORY);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setExtraPaxCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setExtraPaxCategory(null);
			}
		}
	}

	public Item getUndefinedRoomItem() {
		return undefinedRoomItem;
	}
	public void setUndefinedRoomItem(Item undefinedRoomItem) {
		this.undefinedRoomItem = undefinedRoomItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_UNDEFINED_ROOM_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_UNDEFINED_ROOM_ITEM.getValue());
		}
		appParam.setValue((undefinedRoomItem != null) ? undefinedRoomItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_UNDEFINED_ROOM_ITEM, appParam);
	}
	private void initializeUndefinedRoomItem() {
		try {
			setUndefinedRoomItem(obtainItem(getParameters().get(AppParam.PMS_UNDEFINED_ROOM_ITEM)));
		} catch (ManagerBeanException ex) {
			setUndefinedRoomItem(null);
		}
	}

	public Item getUndefinedServiceItem() {
		return undefinedServiceItem;
	}
	public void setUndefinedServiceItem(Item undefinedServiceItem) {
		this.undefinedServiceItem = undefinedServiceItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_UNDEFINED_SERVICE_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_UNDEFINED_SERVICE_ITEM.getValue());
		}
		appParam.setValue((undefinedServiceItem != null) ? undefinedServiceItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_UNDEFINED_SERVICE_ITEM, appParam);
	}
	private void initializeUndefinedServiceItem() {
		try {
			setUndefinedServiceItem(obtainItem(getParameters().get(AppParam.PMS_UNDEFINED_SERVICE_ITEM)));
		} catch (ManagerBeanException ex) {
			setUndefinedServiceItem(null);
		}
	}

	public Item getAdvanceItem() {
		return advanceItem;
	}
	public void setAdvanceItem(Item advanceItem) {
		this.advanceItem = advanceItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_ADVANCE_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_ADVANCE_ITEM.getValue());
		}
		appParam.setValue((advanceItem != null) ? advanceItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_ADVANCE_ITEM, appParam);
	}
	private void initializeAdvanceItem() {
		try {
			setAdvanceItem(obtainItem(getParameters().get(AppParam.PMS_ADVANCE_ITEM)));
		} catch (ManagerBeanException ex) {
			setAdvanceItem(null);
		}
	}

	public Item getEarlyCheckOutItem() {
		return earlyCheckOutItem;
	}
	public void setEarlyCheckOutItem(Item earlyCheckOutItem) {
		this.earlyCheckOutItem = earlyCheckOutItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_EARLY_CHECKOUT_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_EARLY_CHECKOUT_ITEM.getValue());
		}
		appParam.setValue((earlyCheckOutItem != null) ? earlyCheckOutItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_EARLY_CHECKOUT_ITEM, appParam);
	}
	private void initializeEarlyCheckOutItem() {
		try {
			setEarlyCheckOutItem(obtainItem(getParameters().get(AppParam.PMS_EARLY_CHECKOUT_ITEM)));
		} catch (ManagerBeanException ex) {
			setEarlyCheckOutItem(null);
		}
	}

	public Item getNoShowItem() {
		return noShowItem;
	}
	public void setNoShowItem(Item noShowItem) {
		this.noShowItem = noShowItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NOSHOW_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NOSHOW_ITEM.getValue());
		}
		appParam.setValue((noShowItem != null) ? noShowItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_NOSHOW_ITEM, appParam);
	}
	private void initializeNoShowItem() {
		try {
			setNoShowItem(obtainItem(getParameters().get(AppParam.PMS_NOSHOW_ITEM)));
		} catch (ManagerBeanException ex) {
			setNoShowItem(null);
		}
	}

	public Item getCancellationItem() {
		return cancellationItem;
	}
	public void setCancellationItem(Item cancellationItem) {
		this.cancellationItem = cancellationItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_CANCELLATION_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_CANCELLATION_ITEM.getValue());
		}
		appParam.setValue((cancellationItem != null) ? cancellationItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_CANCELLATION_ITEM, appParam);
	}
	private void initializeCancellationItem() {
		try {
			setCancellationItem(obtainItem(getParameters().get(AppParam.PMS_CANCELLATION_ITEM)));
		} catch (ManagerBeanException ex) {
			setCancellationItem(null);
		}
	}

	public Item getTouristTaxItem() {
		return touristTaxItem;
	}
	public void setTouristTaxItem(Item touristTaxItem) {
		this.touristTaxItem = touristTaxItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_TOURIST_TAX_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_TOURIST_TAX_ITEM.getValue());
		}
		appParam.setValue((touristTaxItem != null) ? touristTaxItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_TOURIST_TAX_ITEM, appParam);
	}
	private void initializeTouristTaxItem() {
		try {
			setTouristTaxItem(obtainItem(getParameters().get(AppParam.PMS_TOURIST_TAX_ITEM)));
		} catch (ManagerBeanException ex) {
			setTouristTaxItem(null);
		}
	}

	public Item getBestPriceItem() {
		return bestPriceItem;
	}
	public void setBestPriceItem(Item bestPriceItem) {
		this.bestPriceItem = bestPriceItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_BEST_PRICE_DISCOUNT_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_BEST_PRICE_DISCOUNT_ITEM.getValue());
		}
		appParam.setValue((bestPriceItem != null) ? bestPriceItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_BEST_PRICE_DISCOUNT_ITEM, appParam);
	}
	private void initializeBestPriceItem() {
		try {
			setBestPriceItem(obtainItem(getParameters().get(AppParam.PMS_BEST_PRICE_DISCOUNT_ITEM)));
		} catch (ManagerBeanException ex) {
			setBestPriceItem(null);
		}
	}

	public Item getAutoDiscountItem() {
		return autoDiscountItem;
	}
	public void setAutoDiscountItem(Item autoDiscountItem) {
		this.autoDiscountItem = autoDiscountItem;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_AUTO_DISCOUNT_ITEM);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_AUTO_DISCOUNT_ITEM.getValue());
		}
		appParam.setValue((autoDiscountItem != null) ? autoDiscountItem.getProduct().getCode() : null);
		getParameters().put(AppParam.PMS_AUTO_DISCOUNT_ITEM, appParam);
	}
	private void initializeAutoDiscountItem() {
		try {
			setAutoDiscountItem(obtainItem(getParameters().get(AppParam.PMS_AUTO_DISCOUNT_ITEM)));
		} catch (ManagerBeanException ex) {
			setAutoDiscountItem(null);
		}
	}

	public String getSimpleAvailabilityURL() {
		return simpleAvailabilityURL;
	}
	public void setSimpleAvailabilityURL(String simpleAvailabilityURL) {
		this.simpleAvailabilityURL = simpleAvailabilityURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_SIMPLE_AVAILABILITY_URL);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_SIMPLE_AVAILABILITY_URL.getValue());
		}
		appParam.setValue(simpleAvailabilityURL);
		getParameters().put(AppParam.PMS_SIMPLE_AVAILABILITY_URL, appParam);
	}
	private void initializeSimpleAvailabilityURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_SIMPLE_AVAILABILITY_URL);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setSimpleAvailabilityURL(appParam.getValue());
		}
	}

	public String getBookingURL() {
		return bookingURL;
	}
	public void setBookingURL(String bookingURL) {
		this.bookingURL = bookingURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_BOOKING_URL);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_BOOKING_URL.getValue());
		}
		appParam.setValue(bookingURL);
		getParameters().put(AppParam.PMS_BOOKING_URL, appParam);
	}
	private void initializeBookingURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_BOOKING_URL);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setBookingURL(appParam.getValue());
		}
	}

	public String getHhgURL() {
		return hhgURL;
	}
	public void setHhgURL(String hhgURL) {
		this.hhgURL = hhgURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_HHG_SERVER);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_HHG_SERVER.getValue());
		}
		appParam.setValue(hhgURL);
		getParameters().put(AppParam.PMS_HHG_SERVER, appParam);
	}
	private void initializeHhgURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_HHG_SERVER);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setHhgURL(appParam.getValue());
		}
	}

	public String getNavSaleInvoiceURL() {
		return navSaleInvoiceURL;
	}
	public void setNavSaleInvoiceURL(String navSaleInvoiceURL) {
		this.navSaleInvoiceURL = navSaleInvoiceURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_SALE_INVOICE_URL);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NAV_SALE_INVOICE_URL.getValue());
		}
		appParam.setValue(navSaleInvoiceURL);
		getParameters().put(AppParam.PMS_NAV_SALE_INVOICE_URL, appParam);
	}
	private void initializeNavSaleInvoiceURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_SALE_INVOICE_URL);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setNavSaleInvoiceURL(appParam.getValue());
		}
	}

	public String getNavFinanceBatchURL() {
		return navFinanceBatchURL;
	}
	public void setNavFinanceBatchURL(String navFinanceBatchURL) {
		this.navFinanceBatchURL = navFinanceBatchURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_FINANCE_BATCH_URL);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NAV_FINANCE_BATCH_URL.getValue());
		}
		appParam.setValue(navFinanceBatchURL);
		getParameters().put(AppParam.PMS_NAV_FINANCE_BATCH_URL, appParam);
	}
	private void initializeNavFinanceBatchURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_FINANCE_BATCH_URL);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setNavFinanceBatchURL(appParam.getValue());
		}
	}

	public String getNavProductionURL() {
		return navProductionURL;
	}
	public void setNavProductionURL(String navProductionURL) {
		this.navProductionURL = navProductionURL;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_PRODUCTION_URL);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NAV_PRODUCTION_URL.getValue());
		}
		appParam.setValue(navProductionURL);
		getParameters().put(AppParam.PMS_NAV_PRODUCTION_URL, appParam);
	}
	private void initializeNavProductionURL() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_PRODUCTION_URL);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setNavProductionURL(appParam.getValue());
		}
	}

	public String getNavUsername() {
		return navUsername;
	}
	public void setNavUsername(String navUsername) {
		this.navUsername = navUsername;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_USERNAME);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NAV_USERNAME.getValue());
		}
		appParam.setValue(navUsername);
		getParameters().put(AppParam.PMS_NAV_USERNAME, appParam);
	}
	private void initializeNavUsername() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_USERNAME);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setNavUsername(appParam.getValue());
		}
	}

	public String getNavPassword() {
		return navPassword;
	}
	public void setNavPassword(String navPassword) {
		this.navPassword = navPassword;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_PASSWORD);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_NAV_PASSWORD.getValue());
		}
		appParam.setValue(navPassword);
		getParameters().put(AppParam.PMS_NAV_PASSWORD, appParam);
	}
	private void initializeNavPassword() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_NAV_PASSWORD);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setNavPassword(appParam.getValue());
		}
	}

	public ProductCategory getProductionReportCategory() {
		return productionReportCategory;
	}
	public void setProductionReportCategory(ProductCategory productionReportCategory) {
		this.productionReportCategory = productionReportCategory;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_CATEGORY);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_PRODUCTION_REPORT_CATEGORY.getValue());
		}
		appParam.setValue((productionReportCategory != null) ? productionReportCategory.getId().toString() : null);
		getParameters().put(AppParam.PMS_PRODUCTION_REPORT_CATEGORY, appParam);
	}
	private void initializeProductionReportCategory() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_CATEGORY);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setProductionReportCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setProductionReportCategory(null);
			}
		}
	}

	public String getProductionReportOpenHour() {
		return productionReportOpenHour;
	}
	public void setProductionReportOpenHour(String productionReportOpenHour) {
		this.productionReportOpenHour = productionReportOpenHour;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_OPEN_HOUR);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_PRODUCTION_REPORT_OPEN_HOUR.getValue());
		}
		appParam.setValue(productionReportOpenHour);
		getParameters().put(AppParam.PMS_PRODUCTION_REPORT_OPEN_HOUR, appParam);
	}
	private void initializeProductionReportOpenHour() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_OPEN_HOUR);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setProductionReportOpenHour(appParam.getValue());
		}
	}

	public String getProductionReportCloseHour() {
		return productionReportCloseHour;
	}
	public void setProductionReportCloseHour(String productionReportCloseHour) {
		this.productionReportCloseHour = productionReportCloseHour;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_CLOSE_HOUR);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_PRODUCTION_REPORT_CLOSE_HOUR.getValue());
		}
		appParam.setValue(productionReportCloseHour);
		getParameters().put(AppParam.PMS_PRODUCTION_REPORT_CLOSE_HOUR, appParam);
	}
	private void initializeProductionReportCloseHour() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_PRODUCTION_REPORT_CLOSE_HOUR);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setProductionReportCloseHour(appParam.getValue());
		}
	}

	public Tariff getUndefinedTariff() {
		return undefinedTariff;
	}
	public void setUndefinedTariff(Tariff undefinedTariff) {
		this.undefinedTariff = undefinedTariff;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_UNDEFINED_TARIFF);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_UNDEFINED_TARIFF.getValue());
		}
		appParam.setValue((undefinedTariff != null) ? undefinedTariff.getCode() : null);
		getParameters().put(AppParam.PMS_UNDEFINED_TARIFF, appParam);
	}
	private void initializeUndefinedTariff() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_UNDEFINED_TARIFF);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setUndefinedTariff(getReservationUtils().obtainTariff(appParam.getValue()));
			} catch (ManagerBeanException ex) {
				setUndefinedTariff(null);
			}
		}
	}

	public RegistryBank getAutoFbatchBank() {
		return autoFbatchBank;
	}
	public void setAutoFbatchBank(RegistryBank autoFbatchBank) {
		this.autoFbatchBank = autoFbatchBank;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_AUTO_FBATCH_BANK);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_AUTO_FBATCH_BANK.getValue());
		}
		appParam.setValue((autoFbatchBank != null) ? autoFbatchBank.getId().toString() : null);
		getParameters().put(AppParam.PMS_AUTO_FBATCH_BANK, appParam);
	}
	private void initializeAutoFbatchBank() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_AUTO_FBATCH_BANK);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			try {
				setAutoFbatchBank((RegistryBank)BeanManager.getManagerBean(RegistryBank.class).get(Integer.parseInt(appParam.getValue())));
			} catch (ManagerBeanException ex) {
				setAutoFbatchBank(null);
			}
		}
	}

	public PayMethodType getAutoFbatchPayMethod0() {
		return autoFbatchPayMethod0;
	}
	public void setAutoFbatchPayMethod0(PayMethodType autoFbatchPayMethod0) {
		this.autoFbatchPayMethod0 = autoFbatchPayMethod0;
	}

	public PayMethodType getAutoFbatchPayMethod1() {
		return autoFbatchPayMethod1;
	}
	public void setAutoFbatchPayMethod1(PayMethodType autoFbatchPayMethod1) {
		this.autoFbatchPayMethod1 = autoFbatchPayMethod1;
	}

	public PayMethodType getAutoFbatchPayMethod2() {
		return autoFbatchPayMethod2;
	}
	public void setAutoFbatchPayMethod2(PayMethodType autoFbatchPayMethod2) {
		this.autoFbatchPayMethod2 = autoFbatchPayMethod2;
		setAutoFbatchPayMethod();
	}

	public void setAutoFbatchPayMethod() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_AUTO_FBATCH_PAY_METHOD);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_AUTO_FBATCH_PAY_METHOD.getValue());
		}
		appParam.setValue("[" + (autoFbatchPayMethod0 != null ? autoFbatchPayMethod0.ordinal() : "") + "]");
		appParam.setValue(appParam.getValue() + "[" + (autoFbatchPayMethod1 != null ? autoFbatchPayMethod1.ordinal() : "") + "]");
		appParam.setValue(appParam.getValue() + "[" + (autoFbatchPayMethod2 != null ? autoFbatchPayMethod2.ordinal() : "") + "]");
		getParameters().put(AppParam.PMS_AUTO_FBATCH_PAY_METHOD, appParam);
	}
	private void initializeAutoFbatchPayMethod() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_AUTO_FBATCH_PAY_METHOD);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			String[] payMethodTypeIds = StringUtils.substringsBetween(appParam.getValue(), "[", "]");
			setAutoFbatchPayMethod0(StringUtils.isNotEmpty(payMethodTypeIds[0]) ? PayMethodType.values()[Integer.parseInt(payMethodTypeIds[0])] : null);
			setAutoFbatchPayMethod1(StringUtils.isNotEmpty(payMethodTypeIds[1]) ? PayMethodType.values()[Integer.parseInt(payMethodTypeIds[1])] : null);
			setAutoFbatchPayMethod2(StringUtils.isNotEmpty(payMethodTypeIds[2]) ? PayMethodType.values()[Integer.parseInt(payMethodTypeIds[2])] : null);
		}
	}

	public String getDailyCashLimit() {
		return dailyCashLimit;
	}
	public void setDailyCashLimit(String dailyCashLimit) {
		this.dailyCashLimit = dailyCashLimit;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_DAILY_CASH_LIMIT);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_DAILY_CASH_LIMIT.getValue());
		}
		appParam.setValue(dailyCashLimit);
		getParameters().put(AppParam.PMS_DAILY_CASH_LIMIT, appParam);
	}
	private void initializeDailyCashLimit() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_DAILY_CASH_LIMIT);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setDailyCashLimit(appParam.getValue());
		}
	}

	public String getCleanDays() {
		return cleanDays;
	}
	public void setCleanDays(String cleanDays) {
		this.cleanDays = cleanDays;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_ROOM_CLEAN_MAX_DAYS);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_ROOM_CLEAN_MAX_DAYS.getValue());
		}
		appParam.setValue(cleanDays);
		getParameters().put(AppParam.PMS_ROOM_CLEAN_MAX_DAYS, appParam);
	}
	private void initializeCleanDays() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_ROOM_CLEAN_MAX_DAYS);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setCleanDays(appParam.getValue());
		}
	}

	public String getPoliceCount() {
		return policeCount;
	}
	public void setPoliceCount(String policeCount) {
		this.policeCount = policeCount;
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_POLICE_COUNT);
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.PMS_POLICE_COUNT.getValue());
		}
		appParam.setValue(policeCount);
		getParameters().put(AppParam.PMS_POLICE_COUNT, appParam);
	}
	private void initializePoliceCount() {
		ApplicationParameter appParam = getParameters().get(AppParam.PMS_POLICE_COUNT);
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			setPoliceCount(appParam.getValue());
		}
	}


	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "No se pueden cargar los parámetros de PMS";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void loadParameters() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getLikeExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), "PMS_%"));
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			ApplicationParameter appParam = (ApplicationParameter)ito;
			try {
				getParameters().put(AppParam.valueOf(appParam.getName()), appParam);
			} catch (Exception ex) {
			}
		}

		initializeRoomCategory();
		initializeBoardCategory();
		initializeServiceCategory();
		initializeExtraPaxCategory();
		initializeUndefinedRoomItem();
		initializeUndefinedServiceItem();
		initializeAdvanceItem();
		initializeEarlyCheckOutItem();
		initializeNoShowItem();
		initializeCancellationItem();
		initializeTouristTaxItem();
		initializeBestPriceItem();
		initializeAutoDiscountItem();
		initializeSimpleAvailabilityURL();
		initializeBookingURL();
		initializeHhgURL();
		initializeNavSaleInvoiceURL();
		initializeNavFinanceBatchURL();
		initializeNavProductionURL();
		initializeNavUsername();
		initializeNavPassword();
		initializeProductionReportCategory();
		initializeProductionReportOpenHour();
		initializeProductionReportCloseHour();
		initializeUndefinedTariff();
		initializeAutoFbatchBank();
		initializeAutoFbatchPayMethod();
		initializeDailyCashLimit();
		initializeCleanDays();
		initializePoliceCount();
	}

	private Item obtainItem(ApplicationParameter appParam) throws ManagerBeanException {
		Item item = null;
		if (appParam != null && StringUtils.isNotEmpty(appParam.getValue())) {
			item = getReservationUtils().obtainItem(appParam.getValue());
		}
		return (item != null) ? item : (Item)BeanManager.getManagerBean(Item.class).createNewTo();
	}

	public void onAccept(ActionEvent event) throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		for (ApplicationParameter param : getParameters().values()) {
			appParamBean.insertOrUpdate(param);
		}
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
	}

}
