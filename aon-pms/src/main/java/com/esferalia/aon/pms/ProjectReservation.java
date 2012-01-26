package com.esferalia.aon.pms;


import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.IProject;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationDB;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

@Entity
@Table(name="project_reservation")
@PrimaryKeyJoinColumn(name="project")
public class ProjectReservation extends ProjectReservationDB implements ICalculableContainer, IProject {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectReservation.class.getName());

	public ProjectReservation() {
		setStatus(ReservationStatus.ACTIVE);
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
	public boolean isFinished() {
		return getStatus() == ReservationStatus.FINISHED;
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
	public int getRoomCount() throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), getId());
		return reservationRoomBean.getCount(criteria);
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
		String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
		try {
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), getId());
			return reservationServiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining services list", e);
		}
		return null;
	}

}