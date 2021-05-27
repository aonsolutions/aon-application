package com.code.aon.ui.commercial.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectCommercialExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- OFFER FILTER
	private String name;
	private String source;
	
	
	private Integer target;
	private Integer seller;
	
	private Date fromDate;
	private Date toDate;
	private String fromDateStr; 
	private String toDateStr;
	
	private String status;
	private String comments;
	private String probability;
	
	
	public String getInitialize(){
		return "";
	}

	public ProjectCommercialExportGwtController() {
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	public Integer getSeller() {
		return seller;
	}

	public void setSeller(Integer seller) {
		this.seller = seller;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		setFromDateStr(AonDateUtils.simpleFormat(fromDate));
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		setToDateStr(AonDateUtils.simpleFormat(toDate));
	}

	public String getFromDateStr() {
		return fromDateStr;
	}

	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}

	public String getToDateStr() {
		return toDateStr;
	}

	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}
	
	public void calculate(String expression) {
		String[] expressions = expression.split(" and ");
		for(Integer i = 0; i < expressions.length; i++) {
			String exp = expressions[i];
			if(exp.contains("ProjectCommercial.project.name")) {
				setName(exp.split("%")[1]);
			} else if(exp.contains("ProjectCommercial.source")) {
				setSource(exp.split(" == ")[1]);
			} else if(exp.contains("ProjectCommercial.project.date >=")) {
				Date d = getDate(exp.split(" >= ")[1]);
				if(d != null) {
					setToDate(d);
					setToDateStr(AonDateUtils.simpleFormat(d));
				}
			} else if(exp.contains("ProjectCommercial.project.date <=")) {
				Date d = getDate(exp.split(" <= ")[1]);
				if(d != null) {
					setToDate(d);
					setToDateStr(AonDateUtils.simpleFormat(d));
				}
			} else if(exp.contains("ProjectCommercial.comments")) {
				setComments(exp.split("%")[1]);
			} else if(exp.contains("ProjectCommercial.status")) {
				setStatus(exp.split(" == ")[1]);
			} else if(exp.contains("ProjectCommercial.probability")) {
				setProbability(exp.split(" == ")[1]);
			}
		}
	}
	
	private Date getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}