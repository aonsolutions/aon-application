package com.esferalia.aon.ui.calendar.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.calendar.controller.HolidayController;

public class HolidayControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		HolidayController c = (HolidayController) getController();
		GregorianCalendar cal= new GregorianCalendar();
		c.setYear(cal.get(Calendar.YEAR));	
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		Integer[] ids = {0, DomainManager.getCurrentDomain(), getParentDomainId()};
		try {
			event.getController().getCriteria().setSkipDomainFilter( true );
			event.getController().getCriteria().addInExpression("Holiday.domain", ids);
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
