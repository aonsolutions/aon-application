package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;
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
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TagColor;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.AonUrlApi;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "ReposServlet", urlPatterns = { "/repos/*" })
public class ReposServlet extends HttpServlet{
	
	private static final DBConsults DB = DBConsults.getInstance();
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String scheme = req.getParameter("scheme");
		String accessToken = req.getParameter("access_token");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			String filter = req.getParameter("filter") != null ? req.getParameter("filter") : "";
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "github":
					object = getGithubJSON(domain, userName);
					break;
				case "fast_filter":
					object = getFastFilterJSON(domain, userName);
					break;
				case "faqs":
					object = getFaqIssuesJSON(domain, userName, getFilter(req), scheme);
					break;
				case "duplicates":
					if(pathInfo.length > 4){
						object = getDuplicateIssuesJSON(domain, userName, pathInfo[4], scheme);
					}
					break;
				case "enterprise":
					if(pathInfo.length > 4){
						object = getEnterpriseIssuesJSON(domain, userName, pathInfo[4], scheme);
					}
					break;
				case "issues_light":
					if(pathInfo.length > 4){
						object = getLightIssuesJSON(domain, userName,getFilter(req), pathInfo[4]);				
					}
					break;
				case "issues":
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							if(pathInfo[5].equalsIgnoreCase("labels")) // LABELS
								object = getLabelsJSON(domain, userName, pathInfo[4]);
							else if(pathInfo[5].equalsIgnoreCase("comments")) // COMMENTS
								object = getCommentsJSON(domain, userName, pathInfo[4]);
							else if(pathInfo[5].equalsIgnoreCase("events")) // EVENTS
								object = getEventsJSON(domain, userName, pathInfo[4]);
							else if(pathInfo[5].equalsIgnoreCase("type")) // TYPE
								object = getTypeJSON(domain, userName, pathInfo[4]);
							else if(pathInfo[5].equalsIgnoreCase("priority")) // PRIORITY
								object = getPriorityJSON(domain, userName, pathInfo[4]);	
						} else // TASK / ISSUE
							object = getIssueJSON(domain, userName, DB.getTask(domain, userName, Integer.parseInt(pathInfo[4])), scheme);
					}else{ // TASKS / ISSUES
						object = getIssuesJSON(domain, userName, getFilter(req), scheme);
						meta = getSizeJSON(domain, userName, getFilter(req));
					}
					break;
				case "labels": // ALL LABELS
					object = getAllLabelsJSON(domain, userName, TagType.TASK_LABEL, filter);
					break;
				case "types": // ALL TYPES
					object = getAllLabelsJSON(domain, userName, TagType.TASK_TYPE, filter);
					break;
				case "priorities": // ALL PRIORITIES
					object = getAllPrioritiesJSON(domain, userName);
					break;
				case "registries": // ALL REGISTRIES
					if(!filter.equals("")) object = getFilterRegistriesJSON(domain, userName, filter);
					else object = getAllRegistriesJSON(domain, userName);
					break;
				case "order_options": // ORDER OPTIONS
					object = getOrderOptionsJSON();
					break;
				case "date_options": // DATE OPTIONS
					object = getDateOptionsJSON();
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
		String line = "";
		String s = "";
		while((line = req.getReader().readLine()) != null)
			s = s + " " + line;
		System.out.println(s);
		s = Utils.checkString(s);
		System.out.println(s);
		if(s == null || s.equals("")) s = "{}";
		JSONObject json = new JSONObject(s);
		String scheme = req.getParameter("scheme");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
			
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			switch (pathInfo[3]) {
			case "github":
				addGithub(domain, userName, json);
				break;
			case "fast_filter":
				addFastFilter(domain, userName, json);
				break;
			case "issues":
				if(pathInfo.length > 4){
					if(pathInfo.length > 5){
						if(pathInfo[5].equalsIgnoreCase("labels")){
							if(pathInfo.length > 6){
								Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
								Integer tagId = DB.getTagId(domain, userName, pathInfo[6], TagType.TASK_LABEL);
								TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(taskId).setTag(tagId);
								AON.createTaskTag(domain.getName(), domain.getId(), userName, taskTag);
							}
						} else if(pathInfo[5].equalsIgnoreCase("comments")){
							if(pathInfo.length > 6){
								TaskComment tc = AON.getTaskComment(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[6]))
										.setComment(json.getString("body")).setModificationDate(Calendar.getInstance().getTime())
										.setModificationUser(userName);
								AON.updateTaskComment(domain.getName(), domain.getId(), userName, tc);
								object = new Comment(tc).toJSON();
							} else {
								Task task = AON.getTask(domain.getName(), domain.getId(), userName,
									f -> f.getNumberProperty().eq(Integer.parseInt(pathInfo[4])).and(f.getDomainProperty().eq(domain.getId())));
								TaskComment tc = new TaskComment().setComment(json.getString("body"))
										.setModificationDate(Calendar.getInstance().getTime())
										.setCreationDate(Calendar.getInstance().getTime())
										.setDomain(domain.getId())
										.setTask(task.getId())
										.setCreationUser(userName)
										.setModificationUser(userName);
								TaskComment tc2 = AON.createTaskComment(domain.getName(), domain.getId(), userName, tc, Integer.parseInt(pathInfo[4]));	
								object = new Comment(tc2).toJSON();
							}
						} else if(pathInfo[5].equalsIgnoreCase("events")){
							if(pathInfo.length > 6){
								TaskEvent te = new TaskEvent().setEvent(json.getString("body")).setModificationUser(userName)
										.setModificationDate(Calendar.getInstance().getTime());
								AON.updateTaskEvent(domain.getName(), domain.getId(), userName, te, Integer.parseInt(pathInfo[6]));
								object = new Event(te).toJSON();
							} else {
								TaskEvent te = new TaskEvent().setEvent(json.getString("event"))
										.setCreationDate(Calendar.getInstance().getTime())
										.setModificationDate(Calendar.getInstance().getTime())
										.setDomain(domain.getId())
										.setTask(Integer.parseInt(pathInfo[4]))
										.setCreationUser(userName)
										.setModificationUser(userName);
								TaskEvent te2 = AON.createTaskEvent(domain.getName(), domain.getId(), userName, te, Integer.parseInt(pathInfo[4]));	
								object = new Event(te2).toJSON();
							}
						} else if(pathInfo[5].equalsIgnoreCase("type")){
							if(pathInfo.length > 6){
								Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
								AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
								Integer tagId = DB.getTagId(domain, userName, pathInfo[6], TagType.TASK_TYPE);
								TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(taskId).setTag(tagId);
								AON.createTaskTag(domain.getName(), domain.getId(), userName, taskTag);
							} 						
						} else if(pathInfo[5].equalsIgnoreCase("priority")){
							if(pathInfo.length > 6){
								Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]))
										.setModificationUser(userName).setModificationDate(Calendar.getInstance().getTime());	
								DB.updateTaskPriority(domain, userName,task.setPriority(Priority.valueNameOf(pathInfo[6]).value()));
							}
						} else if(pathInfo[5].equalsIgnoreCase("user")){
							if(pathInfo.length > 6){
								Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]))
										.setModificationUser(userName).setModificationDate(Calendar.getInstance().getTime());
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(Integer.parseInt(pathInfo[6])));
								sendAssigneeNotification(domain, userName, task, Integer.parseInt(pathInfo[6]));
							} 
						} else if(pathInfo[5].equalsIgnoreCase("workgroup")){
							if(pathInfo.length > 6){
								Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]))
										.setModificationUser(userName).setModificationDate(Calendar.getInstance().getTime());
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(Integer.parseInt(pathInfo[6])));
							}
						}
					} else{ // UPDATE TASK / ISSUE
						Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]))
								.setModificationUser(userName).setModificationDate(Calendar.getInstance().getTime());
						
						if(json.opt("state") != null) {
							if(json.get("state").equals("open")){
								task = task.setStatus(TaskStatus.PENDING.value()).setEndDate(null).setId(task.getId())
										.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName);
								if(!DB.isPrincipal(domain, userName, task))
									task.setParent(null);
								else task.setParent(task.getId());
								AON.updateTaskParent(domain.getName(), domain.getId(), userName, task);
								TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
									.setEvent("reopened").setTask(task.getId()).setCreationUser(userName);
								AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
							}
							if(json.get("state").equals("closed")){
								task = task.setStatus(TaskStatus.FINISHED.value()).setEndDate(Calendar.getInstance().getTime()).setId(task.getId())
										.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName);
								TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
										.setEvent("closed").setTask(task.getId()).setCreationUser(userName);
								AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
							}
							if(json.get("state").equals("deleted")){
								task = task.setStatus(TaskStatus.DELETED.value()).setId(task.getId())
										.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName);
								TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
										.setEvent("deleted").setTask(task.getId()).setCreationUser(userName);
								AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
							}
							if(json.get("state").equals("restore")){
								Integer taskId = task.getId();
								TaskEvent te = DB.getLastTaskEvent(domain, userName, f -> f.getTaskProperty().eq(taskId)
										.and(f.getEventProperty().ne("restore")).and(f.getEventProperty().ne("deleted")));
								TaskStatus status = te.getEvent() != null && te.getEvent().equals("closed") ? TaskStatus.FINISHED : TaskStatus.PENDING;
								task = task.setStatus(status.value()).setId(task.getId())
										.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName);
								TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
										.setEvent("restore").setTask(task.getId()).setCreationUser(userName);
								AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
							}
							AON.updateTaskStatus(domain.getName(), domain.getId(), userName, task );								
							object = getIssueJSON(domain, userName, task, scheme);
						} else if(json.opt("body") != null){
							task.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName);
							DB.updateTaskDescription(domain, userName, task.setComments(json.getString("body")));
							Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
							Boolean principal = DB.isPrincipal(domain, userName, task);
							object = new Issue(task, new Registry(), new LinkedList<Label>(), new Label(), 0, domain, userName, new Workgroup(), enterprise, principal, scheme).toJSON();
						} else if(json.opt("duplicate") != null){
							String d = json.getString("duplicate");
							if(!d.equals("liberate")){
								Integer parentId = Integer.parseInt(d); 
								updateTaskDuplicate(domain, userName, new Task().setId(parentId), parentId);
								task = updateTaskDuplicate(domain, userName, task, parentId);
								object = getDuplicateIssueJSON(domain, userName, task, scheme);
							} else if(d.equals("liberate")) {
								task = updateTaskLiberate(domain, userName, task);
								object = getIssueJSON(domain, userName, task, scheme);								
							}
						} else if(json.opt("faq") != null){
							String d = json.getString("faq");	
							Integer parentId = Integer.parseInt(d);
							task = updateTaskFaq(domain, userName, task, parentId);
							object = getDuplicateIssueJSON(domain, userName, task, scheme);
						} else if(json.opt("title") != null){
							task.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName)
								.setDescription(json.getString("title"));
							DB.updateTaskTitle(domain, userName, task.setComments(json.getString("title")));
							Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
							Boolean principal = DB.isPrincipal(domain, userName, task);
							object = new Issue(task, new Registry(), new LinkedList<Label>(), new Label(), 0, domain, userName, new Workgroup(), enterprise, principal, scheme).toJSON();
						}
						
					}
				} else { // CREATE NEW TASK / ISSUE
					Boolean faq = json.opt("state") != null && json.get("state").equals("faq");
					Integer num = AON.getLastTaskNumber(domain.getName(), domain.getId(),userName) != null ?
							AON.getLastTaskNumber(domain.getName(), domain.getId(),userName) : 0;
						
					Registry registry = new Registry();
					if(!faq) registry = AON.getRegistryFD(domain.getName(), domain.getId(), userName, json.getString("enterprise"));
					Task task = new Task()
						.setDescription(json.getString("title"))
						.setComments(json.getString("body"))
						.setDomain(domain.getId())
						.setNumber(num + 1)
						.setStartDate(Calendar.getInstance().getTime())
						.setDueDate(Calendar.getInstance().getTime())
						.setStatus(!faq ? TaskStatus.PENDING.value() : TaskStatus.FAQ.value())
						.setRegistry(!faq ? registry.getId() : null) 
						.setPercent((byte) 0) 
						.setPriority((byte) 0)
						.setRepeatPeriod((byte) 0)
						.setSource(TaskSource.MANUAL.value())
						.setCreationUser(userName)
						.setCreationDate(Calendar.getInstance().getTime())
						.setModificationUser(userName)
						.setModificationDate(Calendar.getInstance().getTime());
					
					Task t = AON.createTask(domain.getName(), domain.getId(), userName, task);
					Boolean principal = DB.isPrincipal(domain, userName, task);
					object = new Issue(t, new Registry(), new LinkedList<Label>(), new Label(), 0, domain, userName, new Workgroup(), registry, principal, scheme).toJSON();
				}
				break;
			case "labels":
				if(pathInfo.length > 4){	
					Tag tag= DB.getTag(domain, userName, pathInfo[4], TagType.TASK_LABEL);
					tag.setName(json.getString("name"));
					AON.updateTag(domainName, domain.getId(), userName, tag);
					object = new Label().setId(tag.getId()).setName(tag.getName()).toJSON();
				} else {
					Random rnd = new Random();		
					Tag tag = new Tag().setName(json.getString("name"))
						.setDomain(domain.getId()).setType(TagType.TASK_LABEL.value())
						.setColor(TagColor.values()[rnd.nextInt(9)].getColor());
					Tag t = AON.insertTag(domain.getName(), domain.getId(), userName, tag);
					object = new Label().setId(t.getId()).setName(t.getName()).toJSON();
				}
				break;
			case "types":
				if(pathInfo.length > 4){
					Tag tag= DB.getTag(domain, userName, pathInfo[4], TagType.TASK_TYPE);
					tag.setName(json.getString("name"));
					AON.updateTag(domainName, domain.getId(), userName, tag);
					object = new Label().setId(tag.getId()).setName(tag.getName()).toJSON();
				} else {
					Random rnd = new Random();		
					Tag tag = new Tag().setName(json.getString("name"))
							.setDomain(domain.getId()).setType(TagType.TASK_TYPE.value())
							.setColor(TagColor.values()[rnd.nextInt(9)].getColor());
					Tag t = AON.insertTag(domain.getName(), domain.getId(),userName, tag);
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
	}

	private Task updateTaskDuplicate(Domain domain, String userName, Task task, Integer parentId){
		task.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName)
			.setParent(parentId);
		TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
			.setEvent("duplicate").setTask(task.getId()).setCreationUser(userName);
		AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
		AON.updateTaskParent(domain.getName(), domain.getId(), userName, task);
		return task;
	}
	
	private Task updateTaskFaq(Domain domain, String userName, Task task, Integer parentId){
		task.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName)
			.setParent(parentId).setStatus(TaskStatus.FAQ.value());
		TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
			.setEvent("closed").setTask(task.getId()).setCreationUser(userName);
		AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
		AON.updateTaskParent(domain.getName(), domain.getId(), userName, task);
		AON.updateTaskStatus(domain.getName(), domain.getId(), userName, task);
		return task;
	}
	
	private Task updateTaskLiberate(Domain domain, String userName, Task task){
		Long count = AON.getTaskStream(domain.getName(), domain.getId(), userName, f -> f.getParentProperty().eq(task.getParent())
				.and(f.getIdProperty().ne(task.getParent()))).count();
		if(count <= 1){
			Task p = AON.getTask(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(task.getParent()));
			if(p.getParent().equals(p.getId())){
				p.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName).setParent(null);
				AON.updateTaskParent(domain.getName(), domain.getId(), userName, p);
			}
		}
		task.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(userName)
			.setParent(null);
		TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
				.setEvent("liberate").setTask(task.getId()).setCreationUser(userName);
		AON.createTaskEvent(domain.getName(), domain.getId(), userName, taskEvent, task.getId());
		AON.updateTaskParent(domain.getName(), domain.getId(), userName, task);
		return task;
	}
	
    private void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
	
	private JSONArray getLabelsJSON(Domain domain, String userName, String taskNumber) {
		Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(taskNumber));
		JSONArray array = new JSONArray();
		AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(taskId))
			.filter(t -> t.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
			.forEach(r -> array.put(r.toJSON()));
		return array;
	}
	
	private JSONObject getTypeJSON(Domain domain, String userName, String taskNumber) {
		Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(taskNumber));
		Stream<Tag> st = AON.getTaskLabelStream(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(taskId));
		return st.filter(t -> t.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName)).findFirst().orElse(new Label()).toJSON();	
	}
	
	private JSONObject getPriorityJSON(Domain domain, String userName, String taskNumber) {
		Task task = DB.getTask(domain, userName, Integer.parseInt(taskNumber));
		Priority p = Priority.values()[task.getPriority()];
		return new Label().setId(p.ordinal()).setName(p.getName()).setColor(p.getColor().getColor()).toJSON();
	}
	
	private JSONArray getCommentsJSON(Domain domain, String userName, String taskNumber) {
		JSONArray array = new JSONArray();
		Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(taskNumber));
		AON.getTaskCommentStream(domain.getName(), domain.getId(), userName, taskId)
		.map(new TaskCommentToCommentFiller(domain, userName)).forEach(c -> array.put(c.toJSON()));
		return array;
	}
	
	private JSONArray getEventsJSON(Domain domain, String userName, String taskNumber) {
		JSONArray array = new JSONArray();
		Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(taskNumber));
		AON.getTaskEventStream(domain.getName(), domain.getId(), userName, taskId)
		.map(new TaskEventToEventFiller(domain, userName)).forEach(e ->	array.put(e.toJSON()));
		return array;
	}
	
	private JSONArray getAllRegistriesJSON(Domain domain, String userName) {		
		JSONArray array = new JSONArray();
		AON.getTaskRegistryStream(domain.getName(), domain.getId(), userName)
			.map(new RegistryToUserFiller()).forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	private JSONArray getFilterRegistriesJSON(Domain domain, String userName, String filter) {		
		Stream<Registry> str = AON.getFilterRegistryStream(domain.getName(), domain.getId(), userName, filter);
		JSONArray array = new JSONArray();
		str.map(new RegistryToUserFiller()).forEach(l -> array.put(l.toJSON()));
		return array;
	}
	
	private JSONArray getAllLabelsJSON(Domain domain, String userName, TagType tt, final String filter) {
		JSONArray array = new JSONArray();
		AON.getTagStream(domain.getName(), domain.getId(), userName,
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getTypeProperty().eq(tt.value()))
				.and(f.getNameProperty().like( "%" + filter +"%")))
				.map(new TagToLabelFiller(domain, userName)).forEach(l->array.put(l.toJSON()));
		return array;
	}
	
	private JSONArray getAllPrioritiesJSON(Domain domain, String userName) {
		JSONArray array = new JSONArray();
		for (Priority p : Priority.values())
			array.put(new Label().setId(p.ordinal()).setName(p.getName()).setColor(p.getColor().getColor())
			.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/labels/" + p.getName()).toJSON());  
		return array;
	}
	
	private JSONObject getIssueJSON(Domain domain, String userName, Task task, String scheme) {
		Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
		Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
		Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
		LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId())); 
		LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
				.collect(Collectors.toCollection(LinkedList::new)); 
		Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
		Boolean principal = DB.isPrincipal(domain, userName, task);
		Issue issue = new Issue(task, assignee, labels, type, comments, domain, userName, workgroup, enterprise, principal, scheme);		
		return issue.toJSON();
	}
	
	private JSONObject getDuplicateIssueJSON(Domain domain, String userName, Task task, String scheme) {
		Task padre = DB.getTask(domain, userName, task.getParent());
		task.setPriority(padre.getPriority());
		Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, padre.getTaskHolder());
		Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
		Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, padre.getWorkgroup());
		LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(padre.getId())); 
		LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
				.collect(Collectors.toCollection(LinkedList::new)); 
		Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
				.findFirst().orElse(new Label());
		Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
		Boolean principal = DB.isPrincipal(domain, userName, task);
		Issue issue = new Issue(task, assignee, labels, type, comments, domain, userName, workgroup, enterprise, principal, scheme);		
		return issue.toJSON();
	}
	
	private JSONArray getIssuesJSON(Domain domain, String userName, IssueFilter filter, String scheme) {
		JSONArray array = new JSONArray();
		DB.getTaskStream(domain, userName, filter).forEach(task -> {
			Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
			Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
			Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
			Boolean principal = DB.isPrincipal(domain, userName, task);
			JSONObject json = new Issue(task, assignee, labels, type, comments, domain, userName, workgroup, enterprise, principal, scheme).toJSON();
			array.put(json);
		});
		return array;
	}

	private JSONArray getFaqIssuesJSON(Domain domain, String userName, IssueFilter filter, String scheme) {
		JSONArray array = new JSONArray();
		DB.getFaqTaskStream(domain, userName, filter).forEach(task -> {
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
			Boolean principal = DB.isPrincipal(domain, userName, task);
			JSONObject json = new Issue(task, new Registry(), labels, type, comments, domain, userName, new Workgroup(), new Registry(), principal, scheme).toJSON();
			array.put(json);
		});
		return array;
	}
	
	private JSONObject getFastFilterJSON(Domain domain, String userName) {
		JSONObject json = new JSONObject();
		String mine = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_MINE).getValue();
		String withoutGroup = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_GROUP).getValue();
		String withoutOperator= AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_OPERATOR).getValue();
		String type = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_TYPE).getValue();
		String priority = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_PRIORITY).getValue();
		json.put("mine", mine != null && mine.equalsIgnoreCase("true"));
		json.put("without_group", withoutGroup != null && withoutGroup.equalsIgnoreCase("true"));
		json.put("without_operator", withoutOperator != null && withoutOperator.equalsIgnoreCase("true"));
		json.put("type", type != null ? type : "");
		json.put("priority", priority != null ? priority : "");
		json.put("types", getAllLabelsJSON(domain, userName, TagType.TASK_TYPE, ""));
		json.put("priorities", getAllPrioritiesJSON(domain, userName));
		return json;
	}
	
	public JSONObject addFastFilter(Domain domain, String userName, JSONObject json){
		if(json.opt("mine") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_MINE, json.getString("mine"));
		if(json.opt("without_group") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_GROUP, json.getString("without_group"));
		if(json.opt("without_operator") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_OPERATOR, json.getString("without_operator"));
		if(json.opt("type") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_TYPE, json.getString("type"));
		if(json.opt("priority") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_FAST_FILTER_PRIORITY, json.getString("priority"));
		return getFastFilterJSON(domain, userName);
	}

	private JSONObject getGithubJSON(Domain domain, String userName) {
		JSONObject json = new JSONObject();
		String username = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_USERNAME).getValue();
		String repository = AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_REPOSITORY).getValue();
		String token= AON.getApplicationParamenter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_TOKEN).getValue();
		json.put("username", username != null ? username : "");
		json.put("repository", repository != null ? repository : "");
		json.put("token", token != null && !token.equals("") ? "**********" : "");
		return json;
	}
	
	public JSONObject addGithub(Domain domain, String userName, JSONObject json){
		if(json.opt("username") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_USERNAME, json.getString("username"));
		if(json.opt("repository") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_REPOSITORY, json.getString("repository"));
		if(json.opt("token") != null) AON.insertApplicationParameter(domain.getName(), domain.getId(), userName, AppParam.CALL_CENTER_GITHUB_TOKEN, json.getString("token"));
		return getGithubJSON(domain, userName);
	}
	
	private JSONArray getLightIssuesJSON(Domain domain, String userName, IssueFilter filter, String act) {
		JSONArray array = new JSONArray();
		DB.getLightTaskStream(domain, userName, filter, Integer.parseInt(act)).forEach(task -> {
			JSONObject json = new JSONObject();
			json.put("id", task.getId());
			json.put("state", TaskStatus.values()[task.getStatus()].getGwtName());
			json.put("number", task.getNumber());
			json.put("title", Utils.getShortString(task.getDescription()));
			json.put("parent", task.getParent());
			json.put("color", Utils.getStatusColor(task));
			Registry r = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
			json.put("enterprise", new User().setId(r.getId()).setLogin(r.getName()).toJSON());
			array.put(json);
		});
		return array;
	}
	
	private JSONArray getDuplicateIssuesJSON(Domain domain, String userName, String parent, String scheme) {
		Task padre = DB.getTask(domain, userName, Integer.parseInt(parent));
		JSONArray array = new JSONArray();
		DB.getDuplicateTaskStream(domain, userName, Integer.parseInt(parent)).forEach(task -> {
			task.setPriority(padre.getPriority());
			Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, padre.getTaskHolder());
			Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
			Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, padre.getWorkgroup());
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(padre.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
			Boolean principal = DB.isPrincipal(domain, userName, task);
			JSONObject json = new Issue(task, assignee, labels, type, comments, domain, userName, workgroup, enterprise, principal, scheme).toJSON();
			array.put(json);
		});
		return array;
	}
	
	private JSONArray getEnterpriseIssuesJSON(Domain domain, String userName, String e, String scheme) {
		JSONArray array = new JSONArray();
		DB.getEnterpriseTaskStream(domain, userName, Integer.parseInt(e)).forEach(task -> {
			task.setPriority(task.getPriority());
			Registry assignee = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getTaskHolder());
			Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
			Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> l.getType() == TagType.TASK_LABEL.value()).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> l.getType() == TagType.TASK_TYPE.value()).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			Integer comments = AON.getCommentsCount(domain.getName(), domain.getId(), userName, task.getId());
			Boolean principal = DB.isPrincipal(domain, userName, task);
			JSONObject json = new Issue(task, assignee, labels, type, comments, domain, userName, workgroup, enterprise, principal, scheme).toJSON();
			array.put(json);
		});
		return array;
	}
	
	private JSONObject getSizeJSON(Domain domain, String userName, IssueFilter filter) {
		Integer[] i = AON.getTaskCount(domain.getName(), domain.getId(), userName, f -> f.getDomainProperty().eq(domain.getId()), filter);
		JSONObject json = new JSONObject();
		json.put("open", i[0]);
		json.put("closed", i[1]);
		json.put("deleted", i[2]);
		return json;
	}
	
	private IssueFilter getFilter(HttpServletRequest req){
		return new IssueFilter()
				.setTitle(req.getParameter("title"))
				.setMine(req.getParameter("mine"))
				.setAssignee(req.getParameter("asignee"))
				.setWorkgroup(req.getParameter("workgroup"))
				.setCreator(req.getParameter("creator"))
				.setDirection(req.getParameter("direction"))
				.setLabels(req.getParameter("labels"))
				.setMentioned(req.getParameter("mentioned"))
				.setMilestone(req.getParameter("milestone"))
				.setSince(req.getParameter("since"))
				.setSort(req.getParameter("sort"))
				.setState(req.getParameter("state"))
				.setPriority(req.getParameter("priority"))
				.setType(req.getParameter("type"))
				.setEnterprise(req.getParameter("enterprise"))
				.setPerPage(Integer.parseInt(req.getParameter("per_page")))
				.setPage(Integer.parseInt(req.getParameter("page")))
				.setDateDiff(req.getParameter("date_diff"));
	}
	
	private void sendAssigneeNotification(Domain domain, String login, Task task, Integer taskHolderId) {
		// get taskHolder email!!!
		Registry r = AON.getRegistry(domain.getName(), domain.getId(), login, taskHolderId);
		String thName =  r.getAlias() != null && !r.getAlias().equals("") ? r.getAlias() : r.getName();
		NotificationServlet NS = new NotificationServlet();
		NotificationInfo ni = NS.buildNotificationInfo(domain, login, task, NotificationType.ASSIGNEE);
		ni.setNotifyAssignee(true);
		LinkedList<NotificationInfo> list = NS.buildNotificationInfoList(domain, login, task, NotificationType.ASSIGNEE);
 		String url = "http://"+domain.getName()+ "/emailFunction/"+ thName + "/" + domain.getName() + "/close/" + task.getId();
		NS.sendNotification(domain, login, ni, list, NotificationType.ASSIGNEE, url, r.getId());
	}
	
	private JSONArray getDateOptionsJSON(){
		JSONArray array = new JSONArray();
		JSONObject json1 = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONObject json3 = new JSONObject();
		JSONObject json4 = new JSONObject();					
		json1.put("id", "0");
		json1.put("name", "Hoy");
		json2.put("id", "1");
		json2.put("name", "Ayer");
		json3.put("id", "2");
		json3.put("name", "Hace 1 semana");
		json4.put("id", "3");
		json4.put("name", "Hace 1 mes");
		array.put(json1);
		array.put(json2);
		array.put(json3);
		array.put(json4);
		return array;
	}
	
	private JSONArray getOrderOptionsJSON(){
		JSONArray array = new JSONArray();
		JSONObject json1 = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONObject json3 = new JSONObject();
		JSONObject json4 = new JSONObject();					
		json1.put("id", "0");
		json1.put("name", "Creados - Recientes");
		json2.put("id", "1");
		json2.put("name", "Creados - Antiguos");
		json3.put("id", "2");
		json3.put("name", "Modificados - Recientes");
		json4.put("id", "3");
		json4.put("name", "Modificados - Antiguos");
		array.put(json1);
		array.put(json2);
		array.put(json3);
		array.put(json4);
		return array;
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
					.setCreatedAt(dateFormat.format(r.getCreationDate()))
					.setId(r.getId())
					.setUpdatedAt(dateFormat.format(r.getModificationDate()))
					.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/comments/" + r.getId())
					.setUser(new User(r.getCreationUser()));
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
					.setCreatedAt(dateFormat.format(r.getCreationDate()))
					.setId(r.getId())
					.setUrl(AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/events/" + r.getId()) 
					.setUser(new User(r.getCreationUser()));
		}
	}
	
	private static class RegistryToUserFiller implements Function<Registry, User>{
	
		@Override
		public User apply(Registry r) {
			return new User()
					.setId(r.getId())
					.setLogin(r.getName());
		}
	}
	
}
