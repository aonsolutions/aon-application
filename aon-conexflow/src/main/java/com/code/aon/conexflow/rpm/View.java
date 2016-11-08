package com.code.aon.conexflow.rpm;

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
	
	public static void preauthorized(String projectName, Integer projectId, String msg, Double amount){
		System.out.println("PRE-AUTORIZACION --> " + projectId + " - " + projectName + " - " + msg + " - Importe = " + amount);
		
	}
}
