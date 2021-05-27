package com.code.aon.ui.commercial.event	;


import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;

import com.code.aon.AonVersion;
import com.code.aon.google.apis.CalendarUtils;
import com.code.aon.google.apis.jooq.DBCalendar;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Events;


public class GoogleCalendarSynchronizer extends ControllerAdapter {
	
	public class UpdateThread extends Thread{
		
		private final ControllerEvent event;
		private final String domain;
		private final String login;
		
		public UpdateThread(ControllerEvent event, String domain, String login) {
			this.event = event;
			this.domain = domain;
			this.login = login;
		}
		
		@Override
		public void run() {
			try {
				CommercialTracking tracking = getCommercialTracking(event);
				Domain company = DBConsults.getDomain(domain,tracking.getDomain());
				User user = new User().setLogin(login);
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, company.getId());
				if (g.getClientId() != null){
					CalendarUtils.serviceInitialize(g);
					
					CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
					int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
					Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
					int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
					CalendarUtils.modifyEvent(company, user, events.getItems().get(j).getId(), DBCalendar.getCommercialTrackingOne(company, user, tracking.getId()), calendars.getItems().get(i).getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}

		public ControllerEvent getEvent() {
			return event;
		}


	}
	
	public class AddThread extends Thread{
		
		private final ControllerEvent event;
		private final String domain;
		private final String login;
		
		public AddThread(ControllerEvent event, String domain, String login) {
			this.event = event;
			this.domain = domain;
			this.login = login;
		}
		
		@Override
		public void run() {
			try {
				User user = new User().setLogin(login);
				CommercialTracking tracking = getCommercialTracking(event);
				Domain company = DBConsults.getDomain(domain,tracking.getDomain());
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, company.getId());
				if (g.getClientId() != null){
					CalendarUtils.serviceInitialize(g);

					CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
					int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
					CommercialTracking ct =  DBCalendar.getCommercialTrackingOne(company, user, tracking.getId());
					if(i==-1){
						Calendar calendar = CalendarUtils.newCalendar(company, user);
						CalendarUtils.addEvent(company, user, calendar.getId(), CalendarUtils.newEvent(company, user, ct),ct);

					}
					else{
						CalendarUtils.addEvent(company, user, calendars.getItems().get(i).getId(), CalendarUtils.newEvent(company, user, ct),ct);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}

		public ControllerEvent getEvent() {
			return event;
		}
	}
	
	public class RemoveThread extends Thread{
	
		private final ControllerEvent event;
		private final String domain;
		private final CommercialTracking tracking;
		private final String login;
	
		public RemoveThread(ControllerEvent event, String domain, CommercialTracking tracking, String login) {
			this.event = event;
			this.domain = domain;
			this.tracking = tracking;
			this.login = login;
		}
	
		@Override
		public void run() {
			try {
				User user = new User().setLogin(login);
				Domain company = DBConsults.getDomain(domain,tracking.getDomain());
				DomainGserviceaccount g = DBConsults.getServiceAccount(company, user);
				if (g.getClientId() != null){
					CalendarUtils.serviceInitialize(g);	
					CalendarList calendars = CalendarUtils.Quicksort.calendarsSort(CalendarUtils.getCalendars());
					int i=CalendarUtils.searchCalendars(calendars, company.getName(), calendars.getItems().size() );
					Events events=CalendarUtils.Quicksort.eventsSort(CalendarUtils.getEvents(calendars.getItems().get(i).getId()));
					int j=CalendarUtils.searchEvents(events, tracking.getId(), events.getItems().size());
					CalendarUtils.removeEvent(calendars.getItems().get(i).getId(), events.getItems().get(j).getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}	
		}

		public ControllerEvent getEvent() {
			return event;
		}	
	}

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		String domain= AonUtil.getDomainName();
		String login = AonUtil.getRemoteUser();
		UpdateThread thread = new UpdateThread(event,domain, login);
		thread.start();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		String domain= AonUtil.getDomainName();
		String login = AonUtil.getRemoteUser();
		AddThread thread = new AddThread(event,domain, login);
		thread.start();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		String domain= AonUtil.getDomainName();
		String login = AonUtil.getRemoteUser();
		CommercialTracking tracking = getCommercialTracking(event);
		RemoveThread thread = new RemoveThread(event,domain,tracking, login);
		thread.start();
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
		com.code.aon.commercial.CommercialTracking ct = (com.code.aon.commercial.CommercialTracking) ((CommercialTrackingController) event
				.getController()).getTo();
		return new CommercialTracking()
				.setActivity(ct.getActivity() != null ? ct.getActivity().getId() : null)
				.setAllday(ct.isAllDay())
				.setComments(ct.getComments())
				.setDate(ct.getDate())
				.setDomain(ct.getDomain())
				.setEndDate(ct.getEndDate())
				.setEventId(ct.getEventId())
				.setId(ct.getId())
				.setLocation(ct.getLocation())
				.setNextCommercialTracking(ct.getNext() != null ? ct.getNext().getId() : null)
				.setOffer(ct.getOffer() != null ? ct.getOffer().getId() : null)
				.setProjectCommercial(ct.getProject() != null ? ct.getProject().getId() : null)
				.setSeller(ct.getSeller() != null ? ct.getSeller().getId() : null)
				.setStatus((byte)ct.getStatus().ordinal());
	}
}
