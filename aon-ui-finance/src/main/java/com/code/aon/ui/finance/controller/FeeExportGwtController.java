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
	
	public static Date from;
	public static Date to;
	public static Integer item;
	public static Integer status;
	public static Integer scope;
	
	public static List<Integer> segment;
	public static Integer customer;
	public static Integer seller;
	public static Integer workplace;
	public static Integer category;
	public static Integer period;

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

	public static Date getFrom() {
		return from;
	}

	public static void setFrom(Date from) {
		FeeExportGwtController.from = from;
	}

	public static Date getTo() {
		return to;
	}

	public static void setTo(Date to) {
		FeeExportGwtController.to = to;
	}

	public static Integer getItem() {
		return item;
	}

	public static void setItem(Integer item) {
		FeeExportGwtController.item = item;
	}

	public static Integer getStatus() {
		return status;
	}

	public static void setStatus(Integer status) {
		FeeExportGwtController.status = status;
	}

	public static Integer getScope() {
		return scope;
	}

	public static void setScope(Integer scope) {
		FeeExportGwtController.scope = scope;
	}
	
	public static List<Integer> getSegment() {
		return segment;
	}
	
	public static void setSegment(List<Integer> segment) {
		FeeExportGwtController.segment = segment;
	}
	
	public static Integer getCustomer() {
		return customer;
	}
	
	public static void setCustomer(Integer customer) {
		FeeExportGwtController.customer = customer;
	}
	
	public static Integer getSeller() {
		return seller;
	}
	
	public static void setSeller(Integer seller) {
		FeeExportGwtController.seller = seller;
	}
	
	public static Integer getWorkplace() {
		return workplace;
	}
	
	public static void setWorkplace(Integer workplace) {
		FeeExportGwtController.workplace = workplace;
	}
	
	public static Integer getCategory() {
		return category;
	}
	
	public static void setCategory(Integer category) {
		FeeExportGwtController.category = category;
	}
	
	public static Integer getPeriod() {
		return period;
	}
	
	public static void setPeriod(Integer period) {
		FeeExportGwtController.period = period;
	}
	
}