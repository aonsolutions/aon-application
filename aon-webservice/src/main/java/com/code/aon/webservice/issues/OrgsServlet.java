package com.code.aon.webservice.issues;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;

@SuppressWarnings("serial")
@WebServlet(name = "OrgsServlet", urlPatterns = { "/orgs/*",
												  "/aon_gwt_aio/ms/orgs/*"})
public class OrgsServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
			
		String md5 = Utils.getMd5(userName+domainName);
		if(accessToken.equals(md5)){
			String filter = req.getParameter("filter") != null ? req.getParameter("filter") : "";
			Integer workgroupId = req.getParameter("w") != null ? Integer.parseInt(req.getParameter("w")):-1; 
			Domain domain = AON.getDomain(domainName, 1, userName, f-> f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				switch (pathInfo[3]) {
				case "members": // ALL MEMBERS
					object = getAllUsersJSON(domain, userName, filter, workgroupId);
					break;
				case "workgroups": // ALL WORKGROUPS
					object = getAllWorkgroupsJSON(domain, userName, filter);
					break;
				case "app_users": // ALL  APP USERS
					object = getAllAppUsersJSON(domain, userName);
					break;
				default:
					break;
				}
				Utils.giveBack(req, resp, object, new JSONObject());		
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		super.doPost(req, resp);
	}
	
	
	private JSONArray getAllUsersJSON(Domain domain, String userName, final String filter, Integer workgroupId) {
		JSONArray array = new JSONArray();
		Stream<User> userList;
		
		if(workgroupId != -1) userList = AON.getTaskMemberWStream(domain.getName(), domain.getId(), userName, "%" + filter + "%",
				workgroupId).filter(t -> t.getActive() != 0).map(new RegistryToUserFiller());
		else userList = AON.getTaskHolderStream(domain.getName(), domain.getId(), userName, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getNameProperty().like("%" + filter + "%")).and(f.getActiveProperty().eq((byte)1)))
				.sorted((e1,e2) -> e1.getName().compareTo(e2.getName())).map(new RegistryToUserFiller());
		
		array.put(new User().setId(-1).setLogin("Sin Asignar").toJSON());
		userList.forEach(l->{
			if(l.getStatus().equals(CustomerStatus.ACTIVE)) 
				array.put(l.toJSON());	
		});
		return array;
	}
	
	private JSONArray getAllWorkgroupsJSON(Domain domain, String userName, final String filter) {
		JSONArray array = new JSONArray();
		Stream<User> userList = AON.getTaskWorkgroupStream(domain.getName(), domain.getId(), userName,"%" + filter + "%")
				.map(new WorkgroupToUserFiller());
		array.put(new User().setId(-1).setLogin("Sin Asignar").toJSON());
		userList.forEach(l->{
			if(l.getStatus().equals(CustomerStatus.ACTIVE)) 
				array.put(l.toJSON());	
		});

		return array;
	}

	private JSONArray getAllAppUsersJSON(Domain domain, String userName) {
		JSONArray array = new JSONArray();		
		Stream<User> userList = AON.getUsers(domain.getId(), domain.getName(), userName).stream().map(new UserToUserFiller());
		userList.forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	
	private static class RegistryToUserFiller implements Function<TaskHolder, User> {
		
		@Override
		public User apply(TaskHolder r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getName())
					.setStatus(r.getActive() == 1 ? CustomerStatus.ACTIVE : CustomerStatus.INACTIVE);  
		}
	}
	
	private static class UserToUserFiller implements Function<com.esferalia.aon.occam.api.model.security.User , User> {
		
		@Override
		public User apply(com.esferalia.aon.occam.api.model.security.User r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getLogin());  
		}
	}

	private static class WorkgroupToUserFiller implements Function<Workgroup, User> {
		
		@Override
		public User apply(Workgroup r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getDescription())
					.setStatus(CustomerStatus.values()[r.getStatus()]);    
		}
	}
}
