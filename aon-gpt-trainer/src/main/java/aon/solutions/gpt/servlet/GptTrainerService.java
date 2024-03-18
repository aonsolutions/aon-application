package aon.solutions.gpt.servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.SECURITY;


import aon.solutions.DatabaseSchemaString;


public class GptTrainerService {
	

    public static void main(String[] args) {
    	
    	Option chatbotOption = Option.builder("c").hasArgs().required().longOpt("chatbot").argName("name")
    			.desc("A chatbot name ; must match the chatbot name as it is stored in the chatbots of GPT Trainer").build();
    	Option queryOption = Option.builder("q").hasArgs().required().longOpt("query").argName("name")
    			.desc("A query  ; ask GPT Trainer about AON database information").build();
    	Options options = new Options();
    	options.addOption(chatbotOption);
    	options.addOption(queryOption);

    	
    	CommandLine commandLine = null;
    	
    	try {
    	    CommandLineParser parser = new DefaultParser();
    	    commandLine = parser.parse(options, args);
    	} catch (ParseException e) {
    	    // oops, something went wrong
    	    System.out.println("Error: " + e.getLocalizedMessage());
    	    new HelpFormatter().printHelp(DatabaseSchemaString.class.getSimpleName(), options);
    	    return;
    	}

    	String chatbotName = commandLine.getOptionValue(chatbotOption.getLongOpt());
    	String query = commandLine.getOptionValue(queryOption.getLongOpt());

    	
    	String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcwOTExODY1OCwianRpIjoiMDNmNDExM2EtZjkwYS00MTAyLTgwZTYtMjA4YjAxYWMxNDFhIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6eyJhcGlfa2V5IjoiYTQ2MmJhMjk2YzM5ZjQ2YzhkZjE4OWZiOTgyN2NiMGRjNmRkOWJkODYyOTNmODU1NzYyZGNjNTkzZGI3ZDFhMyJ9LCJuYmYiOjE3MDkxMTg2NTh9.hRNe1VYIo88Zc4d-_ts1XMT7ETiwqGP6umlHUn8PHME";


        //System.out.println(getChatbotUuid(chatbotName, apiKey));
        //System.out.println(getCreatedSessionUuid(chatbotName, apiKey));
        System.out.println(getAuthData("c5b5b3945ca34379b2d264011296542f", apiKey));
        //System.out.println(askGPT(query, chatbotName, apiKey));
        //JSONObject token = SECURITY.decodeJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJkb21haW5cIjpcInBheXJvbGwtdGVzdC5hb25zb2x1dGlvbnMub3JnXCIsXCJ1c2VyXCI6XCJBRE1JTlwifSIsImlzcyI6ImF1dGgwIiwiZXhwIjoxNzA5ODE0OTc3LCJpYXQiOjE3MDk3Mjg1Nzd9.eJ1sw45dPnq0q1f1L3EJxwEJCmEq1_A1XPfEYE0gdM4");
        //System.out.println(token);

    }
    
public static String getAuthData(String sessionUuid, String apiKey) {
    	
    	
    	
    	try {
		URL apiUrl = new URL("https://app.gpt-trainer.com/api/v1/session/" + sessionUuid);
		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", "Bearer" + " " + apiKey);
		connection.setRequestProperty("Content-type", "application/json");

		
		int responsecode = connection.getResponseCode();
		
		
      		if(responsecode != 200) {
			throw new RuntimeException("HttpResponseCode:" + responsecode);
		} else{
			
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					
					in.close();	
					
					JSONObject session = new JSONObject(content.toString());
					JSONObject meta = session.getJSONObject("meta");
					JSONObject auth_data = meta.getJSONObject("auth_data") ;
					
					String user = auth_data.getString("user");
					String domain = auth_data.getString("domain");
                    String result = "El usuario es" + " " + user + " " + "y el dominio es" + " " + domain;
					
					
				    return auth_data.toString();
					

		}
	} catch (Exception e) {
		e.printStackTrace();
	}
		return null;
    	
    }
    
    private static String getChatbotUuid(String chatbotName, String apiKey) {
    	
    	
    	
    	try {
		URL apiUrl = new URL("https://app.gpt-trainer.com/api/v1/chatbots");
		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", "Bearer" + " " + apiKey);
		
		int responsecode = connection.getResponseCode();
		
		
      		if(responsecode != 200) {
			throw new RuntimeException("HttpResponseCode:" + responsecode);
		} else{
			
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					
					in.close();	
					
					JSONArray chatbots = new JSONArray(content.toString());
					
				    String uuid = null;
					
					for(int i = 0; i < chatbots.length(); i++ ) {
						
						JSONObject chatbot = new JSONObject(chatbots.get(i).toString());
						
						if(chatbot.get("name").equals(chatbotName)) {
							uuid = chatbot.getString("uuid");
							break;
						}
					}
					
					if(uuid != null) return uuid;
					else System.out.println("No existe el chatbot con ese nombre.");

					

		}
	} catch (Exception e) {
		e.printStackTrace();
	}
		return null;
    	
    }
    
      private static String getCreatedSessionUuid(String chatbotName, String apiKey) {
    	
    	
    	try {
    		URL apiUrl = new URL("https://app.gpt-trainer.com/api/v1/chatbot/" + getChatbotUuid(chatbotName, apiKey) + "/session/create");
    		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
    		
    		connection.setRequestMethod("POST");
    		connection.setRequestProperty("Content-Type", "appllication/json");
    		connection.setRequestProperty("Authorization", "Bearer" + " " + apiKey);
    		
    		int responsecode = connection.getResponseCode();
    		
    		
          		if(responsecode != 200) {
    			throw new RuntimeException("HttpResponseCode:" + responsecode);
    		} else{
    			
    			BufferedReader in = new BufferedReader(
    					  new InputStreamReader(connection.getInputStream()));
    					String inputLine;
    					StringBuffer content = new StringBuffer();
    					while ((inputLine = in.readLine()) != null) {
    					    content.append(inputLine);
    					}
    					
    					in.close();	
    					
    					JSONObject session = new JSONObject(content.toString());
    			         connection.disconnect();

    					
    				    return session.getString("uuid"); 


    		}
          		
          
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return null;
    }
    
      private static String askGPT(String query, String chatbotName, String apiKey) {
    	  
    	  try {
      		URL apiUrl = new URL("https://app.gpt-trainer.com/api/v1/session/" + getCreatedSessionUuid(chatbotName, apiKey) + "/message/stream");
      		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
      		
      		connection.setRequestMethod("POST");
      		connection.setRequestProperty("Authorization", "Bearer" + " " + apiKey);
      		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      		connection.setRequestProperty("Accept", "application/json");
      		connection.setDoOutput(true);
      		
      		
      		
      		JSONObject question = new JSONObject();
      		question.put("query", query);
      		
			try (OutputStream os = connection.getOutputStream()) {
		         os.write(question.toString().getBytes("UTF-8"));
		         os.close();
		     }
      		
      		int responsecode = connection.getResponseCode();
      		
      		
            		if(responsecode != 200) {
      			throw new RuntimeException("HttpResponseCode:" + responsecode);
      		} else{
      			
      			BufferedReader in = new BufferedReader(
      					  new InputStreamReader(connection.getInputStream()));
      					String inputLine;
      					StringBuffer content = new StringBuffer();
      					while ((inputLine = in.readLine()) != null) {
      					    content.append(inputLine);
      					}
      					
      					in.close();
      					
      					return content.toString();
      					

      					

      		}
      	} catch (Exception e) {
      		e.printStackTrace();
      	}
    	  
    	  
    	  return null;
      }
	
}
