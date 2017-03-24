package com.esferalia.aon.pms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.IProject;
import com.code.aon.project.ProjectAttachment;
import com.code.aon.project.enumeration.ProjectAttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationDB;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.MealPlan;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.pms.reservation.ReservationUtils;

@Entity
@Table(name="project_reservation")
@PrimaryKeyJoinColumn(name="project")
public class ProjectReservation extends ProjectReservationDB implements ICalculableContainer, IProject, IAuditable, IReservationConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectReservation.class.getName());
	private boolean forceCalculateTotals;
	private boolean forceRefreshBooking;
	private boolean forceInventoryControl;
	private boolean refreshRooms;
	private boolean skipDirtyControl;
	private boolean notRefundable;
	private double vatPercent;
	private double realDiscountPercent;
	private Double advancedAmount;
	private Integer touristTaxPayed;
	private ProjectAttachment lastConexFlowOperation;
	private boolean newCreditCard;
	private String hrCreditCardNumber;
	private String hrCreditCardExpirationMonth;
	private String hrCreditCardExpirationYear;
	private Set<ProjectReservationGuest> guests = new HashSet<ProjectReservationGuest>();
	private Set<ProjectReservationRoom> rooms = new HashSet<ProjectReservationRoom>();
	private Set<ProjectAttachment> attachments = new HashSet<ProjectAttachment>();
	private Set<Invoice> invoices = new HashSet<Invoice>();

	public ProjectReservation() {
		setCheckStatus(ReservationCheckStatus.NO_CHECK);
		setStatus(ReservationStatus.ACTIVE);
		setForceCalculateTotals(false);
		setForceRefreshBooking(false);
		setForceInventoryControl(false);
		setSkipDirtyControl(false);
		setNotRefundable(false);
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
	public boolean isForceInventoryControl() {
		return forceInventoryControl;
	}
	public void setForceInventoryControl(boolean forceInventoryControl) {
		this.forceInventoryControl = forceInventoryControl;
	}

	@Transient
	public boolean isRefreshRooms() {
		return refreshRooms;
	}
	public void setRefreshRooms(boolean refreshRooms) {
		this.refreshRooms = refreshRooms;
	}

	@Transient
	public boolean isSkipDirtyControl() {
		return skipDirtyControl;
	}
	public void setSkipDirtyControl(boolean skipDirtyControl) {
		this.skipDirtyControl = skipDirtyControl;
	}

	@Transient
	public boolean isNotRefundable() {
		return notRefundable;
	}
	public void setNotRefundable(boolean notRefundable) {
		this.notRefundable = notRefundable;
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
			ReservationUtils reservationUtils = new ReservationUtils(getDomain());
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
	public boolean isTouristTax() throws ManagerBeanException {
		return getHotel().isTouristTax() && getTouristTaxFree() == null;
	}

	@Transient
	public Integer getTouristTaxPayed() {
		if (touristTaxPayed == null) {
			ReservationUtils reservationUtils = new ReservationUtils(getDomain());
			try {
				touristTaxPayed = reservationUtils.getReservationTouristTaxPayed(getId());
			} catch (ManagerBeanException ex) {
				LOGGER.error("Error obtaining advanced amount", ex);
			}
		}
		return touristTaxPayed;
	}
	public void setTouristTaxPayed(Integer touristTaxPayed) {
		this.touristTaxPayed = touristTaxPayed;
	}

	@Transient
	public int getTouristTaxPending() throws ManagerBeanException {
		if (isTouristTax()) {
			return getAdultCount() - (getTouristTaxPayed() / getNights());
		}
		return 0;
	}

	@Transient
	public double getTouristTaxAmount() throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		try {
			return reservationUtils.getReservationTouristTaxAmount(this, null, false);
		} catch (ManagerBeanException ex) {
			LOGGER.error("Error obtaining advanced amount", ex);
		}
		return 0;
	}

	@Transient
	public ProjectAttachment getLastConexFlowOperation() {
		if (lastConexFlowOperation == null) {
			try {
				IManagerBean pAttachBean = BeanManager.getManagerBean(ProjectAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID), getId());
				criteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ATTACH_TYPE), ProjectAttachmentType.CONEXFLOW);
				criteria.addOrder(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ATTACH_DATE), false);
				criteria.addOrder(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ID), false);
				for (ITransferObject ito : pAttachBean.getList(criteria)) {
					lastConexFlowOperation = (ProjectAttachment)ito;
					break;
				}
			} catch(ManagerBeanException ex) {
				LOGGER.error("Error obtaining conexFlow operation", ex);
			}
		}
		return lastConexFlowOperation;
	}
	public void setLastConexFlowOperation(ProjectAttachment lastConexFlowOperation) {
		this.lastConexFlowOperation = lastConexFlowOperation;
	}

	@Transient
	public boolean isConexFlowCreateToken() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_CREATE_TOKEN_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowRemoveToken() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_REMOVE_TOKEN_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowPreauthorizedCheck() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_PREAUTH_CHECK_OK_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowPreauthorizedOk() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		Date dueDate = DateUtils.addDays(new Date(), -7);
		return lastCfOperation.matches(CONEXFLOW_PREAUTH_OK_PATTERN.replace("%", "(.*)")) && getLastConexFlowOperation().getAttachDate().after(dueDate);
	}

	@Transient
	public boolean isConexFlowPreauthorizedExpired() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		Date dueDate = DateUtils.addDays(new Date(), -7);
		return lastCfOperation.matches(CONEXFLOW_PREAUTH_OK_PATTERN.replace("%", "(.*)")) && getLastConexFlowOperation().getAttachDate().before(dueDate);
	}

	@Transient
	public boolean isConexFlowFail() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_FAIL_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowSaleOk() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_CONFIRM_OK_PATTERN.replace("%", "(.*)")) || 
				lastCfOperation.matches(CONEXFLOW_SALE_OK_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowRefundOk() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		return lastCfOperation.matches(CONEXFLOW_REFUND_OK_PATTERN.replace("%", "(.*)"));
	}

	@Transient
	public boolean isConexFlowTransactionOk() {
		return isConexFlowSaleOk() || isConexFlowRefundOk();
	}

	@Transient
	public double getConexFlowOperationAmount() {
		String lastCfOperation = (getLastConexFlowOperation() != null) ? getLastConexFlowOperation().getDescription() : "";
		if (StringUtils.indexOf(lastCfOperation, "_", StringUtils.indexOf(lastCfOperation, "#")) < 0) {
			return NumberUtils.toDouble(StringUtils.substringAfterLast(lastCfOperation, "#"));
		} else {
			return NumberUtils.toDouble(StringUtils.substringBetween(lastCfOperation, "#", "_"));
		}
	}
	
	@Transient
	public boolean isConexFlowPayslip() {
		try {
			IManagerBean pAttachBean = BeanManager.getManagerBean(ProjectAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID), getId());
			criteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ATTACH_TYPE), ProjectAttachmentType.PAYSLIP);
			Expression exp = ExpressionUtilities.getLikeExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_DESCRIPTION), 
					"CONEXFLOW%PAYSLIP#%");
			criteria.addExpression(exp);
			return pAttachBean.getList(criteria).size() > 0;
		} catch(ManagerBeanException ex) {
			LOGGER.error("Error obtaining conexFlow payslip", ex);
		}
		return false;
	}

	@Transient
	public boolean isNewCreditCard() {
		return newCreditCard;
	}
	public void setNewCreditCard(boolean newCreditCard) {
		this.newCreditCard = newCreditCard;
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

	@OneToMany(mappedBy = "project", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<ProjectAttachment> getAttachments() {
		return this.attachments;
	}
	public void setAttachments(Set<ProjectAttachment> attachments) {
		this.attachments = attachments;
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
	public boolean isCheckCancelled() {
		return getCheckStatus() == ReservationCheckStatus.CANCEL_INVOICEABLE || getCheckStatus() == ReservationCheckStatus.CANCEL_NO_INVOICEABLE;
	}
	@Transient
	public boolean isNoInvoiceable() {
		return getCheckStatus() == ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE || getCheckStatus() == ReservationCheckStatus.CANCEL_NO_INVOICEABLE;
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
		return getStatus() == ReservationStatus.CANCELLED || getCancellationDate() != null || isCheckCancelled();
	}
	@Transient
	public boolean isInvoiced() {
		return getStatus() == ReservationStatus.INVOICED;
	}

	@Transient
	public ReservationStatus getSavedStatus() {
		if (getId() != null) {
			try {
				IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), getId());
				Projection prjStatus = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS));
				List<?> resultList = reservationBean.getList(new ProjectionList(prjStatus), criteria);
				if (resultList.size() > 0 && resultList.get(0) != null) {
					return (ReservationStatus)resultList.get(0);
				}
			} catch (ManagerBeanException ex) {
				LOGGER.error("Error obtaining saved status", ex);
			}
		}
		return null;
	}

	@Transient
	public ReservationCheckStatus getSavedCheckStatus() {
		if (getId() != null) {
			try {
				IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), getId());
				Projection prjStatus = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CHECK_STATUS));
				List<?> resultList = reservationBean.getList(new ProjectionList(prjStatus), criteria);
				if (resultList.size() > 0 && resultList.get(0) != null) {
					return (ReservationCheckStatus)resultList.get(0);
				}
			} catch (ManagerBeanException ex) {
				LOGGER.error("Error obtaining saved status", ex);
			}
		}
		return null;
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
		List<?> resultList = reservationGuestBean.getList(new ProjectionList(prjName, prjSurname, prjSurname2), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
			Object[] result = (Object[])resultList.get(0);
	    	String guestName = (result[0] == null) ? "" : result[0].toString() + " ";
	    	guestName += (result[1] == null) ? "" : result[1].toString() + " ";
	    	guestName += (result[2] == null) ? "" : result[2].toString();
			return guestName;
		}
		return null;
	}

	@Transient
	public List<ProjectReservationRoom> getReservationRoomList() throws ManagerBeanException {
		List<ProjectReservationRoom> reservationRoomList = new LinkedList<ProjectReservationRoom>();
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			reservationRoomList.add((ProjectReservationRoom)ito);
		}
		return reservationRoomList;
	}

	@Transient
	public String getAllotmentRateCode() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		Projection prjRateCode = Projection.property(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ALLOTMENT_RATE_CODE));
		List<?> resultList = reservationRoomBean.getList(new ProjectionList(prjRateCode), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
			return (String)resultList.get(0);
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
		if (resultList.size() > 0 && resultList.get(0) != null) {
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
		List<?> resultList = reservationRoomBean.getList(new ProjectionList(prjAdults, prjChildren), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
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
		if (resultList.size() > 0 && resultList.get(0) != null) {
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
		if (resultList.size() > 0 && resultList.get(0) != null) {
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
		if (resultList.size() > 0 && resultList.get(0) != null) {
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
				if (resultList.size() > 0 && resultList.get(0) != null) {
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
    public Map<Date, Double> getReservationTaxableBasesPerDay(Date fromDate, Date toDate, Tax vat) throws ManagerBeanException {
		Map<Date, Double> reservationBases = new HashMap<Date, Double>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), getId());
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE;
		criteria.addBetweenExpression(reservationServiceDetailBean.getFieldName(alias), fromDate, toDate);
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_VAT_ID;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), vat.getId());
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), false);
		Projection prjDate = Projection.property(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
		Projection prjBase = Projection.property(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_TAXABLE_BASE));
		for (Object obj : reservationServiceDetailBean.getList(new ProjectionList(prjDate, prjBase), criteria)) {
			Object[] objs = (Object[])obj;
			Date date = (Date)objs[0];
			double base = (Double)objs[1];
			if (reservationBases.containsKey(date)) {
				base += reservationBases.get(date);
			}
			reservationBases.put(date, CommonUtil.round(base, 4));
		}
		return reservationBases;
	}


	@Transient
	public Map<Integer, Double> getReservationTaxableBasesPerTax(Date fromDate, Date toDate) throws ManagerBeanException {
		Map<Integer, Double> reservationBases = new HashMap<Integer, Double>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), getId());
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE;
		criteria.addBetweenExpression(reservationServiceDetailBean.getFieldName(alias), fromDate, toDate);
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA;
		criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), false);
		alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_VAT_ID;
		Projection prjVat = Projection.property(reservationServiceDetailBean.getFieldName(alias));
		Projection prjBase = Projection.property(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_TAXABLE_BASE));
		for (Object obj : reservationServiceDetailBean.getList(new ProjectionList(prjVat, prjBase), criteria)) {
			Object[] objs = (Object[])obj;
			Integer vatId = (Integer)objs[0];
			double base = (Double)objs[1];
			if (reservationBases.containsKey(vatId)) {
				base += reservationBases.get(vatId);
			}
			reservationBases.put(vatId, CommonUtil.round(base, 4));
		}
		return reservationBases;
	}

	@Transient
	public String getPenaltyMode() {
		return getPenaltyMode(getPenaltyValue());
	}

	@Transient
	public String getPenaltyMode(String penaltyValue) {
		String penaltyMode = StringUtils.substring(penaltyValue, -1);
		if (NumberUtils.isDigits(penaltyMode)) {
			penaltyMode = StringUtils.EMPTY;
		}
		return penaltyMode;
	}

	@Transient
	public Integer getPenaltyDays() {
		return getPenaltyDays(getPenaltyValue());
	}

	@Transient
	public Integer getPenaltyDays(String penaltyValue) {
		Integer penaltyDays = null;
		if (penaltyValue != null) {
			String penaltyMode = getPenaltyMode(penaltyValue);
			penaltyDays = Integer.parseInt(StringUtils.substringBeforeLast(penaltyValue, penaltyMode));
			if (penaltyDays != null && !penaltyMode.equals(IReservationConstants.PENALTY_MODE_PERCENT) && (penaltyDays < 0 || penaltyDays > getNights())) {
				penaltyDays = (int)CommonUtil.getDaysBetweenDates(getStartDate(), getEndDate());
			}
		}
		return penaltyDays;
	}

	@Transient
	private double getPenaltyTaxableBase(Item item, Date fromDate, Date toDate) throws ManagerBeanException {
		if (item != null && item.getId() != null) {
			Double taxableBase = getReservationTaxableBasesPerTax(fromDate, toDate).get(item.getProduct().getVat().getId());
			return (taxableBase != null) ? CommonUtil.round(taxableBase.doubleValue(), 4) : 0;
		}
		return 0;
	}

	@Transient
	private double getPenaltyPrice(Item item, Date fromDate, Date toDate) throws ManagerBeanException {
		if (item != null && item.getId() != null) {
			double vatPercent = item.getProduct().getVat().getDatedPercentage(getStartDate());
			return CommonUtil.round(getPenaltyTaxableBase(item, fromDate, toDate) * (1 + vatPercent / 100));
		}
		return 0;
	}

	@Transient
	private double getPenaltyTaxableBase(AppParam appParam, Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyTaxableBase(reservationUtils.obtainAppParamItem(appParam), fromDate, toDate);
	}

	@Transient
	private double getPenaltyPrice(AppParam appParam, Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyPrice(reservationUtils.obtainAppParamItem(appParam), fromDate, toDate);
	}

	@Transient
	public double getEarlyCheckOutPenaltyTaxableBase(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyTaxableBase(reservationUtils.obtainAppParamItem(AppParam.PMS_EARLY_CHECKOUT_ITEM), fromDate, toDate);
	}

	@Transient
	public double getEarlyCheckOutPenaltyPrice(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyPrice(reservationUtils.obtainAppParamItem(AppParam.PMS_EARLY_CHECKOUT_ITEM), fromDate, toDate);
	}

	@Transient
	public double getNoShowPenaltyTaxableBase(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyTaxableBase(reservationUtils.obtainAppParamItem(AppParam.PMS_NOSHOW_ITEM), fromDate, toDate);
	}

	@Transient
	public double getNoShowPenaltyPrice(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyPrice(reservationUtils.obtainAppParamItem(AppParam.PMS_NOSHOW_ITEM), fromDate, toDate);
	}

	@Transient
	public double getFirstNightNoShowPenaltyPrice() throws ManagerBeanException {
		return getNoShowPenaltyPrice(getStartDate(), getStartDate());
	}

	@Transient
	public double getFirstTwoNightNoShowPenaltyPrice() throws ManagerBeanException {
		return getNoShowPenaltyPrice(getStartDate(), DateUtils.addDays(getStartDate(), 1));
	}

	@Transient
	public Double getAutoNoShowPenaltyPrice() throws ManagerBeanException {
		return getAutoNoShowPenaltyPrice(getPenaltyValue());
	}

	@Transient
	public Double getAutoNoShowPenaltyPrice(String penaltyValue) throws ManagerBeanException {
		return getAutoNoShowPenaltyPrice(penaltyValue, false);
	}

	@Transient
	public Double getAutoNoShowPenaltyPrice(String penaltyValue, boolean samePrice) throws ManagerBeanException {
		if (penaltyValue != null) {
			Integer penaltyDays = getPenaltyDays(penaltyValue);
			if (penaltyDays != null && penaltyDays.intValue() != 0) {
				String penaltyMode = getPenaltyMode(penaltyValue);
				if (samePrice || penaltyMode.equals(IReservationConstants.PENALTY_MODE_AVERAGE)) {
					return CommonUtil.round(getTotal() * penaltyDays / getNights());
				} else if (penaltyMode.equals(IReservationConstants.PENALTY_MODE_PERCENT)) {
					return CommonUtil.round(getTotal() * penaltyDays / 100);
				} else if (penaltyMode.equals(IReservationConstants.PENALTY_MODE_DAILY) || penaltyMode.equals(StringUtils.EMPTY)) {
					return getNoShowPenaltyPrice(getStartDate(), DateUtils.addDays(getStartDate(), penaltyDays-1));
				}
			}
			return 0.0;
		}
		return null;
	}

	@Transient
	public double getCancellationPenaltyTaxableBase(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyTaxableBase(reservationUtils.obtainAppParamItem(AppParam.PMS_CANCELLATION_ITEM), fromDate, toDate);
	}

	@Transient
	public double getCancellationPenaltyPrice(Date fromDate, Date toDate) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(getDomain());
		return getPenaltyPrice(reservationUtils.obtainAppParamItem(AppParam.PMS_CANCELLATION_ITEM), fromDate, toDate);
	}

	@Transient
	public double getFirstNightCancellationPenaltyPrice() throws ManagerBeanException {
		return getCancellationPenaltyPrice(getStartDate(), getStartDate());
	}

	@Transient
	public double getFirstTwoNightCancellationPenaltyPrice() throws ManagerBeanException {
		return getCancellationPenaltyPrice(getStartDate(), DateUtils.addDays(getStartDate(), 1));
	}

	@Transient
	public Double getAutoCancellationPenaltyPrice() throws ManagerBeanException {
		return getAutoCancellationPenaltyPrice(getPenaltyValue());
	}

	@Transient
	public Double getAutoCancellationPenaltyPrice(String penaltyValue) throws ManagerBeanException {
		return getAutoCancellationPenaltyPrice(penaltyValue, false);
	}

	@Transient
	public Double getAutoCancellationPenaltyPrice(String penaltyValue, boolean samePrice) throws ManagerBeanException {
		if (penaltyValue != null) {
			Integer penaltyDays = getPenaltyDays(penaltyValue);
			if (penaltyDays != null && penaltyDays.intValue() != 0) {
				String penaltyMode = getPenaltyMode(penaltyValue);
				if (samePrice || penaltyMode.equals(IReservationConstants.PENALTY_MODE_AVERAGE)) {
					return CommonUtil.round(getTotal() * penaltyDays / getNights());
				} else if (penaltyMode.equals(IReservationConstants.PENALTY_MODE_PERCENT)) {
					return CommonUtil.round(getTotal() * penaltyDays / 100);
				} else if (penaltyMode.equals(IReservationConstants.PENALTY_MODE_DAILY) || penaltyMode.equals(StringUtils.EMPTY)) {
					return getCancellationPenaltyPrice(getStartDate(), DateUtils.addDays(getStartDate(), penaltyDays-1));
				}
			}
			return 0.0;
		}
		return null;
	}

	@Transient
	public boolean isBlankToken() {
		return StringUtils.isBlank(getToken());
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

	@Transient
	public boolean isDirty() throws ManagerBeanException {
		if (!isSkipDirtyControl() && getId() != null) {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), getId());
			if (getModificationDate() != null) {
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_MODIFICATION_DATE), getModificationDate());
			} else {
				criteria.addNullExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_MODIFICATION_DATE));
			}
			return reservationBean.getCount(criteria) == 0;
		}
		setSkipDirtyControl(false);
		return false;
	}

}