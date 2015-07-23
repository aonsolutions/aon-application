package com.esferalia.aon.pms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.IProject;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationDB;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.MealPlan;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;

@Entity
@Table(name="project_reservation")
@PrimaryKeyJoinColumn(name="project")
public class ProjectReservation extends ProjectReservationDB implements ICalculableContainer, IProject, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectReservation.class.getName());
	private boolean forceCalculateTotals;
	private boolean forceRefreshBooking;
	private double vatPercent;
	private double realDiscountPercent;
	private Double advancedAmount;
	private String hrCreditCardHolder;
	private String hrCreditCardNumber;
	private String hrCreditCardExpirationMonth;
	private String hrCreditCardExpirationYear;
	private String hrCreditCardCvv;
	private Set<ProjectReservationGuest> guests = new HashSet<ProjectReservationGuest>();
	private Set<ProjectReservationRoom> rooms = new HashSet<ProjectReservationRoom>();
	private Set<Invoice> invoices = new HashSet<Invoice>();

	public ProjectReservation() {
		setCheckStatus(ReservationCheckStatus.NO_CHECK);
		setStatus(ReservationStatus.ACTIVE);
		setForceCalculateTotals(false);
		setForceRefreshBooking(false);
	}

	@Transient
	public boolean isForceCalculateTotals() {
		return forceCalculateTotals;
	}
	public void setForceCalculateTotals(boolean forceCalculateTotals) {
		this.forceCalculateTotals = forceCalculateTotals;
	}

	@Transient
	public boolean isForceRefreshBooking() {
		return forceRefreshBooking;
	}
	public void setForceRefreshBooking(boolean forceRefreshBooking) {
		this.forceRefreshBooking = forceRefreshBooking;
	}

	@Transient
	public double getVatPercent() {
		return vatPercent;
	}
	public void setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
	}

	@Transient
	public double getRealDiscountPercent() {
		return realDiscountPercent;
	}
	public void setRealDiscountPercent(double realDiscountPercent) {
		this.realDiscountPercent = CommonUtil.round(realDiscountPercent, 6);
	}

	@Transient
	public Double getAdvancedAmount() {
		if (advancedAmount == null) {
			ReservationUtils reservationUtils = new ReservationUtils();
			try {
				advancedAmount = reservationUtils.getReservationAdvancedAmount(getId());
			} catch (ManagerBeanException ex) {
				LOGGER.error("Error obtaining advanced amount", ex);
			}
		}
		return advancedAmount;
	}
	public void setAdvancedAmount(Double advancedAmount) {
		this.advancedAmount = advancedAmount;
	}

	@Transient
	public String getHrCreditCardHolder() {
		return hrCreditCardHolder;
	}
	public void setHrCreditCardHolder(String hrCreditCardHolder) {
		this.hrCreditCardHolder = hrCreditCardHolder;
	}

	@Transient
	public String getHrCreditCardNumber() {
		return hrCreditCardNumber;
	}
	public void setHrCreditCardNumber(String hrCreditCardNumber) {
		this.hrCreditCardNumber = hrCreditCardNumber;
	}

	@Transient
	public String getHrCreditCardExpirationMonth() {
		return hrCreditCardExpirationMonth;
	}
	public void setHrCreditCardExpirationMonth(String hrCreditCardExpirationMonth) {
		this.hrCreditCardExpirationMonth = hrCreditCardExpirationMonth;
	}

	@Transient
	public String getHrCreditCardExpirationYear() {
		return hrCreditCardExpirationYear;
	}
	public void setHrCreditCardExpirationYear(String hrCreditCardExpirationYear) {
		this.hrCreditCardExpirationYear = hrCreditCardExpirationYear;
	}

	@Transient
	public String getHrCreditCardCvv() {
		return hrCreditCardCvv;
	}
	public void setHrCreditCardCvv(String hrCreditCardCvv) {
		this.hrCreditCardCvv = hrCreditCardCvv;
	}

	@OneToMany(mappedBy = "projectReservation", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<ProjectReservationGuest> getGuests() {
		return this.guests;
	}
	public void setGuests(Set<ProjectReservationGuest> guests) {
		this.guests = guests;
	}

	@OneToMany(mappedBy = "projectReservation", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<ProjectReservationRoom> getRooms() {
		return this.rooms;
	}
	public void setRooms(Set<ProjectReservationRoom> rooms) {
		this.rooms = rooms;
	}

	@OneToMany(mappedBy = "project")
	@OrderBy()
	public Set<Invoice> getInvoices() {
		return this.invoices;
	}
	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	@Transient
	public int getNights() {
		if (getStartDate() != null && getEndDate() != null && getStartDate().compareTo(getEndDate()) < 0) {
			return (int)CommonUtil.getDaysBetweenDates(getStartDate(), getEndDate());
		}
		return 0;
	}

	@Transient
	public boolean isGuestHolder() {
		return getBookingHolder() == BookingHolder.GUEST;
	}
	@Transient
	public boolean isAgencyHolder() {
		return getBookingHolder() == BookingHolder.AGENCY;
	}
	@Transient
	public boolean isCompanyHolder() {
		return getBookingHolder() == BookingHolder.COMPANY;
	}

	@Transient
	public boolean isNoCheck() {
		return getCheckStatus() == ReservationCheckStatus.NO_CHECK;
	}
	@Transient
	public boolean isCheckIn() {
		return getCheckStatus() == ReservationCheckStatus.CHECK_IN;
	}
	@Transient
	public boolean isCheckOut() {
		return getCheckStatus() == ReservationCheckStatus.CHECK_OUT;
	}
	@Transient
	public boolean isNoShow() {
		return getCheckStatus() == ReservationCheckStatus.NO_SHOW || getCheckStatus() == ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE;
	}

	@Transient
	public boolean isActive() {
		return getStatus() == ReservationStatus.ACTIVE;
	}
	@Transient
	public boolean isBlocked() {
		return getStatus() == ReservationStatus.BLOCKED;
	}
	@Transient
	public boolean isCancelled() {
		return getStatus() == ReservationStatus.CANCELLED;
	}
	@Transient
	public boolean isInvoiced() {
		return getStatus() == ReservationStatus.INVOICED;
	}

	@Transient
	public boolean isSourceManual() {
		return getSource() == ReservationSource.MANUAL;
	}
	@Transient
	public boolean isSourceCrs() {
		return getSource() == ReservationSource.CRS;
	}
	@Transient
	public boolean isSourceRequest() {
		return getSource() == ReservationSource.REQUEST;
	}

	@Transient
	public Customer getCustomer() {
    	if (getBookingHolder() == BookingHolder.AGENCY && getAgency() != null && getAgency().getId() != null) {
    		return getAgency();
    	} else if (getBookingHolder() == BookingHolder.COMPANY && getCompany() != null && getCompany().getId() != null) {
    		return getCompany();
    	}
    	return getHotelReservation().getCustomer();
	}

	@Transient
	public boolean isDiverted() {
		return !getHotel().equals(getHotelReservation());
	}

	@Transient
	public String getGuestFullName() throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
		Projection prjName = Projection.property(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_NAME));
		Projection prjSurname = Projection.property(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_SURNAME));
		Projection prjSurname2 = Projection.property(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_SURNAME2));
		ProjectionList projectionList = new ProjectionList(prjName, prjSurname, prjSurname2);
		List<?> resultList = reservationGuestBean.getList(projectionList, criteria);
		if (resultList.size() > 0) {
			Object[] result = (Object[])resultList.get(0);
	    	String guestName = (result[0] == null) ? "" : result[0].toString() + " ";
	    	guestName += (result[1] == null) ? "" : result[1].toString() + " ";
	    	guestName += (result[2] == null) ? "" : result[2].toString();
			return guestName;
		}
		return null;
	}

	@Transient
	public Integer getMainTariffId() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		Projection prjTariff = Projection.property(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_TARIFF_ID));
		List<?> resultList = reservationRoomBean.getList(new ProjectionList(prjTariff), criteria);
		if (resultList.size() > 0) {
			return (Integer)resultList.get(0);
		}
		return null;
	}

	@Transient
	public String getTariffInfo() throws ManagerBeanException {
		Map<Integer, String> tariffInfoMap = new HashMap<Integer, String>();
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		List<ITransferObject> reservationRoomList = reservationRoomBean.getList(criteria);
		for (int i=0; i<reservationRoomList.size(); i++) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)reservationRoomList.get(i);
			Tariff tariff = reservationRoom.getTariff();
			String room = reservationRoom.getRoomNumber();
			room = (room==null) ? "R"+(i+1) : room;
			if (!tariffInfoMap.containsKey(tariff.getId())) {
				tariffInfoMap.put(tariff.getId(), tariff.getName() + " (" + room);
			} else {
				tariffInfoMap.put(tariff.getId(), tariffInfoMap.get(tariff.getId()) + ", " + room);
			}
		}

		String tariffInfo = "";
		for (String info : tariffInfoMap.values()) {
			if (!tariffInfo.equals("")) {
				tariffInfo += " - ";
			}
			tariffInfo += info + ")";
		}
		return tariffInfo;
	}

	@Transient
	public int getGuestCount() throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), getId());
		return reservationGuestBean.getCount(criteria);
	}

	@Transient
	public int getPersonCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		Projection prjAdults = Projection.sum(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ADULTS));
		Projection prjChildren = Projection.sum(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_CHILDREN));
		ProjectionList projectionList = new ProjectionList(prjAdults, prjChildren);
		List<?> resultList = reservationRoomBean.getList(projectionList, criteria);
		if (resultList.size() > 0) {
			Object[] result = (Object[])resultList.get(0);
	    	Integer adults = (result[0] == null) ? 0 : (Integer)result[0];
	    	Integer children = (result[1] == null) ? 0 : (Integer)result[1];
	    	return adults + children;
		}
		return 0;
	}

	@Transient
	public int getAdultCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		Projection prjAdults = Projection.sum(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ADULTS));
		List<?> resultList = reservationRoomBean.getList(new ProjectionList(prjAdults), criteria);
		if (resultList.size() > 0) {
			return (Integer)resultList.get(0);
		}
		return 0;
	}

	@Transient
	public int getChildCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		Projection prjChild = Projection.sum(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_CHILDREN));
		List<?> resultList = reservationRoomBean.getList(new ProjectionList(prjChild), criteria);
		if (resultList.size() > 0) {
			return (Integer)resultList.get(0);
		}
		return 0;
	}

	@Transient
	public int getRoomCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		return reservationRoomBean.getCount(criteria);
	}
	
	@Transient
	public int getRoomAssignedCount() throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID;
		criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(alias), getId());
		return reservationRoomDetailBean.getCount(criteria);
	}

	@Transient
	public MealPlan getMealPlan() throws ManagerBeanException {
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), getId());
		criteria.addNotNullExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_MEAL_PLAN));
		criteria.addOrder(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_COMPOSITION), false);
		Projection prjMealPlan = Projection.property(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_MEAL_PLAN));
		List<?> resultList = reservationServiceBean.getList(new ProjectionList(prjMealPlan), criteria);
		if (resultList.size() > 0) {
			return (MealPlan)resultList.get(0);
		}
		return null;
	}

	@Transient
	public Date getEarlyCheckOutDate() throws ManagerBeanException {
		if (isEarlyCheckOut()) {
			if (getEndDate().after(getEndTime())) {
				return DateUtils.truncate(getEndTime(), Calendar.DATE);
			} else {
				IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
				Criteria criteria = new Criteria();
				String alias = IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID;
				criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(alias), getId());
				alias = IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE;
				criteria.addOrder(reservationRoomDetailBean.getFieldName(alias), false);
				Projection prjCheckOutDate = Projection.property(reservationRoomDetailBean.getFieldName(alias));
				List<?> resultList = reservationRoomDetailBean.getList(new ProjectionList(prjCheckOutDate), criteria);
				if (resultList.size() > 0) {
					return DateUtils.addDays((Date)resultList.get(0), 1);
				} else {
					return getStartDate();
				}
			}
		}
		return null;
	}

	@Transient
	public int getServiceCount() throws ManagerBeanException {
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), getId());
		return reservationServiceBean.getCount(criteria);
	}

	@Transient
	public int getServiceAssignedCount() throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), getId());
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), false);
		return reservationServiceDetailBean.getCount(criteria);
	}

	@Transient
	public boolean isInUse() throws ManagerBeanException {
		return (getRoomAssignedCount() > 0 || getServiceAssignedCount() > 0);
	}

	@Transient
	public Map<Tax, Double> getReservationTaxableBasesPerTax(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		return reservationUtils.getReservationServicesTaxableBasesPerTax(getId(), fromDate, toDate);
	}

	@Transient
	public Map<Tax, Double> getFirstNightTaxableBases() throws ManagerBeanException {
		return getReservationTaxableBasesPerTax(getStartDate(), getStartDate());
	}

	@Transient
	public Map<Tax, Double> getFirstTwoNightTaxableBases() throws ManagerBeanException {
		return getReservationTaxableBasesPerTax(getStartDate(), DateUtils.addDays(getStartDate(), 1));
	}

	@Transient
	public double getFirstNightPenaltyTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemNoShow() != null && getHotelReservation().getItemNoShow().getId() != null) {
			Double taxableBase = getFirstNightTaxableBases().get(getHotelReservation().getItemNoShow().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	public double getFirstTwoNightPenaltyTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemNoShow() != null && getHotelReservation().getItemNoShow().getId() != null) {
			Double taxableBase = getFirstTwoNightTaxableBases().get(getHotelReservation().getItemNoShow().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	public double getFirstNightPenaltyPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemNoShow().getProduct().getVat(), getStartDate());
		return CommonUtil.round(CommonUtil.round(getFirstNightPenaltyTaxableBase()) * (1 + vatPercent / 100));
	}

	@Transient
	public double getFirstTwoNightPenaltyPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemNoShow().getProduct().getVat(), getStartDate());
		return CommonUtil.round(CommonUtil.round(getFirstTwoNightPenaltyTaxableBase()) * (1 + vatPercent / 100));
	}

	@Transient
	public double getReservationPenaltyTaxableBase(Date fromDate, Date toDate) throws ManagerBeanException {
		if (getHotelReservation().getItemPenalty() != null && getHotelReservation().getItemPenalty().getId() != null) {
			Double taxableBase = getReservationTaxableBasesPerTax(fromDate, toDate).get(getHotelReservation().getItemPenalty().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	public double getReservationPenaltyPrice(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemPenalty().getProduct().getVat(), getStartDate());
		return CommonUtil.round(CommonUtil.round(getReservationPenaltyTaxableBase(fromDate, toDate)) * (1 + vatPercent / 100));
	}

	@Transient
	public double getPendingAmount() throws ManagerBeanException {
		return CommonUtil.round(getTotal() - getAdvancedAmount());
	}

	@Transient
	public Registry getRegistry() {
		return getProject().getRegistry();
	}

	@Transient
	public Date getDate() {
		return getStartDate();
	}

	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Transient
	public List<?> getDetailList() {
		try {
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
			criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), getId());
			alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA;
			criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), false);
			return reservationServiceDetailBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			LOGGER.error("Error obtaining services list", ex);
		}
		return null;
	}

}