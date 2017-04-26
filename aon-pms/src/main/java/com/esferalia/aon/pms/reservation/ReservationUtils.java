package com.esferalia.aon.pms.reservation;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.opentravel.ota.x2003.x05.AmountType;
import org.opentravel.ota.x2003.x05.CommentType.Comment;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.RatePlanType;
import org.opentravel.ota.x2003.x05.RoomTypeType;
import org.opentravel.ota.x2003.x05.ServicesType.Service;
import org.opentravel.ota.x2003.x05.SourceType;

import com.code.aon.AonVersion;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.common.util.CryptoUtil;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Tariff;
import com.code.aon.config.TariffAddInfo;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAddInfo;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.enumeration.MailAccountType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.MealPlan;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationUtils implements IReservationConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int domain;
	private boolean sellerUnknown;
	private boolean agencyUnknown;
	private boolean companyUnknown;

	public ReservationUtils() {
	}

	public ReservationUtils(int domain) {
		setDomain(domain);
	}

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

	public String obtainAllotmentRateCode(ProjectReservation reservation) throws ManagerBeanException {
		String rateCode = null;
		if (reservation.getAgency() != null && reservation.getAgency().getId() != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), reservation.getAgency().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), ALLOTMENT_RATE_CODE);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), reservation.getDomain());
			criteria.addLessThanOrEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE_DATE), reservation.getStartDate());
			criteria.addOrder(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE_DATE), false);
			Projection prjValue = Projection.property(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE));
			List<?> resultList = rAddInfoBean.getList(new ProjectionList(prjValue), criteria);
			if (resultList.size() > 0 && resultList.get(0) != null) {
				rateCode = (String)resultList.get(0);
			}
		}

		if (rateCode == null) {
			rateCode = obtainDefaultAllotmentRateCode();
		}
		return rateCode;
	}

	public String obtainDefaultAllotmentRateCode() throws ManagerBeanException {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.PMS_ALLOTMENT_RATE_CODE, domain);
		if (appParam != null) {
			return appParam.getValue();
		}
		return null;
	}

	private void updateBooking(ProjectReservationRoom reservationRoom) throws ManagerBeanException {
    	reservationRoom.setForceRefreshBooking(true);
    	BeanManager.getManagerBean(ProjectReservationRoom.class).update(reservationRoom);
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

	public List<Item> getProjectReservationRoomDetailItems(ProjectReservationRoom reservationRoom) throws ManagerBeanException {
		ProjectReservation reservation = reservationRoom.getProjectReservation();
		return getProjectReservationRoomDetailItems(reservationRoom, reservation.getStartDate(), reservation.getEndDate());
	}

	public List<Item> getProjectReservationRoomDetailItems(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) throws ManagerBeanException {
		List<Item> roomDetailItemIds = new LinkedList<Item>();
		roomDetailItemIds.add(reservationRoom.getItem());

		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionFactoryName);
		String hqlQuery = 
				"SELECT DISTINCT R.item" +
				" FROM ProjectReservationRoomDetail AS PRRD," +
					" AssetActivity AS AA," +
					" Room AS R" +
				" WHERE PRRD.projectReservationRoom.id = " + reservationRoom.getId() +
				" AND PRRD.assetActivity.id = AA.id" +
				" AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset.id = R.asset.id";
		Query query = session.createQuery(hqlQuery);
		query.setDate("start", startDate);
		query.setDate("end", endDate);
		for (Object obj : query.list()) {
			if (!roomDetailItemIds.contains((Item)obj)) {
				roomDetailItemIds.add((Item)obj);
			}
		}
		return roomDetailItemIds;
	}

	public List<Item> getProjectReservationRoomDetailItems(ProjectReservation reservation) throws ManagerBeanException {
		return getProjectReservationRoomDetailItems(reservation, reservation.getStartDate());
	}

	public List<Item> getProjectReservationRoomDetailItems(ProjectReservation reservation, Date startDate) throws ManagerBeanException {
		return getProjectReservationRoomDetailItems(reservation, startDate, reservation.getEndDate());
	}

	public List<Item> getProjectReservationRoomDetailItems(ProjectReservation reservation, Date startDate, Date endDate) throws ManagerBeanException {
		List<Item> roomDetailItemIds = new LinkedList<Item>();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionFactoryName);
		String hqlQuery = 
				"SELECT DISTINCT R.item" +
				" FROM ProjectReservationRoomDetail AS PRRD," +
					" ProjectReservationRoom AS PRR," +
					" AssetActivity AS AA," +
					" Room AS R" +
				" WHERE PRRD.projectReservationRoom.id = PRR.id" +
				" AND PRR.projectReservation.id = " + reservation.getId() +
				" AND PRRD.assetActivity.id = AA.id" +
				" AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset.id = R.asset.id";
		Query query = session.createQuery(hqlQuery);
		query.setDate("start", startDate);
		query.setDate("end", endDate);
		for (Object obj : query.list()) {
			if (!roomDetailItemIds.contains((Item)obj)) {
				roomDetailItemIds.add((Item)obj);
			}
		}

		hqlQuery = 
				" SELECT DISTINCT PRR.item" +
				" FROM ProjectReservationRoom AS PRR" +
				" WHERE PRR.projectReservation.id = " + reservation.getId();
		query = session.createQuery(hqlQuery);
		for (Object obj : query.list()) {
			if (!roomDetailItemIds.contains((Item)obj)) {
				roomDetailItemIds.add((Item)obj);
			}
		}
		return roomDetailItemIds;
	}

	public boolean isPendingRoomAssignation(ProjectReservation reservation) throws ManagerBeanException {
		boolean pendingRooms = true;
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		Projection prjRoomId = Projection.property(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ID));
		for (Object obj : reservationRoomBean.getList(new ProjectionList(prjRoomId), criteria)) {
			pendingRooms = false;
			Integer reservationRoomId = (Integer)obj;
			criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
			criteria.addEqualExpression(alias, reservationRoomId);
			if (reservationRoomDetailBean.getCount(criteria) == 0) {
				return true;
			}
		}
		return pendingRooms;
	}

	public void insertProjectReservationServiceDetails(ProjectReservationService reservationService, Date fromDate, Date toDate, double quantity, double price, 
    													ProjectReservationRoom reservationRoom, IPriceStrategy strategy) throws ManagerBeanException {
		strategy = (strategy != null) ? strategy : PriceStrategyFactory.getPriceStrategy();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
    	Date effectiveDate = fromDate;
		while (!effectiveDate.after(toDate)) {
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
		strategy = (strategy != null) ? strategy : PriceStrategyFactory.getPriceStrategy();
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
		Projection prjServiceId = Projection.property(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_ID));
		for (Object obj : reservationServiceBean.getList(new ProjectionList(prjServiceId), criteria)) {
			pendingServices = false;
			Integer reservationServiceId = (Integer)obj;
			criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			criteria.addEqualExpression(alias, reservationServiceId);
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
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), Boolean.FALSE);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), Boolean.TRUE);
		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
		Object result = invoiceBean.getUniqueResult(projection, criteria);
		return (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;
	}

	public double getReservationAdvancedVatAmount(Integer reservationId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservationId);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), Boolean.FALSE);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), Boolean.TRUE);
		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_VAT_QUOTA));
		Object result = invoiceBean.getUniqueResult(projection, criteria);
		return (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;
	}

	public double getDailyRegistryFinanceCashAmount(Date date, Registry registry) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), Boolean.FALSE);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), date);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_DOCUMENT), registry.getDocument());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_DOCUMENT_TYPE), registry.getDocumentType());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_DOCUMENT_COUNTRY), registry.getDocumentCountry());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		Projection projection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
		Object result = financeBean.getUniqueResult(projection, criteria);
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

	public double getReservationCalculatedTotal(ProjectReservation reservation) throws ManagerBeanException {
		Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(reservation.getProject().getRegistry().getId());
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		return strategy.getTotalPrice(reservation, customer);
    }

	public double getReservationTouristTaxAmount(ProjectReservation reservation, Item touristTaxItem, boolean pending) throws ManagerBeanException {
		if (touristTaxItem == null) {
			touristTaxItem = obtainTouristTaxItem();
		}
		ITariffable iTariffable = reservation.getHotel().getCustomer();
		int adults = pending ? reservation.getTouristTaxPending() : reservation.getAdultCount();

		return getReservationTouristTaxAmount(touristTaxItem, iTariffable, reservation.getStartDate(), reservation.getEndDate(), adults, pending);
	}

	public double getReservationTouristTaxAmount(Item touristTaxItem, ITariffable iTariffable, Date startDate, Date endDate, int adults, boolean pending) {
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		double vatPercent = (touristTaxItem != null) ? touristTaxItem.getVat().getPercentage() : 0;
		double amount = 0;

		InvoiceDetail calculable = new InvoiceDetail();
		calculable.setItem(touristTaxItem);
		calculable.setQuantity(0);
		for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(DateUtils.addDays(endDate, -1)); date = DateUtils.addDays(date, 1)) {
			calculable.setQuantity(calculable.getQuantity() + 1);
			double price = strategy.getUnitPrice(calculable, date, iTariffable);
			amount = CommonUtil.round(amount + adults * price, 4);
		}
		return CommonUtil.round(amount * (1 + vatPercent / 100));
	}

	public int getReservationTouristTaxPayed(Integer reservationId) throws ManagerBeanException {
		Item touristTaxItem = obtainTouristTaxItem();
		if (touristTaxItem != null) {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_PROJECT_ID), reservationId);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_TYPE), InvoiceType.SALES);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_SERVICE), Boolean.TRUE);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ADVANCE), Boolean.FALSE);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_ID), touristTaxItem.getId());
			Projection projection = Projection.sum(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_QUANTITY));
			Object result = invoiceDetailBean.getUniqueResult(projection, criteria);
			return (result != null) ? ((Double)result).intValue() : 0;
		}
		return 0;
	}

	public boolean isTouristTaxInvoice(Invoice invoice) throws ManagerBeanException {
		Item touristTaxItem = obtainTouristTaxItem();
		if (touristTaxItem != null) {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_ID), touristTaxItem.getId());
			return invoiceDetailBean.getCount(criteria) > 0;
		}
		return false;
	}


	public Hotel obtainHotel(String hotelCode) throws ManagerBeanException, ReservationException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_CODE), hotelCode);
		List<ITransferObject> hotelList = hotelBean.getList(criteria);
		if (hotelList.size() > 0) {
			return (Hotel)hotelList.get(0);
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
						seller.setScope(obtainDefaultScope());
						seller = (Seller)sellerBean.insert(seller);
					}
				} else {
					setSellerUnknown(true);
				}
			}
		}
		return seller;
	}

	public Scope obtainDefaultScope() throws ManagerBeanException {
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(scopeBean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domain);
		criteria.addOrder(scopeBean.getFieldName(IEntityAlias.SCOPE_ID));
		for (ITransferObject ito : scopeBean.getList(criteria)) {
			return (Scope)ito;
		}
		return null;
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
		double amount = 0;
		if (agencyInfo != null && agencyInfo.getProfile().getAgreements().sizeOfCommissionInfoArray() > 0) {
			amount = agencyInfo.getProfile().getAgreements().getCommissionInfoArray(0).getAmount().doubleValue();
		}
		return amount;
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
				for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
					Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(((RegistryAddInfo)ito).getRegistry().getId());
					if (customer != null && customer.getDomain() == domain && customer.getStatus() == CustomerStatus.ACTIVE) {
						return customer;
					}
				}
			}
		}
		return null;
	}

	public String[] obtainCustomerCodes(Customer customer, String context) throws ManagerBeanException {
		String[] customerCodes = ArrayUtils.EMPTY_STRING_ARRAY;
		if (customer != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), context);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
			Projection prjValue = Projection.property(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE));
			for (Object obj : rAddInfoBean.getList(new ProjectionList(prjValue), criteria)) {
				customerCodes = (String[])ArrayUtils.add(customerCodes, obj.toString());
			}
		}
		return customerCodes;
	}

	public String obtainCustomerCode(Customer customer, String context) throws ManagerBeanException {
		String[] customerCodes = obtainCustomerCodes(customer, context);
		return customerCodes.length > 0 ? customerCodes[0] : null;
	}

	private Company getCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(companyBean.getFieldName(IEntityAlias.COMPANY_DOMAIN), domain);
		for (ITransferObject ito : companyBean.getList(criteria, 0, 1)) {
			return (Company)ito;
		}
		return null;
	}

	public String[] obtainCompanyCodes(Company company, String context) throws ManagerBeanException {
		String[] companyCodes = ArrayUtils.EMPTY_STRING_ARRAY;
		if (company != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), company.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), context);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), domain);
			Projection prjValue = Projection.property(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE));
			for (Object obj : rAddInfoBean.getList(new ProjectionList(prjValue), criteria)) {
				companyCodes = (String[])ArrayUtils.add(companyCodes, obj.toString());
			}
		}
		return companyCodes;
	}

	public BookingHolder obtainBookingHolder(String bookingHolder) {
		if (bookingHolder == null || bookingHolder.equals(GUEST_HOLDER)) {
			return BookingHolder.GUEST;
		} else if (bookingHolder.equals(AGENCY_HOLDER)) {
			return BookingHolder.AGENCY;
		} else if (bookingHolder.equals(COMPANY_HOLDER)) {
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
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return obtainRoomItem(((ApplicationParameter)appParamList.get(0)).getValue());
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
		Item item = obtainServiceItemComposition(roomItem, mealPlan);
		if (item != null) {
			return item;
		}
		return obtainServiceItem(serviceCode);
	}

	private Item obtainServiceItemComposition(Item roomItem, String mealPlanValue) throws ManagerBeanException {
		if (StringUtils.isNotBlank(mealPlanValue)) {
			MealPlan mealPlan = obtainMealPlan(mealPlanValue);
			if (mealPlan != null) {
				if (mealPlan != MealPlan.SA) {
					IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression("Item.compositions.compositionItem.id", roomItem.getId());
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL), mealPlan.getValue());
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DOMAIN), domain);
					List<ITransferObject> itemList = itemBean.getList(criteria);
					if (itemList.size() > 0) {
						return (Item)itemList.get(0);
					}
				} else {
					return roomItem;
				}
			}
		}
		return null;
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
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return obtainServiceItem(((ApplicationParameter)appParamList.get(0)).getValue());
		}
		return null;
	}

	public Item obtainAppParamItem(AppParam param) throws ManagerBeanException {
		ApplicationParameter appParam = AppParamUtil.getParameter(param, domain);
		if (appParam != null) {
			return obtainItem(appParam.getValue());
		}
		return null;
	}

	public Item obtainItem(String itemCode) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), itemCode);
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DOMAIN), domain);
		List<ITransferObject> itemList = itemBean.getList(criteria);
		if (itemList.size() > 0) {
			return (Item)itemList.get(0);
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
		criteria.addEqualExpression(itemAddInfoBean.getFieldName(IEntityAlias.ITEM_ADD_INFO_DOMAIN), domain);
		List<ITransferObject> itemAddInfoList = itemAddInfoBean.getList(criteria);
		if (itemAddInfoList.size() > 0) {
			return (ItemAddInfo)itemAddInfoList.get(0);
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

	public MealPlan obtainServiceMealPlan(Service service) {
		String comments = "";
		if (service.getServiceDetails() != null && service.getServiceDetails().getComments() != null) {
			for (int i=0; i<service.getServiceDetails().getComments().sizeOfCommentArray(); i++) {
				Comment comment = service.getServiceDetails().getComments().getCommentArray(i);
				if (comment.getName().equals(MEAL_PLAN_CODES) && comment.sizeOfTextArray() > 0) {
					comments += comment.getTextArray(0).getStringValue();
				}
			}
		}
		return obtainMealPlan(comments);
	}

	public MealPlan obtainMealPlan(String mealPlanValue) {
		for (MealPlan mealPlan : MealPlan.values()) {
			if (mealPlan.getValue().equals(mealPlanValue) || mealPlan.getCrsValue().equals(mealPlanValue)) {
				return mealPlan;
			}
		}
		return null;
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
				criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_PURCHASE), Boolean.FALSE);
				criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_DOMAIN), domain);
				List<ITransferObject> tariffList = tariffBean.getList(criteria);
				if (tariffList.size() > 0) {
					return (Tariff)tariffList.get(0);
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
		criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_DOMAIN), domain);
		List<ITransferObject> tariffAddInfoList = tariffAddInfoBean.getList(criteria);
		if (tariffAddInfoList.size() > 0) {
			return (TariffAddInfo)tariffAddInfoList.get(0);
		}
		return null;
	}

	public String[] obtainTariffCodes(Integer tariffId, String context) throws ManagerBeanException {
		String[] tariffCodes = ArrayUtils.EMPTY_STRING_ARRAY;
		if (tariffId != null) {
			IManagerBean tariffAddInfoBean = BeanManager.getManagerBean(TariffAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_TARIFF_ID), tariffId);
			criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_ATTRIBUTE), context);
			criteria.addEqualExpression(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_DOMAIN), domain);
			Projection prjValue = Projection.property(tariffAddInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_VALUE));
			for (Object obj : tariffAddInfoBean.getList(new ProjectionList(prjValue), criteria)) {
				tariffCodes = (String[])ArrayUtils.add(tariffCodes, obj.toString());
			}
		}
		return tariffCodes;
	}

	public Tariff obtainDefaultTariff() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), UNDEFINED_TARIFF);
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return obtainTariff(((ApplicationParameter)appParamList.get(0)).getValue());
		}
		return null;
	}

	public boolean isTariffPrepaid(List<String> tariffList) throws ManagerBeanException {
		for (String tariffCode : tariffList) {
			if (isTariffPrepaid(tariffCode)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTariffPrepaid(String tariffCode) throws ManagerBeanException {
		Tariff tariff = obtainTariff(tariffCode);
		if (tariff != null) {
			return obtainTariffAddInfo(tariff, PREPAID, YES) != null;
		}
		return false;
	}

	public boolean isTariffNoRefundable(List<String> tariffList) throws ManagerBeanException {
		for (String tariffCode : tariffList) {
			if (isTariffNoRefundable(tariffCode)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTariffNoRefundable(String tariffCode) throws ManagerBeanException {
		Tariff tariff = obtainTariff(tariffCode);
		if (tariff != null) {
			return obtainTariffAddInfo(tariff, NOT_REFUNDABLE, YES) != null;
		}
		return false;
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

	public String obtainUrl(String urlParam) throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), urlParam);
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return ((ApplicationParameter)appParamList.get(0)).getValue();
		}
		return null;
	}

	public Seller obtainRequestSeller() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), RESERVATION_REQUEST_AUTO_SELLER);
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
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
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), YES);
			return rAddInfoBean.getCount(criteria) > 0;
		}
		return false;
	}

	public boolean isAgencySelfBooking(Customer agency) throws ManagerBeanException {
		if (agency != null && agency.getId() != null) {
			String selfBooking = obtainCustomerCode(agency, SELF_BOOKING);
			return (selfBooking != null && selfBooking.equalsIgnoreCase(YES));
		}
		return false;
	}

	public double getAgreedPriceValue(String crsCode) throws ManagerBeanException {
		IManagerBean requestRoomBean = BeanManager.getManagerBean(ReservationRequestRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_CRS_CODE), crsCode);
		criteria.addGreaterThanExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_AGREED_PRICE), Double.valueOf(0));
		Projection prjAgreedPrice = Projection.property(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_AGREED_PRICE));
		List<?> resultList = requestRoomBean.getList(new ProjectionList(prjAgreedPrice), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
			return (Double)resultList.get(0);
		}
		return 0;
	}

	public Item obtainBestPriceDiscountItem() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), BEST_PRICE_DISCOUNT_ITEM);
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return obtainItem(((ApplicationParameter)appParamList.get(0)).getValue());
		}
		return null;
	}

	public double getAutoDiscountValue(Customer agency) throws ManagerBeanException {
		double autoDiscount = 0;
		if (agency != null && agency.getId() != null) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), agency.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), AUTO_DISCOUNT);
			for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
				RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
				autoDiscount = NumberUtils.toDouble(rAddInfo.getValue(), 0);
				if (autoDiscount > 0) {
					break;
				}
			}
		}
		return autoDiscount;
	}

	public Item obtainAutoDiscountItem() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AUTO_DISCOUNT_ITEM);
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain);
		List<ITransferObject> appParamList = appParamBean.getList(criteria);
		if (appParamList.size() > 0) {
			return obtainItem(((ApplicationParameter)appParamList.get(0)).getValue());
		}
		return null;
	}

	public MailAccount obtainCompanyMailAccount() throws ManagerBeanException {
		IManagerBean mailAccountBean = BeanManager.getManagerBean(MailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(mailAccountBean.getFieldName(IEntityAlias.MAIL_ACCOUNT_USER));
		criteria.addEqualExpression(mailAccountBean.getFieldName(IEntityAlias.MAIL_ACCOUNT_DOMAIN), domain);
		criteria.addEqualExpression(mailAccountBean.getFieldName(IEntityAlias.MAIL_ACCOUNT_TYPE), MailAccountType.SYSTEM);
		List<ITransferObject> mailAccountList = mailAccountBean.getList(criteria);
		if (mailAccountList.size() > 0) {
			return (MailAccount)mailAccountList.get(0);
		}
		return null;							
	}

	public String obtainAgencyAdministrativeEmail(Customer agency) throws ManagerBeanException {
		if (agency != null && agency.getId() != null) {
			IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), agency.getId());
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.EMAIL);
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_ADMINISTRATIVE), Boolean.TRUE);
			Projection prjValue = Projection.property(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_VALUE));
			List<?> resultList = rMediaBean.getList(new ProjectionList(prjValue), criteria);
			if (resultList.size() > 0 && resultList.get(0) != null) {
				return (String)resultList.get(0);
			}
		}
		return null;
	}

	public Item obtainAdvanceItem() throws ManagerBeanException {
		return obtainAppParamItem(AppParam.PMS_ADVANCE_ITEM);
	}

	public Item obtainTouristTaxItem() throws ManagerBeanException {
		return obtainAppParamItem(AppParam.PMS_TOURIST_TAX_ITEM);
	}

	public Item obtainEarlyCheckOutItem() throws ManagerBeanException {
		return obtainAppParamItem(AppParam.PMS_EARLY_CHECKOUT_ITEM);
	}

	public Item obtainNoShowItem() throws ManagerBeanException {
		return obtainAppParamItem(AppParam.PMS_NOSHOW_ITEM);
	}

	public Item obtainCancellationItem() throws ManagerBeanException {
		return obtainAppParamItem(AppParam.PMS_CANCELLATION_ITEM);
	}

	public String obtainEarlyCheckOutPenaltyValue(ProjectReservation reservation, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, EARLY_CHECKOUT_PENALTY, date);
	}

	public String obtainEarlyCheckOutPenaltyValue(ProjectReservation reservation, Integer tariffId, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, tariffId, EARLY_CHECKOUT_PENALTY, date);
	}

	public String obtainNoShowPenaltyValue(ProjectReservation reservation, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, NOSHOW_PENALTY, date);
	}

	public String obtainNoShowPenaltyValue(ProjectReservation reservation, Integer tariffId, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, tariffId, NOSHOW_PENALTY, date);
	}

	public String obtainCancellationPenaltyValue(ProjectReservation reservation, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, OUTOFDATE_CANCEL_PENALTY, date);
	}

	public String obtainCancellationPenaltyValue(ProjectReservation reservation, Integer tariffId, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, tariffId, OUTOFDATE_CANCEL_PENALTY, date);
	}

	private String obtainPenaltyValue(ProjectReservation reservation, String key, Date date) throws ManagerBeanException {
		return obtainPenaltyValue(reservation, reservation.getMainTariffId(), key, date);
	}

	private String obtainPenaltyValue(ProjectReservation reservation, Integer tariffId, String key, Date date) throws ManagerBeanException {
		String penaltyStr = obtainPenaltyPattern(reservation, tariffId, key);
		if (StringUtils.contains(penaltyStr, "#")) {
			String penaltyTmp = penaltyStr.substring(0, penaltyStr.indexOf("#"));
			String dueHours = penaltyStr.substring(penaltyStr.indexOf("#") + 1);
			penaltyStr = "0";
			if (NumberUtils.isNumber(dueHours)) {
				Date dueDate = DateUtils.addHours(reservation.getStartTime(), 0-Integer.parseInt(dueHours));
				if (dueDate.before(date)) {
					penaltyStr = penaltyTmp;
				}
			}
		}
		return penaltyStr;
	}

	public Date obtainCancellationPenaltyDate(ProjectReservation reservation) throws ManagerBeanException {
		return obtainPenaltyDate(reservation, OUTOFDATE_CANCEL_PENALTY);
	}

	public Date obtainCancellationPenaltyDate(ProjectReservation reservation, Integer tariffId) throws ManagerBeanException {
		return obtainPenaltyDate(reservation, tariffId, OUTOFDATE_CANCEL_PENALTY);
	}

	private Date obtainPenaltyDate(ProjectReservation reservation, String key) throws ManagerBeanException {
		return obtainPenaltyDate(reservation, reservation.getMainTariffId(), key);
	}

	private Date obtainPenaltyDate(ProjectReservation reservation, Integer tariffId, String key) throws ManagerBeanException {
		String penaltyStr = obtainPenaltyPattern(reservation, tariffId, key);
		if (StringUtils.contains(penaltyStr, "#")) {
			String dueHours = penaltyStr.substring(penaltyStr.indexOf("#") + 1);
			if (NumberUtils.isNumber(dueHours)) {
				return DateUtils.addHours(reservation.getStartTime(), 0-Integer.parseInt(dueHours));
			}
		}
		return new Date();
	}

	private String obtainPenaltyPattern(ProjectReservation reservation, Integer tariffId, String key) throws ManagerBeanException {
		String penaltyPattern = null;
		if (StringUtils.isNotEmpty(reservation.getHotel().getCode())) {
			for (String profileTmp : obtainTariffCodes(tariffId, key + "_" + reservation.getHotel().getCode())) {
				if (profileTmp.contains("|")) {
					profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
					if (profileTmp != null) {
						penaltyPattern = profileTmp;
						break;
					}
				} else if (penaltyPattern == null) {
					penaltyPattern = profileTmp;
				}
			}
		}
		if (penaltyPattern == null) {
			for (String profileTmp : obtainTariffCodes(tariffId, key)) {
				if (profileTmp.contains("|")) {
					profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
					if (profileTmp != null) {
						penaltyPattern = profileTmp;
						break;
					}
				} else if (penaltyPattern == null) {
					penaltyPattern = profileTmp;
				}
			}
		}
		if (penaltyPattern == null) {
			if (StringUtils.isNotEmpty(reservation.getHotel().getCode())) {
				for (String profileTmp : obtainCustomerCodes(reservation.getAgency(), key + "_" + reservation.getHotel().getCode())) {
					if (profileTmp.contains("|")) {
						profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
						if (profileTmp != null) {
							penaltyPattern = profileTmp;
							break;
						}
					} else if (penaltyPattern == null) {
						penaltyPattern = profileTmp;
					}
				}
			}
		}
		if (penaltyPattern == null) {
			for (String profileTmp : obtainCustomerCodes(reservation.getAgency(), key)) {
				if (profileTmp.contains("|")) {
					profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
					if (profileTmp != null) {
						penaltyPattern = profileTmp;
						break;
					}
				} else if (penaltyPattern == null) {
					penaltyPattern = profileTmp;
				}
			}
		}
		Company company = getCompany();
		if (penaltyPattern == null) {
			if (StringUtils.isNotEmpty(reservation.getHotel().getCode())) {
				for (String profileTmp : obtainCompanyCodes(company, key + "_" + reservation.getHotel().getCode())) {
					if (profileTmp.contains("|")) {
						profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
						if (profileTmp != null) {
							penaltyPattern = profileTmp;
							break;
						}
					} else if (penaltyPattern == null) {
						penaltyPattern = profileTmp;
					}
				}
			}
		}
		if (penaltyPattern == null) {
			for (String profileTmp : obtainCompanyCodes(company, key)) {
				if (profileTmp.contains("|")) {
					profileTmp = obtainProfileData(profileTmp, reservation.getStartDate());
					if (profileTmp != null) {
						penaltyPattern = profileTmp;
						break;
					}
				} else if (penaltyPattern == null) {
					penaltyPattern = profileTmp;
				}
			}
		}
		return penaltyPattern;
	}

	public Double obtainCancellationPenaltyAmount(ProjectReservation reservation) throws ManagerBeanException {
		String penaltyValue = reservation.getPenaltyValue();
		if (penaltyValue == null) {
			penaltyValue = obtainCancellationPenaltyValue(reservation, reservation.getStartDate());
		}
		return reservation.getAutoCancellationPenaltyPrice(penaltyValue);
	}

	public double obtainNoShowPenaltyAmount(ProjectReservation reservation) throws ManagerBeanException {
		String penaltyValue = reservation.getPenaltyValue();
		if (penaltyValue == null) {
			penaltyValue = obtainNoShowPenaltyValue(reservation, reservation.getStartDate());
		}
		return reservation.getAutoNoShowPenaltyPrice(penaltyValue);
	}

	public String obtainProfileData(String profile, Date date) {
		String[] patterns = {"ddMMyyyy", "dd/MM/yyyy"};
		try {
			String period = profile.substring(profile.indexOf("|") + 1);
			Date startPeriod = DateUtils.parseDateStrictly(period.substring(0, period.indexOf("-")), patterns);
			Date endPeriod = DateUtils.parseDateStrictly(period.substring(period.indexOf("-") + 1), patterns);
			if (!date.before(startPeriod) && !date.after(endPeriod)) {
				return profile.substring(0, profile.indexOf("|"));
			}
		} catch (Exception ex) {
		}
		return null;
	}

	public void encryptReservationCreditCardData(ProjectReservation reservation) {
		String key = getCryptoKey(reservation);
		reservation.setCreditCardNumber(CryptoUtil.encrypt(key, reservation.getHrCreditCardNumber()));
		reservation.setCreditCardExpirationMonth(CryptoUtil.encrypt(key, reservation.getHrCreditCardExpirationMonth()));
		reservation.setCreditCardExpirationYear(CryptoUtil.encrypt(key, reservation.getHrCreditCardExpirationYear()));
	}

	public void decryptReservationCreditCardData(ProjectReservation reservation) {
		String key = getCryptoKey(reservation);
		reservation.setHrCreditCardNumber(CryptoUtil.decrypt(key, reservation.getCreditCardNumber()));
		reservation.setHrCreditCardExpirationMonth(CryptoUtil.decrypt(key, reservation.getCreditCardExpirationMonth()));
		reservation.setHrCreditCardExpirationYear(CryptoUtil.decrypt(key, reservation.getCreditCardExpirationYear()));
	}

	private String getCryptoKey(ProjectReservation reservation) {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(reservation.getCreationDate());
	}

	public boolean isConexFlowAvailable() {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.PMS_CONEXFLOW_SERVER_PARAM, domain);
		return (appParam != null && StringUtils.isNotBlank(appParam.getValue()));
	}

	public PayMethod obtainConexFlowPayMethod() throws ManagerBeanException {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.PMS_CONEXFLOW_PAY_METHOD, domain);
		if (appParam != null && StringUtils.isNotBlank(appParam.getValue())) {
			return (PayMethod)BeanManager.getManagerBean(PayMethod.class).get(Integer.parseInt(appParam.getValue()));
		}
		return null;
	}

	public Double obtainDailyCashLimit() {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.PMS_DAILY_CASH_LIMIT, domain);
		if (appParam != null && NumberUtils.isNumber(appParam.getValue())) {
			return NumberUtils.toDouble(appParam.getValue());
		}
		return null;
	}

}
