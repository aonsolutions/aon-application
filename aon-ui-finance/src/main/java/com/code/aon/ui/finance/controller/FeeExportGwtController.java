package com.code.aon.ui.finance.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.jooq.tools.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;

public class FeeExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- PRODUCT FILTER
	
	public Date from;
	public Date to;
	public Integer item;
	public Integer status;
	public Integer scope;
	
	public List<Integer> segment;
	public Integer customer;
	public Integer seller;
	public Integer workplace;
	public Integer category;
	public Integer period;

	public FeeExportGwtController() {
	
	}

	public String getFilter() {
		JSONObject json = new JSONObject();
		if(from != null) {
			json.put("from", from.getTime());
		}
		
		if(to != null) {
			json.put("to", to.getTime());
		}
		
		if(item != null) {
			json.put("item", item);
		}
		
		if(status != null) {
			json.put("status", status);
		}
		
		if(scope != null) {
			json.put("scope", scope);
		}
		
		if(segment != null) {
			json.put("segment", new JSONArray(segment));
		}
		
		if(seller != null) {
			json.put("seller", seller);
		}
		
		if(customer != null) {
			json.put("customer", customer);
		}
		
		if(workplace != null) {
			json.put("workplace", workplace);
		}
		
		if(category != null) {
			json.put("category", category);
		}
		
		if(period != null) {
			json.put("period", period);
		}
		
		return Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
	public List<Integer> getSegment() {
		return segment;
	}
	
	public void setSegment(List<Integer> segment) {
		this.segment = segment;
	}
	
	public Integer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Integer customer) {
		this.customer = customer;
	}
	
	public Integer getSeller() {
		return seller;
	}
	
	public void setSeller(Integer seller) {
		this.seller = seller;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	
	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}
	
	public Integer getCategory() {
		return category;
	}
	
	public void setCategory(Integer category) {
		this.category = category;
	}
	
	public Integer getPeriod() {
		return period;
	}
	
	public void setPeriod(Integer period) {
		this.period = period;
	}
	
}