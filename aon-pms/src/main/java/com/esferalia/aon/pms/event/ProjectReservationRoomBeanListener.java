package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.AdminUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationRoomBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(to.getDomain()));
			SQLBooking.insert(connection, to);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(connection);
		}
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
    	if (to.isForceRefreshBooking()) {
    		Connection connection = null;
    		try {
    			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(to.getDomain()));
    			SQLBooking.delete(connection, to);
    			if (!to.getProjectReservation().isCancelled() && !to.getProjectReservation().isNoShow()) {
       				SQLBooking.insert(connection, to);
    			}
			} catch (Throwable e) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
				}
				throw new ManagerBeanException(e.getMessage());
			} finally {
				SQLUtils.closeQuietly(connection);
			}
    		to.setForceRefreshBooking(false);
    	}
    }

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ROOM), to.getId());
		for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
			ProjectReservationService reservationService = (ProjectReservationService)ito;
			reservationService.setProjectReservationRoom(null);
			reservationServiceBean.update(reservationService);
		}
	}

}
