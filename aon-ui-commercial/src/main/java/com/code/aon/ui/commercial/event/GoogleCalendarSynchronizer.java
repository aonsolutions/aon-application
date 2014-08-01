package com.code.aon.ui.commercial.event	;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import com.code.aon.AonVersion;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.google.apis.*;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
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
			DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);
				CommercialTracking tracking = getCommercialTracking(event);
				Domain company = DatabaseSync.getDomainName(tracking.getId(),domain);
				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
				int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
				CalendarUtils.modifyEvent(events.getItems().get(j).getId(),DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain), calendars.getItems().get(i).getId(),domain);
			}
			
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Apéndice de método generado automáticamente
		super.afterBeanAdded(event);
		String domain=AonUtil.getDomainName();
		try {
			
			DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);
				CommercialTracking tracking = getCommercialTracking(event);
				Domain company = DatabaseSync.getDomainName(tracking.getId(),domain);
				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				if(i==-1){
					Calendar calendar =CalendarUtils.newCalendar(domain);
					CalendarUtils.addEvent(calendar.getId(), CalendarUtils.newEvent(DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain),domain));

				}
				else{
					CalendarUtils.addEvent(calendars.getItems().get(i).getId(), CalendarUtils.newEvent(DatabaseSync.getCommercialTrackingOne(tracking.getId(), domain),domain));
				}
			}
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Apéndice de método generado automáticamente

		super.afterBeanRemoved(event);
		String domain=AonUtil.getDomainName();
		try {
			
			DomainGserviceaccount g = DatabaseSync.getServiceAccount(domain);
			if (g.getClientId() != null){
				CalendarUtils.serviceInitialize(g);
				CommercialTracking tracking = getCommercialTracking(event);
				Domain company = DatabaseSync.getDomainName(tracking.getId(),domain);			
				CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
				int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
				Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
				int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
				CalendarUtils.removeEvent(calendars.getItems().get(i).getId(), events.getItems().get(j).getId());
			}
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Bloque catch generado automáticamente
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
