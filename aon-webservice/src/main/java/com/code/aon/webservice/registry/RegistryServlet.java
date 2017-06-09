package com.code.aon.webservice.registry;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "RegistryServlet", urlPatterns = { "/registry/*",
													  "/aon_gwt_aio/registry/*"})
public class RegistryServlet extends HttpServlet{
			
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "registry": // REGISTRY
					break;
				case "general":
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								if(AonStringUtils.isNumeric(pathInfo[5]))
									object = getGeneralList(domain, userName, Integer.parseInt(pathInfo[5]));
								else object = new JSONObject();
						} 
					}
					break;
				case "rprofile":
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								if(AonStringUtils.isNumeric(pathInfo[5]))
									object = getProfileList(domain, userName, Integer.parseInt(pathInfo[5]));
								else object = new JSONObject();
						} 
					}
					break;
				case "rmedia": // RMEDIA
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								if(AonStringUtils.isNumeric(pathInfo[5]))
									object = getRmediaList(domain, userName, Integer.parseInt(pathInfo[5]));
								else object = new JSONObject();
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) 
								// RMEDIA CON ID X
								object = getRmedia(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {
						// LISTA DE RMEDIA CONDICION DOMAIN
						object = getRmediaList(domain, userName);
					}
					break;
				case "rnote": // RNOTE
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								if(AonStringUtils.isNumeric(pathInfo[5]))
									object = getRnoteList(domain, userName, Integer.parseInt(pathInfo[5]));
								else object = new JSONObject();
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) 
								// RMEDIA CON ID X
								object = getRnote(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {
						// LISTA DE RMEDIA CONDICION DOMAIN
						object = getRnoteList(domain, userName);
					}
					break;
				case "customer": // customer
					object = getCustomerList(domain, userName);
					break;
				case "seller": // customer
					object = getSellerList(domain, userName);
					break;
				default:
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
	}
	
	private String segmentation;
	private String commercial;
	private JSONObject getGeneralList(Domain domain, String login, Integer registryId){
		RAddress ra = AON.getRAddres(domain.getName(), domain.getId(), login, registryId);
		String direction = n(ra.getStreet_type()) + " " + n(ra.getAddress()) + " " + n(ra.getNumber())+ " " + n(ra.getAddress2())
			+ " " +n(ra.getAddress3()) + " " + n(ra.getZip()) + " " + n(ra.getCity());
		commercial = "";
		segmentation = "";
		
		AON.getRSegmentStream(domain.getName(), domain.getId(), login, registryId)
		.forEach(s -> {segmentation = segmentation + " - " + s.getName();});

		AON.getRSellerStream(domain.getName(), domain.getId(), login, registryId)
		.forEach(s -> {commercial = commercial + " - " + s.getRegistryName();});
		
		CustomerStatus status = AON.getCustomer(domain.getName(), domain.getId(), login, registryId).getStatus();
		String st = !CustomerStatus.ACTIVE.equals(status) ? status.getDescription() : "";
		String observation = AON.getRNote(domain.getName(), domain.getId(), login,
				f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId))
    			.and(f.getNoteTypeProperty().eq(NoteType.OBSERVATION.value()))).getComments();
		
		
		return ToJSON.generalToJSON(direction, commercial, segmentation, observation, getRmediaList(domain, login, registryId), st);
	}

    private JSONArray getRmediaList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getRMediaStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(rm -> array.put(ToJSON.rmediaToJSON(rm)));
    	return array;
    }
    
    private JSONArray getRmediaList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
       	AON.getRMediaStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId)))
       		.forEach(rm -> array.put(ToJSON.rmediaToJSON(rm)));
    	return array;
    }
    
    private JSONObject getRmedia(Domain domain, String login, Integer id){
    	return ToJSON.rmediaToJSON(AON.getRMedia(domain.getName(),
    			domain.getId(), login, f -> f.getIdProperty().eq(id)));
    }
    
    private JSONArray getRnoteList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getRNoteStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getNoteTypeProperty().ne(NoteType.OBSERVATION.value())))
    		.forEach(rn -> array.put(ToJSON.rnoteToJSON(rn)));
    	return array;
    }
    
    private JSONArray getCustomerList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getCustomerStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getStatusProperty().eq(CustomerStatus.ACTIVE.value())))
    		.forEach(customer -> {
				JSONObject json = new JSONObject();
				json.put("id", customer.getId());
				json.put("name", customer.getRegistry().getName());
				array.put(json);
    	});
    	return array;    	
    }
    
    private JSONArray getSellerList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getSellerStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getStatusProperty().eq(CustomerStatus.ACTIVE.value())))
    		.forEach(seller -> {
				JSONObject json = new JSONObject();
				json.put("id", seller.getId());
				json.put("name", seller.getRegistryName());
				array.put(json);
    	});
    	return array;    	
    }
    
    private JSONArray getRnoteList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
      	AON.getRNoteStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId))
    			.and(f.getNoteTypeProperty().ne(NoteType.OBSERVATION.value())))
      		.forEach(rn -> array.put(ToJSON.rnoteToJSON(rn)));
    	return array;
    }
    
    private JSONObject getRnote(Domain domain, String login, Integer id){
    	return ToJSON.rnoteToJSON(AON.getRNote(domain.getName(),
    			domain.getId(), login,f -> f.getIdProperty().eq(id)
    			.and(f.getNoteTypeProperty().ne(NoteType.OBSERVATION.value()))));
    }
    
    private JSONArray getProfileList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
    	AON.getRegistryQuestionStream(domain.getName(), domain.getId(), login, registryId).forEach(q -> {
    		JSONObject json = new JSONObject();
    		json.put("id", q.getId());
    		json.put("question", q.getQuestionText());
    		JSONArray array2 = new JSONArray();
    		AON.getRegistryProfileStream(domain.getName(), domain.getId(), login, q.getId(), registryId)
			.sorted((n1,n2)-> n2.getLastUpdate().compareTo(n1.getLastUpdate())).forEach(rp -> {
        		JSONObject json2 = new JSONObject();
        		json2.put("id", rp.getId());
        		if(q.getType().equals(QuestionType.INFO.value())
        				|| q.getType().equals(QuestionType.TEXT.value()))
        			json2.put("name", rp.getValueText());
        		else if(q.getType().equals(QuestionType.NUMBER.value()))
        			json2.put("name", rp.getValueNumber());
        		else if(q.getType().equals(QuestionType.BOOLEAN.value()))
        			json2.put("name", rp.getValueNumber().equals(1));
        		else if(q.getType().equals(QuestionType.DATE.value()))
        			json2.put("name", rp.getValueDate());
        		json2.put("date", AonDateUtils.simpleFormat(rp.getLastUpdate()));
        		array2.put(json2);
    		});
    		json.put("array", array2);
    		array.put(json);
    	});
    	return array;
    }
    
    private String n(String str){
    	if(str != null) return str;
    	else return "";
    }
}
