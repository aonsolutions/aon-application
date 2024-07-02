package aon.solutions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

public class GptTrainerApi {
	
	 
	
	
   
	public static JSONObject createChatbotSession(String chatbUuid, String token) throws ParseException, IOException, URISyntaxException{
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI("https://app.gpt-trainer.com/api/v1/chatbot/" + chatbUuid + "/session/create"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
 
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                //System.out.println(EntityUtils.toString(response.getEntity()));
                return new JSONObject(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
		
		
	}
	
public static String createMessage(String sessUuid, String token, String query) throws ParseException, IOException, URISyntaxException{
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	JSONObject jsonBody = new JSONObject();
            jsonBody.put("query", query);
            
            StringEntity entity = new StringEntity(jsonBody.toString(), "UTF-8");
           
        	
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI("https://app.gpt-trainer.com/api/v1/session/" + sessUuid + "/message/stream"))
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .addHeader("Authorization", "Bearer " + token)
                    .setEntity(entity)
                    .build();
 
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                //System.out.println(EntityUtils.toString(response.getEntity()));
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
		
		
	}
	
	public static void main(String[] args) throws ParseException, IOException, URISyntaxException
    {
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcwOTExODY1OCwianRpIjoiMDNmNDExM2EtZjkwYS00MTAyLTgwZTYtMjA4YjAxYWMxNDFhIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6eyJhcGlfa2V5IjoiYTQ2MmJhMjk2YzM5ZjQ2YzhkZjE4OWZiOTgyN2NiMGRjNmRkOWJkODYyOTNmODU1NzYyZGNjNTkzZGI3ZDFhMyJ9LCJuYmYiOjE3MDkxMTg2NTh9.hRNe1VYIo88Zc4d-_ts1XMT7ETiwqGP6umlHUn8PHME"
;		createChatbotSession("d22a72b597824782bebcf05028075b50", token);
        String sessUuid = createChatbotSession("d22a72b597824782bebcf05028075b50", token).getString("uuid");
        //Lamentablemente, no puedo responder a ese correo electrónico ya que soy un asistente de inteligencia artificial y no tengo acceso a correos electrónicos ni puedo interactuar con ellos. Mi función principal es proporcionar información y responder preguntas basadas en los contextos proporcionados. ¿Hay algo más en lo que pueda ayudarte?
        System.out.println(createMessage(sessUuid, token, "Hola buenas como te llamás "));
        
    }
	
	 

}
