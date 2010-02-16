package com.code.aon.ui.report.context;

import java.util.LinkedList;
import java.util.List;

public class ReportGroup {

	private String name;
	
	private String description;
	
	private List<Object> list;
	
	public ReportGroup(){
		list = new LinkedList<Object>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Object getElement() {
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void addElement(Object element) {
		list.add(element);
	}

	public List<Object> getElements() {
		return list;
	}
}
