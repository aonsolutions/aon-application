package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class ProductBooking extends Product implements Serializable {

	private static final long serialVersionUID = -1616838783810544813L;
	
	private ProductBookingType bookingType;
	private ProductBookingPriceType bookingPriceType;
	
	private Integer position;
	private Workgroup workgroup;
	private TaskHolder taskHolder;
	private ProjectType projectType;
	
	private Boolean noBooking;
	
	private Boolean webhook;
	private String webhookProductId;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private boolean isBookingComposition;
	private boolean isConsole;
	private List<AonApp> aonApps;
	private List<DomainType> domainTypes;
	private String descriptionTemplate;
	
	public ProductBooking() {
		super();
	}

	public ProductBookingType getBookingType() {
		return bookingType;
	}

	public ProductBooking setBookingType(ProductBookingType bookingType) {
		this.bookingType = bookingType;
		return this;
	}

	public ProductBookingPriceType getBookingPriceType() {
		return bookingPriceType;
	}

	public ProductBooking setBookingPriceType(ProductBookingPriceType bookingPriceType) {
		this.bookingPriceType = bookingPriceType;
		return this;
	}

	public Integer getPosition() {
		return position;
	}

	public ProductBooking setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public Workgroup getWorkgroup() {
		return workgroup;
	}

	public ProductBooking setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public ProductBooking setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public ProductBooking setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public ProductBooking setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public ProductBooking setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public ProductBooking setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public boolean isBookingComposition() {
		return isBookingComposition;
	}
	
	public ProductBooking setBookingComposition(boolean isBookingComposition) {
		this.isBookingComposition = isBookingComposition;
		return this;
	}
	
	public boolean isConsole() {
		return isConsole;
	}
	
	public ProductBooking setConsole(boolean isConsole) {
		this.isConsole = isConsole;
		return this;
	}

	public List<AonApp> getAonApps() {
		return aonApps;
	}

	public ProductBooking setAonApps(List<AonApp> aonApps) {
		this.aonApps = aonApps;
		return this;
	}
	
	public List<DomainType> getDomainTypes() {
		return domainTypes;
	}

	public ProductBooking setDomainTypes(List<DomainType> domainTypes) {
		this.domainTypes = domainTypes;
		return this;
	}

	public String getDescriptionTemplate() {
		return descriptionTemplate;
	}

	public ProductBooking setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;
		return this;
	}

	public ProjectType getProjectType() {
		return projectType;
	}

	public ProductBooking setProjectType(ProjectType projectType) {
		this.projectType = projectType;
		return this;
	}

	public Boolean isNoBooking() {
		return noBooking;
	}

	public ProductBooking setNoBooking(Boolean noBooking) {
		this.noBooking = noBooking;
		return this;
	}

	public Boolean isWebhook() {
		return webhook;
	}

	public ProductBooking setWebhook(Boolean webhook) {
		this.webhook = webhook;
		return this;
	}

	public String getWebhookProductId() {
		return webhookProductId;
	}

	public ProductBooking setWebhookProductId(String webhookProductId) {
		this.webhookProductId = webhookProductId;
		return this;
	}
	
}
