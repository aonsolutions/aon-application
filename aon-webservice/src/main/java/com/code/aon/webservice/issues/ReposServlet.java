package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.AonUrlApi;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "ReposServlet", urlPatterns = { "/repos/*" })
public class ReposServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		String serverName = req.getServerName();
		
		//if(AonUrlApi.AONTEST.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String userName = pathInfo[1];
			String domainName = pathInfo[2]; 
			
			String md5 = getMd5(userName+domainName);
			if(accessToken.equals(md5)){
				String filter = req.getParameter("filter") != null ? req.getParameter("filter") : "";
				Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
				if(pathInfo.length > 3){
					Object object = new Object();
					switch (pathInfo[3]) {
					case "issues":
						if(pathInfo.length > 4){
							if(pathInfo.length > 5){
								if(pathInfo[5].equalsIgnoreCase("labels")){ // LABELS
									object = getLabelsJSON(domain, userName, pathInfo[4]);
								} else if(pathInfo[5].equalsIgnoreCase("comments")){ // COMMENTS
									object = getCommentsJSON(domain, userName, pathInfo[4]);
								} else if(pathInfo[5].equalsIgnoreCase("events")){ // EVENTS
									object = getEventsJSON(domain, userName, pathInfo[4]);
								} else if(pathInfo[5].equalsIgnoreCase("type")){ // TYPE
									object = getTypeJSON(domain, userName, pathInfo[4]);
								} else if(pathInfo[5].equalsIgnoreCase("priority")){ // PRIORITY
									object = getPriorityJSON(domain, userName, pathInfo[4]);
								}
							} else{ // TASK / ISSUE
								object = getIssueJSON(domain, userName, pathInfo[4]);
							}
						}else{ // TASKS / ISSUES
							object = getIssuesJSON(domain, userName, getFilter(req));
						}
						break;
					case "labels": // ALL LABELS
						object = getAllLabelsJSON(domain, userName, TagType.TASK_LABEL, filter);
						break;
					case "types": // ALL TYPES
						object = getAllLabelsJSON(domain, userName, TagType.TASK_TYPE, filter);
						break;
					case "priorities": // ALL PRIORITIES
						object = getAllLabelsJSON(domain, userName, TagType.TASK_PRIORITY, filter);
						break;
					case "registries": // ALL REGISTRIES
						object = getAllRegistriesJSON(domain, userName);
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
		//}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		String serverName = req.getServerName();
		
		//if(AonUrlApi.AON.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String userName = pathInfo[1];
			String domainName = pathInfo[2]; 
			
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				String s = req.getReader().readLine();
				System.out.println(s);
				if(s == null) s = "{}";
				JSONObject json = new JSONObject(s);
				
				Object object = new Object();
				com.esferalia.aon.occam.api.model.security.User user = AON.getUser(domain.getName(), domain.getId(), userName);
				switch (pathInfo[3]) {
				case "issues":
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							if(pathInfo[5].equalsIgnoreCase("labels")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									Integer tagId = DBConsults.getTagId(domain.getName(), domain.getId(), userName, pathInfo[6], TagType.TASK_LABEL);
									TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(taskId).setTag(tagId);
									AON.createTaskTag(domain.getName(), domain.getId(), userName, taskTag);
								}
							} else if(pathInfo[5].equalsIgnoreCase("comments")){
								if(pathInfo.length > 6){
									TaskComment tc = new TaskComment().setComment(json.getString("body"))
											.setUpdateDate(Calendar.getInstance().getTime());
									AON.updateTaskComment(domain.getName(), domain.getId(), userName, tc, Integer.parseInt(pathInfo[6]));
									object = new Comment(tc).toJSON();
								} else {
									Task task = AON.getTask(domain.getName(), domain.getId(), userName,
										f -> f.getNumberProperty().eq(Integer.parseInt(pathInfo[4])).and(f.getDomainProperty().eq(domain.getId())));
									TaskComment tc = new TaskComment().setComment(json.getString("body"))
											.setUpdateDate(Calendar.getInstance().getTime())
											.setCreateDate(Calendar.getInstance().getTime())
											.setDomain(domain.getId())
											.setTask(task.getId())
											.setUser(user);
									TaskComment tc2 = AON.createTaskComment(domain.getName(), domain.getId(), userName, tc, Integer.parseInt(pathInfo[4]));	
									object = new Comment(tc2).toJSON();
								}
							} else if(pathInfo[5].equalsIgnoreCase("events")){
								if(pathInfo.length > 6){
									TaskEvent te = new TaskEvent().setEvent(json.getString("body"));
									AON.updateTaskEvent(domain.getName(), domain.getId(), userName, te, Integer.parseInt(pathInfo[6]));
									object = new Event(te).toJSON();
								} else {
									TaskEvent te = new TaskEvent().setEvent(json.getString("event"))
											.setCreateDate(Calendar.getInstance().getTime())
											.setDomain(domain.getId())
											.setTask(Integer.parseInt(pathInfo[4]))
											.setUser(user);
									TaskEvent te2 = AON.createTaskEvent(domain.getName(), domain.getId(), userName, te, Integer.parseInt(pathInfo[4]));	
									object = new Event(te2).toJSON();

								}
							} else if(pathInfo[5].equalsIgnoreCase("type")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
									Integer tagId = DBConsults.getTagId(domain.getName(), domain.getId(), userName, pathInfo[6], TagType.TASK_TYPE);
									TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(taskId).setTag(tagId);
									AON.createTaskTag(domain.getName(), domain.getId(), userName, taskTag);
								} 						
							} else if(pathInfo[5].equalsIgnoreCase("priority")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.deletePriorityTaskTag(domain.getName(), domain.getId(), userName, taskId);
									Integer tagId = DBConsults.getTagId(domain.getName(), domain.getId(), userName, pathInfo[6], TagType.TASK_PRIORITY);
									TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(taskId).setTag(tagId);
									AON.createTaskTag(domain.getName(), domain.getId(), userName, taskTag);
								}
							} else if(pathInfo[5].equalsIgnoreCase("user")){
								if(pathInfo.length > 6){
									Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(Integer.parseInt(pathInfo[6])));
								} 
							} else if(pathInfo[5].equalsIgnoreCase("workgroup")){
								if(pathInfo.length > 6){
									Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(Integer.parseInt(pathInfo[6])));
								}
							}
						} else{ // UPDATE TASK / ISSUE
							Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
							if(json.get("state") != null) {
								Task task = DBConsults.getTask(domain.getName(), domain.getId(), userName, taskId);
								if(json.get("state").equals("open")){
									task = task.setStatus(TaskStatus.OPEN.value()).setEndDate(null).setId(taskId);
									TaskEvent taskEvent = new TaskEvent().setCreateDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
										.setEvent("reopened").setTask(taskId).setUser(user);
									AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, taskId);
								}
								if(json.get("state").equals("closed")){
									task = task.setStatus(TaskStatus.CLOSED.value()).setEndDate(Calendar.getInstance().getTime()).setId(taskId);
									TaskEvent taskEvent = new TaskEvent().setCreateDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
											.setEvent("closed").setTask(taskId).setUser(user);
									AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, taskId);
								}
								AON.updateTaskStatus(domain.getName(), domain.getId(), userName, task );								
								object = getIssueJSON(domain, userName, task);
							}
							// TODO  UPDATE ISSUE / TASK	
						}
					} else { // CREATE NEW TASK / ISSUE
						Integer num = AON.getLastTaskNumber(domain.getName(), domain.getId(),userName) != null ?
								AON.getLastTaskNumber(domain.getName(), domain.getId(),userName) : 0;
						Task task = new Task()
							.setDescription(json.getString("title"))
							.setComments(json.getString("body"))
							.setDomain(domain.getId())
							.setNumber(num + 1)
							.setStartDate(Calendar.getInstance().getTime())
							.setUpdateDate(Calendar.getInstance().getTime())
							.setDueDate(Calendar.getInstance().getTime())// TODO 
							.setStatus(TaskStatus.OPEN.value())
							.setUser(user.getId())
							//.setEnterpriseRegistry(json.getInt("enterprise")) // TODO
							;
						
						Task t = AON.createTask(domain.getName(), domain.getId(), userName, task);
						object = new Issue(t, user, new Registry(), new LinkedList<Label>(), new Label(), new Label(), 0, domain, userName, new Workgroup()).toJSON();
					}
					break;
				case "labels":
					if(pathInfo.length > 4){
						Tag tag= DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_LABEL);
						tag.setName(json.getString("name"));
						AON.updateTag(domainName, domain.getId(), userName, tag);
						object = new Label().setId(tag.getId()).setName(tag.getName()).toJSON();
					} else {
						Tag tag = new Tag().setName(json.getString("name"))
								.setDomain(domain.getId()).setType(TagType.TASK_LABEL.value());
						Tag t = AON.addNewTag(domain.getId(), domain.getName(), userName, tag);
						object = new Label().setId(t.getId()).setName(t.getName()).toJSON();
					}
					break;
				case "types":
					if(pathInfo.length > 4){
						Tag tag= DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_TYPE);
						tag.setName(json.getString("name"));
						AON.updateTag(domainName, domain.getId(), userName, tag);
						object = new Label().setId(tag.getId()).setName(tag.getName()).toJSON();
					} else {
						Tag tag = new Tag().setName(json.getString("name"))
								.setDomain(domain.getId()).setType(TagType.TASK_TYPE.value());
						Tag t = AON.addNewTag(domain.getId(), domain.getName(), userName, tag);
						object = new Label().setId(t.getId()).setName(t.getName()).toJSON();
					}
					break;
				case "priorities":
					if(pathInfo.length > 4){
						Tag tag= DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_PRIORITY);
						tag.setName(json.getString("name"));
						AON.updateTag(domainName, domain.getId(), userName, tag);
						object = new Label().setId(tag.getId()).setName(tag.getName()).toJSON();
					} else {
						Tag tag = new Tag().setName(json.getString("name"))
								.setDomain(domain.getId()).setType(TagType.TASK_PRIORITY.value());
						Tag t = AON.addNewTag(domain.getId(), domain.getName(), userName, tag);
						object = new Label().setId(t.getId()).setName(t.getName()).toJSON();
					}
					break;
				default:
					break;
				}
				
				resp.setContentType("application/json;charset=UTF-8");
				addCorsHeader(resp);
				PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
				os.println(object.toString());
				os.flush();
			}
		//}
	}
	
    private void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
    
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("DELETE METHOD");
		String serverName = req.getServerName();
		
		if(AonUrlApi.AON.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String userName = pathInfo[1];
			String domainName = pathInfo[2]; 
			
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				String s = req.getReader().readLine();
				if(s == null) s = "{}";
				JSONObject json = new JSONObject(s);
				Object object = new Object();
		
				switch (pathInfo[3]) {
				case "issues":
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							if(pathInfo[5].equalsIgnoreCase("labels")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									Integer tagId = Integer.parseInt(pathInfo[6]);
									AON.deleteTaskTag(domain.getName(), domain.getId(), userName, 
											f -> f.getTagProperty().eq(tagId).and(f.getTaskProperty().eq(taskId)));
									object = new Label().toJSON();
								}
							} else if(pathInfo[5].equalsIgnoreCase("type")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
									object = new Label().toJSON();
								} 						
							} else if(pathInfo[5].equalsIgnoreCase("priority")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.deletePriorityTaskTag(domain.getName(), domain.getId(), userName, taskId);
									object = new Label().toJSON();
								}
							} else if(pathInfo[5].equalsIgnoreCase("user")){
								if(pathInfo.length > 6){
									Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(null));
									object = new User().toJSON();
								} 
							} else if(pathInfo[5].equalsIgnoreCase("workgroup")){
								if(pathInfo.length > 6){
									Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(null));
									object = new User().toJSON();
								}
							}
						}
						// DELETE ISSUE / TASK
					}
					break;
				case "labels":
					if(pathInfo.length > 4){
						// DELETE LABEL
					}
					break;
				case "types":
					if(pathInfo.length > 4){
						// DELETE TYPE
					}
					break;
				case "priorities":
					if(pathInfo.length > 4){
						// DELETE PRIORITY
					}
					break;
				default:
					break;
				}
				
				resp.setContentType("application/json;charset=UTF-8");
				addCorsHeader(resp);
				PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
				os.println(object.toString());
				os.flush();
			}
		}
	}
	
	private JSONArray getLabelsJSON(Domain domain, String userName, String taskNumber) {
		Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(taskNumber));
		Stream<Tag> st = AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(taskId));
		JSONArray array = new JSONArray();
		st.filter(t -> t.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName)).forEach(r -> array.put(r.toJSON()));
		return array;
	}
	
	private JSONObject getTypeJSON(Domain domain, String userName, String taskNumber) {
		Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(taskNumber));
		Stream<Tag> st = AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(taskId));
		return st.filter(t -> t.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName)).findFirst().orElse(new Label()).toJSON();	
	}
	
	private JSONObject getPriorityJSON(Domain domain, String userName, String taskNumber) {
		Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(taskNumber));
		Stream<Tag> st = AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(taskId));
		return st.filter(t -> t.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName)).findFirst().orElse(new Label()).toJSON();	
	}
	
	private JSONArray getCommentsJSON(Domain domain, String userName, String taskNumber) {
		JSONArray array = new JSONArray();
		Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(taskNumber));
		AON.getTaskCommentStream(domain.getName(), domain.getId(), userName, taskId)
		.map(new TaskCommentToCommentFiller(domain, userName)).forEach(c -> array.put(c.toJSON()));
		return array;
	}
	
	private JSONArray getEventsJSON(Domain domain, String userName, String taskNumber) {
		JSONArray array = new JSONArray();
		Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(taskNumber));
		AON.getTaskEventStream(domain.getName(), domain.getId(), userName, taskId)
		.map(new TaskEventToEventFiller(domain, userName)).forEach(e ->	array.put(e.toJSON()));
		return array;
	}
	
	private JSONArray getAllRegistriesJSON(Domain domain, String userName) {
		List<Registry> list = AON.getRegistries(domain.getId(), domain.getName(), userName);
		JSONArray array = new JSONArray();
		list.stream().map(new RegistryToUserFiller()).forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	private JSONArray getAllLabelsJSON(Domain domain, String userName, TagType tt, final String filter) {
		JSONArray array = new JSONArray();
		Stream<Label> labelList = AON.getTagStream(domain.getName(), domain.getId(), userName,
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getTypeProperty().eq(tt.value()))
				.and(f.getNameProperty().like( "%" + filter +"%")))
				.map(new TagToLabelFiller(domain, userName));
		labelList.forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	private JSONObject getIssueJSON(Domain domain, String userName, String taskNumber) {
		Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.valueOf(taskNumber));
		Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
		Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
		com.esferalia.aon.occam.api.model.security.User creator = AON.getUser(domain.getId(), domain.getName(), userName, task.getUser());
		Stream<Tag> label = AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId())); 
		LinkedList<Label> labels = label.filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
				.collect(Collectors.toCollection(LinkedList::new)); 
		Label type = label.filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Label priority = label.filter(l -> l.getType() == TagType.TASK_PRIORITY.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
		
		Issue issue = new Issue(task, creator, assignee, labels, type, priority, comments, domain, userName, workgroup);		
		return issue.toJSON();
	}
	
	private JSONObject getIssueJSON(Domain domain, String userName, Task task) {
		Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
		Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
		com.esferalia.aon.occam.api.model.security.User creator = AON.getUser(domain.getId(), domain.getName(), userName, task.getUser());
		LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId())); 
		LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
				.collect(Collectors.toCollection(LinkedList::new)); 
		Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Label priority = label.stream().filter(l -> l.getType() == TagType.TASK_PRIORITY.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
		Issue issue = new Issue(task, creator, assignee, labels, type, priority, comments, domain, userName, workgroup);		
		return issue.toJSON();
	}
	
	private JSONArray getIssuesJSON(Domain domain, String userName, IssueFilter filter) {
		LinkedList<Task> taskList = DBConsults.getTaskList(domain.getName(), domain.getId(), userName, filter);
		System.out.println(taskList.size());
		JSONArray array = new JSONArray();
		for (Task task : taskList) {
			Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
			Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
			com.esferalia.aon.occam.api.model.security.User creator = AON.getUser(domain.getId(), domain.getName(), userName, task.getUser());
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Label priority = label.stream().filter(l -> l.getType() == TagType.TASK_PRIORITY.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
			JSONObject json = new Issue(task, creator, assignee, labels, type, priority, comments, domain, userName, workgroup).toJSON();
			array.put(json);
		}
		System.out.println(array.toString());
		return array;
	}
	
	private IssueFilter getFilter(HttpServletRequest req){
		return new IssueFilter().setAssignee(req.getParameter("asignee"))
				.setCreator(req.getParameter("creator"))
				.setDirection(req.getParameter("direction"))
				.setLabels(req.getParameter("labels"))
				.setMentioned(req.getParameter("mentioned"))
				.setMilestone(req.getParameter("milestone"))
				.setSince(req.getParameter("since"))
				.setSort(req.getParameter("sort"))
				.setState(req.getParameter("state"))
				.setPerPage(Integer.parseInt(req.getParameter("per_page")))
				.setPage(Integer.parseInt(req.getParameter("page")));
	}
	
	private static class TagToLabelFiller implements Function<Tag, Label> {
		private Domain domain;
		private String userName;
		
		public TagToLabelFiller(Domain domain, String userName) {
			this.domain = domain;
			this.userName = userName;
		}
		
		@Override
		public Label apply(Tag r) {
			return new Label().setId(r.getId())
					.setName(r.getName())
					.setColor(r.getColor())
					.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/labels/" + r.getName());  
		}
	}
	
	private static class TaskCommentToCommentFiller implements Function<TaskComment, Comment> {
		Domain domain;
		String userName;
		
		public TaskCommentToCommentFiller(Domain domain, String userName) {
			this.domain = domain;
			this.userName = userName;
		}
		
		@Override
		public Comment apply(TaskComment r) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return new Comment()
					.setBody(r.getComment())
					.setCreatedAt(dateFormat.format(r.getCreateDate()))
					.setId(r.getId())
					.setUpdatedAt(dateFormat.format(r.getUpdateDate()))
					.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/comments/" + r.getId())
					.setUser(new User(r.getUser()));
		}
	}
	
	private static class TaskEventToEventFiller implements Function<TaskEvent, Event> {
		Domain domain;
		String userName;

		public TaskEventToEventFiller(Domain domain, String userName) {
			this.domain = domain;
			this.userName = userName;
		}
		
		@Override
		public Event apply(TaskEvent r) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return new Event()
					.setEvent(r.getEvent())
					.setCreatedAt(dateFormat.format(r.getCreateDate()))
					.setId(r.getId())
					.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/events/" + r.getId()) 
					.setUser(new User(r.getUser()));
		}
	}
	
	private static class RegistryToUserFiller implements Function<Registry, User>{
	
		@Override
		public User apply(Registry r) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return new User()
					.setId(r.getId())
					.setLogin(r.getName());
		}
	}
	
	
	private String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(str.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
	}
}
