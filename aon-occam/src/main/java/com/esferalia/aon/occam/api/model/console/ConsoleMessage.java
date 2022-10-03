package com.esferalia.aon.occam.api.model.console;

import java.io.Serializable;

public class ConsoleMessage implements Serializable {
	
	private static final long serialVersionUID = 152238620029335480L;
	
	private String processId;
	private ConsoleMessageType type;
	private String message;
	
	private Integer count;
	private Integer progress;
	private Double percent;
	
	public String getProcessId() {
		return processId;
	}
	public ConsoleMessage setProcessId(String processId) {
		this.processId = processId;
		return this;
	}
	
	public ConsoleMessageType getType() {
		return type;
	}
	public ConsoleMessage setType(ConsoleMessageType type) {
		this.type = type;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	public ConsoleMessage setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Integer getCount() {
		return count;
	}
	public ConsoleMessage setCount(Integer count) {
		this.count = count;
		return this;
	}
	
	public Integer getProgress() {
		return progress ;
	}
	public ConsoleMessage setProgress(Integer progress ) {
		this.progress = progress ;
		return this;
	}
	
	public Double getPercent() {
		return percent;
	}
	public ConsoleMessage setPercent(Double percent) {
		this.percent = percent;
		return this;
	}
	
}
