package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Priority;

public class Task  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain; 
	private String title; // description in DB
	private String description; // comments in DB
	private Date startDate; 
	private Date endDate; 
	private Date dueDate; 
	private Priority priority;
	private TaskStatus status; 
	private Byte percent; 
	private TaskHolder sender; 
	private TaskHolder taskHolder; 
	private Workgroup workgroup; 
	private Registry registry; 
	private TaskSource source; 
	private Integer sourceId;
	private Project project; 
	private Integer activityType; 
	private TaskPeriod repeatPeriod;  
	private TaskEvaluation evaluation;  
	private Integer number;
	private Integer parent;
	
	// TASK WORKFLOW
	
	private List<TaskWorkflow> workflows; 
	
	private List<Tag> tags;
	private List<Task> childs;

	// GOOGLE TASK IDS
	
	private String gtaskId; 
	private String gtasklistId;
	
	// AUDIT
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	
	private Task parentObj;
	
	//TMP
	private String tmp;
	
	public Task() { 
		this.tags = new ArrayList<>();
		this.childs = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}

	public Task setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public Task setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Task setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Task setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Task setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public Task setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Priority getPriority() {
		return priority;
	}

	public Task setPriority(Priority priority) {
		this.priority = priority;
		return this;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public Task setStatus(TaskStatus status) {
		this.status = status;
		return this;
	}

	public TaskEvaluation getEvaluation() {
		return evaluation;
	}

	public Task setEvaluation(TaskEvaluation evaluation) {
		this.evaluation = evaluation;
		return this;
	}
	
	public Byte getPercent() {
		return percent;
	}

	public Task setPercent(Byte percent) {
		this.percent = percent;
		return this;
	}

	public TaskHolder getTaskHolder() {
		if(taskHolder == null) {
			taskHolder = new TaskHolder();
		}
		return taskHolder;
	}

	public Task setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public Workgroup getWorkgroup() {
		return workgroup;
	}

	public Task setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public TaskSource getSource() {
		return source;
	}

	public Task setSource(TaskSource source) {
		this.source = source;
		return this;
	}
	
	public Integer getSourceId() {
		return sourceId;
	}

	public Task setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public Project getProject() {
		if(project == null)
			project = new Project();
		return project;
	}

	public Task setProject(Project project) {
		this.project = project;
		return this;
	}

	public Registry getRegistry() {
		return registry;
	}

	public Task setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public Task setActivityType(Integer activityType) {
		this.activityType = activityType;
		return this;
	}

	public TaskHolder getSender() {
		return sender;
	}

	public Task setSender(TaskHolder sender) {
		this.sender = sender;
		return this;
	}

	public List<TaskWorkflow> getWorkflows() {
		return workflows;
	}
	
	public Task setWorkflows(List<TaskWorkflow> workflows) {
		this.workflows = workflows;
		return this;
	}
	
	public void addWorkflow(TaskWorkflow w) {
		this.workflows.add(w);
	}

	public TaskPeriod getRepeatPeriod() {
		return repeatPeriod;
	}

	public Task setRepeatPeriod(TaskPeriod repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
		return this;
	}

	public Optional<String> getGtaskId() {
		return Optional.ofNullable(gtaskId);
	}

	public Task setGtaskId(String gtaskId) {
		this.gtaskId = gtaskId;
		return this;
	}

	public String getGtasklistId() {
		return gtasklistId;
	}

	public Task setGtasklistId(String gtasklistId) {
		this.gtasklistId = gtasklistId;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}

	public Task setNumber(Integer number) {
		this.number = number;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}

	public Task setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public Task setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}

	public Task setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public Task setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Integer getParent() {
		return parent;
	}

	public Task setParent(Integer parent) {
		this.parent = parent;
		return this;
	}

	public boolean isChild() {
		return parent!=null && parent>0;
	}
	
	public boolean isParent() {
		return parent==null;
	}
	
	public Optional<String> getTmp() {
		return Optional.ofNullable(tmp);
	}

	public Task setTmp(String tmp) {
		this.tmp = tmp;
		return this;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public Task setTags(List<Tag> tags) {
		this.tags = tags;
		return this;
	}
	
	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public List<Task> getChilds() {
		return childs;
	}
	
	public void addChild(Task task) {
		this.childs.add(task);
	}
	
	public Task setChilds(List<Task> childs) {
		this.childs = childs;
		return this;
	}
	
	public Task getParentObj() {
		return parentObj;
	}
	
	public Task setParentObj(Task parentObj) {
		this.parentObj = parentObj;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Task ) )
			return false;
		
		Task task = (Task) obj;
		
		return Objects.equals(id, task.id);
	}
}
