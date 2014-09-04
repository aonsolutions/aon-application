package com.code.aon.google.apis.calendar;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.drive.model.File;

public class View {

	public static void event(Event e){
		System.out.println("> EVENTO "+e.getSummary());
		System.out.println("   --> ID:"+e.getId());
		System.out.println("   --> F.inicio:"+e.getStart());
		System.out.println("   --> F.fin:"+e.getEnd());
		System.out.println("");
	}
	
	public static void calendarListEntry(CalendarListEntry cle){
		System.out.println("> CALENDARIO "+cle.getSummary());
		System.out.println("   --> ID:"+cle.getId());
	}
	
	public static void calendar(Calendar c){
		System.out.println("> CALENDARIO "+c.getSummary());
		System.out.println("   --> ID:"+c.getId());

		System.out.println("");
	}
	
	public static void eventOut(Event e){
		System.out.println(e.getId());
	}
	
	public static void domain(String d){
		System.out.println("DOMAIN --> "+d);
		System.out.println();
	}
	
	public static void delete(Event e){
		System.out.println("> Evento eliminado:");
		System.out.println("   --> TITLE:"+e.getSummary());
		System.out.println("   --> ID:"+e.getId());
		System.out.println("");
	}
	
	public static void error1(){
		System.out.println("ERROR:El tipo de búsqueda introducido no es correcto.");
	}
	
	public static void error2(){
		System.out.println("ERROR:El tipo de borrado introducido no es correcto.");
	}
	
	public static void error3(){
		System.out.println("ERROR: No se ha especificado el id del archivo a eliminar.");
	}
	
	public static void error4(){
		System.out.println("ERROR: No se ha especificado el email.");
	}
	
}
