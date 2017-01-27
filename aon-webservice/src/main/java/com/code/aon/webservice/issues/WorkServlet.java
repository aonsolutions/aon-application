package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "WorkServlet", urlPatterns = { "/work/*",
												  "/aon_gwt_aio/work/*"})
public class WorkServlet extends HttpServlet{
	private static final Logger LOGGER  = Logger.getLogger(WorkServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Work Servlet - GET METHOD");
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];	
		
		String md5 = Utils.getMd5(userName+domainName);
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f-> f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				switch (pathInfo[3]) {
				case "operator": 
					object = getOperatorsJSON(domain, userName);
					break;
				case "workgroup": 
					object = getWorkgroupsJSON(domain, userName);
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
		LOGGER.info("Work Servlet - POST METHOD");
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
			
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){
			String line = "";
			String s = "";
			while((line = req.getReader().readLine()) != null)
				s = s + " " + line;
			System.out.println(s);
			s = Utils.checkString(s);
			System.out.println(s);
			if(s == null || s.equals("")) s = "{}";
			JSONObject json = new JSONObject(s);
							
			Object object = new Object();
			switch (pathInfo[3]) {
			case "operator":
				if(pathInfo.length > 4){
					if(AonStringUtils.isNumeric(pathInfo[5])){
						Integer id = Integer.parseInt(pathInfo[5]);
						switch(pathInfo[4]){
						case "remove": case "delete":
							object = removeOperator(domain, userName, id);
							break;
						case "update": case "edit":
							object = updateOperator(domain, userName, id, json);
							break;
						}	
					}
				} else {
					object = addOperator(domain, userName, json);
				}
				break;
			case "workgroup":
				if(pathInfo.length > 4){
					if(AonStringUtils.isNumeric(pathInfo[5])){
						Integer id = Integer.parseInt(pathInfo[5]);
						switch(pathInfo[4]){
						case "remove": case "delete":
							object = removeWorkgroup(domain, userName, id);
							break;
						case "update": case "edit":
							object = updateWorkgroup(domain, userName, id, json);
							break;
						}	
					}
				} else {
					object = addWorkgroup(domain, userName,json);
				}
				break;
			default:
				break;
			}
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	
	
	private JSONArray getOperatorsJSON(Domain domain, String userName) {
		JSONArray array = new JSONArray();
		Stream<User> userList = AON.getTaskMemberStream(domain.getName(), domain.getId(), userName,"%%")
				.map(new RegistryToUserFiller());		
		userList.forEach(l-> {
			l.setEmail(AON.getRMedia(domain.getName(), domain.getId(), userName,
				f -> f.getMediaProperty().eq((byte)4).and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getRegistryProperty().eq(l.getId()))).getValue());
			
			l.setWorkgroups(AON.getTaskHolderWorkgroupStream(domain.getName(), domain.getId(), userName, f2 -> f2.getTaskHolderProperty().eq(l.getId()))
				.map(new WorkgroupToUserFiller()).collect(Collectors.toCollection(LinkedList::new)));
			
			array.put(l.toJSON());
		});
		return array;
	}
	
