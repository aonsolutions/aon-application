package com.code.aon.hyperview.model;

import java.util.LinkedList;
import java.util.List;

public class HyperView {

	String label;
	String description;

	List<HyperViewParameter> parameters;

	HyperViewNode root;

	private int dynamicNodesPerPage;

	private int listLinesPerPage;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<HyperViewParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<HyperViewParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(HyperViewParameter parameter) {
		if (this.parameters == null) {
			this.parameters = new LinkedList<HyperViewParameter>();
		}
		this.parameters.add(parameter);
	}

	public HyperViewNode getRoot() {
		return root;
	}

	public void setRoot(HyperViewNode root) {
		this.root = root;
	}

	public void addNode(HyperViewNode node) {
		this.root = node;
	}

	public int getDynamicNodesPerPage() {
		return dynamicNodesPerPage;
	}

	public void setDynamicNodesPerPage(int dynamicNodesPerPage) {
		this.dynamicNodesPerPage = dynamicNodesPerPage;
	}

	public int getListLinesPerPage() {
		return listLinesPerPage;
	}

	public void setListLinesPerPage(int listLinesPerPage) {
		this.listLinesPerPage = listLinesPerPage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
