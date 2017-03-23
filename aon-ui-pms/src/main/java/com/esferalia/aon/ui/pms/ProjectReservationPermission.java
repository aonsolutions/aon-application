package com.esferalia.aon.ui.pms;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.common.role.BasicRoleManager;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationPermission implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ProjectReservation reservation;
	private ProjectReservationRoom reservationRoom;
	private ProjectReservationService reservationService;
	private ReservationUtils reservationUtils;
	private BasicRoleManager roleManager;

	public ProjectReservation getReservation() {
		return reservation;
	}

	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
	}

	public ProjectReservationRoom getReservationRoom() {
		return reservationRoom;
	}

	public void setReservationRoom(ProjectReservationRoom reservationRoom) {
		this.reservationRoom = reservationRoom;
	}

	public ProjectReservationService getReservationService() {
		return reservationService;
	}

	public void setReservationService(ProjectReservationService reservationService) {
		this.reservationService = reservationService;
	}

	private ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	private BasicRoleManager getRoleManager() {
		if (roleManager == null) {
			roleManager = AonUtil.getRoleManager();
		}
		return roleManager;
	}

	/*************************** GENERIC *******************************/

	private boolean isRoleAdmin() {
		return getRoleManager().isAdmin();
	}

	private boolean isRoleManager() {
		return getRoleManager().isAccountingManager();
	}

	private boolean isRoleConfig() {
		return getRoleManager().isConfig();
	}

	private boolean isRoleCommercial() {
		return getRoleManager().isCommercialOperator();
	}

	private boolean isRoleFinance() {
		return getRoleManager().isFinanceOperator();
	}

	public boolean isRoleUser() {
		return !isRoleAdmin() && !isRoleConfig() && !isRoleCommercial() && !isRoleFinance();
	}


	/*************************** RESERVATION *******************************/

	public boolean isMyScope() {
		return (!reservation.isDiverted() || UserUtils.getInstance().isScopeInUserScopes(reservation.getHotel().getScope()));
	}

	private boolean isInHouse(Date date) {
		return (!isBeforeCheckIn(date) && !isAfterCheckOut(date));
	}

	private boolean isBeforeCheckIn(Date date) {
		return reservation.getStartDate().after(date);
	}
	
	private boolean isAfterCheckOut(Date date) {
		return DateUtils.addDays(reservation.getEndDate(), 1).before(date);
	}

	public boolean isCheckInable(Date date) throws ManagerBeanException {
		return reservation.isNoCheck() && isInHouse(date) && !getReservationUtils().isPendingRoomAssignation(reservation);
	}

	private boolean isCheckOutDay(Date date) {
		return reservation.getEndDate().before(date) && DateUtils.addDays(reservation.getEndDate(), 1).after(date);
	}

	private boolean isCheckOutable(Date date) {
		return (reservation.isCheckIn() && isCheckOutDay(date)) || ((reservation.isNoCheck() || reservation.isCheckIn()) && isAfterCheckOut(date));
	}
	
	private boolean isNoShowable(Date date) {
		Date referenceDate = DateUtils.addDays(reservation.getStartDate(), 1);
		return (referenceDate.before(date) && DateUtils.addHours(referenceDate, 12).after(date));
	}

	private boolean isPendingAssignation() throws ManagerBeanException {
		return getReservationUtils().isPendingRoomAssignation(reservation) || getReservationUtils().isPendingServiceAssignation(reservation, false);
	}

	public boolean isNewReservationAllowed() {
		return isRoleCommercial();
	}

	public boolean isSaveReservationAllowed() {
		return isMyScope();
	}

	public boolean isShowMoreMenuAllowed() throws ManagerBeanException {
		return (isCheckInAllowed() || isCheckOutAllowed() || isEarlyCheckOutAllowed() || isAdvanceInvoiceAllowed() || isInvoiceAllowed() || isNoShowAllowed() 
				|| isCancelAllowed() || isDivertAllowed() || isConexFlowVisible()) && isMyScope();
	}

	public boolean isCheckInAllowed() throws ManagerBeanException {
		Date now = new Date();
		return (reservation.isActive() || reservation.isInvoiced()) && isCheckInable(now);
	}

	public boolean isCheckOutAllowed() {
		Date now = new Date();
		return reservation.isInvoiced() && isCheckOutable(now);
	}

	public boolean isEarlyCheckOutAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isInHouse(now)) || ((isRoleConfig() || isRoleFinance()) && isAfterCheckOut(now));
		return roleAllowed && !reservation.isEarlyCheckOut() && reservation.isInvoiced() && reservation.isCheckIn();
	}

	public boolean isAdvanceInvoiceAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleAdmin() || (isInHouse(now) && !isPendingAssignation()) || (isRoleFinance() && !isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isGuestHolder();
	}

	public boolean isTouristTaxInvoiceAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isInHouse(now) || isAfterCheckOut(now);
		return roleAllowed && reservation.isActive() && !isPendingAssignation() && reservation.getTouristTaxPending() > 0;
	}

	public boolean isInvoiceAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isInHouse(now) || isAfterCheckOut(now);
		return roleAllowed && reservation.isActive() && !isPendingAssignation() && reservation.getTouristTaxPending() == 0;
	}

	public boolean isNoShowAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isNoShowable(now)) || (!isRoleUser() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isNoCheck();
	}

	public boolean isCancelAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && !isBeforeCheckIn(now));
		return roleAllowed && (reservation.isActive() || reservation.isBlocked()) && (reservation.isNoCheck() || reservation.isNoShow());
	}

	public boolean isDivertAllowed() throws ManagerBeanException {
		Date now = new Date();
		return !reservation.isCancelled() && !reservation.isNoShow() && !isCheckOutDay(now) && !isAfterCheckOut(now) && !isPendingDivertVisible();
	}

	public boolean isBlockAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && !isAfterCheckOut(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isNoCheck();
	}

	public boolean isUnBlockAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && !isAfterCheckOut(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isBlocked() && !reservation.isNoShow();
	}

	public boolean isUndoCheckStatusAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean isCancelled = reservation.getStatus() == ReservationStatus.CANCELLED;
		boolean isOnlyNoShow = reservation.getCheckStatus() == ReservationCheckStatus.NO_SHOW;
		boolean isCancelInvoiceable = reservation.getCheckStatus() == ReservationCheckStatus.CANCEL_INVOICEABLE;
		boolean roleCheckAllowed = reservation.isCheckIn() && !isRoleUser() && isInHouse(now);
		boolean roleNoShowAllowed = isCancelled && isOnlyNoShow && ((isRoleCommercial() && !isAfterCheckOut(now)) || (isRoleFinance() && !isBeforeCheckIn(now)));
		boolean roleCancelAllowed = isCancelled && isCancelInvoiceable && isRoleFinance();
		return roleCheckAllowed || roleNoShowAllowed || roleCancelAllowed;
	}

	public boolean isCheckStatusVisible() throws ManagerBeanException {
		return !reservation.isNoCheck();
	}

	public boolean isPendingDivertVisible() throws ManagerBeanException {
		return getReservationUtils().isPendingDivert(reservation.getId());
	}

	public boolean isDivertInfoVisible() throws ManagerBeanException {
		return !reservation.getHotel().equals(reservation.getHotelReservation());
	}

	public boolean isModificationDateVisible() {
		return reservation.getModificationDate() != null;
	}

	public boolean isCancellationDateVisible() {
		return reservation.getCancellationDate() != null;
	}

	public boolean isCrsCodeVisible() {
		return StringUtils.isNotEmpty(reservation.getCrsCode());
	}

	public boolean isReservationCodeEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return isRoleManager() || (roleAllowed && reservation.isActive());
	}

	public boolean isReservationDatesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && !reservation.isInUse();
	}

	public boolean isBookingHolderEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive();
	}

	public boolean isAgencyVisible() throws ManagerBeanException {
		return !reservation.isCompanyHolder();
	}

	public boolean isCompanyVisible() throws ManagerBeanException {
		return reservation.isCompanyHolder();
	}

	public boolean isAgencyEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && isAgencyVisible();
	}

	public boolean isCompanyEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && isCompanyVisible();
	}

	public boolean isAdvanceEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleAdmin() || (isRoleFinance() && !isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && !reservation.isAdvanceInvoiced() && reservation.isGuestHolder();
	}

	public boolean isAdvanceEnable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleAdmin() || (isRoleFinance() && !isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isAdvanceInvoiced() && reservation.isGuestHolder() && reservation.getPendingAmount() > 0;
	}

	public boolean isSellerEditable() throws ManagerBeanException {
		boolean roleAllowed = isRoleAdmin();
		return roleAllowed && reservation.isActive();
	}

	public boolean isCommentsEditable() throws ManagerBeanException {
		return !isAfterCheckOut(new Date());
	}

	public boolean isRemarksEditable() throws ManagerBeanException {
		return isRoleAdmin();
	}

	public boolean isTouristTaxFreeAllowed() throws ManagerBeanException {
		return reservation.isActive() && reservation.getTouristTaxFree() == null && reservation.getTouristTaxPending() == reservation.getAdultCount();
	}

	public boolean isUndoTouristTaxFreeAllowed() throws ManagerBeanException {
		return reservation.isActive() && reservation.getTouristTaxFree() != null;
	}

	public boolean isConexFlowVisible() throws ManagerBeanException {
		boolean roleAllowed = isRoleCommercial() || isRoleFinance();
		return roleAllowed && !reservation.isInvoiced() && !reservation.isNoInvoiceable() && getReservationUtils().isConexFlowAvailable();
	}

	public boolean isNewConexFlowAllowed() throws ManagerBeanException {
		boolean roleAllowed = isRoleCommercial() || isRoleFinance();
		return roleAllowed && !reservation.isNewCreditCard();
	}

	public boolean isSaveConexFlowAllowed() throws ManagerBeanException {
		boolean roleAllowed = isRoleCommercial() || isRoleFinance();
		return roleAllowed && reservation.isNewCreditCard();
	}

	public boolean isCreditCardVisible() throws ManagerBeanException {
		boolean roleAllowed = isRoleCommercial() || isRoleFinance();
		return roleAllowed && (StringUtils.isNotBlank(reservation.getCreditCardNumber()) || reservation.isNewCreditCard());
	}

	public boolean isCreditCardDataEditable() throws ManagerBeanException {
		boolean roleAllowed = isRoleCommercial() || isRoleFinance();
		return roleAllowed && reservation.isNewCreditCard();
	}

	public boolean isConexFlowOperable() throws ManagerBeanException {
		boolean roleAllowed = isRoleFinance();
		return roleAllowed && !reservation.isBlankToken() && !reservation.isNewCreditCard();
	}

	public boolean isTokenRemoveEnable() {
		return isRoleCommercial() || isRoleFinance();
	}

	/*************************** RESERVATION GUEST *******************************/

	public boolean isNewReservationGuestAllowed() throws ManagerBeanException {
		return (reservation.isActive() || reservation.isInvoiced()) && isMyScope() && reservation.getPersonCount() > reservation.getGuestCount();
	}

	public boolean isEditReservationGuestAllowed() {
		return (reservation.isActive() || reservation.isInvoiced()) && isMyScope();
	}


	/*************************** RESERVATION ROOM *******************************/

	public boolean isNewReservationRoomAllowed() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.getAdvancedAmount() == 0 && !reservation.isEarlyCheckOut() && isMyScope();
	}

	public boolean isSelectReservationRoomAllowed() {
		return (reservation.isActive() || reservation.isInvoiced()) && !reservation.isEarlyCheckOut() && isMyScope();
	}

	public boolean isAcceptReservationRoomAllowed() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed;
	}

	public boolean isCancelReservationRoomAllowed() throws ManagerBeanException {
		return reservation.isActive() && reservationRoom.getRoomNumber() != null;
	}

	public boolean isRemoveReservationRoomAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.getAdvancedAmount() == 0 && reservationRoom.getRoomNumber() == null;
	}

	public boolean isReservationRoomTariffAllowed() {
		return isRoleAdmin();
	}

	public boolean isReservationRoomTariffEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive();
	}

	public boolean isReservationRoomPaxesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed;
	}

	public boolean isReservationRoomServicesEditable() throws ManagerBeanException {
		return reservationRoom.getRoomNumber() == null;
	}


	/*************************** RESERVATION SERVICE *******************************/

	public boolean isNewReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.getAdvancedAmount() == 0 && isMyScope();
	}

	public boolean isSelectReservationServiceAllowed() throws ManagerBeanException {
		return reservation.isActive() && isMyScope();
	}

	public boolean isAcceptReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.getAdvancedAmount() == 0;
	}

	public boolean isRemoveReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && !reservationService.isExtra() && reservation.getAdvancedAmount() == 0;
	}

	public boolean isReservationServicesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && !reservationService.isExtra() && reservation.getAdvancedAmount() == 0;
	}

	public boolean isReservationServicePricesEditable() {
		Date now = new Date();
		boolean roleAllowed = isRoleCommercial() || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && isMyScope() && reservation.getAdvancedAmount() == 0;
	}


	/*************************** RESERVATION ATTACH *******************************/

	public boolean isReservationAttachEditable() {
		Date now = new Date();
		return isRoleAdmin() || (!reservation.isCancelled() && isMyScope() && !isAfterCheckOut(now));
	}

}
