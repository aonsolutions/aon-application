package net.aonsolutions.aon.tbai;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;

public class TbaiMain {

	public static void json_to_invoice(InputStream is) {

		try {
			//GET JSON DATA
			JsonObject json = Json.createReader(is).readObject();
			
			String date = 			(json.containsKey("date")) 		  ? json.getString("date") 					: null;
			JsonArray comments = 	(json.containsKey("comments"))	  ? json.getJsonArray("comments")			: null;
			JsonObject receiver_o = (json.containsKey("receiver")) 	  ? json.getJsonObject("receiver") 			: null;
			
			JsonObject address_o = 	
			(receiver_o != null && receiver_o.containsKey("address"))  	? receiver_o.getJsonObject("address") 	: null;	
			
			String rec_zip = 		
			(address_o != null && address_o.containsKey("zip")) 	   	? address_o.getString("zip") 		 	: null;
			
			String rec_address = 	
			(address_o != null && address_o.containsKey("address"))    	? address_o.getString("address") 	 	: null;
			
			String rec_province = 	
			(address_o != null && address_o.containsKey("province"))   	? address_o.getString("province") 	 	: null;
			
			String rec_city = 		
			(address_o != null && address_o.containsKey("city")) 	   	? address_o.getString("city") 		 	: null;
			
			String rec_document = 			
			(receiver_o != null && receiver_o.containsKey("document")) 	? receiver_o.getString("document") 		: null;
			
			String rec_name = 				
			(receiver_o != null && receiver_o.containsKey("name"))  	? receiver_o.getString("name") 			: null;	
			
			String rec_total_address = 		"ALL ADDRESS"; 
			
			JsonArray taxes = 				(json.containsKey("taxes")) 		? 		json.getJsonArray("taxes") 					: null;		
			String type = 					(json.containsKey("type")) 			? 		json.getString("type") 						: null;	
			String reference = 				(json.containsKey("reference")) 	? 		json.getString("reference") 				: null;
			String number = 				(json.containsKey("number")) 		? 		json.getString("number") 					: null;
			JsonNumber total = 				(json.containsKey("total")) 		? 		json.getJsonNumber("total") 				: null;
			
			JsonObject sender_o = 			(json.containsKey("sender")) 		? 		json.getJsonObject("sender")				: null;
			JsonObject sender_address_o = 	
			(sender_o != null && sender_o.containsKey("address")) 				? 		sender_o.getJsonObject("address") 			: null;
			
			String sen_zip = 				
			(sender_address_o != null && sender_address_o.containsKey("zip")) 		? 		sender_address_o.getString("zip")		: null;
			
			String sen_country = 			
			(sender_address_o != null && sender_address_o.containsKey("country")) 	? 		sender_address_o.getString("country") 	: null;
			
			String sen_address = 			
			(sender_address_o != null && sender_address_o.containsKey("address")) 	? 		sender_address_o.getString("address") 	: null;
			
			String sen_province = 			
			(sender_address_o != null && sender_address_o.containsKey("province")) 	? 		sender_address_o.getString("province") 	: null;
		
			String sen_city = 				
			(sender_address_o != null && sender_address_o.containsKey("city")) 		? 		sender_address_o.getString("city") 		: null;
			
			
			String sen_total_address = 		"ALL ADDRESS"; 
			
			String sen_document = 	(sender_o != null && sender_o.containsKey("document")) 	? 	sender_o.getString("document")		: null;
			String sen_name = 		(sender_o != null && sender_o.containsKey("name")) 		? 	sender_o.getString("name")			: null;
			
			String series = 				(json.containsKey("series")) 		? 		json.getString("series") 					: null;	
			JsonArray details = 			(json.containsKey("details")) 		? 		json.getJsonArray("details") 				: null;
			JsonNumber id =					(json.containsKey("id")) 			? 		json.getJsonNumber("id") 					: null;
			String category =  				(json.containsKey("category")) 		? 		json.getString("category") 					: null;
			JsonArray finances = 			(json.containsKey("finances")) 		? 		json.getJsonArray("finances") 				: null;
			String transaction = 			(json.containsKey("transaction")) 	? 		json.getString("transaction") 				: null;
			String status = 				(json.containsKey("status")) 		? 		json.getString("status") 					: null;
			
			
			JsonObject suplidos_o = 		(json.containsKey("suplidos")) 		? 		json.getJsonObject("suplidos") 				: null;
			JsonNumber sup_total = 			
			(suplidos_o != null && suplidos_o.containsKey("suplidos")) 			? 		suplidos_o.getJsonNumber("total") 			: null;
			
			boolean sup_active = 			
			(suplidos_o != null && suplidos_o.containsKey("active")) 			? 		suplidos_o.getBoolean("active")   			: null;
			
			String sup_description = 		
			(suplidos_o != null && suplidos_o.containsKey("description")) 		? 		suplidos_o.getString("description") 		: null;

			//CREATE EMISION OBJECT owo
			
		} catch (Exception e) {e.printStackTrace();}
	}

}
