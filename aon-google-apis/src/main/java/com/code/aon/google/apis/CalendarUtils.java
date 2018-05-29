package com.code.aon.google.apis;


//import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getHttpTransport;
//import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getJsonFactory;
//import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getPrincipalShortName;
//import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.newFlow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.google.apis.drive.SearchFiles;
import com.code.aon.google.apis.jooq.DBCalendar;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;


/**
 * @author aibanez
 */
public class CalendarUtils {
	

	/**
	 * @class Quicksort, implementación del algoritmo de ordanación
	 * rápida, Quicksort. Distinguiendo entre calendarios y eventos.
	 * @author aibanez
	 */
	public static class Quicksort {
		private static Events events;
		private static CalendarList calendars;
		private static int number;

		public static CalendarList calendarsSort(CalendarList cals){
			calendars = cals;
			number = cals.getItems().size();
			if (number!=0)
				quicksortCalendars(0,number - 1);
			return calendars;
		}
		public static Events eventsSort(Events eventos) {
			events = eventos;
			number = eventos.getItems().size();
			if(number != 0)
				quicksortEvents(0, number -1);
			return events;
		}

		static void quicksortCalendars(int low, int high) {
			int i = low, j = high;
			String pivot = calendars.getItems().get(low + (high - low) / 2).getSummary();
			while (i <= j) {
				while ( calendars.getItems().get(i).getSummary().compareTo(pivot)<0) {
					i++;
				}
				while (calendars.getItems().get(j).getSummary().compareTo(pivot)>0) {
					j--;
				}
				if (i <= j) {
					exchangeCalendars(i, j);
					i++;
					j--;
				}
			}
			if (low < j)
				quicksortCalendars(low, j);
			if (i < high)
				quicksortCalendars(i, high);
		}

		
		static void quicksortEvents(int low, int high) {
			int i = low, j = high;
			String pivot = (String) events.getItems().get(low + (high - low) / 2).getExtendedProperties().getPrivate().get("aonId");
			while (i <= j) {
				while ( events.getItems().get(i)
						.getExtendedProperties().getPrivate().get("aonId").compareTo(pivot)  <0) {
					i++;
				}
				while (events.getItems().get(j)
						.getExtendedProperties().getPrivate().get("aonId").compareTo(pivot)  >0) {
					j--;
				}
				if (i <= j) {
					exchange(i, j);
					i++;
					j--;
				}
			}
			if (low < j)
				quicksortEvents(low, j);
			if (i < high)
				quicksortEvents(i, high);
		}
		
		static void exchangeCalendars(int i, int j) {
			CalendarListEntry aux = calendars.getItems().get(i);
			calendars.getItems().set(i, calendars.getItems().get(j));
			calendars.getItems().set(j, aux);
		}
		
		static void exchange(int i, int j) {
			Event aux = events.getItems().get(i);
			events.getItems().set(i, events.getItems().get(j));
			events.getItems().set(j, aux);
		}
	}

	//private static Credential credential;	
	private static com.google.api.services.calendar.Calendar client;	
	
