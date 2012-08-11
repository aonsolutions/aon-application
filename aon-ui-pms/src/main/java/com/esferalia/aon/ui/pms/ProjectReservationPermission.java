package com.esferalia.aon.ui.pms;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.role.BasicRoleManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationPermission {

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
			reservationUtils = new ReservationUtils();
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

	private boolean isRoleConfig() {
		return getRoleManager().isConfig();
	}

	private boolean isRoleCommercial() {
		return getRoleManager().isCommercialOperator();
	}

	private boolean isRoleFinance() {
		return getRoleManager().isFinanceOperator();
	}

	private boolean isRoleUser() {
		return !isRoleAdmin() && !isRoleConfig() && !isRoleCommercial() && !isRoleFinance();
	}


	/*************************** RESERVATION *******************************/

	private boolean isInHouse(Date date) {
		return (!isBeforeCheckIn(date) && !isAfterCheckOut(date));
	}

	private boolean isBeforeCheckIn(Date date) {
		return reservation.getStartDate().after(date);
	}
	
	private boolean isAfterCheckOut(Date date) {
		return DateUtils.addDays(reservation.getEndDate(), 1).before(date);
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
		return isRoleConfig() || isRoleFinance();
	}

	public boolean isShowMoreMenuAllowed() {
		return !reservation.isCancelled() && !reservation.isCheckOut();
	}

	public boolean isCheckInAllowed() throws ManagerBeanException {
		Date now = new Date();
		return reservation.isActive() && reservation.isNoCheck() && isInHouse(now) && !getReservationUtils().isPendingRoomAssignation(reservation);
	}

	public boolean isCheckOutAllowed() {
		Date now = new Date();
		return reservation.isInvoiced() && isCheckOutable(now);
	}

	public boolean isEarlyCheckOutAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isInHouse(now)) || ((isRoleConfig() || isRoleFinance()) && isAfterCheckOut(now));
		return roleAllowed && reservation.isInvoiced() && reservation.isCheckIn();
	}

	public boolean isInvoiceAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isInHouse(now) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && !isPendingAssignation();
	}

	public boolean isNoShowAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isNoShowable(now)) || (!isRoleUser() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isNoCheck();
	}

	public boolean isCancelAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleAdmin()) || ((isRoleConfig() || isRoleCommercial()) && isBeforeCheckIn(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isNoCheck();
	}

	public boolean isDivertAllowed() throws ManagerBeanException {
		Date now = new Date();
		return !reservation.isCancelled() && !isCheckOutDay(now) && !isAfterCheckOut(now) && !getReservationUtils().isPendingDivert(reservation.getId());
	}

	public boolean isBlockAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleAdmin()) || ((isRoleConfig() || isRoleCommercial()) && isBeforeCheckIn(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservation.isNoCheck();
	}

	public boolean isUnBlockAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleAdmin()) || ((isRoleConfig() || isRoleCommercial()) && isBeforeCheckIn(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isBlocked();
	}

	public boolean isUndoCheckStatusAllowed() throws ManagerBeanException {
		Date now = new Date();
		return reservation.isCheckIn() && reservation.isActive() && isInHouse(now);
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

	public boolean isCrsCodeVisible() {
		return StringUtils.isNotEmpty(reservation.getCrsCode());
	}

	public boolean isReservationCodeEditable() throws ManagerBeanException {
		boolean roleAllowed = isRoleAdmin();
		return roleAllowed && reservation.isActive();
	}

	public boolean isReservationDatesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && !reservation.isInUse();
	}

	public boolean isBookingHolderEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
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
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && isAgencyVisible();
	}

	public boolean isCompanyEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && isCompanyVisible();
	}

	public boolean isAdvanceEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = isRoleAdmin() || (isRoleFinance() && !isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && !reservation.isAdvanceInvoiced() && reservation.isGuestHolder();
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


	/*************************** RESERVATION GUEST *******************************/

	public boolean isNewReservationGuestAllowed() {
		return reservation.isActive() || reservation.isInvoiced();
	}

	public boolean isEditReservationGuestAllowed() {
		return reservation.isActive() || reservation.isInvoiced();
	}


	/*************************** RESERVATION ROOM *******************************/

	public boolean isNewReservationRoomAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive();
	}

	public boolean isSelectReservationRoomAllowed() {
		return reservation.isActive() || reservation.isInvoiced();
	}

	public boolean isAcceptReservationRoomAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed;
	}

	public boolean isCancelReservationRoomAllowed() throws ManagerBeanException {
		return reservation.isActive() && reservationRoom.getRoomNumber() != null;
	}

	public boolean isRemoveReservationRoomAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive() && reservationRoom.getRoomNumber() == null;
	}

	public boolean isReservationRoomTariffEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive();
	}

	public boolean isReservationRoomPaxesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed;
	}

	public boolean isReservationRoomServicesEditable() throws ManagerBeanException {
		return reservationRoom.getRoomNumber() == null;
	}


	/*************************** RESERVATION SERVICE *******************************/

	public boolean isNewReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && reservation.isActive();
	}

	public boolean isSelectReservationServiceAllowed() throws ManagerBeanException {
		return reservation.isActive();
	}

	public boolean isAcceptReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed;
	}

	public boolean isRemoveReservationServiceAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && !reservationService.isExtra();
	}

	public boolean isReservationServicesEditable() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleCommercial() && isBeforeCheckIn(now)) || (isRoleConfig() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return roleAllowed && !reservationService.isExtra();
	}

}
