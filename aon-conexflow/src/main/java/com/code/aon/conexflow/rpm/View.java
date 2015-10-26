package com.code.aon.conexflow.rpm;

import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;

public class View {

	public static void projectReservation(){

	}
	
	public static void domain(String domain){
		System.out.println("DOMAIN --> " + domain);
		System.out.println();
	}
	
	public static void hotel(String hotel){
		System.out.println("HOTEL --> " + hotel);
		System.out.println();
	}
	
	public static void error1(){
		System.out.println("ERROR");
	}
	
	public static void preauthorized(String projectName, ProjectReservationRecord prr, String errorMsg, Double amount){
		System.out.println(prr.getProject() + " - " + projectName + " - " + errorMsg + " - Importe = " + amount);
		
	}
}
