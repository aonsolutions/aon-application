package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;

public class AonMenuItem implements Serializable{

	private static final long serialVersionUID = 1L;

	private String title;
	private List<AonMenuItem> items;
	private boolean disabled;
	private boolean opened;
	private ClickHandler handler;
	
	public String getTitle() {
		return title;
	}
	
	public AonMenuItem setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public List<AonMenuItem> getItems() {
		if(items == null) {
			items = new LinkedList<>();
		}
		return items;
	}
	
	public AonMenuItem addItem(AonMenuItem item) {
		getItems().add(item);
		return this;
	}
	
	public AonMenuItem setItems(List<AonMenuItem> items) {
		this.items = items;
		return this;
	}
	
	public boolean hasHandler() {
		return getHandler() != null;
	}
	
	public ClickHandler getHandler() {
		return handler;
	}
	
	public AonMenuItem setHandler(ClickHandler handler) {
		this.handler = handler;
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public AonMenuItem setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	public boolean isOpened() {
		return opened;
	}
	
	public AonMenuItem setOpened(boolean opened) {
		this.opened = opened;
		return this;
	}
	
}
