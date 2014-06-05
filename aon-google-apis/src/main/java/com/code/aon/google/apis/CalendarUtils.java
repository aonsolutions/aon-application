package com.code.aon.google.apis;


import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getHttpTransport;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getJsonFactory;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.newFlow;
import static com.code.aon.google.apis.DatabaseSync.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialActivity;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialTracking;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Project;
import com.esferalia.aon.google.sql.AbstractSQL.Registry;
import com.google.api.client.auth.oauth2.Credential;
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
	 * @class View, escribe por pantalla calendarios y eventos
	 * @author aibanez
	 */
	static class View {

		static void header(String name) {
			System.out.println();
			System.out.println("============== " + name + " ==============");
			System.out.println();
		}

		static void display(CalendarList feed) {
			if (feed.getItems() != null) {
				for (CalendarListEntry entry : feed.getItems()) {
					System.out.println();
					System.out
							.println("-----------------------------------------------");
					display(entry);
				}
			}
		}

		static void display(Events feed) {
			if (feed.getItems() != null) {
				for (Event entry : feed.getItems()) {
					System.out.println();
					System.out
							.println("-----------------------------------------------");
					display(entry);
				}
			}
		}

		static void display(CalendarListEntry entry) {
			System.out.println("ID: " + entry.getId());
			System.out.println("Summary: " + entry.getSummary());
			if (entry.getDescription() != null) {
				System.out.println("Description: " + entry.getDescription());
			}
		}

		static void display(Calendar entry) {
			System.out.println("ID: " + entry.getId());
			System.out.println("Summary: " + entry.getSummary());
			if (entry.getDescription() != null) {
				System.out.println("Description: " + entry.getDescription());
			}
		}

		static void display(Event event) {
			System.out.println("ID: " + event.getId());
			System.out.println("Summary: " + event.getSummary());
			System.out.println("AonId: "+event.getExtendedProperties().getPrivate().get("aonId"));
			if (event.getStart() != null) {
				System.out.println("Start Time: " + event.getStart());
			}
			if (event.getEnd() != null) {
				System.out.println("End Time: " + event.getEnd());
			}
		}
	}
	
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

	private static Credential credential;	
	private static com.google.api.services.calendar.Calendar client;	
	private static Pattern GMAIL = Pattern.compile("(\\b[A-Z0-9\\._%+-]+@gmail.com)", Pattern.CASE_INSENSITIVE);
	
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
		credential = newFlow().loadCredential(getPrincipalShortName(req));
		client = new com.google.api.services.calendar.Calendar.Builder(
				getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
	}
	
	
	public static void serviceInitialize(String domain) throws KeyStoreException, IOException, GeneralSecurityException, SQLException{
		
		DomainGserviceaccount dgserviceaccount= getServiceAccount(domain);
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = dgserviceaccount.getEmailAddress();

		InputStream keyStream = dgserviceaccount.getPrivateKey();
		PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret",
		          "privatekey", "notasecret");
		
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Collections.singletonList(CalendarScopes.CALENDAR))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey).build();
		
		client = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential )
				.setApplicationName("AON SOLUTIONS").build();
		
	}
	
	
	//------------------------------------------- CALENDARS
	
	/**
	 * showCalendars(), Muestra por pantalla todos los calendarios que el usuario
	 *  conectado a la cuenta de google tiene en Google Calendar.
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void showCalendars() throws IOException, ServletException {
		View.header("Show Calendars");
		CalendarList feed = client.calendarList().list().execute();
		View.display(feed);
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
	 */
	public static Calendar addCalendar(Calendar entry, String email) throws IOException, SQLException, AonConnectionException{
		View.header("Add Calendar");
		Calendar result = client.calendars().insert(entry).execute();
		email="aibanezdegau004@gmail.com";
		if (email!= null && isGmail(email)){
			AclRule rule = new AclRule();
			Scope scope = new Scope();
			scope.setType("group");
			scope.setValue(email);
			rule.setRole("reader");
			rule.setScope(scope);
			AclRule createdRule = client.acl().insert(result.getId(), rule).execute();
			System.out.println(createdRule.getId());
		}	
		View.display(result);
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
	 */
	public static Calendar newCalendar(Domain company, String key) throws SQLException, AonConnectionException, IOException{
		String email= getEnterpriseEmail(company.getId(),key);
		Calendar entry = new Calendar();
		entry.setSummary(company.getName());
		entry.setDescription(company.getDescription());
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
		View.header("Update Calendar");
		Calendar result = client.calendars().patch(calendar.getId(), calendar)
				.execute();
		View.display(result);
		return result;
	}
	
	/**
	 * removeCalendar(String calendarId), Borra el Calendario especificado
	 * en Google Calendar.
	 * @param calendarId, Identificador del Calendario en Google Calendar. 
	 * @throws IOException
	 */
	public static void removeCalendar(String calendarId) throws IOException {
		View.header("Delete Calendar");
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
	 */
	public static Calendar modifyCalendar(Calendar entry,String  email) throws IOException, SQLException, AonConnectionException{

		Calendar result=updateCalendar(entry);
		if (email!= null && isGmail(email)){
			email="aibanezdegau004@gmail.com";
			AclRule rule = new AclRule();
			Scope scope = new Scope();
			scope.setType("group");
			scope.setValue(email);
			rule.setRole("reader");
			rule.setScope(scope);
			AclRule createdRule = client.acl().insert(result.getId(), rule).execute();
			System.out.println(createdRule.getId());
		}	
		View.display(result);
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
	 */
	public static Calendar modifyCommercialCalendar(CalendarListEntry calendar, Domain company, String key) throws SQLException, AonConnectionException, IOException{
		String email= getEnterpriseEmail(company.getId(),key);	
		Calendar entry = new Calendar();
		entry.setSummary(company.getName());
		entry.setDescription(company.getDescription());
		entry.setId(calendar.getId());
		return modifyCalendar(entry, email);
	}
	
	
	//------------------------------------------- EVENTS
	
	/**
	 * showEvents(String calendarId), Muestra por pantalla todos los eventos 
	 * del calendario indicado de Google Calendar.
	 * @param calendarId, Identificador del calendario de Google Calendar.
	 * @throws IOException
	 */
	public static void showEvents(String calendarId) throws IOException {
		View.header("Show Events");
		Events feed = client.events().list(calendarId).execute();

		View.display(feed);
	}

	/**
	 * newEvent(CommercialTracking commercialTracking,String domainC), Crea un 
	 * evento de Google Calendar con los datos de un evento de la BD.
	 * @param commercialTracking, evento de la BD.
	 * @param domainC, Dominio para la conexión con la base de datos.
	 * @return Devuelve un evento de Google Calendar.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Event newEvent(CommercialTracking commercialTracking, String key) throws SQLException, AonConnectionException {
		Event event = new Event();
		
		event.setColorId("9");
		
		//SUMMARY
		Project project = getProject(commercialTracking,key);
		event.setSummary(project.getName());
		
		// DESCRIPTION/COMMENTS
		CommercialActivity commercialActivity=getActivity(commercialTracking,key);
		Registry registry= getPotencialClient(commercialTracking,key);
		String description ="Actividad: "+commercialActivity.getName() 
				+"\nCliente Potencial: "+ registry.getName() + ", "+ registry.getNationality()
				+"\nComentarios: "+commercialTracking.getComments();
		event.setDescription(description);
		
		// ATTENDEES
		String email = getSellerEmail(commercialTracking,key);
		email="ibznav@gmail.com";
		if (email!=null && isGmail(email)){
			
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
	 */
	public static Event addEvent(String calendarId, Event event)
			throws IOException {
		View.header("Add Event");
		Event result = client.events().insert(calendarId, event)
				.execute();
		View.display(result);
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
		View.header("Update Event");
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
		View.header("Delete Event");
		client.events().delete(calendarId, eventId).execute();
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
	public static int searchEvents(Events events, int dato, int n) {

		int centro;
		int inf = 0;
		int sup = n - 1;
		System.out.println("tamañoo!!! : "+n);
		while (n!=0 && inf <= sup) {
			centro = (sup + inf) / 2;
			if (Integer.valueOf(events.getItems().get(centro).getExtendedProperties()
					.getPrivate().get("aonId"))== dato) {
				return centro;
			} else if (dato < Integer.valueOf(events.getItems().get(centro).getExtendedProperties()
					.getPrivate().get("aonId"))) {
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
	public static void modifyEvent(Event event, CommercialTracking eventBD, String id,String key) throws SQLException, IOException, AonConnectionException {
		Event aux = newEvent(eventBD,key);
		aux.setId(event.getId());
		updateEvent(id, aux);
	}
	
	
	//------------------------------------------- UTILS
	
	/**
	 * synchronize(String domainC), Sincronización con la base da datos, crea 
	 * un calendario para cada empresa (si no esta creado) y añade todos los eventos
	 * de la base de datos para cada calendario. 
	 * @param domainC, Dominio para la conexión con la base de datos.
	 * @throws IOException
	 * @throws SQLException
	 * @throws AonConnectionException 
	 * @throws GeneralSecurityException 
	 * @throws KeyStoreException 
	 */
	public static void synchronize() throws IOException, SQLException, AonConnectionException, KeyStoreException, GeneralSecurityException {
		
		Map<String, String> domains=getDomains();//obtiene todos los dominios de la BD
		for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
			Vector<Domain> companies = getDomain(key);//Obtiene todos los dominios del dominio padre
			Map<Integer,Vector<CommercialTracking>> map= getCommercialTrackingAll(key);// Obtiene todos los eventos(CommercialTracking) de la BD
			for(int j=0;j<companies.size();j++){
				serviceInitialize(companies.get(j).getName());
				CalendarList calendars = Quicksort.calendarsSort(getCalendars());		
				Vector<CommercialTracking> eventsBD = map.get(companies.get(j).getId());
				if (eventsBD !=null && eventsBD.size()>0){
					int aux=searchCalendars(calendars,companies.get(j).getName(),calendars.getItems().size());
					if (aux==-1){
						Calendar calendar = newCalendar(companies.get(j),key);
						int k=0;
						while( k<eventsBD.size()){		
							CommercialTracking commercialTracking = eventsBD.get(k);
							System.out.println("		"+commercialTracking.getId());
							addEvent(calendar.getId(),newEvent(commercialTracking,key));
							k++;
						}
					}
					else{
						modifyCommercialCalendar(calendars.getItems().get(aux),companies.get(j),key);
						Events events = Quicksort.eventsSort(getEvents(calendars.getItems().get(aux).getId()));
						int k=0;
						while(k<eventsBD.size()){		
							CommercialTracking commercialTracking = eventsBD.get(k);
							int aux2 = searchEvents(events, commercialTracking.getId(), events.getItems().size());
							if (aux2 == -1) {
								addEvent(calendars.getItems().get(aux).getId(),newEvent(commercialTracking,key));
							}
							else{
								modifyEvent(events.getItems().get(aux2), commercialTracking,calendars.getItems().get(aux).getId(),key);
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
	
	/**
	 * isGmail(String email), Comprueba si el email introducido corresponde a
	 * Gmail o no.
	 * @param email, cuenta de correo electronico.
	 * @return true sii es un email de Gmail.
	 */
	private static boolean isGmail(String email){
		Matcher matcher = GMAIL.matcher(email);
		boolean isGMail = matcher.find();
		//System.out.println(matcher.group(1));
		return isGMail;
	}
	
	
	//------------------------------------------- MAIN
	
	/**
	 * main(String[] args), programa principal, que se conecta a Google desde
	 * una cuenta de servicio, para luego crear todos los calendarios y eventos
	 * de la BD.
	 * @param args
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws ServletException
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static void main(String[] args) throws GeneralSecurityException,
			IOException, ServletException, SQLException, AonConnectionException {
		
/*** ELIMINAR TODOS LOS CALENDARIOS!	
	
		showCalendars();
		CalendarList calendars = getCalendars();
		int a=0;
		while(!calendars.isEmpty()){
			Events events = getEvents(calendars.getItems().get(a).getId());
			if (events.getItems().size()!= 0 ){
				for(int b = 0; b< events.getItems().size();b++){
					removeEvent(calendars.getItems().get(a).getId(), events.getItems().get(b).getId());
				}
			}
			
			removeCalendar(calendars.getItems().get(a).getId());
			a++;
		}
/**/		
		
		
		
		synchronize();
	}





}
