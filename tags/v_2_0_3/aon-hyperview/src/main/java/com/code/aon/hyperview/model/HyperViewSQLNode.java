package com.code.aon.hyperview.model;

import java.util.LinkedList;
import java.util.List;

public abstract class HyperViewSQLNode implements IHyperViewContent{
	private String label;
	
	private String sentence;

	private List<HyperViewAttribute> metadata;

	private List<HyperViewParameter> parameters;

	private String linkUrl;

	private String linkLabel;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<HyperViewAttribute> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<HyperViewAttribute> metadata) {
		this.metadata = metadata;
	}

	public List<HyperViewParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<HyperViewParameter> parameters) {
		this.parameters = parameters;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}
	
	public boolean isLinkable() {
		return (getLinkUrl() != null);
	}

	public void addAttribute(HyperViewAttribute attr) {
		if (this.metadata == null) {
			metadata = new LinkedList<HyperViewAttribute>();
		}
		attr.setPosition(this.metadata.size());
		this.metadata.add(attr);
	}

	public void addParameter(HyperViewParameter param) {
		if (this.parameters == null) {
			parameters = new LinkedList<HyperViewParameter>();
		}
		this.parameters.add(param);
	}
	
	public boolean isContentCompositeView(){
		return false;
	}
	
	public boolean isHyperLinkView() {
		return false;
	}

}
