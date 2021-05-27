package com.esferalia.aon.ui.calendar.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.calendar.controller.HolidayController;

public class HolidayDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HolidayDetailControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		LinesController controller = (LinesController) getController();
		Integer year = ((HolidayController)controller.getMasterController()).getYear();
		Calendar startCal = new GregorianCalendar();
		Calendar endCal = new GregorianCalendar();
		startCal.set(year, Calendar.JANUARY, 1);
		endCal.set(year, Calendar.DECEMBER, 31);
		Integer masterId = ((Holiday)controller.getMasterController().getTo()).getId();
		try {
			controller.clearCriteria();
			controller.getCriteria().addEqualExpression(this.getController().getFieldName(IEntityAlias.HOLIDAY_DETAIL_HOLIDAY_ID), masterId);
			controller.getCriteria().addBetweenExpression(this.getController().getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE), startCal.getTime(), endCal.getTime());
			Integer[] ids = {0, DomainManager.getCurrentDomain(), getParentDomainId()};
			controller.getCriteria().setSkipDomainFilter( true );
			controller.getCriteria().addInExpression("HolidayDetail.domain", ids);
		} catch (ManagerBeanException e) {
			LOGGER.error("error on HolidayDetailControllerListener");
			try {
				controller.getCriteria().setSkipDomainFilter( false );
			} catch (ManagerBeanException e2) {
				// nada
			}
		}
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		Integer[] ids = {0, DomainManager.getCurrentDomain(), getParentDomainId()};
		try {
			event.getController().getCriteria().setSkipDomainFilter( true );
			event.getController().getCriteria().addInExpression("HolidayDetail.domain", ids);
		} catch (ManagerBeanException e) {
			try {
				event.getController().getCriteria().setSkipDomainFilter( false );
			} catch (ManagerBeanException e2) {
				// nada
			}
		}
	}
	
	private Integer getParentDomainId() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT parent FROM domain WHERE id = " + DomainManager.getCurrentDomain();
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	
}
