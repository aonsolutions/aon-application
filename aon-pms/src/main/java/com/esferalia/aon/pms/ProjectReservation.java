package com.esferalia.aon.pms;

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
import com.code.aon.finance.Invoice;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.IProject;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationDB;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
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
	public boolean isDiverted() {
		return !getHotel().equals(getHotelReservation());
	}

	@Transient
	public String getGuestFullName() throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
		for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
			ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
			return reservationGuest.getFullName();
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
		int i = 1;
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			Tariff tariff = ((ProjectReservationRoom)ito).getTariff();
			String rooms = ((ProjectReservationRoom)ito).getRoomNumber();
			rooms = (rooms==null?"R"+i:rooms);
			if (!tariffInfoMap.containsKey(tariff.getId())) {
				tariffInfoMap.put(tariff.getId(), tariff.getName() + " (" + rooms);
			} else {
				tariffInfoMap.put(tariff.getId(), tariffInfoMap.get(tariff.getId()) + ", " + rooms);
			}
			i++;
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
		int count = 0;
		for (ITransferObject to : reservationRoomBean.getList(criteria)) {
			count += ((ProjectReservationRoom)to).getAdults();
			count += ((ProjectReservationRoom)to).getChildren();
		}
		return count;
	}

	@Transient
	public int getAdultCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		int count = 0;
		for (ITransferObject to : reservationRoomBean.getList(criteria)) {
			count += ((ProjectReservationRoom)to).getAdults();
		}
		return count;
	}

	@Transient
	public int getChildCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		int count = 0;
		for (ITransferObject to : reservationRoomBean.getList(criteria)) {
			count += ((ProjectReservationRoom)to).getChildren();
		}
		return count;
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
	public Map<Tax, Double> getOneNightTaxableBases() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		return reservationUtils.getReservationServicesTaxableBases(getId(), getStartDate(), getStartDate());
	}

	@Transient
	public Map<Tax, Double> getTwoNightTaxableBases() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		return reservationUtils.getReservationServicesTaxableBases(getId(), getStartDate(), DateUtils.addDays(getStartDate(), 1));
	}

	/*Las bases imponibles para facturacion de No Shows las redondeamos a 2 decimales ya que son conceptos que van ellos solos en la factura y posteriormente
	se redondea la base imponible de la factura a 2 decimales, con lo que no tiene sentido redondear a 4 y posibilitar el dar lugar a pequeñas diferencias
	en el calculo de los impuestos. Las bases imponibles para penalizaciones si van a 4 porque estas van acompañadas en la factura de los servicios que 
	correspondan y de esta manera se afina mas el calculo de impuestos.	*/
	@Transient
	public double getOneNightNoShowTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemNoShow() != null && getHotelReservation().getItemNoShow().getId() != null) {
			Double taxableBase = getOneNightTaxableBases().get(getHotelReservation().getItemNoShow().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue()) : 0;
		}
		return 0;
	}

	@Transient
	public double getTwoNightNoShowTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemNoShow() != null && getHotelReservation().getItemNoShow().getId() != null) {
			Double taxableBase = getTwoNightTaxableBases().get(getHotelReservation().getItemNoShow().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue()) : 0;
		}
		return 0;
	}

	@Transient
	public double getOneNightNoShowPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemNoShow().getProduct().getVat(), new Date());
		return CommonUtil.round(getOneNightNoShowTaxableBase() * (1 + vatPercent / 100));
	}

	@Transient
	public double getTwoNightNoShowPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemNoShow().getProduct().getVat(), new Date());
		return CommonUtil.round(getTwoNightNoShowTaxableBase() * (1 + vatPercent / 100));
	}

	@Transient
	public double getOneNightPenaltyTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemPenalty() != null && getHotelReservation().getItemPenalty().getId() != null) {
			Double taxableBase = getOneNightTaxableBases().get(getHotelReservation().getItemPenalty().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	public double getTwoNightPenaltyTaxableBase() throws ManagerBeanException {
		if (getHotelReservation().getItemPenalty() != null && getHotelReservation().getItemPenalty().getId() != null) {
			Double taxableBase = getTwoNightTaxableBases().get(getHotelReservation().getItemPenalty().getProduct().getVat());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	public double getOneNightPenaltyPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemPenalty().getProduct().getVat(), getStartDate());
		return CommonUtil.round(getOneNightPenaltyTaxableBase() * (1 + vatPercent / 100));
	}

	@Transient
	public double getTwoNightPenaltyPrice() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getHotelReservation().getItemPenalty().getProduct().getVat(), getStartDate());
		return CommonUtil.round(getTwoNightPenaltyTaxableBase() * (1 + vatPercent / 100));
	}

	@Transient
	public double getAdvancedAmount() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		return reservationUtils.getReservationAdvancedAmount(getId());
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
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining services list", e);
		}
		return null;
	}

}