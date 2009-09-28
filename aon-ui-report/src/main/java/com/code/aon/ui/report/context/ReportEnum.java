package com.code.aon.ui.report.context;

import java.util.LinkedList;
import java.util.List;

public class ReportEnum {

	private String name;
	
	private String description;
	
	private List<ReportEnumElement> list;
	
	public ReportEnum(){
		this.list = new LinkedList<ReportEnumElement>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addEnumElement(ReportEnumElement element){
		list.add(element);
	}
	
	public ReportEnumElement getEnumElement(){
		return list.get(0);
	}
	
	public List<ReportEnumElement> getEnumElements(){
		return list;
	}
}
