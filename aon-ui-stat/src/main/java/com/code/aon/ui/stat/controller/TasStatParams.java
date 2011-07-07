package com.code.aon.ui.stat.controller;

import java.util.Date;

import org.apache.poi.hssf.model.Model;

import antlr.MakeGrammar;

import com.code.aon.commercial.Target;
import com.code.aon.tas.Make;

public class TasStatParams {

	private Date fromDate;
	private Date toDate;
	private String publicCode;
	private String privateCode;
	private Make make;
	private Model model;
	private Target target;
	private TasStatType statType;

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public String getPublicCode() {
		return publicCode;
	}
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}
	
	public String getPrivateCode() {
		return privateCode;
	}
	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}

	public Make getMake() {
		return make;
	}
	public void setMake(Make make) {
		this.make = make;
	}

	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}
	
	public TasStatType getStatType() {
		return statType;
	}
	public void setStatType(TasStatType statType) {
		this.statType = statType;
	}

}
