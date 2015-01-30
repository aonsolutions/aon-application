package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ReservationRequestRoomDB;

@Entity
@Table(name="reservation_request_room")
public class ReservationRequestRoom extends ReservationRequestRoomDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ProjectReservation reservation;

	@Transient
	public ProjectReservation getReservation() throws ManagerBeanException {
		if (StringUtils.isNotBlank(getCrsCode()) && (reservation == null || reservation.getId() == null)) {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), getCrsCode());
			for (ITransferObject ito : reservationBean.getList(criteria)) {
				reservation = (ProjectReservation)ito;
				break;
			}
		}
		return reservation;
	}
	public void setReservation(ProjectReservation reservation) {
		this.reservation = reservation;
	}

}