	private JSONArray getWorkgroupsJSON(Domain domain, String userName) {
		JSONArray array = new JSONArray();
		Stream<User> userList = AON.getTaskWorkgroupStream(domain.getName(), domain.getId(), userName,"%%")
				.map(new WorkgroupToUserFiller());
		userList.forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	private JSONObject addOperator(Domain domain, String userName, JSONObject json){
		Registry registry = new Registry().setName(json.getString("name")).setAlias(json.getString("name"))
				.setDomain(domain.getId());
		registry = AON.insertRegistry(domain.getName(), domain.getId(), userName, registry);
		if(json.opt("email") != null){
			RegistryMedia rmedia = new RegistryMedia().setMedia((byte) 4).setValue(json.getString("email"))
				.setDomain(domain.getId()).setRegistry(registry);
			AON.insertRMedia(domain.getName(), domain.getId(), userName, rmedia);
		}			
		TaskHolder taskHolder = new TaskHolder().setId(registry.getId()).setActive((byte) 1).setDomain(domain.getId())
				.setType((byte) 0);
		AON.insertTaskHolder(domain.getName(), domain.getId(), userName, taskHolder);
		if(json.opt("workgroups") != null){
			String[] workgroups = json.getString("workgroups").split("@");
			for(Integer j = 0 ; j< workgroups.length; j++){
				if(AonStringUtils.isNumeric(workgroups[j])){
					AON.insertTaskHolderWorkgroup(domain.getName(), domain.getId(), userName, registry.getId(), Integer.parseInt(workgroups[j]));
				}
			}
		}
		return new JSONObject();
	}
	
	private JSONObject updateOperator(Domain domain, String userName, Integer id, JSONObject json){
		Registry registry = AON.getRegistry(domain.getName(), domain.getId(), userName, id)
			.setName(json.getString("name")).setAlias(json.getString("name"));
		registry = AON.updateRegistry(domain.getName(), domain.getId(), userName, registry);
		if(json.opt("email") != null){
			RegistryMedia rmedia = AON.getRMedia(domain.getName(), domain.getId(), userName,
				f -> f.getMediaProperty().eq((byte) 4).and(f.getRegistryProperty().eq(id)));
			if(rmedia.getValue() != null && !rmedia.getValue().equals(json.getString("email"))){
				AON.updateRMedia(domain.getName(), domain.getId(), userName,
						rmedia.setValue(json.getString("email")));
			} else if(rmedia.getValue() == null){
				rmedia = new RegistryMedia().setMedia((byte) 4).setValue(json.getString("email"));
				AON.insertRMedia(domain.getName(), domain.getId(), userName, rmedia);
			}
		}			
		if(json.opt("workgroups") != null){
			String[] workgroups = json.getString("workgroups").split("@");
			for(Integer j = 0 ; j< workgroups.length; j++){
				if(AonStringUtils.isNumeric(workgroups[j])){
					String w = workgroups[j];
					Boolean bool = AON.isTaskHolderWorkgroup(domain.getName(), domain.getId(), userName, 
							f -> f.getDomainProperty().eq(domain.getId())
							.and(f.getTaskHolderProperty().eq(id))
							.and(f.getWorkgroupProperty().eq(Integer.parseInt(w))));
					if(!bool) AON.insertTaskHolderWorkgroup(domain.getName(), domain.getId(), userName, id, Integer.parseInt(w));
				}
			}
		}
		return new JSONObject();
	}
	
	private JSONObject removeOperator(Domain domain, String userName, Integer id){
		AON.deleteTaskHolderWorkgroup(domain.getName(), domain.getId(), userName, f -> f.getTaskHolderProperty().eq(id));
		AON.deleteTaskHolder(domain.getName(), domain.getId(), userName, id);
		AON.deleteRMedia(domain.getName(), domain.getId(), userName, id);
		AON.deleteRegistry(domain.getName(), domain.getId(), userName, id);
		return new JSONObject();
	}
	
	private JSONObject addWorkgroup(Domain domain, String userName, JSONObject json){
		Workgroup workgroup = new Workgroup().setDomain(domain.getId())
				.setDescription(json.getString("name")).setStatus((byte) 0);
		AON.insertWorkgroup(domain.getName(), domain.getId(), userName, workgroup);
		return new JSONObject(); // TODO
	}
	
	private JSONObject updateWorkgroup(Domain domain, String userName, Integer id, JSONObject json){
		Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, id)
			.setDescription(json.getString("name"));
		AON.updateWorkgroup(domain.getName(), domain.getId(), userName, workgroup);
		return new JSONObject(); // TODO
	}
	
	private JSONObject removeWorkgroup(Domain domain, String userName, Integer id){
		AON.deleteTaskHolderWorkgroup(domain.getName(), domain.getId(), userName, f -> f.getWorkgroupProperty().eq(id));
		AON.deleteWorkgroup(domain.getName(), domain.getId(), userName, id);
		return new JSONObject(); // TODO
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
