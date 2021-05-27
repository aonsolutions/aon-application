package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.CreditCardType;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.enumeration.TouristTaxFreeCause;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class PmsCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> reservationStatuses;
	private List<SelectItem> reservationDivertStatuses;
	private List<SelectItem> bookingHolders;
	private List<SelectItem> personDocumentTypes;
	private List<SelectItem> touristTaxFreeCauses;
	private List<SelectItem> creditCardTypes;

	public Hotel getHotel() {
		return null;
	}
	public void setHotel(Hotel hotel) {
	}

	public List<SelectItem> getCurrentUserHotels() throws ManagerBeanException {
		List<SelectItem> currentUserHotels = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCurrentUserHotelList()) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			currentUserHotels.add(item);
		}
		return currentUserHotels;
	}
	
	public int getCurrentUserHotelsCount() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		return hotelBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserHotelList() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		criteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		return hotelBean.getList(criteria);
	}
	
	public List<Integer> getCurrentUserHotelIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for (ITransferObject ito: getCurrentUserHotelList()) {
			Hotel hotel = (Hotel)ito;
			list.add(hotel.getId());
		}
		return list;
	}

	public List<SelectItem> getCurrentUserServiceHotels() throws ManagerBeanException {
		List<SelectItem> currentUserServiceHotels = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCurrentUserServiceHotelList()) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			currentUserServiceHotels.add(item);
		}
		return currentUserServiceHotels;
	}
	
	public int getCurrentUserServiceHotelsCount() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		criteria.addNotNullExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_SERVICE_CATALOGUE_ID));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		return hotelBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserServiceHotelList() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		criteria.addNotNullExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_SERVICE_CATALOGUE_ID));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		criteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		return hotelBean.getList(criteria);
	}
	
	public List<SelectItem> getAgencies() throws ManagerBeanException {
		List<SelectItem> agencies = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCustomerList(true)) {
			Customer agency = (Customer)ito;
			SelectItem agencyItem = new SelectItem(agency, agency.getRegistry().getFullName());
			agencies.add(agencyItem);
		}
		return agencies;
	}

	public List<Integer> getAgencyIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for (ITransferObject ito: getCustomerList(true)) {
			Customer agency = (Customer)ito;
			list.add(agency.getId());
		}
		return list;
	}

	public List<SelectItem> getCompanies() throws ManagerBeanException {
		List<SelectItem> companies = new LinkedList<SelectItem>();
		for (ITransferObject ito : getCustomerList(false)) {
			Customer company = (Customer)ito;
			SelectItem companyItem = new SelectItem(company, company.getRegistry().getFullName());
			companies.add(companyItem);
		}
		return companies;
	}

	private List<ITransferObject> getCustomerList(boolean agency) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		if (agency) {
			criteria.addNotEqualExpression("Customer.registry.segments.segment.name", IReservationConstants.COMPANY);
		} else {
			criteria.addEqualExpression("Customer.registry.segments.segment.name", IReservationConstants.COMPANY);
		}
		criteria.addEqualExpression("Customer.registry.addInfos.attribute", IReservationConstants.SOLRES.toUpperCase());
		if (PmsUtils.isAgencyUser()) {
			criteria.addInExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_ID), PmsUtils.getUserAgencies());
		}
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, customerBean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID));
		criteria.addOrder(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_NAME));
		return customerBean.getList(criteria);
	}

	public List<SelectItem> getAgencyGroups() throws ManagerBeanException {
		List<SelectItem> groups = new LinkedList<SelectItem>();
		for (ITransferObject ito : getAgencyGroupList()) {
			InvoicingGroup invoicingGroup = (InvoicingGroup)ito;
			SelectItem groupItem = new SelectItem(invoicingGroup, invoicingGroup.getDescription());
			groups.add(groupItem);
		}
		return groups;
	}

	public int getAgencyGroupsCount() throws ManagerBeanException {
		IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_CUSTOMER_SCOPE_ID));
		return invoicingGroupBean.getCount(criteria);
	}

	private List<ITransferObject> getAgencyGroupList() throws ManagerBeanException {
		IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_CUSTOMER_SCOPE_ID));
		criteria.addOrder(invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_DESCRIPTION));
		return invoicingGroupBean.getList(criteria);
	}

	public List<Integer> getAgencyGroupIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for (ITransferObject ito: getAgencyGroupList()) {
			InvoicingGroup agencyGroup = (InvoicingGroup)ito;
			list.add(agencyGroup.getId());
		}
		return list;
	}

	public List<SelectItem> getAllotmentAgencies() throws ManagerBeanException {
		List<SelectItem> customers = new LinkedList<SelectItem>();
		IManagerBean allotmentBean = BeanManager.getManagerBean(Allotment.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_ID));
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_STATUS), CustomerStatus.ACTIVE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_SCOPE_ID));
		criteria.addOrder(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_REGISTRY_NAME));
		Projection projection = Projection.group(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY));
		for (Object obj : allotmentBean.getList(new ProjectionList(projection), criteria)) {
			Customer customer = (Customer)obj;
			SelectItem customerItem = new SelectItem(customer, customer.getRegistry().getFullName());
			customers.add(customerItem);
		}
		return customers;
	}

	public List<SelectItem> getAllotmentAgencyGroups() throws ManagerBeanException {
		List<SelectItem> groups = new LinkedList<SelectItem>();
		IManagerBean allotmentBean = BeanManager.getManagerBean(Allotment.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_ID));
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_ACTIVE), Boolean.TRUE);
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_CUSTOMER_SCOPE_ID));
		criteria.addOrder(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_DESCRIPTION));
		Projection projection = Projection.group(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP));
		for (Object obj : allotmentBean.getList(new ProjectionList(projection), criteria)) {
			InvoicingGroup invoicingGroup = (InvoicingGroup)obj;
			SelectItem groupItem = new SelectItem(invoicingGroup, invoicingGroup.getDescription());
			groups.add(groupItem);
		}
		return groups;
	}

	public int getAllotmentAgencyGroupsCount() throws ManagerBeanException {
		IManagerBean allotmentBean = BeanManager.getManagerBean(Allotment.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_ID));
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_ACTIVE), Boolean.TRUE);
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_AGENCY_GROUP_CUSTOMER_SCOPE_ID));
		return allotmentBean.getCount(criteria);
	}

	public List<SelectItem> getAllotmentRateCodes() throws ManagerBeanException {
		List<SelectItem> rateCodes = new LinkedList<SelectItem>();
		IManagerBean allotmentBean = BeanManager.getManagerBean(Allotment.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_RATE_CODE));
		criteria.addEqualExpression(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_ACTIVE), Boolean.TRUE);
		criteria.addOrder(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_RATE_CODE));
		Projection projection = Projection.group(allotmentBean.getFieldName(IEntityAlias.ALLOTMENT_RATE_CODE));
		for (Object obj : allotmentBean.getList(new ProjectionList(projection), criteria)) {
			String rateCode = (String)obj;
			SelectItem rateCodeItem = new SelectItem(rateCode, rateCode);
			rateCodes.add(rateCodeItem);
		}
		return rateCodes;
	}

	public List<SelectItem> getSellers() throws ManagerBeanException {
		List<SelectItem> sellers = new LinkedList<SelectItem>();
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_STATUS), SellerStatus.ACTIVE);
		criteria.addOrder(sellerBean.getFieldName(IEntityAlias.SELLER_REGISTRY_NAME));
		for (ITransferObject ito : sellerBean.getList(criteria)) {
			Seller seller = (Seller)ito;
			SelectItem sellerItem = new SelectItem(seller, seller.getRegistry().getFullName());
			sellers.add(sellerItem);
		}
		return sellers;
	}

	public List<SelectItem> getRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		int roomCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_ROOM_CATEGORY));
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), roomCategory);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		for (ITransferObject itr : itemBean.getList(criteria)) {
			Item item = (Item)itr;
			SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			roomItems.add(roomItem);
		}
		return roomItems;
	}

	public List<SelectItem> getServiceItems() throws ManagerBeanException {
		List<SelectItem> serviceItems = new LinkedList<SelectItem>();
		int roomCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_ROOM_CATEGORY));
		int serviceCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_SERVICE_CATEGORY));
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria = new Criteria();
		Expression roomExp = ExpressionUtilities.getEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), roomCategory);
		Expression serviceExp = ExpressionUtilities.getEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), serviceCategory);
		criteria.addExpression(ExpressionUtilities.getOrExpression(roomExp, serviceExp));
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		for (ITransferObject itr : itemBean.getList(criteria)) {
			Item item = (Item)itr;
			SelectItem serviceItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			serviceItems.add(serviceItem);
		}
		return serviceItems;
	}

	public List<SelectItem> getExtraPaxItems() throws ManagerBeanException {
		List<SelectItem> extraPaxItems = new LinkedList<SelectItem>();
		int extraPaxCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_EXTRAPAX_CATEGORY));
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), extraPaxCategory);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		for (ITransferObject itr : itemBean.getList(criteria)) {
			Item item = (Item)itr;
			SelectItem extraPaxItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			extraPaxItems.add(extraPaxItem);
		}
		return extraPaxItems;
	}

	public List<SelectItem> getServiceAndExtraPaxItems() throws ManagerBeanException {
		List<SelectItem> serviceItems = new LinkedList<SelectItem>();
		int roomCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_ROOM_CATEGORY));
		int serviceCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_SERVICE_CATEGORY));
		int extraPaxCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_EXTRAPAX_CATEGORY));
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria = new Criteria();
		Expression roomExp = ExpressionUtilities.getEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), roomCategory);
		Expression serviceExp = ExpressionUtilities.getEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), serviceCategory);
		Expression extraPaxExp = ExpressionUtilities.getEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), extraPaxCategory);
		criteria.addExpression(ExpressionUtilities.getOrExpression(roomExp, ExpressionUtilities.getOrExpression(serviceExp, extraPaxExp)));
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		for (ITransferObject itr : itemBean.getList(criteria)) {
			Item item = (Item)itr;
			SelectItem serviceItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			serviceItems.add(serviceItem);
		}
		return serviceItems;
	}

	public List<SelectItem> getBoardItems() throws ManagerBeanException {
		List<SelectItem> boardItems = new LinkedList<SelectItem>();
		int boardCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_BOARD_CATEGORY));
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), boardCategory);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_COMPOSITION), Boolean.FALSE);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		for (ITransferObject itr : itemBean.getList(criteria)) {
			Item item = (Item)itr;
			SelectItem boardItem = new SelectItem(item, item.getProduct().getName());
			boardItems.add(boardItem);
		}
		return boardItems;
	}

	public List<SelectItem> getTariffs() throws ManagerBeanException {
		List<SelectItem> tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_PURCHASE), Boolean.FALSE);
		criteria.addOrder(tariffBean.getFieldName(IEntityAlias.TARIFF_CODE));
		for (ITransferObject ito : tariffBean.getList(criteria)) {
			Tariff tariff = (Tariff)ito;
			SelectItem item = new SelectItem(tariff, tariff.getCode() + " - " + tariff.getName());
			tariffs.add(item);
		}
		return tariffs;
	}
	
	public List<SelectItem> getReservationCheckStatuses(boolean abbreviated, boolean excludeCheckOut, boolean excludeNoShow) {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> reservationCheckStatuses = new LinkedList<SelectItem>();
		for (ReservationCheckStatus status : ReservationCheckStatus.values()) {
			if (excludeCheckOut && status == ReservationCheckStatus.CHECK_OUT) {
				continue;
			}
			if (excludeNoShow && (status == ReservationCheckStatus.NO_SHOW || status == ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE)) {
				continue;
			}

			String name = status.getName(locale);
			if (abbreviated) {
				if (status == ReservationCheckStatus.NO_CHECK) {
					name = "No";
				} else if (status == ReservationCheckStatus.CHECK_IN) {
					name = "In";
				} else if (status == ReservationCheckStatus.CHECK_OUT) {
					name = "Out";
				} 
			}
			SelectItem item = new SelectItem(status, name);
			reservationCheckStatuses.add(item);
		}
		return reservationCheckStatuses;
	}
	
	public List<SelectItem> getAbbreviatedReservationCheckStatuses() {
		return getReservationCheckStatuses(true, false, false);
	}
	
	public List<SelectItem> getAbbreviatedReservationInOutCheckStatuses() {
		return getReservationCheckStatuses(true, false, true);
	}
	
	public List<SelectItem> getAbbreviatedReservationInCheckStatuses() {
		return getReservationCheckStatuses(true, true, true);
	}
	
	public List<SelectItem> getReservationStatuses() {
		if (reservationStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationStatuses = new LinkedList<SelectItem>();
			for (ReservationStatus status : ReservationStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				reservationStatuses.add(item);
			}
		}
		return reservationStatuses;
	}
	
	public List<SelectItem> getReservationDivertStatuses() {
		if (reservationDivertStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reservationDivertStatuses = new LinkedList<SelectItem>();
			for (ReservationDivertStatus status : ReservationDivertStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				reservationDivertStatuses.add(item);
			}
		}
		return reservationDivertStatuses;
	}

	public List<SelectItem> getBookingHolders() {
		if (bookingHolders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			bookingHolders = new LinkedList<SelectItem>();
			for (BookingHolder holder : BookingHolder.values()) {
				String name = holder.getName(locale);
				SelectItem item = new SelectItem(holder, name);
				bookingHolders.add(item);
			}
		}
		return bookingHolders;
	}

	public List<SelectItem> getPersonDocumentTypes() {
		if (personDocumentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			personDocumentTypes = new LinkedList<SelectItem>();
			personDocumentTypes.add(new SelectItem(DocumentType.NIF, DocumentType.NIF.getName(locale)));
			personDocumentTypes.add(new SelectItem(DocumentType.NIE, DocumentType.NIE.getName(locale)));
			personDocumentTypes.add(new SelectItem(DocumentType.PASSPORT, DocumentType.PASSPORT.getName(locale)));
		}
		return personDocumentTypes;
	}

	public List<SelectItem> getTouristTaxFreeCauses() {
		if (touristTaxFreeCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			touristTaxFreeCauses = new LinkedList<SelectItem>();
			for (TouristTaxFreeCause cause : TouristTaxFreeCause.values()) {
				String name = cause.getName(locale);
				SelectItem item = new SelectItem(cause, name);
				touristTaxFreeCauses.add(item);
			}
		}
		return touristTaxFreeCauses;
	}

	public List<SelectItem> getCreditCardTypes() {
		if (creditCardTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			creditCardTypes = new LinkedList<SelectItem>();
			for (CreditCardType type : CreditCardType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				creditCardTypes.add(item);
			}
		}
		return creditCardTypes;
	}

	public List<SelectItem> getYearItems() throws ManagerBeanException {
		List<SelectItem> yearItems = new LinkedList<SelectItem>();
		Integer currentYear = CommonUtil.getYear(new Date());
		for(Integer y=2010; y<=currentYear; y++){
			SelectItem item = new SelectItem(y, y.toString());
			yearItems.add(item);
		}
		return yearItems;
	}

}
