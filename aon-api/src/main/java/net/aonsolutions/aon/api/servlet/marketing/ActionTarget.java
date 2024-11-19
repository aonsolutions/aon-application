package net.aonsolutions.aon.api.servlet.marketing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;

public class ActionTarget implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Target
	class Target implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String name;
		private String documentType;
		private String documentCountry;
		private String document;
		
		private String streetType;
		private String address;
		private String number;
		private String zip;
		private String geozoneCode;
		private String city;
		
		private String phone;
		private String email;
		
		private String comments;
		
		public Target() {
			super();
		}

		public String getName() {
			return name;
		}

		public Target setName(String name) {
			this.name = name;
			return this;
		}

		public String getDocumentType() {
			return documentType;
		}

		public Target setDocumentType(String documentType) {
			this.documentType = documentType;
			return this;
		}

		public String getDocumentCountry() {
			return documentCountry;
		}

		public Target setDocumentCountry(String documentCountry) {
			this.documentCountry = documentCountry;
			return this;
		}

		public String getDocument() {
			return document;
		}

		public Target setDocument(String document) {
			this.document = document;
			return this;
		}

		public String getStreetType() {
			return streetType;
		}

		public Target setStreetType(String streetType) {
			this.streetType = streetType;
			return this;
		}

		public String getAddress() {
			return address;
		}

		public Target setAddress(String address) {
			this.address = address;
			return this;
		}

		public String getNumber() {
			return number;
		}

		public Target setNumber(String number) {
			this.number = number;
			return this;
		}

		public String getZip() {
			return zip;
		}

		public Target setZip(String zip) {
			this.zip = zip;
			return this;
		}

		public String getGeozoneCode() {
			return geozoneCode;
		}

		public Target setGeozoneCode(String geozoneCode) {
			this.geozoneCode = geozoneCode;
			return this;
		}

		public String getCity() {
			return city;
		}

		public Target setCity(String city) {
			this.city = city;
			return this;
		}

		public String getPhone() {
			return phone;
		}

		public Target setPhone(String phone) {
			this.phone = phone;
			return this;
		}

		public String getEmail() {
			return email;
		}

		public Target setEmail(String email) {
			this.email = email;
			return this;
		}

		public String getComments() {
			return comments;
		}

		public Target setComments(String comments) {
			this.comments = comments;
			return this;
		}
		
	}
	
	class MarketingAction extends com.esferalia.aon.occam.api.model.MarketingAction implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Integer seller;
		
		public MarketingAction() {
			super();
		}
		
		public Integer getSeller() {
			return seller;
		}

		public MarketingAction setSeller(Integer seller) {
			this.seller = seller;
			return this;
		}
		
	}
	
	// Variables
	
	private Target target;
	private MarketingAction marketingAction;
	private boolean trial;
	
	public ActionTarget() {
		super();
	}
	
	public ActionTarget(JSONObject json) {
		super();
		
		JSONObject actionTargetJson = json.optJSONObject("actionTarget");
		JSONObject targetJson = actionTargetJson.optJSONObject("target");
		JSONObject marketingActionJson = actionTargetJson.optJSONObject("marketingAction");
		
		// Marketing Action Target
		Integer actionId = Integer.parseInt(marketingActionJson.getString("id"));
		Integer sellerDistribution =  Integer.parseInt(marketingActionJson.getString("sellerDistribution"));
		Integer workgroup = JsonUtils.getInteger(marketingActionJson, "workgroup");
		Integer seller = JsonUtils.getInteger(marketingActionJson, "seller");
		
		marketingAction = new MarketingAction()
				.setSeller(seller);
		
		marketingAction.setId(actionId);
		marketingAction.setWorkgroup(new Workgroup().setId(workgroup));
		marketingAction.setSellerDistribution(
				null == sellerDistribution
				? MarketingSellerDistribution.MANUAL 
				: MarketingSellerDistribution.getSellerDistribution(sellerDistribution));
		
		// Target
		String name = targetJson.getString("name");
		String documentType = targetJson.getString("documentType");
		String documentCountry = targetJson.getString("documentCountry");
		String document = targetJson.getString("document");
		
		String streetType = targetJson.getString("streetType");
		String address = targetJson.getString("address");
		String number = targetJson.getString("number");
		String zip = targetJson.getString("zip");
		String geozoneCode = targetJson.getString("geozoneCode");
		String city = targetJson.getString("city");
		
		String phone = targetJson.getString("phone");
		String email = targetJson.getString("email");
		
		String comments = targetJson.getString("comments");
		
		target = new Target()
				.setName(name)
				.setDocumentType(documentType)
				.setDocumentCountry(documentCountry)
				.setDocument(document)
				.setStreetType(streetType)
				.setAddress(address)
				.setNumber(number)
				.setZip(zip)
				.setGeozoneCode(geozoneCode)
				.setCity(city)
				.setPhone(phone)
				.setEmail(email)
				.setComments(comments);
		
		trial = actionTargetJson.optBoolean("trial");
	}

	public Target getTarget() {
		return target;
	}

	public ActionTarget setTarget(Target target) {
		this.target = target;
		return this;
	}

	public MarketingAction getMarketingAction() {
		return marketingAction;
	}

	public ActionTarget setMarketingAction(MarketingAction marketingAction) {
		this.marketingAction = marketingAction;
		return this;
	}

	public boolean isTrial() {
		return trial;
	}

	public ActionTarget setCTrial(boolean trial) {
		this.trial = trial;
		return this;
	}

	public void setMarketingAction(com.esferalia.aon.occam.api.model.MarketingAction marketingAction) {
		this.marketingAction.setId(marketingAction.getId());
		this.marketingAction.setDomain(marketingAction.getDomain());
		this.marketingAction.setMarketingCampaign(marketingAction.getMarketingCampaign());
		this.marketingAction.setMediaType(marketingAction.getMediaType());
		this.marketingAction.setTag(marketingAction.getTag());
		this.marketingAction.setStartDate(marketingAction.getStartDate());
		this.marketingAction.setEndDate(marketingAction.getEndDate());
		this.marketingAction.setDescription(marketingAction.getDescription());
		this.marketingAction.setBudget(marketingAction.getBudget());
		this.marketingAction.setExpense(marketingAction.getExpense());
		this.marketingAction.setWorkgroup(marketingAction.getWorkgroup());
		this.marketingAction.setTaskHolder(marketingAction.getTaskHolder());
		this.marketingAction.setSurvey(marketingAction.getSurvey());
		this.marketingAction.setNewsletter(marketingAction.getNewsletter());
		this.marketingAction.setNews(marketingAction.getNews());
		
		this.marketingAction.setSellerDistribution(marketingAction.getSellerDistribution());
		this.marketingAction.setTargets(marketingAction.getTargets());
		this.marketingAction.setDeleted(marketingAction.isDeleted());
	}
	
	
}
