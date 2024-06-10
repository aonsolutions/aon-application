package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class MarketingAction implements Serializable {
	
	public static enum MarketingActionMediaType {
		PHONE("Telefono", 0),
		EMAIL("Correo Electr\u00f3nico", 2),
		MAIL("Correo", 4),
		BULLETIN("Boletin", 5),
		INCOMING("Incoming", 6)
		;
		
		private String description;
		private Integer value;

		MarketingActionMediaType(String description, Integer value) {
			this.description = description;
			this.value = value;
		}

		public String getDescription() {
			return description;
		}

		public Integer getValue() {
			return value;
		}
		
		public static MarketingActionMediaType getMediaType(Integer value) {
			for(int i=0; i<MarketingActionMediaType.values().length; i++) {
				if(MarketingActionMediaType.values()[i].getValue() == value)
					return MarketingActionMediaType.values()[i];
			}
			
			return MarketingActionMediaType.PHONE;
		}
		
	}
	
	public static enum MarketingSellerDistribution {
		MANUAL("Manual", 0),
		AUTOMATIC("Autom\u00e1tico", 1),
//		WORK("Carga Trabajo", 2),
//		CALENDAR("Calendario Laboral", 3)
		;
		
		private String description;
		private Integer value;

		MarketingSellerDistribution(String description, Integer value) {
			this.description = description;
			this.value = value;
		}

		public String getDescription() {
			return description;
		}

		public Integer getValue() {
			return value;
		}
		
		public static MarketingSellerDistribution getSellerDistribution(Integer value) {
			for(int i=0; i<MarketingSellerDistribution.values().length; i++) {
				if(MarketingSellerDistribution.values()[i].getValue() == value)
					return MarketingSellerDistribution.values()[i];
			}
			
			return MarketingSellerDistribution.MANUAL;
		}
		
	}

	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer id;
	private Integer domain;
	private MarketingCampaign marketingCampaign;
	private MarketingActionMediaType mediaType;
	private Tag tag;
	private Date startDate;
	private Date endDate;
	private String description;
	private Double budget;
	private Double expense;
	private Workgroup workgroup;
	private TaskHolder taskHolder;
	
	private Survey survey;
	private Integer newsletter;
	private Integer news;
	
	private MarketingSellerDistribution sellerDistribution;
	
	private List<MarketingActionTarget> targets;
	
	private boolean deleted = false;
	
	public Integer getId() {
		return id;
	}
	public MarketingAction setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public MarketingAction setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public MarketingCampaign getMarketingCampaign() {
		return marketingCampaign;
	}
	public MarketingAction setMarketingCampaign(MarketingCampaign marketingCampaign) {
		this.marketingCampaign = marketingCampaign;
		return this;
	}
	public MarketingActionMediaType getMediaType() {
		return mediaType;
	}
	public MarketingAction setMediaType(MarketingActionMediaType mediaType) {
		this.mediaType = mediaType;
		return this;
	}
	public Tag getTag() {
		return tag;
	}
	public MarketingAction setTag(Tag tag) {
		this.tag = tag;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public MarketingAction setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public MarketingAction setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingAction setDescription(String description) {
		this.description = description;
		return this;
	}
	public Survey getSurvey() {
		return survey;
	}
	public MarketingAction setSurvey(Survey survey) {
		this.survey = survey;
		return this;
	}
	public Integer getNewsletter() {
		return newsletter;
	}
	public MarketingAction setNewsletter(Integer newsletter) {
		this.newsletter = newsletter;
		return this;
	}
	public Integer getNews() {
		return news;
	}
	public MarketingAction setNews(Integer news) {
		this.news = news;
		return this;
	}
	public Double getBudget() {
		return budget == null ? 0.00 : budget;
	}
	public MarketingAction setBudget(Double budget) {
		this.budget = budget;
		return this;
	}
	public Double getExpense() {
		return expense == null ? 0.00 : expense;
	}
	public MarketingAction setExpense(Double expense) {
		this.expense = expense;
		return this;
	}
	public Workgroup getWorkgroup() {
		return workgroup;
	}
	public MarketingAction setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public MarketingAction setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	public MarketingSellerDistribution getSellerDistribution() {
		return null == sellerDistribution ? MarketingSellerDistribution.MANUAL : sellerDistribution;
	}
	public MarketingAction setSellerDistribution(MarketingSellerDistribution sellerDistribution) {
		this.sellerDistribution = sellerDistribution;
		return this;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public List<MarketingActionTarget> getTargets() {
		return targets;
	}
	public MarketingAction setTargets(List<MarketingActionTarget> targets) {
		this.targets = targets;
		return this;
	}
	
}
