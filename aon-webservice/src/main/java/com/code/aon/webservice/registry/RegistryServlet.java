package com.code.aon.webservice.registry;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.issues.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "RegistryServlet", urlPatterns = { "/registry/*" })
public class RegistryServlet extends HttpServlet{
		
	String h = "http://";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		if(req.getServerPort() == 80) h = "http://";
		else if(req.getServerPort() == 443) h = "https://";
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
				case "rmedia": // RMEDIA
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								object = getRmediaList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) 
								// RMEDIA CON ID X
								object = getRmedia(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {
						// LISTA DE RMEDIA CONDICION DOMAIN
						getRmediaList(domain, userName);
					}
					break;
				case "rnote": // RNOTE
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								// LISTA DE RMEDIA CON REGISTRY X
								object = getRnoteList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) 
								// RMEDIA CON ID X
								object = getRnote(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {
						// LISTA DE RMEDIA CONDICION DOMAIN
						getRnoteList(domain, userName);
					}
					break;
				default:
					break;
				}
				String js = req.getParameter("callback");
				if(js != null){
					resp.setContentType("application/javascript; charset=utf-8");     
					PrintWriter out = resp.getWriter();
					out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
					out.flush();
				} else {
					resp.setContentType("application/json");     
					PrintWriter out = resp.getWriter();
					out.print(object);
					out.flush();
				}
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
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
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(rn -> array.put(ToJSON.rnoteToJSON(rn)));
    	return array;
    }
    
    private JSONArray getRnoteList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
      	AON.getRNoteStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId)))
      		.forEach(rn -> array.put(ToJSON.rnoteToJSON(rn)));
    	return array;
    }
    
    private JSONObject getRnote(Domain domain, String login, Integer id){
    	return ToJSON.rnoteToJSON(AON.getRNote(domain.getName(),
    			domain.getId(), login,f -> f.getIdProperty().eq(id)));
    }
}
