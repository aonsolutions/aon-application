package com.code.aon.hhg.webservice.rpm;

import com.code.aon.hhg.webservice.dialog.Response;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;

public class View {
	
	public static void response(Response response){
		System.out.println(response.getResult().getType());
		if(response.getResult().getType().equals("error"))
			System.out.println(
				"Error " + response.getResult().getPayload().getCode()
				+ ": " + response.getResult().getPayload().getMessage());
		else System.out.println("Nonce: " + response.getResult().getPayload().getNonce());
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