	/**
	 * initialize(HttpServletRequest req), Inicializa las variables globales
	 * "credential" y "client",para sincronizar la aplicación con 
	 * la cuenta de google del usuario.
	 * @param req, interfaz de ServletRequest que proporciona información
	 * para la solicitud de servlets HTTP.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void initialize(HttpServletRequest req) throws IOException,
			ServletException {
		/*
		 * credential = newFlow().loadCredential(getPrincipalShortName(req));
		 * client = new com.google.api.services.calendar.Calendar.Builder(
		 * 		getHttpTransport(), getJsonFactory(), credential)
		 * 		.setApplicationName("AON SOLUTIONS").build();
		*/
	}
	
	
	public static com.google.api.services.calendar.Calendar serviceInitialize(DomainGserviceaccount g) throws IOException, GeneralSecurityException{
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = g.getEmailAddress();
		
		InputStream keyStream = new ByteArrayInputStream(g.getPrivateKey());
		PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret",
		          "privatekey", "notasecret");
		String googleAccount = g.getGoogleAccount();
		GoogleCredential credential;
		if(googleAccount != null)
			credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Collections.singletonList(CalendarScopes.CALENDAR))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.setServiceAccountUser(googleAccount)
				.build();
		else credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Collections.singletonList(CalendarScopes.CALENDAR))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.build();
		
		client = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential )
				.setApplicationName("AON SOLUTIONS").build();
		return client;
		
	}
	
	
	//------------------------------------------- CALENDARS
	
	public static CalendarList getCalendars(com.google.api.services.calendar.Calendar calendar) throws IOException{
		return calendar.calendarList().list().execute();
	}
	
	public static Events getEvents(String calId,com.google.api.services.calendar.Calendar calendar) throws IOException{
		return calendar.events().list(calId).execute();
	}
	
	
	/**
	 * getCalendars(), Devuelve la lista de todos los calendarios, de Google
	 * Calendar (del usuario identificado).
	 * @return CalendarList, lista de calendarios de Google Calendar
	 * @throws IOException
	 */
	public static CalendarList getCalendars() throws IOException {
		return client.calendarList().list().execute();
	}
	
	/**
	 * searchCalendars(CalendarList calendars , String dato, int n), Búsqueda 
	 * dicotomica de un calendario en Google Calendar. 
	 * @param calendars, Lista de calendarios de Google Calendar
	 * @param dato, valor con el que se realiza la búsqueda. 
	 * @param n, tamaño de la lista "calendars"
	 * @return Devuelve -1 si no esta en la lista, y la posicion de la lista
	 * si está en la lista.
	 */
	public static int searchCalendars(CalendarList calendars , String dato, int n) {

		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			if (calendars.getItems().get(centro).getSummary().compareTo(dato)== 0) {
				return centro;
			} else if (calendars.getItems().get(centro).getSummary().compareTo(dato)>0) {
				sup = centro - 1;
			} else {
				inf = centro + 1;
			}
		}
		return -1;
	}
	
	/**
	 * addCalendar(Domain company,String domainC), Añade un calendario a 
	 * Google Calendar.
	 * @param company, Dominio y datos de una empresa para la cual se creará 
	 * el calendario.
	 * @param domainC, Dominio para la conexión con la base de datos.
	 * @return Devuelve el calendario añadido.
	 * @throws IOException
	 * @throws SQLException
	 * @throws AonConnectionException 
	 * @throws NamingException 
	 */
	public static Calendar addCalendar(Calendar entry, String email) throws IOException, NamingException{
		Calendar result = client.calendars().insert(entry).execute();
		
		if (email!= null && Utils.isGmail(email)){
			AclRule rule = new AclRule();
			Scope scope = new Scope();
			scope.setType("group");
			scope.setValue(email);
			rule.setRole("reader");
			rule.setScope(scope);
			AclRule createdRule = client.acl().insert(result.getId(), rule).execute();
			System.out.println(createdRule.getId());
			
		}	
		return result;
	}
	
	/**
	 * 
	 * @param company
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 * @throws IOException
	 * @throws NamingException 
	 */
	public static Calendar newCalendar(Domain domain, User user) throws IOException, NamingException{
		String email= DBCalendar.getEnterpriseEmail(domain, user);
		Calendar entry = new Calendar();
		entry.setSummary(domain.getName());
		entry.setDescription(domain.getDescription());
		return addCalendar(entry, email);
	}
	
	
	/**
	 * updateCalendar(Calendar calendar), Modifica un calendario especificado
	 * de Google Calendar.
	 * @param calendar, Calendario a modificar.
	 * @return Devuelve el calendario que s eha modificado.
	 * @throws IOException
	 */
	public static Calendar updateCalendar(Calendar calendar) throws IOException {
		Calendar result = client.calendars().patch(calendar.getId(), calendar)
				.execute();
		return result;
	}
	
	/**
	 * removeCalendar(String calendarId), Borra el Calendario especificado
	 * en Google Calendar.
	 * @param calendarId, Identificador del Calendario en Google Calendar. 
	 * @throws IOException
	 */
	public static void removeCalendar(String calendarId) throws IOException {
		client.calendars().delete(calendarId).execute();
	}
	
	/**
	 * 
	 * @param calendar
	 * @param company
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws AonConnectionException
	 * @throws NamingException 
	 */
	public static Calendar modifyCalendar(Calendar entry,String  email) throws IOException, SQLException, AonConnectionException, NamingException{
		Calendar result=updateCalendar(entry);
		if (email!= null && Utils.isGmail(email)){
			AclRule rule = new AclRule();
			Scope scope = new Scope();
			scope.setType("group");
			scope.setValue(email);
			rule.setRole("reader");
			rule.setScope(scope);
			AclRule createdRule = client.acl().insert(result.getId(), rule).execute();
			System.out.println(createdRule.getId());
		}	
		return result;
	}
	
	/**
	 * 
	 * @param calendar
	 * @param company
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 * @throws IOException
	 * @throws NamingException 
	 */
	public static Calendar modifyCommercialCalendar( Domain domain, User user, CalendarListEntry calendar, String key) throws SQLException, AonConnectionException, IOException, NamingException{
		String email= DBCalendar.getEnterpriseEmail(domain, user);	
		Calendar entry = new Calendar()
				.setSummary(domain.getName())
				.setDescription(domain.getDescription())
				.setId(calendar.getId());
		return modifyCalendar(entry, email);
	}
	
	
	//------------------------------------------- EVENTS

	/**
	 * newEvent(CommercialTracking commercialTracking,String domainC), Crea un 
	 * evento de Google Calendar con los datos de un evento de la BD.
	 * @param commercialTracking, evento de la BD.
	 * @param domainC, Dominio para la conexión con la base de datos.
	 * @return Devuelve un evento de Google Calendar.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Event newEvent(Domain domain, User user, CommercialTracking commercialTracking){
		Event event = new Event();
		
		event.setColorId("9");
		
		//SUMMARY
		Project project = DBCalendar.getProject(domain, user, commercialTracking);
		event.setSummary(project.getName());
		
		// DESCRIPTION/COMMENTS
		CommercialActivity commercialActivity= DBCalendar.getActivity(domain, user, commercialTracking);
		Registry registry= DBCalendar.getPotencialClient(domain, user, commercialTracking);
		String description ="Actividad: "+commercialActivity.getName() 
				+"\nCliente Potencial: "+ registry.getName() + ", "+ registry.getNationality()
				+"\nComentarios: "+commercialTracking.getComments()
				+"\nComercial: "+DBConsults.getUserName(domain, user,commercialTracking.getSeller());
		event.setDescription(description);
		
		// ATTENDEES
		Vector<String> emails = DBCalendar.getSellerEmails(domain, user, commercialTracking);
		String email = DBCalendar.getSellerEmail(domain, user, commercialTracking);
		
		if(email == null ){
			email = "";
			for (String s : emails) {
				if (s!=null && Utils.isGmail(s)){
					if((!s.contains("gmail.com") && email.contains("gmail.com"))
						|| email.equals(""))
						email = s;
				}	
			}
			if(!email.equals(""))DBCalendar.setSellerEmail(domain, user, commercialTracking, email);
		}
		if(email !=null && email!= ""){
			EventAttendee eventAttendee = new EventAttendee();
			List<EventAttendee> list = new LinkedList<EventAttendee>();
			eventAttendee.setEmail(email);
			list.add(eventAttendee);
			event.setAttendees(list);
		}
		
		
		// START DATE
		Date startDate = convertDate(commercialTracking.getDate());
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
		event.setStart(new EventDateTime().setDateTime(start));

		// END DATE
		Date endDate;
		if(commercialTracking.getEndDate()!=null)
			endDate = convertDate(commercialTracking.getEndDate());
		else	endDate = new Date(startDate.getTime() + 3600000);
		DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
		
		event.setEnd(new EventDateTime().setDateTime(end));

		// AON ID & END UPDATE (EXTENDED PROPERTIES)
        Event.ExtendedProperties extendedProperties = new Event.ExtendedProperties();
        Map<String, String> privateExtendedProperties = new HashMap<String, String>();
        privateExtendedProperties.put("aonId", commercialTracking.getId().toString());
        extendedProperties.setPrivate(privateExtendedProperties);
        event.setExtendedProperties(extendedProperties);

		// LOCATION
		if (commercialTracking.getLocation() != null)
			event.setLocation(commercialTracking.getLocation());

		return event;
	}
	
	/**
	 * getEvents(String calendarId), Devuelve la lista de todos los eventos
	 * del calendario de Google Calendar especificado(del usuario identificado).
	 * @param calendarId, Identificador del calendario de Google Calendar.
	 * @return Devuelve la lista de eventos del calendario.
	 * @throws IOException
	 */
	public static Events getEvents(String calendarId) throws IOException {
		return client.events().list(calendarId).execute();
	}

	/**
	 * addEvent(String calendarId, Event event), Añade un evento al calendario
	 * de Google Calendar especificado.
	 * @param calendarId, Identificador del Calendario de Google Calendar.
	 * @param event, Evento a introducir en el calendario.
	 * @return Devuelve el evento introducido en el calendario.
	 * @throws IOException
	 * @throws AonConnectionException 
	 */
	public static Event addEvent(Domain domain, User user, String calendarId, Event event, CommercialTracking ct) throws IOException {
		Event result = client.events().insert(calendarId, event).setSendNotifications(false)
				.execute();
		try {
			Utils.sendNotification(domain, user, result, ct);
		} catch ( GeneralSecurityException | MessagingException | NamingException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	

	/**
	 * (String calendarId, Event event), Modifica un evento dado del calendario de
	 * Google Calendar especificado.
	 * @param calendarId, Identificador del calendario de Google Calendar.
	 * @param event, evento modificado.
	 * @throws IOException
	 */
	public static void updateEvent(String calendarId, Event event)
			throws IOException {
		client.events().update(calendarId, event.getId(), event)
				.execute();
	}

	/**
	 * removeEvent(String calendarId, Event event), Borra el evento especificado,
	 * del calendario dado.
	 * @param calendarId, Identificador del calendario de Google Calendar
	 * @param event, Evento a borrar.
	 * @throws IOException
	 */
	public static void removeEvent(String calendarId, String eventId)
			throws IOException {
		client.events().delete(calendarId, eventId).execute();
	}
	
	public static void removeEvent(com.google.api.services.calendar.Calendar calendar,String calendarId, String eventId)
			throws IOException {
		calendar.events().delete(calendarId, eventId).execute();
	}
	
	
	/**
	 * searchEvents(Events events, int dato, int n), Búsqueda 
	 * dicotomica de un evento de un calendario de Google Calendar.
	 * @param events, Lista de eventos.
	 * @param dato, valor con el que se realiza la búsqueda. 
	 * @param n, tamaño de la lista "events"
	 * @return Devuelve -1 si no esta en la lista, y la posicion de la lista
	 * si está en la lista.
	 */
	public static int searchEvents(Events events, Integer dato, Integer n) {
		String datoStr= dato.toString();
		int centro;
		int inf = 0;
		int sup = n - 1;
		System.out.println("tamañoo!!! : "+n);
		while (n!=0 && inf <= sup) {
			centro = (sup + inf) / 2;
			if (events.getItems().get(centro).getExtendedProperties()
					.getPrivate().get("aonId").equals(datoStr)) {
				return centro;
			} else if (datoStr.compareTo(events.getItems().get(centro).getExtendedProperties()
					.getPrivate().get("aonId"))<0) {
				sup = centro - 1;
			} else{
				inf = centro + 1;
			}
		}
		return -1;
	}
	
	/**
	 * modify(Event event, CommercialTracking eventBD, String domainC, String id),
	 * Actualiza un evento de la base de datos a Google Calendar.
	 * @param event, Evento de Google Calendar.
	 * @param eventBD, Evento de la BD.
	 * @param domainC, Dominio para la conexión con la BD.
	 * @param id,  Identificado del calendario de Google Calendar.
	 * @throws SQLException
	 * @throws IOException
	 * @throws AonConnectionException 
	 */
	public static void modifyEvent(Domain domain, User user, String eventId, CommercialTracking eventBD, String id) throws IOException{
		Event aux = newEvent(domain, user, eventBD);
		aux.setId(eventId);
		updateEvent(id, aux);
	}
	
	
	//------------------------------------------- UTILS
	
	public static void synchronize(Domain domain, User user) throws IOException, GeneralSecurityException, NamingException {
		LinkedList<CommercialTracking> eventsBD= DBCalendar.getCommercialTrackingList(domain, user);// Obtiene todos los eventos(CommercialTracking) de la BD
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
		if(g.getClientId()!=null){
			serviceInitialize(g);
			CalendarList calendars = Quicksort.calendarsSort(getCalendars());		
			int aux=searchCalendars(calendars,domain.getName(),calendars.getItems().size());
			String calendarId;
			if (aux==-1){
				Calendar calendar = newCalendar(domain, user);
				calendarId= calendar.getId();
			}
			else calendarId = calendars.getItems().get(aux).getId();
					
			if (eventsBD !=null && eventsBD.size()>0 ){
				for (CommercialTracking ct : eventsBD) {
					if (isRegular(ct)){
						if (ct.getEventId() == null){
							Event event=addEvent(domain, user, calendarId,newEvent(domain, user, ct),ct);
							DBCalendar.updateEventId(domain, user, ct.getId(), event.getId());// añadir el id del evento a la base de datos!!!
						}
						else{
							modifyEvent(domain, user, ct.getEventId(), ct,calendarId);
						}				
					}
				}
			}	
		}								
	}
	
	
	// CREA CALENDARIOS PARA TODOS LOS DOMINIOS.
	public static void synchronize() throws IOException, GeneralSecurityException, NamingException{
		// Obtiene todos los dominios de la BD.
		Map<String, String> domains = DBSync.getDomains();
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		User user = new User().setLogin(""); //TODO GET USER
		
		// Ordena los dominios por orden alfabetico.
		List<String> list = new ArrayList<String>(domains.keySet());
		Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
		
		// Recorre todos los dominios de la BD.
		for (String domainName : list){
			Domain domain = AON.getDomain(domainName, domainMap.get(domainName), user.getLogin());
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
			if(g.getClientId() != null){
				serviceInitialize(g);
				CalendarList calendars = Quicksort.calendarsSort(getCalendars());		
				int aux=searchCalendars(calendars,domain.getName(),calendars.getItems().size());
				String calendarId;
				if (aux==-1){
					Calendar calendar = newCalendar(domain, user);
					calendarId= calendar.getId();
				}
				else calendarId = calendars.getItems().get(aux).getId();
				
				LinkedList<CommercialTracking> eventsBD = DBCalendar.getCommercialTrackingList(domain, user);
				
				if (eventsBD !=null && eventsBD.size()>0 ){
					for (CommercialTracking ct : eventsBD) {
						if (isRegular(ct)){
							if (ct.getEventId() == null){
								Event event=addEvent(domain, user, calendarId,newEvent(domain, user, ct),ct);
								DBCalendar.updateEventId(domain, user,ct.getId(), event.getId());// añadir el id del evento a la base de datos!!!
							}
							else{
								modifyEvent(domain, user, ct.getEventId(), ct,calendarId);
							}				
						}
					}
				}	
			} 
		}
	}
	
	// CREA CALENDARIOS PARA TODOS LOS DOMINIOS PADRE.
	public static void synchronize2(User user) throws IOException, KeyStoreException, GeneralSecurityException, NamingException {
		Map<String, String> domains= DBSync.getDomains();//obtiene todos los dominios de la BD
		Map<String, Integer> domainMap = DBSync.getDomainMap();

		Hashtable<String,String> schemas = new Hashtable<String, String>();
		
		for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
			if (!SearchFiles.esta(schemas,domains.get(key))){
				schemas.put(domains.get(key), key);
			}
		}
		Vector<String> domains2 = new Vector<String>();
		for (String sch : schemas.keySet()){
			Domain d = AON.getDomain(schemas.get(sch),domainMap.get(schemas.get(sch)), user.getLogin());
			domains2.addAll(DBConsults.getParentName(d, user));
		}
		for(String key : domains2) { // recorre todos los dominios de la BD
			Domain d = AON.getDomain(key, domainMap.get(key), user.getLogin());
			LinkedList<CommercialTracking> eventsBD= DBCalendar.getCommercialTrackingList(d, user);// Obtiene todos los eventos(CommercialTracking) de la BD
			DomainGserviceaccount g = DBConsults.getServiceAccount(key, d.getId());
			if(g.getClientId()!=null){
				serviceInitialize(g);
				CalendarList calendars = Quicksort.calendarsSort(getCalendars());		
				int aux=searchCalendars(calendars,key,calendars.getItems().size());
				String calendarId;
				if (aux==-1){
					Calendar calendar = newCalendar(d,user);
					calendarId= calendar.getId();
				}
				else calendarId = calendars.getItems().get(aux).getId();
					
				if (eventsBD !=null && eventsBD.size()>0 ){
					for (CommercialTracking ct : eventsBD) {
						if (isRegular(ct)){
							if (ct.getEventId() == null){
								Event event=addEvent(d, user, calendarId,newEvent(d, user, ct),ct);
								DBCalendar.updateEventId(d, user, ct.getId(), event.getId());// añadir el id del evento a la base de datos!!!
							}
							else{
								modifyEvent(d, user, ct.getEventId(), ct,calendarId);
							}				
						}
					}
				}	
			}						
		}	
	}
	
	
	
	/**
	 * 
	 * @param vector
	 * @param domain
	 * @return
	 * @throws AonConnectionException
	 */
	public static Integer ejecutado(Vector<String> vector, String domain) throws AonConnectionException{
		int centro;
		int inf=0;
		int sup= vector.size()-1;
		while (inf <= sup){
			centro= (sup + inf)/2;
			if(domain.compareTo(vector.get(centro))==0)
				return -1;
			if (domain.compareTo(vector.get(centro))<0)
				sup=centro-1;
			if (domain.compareTo(vector.get(centro))> 0)
				inf= centro+1;
		}
		return inf;			
	}
	
	public static boolean isRegular(CommercialTracking c){
		System.out.println(c.getId()+" : "+c.getDate()+" - "+c.getEndDate()+" - "+c.getAllday());
		
		if(c.getDate()!=null && (c.getEndDate()!= null || c.getAllday())){
			return (c.getAllday() || c.getDate().before(c.getEndDate()) );
		}
		return false;
	}
	
	/**
	 * convertDate(Date b), Convierte la fecha de la BD en un formato compatible
	 * con el tipo de fecha de Google Calendar. 
	 * @param b, La fecha a cambiar de formato
	 * @return Devuelve la misma fecha introducido, pero con un formato compatible
	 * con Google Calendar.
	 */
	@SuppressWarnings("deprecation")
	public static Date convertDate(Date b) {
		Date a = new Date();

		a.setDate(b.getDate());
		a.setHours(b.getHours());
		a.setMinutes(b.getMinutes());
		a.setMonth(b.getMonth());
		a.setSeconds(b.getSeconds());
		a.setTime(b.getTime());
		a.setYear(b.getYear());

		return a;
	}
	
	//------------------------------------------- MAIN
	
	public static void main(String[] args) throws GeneralSecurityException,
			IOException, ServletException, SQLException, AonConnectionException, NamingException {
	
	}
}
