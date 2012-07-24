package com.esferalia.aon.ui.pms;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.role.BasicRoleManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationPermission {

	private ProjectReservation reservation;
	private ReservationUtils reservationUtils;
	private BasicRoleManager roleManager;

	public ProjectReservation getReservation() {
		return reservation;
	}

	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
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

	private boolean isNoShowable(Date date) {
		Date now = new Date();
		Date referenceDate = DateUtils.addDays(reservation.getStartDate(), 1);
		return (referenceDate.before(now) && DateUtils.addHours(referenceDate, 12).after(now));
	}


	public boolean isNewReservationAllowed() {
		return isRoleConfig();
	}

	public boolean isCheckInAllowed() throws ManagerBeanException {
		Date now = new Date();
		return reservation.isActive() && reservation.isNoCheck() && isInHouse(now) && !getReservationUtils().isPendingRoomAssignation(reservation);
	}

	public boolean isCheckOutAllowed() {
		Date now = new Date();
		return reservation.isInvoiced() && reservation.isCheckIn() && (isCheckOutDay(now) || isAfterCheckOut(now));
	}

	public boolean isEarlyCheckOutAllowed() {
		Date now = new Date();
		boolean roleAllowed = (isRoleConfig() && !isBeforeCheckIn(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return reservation.isInvoiced() && reservation.isCheckIn() && roleAllowed;
	}

	public boolean isInvoiceAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleAdmin() && isAfterCheckOut(now)) || isInHouse(now);
		return reservation.isActive() && !getReservationUtils().isPendingServiceAssignation(reservation, false) && roleAllowed;
	}

	public boolean isNoShowAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isNoShowable(now)) || (!isRoleUser() && isInHouse(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return reservation.isActive() && reservation.isNoCheck() && roleAllowed;
	}

	public boolean isCancelAllowed() throws ManagerBeanException {
		Date now = new Date();
		boolean roleAllowed = (isRoleAdmin()) || ((isRoleConfig() || isRoleCommercial()) && isBeforeCheckIn(now)) || (isRoleFinance() && isAfterCheckOut(now));
		return reservation.isActive() && reservation.isNoCheck() && roleAllowed;
	}

	public boolean isDivertAllowed() throws ManagerBeanException {
		Date now = new Date();
		return !reservation.isCancelled() && !isCheckOutDay(now) && !isAfterCheckOut(now) && !getReservationUtils().isPendingDivert(reservation.getId());
	}

}
