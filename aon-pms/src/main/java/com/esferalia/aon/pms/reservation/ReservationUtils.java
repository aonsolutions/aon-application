package com.esferalia.aon.pms.reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.SourceType;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationUtils implements IReservationConstants {

	private int domain;
	private boolean sellerUnknown;
	private boolean agencyUnknown;
	private boolean companyUnknown;

	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}

	public boolean isSellerUnknown() {
		return sellerUnknown;
	}
	public void setSellerUnknown(boolean sellerUnknown) {
		this.sellerUnknown = sellerUnknown;
	}

	public boolean isAgencyUnknown() {
		return agencyUnknown;
	}
	public void setAgencyUnknown(boolean agencyUnknown) {
		this.agencyUnknown = agencyUnknown;
	}

	public boolean isCompanyUnknown() {
		return companyUnknown;
	}
	public void setCompanyUnknown(boolean companyUnknown) {
		this.companyUnknown = companyUnknown;
	}

	public void init() {
		domain = 0;
		sellerUnknown = false;
		agencyUnknown = false;
		companyUnknown = false;
	}

	public void fillProject(ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.getProject() == null) {
			reservation.setProject(new Project());
		}
		reservation.getProject().setProjectType(null);
		reservation.getProject().setDate(reservation.getStartDate());
		reservation.getProject().setRegistry(obtainProjectReservationRegistry(reservation));
		reservation.getProject().setName(obtainProjectReservationName(reservation));
		reservation.getProject().setReservation(true);
		reservation.getProject().setActive(reservation.getStatus() == ReservationStatus.ACTIVE);
	}

    private Registry obtainProjectReservationRegistry(ProjectReservation reservation) {
    	if (reservation.getBookingHolder() == BookingHolder.AGENCY && reservation.getAgency() != null && reservation.getAgency().getId() != null) {
    		return reservation.getAgency().getRegistry();
    	} else if (reservation.getBookingHolder() == BookingHolder.COMPANY && reservation.getCompany() != null && reservation.getCompany().getId() != null) {
    		return reservation.getCompany().getRegistry();
    	}
    	return reservation.getHotel().getCustomer().getRegistry();
    }

    private String obtainProjectReservationName(ProjectReservation reservation) throws ManagerBeanException {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String dates = formatter.format(reservation.getStartDate()) + "-" + formatter.format(reservation.getEndDate());
    	String guest = reservation.getGuestFullName();
    	String code = reservation.getCode();
    	return StringUtils.abbreviate(dates + (guest == null ? "" : " " + guest) + (code == null ? "" : " (" + reservation.getCode() + ")"), 64);
    }

    public void insertProjectReservationRoomDetails(ProjectReservationRoom reservationRoom, Room room, Integer[] services) throws ManagerBeanException {
    	Date startDate = reservationRoom.getProjectReservation().getStartDate();
    	Date endDate = reservationRoom.getProjectReservation().getEndDate();
    	insertProjectReservationRoomDetails(reservationRoom, startDate, endDate, room, services);
    }

    public void insertProjectReservationRoomDetails(ProjectReservationRoom reservationRoom, Date fromDate, Date toDate, Room room, Integer[] services) 
    		throws ManagerBeanException {
    	IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
    	IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
    	Date effectiveDate = fromDate;
		while (effectiveDate.compareTo(toDate) < 0) {
			AssetActivity assetActivity = new AssetActivity();
			assetActivity.setAsset(room.getAsset());
			assetActivity.setDate(effectiveDate);
			assetActivity.setFromTime(effectiveDate);
			assetActivity.setToTime(effectiveDate);
			assetActivity.setStatus(ActivityStatus.BUSY);
			assetActivity = (AssetActivity)assetActivityBean.insert(assetActivity);

			ProjectReservationRoomDetail reservationRoomDetail = new ProjectReservationRoomDetail();
			reservationRoomDetail.setProjectReservationRoom(reservationRoom);
			reservationRoomDetail.setAssetActivity(assetActivity);
			reservationRoomDetail = (ProjectReservationRoomDetail)reservationRoomDetailBean.insert(reservationRoomDetail);
			if (services != null && services.length > 0) {
				IManagerBean resServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
				Criteria criteria = new Criteria();
				String alias = resServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
				criteria.addEqualExpression(alias, reservationRoom.getProjectReservation().getId());
				alias = resServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
				criteria.addExpression(ExpressionUtilities.getInExpression(alias, Arrays.asList(services)));
				criteria.addEqualExpression(resServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE), effectiveDate);
				criteria.addNullExpression(resServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL));
				for (ITransferObject ito : resServiceDetailBean.getList(criteria)) {
					ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
					reservationServiceDetail.setProjectReservationRoomDetail(reservationRoomDetail);
					resServiceDetailBean.update(reservationServiceDetail);
				}
			}
			effectiveDate = DateUtils.addDays(effectiveDate, 1);
		}
    }

    public void updateProjectReservationRoomDetails(ProjectReservationRoom reservationRoom, Date fromDate, Date toDate, Room room) throws ManagerBeanException {
    	IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
    	IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
    	Criteria criteria = new Criteria();
    	String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
    	criteria.addEqualExpression(alias, reservationRoom.getId());
    	criteria.addGreaterThanOrEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), fromDate);
    	criteria.addLessThanExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), toDate);
    	for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
    		ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)ito;
    		AssetActivity assetActivity = reservationRoomDetail.getAssetActivity();
    		assetActivity.setAsset(room.getAsset());
    		assetActivityBean.update(assetActivity);
    	}
    }

	public void removeProjectReservationRoomDetails(ProjectReservationRoom reservationRoom, boolean removeService) throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
		criteria.addEqualExpression(alias, reservationRoom.getId());
		for (ITransferObject roomDetail : reservationRoomDetailBean.getList(criteria)) {
			ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)roomDetail;
			criteria = new Criteria();
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ID);
			criteria.addEqualExpression(alias, reservationRoomDetail.getId());
			for (ITransferObject serviceDetail : reservationServiceDetailBean.getList(criteria)) {
				ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)serviceDetail;
				if (removeService) {
					reservationServiceDetailBean.remove(reservationServiceDetail);
				} else {
					reservationServiceDetail.setProjectReservationRoomDetail(null);
					reservationServiceDetailBean.update(reservationServiceDetail);
				}
			}
			reservationRoomDetailBean.remove(reservationRoomDetail);
			if (reservationRoomDetail.getAssetActivity() != null) {
				assetActivityBean.remove(reservationRoomDetail.getAssetActivity());
			}
		}
	}

    public void insertProjectReservationServiceDetails(ProjectReservationService reservationService, Date fromDate, Date toDate, double quantity, double price, 
    													ProjectReservationRoom reservationRoom, IPriceStrategy strategy) throws ManagerBeanException {
    	IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
    	Date effectiveDate = fromDate;
		while (effectiveDate.compareTo(toDate) < 0) {
			ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
			reservationServiceDetail.setProjectReservationService(reservationService);
			reservationServiceDetail.setEffectiveDate(effectiveDate);
			reservationServiceDetail.setQuantity(quantity);
			reservationServiceDetail.setPrice(price);
			reservationServiceDetail.setTaxableBase(strategy.getBasePrice(reservationServiceDetail));
			if (reservationRoom != null) {
				IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
				Criteria criteria = new Criteria();
				String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
				criteria.addEqualExpression(alias, reservationRoom.getId());
				criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), effectiveDate);
				for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
					ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)ito;
					reservationServiceDetail.setProjectReservationRoomDetail(reservationRoomDetail);
					break;
				}
			}
			reservationServiceDetailBean.insert(reservationServiceDetail);
			effectiveDate = DateUtils.addDays(effectiveDate, 1);
		}
    }

    public void updateProjectReservationServiceDetails(ProjectReservationService reservationService, Double quantity, Double price, 
    													ProjectReservationRoom reservationRoom, IPriceStrategy strategy) throws ManagerBeanException {
    	IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
		criteria.addEqualExpression(alias, reservationService.getId());
		for (ITransferObject serviceDetail : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)serviceDetail;
			if (quantity != null && price != null) {
				reservationServiceDetail.setQuantity(quantity);
				reservationServiceDetail.setPrice(price);
				reservationServiceDetail.setTaxableBase(strategy.getBasePrice(reservationServiceDetail));
			}
			if (reservationRoom != null) {
		    	Date effectiveDate = reservationServiceDetail.getEffectiveDate();
				IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
				criteria = new Criteria();
				alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
				criteria.addEqualExpression(alias, reservationRoom.getId());
				criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), effectiveDate);
				for (ITransferObject roomDetail : reservationRoomDetailBean.getList(criteria)) {
					ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)roomDetail;
					reservationServiceDetail.setProjectReservationRoomDetail(reservationRoomDetail);
					break;
				}
			}
			reservationServiceDetailBean.update(reservationServiceDetail);
		}
    }
    
    public void removeProjectReservationServiceDetails(ProjectReservationService reservationService) throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
		criteria.addEqualExpression(alias, reservationService.getId());
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			reservationServiceDetailBean.remove(reservationServiceDetail);
		}
	}

    public double getReservationCalculatedTaxableBase(ProjectReservation reservation) {
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		return strategy.getTaxableBase(reservation);
    }

    public double getReservationCalculatedVatQuota(ProjectReservation reservation) throws ManagerBeanException {
		Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(reservation.getProject().getRegistry().getId());
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		return strategy.getTotalVatQuota(reservation, customer);
    }


    public Hotel obtainHotel(String hotelCode) throws ManagerBeanException, ReservationException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_CODE), hotelCode);
		for (ITransferObject ito : hotelBean.getList(criteria)) {
			return (Hotel)ito;
		}

		throw new ReservationException("Invalid Hotel: " + hotelCode, 361);
    }

	public Date obtainCreationDate(String date, String time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd+hh:mm:ss");
			return dateFormat.parse(date + "+" + time);
		} catch (ParseException ex) {
			return new Date();
		}
	}

	public Seller obtainSeller(SourceType sellerSource) throws ManagerBeanException {
		Seller seller = null;
		if (sellerSource != null) {
			String cro = sellerSource.getRequestorID().getID();
			if (StringUtils.isNotEmpty(cro)) {
				IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), CRO);
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), cro);
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
				RegistryAddInfo rAddInfo = null;
				for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
					rAddInfo = (RegistryAddInfo)ito;
					break;
				}

				if (rAddInfo != null) {
					IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
					criteria = new Criteria();
					criteria.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_REGISTRY_ID), rAddInfo.getRegistry().getId());
					criteria.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_DOMAIN), domain);
					for (ITransferObject ito : sellerBean.getList(criteria)) {
						seller = (Seller)ito;
						break;
					}

					if (seller == null) {
						seller = new Seller();
						seller.setRegistry(rAddInfo.getRegistry());
						seller.setDomain(domain);
						seller.setStatus(SellerStatus.ACTIVE);
						seller = (Seller)sellerBean.insert(seller);
					}
				} else {
					setSellerUnknown(true);
				}
			}
		}
		return seller;
	}

	public Customer obtainAgency(ProfileInfo agencyInfo) throws ManagerBeanException {
		if (agencyInfo != null) {
			String code = agencyInfo.getUniqueID().getID();
			String context = agencyInfo.getUniqueID().getIDContext();
			if (StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(context)) {
				IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), context.toUpperCase());
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), code);
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
				RegistryAddInfo rAddInfo = null;
				for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
					rAddInfo = (RegistryAddInfo)ito;
					break;
				}

				if (rAddInfo != null) {
					IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
					criteria = new Criteria();
					criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), rAddInfo.getRegistry().getId());
					criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_DOMAIN), domain);
					for (ITransferObject ito : customerBean.getList(criteria)) {
						return (Customer)ito;
					}
				}
				setAgencyUnknown(true);
			}
		}
		return null;
	}

	public double obtainAgencyCommissionPercent(ProfileInfo agencyInfo) throws ManagerBeanException {
		double percent = 0;
		if (agencyInfo != null && agencyInfo.getProfile().getAgreements().sizeOfCommissionInfoArray() > 0) {
			percent = agencyInfo.getProfile().getAgreements().getCommissionInfoArray(0).getAmount().doubleValue();
		}
		return percent;
	}

	public double obtainAgencyCommissionAmount(ProfileInfo agencyInfo) throws ManagerBeanException {
		double percent = 0;
		if (agencyInfo != null && agencyInfo.getProfile().getAgreements().sizeOfCommissionInfoArray() > 0) {
			percent = agencyInfo.getProfile().getAgreements().getCommissionInfoArray(0).getAmount().doubleValue();
		}
		return percent;
	}

	public Customer obtainCompany(ProfileInfo companyInfo) throws ManagerBeanException {
		if (companyInfo != null) {
			String code = companyInfo.getUniqueID().getID();
			if (StringUtils.isNotEmpty(code)) {
				IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), COMPANY);
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), code);
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
				RegistryAddInfo rAddInfo = null;
				for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
					rAddInfo = (RegistryAddInfo)ito;
					break;
				}

				if (rAddInfo != null) {
					IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
					criteria = new Criteria();
					criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), rAddInfo.getRegistry().getId());
					criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_DOMAIN), domain);
					for (ITransferObject ito : customerBean.getList(criteria)) {
						return (Customer)ito;
					}
				}
				setCompanyUnknown(true);
			}
		}
		return null;
	}

	public BookingHolder obtainBookingHolder(String bookingHolder) {
		if (bookingHolder == null || bookingHolder.equals(GUEST_HOLDER)) {
			return BookingHolder.GUEST;
		} else if (bookingHolder.equals(AGENCY_HOLDER)) {
			return BookingHolder.AGENCY;
		} else if (bookingHolder.equals(BOOKING_HOLDER)) {
			return BookingHolder.COMPANY;
		}
		return BookingHolder.GUEST;
	}

	public void verifySellerEntity(ProjectReservation reservation, SourceType sellerSource) {
		if (isSellerUnknown()) {
			String remarks = reservation.getRemarks();
			remarks = "CANAL DE VENTA DESCONOCIDO [CRO: " + sellerSource.getRequestorID().getID() + "]\n" + remarks;
			reservation.setRemarks(remarks);
			reservation.setStatus(ReservationStatus.BLOCKED);
		}
	}

	public void verifyAgencyEntity(ProjectReservation reservation, ProfileInfo agencyInfo) {
		if (isAgencyUnknown()) {
			String remarks = reservation.getRemarks();
			String agencyContext = agencyInfo.getUniqueID().getIDContext().toUpperCase();
			String agencyName = "";
			if (agencyInfo.getProfile().getCompanyInfo().sizeOfCompanyNameArray() > 0) {
				agencyName = agencyInfo.getProfile().getCompanyInfo().getCompanyNameArray(0).getStringValue();
			}
			remarks = "AGENCIA DESCONOCIDA [" + agencyContext + ": " + agencyInfo.getUniqueID().getID() + " - " + agencyName + "]\n" + remarks;
			reservation.setRemarks(remarks);
			reservation.setStatus(ReservationStatus.BLOCKED);
		}
	}

	public void verifyCompanyEntity(ProjectReservation reservation, ProfileInfo companyInfo) {
		if (isCompanyUnknown()) {
			String remarks = reservation.getRemarks();
			String companyName = "";
			if (companyInfo.getProfile().getCompanyInfo().sizeOfCompanyNameArray() > 0) {
				companyName = companyInfo.getProfile().getCompanyInfo().getCompanyNameArray(0).getStringValue();
			}
			remarks = "EMPRESA DESCONOCIDA [COMPANY: " + companyInfo.getUniqueID().getID() + " - " + companyName + "]\n" + remarks;
			reservation.setRemarks(remarks);
			reservation.setStatus(ReservationStatus.BLOCKED);
		}
	}

	public Item obtainRoomItem(String itemCode) throws ManagerBeanException, ReservationException {
		Item item = obtainItem(itemCode);
		if (item != null) {
			return item;
		}
		throw new ReservationException("Invalid Room Type: " + itemCode, 131);
	}

	public Item obtainServiceItem(String itemCode) throws ManagerBeanException, ReservationException {
		Item item = obtainItem(itemCode);
		if (item != null) {
			return item;
		}
		throw new ReservationException("Invalid Service: " + itemCode, 146);
	}

	public Item obtainItem(String itemCode) throws ManagerBeanException{
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), itemCode);
		for (ITransferObject ito : itemBean.getList(criteria)) {
			return (Item)ito;
		}
		return null;
	}

	public Tariff obtainTariff(String tariffCode) throws ManagerBeanException, ReservationException {
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_CODE), tariffCode);
		for (ITransferObject ito : tariffBean.getList(criteria)) {
			return (Tariff)ito;
		}

		throw new ReservationException("Invalid Rate Code: " + tariffCode, 249);
	}

}
