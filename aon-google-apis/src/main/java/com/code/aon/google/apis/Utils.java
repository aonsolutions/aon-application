package com.code.aon.google.apis;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.google.apis.jooq.DBCalendar;
import com.code.aon.google.apis.jooq.DBConsults;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.gmail.Gmail;


public class Utils{
	
	public static class PasswordGenerator {

		public static final String NUMEROS = "0123456789";

		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

		//public static final String ESPECIALES = "ñÑ";

		//
		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}

		public static String getPassword() {
			return getPassword(8);
		}

		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}

		public static String getPassword(String key, int length) {
			String pswd = "";

			for (int i = 0; i < length; i++) {
				pswd += key.charAt((int) (Math.random() * key.length()));
			}

			return pswd;
		}
	}

	/************************************ QUICKSORT *********************************/
	private  static List list;
	private static int number;
	
	public static <T extends Comparable<? super T>> List<T> sort(List<T> l){
		list = l;
		number = l.size();
		quicksort(0,number - 1);
		
		Collections.sort(l);
		return list;
	}

	static void quicksort(int low, int high){
		int i = low, j = high;
		/*String pivot = list.get(low + (high - low) / 2).getTitle();
		while (i <= j) {
			while ( list.get(i).getTitle().compareTo(pivot)<0) {
				i++;
			}
			while (list.get(j).getTitle().compareTo(pivot)>0) {
				j--;
			}
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}*/
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}


	
	static void exchange(int i, int j) {
		Object aux = list.get(i);
		list.set(i, list.get(j));
		list.set(j, aux);
	}
	
	/******************************** SEARCH ************************************/
	
	public static <T extends Comparable<? super T>> int searchFiles(List<T> l , String dato, int n) {
		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			/*if (l.get(centro).getTitle().compareTo(dato)== 0) {
				return centro;
			} else if (l.get(centro).getTitle().compareTo(dato)>0) {
				sup = centro - 1;
			} else {
				inf = centro + 1;
			}*/
		}
		return -1;
	}

	/******************************* CHECK GMAIL *******************************/
	
	public static Boolean isGmail(String email) throws NamingException{ 
		Integer pos= email.indexOf("@");
		String username = email.substring(0, pos);
		String hostname = email.substring(pos+1);
		Attribute attr = doLookup(hostname);
		int i=0;
		if (attr!=null){
			while(i<attr.size()){
				String a = (String) attr.get(i);
				if(StringUtils.containsIgnoreCase(a, "google.com") || StringUtils.containsIgnoreCase(a, "googlemail.com")){
					return true;
				}
				i++;
			}
		} 
		return false;	  
	}
	  
	  static Attribute doLookup( String hostName ) throws NamingException {
	    Hashtable<String, String> env = new Hashtable<String, String>();
	    env.put("java.naming.factory.initial",
	            "com.sun.jndi.dns.DnsContextFactory");
	    DirContext ictx = new InitialDirContext( env );
	    Attributes attrs = 
	       ictx.getAttributes( hostName, new String[] { "MX" });
	    Attribute attr = attrs.get( "MX" );
	   return attr;
	  }
	  
	  
	  public static String toDay(Integer i){
			String day="";
			switch (i) {
			case 1: day = "Lun.";break;
			case 2: day = "Mar.";break;
			case 3: day = "Mie.";break;
			case 4: day = "Jue.";break;
			case 5: day = "Vie.";break;
			case 6: day = "Sab.";break;
			case 7: day = "Dom.";break;
			default: break;
			}
			return day;
		}
		
		public static String toMonth(String month){
			String month2="";
			switch (month) {
			case "01": month2 = "Ene.";break;
			case "02": month2 = "Feb.";break;
			case "03": month2 = "Mar.";break;
			case "04": month2 = "Abr.";break;
			case "05": month2 = "May.";break;
			case "06": month2 = "Jun.";break;
			case "07": month2 = "Jul.";break;
			case "08": month2 = "Ago.";break;
			case "09": month2 = "Sep.";break;
			case "10": month2 = "Oct.";break;
			case "11": month2 = "Nov.";break;
			case "12": month2 = "Dic.";break;
			default: break;
			}
			return month2;
		}
		
		public static void sendNotification(Domain domain, User user, Event event, CommercialTracking ct) throws IOException, GeneralSecurityException, MessagingException, NamingException{
			Vector<String> emails = DBCalendar.getSellerEmails(domain, user, ct);
			String email = DBCalendar.getSellerEmail(domain, user, ct);
			if(email != null){
				String emls = "";
				for(String e : emails){
					if(!e.equals(email) && Utils.isGmail(e)){
						String url = domain.getName()+ "/googleMail/"
		                	+ "?registry=" + Integer.toString(ct.getSeller())
		                	+ "&email=" + email
		                	+ "&domain=" +domain.getName()
		                	+ "&type=" + e;
						emls = emls +"<p style=\"color: #888;padding-left:10px;\">"+e+" <a href=\""+url+"\"> cambiar </a></p>";
					}
				}
				String url2 =  domain.getName()+ "/googleMail/"
						+ "?registry=" + Integer.toString(ct.getSeller())
						+ "&email=" + email
						+ "&domain=" + domain.getName()
						+ "&type=" + "baja";
				Date dt = new Date(event.getStart().getDateTime().getValue());
				Integer day2 = dt.getDay();
				String date = event.getStart().getDateTime().toString();
				String day = date.substring(8, 10);
				String month = date.substring(5, 7);
				String year = date. substring(0, 4);
				String hour = date.substring(11,16);
				String date3 = toDay(day2) + " "+day+" de "+toMonth(month)+", "+hour; 
				
				String date4 ="";
				if(event.getEnd() != null){
					Date dt2 = new Date(event.getEnd().getDateTime().getValue());
					Integer day4 = dt2.getDay();
					String date2 = event.getEnd().getDateTime().toString();
					String day3 = date2.substring(8,10);
					String month2 = date2.substring(5,7);
					String year2 = date2.substring(0,4);
					String hour2 = date2.substring(11,16);
					date4 =" To "+ toDay(day4) + " "+day3+" de "+toMonth(month2)+", "+hour2; 
				}	
				String location = "-";
				if(event.getLocation()!= null) location = event.getLocation();
				String msg = 
				"<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>"
				+"<table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'>"
				+"<tbody><tr><td style='width: 15%;vertical-align: top;padding: 21px;border: 1px solid #E5E5E5;background-color: #F6F6F6;'>"
				
					+"<div><img src=\"http://www.aonsolutions.es/wp-content/themes/aonsolutions/img/h_logo.gif\" width=\"100%\"></div>"
					+"<div style='background: url(\"https://ssl.gstatic.com/ui/v1/icons/mail/smart_mail_conv_icons.png\") no-repeat scroll 0px 0px transparent;height: 80px;position: relative;width: 70px;'>"
							+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color: #FFF;font-size: 11px;top: 75px;width: 70px;text-align: center;position: absolute;font-weight: bold;'>"+toDay(day2)+"</span></p>"
							+ "&nbsp;&nbsp;&nbsp;&nbsp;<span style='color: #222;font-size: 200%;top: 100px;width: 70px;text-align: center;position: absolute;font-weight: bold;'>"+day+"</span>"
							+ "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color: #222;font-size: 11px;top: 130px;width: 70px;text-align: center;position: absolute;font-weight: bold;'>"+toMonth(month)+"</span>"
					+"</div></td>"
					+"<td style='padding: 21px;vertical-align: top;'>"
						+"<div style='color: #222;font-size: 140%;margin-bottom: 2px;'>"+event.getSummary()+"</div>"
						+"<div style='color: #999;margin-bottom: 14px;'><a style='text-decoration: none;color: #15C;' href='"+event.getHtmlLink()+"' target='_blank'>Míralo en Google Calendar</a></div>"
						+ "<table style='table-layout: fixed;border-collapse: collapse;'><tbody>"
							+ "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Cuándo</td>"
								+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+date3+date4+"</td></tr>"
							+ "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Ubicación</td>"
								+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+location+"</td></tr>"
					+ "</tbody></table></td>"
			
				+"</tr></tbody></table>"

				+"<p></p><table><tbody>"
					+"<tr><td style=\"background-color: #F6F6F6;color: #888;border: 1px solid #CCC;font-family: Arial,sans-serif;font-size: 11px;  \">"
						+"<p style=\"color: #888;padding-left:5px;\">Invitación de <a href=\"https://www.google.com/calendar/\" target=\"_blank\">Google Calendar</a></p>"
						+"<p style=\"color: #888;padding-left:5px;\">Recibes este mensaje de correo electrónico en la dirección <a href=\"mailto:"+email+"\" target=\"_blank\">"+email+"</a> de la cuenta porque estás suscrito para recibir invitaciones del calendario "+domain.getName()+".</p>"
						+"<p style=\"color: #888;padding-left:5px;\">Si deseas cambiar el correo electrónico con el que compartir los eventos comerciales de la aplicación elige una de las presentadas a continuación.Si no dispones de ninguno añade uno nuevo en la aplicación con el atributo comercial activo. </p>"
						+emls
						+"<p style=\"color: #888;padding-left:5px;\">Si deseas dejar de recibir estas notificaciones, pulsa <a href=\""+url2+"\" target=\"_blank\"> aquí</a>  para date de baja en servicio de Google Calendar de la aplciación.</p>"
					+ "</tr></tbody></table>"
					
				+ "</div></div>"
				;
				DomainGserviceaccount g = DBConsults.getServiceAccount(domain, new User().setLogin(""));
				Gmail service = GmailUtils.serviceInitialize(g);
			
				MimeMessage emailMessage = GmailUtils.createEmail(email, g.getGoogleAccount(), "Google Calendar", msg);
				GmailUtils.sendMessage(service, g.getGoogleAccount(), emailMessage);
			}
		}
	  
	 
}
