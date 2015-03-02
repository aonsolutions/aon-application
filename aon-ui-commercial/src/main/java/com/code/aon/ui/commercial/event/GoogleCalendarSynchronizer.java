package com.code.aon.ui.commercial.event	;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import javax.naming.NamingException;

import com.code.aon.AonVersion;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.google.apis.CalendarUtils;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Events;


public class GoogleCalendarSynchronizer extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {

		String domain= AonUtil.getDomainName();
		
		try {
			CommercialTracking tracking = getCommercialTracking(event);
			Domain company = DBConsults.getDomain(domain,tracking.getDomain());
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, company.getId());
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);
				
				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
				int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
				CalendarUtils.modifyEvent(events.getItems().get(j).getId(),DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain), calendars.getItems().get(i).getId(),domain);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		String domain=AonUtil.getDomainName();
		try {
			
			CommercialTracking tracking = getCommercialTracking(event);
			Domain company = DBConsults.getDomain(domain,tracking.getDomain());
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, company.getId());
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);

				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				if(i==-1){
					Calendar calendar =CalendarUtils.newCalendar(company);
					CalendarUtils.addEvent(calendar.getId(), CalendarUtils.newEvent(DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain),domain));

				}
				else{
					CalendarUtils.addEvent(calendars.getItems().get(i).getId(), CalendarUtils.newEvent(DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain),domain));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		
		String domain=AonUtil.getDomainName();
		try {
			
			CommercialTracking tracking = getCommercialTracking(event);
			Domain company = DBConsults.getDomain(domain,tracking.getDomain());
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, company.getId());
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);	
				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
				int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
				CalendarUtils.removeEvent(calendars.getItems().get(i).getId(), events.getItems().get(j).getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	Integer getDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getDomainId();
	}
	
	Integer getParentDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getParentDomainId();
	}
	
	// --------------------------------------------------------- Private methods

	private CommercialTracking getCommercialTracking(ControllerEvent event) {
		return (CommercialTracking) ((CommercialTrackingController) event
				.getController()).getTo();

	}
}
