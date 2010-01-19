package com.code.aon.ui.desktop.applications;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.controller.DesktopController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.messaging.controller.SMSController;
import com.code.aon.ui.util.AonUtil;

public class SMSManager implements Serializable, IServices, IDesktopConstants  {

	private static final long serialVersionUID = -5534264216750579958L;
	
	private static final Logger LOGGER = Logger.getLogger( SMSManager.class.getName() );
	
	private static final String APP_BUNDLE = "appBundle";
	
	private static final String totalMessages = "SELECT count(*) FROM Message as msg " +
	"WHERE msg.sentDate BETWEEN :fromDate AND :toDate";

	private ApplicationsManager.App app;

	@SuppressWarnings("unchecked")
	public SMSManager() {
		try {
			ApplicationsManager apps = 
				(ApplicationsManager) AonUtil.getRegisteredBean( APPLICATIONS_CONTROLLER_NAME );
			app = apps.getApplication( SMSController.AON_SMS_APPLICATION );
			initSMSController();
		} catch (ManagerBeanException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	private void initSMSController() throws ManagerBeanException {
		SMSController sms = (SMSController) AonUtil.getRegisteredBean( SMS_CONTROLLER_NAME );
		String username = UserUtils.getInstance().getPrincipal().getShortName();
		sms.setUsername(username);
		DesktopController desktop = (DesktopController) AonUtil.getRegisteredBean( DESKTOP_CONTROLLER_NAME );
		sms.setOrganization(desktop.getCompanyAlias());
	}
	
	public void sendMessage(ActionEvent event) {
		if ( !isExecutable() ) {
			String message = AonUtil.addInfoMessageFromBundle(APP_BUNDLE, "aon_sms_application_service_exception");
			throw new AbortProcessingException( message );
		}
		SMSController sms = (SMSController) AonUtil.getRegisteredBean( SMS_CONTROLLER_NAME );
		updateDomainName(sms);
		sms.sendMessage(event);
	}

// ************************************** IServices methods implementation *************************************
	public boolean isExecutable() {
		if ( app == null ) {
			int sent = (int)getCurrentMonthMessageSent();
			if ( sent >= 5 ) return false;
			return true;
		}
		return app.isExecutable();
	}

	public boolean isInfobarEnabled() {
		return app != null && app.isInfobarEnabled();
	}

	public boolean isSidebarEnabled() {
		return (app == null)? true: app.isSidebarEnabled();
	}

	public boolean isToolbarEnabled() {
		return app != null && app.isToolbarEnabled();
	}

	public boolean isNoticeEnabled() {
		return app != null;
	}

// ********************************** End of IServices methods implementation **********************************
	
	private void updateDomainName( SMSController sms ) {
		long sent = getCurrentMonthMessageSent();
        
		if ( app == null ) {
			if ( sent < 6 ) {
				sms.setDomainName(null);
			}
		} else {
			String domain = UserUtils.getInstance().getPrincipal().getDomain();
			sms.setDomainName(domain);	
		}
	}

	private long getCurrentMonthMessageSent() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		Month current_month = Month.getMonthByValue( calendar.get( Calendar.MONTH ) );
		int current_year = calendar.get( Calendar.YEAR );

        Calendar fromDate = Calendar.getInstance();
        fromDate.set( Calendar.DATE, 1 );
        fromDate.set( Calendar.MONTH, current_month.getValue() );
        fromDate.set( Calendar.YEAR, current_year );
        Calendar toDate = Calendar.getInstance();
        toDate.set( Calendar.DATE, 1 );
        toDate.set( Calendar.MONTH, current_month.getValue() + 1 );
        toDate.set( Calendar.YEAR, current_year );
        //Ahora restamos un dia
        toDate.add(Calendar.DATE, -1);
        String sessionFactoryName = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(sessionFactoryName);
        Query query = session.createQuery( totalMessages );
        query.setDate( "fromDate", fromDate.getTime() );
        query.setDate( "toDate", toDate.getTime() );
        long sent = (Long) query.uniqueResult();
        return sent;
	}

}