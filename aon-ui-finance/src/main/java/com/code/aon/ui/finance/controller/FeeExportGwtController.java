package com.code.aon.ui.finance.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

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
}