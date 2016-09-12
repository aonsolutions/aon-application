package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.AonUrlApi;

@SuppressWarnings("serial")
@WebServlet(name = "OrgsServlet", urlPatterns = { "/orgs/*" })
public class OrgsServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		String serverName = req.getServerName();
		
		if(AonUrlApi.AONTEST.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String domainName = pathInfo[1]; 
			String userName = "";
			
			String md5 = "aaaaa";// TODO getMd5(userName+domainName);
			if(accessToken.equals(md5)){
				String filter = req.getParameter("filter") != null ? req.getParameter("filter") : "";
				Domain domain = AON.getDomain(domainName, 1, userName, f-> f.getNameProperty().eq(domainName));
				if(pathInfo.length > 2){
					Object object = new Object();
					
					
					switch (pathInfo[2]) {
					case "members": // ALL MEMBERS
						object = getAllUsersJSON(domain, userName, filter);
						break;
					case "workgroups": // ALL MEMBERS
						object = getAllWorkgroupsJSON(domain, userName, filter);
						break;

					default:
						break;
					}
					String js = req.getParameter("callback");
					if(js != null){
						resp.setContentType("application/javascript; charset=utf-8");     
						PrintWriter out = resp.getWriter();
						out.print(js + "({" +"\"meta\":{}, \"data\":" + object +"});");
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
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		super.doPost(req, resp);
	}
	
	
	private JSONArray getAllUsersJSON(Domain domain, String userName, final String filter) {
		JSONArray array = new JSONArray();
		Stream<User> userList = AON.getTaskMemberStream(domain.getName(), domain.getId(), userName,"%" + filter + "%")
				.map(new RegistryToUserFiller());
		userList.forEach(l->array.put(l.toJSON()));
		
		return array;
	}
	
	private JSONArray getAllWorkgroupsJSON(Domain domain, String userName, final String filter) {
		JSONArray array = new JSONArray();
		Stream<User> userList = AON.getTaskWorkgroupStream(domain.getName(), domain.getId(), userName,"%" + filter + "%")
				.map(new WorkgroupToUserFiller());
		userList.forEach(l->array.put(l.toJSON()));

		return array;
	}
	
	private static class RegistryToUserFiller implements Function<Registry, User> {
		
		@Override
		public User apply(Registry r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getName());  
		}
	}

	private static class WorkgroupToUserFiller implements Function<Workgroup, User> {
		
		@Override
		public User apply(Workgroup r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getDescription());  
		}
	}
}
