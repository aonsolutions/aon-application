package com.esferalia.aon.pms.reservation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.opentravel.ota.x2003.x05.AmountType;
import org.opentravel.ota.x2003.x05.CommentType.Comment;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.RatePlanType;
import org.opentravel.ota.x2003.x05.RoomTypeType;
import org.opentravel.ota.x2003.x05.ServicesType.Service;
import org.opentravel.ota.x2003.x05.SourceType;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Tariff;
import com.code.aon.config.TariffAddInfo;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAddInfo;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
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
		reservation.getProject().setAlias(reservation.getCrsCode());
		reservation.getProject().setReservation(true);
		reservation.getProject().setActive(reservation.getStatus() == ReservationStatus.ACTIVE);
	}

    private Registry obtainProjectReservationRegistry(ProjectReservation reservation) {
    	if (reservation.getBookingHolder() == BookingHolder.AGENCY && reservation.getAgency() != null && reservation.getAgency().getId() != null) {
    		return reservation.getAgency().getRegistry();
    	} else if (reservation.getBookingHolder() == BookingHolder.COMPANY && reservation.getCompany() != null && reservation.getCompany().getId() != null) {
    		return reservation.getCompany().getRegistry();
    	}
    	return reservation.getHotelReservation().getCustomer().getRegistry();
    }

    private String obtainProjectReservationName(ProjectReservation reservation) throws ManagerBeanException {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String dates = formatter.format(reservation.getStartDate()) + "-" + formatter.format(reservation.getEndDate());
    	String guest = reservation.getGuestFullName();
    	String code = reservation.getCode();
    	return StringUtils.abbreviate(dates + (guest == null ? "" : " " + guest) + (code == null ? "" : " (" + reservation.getCode() + ")"), 64);
    }

    private void updateBooking(ProjectReservationRoom reservationRoom) throws ManagerBeanException {
    	reservationRoom.setForceRefreshBooking(true);
    	BeanManager.getManagerBean(ProjectReservationRoom.class).update(reservationRoom);
    }

    public Map<Tax, Double> getReservationServicesTaxableBases(Integer reservationId, Date fromDate, Date toDate) throws ManagerBeanException {
		Map<Tax, Double> reservationBases = new HashMap<Tax, Double>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), reservationId);
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE;
		criteria.addBetweenExpression(reservationServiceDetailBean.getFieldName(alias), fromDate, toDate);
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), false);
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			Tax vat = reservationServiceDetail.getProjectReservationService().getItem().getProduct().getVat();
			double base = reservationServiceDetail.getTaxableBase();
			if (reservationBases.containsKey(vat)) {
				base += reservationBases.get(vat);
			}
			reservationBases.put(vat, CommonUtil.round(base, 4));
		}
		return reservationBases;
	}

    public void releaseProjectReservationResources(ProjectReservation reservation, boolean removeService, Date effectiveDate) throws ManagerBeanException {
    	IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
    	for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
    		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
    		removeProjectReservationRoomDetails(reservationRoom, removeService, effectiveDate);
    	}
    	removeProjectReservationServiceDetails(reservation, removeService, effectiveDate);
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
		while (effectiveDate.before(toDate)) {
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
		updateBooking(reservationRoom);
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
		updateBooking(reservationRoom);
    }

	public void removeProjectReservationRoomDetails(ProjectReservationRoom reservationRoom, boolean removeService, Date effectiveDate) throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
		criteria.addEqualExpression(alias, reservationRoom.getId());
		if (effectiveDate != null) {
			alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
			criteria.addGreaterThanOrEqualExpression(alias, effectiveDate);
		}
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
		updateBooking(reservationRoom);
	}

	public boolean isPendingRoomAssignation(ProjectReservation reservation) throws ManagerBeanException {
		boolean pendingRooms = true;
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			pendingRooms = false;
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
			criteria.addEqualExpression(alias, reservationRoom.getId());
			if (reservationRoomDetailBean.getCount(criteria) == 0) {
				return true;
			}
		}
		return pendingRooms;
	}

	public void insertProjectReservationServiceDetails(ProjectReservationService reservationService, Date fromDate, Date toDate, double quantity, double price, 
    													ProjectReservationRoom reservationRoom, IPriceStrategy strategy) throws ManagerBeanException {
    	IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
    	Date effectiveDate = fromDate;
		while (effectiveDate.before(toDate)) {
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

	public void removeProjectReservationServiceDetails(ProjectReservation reservation, boolean removeService, Date effectiveDate) throws ManagerBeanException {
    	IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
    	Criteria criteria = new Criteria();
    	String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
    	criteria.addEqualExpression(alias, reservation.getId());
    	alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE);
    	criteria.addGreaterThanOrEqualExpression(alias, effectiveDate);
    	if (!removeService) {
        	alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL);
        	criteria.addNotNullExpression(alias);
    	}
    	for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
    		ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			if (removeService) {
				reservationServiceDetailBean.remove(reservationServiceDetail);
			} else {
				reservationServiceDetail.setProjectReservationRoomDetail(null);
				reservationServiceDetailBean.update(reservationServiceDetail);
			}
    	}
	}

	public boolean isPendingServiceAssignation(ProjectReservation reservation, boolean extraIncluded) throws ManagerBeanException {
		boolean pendingServices = true;
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), reservation.getId());
		if (!extraIncluded) {
			String alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, false);
		}
		for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
			pendingServices = false;
			ProjectReservationService reservationService = (ProjectReservationService)ito;
			criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			criteria.addEqualExpression(alias, reservationService.getId());
			criteria.addNullExpression(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL));
			if (reservationServiceDetailBean.getCount(criteria) > 0) {
				return true;
			}
		}
		return pendingServices;
	}

	public boolean isPendingDivert(Integer reservationId) throws ManagerBeanException {
		IManagerBean reservationDivertBean = BeanManager.getManagerBean(ProjectReservationDivert.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationDivertBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_PROJECT_RESERVATION_ID), reservationId);
		criteria.addEqualExpression(reservationDivertBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_STATUS), ReservationDivertStatus.PENDING);
		return (reservationDivertBean.getCount(criteria) > 0);
	}

	public double getReservationAdvancedAmount(Integer reservationId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservationId);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), true);
		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
		Object result = invoiceBean.getUniqueResult(projection, criteria);
		return (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;
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

    public double getTaxPercentage(Tax tax, Date date) throws ManagerBeanException {
		if (date.before(tax.getStartDate())) {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
	    	Criteria criteria = new Criteria();
	    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), tax.getId());
	    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
	    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
	    	for (ITransferObject ito : taxDetailBean.getList(criteria)) {
	    		TaxDetail taxDetail = (TaxDetail)ito;
	    		return taxDetail.getValue();
	    	}
		}
    	return tax.getPercentage();
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
		Customer agency = null;
		if (agencyInfo != null) {
			agency = obtainCustomer(agencyInfo);
			setAgencyUnknown(agency == null);
		}
		return agency;
	}

	public double obtainAgencyCommissionPercent(ProfileInfo agencyInfo) throws ManagerBeanException {
		double percent = 0;
		if (agencyInfo != null && agencyInfo.getProfile().getAgreements().sizeOfCommissionInfoArray() > 0) {
			percent = Double.parseDouble(agencyInfo.getProfile().getAgreements().getCommissionInfoArray(0).getStringValue());
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
		Customer company = null;
		if (companyInfo != null) {
			company = obtainCustomer(companyInfo);
			setCompanyUnknown(company == null);
		}
		return company;
	}

	private Customer obtainCustomer(ProfileInfo profileInfo) throws ManagerBeanException {
		if (profileInfo != null) {
			String code = profileInfo.getUniqueID().getID();
			String context = profileInfo.getUniqueID().getIDContext();
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
			}
		}
		return null;
	}

	public String obtainCustomerCode(Customer customer, String context) throws ManagerBeanException {
		if (customer != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), context);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
			for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
				RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
				return rAddInfo.getValue();
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

	public void verifyOperationDate(ProjectReservation reservation, String date, String time) {
		String remarks = reservation.getRemarks();
		remarks = "FECHA Y HORA DE LA OPERACION [" + date + " " + time + "]\n" + remarks;
		reservation.setRemarks(remarks);
	}

	public Item obtainRoomItem(ProjectReservation reservation, RoomTypeType roomType) throws ManagerBeanException, ReservationException {
		Item item = obtainRoomItem(roomType.getRoomTypeCode());
		if (item != null) {
			return item;
		} else {
			item = obtainDefaultRoomItem();
			if (item != null) {
				reservation.setRemarks("TIPO HABITACION DESCONOCIDO [" + roomType.getRoomTypeCode() + "]\n" + reservation.getRemarks());
				reservation.setStatus(ReservationStatus.BLOCKED);

				return item;
			}
		}
		throw new ReservationException("Invalid Room Type: " + roomType.getRoomTypeCode(), 131);
	}

	public Item obtainRoomItem(String roomCode) throws ManagerBeanException {
		ItemAddInfo itemAddInfo = obtainItemAddInfo(null, ROOM_ALIAS, roomCode);
		if (itemAddInfo != null) {
			return itemAddInfo.getItem();
		}
		return obtainItem(roomCode);
	}

	public Item obtainDefaultRoomItem() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), UNDEFINED_ROOM_ITEM);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			return obtainRoomItem(((ApplicationParameter)ito).getValue());
		}
		return null;
	}

	public Item obtainServiceItem(ProjectReservation reservation, Service service) throws ManagerBeanException, ReservationException {
		Item item = obtainServiceItem(service.getServiceInventoryCode());
		if (item != null) {
			return item;
		} else {
			item = obtainDefaultServiceItem();
			if (item != null) {
				String serviceName = obtainServiceName(service);
				reservation.setRemarks("SERVICIO DESCONOCIDO [" + service.getServiceInventoryCode() + " - " + serviceName + "]\n" + reservation.getRemarks());
				reservation.setStatus(ReservationStatus.BLOCKED);

				return item;
			}
		}
		throw new ReservationException("Invalid Service: " + service.getServiceInventoryCode(), 146);
	}

	public Item obtainServiceItem(Item roomItem, String serviceCode, String mealPlan) throws ManagerBeanException {
		Item item = obtainServiceItem(serviceCode);
		if (item != null) {
			return item;
		} else {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression("Item.composition.compositionItem.id", roomItem.getId());
			criteria.addEqualExpression("Item.addInfos.attribute", MEAL_PLAN);
			criteria.addEqualExpression("Item.addInfos.value", mealPlan);
			for (ITransferObject ito : itemBean.getList(criteria)) {
				return (Item)ito;
			}
		}
		return roomItem;
	}

	private Item obtainServiceItem(String serviceCode) throws ManagerBeanException {
		ItemAddInfo itemAddInfo = obtainItemAddInfo(null, SERVICE_ALIAS, serviceCode);
		if (itemAddInfo != null) {
			return itemAddInfo.getItem();
		}
		return obtainItem(serviceCode);
	}

	public Item obtainDefaultServiceItem() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), UNDEFINED_SERVICE_ITEM);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			return obtainServiceItem(((ApplicationParameter)ito).getValue());
		}
		return null;
	}

	private Item obtainItem(String itemCode) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), itemCode);
		for (ITransferObject ito : itemBean.getList(criteria)) {
			return (Item)ito;
		}
		return null;
	}

	private ItemAddInfo obtainItemAddInfo(Item item, String attribute, String value) throws ManagerBeanException {
		IManagerBean itemAddInfoBean = BeanManager.getManagerBean(ItemAddInfo.class);
		Criteria criteria = new Criteria();
		if (item != null) {
			criteria.addEqualExpression(itemAddInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_ITEM_ID), item.getId());
		}
		if (StringUtils.isNotEmpty(value)) {
			criteria.addEqualExpression(itemAddInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_VALUE), value);
		}
		criteria.addEqualExpression(itemAddInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_ATTRIBUTE), attribute);
		for (ITransferObject ito : itemAddInfoBean.getList(criteria)) {
			return (ItemAddInfo)ito;
		}
		return null;
	}

	private String obtainServiceName(Service service) {
		String comments = "";
		if (service.getServiceDetails() != null && service.getServiceDetails().getComments() != null) {
			for (int i=0; i<service.getServiceDetails().getComments().sizeOfCommentArray(); i++) {
				Comment comment = service.getServiceDetails().getComments().getCommentArray(i);
				if (comment.getName().equals(DESCRIPTION) && comment.sizeOfTextArray() > 0) {
					comments += comment.getTextArray(0).getStringValue();
				}
			}
		}
		return comments;
	}

	public String obtainServiceMealPlan(Service service) {
		//Devolver un ReservationMealPlan
		String comments = "";
		if (service.getServiceDetails() != null && service.getServiceDetails().getComments() != null) {
			for (int i=0; i<service.getServiceDetails().getComments().sizeOfCommentArray(); i++) {
				Comment comment = service.getServiceDetails().getComments().getCommentArray(i);
				if (comment.getName().equals(MEAL_PLAN_CODES) && comment.sizeOfTextArray() > 0) {
					comments += comment.getTextArray(0).getStringValue();
				}
			}
		}
		return comments;
	}

	public boolean isServiceBreakdown(Item item) throws ManagerBeanException {
		ItemAddInfo itemAddInfo = obtainItemAddInfo(item, SERVICE_BREAKDOWN, null);
		return (itemAddInfo == null || itemAddInfo.getValue() == null) ? true : !itemAddInfo.getValue().equalsIgnoreCase(NO);
	}

	public Tariff obtainRoomTariff(ProjectReservation reservation, RatePlanType ratePlan) throws ManagerBeanException, ReservationException {
		Tariff tariff = obtainTariff(ratePlan.getRatePlanCode());
		if (tariff != null) {
			return tariff;
		} else {
			tariff = obtainDefaultTariff();
			if (tariff != null) {
				reservation.setRemarks("TARIFA DESCONOCIDA [" + ratePlan.getRatePlanCode() + "]\n" + reservation.getRemarks());
				reservation.setStatus(ReservationStatus.BLOCKED);

				return tariff;
			}
		}
		throw new ReservationException("Invalid Rate Code: " + ratePlan.getRatePlanCode(), 249);
	}

	public Tariff obtainTariff(String tariffCode) throws ManagerBeanException {
		if (StringUtils.isNotBlank(tariffCode)) {
			TariffAddInfo tariffAddInfo = obtainTariffAddInfo(null, TARIFF_ALIAS, tariffCode);
			if (tariffAddInfo != null) {
				return tariffAddInfo.getTariff();
			} else {
				IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_CODE), tariffCode);
				for (ITransferObject ito : tariffBean.getList(criteria)) {
					return (Tariff)ito;
				}
			}
		}
		return null;
	}

	private TariffAddInfo obtainTariffAddInfo(Tariff tariff, String attribute, String value) throws ManagerBeanException {
		IManagerBean tariffAddInfoBean = BeanManager.getManagerBean(TariffAddInfo.class);
		Criteria criteria = new Criteria();
		if (tariff != null) {
			criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_TARIFF_ID), tariff.getId());
		}
		if (StringUtils.isNotEmpty(value)) {
			criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_VALUE), value);
		}
		criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_ATTRIBUTE), attribute);
		for (ITransferObject ito : tariffAddInfoBean.getList(criteria)) {
			return (TariffAddInfo)ito;
		}
		return null;
	}

	public Tariff obtainDefaultTariff() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), UNDEFINED_TARIFF);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			return obtainTariff(((ApplicationParameter)ito).getValue());
		}
		return null;
	}

	public Map<Date, List<Double>> obtainPricesMap(ProjectReservation reservation, Service service, Date fromDate, Date toDate) throws ReservationException {
		double serviceBase = 0;
		Map<Date, List<Double>> pricesMap = new HashMap<Date, List<Double>>();
		for (int i=0; i<service.sizeOfPriceArray(); i++) {
			AmountType price = service.getPriceArray(i);
			if (price.getEffectiveDate() != null) {
				Date effectiveDate = DateUtils.truncate(price.getEffectiveDate().getTime(), Calendar.DATE);
				List<Double> pricesList = pricesMap.get(effectiveDate);
				if (pricesList == null) {
					pricesList = new LinkedList<Double>();
				}
				for (int j=0; j<price.getNumberOfUnits(); j++) {
					pricesList.add(CommonUtil.round(price.getBase().getAmountBeforeTax().doubleValue() * (1 - reservation.getRealDiscountPercent() / 100), 6));
				}
				pricesMap.put(effectiveDate, pricesList);
			} else if (price.getTotal() != null) {
				serviceBase = CommonUtil.round(price.getTotal().getAmountBeforeTax().doubleValue() * (1 - reservation.getRealDiscountPercent() / 100), 6);
			}
		}

		if (pricesMap.size() > 0) {
			for (Date date=DateUtils.addDays(fromDate, 1); date.before(toDate); date=DateUtils.addDays(date, 1)) {
				if (!pricesMap.containsKey(date) && pricesMap.containsKey(DateUtils.addDays(date, -1))) {
					pricesMap.put(date, pricesMap.get(DateUtils.addDays(date, -1)));
				}
			}

			for (Date date=DateUtils.truncate(fromDate, Calendar.DATE); date.before(toDate); date=DateUtils.addDays(date, 1)) {
				for (Double price : pricesMap.get(date)) {
					serviceBase = CommonUtil.round(serviceBase - price, 6);
				}
			}
			if (CommonUtil.round(serviceBase, 4) != 0) {
				throw new ReservationException("Service Price is not correctly defined for RPH " + service.getServiceRPH() + ".", reservation.getCrsCode(), 197);
			}
		} else {
			List<Double> pricesList = new LinkedList<Double>();
			pricesList.add(serviceBase);
			pricesMap.put(fromDate, pricesList);
		}

		return pricesMap;
	}

	public int obtainPricesMapSize(Map<Date, List<Double>> pricesMap) {
		int serviceQuantity = 0;
		for (List<Double> prices : pricesMap.values()) {
			serviceQuantity = (serviceQuantity < prices.size()) ? prices.size() : serviceQuantity;
		}
		return serviceQuantity;
	}

	public Map<Double, Integer> obtainQuantityPerPriceMap(List<Double> priceList) {
		Map<Double, Integer> quantityPerPriceMap = new HashMap<Double, Integer>();
		for (Double price : priceList) {
			int quantity = 1;
			if (quantityPerPriceMap.containsKey(price)) {
				quantity = quantity + quantityPerPriceMap.get(price);
			}
			quantityPerPriceMap.put(price, quantity);
		}
		return quantityPerPriceMap;
	}

	public String getRequestMessageId(ReservationRequest request) {
		return getRequestMessageId(request, 1);
	}

	public String getRequestMessageId(ReservationRequest request, int page) {
		String messageId = PLS + page + StringUtils.leftPad(""+request.getId(), 8, "0") + StringUtils.leftPad(""+(request.getRequestCounter()+1), 2, "0");
		return messageId;
	}

	public String getRequestMessageId(ProjectReservation reservation) {
		String messageId = PLS + "0" + StringUtils.leftPad(""+reservation.getId(), 8, "0") + StringUtils.leftPad(""+(int)Math.floor(Math.random()*100), 2, "0");
		return messageId;
	}

	public Seller obtainCrsSeller() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), CRS_REGISTRY_ID);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			String crsId = ((ApplicationParameter)ito).getValue();
			if (StringUtils.isNotEmpty(crsId)) {
				return (Seller)BeanManager.getManagerBean(Seller.class).get(Integer.parseInt(crsId));
			}
		}
		return null;
	}

	public String obtainCrsAttachDescription(String actionType) {
		if (actionType.equals(ADD_RESERVATION)) {
			return CRS_ATTACH_ADD;
		} else if (actionType.equals(MODIFY_RESERVATION)) {
			return CRS_ATTACH_MODIFY;
		} else if (actionType.equals(CANCEL_RESERVATION)) {
			return CRS_ATTACH_CANCEL;
		}
		return null;
	}

	public boolean isAgencyCommission(Customer agency) throws ManagerBeanException {
		if (agency != null && agency.getId() != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), agency.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), AGENCY_COMMISSION);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
			RegistryAddInfo rAddInfo = null;
			for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
				rAddInfo = (RegistryAddInfo)ito;
				break;
			}
			return rAddInfo != null && rAddInfo.getValue().equalsIgnoreCase(YES);
		}
		return false;
	}

}
